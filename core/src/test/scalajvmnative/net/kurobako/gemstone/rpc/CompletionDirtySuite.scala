package net.kurobako.gemstone.rpc

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.implication
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class CompletionDirtySuite extends Suite {
  private val Granule      = Completion.DirtyGranuleBytes.raw.toLong
  private val Run: Int > 0 = 32
  private val Stride       = 512L

  private def completion(): Completion =
    Completion(CompletionId(1L), None, _ => Right(None), Set.empty, TaskControl(), 0, 1)

  private def entry(extent: Long, hostBacked: Boolean): AllocationAddressIndex.Entry =
    AllocationAddressIndex.Entry(
      AllocationId(1L),
      StoredAllocation(StoredAllocation.Storage(extent.unsafe[Long > 0]), GuestAddress(4096L), hostBacked = hostBacked)
    )

  private def stridedStores(target: Completion, allocation: AllocationAddressIndex.Entry, workers: Int): Unit = {
    val done    = CountDownLatch(workers)
    val stripes = allocation.size.raw / Stride
    val threads = (0 until workers).map { worker =>
      Thread { () =>
        (worker.toLong until stripes by workers.toLong).foreach(stripe =>
          target.markDirty(allocation, (stripe * Stride).unsafe[Long >= 0], Run)
        )
        done.countDown()
      }
    }
    threads.foreach(_.start())
    assert(done.await(60L, TimeUnit.SECONDS))
  }

  private def published(target: Completion): Vec[DirtyRange] = {
    val page = ExecutionRPC.MaximumElements
    Vec.from(Iterator.iterate(0)(_ + page.raw).map(offset => target.dirtyRanges(offset.unsafe[Int >= 0], page))
      .takeWhile(_.nonEmpty).flatten)
  }

  test("strided stores into a device-only allocation keep the tracker within the allocation over the granule") {
    val workers    = 4
    val extent     = 256L * 1024L * 1024L
    val allocation = entry(extent, hostBacked = false)
    val target     = completion()
    stridedStores(target, allocation, workers)
    val bound = workers.toLong * (extent / Granule / 8L + 64L)
    assert(target.dirtyBytesRetained <= bound, s"${target.dirtyBytesRetained} bytes retained against a bound of $bound")
  }

  test("a device-only allocation publishes every written byte at granule resolution") {
    val extent     = 64L * 1024L * 1024L
    val allocation = entry(extent, hostBacked = false)
    val target     = completion()
    stridedStores(target, allocation, 4)
    val ranges = published(target)
    val starts = java.util.TreeMap[Long, Long]()
    ranges.foreach(range => starts.put(range.offset.raw, range.offset.raw + range.size.raw))
    (0L until extent / Stride).foreach { stripe =>
      val offset = stripe * Stride
      val floor  = Option(starts.floorEntry(offset))
      assert(floor.exists(_.getValue >= offset + Run.raw), s"bytes at $offset are not published")
    }
    ranges.foreach { range =>
      assertEquals(range.offset.raw % Granule, 0L)
      assert(range.offset.raw + range.size.raw <= extent)
    }
  }

  test("a device-only publish covers only the granules that were written, clipped to the allocation") {
    val extent     = 7L * Granule + 100L
    val allocation = entry(extent, hostBacked = false)
    val target     = completion()
    target.markDirty(allocation, (Granule + 7L).unsafe[Long >= 0], 1)
    target.markDirty(allocation, (4L * Granule - 1L).unsafe[Long >= 0], 2)
    target.markDirty(allocation, (extent - 1L).unsafe[Long >= 0], 1)
    assertEquals(
      published(target).map(range => (range.offset.raw, range.size.raw.toLong)),
      Vec((Granule, Granule), (3L * Granule, 2L * Granule), (7L * Granule, 100L))
    )
  }

  test("a host-visible allocation publishes exact intervals") {
    val extent     = 1024L * 1024L
    val allocation = entry(extent, hostBacked = true)
    val target     = completion()
    stridedStores(target, allocation, 1)
    val expected = (0L until extent / Stride).map(stripe =>
      DirtyRange(allocation.id, (stripe * Stride).unsafe[Long >= 0], Run)
    )
    assertEquals(published(target), Vec.from(expected))
  }
}
