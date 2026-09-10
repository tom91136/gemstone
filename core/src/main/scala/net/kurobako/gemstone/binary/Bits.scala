package net.kurobako.gemstone.binary

object Bits {
  inline val Mask16 = 0xffffL
  inline val Mask32 = 0xffffffffL

  inline def mask(width: Int): Long = {
    require(width >= 1 && width <= 64, s"bit width must be in [1, 64], found $width")
    if (width == 64) -1L else (1L << width) - 1L
  }

  inline def truncate(value: Long, width: Int): Long = value & mask(width)

  /** Count set bits in the low `width` bits, ignoring any upper register bits. */
  inline def bitCount(value: Long, width: Int): Int = java.lang.Long.bitCount(truncate(value, width))

  /** Reverse only the low `width` bits and return a zero-extended result. */
  inline def reverseBits(value: Long, width: Int): Long = {
    require(width >= 1 && width <= 64, s"bit width must be in [1, 64], found $width")
    java.lang.Long.reverse(value) >>> (64 - width)
  }

  inline def unsigned32(value: Int): Long = value.toLong & Mask32

  inline def low16(value: Long): Long = value & Mask16

  inline def low32(value: Long): Long = value & Mask32

  inline def high32(value: Long): Long = value >>> 32

  inline def join32(low: Long, high: Long): Long = (low32(high) << 32) | low32(low)

  inline def signExtend(value: Long, width: Int): Long = {
    require(width >= 1 && width <= 64, s"bit width must be in [1, 64], found $width")
    if (width == 64) value else (value << (64 - width)) >> (64 - width)
  }

  inline def byteCount(width: Int): Int = {
    require(width >= 0 && width <= 64 && width % 8 == 0, s"bit width must be byte-aligned in [0, 64], found $width")
    width / 8
  }
}
