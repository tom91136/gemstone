package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.CompileTime

/** A non-empty SPIR-V opcode name that carries no NUL character. */
opaque type SPIRVOpcode = String

object SPIRVOpcode {

  inline def either(value: String): Either[String, SPIRVOpcode] =
    if (value.isEmpty) Left("opcode name must not be empty")
    else if (value.view.contains('\u0000')) Left("opcode name must not contain a NUL character")
    else Right(value)

  inline def unsafe(value: String): SPIRVOpcode =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): SPIRVOpcode =
    value

  extension (value: SPIRVOpcode) {
    inline def raw: String = value
  }
}
