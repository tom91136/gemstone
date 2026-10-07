package net.kurobako.gemstone.binary

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.util.Random

class IEEE754Suite extends munit.FunSuite {
  private val roundings = IEEE754.Rounding.values.toSeq

  private def exactRound(value: java.math.BigDecimal, width: Int in (16, 32, 64), rounding: IEEE754.Rounding): Long = {
    // reference: walk the finite encodings around the binary64 nearest value
    val nearest                                   = IEEE754.fromDouble(BigDecimal(value).toDouble, width)
    val sign                                      = 1L << (width.raw - 1)
    def decoded(bits: Long): java.math.BigDecimal = java.math.BigDecimal(IEEE754.toDouble(bits, width))
    def next(bits: Long, up: Boolean): Long       =
      if ((bits & ~sign) == 0L) { if (up) 1L else sign | 1L }
      else if (((bits & sign) == 0L) == up) bits + 1L
      else bits - 1L
    val candidates = Seq(next(next(nearest, false), false), next(nearest, false), nearest, next(nearest, true))
      .filter(bits => IEEE754.toDouble(bits, width).isFinite)
    val below = candidates.filter(bits => decoded(bits).compareTo(value) <= 0).maxBy(bits => decoded(bits))
    val above = candidates.filter(bits => decoded(bits).compareTo(value) >= 0).minBy(bits => decoded(bits))
    rounding match {
      case IEEE754.Rounding.Down        => below
      case IEEE754.Rounding.Up          => above
      case IEEE754.Rounding.TowardZero  => if (value.signum() >= 0) below else above
      case IEEE754.Rounding.NearestEven =>
        val lower = value.subtract(decoded(below)).abs
        val upper = decoded(above).subtract(value).abs
        val order = lower.compareTo(upper)
        if (order < 0) below else if (order > 0) above else if ((below & 1L) == 0L) below else above
    }
  }

  test("directed binary64 narrowing matches exact rounding for half, single and bfloat16") {
    val random = Random(0x16L)
    val inputs = Vec(1.0, 1.0 + Math.ulp(1.0), 1.0 / 3.0, -1.0 / 3.0, 65504.0, 6.0e-8, -6.0e-8, 1.0e-40, 3.0e38) ++
      Vec.fill(500)(random.nextGaussian() * Math.pow(2.0, random.nextInt(40) - 20))
    for {
      value    <- inputs
      rounding <- roundings
      width    <- Seq[Int in (16, 32, 64)](16, 32)
      if Math.abs(value) < (if (width.raw == 16) 65504.0 else Float.MaxValue.toDouble)
    } assertEquals(
      IEEE754.fromDouble(value, width, rounding),
      exactRound(java.math.BigDecimal(value), width, rounding),
      clues(value, width, rounding)
    )
    for (rounding <- roundings) {
      val value = 1.0 + 1.0 / 1024.0
      val bits  = IEEE754.doubleToBfloat16(value, rounding).toLong
      val up    = rounding == IEEE754.Rounding.Up
      assertEquals(bits, if (up) 0x3f81L else 0x3f80L, clues(rounding))
    }
  }

  test("directed narrowing overflows to infinity or the largest finite value by rounding direction") {
    assertEquals(IEEE754.fromDouble(1.0e10, 16, IEEE754.Rounding.NearestEven), 0x7c00L)
    assertEquals(IEEE754.fromDouble(1.0e10, 16, IEEE754.Rounding.TowardZero), 0x7bffL)
    assertEquals(IEEE754.fromDouble(-1.0e10, 16, IEEE754.Rounding.Up), 0xfbffL)
    assertEquals(IEEE754.fromDouble(-1.0e10, 16, IEEE754.Rounding.Down), 0xfc00L)
    assertEquals(IEEE754.fromDouble(1.0e300, 32, IEEE754.Rounding.Down), 0x7f7fffffL)
  }

