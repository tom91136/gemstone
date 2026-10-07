package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

/** Unsigned and signed normalised fixed-point values. Encoding clamps, maps NaN to zero, and rounds halfway values away
  * from zero.
  */
object Normalised {
  def decodeUnsigned(bits: Long, width: Int in 1 ~ 32): Double =
    Bits.truncate(bits, width).toDouble / Bits.mask(width).toDouble

  def decodeSigned(bits: Long, width: Int in 2 ~ 32): Double =
    math.max(-1.0, Bits.signExtend(bits, width).toDouble / (Bits.mask(width) >>> 1).toDouble)

  def encodeUnsigned(value: Double, width: Int in 1 ~ 32): Long =
    if (value.isNaN) 0L else Math.floor(math.max(0.0, math.min(1.0, value)) * Bits.mask(width).toDouble + 0.5).toLong

  /** Returns the low `width` bits of the two's-complement encoding. */
  def encodeSigned(value: Double, width: Int in 2 ~ 32): Long =
    if (value.isNaN) 0L
    else {
      val scaled = math.max(-1.0, math.min(1.0, value)) * (Bits.mask(width) >>> 1).toDouble
      Bits.truncate(Math.copySign(Math.floor(Math.abs(scaled) + 0.5), scaled).toLong, width)
    }
}
