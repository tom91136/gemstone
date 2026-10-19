package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** The Southern and Sea Islands encodings and opcodes later generations dropped: the VOP2 forms of operations GFX8
  * encodes only in VOP3, the non-reversed shifts, the DX9 minimum, maximum and multiply-accumulate, the clamped and
  * legacy reciprocals and the signalling comparisons. Expectations follow the Sea Islands ISA (rev 1.0, chapter 12) and
  * for MULLIT the R600 ISA's MUL_LIT; encodings are `llvm-mc -triple=amdgcn -mcpu=gfx601 -show-encoding`, the same
  * bytes on every GFX6 and GFX7 target. Inputs are in v2 onwards and the result in v4 unless named.
  */
class AMDGPULegacySuite extends munit.FunSuite {
  private val targets = Vec(Target("gfx600"), Target("gfx601"), Target("gfx602"), Target("gfx700"), Target("gfx705"))

  private def f(value: Float): Long          = java.lang.Float.floatToRawIntBits(value).toLong & 0xffffffffL
  private def d(value: Double): (Long, Long) = {
    val bits = java.lang.Double.doubleToRawLongBits(value)
    (bits & 0xffffffffL, bits >>> 32)
  }
  private val MaxFloat = 0x7f7fffffL
  private val NaN      = 0x7fc00000L

  private def check(name: String, code: Vec[Int], inputs: Vec[Long], expected: Long, result: Int = 4)(using
      munit.Location
  ): Unit = targets.foreach { target =>
    test(s"$target $name") {
      val found = laneResult(target, inputs, program(target, octets(code*)), result.unsafe[VectorRegisterIndex])
      assertEquals(found, expected, f"$found%x")
    }
  }

