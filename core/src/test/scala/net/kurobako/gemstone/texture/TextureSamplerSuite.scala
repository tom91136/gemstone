package net.kurobako.gemstone.texture

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.texture.TextureFixtures.*

class TextureSamplerSuite extends Suite {
  private val line = floats(10f, 20f, 30f, 40f)

  private def sample1(d: TextureDescriptor, x: Double, memory: ArrayMemory = line): Double =
    TextureSampler.sample(d, Vector(x), memory).fold(fault => fail(fault.toString), _(0))

  private def lineOf(address: AddressMode, filter: FilterMode, normalised: Boolean = false, border: Float = 0f) =
    descriptor(
      F32x1,
      4,
      address = address,
      filter = filter,
      normalised = normalised,
      border = Vector(border, 0f, 0f, 0f)
    )

  test("address resolution wraps, mirrors, clamps, and marks the border") {
    val wrap = Vector(-5L, -1L, 0L, 3L, 4L, 9L).map(TextureSampler.resolve(AddressMode.Wrap, _, 4))
    assertEquals(wrap, Vector(3L, 3L, 0L, 3L, 0L, 1L))
    val mirror = Vector(-5L, -4L, -2L, -1L, 3L, 4L, 5L, 7L, 8L).map(TextureSampler.resolve(AddressMode.Mirror, _, 4))
    assertEquals(mirror, Vector(3L, 3L, 1L, 0L, 3L, 3L, 2L, 0L, 0L))
    val clamp = Vector(-3L, 0L, 3L, 4L, 100L).map(TextureSampler.resolve(AddressMode.Clamp, _, 4))
    assertEquals(clamp, Vector(0L, 0L, 3L, 3L, 3L))
    val border = Vector(-1L, 0L, 3L, 4L).map(TextureSampler.resolve(AddressMode.Border, _, 4))
    assertEquals(border, Vector(-1L, 0L, 3L, -1L))
  }

  test("extent 1 resolves every index to texel 0 except at the border") {
    Vector(AddressMode.Wrap, AddressMode.Mirror, AddressMode.Clamp).foreach { mode =>
      (-3L to 3L).foreach(index => assertEquals(TextureSampler.resolve(mode, index, 1), 0L, s"$mode $index"))
    }
    assertEquals(TextureSampler.resolve(AddressMode.Border, index = 1, extent = 1), -1L)
  }

  test("point sampling takes the texel containing the coordinate") {
    val d = lineOf(AddressMode.Clamp, FilterMode.Point)
    assertEquals(Vector(0.0, 0.99, 1.0, 3.9).map(sample1(d, _)), Vector(10.0, 10.0, 20.0, 40.0))
  }

  test("point sampling clamps and borders outside the texture") {
    val clamp = lineOf(AddressMode.Clamp, FilterMode.Point)
    assertEquals(Vector(-0.5, -100.0, 4.0, 99.0).map(sample1(clamp, _)), Vector(10.0, 10.0, 40.0, 40.0))
    val border = lineOf(AddressMode.Border, FilterMode.Point, border = 7f)
    assertEquals(Vector(-0.5, 4.0, 2.0).map(sample1(border, _)), Vector(7.0, 7.0, 30.0))
  }

  test("normalised coordinates scale by the extent") {
    val d = lineOf(AddressMode.Clamp, FilterMode.Point, normalised = true)
    assertEquals(Vector(0.0, 0.25, 0.5, 0.999, 1.0).map(sample1(d, _)), Vector(10.0, 20.0, 30.0, 40.0, 40.0))
  }

  test("wrap and mirror apply to normalised coordinates") {
    val wrap = lineOf(AddressMode.Wrap, FilterMode.Point, normalised = true)
    assertEquals(Vector(1.25, -0.25, 2.0, -1.0).map(sample1(wrap, _)), Vector(20.0, 40.0, 10.0, 10.0))
    val mirror = lineOf(AddressMode.Mirror, FilterMode.Point, normalised = true)
    assertEquals(Vector(1.25, -0.25, 0.99, 1.0, 1.01).map(sample1(mirror, _)), Vector(30.0, 10.0, 40.0, 40.0, 40.0))
  }

  test("wrap and mirror behave as clamp for unnormalised coordinates") {
    Vector(AddressMode.Wrap, AddressMode.Mirror).foreach { mode =>
      val d = lineOf(mode, FilterMode.Point)
      assertEquals(Vector(-1.0, 5.0, 9.0).map(sample1(d, _)), Vector(10.0, 40.0, 40.0), s"$mode")
    }
  }

