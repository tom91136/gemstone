package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.binary.Bits

/** A SPIR-V header version, shared with the rest of the ISA front-ends. */
type SPIRVVersion = net.kurobako.gemstone.isa.Version

object SPIRVVersion {
  def apply(major: Positive[Int], minor: NonNegative[Int]): SPIRVVersion =
    net.kurobako.gemstone.isa.Version(major, minor)

  def fromWord(word: Word): SPIRVVersion = {
    val raw = word.toUnsigned.raw
    SPIRVVersion(
      Positive.unsafe(Bits.unsignedIntField(raw, 16, 8)),
      NonNegative.unsafe(Bits.unsignedIntField(raw, 8, 8))
    )
  }
}

extension (version: SPIRVVersion) {
  def word: Word = Word((version.major.raw << 16) | (version.minor.raw << 8))
}
