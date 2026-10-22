package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import scala.concurrent.duration.*

class HostMemoryMirrorSuite extends munit.FunSuite {
  override val munitTimeout: Duration = 120.seconds

  private def at(address: Long): GuestAddress = GuestAddress(address)

  test("executions cannot share pending reads or dirty bytes") {
    val first  = HostMemoryMirror()
    val second = HostMemoryMirror()
    first.write(at(0x1000), Vec[Byte](9))
    assertEquals(second.read(at(0x1000), 1), None)
    assert(!first.hasPending)
    assert(!second.hasDirty)
    val reply = Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1)))
    assert(second.supply(reply).isRight)
    assert(first.supply(reply).isLeft)
    assertEquals(first.read(at(0x1000), 1), Some(Vec[Byte](9)))
    assertEquals(second.read(at(0x1000), 1), Some(Vec[Byte](1)))
  }

  test("contiguous lanes merge while strided and gather accesses preserve holes") {
    val mirror = HostMemoryMirror()
    for (offset <- 0 until 32) assertEquals(mirror.read(at(0x1000L + offset * 4), 4), None)
    assertEquals(mirror.requests(), Vec(HostMemoryRange(GuestAddress(0x1000L), 128)))
    for (address <- List(0x2000L, 0x2010L, 0x9000L, 0x2000L)) mirror.read(at(address), 4)
    assertEquals(mirror.requests().size, 4)
    assertEquals(mirror.requests().map(_.size.raw).sum, 140)
  }

  test("linked discovery fetches only newly visited bytes and shares aliases") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x1000), 4), None)
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1, 2, 3, 4)))).isRight)
    assertEquals(mirror.read(at(0x1001), 2), Some(Vec[Byte](2, 3)))
    assertEquals(mirror.read(at(0x8000), 2), None)
    assertEquals(mirror.requests(), Vec(HostMemoryRange(GuestAddress(0x8000L), 2)))
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x8000L), Vec[Byte](5, 6)))).isRight)
    mirror.write(at(0x8001), Vec[Byte](9))
    assertEquals(mirror.read(at(0x8000), 2), Some(Vec[Byte](5, 9)))
    assertEquals(mirror.takeDirty(), Vec(HostMemoryBytes(GuestAddress(0x8001L), Vec[Byte](9))))
  }

  test("write-only bytes need no upload and unsolicited host data is rejected atomically") {
    val mirror = HostMemoryMirror()
    mirror.write(at(0x1000), Vec[Byte](7, 8))
    assertEquals(mirror.requests(), Vec.empty[HostMemoryRange])
    assertEquals(mirror.read(at(0x1000), 2), Some(Vec[Byte](7, 8)))
    assertEquals(mirror.read(at(0x2000), 1), None)
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x2000L), Vec[Byte](1, 2)))).isLeft)
    assertEquals(mirror.read(at(0x2000), 1), None)
    assertEquals(HostMemoryMirror().read(at(0x1000), 2), None)
  }
  test("an in-flight upload cannot overwrite a later device write") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x1000), 2), None)
    assertEquals(mirror.requests().size, 1)
    mirror.write(at(0x1001), Vec[Byte](9))
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1, 2)))).isRight)
    assertEquals(mirror.read(at(0x1000), 2), Some(Vec[Byte](1, 9)))
  }

  test("transfers bound both bytes and sparse range count and drain all dirty bytes") {
    val mirror = HostMemoryMirror(maxBatchBytes = 8, maxBatchRanges = 2)
    for (address <- List(0x1000L, 0x2000L, 0x3000L)) mirror.read(at(address), 4)
    val first = mirror.requests()
    assertEquals(first.size, 2)
    assertEquals(first.map(_.size.raw).sum, 8)
    assert(mirror.supply(first.map(range =>
      HostMemoryBytes(range.address, Vec.fill(range.size)(1.toByte))
    )).isRight)
    assertEquals(mirror.requests().size, 1)
    mirror.write(at(0x4000), Vec.fill(20)(2.toByte))
    assertEquals(mirror.takeDirty().map(_.bytes.size).sum, 8)
    assert(mirror.hasDirty)
    assertEquals(mirror.takeDirty().map(_.bytes.size).sum, 8)
    assertEquals(mirror.takeDirty().map(_.bytes.size).sum, 4)
    assert(!mirror.hasDirty)
  }

  test("fulfilled requests are retired and cannot authorise later unsolicited replies") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x1000), 4), None)
    assertEquals(mirror.requests().size, 1)
    val reply = Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1, 2, 3, 4)))
    assert(mirror.supply(reply).isRight)
    assert(mirror.supply(reply).isLeft)
    assertEquals(mirror.read(at(0x1000), 4), Some(Vec[Byte](1, 2, 3, 4)))
  }

  test("acquire invalidates clean bytes but preserves unpublished device writes") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x1000), 2), None)
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1, 2)))).isRight)
    mirror.write(at(0x1001), Vec[Byte](9))
    mirror.invalidateClean()
    assertEquals(mirror.read(at(0x1000), 1), None)
    assertEquals(mirror.read(at(0x1001), 1), Some(Vec[Byte](9)))
  }

  test("system publication drains only its target and leaves unrelated dirty bytes queued") {
    val mirror = HostMemoryMirror()
    mirror.write(at(0x1000), Vec[Byte](1, 2, 3, 4))
    mirror.write(at(0x2000), Vec[Byte](5, 6, 7, 8))
    val published = mirror.takeDirty(Vec(HostMemoryRange(GuestAddress(0x2001L), 2)))
    assertEquals(published, Vec(HostMemoryBytes(GuestAddress(0x2001L), Vec[Byte](6, 7))))
    assertEquals(
      mirror.takeDirty(),
      Vec(
        HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1, 2, 3, 4)),
        HostMemoryBytes(GuestAddress(0x2000L), Vec[Byte](5)),
        HostMemoryBytes(GuestAddress(0x2003L), Vec[Byte](8))
      )
    )
  }

  test("bounded system publication keeps its acknowledgement pending until the target drains") {
    val mirror = HostMemoryMirror(maxBatchBytes = 8)
    mirror.write(at(0x1000), Vec.fill(12)(1.toByte))
    val target = Vec(HostMemoryRange(GuestAddress(0x1000L), 12))
    assertEquals(mirror.takeDirty(target).map(_.bytes.size).sum, 8)
    assert(mirror.hasDirty(target))
    assertEquals(mirror.takeDirty(target).map(_.bytes.size).sum, 4)
    assert(!mirror.hasDirty(target))
  }

  test("publication batches retain atomic widths and do not split words at a byte budget") {
    val mirror = HostMemoryMirror(maxBatchBytes = 10)
    mirror.write(at(0x1000), Vec.fill(4)(1.toByte), 4)
    mirror.write(at(0x1004), Vec.fill(4)(2.toByte), 4)
    mirror.write(at(0x1008), Vec.fill(8)(3.toByte), 8)
    val first = mirror.takeDirty()
    assertEquals(
      first,
      Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec.fill(4)(1.toByte) ++ Vec.fill(4)(2.toByte), 4))
    )
    assertEquals(mirror.takeDirty(), Vec(HostMemoryBytes(GuestAddress(0x1008L), Vec.fill(8)(3.toByte), 8)))
    assert(!mirror.hasDirty)
  }

  test("matrix-sized writes fit a bounded heap and drain exactly") {
    val mirror = HostMemoryMirror()
    val block  = Vec.from((0 until 65536).map(index => (index % 251).toByte))
    val base   = 0x100003L
    // The SYCL namespace CTS writes a 24 MB matrix. Keep its memory scale while
    // testing bookkeeping directly, without executing six million work-items.
    for (index <- 0 until 384) mirror.write(at(base + index.toLong * block.size), block)
    mirror.invalidateClean()
    for (batch <- 0 until 24) {
      val result = mirror.takeDirty()
      assertEquals(result.size, 16)
      result.zipWithIndex.foreach { case (range, index) =>
        assertEquals(range.address.raw, base + (batch.toLong * 16 + index) * block.size)
        assertEquals(range.bytes, block)
      }
    }
    assert(!mirror.hasDirty)
    assertEquals(mirror.read(at(base - 1), 1), None)
    assertEquals(mirror.read(at(base + 384L * block.size), 1), None)
    mirror.invalidateClean()
    assertEquals(mirror.read(at(base), 1), None)
  }

  test("page boundaries preserve holes and in-flight writes from other lanes") {
    val mirror = HostMemoryMirror()
    val base   = 0x1ffeL
    assertEquals(mirror.read(at(base), 4), None)
    assertEquals(mirror.requests(), Vec(HostMemoryRange(GuestAddress(base), 4)))
    mirror.write(at(base + 2), Vec[Byte](9))
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(base), Vec[Byte](1, 2, 3, 4)))).isRight)
    assertEquals(mirror.read(at(base), 4), Some(Vec[Byte](1, 2, 9, 4)))
    mirror.invalidateClean()
    assertEquals(mirror.read(at(base + 2), 1), Some(Vec[Byte](9)))
    assertEquals(mirror.read(at(base + 1), 1), None)
    assertEquals(mirror.read(at(base + 3), 1), None)
    assertEquals(mirror.takeDirty(), Vec(HostMemoryBytes(GuestAddress(base + 2), Vec[Byte](9))))
    mirror.invalidateClean()
    assertEquals(mirror.read(at(base + 2), 1), None)
  }

  test("dense scalar writes preserve their atomic width across publication pages") {
    val mirror = HostMemoryMirror()
    val word   = Vec[Byte](1, 2, 3, 4)
    for (index <- 0 until 262144) mirror.write(at(0x100000L + index.toLong * 4), word, 4)
    mirror.invalidateClean()
    val batch = mirror.takeDirty()
    assertEquals(batch.size, 16)
    batch.zipWithIndex.foreach { case (range, index) =>
      assertEquals(range.address.raw, 0x100000L + index.toLong * 65536)
      assertEquals(range.atomicWidth.raw, 4)
      assertEquals(range.bytes, Vec.from((0 until 65536).map(offset => word(offset % 4))))
    }
    assert(!mirror.hasDirty)
  }

  test("overlapping stores retire wider atomic markers without merging incompatible widths") {
    val mirror = HostMemoryMirror()
    mirror.write(at(0x1ff8), Vec.fill(8)(1.toByte), 8)
    mirror.write(at(0x1ffc), Vec.fill(4)(2.toByte), 4)
    mirror.write(at(0x2000), Vec.fill(4)(3.toByte), 4)
    mirror.write(at(0x2001), Vec[Byte](9))
    assertEquals(
      mirror.takeDirty(),
      Vec(
        HostMemoryBytes(GuestAddress(0x1ff8), Vec.fill(4)(1.toByte)),
        HostMemoryBytes(GuestAddress(0x1ffc), Vec.fill(4)(2.toByte), 4),
        HostMemoryBytes(GuestAddress(0x2000), Vec[Byte](3, 9, 3, 3))
      )
    )
  }

  test("selected publication expands a clipped atomic store to its aligned word") {
    val mirror = HostMemoryMirror(maxBatchBytes = 8)
    mirror.write(at(0x3000), Vec[Byte](1, 2, 3, 4), 4)
    val selection = Vec(HostMemoryRange(GuestAddress(0x3001L), 2))
    assert(mirror.hasDirty(selection))
    assertEquals(
      mirror.takeDirty(selection),
      Vec(HostMemoryBytes(GuestAddress(0x3000L), Vec[Byte](1, 2, 3, 4), 4))
    )
    assert(!mirror.hasDirty(selection))
    assert(!mirror.hasDirty)
  }

  test("selected publication preserves atomic widths one through eight") {
    val mirror = HostMemoryMirror()
    mirror.write(at(0x5000), Vec[Byte](1), 1)
    mirror.write(at(0x5002), Vec[Byte](2, 3), 2)
    mirror.write(at(0x5004), Vec[Byte](4, 5, 6, 7), 4)
    mirror.write(at(0x5008), Vec[Byte](8, 9, 10, 11, 12, 13, 14, 15), 8)
    val published = mirror.takeDirty(Vec(HostMemoryRange(GuestAddress(0x5000L), 16)))
    assertEquals(published.map(_.atomicWidth.raw), Vec(1, 2, 4, 8))
    assertEquals(published.map(_.address.raw), Vec(0x5000L, 0x5002L, 0x5004L, 0x5008L))
  }

  test("an atomic observation preserves bytes dirtied while its host request was pending") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x4000), 4), None)
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x4000L), Vec[Byte](1, 2, 3, 4)))).isRight)
    mirror.write(at(0x4001), Vec[Byte](9))
    mirror.observeAtomic(at(0x4000), Vec[Byte](5, 6, 7, 8))
    assertEquals(mirror.read(at(0x4000), 4), Some(Vec[Byte](5, 9, 7, 8)))
    assertEquals(mirror.takeDirty(), Vec(HostMemoryBytes(GuestAddress(0x4001L), Vec[Byte](9))))
  }

}
