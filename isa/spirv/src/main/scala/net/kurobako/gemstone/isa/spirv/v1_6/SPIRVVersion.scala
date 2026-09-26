package net.kurobako.gemstone.isa.spirv.v1_6

import scala.compiletime.{constValue, error}

import net.kurobako.gemstone.binary.{Bits, IntFieldWidth}

/** A SPIR-V version component, from zero to 255. */
opaque type VersionByte = Int

object VersionByte {

  inline def either(value: Int): Either[String, VersionByte] =
    if (value >= 0 && value <= 255) Right(value)
    else Left(s"version component must be between 0 and 255: $value")

  inline def unsafe(value: Int): VersionByte =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Int & Singleton](inline value: A): VersionByte =
    inline if constValue[A] >= 0 && constValue[A] <= 255 then value else error("version byte out of range")

  extension (value: VersionByte) {
    inline def raw: Int = value

    def <(other: VersionByte): Boolean  = value < other
    def <=(other: VersionByte): Boolean = value <= other
    def >(other: VersionByte): Boolean  = value > other
    def >=(other: VersionByte): Boolean = value >= other
  }
}

final case class SPIRVVersion(
    major: VersionByte,
    minor: VersionByte
) {
  def word: Word = Word((major.raw << 16) | (minor.raw << 8))
}

object SPIRVVersion {
  def fromWord(word: Word): SPIRVVersion =
    SPIRVVersion(
      VersionByte.unsafe(Bits.unsignedIntField(word.toUnsigned.raw, 16, IntFieldWidth(8))),
      VersionByte.unsafe(Bits.unsignedIntField(word.toUnsigned.raw, 8, IntFieldWidth(8)))
    )
}
