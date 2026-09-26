package net.kurobako.gemstone.isa

import net.kurobako.gemstone.{CompileTime, NoNulString, NonEmptyString}

import scala.annotation.targetName

/** A non-empty linkage name that carries no NUL character. */
opaque type LinkageName = NonEmptyString

object LinkageName {
  inline def either(value: String): Either[String, LinkageName] = NonEmptyString.either(value)

  inline def unsafe(value: String): LinkageName = NonEmptyString.unsafe(value)

  inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): LinkageName =
    NonEmptyString.unsafe(value)

  @targetName("fromNonEmptyString")
  inline def apply(inline value: NonEmptyString): LinkageName = value

  extension (value: LinkageName) {
    inline def raw: String               = NonEmptyString.rawOf(value)
    def toNonEmptyString: NonEmptyString = value
    def toNoNul: NoNulString             = NonEmptyString.toNoNulOf(value)
  }
}
