package net.kurobako.gemstone.binary

import net.kurobako.gemstone.binary.IEEE754.*
import net.kurobako.gemstone.constraint.*

object FloatFormat {

  /** Which encodings are not finite: IEEE-754's infinities and NaNs, a single NaN magnitude, NaN in place of negative
    * zero, none at all, an unsigned scale with all ones NaN, or IEEE-754's specials without a sign bit.
    */
  enum Special derives CanEqual {
    case Infinite, NaNOnly, NegativeZeroNaN, Finite, Scale, UnsignedInfinite
  }
}

/** A floating-point encoding of at most 32 bits: a sign bit unless unsigned, `exponentBits` of biased exponent, then
  * `fractionBits`. Every value is exactly a `Double`, so a conversion from one rounds once. Encodings are the low
  * `width` bits of a `Long`.
  */
class FloatFormat(
    val exponentBits: Int in 1 ~ 8,
    val fractionBits: Int in 0 ~ 23,
    val bias: Int,
    val special: FloatFormat.Special
) {
  import FloatFormat.*

  private val signed: Boolean   = special != Special.Scale && special != Special.UnsignedInfinite
  private val infinite: Boolean = special == Special.Infinite || special == Special.UnsignedInfinite

  val width: Int in 1 ~ 32 = ((if (signed) 1 else 0) + exponentBits + fractionBits).unsafe[Int in 1 ~ 32]

  private val signBit: Long        = if (signed) 1L << (width - 1) else 0L
  private val codeMask: Long       = (1L << width) - 1L
  private val maximumExponent: Int = (1 << exponentBits) - 1
  private val fractionMask: Long   = (1L << fractionBits) - 1L

  /** The largest finite magnitude, encoded. */
  val maximum: Long = special match {
    case Special.Infinite | Special.UnsignedInfinite => ((maximumExponent - 1).toLong << fractionBits) | fractionMask
    case Special.NaNOnly                             => (maximumExponent.toLong << fractionBits) | (fractionMask - 1L)
    case Special.Scale                               => maximumExponent.toLong - 1L
    case _                                           => (maximumExponent.toLong << fractionBits) | fractionMask
  }

  /** The quiet NaN conversions produce, or the largest finite magnitude where the format has no NaN. */
  val nan: Long = special match {
    case Special.Infinite | Special.UnsignedInfinite =>
      (maximumExponent.toLong << fractionBits) | (1L << (fractionBits - 1))
    case Special.NaNOnly         => (maximumExponent.toLong << fractionBits) | fractionMask
    case Special.NegativeZeroNaN => signBit
    case Special.Scale           => maximumExponent.toLong
    case Special.Finite          => maximum
  }

  def isNaN(bits: Long): Boolean = {
    val code = bits & codeMask
    special match {
      case Special.Infinite | Special.UnsignedInfinite =>
        ((code & ~signBit) >>> fractionBits) == maximumExponent.toLong && (code & fractionMask) != 0L
      case Special.NaNOnly         => (code & ~signBit) == ((maximumExponent.toLong << fractionBits) | fractionMask)
      case Special.NegativeZeroNaN => code == signBit
      case Special.Scale           => code == maximumExponent.toLong
      case Special.Finite          => false
    }
  }

  // a format with binary32's exponent is the upper bits of the binary32 of the same value
  private val floatShift: Int = if (exponentBits == 8 && special == Special.Infinite) 23 - fractionBits else -1

  // every value of a format of at most 8 bits
  private val decoded: Array[Double] =
    if (width <= 8) Array.tabulate(1 << width)(code => decode(code.toLong)) else Array.empty

  /** The largest finite magnitude. */
  val largest: Double = toDouble(maximum)

  /** The value `bits` encodes, exactly. */
  def toDouble(bits: Long): Double =
    if (decoded.nonEmpty) decoded((bits & codeMask).toInt)
    else if (floatShift >= 0) {
      val value = java.lang.Float.intBitsToFloat(((bits & codeMask) << floatShift).toInt)
      if (value.isNaN) Double.NaN else value.toDouble
    } else decode(bits)

  private def decode(bits: Long): Double = {
    val code     = bits & codeMask
    val negative = (code & signBit) != 0L
    val exponent = ((code & ~signBit) >>> fractionBits).toInt
    val fraction = code & fractionMask
    if (isNaN(code)) Double.NaN
    else if (infinite && exponent == maximumExponent)
      if (negative) Double.NegativeInfinity else Double.PositiveInfinity
    else if (special == Special.Scale) Math.scalb(1.0, exponent - bias)
    else {
      val magnitude =
        if (exponent == 0) Math.scalb(fraction.toDouble, 1 - bias - fractionBits)
        else Math.scalb(((1L << fractionBits) | fraction).toDouble, exponent - bias - fractionBits)
      if (negative) -magnitude else magnitude
    }
  }

  /** `value` rounded once to this format. With `saturate`, a magnitude beyond the largest finite one, infinity
    * included, becomes that largest one; without it, it becomes infinity or NaN as the format allows, and the largest
    * finite magnitude where it allows neither. An unsigned format takes a negative value to zero.
    */
  def fromDouble(value: Double, rounding: Rounding = Rounding.NearestEven, saturate: Boolean = false): Long = {
    val negative       = value < 0.0 || (value == 0.0 && 1.0 / value < 0.0)
    val sign           = if (negative) signBit else 0L
    def overflow: Long =
      if (saturate) sign | maximum
      else
        special match {
          case Special.Infinite | Special.UnsignedInfinite =>
            val toMaximum = rounding match {
              case Rounding.TowardZero => true
              case Rounding.Up         => negative
              case Rounding.Down       => !negative
              case _                   => false
            }
            sign | (if (toMaximum) maximum else maximumExponent.toLong << fractionBits)
          case Special.Finite => sign | maximum
          case _              => nan | (if (special == Special.NaNOnly) sign else 0L)
        }
    if (value.isNaN) nan
    else if (special == Special.UnsignedInfinite && negative) 0L
    else if (value.isInfinite) overflow
    else if (special == Special.Scale) {
      val magnitude = Math.abs(value)
      if (magnitude == 0.0 || negative) 0L
      else {
        val exponent = Math.getExponent(magnitude)
        val exact    = Math.scalb(1.0, exponent) == magnitude
        val rounded  = rounding match {
          case Rounding.Up | Rounding.NearestAway if !exact => exponent + 1
          case Rounding.NearestEven if !exact               =>
            if (magnitude >= Math.scalb(1.5, exponent)) exponent + 1 else exponent
          case _ => exponent
        }
        val code = rounded + bias
        if (code < 0) 0L else if (code > maximum) overflow else code.toLong
      }
    } else if (value == 0.0) if (special == Special.NegativeZeroNaN) 0L else sign
    else {
      val magnitude = Math.abs(value)
      val minimum   = 1 - bias
      val exponent  = math.max(Math.getExponent(magnitude), minimum)
      val scaled    = Math.scalb(magnitude, fractionBits - exponent)
      val integral  = IEEE754.integral(if (negative) -scaled else scaled, rounding).abs.toLong
      val carry     = integral >= (2L << fractionBits)
      val mantissa  = if (carry) integral >>> 1 else integral
      val adjusted  = if (carry) exponent + 1 else exponent
      val code      =
        if (mantissa >= (1L << fractionBits)) ((adjusted + bias).toLong << fractionBits) | (mantissa & fractionMask)
        else mantissa
      if (code > maximum) overflow
      else if (code == 0L && special == Special.NegativeZeroNaN) 0L
      else sign | code
    }
  }
}
