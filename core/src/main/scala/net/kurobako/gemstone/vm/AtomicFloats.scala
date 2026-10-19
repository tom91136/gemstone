package net.kurobako.gemstone.vm

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.semantics.prelude.*

/** The bfloat16 and packed 16-bit read-modify-write combines of [[AtomicOperation]]. */
object AtomicFloats {
  def addBFloat16(old: BFloat16, operand: BFloat16): BFloat16 =
    BFloat16(UInt[16](bfloat16Sum(old.bits.raw, operand.bits.raw)))

  def addPackedHalf(old: UInt[32], operand: UInt[32]): UInt[32] =
    perHalf(old, operand)(IEEE754.add(_, _, 16, IEEE754.NaNOperand.Left))

  def addPackedBFloat16(old: UInt[32], operand: UInt[32]): UInt[32] = perHalf(old, operand)(bfloat16Sum(_, _))

  def minBFloat16(old: BFloat16, operand: BFloat16): BFloat16 =
    BFloat16(UInt[16](bfloat16Minimum(old.bits.raw, operand.bits.raw)))
  def maxBFloat16(old: BFloat16, operand: BFloat16): BFloat16 =
    BFloat16(UInt[16](bfloat16Maximum(old.bits.raw, operand.bits.raw)))

  def minPackedHalf(old: UInt[32], operand: UInt[32]): UInt[32] = perHalf(old, operand)(IEEE754.minNum(_, _, 16))
  def maxPackedHalf(old: UInt[32], operand: UInt[32]): UInt[32] = perHalf(old, operand)(IEEE754.maxNum(_, _, 16))

  def minPackedBFloat16(old: UInt[32], operand: UInt[32]): UInt[32] =
    perHalf(old, operand)(bfloat16Minimum(_, _))
  def maxPackedBFloat16(old: UInt[32], operand: UInt[32]): UInt[32] =
    perHalf(old, operand)(bfloat16Maximum(_, _))

  private inline def perHalf(old: UInt[32], operand: UInt[32])(inline combine: (Long, Long) => Long): UInt[32] =
    UInt[32](
      combine(old.raw & 0xffffL, operand.raw & 0xffffL) | (combine(old.raw >>> 16, operand.raw >>> 16) << 16)
    )

  // a bfloat16 is the upper half of the binary32 of the same value
  private def bfloat16Minimum(left: Long, right: Long): Long = IEEE754.minNum(left << 16, right << 16, 32) >>> 16
  private def bfloat16Maximum(left: Long, right: Long): Long = IEEE754.maxNum(left << 16, right << 16, 32) >>> 16

  // a bfloat16 sum is exact in binary64 or too far apart to round differently there
  private def bfloat16Sum(left: Long, right: Long): Long = {
    val a = Minifloat.BFloat16.toDouble(left)
    val b = Minifloat.BFloat16.toDouble(right)
    if (a.isNaN) left | 0x40L
    else if (b.isNaN) right | 0x40L
    else Minifloat.BFloat16.fromDouble(a + b)
  }
}
