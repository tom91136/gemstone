/* The polynomials, coefficient tables and radian range reduction are ported from ptx-math
 * (https://github.com/bcarlet/ptx-math), under the MIT License:
 *
 * Copyright (c) 2020 Benjamin Carleton
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.semantics.prelude.*

/** NVIDIA's special-function unit: the approximations of the reciprocal, the reciprocal square root, the square root,
  * the base-2 exponential and logarithm, and of the sine and cosine. The results are the hardware's bit patterns, where
  * the unit's own arithmetic differs from the correctly rounded one.
  *
  * The polynomials, their coefficient tables and the radian range reduction are ported from the `src/ptxm` sources of
  * [[https://github.com/bcarlet/ptx-math ptx-math]] (`ex2.c`, `lg2.c`, `rcp.c`, `rsqrt.c`, `sqrt.c`, `sincos.c`, the
  * `tables` directory and `reduction/rro_sincos.c`), under the MIT License at the top of this file.
  */
private[isa] object Mufu {

  private val CanonicalNaN = 0x7fffffffL
  private val Infinity     = 0x7f800000L
  private val One          = 0x3f800000L
  private val TwoTo24      = 0x4b800000L
  private val Half         = 0x3f000000L

  private val Ex2Bias   = 0x6fc4000000000000L
  private val Lg2Bias   = 0x868b000000000000L
  private val RcpBias   = 0x67e7000000000000L
  private val RsqrtBias = 0x7fff800000000000L
  private val SqrtBias  = 0x7fff800000000000L

  private val Ex2M   = 6
  private val Lg2M   = 6
  private val RcpM   = 7
  private val RsqrtM = 6
  private val SqrtM  = 6
  private val SinM   = 6

  private val SincosPi2Reciprocal = java.lang.Float.intBitsToFloat(0x3f22f983).toDouble
  private val SincosPi2           = 1.5707963901640554
  private val SincosQuarterMax    = java.lang.Float.intBitsToFloat(0x3fc90fdb).toDouble

  private inline def exponent(bits: Long): Int        = ((bits >> 23) & 0xff).toInt
  private inline def fraction(bits: Long): Long       = bits & 0x7fffffL
  private inline def isNaN(bits: Long): Boolean       = IEEE754.isNaN(bits, 32)
  private inline def isInfinite(bits: Long): Boolean  = exponent(bits) == 0xff && fraction(bits) == 0L
  private inline def isZero(bits: Long): Boolean      = (bits & 0x7fffffffL) == 0L
  private inline def isSubnormal(bits: Long): Boolean = exponent(bits) == 0 && fraction(bits) != 0L
  private inline def isNormal(bits: Long): Boolean    = exponent(bits) > 0 && exponent(bits) < 0xff
  private inline def isNegative(bits: Long): Boolean  = (bits & 0x80000000L) != 0L

  private inline def value(bits: Long): Float   = java.lang.Float.intBitsToFloat(bits.toInt)
  private inline def encode(value: Float): Long = java.lang.Float.floatToRawIntBits(value).toLong & 0xffffffffL

  /** The binary32 product of two values. */
  private inline def multiply(left: Long, right: Long): Long =
    IEEE754.multiply(left, right, 32, IEEE754.NaNOperand.Right)

  /** The binary32 difference of two values. */
  private inline def subtract(left: Long, right: Long): Long =
    IEEE754.add(left, right ^ 0x80000000L, 32, IEEE754.NaNOperand.Right)

  /** The exponent of a normal value, which the fractions of a zero or a subnormal do not have. */
  private inline def ilogb(bits: Long): Int = exponent(bits) - 127

  /** The integral part of a value, truncated toward zero, and its fractional part. */
  private def split(bits: Long): (Long, Long) = {
    val integral = encode(IEEE754.integral(value(bits).toDouble, IEEE754.Rounding.TowardZero).toFloat)
    (integral, subtract(bits, integral))
  }

  /** The value of `2^n`, which the unit's range reaches only between the normal exponents. */
  private def exp2i(bits: Long): Long = {
    val n = value(bits).toDouble
    if (n > 127.0) Infinity
    else if (n < -126.0) 0L
    else (127L + n.toLong) << 23
  }

  /** The value scaled by `2^n`, which overflows to infinity and underflows to zero. */
  private def scale(bits: Long, n: Int): Long = {
    val doubled = IEEE754.toDouble(bits, 32) * math.pow(2.0, n.toDouble)
    if (doubled.isNaN || doubled.isInfinite) (if (doubled < 0) 0x80000000L else 0L) | Infinity
    else IEEE754.fromDouble(doubled, 32)
  }

  /** The `x` bits deposited into the positions the mask selects. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def pdep(source: Long, mask: Long): Long = {
    var result  = 0L
    var value   = source
    var target  = 0
    var pending = mask
    while (pending != 0L) {
      if ((pending & 1L) != 0L) { if ((value & 1L) != 0L) result |= 1L << target; value >>>= 1 }
      pending >>>= 1
      target += 1
    }
    result
  }

  /** The row of the plus-plus-minus matrix of `x` at `index`. */
  private def ppmRow(x: Long, index: Int): Long =
    if (((x >> index) & 1L) == 0L) 0L
    else {
      val shifted = (x << (index + 1)) & 0xffffffffL
      shifted & (~((1L << (2 * index + 2)) - 1L) & 0xffffffffL)
    }

  /** The square of `x` less the error the plus-plus-minus squaring leaves, which the polynomials use in place of it. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def squareApprox(x: Long): Long = {
    var error = pdep(x, 0x55555555L) & 0x7ffffL
    var index = 0
    while (index < 9) {
      error += ppmRow(x, index) & 0x7ffffL
      index += 1
    }
    x * x - error
  }

  private inline def upperSignificand(bits: Long, width: Int): Long = (bits >>> (23 - width)) & ((1L << width) - 1L)
  private inline def lowerSignificand(bits: Long, width: Int): Long = bits & ((1L << (23 - width)) - 1L)
  private inline def format(sign: Long, exponent: Long, fraction: Long): Long =
    ((sign & 1L) << 31) | ((exponent & 0xffL) << 23) | (fraction & 0x7fffffL)

  /** The base-2 exponential of a value, as the unit approximates it. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def exponent2(bits: Long): Long =
    if (isNaN(bits)) CanonicalNaN
    else if (isInfinite(bits)) if (isNegative(bits)) 0L else Infinity
    else if (isZero(bits) || isSubnormal(bits)) One
    else {
      val square               = value(bits) < -126.0f
      val halved               = if (square) multiply(bits, Half) else bits
      val (integral, fraction) = split(halved)
      var whole                = integral
      var significand          = fraction
      if (fraction != 0L) {
        significand = (significand | 0x800000L) & 0xffffffL
        significand = significand >>> math.min(-ilogb(fraction), 24)
        if (isNegative(fraction) && significand != 0L) {
          significand = ~significand & 0xffffffffL
          whole = subtract(whole, One)
        }
      }
      val (c0, c1, c2) = ex2Table(upperSignificand(significand, Ex2M).toInt)
      val c0Term       = c0.toLong << 32
      val c1Term       = (c1.toLong * lowerSignificand(significand, Ex2M)) << 19
      val c2Term       = c2.toLong * squareApprox(lowerSignificand(significand, Ex2M))
      var sum          = c0Term + c1Term + c2Term
      sum = sum + (Ex2Bias >>> ((64 - 57) + 23))
      val result = format(sign = 0L, exponent = 127L, fraction = (sum >>> (57 - 23)) & 0x7fffffL)
      val scaled = multiply(exp2i(whole), result)
      if (square) multiply(scaled, scaled) else scaled
    }

  /** The base-2 logarithm of a value, as the unit approximates it. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def logarithm2(bits: Long): Long =
    if (isNaN(bits)) CanonicalNaN
    else if (isInfinite(bits)) if (isNegative(bits)) CanonicalNaN else Infinity
    else if (isZero(bits)) 0xff800000L
    else {
      val subnormal = isSubnormal(bits)
      val scaled    = if (subnormal) multiply(bits, TwoTo24) else bits
      if (isNegative(scaled)) CanonicalNaN
      else if (scaled == One) 0L
      else {
        val (c0, c1, c2) = lg2Table(upperSignificand(scaled, Lg2M).toInt)
        val c0Term       = c0.toLong << 30
        val c1Term       = (c1.toLong * lowerSignificand(scaled, Lg2M)) << 18
        val c2Term       = c2.toLong * squareApprox(lowerSignificand(scaled, Lg2M))
        var sum          = c0Term + c1Term - c2Term
        sum = sum + (Lg2Bias >>> ((64 - 56) + 23))
        val offset    = ilogb(scaled).toLong << 56
        var magnitude = math.abs(sum + offset)
        magnitude = magnitude & ~0xfffffL
        var resultExponent = 127L
        if ((magnitude >>> 56) == 0L) {
          while ((magnitude >>> 56) == 0L) {
            resultExponent -= 1
            magnitude = magnitude << 1
          }
        } else {
          while ((magnitude >>> 57) != 0L) {
            resultExponent += 1
            magnitude = magnitude >>> 1
          }
        }
        val result = format(
          sign = if (value(scaled) < 1.0f) 1L else 0L,
          exponent = resultExponent,
          fraction = (magnitude >>> (56 - 23)) & 0x7fffffL
        )
        if (subnormal) subtract(result, encode(24.0f)) else result
      }
    }

  /** The reciprocal of a value, as the unit approximates it. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def reciprocal(bits: Long): Long =
    if (isNaN(bits)) CanonicalNaN
    else if (isInfinite(bits)) if (isNegative(bits)) 0x80000000L else 0L
    else if (isZero(bits)) if (isNegative(bits)) 0xff800000L else Infinity
    else {
      val subnormal    = isSubnormal(bits)
      val scaled       = if (subnormal) multiply(bits, TwoTo24) else bits
      val logarithm    = (if (subnormal) -24 else 0) + ilogb(scaled)
      val (c0, c1, c2) = rcpTable(upperSignificand(scaled, RcpM).toInt)
      val lower        = lowerSignificand(scaled, RcpM)
      val c0Term       = c0.toLong << 30
      val c1Term       = (c1.toLong * lower) << 17
      val c2Term       = c2.toLong * (squareApprox((lower << 1) & 0xffffffffL) >>> 2)
      var sum          = c0Term - c1Term + c2Term
      sum = sum + (RcpBias >>> ((64 - 56) + 23))
      var resultExponent = 127L
      if ((sum >>> 56) == 0L) { resultExponent -= 1; sum = sum << 1 }
      val result = format(
        sign = if (isNegative(scaled)) 1L else 0L,
        exponent = resultExponent,
        fraction = (sum >>> (56 - 23)) & 0x7fffffL
      )
      scale(result, -logarithm)
    }

  /** The reciprocal square root of a value, as the unit approximates it. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def reciprocalSquareRoot(bits: Long): Long =
    if (isNaN(bits)) CanonicalNaN
    else if (isInfinite(bits)) if (isNegative(bits)) CanonicalNaN else 0L
    else if (isZero(bits)) if (isNegative(bits)) 0xff800000L else Infinity
    else {
      val subnormal = isSubnormal(bits)
      val scaled    = if (subnormal) multiply(bits, TwoTo24) else bits
      if (isNegative(scaled)) CanonicalNaN
      else {
        var logarithm = (if (subnormal) -24 else 0) + ilogb(scaled)
        val lower     = lowerSignificand(scaled, RsqrtM)
        var index     = upperSignificand(scaled, RsqrtM)
        val odd       = logarithm % 2 != 0
        if (odd) { logarithm -= 1; index += 1L << RsqrtM }
        if (!odd && fraction(scaled) == 0L) scale(One, -logarithm / 2)
        else {
          val (c0, c1, c2) = rsqrtTable(index.toInt)
          val c0Term       = c0.toLong << 31
          val c1Term       = (c1.toLong * lower) << 17
          val c2Term       = c2.toLong * squareApprox(lower)
          var sum          = c0Term - c1Term + c2Term
          sum = sum + (RsqrtBias >>> ((64 - 57) + 23))
          sum = sum << 1
          val result = format(sign = 0L, exponent = 126L, fraction = (sum >>> (57 - 23)) & 0x7fffffL)
          scale(result, -logarithm / 2)
        }
      }
    }

  /** The square root of a value, as the unit approximates it. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def squareRoot(bits: Long, flavour: MufuFlavour): Long =
    flavour match {
      case MufuFlavour.Legacy => reciprocal(reciprocalSquareRoot(bits))
      case MufuFlavour.Modern =>
        if (isNaN(bits)) CanonicalNaN
        else if (isInfinite(bits)) if (isNegative(bits)) CanonicalNaN else Infinity
        else if (isZero(bits)) if (isNegative(bits)) 0x80000000L else 0L
        else {
          val subnormal = isSubnormal(bits)
          val scaled    = if (subnormal) multiply(bits, TwoTo24) else bits
          if (isNegative(scaled)) CanonicalNaN
          else {
            var logarithm = (if (subnormal) -24 else 0) + ilogb(scaled)
            var index     = upperSignificand(scaled, SqrtM)
            val lower     = lowerSignificand(scaled, SqrtM)
            if (logarithm % 2 != 0) { logarithm -= 1; index += 1L << SqrtM }
            val (c0, c1, c2) = sqrtTable(index.toInt)
            val c0Term       = c0.toLong << 33
            val c1Term       = (c1.toLong * lower) << 18
            val c2Term       = c2.toLong * squareApprox(lower)
            var sum          = c0Term + c1Term - c2Term
            sum = sum + (SqrtBias >>> ((64 - 58) + 23))
            val result = format(sign = 0L, exponent = 127L, fraction = (sum >>> (58 - 23)) & 0x7fffffL)
            scale(result, logarithm / 2)
          }
        }
    }

  /** The reduced angle of a value in radians, which the unit's trigonometric polynomials take. */
  def reduceRadians(bits: Long, flavour: MufuFlavour): Long = {
    val reduced = reduce(bits)
    flavour match {
      case MufuFlavour.Legacy => reduced
      case MufuFlavour.Modern =>
        if (isNormal(bits)) {
          val count = math.min(math.max(exponentOf(value(bits).toDouble * SincosPi2Reciprocal), 0), 25)
          reduced & ~((1L << count) - 1L) & 0xffffffffL
        } else reduced
    }
  }

  /** The reduction of a value in radians to a quarter period, with its sign and quadrant. */
  private def reduce(bits: Long): Long = {
    val sign = if (isNegative(bits)) 1L else 0L
    if (isNaN(bits)) format(sign = sign, exponent = 0x80L, fraction = 0L)
    else if (isInfinite(bits)) format(sign = sign, exponent = 0x81L, fraction = 0L)
    else if (isZero(bits) || isSubnormal(bits)) format(sign = sign, exponent = 0L, fraction = 0L)
    else {
      val magnitude = math.abs(value(bits).toDouble)
      if (magnitude >= 8589934592.0) format(sign = sign, exponent = 0L, fraction = 0L)
      else {
        val count   = (magnitude * SincosPi2Reciprocal).toLong
        val reduced = magnitude - count * SincosPi2
        val clamped = math.min(math.max(reduced, 0.0), SincosQuarterMax)
        val packed  = format(sign = sign, exponent = count & 3L, fraction = lerp(clamped))
        packed & ~((1L << math.max(ilogb(bits) - 8, 0)) - 1L) & 0xffffffffL
      }
    }
  }

  /** The fixed-point quarter period of an angle, which the unit's polynomials index. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def lerp(angle: Double): Long = {
    val scaled = angle * SincosPi2Reciprocal
    if (scaled == 0.0) 0L
    else {
      var bits = (java.lang.Double.doubleToRawLongBits(scaled) >>> 29) & 0xffffffffL
      bits = (bits | (1L << 23)) & 0xffffffL
      bits >>> math.min(-exponentOf(scaled), 24)
    }
  }

  /** The exponent of a double, which the fractions of a zero or a subnormal do not have. */
  private def exponentOf(value: Double): Int = {
    val bits   = java.lang.Double.doubleToRawLongBits(value)
    val biased = ((bits >>> 52) & 0x7ffL).toInt
    if (biased == 0) -1074 else biased - 1023
  }

  /** The sine of a value in radians, as the unit approximates it. */
  def sine(bits: Long, flavour: MufuFlavour): Long = polynomialSine(reduceRadians(bits, flavour))

  /** The cosine of a value in radians, as the unit approximates it. */
  def cosine(bits: Long, flavour: MufuFlavour): Long = polynomialCosine(reduceRadians(bits, flavour))

  /** The hyperbolic tangent of a value, as the unit approximates it: the value itself below 2^-8 and one from 8; in
    * between, the quadratic of the segment of its binade that the top fraction bits select, evaluated on the next 17
    * bits and truncated to 24 significant bits. The coefficients are fitted to the hardware's results.
    */
  def tanh(bits: Long): Long = {
    val sign      = bits & 0x80000000L
    val magnitude = bits & 0x7fffffffL
    if (magnitude > Infinity) CanonicalNaN
    else if (magnitude >= 0x41000000L) sign | One
    else if (magnitude < 0x3b800000L) bits
    else {
      val binade    = exponent(magnitude) - 127
      val segment   = TanhSegmentBits(binade + 8)
      val index     = TanhSegmentStart(binade + 8) + ((magnitude >>> (23 - segment)) & ((1L << segment) - 1L)).toInt
      val lower     = (magnitude >>> (6 - segment)) & 0x1ffffL
      val (a, k, c) = tanhTable(index)
      val sum       = a + k * lower + c * squareApprox(lower)
      val width     = 64 - java.lang.Long.numberOfLeadingZeros(sum)
      sign | ((width - 1 + binade - 2 * segment - 51 + 127).toLong << 23) | ((sum >>> (width - 24)) & 0x7fffffL)
    }
  }

  /** The sine of an angle in cycles, which the unit reduces from the cycles themselves. */
  def sineCycles(bits: Long): Long = polynomialSine(reduceCycles(bits))

  /** The cosine of an angle in cycles, which the unit reduces from the cycles themselves. */
  def cosineCycles(bits: Long): Long = polynomialCosine(reduceCycles(bits))

  /** The quarter period of an angle in cycles, with its sign and quadrant. */
  private def reduceCycles(bits: Long): Long = {
    val sign = if (isNegative(bits)) 1L else 0L
    if (isNaN(bits)) format(sign = sign, exponent = 0x80L, fraction = 0L)
    else if (isInfinite(bits)) format(sign = sign, exponent = 0x81L, fraction = 0L)
    else if (isZero(bits)) format(sign = sign, exponent = 0L, fraction = 0L)
    else {
      val magnitude = math.abs(value(bits).toDouble)
      val quarters  = magnitude * 4.0
      val quadrant  = math.floor(quarters)
      val fraction  = quarters - quadrant
      // The whole turns of the argument do not reach the unit, whose quadrant is the remainder of the quarter periods
      // the argument holds; a whole number of quarters has none.
      format(
        sign = sign,
        exponent = (quadrant % 4.0).toLong,
        fraction = (fraction * (1L << 23)).toLong & 0x7fffffL
      )
    }
  }

  /** The unit's approximation of the function, on a binary32 value.
    *
    * The `flush` argument is the `.ftz` modifier, which flushes the subnormal operand and the subnormal result to a
    * sign-preserving zero.
    */
  def approximate(function: VMIR.MufuFunction, flavour: MufuFlavour, value: Float32, flush: Boolean): Float32 =
    fromRaw[Float32](apply(function, flavour, toRaw(value), flush))

  /** The unit's approximation of the function, on the binary32 bits of the operand. */
  def apply(function: VMIR.MufuFunction, flavour: MufuFlavour, bits: Long, flush: Boolean): Long = {
    val operand = if (flush && isSubnormal(bits)) bits & 0x80000000L else bits
    val result  = function match {
      case VMIR.MufuFunction.Exponent2            => exponent2(operand)
      case VMIR.MufuFunction.Log2                 => logarithm2(operand)
      case VMIR.MufuFunction.Reciprocal           => reciprocal(operand)
      case VMIR.MufuFunction.ReciprocalSquareRoot => reciprocalSquareRoot(operand)
      case VMIR.MufuFunction.SquareRoot           => squareRoot(operand, flavour)
      case VMIR.MufuFunction.Sine                 => sine(operand, flavour)
      case VMIR.MufuFunction.Cosine               => cosine(operand, flavour)
      case VMIR.MufuFunction.SineCycles           => sineCycles(operand)
      case VMIR.MufuFunction.CosineCycles         => cosineCycles(operand)
    }
    if (flush && isSubnormal(result)) result & 0x80000000L else result
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def polynomialSine(reduced: Long): Long = {
    var sign     = (reduced >>> 31) & 1L
    val quadrant = (reduced >>> 23) & 0xffL
    if ((quadrant & 0x80L) != 0L) CanonicalNaN
    else {
      var angle = reduced
      if ((quadrant & 1L) != 0L) angle = ~angle & 0xffffffffL
      if ((quadrant & 2L) != 0L) sign = 1L - sign
      polynomial(angle, sign)
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def polynomialCosine(reduced: Long): Long = {
    val quadrant = (reduced >>> 23) & 0xffL
    if ((quadrant & 0x80L) != 0L) CanonicalNaN
    else {
      var angle = reduced
      if ((quadrant & 1L) == 0L) angle = ~angle & 0xffffffffL
      val sign = if (((quadrant + 1L) & 2L) != 0L) 1L else 0L
      polynomial(angle, sign)
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def polynomial(reduced: Long, sign: Long): Long = {
    val (c0, c1, c2)   = sinTable(upperSignificand(reduced, SinM).toInt)
    val c0Term         = c0.toLong << 31
    val c1Term         = (c1.toLong * lowerSignificand(reduced, SinM)) << 19
    val c2Term         = c2.toLong * squareApprox(lowerSignificand(reduced, SinM))
    var sum            = c0Term + c1Term - c2Term
    var resultExponent = 0L
    if (sum != 0L) {
      resultExponent = 127L
      while ((sum >>> 56) == 0L) {
        resultExponent -= 1
        sum = sum << 1
      }
    }
    format(sign = sign, exponent = resultExponent, fraction = (sum >>> (56 - 23)) & 0x7fffffL)
  }

  // the fraction bits that select a tanh segment in each binade from 2^-8 to 2^2, and the binade's first segment
  private val TanhSegmentBits  = Array(0, 0, 0, 1, 1, 2, 2, 2, 3, 4, 3)
  private val TanhSegmentStart = Array(0, 1, 2, 3, 5, 7, 11, 15, 19, 27, 43)

  private val tanhTable: Array[(Long, Long, Long)] = Array(
    (0x8000000000000L, 0x400000000L, -4L),
    (0x7fff800000000L, 0x3fff00000L, -12L),
    (0x7ffd800000000L, 0x3ffd00000L, -52L),
    (0x1ffd5800000000L, 0x7fe200000L, -164L),
    (0x2ff70800000000L, 0x7fb900000L, -220L),
    (0x1ff55800000000L, 0x7f8600000L, -636L),
    (0x2fdc1800000000L, 0x7ee800000L, -888L),
    (0x7f566800000000L, 0xfc1400000L, -2236L),
    (0x9eb5d800000000L, 0xf9e400000L, -2704L),
    (0xbdc7f000000000L, 0xf74000000L, -3160L),
    (0xdc7e8000000000L, 0xf42a00000L, -3564L),
    (0x7d65e000000000L, 0xf0c400000L, -8276L),
    (0x9afc9800000000L, 0xe8a800000L, -9648L),
    (0xb77a8000000000L, 0xdf3700000L, -10744L),
    (0xd2b94000000000L, 0xd4b300000L, -11576L),
    (0x764d2800000000L, 0xc97300000L, -24648L),
    (0x8dfa5000000000L, 0xb13e00000L, -25152L),
    (0xa2996800000000L, 0x989700000L, -24152L),
    (0xb4338800000000L, 0x80f300000L, -22172L),
    (0x185f09000000000L, 0xd68a00000L, -39292L),
    (0x19e5d7000000000L, 0xb02e00000L, -33912L),
    (0x1b2531000000000L, 0x8f1a00000L, -28576L),
    (0x1c2796800000000L, 0x733d00000L, -23692L),
    (0x1cf704000000000L, 0x5c2600000L, -19368L),
    (0x1d9c78800000000L, 0x494800000L, -15688L),
    (0x1e1fc7000000000L, 0x3a0200000L, -12576L),
    (0x1e878f800000000L, 0x2dc400000L, -10036L),
    (0x3db2a8000000000L, 0x480500000L, -15946L),
    (0x3e3330400000000L, 0x388700000L, -12590L),
    (0x3e9801400000000L, 0x2c4900000L, -9898L),
    (0x3ee6f3400000000L, 0x22a900000L, -7784L),
    (0x3f24b4400000000L, 0x1b1700000L, -6094L),
    (0x3f54f5400000000L, 0x152980000L, -4776L),
    (0x3f7aa3c00000000L, 0x108480000L, -3734L),
    (0x3f980b400000000L, 0xce380000L, -2920L),
    (0x3faefb800000000L, 0xa0d00000L, -2278L),
    (0x3fc0de800000000L, 0x7d600000L, -1778L),
    (0x3fced0000000000L, 0x61b80000L, -1386L),
    (0x3fd9ae400000000L, 0x4c280000L, -1082L),
    (0x3fe226000000000L, 0x3b580000L, -842L),
    (0x3fe8bf400000000L, 0x2e380000L, -654L),
    (0x3fede3400000000L, 0x24000000L, -508L),
    (0x3ff1e4800000000L, 0x1c080000L, -394L),
    (0x7fea1800000000L, 0xa4c0000L, -438L),
    (0x7ff7f000000000L, 0x3cc0000L, -162L),
    (0x7ffd0800000000L, 0x1640000L, -58L),
    (0x7ffee800000000L, 0x840000L, -22L),
    (0x7fff9a00000000L, 0x300000L, -8L),
    (0x7fffda00000000L, 0x140000L, -4L),
    (0x7ffff200000000L, 0x80000L, -2L),
    (0x7ffffa00000000L, 0x40000L, -1L)
  )

  private val ex2Table: Array[(Int, Int, Int)] = buildEx2Table()

  private def buildEx2Table(): Array[(Int, Int, Int)] = Array(
    (0x2000002, 0x58b9, 0x1ee),
    (0x2059349, 0x59b0, 0x1f5),
    (0x20b361d, 0x5aaa, 0x1fa),
    (0x210e8a4, 0x5ba7, 0x1ff),
    (0x216ab11, 0x5ca6, 0x206),
    (0x21c7d88, 0x5da9, 0x209),
    (0x222603b, 0x5eae, 0x20f),
    (0x2285358, 0x5fb6, 0x214),
    (0x22e570b, 0x60c0, 0x21d),
    (0x2346b80, 0x61ce, 0x222),
    (0x23a90e7, 0x62df, 0x227),
    (0x240c774, 0x63f2, 0x22f),
    (0x2470f50, 0x6509, 0x234),
    (0x24d68af, 0x6623, 0x238),
    (0x253d3c2, 0x673f, 0x241),
    (0x25a50b8, 0x685f, 0x247),
    (0x260dfc4, 0x6982, 0x24d),
    (0x2678119, 0x6aa8, 0x254),
    (0x26e34e8, 0x6bd2, 0x258),
    (0x274fb69, 0x6cfe, 0x261),
    (0x27bd4cb, 0x6e2e, 0x267),
    (0x282c147, 0x6f61, 0x26e),
    (0x289c10d, 0x7098, 0x273),
    (0x290d458, 0x71d2, 0x279),
    (0x297fb5c, 0x730f, 0x281),
    (0x29f3650, 0x7450, 0x287),
    (0x2a6856c, 0x7594, 0x28f),
    (0x2ade8e9, 0x76dc, 0x295),
    (0x2b560fd, 0x7827, 0x29e),
    (0x2bcede4, 0x7976, 0x2a5),
    (0x2c48fd9, 0x7ac8, 0x2ae),
    (0x2cc4712, 0x7c1f, 0x2b3),
    (0x2d413ce, 0x7d79, 0x2bb),
    (0x2dbf64a, 0x7ed7, 0x2c1),
    (0x2e3eec0, 0x8038, 0x2cb),
    (0x2ebfd6c, 0x819e, 0x2d1),
    (0x2f42290, 0x8307, 0x2da),
    (0x2fc5e69, 0x8474, 0x2e3),
    (0x304b136, 0x85e5, 0x2ec),
    (0x30d1b35, 0x875b, 0x2f1),
    (0x3159caa, 0x88d4, 0x2fb),
    (0x31e35d5, 0x8a51, 0x305),
    (0x326e6f9, 0x8bd3, 0x30b),
    (0x32fb056, 0x8d59, 0x313),
    (0x3389232, 0x8ee3, 0x31c),
    (0x3418cd1, 0x9071, 0x326),
    (0x34aa078, 0x9204, 0x32d),
    (0x353cd6c, 0x939b, 0x336),
    (0x35d13f6, 0x9536, 0x341),
    (0x3667459, 0x96d6, 0x34a),
    (0x36feee0, 0x987b, 0x351),
    (0x37983d5, 0x9a24, 0x35a),
    (0x383337f, 0x9bd1, 0x366),
    (0x38cfe29, 0x9d84, 0x36d),
    (0x396e41e, 0x9f3b, 0x377),
    (0x3a0e5ac, 0xa0f7, 0x380),
    (0x3ab031f, 0xa2b7, 0x38d),
    (0x3b53cc3, 0xa47d, 0x395),
    (0x3bf92e9, 0xa648, 0x39d),
    (0x3ca05df, 0xa817, 0x3aa),
    (0x3d495f7, 0xa9ec, 0x3b2),
    (0x3df4381, 0xabc5, 0x3bf),
    (0x3ea0ece, 0xada4, 0x3c9),
    (0x3f4f833, 0xaf88, 0x3d4)
  )

  private val lg2Table: Array[(Int, Int, Int)] = buildLg2Table()

  /** The coefficients of the lg2Table's polynomial, built apart from the class's initialiser, whose bytecode the JIT
    * would refuse to compile.
    */
  private def buildLg2Table(): Array[(Int, Int, Int)] = Array(
    (0x0000003, 0xb8a8, 0x2d7),
    (0x016e796, 0xb5d1, 0x2c1),
    (0x02d75ac, 0xb310, 0x2ad),
    (0x043ace6, 0xb064, 0x299),
    (0x0598fe1, 0xadcc, 0x286),
    (0x06f210e, 0xab47, 0x273),
    (0x08462c8, 0xa8d5, 0x262),
    (0x099574f, 0xa674, 0x250),
    (0x0ae00d7, 0xa424, 0x240),
    (0x0c26161, 0xa1e4, 0x22f),
    (0x0d67af4, 0x9fb4, 0x220),
    (0x0ea4f75, 0x9d93, 0x212),
    (0x0fde0b8, 0x9b81, 0x206),
    (0x111307b, 0x997c, 0x1f8),
    (0x124407b, 0x9784, 0x1eb),
    (0x1371250, 0x9599, 0x1df),
    (0x149a785, 0x93ba, 0x1d2),
    (0x15c01a7, 0x91e7, 0x1c7),
    (0x16e2221, 0x9020, 0x1bd),
    (0x1800a58, 0x8e63, 0x1b1),
    (0x191bbab, 0x8cb2, 0x1a9),
    (0x1a3375f, 0x8b0a, 0x19e),
    (0x1b47ebd, 0x896c, 0x194),
    (0x1c592fa, 0x87d8, 0x18c),
    (0x1d6753e, 0x864c, 0x181),
    (0x1e726a9, 0x84ca, 0x179),
    (0x1f7a856, 0x8350, 0x170),
    (0x207fb51, 0x81df, 0x169),
    (0x218209e, 0x8076, 0x162),
    (0x2281941, 0x7f14, 0x15a),
    (0x237e626, 0x7dba, 0x153),
    (0x247883a, 0x7c67, 0x14b),
    (0x2570069, 0x7b1c, 0x146),
    (0x2664f8c, 0x79d7, 0x13f),
    (0x2757681, 0x7898, 0x137),
    (0x2847613, 0x7761, 0x133),
    (0x2934f0c, 0x762f, 0x12c),
    (0x2a20232, 0x7503, 0x125),
    (0x2b09049, 0x73de, 0x121),
    (0x2bef9ff, 0x72be, 0x11b),
    (0x2cd4010, 0x71a3, 0x114),
    (0x2db632d, 0x708e, 0x10f),
    (0x2e963fc, 0x6f7e, 0x10a),
    (0x2f7431c, 0x6e74, 0x106),
    (0x3050139, 0x6d6e, 0x101),
    (0x3129eed, 0x6c6d, 0x0fd),
    (0x3201cc5, 0x6b71, 0x0f9),
    (0x32d7b5c, 0x6a79, 0x0f4),
    (0x33abb40, 0x6985, 0x0ee),
    (0x347dcff, 0x6896, 0x0ea),
    (0x354e120, 0x67ac, 0x0e8),
    (0x361c822, 0x66c5, 0x0e3),
    (0x36e9292, 0x65e2, 0x0df),
    (0x37b40e3, 0x6503, 0x0db),
    (0x387d397, 0x6428, 0x0d8),
    (0x3944b1d, 0x6350, 0x0d3),
    (0x3a0a7eb, 0x627d, 0x0d1),
    (0x3acea79, 0x61ac, 0x0cc),
    (0x3b91336, 0x60df, 0x0c9),
    (0x3c52284, 0x6016, 0x0c7),
    (0x3d118d9, 0x5f4f, 0x0c3),
    (0x3dcf68f, 0x5e8c, 0x0c0),
    (0x3e8bc11, 0x5dcc, 0x0bd),
    (0x3f469c0, 0x5d0f, 0x0ba)
  )

  private val rcpTable: Array[(Int, Int, Int)] = buildRcpTable()

  /** The coefficients of the rcpTable's polynomial, built apart from the class's initialiser, whose bytecode the JIT
    * would refuse to compile.
    */
  private def buildRcpTable(): Array[(Int, Int, Int)] = Array(
    (0x3fffffd, 0xfffe, 0x3f5),
    (0x3f80fdf, 0xfc0a, 0x3dd),
    (0x3f03f03, 0xf82d, 0x3c6),
    (0x3e88cb2, 0xf467, 0x3b1),
    (0x3e0f83b, 0xf0b6, 0x39b),
    (0x3d980f5, 0xed1b, 0x386),
    (0x3d22635, 0xe995, 0x373),
    (0x3cae757, 0xe622, 0x35f),
    (0x3c3c3c1, 0xe2c3, 0x34d),
    (0x3bcbadb, 0xdf77, 0x33b),
    (0x3b5cc0e, 0xdc3d, 0x329),
    (0x3aef6c8, 0xd914, 0x317),
    (0x3a83a82, 0xd5fd, 0x306),
    (0x3a196b1, 0xd2f7, 0x2f6),
    (0x39b0ad0, 0xd001, 0x2e6),
    (0x394965f, 0xcd1b, 0x2d7),
    (0x38e38e2, 0xca44, 0x2c7),
    (0x387f1df, 0xc77c, 0x2b8),
    (0x381c0df, 0xc4c3, 0x2aa),
    (0x37ba56f, 0xc218, 0x29d),
    (0x3759f20, 0xbf7b, 0x290),
    (0x36fad86, 0xbceb, 0x282),
    (0x369d034, 0xba69, 0x277),
    (0x36406c7, 0xb7f3, 0x26a),
    (0x35e50d6, 0xb589, 0x25d),
    (0x358ae03, 0xb32c, 0x252),
    (0x3531deb, 0xb0da, 0x246),
    (0x34da033, 0xae94, 0x23c),
    (0x3483483, 0xac59, 0x231),
    (0x342da7e, 0xaa28, 0x225),
    (0x33d91d2, 0xa803, 0x21c),
    (0x3385a28, 0xa5e7, 0x211),
    (0x3333332, 0xa3d6, 0x207),
    (0x32e1c9e, 0xa1cf, 0x1fe),
    (0x329161d, 0x9fd1, 0x1f5),
    (0x3241f68, 0x9ddc, 0x1ea),
    (0x31f3830, 0x9bf1, 0x1e2),
    (0x31a6031, 0x9a0f, 0x1da),
    (0x3159721, 0x9835, 0x1d1),
    (0x310dcbe, 0x9664, 0x1c9),
    (0x30c30c2, 0x949b, 0x1c1),
    (0x30792ee, 0x92da, 0x1b9),
    (0x3030302, 0x9121, 0x1b1),
    (0x2fe80bf, 0x8f70, 0x1aa),
    (0x2fa0be7, 0x8dc6, 0x1a2),
    (0x2f5a440, 0x8c24, 0x19c),
    (0x2f1498f, 0x8a88, 0x193),
    (0x2ecfb9b, 0x88f4, 0x18d),
    (0x2e8ba2e, 0x8767, 0x186),
    (0x2e4850e, 0x85e0, 0x17f),
    (0x2e05c09, 0x8460, 0x179),
    (0x2dc3eed, 0x82e7, 0x173),
    (0x2d82d82, 0x8174, 0x16e),
    (0x2d42799, 0x8006, 0x166),
    (0x2d02d01, 0x7e9f, 0x161),
    (0x2cc3d8b, 0x7d3e, 0x15c),
    (0x2c8590a, 0x7be2, 0x155),
    (0x2c47f4f, 0x7a8d, 0x151),
    (0x2c0b02b, 0x793c, 0x14b),
    (0x2bceb75, 0x77f1, 0x146),
    (0x2b93104, 0x76ab, 0x140),
    (0x2b580ad, 0x756b, 0x13c),
    (0x2b1da46, 0x742f, 0x136),
    (0x2ae3da6, 0x72f8, 0x131),
    (0x2aaaaa9, 0x71c7, 0x12e),
    (0x2a72129, 0x709a, 0x129),
    (0x2a3a0fd, 0x6f71, 0x123),
    (0x2a02a01, 0x6e4d, 0x11f),
    (0x29cbc14, 0x6d2e, 0x11b),
    (0x299570f, 0x6c13, 0x117),
    (0x295fad3, 0x6afc, 0x112),
    (0x292a73c, 0x69ea, 0x10f),
    (0x28f5c28, 0x68db, 0x10a),
    (0x28c1978, 0x67d1, 0x107),
    (0x288df0b, 0x66ca, 0x102),
    (0x285acc3, 0x65c8, 0x100),
    (0x2828282, 0x64c9, 0x0fb),
    (0x27f6027, 0x63ce, 0x0f8),
    (0x27c4597, 0x62d6, 0x0f3),
    (0x27932b3, 0x61e2, 0x0f0),
    (0x2762761, 0x60f2, 0x0ed),
    (0x2732385, 0x6005, 0x0e9),
    (0x2702702, 0x5f1c, 0x0e7),
    (0x26d31be, 0x5e35, 0x0e2),
    (0x26a439e, 0x5d52, 0x0df),
    (0x2675c8a, 0x5c73, 0x0de),
    (0x2647c68, 0x5b96, 0x0da),
    (0x261a31f, 0x5abc, 0x0d6),
    (0x25ed097, 0x59e6, 0x0d4),
    (0x25c04b8, 0x5912, 0x0d0),
    (0x2593f68, 0x5841, 0x0cd),
    (0x2568095, 0x5774, 0x0cc),
    (0x253c824, 0x56a8, 0x0c7),
    (0x2511601, 0x55e0, 0x0c5),
    (0x24e6a16, 0x551b, 0x0c4),
    (0x24bc44d, 0x5458, 0x0c1),
    (0x2492491, 0x5397, 0x0bd),
    (0x2468ace, 0x52d9, 0x0ba),
    (0x243f6f0, 0x521e, 0x0b8),
    (0x24168e0, 0x5165, 0x0b6),
    (0x23ee08f, 0x50af, 0x0b4),
    (0x23c5de6, 0x4ffb, 0x0b2),
    (0x239e0d4, 0x4f49, 0x0af),
    (0x2376947, 0x4e9a, 0x0ad),
    (0x234f72b, 0x4ded, 0x0ab),
    (0x2328a6e, 0x4d42, 0x0a9),
    (0x2302301, 0x4c99, 0x0a6),
    (0x22dc0d0, 0x4bf3, 0x0a5),
    (0x22b63cb, 0x4b4e, 0x0a1),
    (0x2290be1, 0x4aac, 0x0a0),
    (0x226b901, 0x4a0c, 0x09f),
    (0x2246b1c, 0x496d, 0x09b),
    (0x2222221, 0x48d1, 0x09a),
    (0x21fde01, 0x4837, 0x099),
    (0x21d9eac, 0x479e, 0x096),
    (0x21b6415, 0x4708, 0x095),
    (0x2192e29, 0x4673, 0x093),
    (0x216fcdc, 0x45e0, 0x091),
    (0x214d021, 0x454f, 0x08f),
    (0x212a7e5, 0x44c0, 0x08f),
    (0x2108420, 0x4432, 0x08c),
    (0x20e64c1, 0x43a6, 0x08a),
    (0x20c49ba, 0x431c, 0x089),
    (0x20a32fe, 0x4293, 0x087),
    (0x2082081, 0x420c, 0x085),
    (0x2061236, 0x4187, 0x084),
    (0x204080e, 0x4103, 0x083),
    (0x2020200, 0x4081, 0x082)
  )

  private val rsqrtTable: Array[(Int, Int, Int)] = buildRsqrtTable()

  /** The coefficients of the rsqrtTable's polynomial, built apart from the class's initialiser, whose bytecode the JIT
    * would refuse to compile.
    */
  private def buildRsqrtTable(): Array[(Int, Int, Int)] = Array(
    (0x3fffffb, 0xfffb, 0x2f2),
    (0x3f817ab, 0xfa18, 0x2d5),
    (0x3f05d8d, 0xf46f, 0x2ba),
    (0x3e8cfe1, 0xeefb, 0x2a0),
    (0x3e16d06, 0xe9bb, 0x288),
    (0x3da336b, 0xe4ab, 0x271),
    (0x3d32197, 0xdfc9, 0x25b),
    (0x3cc3623, 0xdb13, 0x246),
    (0x3c56fb9, 0xd687, 0x232),
    (0x3becd10, 0xd222, 0x21f),
    (0x3b84cf2, 0xcde4, 0x20e),
    (0x3b1ee3d, 0xc9c9, 0x1fc),
    (0x3abafd0, 0xc5d1, 0x1ec),
    (0x3a590a4, 0xc1fa, 0x1dc),
    (0x39f8fb1, 0xbe42, 0x1cd),
    (0x399ac06, 0xbaa8, 0x1be),
    (0x393e4b4, 0xb72b, 0x1b1),
    (0x38e38e1, 0xb3ca, 0x1a4),
    (0x388a7ae, 0xb082, 0x197),
    (0x3833053, 0xad54, 0x18b),
    (0x37dd208, 0xaa3e, 0x17f),
    (0x3788c0f, 0xa73f, 0x174),
    (0x3735db3, 0xa457, 0x16a),
    (0x36e4647, 0xa184, 0x160),
    (0x3694523, 0x9ec5, 0x156),
    (0x36459a9, 0x9c1a, 0x14c),
    (0x35f8339, 0x9982, 0x143),
    (0x35ac142, 0x96fc, 0x13a),
    (0x3561334, 0x9487, 0x131),
    (0x3517886, 0x9224, 0x12a),
    (0x34cf0b2, 0x8fd0, 0x122),
    (0x3487b3e, 0x8d8d, 0x11b),
    (0x34417ac, 0x8b58, 0x113),
    (0x33fc585, 0x8932, 0x10c),
    (0x33b8454, 0x871a, 0x106),
    (0x33753b2, 0x850f, 0x0ff),
    (0x3333331, 0x8311, 0x0f8),
    (0x32f226a, 0x8120, 0x0f2),
    (0x32b20fc, 0x7f3b, 0x0ec),
    (0x3272e83, 0x7d62, 0x0e7),
    (0x3234aaa, 0x7b94, 0x0e1),
    (0x31f7510, 0x79d1, 0x0dc),
    (0x31bad65, 0x7819, 0x0d7),
    (0x317f353, 0x766b, 0x0d2),
    (0x3144689, 0x74c7, 0x0cd),
    (0x310a6b8, 0x732d, 0x0c9),
    (0x30d1398, 0x719c, 0x0c4),
    (0x3098cda, 0x7013, 0x0bf),
    (0x306123c, 0x6e94, 0x0bb),
    (0x302a375, 0x6d1d, 0x0b7),
    (0x2ff4045, 0x6bae, 0x0b3),
    (0x2fbe86c, 0x6a48, 0x0b0),
    (0x2f89bab, 0x68e9, 0x0ac),
    (0x2f559c3, 0x6791, 0x0a8),
    (0x2f2227b, 0x6641, 0x0a5),
    (0x2eef598, 0x64f8, 0x0a2),
    (0x2ebd2e6, 0x63b5, 0x09e),
    (0x2e8ba2d, 0x6279, 0x09a),
    (0x2e5ab38, 0x6144, 0x097),
    (0x2e2a5d3, 0x6015, 0x094),
    (0x2dfa9ce, 0x5eec, 0x091),
    (0x2dcb6f6, 0x5dc9, 0x08f),
    (0x2d9cd24, 0x5cac, 0x08c),
    (0x2d6ec23, 0x5b94, 0x089),
    (0x2d413c7, 0xb501, 0x215),
    (0x2ce7c60, 0xb0d8, 0x201),
    (0x2c905a4, 0xacd7, 0x1ed),
    (0x2c3ae53, 0xa8fc, 0x1db),
    (0x2be7548, 0xa546, 0x1cb),
    (0x2b9596a, 0xa1b1, 0x1ba),
    (0x2b459af, 0x9e3e, 0x1ab),
    (0x2af7512, 0x9ae9, 0x19c),
    (0x2aaaaa8, 0x97b2, 0x18e),
    (0x2a5f985, 0x9496, 0x180),
    (0x2a160d3, 0x9196, 0x173),
    (0x29cdfbb, 0x8eaf, 0x167),
    (0x298757b, 0x8be1, 0x15c),
    (0x2942150, 0x8929, 0x150),
    (0x28fe285, 0x8688, 0x146),
    (0x28bb873, 0x83fd, 0x13c),
    (0x287a269, 0x8185, 0x132),
    (0x2839fcf, 0x7f21, 0x129),
    (0x27fb00e, 0x7cd0, 0x120),
    (0x27bd28f, 0x7a90, 0x117),
    (0x27806c7, 0x7861, 0x10f),
    (0x2744c35, 0x7643, 0x107),
    (0x270a255, 0x7435, 0x100),
    (0x26d08ad, 0x7235, 0x0f8),
    (0x2697ec5, 0x7044, 0x0f1),
    (0x266042c, 0x6e61, 0x0eb),
    (0x2629877, 0x6c8c, 0x0e5),
    (0x25f3b3e, 0x6ac3, 0x0de),
    (0x25bec15, 0x6907, 0x0d9),
    (0x258aaa4, 0x6756, 0x0d2),
    (0x2557685, 0x65b1, 0x0cd),
    (0x2524f65, 0x6417, 0x0c7),
    (0x24f34e5, 0x6288, 0x0c3),
    (0x24c26ba, 0x6103, 0x0be),
    (0x2492491, 0x5f88, 0x0b9),
    (0x2462e18, 0x5e16, 0x0b4),
    (0x2434308, 0x5cae, 0x0b0),
    (0x2406317, 0x5b4e, 0x0ab),
    (0x23d8e00, 0x59f7, 0x0a7),
    (0x23ac380, 0x58a9, 0x0a3),
    (0x2380352, 0x5762, 0x09f),
    (0x2354d3b, 0x5623, 0x09b),
    (0x232a0fb, 0x54ec, 0x098),
    (0x22ffe5b, 0x53bc, 0x094),
    (0x22d651c, 0x5293, 0x091),
    (0x22ad50c, 0x5171, 0x08e),
    (0x2284df5, 0x5055, 0x08a),
    (0x225cf9d, 0x4f40, 0x088),
    (0x22359d9, 0x4e31, 0x085),
    (0x220ec77, 0x4d28, 0x082),
    (0x21e8748, 0x4c24, 0x07e),
    (0x21c2a1b, 0x4b27, 0x07c),
    (0x219d4c4, 0x4a2f, 0x07a),
    (0x217871d, 0x493c, 0x077),
    (0x21540f4, 0x484e, 0x075),
    (0x213022a, 0x4765, 0x072),
    (0x210ca91, 0x4681, 0x070),
    (0x20e9a06, 0x45a2, 0x06e),
    (0x20c7065, 0x44c7, 0x06b),
    (0x20a4d86, 0x43f1, 0x069),
    (0x2083148, 0x431f, 0x067),
    (0x2061b87, 0x4251, 0x065),
    (0x2040c25, 0x4187, 0x063),
    (0x2020305, 0x40c2, 0x061)
  )

  private val sqrtTable: Array[(Int, Int, Int)] = buildSqrtTable()

  /** The coefficients of the sqrtTable's polynomial, built apart from the class's initialiser, whose bytecode the JIT
    * would refuse to compile.
    */
  private def buildSqrtTable(): Array[(Int, Int, Int)] = Array(
    (0x1ffffff, 0x0ffff, 0x1fa),
    (0x203fc07, 0x0fe05, 0x1ee),
    (0x207f03e, 0x0fc16, 0x1e2),
    (0x20bdcd1, 0x0fa33, 0x1d8),
    (0x20fc1ec, 0x0f85a, 0x1cd),
    (0x2139fb9, 0x0f68c, 0x1c4),
    (0x2177660, 0x0f4c8, 0x1bb),
    (0x21b4609, 0x0f30d, 0x1b2),
    (0x21f0ed9, 0x0f15b, 0x1a8),
    (0x222d0f4, 0x0efb3, 0x1a1),
    (0x2268c7e, 0x0ee13, 0x198),
    (0x22a4199, 0x0ec7b, 0x18f),
    (0x22df066, 0x0eaeb, 0x187),
    (0x2319903, 0x0e964, 0x181),
    (0x2353b91, 0x0e7e3, 0x178),
    (0x238d82c, 0x0e66b, 0x172),
    (0x23c6ef2, 0x0e4f9, 0x16b),
    (0x23fffff, 0x0e38e, 0x165),
    (0x2438b6d, 0x0e22a, 0x15f),
    (0x2471157, 0x0e0cc, 0x158),
    (0x24a91d6, 0x0df74, 0x151),
    (0x24e0d03, 0x0de23, 0x14c),
    (0x25182f6, 0x0dcd7, 0x145),
    (0x254f3c6, 0x0db91, 0x13f),
    (0x2585f8a, 0x0da51, 0x13b),
    (0x25bc658, 0x0d916, 0x135),
    (0x25f2845, 0x0d7e0, 0x130),
    (0x2628565, 0x0d6b0, 0x12b),
    (0x265ddce, 0x0d584, 0x126),
    (0x2693192, 0x0d45e, 0x122),
    (0x26c80c5, 0x0d33c, 0x11d),
    (0x26fcb7a, 0x0d21e, 0x118),
    (0x27311c1, 0x0d106, 0x115),
    (0x27653af, 0x0cff1, 0x110),
    (0x2799152, 0x0cee1, 0x10c),
    (0x27ccabd, 0x0cdd5, 0x109),
    (0x27fffff, 0x0cccd, 0x105),
    (0x2833129, 0x0cbc8, 0x0ff),
    (0x2865e49, 0x0cac8, 0x0fc),
    (0x289876f, 0x0c9cc, 0x0f9),
    (0x28cacab, 0x0c8d2, 0x0f4),
    (0x28fce0a, 0x0c7dd, 0x0f2),
    (0x292eb9a, 0x0c6eb, 0x0ee),
    (0x2960569, 0x0c5fd, 0x0ec),
    (0x2991b85, 0x0c511, 0x0e7),
    (0x29c2dfa, 0x0c42a, 0x0e5),
    (0x29f3cd6, 0x0c345, 0x0e2),
    (0x2a24826, 0x0c263, 0x0de),
    (0x2a54ff4, 0x0c185, 0x0dd),
    (0x2a8544e, 0x0c0a9, 0x0d9),
    (0x2ab553f, 0x0bfd0, 0x0d6),
    (0x2ae52d2, 0x0befa, 0x0d3),
    (0x2b14d14, 0x0be26, 0x0cf),
    (0x2b4440d, 0x0bd57, 0x0cf),
    (0x2b737cb, 0x0bc88, 0x0ca),
    (0x2ba2856, 0x0bbbd, 0x0c8),
    (0x2bd15b9, 0x0baf5, 0x0c7),
    (0x2bfffff, 0x0ba2e, 0x0c2),
    (0x2c2e731, 0x0b96b, 0x0c2),
    (0x2c5cb59, 0x0b8a9, 0x0be),
    (0x2c8ac80, 0x0b7ea, 0x0bc),
    (0x2cb8aaf, 0x0b72e, 0x0bb),
    (0x2ce65f1, 0x0b673, 0x0b8),
    (0x2d13e4d, 0x0b5bb, 0x0b6),
    (0x2d413cc, 0x16a08, 0x2ca),
    (0x2d9b657, 0x1673c, 0x2b9),
    (0x2df4dd3, 0x16482, 0x2ac),
    (0x2e4da82, 0x161d6, 0x29c),
    (0x2ea5ca1, 0x15f3a, 0x28f),
    (0x2efd46b, 0x15cac, 0x281),
    (0x2f54219, 0x15a2c, 0x272),
    (0x2faa5e3, 0x157ba, 0x265),
    (0x2ffffff, 0x15554, 0x257),
    (0x305509f, 0x152fc, 0x24c),
    (0x30a97f5, 0x150b0, 0x241),
    (0x30fd632, 0x14e70, 0x237),
    (0x3150b84, 0x14c3a, 0x22a),
    (0x31a3817, 0x14a10, 0x220),
    (0x31f5c17, 0x147f0, 0x213),
    (0x32477af, 0x145dc, 0x20c),
    (0x3298b07, 0x143d0, 0x200),
    (0x32e9645, 0x141d0, 0x1f9),
    (0x3339992, 0x13fd8, 0x1f0),
    (0x3389512, 0x13de8, 0x1e4),
    (0x33d88e9, 0x13c02, 0x1dc),
    (0x3427538, 0x13a26, 0x1d6),
    (0x3475a25, 0x13850, 0x1cb),
    (0x34c37cd, 0x13684, 0x1c5),
    (0x3510e52, 0x134be, 0x1bb),
    (0x355ddd2, 0x13302, 0x1b7),
    (0x35aa66c, 0x1314c, 0x1af),
    (0x35f683d, 0x12f9e, 0x1a9),
    (0x3642362, 0x12df6, 0x1a1),
    (0x368d7f7, 0x12c54, 0x198),
    (0x36d8616, 0x12aba, 0x191),
    (0x3722dda, 0x12928, 0x18f),
    (0x376cf5c, 0x1279a, 0x187),
    (0x37b6ab6, 0x12612, 0x17f),
    (0x37fffff, 0x12492, 0x17b),
    (0x3848f50, 0x12316, 0x174),
    (0x38918bf, 0x121a2, 0x172),
    (0x38d9c63, 0x12032, 0x16c),
    (0x3921a53, 0x11ec6, 0x164),
    (0x39692a2, 0x11d62, 0x161),
    (0x39b0567, 0x11c02, 0x15c),
    (0x39f72b7, 0x11aa6, 0x155),
    (0x3a3daa3, 0x11950, 0x150),
    (0x3a83d41, 0x117fe, 0x14a),
    (0x3ac9aa3, 0x116b2, 0x147),
    (0x3b0f2dc, 0x1156a, 0x143),
    (0x3b545fd, 0x11426, 0x13d),
    (0x3b99419, 0x112e8, 0x13c),
    (0x3bddd41, 0x111ac, 0x135),
    (0x3c22186, 0x11076, 0x132),
    (0x3c660f8, 0x10f44, 0x12f),
    (0x3ca9ba9, 0x10e14, 0x129),
    (0x3ced1a6, 0x10cea, 0x126),
    (0x3d30301, 0x10bc4, 0x125),
    (0x3d72fc8, 0x10aa0, 0x11f),
    (0x3db580a, 0x10980, 0x11a),
    (0x3df7bd5, 0x10866, 0x11a),
    (0x3e39b39, 0x1074c, 0x113),
    (0x3e7b642, 0x10638, 0x111),
    (0x3ebccff, 0x10526, 0x10d),
    (0x3efdf7d, 0x10418, 0x10a),
    (0x3f3edc8, 0x1030e, 0x108),
    (0x3f7f7ef, 0x10206, 0x105),
    (0x3fbfdfd, 0x10102, 0x103)
  )

  private val sinTable: Array[(Int, Int, Int)] = buildSinTable()

  /** The coefficients of the sinTable's polynomial, built apart from the class's initialiser, whose bytecode the JIT
    * would refuse to compile.
    */
  private def buildSinTable(): Array[(Int, Int, Int)] = Array(
    (0x0000000, 0x6489, 0x00e),
    (0x00c90a7, 0x6482, 0x030),
    (0x0191f64, 0x646a, 0x04c),
    (0x025aa3e, 0x6444, 0x06e),
    (0x0322f4a, 0x640d, 0x089),
    (0x03eac9a, 0x63c8, 0x0ab),
    (0x04b203d, 0x6373, 0x0c9),
    (0x057884e, 0x630e, 0x0e5),
    (0x063e2dd, 0x629b, 0x106),
    (0x0702e08, 0x6218, 0x124),
    (0x07c67e4, 0x6186, 0x142),
    (0x0888e92, 0x60e5, 0x160),
    (0x094a02f, 0x6035, 0x17d),
    (0x0a09ae5, 0x5f76, 0x19a),
    (0x0ac7cd1, 0x5ea9, 0x1b9),
    (0x0b84426, 0x5dcd, 0x1d6),
    (0x0c3ef15, 0x5ce2, 0x1f1),
    (0x0cf7bcb, 0x5be9, 0x20d),
    (0x0dae87f, 0x5ae2, 0x229),
    (0x0e63374, 0x59cd, 0x245),
    (0x0f15aea, 0x58aa, 0x260),
    (0x0fc5d24, 0x577a, 0x27d),
    (0x1073876, 0x563c, 0x298),
    (0x111eb36, 0x54f0, 0x2b0),
    (0x11c73b0, 0x5398, 0x2cb),
    (0x126d054, 0x5232, 0x2e2),
    (0x130ff7e, 0x50c0, 0x2fb),
    (0x13affa2, 0x4f42, 0x316),
    (0x144cf2f, 0x4db7, 0x32c),
    (0x14e6ca9, 0x4c21, 0x347),
    (0x157d690, 0x4a7e, 0x35b),
    (0x1610b72, 0x48d0, 0x371),
    (0x16a09e5, 0x4717, 0x388),
    (0x172d082, 0x4553, 0x39e),
    (0x17b5df2, 0x4384, 0x3b2),
    (0x183b0df, 0x41ab, 0x3c7),
    (0x18bc804, 0x3fc8, 0x3dc),
    (0x193a226, 0x3dda, 0x3ec),
    (0x19b3e02, 0x3be4, 0x401),
    (0x1a29a79, 0x39e4, 0x413),
    (0x1a9b660, 0x37db, 0x423),
    (0x1b090a7, 0x35c9, 0x432),
    (0x1b72832, 0x33b0, 0x445),
    (0x1bd7c09, 0x318e, 0x453),
    (0x1c38b2c, 0x2f65, 0x463),
    (0x1c954b0, 0x2d34, 0x470),
    (0x1ced7ae, 0x2afc, 0x47c),
    (0x1d4134c, 0x28be, 0x48a),
    (0x1d906bd, 0x2679, 0x494),
    (0x1ddb13a, 0x242f, 0x4a1),
    (0x1e2120e, 0x21df, 0x4ac),
    (0x1e6288f, 0x1f89, 0x4b3),
    (0x1e9f416, 0x1d2f, 0x4bd),
    (0x1ed7411, 0x1ad0, 0x4c4),
    (0x1f0a7ed, 0x186e, 0x4ce),
    (0x1f38f3c, 0x1607, 0x4d4),
    (0x1f6297e, 0x139d, 0x4da),
    (0x1f87650, 0x1130, 0x4df),
    (0x1fa7559, 0x0ec0, 0x4e2),
    (0x1fc2647, 0x0c4f, 0x4ea),
    (0x1fd88d9, 0x09db, 0x4ec),
    (0x1fe9cdd, 0x0765, 0x4ec),
    (0x1ff621e, 0x04ef, 0x4ef),
    (0x1ffd886, 0x0278, 0x4f1)
  )
}
