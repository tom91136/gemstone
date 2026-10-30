package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.either.*

/** GFX12.5 vector instructions that extend GFX12 ones. The encodings are LLVM 22 `llvm-mc -triple=amdgcn -mcpu=gfx1250
  * -show-encoding`; the semantics follow LLVM's `VOP3Instructions.td`, `VOP3PInstructions.td` and
  * `IntrinsicsAMDGPU.td`, whose selection patterns map `llvm.sadd.sat`/`llvm.uadd.sat` feeding `llvm.smax`/`llvm.umin`
  * to the add-min and add-max instructions without the clamp bit, and `llvm/test/CodeGen/AMDGPU/add-max.ll`.
  */
class AMDGPUGfx1250Suite extends Suite {
  private val gfx1250 = Target("gfx1250")

  private val encodings: Map[String, Vec[Int]] = Map(
    "v_add_max_i32 v5, v2, v3, v4"                       -> Vec(0x05, 0x00, 0x5e, 0xd6, 0x02, 0x07, 0x12, 0x04),
    "v_add_max_u32 v5, v2, v3, v4"                       -> Vec(0x05, 0x00, 0x5f, 0xd6, 0x02, 0x07, 0x12, 0x04),
    "v_add_min_i32 v5, v2, v3, v4"                       -> Vec(0x05, 0x00, 0x60, 0xd6, 0x02, 0x07, 0x12, 0x04),
    "v_add_min_u32 v5, v2, v3, v4"                       -> Vec(0x05, 0x00, 0x61, 0xd6, 0x02, 0x07, 0x12, 0x04),
    "v_mad_u32 v5, v2, v3, v4"                           -> Vec(0x05, 0x00, 0x35, 0xd6, 0x02, 0x07, 0x12, 0x04),
    "v_pk_add_max_i16 v5, v2, v3, v4"                    -> Vec(0x05, 0x40, 0x14, 0xcc, 0x02, 0x07, 0x12, 0x1c),
    "v_pk_add_max_u16 v5, v2, v3, v4"                    -> Vec(0x05, 0x40, 0x15, 0xcc, 0x02, 0x07, 0x12, 0x1c),
    "v_pk_add_min_i16 v5, v2, v3, v4"                    -> Vec(0x05, 0x40, 0x2d, 0xcc, 0x02, 0x07, 0x12, 0x1c),
    "v_pk_add_min_u16 v5, v2, v3, v4"                    -> Vec(0x05, 0x40, 0x2e, 0xcc, 0x02, 0x07, 0x12, 0x1c),
    "v_pk_max3_i16 v5, v2, v3, v4"                       -> Vec(0x05, 0x40, 0x2f, 0xcc, 0x02, 0x07, 0x12, 0x1c),
    "v_pk_max3_u16 v5, v2, v3, v4"                       -> Vec(0x05, 0x40, 0x30, 0xcc, 0x02, 0x07, 0x12, 0x1c),
    "v_pk_min3_i16 v5, v2, v3, v4"                       -> Vec(0x05, 0x40, 0x31, 0xcc, 0x02, 0x07, 0x12, 0x1c),
    "v_pk_min3_u16 v5, v2, v3, v4"                       -> Vec(0x05, 0x40, 0x32, 0xcc, 0x02, 0x07, 0x12, 0x1c),
    "v_pk_min3_num_f16 v5, v2, v3, v4"                   -> Vec(0x05, 0x40, 0x38, 0xcc, 0x02, 0x07, 0x12, 0x1c),
    "v_pk_max3_num_f16 v5, v2, v3, v4"                   -> Vec(0x05, 0x40, 0x39, 0xcc, 0x02, 0x07, 0x12, 0x1c),
    "v_add_nc_u64 v[6:7], v[2:3], v[4:5]"                -> Vec(0x02, 0x09, 0x0c, 0x50),
    "v_add_nc_u64_e64 v[6:7], v[2:3], v[4:5]"            -> Vec(0x06, 0x00, 0x28, 0xd5, 0x02, 0x09, 0x02, 0x00),
    "v_sub_nc_u64 v[6:7], v[2:3], v[4:5]"                -> Vec(0x02, 0x09, 0x0c, 0x52),
    "v_mul_u64 v[6:7], v[2:3], v[4:5]"                   -> Vec(0x02, 0x09, 0x0c, 0x54),
    "v_max_i64 v[6:7], v[2:3], v[4:5]"                   -> Vec(0x06, 0x00, 0x1b, 0xd7, 0x02, 0x09, 0x02, 0x00),
    "v_max_u64 v[6:7], v[2:3], v[4:5]"                   -> Vec(0x06, 0x00, 0x19, 0xd7, 0x02, 0x09, 0x02, 0x00),
    "v_min_i64 v[6:7], v[2:3], v[4:5]"                   -> Vec(0x06, 0x00, 0x1a, 0xd7, 0x02, 0x09, 0x02, 0x00),
    "v_min_u64 v[6:7], v[2:3], v[4:5]"                   -> Vec(0x06, 0x00, 0x18, 0xd7, 0x02, 0x09, 0x02, 0x00),
    "v_mad_nc_u64_u32 v[6:7], v2, v3, v[4:5]"            -> Vec(0x06, 0x00, 0xfa, 0xd6, 0x02, 0x07, 0x12, 0x04),
    "v_mad_nc_i64_i32 v[6:7], v2, v3, v[4:5]"            -> Vec(0x06, 0x00, 0xfb, 0xd6, 0x02, 0x07, 0x12, 0x04),
    "v_sat_pk4_i4_i8 v5, v2"                             -> Vec(0x02, 0xe7, 0x0a, 0x7e),
    "v_sat_pk4_u4_u8 v5, v2"                             -> Vec(0x02, 0xe9, 0x0a, 0x7e),
    "s_set_pc_i64 s[2:3]"                                -> Vec(0x02, 0x48, 0x80, 0xbe),
    "s_swap_pc_i64 s[4:5], s[2:3]"                       -> Vec(0x02, 0x49, 0x84, 0xbe),
    "s_get_pc_i64 s[4:5]"                                -> Vec(0x00, 0x47, 0x84, 0xbe),
    "s_call_i64 s[4:5], 8"                               -> Vec(0x08, 0x00, 0x04, 0xba),
    "v_cvt_pk_fp8_f16 v5, v2"                            -> Vec(0x05, 0x00, 0x72, 0xd7, 0x02, 0x01, 0x01, 0x02),
    "v_cvt_pk_bf8_f16 v5, v2"                            -> Vec(0x05, 0x00, 0x73, 0xd7, 0x02, 0x01, 0x01, 0x02),
    "v_cvt_f16_fp8 v5.l, v2"                             -> Vec(0x02, 0xef, 0x0a, 0x7e),
    "v_cvt_f16_bf8 v5.l, v2"                             -> Vec(0x02, 0xf1, 0x0a, 0x7e),
    "v_cvt_pk_f16_fp8 v5, v2.l"                          -> Vec(0x02, 0xeb, 0x0a, 0x7e),
    "v_cvt_pk_f16_bf8 v5, v2.l"                          -> Vec(0x02, 0xed, 0x0a, 0x7e),
    "v_cvt_f16_fp8_e64 v5.l, v2"                         -> Vec(0x05, 0x00, 0xf7, 0xd5, 0x02, 0x01, 0x00, 0x00),
    "v_cvt_f16_fp8_e64 v5.l, v2 byte_sel:1"              -> Vec(0x05, 0x10, 0xf7, 0xd5, 0x02, 0x01, 0x00, 0x00),
    "v_cvt_f16_fp8_e64 v5.l, v2 byte_sel:2"              -> Vec(0x05, 0x08, 0xf7, 0xd5, 0x02, 0x01, 0x00, 0x00),
    "v_cvt_f16_fp8_e64 v5.l, v2 byte_sel:3"              -> Vec(0x05, 0x18, 0xf7, 0xd5, 0x02, 0x01, 0x00, 0x00),
    "v_cvt_f16_fp8_e64 v5.h, v2 op_sel:[0,1]"            -> Vec(0x05, 0x40, 0xf7, 0xd5, 0x02, 0x01, 0x00, 0x00),
    "v_cvt_f16_bf8_e64 v5.l, v2"                         -> Vec(0x05, 0x00, 0xf8, 0xd5, 0x02, 0x01, 0x00, 0x00),
    "v_cvt_f16_bf8_e64 v5.l, v2 byte_sel:2"              -> Vec(0x05, 0x08, 0xf8, 0xd5, 0x02, 0x01, 0x00, 0x00),
    "v_cvt_f16_bf8_e64 v5.l, v2 byte_sel:3"              -> Vec(0x05, 0x18, 0xf8, 0xd5, 0x02, 0x01, 0x00, 0x00),
    "v_cvt_pk_f16_fp8_e64 v5, v2"                        -> Vec(0x05, 0x00, 0xf5, 0xd5, 0x02, 0x01, 0x00, 0x00),
    "v_cvt_pk_f16_bf8_e64 v5, v2"                        -> Vec(0x05, 0x00, 0xf6, 0xd5, 0x02, 0x01, 0x00, 0x00),
    "v_cvt_f16_fp8_e64 v5.h, v2 op_sel:[0,1] byte_sel:1" ->
      Vec(0x05, 0x50, 0xf7, 0xd5, 0x02, 0x01, 0x00, 0x00),
    "v_cvt_pk_f16_fp8_e64 v5, v2 op_sel:[1,0]" ->
      Vec(0x05, 0x08, 0xf5, 0xd5, 0x02, 0x01, 0x00, 0x00),
    "v_cvt_pk_f16_bf8_e64 v5, v2 op_sel:[1,0]" ->
      Vec(0x05, 0x08, 0xf6, 0xd5, 0x02, 0x01, 0x00, 0x00),
    "v_cvt_f16_fp8_dpp v5.l, v2 quad_perm:[1,0,3,2] row_mask:0xf bank_mask:0xf" ->
      Vec(0xfa, 0xee, 0x0a, 0x7e, 0x02, 0xb1, 0x00, 0xff),
    "v_cvt_f16_bf8_dpp v5.l, v2 dpp8:[7,6,5,4,3,2,1,0]" ->
      Vec(0xe9, 0xf0, 0x0a, 0x7e, 0x02, 0x77, 0x39, 0x05),
    "v_cvt_pk_f16_fp8_dpp v5, v2.l quad_perm:[0,1,2,3] row_mask:0xf bank_mask:0xf" ->
      Vec(0xfa, 0xea, 0x0a, 0x7e, 0x02, 0xe4, 0x00, 0xff),
    "v_cvt_f16_fp8_e64_dpp v5.l, v2 byte_sel:1 quad_perm:[1,2,3,0] row_mask:0xf bank_mask:0xf" ->
      Vec(0x05, 0x10, 0xf7, 0xd5, 0xfa, 0x00, 0x00, 0x00, 0x02, 0x39, 0x00, 0xff),
    "v_cvt_f16_bf8_e64_dpp v5.l, v2 byte_sel:3 dpp8:[7,6,5,4,3,2,1,0]" ->
      Vec(0x05, 0x18, 0xf8, 0xd5, 0xe9, 0x00, 0x00, 0x00, 0x02, 0x77, 0x39, 0x05),
    "s_wait_xcnt 0x0"                                                     -> Vec(0x00, 0x00, 0xc5, 0xbf),
    "s_setprio_inc_wg 1"                                                  -> Vec(0x01, 0x00, 0xbe, 0xbf),
    "v_bitop3_b32_e64_dpp v5, v2, v3, v4 bitop3:0x96 quad_perm:[1,0,3,2]" ->
      Vec(0x05, 0x02, 0x34, 0xd6, 0xfa, 0x06, 0x12, 0xd4, 0x02, 0xb1, 0x00, 0xff)
  )

