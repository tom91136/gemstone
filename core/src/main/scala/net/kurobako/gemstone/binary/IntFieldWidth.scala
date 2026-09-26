package net.kurobako.gemstone.binary

import scala.compiletime.{constValue, error}

/** A signed integer field width in bits, from one to thirty-one. */
private[gemstone] opaque type IntFieldWidth = Int

private[gemstone] object IntFieldWidth {
  inline def either(value: Int): Either[String, IntFieldWidth] =
    if (value >= 1 && value <= 31) Right(value) else Left(s"int field width must be between 1 and 31: $value")

  inline def unsafe(value: Int): IntFieldWidth =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Int & Singleton](inline value: A): IntFieldWidth =
    inline if constValue[A] >= 1 && constValue[A] <= 31 then value
    else error("int field width must be between 1 and 31")

  given Ordering[IntFieldWidth] = Ordering.Int

  extension (width: IntFieldWidth) {
    inline def raw: Int = width
  }
}
