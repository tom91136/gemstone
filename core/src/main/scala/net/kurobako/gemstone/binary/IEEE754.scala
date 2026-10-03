package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*

/** Raw IEEE-754 interchange encodings used by ISA registers and memory. */
object IEEE754 {
  enum Rounding {
    case NearestEven, TowardZero, Up, Down
  }

  /** Convert a signed or unsigned 64-bit register without rounding through Double first. */
  def integerToBinary(
      value: Long,
      unsigned: Boolean,
      width: Int in (32, 64),
      rounding: Rounding = Rounding.NearestEven
  ): Long = {
    val negative  = !unsigned && value < 0L
    val magnitude = if (negative) -value else value
    if (magnitude == 0L) 0L
    else {
      val fractionBits = if (width.raw == 32) 23 else 52
      val bias         = if (width.raw == 32) 127 else 1023
      val exponent     = 63 - java.lang.Long.numberOfLeadingZeros(magnitude)
      val shift        = exponent - fractionBits
      val significand  = if (shift <= 0) magnitude << -shift else magnitude >>> shift
      val remainder    = if (shift <= 0) 0L else magnitude & ((1L << shift) - 1L)
      val increment    = if (remainder == 0L) false
      else rounding match {
        case Rounding.TowardZero  => false
        case Rounding.Up          => !negative
        case Rounding.Down        => negative
        case Rounding.NearestEven =>
          val half = 1L << (shift - 1)
          remainder > half || (remainder == half && (significand & 1L) != 0L)
      }
      val rounded  = significand + (if (increment) 1L else 0L)
      val carry    = rounded >>> (fractionBits + 1)
      val fraction = (if (carry != 0L) rounded >>> 1 else rounded) & ((1L << fractionBits) - 1L)
      (if (negative) 1L << (width.raw - 1) else 0L) |
        ((exponent.toLong + bias + carry) << fractionBits) | fraction
    }
  }

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

  private inline val Binary16Bias        = 15
  private inline val Binary16ExponentMax = 0x1f
  private inline val Binary32Bias        = 127
  private inline val Binary64Bias        = 1023
  private inline val Binary16Infinity    = 0x7c00
  private inline val Binary16NaN         = 0x7e00
  private inline val Binary32Infinity    = 0x7f800000

  inline def binary32ToFloat(bits: Binary32): Float = java.lang.Float.intBitsToFloat(bits.toInt)

  inline def floatToBinary32(value: Float): Binary32 =
    Binary32.fromLow(Bits.unsigned32(java.lang.Float.floatToRawIntBits(value)))

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
          sign | ((Binary32Bias - 14 - shift) << 23) | ((mantissa & 0x3ff) << 13)
        }
      } else if (exponent == Binary16ExponentMax) sign | Binary32Infinity | (fraction << 13)
      else sign | ((exponent - Binary16Bias + Binary32Bias) << 23) | (fraction << 13)
    java.lang.Float.intBitsToFloat(bits32)
  }

  def floatToBinary16(value: Float): Binary16 = {
    val bits     = java.lang.Float.floatToRawIntBits(value)
    val sign     = (bits >>> 16) & 0x8000
    val exponent = (bits >>> 23) & 0xff
    val fraction = bits & 0x7fffff
    val encoded  =
      if (exponent == 0xff) sign | (if (fraction == 0) Binary16Infinity else Binary16NaN)
      else {
        val halfExponent = exponent - Binary32Bias + Binary16Bias
        if (halfExponent >= Binary16ExponentMax) sign | Binary16Infinity
        else if (halfExponent <= 0) {
          if (halfExponent < -10) sign
          else {
            val mantissa = fraction | 0x800000
            val shift    = 14 - halfExponent
            val rounded  = (mantissa + (1 << (shift - 1)) - 1 + ((mantissa >>> shift) & 1)) >>> shift
            sign | rounded
          }
        } else {
          val rounded = fraction + 0xfff + ((fraction >>> 13) & 1)
          if ((rounded & 0x800000) != 0) {
            val nextExponent = halfExponent + 1
            if (nextExponent >= Binary16ExponentMax) sign | Binary16Infinity else sign | (nextExponent << 10)
          } else sign | (halfExponent << 10) | (rounded >>> 13)
        }
      }
    Binary16.low(encoded.toLong)
  }

  /** One round-to-nearest, ties-to-even step from the full binary64 significand. */
  def doubleToBinary16(value: Double): Binary16 = {
    val bits        = java.lang.Double.doubleToRawLongBits(value)
    val sign        = (bits >>> 48) & 0x8000L
    val rawExponent = ((bits >>> 52) & 0x7ffL).toInt
    val fraction    = bits & 0x000fffffffffffffL
    val exponent    = rawExponent - Binary64Bias
    val encoded     =
      if (rawExponent == 0x7ff) sign | (if (fraction == 0L) Binary16Infinity.toLong else Binary16NaN.toLong)
      else if (exponent > Binary16Bias) sign | Binary16Infinity.toLong
      else if (exponent < -25) sign
      else {
        val significand = fraction | 0x0010000000000000L
        val shift       = if (exponent < -14) 28 - exponent else 42
        val retained    = significand >>> shift
        val discarded   = significand & ((1L << shift) - 1L)
        val halfway     = 1L << (shift - 1)
        val rounded     =
          retained + (if (discarded > halfway || (discarded == halfway && (retained & 1L) != 0L)) 1L else 0L)
        sign | (if (exponent < -14) rounded else ((exponent + 14).toLong << 10) + rounded)
      }
    Binary16.low(encoded)
  }

  def toDouble(bits: Long, width: Int in (16, 32, 64)): Double = width.raw match {
    case 16 => binary16ToFloat(Binary16.low(bits)).toDouble
    case 32 => binary32ToFloat(Binary32.fromLow(Bits.low32(bits))).toDouble
    case 64 => binary64ToDouble(bits)
  }

  def fromDouble(value: Double, width: Int in (16, 32, 64)): Long = width.raw match {
    case 16 => doubleToBinary16(value).toLong
    case 32 => floatToBinary32(value.toFloat).toLong
    case 64 => doubleToBinary64(value)
  }
}
