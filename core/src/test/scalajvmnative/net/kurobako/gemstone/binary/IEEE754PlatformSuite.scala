package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*
import scala.util.Random

class IEEE754PlatformSuite extends munit.FunSuite {
  test("the exact fused multiply-add that platforms without one use matches the JVM's") {
    val random           = Random(0xfa5L)
    def finite(): Double = {
      val value = java.lang.Double.longBitsToDouble(random.nextLong())
      if (value.isFinite) value else random.nextGaussian()
    }
    val edges =
      Seq(0.0, -0.0, 1.0, -1.0, Double.MinPositiveValue, java.lang.Double.MIN_NORMAL, Double.MaxValue, 0.1, 3.0)
    val cases =
      (for {
        a <- edges
        b <- edges
        c <- edges
      } yield (a, b, c)) ++
        Seq.fill(20000) {
          val (a, b)     = (finite(), finite())
          val cancelling = -(a * b)
          (a, b, if (random.nextBoolean() && cancelling.isFinite) cancelling else finite())
        } ++ Seq.fill(20000)((random.nextGaussian(), random.nextGaussian(), random.nextGaussian()))
    for ((a, b, c) <- cases) {
      assertEquals(
        IEEE754.finiteFusedMultiplyAdd(a, b, c, 64, IEEE754.Rounding.NearestEven),
        IEEE754.doubleToBinary64(Math.fma(a, b, c)),
        (a, b, c)
      )
      val (x, y, z) = (a.toFloat, b.toFloat, c.toFloat)
      if (x.isFinite && y.isFinite && z.isFinite)
        assertEquals(
          IEEE754.finiteFusedMultiplyAdd(x, y, z, 32, IEEE754.Rounding.NearestEven),
          IEEE754.floatToBinary32(Math.fma(x, y, z)).toLong,
          (x, y, z)
        )
    }
  }

  test("a binary32 fma intermediate double-rounds the binary16 tie case") {
    val (a, b, c) = (0x3c01L, 0x3bffL, 0x0009L)
    val viaFloat  = IEEE754.floatToBinary16(
      Math.fma(
        IEEE754.binary16ToFloat(Binary16.low(a)),
        IEEE754.binary16ToFloat(Binary16.low(b)),
        IEEE754.binary16ToFloat(Binary16.low(c))
      )
    )
    assertEquals(viaFloat.toLong, 0x3c00L)
  }
}
