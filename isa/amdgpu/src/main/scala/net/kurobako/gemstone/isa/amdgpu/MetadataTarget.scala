package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.CompileTime

import net.kurobako.gemstone.NonEmptyString

opaque type MetadataTarget = String
object MetadataTarget {
  inline def either(value: String): Either[String, MetadataTarget] = NonEmptyString.either(value).map(_ => value)

  inline def unsafe(value: String): MetadataTarget =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): MetadataTarget =
    value

  extension (value: MetadataTarget) {
    inline def raw: String = value
  }
}
