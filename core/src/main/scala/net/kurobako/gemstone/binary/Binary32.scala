package net.kurobako.gemstone.binary

import net.kurobako.gemstone.Constraint.{LongBetween, Newtype}

/** An unsigned 32-bit value, from zero to 4294967295. */
private[gemstone] object Binary32 extends Newtype[LongBetween[0L, 4294967295L]]("binary32") {
  def fromLow(value: Type): Type = value

  def low(value: Long): Type = unsafe(value & Bits.Mask32)
}

private[gemstone] type Binary32 = Binary32.Type
