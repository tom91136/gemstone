package net.kurobako.gemstone.texture

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.texture.TextureFixtures.*

class TexelFormatSuite extends Suite {
  private def encode(format: TexelFormat, values: Double*): Vector[Int] =
    format.encode(values.toVector).map(_ & 0xff).toVector

  test("sizes and offsets follow the packed channels") {
    val format = TexelFormat(ChannelKind.Unsigned, width = 16, channels = 3)
    assertEquals(format.texelBytes, 6)
    assertEquals(format.channels, 3)
    assertEquals((0 until 3).map(format.offset).toVector, Vector(0, 2, 4))
  }

  test("valid formats are uniform in a supported width") {
    assert(TexelFormat(ChannelKind.Float, width = 16, channels = 4).isValid)
    assert(TexelFormat(ChannelKind.Float, width = 32, channels = 1).isValid)
    assert(TexelFormat(ChannelKind.Signed, width = 8, channels = 2).isValid)
    assert(TexelFormat(ChannelKind.UnsignedNormalised, width = 16, channels = 4).isValid)
    assert(!TexelFormat(ChannelKind.Float, width = 8, channels = 1).isValid)
    assert(!TexelFormat(ChannelKind.Unsigned, width = 12, channels = 1).isValid)
    assert(!TexelFormat(ChannelKind.SignedNormalised, width = 32, channels = 1).isValid)
    assert(!TexelFormat(ChannelKind.Unsigned, Vector(8, 16)).isValid)
    assert(!TexelFormat(ChannelKind.Unsigned, Vector.empty).isValid)
    assert(!TexelFormat(ChannelKind.Unsigned, width = 8, channels = 5).isValid)
  }

  test("normalised reads need narrow integers and sRGB needs 8-bit unsigned") {
    assert(U8x4.supports(ReadMode.NormalisedFloat, srgb = true))
    assert(U8x4.supports(ReadMode.ElementType, srgb = false))
    assert(!U8x4.supports(ReadMode.ElementType, srgb = true))
    assert(!U32x1.supports(ReadMode.NormalisedFloat, srgb = false))
    assert(U32x1.supports(ReadMode.ElementType, srgb = false))
    assert(!S8x1.supports(ReadMode.NormalisedFloat, srgb = true))
    assert(F32x1.supports(ReadMode.NormalisedFloat, srgb = false))
    assert(!F32x1.supports(ReadMode.NormalisedFloat, srgb = true))
  }

  test("only element-type reads of integer channels are integral") {
    assert(U8x4.isIntegral(ReadMode.ElementType))
    assert(!U8x4.isIntegral(ReadMode.NormalisedFloat))
    assert(!F32x1.isIntegral(ReadMode.ElementType))
    assert(!TexelFormat(ChannelKind.UnsignedNormalised, width = 8, channels = 1).isIntegral(ReadMode.ElementType))
  }

  test("32-bit integer channels read raw whatever the read mode") {
    val ones = Array.fill[Byte](4)(0xff.toByte)
    assertEquals(
      TexelFormat(
        ChannelKind.Signed,
        width = 32,
        channels = 1
      ).channelValue(ones, start = 0, channel = 0, mode = ReadMode.NormalisedFloat, srgb = false),
      -1.0
    )
    assertEquals(
      TexelFormat(
        ChannelKind.Unsigned,
        width = 32,
        channels = 1
      ).channelValue(ones, start = 0, channel = 0, mode = ReadMode.NormalisedFloat, srgb = false),
      4294967295.0
    )
    assert(TexelFormat(ChannelKind.Signed, width = 32, channels = 1).isIntegral(ReadMode.NormalisedFloat))
    assertEquals(
      TexelFormat(ChannelKind.Signed, width = 32, channels = 1).decodeMode(ReadMode.NormalisedFloat),
      ReadMode.ElementType
    )
    assertEquals(
      TexelFormat(ChannelKind.Signed, width = 16, channels = 1).decodeMode(ReadMode.NormalisedFloat),
      ReadMode.NormalisedFloat
    )
  }

