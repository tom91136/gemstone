package net.kurobako.gemstone.binary

import net.kurobako.gemstone.Constraint.{LongBetween, Newtype}

/** An unsigned 16-bit value, from zero to 65535. */
private[gemstone] object Binary16 extends Newtype[LongBetween[0L, 65535L]]("binary16") {
  def fromLow(value: Type): Type = value

  def low(value: Long): Type = unsafe(value & Bits.Mask16)
}

private[gemstone] type Binary16 = Binary16.Type