  test("bfloat16 keeps NaN payloads as quiet NaNs instead of rounding them to infinity") {
    val signalling = java.lang.Float.intBitsToFloat(0x7f800001)
    assertEquals(IEEE754.doubleToBfloat16(signalling.toDouble).toLong, 0x7fc0L)
    assertEquals(IEEE754.doubleToBfloat16(Float.PositiveInfinity.toDouble).toLong, 0x7f80L)
    assertEquals(IEEE754.doubleToBfloat16(java.lang.Float.intBitsToFloat(0x3f808000).toDouble).toLong, 0x3f80L)
    assertEquals(IEEE754.doubleToBfloat16(java.lang.Float.intBitsToFloat(0x3f818000).toDouble).toLong, 0x3f82L)
    assertEquals(IEEE754.bfloat16ToFloat(Binary16.low(0xbf80L)), -1.0f)
  }

  test("integer conversion to half and bfloat16 rounds once and saturates by direction") {
    assertEquals(IEEE754.integerToBinary(2049L, false, 16), 0x6800L)
    assertEquals(IEEE754.integerToBinary(2049L, false, 16, IEEE754.Rounding.Up), 0x6801L)
    assertEquals(IEEE754.integerToBinary(-1L, true, 16), 0x7c00L)
    assertEquals(IEEE754.integerToBinary(-1L, true, 16, IEEE754.Rounding.TowardZero), 0x7bffL)
    assertEquals(IEEE754.integerToBfloat16(257L, false).toLong, 0x4380L)
    assertEquals(IEEE754.integerToBfloat16(257L, false, IEEE754.Rounding.Up).toLong, 0x4381L)
  }

  test("saturating float-to-integer conversion clamps, rounds by direction and maps NaN to zero") {
    import IEEE754.Rounding.*
    assertEquals(IEEE754.toIntegerSaturated(1.0e10, 32, true, TowardZero), 0x7fffffffL)
    assertEquals(IEEE754.toIntegerSaturated(-1.0e10, 32, true, TowardZero), 0x80000000L)
    assertEquals(IEEE754.toIntegerSaturated(-1.0, 32, false, TowardZero), 0L)
    assertEquals(IEEE754.toIntegerSaturated(1.0e10, 32, false, TowardZero), 0xffffffffL)
    assertEquals(IEEE754.toIntegerSaturated(70000.0, 16, true, TowardZero), 0x7fffL)
    assertEquals(IEEE754.toIntegerSaturated(Double.NaN, 64, true, NearestEven), 0L)
    assertEquals(IEEE754.toIntegerSaturated(1.0e30, 64, true, NearestEven), Long.MaxValue)
    assertEquals(IEEE754.toIntegerSaturated(1.0e30, 64, false, NearestEven), -1L)
    assertEquals(IEEE754.toIntegerSaturated(1.8446744073709550e19, 64, false, TowardZero), 0xfffffffffffff800L)
    assertEquals(IEEE754.toIntegerSaturated(2.5, 32, true, NearestEven), 2L)
    assertEquals(IEEE754.toIntegerSaturated(-2.5, 32, true, Down), 0xfffffffdL)
    assertEquals(IEEE754.toIntegerSaturated(-2.5, 32, true, Up), 0xfffffffeL)
    assertEquals(IEEE754.toIntegerSaturated(-2.5, 32, true, TowardZero), 0xfffffffeL)
  }

  test("minNum and maxNum ignore one NaN and order signed zeros; minimum and maximum propagate NaN") {
    val nan  = 0x7fc00001L
    val one  = 0x3f800000L
    val zero = 0x0L
    val neg  = 0x80000000L
    assertEquals(IEEE754.minNum(nan, one, 32), one)
    assertEquals(IEEE754.maxNum(one, nan, 32), one)
    assertEquals(IEEE754.minNum(zero, neg, 32), neg)
    assertEquals(IEEE754.minNum(neg, zero, 32), neg)
    assertEquals(IEEE754.maxNum(neg, zero, 32), zero)
    assertEquals(IEEE754.maxNum(zero, neg, 32), zero)
    assert(IEEE754.isNaN(IEEE754.minNum(nan, nan, 32), 32))
    assert(IEEE754.isNaN(IEEE754.minimum(one, nan, 32), 32))
    assert(IEEE754.isNaN(IEEE754.maximum(0x7f800001L, one, 32), 32))
    assertEquals(IEEE754.maximum(0x7f800001L, one, 32), 0x7fc00001L)
    assertEquals(IEEE754.minimum(0xc000L, 0x3c00L, 16), 0xc000L)
    assertEquals(IEEE754.maxNum(0x7e00L, 0xbc00L, 16), 0xbc00L)
    assertEquals(IEEE754.minNum(0xfff0000000000000L, 0x0L, 64), 0xfff0000000000000L)
  }

