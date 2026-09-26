package net.kurobako.gemstone.memory

import net.kurobako.gemstone.binary.ScalarByteWidth

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type MemoryValueByteWidth = MemoryValueByteWidth.T
object MemoryValueByteWidth extends RefinedType[Int, Interval.Closed[1, 8]] {
  given (Interval.Closed[1, 8] ==> Positive0)             = Implication()
  given (Interval.Closed[1, 8] ==> Positive)              = Implication()
  given (Interval.Closed[1, 8] ==> Interval.Closed[0, 8]) = Implication()

  val Byte  = MemoryValueByteWidth(1)
  val Word  = MemoryValueByteWidth(2)
  val Dword = MemoryValueByteWidth(4)
  val Qword = MemoryValueByteWidth(8)

  extension (width: MemoryValueByteWidth) {
    private[gemstone] def nonNegative: Int :| Positive0 = width.value

    @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
    private[gemstone] def scalarByteWidth: ScalarByteWidth = ScalarByteWidth.assume(width.value)
  }
}
