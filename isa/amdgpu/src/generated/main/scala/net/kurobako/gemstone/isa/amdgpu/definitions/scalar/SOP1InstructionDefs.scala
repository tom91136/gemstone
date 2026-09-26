// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import Instruction.*
import Program.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private[amdgpu] object SOP1InstructionDefs {
  case object SAbsI32 extends Definition(
        Opcode("s_abs_i32"),
        decoder(form(2)((operands, mods) => Instruction.SAbsI32(operands(0), operands(1), mods))),
        827
      )
  case object SAddPcI64 extends Definition(
        Opcode("s_add_pc_i64"),
        decoder(form(1)((operands, mods) => Instruction.SAddPcI64(operands(0), mods))),
        836
      )
  case object SAllocVgpr extends Definition(
        Opcode("s_alloc_vgpr"),
        decoder(form(1)((operands, mods) => Instruction.SAllocVgpr(operands(0), mods))),
        841
      )
  case object SAndNot0SaveexecB32 extends Definition(
        Opcode("s_and_not0_saveexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SAndNot0SaveexecB32(operands(0), operands(1), mods))),
        844
      )
  case object SAndNot0SaveexecB64 extends Definition(
        Opcode("s_and_not0_saveexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SAndNot0SaveexecB64(operands(0), operands(1), mods))),
        845
      )
  case object SAndNot0WrexecB32 extends Definition(
        Opcode("s_and_not0_wrexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SAndNot0WrexecB32(operands(0), operands(1), mods))),
        846
      )
  case object SAndNot0WrexecB64 extends Definition(
        Opcode("s_and_not0_wrexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SAndNot0WrexecB64(operands(0), operands(1), mods))),
        847
      )
  case object SAndNot1SaveexecB32 extends Definition(
        Opcode("s_and_not1_saveexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SAndNot1SaveexecB32(operands(0), operands(1), mods))),
        850
      )
  case object SAndNot1SaveexecB64 extends Definition(
        Opcode("s_and_not1_saveexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SAndNot1SaveexecB64(operands(0), operands(1), mods))),
        851
      )
  case object SAndNot1WrexecB32 extends Definition(
        Opcode("s_and_not1_wrexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SAndNot1WrexecB32(operands(0), operands(1), mods))),
        852
      )
  case object SAndNot1WrexecB64 extends Definition(
        Opcode("s_and_not1_wrexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SAndNot1WrexecB64(operands(0), operands(1), mods))),
        853
      )
  case object SAndSaveexecB32 extends Definition(
        Opcode("s_and_saveexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SAndSaveexecB32(operands(0), operands(1), mods))),
        854
      )
  case object SAndSaveexecB64 extends Definition(
        Opcode("s_and_saveexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SAndSaveexecB64(operands(0), operands(1), mods))),
        855
      )
  case object SAndn1SaveexecB32 extends Definition(
        Opcode("s_andn1_saveexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SAndn1SaveexecB32(operands(0), operands(1), mods))),
        856
      )
  case object SAndn1SaveexecB64 extends Definition(
        Opcode("s_andn1_saveexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SAndn1SaveexecB64(operands(0), operands(1), mods))),
        857
      )
  case object SAndn1WrexecB32 extends Definition(
        Opcode("s_andn1_wrexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SAndn1WrexecB32(operands(0), operands(1), mods))),
        858
      )
  case object SAndn1WrexecB64 extends Definition(
        Opcode("s_andn1_wrexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SAndn1WrexecB64(operands(0), operands(1), mods))),
        859
      )
  case object SAndn2SaveexecB32 extends Definition(
        Opcode("s_andn2_saveexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SAndn2SaveexecB32(operands(0), operands(1), mods))),
        862
      )
  case object SAndn2SaveexecB64 extends Definition(
        Opcode("s_andn2_saveexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SAndn2SaveexecB64(operands(0), operands(1), mods))),
        863
      )
  case object SAndn2WrexecB32 extends Definition(
        Opcode("s_andn2_wrexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SAndn2WrexecB32(operands(0), operands(1), mods))),
        864
      )
  case object SAndn2WrexecB64 extends Definition(
        Opcode("s_andn2_wrexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SAndn2WrexecB64(operands(0), operands(1), mods))),
        865
      )
  case object SBarrierInit extends Definition(
        Opcode("s_barrier_init"),
        decoder(form(1)((operands, mods) => Instruction.SBarrierInit(operands(0), mods))),
        897
      )
  case object SBarrierJoin extends Definition(
        Opcode("s_barrier_join"),
        decoder(form(1)((operands, mods) => Instruction.SBarrierJoin(operands(0), mods))),
        898
      )
  case object SBarrierSignal extends Definition(
        Opcode("s_barrier_signal"),
        decoder(form(1)((operands, mods) => Instruction.SBarrierSignal(operands(0), mods))),
        900
      )
  case object SBarrierSignalIsfirst extends Definition(
        Opcode("s_barrier_signal_isfirst"),
        decoder(form(1)((operands, mods) => Instruction.SBarrierSignalIsfirst(operands(0), mods))),
        901
      )
  case object SBcnt0I32B32 extends Definition(
        Opcode("s_bcnt0_i32_b32"),
        decoder(form(2)((operands, mods) => Instruction.SBcnt0I32B32(operands(0), operands(1), mods))),
        903
      )
  case object SBcnt0I32B64 extends Definition(
        Opcode("s_bcnt0_i32_b64"),
        decoder(form(2)((operands, mods) => Instruction.SBcnt0I32B64(operands(0), operands(1), mods))),
        904
      )
  case object SBcnt1I32B32 extends Definition(
        Opcode("s_bcnt1_i32_b32"),
        decoder(form(2)((operands, mods) => Instruction.SBcnt1I32B32(operands(0), operands(1), mods))),
        905
      )
  case object SBcnt1I32B64 extends Definition(
        Opcode("s_bcnt1_i32_b64"),
        decoder(form(2)((operands, mods) => Instruction.SBcnt1I32B64(operands(0), operands(1), mods))),
        906
      )
  case object SBitreplicateB64B32 extends Definition(
        Opcode("s_bitreplicate_b64_b32"),
        decoder(form(2)((operands, mods) => Instruction.SBitreplicateB64B32(operands(0), operands(1), mods))),
        917
      )
  case object SBitset0B32 extends Definition(
        Opcode("s_bitset0_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.SBitset0B32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.SBitset0B32_3(operands(0), operands(1), operands(2), mods))
        ),
        918
      )
  case object SBitset0B64 extends Definition(
        Opcode("s_bitset0_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.SBitset0B64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.SBitset0B64_3(operands(0), operands(1), operands(2), mods))
        ),
        919
      )
  case object SBitset1B32 extends Definition(
        Opcode("s_bitset1_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.SBitset1B32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.SBitset1B32_3(operands(0), operands(1), operands(2), mods))
        ),
        920
      )
  case object SBitset1B64 extends Definition(
        Opcode("s_bitset1_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.SBitset1B64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.SBitset1B64_3(operands(0), operands(1), operands(2), mods))
        ),
        921
      )
  case object SBrevB32 extends Definition(
        Opcode("s_brev_b32"),
        decoder(form(2)((operands, mods) => Instruction.SBrevB32(operands(0), operands(1), mods))),
        923
      )
  case object SBrevB64 extends Definition(
        Opcode("s_brev_b64"),
        decoder(form(2)((operands, mods) => Instruction.SBrevB64(operands(0), operands(1), mods))),
        924
      )
  case object SCbranchJoin extends Definition(
        Opcode("s_cbranch_join"),
        decoder(form(1)((operands, mods) => Instruction.SCbranchJoin(operands(0), mods))),
        980
      )
  case object SCeilF16 extends Definition(
        Opcode("s_ceil_f16"),
        decoder(form(2)((operands, mods) => Instruction.SCeilF16(operands(0), operands(1), mods))),
        985
      )
  case object SCeilF32 extends Definition(
        Opcode("s_ceil_f32"),
        decoder(form(2)((operands, mods) => Instruction.SCeilF32(operands(0), operands(1), mods))),
        986
      )
  case object SClsI32 extends Definition(
        Opcode("s_cls_i32"),
        decoder(form(2)((operands, mods) => Instruction.SClsI32(operands(0), operands(1), mods))),
        988
      )
  case object SClsI32I64 extends Definition(
        Opcode("s_cls_i32_i64"),
        decoder(form(2)((operands, mods) => Instruction.SClsI32I64(operands(0), operands(1), mods))),
        989
      )
  case object SClzI32U32 extends Definition(
        Opcode("s_clz_i32_u32"),
        decoder(form(2)((operands, mods) => Instruction.SClzI32U32(operands(0), operands(1), mods))),
        990
      )
  case object SClzI32U64 extends Definition(
        Opcode("s_clz_i32_u64"),
        decoder(form(2)((operands, mods) => Instruction.SClzI32U64(operands(0), operands(1), mods))),
        991
      )
  case object SCmovB32 extends Definition(
        Opcode("s_cmov_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.SCmovB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.SCmovB32_3(operands(0), operands(1), operands(2), mods))
        ),
        992
      )
  case object SCmovB64 extends Definition(
        Opcode("s_cmov_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.SCmovB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.SCmovB64_3(operands(0), operands(1), operands(2), mods))
        ),
        993
      )
  case object SCtzI32B32 extends Definition(
        Opcode("s_ctz_i32_b32"),
        decoder(form(2)((operands, mods) => Instruction.SCtzI32B32(operands(0), operands(1), mods))),
        1052
      )
  case object SCtzI32B64 extends Definition(
        Opcode("s_ctz_i32_b64"),
        decoder(form(2)((operands, mods) => Instruction.SCtzI32B64(operands(0), operands(1), mods))),
        1053
      )
  case object SCvtF16F32 extends Definition(
        Opcode("s_cvt_f16_f32"),
        decoder(form(2)((operands, mods) => Instruction.SCvtF16F32(operands(0), operands(1), mods))),
        1054
      )
  case object SCvtF32F16 extends Definition(
        Opcode("s_cvt_f32_f16"),
        decoder(form(2)((operands, mods) => Instruction.SCvtF32F16(operands(0), operands(1), mods))),
        1055
      )
  case object SCvtF32I32 extends Definition(
        Opcode("s_cvt_f32_i32"),
        decoder(form(2)((operands, mods) => Instruction.SCvtF32I32(operands(0), operands(1), mods))),
        1056
      )
  case object SCvtF32U32 extends Definition(
        Opcode("s_cvt_f32_u32"),
        decoder(form(2)((operands, mods) => Instruction.SCvtF32U32(operands(0), operands(1), mods))),
        1057
      )
  case object SCvtHiF32F16 extends Definition(
        Opcode("s_cvt_hi_f32_f16"),
        decoder(form(2)((operands, mods) => Instruction.SCvtHiF32F16(operands(0), operands(1), mods))),
        1058
      )
  case object SCvtI32F32 extends Definition(
        Opcode("s_cvt_i32_f32"),
        decoder(form(2)((operands, mods) => Instruction.SCvtI32F32(operands(0), operands(1), mods))),
        1059
      )
  case object SCvtU32F32 extends Definition(
        Opcode("s_cvt_u32_f32"),
        decoder(form(2)((operands, mods) => Instruction.SCvtU32F32(operands(0), operands(1), mods))),
        1061
      )
  case object SFf0I32B32 extends Definition(
        Opcode("s_ff0_i32_b32"),
        decoder(form(2)((operands, mods) => Instruction.SFf0I32B32(operands(0), operands(1), mods))),
        1074
      )
  case object SFf0I32B64 extends Definition(
        Opcode("s_ff0_i32_b64"),
        decoder(form(2)((operands, mods) => Instruction.SFf0I32B64(operands(0), operands(1), mods))),
        1075
      )
  case object SFf1I32B32 extends Definition(
        Opcode("s_ff1_i32_b32"),
        decoder(form(2)((operands, mods) => Instruction.SFf1I32B32(operands(0), operands(1), mods))),
        1076
      )
  case object SFf1I32B64 extends Definition(
        Opcode("s_ff1_i32_b64"),
        decoder(form(2)((operands, mods) => Instruction.SFf1I32B64(operands(0), operands(1), mods))),
        1077
      )
  case object SFlbitI32 extends Definition(
        Opcode("s_flbit_i32"),
        decoder(form(2)((operands, mods) => Instruction.SFlbitI32(operands(0), operands(1), mods))),
        1078
      )
  case object SFlbitI32B32 extends Definition(
        Opcode("s_flbit_i32_b32"),
        decoder(form(2)((operands, mods) => Instruction.SFlbitI32B32(operands(0), operands(1), mods))),
        1079
      )
  case object SFlbitI32B64 extends Definition(
        Opcode("s_flbit_i32_b64"),
        decoder(form(2)((operands, mods) => Instruction.SFlbitI32B64(operands(0), operands(1), mods))),
        1080
      )
  case object SFlbitI32I64 extends Definition(
        Opcode("s_flbit_i32_i64"),
        decoder(form(2)((operands, mods) => Instruction.SFlbitI32I64(operands(0), operands(1), mods))),
        1081
      )
  case object SFloorF16 extends Definition(
        Opcode("s_floor_f16"),
        decoder(form(2)((operands, mods) => Instruction.SFloorF16(operands(0), operands(1), mods))),
        1082
      )
  case object SFloorF32 extends Definition(
        Opcode("s_floor_f32"),
        decoder(form(2)((operands, mods) => Instruction.SFloorF32(operands(0), operands(1), mods))),
        1083
      )
  case object SGetBarrierState extends Definition(
        Opcode("s_get_barrier_state"),
        decoder(form(2)((operands, mods) => Instruction.SGetBarrierState(operands(0), operands(1), mods))),
        1088
      )
  case object SGetPcI64 extends Definition(
        Opcode("s_get_pc_i64"),
        decoder(form(1)((operands, mods) => Instruction.SGetPcI64(operands(0), mods))),
        1089
      )
  case object SGetShaderCyclesU64 extends Definition(
        Opcode("s_get_shader_cycles_u64"),
        decoder(form(1)((operands, mods) => Instruction.SGetShaderCyclesU64(operands(0), mods))),
        1090
      )
  case object SGetpcB64 extends Definition(
        Opcode("s_getpc_b64"),
        decoder(form(1)((operands, mods) => Instruction.SGetpcB64(operands(0), mods))),
        1092
      )
  case object SMovB32 extends Definition(
        Opcode("s_mov_b32"),
        decoder(form(2)((operands, mods) => Instruction.SMovB32(operands(0), operands(1), mods))),
        1140
      )
  case object SMovB64 extends Definition(
        Opcode("s_mov_b64"),
        decoder(form(2)((operands, mods) => Instruction.SMovB64(operands(0), operands(1), mods))),
        1141
      )
  case object SMovFedB32 extends Definition(
        Opcode("s_mov_fed_b32"),
        decoder(form(0)((_, mods) => Instruction.SMovFedB32(mods))),
        1142
      )
  case object SMovreldB32 extends Definition(
        Opcode("s_movreld_b32"),
        decoder(form(2)((operands, mods) => Instruction.SMovreldB32(operands(0), operands(1), mods))),
        1144
      )
  case object SMovreldB64 extends Definition(
        Opcode("s_movreld_b64"),
        decoder(form(2)((operands, mods) => Instruction.SMovreldB64(operands(0), operands(1), mods))),
        1145
      )
  case object SMovrelsB32 extends Definition(
        Opcode("s_movrels_b32"),
        decoder(form(2)((operands, mods) => Instruction.SMovrelsB32(operands(0), operands(1), mods))),
        1146
      )
  case object SMovrelsB64 extends Definition(
        Opcode("s_movrels_b64"),
        decoder(form(2)((operands, mods) => Instruction.SMovrelsB64(operands(0), operands(1), mods))),
        1147
      )
  case object SMovrelsd2B32 extends Definition(
        Opcode("s_movrelsd_2_b32"),
        decoder(form(2)((operands, mods) => Instruction.SMovrelsd2B32(operands(0), operands(1), mods))),
        1148
      )
  case object SNandSaveexecB32 extends Definition(
        Opcode("s_nand_saveexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SNandSaveexecB32(operands(0), operands(1), mods))),
        1158
      )
  case object SNandSaveexecB64 extends Definition(
        Opcode("s_nand_saveexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SNandSaveexecB64(operands(0), operands(1), mods))),
        1159
      )
  case object SNorSaveexecB32 extends Definition(
        Opcode("s_nor_saveexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SNorSaveexecB32(operands(0), operands(1), mods))),
        1163
      )
  case object SNorSaveexecB64 extends Definition(
        Opcode("s_nor_saveexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SNorSaveexecB64(operands(0), operands(1), mods))),
        1164
      )
  case object SNotB32 extends Definition(
        Opcode("s_not_b32"),
        decoder(form(2)((operands, mods) => Instruction.SNotB32(operands(0), operands(1), mods))),
        1165
      )
  case object SNotB64 extends Definition(
        Opcode("s_not_b64"),
        decoder(form(2)((operands, mods) => Instruction.SNotB64(operands(0), operands(1), mods))),
        1166
      )
  case object SOrNot0SaveexecB32 extends Definition(
        Opcode("s_or_not0_saveexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SOrNot0SaveexecB32(operands(0), operands(1), mods))),
        1169
      )
  case object SOrNot0SaveexecB64 extends Definition(
        Opcode("s_or_not0_saveexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SOrNot0SaveexecB64(operands(0), operands(1), mods))),
        1170
      )
  case object SOrNot1SaveexecB32 extends Definition(
        Opcode("s_or_not1_saveexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SOrNot1SaveexecB32(operands(0), operands(1), mods))),
        1173
      )
  case object SOrNot1SaveexecB64 extends Definition(
        Opcode("s_or_not1_saveexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SOrNot1SaveexecB64(operands(0), operands(1), mods))),
        1174
      )
  case object SOrSaveexecB32 extends Definition(
        Opcode("s_or_saveexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SOrSaveexecB32(operands(0), operands(1), mods))),
        1175
      )
  case object SOrSaveexecB64 extends Definition(
        Opcode("s_or_saveexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SOrSaveexecB64(operands(0), operands(1), mods))),
        1176
      )
  case object SOrn1SaveexecB32 extends Definition(
        Opcode("s_orn1_saveexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SOrn1SaveexecB32(operands(0), operands(1), mods))),
        1177
      )
  case object SOrn1SaveexecB64 extends Definition(
        Opcode("s_orn1_saveexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SOrn1SaveexecB64(operands(0), operands(1), mods))),
        1178
      )
  case object SOrn2SaveexecB32 extends Definition(
        Opcode("s_orn2_saveexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SOrn2SaveexecB32(operands(0), operands(1), mods))),
        1181
      )
  case object SOrn2SaveexecB64 extends Definition(
        Opcode("s_orn2_saveexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SOrn2SaveexecB64(operands(0), operands(1), mods))),
        1182
      )
  case object SQuadmaskB32 extends Definition(
        Opcode("s_quadmask_b32"),
        decoder(form(2)((operands, mods) => Instruction.SQuadmaskB32(operands(0), operands(1), mods))),
        1191
      )
  case object SQuadmaskB64 extends Definition(
        Opcode("s_quadmask_b64"),
        decoder(form(2)((operands, mods) => Instruction.SQuadmaskB64(operands(0), operands(1), mods))),
        1192
      )
  case object SRfeB64 extends Definition(
        Opcode("s_rfe_b64"),
        decoder(form(1)((operands, mods) => Instruction.SRfeB64(operands(0), mods))),
        1193
      )
  case object SRfeI64 extends Definition(
        Opcode("s_rfe_i64"),
        decoder(form(1)((operands, mods) => Instruction.SRfeI64(operands(0), mods))),
        1194
      )
  case object SRndneF16 extends Definition(
        Opcode("s_rndne_f16"),
        decoder(form(2)((operands, mods) => Instruction.SRndneF16(operands(0), operands(1), mods))),
        1196
      )
  case object SRndneF32 extends Definition(
        Opcode("s_rndne_f32"),
        decoder(form(2)((operands, mods) => Instruction.SRndneF32(operands(0), operands(1), mods))),
        1197
      )
  case object SSendmsgRtnB32 extends Definition(
        Opcode("s_sendmsg_rtn_b32"),
        decoder(form(2)((operands, mods) => Instruction.SSendmsgRtnB32(operands(0), operands(1), mods))),
        1206
      )
  case object SSendmsgRtnB64 extends Definition(
        Opcode("s_sendmsg_rtn_b64"),
        decoder(form(2)((operands, mods) => Instruction.SSendmsgRtnB64(operands(0), operands(1), mods))),
        1207
      )
  case object SSetGprIdxIdx extends Definition(
        Opcode("s_set_gpr_idx_idx"),
        decoder(form(1)((operands, mods) => Instruction.SSetGprIdxIdx(operands(0), mods))),
        1209
      )
  case object SSetPcI64 extends Definition(
        Opcode("s_set_pc_i64"),
        decoder(form(1)((operands, mods) => Instruction.SSetPcI64(operands(0), mods))),
        1214
      )
  case object SSetpcB64 extends Definition(
        Opcode("s_setpc_b64"),
        decoder(form(1)((operands, mods) => Instruction.SSetpcB64(operands(0), mods))),
        1219
      )
  case object SSextI32I16 extends Definition(
        Opcode("s_sext_i32_i16"),
        decoder(form(2)((operands, mods) => Instruction.SSextI32I16(operands(0), operands(1), mods))),
        1225
      )
  case object SSextI32I8 extends Definition(
        Opcode("s_sext_i32_i8"),
        decoder(form(2)((operands, mods) => Instruction.SSextI32I8(operands(0), operands(1), mods))),
        1226
      )
  case object SSleepVar extends Definition(
        Opcode("s_sleep_var"),
        decoder(form(1)((operands, mods) => Instruction.SSleepVar(operands(0), mods))),
        1228
      )
  case object SSwapPcI64 extends Definition(
        Opcode("s_swap_pc_i64"),
        decoder(form(2)((operands, mods) => Instruction.SSwapPcI64(operands(0), operands(1), mods))),
        1243
      )
  case object SSwappcB64 extends Definition(
        Opcode("s_swappc_b64"),
        decoder(form(2)((operands, mods) => Instruction.SSwappcB64(operands(0), operands(1), mods))),
        1244
      )
  case object STruncF16 extends Definition(
        Opcode("s_trunc_f16"),
        decoder(form(2)((operands, mods) => Instruction.STruncF16(operands(0), operands(1), mods))),
        1246
      )
  case object STruncF32 extends Definition(
        Opcode("s_trunc_f32"),
        decoder(form(2)((operands, mods) => Instruction.STruncF32(operands(0), operands(1), mods))),
        1247
      )
  case object SWakeupBarrier extends Definition(
        Opcode("s_wakeup_barrier"),
        decoder(form(1)((operands, mods) => Instruction.SWakeupBarrier(operands(0), mods))),
        1273
      )
  case object SWqmB32 extends Definition(
        Opcode("s_wqm_b32"),
        decoder(form(2)((operands, mods) => Instruction.SWqmB32(operands(0), operands(1), mods))),
        1274
      )
  case object SWqmB64 extends Definition(
        Opcode("s_wqm_b64"),
        decoder(form(2)((operands, mods) => Instruction.SWqmB64(operands(0), operands(1), mods))),
        1275
      )
  case object SXnorSaveexecB32 extends Definition(
        Opcode("s_xnor_saveexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SXnorSaveexecB32(operands(0), operands(1), mods))),
        1278
      )
  case object SXnorSaveexecB64 extends Definition(
        Opcode("s_xnor_saveexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SXnorSaveexecB64(operands(0), operands(1), mods))),
        1279
      )
  case object SXorSaveexecB32 extends Definition(
        Opcode("s_xor_saveexec_b32"),
        decoder(form(2)((operands, mods) => Instruction.SXorSaveexecB32(operands(0), operands(1), mods))),
        1282
      )
  case object SXorSaveexecB64 extends Definition(
        Opcode("s_xor_saveexec_b64"),
        decoder(form(2)((operands, mods) => Instruction.SXorSaveexecB64(operands(0), operands(1), mods))),
        1283
      )

  val definitions: Vec[Definition] = Vec(
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
