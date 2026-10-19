package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** Conversions between binary32 and the 8-bit floats, CDNA3's FNUZ formats and the OCP formats of gfx950 and GFX12
  * (MI300 ISA, 7.2). Widening is exact. Narrowing rounds to nearest even, or stochastically: a seed's high bits add to
  * the significand bits the format drops, the sum wrapping within the significand, and the result truncates. A
  * magnitude beyond the format's largest finite one, infinity included, saturates under MODE.FP16_OVFL and otherwise
  * becomes the format's infinity, or NaN where it has none.
  */
private[isa] object Minifloats {
  private inline def bits[T](value: T): Long = toRaw[T](value)

  private inline val Fp16Overflow = 1L << 23

  private def pair(format: Minifloat, value: Long): UInt[64] = fromRaw[UInt[64]](
    bits(Float32(format.toDouble(value))) | (bits(Float32(format.toDouble(value >>> format.width))) << 32)
  )

  def fromE4M3(value: Float8E4M3): Float32         = Float32(value.toDouble)
  def fromE5M2(value: Float8E5M2): Float32         = Float32(value.toDouble)
  def fromE4M3Fnuz(value: Float8E4M3FNUZ): Float32 = Float32(value.toDouble)
  def fromE5M2Fnuz(value: Float8E5M2FNUZ): Float32 = Float32(value.toDouble)

  /** Two elements, the low byte's in the low dword. */
  def pairFromE4M3(value: UInt[16]): UInt[64]     = pair(Minifloat.E4M3, bits(value) & 0xffffL)
  def pairFromE5M2(value: UInt[16]): UInt[64]     = pair(Minifloat.E5M2, bits(value) & 0xffffL)
  def pairFromE4M3Fnuz(value: UInt[16]): UInt[64] = pair(Minifloat.E4M3FNUZ, bits(value) & 0xffffL)
  def pairFromE5M2Fnuz(value: UInt[16]): UInt[64] = pair(Minifloat.E5M2FNUZ, bits(value) & 0xffffL)

  /** `value` rounded as `rounding` says to `format`'s code; NaN keeps its sign in the OCP formats, as ROCm's emulation
    * of the conversions does.
    */
  private def narrowed(value: Long, format: Minifloat, rounding: IEEE754.Rounding, mode: UInt[32]): Long = {
    val real = java.lang.Float.intBitsToFloat(value.toInt).toDouble
    if (real.isNaN) narrowedNaN(value, format) else rounded(real, format, rounding, mode)
  }

  private def narrowedNaN(value: Long, format: Minifloat): Long = format match {
    case Minifloat.E4M3FNUZ | Minifloat.E5M2FNUZ => 0x80L
    case _                                       => ((value >>> 24) & 0x80L) | 0x7fL
  }

  private def rounded(real: Double, format: Minifloat, rounding: IEEE754.Rounding, mode: UInt[32]): Long = {
    val saturate  = (bits(mode) & Fp16Overflow) != 0
    val beyond    = Math.abs(real) > format.largest
    val magnitude = if (beyond) Math.copySign(Double.PositiveInfinity, real) else real
    format.fromDouble(magnitude, if (beyond) IEEE754.Rounding.NearestEven else rounding, saturate)
  }

  /** V_CVT_PK_*8_F32: `a` and `b` rounded to nearest even into the low and high bytes of `old`'s low or high word. */
  def pack(a: Float32, b: Float32, old: UInt[32], mode: UInt[32], format: Minifloat, high: Boolean): UInt[32] = {
    val word = narrowed(bits(a), format, IEEE754.Rounding.NearestEven, mode) |
      (narrowed(bits(b), format, IEEE754.Rounding.NearestEven, mode) << 8)
    fromRaw[UInt[32]](
      if (high) (bits(old) & 0xffffL) | (word << 16) else (bits(old) & 0xffff0000L) | word
    )
  }

  /** V_CVT_SR_*8_F32: `value` rounded stochastically by `seed` into byte `position` of `old`. */
  def stochastic(
      value: Float32,
      seed: UInt[32],
      old: UInt[32],
      mode: UInt[32],
      format: Minifloat,
      position: Int
  ): UInt[32] = {
    val raw      = bits(value)
    val dropped  = 23 - format.fractionBits
    val special  = (raw & 0x7f800000L) == 0x7f800000L
    val mantissa = ((raw & 0x7fffffL) + (bits(seed) >>> (32 - dropped))) & 0x7fffffL
    val sum      = if (special) raw else (raw & 0xff800000L) | mantissa
    val code     = narrowed(sum, format, IEEE754.Rounding.TowardZero, mode)
    val shift    = 8 * position
    fromRaw[UInt[32]]((bits(old) & ~(0xffL << shift) & 0xffffffffL) | (code << shift))
  }

  /** The binary32, binary16 or bfloat16 side of a gfx950 scaled conversion, and the significand bits it carries. */
  enum Wide(val fractionBits: Int in (7, 10, 23)) derives CanEqual {
    case Single extends Wide(23)
    case Half   extends Wide(10)
    case BFloat extends Wide(7)

    def toDouble(value: Long): Double = this match {
      case Single => IEEE754.toDouble(value, 32)
      case Half   => IEEE754.toDouble(value, 16)
      case BFloat => Minifloat.BFloat16.toDouble(value)
    }

    def fromDouble(value: Double): Long = this match {
      case Single => IEEE754.fromDouble(value, 32)
      case Half   => IEEE754.fromDouble(value, 16)
      case BFloat => IEEE754.doubleToBfloat16(value).toLong
    }
  }

  /** The E8M0 exponent a scale operand carries in its binary32 exponent field (CDNA4 ISA, 7.3). */
  private[isa] def exponent(scale: Float32): Int = ((bits(scale) >>> 23) & 0xffL).toInt

  /** `real` rounded to `format`: the 8-bit formats overflow as [[rounded]] says, the 6- and 4-bit ones saturate. */
  private def scaledCode(real: Double, format: Minifloat, rounding: IEEE754.Rounding, mode: UInt[32]): Long =
    if (format.special == FloatFormat.Special.Finite) format.fromDouble(real, rounding, saturate = true)
    else rounded(real, format, rounding, mode)

  /** NaN in `format` with `negative`'s sign, or the signed largest magnitude where the format has no NaN, as ROCm's
    * emulation of the conversions gives.
    */
  private def scaledNaN(negative: Boolean, format: Minifloat): Long = {
    val sign = if (negative) 1L << (format.width - 1) else 0L
    if (format.special == FloatFormat.Special.Finite) sign | format.maximum else sign | 0x7fL
  }

  /** V_CVT_SCALEF32_*: `code` in `format` times two to the scale's exponent less 127, rounded to nearest even into
    * `side`; an all-ones scale exponent is NaN.
    */
  def widenScaled(code: UInt[32], scale: Float32, format: Minifloat, side: Wide): UInt[32] = {
    val power = exponent(scale)
    fromRaw[UInt[32]](
      side.fromDouble(if (power == 0xff) Double.NaN else Math.scalb(format.toDouble(bits(code)), power - 127))
    )
  }

  /** V_CVT_SCALEF32_*: the `side` float `value` divided by two to the scale's exponent less 127, rounded to nearest
    * even into `format`'s code.
    */
  def narrowScaled(value: UInt[32], scale: Float32, mode: UInt[32], side: Wide, format: Minifloat): UInt[32] = {
    val power = exponent(scale)
    val real  = side.toDouble(bits(value))
    fromRaw[UInt[32]](
      if (power == 0xff || real.isNaN) scaledNaN(real < 0.0 || 1.0 / real < 0.0, format)
      else scaledCode(Math.scalb(real, 127 - power), format, IEEE754.Rounding.NearestEven, mode)
    )
  }

  /** `magnitude`, which carries at most `sourceBits` significand bits after its leading one, rounded stochastically
    * onto the grid of a format with `fractionBits` significand bits and least normal exponent `minimum`: the seed's
    * high bits add, below the grid's step, to the magnitude truncated to `sourceBits` there, and the sum truncates onto
    * the grid.
    */
  private def stochasticMagnitude(magnitude: Double, sourceBits: Int, fractionBits: Int, minimum: Int, seed: Long)
      : Double = {
    val dropped   = sourceBits - fractionBits
    val unit      = math.max(Math.getExponent(magnitude), minimum) - sourceBits
    val truncated = Math.floor(Math.scalb(magnitude, -unit)).toLong
    Math.scalb(((truncated + ((seed & 0xffffffffL) >>> (32 - dropped))) >>> dropped).toDouble, unit + dropped)
  }

  /** V_CVT_SCALEF32_SR_*: the `side` float `value` divided by two to the scale's exponent less 127 and rounded
    * stochastically by `seed` into `format`'s code, overflowing as rounding to nearest does.
    */
  def stochasticScaled(value: UInt[32], seed: UInt[32], scale: Float32, mode: UInt[32], side: Wide, format: Minifloat)
      : UInt[32] = {
    val power    = exponent(scale)
    val real     = side.toDouble(bits(value))
    val negative = real < 0.0 || 1.0 / real < 0.0
    fromRaw[UInt[32]](
      if (power == 0xff || real.isNaN) scaledNaN(negative, format)
      else if (real == 0.0 || real.isInfinite) scaledCode(real, format, IEEE754.Rounding.NearestEven, mode)
      else {
        val magnitude = stochasticMagnitude(
          Math.scalb(Math.abs(real), 127 - power),
          side.fractionBits,
          format.fractionBits,
          1 - format.bias,
          bits(seed)
        )
        scaledCode(if (negative) -magnitude else magnitude, format, IEEE754.Rounding.TowardZero, mode)
      }
    )
  }

  /** V_CVT_SR_F16_F32 and V_CVT_SR_BF16_F32: `value` rounded stochastically by `seed` into `side`, past whose largest
    * finite magnitude it overflows to infinity.
    */
  def stochasticWide(value: Float32, seed: UInt[32], side: Wide): UInt[32] = {
    val real = java.lang.Float.intBitsToFloat(bits(value).toInt).toDouble
    fromRaw[UInt[32]](
      if (real.isNaN || real.isInfinite || real == 0.0) side.fromDouble(real)
      else {
        val minimum   = if (side == Wide.Half) -14 else -126
        val magnitude = stochasticMagnitude(Math.abs(real), 23, side.fractionBits, minimum, bits(seed))
        side.fromDouble(if (real < 0.0) -magnitude else magnitude)
      }
    )
  }

  /** V_PRNG_B32: one step of the linear feedback shift register (CDNA4 ISA, V_PRNG_B32). */
  def prng(seed: UInt[32]): UInt[32] =
    fromRaw[UInt[32]](((bits(seed) << 1) ^ (if ((bits(seed) & 0x80000000L) != 0L) 197L else 0L)) & 0xffffffffL)
}