  test("NaN coordinates read texel 0 and infinities saturate") {
    val d = lineOf(AddressMode.Clamp, FilterMode.Point)
    assertEquals(sample1(d, Double.NaN), 10.0)
    assertEquals(sample1(d, Double.PositiveInfinity), 40.0)
    assertEquals(sample1(d, Double.NegativeInfinity), 10.0)
  }

  test("linear filtering blends around texel centres") {
    val d = lineOf(AddressMode.Clamp, FilterMode.Linear)
    assertEquals(Vector(0.5, 1.5, 2.0, 2.5, 3.5).map(sample1(d, _)), Vector(10.0, 20.0, 25.0, 30.0, 40.0))
    assertEquals(sample1(d, 1.75), 22.5)
  }

  test("linear clamp holds the edge texel beyond the centres") {
    val d = lineOf(AddressMode.Clamp, FilterMode.Linear)
    assertEquals(
      Vector(-2.0, 0.0, 0.25, 3.75, 9.0).map(sample1(d, _)),
      Vector.fill(2)(10.0) ++ Vector(10.0, 40.0, 40.0)
    )
  }

  test("linear border blends the border colour into edge samples") {
    val d = lineOf(AddressMode.Border, FilterMode.Linear, border = 0f)
    assertEquals(sample1(d, 0.0), 5.0)
    assertEquals(sample1(d, 0.25), 7.5)
    assertEquals(sample1(d, 4.0), 20.0)
    val coloured = lineOf(AddressMode.Border, FilterMode.Linear, border = 100f)
    assertEquals(sample1(coloured, 0.0), 55.0)
  }

  test("linear wrap blends across the seam and linear mirror repeats the edge") {
    val wrap = lineOf(AddressMode.Wrap, FilterMode.Linear, normalised = true)
    assertEquals(Vector(0.0, 1.0).map(sample1(wrap, _)), Vector(25.0, 25.0))
    val mirror = lineOf(AddressMode.Mirror, FilterMode.Linear, normalised = true)
    assertEquals(Vector(0.0, 1.0).map(sample1(mirror, _)), Vector(10.0, 40.0))
  }

  test("linear weights quantise to 1/256") {
    val d = lineOf(AddressMode.Clamp, FilterMode.Linear)
    assertEquals(sample1(d, 1.5 + 0.001), 20.0)
    assertEquals(sample1(d, 1.5 + 0.002), 20.0 + 10.0 / 256.0)
    assertEquals(sample1(d, 1.5 + 0.998), 29.9609375)
    assertEquals(sample1(d, 1.5 + 0.999), 30.0)
  }

  private val square = floats(1f, 2f, 3f, 4f)

  private def square2(address: AddressMode, filter: FilterMode, border: Float = 0f) =
    descriptor(
      F32x1,
      width = 2,
      height = 2,
      dimensions = 2,
      address = address,
      filter = filter,
      border = Vector(border, 0f, 0f, 0f)
    )

  private def sample2(d: TextureDescriptor, x: Double, y: Double): Double =
    TextureSampler.sample(d, Vector(x, y), square).fold(fault => fail(fault.toString), _(0))

  test("2D point sampling indexes rows by pitch") {
    val d = square2(AddressMode.Clamp, FilterMode.Point)
    assertEquals(
      Vector((0.5, 0.5), (1.5, 0.5), (0.5, 1.5), (1.5, 1.5)).map(sample2(d, _, _)),
      Vector(1.0, 2.0, 3.0, 4.0)
    )
  }

  test("2D point sampling honours a padded pitch") {
    val padded = ArrayMemory(Base, floats(1f, 2f, 0f, 0f, 3f, 4f, 0f, 0f).bytes)
    val d      = descriptor(F32x1, width = 2, height = 2, dimensions = 2, pitch = 16)
    assertEquals(TextureSampler.sample(d, Vector(0.5, 1.5), padded).map(_(0)), Right(3.0))
    assertEquals(TextureSampler.sample(d, Vector(1.5, 1.5), padded).map(_(0)), Right(4.0))
  }

  test("bilinear filtering weights the four texels") {
    val d = square2(AddressMode.Clamp, FilterMode.Linear)
    assertEquals(sample2(d, 1.0, 1.0), 2.5)
    assertEquals(sample2(d, 0.5, 0.5), 1.0)
    assertEquals(sample2(d, 1.0, 0.5), 1.5)
    assertEquals(sample2(d, 0.5, 1.0), 2.0)
    assertEquals(sample2(d, 0.75, 1.25), 2.75)
  }

