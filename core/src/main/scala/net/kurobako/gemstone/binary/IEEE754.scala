package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

/** Raw IEEE-754 interchange encodings used by ISA registers and memory. Every conversion and arithmetic helper rounds
  * once; NaN results are quiet and keep the leading payload bits of their source.
  */
object IEEE754 {
  enum Rounding {
    case NearestEven, TowardZero, Up, Down
  }

  private case class Format(exponentBits: Int, fractionBits: Int) {
    val width: Int     = 1 + exponentBits + fractionBits
    val bias: Int      = (1 << (exponentBits - 1)) - 1
    val signBit: Long  = 1L << (width - 1)
    val infinity: Long = ((1L << exponentBits) - 1L) << fractionBits
    val quietBit: Long = 1L << (fractionBits - 1)
  }

  private val Half     = Format(exponentBits = 5, fractionBits = 10)
  private val BFloat16 = Format(exponentBits = 8, fractionBits = 7)
  private val Single   = Format(exponentBits = 8, fractionBits = 23)
  private val Double64 = Format(exponentBits = 11, fractionBits = 52)

  private def format(width: Int in (16, 32, 64)): Format = width.raw match {
    case 16 => Half
    case 32 => Single
    case _  => Double64
  }

  /** Round the exact value `magnitude * 2^scale`, with `magnitude` unsigned and nonzero. */
  private def encode(format: Format, negative: Boolean, magnitude: Long, scale: Int, rounding: Rounding): Long = {
    val exponent = 63 - java.lang.Long.numberOfLeadingZeros(magnitude) + scale
    if (exponent > format.bias) (if (negative) format.signBit else 0L) | overflow(format, negative, rounding)
    else {
      val minimum  = 1 - format.bias
      val shift    = math.max(exponent, minimum) - format.fractionBits - scale
      val retained =
        if (shift <= 0) magnitude << -shift
        else if (shift >= 64) 0L
        else magnitude >>> shift
      // -1 below the halfway point, 0 exactly halfway, 1 above it
      val half =
        if (shift <= 0) -1
        else if (shift > 64) -1
        else if (shift == 64) java.lang.Long.compareUnsigned(magnitude, Long.MinValue).sign
        else java.lang.Long.compare(magnitude & ((1L << shift) - 1L), 1L << (shift - 1)).sign
      val inexact   = shift > 0 && (shift >= 64 || (magnitude & ((1L << shift) - 1L)) != 0L)
      val increment = rounding match {
        case Rounding.NearestEven => half > 0 || (half == 0 && (retained & 1L) != 0L)
        case Rounding.TowardZero  => false
        case Rounding.Up          => !negative && inexact
        case Rounding.Down        => negative && inexact
      }
      val base    = if (exponent < minimum) 0L else (exponent + format.bias - 1).toLong << format.fractionBits
      val encoded = base + retained + (if (increment) 1L else 0L)
      (if (negative) format.signBit else 0L) |
        (if (encoded < format.infinity) encoded else overflow(format, negative, rounding))
    }
  }

  private def overflow(format: Format, negative: Boolean, rounding: Rounding): Long = rounding match {
    case Rounding.NearestEven => format.infinity
    case Rounding.TowardZero  => format.infinity - 1L
    case Rounding.Up          => if (negative) format.infinity - 1L else format.infinity
    case Rounding.Down        => if (negative) format.infinity else format.infinity - 1L
  }

  private def roundDouble(value: Double, format: Format, rounding: Rounding): Long = {
    val bits     = java.lang.Double.doubleToRawLongBits(value)
    val negative = bits < 0L
    val sign     = if (negative) format.signBit else 0L
    val exponent = ((bits >>> 52) & 0x7ffL).toInt
    val fraction = bits & 0xfffffffffffffL
    if (exponent == 0x7ff)
      if (fraction == 0L) sign | format.infinity
      else sign | format.infinity | format.quietBit | (fraction >>> (52 - format.fractionBits))
    else if (exponent == 0 && fraction == 0L) sign
    else if (exponent == 0) encode(format, negative, fraction, -1074, rounding)
    else encode(format, negative, fraction | (1L << 52), exponent - 1075, rounding)
  }

  private def roundInteger(value: Long, unsigned: Boolean, format: Format, rounding: Rounding): Long = {
    val negative = !unsigned && value < 0L
    if (value == 0L) 0L else encode(format, negative, if (negative) -value else value, 0, rounding)
  }

