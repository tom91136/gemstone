package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.testing.*
import scala.collection.mutable
import scala.concurrent.duration.*
import scala.util.Random

@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
class HostMemoryMirrorSuite extends Suite {
  override def baseTimeout: FiniteDuration = 120.seconds

  private def at(address: Long): GuestAddress = GuestAddress(address)

  private def page(address: Long): HostMemoryRange = HostMemoryRange(at(address), 4096)

  private def reply(ranges: Vec[HostMemoryRange], value: Byte = 1): Vec[HostMemoryBytes] =
    ranges.map(range => HostMemoryBytes(range.address, Vec.fill(range.size)(value)))

  test("executions cannot share pending reads or dirty bytes") {
    val first  = HostMemoryMirror()
    val second = HostMemoryMirror()
    first.write(at(0x1000), Vec[Byte](9))
    assertEquals(second.read(at(0x1000), 1, None), None)
    assert(!first.hasPending)
    assert(!second.hasDirty)
    val reply = Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1)))
    assert(second.supply(reply).isRight)
    assert(first.supply(reply).isLeft)
    assertEquals(first.read(at(0x1000), 1, None), Some(Vec[Byte](9)))
    assertEquals(second.read(at(0x1000), 1, None), Some(Vec[Byte](1)))
  }

  test("a miss requests its page, so lanes in one page share a range and distant pages stay apart") {
    val mirror = HostMemoryMirror()
    for (offset <- 0 until 32) assertEquals(mirror.read(at(0x1000L + offset * 4), 4, None), None)
    assertEquals(mirror.requests(), Vec(page(0x1000L)))
    for (address <- List(0x20000L, 0x20010L, 0x90000L, 0x20000L)) mirror.read(at(address), 4, None)
    assertEquals(mirror.requests(), Vec(page(0x1000L), page(0x20000L), page(0x90000L)))
  }

  test("sequential misses double the window up to the batch budget") {
    val mirror  = HostMemoryMirror(maxBatchBytes = 32768)
    val windows = (0 until 6).foldLeft((0x10000L, Vector.empty[Long])) { case ((next, sizes), _) =>
      assertEquals(mirror.read(at(next), 4, None), None)
      val requested = mirror.requests()
      assertEquals(requested.head.address.raw, next)
      assert(mirror.supply(reply(requested)).isRight)
      (next + requested.map(_.size.raw).sum, sizes :+ requested.map(_.size.raw.toLong).sum / 4096)
    }
    assertEquals(windows._2, Vector(1L, 2L, 4L, 8L, 8L, 8L))
  }

  test("a miss that skips a few pages of a scan still continues it") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x10000L), 4, None), None)
    assert(mirror.supply(reply(mirror.requests())).isRight)
    assertEquals(mirror.read(at(0x12010L), 4, None), None)
    assertEquals(mirror.requests(), Vec(HostMemoryRange(at(0x11000L), 2 * 4096)))
  }

  test("a retried miss does not widen the window again") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x10000L), 4, None), None)
    assert(mirror.supply(reply(mirror.requests())).isRight)
    for (_ <- 0 until 4) assertEquals(mirror.read(at(0x11000L), 4, None), None)
    assertEquals(mirror.requests(), Vec(HostMemoryRange(at(0x11000L), 2 * 4096)))
  }

  test("a read waits for its own bytes, not for the rest of their page") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x1010), 4, None), None)
    assert(mirror.hasPending)
    assert(mirror.supply(Vec(HostMemoryBytes(at(0x1010), Vec[Byte](1, 2, 3, 4)))).isRight)
    assert(!mirror.hasPending)
    assertEquals(mirror.read(at(0x1010), 4, None), Some(Vec[Byte](1, 2, 3, 4)))
    assertEquals(mirror.read(at(0x1014), 4, None), None)
    assertEquals(mirror.requests().map(_.size.raw).sum, 4096 - 4)
  }

  test("a read in a registered allocation that ends mid-page requests nothing past its end") {
    val allocation = HostMemoryRange(at(0x2000L), 1000)
    val mirror     = HostMemoryMirror()
    assertEquals(mirror.read(at(0x2000L + 996), 4, Some(allocation)), None)
    assertEquals(mirror.requests(), Vec(allocation))
    val unregistered = HostMemoryMirror()
    assertEquals(unregistered.read(at(0x2000L + 996), 4, None), None)
    assertEquals(unregistered.requests(), Vec(page(0x2000L)))
  }

  test("a window in a registered allocation stops at its end") {
    val allocation = HostMemoryRange(at(0x10000L), 3 * 4096 + 100)
    val limit      = allocation.address.raw + allocation.size.toLong
    val mirror     = HostMemoryMirror()
    for (index <- 0 until 4) {
      mirror.read(at(0x10000L + index * 4096L), 4, Some(allocation))
      val requested = mirror.requests()
      assert(requested.forall(range => range.address.raw + range.size.toLong <= limit), s"$requested")
      assert(mirror.supply(reply(requested)).isRight)
    }
    assertEquals(mirror.read(at(0x10000L + 3 * 4096L + 96), 4, Some(allocation)), Some(Vec.fill(4)(1.toByte)))
    assertEquals(mirror.requests(), Vec.empty[HostMemoryRange])
  }

  test("a short reply forgets the bytes no read was waiting on") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x1010), 4, None), None)
    assertEquals(mirror.requests(), Vec(page(0x1000L)))
    assert(mirror.supply(Vec(HostMemoryBytes(at(0x1000), Vec.fill(0x20)(7.toByte)))).isRight)
    assert(!mirror.hasPending)
    assert(!mirror.isUnreadable(at(0x1010), 4))
    assertEquals(mirror.read(at(0x1010), 4, None), Some(Vec.fill(4)(7.toByte)))
    assertEquals(mirror.requests(), Vec.empty[HostMemoryRange])
    assertEquals(mirror.read(at(0x1100), 4, None), None)
    assertEquals(mirror.requests(), Vec(HostMemoryRange(at(0x1020L), 4096 - 0x20)))
  }

  test("a short reply that stops before a waiting read makes its bytes unreadable until the next acquire") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x1ff8), 16, None), None)
    assertEquals(mirror.requests(), Vec(HostMemoryRange(at(0x1000L), 2 * 4096)))
    assert(mirror.supply(Vec(HostMemoryBytes(at(0x1000), Vec.fill(0x1000 + 4)(1.toByte)))).isRight)
    assert(!mirror.hasPending)
    assert(mirror.isUnreadable(at(0x1ff8), 16))
    assert(mirror.isUnreadable(at(0x2004), 4))
    assert(!mirror.isUnreadable(at(0x1ff8), 4))
    assertEquals(mirror.requests(), Vec.empty[HostMemoryRange])
    mirror.write(at(0x2004), Vec.fill(4)(2.toByte))
    assert(!mirror.isUnreadable(at(0x2004), 4))
    mirror.invalidateClean()
    assert(!mirror.isUnreadable(at(0x1ff8), 16))
  }

  test("replies that skip an unreadable hole still serve the bytes after it") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x10000L), 4, None), None)
    assert(mirror.supply(reply(mirror.requests())).isRight)
    assertEquals(mirror.read(at(0x12000L), 4, None), None)
    assertEquals(mirror.requests(), Vec(HostMemoryRange(at(0x11000L), 2 * 4096)))
    assert(mirror.supply(Vec(HostMemoryBytes(at(0x12000L), Vec.fill(4096)(5.toByte)))).isRight)
    assert(!mirror.hasPending)
    assertEquals(mirror.read(at(0x12000L), 4, None), Some(Vec.fill(4)(5.toByte)))
    assert(!mirror.isUnreadable(at(0x12000L), 4))
    assertEquals(mirror.requests(), Vec.empty[HostMemoryRange])
  }

  test("an empty reply makes everything a read was waiting on unreadable") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x1010), 4, None), None)
    assertEquals(mirror.requests(), Vec(page(0x1000L)))
    assert(mirror.supply(Vec(HostMemoryBytes(at(0x1000), Vec.empty))).isRight)
    assert(!mirror.hasPending)
    assert(mirror.isUnreadable(at(0x1010), 4))
    assert(!mirror.isUnreadable(at(0x1014), 4))
    assertEquals(mirror.requests(), Vec.empty[HostMemoryRange])
  }

  test("linked discovery fetches only newly visited pages and shares aliases") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x1000), 4, None), None)
    assert(mirror.supply(reply(Vec(page(0x1000L)), 3)).isRight)
    assertEquals(mirror.read(at(0x1001), 2, None), Some(Vec[Byte](3, 3)))
    assertEquals(mirror.read(at(0x1ffe), 2, None), Some(Vec[Byte](3, 3)))
    assertEquals(mirror.read(at(0x8000), 2, None), None)
    assertEquals(mirror.requests(), Vec(page(0x8000L)))
    assert(mirror.supply(reply(Vec(page(0x8000L)), 5)).isRight)
    mirror.write(at(0x8001), Vec[Byte](9))
    assertEquals(mirror.read(at(0x8000), 2, None), Some(Vec[Byte](5, 9)))
    assertEquals(mirror.takeDirty(), Vec(HostMemoryBytes(GuestAddress(0x8001L), Vec[Byte](9))))
  }

  test("write-only bytes need no upload and unsolicited host data is rejected atomically") {
    val mirror = HostMemoryMirror()
    mirror.write(at(0x1000), Vec[Byte](7, 8))
    assertEquals(mirror.requests(), Vec.empty[HostMemoryRange])
    assertEquals(mirror.read(at(0x1000), 2, None), Some(Vec[Byte](7, 8)))
    assertEquals(mirror.read(at(0x2000), 1, None), None)
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x5000L), Vec[Byte](1, 2)))).isLeft)
    assertEquals(mirror.read(at(0x2000), 1, None), None)
    assertEquals(HostMemoryMirror().read(at(0x1000), 2, None), None)
  }
  test("an in-flight upload cannot overwrite a later device write") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x1000), 2, None), None)
    assertEquals(mirror.requests().size, 1)
    mirror.write(at(0x1001), Vec[Byte](9))
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1, 2)))).isRight)
    assertEquals(mirror.read(at(0x1000), 2, None), Some(Vec[Byte](1, 9)))
  }

  test("transfers bound both bytes and sparse range count and drain all dirty bytes") {
    val mirror = HostMemoryMirror(maxBatchBytes = 6000, maxBatchRanges = 2)
    for (address <- List(0x10000L, 0x30000L, 0x50000L)) mirror.read(at(address), 4, None)
    val first = mirror.requests()
    assertEquals(first.size, 2)
    assertEquals(first.map(_.size.raw).sum, 6000)
    assert(mirror.supply(reply(first)).isRight)
    val second = mirror.requests()
    assertEquals(second.size, 2)
    assertEquals(second.map(_.size.raw).sum, 6000)
    val writes = HostMemoryMirror(maxBatchBytes = 8, maxBatchRanges = 2)
    writes.write(at(0x4000), Vec.fill(20)(2.toByte))
    assertEquals(writes.takeDirty().map(_.bytes.size).sum, 8)
    assert(writes.hasDirty)
    assertEquals(writes.takeDirty().map(_.bytes.size).sum, 8)
    assertEquals(writes.takeDirty().map(_.bytes.size).sum, 4)
    assert(!writes.hasDirty)
  }

  test("fulfilled requests are retired and cannot authorise later unsolicited replies") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x1000), 4, None), None)
    assertEquals(mirror.requests().size, 1)
    val reply = Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1, 2, 3, 4)))
    assert(mirror.supply(reply).isRight)
    assert(mirror.supply(reply).isLeft)
    assertEquals(mirror.read(at(0x1000), 4, None), Some(Vec[Byte](1, 2, 3, 4)))
  }

  test("acquire invalidates clean bytes but preserves unpublished device writes") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x1000), 2, None), None)
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1, 2)))).isRight)
    mirror.write(at(0x1001), Vec[Byte](9))
    mirror.invalidateClean()
    assertEquals(mirror.read(at(0x1000), 1, None), None)
    assertEquals(mirror.read(at(0x1001), 1, None), Some(Vec[Byte](9)))
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
    assertEquals(mirror.read(at(base - 1), 1, None), None)
    assertEquals(mirror.read(at(base + 384L * block.size), 1, None), None)
    mirror.invalidateClean()
    assertEquals(mirror.read(at(base), 1, None), None)
  }

  test("page boundaries preserve in-flight writes from other lanes") {
    val mirror = HostMemoryMirror()
    val base   = 0x1ffeL
    assertEquals(mirror.read(at(base), 4, None), None)
    assertEquals(mirror.requests(), Vec(HostMemoryRange(at(0x1000L), 8192)))
    mirror.write(at(base + 2), Vec[Byte](9))
    assert(mirror.supply(reply(Vec(HostMemoryRange(at(0x1000L), 8192)))).isRight)
    assertEquals(mirror.read(at(base), 4, None), Some(Vec[Byte](1, 1, 9, 1)))
    mirror.invalidateClean()
    assertEquals(mirror.read(at(base + 2), 1, None), Some(Vec[Byte](9)))
    assertEquals(mirror.read(at(base + 1), 1, None), None)
    assertEquals(mirror.read(at(base + 3), 1, None), None)
    assertEquals(mirror.takeDirty(), Vec(HostMemoryBytes(GuestAddress(base + 2), Vec[Byte](9))))
    mirror.invalidateClean()
    assertEquals(mirror.read(at(base + 2), 1, None), None)
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
    assertEquals(mirror.read(at(0x4000), 4, None), None)
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x4000L), Vec[Byte](1, 2, 3, 4)))).isRight)
    mirror.write(at(0x4001), Vec[Byte](9))
    mirror.observeAtomic(at(0x4000), Vec[Byte](5, 6, 7, 8), mirror.generation)
    assertEquals(mirror.read(at(0x4000), 4, None), Some(Vec[Byte](5, 9, 7, 8)))
    assertEquals(mirror.takeDirty(), Vec(HostMemoryBytes(GuestAddress(0x4001L), Vec[Byte](9))))
  }

  test("a system invalidation refuses the reply of a read issued before it") {
    val mirror = HostMemoryMirror()
    val word   = HostMemoryRange(GuestAddress(0x4000L), 4)
    assertEquals(mirror.read(at(0x4000), 4, None), None)
    assertEquals(mirror.requests(), Vec(page(0x4000L)))
    mirror.invalidateClean(Vec(word))
    assertEquals(mirror.read(at(0x4000), 4, None), None)
    assert(mirror.supply(reply(Vec(page(0x4000L)))).isRight)
    assertEquals(mirror.read(at(0x4000), 4, None), None)
    assert(mirror.hasPending)
    assertEquals(mirror.requests(), Vec(word))
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x4000L), Vec[Byte](2, 2, 2, 2)))).isRight)
    assertEquals(mirror.read(at(0x4000), 4, None), Some(Vec[Byte](2, 2, 2, 2)))
    assert(!mirror.hasPending)
  }

  test("a system invalidation refuses only the in-flight bytes it covers") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x4000), 8, None), None)
    assertEquals(mirror.requests(), Vec(page(0x4000L)))
    mirror.invalidateClean(Vec(HostMemoryRange(GuestAddress(0x4004L), 4)))
    assert(mirror.supply(reply(Vec(page(0x4000L)))).isRight)
    assertEquals(mirror.read(at(0x4000), 4, None), Some(Vec[Byte](1, 1, 1, 1)))
    assertEquals(mirror.read(at(0x4004), 4, None), None)
    assertEquals(mirror.requests(), Vec(HostMemoryRange(GuestAddress(0x4004L), 4)))
  }

  test("a system invalidation keeps unpublished writes and clean bytes elsewhere") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x4000), 8, None), None)
    assert(mirror.supply(Vec(HostMemoryBytes(GuestAddress(0x4000L), Vec[Byte](1, 2, 3, 4, 5, 6, 7, 8)))).isRight)
    mirror.write(at(0x4001), Vec[Byte](9))
    mirror.invalidateClean(Vec(HostMemoryRange(GuestAddress(0x4000L), 4)))
    assertEquals(mirror.read(at(0x4001), 1, None), Some(Vec[Byte](9)))
    assertEquals(mirror.read(at(0x4000), 1, None), None)
    assertEquals(mirror.read(at(0x4002), 2, None), None)
    assertEquals(mirror.read(at(0x4004), 4, None), Some(Vec[Byte](5, 6, 7, 8)))
    assertEquals(mirror.takeDirty(), Vec(HostMemoryBytes(GuestAddress(0x4001L), Vec[Byte](9))))
  }

  test("an atomic observation begun before a system invalidation is not cached") {
    val mirror = HostMemoryMirror()
    val since  = mirror.generation
    mirror.invalidateClean(Vec(HostMemoryRange(GuestAddress(0x4000L), 4)))
    mirror.observeAtomic(at(0x4000), Vec[Byte](5, 6, 7, 8), since)
    assertEquals(mirror.read(at(0x4000), 4, None), None)
    mirror.observeAtomic(at(0x4000), Vec[Byte](5, 6, 7, 8), mirror.generation)
    assertEquals(mirror.read(at(0x4000), 4, None), Some(Vec[Byte](5, 6, 7, 8)))
  }

  test("dirty batches match a byte-wise reference for random stores of every width") {
    val random             = Random(11L)
    val widthChoices       = Vector(0, 1, 2, 4, 8)
    val wordWidths         = List(1, 2, 4, 8)
    val widthsLargestFirst = List(8, 4, 2, 1)
    var atomics            = 0
    var merged             = 0
    for (round <- 0 until 40) {
      val bytesBudget  = 8 + random.nextInt(200)
      val rangesBudget = 1 + random.nextInt(5)
      val mirror       = HostMemoryMirror(bytesBudget.unsafe[Int >= 8], rangesBudget.unsafe[Int > 0])
      val dirty        = mutable.TreeSet.empty[Long]
      val contents     = mutable.Map.empty[Long, Byte]
      val words        =
        Map(1 -> 1, 2 -> 2, 4 -> 4, 8 -> 8).view.mapValues(_ => mutable.Set.empty[Long]).toMap
      def widthAt(address: Long): Int =
        widthsLargestFirst.find(width => words(width).contains(address / width)).getOrElse(0)
      for (_ <- 0 until 60) {
        val width   = widthChoices(random.nextInt(5))
        val size    = if (width == 0) 1 + random.nextInt(20) else width
        val address = 0x1000L + random.nextInt(3 * 4096 - 24) + (if (random.nextBoolean()) 4090 else 0)
        val aligned = if (width == 0) address else address / width * width
        val values  = Vec.tabulate(size)(_ => random.nextInt(256).toByte)
        mirror.write(at(aligned), values, width.unsafe[Int in (0, 1, 2, 4, 8)])
        wordWidths.foreach(k =>
          words(k).filterInPlace(index => index * k + k <= aligned || index * k >= aligned + size)
        )
        if (width != 0) words(width).add(aligned / width)
        (0 until size).foreach { index =>
          dirty.add(aligned + index)
          contents(aligned + index) = values(index)
        }
      }
      def reference(): Vector[(Long, Int, Int)] = {
        val chunks    = Vector.newBuilder[(Long, Int, Int)]
        val source    = dirty.iterator.buffered
        var remaining = bytesBudget
        var count     = 0
        while (source.hasNext && remaining >= math.max(1, widthAt(source.head)) && count < rangesBudget) {
          val base     = source.next()
          val width    = math.max(1, widthAt(base))
          val capacity = math.min(65536, remaining / width * width)
          var size     = 1
          remaining -= 1
          while (
            source.hasNext && remaining > 0 && size < capacity && source.head == base + size &&
            widthAt(source.head) == widthAt(base)
          ) {
            source.next()
            size += 1
            remaining -= 1
          }
          chunks += ((base, size, widthAt(base)))
          count += 1
        }
        chunks.result()
      }
      while (mirror.hasDirty) {
        val expected = reference()
        val taken    = mirror.takeDirty()
        assertEquals(
          taken.map(batch => (batch.address.raw, batch.bytes.size, batch.atomicWidth.raw)).toVector,
          expected,
          s"round $round"
        )
        taken.foreach { batch =>
          if (batch.atomicWidth.raw != 0) atomics += 1
          if (batch.bytes.size > 8) merged += 1
          assertEquals(
            batch.bytes.toVector,
            Vec.indices(batch.bytes.size.unsafe[Int >= 0]).map(index => contents(batch.address.raw + index)).toVector
          )
          Vec.indices(batch.bytes.size.unsafe[Int >= 0]).foreach(index => dirty.remove(batch.address.raw + index))
        }
      }
      assert(dirty.isEmpty)
    }
    assert(atomics > 20 && merged > 20, s"$atomics $merged")
  }

  test("a read spanning a shard boundary sees bytes written on both sides") {
    val mirror = HostMemoryMirror()
    mirror.write(at(0x3fffeL), Vec[Byte](1, 2))
    mirror.write(at(0x40000L), Vec[Byte](3, 4))
    assertEquals(mirror.read(at(0x3fffeL), 4, None), Some(Vec[Byte](1, 2, 3, 4)))
    assertEquals(
      mirror.takeDirty().map(batch => (batch.address.raw, batch.bytes.toVector)).toVector,
      Vector((0x3fffeL, Vector[Byte](1, 2, 3, 4)))
    )
  }

  test("a miss spanning a shard boundary requests both pages in one range and wakes once they arrive") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.read(at(0x3fffeL), 4, None), None)
    assert(mirror.hasPending)
    assertEquals(mirror.requests(), Vec(HostMemoryRange(at(0x3f000L), 2 * 4096)))
    assert(
      mirror.supply(Vec(HostMemoryBytes(
        at(0x3f000L),
        Vec.tabulate(2 * 4096)(index => (index / 4096 + 5).toByte)
      ))).isRight
    )
    assert(!mirror.hasPending)
    assertEquals(mirror.read(at(0x3fffeL), 4, None), Some(Vec[Byte](5, 5, 6, 6)))
  }

  test("dirty bytes merge across shards in address order") {
    val mirror = HostMemoryMirror()
    for (index <- 69 to 60 by -1) mirror.write(at(index * 4096L), Vec.fill(4096)(index.toByte))
    val taken = mirror.takeDirty()
    assertEquals(taken.map(batch => (batch.address.raw, batch.bytes.size)).toVector, Vector((60 * 4096L, 10 * 4096)))
    assertEquals(taken.head.bytes(5 * 4096 + 7), 65.toByte)
    assert(!mirror.hasDirty)
  }

  test("a system invalidation retains dirty bytes in every shard it touches") {
    val mirror = HostMemoryMirror()
    for (index <- 0 until 130) mirror.write(at(index * 4096L), Vec[Byte](index.toByte))
    val before = mirror.generation
    mirror.invalidateClean()
    assertEquals(mirror.generation, before + 1)
    for (index <- 0 until 130) assertEquals(mirror.read(at(index * 4096L), 1, None), Some(Vec[Byte](index.toByte)))
    assertEquals(mirror.read(at(0L + 1), 1, None), None)
  }

  test("modify updates a present word and leaves it dirty with its width") {
    val mirror = HostMemoryMirror()
    mirror.write(at(0x1000L), Vec[Byte](5, 0, 0, 0))
    val _ = mirror.takeDirty()
    assertEquals(mirror.modify(at(0x1000L), 4, None)(previous => previous + 7), Some(5L))
    assertEquals(mirror.read(at(0x1000L), 4, None), Some(Vec[Byte](12, 0, 0, 0)))
    val taken = mirror.takeDirty()
    assertEquals(
      taken.map(batch => (batch.address.raw, batch.bytes.toVector, batch.atomicWidth.raw)).toVector,
      Vector((0x1000L, Vector[Byte](12, 0, 0, 0), 4))
    )
  }

  test("modify of an absent word demands it and applies once it arrives") {
    val mirror = HostMemoryMirror()
    assertEquals(mirror.modify(at(0x5008L), 8, None)(_ + 1), None)
    assert(mirror.hasPending)
    assertEquals(mirror.requests(), Vec(page(0x5000L)))
    assert(mirror.supply(Vec(HostMemoryBytes(
      at(0x5000L),
      Vec.tabulate(4096)(index => if (index == 8) 41.toByte else 0.toByte)
    ))).isRight)
    assert(!mirror.hasPending)
    assertEquals(mirror.modify(at(0x5008L), 8, None)(_ + 1), Some(41L))
    assertEquals(mirror.read(at(0x5008L), 8, None), Some(Vec[Byte](42, 0, 0, 0, 0, 0, 0, 0)))
  }

  test("modify of a word an acquire dropped fetches it again") {
    val mirror = HostMemoryMirror()
    mirror.write(at(0x1000L), Vec[Byte](1, 0, 0, 0))
    val _ = mirror.takeDirty()
    mirror.invalidateClean()
    assertEquals(mirror.modify(at(0x1000L), 4, None)(_ + 1), None)
  }

  test("stage demands every missing read in one pass and reports whether any is outstanding") {
    val mirror = HostMemoryMirror()
    mirror.write(at(0x1000L), Vec.fill(8)(1.toByte))
    assert(!mirror.stage(Array(0x1000L, 0x1004L), Array(4, 4), 2, _ => None))
    assert(!mirror.hasPending)
    assert(mirror.stage(Array(0x1000L, 0x2000L, 0x90000L), Array(4, 4, 4), 3, _ => None))
    assert(mirror.hasPending)
    assertEquals(mirror.requests(), Vec(page(0x2000L), page(0x90000L)))
  }
}
