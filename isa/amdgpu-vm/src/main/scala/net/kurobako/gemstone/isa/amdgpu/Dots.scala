package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
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

  /** The binary16 halves of `a` and `b` multiplied pairwise and added to `c` in binary32: the first NaN operand quieted
    * with its sign and payload, an invalid sum the negative default NaN as the FMA gives, and otherwise the exact
    * products summed exactly with the addend and rounded once into binary32.
    */
  def halvesToFloat(a: UInt[32], b: UInt[32], c: Float32): Float32 = {
    val aLow  = (bits(a) & 0xffffL).toInt
    val aHigh = (bits(a) >>> 16).toInt
    val bLow  = (bits(b) & 0xffffL).toInt
    val bHigh = (bits(b) >>> 16).toInt
    val low   = IEEE754.toDouble(aLow, 16) * IEEE754.toDouble(bLow, 16)
    val high  = IEEE754.toDouble(aHigh, 16) * IEEE754.toDouble(bHigh, 16)
    if (IEEE754.isNaN(aLow, 16)) Floats.cvtF32F16(fromRaw[Float16](aLow.toLong))
    else if (IEEE754.isNaN(bLow, 16)) Floats.cvtF32F16(fromRaw[Float16](bLow.toLong))
    else if (IEEE754.isNaN(aHigh, 16)) Floats.cvtF32F16(fromRaw[Float16](aHigh.toLong))
    else if (IEEE754.isNaN(bHigh, 16)) Floats.cvtF32F16(fromRaw[Float16](bHigh.toLong))
    else if (IEEE754.isNaN(bits(c), 32)) fromRaw[Float32](bits(c) | IEEE754.quietBit(32))
    else if (!low.isFinite || !high.isFinite || !c.toFloat.isFinite) {
      val sum = low + high + c.toDouble
      if (sum.isNaN) fromRaw[Float32](0xffc00000L) else Float32(sum)
    } else {
      val terms = Vec(product(aLow, bLow), product(aHigh, bHigh), single(bits(c)))
      val scale = terms.map(_.scale).min
      val exact = terms.map(term => term.signed << (term.scale - scale)).sum
      // an exact zero is -0 only when every term is
      val negative = terms.forall(_.negative)
      // 62 bits keep two bits and a sticky bit below a binary32 significand
      val dropped = math.max(exact.bitLength - 62, 0)
      val sticky  = if (dropped > 0 && exact.abs.lowestSetBit < dropped) 1L else 0L
      val sum     = (exact.abs >> dropped).toLong | sticky
      if (exact.signum == 0) fromRaw[Float32](if (negative) 0x80000000L else 0L)
      else
        fromRaw[Float32](
          IEEE754.encodeSum(
            if (exact.signum < 0) -sum else sum,
            scale + dropped,
            exponentBits = 8,
            fieldBits = 23,
            kept = 23,
            IEEE754.Rounding.NearestEven
          )
        )
    }
  }

  /** A finite binary16 or binary32 value, or the exact product of two binary16 ones, as `magnitude * 2^scale`; `top` is
    * the bit position its significand format leads at, and denormals keep the least normal exponent.
    */
  private case class Term(negative: Boolean, magnitude: Long, scale: Int, top: Int) {
    def signed: BigInt = if (negative) -BigInt(magnitude) else BigInt(magnitude)
  }

  private def exponent16(half: Int): Int     = math.max((half >>> 10) & 0x1f, 1) - 15
  private def significand16(half: Int): Long =
    if ((half & 0x7c00) == 0) (half & 0x3ff).toLong else ((half & 0x3ff) | 0x400).toLong

  private def product(a: Int, b: Int): Term = {
    val exponent = exponent16(a) + exponent16(b)
    Term(((a ^ b) & 0x8000) != 0, significand16(a) * significand16(b), exponent - 20, exponent + 2)
  }

  private def single(value: Long): Term = {
    val exponent = math.max(((value >>> 23) & 0xffL).toInt, 1) - 127
    val fraction = value & 0x7fffffL
    Term(
      (value & 0x80000000L) != 0L,
      if ((value & 0x7f800000L) == 0L) fraction else fraction | 0x800000L,
      exponent - 23,
      exponent
    )
  }

  /** V_DOT2_F32_F16 as RDNA3.5 computes it, ignoring its clamp. A binary16 NaN gives 0xffc00a3d and an invalid product
    * or sum of infinities the negative default NaN, a NaN addend in their absence, quieted; infinities pass. Finite
    * terms align to a grid 26 bits below the leading bit of the largest non-zero one, each truncated toward zero, and
    * those whose sign differs from the addend's add as their one's complement, losing a unit of the grid each; the sum
    * then rounds once to nearest even.
    */
  def halvesToFloatAligned(a: UInt[32], b: UInt[32], c: Float32): Float32 = {
    val halves = Vec(bits(a) & 0xffffL, bits(a) >>> 16, bits(b) & 0xffffL, bits(b) >>> 16).map(_.toInt)
    val addend = bits(c)
    def infinite(half: Int): Boolean = (half & 0x7fff) == 0x7c00
    def zero(half: Int): Boolean     = (half & 0x7fff) == 0
    val pairs                        = Vec((halves(0), halves(2)), (halves(1), halves(3)))
    // the signs of the infinite terms
    val infinities = pairs.filter((x, y) => infinite(x) || infinite(y)).map((x, y) => ((x ^ y) & 0x8000) != 0) ++
      (if ((addend & 0x7fffffffL) == 0x7f800000L) Vec((addend & 0x80000000L) != 0L) else Vec.empty[Boolean])
    if (halves.exists(IEEE754.isNaN(_, 16))) fromRaw[Float32](0xffc00a3dL)
    else if (pairs.exists((x, y) => (infinite(x) && zero(y)) || (zero(x) && infinite(y))))
      fromRaw[Float32](0xffc00000L)
    else if (infinities.distinct.size > 1) fromRaw[Float32](0xffc00000L)
    else if (IEEE754.isNaN(addend, 32)) fromRaw[Float32](addend | IEEE754.quietBit(32))
    else if (infinities.nonEmpty) fromRaw[Float32](if (infinities.head) 0xff800000L else 0x7f800000L)
    else {
      val terms   = pairs.map(product(_, _)) :+ single(addend)
      val leading = terms.filter(_.magnitude != 0L).map(_.top)
      if (leading.isEmpty) Float32(0.0f)
      else {
        val grid     = leading.max - 26
        val negative = (addend & 0x80000000L) != 0L
        val sum      = terms.map { term =>
          val aligned =
            if (term.scale >= grid) term.magnitude << (term.scale - grid)
            else if (grid - term.scale >= 64) 0L
            else term.magnitude >>> (grid - term.scale)
          if (term.negative == negative) aligned else ~aligned
        }.sum
        fromRaw[Float32](
          IEEE754.encodeSum(
            if (negative) -sum else sum,
            grid,
            exponentBits = 8,
            fieldBits = 23,
            kept = 23,
            IEEE754.Rounding.NearestEven
          )
        )
      }
    }
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
