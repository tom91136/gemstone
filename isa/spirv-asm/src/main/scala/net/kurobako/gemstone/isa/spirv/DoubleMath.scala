package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*

/** Binary64 kernels of the OpenCL.std and GLSL.std.450 functions the host evaluates and rounds once to the width. */
object DoubleMath {

  /** A lane normalize scales by the largest magnitude `scale`; where that is infinite, infinite lanes become +-1 and
    * finite ones 0, as OpenCL C requires.
    */
  def scaled(x: Double, scale: Double): Double =
    if (!scale.isInfinite) x / scale else if (x.isInfinite) Math.copySign(1.0, x) else 0.0 * x

  def truncate(value: Double): Double = IEEE754.integral(value, IEEE754.Rounding.TowardZero)

  def round(value: Double): Double = IEEE754.integral(value, IEEE754.Rounding.NearestAway)

  def signum(value: Double): Double = if (value > 0.0) 1.0 else if (value < 0.0) -1.0 else 0.0

  def logb(value: Double): Double =
    if (value.isNaN) Double.NaN
    else if (value == 0.0) Double.NegativeInfinity
    else if (value.isInfinite) Double.PositiveInfinity
    else exponent(value).toDouble

  /** IEEE 754 remainder, `x - n * y` for the integer `n` nearest `x / y` with ties to even, as fdlibm computes it, so
    * every platform agrees.
    */
  def remainder(x: Double, y: Double): Double =
    if (y == 0.0 || !x.isFinite || y.isNaN) Double.NaN
    else if (Math.abs(x) == Math.abs(y)) 0.0 * x
    else {
      val divisor = Math.abs(y)
      // below 2^1023, reduce to under twice the divisor exactly
      val reduced   = Math.abs(if (divisor < Math.scalb(1.0, 1023)) x % (divisor + divisor) else x)
      val magnitude =
        if (divisor < Math.scalb(1.0, -1021)) {
          if (reduced + reduced <= divisor) reduced
          else if ((reduced - divisor) * 2.0 >= divisor) reduced - divisor - divisor
          else reduced - divisor
        } else {
          val half = 0.5 * divisor
          if (reduced <= half) reduced
          else if (reduced - divisor >= half) reduced - divisor - divisor
          else reduced - divisor
        }
      if (java.lang.Double.doubleToRawLongBits(x) < 0L) -magnitude else magnitude
    }

  def sinPi(value: Double): Double =
    if (!value.isFinite) Double.NaN
    else if (value == Math.rint(value)) Math.copySign(0.0, value)
    else {
      val reduced   = remainder(value, 2.0)
      val x         = Math.abs(reduced)
      val magnitude =
        if (x <= 0.25) Math.sin(Math.PI * x)
        else if (x <= 0.75) Math.cos(Math.PI * (0.5 - x)) else Math.sin(Math.PI * (1.0 - x))
      Math.copySign(magnitude, reduced)
    }

  def cosPi(value: Double): Double =
    if (!value.isFinite) Double.NaN
    else {
      val x = Math.abs(remainder(value, 2.0))
      if (x == 0.5) 0.0
      else if (x <= 0.25) Math.cos(Math.PI * x)
      else if (x <= 0.75) Math.sin(Math.PI * (0.5 - x))
      else -Math.cos(Math.PI * (1.0 - x))
    }

  def tanPi(value: Double): Double =
    if (!value.isFinite) Double.NaN
    else {
      val period  = remainder(value, 2.0)
      val reduced = remainder(value, 1.0)
      val x       = Math.abs(reduced)
      if (x == 0.0) Math.copySign(0.0, if (Math.abs(period) == 1.0) -value else value)
      else if (x == 0.5) Math.copySign(Double.PositiveInfinity, period)
      else Math.copySign(if (x <= 0.25) Math.tan(Math.PI * x) else 1.0 / Math.tan(Math.PI * (0.5 - x)), reduced)
    }