  private def code(text: String): Vec[Byte] = Vec.from(encodings(text).map(_.toByte))

  private def run(text: String, inputs: Vec[Long], result: Int = 5)(using munit.Location): Long =
    laneResults(gfx1250, Vec(inputs), program(gfx1250, code(text)), result.unsafe[VectorRegisterIndex]).head

  private def run64(text: String, inputs: Vec[Long])(using munit.Location): Long =
    (run(text, inputs, 7) << 32) | run(text, inputs, 6)

  private val mode = TargetMode(gfx1250, 32)

  test("the assembler writes what decodes as LLVM's bytes") {
    val bad = encodings.keys.filter(text =>
      decoded(gfx1250, asm.Assembler.encode(asm.Parser.parse(text).value, mode).value) != decoded(gfx1250, code(text))
    )
    assertEquals(bad.toSet, Set.empty[String])
  }

  test("add-min and add-max saturate the sum at 32 bits before they compare it with the third operand") {
    assertEquals(run("v_add_max_i32 v5, v2, v3, v4", Vec(0x7fffffffL, 1L, 5L)), 0x7fffffffL)
    assertEquals(run("v_add_max_i32 v5, v2, v3, v4", Vec(0xfffffffdL, 1L, 0xffffffffL)), 0xffffffffL)
    assertEquals(run("v_add_max_i32 v5, v2, v3, v4", Vec(0x80000000L, 0xffffffffL, 0xfffffff0L)), 0xfffffff0L)
    assertEquals(run("v_add_min_i32 v5, v2, v3, v4", Vec(0x80000000L, 0xffffffffL, 0L)), 0x80000000L)
    assertEquals(run("v_add_min_i32 v5, v2, v3, v4", Vec(3L, 4L, 100L)), 7L)
    assertEquals(run("v_add_max_u32 v5, v2, v3, v4", Vec(0xffffffffL, 2L, 7L)), 0xffffffffL)
    assertEquals(run("v_add_max_u32 v5, v2, v3, v4", Vec(3L, 4L, 100L)), 100L)
    assertEquals(run("v_add_min_u32 v5, v2, v3, v4", Vec(0xffffffffL, 2L, 7L)), 7L)
    assertEquals(run("v_add_min_u32 v5, v2, v3, v4", Vec(0xfffffff0L, 0x20L, 0xfffffff5L)), 0xfffffff5L)
    assertEquals(run("v_add_min_u32 v5, v2, v3, v4", Vec(3L, 4L, 100L)), 7L)
  }

