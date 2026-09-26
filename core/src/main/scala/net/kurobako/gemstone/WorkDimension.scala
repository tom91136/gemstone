package net.kurobako.gemstone

import scala.compiletime.{constValue, error}

/** A work dimension count, from one to three. */
opaque type WorkDimension = Int

object WorkDimension {
  inline def either(value: Int): Either[String, WorkDimension] =
    if (value >= 1 && value <= 3) Right(value) else Left(s"work dimension must be between 1 and 3: $value")

  inline def unsafe(value: Int): WorkDimension =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Int & Singleton](inline value: A): WorkDimension =
    inline if constValue[A] >= 1 && constValue[A] <= 3 then value else error("work dimension must be between 1 and 3")

  val One: WorkDimension   = 1
  val Two: WorkDimension   = 2
  val Three: WorkDimension = 3

  given Ordering[WorkDimension] = Ordering.Int

  extension (value: WorkDimension) {
    inline def raw: Int         = value
    def toPositive: PositiveInt = PositiveInt.unsafe(value)
  }
}
