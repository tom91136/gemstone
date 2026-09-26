package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.CompileTime

opaque type BenchmarkName = String

object BenchmarkName {
  inline def either(value: String): Either[String, BenchmarkName] =
    if (value.isEmpty) Left("benchmark name must not be empty")
    else if (value.view.contains('\u0000')) Left("benchmark name must not contain a NUL character")
    else Right(value)

  inline def unsafe(value: String): BenchmarkName =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): BenchmarkName =
    value

  extension (value: BenchmarkName) {
    inline def raw: String = value
  }
}
