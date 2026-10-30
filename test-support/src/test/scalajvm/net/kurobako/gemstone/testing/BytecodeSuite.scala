package net.kurobako.gemstone.testing

class BytecodeSuite extends Suite {
  test("method sizes come from each method's Code attribute") {
    val sizes = bytecode.methodSizes(bytecode.getClass)
    assert(sizes.contains("methodSizes(Ljava/lang/Class;)Lscala/collection/immutable/Map;"), sizes.keys)
    assert(sizes.values.forall(_ > 0), sizes)
    assertEquals(bytecode.oversized(bytecode.getClass), Map.empty)
  }
}
