package net.kurobako.gemstone

import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import scala.util.Using

class ImageCacheConcurrencySuite extends Suite {
  private def image(name: String): ImageKey = ImageKey(Vec.from(name.getBytes("UTF-8")))

  test("concurrent loads of equal images share one load until the last lease is released") {
    val cache   = ImageCache[String, Int](retainReleased = false)
    val entered = CountDownLatch(1)
    val release = CountDownLatch(1)
    val calls   = AtomicInteger()
    Using.resource(threads.Pool(8)) { pool =>
      val workers = Vector.fill(8)(pool.executor.submit { () =>
        cache.acquire(image("same")) {
          val _ = calls.incrementAndGet()
          entered.countDown()
          if (!release.await(5, TimeUnit.SECONDS)) throw IllegalStateException("load was not released")
          Right(42)
        }
      })
      try assert(entered.await(5, TimeUnit.SECONDS))
      finally release.countDown()
      val leases = workers.map(_.get(5, TimeUnit.SECONDS).toOption.get)
      assertEquals(leases.map(_.value), Vector.fill(8)(42))
      assertEquals(calls.get(), 1)
      leases.dropRight(1).foreach(_.release())
      val held = cache.acquire(image("same"))(fail("evicted while held")).toOption.get
      assertEquals(held.value, 42)
      leases.last.release()
      held.release()
      assertEquals(cache.acquire(image("same"))(Right(0)).map(_.value), Right(0))
      assertEquals(calls.get(), 1)
    }
  }

  test("a blocked load does not block other images or releases") {
    val cache   = ImageCache[String, Int]()
    val entered = CountDownLatch(1)
    val release = CountDownLatch(1)
    val held    = cache.acquire(image("fast"))(Right(1)).toOption.get
    Using.resource(threads.Pool(2)) { pool =>
      val slow = pool.executor.submit { () =>
        cache.acquire(image("slow")) {
          entered.countDown()
          if (!release.await(5, TimeUnit.SECONDS)) throw IllegalStateException("load was not released")
          Right(2)
        }
      }
      try {
        assert(entered.await(5, TimeUnit.SECONDS))
        val independent = pool.executor.submit { () =>
          held.release()
          cache.acquire(image("fast"))(Right(0)).map(_.value)
        }
        assertEquals(independent.get(2, TimeUnit.SECONDS), Right(1))
      } finally release.countDown()
      assertEquals(slow.get(5, TimeUnit.SECONDS).map(_.value), Right(2))
    }
  }

  test("interrupting a waiter leaves the owner usable and leaks no reference") {
    val cache   = ImageCache[String, Int](retainReleased = false)
    val entered = CountDownLatch(1)
    val release = CountDownLatch(1)
    val calls   = AtomicInteger()
    def load    = {
      val _ = calls.incrementAndGet()
      entered.countDown()
      if (!release.await(5, TimeUnit.SECONDS)) throw IllegalStateException("load was not released")
      Right(7)
    }
    Using.resource(threads.Pool(1)) { pool =>
      val owner  = pool.executor.submit(() => cache.acquire(image("key"))(load))
      val waiter = FutureTask(() => cache.acquire(image("key"))(load))
      val thread = Thread(waiter)
      try {
        assert(entered.await(5, TimeUnit.SECONDS))
        thread.start()
        thread.interrupt()
        val error = intercept[ExecutionException](waiter.get(2, TimeUnit.SECONDS))
        assert(error.getCause match { case _: InterruptedException => true; case _ => false })
      } finally {
        release.countDown()
        thread.join(5000)
      }
      owner.get(5, TimeUnit.SECONDS).toOption.get.release()
      cache.acquire(image("key"))(load).toOption.get.release()
      assertEquals(calls.get(), 2)
    }
  }

  test("a thrown load fails its caller and the next caller retries") {
    val cache       = ImageCache[String, Int]()
    val interrupted = InterruptedException("stop")
    val observed    = try { val _ = cache.acquire(image("key"))(throw interrupted); None }
    catch { case error: InterruptedException => Some(error) }
    assert(observed.contains(interrupted))
    assertEquals(cache.acquire(image("key"))(Right(42)).map(_.value), Right(42))
  }

  test("concurrent memo callers share one computation") {
    val memo    = Memo[String, Int]()
    val entered = CountDownLatch(1)
    val release = CountDownLatch(1)
    val calls   = AtomicInteger()
    Using.resource(threads.Pool(8)) { pool =>
      val workers = Vector.fill(8)(pool.executor.submit { () =>
        memo.get("same") {
          val _ = calls.incrementAndGet()
          entered.countDown()
          if (!release.await(5, TimeUnit.SECONDS)) throw IllegalStateException("computation was not released")
          42
        }
      })
      try assert(entered.await(5, TimeUnit.SECONDS))
      finally release.countDown()
      assertEquals(workers.map(_.get(5, TimeUnit.SECONDS)), Vector.fill(8)(42))
      assertEquals(calls.get(), 1)
    }
  }
}
