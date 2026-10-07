package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*

/** A SPIR-V 32-bit word. */
type Word = Word.Type

object Word extends Newtype[Int] {
  extension (word: Word) {
    def toUnsigned: Binary32 = Bits.unsigned32(word.raw)
  }
}
