// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@Generated(Array("sbt codegen"))
private object SOPCInstructionDefs {
  case object SBitcmp0B32 extends Opcode("s_bitcmp0_b32", 914)

  case object SBitcmp0B64 extends Opcode("s_bitcmp0_b64", 915)

  case object SBitcmp1B32 extends Opcode("s_bitcmp1_b32", 916)

  case object SBitcmp1B64 extends Opcode("s_bitcmp1_b64", 917)

  case object SCmpEqF16 extends Opcode("s_cmp_eq_f16", 996)

  case object SCmpEqF32 extends Opcode("s_cmp_eq_f32", 997)

  case object SCmpEqI32 extends Opcode("s_cmp_eq_i32", 998)

  case object SCmpEqU32 extends Opcode("s_cmp_eq_u32", 999)

  case object SCmpEqU64 extends Opcode("s_cmp_eq_u64", 1000)

  case object SCmpGeF16 extends Opcode("s_cmp_ge_f16", 1001)

  case object SCmpGeF32 extends Opcode("s_cmp_ge_f32", 1002)

  case object SCmpGeI32 extends Opcode("s_cmp_ge_i32", 1003)

  case object SCmpGeU32 extends Opcode("s_cmp_ge_u32", 1004)

  case object SCmpGtF16 extends Opcode("s_cmp_gt_f16", 1005)

  case object SCmpGtF32 extends Opcode("s_cmp_gt_f32", 1006)

  case object SCmpGtI32 extends Opcode("s_cmp_gt_i32", 1007)

  case object SCmpGtU32 extends Opcode("s_cmp_gt_u32", 1008)

  case object SCmpLeF16 extends Opcode("s_cmp_le_f16", 1009)

  case object SCmpLeF32 extends Opcode("s_cmp_le_f32", 1010)

  case object SCmpLeI32 extends Opcode("s_cmp_le_i32", 1011)

  case object SCmpLeU32 extends Opcode("s_cmp_le_u32", 1012)

  case object SCmpLgF16 extends Opcode("s_cmp_lg_f16", 1013)

  case object SCmpLgF32 extends Opcode("s_cmp_lg_f32", 1014)

  case object SCmpLgI32 extends Opcode("s_cmp_lg_i32", 1015)

  case object SCmpLgU32 extends Opcode("s_cmp_lg_u32", 1016)

  case object SCmpLgU64 extends Opcode("s_cmp_lg_u64", 1017)

  case object SCmpLtF16 extends Opcode("s_cmp_lt_f16", 1018)

  case object SCmpLtF32 extends Opcode("s_cmp_lt_f32", 1019)

  case object SCmpLtI32 extends Opcode("s_cmp_lt_i32", 1020)

  case object SCmpLtU32 extends Opcode("s_cmp_lt_u32", 1021)

  case object SCmpNeqF16 extends Opcode("s_cmp_neq_f16", 1022)

  case object SCmpNeqF32 extends Opcode("s_cmp_neq_f32", 1023)

  case object SCmpNgeF16 extends Opcode("s_cmp_nge_f16", 1024)

  case object SCmpNgeF32 extends Opcode("s_cmp_nge_f32", 1025)

  case object SCmpNgtF16 extends Opcode("s_cmp_ngt_f16", 1026)

  case object SCmpNgtF32 extends Opcode("s_cmp_ngt_f32", 1027)

  case object SCmpNleF16 extends Opcode("s_cmp_nle_f16", 1028)

  case object SCmpNleF32 extends Opcode("s_cmp_nle_f32", 1029)

  case object SCmpNlgF16 extends Opcode("s_cmp_nlg_f16", 1030)

  case object SCmpNlgF32 extends Opcode("s_cmp_nlg_f32", 1031)

  case object SCmpNltF16 extends Opcode("s_cmp_nlt_f16", 1032)

  case object SCmpNltF32 extends Opcode("s_cmp_nlt_f32", 1033)

  case object SCmpOF16 extends Opcode("s_cmp_o_f16", 1034)

  case object SCmpOF32 extends Opcode("s_cmp_o_f32", 1035)

  case object SCmpUF16 extends Opcode("s_cmp_u_f16", 1036)

  case object SCmpUF32 extends Opcode("s_cmp_u_f32", 1037)

  case object SSetGprIdxOn extends Opcode("s_set_gpr_idx_on", 1213)

  case object SSetvskip extends Opcode("s_setvskip", 1225)

  val definitions: Vec[Opcode] = Vec(
    SBitcmp0B32,
    SBitcmp0B64,
    SBitcmp1B32,
    SBitcmp1B64,
    SCmpEqF16,
    SCmpEqF32,
    SCmpEqI32,
    SCmpEqU32,
    SCmpEqU64,
    SCmpGeF16,
    SCmpGeF32,
    SCmpGeI32,
    SCmpGeU32,
    SCmpGtF16,
    SCmpGtF32,
    SCmpGtI32,
    SCmpGtU32,
    SCmpLeF16,
    SCmpLeF32,
    SCmpLeI32,
    SCmpLeU32,
    SCmpLgF16,
    SCmpLgF32,
    SCmpLgI32,
    SCmpLgU32,
    SCmpLgU64,
    SCmpLtF16,
    SCmpLtF32,
    SCmpLtI32,
    SCmpLtU32,
    SCmpNeqF16,
    SCmpNeqF32,
    SCmpNgeF16,
    SCmpNgeF32,
    SCmpNgtF16,
    SCmpNgtF32,
    SCmpNleF16,
    SCmpNleF32,
    SCmpNlgF16,
    SCmpNlgF32,
    SCmpNltF16,
    SCmpNltF32,
    SCmpOF16,
    SCmpOF32,
    SCmpUF16,
    SCmpUF32,
    SSetGprIdxOn,
    SSetvskip
  )
}
