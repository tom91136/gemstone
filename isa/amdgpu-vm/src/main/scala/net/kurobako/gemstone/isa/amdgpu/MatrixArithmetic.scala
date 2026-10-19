package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import scala.annotation.targetName

/** The element arithmetic of the matrix instructions. Narrow floats widen to binary32 exactly, and each product joins
  * the sum in a fused multiply-add that rounds to nearest even once, in K order; the matrix units take denormals in and
  * out as they are, except binary32 A and B, which MODE may flush. Integer products accumulate exactly, wrapping or
  * saturating to 32 bits at the end.
  */
private[isa] object MatrixArithmetic {
  private inline def bits[T](value: T): Long = toRaw[T](value)

  private inline val SingleInputDenormals = 1L << 4
  private inline val Fp16Overflow         = 1L << 23

  private def infinite(value: Float32): Boolean = (bits(value) & 0x7fffffffL) == 0x7f800000L

  /** A binary32 element of A or B, its denormal flushed to a signed zero unless MODE.FP_DENORM allows binary32 input
    * denormals.
    */
  def input(value: Float32, mode: UInt[32]): Float32 =
    if ((bits(mode) & SingleInputDenormals) != 0 || (bits(value) & 0x7f800000L) != 0) value
    else fromRaw[Float32](bits(value) & 0x80000000L)

  /** `result` saturated to the largest finite magnitude where it overflows under MODE.FP16_OVFL; a result of infinite
    * operands keeps its infinity.
    */
  private def saturated(result: Float32, infiniteOperand: Boolean, mode: UInt[32]): Float32 =
    if ((bits(mode) & Fp16Overflow) == 0 || !infinite(result) || infiniteOperand) result
    else fromRaw[Float32]((bits(result) & 0x80000000L) | 0x7f7fffffL)

  /** `sum` plus the product of `a` and `b`, rounded once to binary32, on the matrix core, which clamps by
    * MODE.FP16_OVFL as other instructions clamp by CLAMP: each overflowing step saturates.
    */
  def fma(a: Float32, b: Float32, sum: Float32, mode: UInt[32]): Float32 =
    saturated(Floats.fma(a, b, sum), infinite(a) || infinite(b) || infinite(sum), mode)

  /** [[accumulateScaled]] on the matrix core, saturating as [[fma]] does. */
  def scaledFma(a: Float64, b: Float64, sum: Float32, mode: UInt[32]): Float32 = {
    def wide(value: Float64): Boolean = value.toDouble.isInfinite
    saturated(accumulateScaled(a, b, sum), wide(a) || wide(b) || infinite(sum), mode)
  }

  @targetName("bf16")
  def widen(value: BFloat16): Float32 = Float32(value.toDouble)
  @targetName("fp8")
  def widen(value: Float8E4M3): Float32 = Float32(value.toDouble)
  @targetName("bf8")
  def widen(value: Float8E5M2): Float32 = Float32(value.toDouble)
  @targetName("fp8Fnuz")
  def widen(value: Float8E4M3FNUZ): Float32 = Float32(value.toDouble)
  @targetName("bf8Fnuz")
  def widen(value: Float8E5M2FNUZ): Float32 = Float32(value.toDouble)
  @targetName("fp6")
  def widen(value: Float6E2M3): Float32 = Float32(value.toDouble)
  @targetName("bf6")
  def widen(value: Float6E3M2): Float32 = Float32(value.toDouble)
  @targetName("fp4")
  def widen(value: Float4E2M1): Float32 = Float32(value.toDouble)
  @targetName("tf32")
  def widen(value: TensorFloat32): Float32 = Float32(value.toDouble)

  /** XF32 keeps 10 bits of a binary32 significand, rounded to nearest even. */
  def roundTf32(value: Float32): TensorFloat32 =
    fromRaw[TensorFloat32](Minifloat.TensorFloat32.fromDouble(value.toDouble))

  /** A binary32 result as bfloat16, rounded to nearest even. */
  def roundBf16(value: Float32): BFloat16 =
    fromRaw[BFloat16](Minifloat.BFloat16.fromDouble(value.toDouble))

  /** A widened element scaled by two to the power of an E8M0 scale's exponent, exactly; the NaN scale gives NaN. */
  def scaled(value: Float32, scale: ScaleE8M0): Float64 = Float64(value.toDouble * scale.toDouble)

  /** `sum` plus the product of two scaled elements, rounded once to binary32, or the quiet NaN where it is NaN. The
    * product of elements of at most 4 significant bits under scales is exact in binary64; the binary64 sum, made odd
    * where it is inexact, then rounds to binary32 as the exact sum would.
    */
  def accumulateScaled(a: Float64, b: Float64, sum: Float32): Float32 = {
    val total = IEEE754.oddSum(a.toDouble * b.toDouble, sum.toDouble)
    // the JVM leaves which operand's payload a NaN keeps open
    Float32(if (total.isNaN) java.lang.Double.NaN else total)
  }

  /** An integer product added to an exact sum. */
  def multiplyAdd(a: SInt[64], b: SInt[64], sum: SInt[64]): SInt[64] =
    fromRaw[SInt[64]](bits(sum) + bits(a) * bits(b))

  // the element's bits may arrive with or without their sign extended
  def signed8(value: SInt[8]): SInt[64]      = fromRaw[SInt[64]](bits(value).toByte.toLong)
  def unsigned8(value: UInt[8]): SInt[64]    = fromRaw[SInt[64]](bits(value) & 0xffL)
  def signed4(value: SInt[4]): SInt[64]      = fromRaw[SInt[64]](((bits(value) & 15L) ^ 8L) - 8L)
  def unsigned4(value: UInt[4]): SInt[64]    = fromRaw[SInt[64]](bits(value) & 15L)
  def accumulator(value: SInt[32]): SInt[64] = fromRaw[SInt[64]](bits(value).toInt.toLong)

  /** An exact sum as a 32-bit result, saturated with `clamp` and wrapped without. */
  def result(sum: SInt[64], clamp: Boolean): SInt[32] = fromRaw[SInt[32]](
    if (clamp) math.max(Int.MinValue.toLong, math.min(Int.MaxValue.toLong, bits(sum))) else bits(sum).toInt.toLong
  )

  /** [[result]] on the matrix core, which saturates under MODE.FP16_OVFL. */
  def coreResult(sum: SInt[64], mode: UInt[32]): SInt[32] = result(sum, (bits(mode) & Fp16Overflow) != 0)
}
