package net.kurobako.gemstone.isa.amdgpu

import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite

/** The GFX11 and GFX12 dot products and DX9 multiplies, which hipcc emits for `__builtin_amdgcn_sudot4`, `sudot8`,
  * `fdot2_f16_f16`, `fdot2_bf16_bf16` and `dot4_f32_fp8_bf8` (ROCm 7.1.1 `hipcc -O2 --offload-arch=gfx1100`:
  * `v_dot4_i32_iu8 v4, v2, v3, v4 neg_lo:[1,0,0]`, `v_dot8_i32_iu4 v4, v2, v3, v4 neg_lo:[0,1,0] clamp`,
  * `v_dot2_f16_f16 v4, v2, v3, v4`, `v_dot2_bf16_bf16 v1, v2, v3, v1`; gfx1201: `v_dot4_f32_fp8_bf8 v7, v2, v3, v6`).
  * Bytes from LLVM 22 `llvm-mc -triple=amdgcn -mcpu=<target> -show-encoding`.
  */
class AMDGPUDotSuite extends Suite {
  private val gfx1100 = Target("gfx1100")
  private val gfx1201 = Target("gfx1201")

  /** `result` after `code` with v2 to v5 holding `inputs`. */
  private def run(target: Target, code: Vec[Int], inputs: Vec[Long], result: Int = 5): Long =
    encoded(target, code, Vec(inputs), result).head

  test("V_DOT4_I32_IU8 and V_DOT8_I32_IU4 sign the elements NEG_LO names and clamp the signed sum") {
    val signedA = Vec(0x05, 0x40, 0x16, 0xcc, 0x02, 0x07, 0x12, 0x3c)
    assertEquals(run(gfx1100, signedA, Vec(0xff028001L, 0x0102ff03L, 10L, 0L)), 0xffff8090L)
    val clamped = Vec(0x05, 0xc0, 0x16, 0xcc, 0x02, 0x07, 0x12, 0x5c)
    assertEquals(run(gfx1100, clamped, Vec(0xffffffffL, 0x7f7f7f7fL, 0x7fffff00L, 0L)), 0x7fffffffL)
    val nibbles = Vec(0x05, 0x40, 0x18, 0xcc, 0x02, 0x07, 0x12, 0x7c)
    assertEquals(run(gfx1100, nibbles, Vec(0xf0000008L, 0x80000007L, 0L, 0L)), 0xffffffd0L)
  }

  test("V_DOT2_F16_F16 and V_DOT2_BF16_BF16 add the products to the addend half into the half OP_SEL names") {
    val half = Vec(0x05, 0x00, 0x66, 0xd6, 0x02, 0x07, 0x12, 0x04)
    assertEquals(run(gfx1100, half, Vec(0x40003e00L, 0x34004000L, 0xabcd3800L, 0x12345678L)), 0x12344400L)
    val high = Vec(0x05, 0x60, 0x66, 0xd6, 0x02, 0x07, 0x12, 0x04)
    assertEquals(run(gfx1100, high, Vec(0x40003e00L, 0x34004000L, 0x3800abcdL, 0x12345678L)), 0x44005678L)
    val bfloat = Vec(0x05, 0x00, 0x67, 0xd6, 0x02, 0x07, 0x12, 0x04)
    assertEquals(run(gfx1100, bfloat, Vec(0x40003fc0L, 0x3e804000L, 0x3f00L, 0x12345678L)), 0x12344080L)
  }

  test("V_FMA_DX9_ZERO_F32 and V_FMAC_DX9_ZERO_F32 take a zero product whatever the other factor") {
    val fma = Vec(0x05, 0x00, 0x09, 0xd6, 0x02, 0x07, 0x12, 0x04)
    assertEquals(run(gfx1100, fma, Vec(0L, 0x7f800000L, 0x3f800000L, 0L)), 0x3f800000L)
    val fmac = Vec(0x02, 0x07, 0x0a, 0x0c)
    assertEquals(run(gfx1100, fmac, Vec(0L, 0x7fc00000L, 0L, 0x40000000L)), 0x40000000L)
  }

  test("VOPD pairs the DX9 multiply and the accumulating dot products with a move") {
    val multiply = Vec(0x02, 0x07, 0xd0, 0xc9, 0x04, 0x01, 0x06, 0x05)
    assertEquals(run(gfx1100, multiply, Vec(0L, 0x7f800000L, 0x1234L, 0x40000000L)), 0L)
    assertEquals(run(gfx1100, multiply, Vec(0L, 0x7f800000L, 0x1234L, 0x40000000L), 6), 0x1234L)
    val halves = Vec(0x02, 0x07, 0x10, 0xcb, 0x04, 0x01, 0x06, 0x05)
    assertEquals(run(gfx1100, halves, Vec(0x40003e00L, 0x34004000L, 0L, 0x3f800000L)), 0x40900000L)
    val bfloat = Vec(0x02, 0x07, 0x50, 0xcb, 0x04, 0x01, 0x06, 0x05)
    assertEquals(run(gfx1100, bfloat, Vec(0x40003fc0L, 0x3e804000L, 0L, 0x3f800000L)), 0x40900000L)
  }

  test("V_DOT4_F32_*8_*8 sum the OCP 8-bit float products into the binary32 addend, negated by NEG_LO") {
    val mixed = Vec(0x05, 0x40, 0x24, 0xcc, 0x02, 0x07, 0x12, 0x1c)
    assertEquals(run(gfx1201, mixed, Vec(0x00b04038L, 0x4440383cL, 0x3e800000L, 0L)), 0x3fa00000L)
    val negated = Vec(0x05, 0x40, 0x27, 0xcc, 0x02, 0x07, 0x12, 0x9c)
    assertEquals(run(gfx1201, negated, Vec(0x4440383cL, 0x4440383cL, 0x3e800000L, 0L)), 0x41a80000L)
  }
}
