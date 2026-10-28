package net.kurobako.gemstone.binary

import net.kurobako.gemstone.binary.IEEE754.*
import net.kurobako.gemstone.binary.Minifloat.*
import scala.util.Random

class MinifloatSuite extends munit.FunSuite {
  private val narrow = Vector(E4M3, E5M2, E4M3FNUZ, E5M2FNUZ, E2M3, E3M2, E2M1, E8M0)

  test("every finite encoding of a narrow format converts back to itself") {
    narrow.foreach { format =>
      (0L until (1L << format.width)).foreach { code =>
        val value = format.toDouble(code)
        if (!value.isNaN && !value.isInfinite)
          assertEquals(format.fromDouble(value), code, s"$format 0x${code.toHexString} = $value")
      }
    }
  }

  test("the largest finite magnitudes are those the OCP and AMD formats specify") {
    assertEquals(E4M3.toDouble(E4M3.maximum), 448.0)
    assertEquals(E5M2.toDouble(E5M2.maximum), 57344.0)
    assertEquals(E4M3FNUZ.toDouble(E4M3FNUZ.maximum), 240.0)
    assertEquals(E5M2FNUZ.toDouble(E5M2FNUZ.maximum), 57344.0)
    assertEquals(E2M3.toDouble(E2M3.maximum), 7.5)
    assertEquals(E3M2.toDouble(E3M2.maximum), 28.0)
    assertEquals(E2M1.toDouble(E2M1.maximum), 6.0)
    assertEquals(E8M0.toDouble(E8M0.maximum), Math.scalb(1.0, 127))
    assertEquals(E8M0.toDouble(0L), Math.scalb(1.0, -127))
  }

  test("special encodings follow each format") {
    assert(E4M3.isNaN(0x7fL) && E4M3.isNaN(0xffL) && !E4M3.isNaN(0x7eL))
    assert(E5M2.toDouble(0x7cL).isPosInfinity && E5M2.isNaN(0x7dL))
    assert(E4M3FNUZ.isNaN(0x80L) && E4M3FNUZ.toDouble(0x7fL) == 240.0)
    assertEquals(E4M3FNUZ.fromDouble(-0.0), 0L)
    assertEquals(E4M3.fromDouble(-0.0), 0x80L)
    assert(E8M0.isNaN(0xffL))
    assert((0L until 64L).forall(code => !E2M3.isNaN(code)))
  }

  test("conversions round to nearest even or away, and overflow to infinity, NaN or the largest magnitude") {
    assertEquals(E2M1.fromDouble(2.5), E2M1.fromDouble(2.0))
    assertEquals(E2M1.fromDouble(2.5, Rounding.NearestAway), E2M1.fromDouble(3.0))
    assertEquals(E2M1.fromDouble(2.5, Rounding.Up), E2M1.fromDouble(3.0))
    assertEquals(E2M1.fromDouble(-2.5, Rounding.Down), E2M1.fromDouble(-3.0))
    assertEquals(E4M3.fromDouble(1000.0), 0x7fL)
    assertEquals(E4M3.fromDouble(1000.0, saturate = true), E4M3.maximum)
    assertEquals(E4M3.fromDouble(-1000.0, saturate = true), 0x80L | E4M3.maximum)
    assertEquals(E5M2.fromDouble(1e6), 0x7cL)
    assertEquals(E5M2.fromDouble(1e6, Rounding.TowardZero), E5M2.maximum)
    assertEquals(E4M3FNUZ.fromDouble(1000.0), 0x80L)
    assertEquals(E2M1.fromDouble(100.0), E2M1.maximum)
    assertEquals(E4M3.fromDouble(Double.NaN), 0x7fL)
    assertEquals(E8M0.fromDouble(3.0, Rounding.TowardZero), 128L)
    assertEquals(E8M0.fromDouble(3.0, Rounding.Up), 129L)
  }

  test("subnormals round through the smallest normal") {
    val smallest = E4M3.toDouble(1L)
    assertEquals(smallest, Math.scalb(1.0, -9))
    assertEquals(E4M3.fromDouble(smallest / 2), 0L)
    assertEquals(E4M3.fromDouble(smallest * 1.5), 2L)
    assertEquals(E4M3.fromDouble(E4M3.toDouble(7L) + smallest), 8L)
  }

