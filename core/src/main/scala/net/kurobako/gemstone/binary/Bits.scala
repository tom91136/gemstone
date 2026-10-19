package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*

object Bits {
  inline val Mask8  = 0xffL
  inline val Mask16 = Binary16.Mask
  inline val Mask32 = Binary32.Mask

  inline def mask(width: Int in 1 ~ 64): Long =
    if (width == 64) -1L else (1L << width) - 1L

  inline def truncate(value: Long, width: Int in 1 ~ 64): Long = value & mask(width)

  inline def unsignedField(value: Long, offset: Int, width: Int in 1 ~ 64): Long =
    truncate(value >>> offset, width)

  inline def signedField(value: Long, offset: Int, width: Int in 1 ~ 64): Long =
    signExtend(unsignedField(value, offset, width), width)

  inline def unsignedIntField(value: Long, offset: Int, width: Int in 1 ~ 31): Int >= 0 =
    ((value >>> offset) & ((1L << width) - 1L)).toInt.unsafe[Int >= 0]

  inline def signedIntField(value: Long, offset: Int, width: Int in 1 ~ 31): Int = {
    val field   = (value >>> offset) & ((1L << width) - 1L)
    val signBit = 1L << (width - 1)
    ((field ^ signBit) - signBit).toInt
  }

  inline def isSet(value: Long, index: Int): Boolean = ((value >>> index) & 1L) != 0L

  /** Count set bits in the low `width` bits, ignoring any upper register bits. */
  inline def bitCount(value: Long, width: Int in 1 ~ 64): Int in 0 ~ 64 =
    java.lang.Long.bitCount(truncate(value, width)).unsafe[Int in 0 ~ 64]

  /** Reverse only the low `width` bits and return a zero-extended result. */
  inline def reverseBits(value: Long, width: Int in 1 ~ 64): Long =
    java.lang.Long.reverse(value) >>> (64 - width)

  inline def unsigned32(value: Int): Binary32 = Binary32.low(value.toLong)

  inline def low8Int(value: Long): Int in 0 ~ 255 = (value & Mask8).toInt.unsafe[Int in 0 ~ 255]

  inline def low16(value: Long): Binary16 = Binary16.low(value)

  inline def low16Int(value: Long): Int in 0 ~ 65535 = (value & Mask16).toInt.unsafe[Int in 0 ~ 65535]

  inline def low32(value: Long): Binary32 = Binary32.low(value)

  /** Interpret the low 32 bits as a signed two's-complement value. */
  inline def signed32(value: Long): Int = value.toInt

  inline def high32(value: Long): Binary32 = Binary32.low(value >>> 32)

  inline def join32(low: Long, high: Long): Long = (low32(high).toLong << 32) | low32(low).toLong

  inline def signExtend(value: Long, width: Int in 1 ~ 64): Long =
    if (width == 64) value else (value << (64 - width)) >> (64 - width)

  /** The low `width` bits, sign- or zero-extended. */
  inline def extend(value: Long, width: Int in 1 ~ 64, inline signed: Boolean): Long =
    if (signed) signExtend(value, width) else truncate(value, width)

  /** The low `count` bits of `value` shifted right by `offset` modulo 64, zero- or sign-extended; shifted in bits
    * repeat the sign when `signed`. An empty field is 0, and `count` is at most 64.
    */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def bitFieldExtract(value: Long, offset: Long, count: Long, inline signed: Boolean): Long =
    if (count == 0L) 0L
    else if (signed) ((value >> offset.toInt) << (64 - count.toInt)) >> (64 - count.toInt)
    else ((value >>> offset.toInt) << (64 - count.toInt)) >>> (64 - count.toInt)

  /** The leading zeros of the low `width` bits. */
  inline def leadingZeros(value: Long, width: Int in 1 ~ 64): Int =
    java.lang.Long.numberOfLeadingZeros(truncate(value, width)) - (64 - width)

  /** The low 32 bits of the 64-bit pair `high:low` shifted right by `shift` bits, for `shift` in 0 to 63. */
  inline def funnelShiftRight(low: Long, high: Long, shift: Int): Long =
    ((low32(low).toLong | (high << 32)) >>> shift) & Mask32

  /** The high 32 bits of the 64-bit pair `high:low` shifted left by `shift` bits, for `shift` in 0 to 63. */
  inline def funnelShiftLeft(low: Long, high: Long, shift: Int): Long =
    (((low32(low).toLong | (high << 32)) << shift) >>> 32) & Mask32

  inline def byteCount(width: Int where (Between[0, 64] & MultipleOf[8])): Int in 0 ~ 8 = width.raw match {
    case 0  => 0
    case 8  => 1
    case 16 => 2
    case 24 => 3
    case 32 => 4
    case 40 => 5
    case 48 => 6
    case 56 => 7
    case _  => 8
  }
}
