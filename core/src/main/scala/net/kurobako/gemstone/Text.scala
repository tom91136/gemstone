package net.kurobako.gemstone

/** A `String` guaranteed not to contain a NUL character. May be empty. */
opaque type NoNulString = String

object NoNulString {
  inline def either(value: String): Either[String, NoNulString] =
    if (value.view.contains('\u0000')) Left("value must not contain a NUL character") else Right(value)

  inline def unsafe(value: String): NoNulString =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NoNul[A]): NoNulString =
    value

  extension (value: NoNulString) {
    inline def raw: String = value
  }
}

/** A non-empty `String` guaranteed not to contain a NUL character. */
opaque type NonEmptyString = String

object NonEmptyString {
  inline def either(value: String): Either[String, NonEmptyString] =
    if (value.isEmpty) Left("value must not be empty")
    else if (value.view.contains('\u0000')) Left("value must not contain a NUL character")
    else Right(value)

  inline def unsafe(value: String): NonEmptyString =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: String & Singleton](inline value: A)(using
      CompileTime.NonEmptyNoNul[A]
  ): NonEmptyString = value

  /** The underlying `String` of a [[NonEmptyString]]. Used by newtypes that wrap it. */
  inline def rawOf(value: NonEmptyString): String = value

  /** The NUL-free view of a [[NonEmptyString]]. Used by newtypes that wrap it. */
  inline def toNoNulOf(value: NonEmptyString): NoNulString = value.toNoNul

  extension (value: NonEmptyString) {
    inline def raw: String   = value
    def toNoNul: NoNulString = NoNulString.unsafe(value)
  }
}
