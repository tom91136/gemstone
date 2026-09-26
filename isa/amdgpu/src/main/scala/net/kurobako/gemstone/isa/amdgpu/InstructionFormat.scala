package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.CompileTime

import net.kurobako.gemstone.NonEmptyString

opaque type InstructionFormat = String
object InstructionFormat {
  inline def either(value: String): Either[String, InstructionFormat] = NonEmptyString.either(value).map(_ => value)

  inline def unsafe(value: String): InstructionFormat =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): InstructionFormat =
    value

  extension (value: InstructionFormat) {
    inline def raw: String = value
  }
}