  test("v_mad_u32 adds the third operand to the low dword of the product") {
    assertEquals(run("v_mad_u32 v5, v2, v3, v4", Vec(0x10001L, 0x10001L, 5L)), 0x20006L)
    assertEquals(run("v_mad_u32 v5, v2, v3, v4", Vec(0xffffffffL, 2L, 3L)), 1L)
  }

  test("packed add-min and add-max saturate each 16-bit sum before they compare it with the third operand") {
    assertEquals(run("v_pk_add_max_i16 v5, v2, v3, v4", Vec(0x7fff0003L, 0x0001fffeL, 0x80000000L)), 0x7fff0001L)
    assertEquals(run("v_pk_add_max_i16 v5, v2, v3, v4", Vec(0x8000fff0L, 0xffff0001L, 0xfff00000L)), 0xfff00000L)
    assertEquals(run("v_pk_add_min_i16 v5, v2, v3, v4", Vec(0x80000005L, 0xffff0001L, 0x00000100L)), 0x80000006L)
    assertEquals(run("v_pk_add_min_u16 v5, v2, v3, v4", Vec(0xffff0010L, 0x00020020L, 0xfff00100L)), 0xfff00030L)
    assertEquals(run("v_pk_add_max_u16 v5, v2, v3, v4", Vec(0xffff0010L, 0x00020020L, 0x00100100L)), 0xffff0100L)
  }

