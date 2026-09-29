package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*

type Binary32 = Binary32.Type

/** An unsigned 32-bit value, from zero to 4294967295. */
object Binary32 extends Newtype[Long where Between[0L, 4294967295L]] {
  def fromLow(value: Type): Type = value

  def low(value: Long): Type = apply((value & Bits.Mask32).unsafe[Long where Between[0L, 4294967295L]])

  extension (value: Type) {
    def toLong: Long = value.raw

    /** Interpret these bits as a signed 32-bit integer. */
    inline def toInt: Int = value.toLong.toInt
    def toBigInt: BigInt  = BigInt(value.toLong)
  }
}
