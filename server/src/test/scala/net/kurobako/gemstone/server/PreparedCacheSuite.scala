package net.kurobako.gemstone.server

final class PreparedCacheSuite extends munit.FunSuite {
  test("preparation is reused, bounded, and does not mix different images") {
    val cache = PreparedCache[String, String](2)
    assertEquals(cache.get("a")("prepared-a"), "prepared-a")
    assertEquals(cache.get("a")(fail("duplicate preparation")), "prepared-a")
    assertEquals(cache.get("b")("prepared-b"), "prepared-b")
    assertEquals(cache.get("c")("prepared-c"), "prepared-c")
    assertEquals(cache.get("a")("reprepared-a"), "reprepared-a")
  }
  test("failed preparation wakes subsequent readers with the original error") {
    val cache = PreparedCache[String, Int](2)
    val error = intercept[IllegalArgumentException](cache.get("bad")(throw IllegalArgumentException("invalid image")))
    assertEquals(error.getMessage, "invalid image")
    assertEquals(intercept[IllegalArgumentException](cache.get("bad")(42)).getMessage, "invalid image")
  }
}
