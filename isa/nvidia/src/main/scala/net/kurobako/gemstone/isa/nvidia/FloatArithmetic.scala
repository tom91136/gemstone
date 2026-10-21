package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import scala.annotation.targetName

/** NVIDIA floating-point arithmetic on IEEE binary16, binary32 and binary64 values, and on bfloat16 values as their
  * bits. NaN results are canonical where the hardware canonicalises them.
  */
// the Float16, Float32 and Float64 overloads differ in operand and result types
@SuppressWarnings(Array("baumkuchen:near-duplicate-code"))
private[isa] object FloatArithmetic {
  private inline def unary[W <: Int](value: FloatingPoint[W])(inline function: Long => Long): FloatingPoint.Named[W] =
    fromRaw[FloatingPoint.Named[W]](function(toRaw(value)))

  private inline def binary[W <: Int](left: FloatingPoint[W], right: FloatingPoint[W])(
      inline function: (Long, Long) => Long
  ): FloatingPoint.Named[W] =
    fromRaw[FloatingPoint.Named[W]](function(toRaw(left), toRaw(right)))

  private inline def bits(value: UInt[16]): Long    = toRaw(value)
  private inline def bfloat16(bits: Long): UInt[16] = fromRaw[UInt[16]](bits)

  // binary64 arithmetic propagates the right of two NaN operands quieted, and the default NaN where invalid
  private inline def double(left: Float64, right: Float64)(inline function: (Long, Long) => Long): Float64 = {
    val (x, y) = (toRaw(left), toRaw(right))
    val result = function(x, y)
    fromRaw[Float64](
      if (IEEE754.isNaN(result, 64) && !IEEE754.isNaN(x, 64) && !IEEE754.isNaN(y, 64)) VMArithmetic.DefaultNaN64
      else result
    )
  }

  @targetName("add16")
  def add(left: Float16, right: Float16): Float16 = binary(left, right)(IEEE754.add(_, _, 16, IEEE754.NaNOperand.Right))
  @targetName("add32")
  def add(left: Float32, right: Float32): Float32 = binary(left, right)(IEEE754.add(_, _, 32, IEEE754.NaNOperand.Right))
  @targetName("add64")
  def add(left: Float64, right: Float64): Float64 = double(left, right)(IEEE754.add(_, _, 64, IEEE754.NaNOperand.Right))

  @targetName("multiply16")
  def multiply(left: Float16, right: Float16): Float16 =
    binary(left, right)(IEEE754.multiply(_, _, 16, IEEE754.NaNOperand.Right))
  @targetName("multiply32")
  def multiply(left: Float32, right: Float32): Float32 =
    binary(left, right)(IEEE754.multiply(_, _, 32, IEEE754.NaNOperand.Right))
  @targetName("multiply64")
  def multiply(left: Float64, right: Float64): Float64 =
    double(left, right)(IEEE754.multiply(_, _, 64, IEEE754.NaNOperand.Right))

  @targetName("divide16")
  def divide(left: Float16, right: Float16): Float16 = binary(left, right)(VMArithmetic.floatQuotient(_, _, 16))
  @targetName("divide32")
  def divide(left: Float32, right: Float32): Float32 = binary(left, right)(VMArithmetic.floatQuotient(_, _, 32))
  @targetName("divide64")
  def divide(left: Float64, right: Float64): Float64 = binary(left, right)(VMArithmetic.floatQuotient(_, _, 64))

  @targetName("minimum16")
  def minimum(left: Float16, right: Float16, absolute: Boolean, xorSign: Boolean): Float16 =
    binary(left, right)(VMArithmetic.extremum(_, _, 16, maximum = false, propagate = true, absolute, xorSign))
  @targetName("minimum32")
  def minimum(left: Float32, right: Float32, absolute: Boolean, xorSign: Boolean): Float32 =
    binary(left, right)(VMArithmetic.extremum(_, _, 32, maximum = false, propagate = true, absolute, xorSign))
  @targetName("minimum64")
  def minimum(left: Float64, right: Float64, absolute: Boolean, xorSign: Boolean): Float64 =
    binary(left, right)(VMArithmetic.extremum(_, _, 64, maximum = false, propagate = true, absolute, xorSign))

  @targetName("maximum16")
  def maximum(left: Float16, right: Float16, absolute: Boolean, xorSign: Boolean): Float16 =
    binary(left, right)(VMArithmetic.extremum(_, _, 16, maximum = true, propagate = true, absolute, xorSign))
  @targetName("maximum32")
  def maximum(left: Float32, right: Float32, absolute: Boolean, xorSign: Boolean): Float32 =
    binary(left, right)(VMArithmetic.extremum(_, _, 32, maximum = true, propagate = true, absolute, xorSign))
  @targetName("maximum64")
  def maximum(left: Float64, right: Float64, absolute: Boolean, xorSign: Boolean): Float64 =
    binary(left, right)(VMArithmetic.extremum(_, _, 64, maximum = true, propagate = true, absolute, xorSign))

  @targetName("minimumNumber16")
  def minimumNumber(left: Float16, right: Float16, absolute: Boolean, xorSign: Boolean): Float16 =
    binary(left, right)(VMArithmetic.extremum(_, _, 16, maximum = false, propagate = false, absolute, xorSign))
  @targetName("minimumNumber32")
  def minimumNumber(left: Float32, right: Float32, absolute: Boolean, xorSign: Boolean): Float32 =
    binary(left, right)(VMArithmetic.extremum(_, _, 32, maximum = false, propagate = false, absolute, xorSign))
  @targetName("minimumNumber64")
  def minimumNumber(left: Float64, right: Float64, absolute: Boolean, xorSign: Boolean): Float64 =
    binary(left, right)(VMArithmetic.extremum(_, _, 64, maximum = false, propagate = false, absolute, xorSign))

  @targetName("maximumNumber16")
  def maximumNumber(left: Float16, right: Float16, absolute: Boolean, xorSign: Boolean): Float16 =
    binary(left, right)(VMArithmetic.extremum(_, _, 16, maximum = true, propagate = false, absolute, xorSign))
  @targetName("maximumNumber32")
  def maximumNumber(left: Float32, right: Float32, absolute: Boolean, xorSign: Boolean): Float32 =
    binary(left, right)(VMArithmetic.extremum(_, _, 32, maximum = true, propagate = false, absolute, xorSign))
  @targetName("maximumNumber64")
  def maximumNumber(left: Float64, right: Float64, absolute: Boolean, xorSign: Boolean): Float64 =
    binary(left, right)(VMArithmetic.extremum(_, _, 64, maximum = true, propagate = false, absolute, xorSign))

  @targetName("reciprocal64")
  def reciprocal(value: Float64): Float64 =
    unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Reciprocal, _, 64))

  @targetName("squareRoot64")
  def squareRoot(value: Float64): Float64 =
    unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.SquareRoot, _, 64))

  @targetName("reciprocalSquareRoot16")
  def reciprocalSquareRoot(value: Float16): Float16 =
    unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.ReciprocalSquareRoot, _, 16))
  @targetName("reciprocalSquareRoot32")
  def reciprocalSquareRoot(value: Float32): Float32 =
    unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.ReciprocalSquareRoot, _, 32))
  @targetName("reciprocalSquareRoot64")
  def reciprocalSquareRoot(value: Float64): Float64 = fromRaw[Float64](reciprocalSquareRootBits(toRaw(value)))

  /** The reciprocal square root of a binary64 value's bits, which the unit rounds to nearest even. The square root of
    * the significand is taken at more than binary64's precision, so that the result is the correctly rounded one.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def reciprocalSquareRootBits(bits: Long): Long = {
    val sign     = bits & 0x8000000000000000L
    val exponent = ((bits >>> 52) & 0x7ffL).toInt
    val fraction = bits & 0xfffffffffffffL
    if (exponent == 0x7ff) {
      // an infinity's reciprocal square root is zero, and a NaN has none
      if (fraction != 0 || sign != 0) VMArithmetic.DefaultNaN64 else 0L
    } else if (exponent == 0 && fraction == 0) {
      if (sign != 0) 0xfff0000000000000L else 0x7ff0000000000000L
    } else if (sign != 0) {
      VMArithmetic.DefaultNaN64
    } else {
      // the value is m * 2^e, whose reciprocal square root is 2^-j over the square root of an even-scaled m
      val m: BigInt = if (exponent == 0) BigInt(fraction) else BigInt(fraction | (1L << 52))
      val e: Int    = if (exponent == 0) -1074 else exponent - 1075
      val odd       = (e & 1) != 0
      val scaled    = if (odd) m * 2 else m
      val j         = (e - (if (odd) 1 else 0)) / 2
      // the square root of the scaled significand, which bounds the result from both sides
      var scale       = 64 + scaled.bitLength
      var root        = integerSquareRoot(scaled << (2 * scale))
      var power       = root.bitLength - 1
      var significand = roundedQuotient(BigInt(1) << (52 + power + 1), root)
      while (significand != roundedQuotient(BigInt(1) << (52 + power + 1), root + 1)) {
        scale += 16
        root = integerSquareRoot(scaled << (2 * scale))
        power = root.bitLength - 1
        significand = roundedQuotient(BigInt(1) << (52 + power + 1), root)
      }
      val carried  = significand >= (BigInt(1) << 53)
      val mantissa = (if (carried) significand >> 1 else significand) - (BigInt(1) << 52)
      val biased   = (scale - power - 1 - j) + 1023 + (if (carried) 1 else 0)
      (biased.toLong << 52) | mantissa.toLong
    }
  }

  /** The quotient of `numerator` by `denominator`, rounded to nearest even. */
  private def roundedQuotient(numerator: BigInt, denominator: BigInt): BigInt = {
    val quotient  = numerator / denominator
    val remainder = numerator % denominator
    val twice     = remainder * 2
    if (twice > denominator || (twice == denominator && quotient.testBit(0))) quotient + 1 else quotient
  }

  /** The integer square root of a nonnegative integer. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def integerSquareRoot(value: BigInt): BigInt =
    if (value < 2) value
    else {
      var guess = BigInt(1) << ((value.bitLength + 1) / 2)
      var next  = (guess + value / guess) / 2
      while (next < guess) {
        guess = next
        next = (guess + value / guess) / 2
      }
      guess
    }

  @targetName("exponent232")
  def exponent2(value: Float32): Float32 =
    unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Exponent2, _, 32))
  @targetName("exponent264")
  def exponent2(value: Float64): Float64 =
    unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Exponent2, _, 64))

  /** The base-2 exponential of a binary16 value, as the half unit approximates it: the unit's binary32 result rounded
    * toward zero into the half format, which is less accurate than the binary32 unit's own.
    */
  @targetName("exponent2Half")
  def exponent2Half(value: Float16): Float16 = {
    val widened = IEEE754.fromDouble(IEEE754.toDouble(toRaw(value), 16), 32)
    val result  = Mufu.exponent2(widened)
    fromRaw[Float16](halfTruncated(IEEE754.toDouble(result, 32)))
  }

  /** A binary64 value rounded toward zero into the half format, whose overflow the unit turns into an infinity where
    * the IEEE's directed rounding would keep the largest finite value.
    */
  private def halfTruncated(value: Double): Long =
    if (value >= 65536.0) 0x7c00L
    else if (value <= -65536.0) 0xfc00L
    else IEEE754.fromDouble(value, 16, IEEE754.Rounding.TowardZero)

  /** The base-2 exponential of a binary16 value's bits, as the half unit approximates it. */
  def exponent2HalfBits(bits: UInt[32]): UInt[32] =
    fromRaw[UInt[32]](toRaw(exponent2Half(fromRaw[Float16](toRaw(bits)))))

  @targetName("log216")
  def log2(value: Float16): Float16 = unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Log2, _, 16))
  @targetName("log232")
  def log2(value: Float32): Float32 = unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Log2, _, 32))
  @targetName("log264")
  def log2(value: Float64): Float64 = unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Log2, _, 64))

  @targetName("sine16")
  def sine(value: Float16): Float16 = unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Sine, _, 16))
  @targetName("sine32")
  def sine(value: Float32): Float32 = unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Sine, _, 32))
  @targetName("sine64")
  def sine(value: Float64): Float64 = unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Sine, _, 64))

  @targetName("cosine16")
  def cosine(value: Float16): Float16 = unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Cosine, _, 16))
  @targetName("cosine32")
  def cosine(value: Float32): Float32 = unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Cosine, _, 32))
  @targetName("cosine64")
  def cosine(value: Float64): Float64 = unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Cosine, _, 64))

  @targetName("tanh16")
  def tanh(value: Float16): Float16 = unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Tanh, _, 16))
  @targetName("tanh32")
  def tanh(value: Float32): Float32 = fromRaw[Float32](Mufu.tanh(toRaw(value)))
  @targetName("tanh64")
  def tanh(value: Float64): Float64 = unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Tanh, _, 64))

  @targetName("fusedMultiplyAdd16")
  def fusedMultiplyAdd(left: Float16, right: Float16, addend: Float16, rounding: ConversionRounding): Float16 =
    fromRaw[Float16](
      IEEE754.fusedMultiplyAdd(
        toRaw(left),
        toRaw(right),
        toRaw(addend),
        16,
        VMArithmetic.roundingMode(rounding)
      )
    )
  @targetName("fusedMultiplyAdd32")
  def fusedMultiplyAdd(left: Float32, right: Float32, addend: Float32, rounding: ConversionRounding): Float32 =
    fromRaw[Float32](
      IEEE754.fusedMultiplyAdd(
        toRaw(left),
        toRaw(right),
        toRaw(addend),
        32,
        VMArithmetic.roundingMode(rounding)
      )
    )

  /** A NaN operand propagates quieted, taken from `right`, then `addend`, then `left`; an invalid operation gives the
    * default NaN.
    */
  @targetName("fusedMultiplyAdd64")
  def fusedMultiplyAdd(left: Float64, right: Float64, addend: Float64, rounding: ConversionRounding): Float64 = {
    val (x, y, z) = (toRaw(left), toRaw(right), toRaw(addend))
    val quiet     = IEEE754.quietBit(64)
    fromRaw[Float64](
      if (IEEE754.isNaN(y, 64)) y | quiet
      else if (IEEE754.isNaN(z, 64)) z | quiet
      else if (IEEE754.isNaN(x, 64)) x | quiet
      else {
        val result = IEEE754.fusedMultiplyAdd(x, y, z, 64, VMArithmetic.roundingMode(rounding))
        if (IEEE754.isNaN(result, 64)) VMArithmetic.DefaultNaN64 else result
      }
    )
  }

  /** `red` and `atom` addition: binary16 and binary32 NaN results are canonical, and binary32 flushes subnormals. */
  @targetName("atomicAdd16")
  def atomicAdd(old: Float16, operand: Float16): Float16 = canonical(add(old, operand))
  @targetName("atomicAdd32")
  def atomicAdd(old: Float32, operand: Float32): Float32 = flush(canonical(add(flush(old), flush(operand))))

  /** `red` and `atom` binary64 addition, which returns a NaN operand, then a NaN old value, as it is. */
  @targetName("atomicAdd64")
  def atomicAdd(old: Float64, operand: Float64): Float64 =
    if (IEEE754.isNaN(toRaw(operand), 64)) operand
    else if (IEEE754.isNaN(toRaw(old), 64)) old
    else add(old, operand)

  def atomicAddPackedHalf(old: UInt[32], operand: UInt[32]): UInt[32] = {
    def half(shift: Int): Long = toRaw(
      atomicAdd(
        fromRaw[Float16]((toRaw(old) >>> shift) & 0xffffL),
        fromRaw[Float16]((toRaw(operand) >>> shift) & 0xffffL)
      )
    ) << shift
    fromRaw[UInt[32]](half(0) | half(16))
  }

  /** A subnormal flushed to zero of its sign. */
  @targetName("flush16")
  def flush(value: Float16): Float16 = unary(value)(VMArithmetic.flushSubnormal(_, 16))
  @targetName("flush32")
  def flush(value: Float32): Float32 = unary(value)(VMArithmetic.flushSubnormal(_, 32))
  @targetName("flush64")
  def flush(value: Float64): Float64 = unary(value)(VMArithmetic.flushSubnormal(_, 64))

  /** The value clamped to [0, 1], with NaN as 0. */
  @targetName("saturate16")
  def saturate(value: Float16): Float16 = unary(value)(VMArithmetic.saturate(_, 16))
  @targetName("saturate32")
  def saturate(value: Float32): Float32 = unary(value)(VMArithmetic.saturate(_, 32))
  @targetName("saturate64")
  def saturate(value: Float64): Float64 = unary(value)(VMArithmetic.saturate(_, 64))

  /** The value, or the canonical NaN for any NaN. */
  @targetName("canonical16")
  def canonical(value: Float16): Float16 = unary(value)(VMArithmetic.canonical(_, 16))
  @targetName("canonical32")
  def canonical(value: Float32): Float32 = unary(value)(VMArithmetic.canonical(_, 32))

  /** The sign of a binary16, bfloat16 or binary32 element flipped or cleared, with a NaN element canonical. */
  def signFlipped(value: UInt[32], width: Int in (16, 32), exponentBits: Int in (5, 8), absolute: Boolean): UInt[32] = {
    val bits         = toRaw(value)
    val mantissaBits = width - 1 - exponentBits
    val exponentMask = (1L << exponentBits) - 1L
    val mantissaMask = (1L << mantissaBits) - 1L
    val canonicalNaN = (1L << (width - 1)) - 1L
    val isNaN        = ((bits >>> mantissaBits) & exponentMask) == exponentMask && (bits & mantissaMask) != 0L
    if (isNaN) UInt[32](canonicalNaN)
    else if (absolute) UInt[32](bits & canonicalNaN)
    else UInt[32](bits ^ (1L << (width - 1)))
  }

  @targetName("compare16")
  def compare(left: Float16, right: Float16, comparison: Comparison): Bool =
    VMArithmetic.compareFloating(toRaw(left), toRaw(right), comparison, 16)
  @targetName("compare32")
  def compare(left: Float32, right: Float32, comparison: Comparison): Bool =
    VMArithmetic.compareFloating(toRaw(left), toRaw(right), comparison, 32)
  @targetName("compare64")
  def compare(left: Float64, right: Float64, comparison: Comparison): Bool =
    VMArithmetic.compareFloating(toRaw(left), toRaw(right), comparison, 64)

  def compareBfloat16(left: UInt[16], right: UInt[16], comparison: Comparison): Bool =
    VMArithmetic.compareBfloat16Values(bits(left), bits(right), comparison)

  def flushBfloat16(value: UInt[16]): UInt[16] = bfloat16(VMArithmetic.flushBfloat16(bits(value)))

  /** A binary32 value rounded to the nearest bfloat16, with NaN canonical. */
  def roundBfloat16(value: Float32): UInt[16] = bfloat16(VMArithmetic.roundBfloat16(toRaw(value)))

  def fusedMultiplyAddBfloat16(left: UInt[16], right: UInt[16], addend: UInt[16], saturate: Boolean): UInt[16] =
    bfloat16(VMArithmetic.bfloat16FusedMultiplyAdd(bits(left), bits(right), bits(addend), saturate))
}