  test("bilinear border contributes at corners") {
    val d = square2(AddressMode.Border, FilterMode.Linear)
    assertEquals(sample2(d, 0.0, 0.0), 0.25)
    assertEquals(sample2(d, 2.0, 2.0), 1.0)
    assertEquals(sample2(d, 1.0, 0.0), 0.75)
  }

  test("the coordinate count sets the axes sampled whatever the descriptor dimensions") {
    val strip = lineOf(AddressMode.Clamp, FilterMode.Point)
    assertEquals(TextureSampler.sample(strip, Vector(2.5, 0.5), line).map(_(0)), Right(30.0))
    assertEquals(TextureSampler.sample(strip, Vector(2.5, 7.5, 3.0), line).map(_(0)), Right(30.0))
    val rows = descriptor(F32x1, width = 2, height = 2, dimensions = 2)
    assertEquals(TextureSampler.sample(rows, Vector(1.5), square).map(_(0)), Right(2.0))
    val border = lineOf(AddressMode.Border, FilterMode.Point)
    assertEquals(TextureSampler.sample(border, Vector(2.5, 1.5), line).map(_(0)), Right(0.0))
  }

  test("trilinear filtering weights eight texels") {
    val volume = floats((1 to 8).map(_.toFloat)*)
    val d      = descriptor(F32x1, width = 2, height = 2, depth = 2, dimensions = 3, filter = FilterMode.Linear)
    assertEquals(TextureSampler.sample(d, Vector(1.0, 1.0, 1.0), volume).map(_(0)), Right(4.5))
    assertEquals(TextureSampler.sample(d, Vector(0.5, 0.5, 0.5), volume).map(_(0)), Right(1.0))
    assertEquals(TextureSampler.sample(d, Vector(1.5, 1.5, 1.5), volume).map(_(0)), Right(8.0))
    assertEquals(TextureSampler.sample(d, Vector(0.5, 0.5, 1.0), volume).map(_(0)), Right(3.0))
  }

  test("3D point sampling steps by slice pitch") {
    val volume = floats((1 to 8).map(_.toFloat)*)
    val d      = descriptor(F32x1, width = 2, height = 2, depth = 2, dimensions = 3, slicePitch = 16)
    assertEquals(TextureSampler.sample(d, Vector(1.5, 0.5, 1.5), volume).map(_(0)), Right(6.0))
  }

  test("gather returns the footprint in tld4 order") {
    val d = square2(AddressMode.Clamp, FilterMode.Linear)
    assertEquals(TextureSampler.gather(d, Vector(1.0, 1.0), 0, square).map(_.values), Right(Vector(3.0, 4.0, 2.0, 1.0)))
    assertEquals(TextureSampler.gather(d, Vector(0.5, 0.5), 0, square).map(_.values), Right(Vector(3.0, 4.0, 2.0, 1.0)))
  }

  test("gather beyond the edge clamps or reads the border") {
    val clamp = square2(AddressMode.Clamp, FilterMode.Point)
    assertEquals(TextureSampler.gather(clamp, Vector(0.0, 0.0), 0, square).map(_.values), Right(Vector.fill(4)(1.0)))
    val border = square2(AddressMode.Border, FilterMode.Point)
    assertEquals(
      TextureSampler.gather(border, Vector(0.0, 0.0), 0, square).map(_.values),
      Right(Vector(0.0, 1.0, 0.0, 0.0))
    )
  }

  test("gather selects the requested component") {
    val texels = floats((0 until 16).map(i => (10 * (i / 4) + i % 4).toFloat)*)
    val d      = descriptor(F32x4, width = 2, height = 2, dimensions = 2)
    assertEquals(
      TextureSampler.gather(d, Vector(1.0, 1.0), 2, texels).map(_.values),
      Right(Vector(22.0, 32.0, 12.0, 2.0))
    )
  }