  test("packed three-way minimum and maximum compare each half") {
    val inputs = Vec(0x0005ffffL, 0xfff00002L, 0x0001fffeL)
    assertEquals(run("v_pk_max3_i16 v5, v2, v3, v4", inputs), 0x00050002L)
    assertEquals(run("v_pk_max3_u16 v5, v2, v3, v4", inputs), 0xfff0ffffL)
    assertEquals(run("v_pk_min3_i16 v5, v2, v3, v4", inputs), 0xfff0fffeL)
    assertEquals(run("v_pk_min3_u16 v5, v2, v3, v4", inputs), 0x00010002L)
  }

  test("packed three-way float minimum and maximum return the number when one operand is a NaN") {
    val inputs = Vec(0xc0003c00L, 0x38004000L, 0x7e003800L)
    assertEquals(run("v_pk_min3_num_f16 v5, v2, v3, v4", inputs), 0xc0003800L)
    assertEquals(run("v_pk_max3_num_f16 v5, v2, v3, v4", inputs), 0x38004000L)
  }

  test("64-bit integer add, subtract and multiply wrap at 64 bits") {
    val carry = Vec(0xffffffffL, 1L, 1L, 0L)
    val wrap  = Vec(0xffffffffL, 0xffffffffL, 2L, 0L)
    Seq("v_add_nc_u64 v[6:7], v[2:3], v[4:5]", "v_add_nc_u64_e64 v[6:7], v[2:3], v[4:5]").foreach { text =>
      assertEquals(run64(text, carry), 0x200000000L, text)
      assertEquals(run64(text, wrap), 1L, text)
    }
    assertEquals(run64("v_sub_nc_u64 v[6:7], v[2:3], v[4:5]", Vec(0L, 0L, 1L, 0L)), -1L)
    assertEquals(run64("v_sub_nc_u64 v[6:7], v[2:3], v[4:5]", Vec(0L, 1L, 1L, 0L)), 0xffffffffL)
    assertEquals(run64("v_mul_u64 v[6:7], v[2:3], v[4:5]", Vec(3L, 2L, 5L, 0L)), 0xa0000000fL)
    assertEquals(run64("v_mul_u64 v[6:7], v[2:3], v[4:5]", Vec(0xffffffffL, 0xffffffffL, 2L, 0L)), -2L)
  }

