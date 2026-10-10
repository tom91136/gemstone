package net.kurobako.gemstone.binary

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.util.Try

class BinarySuite extends munit.FunSuite {
  test("width conversions preserve finite scalar choices and allow general whole-byte widths") {
    val scalarBits: Int in (8, 16, 32, 64)                     = 32
    val scalarBytes: Int in (1, 2, 4, 8)                       = scalarBits.toMemoryValueByteWidth
    val packedBytes: Int in 1 ~ 8                              = 3
    val packedBits: Int where (Between[8, 64] & MultipleOf[8]) = packedBytes.toBitWidth
    assertEquals(scalarBytes.raw, 4)
    assertEquals(packedBits.raw, 24)
  }

  // Fixed bytes and boundary values below are independent expectations for the wire/bit representation.
  // Do not derive these from the encoder or the constants whose values these cases verify.

  test("little-endian scalars round-trip through arrays and runtime collections") {
    val value               = 0x8877665544332211L
    val width: Int in 0 ~ 8 = 8
    val bytes               = LittleEndian.toArray(value, width)

    assertEquals(bytes.toSeq, Seq[Byte](0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88.toByte))
    assertEquals(LittleEndian.decode(bytes = bytes, offset = 0, size = width), value)
    assertEquals(LittleEndian.decode(bytes = Vec.from(bytes), offset = 0, size = width), value)
  }

  test("fixed-width bit operations share one validated definition") {
    assertEquals(Bits.mask(13), 0x1fffL)
    assertEquals(Bits.truncate(0x12345L, 16), 0x2345L)
    assertEquals(Bits.unsignedField(0x12345L, 4, 8), 0x34L)
    assertEquals(Bits.signedField(0xf0L, 4, 4), -1L)
    assertEquals(Bits.unsignedIntField(0x12345L, 4, 8).raw, 0x34)
    assertEquals(Bits.signedIntField(0xf0L, 4, 4), -1)
    assert(Bits.isSet(0x40L, 6))
    assert(!Bits.isSet(0x40L, 5))
    assertEquals(Bits.signExtend(0x1fffL, 13), -1L)
    assertEquals(Bits.byteCount(64).raw, 8)
  }

  test("bit reversal and population count respect every supported width") {
    val values = Seq(0L, -1L, Long.MinValue, Long.MaxValue, 0x8877665544332211L)
    (1 to 64).foreach { width =>
      val bitWidth = width.refine[Int in 1 ~ 64].fold(
        message => fail(message),
        value => value
      )
      values.foreach { value =>
        val digits          = java.lang.Long.toBinaryString(value).reverse.padTo(64, '0').take(width)
        val expectedReverse = BigInt(digits, 2).toLong
        assertEquals(Bits.reverseBits(value, bitWidth), expectedReverse)
        assertEquals(Bits.bitCount(value, bitWidth).raw, digits.count(_ == '1'))
        assertEquals(
          Bits.reverseBits(Bits.reverseBits(value, bitWidth), bitWidth),
          Bits.truncate(value, bitWidth)
        )
      }
    }
    Seq(0, -1, 65).foreach { width =>
      assert(width.refine[Int in 1 ~ 64].isLeft)
    }
    val unaligned = Integer.parseInt("7")
    assert(Try(unaligned.unsafe[Int where (Between[0, 64] & MultipleOf[8])]).isFailure)
  }

  test("32-bit words slice and join without sign extension") {
    assertEquals(Bits.unsigned32(-1).toLong, Bits.Mask32)
    assertEquals(Bits.low8Int(0x12345L).raw, 0x45)
    assertEquals(Bits.low16(0x12345L).toLong, 0x2345L)
    assertEquals(Bits.low16Int(0x12345L).raw, 0x2345)
    assertEquals(Bits.low32(0x123456789abcdef0L).toLong, 0x9abcdef0L)
    assertEquals(Bits.signed32(0x123456789abcdef0L), 0x9abcdef0)
    assertEquals(Bits.high32(0x123456789abcdef0L).toLong, 0x12345678L)
    assertEquals(Bits.join32(low = 0x9abcdef0L, high = 0x12345678L), 0x123456789abcdef0L)
  }