  test("binary32 values round to bfloat16 as the IEEE-754 conversion does") {
    val random = Random(16)
    (0 until 20000).foreach { _ =>
      val value = java.lang.Float.intBitsToFloat(random.nextInt()).toDouble
      if (!value.isNaN)
        assertEquals(BFloat16.fromDouble(value), IEEE754.doubleToBfloat16(value).toLong, s"$value")
    }
  }

  private def reference(bits: Long, exponentBits: Int, fractionBits: Int, bias: Int): Double = {
    val negative  = ((bits >>> (exponentBits + fractionBits)) & 1L) != 0L
    val exponent  = ((bits >>> fractionBits) & ((1L << exponentBits) - 1L)).toInt
    val fraction  = bits & ((1L << fractionBits) - 1L)
    val magnitude =
      if (exponent == (1 << exponentBits) - 1) { if (fraction == 0L) Double.PositiveInfinity else Double.NaN }
      else if (exponent == 0) Math.scalb(fraction.toDouble, 1 - bias - fractionBits)
      else Math.scalb(((1L << fractionBits) | fraction).toDouble, exponent - bias - fractionBits)
    if (negative) -magnitude else magnitude
  }

  test("bfloat16 and TF32 decode every encoding as the IEEE-754 layout gives, NaN canonical") {
    (0L until (1L << 16)).foreach { code =>
      val expected = reference(code, exponentBits = 8, fractionBits = 7, bias = 127)
      val actual   = BFloat16.toDouble(code)
      if (expected.isNaN) assert(actual.isNaN) else assertEquals(actual, expected, s"bf16 0x${code.toHexString}")
    }
    (0L until (1L << 19)).foreach { code =>
      val expected = reference(code, exponentBits = 8, fractionBits = 10, bias = 127)
      val actual   = TensorFloat32.toDouble(code)
      if (expected.isNaN) assert(actual.isNaN) else assertEquals(actual, expected, s"tf32 0x${code.toHexString}")
    }
  }

  test("the largest finite value of each format is the decoded maximum") {
    (Minifloat.values.toSeq :+ Minifloat.E5M6 :+ Minifloat.E5M5).foreach(format =>
      assertEquals(format.largest, format.toDouble(format.maximum), format.toString)
    )
    assertEquals(Minifloat.E5M6.largest, 65024.0)
    assertEquals(Minifloat.E5M5.largest, 64512.0)
  }

  test("the unsigned 11- and 10-bit floats decode and round trip every encoding") {
    Seq(Minifloat.E5M6, Minifloat.E5M5).foreach { format =>
      (0L until (1L << format.width)).foreach { code =>
        val expected = reference(code, 5, format.fractionBits, 15)
        val actual   = format.toDouble(code)
        if (expected.isNaN) assert(actual.isNaN && format.isNaN(code))
        else {
          assertEquals(actual, expected, s"${format.width} 0x${code.toHexString}")
          assertEquals(format.fromDouble(actual), code)
        }
      }
    }
  }

  test("the unsigned 11- and 10-bit floats clamp negatives, truncate toward zero and saturate on request") {
    val format = Minifloat.E5M6
    assertEquals(format.fromDouble(-1.0), 0L)
    assertEquals(format.fromDouble(Double.NegativeInfinity), 0L)
    assertEquals(format.fromDouble(-0.0), 0L)
    assertEquals(format.fromDouble(1.0 + 1.9 / 64.0, Rounding.TowardZero), format.fromDouble(1.0 + 1.0 / 64.0))
    assertEquals(format.fromDouble(1.0e9), 31L << 6)
    assertEquals(format.fromDouble(1.0e9, Rounding.TowardZero), format.maximum)
    assertEquals(format.fromDouble(1.0e9, saturate = true), format.maximum)
    assertEquals(format.fromDouble(Double.NaN), (31L << 6) | 32L)
    assertEquals(format.fromDouble(65280.0), 31L << 6)
    assertEquals(format.fromDouble(65279.0), format.maximum)
  }
}
