package net.kurobako.gemstone.isa.amdgpu

import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.bits.*

/** SDWA selections, sign extension, source modifiers, clamp and output placement on the VOP1 and VOP2 operations the
  * LLVM SDWA peephole forms for byte, short and half arithmetic on GFX8 to GFX10, e.g. from
  * `hipcc --offload-arch=gfx906 -O3` on `max((int)(short)(s >> 16), (int)(short)s)`, which emits
  * `v_max_i32_sdwa v3, sext(v1), sext(v1) dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:WORD_1 src1_sel:WORD_0`.
  */
class AMDGPUSubDwordSuite extends munit.FunSuite {

  // /opt/rocm/llvm/bin/llvm-mc -triple=amdgcn -show-encoding with -mcpu=gfx803, gfx906 and gfx1030; v2, v3 and v4 hold
  // the inputs and v4 the result
  private val cases = Seq[(String, Vec[(Target, Vec[Int])], Vec[Long], Long)](
    (
      "v_cvt_f32_i32_sdwa v4, sext(v2) dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:WORD_1",
      Vec(
        Target("gfx803")  -> Vec(0xf9, 0x0a, 0x08, 0x7e, 0x02, 0x06, 0x0d, 0x00),
        Target("gfx906")  -> Vec(0xf9, 0x0a, 0x08, 0x7e, 0x02, 0x06, 0x0d, 0x00),
        Target("gfx1030") -> Vec(0xf9, 0x0a, 0x08, 0x7e, 0x02, 0x06, 0x0d, 0x00)
      ),
      Vec(0xfff90003L, 0L, 0L),
      single(-7.0f)
    ),
    (
      "v_cvt_f32_u32_sdwa v4, v2 dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:WORD_0",
      Vec(
        Target("gfx803")  -> Vec(0xf9, 0x0c, 0x08, 0x7e, 0x02, 0x06, 0x04, 0x00),
        Target("gfx906")  -> Vec(0xf9, 0x0c, 0x08, 0x7e, 0x02, 0x06, 0x04, 0x00),
        Target("gfx1030") -> Vec(0xf9, 0x0c, 0x08, 0x7e, 0x02, 0x06, 0x04, 0x00)
      ),
      Vec(0xfff90003L, 0L, 0L),
      single(3.0f)
    ),
    (
      "v_max_i32_sdwa v4, sext(v2), sext(v2) dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:WORD_1 src1_sel:WORD_0",
      Vec(
        Target("gfx803")  -> Vec(0xf9, 0x04, 0x08, 0x1a, 0x02, 0x06, 0x0d, 0x0c),
        Target("gfx906")  -> Vec(0xf9, 0x04, 0x08, 0x1a, 0x02, 0x06, 0x0d, 0x0c),
        Target("gfx1030") -> Vec(0xf9, 0x04, 0x08, 0x24, 0x02, 0x06, 0x0d, 0x0c)
      ),
      Vec(0xfff90003L, 0L, 0L),
      3L
    ),
    (
      "v_min_u32_sdwa v4, v2, v3 dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:BYTE_1 src1_sel:BYTE_0",
      Vec(
        Target("gfx803")  -> Vec(0xf9, 0x06, 0x08, 0x1c, 0x02, 0x06, 0x01, 0x00),
        Target("gfx906")  -> Vec(0xf9, 0x06, 0x08, 0x1c, 0x02, 0x06, 0x01, 0x00),
        Target("gfx1030") -> Vec(0xf9, 0x06, 0x08, 0x26, 0x02, 0x06, 0x01, 0x00)
      ),
      Vec(0x0000ab00L, 0x12345612L, 0L),
      0x12L
    ),
    (
      "v_max_u16_sdwa v4, v2, v3 dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:BYTE_3 src1_sel:BYTE_3",
      Vec(
        Target("gfx803") -> Vec(0xf9, 0x06, 0x08, 0x5e, 0x02, 0x06, 0x03, 0x03),
        Target("gfx906") -> Vec(0xf9, 0x06, 0x08, 0x5e, 0x02, 0x06, 0x03, 0x03)
      ),
      Vec(0x7f000000L, 0x80000000L, 0L),
      0x80L
    ),
    (
      "v_sub_f16_sdwa v4, v2, v3 dst_sel:WORD_1 dst_unused:UNUSED_PAD src0_sel:DWORD src1_sel:DWORD",
      Vec(
        Target("gfx803")  -> Vec(0xf9, 0x06, 0x08, 0x40, 0x02, 0x05, 0x06, 0x06),
        Target("gfx906")  -> Vec(0xf9, 0x06, 0x08, 0x40, 0x02, 0x05, 0x06, 0x06),
        Target("gfx1030") -> Vec(0xf9, 0x06, 0x08, 0x66, 0x02, 0x05, 0x06, 0x06)
      ),
      Vec(half(5.0f), half(1.5f), 0xdead1234L),
      half(3.5f) << 16
    ),
    (
      "v_ashrrev_i16_sdwa v4, v2, v3 dst_sel:WORD_0 dst_unused:UNUSED_PRESERVE src0_sel:DWORD src1_sel:WORD_1",
      Vec(
        Target("gfx803") -> Vec(0xf9, 0x06, 0x08, 0x58, 0x02, 0x14, 0x06, 0x05),
        Target("gfx906") -> Vec(0xf9, 0x06, 0x08, 0x58, 0x02, 0x14, 0x06, 0x05)
      ),
      Vec(4L, 0x80000000L, 0xdead1234L),
      0xdeadf800L
    ),
    (
      "v_mul_hi_i32_i24_sdwa v4, sext(v2), v3 dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:WORD_1 src1_sel:DWORD",
      Vec(
        Target("gfx803")  -> Vec(0xf9, 0x06, 0x08, 0x0e, 0x02, 0x06, 0x0d, 0x06),
        Target("gfx906")  -> Vec(0xf9, 0x06, 0x08, 0x0e, 0x02, 0x06, 0x0d, 0x06),
        Target("gfx1030") -> Vec(0xf9, 0x06, 0x08, 0x14, 0x02, 0x06, 0x0d, 0x06)
      ),
      Vec(0xfffe0000L, 0x00400000L, 0L),
      0xffffffffL
    ),
    (
      "v_add_f32_sdwa v4, -v2, |v3| dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:DWORD src1_sel:DWORD",
      Vec(
        Target("gfx803")  -> Vec(0xf9, 0x06, 0x08, 0x02, 0x02, 0x06, 0x16, 0x26),
        Target("gfx906")  -> Vec(0xf9, 0x06, 0x08, 0x02, 0x02, 0x06, 0x16, 0x26),
        Target("gfx1030") -> Vec(0xf9, 0x06, 0x08, 0x06, 0x02, 0x06, 0x16, 0x26)
      ),
      Vec(single(1.5f), single(-4.0f), 0L),
      single(2.5f)
    ),
    (
      "v_add_f32_sdwa v4, v2, v3 clamp dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:DWORD src1_sel:DWORD",
      Vec(
        Target("gfx803")  -> Vec(0xf9, 0x06, 0x08, 0x02, 0x02, 0x26, 0x06, 0x06),
        Target("gfx906")  -> Vec(0xf9, 0x06, 0x08, 0x02, 0x02, 0x26, 0x06, 0x06),
        Target("gfx1030") -> Vec(0xf9, 0x06, 0x08, 0x06, 0x02, 0x26, 0x06, 0x06)
      ),
      Vec(single(0.75f), single(0.5f), 0L),
      single(1.0f)
    ),
    (
      // the kernel's MODE enables IEEE mode, which ignores OMOD
      "v_add_f32_sdwa v4, v2, v3 mul:2 dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:DWORD src1_sel:DWORD",
      Vec(
        Target("gfx906")  -> Vec(0xf9, 0x06, 0x08, 0x02, 0x02, 0x46, 0x06, 0x06),
        Target("gfx1030") -> Vec(0xf9, 0x06, 0x08, 0x06, 0x02, 0x46, 0x06, 0x06)
      ),
      Vec(single(0.75f), single(0.5f), 0L),
      single(1.25f)
    ),
    (
      "v_cvt_f16_f32_sdwa v4, v2 dst_sel:WORD_1 dst_unused:UNUSED_PRESERVE src0_sel:DWORD",
      Vec(
        Target("gfx803")  -> Vec(0xf9, 0x14, 0x08, 0x7e, 0x02, 0x15, 0x06, 0x00),
        Target("gfx906")  -> Vec(0xf9, 0x14, 0x08, 0x7e, 0x02, 0x15, 0x06, 0x00),
        Target("gfx1030") -> Vec(0xf9, 0x14, 0x08, 0x7e, 0x02, 0x15, 0x06, 0x00)
      ),
      Vec(single(2.0f), 0L, 0xdead1234L),
      (half(2.0f) << 16) | 0x1234L
    ),
    (
      "v_mov_b32_sdwa v4, v2 dst_sel:BYTE_2 dst_unused:UNUSED_SEXT src0_sel:BYTE_0",
      Vec(
        Target("gfx803")  -> Vec(0xf9, 0x02, 0x08, 0x7e, 0x02, 0x0a, 0x00, 0x00),
        Target("gfx906")  -> Vec(0xf9, 0x02, 0x08, 0x7e, 0x02, 0x0a, 0x00, 0x00),
        Target("gfx1030") -> Vec(0xf9, 0x02, 0x08, 0x7e, 0x02, 0x0a, 0x00, 0x00)
      ),
      Vec(0x123456f0L, 0L, 0L),
      0xfff00000L
    )
  )

  for {
    (name, encodings, inputs, expected) <- cases
    (target, code)                      <- encodings
  }
    test(s"$target $name") {
      assertEquals(laneResult(target, inputs, program(target, octets(code*)), 4), expected)
    }
}