  test("64-bit integer minimum and maximum compare signed or unsigned") {
    val minusOne = Vec(0xffffffffL, 0xffffffffL, 1L, 0L)
    assertEquals(run64("v_max_i64 v[6:7], v[2:3], v[4:5]", minusOne), 1L)
    assertEquals(run64("v_min_i64 v[6:7], v[2:3], v[4:5]", minusOne), -1L)
    assertEquals(run64("v_max_u64 v[6:7], v[2:3], v[4:5]", minusOne), -1L)
    assertEquals(run64("v_min_u64 v[6:7], v[2:3], v[4:5]", minusOne), 1L)
  }

  test("v_mad_nc_u64_u32 and v_mad_nc_i64_i32 add a 64-bit value to a 32-bit product with no carry out") {
    assertEquals(
      run64("v_mad_nc_u64_u32 v[6:7], v2, v3, v[4:5]", Vec(0xffffffffL, 0xffffffffL, 0L, 1L)),
      0xffffffff00000001L
    )
    assertEquals(run64("v_mad_nc_i64_i32 v[6:7], v2, v3, v[4:5]", Vec(0xfffffffeL, 3L, 10L, 0L)), 4L)
    assertEquals(
      run64("v_mad_nc_i64_i32 v[6:7], v2, v3, v[4:5]", Vec(0x80000000L, 2L, 0L, 0L)),
      0xffffffff00000000L
    )
  }

  test("v_bitop3_b32 routes its first source under DPP") {
    val text   = "v_bitop3_b32_e64_dpp v5, v2, v3, v4 bitop3:0x96 quad_perm:[1,0,3,2]"
    val inputs = Vec(Vec(0x0fL, 0xf0L, 0x00L), Vec(0xff00L, 0x00L, 0xffL))
    assertEquals(laneResults(gfx1250, inputs, program(gfx1250, code(text)), 5), Vec(0xfff0L, 0xf0L))
  }

  test("v_sat_pk4_i4_i8 and v_sat_pk4_u4_u8 saturate each byte into a nibble, the lowest byte first") {
    assertEquals(run("v_sat_pk4_i4_i8 v5, v2", Vec(0xf905807fL)) & 0xffffL, 0x9587L)
    assertEquals(run("v_sat_pk4_i4_i8 v5, v2", Vec(0x01fff880L)) & 0xffffL, 0x1f88L)
    assertEquals(run("v_sat_pk4_u4_u8 v5, v2", Vec(0x030a10ffL)) & 0xffffL, 0x3affL)
  }

