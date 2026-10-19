package net.kurobako.gemstone.isa.spirv

class OpenCLRoundSuite extends munit.FunSuite {
  private def bits(value: Double): Long = java.lang.Double.doubleToRawLongBits(value)

  test("round is exact at the binary64 edges where adding one half is not") {
    assertEquals(bits(DoubleMath.round(0.49999999999999994)), bits(0.0))
    assertEquals(bits(DoubleMath.round(Math.scalb(1.0, 52) + 1.0)), bits(Math.scalb(1.0, 52) + 1.0))
    assertEquals(bits(DoubleMath.round(-0.49999999999999994)), bits(-0.0))
    assertEquals(bits(DoubleMath.round(2.5)), bits(3.0))
    assertEquals(bits(DoubleMath.round(-2.5)), bits(-3.0))
    assertEquals(bits(DoubleMath.round(-0.3)), bits(-0.0))
    assert(DoubleMath.round(Double.NaN).isNaN)
    assertEquals(DoubleMath.round(Double.NegativeInfinity), Double.NegativeInfinity)
  }
}
