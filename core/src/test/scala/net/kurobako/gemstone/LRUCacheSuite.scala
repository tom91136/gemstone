package net.kurobako.gemstone

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import scala.util.Using

final class LRUCacheSuite extends munit.FunSuite {
  private final case class BlockedWorker(thread: Thread, release: CountDownLatch)

  private given Using.Releasable[BlockedWorker] with {
    override def release(worker: BlockedWorker): Unit = {
      worker.release.countDown()
      worker.thread.join()
    }
  }

  test("values are reused and the least recently used value is evicted") {
    val cache = LRUCache[String, String](PositiveInt(2))
    assertEquals(cache.get("a")("value-a"), "value-a")
    assertEquals(cache.get("b")("value-b"), "value-b")
    assertEquals(cache.get("a")(fail("duplicate computation")), "value-a")
    assertEquals(cache.get("c")("value-c"), "value-c")
    assertEquals(cache.get("a")(fail("a should remain cached")), "value-a")
    assertEquals(cache.get("b")("recomputed-b"), "recomputed-b")
  }

  test("failed computation wakes subsequent readers with the original error") {
    val cache = LRUCache[String, Int](PositiveInt(2))
    val error = intercept[IllegalArgumentException](cache.get("bad")(throw IllegalArgumentException("invalid value")))
    assertEquals(error.getMessage, "invalid value")
    assertEquals(intercept[IllegalArgumentException](cache.get("bad")(42)).getMessage, "invalid value")
  }

  test("concurrent readers share one in-flight computation") {
    val cache          = LRUCache[String, Int](PositiveInt(2))
    val entered        = CountDownLatch(8)
    val release        = CountDownLatch(1)
    val calls          = AtomicInteger()
    val mutableResults = Array.ofDim[Int](8)
    val mutableWorkers = Vector.newBuilder[Thread]
    mutableResults.indices.foreach { index =>
      val worker = Thread { () =>
        entered.countDown()
        mutableResults(index) = cache.get("same") {
          val _ = calls.incrementAndGet()
          release.await()
          42
        }
      }
      val _ = mutableWorkers += worker
    }
    val workers = mutableWorkers.result()
    workers.foreach(_.start())
    entered.await()
    release.countDown()
    workers.foreach(_.join())
    assertEquals(calls.get(), 1)
    assert(mutableResults.forall(_ == 42))
  }

  test("single-flight remains exact while the completed LRU is at capacity") {
    val cache         = LRUCache[String, Int](PositiveInt(1))
    val firstEntered  = CountDownLatch(1)
    val releaseFirst  = CountDownLatch(1)
    val secondEntered = CountDownLatch(1)
    val releaseSecond = CountDownLatch(1)
    val secondCalls   = AtomicInteger()

    val first = Thread { () =>
      val _ = cache.get("first") {
        firstEntered.countDown()
        releaseFirst.await()
        1
      }
    }
    first.start()
    firstEntered.await()

    Using.resource(BlockedWorker(first, releaseFirst)) { _ =>
      val second = Thread { () =>
        val _ = cache.get("second") {
          val _ = secondCalls.incrementAndGet()
          secondEntered.countDown()
          releaseSecond.await()
          2
        }
      }
      second.start()
      secondEntered.await()
      releaseSecond.countDown()
      second.join()

      assertEquals(cache.get("second")(3), 2)
      assertEquals(secondCalls.get(), 1)
    }
  }
}
