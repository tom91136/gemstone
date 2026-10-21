package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*

/** Pure arithmetic and lane-selection semantics shared by instruction execution. */
private object VMArithmetic {
  // the leading 1184 fraction bits of 2/pi, which V_TRIG_PREOP_F64 reads as zero beyond
  private val TwoOverPi = BigInt(
    "a2f9836e4e441529fc2757d1f534ddc0db6295993c439041fe5163abdebbc561b7246e3a424dd2e006492eea09d1921c" +
      "fe1deb1cb129a73ee88235f52ebb4484e99c7026b45f7e413991d639835339f49c845f8bbdf9283b1ff897ffde05980f" +
      "ef2f118b5a0a6d1f6d367ecf27cb09b74f463f669e5fea2d7527bac7ebe5f17b3d0739f78a5292ea6bfb5fb11f8d5d08" +
      "56033046",
    16
  )

  /** The low and high 64 bits of `value`, `width` bits wide, placed `at` bits into a 128-bit word. */
  inline def placedLow(value: Long, at: Int): Long = if (at >= 64) 0L else value << at

  inline def placedHigh(value: Long, at: Int, width: Int): Long =
    if (at >= 64) value << (at - 64) else if (at + width > 64) value >>> (64 - at) else 0L

  def trigPreop(value: Double, segment: Int): Double = {
    val exponent = ((IEEE754.doubleToBinary64(value) >>> 52) & 0x7ffL).toInt
    val shift    = (segment & 0x1f) * 53 + (exponent - 1077).max(0)
    val chunk    = ((TwoOverPi >> (1184 - 53 - shift)) & 0x1fffffffffffffL).toLong
    val scale    = -53 - shift + (if (exponent >= 1968) 128 else 0)
    Math.scalb(chunk.toDouble, scale)
  }

  /** OMOD scaling by `factor` runs with output denormals flushed: a denormal result flushes before and after scaling,
    * and -0 becomes +0.
    */
  def outputModified(bits: Long, factor: Double in (0.5, 2.0, 4.0), width: Int in (16, 32, 64)): Long = {
    val (value, minimum) = width.raw match {
      case 64 => (IEEE754.binary64ToDouble(bits), java.lang.Double.MIN_NORMAL)
      case 32 => (IEEE754.binary32ToFloat(Bits.low32(bits)).toDouble, java.lang.Float.MIN_NORMAL.toDouble)
      case _  => (IEEE754.binary16ToFloat(Bits.low16(bits)).toDouble, Binary16MinNormal)
    }
    if (value.isNaN) bits
    else {
      // Scaling by a power of two is exact, so the double carries every width; adding +0 turns -0 into +0.
      val scaled = flushDenormal(flushDenormal(value, minimum) * factor, minimum) + 0.0
      width.raw match {
        case 64 => IEEE754.doubleToBinary64(scaled)
        case 32 => IEEE754.floatToBinary32(scaled.toFloat).toLong
        case _  => IEEE754.floatToBinary16(scaled.toFloat).toLong
      }
    }
  }

  private val Binary16MinNormal = 6.103515625e-5

  private def flushDenormal(value: Double, minimum: Double): Double =
    if (value != 0.0 && math.abs(value) < minimum) math.copySign(0.0, value) else value

  private def fractionBits(width: Int in (16, 32, 64)): Int = if (width == 64) 52 else if (width == 32) 23 else 10

  private def biasedExponent(bits: Long, width: Int in (16, 32, 64)): Int =
    ((bits & IEEE754.infinity(width)) >>> fractionBits(width)).toInt

  /** The NaN an invalid operation gives: the negative default NaN. */
  private def defaultNaN(width: Int in (16, 32, 64)): Long =
    IEEE754.signBit(width) | IEEE754.infinity(width) | IEEE754.quietBit(width)

