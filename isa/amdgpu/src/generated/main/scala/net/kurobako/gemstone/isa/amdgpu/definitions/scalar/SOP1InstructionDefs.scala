// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private object SOP1InstructionDefs {
  case object SAbsI32 extends Opcode("s_abs_i32", 828)

  case object SAddPcI64 extends Opcode("s_add_pc_i64", 837)

  case object SAllocVgpr extends Opcode("s_alloc_vgpr", 842)

  case object SAndNot0SaveexecB32 extends Opcode("s_and_not0_saveexec_b32", 845)

  case object SAndNot0SaveexecB64 extends Opcode("s_and_not0_saveexec_b64", 846)

  case object SAndNot0WrexecB32 extends Opcode("s_and_not0_wrexec_b32", 847)

  case object SAndNot0WrexecB64 extends Opcode("s_and_not0_wrexec_b64", 848)

  case object SAndNot1SaveexecB32 extends Opcode("s_and_not1_saveexec_b32", 851)

  case object SAndNot1SaveexecB64 extends Opcode("s_and_not1_saveexec_b64", 852)

  case object SAndNot1WrexecB32 extends Opcode("s_and_not1_wrexec_b32", 853)

  case object SAndNot1WrexecB64 extends Opcode("s_and_not1_wrexec_b64", 854)

  case object SAndSaveexecB32 extends Opcode("s_and_saveexec_b32", 855)

  case object SAndSaveexecB64 extends Opcode("s_and_saveexec_b64", 856)

  case object SAndn1SaveexecB32 extends Opcode("s_andn1_saveexec_b32", 857)

  case object SAndn1SaveexecB64 extends Opcode("s_andn1_saveexec_b64", 858)

  case object SAndn1WrexecB32 extends Opcode("s_andn1_wrexec_b32", 859)

  case object SAndn1WrexecB64 extends Opcode("s_andn1_wrexec_b64", 860)

  case object SAndn2SaveexecB32 extends Opcode("s_andn2_saveexec_b32", 863)

  case object SAndn2SaveexecB64 extends Opcode("s_andn2_saveexec_b64", 864)

  case object SAndn2WrexecB32 extends Opcode("s_andn2_wrexec_b32", 865)

  case object SAndn2WrexecB64 extends Opcode("s_andn2_wrexec_b64", 866)

  case object SBarrierInit extends Opcode("s_barrier_init", 898)

  case object SBarrierJoin extends Opcode("s_barrier_join", 899)

  case object SBarrierSignal extends Opcode("s_barrier_signal", 901)

  case object SBarrierSignalIsfirst extends Opcode("s_barrier_signal_isfirst", 902)

  case object SBcnt0I32B32 extends Opcode("s_bcnt0_i32_b32", 904)

  case object SBcnt0I32B64 extends Opcode("s_bcnt0_i32_b64", 905)

  case object SBcnt1I32B32 extends Opcode("s_bcnt1_i32_b32", 906)

  case object SBcnt1I32B64 extends Opcode("s_bcnt1_i32_b64", 907)

  case object SBitreplicateB64B32 extends Opcode("s_bitreplicate_b64_b32", 918)

  case object SBitset0B32 extends Opcode("s_bitset0_b32", 919)

  case object SBitset0B64 extends Opcode("s_bitset0_b64", 920)

  case object SBitset1B32 extends Opcode("s_bitset1_b32", 921)

  case object SBitset1B64 extends Opcode("s_bitset1_b64", 922)

  case object SBrevB32 extends Opcode("s_brev_b32", 924)

  case object SBrevB64 extends Opcode("s_brev_b64", 925)

  case object SCbranchJoin extends Opcode("s_cbranch_join", 981)

  case object SCeilF16 extends Opcode("s_ceil_f16", 986)

  case object SCeilF32 extends Opcode("s_ceil_f32", 987)

  case object SClsI32 extends Opcode("s_cls_i32", 989)

  case object SClsI32I64 extends Opcode("s_cls_i32_i64", 990)

  case object SClzI32U32 extends Opcode("s_clz_i32_u32", 991)

  case object SClzI32U64 extends Opcode("s_clz_i32_u64", 992)

  case object SCmovB32 extends Opcode("s_cmov_b32", 993)

  case object SCmovB64 extends Opcode("s_cmov_b64", 994)

  case object SCtzI32B32 extends Opcode("s_ctz_i32_b32", 1053)

  case object SCtzI32B64 extends Opcode("s_ctz_i32_b64", 1054)

  case object SCvtF16F32 extends Opcode("s_cvt_f16_f32", 1055)

  case object SCvtF32F16 extends Opcode("s_cvt_f32_f16", 1056)

  case object SCvtF32I32 extends Opcode("s_cvt_f32_i32", 1057)

  case object SCvtF32U32 extends Opcode("s_cvt_f32_u32", 1058)

  case object SCvtHiF32F16 extends Opcode("s_cvt_hi_f32_f16", 1059)

  case object SCvtI32F32 extends Opcode("s_cvt_i32_f32", 1060)

  case object SCvtU32F32 extends Opcode("s_cvt_u32_f32", 1062)

  case object SFf0I32B32 extends Opcode("s_ff0_i32_b32", 1075)

  case object SFf0I32B64 extends Opcode("s_ff0_i32_b64", 1076)

  case object SFf1I32B32 extends Opcode("s_ff1_i32_b32", 1077)

  case object SFf1I32B64 extends Opcode("s_ff1_i32_b64", 1078)

  case object SFlbitI32 extends Opcode("s_flbit_i32", 1079)

  case object SFlbitI32B32 extends Opcode("s_flbit_i32_b32", 1080)

  case object SFlbitI32B64 extends Opcode("s_flbit_i32_b64", 1081)

  case object SFlbitI32I64 extends Opcode("s_flbit_i32_i64", 1082)

  case object SFloorF16 extends Opcode("s_floor_f16", 1083)

  case object SFloorF32 extends Opcode("s_floor_f32", 1084)

  case object SGetBarrierState extends Opcode("s_get_barrier_state", 1089)

  case object SGetPcI64 extends Opcode("s_get_pc_i64", 1090)

  case object SGetShaderCyclesU64 extends Opcode("s_get_shader_cycles_u64", 1091)

  case object SGetpcB64 extends Opcode("s_getpc_b64", 1093)

  case object SMovB32 extends Opcode("s_mov_b32", 1141)

  case object SMovB64 extends Opcode("s_mov_b64", 1142)

  case object SMovFedB32 extends Opcode("s_mov_fed_b32", 1143)

  case object SMovreldB32 extends Opcode("s_movreld_b32", 1145)

  case object SMovreldB64 extends Opcode("s_movreld_b64", 1146)

  case object SMovrelsB32 extends Opcode("s_movrels_b32", 1147)

  case object SMovrelsB64 extends Opcode("s_movrels_b64", 1148)

  case object SMovrelsd2B32 extends Opcode("s_movrelsd_2_b32", 1149)

  case object SNandSaveexecB32 extends Opcode("s_nand_saveexec_b32", 1159)

  case object SNandSaveexecB64 extends Opcode("s_nand_saveexec_b64", 1160)

  case object SNorSaveexecB32 extends Opcode("s_nor_saveexec_b32", 1164)

  case object SNorSaveexecB64 extends Opcode("s_nor_saveexec_b64", 1165)

  case object SNotB32 extends Opcode("s_not_b32", 1166)

  case object SNotB64 extends Opcode("s_not_b64", 1167)

  case object SOrNot0SaveexecB32 extends Opcode("s_or_not0_saveexec_b32", 1170)

  case object SOrNot0SaveexecB64 extends Opcode("s_or_not0_saveexec_b64", 1171)

  case object SOrNot1SaveexecB32 extends Opcode("s_or_not1_saveexec_b32", 1174)

  case object SOrNot1SaveexecB64 extends Opcode("s_or_not1_saveexec_b64", 1175)

  case object SOrSaveexecB32 extends Opcode("s_or_saveexec_b32", 1176)

  case object SOrSaveexecB64 extends Opcode("s_or_saveexec_b64", 1177)

  case object SOrn1SaveexecB32 extends Opcode("s_orn1_saveexec_b32", 1178)

  case object SOrn1SaveexecB64 extends Opcode("s_orn1_saveexec_b64", 1179)

  case object SOrn2SaveexecB32 extends Opcode("s_orn2_saveexec_b32", 1182)

  case object SOrn2SaveexecB64 extends Opcode("s_orn2_saveexec_b64", 1183)

  case object SQuadmaskB32 extends Opcode("s_quadmask_b32", 1192)

  case object SQuadmaskB64 extends Opcode("s_quadmask_b64", 1193)

  case object SRfeB64 extends Opcode("s_rfe_b64", 1194)

  case object SRfeI64 extends Opcode("s_rfe_i64", 1195)

  case object SRndneF16 extends Opcode("s_rndne_f16", 1197)

  case object SRndneF32 extends Opcode("s_rndne_f32", 1198)

  case object SSendmsgRtnB32 extends Opcode("s_sendmsg_rtn_b32", 1207)

  case object SSendmsgRtnB64 extends Opcode("s_sendmsg_rtn_b64", 1208)

  case object SSetGprIdxIdx extends Opcode("s_set_gpr_idx_idx", 1210)

  case object SSetPcI64 extends Opcode("s_set_pc_i64", 1215)

  case object SSetpcB64 extends Opcode("s_setpc_b64", 1220)

  case object SSextI32I16 extends Opcode("s_sext_i32_i16", 1226)

  case object SSextI32I8 extends Opcode("s_sext_i32_i8", 1227)

  case object SSleepVar extends Opcode("s_sleep_var", 1230)

  case object SSwapPcI64 extends Opcode("s_swap_pc_i64", 1245)

  case object SSwappcB64 extends Opcode("s_swappc_b64", 1246)

  case object STruncF16 extends Opcode("s_trunc_f16", 1248)

  case object STruncF32 extends Opcode("s_trunc_f32", 1249)

  case object SWakeupBarrier extends Opcode("s_wakeup_barrier", 1275)

  case object SWqmB32 extends Opcode("s_wqm_b32", 1276)

  case object SWqmB64 extends Opcode("s_wqm_b64", 1277)

  case object SXnorSaveexecB32 extends Opcode("s_xnor_saveexec_b32", 1280)

  case object SXnorSaveexecB64 extends Opcode("s_xnor_saveexec_b64", 1281)

  case object SXorSaveexecB32 extends Opcode("s_xor_saveexec_b32", 1284)

  case object SXorSaveexecB64 extends Opcode("s_xor_saveexec_b64", 1285)

  val definitions: Vec[Opcode] = Vec(
    SAbsI32,
    SAddPcI64,
    SAllocVgpr,
    SAndNot0SaveexecB32,
    SAndNot0SaveexecB64,
    SAndNot0WrexecB32,
    SAndNot0WrexecB64,
    SAndNot1SaveexecB32,
    SAndNot1SaveexecB64,
    SAndNot1WrexecB32,
    SAndNot1WrexecB64,
    SAndSaveexecB32,
    SAndSaveexecB64,
    SAndn1SaveexecB32,
    SAndn1SaveexecB64,
    SAndn1WrexecB32,
    SAndn1WrexecB64,
    SAndn2SaveexecB32,
    SAndn2SaveexecB64,
    SAndn2WrexecB32,
    SAndn2WrexecB64,
    SBarrierInit,
    SBarrierJoin,
    SBarrierSignal,
    SBarrierSignalIsfirst,
    SBcnt0I32B32,
    SBcnt0I32B64,
    SBcnt1I32B32,
    SBcnt1I32B64,
    SBitreplicateB64B32,
    SBitset0B32,
    SBitset0B64,
    SBitset1B32,
    SBitset1B64,
    SBrevB32,
    SBrevB64,
    SCbranchJoin,
    SCeilF16,
    SCeilF32,
    SClsI32,
    SClsI32I64,
    SClzI32U32,
    SClzI32U64,
    SCmovB32,
    SCmovB64,
    SCtzI32B32,
    SCtzI32B64,
    SCvtF16F32,
    SCvtF32F16,
    SCvtF32I32,
    SCvtF32U32,
    SCvtHiF32F16,
    SCvtI32F32,
    SCvtU32F32,
    SFf0I32B32,
    SFf0I32B64,
    SFf1I32B32,
    SFf1I32B64,
    SFlbitI32,
    SFlbitI32B32,
    SFlbitI32B64,
    SFlbitI32I64,
    SFloorF16,
    SFloorF32,
    SGetBarrierState,
    SGetPcI64,
    SGetShaderCyclesU64,
    SGetpcB64,
    SMovB32,
    SMovB64,
    SMovFedB32,
    SMovreldB32,
    SMovreldB64,
    SMovrelsB32,
    SMovrelsB64,
    SMovrelsd2B32,
    SNandSaveexecB32,
    SNandSaveexecB64,
    SNorSaveexecB32,
    SNorSaveexecB64,
    SNotB32,
    SNotB64,
    SOrNot0SaveexecB32,
    SOrNot0SaveexecB64,
    SOrNot1SaveexecB32,
    SOrNot1SaveexecB64,
    SOrSaveexecB32,
    SOrSaveexecB64,
    SOrn1SaveexecB32,
    SOrn1SaveexecB64,
    SOrn2SaveexecB32,
    SOrn2SaveexecB64,
    SQuadmaskB32,
    SQuadmaskB64,
    SRfeB64,
    SRfeI64,
    SRndneF16,
    SRndneF32,
    SSendmsgRtnB32,
    SSendmsgRtnB64,
    SSetGprIdxIdx,
    SSetPcI64,
    SSetpcB64,
    SSextI32I16,
    SSextI32I8,
    SSleepVar,
    SSwapPcI64,
    SSwappcB64,
    STruncF16,
    STruncF32,
    SWakeupBarrier,
    SWqmB32,
    SWqmB64,
    SXnorSaveexecB32,
    SXnorSaveexecB64,
    SXorSaveexecB32,
    SXorSaveexecB64
  )
}
