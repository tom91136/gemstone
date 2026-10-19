package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

/** Lane-independent NVIDIA arithmetic on raw register bits. */
private[isa] object VMArithmetic {

  /** The encodings NVIDIA arithmetic produces for every binary16, bfloat16 and binary32 NaN result. */
  val CanonicalNaN16: Long = 0x7fffL
  val CanonicalNaN32: Long = 0x7fffffffL

  /** The binary64 NaN of an invalid operation; binary64 arithmetic otherwise propagates a NaN operand. */
  val DefaultNaN64: Long = 0xfff8000000000000L

  /** `bits` with a subnormal's exponent and mantissa cleared, keeping its sign. */
  def flushSubnormal(bits: Long, width: Int in (16, 32, 64)): Long =
    if ((bits & IEEE754.infinity(width)) == 0) bits & IEEE754.signBit(width) else bits

  def flushBfloat16(bits: Long): Long = if ((bits & 0x7f80L) == 0) bits & 0x8000L else bits

  /** `bits` clamped to [0, 1], with NaN as 0. */
  def saturate(bits: Long, width: Int in (16, 32, 64)): Long = {
    val value = IEEE754.toDouble(bits, width)
    if (value.isNaN || value <= 0.0) 0L
    else if (value >= 1.0) IEEE754.fromDouble(1.0, width)
    else bits
  }

  def canonical(bits: Long, width: Int in (16, 32, 64)): Long =
    if (width == 64 || !IEEE754.isNaN(bits, width)) bits
    else if (width == 16) CanonicalNaN16
    else CanonicalNaN32

  private def canonicalBfloat16(bits: Long): Long = if (Minifloat.BFloat16.isNaN(bits)) CanonicalNaN16 else bits

  private def bfloat16Bits(value: Double, rounding: IEEE754.Rounding = IEEE754.Rounding.NearestEven): Long =
    canonicalBfloat16(IEEE754.doubleToBfloat16(value, rounding).toLong)

  private def bfloat16Value(bits: Long): Double = Minifloat.BFloat16.toDouble(bits)

  /** The lesser or, when `maximum`, the greater operand; a NaN wins when `propagate`, and loses otherwise. */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def extremum(
      leftBits: Long,
      rightBits: Long,
      width: Int in (16, 32, 64),
      maximum: Boolean,
      propagate: Boolean,
      absolute: Boolean,
      xorSign: Boolean
  ): Long = {
    val sign   = IEEE754.signBit(width)
    val a      = if (absolute) leftBits & ~sign else leftBits
    val b      = if (absolute) rightBits & ~sign else rightBits
    val result =
      if (maximum) { if (propagate) IEEE754.maximum(a, b, width) else IEEE754.maxNum(a, b, width) }
      else if (propagate) IEEE754.minimum(a, b, width)
      else IEEE754.minNum(a, b, width)
    if (IEEE754.isNaN(result, width)) canonical(result, width)
    else if (xorSign) (result & ~sign) | ((leftBits ^ rightBits) & sign)
    else result
  }

  def floatQuotient(left: Long, right: Long, width: Int in (16, 32, 64)): Long =
    if (width == 32)
      IEEE754.floatToBinary32(IEEE754.binary32ToFloat(low32(left)) / IEEE754.binary32ToFloat(low32(right))).toLong
    else IEEE754.fromDouble(IEEE754.toDouble(left, width) / IEEE754.toDouble(right, width), width)

  def floatUnaryBits(operation: FloatUnaryOperation, bits: Long, width: Int in (16, 32, 64)): Long = {
    val sign                                    = IEEE754.signBit(width)
    def rounded(result: Double => Double): Long =
      canonical(IEEE754.fromDouble(result(IEEE754.toDouble(bits, width)), width), width)
    operation match {
      case FloatUnaryOperation.Negate               => truncate(bits ^ sign, width)
      case FloatUnaryOperation.Absolute             => truncate(bits & ~sign, width)
      case FloatUnaryOperation.Reciprocal           => rounded(1.0 / _)
      case FloatUnaryOperation.SquareRoot           => rounded(Math.sqrt(_))
      case FloatUnaryOperation.ReciprocalSquareRoot => rounded(value => 1.0 / Math.sqrt(value))
      case FloatUnaryOperation.Exponent2            => rounded(Math.pow(2.0, _))
      case FloatUnaryOperation.Log2                 => rounded(value => Math.log(value) / Math.log(2.0))
      case FloatUnaryOperation.Sine                 => rounded(Math.sin(_))
      case FloatUnaryOperation.Cosine               => rounded(Math.cos(_))
      case FloatUnaryOperation.Tanh                 => rounded(Math.tanh(_))
    }
  }

  def roundingMode(rounding: ConversionRounding): IEEE754.Rounding = rounding match {
    case ConversionRounding.RP | ConversionRounding.RPI => IEEE754.Rounding.Up
    case ConversionRounding.RM | ConversionRounding.RMI => IEEE754.Rounding.Down
    case ConversionRounding.RZ | ConversionRounding.RZI => IEEE754.Rounding.TowardZero
    case _                                              => IEEE754.Rounding.NearestEven
  }

  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def bfloat16FusedMultiplyAdd(left: Long, right: Long, addend: Long, saturate: Boolean): Long = {
    // a bfloat16 product is exact in binary64
    val result = IEEE754.oddSum(bfloat16Value(left) * bfloat16Value(right), bfloat16Value(addend))
    bfloat16Bits(if (!saturate) result else if (result.isNaN || result <= 0.0) 0.0 else math.min(result, 1.0))
  }

  /** A binary32 value rounded to the nearest bfloat16. */
  def roundBfloat16(bits: Long): Long = bfloat16Bits(IEEE754.binary32ToFloat(low32(bits)).toDouble)

  /** A comparison of binary16, binary32 or binary64 values of `width` bits. */
  def compareFloating(left: Long, right: Long, comparison: Comparison, width: Int in (16, 32, 64)): Boolean =
    compareValues(
      IEEE754.toDouble(truncate(left, width.widen[Int in 1 ~ 64]), width),
      IEEE754.toDouble(truncate(right, width.widen[Int in 1 ~ 64]), width),
      comparison
    )

  /** A comparison of bfloat16 values. */
  def compareBfloat16Values(left: Long, right: Long, comparison: Comparison): Boolean =
    compareValues(
      IEEE754.binary32ToFloat(Binary32.low(truncate(left, 16) << 16)).toDouble,
      IEEE754.binary32ToFloat(Binary32.low(truncate(right, 16) << 16)).toDouble,
      comparison
    )

  private def compareValues(left: Double, right: Double, comparison: Comparison): Boolean = {
    val order     = if (left < right) -1 else if (left > right) 1 else 0
    val unordered = left.isNaN || right.isNaN
    comparison match {
      case Comparison.Ordered                     => !unordered
      case Comparison.Unordered                   => unordered
      case Comparison.Equal                       => !unordered && order == 0
      case Comparison.EqualUnordered              => unordered || order == 0
      case Comparison.NotEqual                    => !unordered && order != 0
      case Comparison.NotEqualUnordered           => unordered || order != 0
      case Comparison.LessThan                    => !unordered && order < 0
      case Comparison.LessThanUnordered           => unordered || order < 0
      case Comparison.LessThanOrEqual             => !unordered && order <= 0
      case Comparison.LessThanOrEqualUnordered    => unordered || order <= 0
      case Comparison.GreaterThan                 => !unordered && order > 0
      case Comparison.GreaterThanUnordered        => unordered || order > 0
      case Comparison.GreaterThanOrEqual          => !unordered && order >= 0
      case Comparison.GreaterThanOrEqualUnordered => unordered || order >= 0
    }
  }

  /** `value` converted between formats, at least one of them floating; integer destinations round toward zero and
    * floating ones to nearest even unless `rounding` says otherwise.
    */
  def convert(
      value: Long,
      sourceType: NumericFormat,
      destinationType: NumericFormat,
      rounding: ConversionRounding
  ): Long = sourceType match {
    case NumericFormat.IEEE(bits) =>
      convertFloating(IEEE754.toDouble(value, bits), bits == 64, destinationType, rounding)
    case NumericFormat.BF16 => convertFloating(bfloat16Value(value), fromBinary64 = false, destinationType, rounding)
    case source: (NumericFormat.Bits | NumericFormat.Signed | NumericFormat.Unsigned) =>
      convertInteger(value, source, destinationType, rounding)
  }

  /** A floating `destination` value clamped as a conversion asks; integers are unchanged. */
  def clamp(bits: Long, destination: NumericFormat, clamp: ConversionClamp): Long = {
    def bounded(value: Double, one: => Long): Long = clamp match {
      case ConversionClamp.Saturate => if (value.isNaN || value <= 0.0) 0L else if (value >= 1.0) one else bits
      case ConversionClamp.Relu     => if (value < 0.0) 0L else bits
    }
    destination match {
      case NumericFormat.IEEE(width) => bounded(IEEE754.toDouble(bits, width), IEEE754.fromDouble(1.0, width))
      case NumericFormat.BF16        => bounded(bfloat16Value(bits), bfloat16Bits(1.0))
      case _                         => bits
    }
  }

  // Narrowing binary64 keeps a NaN payload; every other conversion yields the canonical NaN.
  private def convertFloating(
      floating: Double,
      fromBinary64: Boolean,
      destination: NumericFormat,
      rounding: ConversionRounding
  ): Long = destination match {
    case NumericFormat.BF16       => bfloat16Bits(floating, roundingMode(rounding))
    case NumericFormat.IEEE(bits) =>
      // the integral variants round to an integer before the format conversion
      val integral = rounding match {
        case ConversionRounding.RZI | ConversionRounding.RMI | ConversionRounding.RPI | ConversionRounding.RNI =>
          Some(IEEE754.integral(floating, roundingMode(rounding)))
        case _ => None
      }
      val encoded = integral match {
        case Some(value) => IEEE754.fromDouble(value, bits)
        case None        => IEEE754.fromDouble(floating, bits, roundingMode(rounding))
      }
      if (fromBinary64 && bits == 32) encoded else canonical(encoded, bits)
    case integer =>
      val signed = integer match {
        case NumericFormat.Signed(_) => true
        case _                       => false
      }
      IEEE754.toIntegerSaturated(floating, integer.width, signed, roundingMode(rounding))
  }

  private def convertInteger(
      value: Long,
      source: NumericFormat.Bits | NumericFormat.Signed | NumericFormat.Unsigned,
      destination: NumericFormat,
      rounding: ConversionRounding
  ): Long = {
    val sourceBits = truncate(value, source.width)
    val integer    = source match {
      case NumericFormat.Signed(_) => signExtend(sourceBits, source.width)
      case _                       => sourceBits
    }
    val unsigned = source match {
      case NumericFormat.Unsigned(_) => true
      case _                         => false
    }
    destination match {
      case NumericFormat.IEEE(bits) => IEEE754.integerToBinary(integer, unsigned, bits, roundingMode(rounding))
      case NumericFormat.BF16       => IEEE754.integerToBfloat16(integer, unsigned, roundingMode(rounding)).toLong
      case _                        => truncate(integer, destination.width)
    }
  }
}
