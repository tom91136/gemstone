package net.kurobako.gemstone.testing

import net.kurobako.gemstone.binary.*

/** The encodings of floating-point values as the bits a register holds, zero-extended. */
object bits {
  def half(value: Float): Long = IEEE754.floatToBinary16(value).toLong

  def bfloat16(value: Float): Long = IEEE754.doubleToBfloat16(value.toDouble).toLong

  def single(value: Float): Long = IEEE754.floatToBinary32(value).toLong

  def double(value: Double): Long = IEEE754.doubleToBinary64(value)
}
