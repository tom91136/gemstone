package net.kurobako.gemstone.isa.spirv.v1_6

import scala.compiletime.{constValue, error}

/** A SPIR-V opcode number, from zero to 65535. */
opaque type SPIRVOpcodeNumber = Int

object SPIRVOpcodeNumber {

  inline def either(value: Int): Either[String, SPIRVOpcodeNumber] =
    if (value >= 0 && value <= 65535) Right(value)
    else Left(s"opcode number must be between 0 and 65535: $value")

  inline def unsafe(value: Int): SPIRVOpcodeNumber =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Int & Singleton](inline value: A): SPIRVOpcodeNumber =
    inline if constValue[A] >= 0 && constValue[A] <= 65535 then value else error("opcode number out of range")

  extension (value: SPIRVOpcodeNumber) {
    inline def raw: Int = value
  }
}
