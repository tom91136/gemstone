package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*

/** Fixed-width integer operations, including intermediates wider than a register. */
object IntegerArithmetic {
  def unsignedMultiplyHigh(left: Long, right: Long): Long = {
    val leftLow  = left & 0xffffffffL
    val rightLow = right & 0xffffffffL
    val low      = leftLow * rightLow
    val middle1  = (left >>> 32) * rightLow + (low >>> 32)
    val middle2  = leftLow * (right >>> 32) + (middle1 & 0xffffffffL)
    (left >>> 32) * (right >>> 32) + (middle1 >>> 32) + (middle2 >>> 32)
  }

  private def signedMultiplyHigh(left: Long, right: Long): Long =
    unsignedMultiplyHigh(left, right) - (if (left < 0) right else 0L) - (if (right < 0) left else 0L)

  def multiplyHigh(left: Long, right: Long, width: Int in 1 ~ 64, signed: Boolean): Long = {
    val a    = Bits.extend(left, width, signed)
    val b    = Bits.extend(right, width, signed)
    val high = if (signed) signedMultiplyHigh(a, b) else unsignedMultiplyHigh(a, b)
    if (width == 64) high else ((a * b) >>> width) | (high << (64 - width))
  }

  def addUnsigned(left: Long, right: Long, carry: Boolean, width: Int in 1 ~ 64): (value: Long, carry: Boolean) = {
    val a        = Bits.truncate(left, width)
    val b        = Bits.truncate(right, width)
    val first    = a + b
    val result   = if (carry) first + 1L else first
    val overflow = if (width == 64)
      java.lang.Long.compareUnsigned(first, a) < 0 || (carry && result == 0L)
    else (result >>> width) != 0L
    (Bits.truncate(result, width), overflow)
  }

  /** The sum of two zero-extended dwords and a carry, with the carry out in bit 32; see [[carry32]]. */
  inline def add32(left: Long, right: Long, carry: Boolean): Long = left + right + (if (carry) 1L else 0L)

  inline def carry32(sum: Long): Boolean = sum > 0xffffffffL

  /** The difference of two zero-extended dwords and a borrow, negative exactly when it borrows; see [[borrow32]]. */
  inline def subtract32(left: Long, right: Long, borrow: Boolean): Long = left - right - (if (borrow) 1L else 0L)

  inline def borrow32(difference: Long): Boolean = difference < 0L

  def subtractUnsigned(
      left: Long,
      right: Long,
      borrow: Boolean,
      width: Int in 1 ~ 64
  ): (value: Long, borrow: Boolean) = {
    val a          = Bits.truncate(left, width)
    val b          = Bits.truncate(right, width)
    val comparison = java.lang.Long.compareUnsigned(a, b)
    (Bits.truncate(a - b - (if (borrow) 1L else 0L), width), comparison < 0 || (borrow && comparison == 0))
  }

  def addSaturated(left: Long, right: Long, width: Int in 1 ~ 64, signed: Boolean): Long =
    if (signed) {
      val a       = Bits.signExtend(left, width)
      val b       = Bits.signExtend(right, width)
      val sum     = a + b
      val minimum = -1L << (width - 1)
      val maximum = ~minimum
      if (((a ^ sum) & (b ^ sum)) < 0) { if (a < 0) minimum else maximum }
      else sum.max(minimum).min(maximum)
    } else {
      val (sum, overflow) = addUnsigned(left, right, false, width)
      if (overflow) Bits.mask(width) else sum
    }

  def subtractSaturated(left: Long, right: Long, width: Int in 1 ~ 64, signed: Boolean): Long =
    if (signed) {
      val a          = Bits.signExtend(left, width)
      val b          = Bits.signExtend(right, width)
      val difference = a - b
      val minimum    = -1L << (width - 1)
      val maximum    = ~minimum
      if (((a ^ b) & (a ^ difference)) < 0) { if (a < 0) minimum else maximum }
      else difference.max(minimum).min(maximum)
    } else {
      val (difference, borrow) = subtractUnsigned(left, right, false, width)
      if (borrow) 0L else difference
    }

  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def multiplyAddSaturated(
      left: Long,
      right: Long,
      addend: Long,
      width: Int in 1 ~ 64,
      signed: Boolean
  ): Long = {
    val a      = Bits.extend(left, width, signed)
    val b      = Bits.extend(right, width, signed)
    val c      = Bits.extend(addend, width, signed)
    val low    = a * b
    val result = low + c
    val carry  = if (java.lang.Long.compareUnsigned(result, low) < 0) 1L else 0L
    if (signed) {
      val high    = signedMultiplyHigh(a, b) + (if (c < 0) -1L else 0L) + carry
      val minimum = -1L << (width - 1)
      val maximum = ~minimum
      if (high == (result >> 63) && result >= minimum && result <= maximum) result
      else if (high < 0) minimum else maximum
    } else {
      val high = unsignedMultiplyHigh(a, b) + carry
      if (high != 0L || java.lang.Long.compareUnsigned(result, Bits.mask(width)) > 0) Bits.mask(width)
      else result
    }
  }

  /** `value` clamped to the range of a `width`-bit integer, as its low `width` bits. */
  def clampToWidth(value: BigInt, width: Int in 1 ~ 64, signed: Boolean): Long = {
    val (lowest, highest) =
      if (signed) (-(BigInt(1) << (width - 1)), (BigInt(1) << (width - 1)) - 1)
      else (BigInt(0), (BigInt(1) << width) - 1)
    Bits.truncate(value.max(lowest).min(highest).toLong, width)
  }

  /** `value`, read as signed when `valueSigned`, clamped to the range of a `width`-bit integer, as its low `width`
    * bits.
    */
  def clampToWidth(value: Long, valueSigned: Boolean, width: Int in 1 ~ 64, signed: Boolean): Long = {
    val clamped =
      if (signed) {
        val maximum = ~(-1L << (width - 1))
        if (!valueSigned && value < 0L) maximum else value.max(~maximum).min(maximum)
      } else if (valueSigned && value < 0L) 0L
      else if (java.lang.Long.compareUnsigned(value, Bits.mask(width)) > 0) Bits.mask(width)
      else value
    Bits.truncate(clamped, width)
  }
}
