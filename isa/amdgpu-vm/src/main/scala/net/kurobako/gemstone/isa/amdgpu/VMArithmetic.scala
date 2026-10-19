package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*

/** Pure arithmetic and lane-selection semantics shared by instruction execution. */
private object VMArithmetic {
  // The instruction's nonnegative left shift is masked to 53 bits, so only this tail of 2/pi contributes.
  private inline val TrigPreopBits = 0x856033046fc7bL

  /** The low and high 64 bits of `value`, `width` bits wide, placed `at` bits into a 128-bit word. */
  inline def placedLow(value: Long, at: Int): Long = if (at >= 64) 0L else value << at

  inline def placedHigh(value: Long, at: Int, width: Int): Long =
    if (at >= 64) value << (at - 64) else if (at + width > 64) value >>> (64 - at) else 0L

  def trigPreop(value: Double, segment: Int): Double = {
    val exponent = ((IEEE754.doubleToBinary64(value) >>> 52) & 0x7ffL).toInt
    val shift    = (segment & 0x1f) * 53 + (exponent - 1077).max(0)
    val chunk    = if (shift >= 53) 0L else (TrigPreopBits << shift) & 0x1fffffffffffffL
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

  def divisionFixupF32(quotient: Float, denominator: Float, numerator: Float): Float = {
    val negative =
      ((java.lang.Float.floatToRawIntBits(numerator) ^
        java.lang.Float.floatToRawIntBits(denominator)) & 0x80000000) != 0
    val sign = if (negative) -1.0f else 1.0f
    if (
      numerator.isNaN || denominator.isNaN || (numerator.isInfinite && denominator.isInfinite) ||
      (numerator == 0.0f && denominator == 0.0f)
    ) Float.NaN
    else if (denominator == 0.0f || numerator.isInfinite) Math.copySign(Float.PositiveInfinity, sign)
    else if (denominator.isInfinite || numerator == 0.0f) Math.copySign(0.0f, sign)
    else if (quotient.isNaN) numerator / denominator
    else Math.copySign(math.abs(quotient), sign)
  }

  def divisionFixupF64(quotient: Double, denominator: Double, numerator: Double): Double = {
    val sign = if ((IEEE754.doubleToBinary64(numerator) ^ IEEE754.doubleToBinary64(denominator)) < 0L) -1.0 else 1.0
    if (
      numerator.isNaN || denominator.isNaN || (numerator.isInfinite && denominator.isInfinite) ||
      (numerator == 0.0 && denominator == 0.0)
    ) Double.NaN
    else if (denominator == 0.0 || numerator.isInfinite) Math.copySign(Double.PositiveInfinity, sign)
    else if (denominator.isInfinite || numerator == 0.0) Math.copySign(0.0, sign)
    else quotient
  }

  private def denormal(value: Float): Boolean =
    value != 0.0f && !value.isNaN && !value.isInfinite && math.abs(value) < java.lang.Float.MIN_NORMAL

  private def denormal(value: Double): Boolean =
    value != 0.0 && !value.isNaN && !value.isInfinite && math.abs(value) < java.lang.Double.MIN_NORMAL

  /** V_DIV_SCALE_F32: `value`, one of the denominator or numerator, scaled by 2^64 or 2^-64 so the quotient's
    * reciprocal steps stay out of the denormal range.
    */
  def divisionScaleF32(value: Float, denominator: Float, numerator: Float): Float =
    if (denominator == 0.0f || numerator == 0.0f) Float.NaN
    else if (Math.getExponent(numerator) - Math.getExponent(denominator) >= 96)
      if (value == denominator) Math.scalb(value, 64) else value
    else if (denormal(denominator)) Math.scalb(value, 64)
    else if (denormal(1.0f / denominator) && denormal(numerator / denominator))
      if (value == denominator) Math.scalb(value, 64) else value
    else if (denormal(1.0f / denominator)) Math.scalb(value, -64)
    else if (denormal(numerator / denominator)) if (value == numerator) Math.scalb(value, 64) else value
    else if (Math.getExponent(numerator) <= 23 - 127) Math.scalb(value, 64)
    else value

  /** Whether V_DIV_SCALE_F32 leaves the flag V_DIV_FMAS_F32 rescales its result on. */
  def divisionScaleConditionF32(denominator: Float, numerator: Float): Boolean =
    if (denominator == 0.0f || numerator == 0.0f) false
    else if (Math.getExponent(numerator) - Math.getExponent(denominator) >= 96) true
    else if (denormal(denominator)) false
    else denormal(numerator / denominator)

  /** V_DIV_SCALE_F64, scaling by 2^128 or 2^-128. */
  def divisionScaleF64(value: Double, denominator: Double, numerator: Double): Double =
    if (denominator == 0.0 || numerator == 0.0) Double.NaN
    else if (Math.getExponent(numerator) - Math.getExponent(denominator) >= 768)
      if (value == denominator) Math.scalb(value, 128) else value
    else if (denormal(denominator)) Math.scalb(value, 128)
    else if (denormal(1.0 / denominator) && denormal(numerator / denominator))
      if (value == denominator) Math.scalb(value, 128) else value
    else if (denormal(1.0 / denominator)) Math.scalb(value, -128)
    else if (denormal(numerator / denominator)) if (value == numerator) Math.scalb(value, 128) else value
    else if (Math.getExponent(numerator) <= 53 - 1023) Math.scalb(value, 128)
    else value

  def divisionScaleConditionF64(denominator: Double, numerator: Double): Boolean =
    if (denominator == 0.0 || numerator == 0.0) false
    else if (Math.getExponent(numerator) - Math.getExponent(denominator) >= 768) true
    else if (denormal(denominator)) false
    else denormal(numerator / denominator)

  def floatingLiteralToDouble(raw: Long): Double =
    IEEE754.binary64ToDouble(Bits.join32(0L, Bits.low32(raw).toLong))

  def waveConditionMask(mask: Long, lanes: Int in 1 ~ 64): Long =
    if (lanes <= 32) Bits.low32(mask).toLong else mask
}
