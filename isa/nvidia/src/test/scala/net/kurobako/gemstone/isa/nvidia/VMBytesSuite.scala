package net.kurobako.gemstone.isa.nvidia

class VMBytesSuite extends munit.FunSuite {
  test("unwritten bytes read as zero anywhere in the address range") {
    val bytes = VMBytes()
    assertEquals(bytes.read(0L, 8), 0L)
    assertEquals(bytes.read(-8L, 8), 0L)
    assertEquals(bytes.read(0x0ffeL, 4), 0L)
  }

  test("values round trip within a page, across a page boundary and at the top of the range") {
    val bytes = VMBytes()
    bytes.write(address = 0x10L, value = 0x1122334455667788L, 8)
    bytes.write(address = 0x0ffdL, value = 0x0102030405060708L, 8)
    bytes.write(address = -4L, value = 0xaabbccddL, 4)
    assertEquals(bytes.read(0x10L, 8), 0x1122334455667788L)
    assertEquals(bytes.read(0x0ffdL, 8), 0x0102030405060708L)
    assertEquals(bytes.read(0x1000L, 1), 0x05L)
    assertEquals(bytes.read(-4L, 4), 0xaabbccddL)
    assertEquals(bytes.read(0x14L, 2), 0x3344L)
  }

  test("clearing zeroes every written byte") {
    val bytes = VMBytes()
    bytes.write(address = 0x2000L, value = -1L, 8)
    bytes.write(address = 0x0fffL, value = -1L, 2)
    bytes.clear()
    assertEquals(bytes.read(0x2000L, 8), 0L)
    assertEquals(bytes.read(0x0fffL, 2), 0L)
  }
}
