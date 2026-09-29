// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*
import Instruction.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private object SOP2InstructionDefs {
  case object SAbsdiffI32 extends Definition(
        "s_absdiff_i32",
        decoder(form(3)((operands, mods) => Instruction.SAbsdiffI32(operands(0), operands(1), operands(2), mods))),
        828
      )
  case object SAddCoCiU32 extends Definition(
        "s_add_co_ci_u32",
        decoder(form(3)((operands, mods) => Instruction.SAddCoCiU32(operands(0), operands(1), operands(2), mods))),
        829
      )
  case object SAddCoI32 extends Definition(
        "s_add_co_i32",
        decoder(form(3)((operands, mods) => Instruction.SAddCoI32(operands(0), operands(1), operands(2), mods))),
        830
      )
  case object SAddCoU32 extends Definition(
        "s_add_co_u32",
        decoder(form(3)((operands, mods) => Instruction.SAddCoU32(operands(0), operands(1), operands(2), mods))),
        831
      )
  case object SAddF16 extends Definition(
        "s_add_f16",
        decoder(form(3)((operands, mods) => Instruction.SAddF16(operands(0), operands(1), operands(2), mods))),
        832
      )
  case object SAddF32 extends Definition(
        "s_add_f32",
        decoder(form(3)((operands, mods) => Instruction.SAddF32(operands(0), operands(1), operands(2), mods))),
        833
      )
  case object SAddI32 extends Definition(
        "s_add_i32",
        decoder(form(3)((operands, mods) => Instruction.SAddI32(operands(0), operands(1), operands(2), mods))),
        834
      )
  case object SAddNcU64 extends Definition(
        "s_add_nc_u64",
        decoder(form(3)((operands, mods) => Instruction.SAddNcU64(operands(0), operands(1), operands(2), mods))),
        835
      )
  case object SAddU32 extends Definition(
        "s_add_u32",
        decoder(form(3)((operands, mods) => Instruction.SAddU32(operands(0), operands(1), operands(2), mods))),
        837
      )
  case object SAddcU32 extends Definition(
        "s_addc_u32",
        decoder(form(3)((operands, mods) => Instruction.SAddcU32(operands(0), operands(1), operands(2), mods))),
        838
      )
  case object SAndB32 extends Definition(
        "s_and_b32",
        decoder(form(3)((operands, mods) => Instruction.SAndB32(operands(0), operands(1), operands(2), mods))),
        842
      )
  case object SAndB64 extends Definition(
        "s_and_b64",
        decoder(form(3)((operands, mods) => Instruction.SAndB64(operands(0), operands(1), operands(2), mods))),
        843
      )
  case object SAndNot1B32 extends Definition(
        "s_and_not1_b32",
        decoder(form(3)((operands, mods) => Instruction.SAndNot1B32(operands(0), operands(1), operands(2), mods))),
        848
      )
  case object SAndNot1B64 extends Definition(
        "s_and_not1_b64",
        decoder(form(3)((operands, mods) => Instruction.SAndNot1B64(operands(0), operands(1), operands(2), mods))),
        849
      )
  case object SAndn2B32 extends Definition(
        "s_andn2_b32",
        decoder(form(3)((operands, mods) => Instruction.SAndn2B32(operands(0), operands(1), operands(2), mods))),
        860
      )
  case object SAndn2B64 extends Definition(
        "s_andn2_b64",
        decoder(form(3)((operands, mods) => Instruction.SAndn2B64(operands(0), operands(1), operands(2), mods))),
        861
      )
  case object SAshrI32 extends Definition(
        "s_ashr_i32",
        decoder(form(3)((operands, mods) => Instruction.SAshrI32(operands(0), operands(1), operands(2), mods))),
        866
      )
  case object SAshrI64 extends Definition(
        "s_ashr_i64",
        decoder(form(3)((operands, mods) => Instruction.SAshrI64(operands(0), operands(1), operands(2), mods))),
        867
      )
  case object SBfeI32 extends Definition(
        "s_bfe_i32",
        decoder(form(3)((operands, mods) => Instruction.SBfeI32(operands(0), operands(1), operands(2), mods))),
        907
      )
  case object SBfeI64 extends Definition(
        "s_bfe_i64",
        decoder(form(3)((operands, mods) => Instruction.SBfeI64(operands(0), operands(1), operands(2), mods))),
        908
      )
  case object SBfeU32 extends Definition(
        "s_bfe_u32",
        decoder(form(3)((operands, mods) => Instruction.SBfeU32(operands(0), operands(1), operands(2), mods))),
        909
      )
  case object SBfeU64 extends Definition(
        "s_bfe_u64",
        decoder(form(3)((operands, mods) => Instruction.SBfeU64(operands(0), operands(1), operands(2), mods))),
        910
      )
  case object SBfmB32 extends Definition(
        "s_bfm_b32",
        decoder(form(3)((operands, mods) => Instruction.SBfmB32(operands(0), operands(1), operands(2), mods))),
        911
      )
  case object SBfmB64 extends Definition(
        "s_bfm_b64",
        decoder(form(3)((operands, mods) => Instruction.SBfmB64(operands(0), operands(1), operands(2), mods))),
        912
      )
  case object SCbranchGFork extends Definition(
        "s_cbranch_g_fork",
        decoder(form(2)((operands, mods) => Instruction.SCbranchGFork(operands(0), operands(1), mods))),
        978
      )
  case object SCselectB32 extends Definition(
        "s_cselect_b32",
        decoder(form(3)((operands, mods) => Instruction.SCselectB32(operands(0), operands(1), operands(2), mods))),
        1050
      )
  case object SCselectB64 extends Definition(
        "s_cselect_b64",
        decoder(form(3)((operands, mods) => Instruction.SCselectB64(operands(0), operands(1), operands(2), mods))),
        1051
      )
  case object SCvtPkRtzF16F32 extends Definition(
        "s_cvt_pk_rtz_f16_f32",
        decoder(form(3)((operands, mods) => Instruction.SCvtPkRtzF16F32(operands(0), operands(1), operands(2), mods))),
        1060
      )
  case object SFmaakF32 extends Definition(
        "s_fmaak_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.SFmaakF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1084
      )
  case object SFmacF16 extends Definition(
        "s_fmac_f16",
        decoder(form(3)((operands, mods) => Instruction.SFmacF16(operands(0), operands(1), operands(2), mods))),
        1085
      )
  case object SFmacF32 extends Definition(
        "s_fmac_f32",
        decoder(form(3)((operands, mods) => Instruction.SFmacF32(operands(0), operands(1), operands(2), mods))),
        1086
      )
  case object SFmamkF32 extends Definition(
        "s_fmamk_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.SFmamkF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1087
      )
  case object SLshl1AddU32 extends Definition(
        "s_lshl1_add_u32",
        decoder(form(3)((operands, mods) => Instruction.SLshl1AddU32(operands(0), operands(1), operands(2), mods))),
        1113
      )
  case object SLshl2AddU32 extends Definition(
        "s_lshl2_add_u32",
        decoder(form(3)((operands, mods) => Instruction.SLshl2AddU32(operands(0), operands(1), operands(2), mods))),
        1114
      )
  case object SLshl3AddU32 extends Definition(
        "s_lshl3_add_u32",
        decoder(form(3)((operands, mods) => Instruction.SLshl3AddU32(operands(0), operands(1), operands(2), mods))),
        1115
      )
  case object SLshl4AddU32 extends Definition(
        "s_lshl4_add_u32",
        decoder(form(3)((operands, mods) => Instruction.SLshl4AddU32(operands(0), operands(1), operands(2), mods))),
        1116
      )
  case object SLshlB32 extends Definition(
        "s_lshl_b32",
        decoder(form(3)((operands, mods) => Instruction.SLshlB32(operands(0), operands(1), operands(2), mods))),
        1117
      )
  case object SLshlB64 extends Definition(
        "s_lshl_b64",
        decoder(form(3)((operands, mods) => Instruction.SLshlB64(operands(0), operands(1), operands(2), mods))),
        1118
      )
  case object SLshrB32 extends Definition(
        "s_lshr_b32",
        decoder(form(3)((operands, mods) => Instruction.SLshrB32(operands(0), operands(1), operands(2), mods))),
        1119
      )
  case object SLshrB64 extends Definition(
        "s_lshr_b64",
        decoder(form(3)((operands, mods) => Instruction.SLshrB64(operands(0), operands(1), operands(2), mods))),
        1120
      )
  case object SMaxF16 extends Definition(
        "s_max_f16",
        decoder(form(3)((operands, mods) => Instruction.SMaxF16(operands(0), operands(1), operands(2), mods))),
        1121
      )
  case object SMaxF32 extends Definition(
        "s_max_f32",
        decoder(form(3)((operands, mods) => Instruction.SMaxF32(operands(0), operands(1), operands(2), mods))),
        1122
      )
  case object SMaxI32 extends Definition(
        "s_max_i32",
        decoder(form(3)((operands, mods) => Instruction.SMaxI32(operands(0), operands(1), operands(2), mods))),
        1123
      )
  case object SMaxNumF16 extends Definition(
        "s_max_num_f16",
        decoder(form(3)((operands, mods) => Instruction.SMaxNumF16(operands(0), operands(1), operands(2), mods))),
        1124
      )
  case object SMaxNumF32 extends Definition(
        "s_max_num_f32",
        decoder(form(3)((operands, mods) => Instruction.SMaxNumF32(operands(0), operands(1), operands(2), mods))),
        1125
      )
  case object SMaxU32 extends Definition(
        "s_max_u32",
        decoder(form(3)((operands, mods) => Instruction.SMaxU32(operands(0), operands(1), operands(2), mods))),
        1126
      )
  case object SMaximumF16 extends Definition(
        "s_maximum_f16",
        decoder(form(3)((operands, mods) => Instruction.SMaximumF16(operands(0), operands(1), operands(2), mods))),
        1127
      )
  case object SMaximumF32 extends Definition(
        "s_maximum_f32",
        decoder(form(3)((operands, mods) => Instruction.SMaximumF32(operands(0), operands(1), operands(2), mods))),
        1128
      )
  case object SMinF16 extends Definition(
        "s_min_f16",
        decoder(form(3)((operands, mods) => Instruction.SMinF16(operands(0), operands(1), operands(2), mods))),
        1131
      )
  case object SMinF32 extends Definition(
        "s_min_f32",
        decoder(form(3)((operands, mods) => Instruction.SMinF32(operands(0), operands(1), operands(2), mods))),
        1132
      )
  case object SMinI32 extends Definition(
        "s_min_i32",
        decoder(form(3)((operands, mods) => Instruction.SMinI32(operands(0), operands(1), operands(2), mods))),
        1133
      )
  case object SMinNumF16 extends Definition(
        "s_min_num_f16",
        decoder(form(3)((operands, mods) => Instruction.SMinNumF16(operands(0), operands(1), operands(2), mods))),
        1134
      )
  case object SMinNumF32 extends Definition(
        "s_min_num_f32",
        decoder(form(3)((operands, mods) => Instruction.SMinNumF32(operands(0), operands(1), operands(2), mods))),
        1135
      )
  case object SMinU32 extends Definition(
        "s_min_u32",
        decoder(form(3)((operands, mods) => Instruction.SMinU32(operands(0), operands(1), operands(2), mods))),
        1136
      )
  case object SMinimumF16 extends Definition(
        "s_minimum_f16",
        decoder(form(3)((operands, mods) => Instruction.SMinimumF16(operands(0), operands(1), operands(2), mods))),
        1137
      )
  case object SMinimumF32 extends Definition(
        "s_minimum_f32",
        decoder(form(3)((operands, mods) => Instruction.SMinimumF32(operands(0), operands(1), operands(2), mods))),
        1138
      )
  case object SMulF16 extends Definition(
        "s_mul_f16",
        decoder(form(3)((operands, mods) => Instruction.SMulF16(operands(0), operands(1), operands(2), mods))),
        1149
      )
  case object SMulF32 extends Definition(
        "s_mul_f32",
        decoder(form(3)((operands, mods) => Instruction.SMulF32(operands(0), operands(1), operands(2), mods))),
        1150
      )
  case object SMulHiI32 extends Definition(
        "s_mul_hi_i32",
        decoder(form(3)((operands, mods) => Instruction.SMulHiI32(operands(0), operands(1), operands(2), mods))),
        1151
      )
  case object SMulHiU32 extends Definition(
        "s_mul_hi_u32",
        decoder(form(3)((operands, mods) => Instruction.SMulHiU32(operands(0), operands(1), operands(2), mods))),
        1152
      )
  case object SMulI32 extends Definition(
        "s_mul_i32",
        decoder(form(3)((operands, mods) => Instruction.SMulI32(operands(0), operands(1), operands(2), mods))),
        1153
      )
  case object SMulU64 extends Definition(
        "s_mul_u64",
        decoder(form(3)((operands, mods) => Instruction.SMulU64(operands(0), operands(1), operands(2), mods))),
        1154
      )
  case object SNandB32 extends Definition(
        "s_nand_b32",
        decoder(form(3)((operands, mods) => Instruction.SNandB32(operands(0), operands(1), operands(2), mods))),
        1156
      )
  case object SNandB64 extends Definition(
        "s_nand_b64",
        decoder(form(3)((operands, mods) => Instruction.SNandB64(operands(0), operands(1), operands(2), mods))),
        1157
      )
  case object SNorB32 extends Definition(
        "s_nor_b32",
        decoder(form(3)((operands, mods) => Instruction.SNorB32(operands(0), operands(1), operands(2), mods))),
        1161
      )
  case object SNorB64 extends Definition(
        "s_nor_b64",
        decoder(form(3)((operands, mods) => Instruction.SNorB64(operands(0), operands(1), operands(2), mods))),
        1162
      )
  case object SOrB32 extends Definition(
        "s_or_b32",
        decoder(form(3)((operands, mods) => Instruction.SOrB32(operands(0), operands(1), operands(2), mods))),
        1167
      )
  case object SOrB64 extends Definition(
        "s_or_b64",
        decoder(form(3)((operands, mods) => Instruction.SOrB64(operands(0), operands(1), operands(2), mods))),
        1168
      )
  case object SOrNot1B32 extends Definition(
        "s_or_not1_b32",
        decoder(form(3)((operands, mods) => Instruction.SOrNot1B32(operands(0), operands(1), operands(2), mods))),
        1171
      )
  case object SOrNot1B64 extends Definition(
        "s_or_not1_b64",
        decoder(form(3)((operands, mods) => Instruction.SOrNot1B64(operands(0), operands(1), operands(2), mods))),
        1172
      )
  case object SOrn2B32 extends Definition(
        "s_orn2_b32",
        decoder(form(3)((operands, mods) => Instruction.SOrn2B32(operands(0), operands(1), operands(2), mods))),
        1179
      )
  case object SOrn2B64 extends Definition(
        "s_orn2_b64",
        decoder(form(3)((operands, mods) => Instruction.SOrn2B64(operands(0), operands(1), operands(2), mods))),
        1180
      )
  case object SPackHhB32B16 extends Definition(
        "s_pack_hh_b32_b16",
        decoder(form(3)((operands, mods) => Instruction.SPackHhB32B16(operands(0), operands(1), operands(2), mods))),
        1183
      )
  case object SPackHlB32B16 extends Definition(
        "s_pack_hl_b32_b16",
        decoder(form(3)((operands, mods) => Instruction.SPackHlB32B16(operands(0), operands(1), operands(2), mods))),
        1184
      )
  case object SPackLhB32B16 extends Definition(
        "s_pack_lh_b32_b16",
        decoder(form(3)((operands, mods) => Instruction.SPackLhB32B16(operands(0), operands(1), operands(2), mods))),
        1185
      )
  case object SPackLlB32B16 extends Definition(
        "s_pack_ll_b32_b16",
        decoder(form(3)((operands, mods) => Instruction.SPackLlB32B16(operands(0), operands(1), operands(2), mods))),
        1186
      )
  case object SRfeRestoreB64 extends Definition(
        "s_rfe_restore_b64",
        decoder(form(2)((operands, mods) => Instruction.SRfeRestoreB64(operands(0), operands(1), mods))),
        1195
      )
  case object SSubCoCiU32 extends Definition(
        "s_sub_co_ci_u32",
        decoder(form(3)((operands, mods) => Instruction.SSubCoCiU32(operands(0), operands(1), operands(2), mods))),
        1232
      )
  case object SSubCoI32 extends Definition(
        "s_sub_co_i32",
        decoder(form(3)((operands, mods) => Instruction.SSubCoI32(operands(0), operands(1), operands(2), mods))),
        1233
      )
  case object SSubCoU32 extends Definition(
        "s_sub_co_u32",
        decoder(form(3)((operands, mods) => Instruction.SSubCoU32(operands(0), operands(1), operands(2), mods))),
        1234
      )
  case object SSubF16 extends Definition(
        "s_sub_f16",
        decoder(form(3)((operands, mods) => Instruction.SSubF16(operands(0), operands(1), operands(2), mods))),
        1235
      )
  case object SSubF32 extends Definition(
        "s_sub_f32",
        decoder(form(3)((operands, mods) => Instruction.SSubF32(operands(0), operands(1), operands(2), mods))),
        1236
      )
  case object SSubI32 extends Definition(
        "s_sub_i32",
        decoder(form(3)((operands, mods) => Instruction.SSubI32(operands(0), operands(1), operands(2), mods))),
        1237
      )
  case object SSubNcU64 extends Definition(
        "s_sub_nc_u64",
        decoder(form(3)((operands, mods) => Instruction.SSubNcU64(operands(0), operands(1), operands(2), mods))),
        1238
      )
  case object SSubU32 extends Definition(
        "s_sub_u32",
        decoder(form(3)((operands, mods) => Instruction.SSubU32(operands(0), operands(1), operands(2), mods))),
        1239
      )
  case object SSubbU32 extends Definition(
        "s_subb_u32",
        decoder(form(3)((operands, mods) => Instruction.SSubbU32(operands(0), operands(1), operands(2), mods))),
        1240
      )
  case object SXnorB32 extends Definition(
        "s_xnor_b32",
        decoder(form(3)((operands, mods) => Instruction.SXnorB32(operands(0), operands(1), operands(2), mods))),
        1276
      )
  case object SXnorB64 extends Definition(
        "s_xnor_b64",
        decoder(form(3)((operands, mods) => Instruction.SXnorB64(operands(0), operands(1), operands(2), mods))),
        1277
      )
  case object SXorB32 extends Definition(
        "s_xor_b32",
        decoder(form(3)((operands, mods) => Instruction.SXorB32(operands(0), operands(1), operands(2), mods))),
        1280
      )
  case object SXorB64 extends Definition(
        "s_xor_b64",
        decoder(form(3)((operands, mods) => Instruction.SXorB64(operands(0), operands(1), operands(2), mods))),
        1281
      )

  val definitions: Vec[Definition] = Vec(
    SAbsdiffI32,
    SAddCoCiU32,
    SAddCoI32,
    SAddCoU32,
    SAddF16,
    SAddF32,
    SAddI32,
    SAddNcU64,
    SAddU32,
    SAddcU32,
    SAndB32,
    SAndB64,
    SAndNot1B32,
    SAndNot1B64,
    SAndn2B32,
    SAndn2B64,
    SAshrI32,
    SAshrI64,
    SBfeI32,
    SBfeI64,
    SBfeU32,
    SBfeU64,
    SBfmB32,
    SBfmB64,
    SCbranchGFork,
    SCselectB32,
    SCselectB64,
    SCvtPkRtzF16F32,
    SFmaakF32,
    SFmacF16,
    SFmacF32,
    SFmamkF32,
    SLshl1AddU32,
    SLshl2AddU32,
    SLshl3AddU32,
    SLshl4AddU32,
    SLshlB32,
    SLshlB64,
    SLshrB32,
    SLshrB64,
    SMaxF16,
    SMaxF32,
    SMaxI32,
    SMaxNumF16,
    SMaxNumF32,
    SMaxU32,
    SMaximumF16,
    SMaximumF32,
    SMinF16,
    SMinF32,
    SMinI32,
    SMinNumF16,
    SMinNumF32,
    SMinU32,
    SMinimumF16,
    SMinimumF32,
    SMulF16,
    SMulF32,
    SMulHiI32,
    SMulHiU32,
    SMulI32,
    SMulU64,
    SNandB32,
    SNandB64,
    SNorB32,
    SNorB64,
    SOrB32,
    SOrB64,
    SOrNot1B32,
    SOrNot1B64,
    SOrn2B32,
    SOrn2B64,
    SPackHhB32B16,
    SPackHlB32B16,
    SPackLhB32B16,
    SPackLlB32B16,
    SRfeRestoreB64,
    SSubCoCiU32,
    SSubCoI32,
    SSubCoU32,
    SSubF16,
    SSubF32,
    SSubI32,
    SSubNcU64,
    SSubU32,
    SSubbU32,
    SXnorB32,
    SXnorB64,
    SXorB32,
    SXorB64
  )
}