  // VOP3Instructions.td, V_CVT_PK_FP8_F16_gfx1250 and V_CVT_PK_BF8_F16_gfx1250; OP_SEL bit 3 names the high half
  test("v_cvt_pk_fp8_f16 and v_cvt_pk_bf8_f16 round each binary16 half to nearest even, low half first") {
    assertEquals(run("v_cvt_pk_fp8_f16 v5, v2", Vec(0x3cc03c40L)) & 0xffffL, 0x3a38L)
    assertEquals(run("v_cvt_pk_bf8_f16 v5, v2", Vec(0x3cc03c40L)) & 0xffffL, 0x3d3cL)
  }

  test("v_cvt_pk_fp8_f16 and v_cvt_pk_bf8_f16 keep the sign of NaN and map infinity as the format does") {
    assertEquals(run("v_cvt_pk_fp8_f16 v5, v2", Vec(0xfe007c00L)) & 0xffffL, 0xff7fL)
    assertEquals(run("v_cvt_pk_bf8_f16 v5, v2", Vec(0xfe007c00L)) & 0xffffL, 0xff7cL)
  }

  test("v_cvt_pk_fp8_f16 overflows to NaN unless MODE.FP16_OVFL saturates it") {
    val saturate = Vec(Instruction.SSetMode(23, 1, InstructionSource.Constant(1L)))
    assertEquals(run("v_cvt_pk_fp8_f16 v5, v2", Vec(0x63e8L)) & 0xffffL, 0x7fL)
    assertEquals(
      laneResults(gfx1250, Vec(Vec(0x63e8L)), saturate ++ program(gfx1250, code("v_cvt_pk_fp8_f16 v5, v2")), 5).head &
        0xffffL,
      0x7eL
    )
  }

  test("v_cvt_pk_fp8_f16 writes the high half under OP_SEL bit 3 and keeps the low half") {
    val bytes = Vec(0x05, 0x40, 0x72, 0xd7, 0x02, 0x01, 0x01, 0x02).map(_.toByte)
    assertEquals(laneResults(gfx1250, Vec(Vec(0x3c003c00L)), program(gfx1250, bytes), 5).head >>> 16, 0x3838L)
  }

  // VOP1Instructions.td, V_CVT_F16_F8_Profile and V_CVT_PK_F16_F8; every E4M3 and E5M2 value is exact in binary16
  private def low(text: String, input: Long): Long = run(text, Vec(input)) & 0xffffL

  private def isHalfNaN(bits: Long): Boolean = (bits & 0x7c00L) == 0x7c00L && (bits & 0x3ffL) != 0L

  test("v_cvt_f16_fp8 and v_cvt_f16_bf8 widen every value exactly, denormals and zero signs included") {
    Vec(
      0x38L -> 0x3c00L,
      0x40L -> 0x4000L,
      0x01L -> 0x1800L,
      0x07L -> 0x2300L,
      0x08L -> 0x2400L,
      0x7eL -> 0x5f00L,
      0x80L -> 0x8000L,
      0xb8L -> 0xbc00L
    ).foreach { (input, expected) =>
      assertEquals(low("v_cvt_f16_fp8 v5.l, v2", input), expected, s"e4m3 0x${input.toHexString}")
    }
    Vec(
      0x3cL -> 0x3c00L,
      0x40L -> 0x4000L,
      0x01L -> 0x0100L,
      0x02L -> 0x0200L,
      0x03L -> 0x0300L,
      0x04L -> 0x0400L,
      0x7bL -> 0x7b00L,
      0x7cL -> 0x7c00L,
      0xfcL -> 0xfc00L,
      0x80L -> 0x8000L
    ).foreach { (input, expected) =>
      assertEquals(low("v_cvt_f16_bf8 v5.l, v2", input), expected, s"e5m2 0x${input.toHexString}")
    }
  }

