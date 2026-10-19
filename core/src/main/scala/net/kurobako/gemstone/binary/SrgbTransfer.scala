package net.kurobako.gemstone.binary

/** The sRGB transfer function between encoded and linear values in [0, 1]. */
object SrgbTransfer {
  def toLinear(encoded: Double): Double =
    if (encoded <= 0.04045) encoded / 12.92 else Math.pow((encoded + 0.055) / 1.055, 2.4)

  def fromLinear(linear: Double): Double =
    if (linear <= 0.0031308) linear * 12.92 else 1.055 * Math.pow(linear, 1.0 / 2.4) - 0.055
}
