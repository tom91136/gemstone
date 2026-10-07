package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*
import scala.annotation.targetName

/** Unit conversions shared by the byte/bit width refinements. */
object Widths {
  type ScalarBitWidth = Int where (Between[8, 64] & MultipleOf[8])

  extension (width: ScalarBitWidth) {
    def toScalarByteWidth: Int in 1 ~ 8 = width.raw match {
      case 8  => 1
      case 16 => 2
      case 24 => 3
      case 32 => 4
      case 40 => 5
      case 48 => 6
      case 56 => 7
      case _  => 8
    }
  }

  extension (width: Int in (8, 16, 32, 64)) {
    @targetName("bitToMemoryValueByteWidth")
    def toMemoryValueByteWidth: Int in (1, 2, 4, 8) = width.raw match {
      case 8  => 1
      case 16 => 2
      case 32 => 4
      case _  => 8
    }
  }

  extension (width: Int in (16, 32, 64)) {
    @targetName("floatToByteWidth")
    def toFloatByteWidth: Int in (2, 4, 8) = width.raw match {
      case 16 => 2
      case 32 => 4
      case _  => 8
    }
  }

  extension (width: Int in 1 ~ 8) {
    def toBitWidth: ScalarBitWidth = width.raw match {
      case 1 => 8
      case 2 => 16
      case 3 => 24
      case 4 => 32
      case 5 => 40
      case 6 => 48
      case 7 => 56
      case _ => 64
    }
  }
}
