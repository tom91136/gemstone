package net.kurobako.gemstone

import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

class ImageCacheRetentionSuite extends Suite {
  private def image(name: String): ImageKey = ImageKey(Vec.from(name.getBytes("UTF-8")))

  test("released values are reused while retained") {
    val cache               = ImageCache[String, String]()
    val loads               = AtomicInteger()
    def load(value: String) = {
      val _ = loads.incrementAndGet()
      Right(value)
    }
    cache.acquire(image("left"))(load("a")).toOption.get.release()
    val again = cache.acquire(image("left"))(load("b")).toOption.get
    assertEquals(again.value, "a")
    assertEquals(loads.get(), 1)
    again.release()
    assertEquals(cache.acquire(image("right"))(load("c")).toOption.get.value, "c")
    assertEquals(cache.acquire(image("left"))(load("d")).toOption.get.value, "a")
  }
}