  /** Convert a signed or unsigned 64-bit register without rounding through Double first. */
  def integerToBinary(
      value: Long,
      unsigned: Boolean,
      width: Int in (16, 32, 64),
      rounding: Rounding = Rounding.NearestEven
  ): Long = roundInteger(value, unsigned, format(width), rounding)

  def integerToBfloat16(value: Long, unsigned: Boolean, rounding: Rounding = Rounding.NearestEven): Binary16 =
    Binary16.low(roundInteger(value, unsigned, BFloat16, rounding))

  /** Signed low seven bits of the nearest-even integral quotient used by remquo. Binary long division keeps the
    * remainder below a 53-bit significand even when x/y overflows Double.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def remainderQuotient(x: Double, y: Double): Int =
    if (!x.isFinite || !y.isFinite || x == 0.0 || y == 0.0) 0
    else {
      def significand(bits: Long): Long = {
        val fraction = bits & 0xfffffffffffffL
        if ((bits & 0x7ff0000000000000L) == 0L) fraction else fraction | 0x10000000000000L
      }
      def exponent(bits: Long): Int = {
        val encoded = ((bits >>> 52) & 0x7ffL).toInt
        if (encoded == 0) -1074 else encoded - 1075
      }
      val xBits        = doubleToBinary64(x)
      val yBits        = doubleToBinary64(y)
      val xSignificand = significand(xBits)
      val ySignificand = significand(yBits)
      val xShift       = java.lang.Long.numberOfLeadingZeros(xSignificand) - 11
      val yShift       = java.lang.Long.numberOfLeadingZeros(ySignificand) - 11
      val numerator    = xSignificand << xShift
      val denominator  = ySignificand << yShift
      val distance     = exponent(xBits) - xShift - exponent(yBits) + yShift
      val magnitude    = if (distance < -1) 0
      else if (distance == -1) { if (numerator > denominator) 1 else 0 }
      else {
        var remainder = numerator
        var quotient  = 0
        var bit       = distance
        while (bit >= 0) {
          val next = if (remainder >= denominator) { remainder -= denominator; 1 }
          else 0
          quotient = ((quotient << 1) | next) & 0x7f
          if (bit > 0) remainder <<= 1
          bit -= 1
        }
        val twice = remainder << 1
        (quotient + (if (twice > denominator || (twice == denominator && (quotient & 1) != 0)) 1 else 0)) & 0x7f
      }
      if ((xBits ^ yBits) < 0L) -magnitude else magnitude
    }

  inline def binary32ToFloat(bits: Binary32): Float = java.lang.Float.intBitsToFloat(bits.toInt)

  inline def floatToBinary32(value: Float): Binary32 = Bits.unsigned32(java.lang.Float.floatToRawIntBits(value))

  inline def binary64ToDouble(bits: Long): Double = java.lang.Double.longBitsToDouble(bits)

  inline def doubleToBinary64(value: Double): Long = java.lang.Double.doubleToRawLongBits(value)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def binary16ToFloat(value: Binary16): Float = {
    val bits16   = value.toInt
    val sign     = (bits16 & 0x8000) << 16
    val exponent = (bits16 >>> 10) & 0x1f
    val fraction = bits16 & 0x3ff
    val bits32   =
      if (exponent == 0) {
        if (fraction == 0) sign
        else {
          var mantissa = fraction
          var shift    = 0
          while ((mantissa & 0x400) == 0) {
            mantissa <<= 1
            shift += 1
          }
          sign | ((127 - 14 - shift) << 23) | ((mantissa & 0x3ff) << 13)
        }
      } else if (exponent == 0x1f) sign | 0x7f800000 | (fraction << 13)
      else sign | ((exponent - 15 + 127) << 23) | (fraction << 13)
    java.lang.Float.intBitsToFloat(bits32)
  }

  def floatToBinary16(value: Float): Binary16 = Binary16.low(roundDouble(value.toDouble, Half, Rounding.NearestEven))

  def doubleToBinary16(value: Double): Binary16 = Binary16.low(roundDouble(value, Half, Rounding.NearestEven))

  inline def bfloat16ToFloat(bits: Binary16): Float = java.lang.Float.intBitsToFloat(bits.toInt << 16)

  def doubleToBfloat16(value: Double, rounding: Rounding = Rounding.NearestEven): Binary16 =
    Binary16.low(roundDouble(value, BFloat16, rounding))

  def toDouble(bits: Long, width: Int in (16, 32, 64)): Double = width.raw match {
    case 16 => binary16ToFloat(Binary16.low(bits)).toDouble
    case 32 => binary32ToFloat(Bits.low32(bits)).toDouble
    case _  => binary64ToDouble(bits)
  }

  def fromDouble(value: Double, width: Int in (16, 32, 64), rounding: Rounding = Rounding.NearestEven): Long =
    width.raw match {
      case 16                                     => roundDouble(value, Half, rounding)
      case 32 if rounding == Rounding.NearestEven => floatToBinary32(value.toFloat).toLong
      case 32                                     => roundDouble(value, Single, rounding)
      case _                                      => doubleToBinary64(value)
    }

  /** Round to an integer, clamp to the signed or unsigned `width`-bit range, and map NaN to zero. Returns the low
    * `width` bits.
    */
  def toIntegerSaturated(value: Double, width: Int in 1 ~ 64, signed: Boolean, rounding: Rounding): Long = {
    val integral = rounding match {
      case Rounding.NearestEven => Math.rint(value)
      case Rounding.TowardZero  => if (value < 0.0) Math.ceil(value) else Math.floor(value)
      case Rounding.Up          => Math.ceil(value)
      case Rounding.Down        => Math.floor(value)
    }
    val result =
      if (value.isNaN) 0L
      else if (signed) {
        val minimum = -1L << (width.raw - 1)
        val maximum = ~minimum
        if (integral <= minimum.toDouble) minimum
        else if (integral >= maximum.toDouble) maximum
        else integral.toLong
      } else if (integral <= 0.0) 0L
      else if (width.raw < 64) math.min(integral, Bits.mask(width).toDouble).toLong
      else if (integral >= 18446744073709551616.0) -1L
      else if (integral >= 9223372036854775808.0) (integral - 9223372036854775808.0).toLong | Long.MinValue
      else integral.toLong
    Bits.truncate(result, width)
  }

