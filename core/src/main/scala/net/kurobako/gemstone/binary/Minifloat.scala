package net.kurobako.gemstone.binary

import FloatFormat.*
import net.kurobako.gemstone.constraint.*

object Minifloat {

  /** The unsigned 11- and 10-bit floats of the packed R11G11B10 formats. */
  val E5M6: FloatFormat = FloatFormat(exponentBits = 5, fractionBits = 6, bias = 15, special = Special.UnsignedInfinite)
  val E5M5: FloatFormat = FloatFormat(exponentBits = 5, fractionBits = 5, bias = 15, special = Special.UnsignedInfinite)
}

/** Floating-point encodings narrower or stranger than IEEE-754's interchange formats, as tensor and conversion
  * instructions use them.
  */
enum Minifloat(exponentBits: Int in 1 ~ 8, fractionBits: Int in 0 ~ 23, bias: Int, special: Special)
    extends FloatFormat(exponentBits, fractionBits, bias, special) derives CanEqual {
  case BFloat16      extends Minifloat(exponentBits = 8, fractionBits = 7, bias = 127, special = Special.Infinite)
  case TensorFloat32 extends Minifloat(exponentBits = 8, fractionBits = 10, bias = 127, special = Special.Infinite)

  /** OCP 8-bit formats: E4M3 has no infinity and one NaN magnitude, E5M2 follows IEEE-754. */
  case E4M3 extends Minifloat(exponentBits = 4, fractionBits = 3, bias = 7, special = Special.NaNOnly)
  case E5M2 extends Minifloat(exponentBits = 5, fractionBits = 2, bias = 15, special = Special.Infinite)

  /** 8-bit formats whose only special encoding is NaN in place of negative zero, with the bias one higher. */
  case E4M3FNUZ extends Minifloat(exponentBits = 4, fractionBits = 3, bias = 8, special = Special.NegativeZeroNaN)
  case E5M2FNUZ extends Minifloat(exponentBits = 5, fractionBits = 2, bias = 16, special = Special.NegativeZeroNaN)

  /** OCP 6- and 4-bit formats, every encoding finite. */
  case E2M3 extends Minifloat(exponentBits = 2, fractionBits = 3, bias = 1, special = Special.Finite)
  case E3M2 extends Minifloat(exponentBits = 3, fractionBits = 2, bias = 3, special = Special.Finite)
  case E2M1 extends Minifloat(exponentBits = 2, fractionBits = 1, bias = 1, special = Special.Finite)

  /** The OCP scale: an unsigned exponent, `2^(e - 127)`, with all ones NaN. */
  case E8M0 extends Minifloat(exponentBits = 8, fractionBits = 0, bias = 127, special = Special.Scale)
}