  check("v_lshl_b32_e32 shifts SRC0 by VSRC1", Vec(0x02, 0x07, 0x08, 0x32), Vec(1L, 33L), 2L)
  check("v_lshr_b32_e32 shifts SRC0 by VSRC1", Vec(0x02, 0x07, 0x08, 0x2a), Vec(0x80000000L, 31L), 1L)
  check("v_ashr_i32_e32 shifts SRC0 by VSRC1", Vec(0x02, 0x07, 0x08, 0x2e), Vec(0x80000000L, 4L), 0xf8000000L)
  // s_mov_b32 s0, 7; v_lshl_b32_e64 v4, v2, s0
  check(
    "v_lshl_b32_e64 shifts SRC0 by an SGPR",
    Vec(0x87, 0x03, 0x80, 0xbe, 0x04, 0x00, 0x32, 0xd2, 0x02, 0x01, 0x00, 0x00),
    Vec(1L),
    0x80L
  )
  check(
    "v_lshl_b64 v[6:7], v[2:3], v4 low",
    Vec(0x06, 0x00, 0xc2, 0xd2, 0x02, 0x09, 0x02, 0x00),
    Vec(0x80000001L, 0L, 33L),
    0L,
    6
  )
  check(
    "v_lshl_b64 v[6:7], v[2:3], v4 high",
    Vec(0x06, 0x00, 0xc2, 0xd2, 0x02, 0x09, 0x02, 0x00),
    Vec(0x80000001L, 0L, 33L),
    2L,
    7
  )
  check(
    "v_lshr_b64 v[6:7], v[2:3], v4",
    Vec(0x06, 0x00, 0xc4, 0xd2, 0x02, 0x09, 0x02, 0x00),
    Vec(0L, 0x80000000L, 127L),
    1L,
    6
  )
  check(
    "v_ashr_i64 v[6:7], v[2:3], v4 low",
    Vec(0x06, 0x00, 0xc6, 0xd2, 0x02, 0x09, 0x02, 0x00),
    Vec(0L, 0x80000000L, 60L),
    0xfffffff8L,
    6
  )
  check(
    "v_ashr_i64 v[6:7], v[2:3], v4 high",
    Vec(0x06, 0x00, 0xc6, 0xd2, 0x02, 0x09, 0x02, 0x00),
    Vec(0L, 0x80000000L, 60L),
    0xffffffffL,
    7
  )
  check("v_bcnt_u32_b32_e32 adds the count to VSRC1", Vec(0x02, 0x07, 0x08, 0x44), Vec(0xffL, 3L), 11L)
  check("v_bfm_b32_e32 masks SRC0 bits at VSRC1", Vec(0x02, 0x07, 0x08, 0x3c), Vec(4L, 8L), 0xf00L)
  check(
    "v_cvt_pk_i16_i32_e32 saturates both halves",
    Vec(0x02, 0x07, 0x08, 0x62),
    Vec(0x12345L, 0xfffeee90L),
    0x80007fffL
  )
  check("v_cvt_pk_u16_u32_e32 saturates both halves", Vec(0x02, 0x07, 0x08, 0x60), Vec(70000L, 5L), 0x0005ffffL)
  check(
    "v_cvt_pknorm_i16_f32_e32 normalises both halves",
    Vec(0x02, 0x07, 0x08, 0x5a),
    Vec(f(1.0f), f(-1.0f)),
    0x80017fffL
  )
  check(
    "v_cvt_pknorm_u16_f32_e32 normalises both halves",
    Vec(0x02, 0x07, 0x08, 0x5c),
    Vec(f(1.0f), f(0.0f)),
    0x0000ffffL
  )
  check(
    "v_cvt_pkaccum_u8_f32_e32 packs into the byte VSRC1 selects",
    Vec(0x02, 0x07, 0x08, 0x58),
    Vec(f(200.0f), 6L, 0x11223344L),
    0x11c83344L
  )
  check(
    "v_cvt_pkaccum_u8_f32_e64 packs into the byte SRC1 selects",
    Vec(0x04, 0x00, 0x58, 0xd2, 0x02, 0x07, 0x02, 0x00),
    Vec(f(300.0f), 0L, 0x11223344L),
    0x112233ffL
  )
  check(
    "v_mac_legacy_f32_e32 adds a zero product to the destination",
    Vec(0x02, 0x07, 0x08, 0x0c),
    Vec(f(0.0f), 0x7f800000L, f(1.5f)),
    f(1.5f)
  )
  check(
    "v_mac_legacy_f32_e32 accumulates",
    Vec(0x02, 0x07, 0x08, 0x0c),
    Vec(f(2.0f), f(3.0f), f(1.0f)),
    f(7.0f)
  )
  check(
    "v_mac_legacy_f32_e64 clamps",
    Vec(0x04, 0x08, 0x0c, 0xd2, 0x02, 0x07, 0x02, 0x00),
    Vec(f(2.0f), f(3.0f), f(1.0f)),
    f(1.0f)
  )
  check(
    "v_mad_legacy_f32 adds a zero product",
    Vec(0x04, 0x00, 0x80, 0xd2, 0x02, 0x07, 0x12, 0x04),
    Vec(0xff800000L, f(0.0f), f(2.0f)),
    f(2.0f)
  )
  check("v_min_legacy_f32_e32 takes SRC1 for a NaN SRC0", Vec(0x02, 0x07, 0x08, 0x1a), Vec(NaN, f(1.0f)), f(1.0f))
  check("v_min_legacy_f32_e32 takes a NaN SRC1", Vec(0x02, 0x07, 0x08, 0x1a), Vec(f(1.0f), NaN), NaN)
  check("v_min_legacy_f32_e32 takes the smaller", Vec(0x02, 0x07, 0x08, 0x1a), Vec(f(-2.0f), f(1.0f)), f(-2.0f))
  check("v_max_legacy_f32_e32 takes SRC1 for a NaN SRC0", Vec(0x02, 0x07, 0x08, 0x1c), Vec(NaN, f(1.0f)), f(1.0f))
  check("v_max_legacy_f32_e32 takes a NaN SRC1", Vec(0x02, 0x07, 0x08, 0x1c), Vec(f(1.0f), NaN), NaN)
  check(
    "v_min_legacy_f32_e64 negates SRC0",
    Vec(0x04, 0x00, 0x1a, 0xd2, 0x02, 0x07, 0x02, 0x20),
    Vec(f(1.0f), f(0.5f)),
    f(-1.0f)
  )
  check(
    "v_max_legacy_f32_e64 takes the magnitude of SRC0",
    Vec(0x04, 0x01, 0x1c, 0xd2, 0x02, 0x07, 0x02, 0x00),
    Vec(f(-3.0f), f(2.0f)),
    f(3.0f)
  )
  private val mullit = Vec(0x04, 0x00, 0xa0, 0xd2, 0x02, 0x07, 0x12, 0x04)
  check("v_mullit_f32 multiplies", mullit, Vec(f(2.0f), f(3.0f), f(1.0f)), f(6.0f))
  check("v_mullit_f32 multiplies zero by infinity to zero", mullit, Vec(f(0.0f), 0x7f800000L, f(1.0f)), f(0.0f))
  check("v_mullit_f32 gives -max_float for SRC2 at most zero", mullit, Vec(f(2.0f), f(3.0f), f(-0.0f)), 0xff7fffffL)
  check("v_mullit_f32 gives -max_float for a NaN SRC1", mullit, Vec(f(2.0f), NaN, f(1.0f)), 0xff7fffffL)
  check("v_mullit_f32 gives -max_float for SRC1 -infinity", mullit, Vec(f(2.0f), 0xff800000L, f(1.0f)), 0xff7fffffL)
  check("v_mullit_f32 gives -max_float for a NaN SRC2", mullit, Vec(f(2.0f), f(3.0f), NaN), 0xff7fffffL)
  check("v_rcp_clamp_f32 clamps 1/+0 to max_float", Vec(0x02, 0x51, 0x08, 0x7e), Vec(f(0.0f)), MaxFloat)
  check("v_rcp_clamp_f32 clamps 1/-0 to -max_float", Vec(0x02, 0x51, 0x08, 0x7e), Vec(f(-0.0f)), 0xff7fffffL)
  check("v_rcp_clamp_f32 divides", Vec(0x02, 0x51, 0x08, 0x7e), Vec(f(2.0f)), f(0.5f))
  check("v_rcp_legacy_f32 gives +0 for -0", Vec(0x02, 0x53, 0x08, 0x7e), Vec(f(-0.0f)), 0L)
  check("v_rcp_legacy_f32 gives +0 for +0", Vec(0x02, 0x53, 0x08, 0x7e), Vec(f(0.0f)), 0L)
  check("v_rcp_legacy_f32 divides", Vec(0x02, 0x53, 0x08, 0x7e), Vec(f(4.0f)), f(0.25f))
  check(
    "v_rcp_legacy_f32_e64 negates its source",
    Vec(0x04, 0x00, 0x52, 0xd3, 0x02, 0x01, 0x00, 0x20),
    Vec(f(2.0f)),
    f(-0.5f)
  )
  check("v_rsq_clamp_f32 clamps 1/sqrt(+0) to max_float", Vec(0x02, 0x59, 0x08, 0x7e), Vec(f(0.0f)), MaxFloat)
  check("v_rsq_clamp_f32 clamps 1/sqrt(-0) to -max_float", Vec(0x02, 0x59, 0x08, 0x7e), Vec(f(-0.0f)), 0xff7fffffL)
  check("v_rsq_clamp_f32 takes the root", Vec(0x02, 0x59, 0x08, 0x7e), Vec(f(4.0f)), f(0.5f))
  check("v_rsq_legacy_f32 gives +0 for -0", Vec(0x02, 0x5b, 0x08, 0x7e), Vec(f(-0.0f)), 0L)
  check("v_rsq_legacy_f32 takes the root", Vec(0x02, 0x5b, 0x08, 0x7e), Vec(f(16.0f)), f(0.25f))
  check("v_log_clamp_f32 clamps log2(0) to -max_float", Vec(0x02, 0x4d, 0x08, 0x7e), Vec(f(0.0f)), 0xff7fffffL)
  check("v_log_clamp_f32 takes the logarithm", Vec(0x02, 0x4d, 0x08, 0x7e), Vec(f(8.0f)), f(3.0f))
  check(
    "v_rcp_clamp_f64 clamps 1/+0 to max_double",
    Vec(0x02, 0x61, 0x0c, 0x7e),
    Vec(0L, 0L),
    0x7fefffffL,
    7
  )
  check("v_rcp_clamp_f64 divides", Vec(0x02, 0x61, 0x0c, 0x7e), Vec(d(4.0)._1, d(4.0)._2), d(0.25)._2, 7)
  check(
    "v_rsq_clamp_f64 clamps 1/sqrt(-0) to -max_double",
    Vec(0x02, 0x65, 0x0c, 0x7e),
    Vec(0L, 0x80000000L),
    0xffefffffL,
    7
  )
  check("v_rsq_clamp_f64 takes the root", Vec(0x02, 0x65, 0x0c, 0x7e), Vec(d(4.0)._1, d(4.0)._2), d(0.5)._2, 7)

