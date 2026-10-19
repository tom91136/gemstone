package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.*
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

  @targetName("reciprocal16")
  def reciprocal(value: Float16): Float16 =
    unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Reciprocal, _, 16))
  @targetName("reciprocal32")
  def reciprocal(value: Float32): Float32 =
    unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Reciprocal, _, 32))
  @targetName("reciprocal64")
  def reciprocal(value: Float64): Float64 =
    unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Reciprocal, _, 64))

  @targetName("squareRoot16")
  def squareRoot(value: Float16): Float16 =
    unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.SquareRoot, _, 16))
  @targetName("squareRoot32")
  def squareRoot(value: Float32): Float32 =
    unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.SquareRoot, _, 32))
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
  def reciprocalSquareRoot(value: Float64): Float64 =
    unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.ReciprocalSquareRoot, _, 64))

  @targetName("exponent216")
  def exponent2(value: Float16): Float16 =
    unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Exponent2, _, 16))
  @targetName("exponent232")
  def exponent2(value: Float32): Float32 =
    unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Exponent2, _, 32))
  @targetName("exponent264")
  def exponent2(value: Float64): Float64 =
    unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Exponent2, _, 64))

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
  def tanh(value: Float32): Float32 = unary(value)(VMArithmetic.floatUnaryBits(FloatUnaryOperation.Tanh, _, 32))
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
