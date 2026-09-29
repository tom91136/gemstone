package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*

class HostMemoryMirrorSuite extends munit.FunSuite {
  test("executions cannot share pending reads or dirty bytes") {
    val first  = HostMemoryMirror()
    val second = HostMemoryMirror()
    first.write(0x1000, Vec[Byte](9))
    assertEquals(second.read(0x1000, 1), None)
    assert(!first.hasPending)
    assert(!second.hasDirty)
    val reply = Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1)))
    assert(second.supply(reply).isRight)
    assert(first.supply(reply).isLeft)
    assertEquals(first.read(0x1000, 1), Some(Vec[Byte](9)))
    assertEquals(second.read(0x1000, 1), Some(Vec[Byte](1)))
  }

  test("batch configuration always permits forward progress") {
    val _ = intercept[IllegalArgumentException](HostMemoryMirror(maxBatchBytes = 7))
    val _ = intercept[IllegalArgumentException](HostMemoryMirror(maxBatchRanges = 0))
  }

  test("contiguous lanes merge while strided and gather accesses preserve holes") {
    val mirror = HostMemoryMirror()
    for (offset <- 0 until 32) assertEquals(mirror.read(0x1000L + offset * 4, 4), None)
    assertEquals(mirror.requests(), Vec(HostMemoryRange(GuestAddress(0x1000L), 128)))
    for (address <- List(0x2000L, 0x2010L, 0x9000L, 0x2000L)) mirror.read(address, 4)
    assertEquals(mirror.requests().size, 4)
    assertEquals(mirror.requests().map(_.size.raw).sum, 140)
  }

  test("linked discovery fetches only newly visited bytes and shares aliases") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(0x1000, 4), None)
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1, 2, 3, 4)))).isRight)
    assertEquals(mirror.read(0x1001, 2), Some(Vec[Byte](2, 3)))
    assertEquals(mirror.read(0x8000, 2), None)
    assertEquals(mirror.requests(), Vec(HostMemoryRange(GuestAddress(0x8000L), 2)))
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x8000L), Vec[Byte](5, 6)))).isRight)
    mirror.write(0x8001, Vec[Byte](9))
    assertEquals(mirror.read(0x8000, 2), Some(Vec[Byte](5, 9)))
    assertEquals(mirror.dirty, Vec(HostMemoryBytes(GuestAddress(0x8001L), Vec[Byte](9))))
  }

  test("write-only bytes need no upload and unsolicited host data is rejected atomically") {
    val mirror = HostMemoryMirror()
    mirror.write(0x1000, Vec[Byte](7, 8))
    assertEquals(mirror.requests(), Vec.empty[HostMemoryRange])
    assertEquals(mirror.read(0x1000, 2), Some(Vec[Byte](7, 8)))
    assertEquals(mirror.read(0x2000, 1), None)
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x2000L), Vec[Byte](1, 2)))).isLeft)
    assertEquals(mirror.read(0x2000, 1), None)
    assertEquals(HostMemoryMirror().read(0x1000, 2), None)
  }
  test("an in-flight upload cannot overwrite a later device write") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(0x1000, 2), None)
    assertEquals(mirror.requests().size, 1)
    mirror.write(0x1001, Vec[Byte](9))
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1, 2)))).isRight)
    assertEquals(mirror.read(0x1000, 2), Some(Vec[Byte](1, 9)))
  }

  test("transfers bound both bytes and sparse range count and drain all dirty bytes") {
    val mirror = HostMemoryMirror(maxBatchBytes = 8, maxBatchRanges = 2)
    for (address <- List(0x1000L, 0x2000L, 0x3000L)) mirror.read(address, 4)
    val first = mirror.requests()
    assertEquals(first.size, 2)
    assertEquals(first.map(_.size.raw).sum, 8)
    assert(mirror.supply(first.map(range =>
      HostMemoryBytes(range.address, Vec.fill(range.size.raw)(1.toByte))
    )).isRight)
    assertEquals(mirror.requests().size, 1)
    mirror.write(0x4000, Vec.fill(20)(2.toByte))
    assertEquals(mirror.takeDirty().map(_.bytes.size).sum, 8)
    assert(mirror.hasDirty)
    assertEquals(mirror.takeDirty().map(_.bytes.size).sum, 8)
    assertEquals(mirror.takeDirty().map(_.bytes.size).sum, 4)
    assert(!mirror.hasDirty)
  }

  test("fulfilled requests are retired and cannot authorize later unsolicited replies") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(0x1000, 4), None)
    assertEquals(mirror.requests().size, 1)
    val reply = Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1, 2, 3, 4)))
    assert(mirror.supply(reply).isRight)
    assert(mirror.supply(reply).isLeft)
    assertEquals(mirror.read(0x1000, 4), Some(Vec[Byte](1, 2, 3, 4)))
  }

  test("acquire invalidates clean bytes but preserves unpublished device writes") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(0x1000, 2), None)
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1, 2)))).isRight)
    mirror.write(0x1001, Vec[Byte](9))
    mirror.invalidateClean()
    assertEquals(mirror.read(0x1000, 1), None)
    assertEquals(mirror.read(0x1001, 1), Some(Vec[Byte](9)))
  }

  test("publication batches retain atomic widths and do not split words at a byte budget") {
    val mirror = HostMemoryMirror(maxBatchBytes = 10)
    mirror.write(0x1000, Vec.fill(4)(1.toByte), 4)
    mirror.write(0x1004, Vec.fill(4)(2.toByte), 4)
    mirror.write(0x1008, Vec.fill(8)(3.toByte), 8)
    val first = mirror.takeDirty()
    assertEquals(
      first,
      Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec.fill(4)(1.toByte) ++ Vec.fill(4)(2.toByte), 4))
    )
    assertEquals(mirror.takeDirty(), Vec(HostMemoryBytes(GuestAddress(0x1008L), Vec.fill(8)(3.toByte), 8)))
    assert(!mirror.hasDirty)
  }

}