  // v_cmps_lt_f32_e32 vcc, v2, v3; v_cndmask_b32_e64 v4, 0, 1, vcc
  private val signallingLess = Vec(0x02, 0x07, 0x82, 0x7c, 0x04, 0x00, 0x00, 0xd2, 0x80, 0x02, 0xa9, 0x01)
  check("v_cmps_lt_f32_e32 compares", signallingLess, Vec(f(1.0f), f(2.0f)), 1L)
  check("v_cmps_lt_f32_e32 is false on NaN", signallingLess, Vec(NaN, f(2.0f)), 0L)
  // v_cmpsx_nge_f32_e32 vcc, v2, v3; v_cndmask_b32_e64 v4, 0, 1, vcc
  check(
    "v_cmpsx_nge_f32_e32 compares and keeps the lanes it passes",
    Vec(0x02, 0x07, 0xb2, 0x7c, 0x04, 0x00, 0x00, 0xd2, 0x80, 0x02, 0xa9, 0x01),
    Vec(f(1.0f), f(2.0f)),
    1L
  )
  // v_cmps_lt_f32_e64 s[0:1], v2, v3; v_cndmask_b32_e64 v4, 0, 1, s[0:1]
  check(
    "v_cmps_lt_f32_e64 writes an SGPR pair",
    Vec(0x00, 0x00, 0x82, 0xd0, 0x02, 0x07, 0x02, 0x00, 0x04, 0x00, 0x00, 0xd2, 0x80, 0x02, 0x01, 0x00),
    Vec(f(-1.0f), f(2.0f)),
    1L
  )
  // v_cmps_eq_f64_e32 vcc, v[2:3], v[4:5]; v_cndmask_b32_e64 v4, 0, 1, vcc
  check(
    "v_cmps_eq_f64_e32 compares pairs",
    Vec(0x02, 0x09, 0xc4, 0x7c, 0x04, 0x00, 0x00, 0xd2, 0x80, 0x02, 0xa9, 0x01),
    Vec(d(1.5)._1, d(1.5)._2, d(1.5)._1, d(1.5)._2),
    1L
  )

