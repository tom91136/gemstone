package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.{Bits, IntFieldWidth}

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class SPIRVVersion(
    major: Int :| Interval.Closed[0, 255],
    minor: Int :| Interval.Closed[0, 255]
) {
  def word: Word = Word((major << 16) | (minor << 8))
}

object SPIRVVersion {
  def fromWord(word: Word): SPIRVVersion =
    SPIRVVersion(
      Bits.unsignedIntField(word.toUnsigned, 16, IntFieldWidth(8)).refineUnsafe[Interval.Closed[0, 255]],
      Bits.unsignedIntField(word.toUnsigned, 8, IntFieldWidth(8)).refineUnsafe[Interval.Closed[0, 255]]
    )
}
