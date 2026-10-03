package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.annotation.targetName

/** Unit conversions shared by the byte/bit width refinements. */
object Widths {
  type ScalarBitWidth = Int where (Between[8, 64] & MultipleOf[8])

  extension (width: ScalarBitWidth) {
    def toScalarByteWidth: Int in 1 ~ 8 = (width.raw / 8).unsafe[Int in 1 ~ 8]

  }

  extension (width: Int in (8, 16, 32, 64)) {
    @targetName("bitToMemoryValueByteWidth")
    def toMemoryValueByteWidth: Int in (1, 2, 4, 8) = (width.raw / 8).unsafe[Int in (1, 2, 4, 8)]
  }

  extension (width: Int in (16, 32, 64)) {
    def bitWidth: Int in (16, 32, 64) = width

    @targetName("floatScalarByteWidth")
    def scalarByteWidth: Int in (2, 4, 8) = (width.raw / 8).unsafe[Int in (2, 4, 8)]

  }

  extension (width: Int in 0 ~ 8) {
    @targetName("scalarNonNegative")
    def nonNegative: Int >= 0 = width
  }

  extension (width: Int in (1, 2, 4, 8)) {
    def toMemoryValueBitWidth: Int in (8, 16, 32, 64) = (width.raw * 8).unsafe[Int in (8, 16, 32, 64)]
  }

  extension (width: Int in 1 ~ 8) {
    def toBitWidth: Int where (Between[8, 64] & MultipleOf[8]) = (width.raw * 8).unsafe[Int where
      (Between[8, 64] & MultipleOf[8])]
    @targetName("memoryNonNegative")
    def nonNegative: Int >= 0 = width

    @targetName("memoryScalarByteWidth")
    def scalarByteWidth: Int in 0 ~ 8 = width
  }
}
