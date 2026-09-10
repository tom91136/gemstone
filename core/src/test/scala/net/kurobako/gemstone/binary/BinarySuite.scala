package net.kurobako.gemstone.binary

import net.kurobako.gemstone.Col

class BinarySuite extends munit.FunSuite {
  test("little-endian scalars round-trip through arrays and runtime collections") {
    val value = 0x8877665544332211L
    val bytes = LittleEndian.toArray(value, 8)

    assertEquals(bytes.toSeq, Seq[Byte](0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88.toByte))
    assertEquals(LittleEndian.decode(bytes, 0, 8), value)
    assertEquals(LittleEndian.decode(Col.from(bytes), 0, 8), value)
  }

  test("fixed-width bit operations share one validated definition") {
    assertEquals(Bits.mask(13), 0x1fffL)
    assertEquals(Bits.truncate(0x12345L, 16), 0x2345L)
    assertEquals(Bits.signExtend(0x1fffL, 13), -1L)
    assertEquals(Bits.byteCount(64), 8)
  }

  test("bit reversal and population count respect every supported width") {
    val values = Seq(0L, -1L, Long.MinValue, Long.MaxValue, 0x8877665544332211L)
    (1 to 64).foreach { width =>
      values.foreach { value =>
        val digits          = java.lang.Long.toBinaryString(value).reverse.padTo(64, '0').take(width)
        val expectedReverse = BigInt(digits, 2).longValue
        assertEquals(Bits.reverseBits(value, width), expectedReverse)
        assertEquals(Bits.bitCount(value, width), digits.count(_ == '1'))
        assertEquals(Bits.reverseBits(Bits.reverseBits(value, width), width), Bits.truncate(value, width))
      }
    }
    Seq(0, -1, 65).foreach { width =>
      val _ = intercept[IllegalArgumentException](Bits.reverseBits(0L, width))
      val _ = intercept[IllegalArgumentException](Bits.bitCount(0L, width))
    }
  }

  test("32-bit words slice and join without sign extension") {
    assertEquals(Bits.unsigned32(-1), Bits.Mask32)
    assertEquals(Bits.low16(0x12345L), 0x2345L)
    assertEquals(Bits.low32(0x123456789abcdef0L), 0x9abcdef0L)
    assertEquals(Bits.high32(0x123456789abcdef0L), 0x12345678L)
    assertEquals(Bits.join32(0x9abcdef0L, 0x12345678L), 0x123456789abcdef0L)
  }

  test("IEEE-754 interchange encodings round trip representative values") {
    assertEquals(IEEE754.floatToBinary16(1.5f), 0x3e00L)
    assertEquals(IEEE754.binary16ToFloat(0x3e00L), 1.5f)
    assertEquals(IEEE754.floatToBinary32(-0.0f), 0x80000000L)
    assertEquals(java.lang.Float.floatToRawIntBits(IEEE754.binary32ToFloat(0x7fc01234L)), 0x7fc01234)
    assertEquals(IEEE754.doubleToBinary64(-0.0), Long.MinValue)
    assertEquals(
      java.lang.Double.doubleToRawLongBits(IEEE754.binary64ToDouble(0x7ff8000000001234L)),
      0x7ff8000000001234L
    )
  }

  test("byte ranges reject negative and overflowing slices") {
    assertEquals(ByteRange.start(16, 12L, 4), Some(12))
    assertEquals(ByteRange.start(16, 13L, 4), None)
    assertEquals(ByteRange.contains(16, Long.MaxValue, 1L), false)
    assertEquals(ByteRange.contains(16, -1L, 1L), false)
  }
}
