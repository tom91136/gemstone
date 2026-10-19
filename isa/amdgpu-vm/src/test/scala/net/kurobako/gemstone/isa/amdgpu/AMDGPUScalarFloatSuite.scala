package net.kurobako.gemstone.isa.amdgpu

import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.bits.*

/** The GFX11.5 and GFX12 SALU floating-point operations hipcc emits for uniform float and half arithmetic. */
class AMDGPUScalarFloatSuite extends munit.FunSuite {
  private val NaN = 0x7fc00000L

  // /opt/rocm/llvm/bin/llvm-mc -triple=amdgcn -mcpu=gfx1151 -show-encoding, operands s0, s1, s2
  private val gfx1151 = Seq(
    ("s_add_f16", octets(0x01, 0x02, 0x80, 0xa4), half(1.5f), half(2.25f), 0L, half(3.75f), 0xffffL),
    ("s_sub_f16", octets(0x01, 0x02, 0x00, 0xa5), half(1.5f), half(2.25f), 0L, half(-0.75f), 0xffffL),
    ("s_mul_f16", octets(0x01, 0x02, 0x80, 0xa6), half(1.5f), half(2.25f), 0L, half(3.375f), 0xffffL),
    ("s_fmac_f16", octets(0x01, 0x02, 0x00, 0xa7), half(1.5f), half(2.25f), half(0.5f), half(3.875f), 0xffffL),
    ("s_max_f16", octets(0x01, 0x02, 0x00, 0xa6), half(1.5f), half(-2.25f), 0L, half(1.5f), 0xffffL),
    ("s_min_f16", octets(0x01, 0x02, 0x80, 0xa5), half(1.5f), half(-2.25f), 0L, half(-2.25f), 0xffffL),
    ("s_max_f32", octets(0x01, 0x02, 0x80, 0xa1), single(-1.5f), single(3.0f), 0L, single(3.0f), 0xffffffffL),
    ("s_min_f32", octets(0x01, 0x02, 0x00, 0xa1), single(-1.5f), NaN, 0L, single(-1.5f), 0xffffffffL),
    (
      "s_cvt_pk_rtz_f16_f32",
      octets(0x01, 0x02, 0x00, 0xa4),
      single(1.0f / 3),
      single(-2.0f),
      0L,
      // a third truncates to binary16 0x3555
      0x3555L | (half(-2.0f) << 16),
      0xffffffffL
    ),
    ("s_cvt_f32_i32", octets(0x01, 0x64, 0x80, 0xbe), 0xfffffff9L, 0L, 0L, single(-7.0f), 0xffffffffL),
    ("s_cvt_f32_u32", octets(0x01, 0x65, 0x80, 0xbe), 0xffffffffL, 0L, 0L, single(4294967296.0f), 0xffffffffL),
    ("s_cvt_i32_f32", octets(0x01, 0x66, 0x80, 0xbe), single(-7.75f), 0L, 0L, 0xfffffff9L, 0xffffffffL),
    ("s_cvt_u32_f32", octets(0x01, 0x67, 0x80, 0xbe), single(7.75f), 0L, 0L, 7L, 0xffffffffL),
    ("s_cvt_f16_f32", octets(0x01, 0x68, 0x80, 0xbe), single(0.1f), 0L, 0L, half(0.1f), 0xffffL),
    ("s_cvt_f32_f16", octets(0x01, 0x69, 0x80, 0xbe), half(-0.75f), 0L, 0L, single(-0.75f), 0xffffffffL),
    (
      "s_cvt_hi_f32_f16",
      octets(0x01, 0x6a, 0x80, 0xbe),
      (half(3.5f) << 16) | half(1.0f),
      0L,
      0L,
      single(3.5f),
      0xffffffffL
    ),
    ("s_ceil_f32", octets(0x01, 0x60, 0x80, 0xbe), single(-1.5f), 0L, 0L, single(-1.0f), 0xffffffffL),
    ("s_floor_f32", octets(0x01, 0x61, 0x80, 0xbe), single(-1.5f), 0L, 0L, single(-2.0f), 0xffffffffL),
    ("s_trunc_f32", octets(0x01, 0x62, 0x80, 0xbe), single(-1.5f), 0L, 0L, single(-1.0f), 0xffffffffL),
    ("s_rndne_f32", octets(0x01, 0x63, 0x80, 0xbe), single(2.5f), 0L, 0L, single(2.0f), 0xffffffffL),
    ("s_ceil_f16", octets(0x01, 0x6b, 0x80, 0xbe), half(-1.5f), 0L, 0L, half(-1.0f), 0xffffL),
    ("s_floor_f16", octets(0x01, 0x6c, 0x80, 0xbe), half(-1.5f), 0L, 0L, half(-2.0f), 0xffffL),
    ("s_trunc_f16", octets(0x01, 0x6d, 0x80, 0xbe), half(-1.5f), 0L, 0L, half(-1.0f), 0xffffL),
    ("s_rndne_f16", octets(0x01, 0x6e, 0x80, 0xbe), half(2.5f), 0L, 0L, half(2.0f), 0xffffL)
  )

  // /opt/rocm/llvm/bin/llvm-mc -triple=amdgcn -mcpu=gfx1201 -show-encoding, operands s0, s1, s2
  private val gfx1201 = Seq(
    ("s_minimum_f32 of NaN", octets(0x01, 0x02, 0x80, 0xa7), NaN, single(1.0f), 0L, NaN, 0xffffffffL),
    (
      "s_minimum_f32 of zeros",
      octets(0x01, 0x02, 0x80, 0xa7),
      single(0.0f),
      single(-0.0f),
      0L,
      single(-0.0f),
      0xffffffffL
    ),
    (
      "s_maximum_f32 of zeros",
      octets(0x01, 0x02, 0x00, 0xa8),
      single(-0.0f),
      single(0.0f),
      0L,
      single(0.0f),
      0xffffffffL
    ),
    ("s_minimum_f16", octets(0x01, 0x02, 0x80, 0xa8), half(3.0f), 0x7e00L, 0L, 0x7e00L, 0xffffL),
    ("s_maximum_f16", octets(0x01, 0x02, 0x00, 0xa9), half(-0.0f), half(0.0f), 0L, half(0.0f), 0xffffL),
    ("s_min_num_f32", octets(0x01, 0x02, 0x00, 0xa1), NaN, single(1.0f), 0L, single(1.0f), 0xffffffffL),
    ("s_max_num_f16", octets(0x01, 0x02, 0x00, 0xa6), 0x7e00L, half(-1.0f), 0L, half(-1.0f), 0xffffL)
  )

  for {
    (target, cases)                              <- Seq(Target("gfx1151") -> gfx1151, Target("gfx1201") -> gfx1201)
    (name, code, a, b, previous, expected, mask) <- cases
  }
    test(s"$target $name") {
      val result = scalarResult(target, Vec(previous, a, b), program(target, code)) & mask
      if (expected == NaN || expected == 0x7e00L) assert(
        if (mask == 0xffffL) (result & 0x7c00L) == 0x7c00L && (result & 0x3ffL) != 0L
        else java.lang.Float.isNaN(java.lang.Float.intBitsToFloat(result.toInt)),
        f"$result%x is not a NaN"
      )
      else assertEquals(result, expected & mask, f"$result%x")
    }
}
