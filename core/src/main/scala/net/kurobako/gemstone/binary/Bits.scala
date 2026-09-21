package net.kurobako.gemstone.binary

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[gemstone] object Bits {
  inline val Mask8  = 0xffL
  inline val Mask16 = 0xffffL
  inline val Mask32 = 0xffffffffL

  inline def mask(width: BitWidth): Long =
    if (width.value == 64) -1L else (1L << width.value) - 1L

  inline def truncate(value: Long, width: BitWidth): Long = value & mask(width)

  inline def unsignedField(value: Long, offset: Int, width: BitWidth): Long =
    truncate(value >>> offset, width)

  inline def signedField(value: Long, offset: Int, width: BitWidth): Long =
    signExtend(unsignedField(value, offset, width), width)

  inline def unsignedIntField(value: Long, offset: Int, width: IntFieldWidth): Int =
    ((value >>> offset) & ((1L << width.value) - 1L)).toInt

  inline def signedIntField(value: Long, offset: Int, width: IntFieldWidth): Int = {
    val field   = (value >>> offset) & ((1L << width.value) - 1L)
    val signBit = 1L << (width.value - 1)
    ((field ^ signBit) - signBit).toInt
  }

  inline def isSet(value: Long, index: Int): Boolean = ((value >>> index) & 1L) != 0L

  /** Count set bits in the low `width` bits, ignoring any upper register bits. */
  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  inline def bitCount(value: Long, width: BitWidth): Int :| Interval.Closed[0, 64] =
    java.lang.Long.bitCount(truncate(value, width)).assume[Interval.Closed[0, 64]]

  /** Reverse only the low `width` bits and return a zero-extended result. */
  inline def reverseBits(value: Long, width: BitWidth): Long =
    java.lang.Long.reverse(value) >>> (64 - width.value)

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  inline def unsigned32(value: Int)
      : Long :| Interval.Closed[0L, 4294967295L] = (value.toLong & Mask32).assume[Interval.Closed[0L, 4294967295L]]

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  inline def low8(value: Long)
      : Long :| Interval.Closed[0L, 255L] = (value & Mask8).assume[Interval.Closed[0L, 255L]]

  inline def low8Int(value: Long): Int = (value & Mask8).toInt

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  inline def low16(value: Long)
      : Long :| Interval.Closed[0L, 65535L] = (value & Mask16).assume[Interval.Closed[0L, 65535L]]

  inline def low16Int(value: Long): Int = (value & Mask16).toInt

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  inline def low32(value: Long)
      : Long :| Interval.Closed[0L, 4294967295L] = (value & Mask32).assume[Interval.Closed[0L, 4294967295L]]

  /** Interpret the low 32 bits as a signed two's-complement value. */
  inline def signed32(value: Long): Int = value.toInt

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  inline def high32(value: Long)
      : Long :| Interval.Closed[0L, 4294967295L] = (value >>> 32).assume[Interval.Closed[0L, 4294967295L]]

  inline def join32(low: Long, high: Long): Long = (low32(high) << 32) | low32(low)

  inline def signExtend(value: Long, width: BitWidth): Long =
    if (width.value == 64) value else (value << (64 - width.value)) >> (64 - width.value)

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  inline def byteCount(width: ByteAlignedBitWidth): ScalarByteWidth =
    ScalarByteWidth.assume(width.value / 8)
}