  def powr(base: Double, exponent: Double): Double =
    if (
      base.isNaN || exponent.isNaN || base < 0.0 ||
      (base == 1.0 && exponent.isInfinity) ||
      ((base == 0.0 || base == Double.PositiveInfinity) && exponent == 0.0)
    ) Double.NaN
    else if (base == 0.0) { if (exponent < 0.0) Double.PositiveInfinity else 0.0 }
    else Math.pow(base, exponent)

  def rootn(value: Double, n: Int): Double =
    if (n == 0) Double.NaN
    else if (value == 0.0 && IEEE754.doubleToBinary64(value) < 0L && (n & 1) != 0) {
      if (n < 0) Double.NegativeInfinity else -0.0
    } else if (value < 0.0 && (n & 1) != 0) -Math.pow(-value, 1.0 / n.toDouble)
    else if (value < 0.0) Double.NaN
    else Math.pow(value, 1.0 / n.toDouble)

  /** The unbiased exponent of a finite non-zero value, subnormals included. */
  def exponent(value: Double): Int = {
    val exponent = Math.getExponent(value)
    if (exponent == java.lang.Double.MIN_EXPONENT - 1) Math.getExponent(Math.scalb(value, 54)) - 54 else exponent
  }

  def integerLogarithm(value: Double): Long =
    (if (value == 0.0) Int.MinValue else if (!value.isFinite) Int.MaxValue else exponent(value)).toLong & 0xffffffffL

  def frexpExponent(value: Double): Int = if (value == 0.0 || !value.isFinite) 0 else exponent(value) + 1

  def significand(value: Double): Double = Math.scalb(value, -frexpExponent(value))

  def fractional(value: Double): Double =
    if (value.isInfinite) Math.copySign(0.0, value) else Math.copySign(value - truncate(value), value)

  /** fract: `value - floor(value)`, below one by at least `maximum`. */
  def fraction(value: Double, maximum: Double): Double =
    if (value.isNaN) value
    else if (value == 0.0 || value.isInfinite) Math.copySign(0.0, value)
    else Math.min(value - Math.floor(value), maximum)

  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def smoothstep(low: Double, high: Double, value: Double, width: Int in (16, 32, 64)): Double = {
    def rounded(value: Double): Double = IEEE754.toDouble(IEEE754.fromDouble(value, width), width)
    val ratio                          = rounded(rounded(value - low) / rounded(high - low))
    val t                              = minimumDouble(maximumDouble(ratio, 0.0), 1.0)
    rounded(t * t) * rounded(3.0 - rounded(2.0 * t))
  }

  def minimumDouble(left: Double, right: Double): Double = IEEE754.binary64ToDouble(
    IEEE754.minNum(IEEE754.doubleToBinary64(left), IEEE754.doubleToBinary64(right), 64)
  )

  def maximumDouble(left: Double, right: Double): Double = IEEE754.binary64ToDouble(
    IEEE754.maxNum(IEEE754.doubleToBinary64(left), IEEE754.doubleToBinary64(right), 64)
  )

  def nextAfterBinary16(from: Long, to: Long): Long = {
    val x = IEEE754.toDouble(from, 16)
    val y = IEEE754.toDouble(to, 16)
    if (x.isNaN || y.isNaN) IEEE754.doubleToBinary16(Double.NaN).toLong
    else if (x == y) to
    else if (x == 0.0) { if (y < 0.0) 0x8001L else 1L }
    else from + (if ((y > x) == (x > 0.0)) 1L else -1L)
  }

  // GLSL defines these through exp at the operand precision, preserving intermediate overflow
  def hyperbolic(value: Double, width: Int in (16, 32, 64), sine: Boolean): Double = {
    def rounded(result: Double): Double = IEEE754.toDouble(IEEE754.fromDouble(result, width), width)
    val positive                        = rounded(Math.exp(value))
    val negative                        = rounded(Math.exp(-value))
    rounded(if (sine) positive - negative else positive + negative) / 2.0
  }
}
