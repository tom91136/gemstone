package net.kurobako.gemstone.binary

final class DoubleToHalfSuite extends munit.FunSuite {
  test("binary64 conversion rounds once around every finite half midpoint, for both signs") {
    for (lower <- 0 until 0x7bff) {
      val midpoint = (IEEE754.binary16ToFloat(lower).toDouble + IEEE754.binary16ToFloat(lower + 1).toDouble) / 2
      val even     = if ((lower & 1) == 0) lower else lower + 1
      for (
        (
          input,
          expected
        ) <- Seq(Math.nextDown(midpoint) -> lower, midpoint -> even, Math.nextUp(midpoint) -> (lower + 1))
      ) {
        assertEquals(IEEE754.fromDouble(input, 16), expected.toLong, s"input=$input lower=$lower")
        assertEquals(IEEE754.fromDouble(-input, 16), (expected | 0x8000).toLong, s"negative input=$input lower=$lower")
      }
    }
  }
  test("binary64 half overflow, signed zero, infinities and NaNs") {
    assertEquals(IEEE754.fromDouble(Math.nextDown(65520.0), 16), 0x7bffL)
    assertEquals(IEEE754.fromDouble(65520.0, 16), 0x7c00L)
    assertEquals(IEEE754.fromDouble(-65520.0, 16), 0xfc00L)
    assertEquals(IEEE754.fromDouble(0.0, 16), 0L)
    assertEquals(IEEE754.fromDouble(-0.0, 16), 0x8000L)
    assertEquals(IEEE754.fromDouble(Double.PositiveInfinity, 16), 0x7c00L)
    assertEquals(IEEE754.fromDouble(Double.NegativeInfinity, 16), 0xfc00L)
    assertEquals(IEEE754.fromDouble(Double.NaN, 16) & 0x7fffL, 0x7e00L)
  }
}
