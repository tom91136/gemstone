package net.kurobako.gemstone.binary

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[gemstone] type ScalarByteWidth = ScalarByteWidth.T
private[gemstone] object ScalarByteWidth extends RefinedType[Int, Interval.Closed[0, 8]] {
  given (Interval.Closed[0, 8] ==> Positive0) = Implication()

  val Byte  = ScalarByteWidth(1)
  val Word  = ScalarByteWidth(2)
  val Dword = ScalarByteWidth(4)
  val Qword = ScalarByteWidth(8)

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  def fromPositive(width: Int :| Interval.Closed[1, 8]): ScalarByteWidth = ScalarByteWidth.assume(width)

  extension (width: ScalarByteWidth) {
    def nonNegative: Int :| Positive0 = width.value
  }
}
