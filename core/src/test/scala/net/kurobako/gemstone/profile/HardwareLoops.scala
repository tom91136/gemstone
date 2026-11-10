package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** The loop bodies of the hardware validation kernels as compiled for each card: SASS from nvcc 13.3 for sm_89 and
  * AMDGPU code from hipcc 7.2 (clang 22) for gfx1036. Pipe, schedule and text are what the VM profiles on the compiled
  * code object; reads and writes follow the operands of each instruction.
  */
object HardwareLoops {
  case class Row(text: String, pipe: Option[Pipe], reads: Vec[String], writes: Vec[String], schedule: Schedule)

  private def enc(stall: Int, barrier: Boolean): Schedule = Schedule.Encoded(stall.unsafe[Int in 0 ~ 15], barrier)

  private def est(cycles: Int): Schedule = Schedule.Estimated(cycles.unsafe[Int >= 0])

  private def row(text: String, pipe: String, reads: String, writes: String, schedule: Schedule): Row =
    Row(
      text,
      Pipe.values.find(_.toString.equalsIgnoreCase(pipe)),
      Vec.from(reads.split(' ').filter(_.nonEmpty)),
      Vec.from(writes.split(' ').filter(_.nonEmpty)),
      schedule
    )

  val sm89: Map[String, Vec[Row]] = Map(
    "chain_ffma" -> Vec(
      row("FFMA R9, R9, R0, c[0][0x180];", "fma", "R9 R0", "R9", enc(1, false)),
      row("IADD3 R4, R4, 0x1, RZ;", "alu", "R4", "R4", enc(3, false)),
      row("FFMA R9, R9, R0, c[0][0x180];", "fma", "R9 R0", "R9", enc(1, false)),
      row("ISETP.GE.AND P0, PT, R4, c[0][0x178], PT;", "alu", "R4", "P0", enc(3, false)),
      row("FFMA R9, R9, R0, c[0][0x180];", "fma", "R9 R0", "R9", enc(4, false)),
      row("FFMA R9, R9, R0, c[0][0x180];", "fma", "R9 R0", "R9", enc(4, false)),
      row("FFMA R9, R9, R0, c[0][0x180];", "fma", "R9 R0", "R9", enc(4, false)),
      row("FFMA R9, R9, R0, c[0][0x180];", "fma", "R9 R0", "R9", enc(4, false)),
      row("FFMA R9, R9, R0, c[0][0x180];", "fma", "R9 R0", "R9", enc(4, false)),
      row("FFMA R9, R9, R0, c[0][0x180];", "fma", "R9 R0", "R9", enc(1, false)),
      row("@!P0 BRA 0xc0;", "-", "P0", "", enc(5, false))
    ),
    "stream_ffma" -> Vec(
      row("IADD3 R21, R21, 0x1, RZ;", "alu", "R21", "R21", enc(2, false)),
      row("MOV R20, c[0][0x17c];", "-", "", "R20", enc(2, false)),
      row("ISETP.GE.AND P0, PT, R21, c[0][0x178], PT;", "alu", "R21", "P0", enc(3, false)),
      row("FFMA R10, R10, R20, c[0][0x180];", "fma", "R10 R20", "R10", enc(1, false)),
      row("FFMA R11, R11, R20, c[0][0x180];", "fma", "R11 R20", "R11", enc(1, false)),
      row("FFMA R12, R12, R20, c[0][0x180];", "fma", "R12 R20", "R12", enc(1, false)),
      row("FFMA R13, R13, R20, c[0][0x180];", "fma", "R13 R20", "R13", enc(1, false)),
      row("FFMA R14, R14, R20, c[0][0x180];", "fma", "R14 R20", "R14", enc(1, false)),
      row("FFMA R15, R15, R20, c[0][0x180];", "fma", "R15 R20", "R15", enc(1, false)),
      row("FFMA R16, R16, R20, c[0][0x180];", "fma", "R16 R20", "R16", enc(1, false)),
      row("FFMA R17, R17, R20, c[0][0x180];", "fma", "R17 R20", "R17", enc(1, false)),
      row("FFMA R18, R18, R20, c[0][0x180];", "fma", "R18 R20", "R18", enc(1, false)),
      row("FFMA R19, R19, R20, c[0][0x180];", "fma", "R19 R20", "R19", enc(1, false)),
      row("FFMA R7, R7, R20, c[0][0x180];", "fma", "R7 R20", "R7", enc(1, false)),
      row("FFMA R6, R6, R20, c[0][0x180];", "fma", "R6 R20", "R6", enc(1, false)),
      row("FFMA R9, R9, R20, c[0][0x180];", "fma", "R9 R20", "R9", enc(1, false)),
      row("FFMA R8, R8, R20, c[0][0x180];", "fma", "R8 R20", "R8", enc(1, false)),
      row("FFMA R5, R5, R20, c[0][0x180];", "fma", "R5 R20", "R5", enc(1, false)),
      row("FFMA R4, R4, R20, c[0][0x180];", "fma", "R4 R20", "R4", enc(1, false)),
      row("@!P0 BRA 0x1b0;", "-", "P0", "", enc(5, false))
    ),
    "stream_mufu" -> Vec(
      row("IADD3 R11, R11, 0x1, RZ;", "alu", "R11", "R11", enc(1, false)),
      row("MUFU.RCP R8, R8;", "mufu", "R8", "R8", enc(3, true)),
      row("ISETP.GE.AND P0, PT, R11, c[0][0x178], PT;", "alu", "R11", "P0", enc(5, false)),
      row("MUFU.RCP R9, R9;", "mufu", "R9", "R9", enc(8, true)),
      row("MUFU.RCP R10, R10;", "mufu", "R10", "R10", enc(8, true)),
      row("MUFU.RCP R0, R0;", "mufu", "R0", "R0", enc(8, true)),
      row("MUFU.RCP R4, R4;", "mufu", "R4", "R4", enc(8, true)),
      row("MUFU.RCP R5, R5;", "mufu", "R5", "R5", enc(8, true)),
      row("MUFU.RCP R6, R6;", "mufu", "R6", "R6", enc(8, true)),
      row("MUFU.RCP R7, R7;", "mufu", "R7", "R7", enc(1, true)),
      row("@!P0 BRA 0x140;", "-", "P0", "", enc(5, false))
    ),
    "stream_dfma" -> Vec(
      row("IADD3 R24, R24, 0x1, RZ;", "alu", "R24", "R24", enc(1, false)),
      row("DFMA R16, R20, R16, R22;", "fp64", "R20 R21 R16 R17 R22 R23", "R16 R17", enc(3, true)),
      row("ISETP.GE.AND P0, PT, R24, c[0][0x178], PT;", "alu", "R24", "P0", enc(1, false)),
      row("DFMA R2, R20, R2, R22;", "fp64", "R20 R21 R2 R3 R22 R23", "R2 R3", enc(4, true)),
      row("DFMA R4, R20, R4, R22;", "fp64", "R20 R21 R4 R5 R22 R23", "R4 R5", enc(4, true)),
      row("DFMA R6, R20, R6, R22;", "fp64", "R20 R21 R6 R7 R22 R23", "R6 R7", enc(4, true)),
      row("DFMA R8, R20, R8, R22;", "fp64", "R20 R21 R8 R9 R22 R23", "R8 R9", enc(4, true)),
      row("DFMA R10, R20, R10, R22;", "fp64", "R20 R21 R10 R11 R22 R23", "R10 R11", enc(4, true)),
      row("DFMA R12, R20, R12, R22;", "fp64", "R20 R21 R12 R13 R22 R23", "R12 R13", enc(4, true)),
      row("DFMA R14, R20, R14, R22;", "fp64", "R20 R21 R14 R15 R22 R23", "R14 R15", enc(1, true)),
      row("@!P0 BRA 0x160;", "-", "P0", "", enc(5, false))
    ),
    "mixed_ldg" -> Vec(
      row("IMAD.U32 R4, R12, 0x4, RZ;", "fma", "R12", "R4", enc(5, false)),
      row("LOP3.LUT R10, R4, 0xffc, RZ, 0xc0, !PT;", "alu", "R4", "R10", enc(4, false)),
      row("IADD3 R4, P1, R10, c[0][0x170], RZ;", "alu", "R10", "R4 P1", enc(2, false)),
      row("LOP3.LUT R6, R10, 0x200, RZ, 0x3c, !PT;", "alu", "R10", "R6", enc(2, false)),
      row("LOP3.LUT R8, R10, 0x400, RZ, 0x3c, !PT;", "alu", "R10", "R8", enc(1, false)),
      row("IMAD.X R5, RZ, RZ, c[0][0x174], P1;", "fma", "P1", "R5", enc(1, false)),
      row("IADD3 R6, P0, R6, c[0][0x170], RZ;", "alu", "R6", "R6 P0", enc(2, false)),
      row("LOP3.LUT R10, R10, 0x800, RZ, 0x3c, !PT;", "alu", "R10", "R10", enc(2, false)),
      row("IADD3 R8, P1, R8, c[0][0x170], RZ;", "alu", "R8", "R8 P1", enc(1, false)),
      row("LDG.E R4, [R4.64];", "-", "R4 R5", "R4", enc(1, true)),
      row("IADD3.X R7, RZ, c[0][0x174], RZ, P0, !PT;", "alu", "P0", "R7", enc(2, false)),
      row("IADD3 R10, P0, R10, c[0][0x170], RZ;", "alu", "R10", "R10 P0", enc(1, false)),
      row("IMAD.X R9, RZ, RZ, c[0][0x174], P1;", "fma", "P1", "R9", enc(2, false)),
      row("LDG.E R6, [R6.64];", "-", "R6 R7", "R6", enc(2, true)),
      row("IMAD.X R11, RZ, RZ, c[0][0x174], P0;", "fma", "P0", "R11", enc(2, false)),
      row("LDG.E R8, [R8.64];", "-", "R8 R9", "R8", enc(4, true)),
      row("LDG.E R10, [R10.64];", "-", "R10 R11", "R10", enc(1, true)),
      row("IADD3 R0, R0, 0x1, RZ;", "alu", "R0", "R0", enc(4, false)),
      row("ISETP.GE.AND P0, PT, R0, c[0][0x178], PT;", "alu", "R0", "P0", enc(2, false)),
      row("IADD3 R12, R12, 0x20, RZ;", "alu", "R12", "R12", enc(1, false)),
      row("FFMA R13, R4, c[0][0x17c], R13;", "fma", "R4 R13", "R13", enc(4, false)),
      row("FFMA R13, R6, c[0][0x180], R13;", "fma", "R6 R13", "R13", enc(4, false)),
      row("FFMA R13, R8, c[0][0x17c], R13;", "fma", "R8 R13", "R13", enc(4, false)),
      row("FFMA R13, R10, c[0][0x180], R13;", "fma", "R10 R13", "R13", enc(1, false)),
      row("@!P0 BRA 0xc0;", "-", "P0", "", enc(5, false))
    ),
    "reduce_tree" -> Vec(
      row("MOV R0, c[0][0x17c];", "-", "", "R0", enc(2, false)),
      row("IADD3 R16, R16, 0x1, RZ;", "alu", "R16", "R16", enc(3, false)),
      row("FFMA R10, R10, R0, c[0][0x180];", "fma", "R10 R0", "R10", enc(1, false)),
      row("FFMA R13, R13, R0, c[0][0x180];", "fma", "R13 R0", "R13", enc(1, false)),
      row("FFMA R8, R8, R0, c[0][0x180];", "fma", "R8 R0", "R8", enc(1, false)),
      row("FFMA R11, R11, R0, c[0][0x180];", "fma", "R11 R0", "R11", enc(1, false)),
      row("FFMA R6, R6, R0, c[0][0x180];", "fma", "R6 R0", "R6", enc(1, false)),
      row("FFMA R7, R7, R0, c[0][0x180];", "fma", "R7 R0", "R7", enc(1, false)),
      row("FFMA R4, R4, R0, c[0][0x180];", "fma", "R4 R0", "R4", enc(1, false)),
      row("FFMA R5, R5, R0, c[0][0x180];", "fma", "R5 R0", "R5", enc(1, false)),
      row("ISETP.GE.AND P0, PT, R16, c[0][0x178], PT;", "alu", "R16", "P0", enc(1, false)),
      row("FADD R0, R10, R13;", "fma", "R10 R13", "R0", enc(1, false)),
      row("FADD R15, R8, R11;", "fma", "R8 R11", "R15", enc(1, false)),
      row("FADD R12, R6, R7;", "fma", "R6 R7", "R12", enc(1, false)),
      row("FADD R17, R4, R5;", "fma", "R4 R5", "R17", enc(2, false)),
      row("FADD R0, R0, R15;", "fma", "R0 R15", "R0", enc(2, false)),
      row("FADD R17, R12, R17;", "fma", "R12 R17", "R17", enc(4, false)),
      row("FADD R0, R0, R17;", "fma", "R0 R17", "R0", enc(4, false)),
      row("FADD R9, R0, R9;", "fma", "R0 R9", "R9", enc(1, false)),
      row("@!P0 BRA 0x150;", "-", "P0", "", enc(5, false))
    )
  )