  test("fetch applies address modes to integer coordinates") {
    val clamp = lineOf(AddressMode.Clamp, FilterMode.Linear)
    assertEquals(
      Vector(-1L, 0L, 3L, 9L).map(i => TextureSampler.fetch(clamp, Vector(i), line).map(_(0))),
      Vector(Right(10.0), Right(10.0), Right(40.0), Right(40.0))
    )
    val border = lineOf(AddressMode.Border, FilterMode.Point, border = 3f)
    assertEquals(TextureSampler.fetch(border, Vector(4L), line).map(_(0)), Right(3.0))
    val wrap = lineOf(AddressMode.Wrap, FilterMode.Point, normalised = true)
    assertEquals(TextureSampler.fetch(wrap, Vector(5L), line).map(_(0)), Right(40.0))
  }

  test("element-type reads of integer channels are integral and fill missing channels") {
    val memory = memoryOf(1, 2, 3, 4)
    val d      = descriptor(U8x4, 1)
    assertEquals(
      TextureSampler.sample(d, Vector(0.5), memory),
      Right(Texel(Vector(1.0, 2.0, 3.0, 4.0), integral = true))
    )
    val one = descriptor(U8x1, 4)
    assertEquals(
      TextureSampler.sample(one, Vector(2.5), memory),
      Right(Texel(Vector(3.0, 0.0, 0.0, 1.0), integral = true))
    )
  }

  test("integral textures ignore the linear filter") {
    val d = descriptor(U8x1, 2, filter = FilterMode.Linear)
    assertEquals(TextureSampler.sample(d, Vector(1.0), memoryOf(10, 20)).map(_(0)), Right(20.0))
  }

  test("normalised reads convert integers to floats") {
    val unsigned = descriptor(U8x4, 1, readMode = ReadMode.NormalisedFloat)
    val texel    =
      TextureSampler.sample(unsigned, Vector(0.5), memoryOf(0, 51, 255, 128)).fold(
        fault => fail(fault.toString),
        identity
      )
    assert(!texel.integral)
    assertEquals(texel.values, Vector(0.0, 0.2, 1.0, 128.0 / 255.0).map(_.toFloat.toDouble))
    val signed = descriptor(S8x1, 4, readMode = ReadMode.NormalisedFloat)
    val values = (0 until 4).map(i =>
      TextureSampler.sample(signed, Vector(i + 0.5), memoryOf(-128, -127, 0, 127)).map(_(0))
    )
    assertEquals(values.toVector, Vector(Right(-1.0), Right(-1.0), Right(0.0), Right(1.0)))
  }

  test("sRGB converts x, y, and z to linear and leaves w") {
    val d     = descriptor(U8x4, 1, readMode = ReadMode.NormalisedFloat, srgb = true)
    val texel =
      TextureSampler.sample(d, Vector(0.5), memoryOf(128, 128, 128, 128)).fold(
        fault => fail(fault.toString),
        identity
      )
    val linear = SrgbTransfer.toLinear(128.0 / 255.0).toFloat.toDouble
    assertEquals(texel.values, Vector(linear, linear, linear, (128.0 / 255.0).toFloat.toDouble))
  }

  test("sRGB filtering blends linear values") {
    val d     = descriptor(U8x1, 2, readMode = ReadMode.NormalisedFloat, srgb = true, filter = FilterMode.Linear)
    val value =
      TextureSampler.sample(d, Vector(1.0), memoryOf(0, 255)).map(_(0)).fold(fault => fail(fault.toString), identity)
    assertEquals(value, 0.5)
  }

  test("half floats widen exactly and 32-bit integers stay exact") {
    val half = ArrayMemory(Base, Array(0x00, 0x3e, 0x00, 0xc0).map(_.toByte))
    assertEquals(
      TextureSampler.sample(descriptor(F16x2, 1), Vector(0.5), half).map(_.values),
      Right(Vector(1.5, -2.0, 0.0, 1.0))
    )
    val wide = memoryOf(0xff, 0xff, 0xff, 0xff)
    assertEquals(TextureSampler.sample(descriptor(U32x1, 1), Vector(0.5), wide).map(_(0)), Right(4294967295.0))
    val negative = descriptor(TexelFormat(ChannelKind.Signed, width = 32, channels = 1), 1)
    assertEquals(TextureSampler.sample(negative, Vector(0.5), wide).map(_(0)), Right(-1.0))
  }

  test("signed 16-bit channels sign extend") {
    val memory = ArrayMemory(Base, Array(0xfe, 0xff, 0x02, 0x80).map(_.toByte))
    assertEquals(
      TextureSampler.sample(descriptor(S16x2, 1), Vector(0.5), memory).map(_.values),
      Right(Vector(-2.0, -32766.0, 0.0, 1.0))
    )
  }

