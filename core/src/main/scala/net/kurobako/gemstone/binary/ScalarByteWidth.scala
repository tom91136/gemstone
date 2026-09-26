package net.kurobako.gemstone.binary

import net.kurobako.gemstone.NonNegativeInt

import scala.compiletime.{constValue, error}

/** A scalar byte width, from zero to eight. */
private[gemstone] opaque type ScalarByteWidth = Int

private[gemstone] object ScalarByteWidth {
  inline def either(value: Int): Either[String, ScalarByteWidth] =
    if (value >= 0 && value <= 8) Right(value) else Left(s"scalar byte width must be between 0 and 8: $value")

  inline def unsafe(value: Int): ScalarByteWidth =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Int & Singleton](inline value: A): ScalarByteWidth =
    inline if constValue[A] >= 0 && constValue[A] <= 8 then value
    else error("scalar byte width must be between 0 and 8")

  val Byte  = ScalarByteWidth(1)
  val Word  = ScalarByteWidth(2)
  val Dword = ScalarByteWidth(4)
  val Qword = ScalarByteWidth(8)

  def fromPositive(width: Int): ScalarByteWidth = unsafe(width)

  given Ordering[ScalarByteWidth] = Ordering.Int

  extension (width: ScalarByteWidth) {
    inline def raw: Int             = width
    def nonNegative: NonNegativeInt = NonNegativeInt.unsafe(width)
  }
}
