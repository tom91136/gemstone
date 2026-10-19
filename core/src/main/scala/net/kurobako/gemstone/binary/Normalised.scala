package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

/** Unsigned and signed normalised fixed-point values. Encoding clamps, maps NaN to zero, and rounds as asked, halfway
  * values away from zero by default.
  */
object Normalised {
  def decodeUnsigned(bits: Long, width: Int in 1 ~ 32): Double =
    Bits.truncate(bits, width).toDouble / Bits.mask(width).toDouble

  def decodeSigned(bits: Long, width: Int in 2 ~ 32): Double =
    math.max(-1.0, Bits.signExtend(bits, width).toDouble / (Bits.mask(width) >>> 1).toDouble)

  def encodeUnsigned(
      value: Double,
      width: Int in 1 ~ 32,
      rounding: IEEE754.Rounding = IEEE754.Rounding.NearestAway
  ): Long =
    if (value.isNaN) 0L
    else IEEE754.integral(math.max(0.0, math.min(1.0, value)) * Bits.mask(width).toDouble, rounding).toLong

  /** Returns the low `width` bits of the two's-complement encoding. */
  def encodeSigned(
      value: Double,
      width: Int in 2 ~ 32,
      rounding: IEEE754.Rounding = IEEE754.Rounding.NearestAway
  ): Long =
    if (value.isNaN) 0L
    else {
      val scaled = math.max(-1.0, math.min(1.0, value)) * (Bits.mask(width) >>> 1).toDouble
      Bits.truncate(IEEE754.integral(scaled, rounding).toLong, width)
    }
}
