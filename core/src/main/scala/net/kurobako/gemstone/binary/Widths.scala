package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.annotation.targetName

/** Unit conversions shared by the byte/bit width refinements. */
object Widths {
  extension (width: Int where Between[1, 64]) {
    def toScalarByteWidth: Int where Between[0, 8] = (width.raw / 8).unsafe[Int where Between[0, 8]]

  }

  extension (width: OneOf[(8, 16, 32, 64)]) {
    @targetName("bitToMemoryValueByteWidth")
    def toMemoryValueByteWidth: Int where Between[1, 8] = (width.raw / 8).unsafe[Int where Between[1, 8]]
  }

  extension (width: OneOf[(16, 32, 64)]) {
    def bitWidth: Int where Between[1, 64] = width

    @targetName("floatScalarByteWidth")
    def scalarByteWidth: Int where Between[0, 8] = (width.raw / 8).unsafe[Int where Between[0, 8]]

  }

  extension (width: Int where Between[0, 8]) {
    @targetName("scalarNonNegative")
    def nonNegative: Int >= 0 = width
  }

  extension (width: Int where Between[1, 8]) {
    @targetName("memoryNonNegative")
    def nonNegative: Int >= 0 = width

    @targetName("memoryScalarByteWidth")
    def scalarByteWidth: Int where Between[0, 8] = width
  }
}
