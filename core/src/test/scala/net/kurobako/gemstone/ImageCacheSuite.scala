package net.kurobako.gemstone

import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

class ImageCacheSuite extends Suite {
  private def image(values: Int*): Vec[Byte] = Vec.from(values.map(_.toByte))

  // distinct images with the same CRC32
  private val left  = image(0x58, 0xfb, 0xb7, 0xa9, 0xb2, 0x68, 0x03, 0x92)
  private val right = image(0x69, 0x17, 0x6c, 0x4a, 0xa4, 0x71, 0xa3, 0x21)

  private class Loads {
    private val calls = AtomicInteger()

    def apply(value: String): Either[String, String] = {
      val _ = calls.incrementAndGet()
      Right(value)
    }

    def count: Int = calls.get()
  }

  test("keys compare content and keep CRC collisions apart") {
    assertEquals(ImageKey(image(1, 2, 3)), ImageKey(image(1, 2, 3)))
    assertEquals(ImageKey(image(1, 2, 3)).hashCode(), ImageKey(image(1, 2, 3)).hashCode())
    assertNotEquals(ImageKey(image(1, 2, 3)), ImageKey(image(1, 2, 4)))
    assertEquals(ImageKey(left).hashCode(), ImageKey(right).hashCode())
    assertNotEquals(ImageKey(left), ImageKey(right))
  }

  test("colliding images keep independent entries and lifetimes") {
    val cache  = ImageCache[String, String](retainReleased = false)
    val loads  = Loads()
    val first  = cache.acquire(ImageKey(left))(loads("left")).toOption.get
    val second = cache.acquire(ImageKey(right))(loads("right")).toOption.get
    assertEquals((first.value, second.value), ("left", "right"))
    first.release()
    assertEquals(cache.acquire(ImageKey(right))(fail("displaced")).toOption.get.value, "right")
    assertEquals(loads.count, 2)
    assertEquals(cache.acquire(ImageKey(left))(loads("left again")).toOption.get.value, "left again")
    assertEquals(loads.count, 3)
  }

  test("held leases share one load across equal copies of an image") {
    val cache  = ImageCache[String, String](retainReleased = false)
    val loads  = Loads()
    val first  = cache.acquire(ImageKey(left))(loads("value")).toOption.get
    val second = cache.acquire(ImageKey(Vec.from(left.toArray)))(loads("other")).toOption.get
    assert(first.value eq second.value)
    assertEquals(loads.count, 1)
    first.release()
    assertEquals(cache.acquire(ImageKey(left))(fail("evicted while held")).toOption.get.value, "value")
  }

  test("released values are reloaded without retention") {
    val plain = ImageCache[String, String](retainReleased = false)
    val loads = Loads()
    plain.acquire(ImageKey(left))(loads("a")).toOption.get.release()
    plain.acquire(ImageKey(left))(loads("a")).toOption.get.release()
    assertEquals(loads.count, 2)
  }

  test("discarded leases are not retained") {
    val cache = ImageCache[String, String]()
    val loads = Loads()
    cache.acquire(ImageKey(left))(loads("a")).toOption.get.discard()
    cache.acquire(ImageKey(left))(loads("a")).toOption.get.discard()
    assertEquals(loads.count, 2)
  }

  test("finishing a lease twice drops one reference only") {
    val cache  = ImageCache[String, String](retainReleased = false)
    val loads  = Loads()
    val first  = cache.acquire(ImageKey(left))(loads("a")).toOption.get
    val second = cache.acquire(ImageKey(left))(loads("a")).toOption.get
    first.release()
    first.release()
    first.discard()
    assertEquals(cache.acquire(ImageKey(left))(fail("dropped while held")).toOption.get.value, "a")
    second.release()
    assertEquals(loads.count, 1)
  }

  test("failed loads are not kept and the next caller retries") {
    val cache = ImageCache[String, String]()
    val loads = AtomicInteger()
    def load  = loads.incrementAndGet() match {
      case 1 => Left("bad")
      case _ => Right("good")
    }
    assertEquals(cache.acquire(ImageKey(left))(load).map(_.value), Left("bad"))
    assertEquals(cache.acquire(ImageKey(left))(load).map(_.value), Right("good"))
    assertEquals(loads.get(), 2)
  }

  test("thrown failures propagate and release their reference for retry") {
    val cache = ImageCache[String, String](retainReleased = false)
    val loads = AtomicInteger()
    def load  = loads.incrementAndGet() match {
      case 1 => throw IllegalArgumentException("invalid")
      case _ => Right("good")
    }
    assertEquals(intercept[IllegalArgumentException](cache.acquire(ImageKey(left))(load)).getMessage, "invalid")
    assertEquals(cache.acquire(ImageKey(left))(load).map(_.value), Right("good"))
    assertEquals(loads.get(), 2)
  }

  test("memo computes each key once and retries after a thrown failure") {
    val memo  = Memo[String, Int]()
    val calls = AtomicInteger()
    assertEquals(memo.get("a") { val _ = calls.incrementAndGet(); 1 }, 1)
    assertEquals(memo.get("a")(fail("recomputed")), 1)
    assertEquals(memo.get("b")(2), 2)
    val error = intercept[IllegalArgumentException](memo.get("c")(throw IllegalArgumentException("bad")))
    assertEquals(error.getMessage, "bad")
    assertEquals(memo.get("c")(3), 3)
    assertEquals(calls.get(), 1)
  }
}
