package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*

/** A SPIR-V 32-bit word. */
type Word = Word.Type

object Word extends Newtype[Int] {

  def unsigned(value: Long): Either[NonEmpty[String], Word] =
    if (value < 0L || value > Bits.Mask32) Left(s"word is outside unsigned 32-bit range: $value")
    else Right(Word(value.toInt))

  extension (word: Word) {
    def toUnsigned: Binary32 = Bits.unsigned32(word.raw)
  }
}
