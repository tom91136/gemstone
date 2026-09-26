package net.kurobako.gemstone.binary

import net.kurobako.gemstone.Vec

final class BinarySuite extends munit.FunSuite {
  test("little-endian scalars round-trip through arrays and runtime collections") {
    val value = 0x8877665544332211L
    val width = ScalarByteWidth.Qword
    val bytes = LittleEndian.toArray(value, width)

    assertEquals(bytes.toSeq, Seq[Byte](0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88.toByte))
    assertEquals(LittleEndian.decode(bytes, 0, width), value)
    assertEquals(LittleEndian.decode(Vec.from(bytes), 0, width), value)
  }

  test("fixed-width bit operations share one validated definition") {
    assertEquals(Bits.mask(BitWidth(13)), 0x1fffL)
    assertEquals(Bits.truncate(0x12345L, BitWidth(16)), 0x2345L)
    assertEquals(Bits.unsignedField(0x12345L, 4, BitWidth(8)), 0x34L)
    assertEquals(Bits.signedField(0xf0L, 4, BitWidth(4)), -1L)
    assertEquals(Bits.unsignedIntField(0x12345L, 4, IntFieldWidth(8)), 0x34)
    assertEquals(Bits.signedIntField(0xf0L, 4, IntFieldWidth(4)), -1)
    assert(Bits.isSet(0x40L, 6))
    assert(!Bits.isSet(0x40L, 5))
    assertEquals(Bits.signExtend(0x1fffL, BitWidth(13)), -1L)
    assertEquals(Bits.byteCount(ByteAlignedBitWidth(64)).raw, 8)
  }

  test("bit reversal and population count respect every supported width") {
    val values = Seq(0L, -1L, Long.MinValue, Long.MaxValue, 0x8877665544332211L)
    (1 to 64).foreach { width =>
      val bitWidth = BitWidth.either(width).fold(message => fail(message), value => value)
      values.foreach { value =>
        val digits          = java.lang.Long.toBinaryString(value).reverse.padTo(64, '0').take(width)
        val expectedReverse = BigInt(digits, 2).longValue
        assertEquals(Bits.reverseBits(value, bitWidth), expectedReverse)
        assertEquals(Bits.bitCount(value, bitWidth), digits.count(_ == '1'))
        assertEquals(
          Bits.reverseBits(Bits.reverseBits(value, bitWidth), bitWidth),
          Bits.truncate(value, bitWidth)
        )
      }
    }
    Seq(0, -1, 65).foreach { width =>
      assert(BitWidth.either(width).isLeft)
    }
    assert(ByteAlignedBitWidth.either(7).isLeft)
  }

  test("32-bit words slice and join without sign extension") {
    assertEquals(Bits.unsigned32(-1).raw, Bits.Mask32)
    assertEquals(Bits.low8(0x12345L), 0x45L)
    assertEquals(Bits.low8Int(0x12345L), 0x45)
    assertEquals(Bits.low16(0x12345L).raw, 0x2345L)
    assertEquals(Bits.low16Int(0x12345L), 0x2345)
    assertEquals(Bits.low32(0x123456789abcdef0L).raw, 0x9abcdef0L)
    assertEquals(Bits.signed32(0x123456789abcdef0L), 0x9abcdef0)
    assertEquals(Bits.high32(0x123456789abcdef0L).raw, 0x12345678L)
    assertEquals(Bits.join32(0x9abcdef0L, 0x12345678L), 0x123456789abcdef0L)
  }

  test("IEEE-754 interchange encodings round trip representative values") {
    assertEquals(IEEE754.floatToBinary16(1.5f).raw, 0x3e00L)
    assertEquals(IEEE754.binary16ToFloat(Binary16(0x3e00L)), 1.5f)
    assertEquals(IEEE754.floatToBinary32(-0.0f).raw, 0x80000000L)
    assertEquals(java.lang.Float.floatToRawIntBits(IEEE754.binary32ToFloat(Binary32(0x7fc01234L))), 0x7fc01234)
    assertEquals(IEEE754.doubleToBinary64(-0.0), Long.MinValue)
    assertEquals(
      java.lang.Double.doubleToRawLongBits(IEEE754.binary64ToDouble(0x7ff8000000001234L)),
      0x7ff8000000001234L
    )
    assert(FloatingPointWidth.either(8).isLeft)
  }

  test("byte ranges reject negative and overflowing slices") {
    val start: Option[Int] = ByteRange.start(16, 12L, 4)
    assertEquals(start, Some(12))
    assertEquals(ByteRange.start(16, 13L, 4), None)
    assertEquals(ByteRange.contains(16, Long.MaxValue, 1L), false)
    assertEquals(ByteRange.contains(16, -1L, 1L), false)
  }

  test("little-endian array access validates the complete runtime range") {
    val bytes = Array.ofDim[Byte](8)
    val _     = intercept[IllegalArgumentException](LittleEndian.decode(bytes, -1, ScalarByteWidth.Byte))
    val _     = intercept[IllegalArgumentException](LittleEndian.decode(bytes, 7, ScalarByteWidth.Word))
    val _     = intercept[IllegalArgumentException](LittleEndian.write(0L, bytes, -1, ScalarByteWidth.Byte))
    val _     = intercept[IllegalArgumentException](LittleEndian.write(0L, bytes, 7, ScalarByteWidth.Word))
  }

  test("little-endian fixed-width integer readers own their narrowing") {
    val bytes = Array[Byte](0x34, 0x12, 0xef.toByte, 0xcd.toByte, 0xab.toByte, 0x89.toByte)
    assertEquals(LittleEndian.decodeUnsignedInt16(bytes, 0), 0x1234)
    assertEquals(LittleEndian.decodeInt32(bytes, 2), 0x89abcdef)
  }
}
