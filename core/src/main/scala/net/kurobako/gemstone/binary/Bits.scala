package net.kurobako.gemstone.binary

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[gemstone] type BitWidth = BitWidth.T
private[gemstone] object BitWidth extends RefinedType[Int, Interval.Closed[1, 64]]

private[gemstone] type ByteAlignedBitWidth = ByteAlignedBitWidth.T
private[gemstone] object ByteAlignedBitWidth extends RefinedType[Int, Interval.Closed[0, 64] & Multiple[8]]

private[gemstone] object Bits {
  inline val Mask16 = 0xffffL
  inline val Mask32 = 0xffffffffL

  inline def mask(width: BitWidth): Long =
    if (width.value == 64) -1L else (1L << width.value) - 1L

  inline def truncate(value: Long, width: BitWidth): Long = value & mask(width)

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
  inline def low16(value: Long)
      : Long :| Interval.Closed[0L, 65535L] = (value & Mask16).assume[Interval.Closed[0L, 65535L]]

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  inline def low32(value: Long)
      : Long :| Interval.Closed[0L, 4294967295L] = (value & Mask32).assume[Interval.Closed[0L, 4294967295L]]

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