  test("unmapped texels fault with their address") {
    val d = descriptor(F32x1, 4)
    assertEquals(TextureSampler.sample(d, Vector(3.5), floats(1f, 2f)), Left(TexelFault.Unmapped(Base + 12)))
    val linear = descriptor(F32x1, 4, filter = FilterMode.Linear)
    assert(TextureSampler.sample(linear, Vector(2.0), floats(1f, 2f)).isLeft)
  }

  private def mip(
      lod: LevelOfDetail,
      x: Double = 0.5,
      y: Double = 0.5,
      filter: FilterMode = FilterMode.Point,
      mipFilter: FilterMode = FilterMode.Point,
      minLod: Float = 0f,
      maxLod: Float = 2f,
      bias: Float = 0f
  ): Double = {
    val d = mipmapped(filter, mipFilter, minLod, maxLod, bias)
    TextureSampler.sample(d, Vector(x, y), ArrayMemory(Base, mipBytes), lod).fold(fault => fail(fault.toString), _(0))
  }

  private def explicit(level: Double) = LevelOfDetail.Explicit(level)

  test("a mipmapped texture reads the level an explicit level of detail names") {
    assertEquals(Vector(0.0, 1.0, 2.0).map(level => mip(explicit(level))), Vector(10.0, 20.0, 40.0))
  }

  test("unnormalised coordinates count texels of level zero on every level") {
    assertEquals(mip(explicit(1.0), x = 3.5, y = 3.5), 23.0)
    assertEquals(mip(explicit(1.0), x = 3.5, y = 0.5), 21.0)
  }

  test("nearest mip filtering rounds the level of detail with ties going up") {
    assertEquals(
      Vector(0.4, 0.5, 0.6, 1.5, 9.0).map(level => mip(explicit(level))),
      Vector(10.0, 20.0, 20.0, 40.0, 40.0)
    )
  }

  test("linear mip filtering blends the two levels around the level of detail") {
    def blended(level: Double) = mip(explicit(level), mipFilter = FilterMode.Linear)
    assertEquals(Vector(0.0, 0.5, 1.25, 2.0, 7.0).map(blended(_)), Vector(10.0, 15.0, 25.0, 40.0, 40.0))
  }

  test("level of detail bias applies before the clamps") {
    assertEquals(mip(explicit(0.0), bias = 1f), 20.0)
    assertEquals(mip(explicit(0.0), bias = 5f, maxLod = 1f), 20.0)
    assertEquals(mip(explicit(0.0), minLod = 1f), 20.0)
    assertEquals(mip(explicit(2.0), maxLod = 0f), 10.0)
  }

  test("gradients select the level from the larger footprint in texels") {
    def gradient(dx: Vector[Double], dy: Vector[Double]) = mip(LevelOfDetail.Gradient(dx, dy))
    assertEquals(gradient(Vector(1.0, 0.0), Vector(0.0, 1.0)), 10.0)
    assertEquals(gradient(Vector(2.0, 0.0), Vector(0.0, 1.0)), 20.0)
    assertEquals(gradient(Vector(3.0, 0.0), Vector(0.0, 4.0)), 40.0)
    assertEquals(gradient(Vector(0.0, 0.0), Vector(0.0, 0.0)), 10.0)
  }

  test("normalised gradients scale by the level zero extent") {
    val d          = mipmapped()
    val normalised = d.copy(normalisedCoordinates = true)
    val lod        = LevelOfDetail.Gradient(Vector(0.5, 0.0), Vector(0.0, 0.25))
    assertEquals(
      TextureSampler.sample(normalised, Vector(0.1, 0.1), ArrayMemory(Base, mipBytes), lod).fold(_ => -1.0, _(0)),
      20.0
    )
  }

  test("a texel fetch reads the named level") {
    val d                 = mipmapped()
    def fetch(level: Int) =
      TextureSampler.fetch(d, Vector(1L, 1L), ArrayMemory(Base, mipBytes), level).fold(_ => -1.0, _(0))
    assertEquals(Vector(0, 1, 2, 9).map(fetch(_)), Vector(10.0, 23.0, 40.0, 40.0))
  }

  test("a mipmapped texture reports a fault for an unreadable level table") {
    val d      = mipmapped()
    val broken = d.copy(mip = d.mip.map(_.copy(table = Base + 0x10000)))
    assert(TextureSampler.sample(broken, Vector(0.5, 0.5), ArrayMemory(Base, mipBytes), explicit(1.0)).isLeft)
  }
}
