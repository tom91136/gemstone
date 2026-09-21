package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.Bits
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type Word = Word.T
object Word extends RefinedType[Int, Pure] {
  def unsigned(value: Long): Either[String, Word] =
    if (value < 0L || value > Bits.Mask32) Left(s"word is outside unsigned 32-bit range: $value")
    else Right(Word(value.toInt))

  extension (word: Word) {
    def bits: Int                                            = word.value
    def toUnsigned: Long :| Interval.Closed[0L, 4294967295L] = Bits.unsigned32(word.value)
  }
}