  test("IEEE-754 interchange encodings round trip representative values") {
    assertEquals(IEEE754.floatToBinary16(1.5f).toLong, 0x3e00L)
    assertEquals(IEEE754.binary16ToFloat(Binary16(0x3e00L)), 1.5f)
    assertEquals(IEEE754.floatToBinary32(-0.0f).toLong, 0x80000000L)
    assertEquals(java.lang.Float.floatToRawIntBits(IEEE754.binary32ToFloat(Binary32(0x7fc01234L))), 0x7fc01234)
    assertEquals(IEEE754.doubleToBinary64(-0.0), Long.MinValue)
    assertEquals(
      java.lang.Double.doubleToRawLongBits(IEEE754.binary64ToDouble(0x7ff8000000001234L)),
      0x7ff8000000001234L
    )
    val unsupported = Integer.parseInt("8")
    assert(Try(unsupported.unsafe[Int in (16, 32, 64)]).isFailure)
  }

  test("byte ranges reject negative and overflowing slices") {
    val start: Option[Int >= 0] = ByteRange.start(length = 16, offset = 12L, size = 4)
    assertEquals(start.map(_.raw), Some(12))
    assertEquals(ByteRange.start(length = 16, offset = 13L, size = 4), None)
    assertEquals(ByteRange.contains(length = 16, offset = Long.MaxValue, size = 1L), false)
    assertEquals(ByteRange.contains(length = 16, offset = -1L, size = 1L), false)
  }

  test("empty byte ranges are valid through the end of a buffer") {
    assertEquals(ByteRange.start(length = 0, offset = 0L, size = 0).map(_.raw), Some(0))
    assertEquals(ByteRange.start(length = 16, offset = 0L, size = 0).map(_.raw), Some(0))
    assertEquals(ByteRange.start(length = 16, offset = 16L, size = 0).map(_.raw), Some(16))
    assertEquals(ByteRange.start(length = 16, offset = 17L, size = 0), None)
    assertEquals(ByteRange.start(length = 0, offset = 0L, size = 1), None)
    assertEquals(ByteRange.start(length = -1, offset = 0L, size = 0), None)
    assertEquals(ByteRange.start(length = 16, offset = 0L, size = -1), None)
  }

  test("little-endian array access validates the complete runtime range") {
    val bytes = Array.ofDim[Byte](8)
    val _     = intercept[IllegalArgumentException](LittleEndian.decode(bytes = bytes, offset = -1, size = 1))
    val _     = intercept[IllegalArgumentException](LittleEndian.decode(bytes = bytes, offset = 7, size = 2))
    val _ = intercept[IllegalArgumentException](LittleEndian.write(value = 0L, bytes = bytes, offset = -1, size = 1))
    val _ = intercept[IllegalArgumentException](LittleEndian.write(value = 0L, bytes = bytes, offset = 7, size = 2))
  }

  test("little-endian fixed-width integer readers own their narrowing") {
    val bytes = Array[Byte](0x34, 0x12, 0xef.toByte, 0xcd.toByte, 0xab.toByte, 0x89.toByte)
    assertEquals(LittleEndian.decodeUInt16(bytes, 0).raw, 0x1234)
    assertEquals(LittleEndian.decodeInt32(bytes, 2), 0x89abcdef)
  }
  test("positive-width encoding retains nonemptiness and low byte order") {
    val one: NonEmpty[Vec[Byte]]   = LittleEndian.toNonEmptyVec(0x12345678L, 1)
    val eight: NonEmpty[Vec[Byte]] = LittleEndian.toNonEmptyVec(-1L, 8)
    assertEquals(one, Vec(0x78.toByte))
    assertEquals(eight, Vec.fill(8)(0xff.toByte))
  }

}
