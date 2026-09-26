package net.kurobako.gemstone.isa

import net.kurobako.gemstone.{CompileTime, NoNulString, NonEmptyString}

import scala.annotation.targetName

/** A non-empty program global name that carries no NUL character. */
opaque type GlobalName = NonEmptyString

object GlobalName {
  inline def either(value: String): Either[String, GlobalName] = NonEmptyString.either(value)

  inline def unsafe(value: String): GlobalName = NonEmptyString.unsafe(value)

  inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): GlobalName =
    NonEmptyString.unsafe(value)

  @targetName("fromNonEmptyString")
  inline def apply(inline value: NonEmptyString): GlobalName = value

  extension (value: GlobalName) {
    inline def raw: String               = NonEmptyString.rawOf(value)
    def toNonEmptyString: NonEmptyString = value
    def toNoNul: NoNulString             = NonEmptyString.toNoNulOf(value)
  }
}
