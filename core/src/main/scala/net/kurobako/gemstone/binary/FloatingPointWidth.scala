package net.kurobako.gemstone.binary

import net.kurobako.gemstone.memory.MemoryValueByteWidth

import scala.compiletime.{constValue, error}

/** A supported IEEE-754 interchange width in bits: 16, 32, or 64. */
private[gemstone] opaque type FloatingPointWidth = Int

private[gemstone] object FloatingPointWidth {
  inline def either(value: Int): Either[String, FloatingPointWidth] =
    if (value == 16 || value == 32 || value == 64) Right(value)
    else Left(s"floating point width must be 16, 32, or 64: $value")

  inline def unsafe(value: Int): FloatingPointWidth =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Int & Singleton](inline value: A): FloatingPointWidth =
    inline if constValue[A] == 16 || constValue[A] == 32 || constValue[A] == 64 then value
    else error("floating point width must be 16, 32, or 64")

  given Ordering[FloatingPointWidth] = Ordering.Int

  extension (width: FloatingPointWidth) {
    inline def raw: Int = width

    /** Every supported IEEE-754 interchange width is also a valid scalar bit width. */
    def bitWidth: BitWidth = BitWidth.unsafe(width)

    /** Every supported IEEE-754 interchange width is byte-aligned and at most eight bytes wide. */
    def scalarByteWidth: ScalarByteWidth = ScalarByteWidth.unsafe(width / 8)

    def toMemoryValueByteWidth: MemoryValueByteWidth = MemoryValueByteWidth.unsafe(width / 8)
  }
}
