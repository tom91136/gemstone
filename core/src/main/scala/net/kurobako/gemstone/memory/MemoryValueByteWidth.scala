package net.kurobako.gemstone.memory

import scala.compiletime.{constValue, error}

import net.kurobako.gemstone.NonNegativeInt
import net.kurobako.gemstone.binary.ScalarByteWidth

/** A memory value width in bytes, from one to eight. */
opaque type MemoryValueByteWidth = Int

object MemoryValueByteWidth {
  inline def either(value: Int): Either[String, MemoryValueByteWidth] =
    if (value >= 1 && value <= 8) Right(value) else Left(s"memory value width must be between 1 and 8: $value")

  inline def unsafe(value: Int): MemoryValueByteWidth =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Int & Singleton](inline value: A): MemoryValueByteWidth =
    inline if constValue[A] >= 1 && constValue[A] <= 8 then value
    else error("memory value width must be between 1 and 8")

  given Ordering[MemoryValueByteWidth] = Ordering.Int

  val Byte  = MemoryValueByteWidth(1)
  val Word  = MemoryValueByteWidth(2)
  val Dword = MemoryValueByteWidth(4)
  val Qword = MemoryValueByteWidth(8)

  extension (width: MemoryValueByteWidth) {
    inline def raw: Int = width

    private[gemstone] def nonNegative: NonNegativeInt = NonNegativeInt.unsafe(width)

    private[gemstone] def scalarByteWidth: ScalarByteWidth = ScalarByteWidth.unsafe(width)
  }
}
