package net.kurobako.gemstone.binary

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[gemstone] type Binary16 = Binary16.T
private[gemstone] object Binary16 extends RefinedType[Long, Interval.Closed[0L, 65535L]] {
  def fromLow(value: Long :| Interval.Closed[0L, 65535L]): Binary16 = Binary16(value)

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  def low(value: Long): Binary16 = this.assume(value & Bits.Mask16)
}