  test("v_cvt_f16_fp8 and v_cvt_f16_bf8 give a binary16 NaN for every NaN code, E4M3 having no infinity") {
    Vec(0x7fL, 0xffL).foreach(input =>
      assert(isHalfNaN(low("v_cvt_f16_fp8 v5.l, v2", input)), s"e4m3 0x${input.toHexString}")
    )
    Vec(0x7dL, 0x7eL, 0x7fL, 0xfdL, 0xfeL, 0xffL).foreach(input =>
      assert(isHalfNaN(low("v_cvt_f16_bf8 v5.l, v2", input)), s"e5m2 0x${input.toHexString}")
    )
  }

  test("v_cvt_f16_fp8_e64 and v_cvt_f16_bf8_e64 read the byte BYTE_SEL names and OP_SEL bit 3 writes the high half") {
    val fp8 = 0x80074038L
    assertEquals(low("v_cvt_f16_fp8_e64 v5.l, v2", fp8), 0x3c00L)
    assertEquals(low("v_cvt_f16_fp8_e64 v5.l, v2 byte_sel:1", fp8), 0x4000L)
    assertEquals(low("v_cvt_f16_fp8_e64 v5.l, v2 byte_sel:2", fp8), 0x2300L)
    assertEquals(low("v_cvt_f16_fp8_e64 v5.l, v2 byte_sel:3", fp8), 0x8000L)
    assertEquals(run("v_cvt_f16_fp8_e64 v5.h, v2 op_sel:[0,1]", Vec(fp8)) >>> 16, 0x3c00L)
    val bf8 = 0xfc02403cL
    assertEquals(low("v_cvt_f16_bf8_e64 v5.l, v2", bf8), 0x3c00L)
    assertEquals(low("v_cvt_f16_bf8_e64 v5.l, v2 byte_sel:2", bf8), 0x0200L)
    assertEquals(low("v_cvt_f16_bf8_e64 v5.l, v2 byte_sel:3", bf8), 0xfc00L)
  }

  test("v_cvt_pk_f16_fp8 and v_cvt_pk_f16_bf8 widen the low word, OP_SEL bit 0 taking the high word") {
    assertEquals(run("v_cvt_pk_f16_fp8 v5, v2.l", Vec(0x4038L)), 0x40003c00L)
    assertEquals(run("v_cvt_pk_f16_fp8_e64 v5, v2 op_sel:[1,0]", Vec(0x40380000L)), 0x40003c00L)
    assertEquals(run("v_cvt_pk_f16_bf8 v5, v2.l", Vec(0xfc3cL)), 0xfc003c00L)
    assertEquals(run("v_cvt_pk_f16_bf8_e64 v5, v2 op_sel:[1,0]", Vec(0xfc3c0000L)), 0xfc003c00L)
  }

  test("v_cvt_f16_fp8_dpp routes its source under quad_perm before it widens") {
    val inputs = Vec(Vec(0x38L), Vec(0x40L))
    val text   = "v_cvt_f16_fp8_dpp v5.l, v2 quad_perm:[1,0,3,2] row_mask:0xf bank_mask:0xf"
    assertEquals(laneResults(gfx1250, inputs, program(gfx1250, code(text)), 5).map(_ & 0xffffL), Vec(0x4000L, 0x3c00L))
  }

  test("the renamed program counter instructions decode as their GFX12 counterparts") {
    import InstructionSource.*
    assertEquals(decoded(gfx1250, 0x02, 0x48, 0x80, 0xbe), Instruction.SSetPc(Scalar(2)))
    assertEquals(decoded(gfx1250, 0x02, 0x49, 0x84, 0xbe), Instruction.SSwapPc(4, Scalar(2)))
    assertEquals(decoded(gfx1250, 0x08, 0x00, 0x04, 0xba), Instruction.CallRelative(4, 8))
    assertEquals(decoded(gfx1250, 0x00, 0x00, 0xc5, 0xbf), Instruction.Wait)
    assertEquals(decoded(gfx1250, 0x01, 0x00, 0xbe, 0xbf), Instruction.Wait)
  }
}
