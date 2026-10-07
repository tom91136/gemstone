package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*

type Binary16 = Binary16.Type

/** An unsigned 16-bit value, from zero to 65535. */
object Binary16 extends Newtype[Long in 0L ~ 65535L] {
  inline val Mask = 0xffffL

  def low(value: Long): Type = apply((value & Mask).unsafe[Long in 0L ~ 65535L])

  extension (value: Type) {
    def toLong: Long      = value.raw
    inline def toInt: Int = value.toLong.toInt
  }
}
