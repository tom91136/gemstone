package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.CompileTime

/** A non-empty SPIR-V operand-kind name that carries no NUL character. */
opaque type SPIRVOperandKind = String

object SPIRVOperandKind {

  inline def either(value: String): Either[String, SPIRVOperandKind] =
    if (value.isEmpty) Left("operand kind must not be empty")
    else if (value.view.contains('\u0000')) Left("operand kind must not contain a NUL character")
    else Right(value)

  inline def unsafe(value: String): SPIRVOperandKind =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): SPIRVOperandKind =
    value

  extension (value: SPIRVOperandKind) {
    inline def raw: String = value
  }
}
