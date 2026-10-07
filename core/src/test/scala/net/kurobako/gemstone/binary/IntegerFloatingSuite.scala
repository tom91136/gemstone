package net.kurobako.gemstone.binary

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.util.Random

class IntegerFloatingSuite extends munit.FunSuite {
  private def exactParts(value: Double): (BigInt, Int) = {
    val bits        = IEEE754.doubleToBinary64(value)
    val encoded     = ((bits >>> 52) & 0x7ffL).toInt
    val fraction    = bits & 0xfffffffffffffL
    val significand = BigInt(if (encoded == 0) fraction else fraction | (1L << 52))
    (if (bits < 0L) -significand else significand, if (encoded == 0) -1074 else encoded - 1075)
  }

  test("integer conversion retains directed rounding at precision and unsigned boundaries") {
    val random = Random(0x754L)
    val inputs = Vec(
      0L,
      1L,
      -1L,
      Long.MinValue,
      Long.MaxValue,
      (1L << 24) - 1,
      (1L << 24) + 1,
      (1L << 53) - 1,
      (1L << 53) + 1
    ) ++
      Vec.fill(500)(random.nextLong())
    for {
      value    <- inputs
      unsigned <- Vec(false, true)
      width    <- Vec[Int in (32, 64)](32, 64)
      rounding <- IEEE754.Rounding.values
    } {
      val integer   = if (unsigned) BigInt(value) & ((BigInt(1) << 64) - 1) else BigInt(value)
      val nearest   = if (width.raw == 32) integer.toFloat.toDouble else integer.toDouble
      val exact     = BigInt(java.math.BigDecimal(nearest).toBigIntegerExact)
      val direction = rounding match {
        case IEEE754.Rounding.NearestEven => 0
        case IEEE754.Rounding.Up          => 1
        case IEEE754.Rounding.Down        => -1
        case IEEE754.Rounding.TowardZero  => -integer.signum
      }
      val adjusted = if (direction > 0 && exact < integer) {
        if (width.raw == 32) Math.nextUp(nearest.toFloat).toDouble else Math.nextUp(nearest)
      } else if (direction < 0 && exact > integer) {
        if (width.raw == 32) Math.nextDown(nearest.toFloat).toDouble else Math.nextDown(nearest)
      } else nearest
      val expected = if (width.raw == 32) IEEE754.floatToBinary32(adjusted.toFloat).toLong
      else IEEE754.doubleToBinary64(adjusted)
      assertEquals(
        IEEE754.integerToBinary(value, unsigned, width.widen[Int in (16, 32, 64)], rounding),
        expected,
        clues(value, unsigned, width, rounding)
      )
    }
  }

  test("remquo low quotient bits match exact division including ties, subnormals and overflowing ratios") {
    val random = Random(0x7fL)
    val edges  = Vec(
      0.0,
      -0.0,
      1.0,
      -1.0,
      0.5,
      1.5,
      2.5,
      3.5,
      127.5,
      128.5,
      java.lang.Double.MIN_VALUE,
      java.lang.Double.MIN_NORMAL,
      Double.MaxValue,
      Double.NaN,
      Double.PositiveInfinity
    )
    val inputs = edges.flatMap(x => edges.map(y => (x, y))) ++
      Vec.fill(2000)((IEEE754.binary64ToDouble(random.nextLong()), IEEE754.binary64ToDouble(random.nextLong())))
    inputs.foreach { (x, y) =>
      val expected = if (!x.isFinite || !y.isFinite || y == 0.0) 0
      else {
        val (xSignificand, xExponent) = exactParts(x)
        val (ySignificand, yExponent) = exactParts(y)
        val scale                     = math.min(xExponent, yExponent)
        val numerator                 = xSignificand.abs << (xExponent - scale)
        val denominator               = ySignificand.abs << (yExponent - scale)
        val (truncated, remainder)    = numerator /% denominator
        val order                     = (remainder << 1).compare(denominator)
        val quotient                  = truncated + (if (order > 0 || (order == 0 && truncated.testBit(0))) 1 else 0)
        (quotient.toInt & 0x7f) * xSignificand.signum * ySignificand.signum
      }
      assertEquals(IEEE754.remainderQuotient(x, y), expected, clues(x, y))
    }
  }
}
