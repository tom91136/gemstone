package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.testing.*

// NaN results as an RTX 4060 Ti (sm_89) computes them: driver 620.70.06, CUDA 13.3 nvcc with clang 19 as host compiler,
// kernels at -O1 using the round-to-nearest intrinsics, run on 2026-11-15. Binary16 and binary32 results are the
// canonical NaN whether the operation is invalid or has a NaN operand, as the instruction semantics apply
// FloatArithmetic.canonical to them; binary64 keeps a NaN operand quieted and gives a negative quiet NaN where invalid.
class DefaultNaNSuite extends Suite {
  private val rn = ConversionRounding.RN

  private def f16(bits: Long): Float16 = fromRaw[Float16](bits)
  private def f32(bits: Long): Float32 = fromRaw[Float32](bits)
  private def f64(bits: Long): Float64 = fromRaw[Float64](bits)

  private def canonical16(value: Float16): Long = toRaw(FloatArithmetic.canonical(value))
  private def canonical32(value: Float32): Long = toRaw(FloatArithmetic.canonical(value))

  private val cases: Seq[(String, () => Long, Long)] = Seq(
    ("f16 0*inf", () => canonical16(FloatArithmetic.multiply(f16(0x0L), f16(0x7c00L))), 0x7fffL),
    ("f16 inf-inf", () => canonical16(FloatArithmetic.add(f16(0x7c00L), f16(0xfc00L))), 0x7fffL),
    (
      "f16 fma(0,inf,1)",
      () => canonical16(FloatArithmetic.fusedMultiplyAdd(f16(0x0L), f16(0x7c00L), f16(0x3c00L), rn)),
      0x7fffL
    ),
    ("f16 snan+1", () => canonical16(FloatArithmetic.add(f16(0x7d01L), f16(0x3c00L))), 0x7fffL),
    ("f16 qnan*1", () => canonical16(FloatArithmetic.multiply(f16(0x7e01L), f16(0x3c00L))), 0x7fffL),
    ("f32 0*inf", () => canonical32(FloatArithmetic.multiply(f32(0x0L), f32(0x7f800000L))), 0x7fffffffL),
    ("f32 inf-inf", () => canonical32(FloatArithmetic.add(f32(0x7f800000L), f32(0xff800000L))), 0x7fffffffL),
    (
      "f32 fma(0,inf,1)",
      () => canonical32(FloatArithmetic.fusedMultiplyAdd(f32(0x0L), f32(0x7f800000L), f32(0x3f800000L), rn)),
      0x7fffffffL
    ),
    ("f32 0/0", () => canonical32(FloatArithmetic.divide(f32(0x0L), f32(0x0L))), 0x7fffffffL),
    ("f32 inf/inf", () => canonical32(FloatArithmetic.divide(f32(0x7f800000L), f32(0x7f800000L))), 0x7fffffffL),
    ("f32 snan+1", () => canonical32(FloatArithmetic.add(f32(0x7fa00001L), f32(0x3f800000L))), 0x7fffffffL),
    ("f32 qnan*1", () => canonical32(FloatArithmetic.multiply(f32(0x7fc00001L), f32(0x3f800000L))), 0x7fffffffL),
    ("f64 0*inf", () => toRaw(FloatArithmetic.multiply(f64(0x0L), f64(0x7ff0000000000000L))), 0xfff8000000000000L),
    (
      "f64 inf-inf",
      () => toRaw(FloatArithmetic.add(f64(0x7ff0000000000000L), f64(0xfff0000000000000L))),
      0xfff8000000000000L
    ),
    (
      "f64 fma(0,inf,1)",
      () => toRaw(FloatArithmetic.fusedMultiplyAdd(f64(0x0L), f64(0x7ff0000000000000L), f64(0x3ff0000000000000L), rn)),
      0xfff8000000000000L
    ),
    ("f64 0/0", () => toRaw(FloatArithmetic.divide(f64(0x0L), f64(0x0L))), 0xfff8000000000000L),
    (
      "f64 inf/inf",
      () => toRaw(FloatArithmetic.divide(f64(0x7ff0000000000000L), f64(0x7ff0000000000000L))),
      0xfff8000000000000L
    ),
    ("f64 sqrt(-1)", () => toRaw(FloatArithmetic.squareRoot(f64(0xbff0000000000000L))), 0xfff8000000000000L),
    (
      "f64 snan+1",
      () => toRaw(FloatArithmetic.add(f64(0x7ff4000000000001L), f64(0x3ff0000000000000L))),
      0x7ffc000000000001L
    ),
    (
      "f64 qnan*1",
      () => toRaw(FloatArithmetic.multiply(f64(0x7ff8000000000001L), f64(0x3ff0000000000000L))),
      0x7ff8000000000001L
    )
  )

  for ((name, actual, expected) <- cases)
    test(s"sm_89 $name") {
      assertEquals(actual(), expected, f"${actual()}%x != $expected%x")
    }
}
