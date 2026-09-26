package net.kurobako.gemstone.isa

import net.kurobako.gemstone.{CompileTime, NoNulString, NonEmptyString}

import scala.annotation.targetName

/** A non-empty kernel name that carries no NUL character. */
opaque type KernelName = NonEmptyString

object KernelName {
  inline def either(value: String): Either[String, KernelName] = NonEmptyString.either(value)

  inline def unsafe(value: String): KernelName = NonEmptyString.unsafe(value)

  inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): KernelName =
    NonEmptyString.unsafe(value)

  @targetName("fromNonEmptyString")
  inline def apply(inline value: NonEmptyString): KernelName = value

  extension (value: KernelName) {
    inline def raw: String               = NonEmptyString.rawOf(value)
    def toNonEmptyString: NonEmptyString = value
    def toNoNul: NoNulString             = NonEmptyString.toNoNulOf(value)
  }
}
