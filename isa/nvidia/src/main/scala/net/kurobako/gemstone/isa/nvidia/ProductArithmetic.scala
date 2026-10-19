package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import scala.annotation.targetName

/** The element arithmetic warpgroup and tcgen05 products add to `mma`'s: negated multiplicands, and a disabled or
  * scaled accumulator.
  */
private[isa] object ProductArithmetic {
  @targetName("negateHalf")
  def negate(value: Float16): Float16 = fromRaw[Float16](toRaw(value) ^ 0x8000L)
  @targetName("negateBFloat16")
  def negate(value: BFloat16): BFloat16 = fromRaw[BFloat16](toRaw(value) ^ 0x8000L)
  @targetName("negateTensorFloat32")
  def negate(value: TensorFloat32): TensorFloat32 =
    fromRaw[TensorFloat32](toRaw(value) ^ 0x40000L)
  @targetName("negateE4M3")
  def negate(value: Float8E4M3): Float8E4M3 = fromRaw[Float8E4M3](toRaw(value) ^ 0x80L)
  @targetName("negateE5M2")
  def negate(value: Float8E5M2): Float8E5M2 = fromRaw[Float8E5M2](toRaw(value) ^ 0x80L)
  @targetName("negateE3M2")
  def negate(value: Float6E3M2): Float6E3M2 = fromRaw[Float6E3M2](toRaw(value) ^ 0x20L)
  @targetName("negateE2M3")
  def negate(value: Float6E2M3): Float6E2M3 = fromRaw[Float6E2M3](toRaw(value) ^ 0x20L)
  @targetName("negateE2M1")
  def negate(value: Float4E2M1): Float4E2M1 = fromRaw[Float4E2M1](toRaw(value) ^ 0x8L)

  /** D's element as the product adds it, or a positive zero when `scale` disables it. */
  @targetName("addendHalf")
  def addend(scale: Bool, value: Float16): Float16 = if (scale) value else fromRaw[Float16](0L)
  @targetName("addendSingle")
  def addend(scale: Bool, value: Float32): Float32 = if (scale) value else fromRaw[Float32](0L)
  @targetName("addendInteger")
  def addend(scale: Bool, value: SInt[32]): SInt[32] = if (scale) value else fromRaw[SInt[32]](0L)

  /** `value` times `2^-shift`, rounded to nearest. */
  @targetName("scaleHalf")
  def scale(value: Float16, shift: Int): Float16 = fromRaw[Float16](
    IEEE754.multiply(
      toRaw(value),
      IEEE754.fromDouble(math.pow(2.0, -shift.toDouble), 16),
      16,
      IEEE754.NaNOperand.Right
    )
  )
  @targetName("scaleSingle")
  def scale(value: Float32, shift: Int): Float32 = fromRaw[Float32](
    IEEE754.multiply(
      toRaw(value),
      IEEE754.fromDouble(math.pow(2.0, -shift.toDouble), 32),
      32,
      IEEE754.NaNOperand.Right
    )
  )
}
