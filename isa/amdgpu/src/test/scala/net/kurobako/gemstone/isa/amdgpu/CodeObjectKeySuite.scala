package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*

class CodeObjectKeySuite extends munit.FunSuite {
  test("image keys compare content across independent byte arrays") {
    val first  = CodeObjectKey(Vec[Byte](1, 2, 3))
    val second = CodeObjectKey(Vec[Byte](1, 2, 3))
    assertEquals(first, second)
    assertEquals(first.hashCode(), second.hashCode())
    assertNotEquals(first, CodeObjectKey(Vec[Byte](1, 2, 4)))
  }

  test("CRC collisions do not share cached images") {
    // Distinct eight-byte images with the same CRC32, generated independently of CodeObjectKey.
    val first  = CodeObjectKey(Vec.from(java.util.HexFormat.of().parseHex("58fbb7a9b2680392")))
    val second = CodeObjectKey(Vec.from(java.util.HexFormat.of().parseHex("69176c4aa471a321")))
    assertEquals(first.hashCode(), second.hashCode())
    assertNotEquals(first, second)
    val cache = LRUCache[CodeObjectKey, String](2)
    assertEquals(cache.get(first)("first"), "first")
    assertEquals(cache.get(second)("second"), "second")
    assertEquals(cache.get(first)(fail("collision displaced first image")), "first")
  }
}
