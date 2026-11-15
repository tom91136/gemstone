package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.testing.*

// NaN results as a gfx1036 iGPU computes them: the Ryzen 9 9950X3D's, amdgpu kernel 7.3.3, hipcc from ROCm 7.2.2
// (clang 22), HIP kernels at -O1 reading their operands from device memory, run on 2026-11-15
class DefaultNaNSuite extends Suite {
  private val nearest = Floats.NearestEven

  private def f16(bits: Long): Float16 = fromRaw[Float16](bits)
  private def f32(bits: Long): Float32 = fromRaw[Float32](bits)
  private def f64(bits: Long): Float64 = fromRaw[Float64](bits)

  private val cases: Seq[(String, () => Long, Long)] = Seq(
    ("f16 0*inf", () => toRaw(Floats.mul(f16(0x0000L), f16(0x7c00L), nearest)), 0xfe00L),
    ("f16 inf-inf", () => toRaw(Floats.sub(f16(0x7c00L), f16(0x7c00L), nearest)), 0xfe00L),
    ("f16 fma(0,inf,1)", () => toRaw(Floats.fma(f16(0x0000L), f16(0x7c00L), f16(0x3c00L), nearest)), 0xfe00L),
    ("f16 sqrt(-1)", () => toRaw(Floats.sqrt(f16(0xbc00L))), 0xfe00L),
    ("f16 snan+1", () => toRaw(Floats.add(f16(0x7d01L), f16(0x3c00L), nearest)), 0x7f01L),
    ("f16 snan*1", () => toRaw(Floats.mul(f16(0x7d01L), f16(0x3c00L), nearest)), 0x7f01L),
    ("f16 qnan+1", () => toRaw(Floats.add(f16(0x7e01L), f16(0x3c00L), nearest)), 0x7e01L),
    ("f16 qnan*1", () => toRaw(Floats.mul(f16(0x7e01L), f16(0x3c00L), nearest)), 0x7e01L),
    ("f32 0*inf", () => toRaw(Floats.mul(f32(0x0L), f32(0x7f800000L), nearest)), 0xffc00000L),
    ("f32 inf-inf", () => toRaw(Floats.sub(f32(0x7f800000L), f32(0x7f800000L), nearest)), 0xffc00000L),
    ("f32 fma(0,inf,1)", () => toRaw(Floats.fma(f32(0x0L), f32(0x7f800000L), f32(0x3f800000L), nearest)), 0xffc00000L),
    ("f32 sqrt(-1)", () => toRaw(Floats.sqrt(f32(0xbf800000L))), 0xffc00000L),
    ("f32 rsq(-1)", () => toRaw(Floats.rsq(f32(0xbf800000L))), 0xffc00000L),
    ("f32 log2(-1)", () => toRaw(Floats.log2(f32(0xbf800000L))), 0xffc00000L),
    ("f32 rcp(nan)", () => toRaw(Floats.rcp(f32(0xffc00000L))), 0xffc00000L),
    ("f32 snan+1", () => toRaw(Floats.add(f32(0x7fa00001L), f32(0x3f800000L), nearest)), 0x7fe00001L),
    ("f32 snan*1", () => toRaw(Floats.mul(f32(0x7fa00001L), f32(0x3f800000L), nearest)), 0x7fe00001L),
    ("f32 qnan+1", () => toRaw(Floats.add(f32(0x7fc00001L), f32(0x3f800000L), nearest)), 0x7fc00001L),
    ("f32 qnan*1", () => toRaw(Floats.mul(f32(0x7fc00001L), f32(0x3f800000L), nearest)), 0x7fc00001L),
    ("f64 0*inf", () => toRaw(Floats.mul(f64(0x0L), f64(0x7ff0000000000000L), nearest)), 0xfff8000000000000L),
    (
      "f64 inf-inf",
      () => toRaw(Floats.add(f64(0x7ff0000000000000L), f64(0xfff0000000000000L), nearest)),
      0xfff8000000000000L
    ),
    (
      "f64 fma(0,inf,1)",
      () => toRaw(Floats.fma(f64(0x0L), f64(0x7ff0000000000000L), f64(0x3ff0000000000000L), nearest)),
      0xfff8000000000000L
    ),
    ("f64 sqrt(-1)", () => toRaw(Floats.sqrt(f64(0xbff0000000000000L))), 0xfff8000000000000L),
    (
      "f64 snan+1",
      () => toRaw(Floats.add(f64(0x7ff4000000000001L), f64(0x3ff0000000000000L), nearest)),
      0x7ffc000000000001L
    ),
    (
      "f64 snan*1",
      () => toRaw(Floats.mul(f64(0x7ff4000000000001L), f64(0x3ff0000000000000L), nearest)),
      0x7ffc000000000001L
    ),
    (
      "f64 qnan+1",
      () => toRaw(Floats.add(f64(0x7ff8000000000001L), f64(0x3ff0000000000000L), nearest)),
      0x7ff8000000000001L
    ),
    (
      "f64 qnan*1",
      () => toRaw(Floats.mul(f64(0x7ff8000000000001L), f64(0x3ff0000000000000L), nearest)),
      0x7ff8000000000001L
    )
  )

  for ((name, actual, expected) <- cases)
    test(s"gfx1036 $name") {
      assertEquals(actual(), expected, f"${actual()}%x != $expected%x")
    }
}
