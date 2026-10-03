package net.kurobako.gemstone.binary

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.util.Random

class IntegerFloatingSuite extends munit.FunSuite {
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
        IEEE754.integerToBinary(value, unsigned, width, rounding),
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
        val quotient = BigInt(java.math.BigDecimal(x)
          .divide(java.math.BigDecimal(y), 0, java.math.RoundingMode.HALF_EVEN).toBigIntegerExact)
        (quotient.abs.toInt & 0x7f) * quotient.signum
      }
      assertEquals(IEEE754.remainderQuotient(x, y), expected, clues(x, y))
    }
  }
}
