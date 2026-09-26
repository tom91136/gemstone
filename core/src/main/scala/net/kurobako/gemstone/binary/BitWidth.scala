package net.kurobako.gemstone.binary

import net.kurobako.gemstone.memory.MemoryValueByteWidth

import scala.compiletime.{constValue, error}

/** A bit width, from one to sixty-four. */
private[gemstone] opaque type BitWidth = Int

private[gemstone] object BitWidth {
  inline def either(value: Int): Either[String, BitWidth] =
    if (value >= 1 && value <= 64) Right(value) else Left(s"bit width must be between 1 and 64: $value")

  inline def unsafe(value: Int): BitWidth =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Int & Singleton](inline value: A): BitWidth =
    inline if constValue[A] >= 1 && constValue[A] <= 64 then value else error("bit width must be between 1 and 64")

  given Ordering[BitWidth] = Ordering.Int

  extension (width: BitWidth) {
    inline def raw: Int = width

    def toScalarByteWidth: ScalarByteWidth = ScalarByteWidth.unsafe(width / 8)

    def toMemoryValueByteWidth: MemoryValueByteWidth = MemoryValueByteWidth.unsafe(width / 8)

    /** Only valid for IEEE-754 interchange widths (16, 32, 64). */
    def asFloatingPointWidth: FloatingPointWidth = FloatingPointWidth.unsafe(width)
  }
}