  test("binary16 fma rounds once where a binary32 intermediate would double-round") {
    // (1 + 2^-10)(1 - 2^-11) + 9 * 2^-24 = 1 + 2^-11 + 2^-24 lies just above a binary16 tie, but is itself a
    // binary32 tie that rounds down onto the binary16 tie
    val (a, b, c) = (0x3c01L, 0x3bffL, 0x0009L)
    val viaFloat  = IEEE754.floatToBinary16(
      Math.fma(
        IEEE754.binary16ToFloat(Binary16.low(a)),
        IEEE754.binary16ToFloat(Binary16.low(b)),
        IEEE754.binary16ToFloat(Binary16.low(c))
      )
    )
    assertEquals(viaFloat.toLong, 0x3c00L)
    assertEquals(IEEE754.fusedMultiplyAdd(a, b, c, 16), 0x3c01L)
    val random = Random(0xf16L)
    for (_ <- 0 until 20000) {
      val x       = random.nextInt(0x7c00).toLong | (if (random.nextBoolean()) 0x8000L else 0L)
      val y       = random.nextInt(0x7c00).toLong
      val z       = random.nextInt(0x7c00).toLong | (if (random.nextBoolean()) 0x8000L else 0L)
      val product =
        java.math.BigDecimal(IEEE754.toDouble(x, 16)).multiply(java.math.BigDecimal(IEEE754.toDouble(y, 16)))
      val sum = product.add(java.math.BigDecimal(IEEE754.toDouble(z, 16)))
      if (sum.abs().compareTo(java.math.BigDecimal(65504.0)) < 0 && sum.signum() != 0)
        assertEquals(
          IEEE754.fusedMultiplyAdd(x, y, z, 16),
          exactRound(sum, 16, IEEE754.Rounding.NearestEven),
          clues(x, y, z)
        )
    }
  }

  test("directed fma rounds the exact result once for every width") {
    val random = Random(0xfaL)
    for {
      _        <- 0 until 300
      width    <- Seq[Int in (16, 32, 64)](16, 32, 64)
      rounding <- roundings
    } {
      val scale           = if (width.raw == 16) 4 else 30
      def value(): Double = IEEE754.toDouble(
        IEEE754.fromDouble(random.nextGaussian() * Math.pow(2.0, random.nextInt(scale) - scale / 2), width),
        width
      )
      val (a, b, c) = (value(), value(), value() * Math.pow(2.0, -random.nextInt(scale)))
      val exact     = java.math.BigDecimal(a).multiply(java.math.BigDecimal(b)).add(java.math.BigDecimal(c))
      if (exact.signum() != 0 && Math.abs(BigDecimal(exact).toDouble) < (if (width.raw == 16) 65504.0 else 1.0e30))
        assertEquals(
          IEEE754.fusedMultiplyAdd(
            IEEE754.fromDouble(a, width),
            IEEE754.fromDouble(b, width),
            IEEE754.fromDouble(c, width),
            width,
            rounding
          ),
          exactRound(exact, width, rounding),
          clues(a, b, c, width, rounding)
        )
    }
    assertEquals(
      IEEE754.fusedMultiplyAdd(0x3f800000L, 0x3f800000L, 0xbf800000L, 32, IEEE754.Rounding.Down),
      0x80000000L
    )
    assertEquals(IEEE754.fusedMultiplyAdd(0x3f800000L, 0x3f800000L, 0xbf800000L, 32, IEEE754.Rounding.Up), 0x0L)
  }

