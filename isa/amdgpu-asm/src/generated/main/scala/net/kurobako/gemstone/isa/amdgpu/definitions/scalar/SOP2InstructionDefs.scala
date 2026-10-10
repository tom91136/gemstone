// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private object SOP2InstructionDefs {
  case object SAbsdiffI32 extends Opcode("s_absdiff_i32", 829)

  case object SAddCoCiU32 extends Opcode("s_add_co_ci_u32", 830)

  case object SAddCoI32 extends Opcode("s_add_co_i32", 831)

  case object SAddCoU32 extends Opcode("s_add_co_u32", 832)

  case object SAddF16 extends Opcode("s_add_f16", 833)

  case object SAddF32 extends Opcode("s_add_f32", 834)

  case object SAddI32 extends Opcode("s_add_i32", 835)

  case object SAddNcU64 extends Opcode("s_add_nc_u64", 836)

  case object SAddU32 extends Opcode("s_add_u32", 838)

  case object SAddcU32 extends Opcode("s_addc_u32", 839)

  case object SAndB32 extends Opcode("s_and_b32", 843)

  case object SAndB64 extends Opcode("s_and_b64", 844)

  case object SAndNot1B32 extends Opcode("s_and_not1_b32", 849)

  case object SAndNot1B64 extends Opcode("s_and_not1_b64", 850)

  case object SAndn2B32 extends Opcode("s_andn2_b32", 861)

  case object SAndn2B64 extends Opcode("s_andn2_b64", 862)

  case object SAshrI32 extends Opcode("s_ashr_i32", 867)

  case object SAshrI64 extends Opcode("s_ashr_i64", 868)

  case object SBfeI32 extends Opcode("s_bfe_i32", 908)

  case object SBfeI64 extends Opcode("s_bfe_i64", 909)

  case object SBfeU32 extends Opcode("s_bfe_u32", 910)

  case object SBfeU64 extends Opcode("s_bfe_u64", 911)

  case object SBfmB32 extends Opcode("s_bfm_b32", 912)

  case object SBfmB64 extends Opcode("s_bfm_b64", 913)

  case object SCbranchGFork extends Opcode("s_cbranch_g_fork", 979)

  case object SCselectB32 extends Opcode("s_cselect_b32", 1051)

  case object SCselectB64 extends Opcode("s_cselect_b64", 1052)

  case object SCvtPkRtzF16F32 extends Opcode("s_cvt_pk_rtz_f16_f32", 1061)

  case object SFmaakF32 extends Opcode("s_fmaak_f32", 1085)

  case object SFmacF16 extends Opcode("s_fmac_f16", 1086)

  case object SFmacF32 extends Opcode("s_fmac_f32", 1087)

  case object SFmamkF32 extends Opcode("s_fmamk_f32", 1088)

  case object SLshl1AddU32 extends Opcode("s_lshl1_add_u32", 1114)

  case object SLshl2AddU32 extends Opcode("s_lshl2_add_u32", 1115)

  case object SLshl3AddU32 extends Opcode("s_lshl3_add_u32", 1116)

  case object SLshl4AddU32 extends Opcode("s_lshl4_add_u32", 1117)

  case object SLshlB32 extends Opcode("s_lshl_b32", 1118)

  case object SLshlB64 extends Opcode("s_lshl_b64", 1119)

  case object SLshrB32 extends Opcode("s_lshr_b32", 1120)

  case object SLshrB64 extends Opcode("s_lshr_b64", 1121)

  case object SMaxF16 extends Opcode("s_max_f16", 1122)

  case object SMaxF32 extends Opcode("s_max_f32", 1123)

  case object SMaxI32 extends Opcode("s_max_i32", 1124)

  case object SMaxNumF16 extends Opcode("s_max_num_f16", 1125)

  case object SMaxNumF32 extends Opcode("s_max_num_f32", 1126)

  case object SMaxU32 extends Opcode("s_max_u32", 1127)

  case object SMaximumF16 extends Opcode("s_maximum_f16", 1128)

  case object SMaximumF32 extends Opcode("s_maximum_f32", 1129)

  case object SMinF16 extends Opcode("s_min_f16", 1132)

  case object SMinF32 extends Opcode("s_min_f32", 1133)

  case object SMinI32 extends Opcode("s_min_i32", 1134)

  case object SMinNumF16 extends Opcode("s_min_num_f16", 1135)

  case object SMinNumF32 extends Opcode("s_min_num_f32", 1136)

  case object SMinU32 extends Opcode("s_min_u32", 1137)

  case object SMinimumF16 extends Opcode("s_minimum_f16", 1138)

  case object SMinimumF32 extends Opcode("s_minimum_f32", 1139)

  case object SMulF16 extends Opcode("s_mul_f16", 1150)

  case object SMulF32 extends Opcode("s_mul_f32", 1151)

  case object SMulHiI32 extends Opcode("s_mul_hi_i32", 1152)

  case object SMulHiU32 extends Opcode("s_mul_hi_u32", 1153)

  case object SMulI32 extends Opcode("s_mul_i32", 1154)

  case object SMulU64 extends Opcode("s_mul_u64", 1155)

  case object SNandB32 extends Opcode("s_nand_b32", 1157)

  case object SNandB64 extends Opcode("s_nand_b64", 1158)

  case object SNorB32 extends Opcode("s_nor_b32", 1162)

  case object SNorB64 extends Opcode("s_nor_b64", 1163)

  case object SOrB32 extends Opcode("s_or_b32", 1168)

  case object SOrB64 extends Opcode("s_or_b64", 1169)

  case object SOrNot1B32 extends Opcode("s_or_not1_b32", 1172)

  case object SOrNot1B64 extends Opcode("s_or_not1_b64", 1173)

  case object SOrn2B32 extends Opcode("s_orn2_b32", 1180)

  case object SOrn2B64 extends Opcode("s_orn2_b64", 1181)

  case object SPackHhB32B16 extends Opcode("s_pack_hh_b32_b16", 1184)

  case object SPackHlB32B16 extends Opcode("s_pack_hl_b32_b16", 1185)

  case object SPackLhB32B16 extends Opcode("s_pack_lh_b32_b16", 1186)

  case object SPackLlB32B16 extends Opcode("s_pack_ll_b32_b16", 1187)

  case object SRfeRestoreB64 extends Opcode("s_rfe_restore_b64", 1196)

  case object SSubCoCiU32 extends Opcode("s_sub_co_ci_u32", 1234)

  case object SSubCoI32 extends Opcode("s_sub_co_i32", 1235)

  case object SSubCoU32 extends Opcode("s_sub_co_u32", 1236)

  case object SSubF16 extends Opcode("s_sub_f16", 1237)

  case object SSubF32 extends Opcode("s_sub_f32", 1238)

  case object SSubI32 extends Opcode("s_sub_i32", 1239)

  case object SSubNcU64 extends Opcode("s_sub_nc_u64", 1240)

  case object SSubU32 extends Opcode("s_sub_u32", 1241)

  case object SSubbU32 extends Opcode("s_subb_u32", 1242)

  case object SXnorB32 extends Opcode("s_xnor_b32", 1278)

  case object SXnorB64 extends Opcode("s_xnor_b64", 1279)

  case object SXorB32 extends Opcode("s_xor_b32", 1282)

  case object SXorB64 extends Opcode("s_xor_b64", 1283)

  val definitions: Vec[Opcode] = Vec(
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
