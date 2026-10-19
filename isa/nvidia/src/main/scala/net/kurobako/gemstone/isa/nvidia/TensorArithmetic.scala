package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import scala.annotation.targetName

/** The per-element arithmetic of matrix multiply-accumulate: matrix elements widen exactly to binary64 values or 64-bit
  * integers, and floating-point sums accumulate in blocks whose terms align to a shared exponent as
  * [[VMIR.Matrix.Accumulation.Blocked]] describes.
  */
private[isa] object TensorArithmetic {

  /** The alignment exponent of a zero product or accumulator, below every other. */
  val Absent: Int = -100000

  @targetName("widenHalf")
  def widen(value: Float16): Float64 = Float64(value.toDouble)
  @targetName("widenSingle")
  def widen(value: Float32): Float64 = Float64(value.toDouble)
  @targetName("widenBFloat16")
  def widen(value: BFloat16): Float64 = Float64(value.toDouble)
  @targetName("widenTensorFloat32")
  def widen(value: TensorFloat32): Float64 = Float64(value.toDouble)
  @targetName("widenE4M3")
  def widen(value: Float8E4M3): Float64 = Float64(value.toDouble)
  @targetName("widenE5M2")
  def widen(value: Float8E5M2): Float64 = Float64(value.toDouble)
  @targetName("widenE3M2")
  def widen(value: Float6E3M2): Float64 = Float64(value.toDouble)
  @targetName("widenE2M3")
  def widen(value: Float6E2M3): Float64 = Float64(value.toDouble)
  @targetName("widenE2M1")
  def widen(value: Float4E2M1): Float64 = Float64(value.toDouble)

  /** A power-of-two scale factor. */
  @targetName("scaleE8M0")
  def scale(value: ScaleE8M0): Float64 = Float64(value.toDouble)

  /** An unsigned E4M3 scale factor, whose sign bit is ignored. */
  @targetName("scaleE4M3")
  def scale(value: Float8E4M3): Float64 = Float64(Minifloat.E4M3.toDouble(toRaw(value) & 0x7fL))

  @targetName("widenInt8")
  def widen(value: SInt[8]): SInt[64] = fromRaw[SInt[64]](toRaw(value))
  @targetName("widenUInt8")
  def widen(value: UInt[8]): SInt[64] = fromRaw[SInt[64]](toRaw(value))
  @targetName("widenInt4")
  def widen(value: SInt[4]): SInt[64] = fromRaw[SInt[64]](toRaw(value))
  @targetName("widenUInt4")
  def widen(value: UInt[4]): SInt[64] = fromRaw[SInt[64]](toRaw(value))
  @targetName("widenInt32")
  def widen(value: SInt[32]): SInt[64] = fromRaw[SInt[64]](toRaw(value))

  /** The product of two widened elements, which is exact. */
  def product(left: Float64, right: Float64): Float64 = Float64(left.toDouble * right.toDouble)

  /** The exponent a factor or the running sum aligns by, no lower than `minimum`, the least normal exponent of its
    * format; [[Absent]] for zero. A product aligns by the sum of its factors' exponents, so its significand may reach 4.
    */
  def exponent(value: Float64, minimum: Int): SInt[64] = {
    val x = value.toDouble
    fromRaw[SInt[64]](
      if (x == 0.0 || !x.isFinite) Absent.toLong else math.max(Math.getExponent(x), minimum).toLong
    )
  }

  /** A product's alignment exponent scaled by the product of its scale factors. */
  def scaled(exponent: SInt[64], scale: Float64): SInt[64] = {
    val factor = scale.toDouble
    fromRaw[SInt[64]](
      if (toRaw(exponent) <= Absent.toLong || factor == 0.0 || !factor.isFinite) Absent.toLong
      else toRaw(exponent) + Math.getExponent(factor)
    )
  }

  /** `term` aligned to a block whose largest exponent is `maximum`, as an integer count of the block's least
    * significant kept bit, with `extra` bits below a 24-bit significand: its magnitude's lower bits are dropped.
    */
  def align(term: Float64, maximum: SInt[64], extra: Int): SInt[64] = {
    val x = term.toDouble
    fromRaw[SInt[64]](
      if (!x.isFinite) 0L
      else {
        IEEE754.integral(
          Math.scalb(x, 23 + extra - toRaw(maximum).toInt),
          IEEE754.Rounding.TowardZero
        ).toLong
      }
    )
  }

  /** The aligned sum of a block, `sum * 2^(maximum - 23 - extra)`, rounded to binary32 with `fraction` fraction bits,
    * or the block's `running` sum where that is infinite.
    */
  def round(
      sum: SInt[64],
      maximum: SInt[64],
      running: Float64,
      extra: Int,
      fraction: Int,
      nearest: Boolean
  ): Float32 = fromRaw[Float32](rounded(sum, maximum, running, extra, fraction, nearest, 32, 8, 23))

  /** The aligned sum of a block rounded to binary16 with `fraction` fraction bits, or an infinite `running` sum. */
  def roundHalf(
      sum: SInt[64],
      maximum: SInt[64],
      running: Float64,
      extra: Int,
      fraction: Int,
      nearest: Boolean
  ): Float16 = fromRaw[Float16](rounded(sum, maximum, running, extra, fraction, nearest, 16, 5, 10))

  private def rounded(
      sum: SInt[64],
      maximum: SInt[64],
      running: Float64,
      extra: Int,
      fraction: Int,
      nearest: Boolean,
      width: Int in (16, 32, 64),
      exponentBits: Int in (5, 8),
      fractionBits: Int in (10, 23)
  ): Long = {
    val previous = running.toDouble
    if (previous.isInfinite) IEEE754.fromDouble(previous, width)
    else encode(toRaw(sum), toRaw(maximum).toInt - 23 - extra, exponentBits, fractionBits, fraction, nearest)
  }

  /** The IEEE sum of every product and C, which is a NaN or an infinity exactly where the result is one. */
  def special(sum: Float64, product: Float64): Float64 = Float64(sum.toDouble + product.toDouble)

  /** The accumulated sum, or what `special` makes it: the canonical NaN, or an infinity. */
  def finish(sum: Float32, special: Float64): Float32 = {
    val x = special.toDouble
    if (x.isNaN) fromRaw[Float32](0x7fffffffL)
    else if (x.isInfinite) fromRaw[Float32](if (x > 0) 0x7f800000L else 0xff800000L)
    else sum
  }

  @targetName("finishHalf")
  def finish(sum: Float16, special: Float64): Float16 = {
    val x = special.toDouble
    if (x.isNaN) fromRaw[Float16](0x7fffL)
    else if (x.isInfinite) fromRaw[Float16](if (x > 0) 0x7c00L else 0xfc00L)
    else sum
  }

  /** `left * right + sum` rounded once, as DMMA computes it, which is as DFMA does. */
  def fused(left: Float64, right: Float64, sum: Float64, rounding: VMIR.ConversionRounding): Float64 =
    FloatArithmetic.fusedMultiplyAdd(left, right, sum, rounding)

  /** The low 32 bits of an integer sum. */
  def wrap(sum: SInt[64]): SInt[32] = fromRaw[SInt[32]](Bits.signExtend(toRaw(sum), 32))

  /** An integer sum clamped to the 32-bit range. */
  def saturate(sum: SInt[64]): SInt[32] =
    fromRaw[SInt[32]](math.max(Int.MinValue.toLong, math.min(Int.MaxValue.toLong, toRaw(sum))))

  /** An integer sum clamped to the 32-bit range, kept wide. */
  def clamp(sum: SInt[64]): SInt[64] =
    fromRaw[SInt[64]](math.max(Int.MinValue.toLong, math.min(Int.MaxValue.toLong, toRaw(sum))))

  /** The set bits of a 32-bit segment of a row of A combined with the same of a column of B. */
  def population(left: UInt[32], right: UInt[32], operation: VMIR.Matrix.BitOperation): SInt[64] = {
    val x = toRaw(left)
    val y = toRaw(right)
    fromRaw[SInt[64]](java.lang.Long.bitCount(operation match {
      case VMIR.Matrix.BitOperation.And => x & y
      case VMIR.Matrix.BitOperation.Xor => (x ^ y) & 0xffffffffL
    }).toLong)
  }

  private def encode(
      sum: Long,
      scale: Int,
      exponentBits: Int in (5, 8),
      fieldBits: Int in (10, 23),
      fraction: Int,
      nearest: Boolean
  ): Long =
    IEEE754.encodeSum(
      sum = sum,
      scale = scale,
      exponentBits = exponentBits,
      fieldBits = fieldBits,
      kept = fraction,
      rounding = if (nearest) IEEE754.Rounding.NearestEven else IEEE754.Rounding.TowardZero
    )
}
