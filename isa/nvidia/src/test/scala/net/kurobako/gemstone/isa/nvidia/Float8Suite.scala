package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.{IntegerArithmetic as _, *}
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.testing.Suite
import scala.util.Random

/** The 8-bit float conversions over the core minifloat codecs give what the format-specific rounding they replace gave,
  * which conformance/unified/src/cuda/conversions.cu checks against an RTX 4060 Ti (sm_89).
  */
class Float8Suite extends Suite {
  private def minifloat(format: Float8Format): Minifloat = format match {
    case Float8Format.E4M3 => Minifloat.E4M3
    case Float8Format.E5M2 => Minifloat.E5M2
  }

  // round to nearest even, saturating finite values and infinities, NaN as 0x7f
  private def encode(value: Double, format: Float8Format): Long =
    if (value.isNaN) 0x7fL else minifloat(format).fromDouble(value, saturate = true)

  private def decode(code: Long, format: Float8Format): Long = {
    val value = minifloat(format).toDouble(code)
    if (value.isNaN) VMArithmetic.CanonicalNaN16 else IEEE754.fromDouble(value, 16)
  }

  test("every binary16 value converts to E4M3 and E5M2 as the format-specific rounding did") {
    (0L until 0x10000L).foreach { bits =>
      val value = IEEE754.toDouble(bits, 16)
      assertEquals(
        IntegerArithmetic.toE4M3(FloatingPoint[16](bits), relu = false).bits.raw,
        encode(value, Float8Format.E4M3)
      )
      assertEquals(
        IntegerArithmetic.toE5M2(FloatingPoint[16](bits), relu = false).bits.raw,
        encode(value, Float8Format.E5M2)
      )
    }
  }

  test("binary32 values about every E4M3 and E5M2 step convert as the format-specific rounding did") {
    val random = Random(0xf8)
    val inputs = (0L until 0x100000000L by 0x1001L) ++ Seq.fill(100000)(random.nextLong() & 0xffffffffL) ++
      Seq(0x7f800000L, 0xff800000L, 0x7fc00000L, 0x43e00000L, 0x43e80000L, 0x47600000L, 0x47700000L, 0x80000000L)
    inputs.foreach { bits =>
      val value = IEEE754.toDouble(bits, 32)
      assertEquals(
        IntegerArithmetic.toE4M3(FloatingPoint[32](bits), relu = false).bits.raw,
        encode(value, Float8Format.E4M3)
      )
      assertEquals(
        IntegerArithmetic.toE5M2(FloatingPoint[32](bits), relu = false).bits.raw,
        encode(value, Float8Format.E5M2)
      )
    }
  }

  test("every E4M3 and E5M2 code widens to binary16 as before") {
    (0L until 0x100L).foreach { code =>
      assertEquals(IntegerArithmetic.fromE4M3(Float8E4M3(UInt[8](code))).bits.raw, decode(code, Float8Format.E4M3))
      assertEquals(IntegerArithmetic.fromE5M2(Float8E5M2(UInt[8](code))).bits.raw, decode(code, Float8Format.E5M2))
    }
  }

  test("fixed E4M3 and E5M2 vectors") {
    def e4m3(bits: Long) = IntegerArithmetic.toE4M3(FloatingPoint[32](bits), relu = false).bits.raw
    def e5m2(bits: Long) = IntegerArithmetic.toE5M2(FloatingPoint[32](bits), relu = false).bits.raw
    assertEquals(e4m3(0x3f800000L), 0x38L)
    assertEquals(e4m3(0x43e00000L), 0x7eL)
    assertEquals(e4m3(0x7f800000L), 0x7eL)
    assertEquals(e4m3(0xff800000L), 0xfeL)
    assertEquals(e4m3(0x3fa00000L), 0x3aL)
    assertEquals(e4m3(0x3f880000L), 0x38L)
    assertEquals(e4m3(0x3f980000L), 0x3aL)
    assertEquals(e4m3(0x3b000000L), 0x01L)
    assertEquals(e4m3(0x3a800000L), 0x00L)
    assertEquals(e4m3(0x3b400000L), 0x02L)
    assertEquals(e4m3(0xbb400000L), 0x82L)
    assertEquals(e5m2(0x3f800000L), 0x3cL)
    assertEquals(e5m2(0x47600000L), 0x7bL)
    assertEquals(e5m2(0x7f800000L), 0x7bL)
    assertEquals(e5m2(0x7fc00000L), 0x7fL)
    assertEquals(e5m2(0x3f900000L), 0x3cL)
    assertEquals(e5m2(0x3fb00000L), 0x3eL)
    assertEquals(e5m2(0x47700000L), 0x7bL)
    assertEquals(IntegerArithmetic.fromE4M3(Float8E4M3(UInt[8](0x38L))).bits.raw, 0x3c00L)
    assertEquals(IntegerArithmetic.fromE5M2(Float8E5M2(UInt[8](0x7bL))).bits.raw, 0x7b00L)
    assertEquals(IntegerArithmetic.fromE5M2(Float8E5M2(UInt[8](0x7cL))).bits.raw, 0x7c00L)
  }

  test("relu zeroes negative values, either zero included, and keeps NaN") {
    assertEquals(IntegerArithmetic.toE4M3(FloatingPoint[32](0xbf800000L), relu = true).bits.raw, 0L)
    assertEquals(IntegerArithmetic.toE5M2(FloatingPoint[32](0x80000000L), relu = true).bits.raw, 0L)
    assertEquals(IntegerArithmetic.toE4M3(FloatingPoint[32](0xffc00000L), relu = true).bits.raw, 0x7fL)
    assertEquals(IntegerArithmetic.toE5M2(FloatingPoint[32](0x3f800000L), relu = true).bits.raw, 0x3cL)
  }
}
