package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.{Binary32, Bits}

/** A SPIR-V 32-bit word. */
opaque type Word = Int

object Word {
  inline def apply(inline value: Int): Word = value

  def unsigned(value: Long): Either[String, Word] =
    if (value < 0L || value > Bits.Mask32) Left(s"word is outside unsigned 32-bit range: $value")
    else Right(Word(value.toInt))

  given Ordering[Word] = Ordering.Int

  extension (word: Word) {
    inline def raw: Int      = word
    def toUnsigned: Binary32 = Bits.unsigned32(word)
  }
}