  /** One lane with v2 to v7 from `inputs`, LDS address 0 holding v4, 4 holding v6 and 8 holding v7, then `code`; the
    * dword at LDS address `read` afterwards.
    */
  private def local(target: Target, code: Vec[Int], inputs: Vec[Long], read: Int)(using munit.Location): Long =
    laneResults(
      target,
      Vec(inputs),
      Vec(
        Instruction.Store(4, MemoryAddress.Local(1, 0), 4, high = false),
        Instruction.Store(6, MemoryAddress.Local(1, 4), 4, high = false),
        Instruction.Store(7, MemoryAddress.Local(1, 8), 4, high = false),
        Instruction.VMove(1, InstructionSource.Constant(0L))
      ) ++ program(target, octets(code*)) :+
        Instruction.Load(8, MemoryAddress.Local(1, read), 4, signed = false, LoadPlacement.Full),
      8,
      groupSegmentSize = 64
    ).head

  // DS_*_SRC2 update the dword at ADDR (v3) from the one the offset fields' signed dword displacement finds
  private val source2 = Vec(
    (
      "ds_add_src2_u32 v3 offset:1 adds",
      Vec(0x01, 0x00, 0x00, 0xda, 0x03, 0x00, 0x00, 0x00),
      Vec(0L, 0L, 5L, 0L, 7L, 0L),
      0,
      12L
    ),
    (
      "ds_rsub_src2_u32 v3 offset:2 subtracts from the other",
      Vec(0x02, 0x00, 0x08, 0xda, 0x03, 0x00, 0x00, 0x00),
      Vec(0L, 0L, 5L, 0L, 0L, 20L),
      0,
      15L
    ),
    (
      "ds_write_src2_b32 v3 offset:2 copies",
      Vec(0x02, 0x00, 0x34, 0xda, 0x03, 0x00, 0x00, 0x00),
      Vec(0L, 0L, 5L, 0L, 0L, 20L),
      0,
      20L
    ),
    (
      "ds_max_src2_f32 v3 offset:1 takes the larger",
      Vec(0x01, 0x00, 0x4c, 0xda, 0x03, 0x00, 0x00, 0x00),
      Vec(0L, 0L, f(1.0f), 0L, f(2.5f), 0L),
      0,
      f(2.5f)
    ),
    (
      "ds_sub_src2_u32 v3 offset:32766 reaches two dwords back",
      Vec(0xfe, 0x7f, 0x04, 0xda, 0x03, 0x00, 0x00, 0x00),
      Vec(0L, 8L, 3L, 0L, 0L, 10L),
      8,
      7L
    ),
    (
      "ds_inc_src2_u32 v3 offset:1 wraps at the other",
      Vec(0x01, 0x00, 0x0c, 0xda, 0x03, 0x00, 0x00, 0x00),
      Vec(0L, 0L, 5L, 0L, 5L, 0L),
      0,
      0L
    )
  )

