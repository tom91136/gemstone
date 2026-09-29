package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*

object Bits {
  inline val Mask8  = 0xffL
  inline val Mask16 = 0xffffL
  inline val Mask32 = 0xffffffffL

  inline def mask(width: Int where Between[1, 64]): Long =
    if (width.raw == 64) -1L else (1L << width.raw) - 1L

  inline def truncate(value: Long, width: Int where Between[1, 64]): Long = value & mask(width)

  inline def unsignedField(value: Long, offset: Int, width: Int where Between[1, 64]): Long =
    truncate(value >>> offset, width)

  inline def signedField(value: Long, offset: Int, width: Int where Between[1, 64]): Long =
    signExtend(unsignedField(value, offset, width), width)

  inline def unsignedIntField(value: Long, offset: Int, width: Int where Between[1, 31]): Int =
    ((value >>> offset) & ((1L << width.raw) - 1L)).toInt

  inline def signedIntField(value: Long, offset: Int, width: Int where Between[1, 31]): Int = {
    val field   = (value >>> offset) & ((1L << width.raw) - 1L)
    val signBit = 1L << (width.raw - 1)
    ((field ^ signBit) - signBit).toInt
  }

  inline def isSet(value: Long, index: Int): Boolean = ((value >>> index) & 1L) != 0L

  /** Count set bits in the low `width` bits, ignoring any upper register bits. */
  inline def bitCount(value: Long, width: Int where Between[1, 64]): Int =
    java.lang.Long.bitCount(truncate(value, width))

  /** Reverse only the low `width` bits and return a zero-extended result. */
  inline def reverseBits(value: Long, width: Int where Between[1, 64]): Long =
    java.lang.Long.reverse(value) >>> (64 - width.raw)

  inline def unsigned32(value: Int): Binary32 = Binary32.low(value.toLong)

  inline def low8(value: Long): Long = value & Mask8

  inline def low8Int(value: Long): Int = (value & Mask8).toInt

  inline def low16(value: Long): Binary16 = Binary16.low(value)

  inline def low16Int(value: Long): Int = (value & Mask16).toInt

  inline def low32(value: Long): Binary32 = Binary32.low(value)

  /** Interpret the low 32 bits as a signed two's-complement value. */
  inline def signed32(value: Long): Int = value.toInt

  inline def high32(value: Long): Binary32 = Binary32.low(value >>> 32)

  inline def join32(low: Long, high: Long): Long = (low32(high).toLong << 32) | low32(low).toLong

  inline def signExtend(value: Long, width: Int where Between[1, 64]): Long =
    if (width.raw == 64) value else (value << (64 - width.raw)) >> (64 - width.raw)

  inline def byteCount(width: Int where (Between[0, 64] & MultipleOf[8])): Int where Between[0, 8] =
    (width.raw / 8).unsafe[Int where Between[0, 8]]
}
