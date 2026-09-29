package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*

type Binary16 = Binary16.Type

/** An unsigned 16-bit value, from zero to 65535. */
object Binary16 extends Newtype[Long where Between[0L, 65535L]] {
  def fromLow(value: Type): Type = value

  def low(value: Long): Type = apply((value & Bits.Mask16).unsafe[Long where Between[0L, 65535L]])

  extension (value: Type) {
    def toLong: Long      = value.raw
    inline def toInt: Int = value.toLong.toInt
    def toBigInt: BigInt  = BigInt(value.toLong)
  }
}