  def isNaN(bits: Long, width: Int in (16, 32, 64)): Boolean = {
    val encoding = format(width)
    (bits & (encoding.signBit - 1L)) > encoding.infinity
  }

  /** IEEE 754-2008 minNum with -0 below +0: a NaN operand yields the other operand. */
  def minNum(left: Long, right: Long, width: Int in (16, 32, 64)): Long =
    extremum(left, right, width, smaller = true, propagate = false)

  /** IEEE 754-2008 maxNum with +0 above -0: a NaN operand yields the other operand. */
  def maxNum(left: Long, right: Long, width: Int in (16, 32, 64)): Long =
    extremum(left, right, width, smaller = false, propagate = false)

  /** IEEE 754-2019 minimum: any NaN operand yields a quiet NaN, and -0 is below +0. */
  def minimum(left: Long, right: Long, width: Int in (16, 32, 64)): Long =
    extremum(left, right, width, smaller = true, propagate = true)

  /** IEEE 754-2019 maximum: any NaN operand yields a quiet NaN, and +0 is above -0. */
  def maximum(left: Long, right: Long, width: Int in (16, 32, 64)): Long =
    extremum(left, right, width, smaller = false, propagate = true)

  private def extremum(
      left: Long,
      right: Long,
      width: Int in (16, 32, 64),
      smaller: Boolean,
      propagate: Boolean
  ): Long = {
    val encoding = format(width)
    val a        = Bits.truncate(left, width)
    val b        = Bits.truncate(right, width)
    val leftNaN  = isNaN(a, width)
    val rightNaN = isNaN(b, width)
    if (leftNaN || rightNaN) {
      if (propagate || (leftNaN && rightNaN)) (if (leftNaN) a else b) | encoding.quietBit
      else if (leftNaN) b
      else a
    } else {
      def order(bits: Long): Long = {
        val magnitude = bits & (encoding.signBit - 1L)
        if ((bits & encoding.signBit) != 0L) -magnitude else magnitude
      }
      val comparison = java.lang.Long.compare(order(a), order(b))
      if (comparison == 0) { if (((a & encoding.signBit) != 0L) == smaller) a else b }
      else if ((comparison < 0) == smaller) a
      else b
    }
  }

