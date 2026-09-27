package net.kurobako.gemstone.binary

import net.kurobako.gemstone.Constraint.NonNegative
import net.kurobako.gemstone.Constraint.{IntBetween, OneOf}

import scala.annotation.targetName

/** Unit conversions shared by the byte/bit width refinements. */
object Widths {
  extension (width: IntBetween[1, 64]) {
    def toScalarByteWidth: IntBetween[0, 8] = IntBetween.unsafe[0, 8](width / 8)

    @targetName("bitToMemoryValueByteWidth")
    def toMemoryValueByteWidth: IntBetween[1, 8] = IntBetween.unsafe[1, 8](width / 8)

    /** Only valid for IEEE-754 interchange widths (16, 32, 64). */
    def asFloatingPointWidth: OneOf[(16, 32, 64)] = OneOf.unsafe[(16, 32, 64)](width)
  }

  extension (width: OneOf[(16, 32, 64)]) {
    def bitWidth: IntBetween[1, 64] = IntBetween.unsafe[1, 64](width)

    @targetName("floatScalarByteWidth")
    def scalarByteWidth: IntBetween[0, 8] = IntBetween.unsafe[0, 8](width / 8)

    @targetName("floatToMemoryValueByteWidth")
    def toMemoryValueByteWidth: IntBetween[1, 8] = IntBetween.unsafe[1, 8](width / 8)
  }

  extension (width: IntBetween[0, 8]) {
    @targetName("scalarNonNegative") def nonNegative: NonNegative[Int] = NonNegative.unsafe(width)
  }

  extension (width: IntBetween[1, 8]) {
    @targetName("memoryNonNegative") def nonNegative: NonNegative[Int] = NonNegative.unsafe(width)

    @targetName("memoryScalarByteWidth")
    def scalarByteWidth: IntBetween[0, 8] = IntBetween.unsafe[0, 8](width)
  }
}
