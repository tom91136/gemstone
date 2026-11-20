package net.kurobako.gemstone.rpc

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.given
import net.kurobako.gemstone.testing.*
import scala.util.Using

class SegmentedStorageSuite extends Suite {
  import StoredAllocation.Storage

  private val Segment = StoredAllocation.SegmentBytes.toLong

  private def sized(size: Long): Storage = Storage(size.unsafe[Long > 0])

  private def stored(storage: Storage, offset: Long, bytes: Array[Byte]): Unit =
    storage.write(offset, bytes.length)(_ => storage.copyFrom(offset, bytes, 0, bytes.length))

  private def loaded(storage: Storage, offset: Long, length: Int): Array[Byte] = {
    val target = Array.ofDim[Byte](length)
    storage.read(offset, length)(_ => storage.copyTo(offset, target, 0, length))
    target
  }

  private def pattern(seed: Int, length: Int): Array[Byte] = Array.tabulate(length)(index => (seed + index).toByte)

  test("storage past the Int range keeps both ends and every segment boundary") {
    val storage            = sized(Int.MaxValue.toLong + 4096L)
    val probes: List[Long] = List(0L, Segment * 2L - 8L, storage.size - 16L)
    probes.zipWithIndex.foreach((offset, seed) => stored(storage, offset, pattern(seed, 16)))
    probes.zipWithIndex.foreach((offset, seed) =>
      assertEquals(loaded(storage, offset, 16).toVector, pattern(seed, 16).toVector)
    )
  }

  test("a scalar spanning a segment boundary round-trips little endian") {
    val storage = sized(Segment + 4096L)
    val offset  = Segment - 3L
    storage.write(offset, 8)(_ => storage.encode(offset, 0x0102030405060708L, 8))
    assertEquals(storage.read(offset, 8)(_ => storage.decode(offset, 8)), 0x0102030405060708L)
    assertEquals(loaded(storage, offset, 8).toVector, Vector[Byte](8, 7, 6, 5, 4, 3, 2, 1))
  }

  test("untouched segments read as zero and take no memory") {
    val storage = sized(Segment * 3L + 1L)
    assertEquals(storage.residentBytes, 0L)
    assertEquals(loaded(storage, Segment - 4L, 8).toVector, Vector.fill[Byte](8)(0))
    assertEquals(storage.read(Segment * 3L, 1)(_ => storage.decode(Segment * 3L, 1)), 0L)
    assertEquals(storage.residentBytes, 0L)
    stored(storage, Segment * 3L, Array[Byte](7))
    assertEquals(storage.residentBytes, 1L)
    stored(storage, 0L, Array[Byte](1))
    assertEquals(storage.residentBytes, Segment + 1L)
  }

  test("an allocation smaller than a segment backs only its own size") {
    val storage = sized(100L)
    stored(storage, 99L, Array[Byte](5))
    assertEquals(storage.residentBytes, 100L)
  }

  test("concurrent first writes to one segment all survive") {
    val threads = 8
    Using.resource(Executors.newFixedThreadPool(threads)) { pool =>
      for (round <- 0 until 500) {
        val storage = sized(threads * 64L)
        val start   = CountDownLatch(1)
        val done    = CountDownLatch(threads)
        for (thread <- 0 until threads)
          pool.execute { () =>
            start.await()
            stored(storage, thread * 64L, pattern(thread + round, 64))
            done.countDown()
          }
        start.countDown()
        assert(done.await(30, TimeUnit.SECONDS))
        for (thread <- 0 until threads)
          assertEquals(loaded(storage, thread * 64L, 64).toVector, pattern(thread + round, 64).toVector)
        assertEquals(storage.residentBytes, threads * 64L)
      }
    }
  }

  test("concurrent first writes across segments materialise each segment once") {
    val threads = 6
    Using.resource(Executors.newFixedThreadPool(threads)) { pool =>
      val storage = sized(Segment + 8192L)
      val start   = CountDownLatch(1)
      val done    = CountDownLatch(threads)
      val offsets = List(0L, 64L, 128L, Segment, Segment + 64L, Segment + 128L)
      offsets.zipWithIndex.foreach((offset, seed) =>
        pool.execute { () =>
          start.await()
          stored(storage, offset, pattern(seed, 64))
          done.countDown()
        }
      )
      start.countDown()
      assert(done.await(60, TimeUnit.SECONDS))
      offsets.zipWithIndex.foreach((offset, seed) =>
        assertEquals(loaded(storage, offset, 64).toVector, pattern(seed, 64).toVector)
      )
      assertEquals(storage.residentBytes, Segment + 8192L)
    }
  }
}
