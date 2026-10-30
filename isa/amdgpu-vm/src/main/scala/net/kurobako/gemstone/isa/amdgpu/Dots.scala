package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** The floating-point dot products. V_DOT2_F16_F16 and V_DOT2_BF16_BF16 (GFX11 on) and V_DOT4_F32_*8_*8 (GFX12 and
  * gfx1170) round once into their result: each product of two elements is exact, and so is their sum with the addend
  * for all but addends of widely apart magnitude. RDNA3 and RDNA3.5 align the terms of the V_DOT2 forms instead.
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
      val terms = Vec(product16(aLow, bLow), product16(aHigh, bHigh), single32(bits(c)))
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

  /** A floating-point format of the dot product operands: `flushed` formats take a denormal as a signed zero. */
  private case class Format(exponentBits: Int, fractionBits: Int, flushed: Boolean) {
    val width: Int       = 1 + exponentBits + fractionBits
    val bias: Int        = (1 << (exponentBits - 1)) - 1
    private val exponent = (1L << exponentBits) - 1L
    private val fraction = (1L << fractionBits) - 1L

    def negative(value: Long): Boolean    = (value & (1L << (width - 1))) != 0L
    def field(value: Long): Int           = ((value >>> fractionBits) & exponent).toInt
    def isNaN(value: Long): Boolean       = field(value) == exponent && (value & fraction) != 0L
    def infinite(value: Long): Boolean    = field(value) == exponent && (value & fraction) == 0L
    def zero(value: Long): Boolean        = (value & ((1L << (width - 1)) - 1L)) == 0L || (flushed && field(value) == 0)
    def quiet(value: Long): Long          = value | (1L << (fractionBits - 1))
    def infinity(negative: Boolean): Long =
      (if (negative) 1L << (width - 1) else 0L) | (exponent << fractionBits)
  }

  private val Half    = Format(5, 10, flushed = false)
  private val BFloat  = Format(8, 7, flushed = true)
  private val Single  = Format(8, 23, flushed = false)
  private val Flushed = Format(8, 23, flushed = true)

  /** A finite value, or the exact product of two, as `magnitude * 2^scale`; `top` is the exponent the alignment takes
    * it at, and denormals keep the least normal exponent.
    */
  private case class Term(negative: Boolean, magnitude: Long, scale: Int, top: Int) {
    def signed: BigInt = if (negative) -BigInt(magnitude) else BigInt(magnitude)
  }

  private def exponent(value: Long, format: Format): Int = math.max(format.field(value), 1) - format.bias

  private def significand(value: Long, format: Format): Long = {
    val fraction = value & ((1L << format.fractionBits) - 1L)
    if (format.field(value) == 0) if (format.flushed) 0L else fraction else fraction | (1L << format.fractionBits)
  }

  private def product(a: Long, b: Long, format: Format): Term = {
    val power = exponent(a, format) + exponent(b, format)
    Term(
      format.negative(a) != format.negative(b),
      significand(a, format) * significand(b, format),
      power - 2 * format.fractionBits,
      power + 2
    )
  }

  private def single(value: Long, format: Format): Term = {
    val power = exponent(value, format)
    Term(format.negative(value), significand(value, format), power - format.fractionBits, power)
  }

  private def product16(a: Int, b: Int): Term = product(a.toLong, b.toLong, Half)

  private def single32(value: Long): Term = single(value, Single)

  /** How an aligned dot product ends: the encodings of its special results, and the rounding of a finite sum
    * `sum * 2^scale`.
    */
  private enum Result(val format: Format, val nanOperand: Long, val invalid: Long) derives CanEqual {
    case ToSingle   extends Result(Flushed, 0xffc00a3dL, 0xffc00000L)
    case ToHalf     extends Result(Half, 0xfe3dL, 0xfe00L)
    case ToBFloat16 extends Result(BFloat, 0xfffdL, 0xffc0L)

    def round(sum: Long, scale: Int): Long = this match {
      case ToHalf =>
        val rounded =
          IEEE754.encodeSum(sum, scale, exponentBits = 5, fieldBits = 10, kept = 10, IEEE754.Rounding.NearestEven)
        if ((rounded & 0x7fffL) == 0L) 0L else rounded
      case ToSingle   => flushedSingle(sum, scale)
      case ToBFloat16 => flushedSingle(sum, scale) >>> 16
    }
  }

  /** `sum * 2^scale` as a normal binary32 rounded to nearest even, or zero where the rounded value is denormal. */
  private def flushedSingle(sum: Long, scale: Int): Long = {
    val magnitude = math.abs(sum)
    val dropped   = math.max(64 - java.lang.Long.numberOfLeadingZeros(magnitude) - 24, 0)
    val rounded   = roundBits(magnitude, dropped) << dropped
    if (rounded == 0L || 63 - java.lang.Long.numberOfLeadingZeros(rounded) + scale < -126) 0L
    else
      IEEE754.encodeSum(
        if (sum < 0L) -rounded else rounded,
        scale,
        exponentBits = 8,
        fieldBits = 23,
        kept = 23,
        IEEE754.Rounding.NearestEven
      )
  }

  /** `magnitude` shifted right by `dropped` bits, rounded to nearest even. */
  private def roundBits(magnitude: Long, dropped: Int): Long =
    if (dropped == 0) magnitude
    else {
      val kept      = magnitude >>> dropped
      val remainder = magnitude & ((1L << dropped) - 1L)
      val half      = 1L << (dropped - 1)
      if (remainder > half || (remainder == half && (kept & 1L) != 0L)) kept + 1L else kept
    }

  /** The two products of the elements of `a` and `b` and the addend `c` on RDNA3 and RDNA3.5, which do not round the
    * sum once: finite terms align to a grid 26 bits below the exponent of the largest non-zero one, each truncated
    * toward zero, and those whose sign differs from the addend's add as their one's complement, losing a unit of the
    * grid each. Zeros take part with their signs, and the sum then rounds to nearest even once. Any NaN element gives
    * `result.nanOperand`, an invalid product or sum of infinities `result.invalid`, a NaN addend in their absence
    * itself quieted, and infinities pass. A zero sum is positive.
    */
  private def aligned(a: Long, b: Long, raw: Long, element: Format, result: Result): Long = {
    val mask   = (1L << element.width) - 1L
    val addend = result.format
    val c      = raw & ((1L << addend.width) - 1L)
    val pairs  = Vec((a & mask, b & mask), ((a >>> element.width) & mask, (b >>> element.width) & mask))
    def infinite(x: Long, y: Long): Boolean = element.infinite(x) || element.infinite(y)
    val infinities = pairs.filter(infinite).map((x, y) => element.negative(x) != element.negative(y)) ++
      (if (addend.infinite(c)) Vec(addend.negative(c)) else Vec.empty[Boolean])
    if (pairs.exists((x, y) => element.isNaN(x) || element.isNaN(y))) result.nanOperand
    else if (
      pairs.exists((x, y) => (element.infinite(x) && element.zero(y)) || (element.zero(x) && element.infinite(y)))
    )
      result.invalid
    else if (infinities.distinct.size > 1) result.invalid
    else if (addend.isNaN(c)) addend.quiet(c)
    else if (infinities.nonEmpty) addend.infinity(infinities.head)
    else {
      val terms   = pairs.map(product(_, _, element)) :+ single(c, addend)
      val leading = terms.filter(_.magnitude != 0L).map(_.top)
      if (leading.isEmpty) 0L
      else {
        val grid     = leading.max - 26
        val negative = addend.negative(c)
        val sum      = terms.map { term =>
          val shifted =
            if (term.scale >= grid) term.magnitude << (term.scale - grid)
            else if (grid - term.scale >= 64) 0L
            else term.magnitude >>> (grid - term.scale)
          if (term.negative == negative) shifted else ~shifted
        }.sum
        result.round(if (negative) -sum else sum, grid)
      }
    }
  }

  /** V_DOT2_F32_F16 as RDNA3 and RDNA3.5 compute it, ignoring its clamp; see [[aligned]]. A binary32 denormal addend is
    * zero.
    */
  def halvesToFloatAligned(a: UInt[32], b: UInt[32], c: Float32): Float32 =
    fromRaw[Float32](aligned(bits(a), bits(b), bits(c), Half, Result.ToSingle))

  /** V_DOT2_F32_BF16 as RDNA3 and RDNA3.5 compute it; see [[aligned]]. Denormal bfloat16 elements are zero. */
  def bfloat16ToFloatAligned(a: UInt[32], b: UInt[32], c: Float32): Float32 =
    fromRaw[Float32](aligned(bits(a), bits(b), bits(c), BFloat, Result.ToSingle))

  /** V_DOT2_F16_F16 as RDNA3 and RDNA3.5 compute it, rounding the aligned sum to binary16; see [[aligned]]. */
  def halvesAligned(a: UInt[32], b: UInt[32], c: Float16): Float16 =
    fromRaw[Float16](aligned(bits(a), bits(b), bits(c), Half, Result.ToHalf))

  /** V_DOT2_BF16_BF16 as RDNA3 and RDNA3.5 compute it, truncating the binary32 aligned sum to bfloat16. */
  def bfloat16Aligned(a: UInt[32], b: UInt[32], c: BFloat16): BFloat16 =
    fromRaw[BFloat16](aligned(bits(a), bits(b), bits(c), BFloat, Result.ToBFloat16))

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