  /** V_DIV_FIXUP on `width`-bit floats: a NaN numerator, else a NaN denominator, quieted; the default NaN for 0/0 and
    * inf/inf; an infinity for x/0, inf/y and a NaN quotient, and a zero for x/inf, 0/y and a quotient whose exponent
    * falls below every denormal, signed by both operands; otherwise the quotient's magnitude with that sign.
    */
  def divisionFixup(quotient: Long, denominator: Long, numerator: Long, width: Int in (16, 32, 64)): Long = {
    val signBit   = IEEE754.signBit(width)
    val magnitude = signBit - 1L
    val infinity  = IEEE754.infinity(width)
    val sign      = (numerator ^ denominator) & signBit
    val (d, n)    = (denominator & magnitude, numerator & magnitude)
    val underflow = fractionBits(width) + (if (width == 64) 1023 else if (width == 32) 127 else 15)
    if (IEEE754.isNaN(numerator, width)) (numerator & (signBit | magnitude)) | IEEE754.quietBit(width)
    else if (IEEE754.isNaN(denominator, width)) (denominator & (signBit | magnitude)) | IEEE754.quietBit(width)
    else if ((d == 0L && n == 0L) || (d == infinity && n == infinity)) defaultNaN(width)
    else if (d == 0L || n == infinity) sign | infinity
    else if (d == infinity || n == 0L) sign
    else if (biasedExponent(numerator, width) - biasedExponent(denominator, width) < -underflow) sign
    else if (IEEE754.isNaN(quotient, width)) sign | infinity
    else sign | (quotient & magnitude)
  }

  /** V_DIV_SCALE on `width`-bit floats: `value`, the denominator or the numerator, scaled by 2^64 or 2^-64, 2^128 or
    * 2^-128 for binary64, so the quotient's reciprocal steps stay out of the denormal range. A zero operand gives the
    * default NaN and a NaN `value` passes; otherwise the operands' biased exponents alone decide.
    */
  def divisionScale(value: Long, denominator: Long, numerator: Long, wide: Int in (32, 64)): Long = {
    val width     = wide.widen[Int in (16, 32, 64)]
    val magnitude = IEEE754.signBit(width) - 1L
    if ((denominator & magnitude) == 0L || (numerator & magnitude) == 0L) defaultNaN(width)
    else if (IEEE754.isNaN(value, width)) value
    else {
      val (scale, far, top, tiny) = if (width == 64) (128, 768, 2045, 54) else (64, 96, 253, 24)
      val d                       = biasedExponent(denominator, width)
      val difference              = biasedExponent(numerator, width) - d
      val power                   =
        if (difference >= far) if (value == denominator) scale else 0
        else if (d == 0) scale
        else if (d >= top) if (value == denominator || difference > -far) -scale else 0
        else if (difference <= -far) if (value == numerator) scale else 0
        else if (biasedExponent(numerator, width) <= tiny) scale
        else 0
      if (power == 0) value
      else if (width == 64) IEEE754.doubleToBinary64(Math.scalb(IEEE754.binary64ToDouble(value), power))
      else IEEE754.floatToBinary32(Math.scalb(IEEE754.binary32ToFloat(Bits.low32(value)), power)).toLong
    }
  }

  /** The flag V_DIV_SCALE leaves for V_DIV_FMAS: whether the numerator's and denominator's biased exponents differ by
    * 96, or 768 for binary64, either way, whatever their values.
    */
  def divisionScaleCondition(denominator: Long, numerator: Long, wide: Int in (32, 64)): Boolean = {
    val width = wide.widen[Int in (16, 32, 64)]
    math.abs(biasedExponent(numerator, width) - biasedExponent(denominator, width)) >= (if (width == 64) 768 else 96)
  }

  /** V_DIV_FMAS on finite `width`-bit floats where V_DIV_SCALE left its flag: `a * b + c` scaled by 2^64, or 2^128 for
    * binary64, up when the addend's magnitude is at least one and down otherwise, and rounded once.
    */
  def divisionFusedMultiplyAdd(a: Long, b: Long, c: Long, wide: Int in (32, 64)): Long = {
    val width = wide.widen[Int in (16, 32, 64)]
    val bias  = if (width == 64) 1023 else 127
    val scale = if (width == 64) 128 else 64
    IEEE754.finiteFusedMultiplyAdd(
      IEEE754.toDouble(a, width),
      IEEE754.toDouble(b, width),
      IEEE754.toDouble(c, width),
      width,
      IEEE754.Rounding.NearestEven,
      if (biasedExponent(c, width) >= bias) scale else -scale
    )
  }

  def floatingLiteralToDouble(raw: Long): Double =
    IEEE754.binary64ToDouble(Bits.join32(0L, Bits.low32(raw).toLong))

  def waveConditionMask(mask: Long, lanes: Int in 1 ~ 64): Long =
    if (lanes <= 32) Bits.low32(mask).toLong else mask
}