  for {
    target                               <- targets
    (name, code, inputs, read, expected) <- source2
  }
    test(s"$target $name") {
      assertEquals(local(target, code, inputs, read), expected)
    }

  // s_mov_b32 s0, s6; s_mov_b32 s1, s7; s_mov_b32 s2, 0; s_mov_b32 s3, 0; v_mov_b32_e32 v4, v1; v_mov_b32_e32 v5, 0
  // address each lane's inputs from a descriptor based at s[6:7] with no records, which ADDR64 does not check
  private val wide = Vec(
    0x06, 0x03, 0x80, 0xbe, 0x07, 0x03, 0x81, 0xbe, 0x80, 0x03, 0x82, 0xbe, 0x80, 0x03, 0x83, 0xbe,
    0x01, 0x03, 0x08, 0x7e, 0x80, 0x02, 0x0a, 0x7e
  )
  check(
    "buffer_load_dword v2, v[4:5], s[0:3], 0 addr64 offset:4",
    wide ++ Vec(0x04, 0x80, 0x30, 0xe0, 0x04, 0x02, 0x00, 0x80),
    Vec(10L, 20L),
    20L,
    2
  )
  // v_mov_b32_e32 v6, 25; then buffer_atomic_rsub v6, v[4:5], s[0:3], 0 addr64 glc, which LLVM does not assemble, by
  // the Sea Islands MUBUF layout with opcode 52; then buffer_load_dword v7, v[4:5], s[0:3], 0 addr64
  private val reverse =
    wide ++ Vec(0x99, 0x02, 0x0c, 0x7e, 0x00, 0xc0, 0xd0, 0xe0, 0x04, 0x06, 0x00, 0x80) ++
      Vec(0x00, 0x80, 0x30, 0xe0, 0x04, 0x07, 0x00, 0x80)
  for (target <- Vec(Target("gfx600"), Target("gfx601"), Target("gfx602")))
    test(s"$target buffer_atomic_rsub returns the old value and stores the data less it") {
      val code = program(target, octets(reverse*))
      assertEquals(laneResult(target, Vec(10L), code, 6), 10L)
      assertEquals(laneResult(target, Vec(10L), code, 7), 15L)
    }

  for (target <- targets) {
    test(s"$target v_mbcnt_lo_u32_b32_e32 and v_mbcnt_hi_u32_b32_e32 count the lanes below") {
      // v_mbcnt_lo_u32_b32_e32 v4, v2, v3; v_mbcnt_hi_u32_b32_e32 v4, v2, v4
      val code = octets(0x02, 0x07, 0x08, 0x46, 0x02, 0x09, 0x08, 0x48)
      assertEquals(
        laneResults(target, Vec.fill(4)(Vec(0xffffffffL, 5L)), program(target, code), 4, wave32 = false),
        Vec(5L, 6L, 7L, 8L)
      )
    }
    test(s"$target v_readlane_b32 reads the lane an SGPR names") {
      // s_mov_b32 s1, 2; v_readlane_b32 s0, v2, s1; v_mov_b32_e32 v4, s0
      val code = octets(0x82, 0x03, 0x81, 0xbe, 0x02, 0x03, 0x00, 0x02, 0x00, 0x02, 0x08, 0x7e)
      assertEquals(
        laneResults(target, Vec.tabulate(4)(lane => Vec(10L + lane)), program(target, code), 4, wave32 = false),
        Vec.fill(4)(12L)
      )
    }
    test(s"$target v_writelane_b32 writes the lane a constant names") {
      // s_mov_b32 s0, 7; v_writelane_b32 v4, s0, 1
      val code = octets(0x87, 0x03, 0x80, 0xbe, 0x00, 0x02, 0x09, 0x04)
      assertEquals(
        laneResults(target, Vec.fill(4)(Vec(0L, 0L, 100L)), program(target, code), 4, wave32 = false),
        Vec(100L, 7L, 100L, 100L)
      )
    }
  }