  test("directed fma handles overflow, subnormal, wide exponent gaps and exact zeros in every mode") {
    val maximum = 0x7fefffffffffffffL
    val one     = 0x3ff0000000000000L
    val two     = 0x4000000000000000L
    val half    = 0x3fe0000000000000L
    val sign    = Long.MinValue
    val inf     = 0x7ff0000000000000L
    // expected order: NearestEven, TowardZero, Up, Down
    val cases = Seq[(Int in (16, 32, 64), Long, Long, Long, Seq[Long])](
      (64, maximum, two, 0L, Seq(inf, maximum, inf, maximum)),
      (64, maximum | sign, two, 0L, Seq(inf | sign, maximum | sign, maximum | sign, inf | sign)),
      (64, maximum, two, maximum | sign, Seq.fill(4)(maximum)),
      (64, maximum, 0x3ff8000000000000L, maximum | sign, Seq.fill(4)(0x7fdfffffffffffffL)),
      (64, maximum | sign, 0x3ff8000000000000L, maximum, Seq.fill(4)(0xffdfffffffffffffL)),
      (64, 1L, half, 0L, Seq(0L, 0L, 1L, 0L)),
      (64, 1L | sign, half, 0L, Seq(sign, sign, sign, 1L | sign)),
      (64, 3L, half, 0L, Seq(2L, 1L, 2L, 1L)),
      (64, one, one, 1L, Seq(one, one, one + 1L, one)),
      (64, one, one, 1L | sign, Seq(one, one - 1L, one, one - 1L)),
      (
        64,
        0x7e70000000000000L,
        0x4160000000000000L,
        1L,
        Seq(
          0x7fe0000000000000L,
          0x7fe0000000000000L,
          0x7fe0000000000001L,
          0x7fe0000000000000L
        )
      ),
      (64, one, one, one | sign, Seq(0L, 0L, 0L, sign)),
      (64, maximum, two, inf | sign, Seq.fill(4)(inf | sign)),
      (64, sign, one, sign, Seq.fill(4)(sign)),
      (64, 0L, one | sign, 0L, Seq(0L, 0L, 0L, sign)),
      (32, 0x7f7fffffL, 0x40000000L, 0L, Seq(0x7f800000L, 0x7f7fffffL, 0x7f800000L, 0x7f7fffffL)),
      (32, 0x3f800000L, 0x3f800000L, 0x80000001L, Seq(0x3f800000L, 0x3f7fffffL, 0x3f800000L, 0x3f7fffffL)),
      (32, 0x00000001L, 0x3f000000L, 0L, Seq(0L, 0L, 1L, 0L)),
      (16, 0x7bffL, 0x4000L, 0L, Seq(0x7c00L, 0x7bffL, 0x7c00L, 0x7bffL)),
      (16, 0x8001L, 0x3800L, 0x8000L, Seq(0x8000L, 0x8000L, 0x8000L, 0x8001L))
    )
    for {
      (width, a, b, c, expected) <- cases
      (rounding, bits)           <- IEEE754.Rounding.values.toSeq.zip(expected)
    } assertEquals(IEEE754.fusedMultiplyAdd(a, b, c, width, rounding), bits, clues(width, a, b, c, rounding))
  }

  test("normalised values decode and encode with clamping and away-from-zero ties") {
    assertEquals(Normalised.decodeUnsigned(0xffL, 8), 1.0)
    assertEquals(Normalised.decodeSigned(0x80L, 8), -1.0)
    assertEquals(Normalised.decodeSigned(0x81L, 8), -1.0)
    assertEquals(Normalised.decodeSigned(0x7fffL, 16), 1.0)
    assertEquals(Normalised.encodeUnsigned(0.5, 8), 128L)
    assertEquals(Normalised.encodeUnsigned(2.0, 8), 255L)
    assertEquals(Normalised.encodeUnsigned(Double.NaN, 8), 0L)
    assertEquals(Normalised.encodeSigned(-2.0, 8), 0x81L)
    assertEquals(Normalised.encodeSigned(-0.5 / 127.0, 8), 0xffL)
    assertEquals(Normalised.encodeUnsigned(1.0, 10), 1023L)
  }

  test("funnel shifts take 32 bits from the high:low pair") {
    assertEquals(Bits.funnelShiftRight(0x89abcdefL, 0x01234567L, 8), 0x6789abcdL)
    assertEquals(Bits.funnelShiftRight(0x89abcdefL, 0x01234567L, 32), 0x01234567L)
    assertEquals(Bits.funnelShiftLeft(0x89abcdefL, 0x01234567L, 8), 0x23456789L)
    assertEquals(Bits.funnelShiftLeft(0x89abcdefL, 0x01234567L, 32), 0x89abcdefL)
  }
}
