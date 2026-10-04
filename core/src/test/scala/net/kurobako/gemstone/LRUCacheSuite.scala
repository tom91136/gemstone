package net.kurobako.gemstone

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import scala.util.Using

class LRUCacheSuite extends munit.FunSuite {
  private class HashKey(val name: String, hash: () => Int) {
    override def hashCode(): Int             = hash()
    override def equals(other: Any): Boolean = other match {
      case key: HashKey => name == key.name
      case _            => false
    }
  }

  test("hashing a large key does not block unrelated cache hits") {
    val cache   = LRUCache[HashKey, Int](2)
    val entered = CountDownLatch(1)
    val release = CountDownLatch(1)
    val fast    = HashKey("fast", () => 1)
    val slow    = HashKey(
      "slow",
      () => {
        entered.countDown()
        if (!release.await(5, TimeUnit.SECONDS)) throw IllegalStateException("hash was not released")
        2
      }
    )
    assertEquals(cache.get(fast)(1), 1)
    Using.resource(threads.Pool(2)) { pool =>
      val pending = pool.executor.submit(() => cache.get(slow)(2))
      try {
        assert(entered.await(5, TimeUnit.SECONDS))
        val hit = pool.executor.submit(() => cache.get(fast)(fail("lost cached value")))
        assertEquals(hit.get(2, TimeUnit.SECONDS), 1)
      } finally release.countDown()
      assertEquals(pending.get(5, TimeUnit.SECONDS), 2)
    }
  }

  test("cache publication and eviction reuse key hashes while preserving collision equality") {
    val hashes                     = AtomicInteger()
    def key(name: String): HashKey = HashKey(name, () => { val _ = hashes.incrementAndGet(); 7 })
    val cache                      = LRUCache[HashKey, Int](1)
    assertEquals(cache.get(key("first"))(1), 1)
    assertEquals(cache.get(key("first"))(99), 1)
    assertEquals(cache.get(key("second"))(2), 2)
    assertEquals(cache.get(key("first"))(3), 3)
    assertEquals(hashes.get(), 4)
  }

  test("values are reused by byte content and the least recently used value is evicted") {
    val cache = LRUCache[Vec[Byte], String](2)
    assertEquals(cache.get(Vec[Byte](1, 2))("value-a"), "value-a")
    assertEquals(cache.get(Vec[Byte](3, 4))("value-b"), "value-b")
    assertEquals(cache.get(Vec[Byte](1, 2))(fail("duplicate computation")), "value-a")
    assertEquals(cache.get(Vec[Byte](5, 6))("value-c"), "value-c")
    assertEquals(cache.get(Vec[Byte](1, 2))(fail("a should remain cached")), "value-a")
    assertEquals(cache.get(Vec[Byte](3, 4))("recomputed-b"), "recomputed-b")
  }

  test("failed computation wakes subsequent readers with the original error") {
    val cache = LRUCache[String, Int](2)
    val error = intercept[IllegalArgumentException](cache.get("bad")(throw IllegalArgumentException("invalid value")))
    assertEquals(error.getMessage, "invalid value")
    assertEquals(intercept[IllegalArgumentException](cache.get("bad")(42)).getMessage, "invalid value")
  }

  test("concurrent readers share one in-flight computation") {
    val cache   = LRUCache[String, Int](2)
    val entered = CountDownLatch(8)
    val release = CountDownLatch(1)
    val calls   = AtomicInteger()
    // MUnit serializes assertion condition evaluation. Worker waits must not hold that assertion lock.
    Using.resource(threads.Pool(8)) { pool =>
      val workers = Vector.fill(8)(pool.executor.submit { () =>
        entered.countDown()
        cache.get("same") {
          val _ = calls.incrementAndGet()
          if (!release.await(5, TimeUnit.SECONDS)) throw IllegalStateException("readers were not released")
          42
        }
      })
      try assert(entered.await(5, TimeUnit.SECONDS), "readers did not start")
      finally release.countDown()
      assertEquals(workers.map(_.get(5, TimeUnit.SECONDS)), Vector.fill(8)(42))
      assertEquals(calls.get(), 1)
    }
  }

  test("single-flight remains exact while the completed LRU is at capacity") {
    val cache        = LRUCache[String, Int](1)
    val firstEntered = CountDownLatch(1)
    val releaseFirst = CountDownLatch(1)
    val secondCalls  = AtomicInteger()
    Using.resource(threads.Pool(2)) { pool =>
      val first = pool.executor.submit(() =>
        cache.get("first") {
          firstEntered.countDown()
          if (!releaseFirst.await(5, TimeUnit.SECONDS))
            throw IllegalStateException("first computation was not released")
          1
        }
      )
      try {
        assert(firstEntered.await(5, TimeUnit.SECONDS), "first computation did not start")
        val second = pool.executor.submit(() =>
          cache.get("second") {
            val _ = secondCalls.incrementAndGet()
            2
          }
        )
        assertEquals(second.get(5, TimeUnit.SECONDS), 2)
        assertEquals(cache.get("second")(3), 2)
        assertEquals(secondCalls.get(), 1)
      } finally releaseFirst.countDown()
      assertEquals(first.get(5, TimeUnit.SECONDS), 1)
    }
  }
  test("interrupted computations complete their shared cache entry") {
    val cache       = LRUCache[String, Int](1)
    val interrupted = InterruptedException("stop")
    val first       = try { val _ = cache.get("key")(throw interrupted); None }
    catch { case error: InterruptedException => Some(error) }
    assert(first.contains(interrupted))
    Using.resource(threads.Pool(1)) { pool =>
      val retry    = pool.executor.submit(() => cache.get("key")(42))
      val observed = intercept[java.util.concurrent.ExecutionException](retry.get(5, TimeUnit.SECONDS))
      assert(observed.getCause eq interrupted)
    }
  }

}