  // legacy.cl, from clang -x cl -cl-std=CL1.2 -target amdgcn-amd-amdhsa -mcpu=<target> -nogpulib -O2 -S -emit-llvm
  // then llc -mtriple=amdgcn-amd-amdhsa -mcpu=<target> -O2 -show-mc-encoding (ROCm 7.1.1):
  //   kernel void legacy(global uint *out, global const ulong *in, global const float *f) {
  //     uint i = __builtin_amdgcn_workgroup_id_x() * 64 + __builtin_amdgcn_workitem_id_x();
  //     ulong v = in[i];
  //     uint s = (uint)(v >> 58);
  //     float a = f[2 * i], b = f[2 * i + 1];
  //     out[4 * i] = (uint)(v << s) ^ (uint)((v >> s) >> 32);
  //     out[4 * i + 1] = (uint)((long)v >> s);
  //     out[4 * i + 2] = as_uint(a < b ? a : b);
  //     out[4 * i + 3] = __builtin_amdgcn_readlane((int)v, 3) +
  //       __builtin_amdgcn_mbcnt_hi(~0u, __builtin_amdgcn_mbcnt_lo(~0u, 0u));
  //   }
  // The descriptor's COMPUTE_PGM_RSRC1 is 0x00ac0042, RSRC2 0x8c and the kernel code properties 0x9.
  private val legacyGfx600 = octets(
    0x00, 0x05, 0x80, 0xc0,                         // s_load_dwordx4 s[0:3], s[4:5], 0x0
    0x04, 0x05, 0x42, 0xc0,                         // s_load_dwordx2 s[4:5], s[4:5], 0x4
    0x06, 0x86, 0x06, 0x8f,                         // s_lshl_b32 s6, s6, 6
    0x06, 0x00, 0x00, 0x4a,                         // v_add_i32_e32 v0, vcc, s6, v0
    0x80, 0x02, 0x02, 0x7e,                         // v_mov_b32_e32 v1, 0
    0x02, 0x00, 0xc2, 0xd2, 0x00, 0x07, 0x01, 0x00, // v_lshl_b64 v[2:3], v[0:1], 3
    0x80, 0x03, 0x8a, 0xbe,                         // s_mov_b32 s10, 0
    0xff, 0x03, 0x8b, 0xbe, 0x00, 0xf0, 0x00, 0x01, // s_mov_b32 s11, 0x100f000
    0x7f, 0x00, 0x8c, 0xbf,                         // s_waitcnt lgkmcnt(0)
    0x02, 0x04, 0x88, 0xbe,                         // s_mov_b64 s[8:9], s[2:3]
    0x00, 0x80, 0x34, 0xe0, 0x02, 0x02, 0x02, 0x80, // buffer_load_dwordx2 v[2:3], v[2:3], s[8:11], 0 addr64
    0x81, 0x00, 0x08, 0x34,                         // v_lshlrev_b32_e32 v4, 1, v0
    0x01, 0x03, 0x0a, 0x7e,                         // v_mov_b32_e32 v5, v1
    0x04, 0x00, 0xc2, 0xd2, 0x04, 0x05, 0x01, 0x00, // v_lshl_b64 v[4:5], v[4:5], 2
    0x0a, 0x04, 0x86, 0xbe,                         // s_mov_b64 s[6:7], s[10:11]
    0x00, 0x80, 0x34, 0xe0, 0x04, 0x04, 0x01, 0x80, // buffer_load_dwordx2 v[4:5], v[4:5], s[4:7], 0 addr64
    0x06, 0x00, 0x46, 0xd2, 0xc1, 0x00, 0x01, 0x00, // v_mbcnt_lo_u32_b32_e64 v6, -1, 0
    0x82, 0x00, 0x00, 0x34,                         // v_lshlrev_b32_e32 v0, 2, v0
    0xc1, 0x0c, 0x14, 0x48,                         // v_mbcnt_hi_u32_b32_e32 v10, -1, v6
    0x06, 0x00, 0xc2, 0xd2, 0x00, 0x05, 0x01, 0x00, // v_lshl_b64 v[6:7], v[0:1], 2
    0x0a, 0x04, 0x82, 0xbe,                         // s_mov_b64 s[2:3], s[10:11]
    0x71, 0x0f, 0x8c, 0xbf,                         // s_waitcnt vmcnt(1)
    0x9a, 0x06, 0x16, 0x2c,                         // v_lshrrev_b32_e32 v11, 26, v3
    0x00, 0x00, 0xc2, 0xd2, 0x02, 0x17, 0x02, 0x00, // v_lshl_b64 v[0:1], v[2:3], v11
    0x08, 0x00, 0xc4, 0xd2, 0x02, 0x17, 0x02, 0x00, // v_lshr_b64 v[8:9], v[2:3], v11
    0x02, 0x07, 0x09, 0x02,                         // v_readlane_b32 s4, v2, 3
    0x01, 0x00, 0xc6, 0xd2, 0x02, 0x17, 0x02, 0x00, // v_ashr_i64 v[1:2], v[2:3], v11
    0x70, 0x0f, 0x8c, 0xbf,                         // s_waitcnt vmcnt(0)
    0x04, 0x0b, 0x04, 0x1a,                         // v_min_legacy_f32_e32 v2, v4, v5
    0x09, 0x01, 0x00, 0x3a,                         // v_xor_b32_e32 v0, v9, v0
    0x04, 0x14, 0x06, 0x4a,                         // v_add_i32_e32 v3, vcc, s4, v10
    0x00, 0x80, 0x78, 0xe0, 0x06, 0x00, 0x00, 0x80, // buffer_store_dwordx4 v[0:3], v[6:7], s[0:3], 0 addr64
    0x00, 0x00, 0x81, 0xbf                          // s_endpgm
  )
  private val legacyGfx700 = octets(
    0x00, 0x05, 0x80, 0xc0,                         // s_load_dwordx4 s[0:3], s[4:5], 0x0
    0x04, 0x05, 0x42, 0xc0,                         // s_load_dwordx2 s[4:5], s[4:5], 0x4
    0x06, 0x86, 0x06, 0x8f,                         // s_lshl_b32 s6, s6, 6
    0x06, 0x00, 0x00, 0x4a,                         // v_add_i32_e32 v0, vcc, s6, v0
    0x80, 0x02, 0x02, 0x7e,                         // v_mov_b32_e32 v1, 0
    0x02, 0x00, 0xc2, 0xd2, 0x00, 0x07, 0x01, 0x00, // v_lshl_b64 v[2:3], v[0:1], 3
    0x7f, 0x00, 0x8c, 0xbf,                         // s_waitcnt lgkmcnt(0)
    0x03, 0x02, 0x08, 0x7e,                         // v_mov_b32_e32 v4, s3
    0x02, 0x04, 0x04, 0x4a,                         // v_add_i32_e32 v2, vcc, s2, v2
    0x04, 0x07, 0x06, 0x50,                         // v_addc_u32_e32 v3, vcc, v4, v3, vcc
    0x81, 0x00, 0x08, 0x34,                         // v_lshlrev_b32_e32 v4, 1, v0
    0x01, 0x03, 0x0a, 0x7e,                         // v_mov_b32_e32 v5, v1
    0x04, 0x00, 0xc2, 0xd2, 0x04, 0x05, 0x01, 0x00, // v_lshl_b64 v[4:5], v[4:5], 2
    0x00, 0x00, 0x34, 0xdc, 0x02, 0x00, 0x00, 0x02, // flat_load_dwordx2 v[2:3], v[2:3]
    0x05, 0x02, 0x0c, 0x7e,                         // v_mov_b32_e32 v6, s5
    0x04, 0x08, 0x08, 0x4a,                         // v_add_i32_e32 v4, vcc, s4, v4
    0x06, 0x0b, 0x0a, 0x50,                         // v_addc_u32_e32 v5, vcc, v6, v5, vcc
    0x00, 0x00, 0x34, 0xdc, 0x04, 0x00, 0x00, 0x04, // flat_load_dwordx2 v[4:5], v[4:5]
    0x82, 0x00, 0x00, 0x34,                         // v_lshlrev_b32_e32 v0, 2, v0
    0x00, 0x00, 0xc2, 0xd2, 0x00, 0x05, 0x01, 0x00, // v_lshl_b64 v[0:1], v[0:1], 2
    0x06, 0x00, 0x46, 0xd2, 0xc1, 0x00, 0x01, 0x00, // v_mbcnt_lo_u32_b32_e64 v6, -1, 0
    0xc1, 0x0c, 0x14, 0x48,                         // v_mbcnt_hi_u32_b32_e32 v10, -1, v6
    0x01, 0x02, 0x0e, 0x7e,                         // v_mov_b32_e32 v7, s1
    0x00, 0x00, 0x0c, 0x4a,                         // v_add_i32_e32 v6, vcc, s0, v0
    0x07, 0x03, 0x0e, 0x50,                         // v_addc_u32_e32 v7, vcc, v7, v1, vcc
    0x71, 0x0f, 0x8c, 0xbf,                         // s_waitcnt vmcnt(1)
    0x9a, 0x06, 0x16, 0x2c,                         // v_lshrrev_b32_e32 v11, 26, v3
    0x00, 0x00, 0xc2, 0xd2, 0x02, 0x17, 0x02, 0x00, // v_lshl_b64 v[0:1], v[2:3], v11
    0x08, 0x00, 0xc4, 0xd2, 0x02, 0x17, 0x02, 0x00, // v_lshr_b64 v[8:9], v[2:3], v11
    0x02, 0x07, 0x01, 0x02,                         // v_readlane_b32 s0, v2, 3
    0x01, 0x00, 0xc6, 0xd2, 0x02, 0x17, 0x02, 0x00, // v_ashr_i64 v[1:2], v[2:3], v11
    0x09, 0x01, 0x00, 0x3a,                         // v_xor_b32_e32 v0, v9, v0
    0x70, 0x0f, 0x8c, 0xbf,                         // s_waitcnt vmcnt(0)
    0x04, 0x0b, 0x04, 0x1a,                         // v_min_legacy_f32_e32 v2, v4, v5
    0x00, 0x14, 0x06, 0x4a,                         // v_add_i32_e32 v3, vcc, s0, v10
    0x00, 0x00, 0x78, 0xdc, 0x06, 0x00, 0x00, 0x00, // flat_store_dwordx4 v[6:7], v[0:3]
    0x00, 0x00, 0x81, 0xbf                          // s_endpgm
  )