  val gfx1036: Map[String, Vec[Row]] = Map(
    "chain_ffma" -> Vec(
      row("v_fma_f32 v1, v3, s1, s2 e64", "fma", "v3 s1 s2", "v1", est(1)),
      row("s_add_i32 s0, s0, -1", "-", "s0", "s0 scc", est(1)),
      row("s_cmp_lg_u32 s0, 0", "-", "s0", "scc", est(1)),
      row("v_fma_f32 v1, v1, s1, s2 e64", "fma", "v1 s1 s2", "v1", est(1)),
      row("v_fma_f32 v1, v1, s1, s2 e64", "fma", "v1 s1 s2", "v1", est(1)),
      row("v_fma_f32 v1, v1, s1, s2 e64", "fma", "v1 s1 s2", "v1", est(1)),
      row("v_fma_f32 v1, v1, s1, s2 e64", "fma", "v1 s1 s2", "v1", est(1)),
      row("v_fma_f32 v1, v1, s1, s2 e64", "fma", "v1 s1 s2", "v1", est(1)),
      row("v_fma_f32 v1, v1, s1, s2 e64", "fma", "v1 s1 s2", "v1", est(1)),
      row("v_fma_f32 v3, v1, s1, s2 e64", "fma", "v1 s1 s2", "v3", est(1)),
      row("s_cbranch_scc1 -19", "-", "scc", "", est(1))
    ),
    "stream_ffma" -> Vec(
      row("v_fma_f32 v16, v16, s1, s2 e64", "fma", "v16 s1 s2", "v16", est(1)),
      row("v_fma_f32 v15, v15, s1, s2 e64", "fma", "v15 s1 s2", "v15", est(1)),
      row("v_fma_f32 v14, v14, s1, s2 e64", "fma", "v14 s1 s2", "v14", est(1)),
      row("v_fma_f32 v13, v13, s1, s2 e64", "fma", "v13 s1 s2", "v13", est(1)),
      row("v_fma_f32 v12, v12, s1, s2 e64", "fma", "v12 s1 s2", "v12", est(1)),
      row("v_fma_f32 v11, v11, s1, s2 e64", "fma", "v11 s1 s2", "v11", est(1)),
      row("v_fma_f32 v10, v10, s1, s2 e64", "fma", "v10 s1 s2", "v10", est(1)),
      row("v_fma_f32 v9, v9, s1, s2 e64", "fma", "v9 s1 s2", "v9", est(1)),
      row("v_fma_f32 v8, v8, s1, s2 e64", "fma", "v8 s1 s2", "v8", est(1)),
      row("v_fma_f32 v7, v7, s1, s2 e64", "fma", "v7 s1 s2", "v7", est(1)),
      row("v_fma_f32 v6, v6, s1, s2 e64", "fma", "v6 s1 s2", "v6", est(1)),
      row("v_fma_f32 v2, v2, s1, s2 e64", "fma", "v2 s1 s2", "v2", est(1)),
      row("v_fma_f32 v1, v1, s1, s2 e64", "fma", "v1 s1 s2", "v1", est(1)),
      row("v_fma_f32 v5, v5, s1, s2 e64", "fma", "v5 s1 s2", "v5", est(1)),
      row("v_fma_f32 v4, v4, s1, s2 e64", "fma", "v4 s1 s2", "v4", est(1)),
      row("v_fma_f32 v3, v3, s1, s2 e64", "fma", "v3 s1 s2", "v3", est(1)),
      row("s_add_i32 s0, s0, -1", "-", "s0", "s0 scc", est(1)),
      row("s_cmp_lg_u32 s0, 0", "-", "s0", "scc", est(1)),
      row("s_cbranch_scc1 -35", "-", "scc", "", est(1))
    ),
    "stream_mufu" -> Vec(
      row("v_rcp_f32 v8, v8", "mufu", "v8", "v8", est(1)),
      row("v_rcp_f32 v7, v7", "mufu", "v7", "v7", est(1)),
      row("v_rcp_f32 v6, v6", "mufu", "v6", "v6", est(1)),
      row("v_rcp_f32 v2, v2", "mufu", "v2", "v2", est(1)),
      row("v_rcp_f32 v1, v1", "mufu", "v1", "v1", est(1)),
      row("v_rcp_f32 v5, v5", "mufu", "v5", "v5", est(1)),
      row("v_rcp_f32 v4, v4", "mufu", "v4", "v4", est(1)),
      row("v_rcp_f32 v3, v3", "mufu", "v3", "v3", est(1)),
      row("s_add_i32 s0, s0, -1", "-", "s0", "s0 scc", est(1)),
      row("s_cmp_lg_u32 s0, 0", "-", "s0", "scc", est(1)),
      row("s_cbranch_scc1 -11", "-", "scc", "", est(1))
    ),
    "stream_dfma" -> Vec(
      row("v_fma_f64 v[15:16], v[15:16], v[17:18], v[19:20] e64", "fp64", "v15 v16 v17 v18 v19 v20", "v15 v16", est(1)),
      row("v_fma_f64 v[13:14], v[13:14], v[17:18], v[19:20] e64", "fp64", "v13 v14 v17 v18 v19 v20", "v13 v14", est(1)),
      row("v_fma_f64 v[11:12], v[11:12], v[17:18], v[19:20] e64", "fp64", "v11 v12 v17 v18 v19 v20", "v11 v12", est(1)),
      row("v_fma_f64 v[9:10], v[9:10], v[17:18], v[19:20] e64", "fp64", "v9 v10 v17 v18 v19 v20", "v9 v10", est(1)),
      row("v_fma_f64 v[7:8], v[7:8], v[17:18], v[19:20] e64", "fp64", "v7 v8 v17 v18 v19 v20", "v7 v8", est(1)),
      row("v_fma_f64 v[5:6], v[5:6], v[17:18], v[19:20] e64", "fp64", "v5 v6 v17 v18 v19 v20", "v5 v6", est(1)),
      row("v_fma_f64 v[3:4], v[3:4], v[17:18], v[19:20] e64", "fp64", "v3 v4 v17 v18 v19 v20", "v3 v4", est(1)),
      row("v_fma_f64 v[1:2], v[1:2], v[17:18], v[19:20] e64", "fp64", "v1 v2 v17 v18 v19 v20", "v1 v2", est(1)),
      row("s_add_i32 s0, s0, -1", "-", "s0", "s0 scc", est(1)),
      row("s_cmp_lg_u32 s0, 0", "-", "s0", "scc", est(1)),
      row("s_cbranch_scc1 -19", "-", "scc", "", est(1))
    ),
    "mixed_ldg" -> Vec(
      row("v_and_b32 v2, lit(0x3ff), v1", "alu", "v1", "v2", est(1)),
      row("v_add_nc_u32 v1, 32, v1", "alu", "v1", "v1", est(1)),
      row("s_add_i32 s8, s8, -1", "-", "s8", "s8 scc", est(1)),
      row("s_cmp_eq_u32 s8, 0", "-", "s8", "scc", est(1)),
      row("v_lshlrev_b32 v4, 2, v2", "alu", "v2", "v4", est(1)),
      row("v_xor_b32 v5, lit(0x80), v2", "alu", "v2", "v5", est(1)),
      row("v_xor_b32 v6, lit(0x100), v2", "alu", "v2", "v6", est(1)),
      row("v_xor_b32 v2, lit(0x200), v2", "alu", "v2", "v2", est(1)),
      row("global_load_dword v4, v4, s[14:15]", "-", "v4 s14 s15", "v4", est(1)),
      row("v_lshlrev_b32 v5, 2, v5", "alu", "v5", "v5", est(1)),
      row("v_lshlrev_b32 v6, 2, v6", "alu", "v6", "v6", est(1)),
      row("v_lshlrev_b32 v2, 2, v2", "alu", "v2", "v2", est(1)),
      row("s_clause 2", "-", "", "", est(1)),
      row("global_load_dword v5, v5, s[14:15]", "-", "v5 s14 s15", "v5", est(1)),
      row("global_load_dword v6, v6, s[14:15]", "-", "v6 s14 s15", "v6", est(1)),
      row("global_load_dword v2, v2, s[14:15]", "-", "v2 s14 s15", "v2", est(1)),
      row("s_waitcnt 16243", "-", "", "", est(1)),
      row("v_fmac_f32 v3, s9, v4", "fma", "s9 v4 v3", "v3", est(1)),
      row("s_waitcnt 16242", "-", "", "", est(1)),
      row("v_fmac_f32 v3, s10, v5", "fma", "s10 v5 v3", "v3", est(1)),
      row("s_waitcnt 16241", "-", "", "", est(1)),
      row("v_fmac_f32 v3, s9, v6", "fma", "s9 v6 v3", "v3", est(1)),
      row("s_waitcnt 16240", "-", "", "", est(1)),
      row("v_fmac_f32 v3, s10, v2", "fma", "s10 v2 v3", "v3", est(1)),
      row("s_cbranch_scc0 -33", "-", "scc", "", est(1))
    ),
    "reduce_tree" -> Vec(
      row("v_fma_f32 v1, v1, s1, s2 e64", "fma", "v1 s1 s2", "v1", est(1)),
      row("v_fma_f32 v2, v2, s1, s2 e64", "fma", "v2 s1 s2", "v2", est(1)),
      row("v_fma_f32 v4, v4, s1, s2 e64", "fma", "v4 s1 s2", "v4", est(1)),
      row("v_fma_f32 v5, v5, s1, s2 e64", "fma", "v5 s1 s2", "v5", est(1)),
      row("v_fma_f32 v6, v6, s1, s2 e64", "fma", "v6 s1 s2", "v6", est(1)),
      row("v_fma_f32 v7, v7, s1, s2 e64", "fma", "v7 s1 s2", "v7", est(1)),
      row("v_fma_f32 v8, v8, s1, s2 e64", "fma", "v8 s1 s2", "v8", est(1)),
      row("v_fma_f32 v9, v9, s1, s2 e64", "fma", "v9 s1 s2", "v9", est(1)),
      row("v_add_f32 v10, v1, v2", "fma", "v1 v2", "v10", est(1)),
      row("v_add_f32 v11, v4, v5", "fma", "v4 v5", "v11", est(1)),
      row("v_add_f32 v12, v6, v7", "fma", "v6 v7", "v12", est(1)),
      row("s_add_i32 s0, s0, -1", "-", "s0", "s0 scc", est(1)),
      row("v_add_f32 v13, v8, v9", "fma", "v8 v9", "v13", est(1)),
      row("s_cmp_lg_u32 s0, 0", "-", "s0", "scc", est(1)),
      row("v_add_f32 v10, v10, v11", "fma", "v10 v11", "v10", est(1)),
      row("v_add_f32 v11, v12, v13", "fma", "v12 v13", "v11", est(1)),
      row("v_add_f32 v10, v10, v11", "fma", "v10 v11", "v10", est(1)),
      row("v_add_f32 v3, v3, v10", "fma", "v3 v10", "v3", est(1)),
      row("s_cbranch_scc1 -27", "-", "scc", "", est(1))
    )
  )

}