  test("normalised kinds read as floats whatever the read mode") {
    val unorm = TexelFormat(ChannelKind.UnsignedNormalised, width = 16, channels = 1)
    assertEquals(unorm.texel(Array(0xff, 0xff).map(_.toByte), 0, ReadMode.ElementType, srgb = false)(0), 1.0)
    val snorm = TexelFormat(ChannelKind.SignedNormalised, width = 8, channels = 1)
    assertEquals(snorm.texel(Array(0x81).map(_.toByte), 0, ReadMode.ElementType, srgb = false)(0), -1.0)
    assertEquals(snorm.texel(Array(0x80).map(_.toByte), 0, ReadMode.ElementType, srgb = false)(0), -1.0)
  }

  test("16-bit unsigned normalised reads divide by 65535") {
    val format = TexelFormat(ChannelKind.Unsigned, width = 16, channels = 1)
    val value  = format.channelValue(Array(0x34, 0x12).map(_.toByte), 0, 0, ReadMode.NormalisedFloat, srgb = false)
    assertEqualsDouble(value, 0x1234.toDouble / 65535.0, 0.0)
  }

  test("sRGB leaves the fourth channel linear") {
    val bytes = Array(10, 20, 30, 40).map(_.toByte)
    val texel = U8x4.texel(bytes, 0, ReadMode.NormalisedFloat, srgb = true)
    assertEquals(texel.take(3), Vector(10, 20, 30).map(v => SrgbTransfer.toLinear(v / 255.0)))
    assertEquals(texel(3), 40.0 / 255.0)
  }

  test("texel reads start at the given offset") {
    assertEquals(
      U8x1.texel(Array(1, 2, 3).map(_.toByte), 2, ReadMode.ElementType, srgb = false),
      Vector(3.0, 0.0, 0.0, 1.0)
    )
  }

  test("integer stores saturate") {
    assertEquals(encode(U8x1, 300.0), Vector(255))
    assertEquals(encode(U8x1, -5.0), Vector(0))
    assertEquals(encode(U8x1, Double.NaN), Vector(0))
    assertEquals(encode(S8x1, 200.0), Vector(127))
    assertEquals(encode(S8x1, -200.0), Vector(0x80))
    assertEquals(encode(U32x1, 5e9), Vector(0xff, 0xff, 0xff, 0xff))
    assertEquals(encode(S16x2, -2.0, 1.0), Vector(0xfe, 0xff, 0x01, 0x00))
  }

  test("normalised stores round to nearest with ties away from zero") {
    val unorm = TexelFormat(ChannelKind.UnsignedNormalised, width = 8, channels = 1)
    assertEquals(encode(unorm, 0.5), Vector(128))
    assertEquals(encode(unorm, 2.0), Vector(255))
    assertEquals(encode(unorm, -1.0), Vector(0))
    assertEquals(encode(unorm, Double.NaN), Vector(0))
    val snorm = TexelFormat(ChannelKind.SignedNormalised, width = 8, channels = 1)
    assertEquals(encode(snorm, -1.0), Vector(0x81))
    assertEquals(encode(snorm, 0.5), Vector(64))
    assertEquals(encode(snorm, -0.5), Vector(0xc0))
    assertEquals(encode(snorm, 3.0), Vector(127))
  }

  test("float stores narrow and round trip") {
    assertEquals(encode(TexelFormat(ChannelKind.Float, width = 16, channels = 1), 1.5), Vector(0x00, 0x3e))
    assertEquals(encode(F32x1, 1.0), Vector(0x00, 0x00, 0x80, 0x3f))
    val values = Vector(0.25, -3.5, 1024.0, 0.0)
    val bytes  = F32x4.encode(values)
    assertEquals(F32x4.texel(bytes, 0, ReadMode.ElementType, srgb = false), values)
  }

  test("integer stores round trip through reads") {
    Vector(
      U8x4,
      TexelFormat(ChannelKind.Signed, width = 16, channels = 4),
      TexelFormat(ChannelKind.Unsigned, width = 32, channels = 4)
    ).foreach { format =>
      val values = Vector(0.0, 1.0, 100.0, 7.0)
      assertEquals(format.texel(format.encode(values), 0, ReadMode.ElementType, srgb = false), values, s"$format")
    }
  }

  test("normalised stores round trip within half a step") {
    val format = TexelFormat(ChannelKind.UnsignedNormalised, width = 8, channels = 4)
    val values = Vector(0.0, 0.1, 0.5, 1.0)
    val back   = format.texel(format.encode(values), 0, ReadMode.ElementType, srgb = false)
    values.zip(back).foreach((a, b) => assertEqualsDouble(b, a, 0.5 / 255.0))
  }
}