  for ((target, code) <- Vec(Target("gfx600") -> legacyGfx600, Target("gfx700") -> legacyGfx700))
    test(s"$target runs the llc kernel of 64-bit shifts, readlane, mbcnt and the DX9 minimum") {
      val lanes  = 64
      val values = Vec.tabulate(lanes)(lane => ((lane.toLong % 63) << 58) | (0x0123456789abcdefL * (lane + 1) >>> 6))
      val floats = Vec.tabulate(lanes)(lane =>
        (lane % 4) match {
          case 0 => (lane.toFloat, -lane.toFloat)
          case 1 => (Float.NaN, 1.0f)
          case 2 => (2.0f, Float.NaN)
          case _ => (-0.0f, 0.0f)
        }
      )
      val memory              = RegionMemory()
      val (out, input, float) = (AllocationId(1), AllocationId(2), AllocationId(3))
      memory.allocate(out, lanes * 16)
      memory.allocate(input, lanes * 8)
      memory.allocate(float, lanes * 8)
      values.zipWithIndex.foreach { (value, lane) =>
        memory.putInt(input, (lane * 8).unsafe[Int >= 0], value.toInt)
        memory.putInt(input, (lane * 8 + 4).unsafe[Int >= 0], (value >>> 32).toInt)
      }
      floats.zipWithIndex.foreach { case ((a, b), lane) =>
        memory.putInt(float, (lane * 8).unsafe[Int >= 0], java.lang.Float.floatToRawIntBits(a))
        memory.putInt(float, (lane * 8 + 4).unsafe[Int >= 0], java.lang.Float.floatToRawIntBits(b))
      }
      val kernel = codeKernel(
        target = target,
        kernargSize = 24,
        computePgmRsrc1 = 0x00ac0042,
        computePgmRsrc2 = 0x8c,
        kernelCodeProperties = 0x9,
        code = code
      )
      val task = KernelTask.validated(
        "legacy",
        Dimensions(lanes.unsafe[Int > 0], 1, 1),
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(out, 0L),
          KernelTask.Argument.Memory(input, 0L),
          KernelTask.Argument.Memory(float, 0L)
        )
      ).value
      assertEquals(executeKernel(compileKernel(kernel), task, memory), Right(()))
      for (lane <- 0 until lanes) {
        val v                    = values(lane)
        val shift                = (v >>> 58).toInt
        val (a, b)               = floats(lane)
        val minimum              = if (a < b) a else b
        def at(index: Int): Long = memory.getInt(out, (lane * 16 + index * 4).unsafe[Int >= 0]).toLong & 0xffffffffL
        assertEquals(at(0), ((v << shift) ^ ((v >>> shift) >>> 32)) & 0xffffffffL, s"lane $lane")
        assertEquals(at(1), (v >> shift) & 0xffffffffL, s"lane $lane")
        assertEquals(at(2), f(minimum), s"lane $lane")
        assertEquals(at(3), ((values(3) & 0xffffffffL) + lane) & 0xffffffffL, s"lane $lane")
      }
    }
}
