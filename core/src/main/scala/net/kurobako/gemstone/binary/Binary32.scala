package net.kurobako.gemstone.binary

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[gemstone] type Binary32 = Binary32.T
private[gemstone] object Binary32 extends RefinedType[Long, Interval.Closed[0L, 4294967295L]] {
  def fromLow(value: Long :| Interval.Closed[0L, 4294967295L]): Binary32 = Binary32(value)

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  def low(value: Long): Binary32 = this.assume(value & Bits.Mask32)
}
