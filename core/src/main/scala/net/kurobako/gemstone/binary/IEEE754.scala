package net.kurobako.gemstone.binary

/** Raw IEEE-754 interchange encodings used by ISA registers and memory. */
object IEEE754 {
  inline def binary32ToFloat(bits: Long): Float = java.lang.Float.intBitsToFloat(bits.toInt)

  inline def floatToBinary32(value: Float): Long = Bits.unsigned32(java.lang.Float.floatToRawIntBits(value))

  inline def binary64ToDouble(bits: Long): Double = java.lang.Double.longBitsToDouble(bits)

  inline def doubleToBinary64(value: Double): Long = java.lang.Double.doubleToRawLongBits(value)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def binary16ToFloat(value: Long): Float = {
    val bits16   = value.toInt
    val sign     = (bits16 & 0x8000) << 16
    val exponent = (bits16 >>> 10) & 0x1f
    val fraction = bits16 & 0x3ff
    val bits32   =
      if (exponent == 0) {
        if (fraction == 0) sign
        else {
          var mantissa = fraction
          var shift    = 0
          while ((mantissa & 0x400) == 0) {
            mantissa <<= 1
            shift += 1
          }
          sign | ((127 - 14 - shift) << 23) | ((mantissa & 0x3ff) << 13)
        }
      } else if (exponent == 0x1f) sign | 0x7f800000 | (fraction << 13)
      else sign | ((exponent - 15 + 127) << 23) | (fraction << 13)
    java.lang.Float.intBitsToFloat(bits32)
  }

  def floatToBinary16(value: Float): Long = {
    val bits     = java.lang.Float.floatToRawIntBits(value)
    val sign     = (bits >>> 16) & 0x8000
    val exponent = (bits >>> 23) & 0xff
    val fraction = bits & 0x7fffff
    val encoded  =
      if (exponent == 0xff) sign | (if (fraction == 0) 0x7c00 else 0x7e00)
      else {
        val halfExponent = exponent - 127 + 15
        if (halfExponent >= 0x1f) sign | 0x7c00
        else if (halfExponent <= 0) {
          if (halfExponent < -10) sign
          else {
            val mantissa = fraction | 0x800000
            val shift    = 14 - halfExponent
            val rounded  = (mantissa + (1 << (shift - 1)) - 1 + ((mantissa >>> shift) & 1)) >>> shift
            sign | rounded
          }
        } else {
          val rounded = fraction + 0xfff + ((fraction >>> 13) & 1)
          if ((rounded & 0x800000) != 0) {
            val nextExponent = halfExponent + 1
            if (nextExponent >= 0x1f) sign | 0x7c00 else sign | (nextExponent << 10)
          } else sign | (halfExponent << 10) | (rounded >>> 13)
        }
      }
    encoded.toLong & Bits.Mask16
  }

  /** One round-to-nearest, ties-to-even step from the full binary64 significand. */
  def doubleToBinary16(value: Double): Long = {
    val bits        = java.lang.Double.doubleToRawLongBits(value)
    val sign        = (bits >>> 48) & 0x8000L
    val rawExponent = ((bits >>> 52) & 0x7ffL).toInt
    val fraction    = bits & 0x000fffffffffffffL
    val exponent    = rawExponent - 1023
    if (rawExponent == 0x7ff) sign | (if (fraction == 0L) 0x7c00L else 0x7e00L)
    else if (exponent > 15) sign | 0x7c00L
    else if (exponent < -25) sign
    else {
      val significand = fraction | 0x0010000000000000L
      val shift       = if (exponent < -14) 28 - exponent else 42
      val retained    = significand >>> shift
      val discarded   = significand & ((1L << shift) - 1L)
      val halfway     = 1L << (shift - 1)
      val rounded = retained + (if (discarded > halfway || (discarded == halfway && (retained & 1L) != 0L)) 1L else 0L)
      sign | (if (exponent < -14) rounded else ((exponent + 14).toLong << 10) + rounded)
    }
  }

  def toDouble(bits: Long, width: Int): Double = width match {
    case 16 => binary16ToFloat(bits).toDouble
    case 32 => binary32ToFloat(bits).toDouble
    case 64 => binary64ToDouble(bits)
    case _  => throw IllegalArgumentException(s"floating-point width must be 16, 32, or 64, found $width")
  }

  def fromDouble(value: Double, width: Int): Long = width match {
    case 16 => doubleToBinary16(value)
    case 32 => floatToBinary32(value.toFloat)
    case 64 => doubleToBinary64(value)
    case _  => throw IllegalArgumentException(s"floating-point width must be 16, 32, or 64, found $width")
  }
}
