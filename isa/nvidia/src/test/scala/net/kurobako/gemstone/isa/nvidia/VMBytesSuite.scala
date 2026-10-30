package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite

class VMBytesSuite extends Suite {
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

  test("bulk accesses agree with scalar ones across pages and read unwritten bytes as zero") {
    val bytes   = VMBytes()
    val pattern = Array.tabulate[Byte](9000)(index => (index * 7 + 1).toByte)
    bytes.write(0x0ff0L, pattern, from = 1000, count = 8000)
    for (index <- Vec(0, 15, 16, 4095, 4096, 7999))
      assertEquals(bytes.read(0x0ff0L + index, 1), pattern(1000 + index) & 0xffL)
    assertEquals(bytes.read(0x0fefL, 1), 0L)
    assertEquals(bytes.read(0x0ff0L + 8000L, 1), 0L)
    val read = Array.fill[Byte](10000)(-1)
    bytes.read(0x0fe0L, read, from = 100, count = 8100)
    assertEquals(read.slice(0, 100).toVector, Vector.fill(100)((-1).toByte))
    assertEquals(read.slice(100, 116).toVector, Vector.fill(16)(0.toByte))
    assertEquals(read.slice(116, 8116).toVector, pattern.slice(1000, 9000).toVector)
    assertEquals(read.slice(8116, 8200).toVector, Vector.fill(84)(0.toByte))
    assertEquals(read.slice(8200, 10000).toVector, Vector.fill(1800)((-1).toByte))
  }

  test("a bulk read of unwritten pages is zero after an empty bulk write") {
    val bytes = VMBytes()
    bytes.write(0x5000L, Array.emptyByteArray, from = 0, count = 0)
    val read = Array.fill[Byte](16)(-1)
    bytes.read(0x5000L, read, from = 0, count = 16)
    assertEquals(read.toVector, Vector.fill(16)(0.toByte))
  }
}
