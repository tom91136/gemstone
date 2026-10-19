package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** The floating-point dot products that round once into their result: V_DOT2_F16_F16 and V_DOT2_BF16_BF16 (GFX11 on)
  * and V_DOT4_F32_*8_*8 (GFX12 and gfx1170). Each product of two elements is exact, and so is their sum with the addend
  * for all but addends of widely apart magnitude.
  */
private[isa] object Dots {
  private inline def bits[T](value: T): Long = toRaw[T](value)

  private inline def accumulateHalves(a: Long, b: Long, addend: Double)(inline decode: Long => Double): Double =
    addend + decode(a & 0xffffL) * decode(b & 0xffffL) + decode((a >>> 16) & 0xffffL) * decode((b >>> 16) & 0xffffL)

  /** The binary16 halves of `a` and `b` multiplied pairwise and added to `c`. */
  def halves(a: UInt[32], b: UInt[32], c: Float16): Float16 =
    Float16(accumulateHalves(bits(a), bits(b), c.toDouble)(IEEE754.toDouble(_, 16)))

  /** The bfloat16 halves of `a` and `b` multiplied pairwise and added to `c`. */
  def bfloat16(a: UInt[32], b: UInt[32], c: BFloat16): BFloat16 = {
    val result = accumulateHalves(bits(a), bits(b), c.toDouble)(Minifloat.BFloat16.toDouble(_))
    fromRaw[BFloat16](IEEE754.doubleToBfloat16(result).toLong)
  }

  /** The bytes of `a` in `formatA` and of `b` in `formatB` multiplied pairwise and added to `c`. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def eightBit(a: UInt[32], b: UInt[32], c: Float32, formatA: Minifloat, formatB: Minifloat): Float32 = {
    var total = c.toDouble
    var index = 0
    while (index < 4) {
      total += formatA.toDouble((bits(a) >>> (8 * index)) & 0xffL) * formatB.toDouble((bits(b) >>> (8 * index)) & 0xffL)
      index += 1
    }
    Float32(total)
  }
}
