package net.kurobako.gemstone.semantics

import net.kurobako.gemstone.semantics.prelude.*

object Fused {
  def scaled(value: UInt[32], factor: Int, wrap: Boolean): UInt[32] = if (wrap) value * factor.toLong else value
  def sum(left: UInt[32], right: UInt[32]): UInt[32]                = left + right
  def negative(value: Float32): Bool                                = (value.bits.raw >>> 31) == 1L
  def weighted(a: UInt[32], b: UInt[32], c: UInt[32], d: UInt[32], e: UInt[32], f: UInt[32]): UInt[32] =
    a + b * 2L + c * 3L + d * 4L + e * 5L + f * 6L
}
