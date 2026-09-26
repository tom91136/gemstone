package net.kurobako.gemstone.binary

import scala.compiletime.{constValue, error}

/** A byte-aligned bit width, from zero to sixty-four in multiples of eight. */
private[gemstone] opaque type ByteAlignedBitWidth = Int

private[gemstone] object ByteAlignedBitWidth {
  inline def either(value: Int): Either[String, ByteAlignedBitWidth] =
    if (value >= 0 && value <= 64 && value % 8 == 0) Right(value)
    else Left(s"byte-aligned bit width must be between 0 and 64 in multiples of 8: $value")

  inline def unsafe(value: Int): ByteAlignedBitWidth =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Int & Singleton](inline value: A): ByteAlignedBitWidth =
    inline if constValue[A] >= 0 && constValue[A] <= 64 && constValue[A] % 8 == 0 then value
    else error("byte-aligned bit width must be between 0 and 64 in multiples of 8")

  given Ordering[ByteAlignedBitWidth] = Ordering.Int

  extension (width: ByteAlignedBitWidth) {
    inline def raw: Int = width
  }
}