  def add(left: Long, right: Long, width: Int in (16, 32, 64)): Long = width.raw match {
    // binary16 sums and products are exact in binary64
    case 16 => doubleToBinary16(toDouble(left, width) + toDouble(right, width)).toLong
    case 32 => floatToBinary32(binary32ToFloat(Bits.low32(left)) + binary32ToFloat(Bits.low32(right))).toLong
    case _  => doubleToBinary64(binary64ToDouble(left) + binary64ToDouble(right))
  }

  def multiply(left: Long, right: Long, width: Int in (16, 32, 64)): Long = width.raw match {
    case 16 => doubleToBinary16(toDouble(left, width) * toDouble(right, width)).toLong
    case 32 => floatToBinary32(binary32ToFloat(Bits.low32(left)) * binary32ToFloat(Bits.low32(right))).toLong
    case _  => doubleToBinary64(binary64ToDouble(left) * binary64ToDouble(right))
  }

  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def fusedMultiplyAdd(
      left: Long,
      right: Long,
      addend: Long,
      width: Int in (16, 32, 64),
      rounding: Rounding = Rounding.NearestEven
  ): Long =
    if (rounding != Rounding.NearestEven)
      fusedMultiplyAddExact(toDouble(left, width), toDouble(right, width), toDouble(addend, width), width, rounding)
    else width.raw match {
      // a binary16 fma result that stays finite is either exact in binary64 or decided by its dominant term
      case 16 =>
        doubleToBinary16(Math.fma(toDouble(left, width), toDouble(right, width), toDouble(addend, width))).toLong
      case 32 =>
        floatToBinary32(
          Math.fma(
            binary32ToFloat(Bits.low32(left)),
            binary32ToFloat(Bits.low32(right)),
            binary32ToFloat(Bits.low32(addend))
          )
        ).toLong
      case _ => doubleToBinary64(Math.fma(binary64ToDouble(left), binary64ToDouble(right), binary64ToDouble(addend)))
    }

  /** `left * right + addend` computed exactly and rounded once into `width`. */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def fusedMultiplyAddExact(
      left: Double,
      right: Double,
      addend: Double,
      width: Int in (16, 32, 64),
      rounding: Rounding
  ): Long = {
    val nearest = Math.fma(left, right, addend)
    if (!left.isFinite || !right.isFinite || !addend.isFinite || (width.raw == 64 && rounding == Rounding.NearestEven))
      fromDouble(nearest, width, rounding)
    else {
      // BigInt rather than BigDecimal: Scala Native's BigDecimal multiply and divide are wrong for some operands
      val (leftSignificand, leftExponent)     = exactParts(left)
      val (rightSignificand, rightExponent)   = exactParts(right)
      val (addendSignificand, addendExponent) = exactParts(addend)
      val productExponent                     = leftExponent + rightExponent
      val scale                               = math.min(productExponent, addendExponent)
      val exact                               = ((leftSignificand * rightSignificand) << (productExponent - scale)) +
        (addendSignificand << (addendExponent - scale))
      if (exact.signum == 0) {
        val positiveTerms = (left == 0.0 || right == 0.0) && (doubleToBinary64(left) ^ doubleToBinary64(right)) >= 0L &&
          doubleToBinary64(addend) >= 0L
        fromDouble(if (rounding == Rounding.Down && !positiveTerms) -0.0 else nearest, width)
      } else {
        val magnitude = exact.abs
        // 62 bits keep at least two bits plus a sticky bit below a binary64 significand
        val dropped = math.max(magnitude.bitLength - 62, 0)
        val sticky  = if (dropped > 0 && magnitude.lowestSetBit < dropped) 1L else 0L
        encode(format(width), exact.signum < 0, (magnitude >> dropped).toLong | sticky, scale + dropped, rounding)
      }
    }
  }

  /** Finite `value` as `significand * 2^exponent` with a signed integer significand. */
  private def exactParts(value: Double): (BigInt, Int) = {
    val bits        = doubleToBinary64(value)
    val encoded     = ((bits >>> 52) & 0x7ffL).toInt
    val fraction    = bits & 0xfffffffffffffL
    val significand = if (encoded == 0) fraction else fraction | (1L << 52)
    (BigInt(if (bits < 0L) -significand else significand), if (encoded == 0) -1074 else encoded - 1075)
  }

}
