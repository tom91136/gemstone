package net.kurobako.gemstone

import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.Suite

class LRUCacheSuite extends Suite {
  private class HashKey(val name: String, hash: () => Int) {
    override def hashCode(): Int             = hash()
    override def equals(other: Any): Boolean = other match {
      case key: HashKey => name == key.name
      case _            => false
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
}
