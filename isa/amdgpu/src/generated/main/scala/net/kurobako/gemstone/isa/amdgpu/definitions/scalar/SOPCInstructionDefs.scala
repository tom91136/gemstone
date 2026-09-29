// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*
import Instruction.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private object SOPCInstructionDefs {
  case object SBitcmp0B32 extends Definition(
        "s_bitcmp0_b32",
        decoder(form(2)((operands, mods) => Instruction.SBitcmp0B32(operands(0), operands(1), mods))),
        913
      )
  case object SBitcmp0B64 extends Definition(
        "s_bitcmp0_b64",
        decoder(form(2)((operands, mods) => Instruction.SBitcmp0B64(operands(0), operands(1), mods))),
        914
      )
  case object SBitcmp1B32 extends Definition(
        "s_bitcmp1_b32",
        decoder(form(2)((operands, mods) => Instruction.SBitcmp1B32(operands(0), operands(1), mods))),
        915
      )
  case object SBitcmp1B64 extends Definition(
        "s_bitcmp1_b64",
        decoder(form(2)((operands, mods) => Instruction.SBitcmp1B64(operands(0), operands(1), mods))),
        916
      )
  case object SCmpEqF16 extends Definition(
        "s_cmp_eq_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpEqF16(operands(0), operands(1), mods))),
        995
      )
  case object SCmpEqF32 extends Definition(
        "s_cmp_eq_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpEqF32(operands(0), operands(1), mods))),
        996
      )
  case object SCmpEqI32 extends Definition(
        "s_cmp_eq_i32",
        decoder(form(2)((operands, mods) => Instruction.SCmpEqI32(operands(0), operands(1), mods))),
        997
      )
  case object SCmpEqU32 extends Definition(
        "s_cmp_eq_u32",
        decoder(form(2)((operands, mods) => Instruction.SCmpEqU32(operands(0), operands(1), mods))),
        998
      )
  case object SCmpEqU64 extends Definition(
        "s_cmp_eq_u64",
        decoder(form(2)((operands, mods) => Instruction.SCmpEqU64(operands(0), operands(1), mods))),
        999
      )
  case object SCmpGeF16 extends Definition(
        "s_cmp_ge_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpGeF16(operands(0), operands(1), mods))),
        1000
      )
  case object SCmpGeF32 extends Definition(
        "s_cmp_ge_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpGeF32(operands(0), operands(1), mods))),
        1001
      )
  case object SCmpGeI32 extends Definition(
        "s_cmp_ge_i32",
        decoder(form(2)((operands, mods) => Instruction.SCmpGeI32(operands(0), operands(1), mods))),
        1002
      )
  case object SCmpGeU32 extends Definition(
        "s_cmp_ge_u32",
        decoder(form(2)((operands, mods) => Instruction.SCmpGeU32(operands(0), operands(1), mods))),
        1003
      )
  case object SCmpGtF16 extends Definition(
        "s_cmp_gt_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpGtF16(operands(0), operands(1), mods))),
        1004
      )
  case object SCmpGtF32 extends Definition(
        "s_cmp_gt_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpGtF32(operands(0), operands(1), mods))),
        1005
      )
  case object SCmpGtI32 extends Definition(
        "s_cmp_gt_i32",
        decoder(form(2)((operands, mods) => Instruction.SCmpGtI32(operands(0), operands(1), mods))),
        1006
      )
  case object SCmpGtU32 extends Definition(
        "s_cmp_gt_u32",
        decoder(form(2)((operands, mods) => Instruction.SCmpGtU32(operands(0), operands(1), mods))),
        1007
      )
  case object SCmpLeF16 extends Definition(
        "s_cmp_le_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpLeF16(operands(0), operands(1), mods))),
        1008
      )
  case object SCmpLeF32 extends Definition(
        "s_cmp_le_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLeF32(operands(0), operands(1), mods))),
        1009
      )
  case object SCmpLeI32 extends Definition(
        "s_cmp_le_i32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLeI32(operands(0), operands(1), mods))),
        1010
      )
  case object SCmpLeU32 extends Definition(
        "s_cmp_le_u32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLeU32(operands(0), operands(1), mods))),
        1011
      )
  case object SCmpLgF16 extends Definition(
        "s_cmp_lg_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpLgF16(operands(0), operands(1), mods))),
        1012
      )
  case object SCmpLgF32 extends Definition(
        "s_cmp_lg_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLgF32(operands(0), operands(1), mods))),
        1013
      )
  case object SCmpLgI32 extends Definition(
        "s_cmp_lg_i32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLgI32(operands(0), operands(1), mods))),
        1014
      )
  case object SCmpLgU32 extends Definition(
        "s_cmp_lg_u32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLgU32(operands(0), operands(1), mods))),
        1015
      )
  case object SCmpLgU64 extends Definition(
        "s_cmp_lg_u64",
        decoder(form(2)((operands, mods) => Instruction.SCmpLgU64(operands(0), operands(1), mods))),
        1016
      )
  case object SCmpLtF16 extends Definition(
        "s_cmp_lt_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpLtF16(operands(0), operands(1), mods))),
        1017
      )
  case object SCmpLtF32 extends Definition(
        "s_cmp_lt_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLtF32(operands(0), operands(1), mods))),
        1018
      )
  case object SCmpLtI32 extends Definition(
        "s_cmp_lt_i32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLtI32(operands(0), operands(1), mods))),
        1019
      )
  case object SCmpLtU32 extends Definition(
        "s_cmp_lt_u32",
        decoder(form(2)((operands, mods) => Instruction.SCmpLtU32(operands(0), operands(1), mods))),
        1020
      )
  case object SCmpNeqF16 extends Definition(
        "s_cmp_neq_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpNeqF16(operands(0), operands(1), mods))),
        1021
      )
  case object SCmpNeqF32 extends Definition(
        "s_cmp_neq_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpNeqF32(operands(0), operands(1), mods))),
        1022
      )
  case object SCmpNgeF16 extends Definition(
        "s_cmp_nge_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpNgeF16(operands(0), operands(1), mods))),
        1023
      )
  case object SCmpNgeF32 extends Definition(
        "s_cmp_nge_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpNgeF32(operands(0), operands(1), mods))),
        1024
      )
  case object SCmpNgtF16 extends Definition(
        "s_cmp_ngt_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpNgtF16(operands(0), operands(1), mods))),
        1025
      )
  case object SCmpNgtF32 extends Definition(
        "s_cmp_ngt_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpNgtF32(operands(0), operands(1), mods))),
        1026
      )
  case object SCmpNleF16 extends Definition(
        "s_cmp_nle_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpNleF16(operands(0), operands(1), mods))),
        1027
      )
  case object SCmpNleF32 extends Definition(
        "s_cmp_nle_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpNleF32(operands(0), operands(1), mods))),
        1028
      )
  case object SCmpNlgF16 extends Definition(
        "s_cmp_nlg_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpNlgF16(operands(0), operands(1), mods))),
        1029
      )
  case object SCmpNlgF32 extends Definition(
        "s_cmp_nlg_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpNlgF32(operands(0), operands(1), mods))),
        1030
      )
  case object SCmpNltF16 extends Definition(
        "s_cmp_nlt_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpNltF16(operands(0), operands(1), mods))),
        1031
      )
  case object SCmpNltF32 extends Definition(
        "s_cmp_nlt_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpNltF32(operands(0), operands(1), mods))),
        1032
      )
  case object SCmpOF16 extends Definition(
        "s_cmp_o_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpOF16(operands(0), operands(1), mods))),
        1033
      )
  case object SCmpOF32 extends Definition(
        "s_cmp_o_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpOF32(operands(0), operands(1), mods))),
        1034
      )
  case object SCmpUF16 extends Definition(
        "s_cmp_u_f16",
        decoder(form(2)((operands, mods) => Instruction.SCmpUF16(operands(0), operands(1), mods))),
        1035
      )
  case object SCmpUF32 extends Definition(
        "s_cmp_u_f32",
        decoder(form(2)((operands, mods) => Instruction.SCmpUF32(operands(0), operands(1), mods))),
        1036
      )
  case object SSetGprIdxOn extends Definition(
        "s_set_gpr_idx_on",
        decoder(form(2)((operands, mods) => Instruction.SSetGprIdxOn(operands(0), operands(1), mods))),
        1212
      )
  case object SSetvskip extends Definition(
        "s_setvskip",
        decoder(form(2)((operands, mods) => Instruction.SSetvskip(operands(0), operands(1), mods))),
        1224
      )

  val definitions: Vec[Definition] = Vec(
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
