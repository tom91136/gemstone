package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.bits.*
import scala.util.Random

class OpenCLMathPlatformSuite extends Suite {
  test("the portable IEEE remainder matches the JVM's") {
    val random = Random(0x1eeeL)
    val edges  = Seq(
      0.0,
      -0.0,
      1.0,
      -1.0,
      2.0,
      0.5,
      1.5,
      2.5,
      -2.5,
      3.0,
      Double.MinPositiveValue,
      java.lang.Double.MIN_NORMAL,
      2.0 * java.lang.Double.MIN_NORMAL,
      Double.MaxValue,
      -Double.MaxValue,
      Math.scalb(1.0, 1023),
      Double.PositiveInfinity,
      Double.NegativeInfinity,
      Double.NaN
    )
    val cases =
      (for {
        x <- edges
        y <- edges
      } yield (x, y)) ++ Seq.fill(50000) {
        (java.lang.Double.longBitsToDouble(random.nextLong()), java.lang.Double.longBitsToDouble(random.nextLong()))
      } ++ Seq.fill(50000)((random.nextGaussian() * 1e3, random.nextGaussian()))
    for ((x, y) <- cases) {
      val (actual, expected) = (DoubleMath.remainder(x, y), Math.IEEEremainder(x, y))
      if (expected.isNaN) assert(actual.isNaN, (x, y, actual))
      else assertEquals(double(actual), double(expected), (x, y))
    }
  }
}
