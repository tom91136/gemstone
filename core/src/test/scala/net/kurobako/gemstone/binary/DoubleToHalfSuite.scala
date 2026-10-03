package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*

class DoubleToHalfSuite extends munit.FunSuite {
  private val halfWidth: Int in (16, 32, 64) = 16

  test("binary64 conversion rounds once around every finite half midpoint, for both signs") {
    for (lower <- 0 until 0x7bff) {
      val lowerBits = Binary16.low(lower.toLong)
      val upperBits = Binary16.low(lower.toLong + 1L)
      val midpoint  = (IEEE754.binary16ToFloat(lowerBits).toDouble + IEEE754.binary16ToFloat(upperBits).toDouble) / 2
      val even      = if ((lower & 1) == 0) lower else lower + 1
      for (
        (
          input,
          expected
        ) <- Seq(Math.nextDown(midpoint) -> lower, midpoint -> even, Math.nextUp(midpoint) -> (lower + 1))
      ) {
        assertEquals(IEEE754.fromDouble(input, halfWidth), expected.toLong, s"input=$input lower=$lower")
        assertEquals(
          IEEE754.fromDouble(-input, halfWidth),
          (expected | 0x8000).toLong,
          s"negative input=$input lower=$lower"
        )
      }
    }
  }
  test("binary64 half overflow, signed zero, infinities and NaNs") {
    assertEquals(IEEE754.fromDouble(Math.nextDown(65520.0), halfWidth), 0x7bffL)
    assertEquals(IEEE754.fromDouble(65520.0, halfWidth), 0x7c00L)
    assertEquals(IEEE754.fromDouble(-65520.0, halfWidth), 0xfc00L)
    assertEquals(IEEE754.fromDouble(0.0, halfWidth), 0L)
    assertEquals(IEEE754.fromDouble(-0.0, halfWidth), 0x8000L)
    assertEquals(IEEE754.fromDouble(Double.PositiveInfinity, halfWidth), 0x7c00L)
    assertEquals(IEEE754.fromDouble(Double.NegativeInfinity, halfWidth), 0xfc00L)
    assertEquals(IEEE754.fromDouble(Double.NaN, halfWidth) & 0x7fffL, 0x7e00L)
  }
}
