package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.CompileTime

import net.kurobako.gemstone.NonEmptyString

opaque type OperandKind = String
object OperandKind {
  inline def either(value: String): Either[String, OperandKind] = NonEmptyString.either(value).map(_ => value)

  inline def unsafe(value: String): OperandKind =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): OperandKind =
    value

  extension (value: OperandKind) {
    inline def raw: String = value
  }
}
