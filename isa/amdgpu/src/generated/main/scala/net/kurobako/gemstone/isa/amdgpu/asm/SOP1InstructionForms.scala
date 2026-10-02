// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object SOP1InstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    SOP1InstructionDefs.SAbsI32 ->
      decoder(form(2)((operands, mods) => Instruction.SAbsI32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAddPcI64 -> decoder(form(1)((operands, mods) => Instruction.SAddPcI64(operands(0), mods))),
    SOP1InstructionDefs.SAllocVgpr -> decoder(form(1)((operands, mods) => Instruction.SAllocVgpr(operands(0), mods))),
    SOP1InstructionDefs.SAndNot0SaveexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SAndNot0SaveexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndNot0SaveexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SAndNot0SaveexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndNot0WrexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SAndNot0WrexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndNot0WrexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SAndNot0WrexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndNot1SaveexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SAndNot1SaveexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndNot1SaveexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SAndNot1SaveexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndNot1WrexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SAndNot1WrexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndNot1WrexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SAndNot1WrexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndSaveexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SAndSaveexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndSaveexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SAndSaveexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndn1SaveexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SAndn1SaveexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndn1SaveexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SAndn1SaveexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndn1WrexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SAndn1WrexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndn1WrexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SAndn1WrexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndn2SaveexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SAndn2SaveexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndn2SaveexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SAndn2SaveexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndn2WrexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SAndn2WrexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SAndn2WrexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SAndn2WrexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SBarrierInit ->
      decoder(form(1)((operands, mods) => Instruction.SBarrierInit(operands(0), mods))),
    SOP1InstructionDefs.SBarrierJoin ->
      decoder(form(1)((operands, mods) => Instruction.SBarrierJoin(operands(0), mods))),
    SOP1InstructionDefs.SBarrierSignal ->
      decoder(form(1)((operands, mods) => Instruction.SBarrierSignal(operands(0), mods))),
    SOP1InstructionDefs.SBarrierSignalIsfirst ->
      decoder(form(1)((operands, mods) => Instruction.SBarrierSignalIsfirst(operands(0), mods))),
    SOP1InstructionDefs.SBcnt0I32B32 ->
      decoder(form(2)((operands, mods) => Instruction.SBcnt0I32B32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SBcnt0I32B64 ->
      decoder(form(2)((operands, mods) => Instruction.SBcnt0I32B64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SBcnt1I32B32 ->
      decoder(form(2)((operands, mods) => Instruction.SBcnt1I32B32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SBcnt1I32B64 ->
      decoder(form(2)((operands, mods) => Instruction.SBcnt1I32B64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SBitreplicateB64B32 ->
      decoder(form(2)((operands, mods) => Instruction.SBitreplicateB64B32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SBitset0B32 ->
      decoder(form(2)((operands, mods) => Instruction.SBitset0B32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SBitset0B64 ->
      decoder(form(2)((operands, mods) => Instruction.SBitset0B64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SBitset1B32 ->
      decoder(form(2)((operands, mods) => Instruction.SBitset1B32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SBitset1B64 ->
      decoder(form(2)((operands, mods) => Instruction.SBitset1B64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SBrevB32 ->
      decoder(form(2)((operands, mods) => Instruction.SBrevB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SBrevB64 ->
      decoder(form(2)((operands, mods) => Instruction.SBrevB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SCbranchJoin ->
      decoder(form(1)((operands, mods) => Instruction.SCbranchJoin(operands(0), mods))),
    SOP1InstructionDefs.SCeilF16 ->
      decoder(form(2)((operands, mods) => Instruction.SCeilF16(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SCeilF32 ->
      decoder(form(2)((operands, mods) => Instruction.SCeilF32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SClsI32 ->
      decoder(form(2)((operands, mods) => Instruction.SClsI32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SClsI32I64 ->
      decoder(form(2)((operands, mods) => Instruction.SClsI32I64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SClzI32U32 ->
      decoder(form(2)((operands, mods) => Instruction.SClzI32U32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SClzI32U64 ->
      decoder(form(2)((operands, mods) => Instruction.SClzI32U64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SCmovB32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmovB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SCmovB64 ->
      decoder(form(2)((operands, mods) => Instruction.SCmovB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SCtzI32B32 ->
      decoder(form(2)((operands, mods) => Instruction.SCtzI32B32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SCtzI32B64 ->
      decoder(form(2)((operands, mods) => Instruction.SCtzI32B64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SCvtF16F32 ->
      decoder(form(2)((operands, mods) => Instruction.SCvtF16F32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SCvtF32F16 ->
      decoder(form(2)((operands, mods) => Instruction.SCvtF32F16(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SCvtF32I32 ->
      decoder(form(2)((operands, mods) => Instruction.SCvtF32I32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SCvtF32U32 ->
      decoder(form(2)((operands, mods) => Instruction.SCvtF32U32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SCvtHiF32F16 ->
      decoder(form(2)((operands, mods) => Instruction.SCvtHiF32F16(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SCvtI32F32 ->
      decoder(form(2)((operands, mods) => Instruction.SCvtI32F32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SCvtU32F32 ->
      decoder(form(2)((operands, mods) => Instruction.SCvtU32F32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SFf0I32B32 ->
      decoder(form(2)((operands, mods) => Instruction.SFf0I32B32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SFf0I32B64 ->
      decoder(form(2)((operands, mods) => Instruction.SFf0I32B64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SFf1I32B32 ->
      decoder(form(2)((operands, mods) => Instruction.SFf1I32B32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SFf1I32B64 ->
      decoder(form(2)((operands, mods) => Instruction.SFf1I32B64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SFlbitI32 ->
      decoder(form(2)((operands, mods) => Instruction.SFlbitI32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SFlbitI32B32 ->
      decoder(form(2)((operands, mods) => Instruction.SFlbitI32B32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SFlbitI32B64 ->
      decoder(form(2)((operands, mods) => Instruction.SFlbitI32B64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SFlbitI32I64 ->
      decoder(form(2)((operands, mods) => Instruction.SFlbitI32I64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SFloorF16 ->
      decoder(form(2)((operands, mods) => Instruction.SFloorF16(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SFloorF32 ->
      decoder(form(2)((operands, mods) => Instruction.SFloorF32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SGetBarrierState ->
      decoder(form(2)((operands, mods) => Instruction.SGetBarrierState(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SGetPcI64 -> decoder(form(1)((operands, mods) => Instruction.SGetPcI64(operands(0), mods))),
    SOP1InstructionDefs.SGetShaderCyclesU64 ->
      decoder(form(1)((operands, mods) => Instruction.SGetShaderCyclesU64(operands(0), mods))),
    SOP1InstructionDefs.SGetpcB64 -> decoder(form(1)((operands, mods) => Instruction.SGetpcB64(operands(0), mods))),
    SOP1InstructionDefs.SMovB32 ->
      decoder(form(2)((operands, mods) => Instruction.SMovB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SMovB64 ->
      decoder(form(2)((operands, mods) => Instruction.SMovB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SMovFedB32 -> decoder(form(0)((_, mods) => Instruction.SMovFedB32(mods))),
    SOP1InstructionDefs.SMovreldB32 ->
      decoder(form(2)((operands, mods) => Instruction.SMovreldB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SMovreldB64 ->
      decoder(form(2)((operands, mods) => Instruction.SMovreldB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SMovrelsB32 ->
      decoder(form(2)((operands, mods) => Instruction.SMovrelsB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SMovrelsB64 ->
      decoder(form(2)((operands, mods) => Instruction.SMovrelsB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SMovrelsd2B32 ->
      decoder(form(2)((operands, mods) => Instruction.SMovrelsd2B32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SNandSaveexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SNandSaveexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SNandSaveexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SNandSaveexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SNorSaveexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SNorSaveexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SNorSaveexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SNorSaveexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SNotB32 ->
      decoder(form(2)((operands, mods) => Instruction.SNotB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SNotB64 ->
      decoder(form(2)((operands, mods) => Instruction.SNotB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SOrNot0SaveexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SOrNot0SaveexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SOrNot0SaveexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SOrNot0SaveexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SOrNot1SaveexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SOrNot1SaveexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SOrNot1SaveexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SOrNot1SaveexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SOrSaveexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SOrSaveexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SOrSaveexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SOrSaveexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SOrn1SaveexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SOrn1SaveexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SOrn1SaveexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SOrn1SaveexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SOrn2SaveexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SOrn2SaveexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SOrn2SaveexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SOrn2SaveexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SQuadmaskB32 ->
      decoder(form(2)((operands, mods) => Instruction.SQuadmaskB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SQuadmaskB64 ->
      decoder(form(2)((operands, mods) => Instruction.SQuadmaskB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SRfeB64 -> decoder(form(1)((operands, mods) => Instruction.SRfeB64(operands(0), mods))),
    SOP1InstructionDefs.SRfeI64 -> decoder(form(1)((operands, mods) => Instruction.SRfeI64(operands(0), mods))),
    SOP1InstructionDefs.SRndneF16 ->
      decoder(form(2)((operands, mods) => Instruction.SRndneF16(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SRndneF32 ->
      decoder(form(2)((operands, mods) => Instruction.SRndneF32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SSendmsgRtnB32 ->
      decoder(form(2)((operands, mods) => Instruction.SSendmsgRtnB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SSendmsgRtnB64 ->
      decoder(form(2)((operands, mods) => Instruction.SSendmsgRtnB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SSetGprIdxIdx ->
      decoder(form(1)((operands, mods) => Instruction.SSetGprIdxIdx(operands(0), mods))),
    SOP1InstructionDefs.SSetPcI64 -> decoder(form(1)((operands, mods) => Instruction.SSetPcI64(operands(0), mods))),
    SOP1InstructionDefs.SSetpcB64 -> decoder(form(1)((operands, mods) => Instruction.SSetpcB64(operands(0), mods))),
    SOP1InstructionDefs.SSextI32I16 ->
      decoder(form(2)((operands, mods) => Instruction.SSextI32I16(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SSextI32I8 ->
      decoder(form(2)((operands, mods) => Instruction.SSextI32I8(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SSleepVar -> decoder(form(1)((operands, mods) => Instruction.SSleepVar(operands(0), mods))),
    SOP1InstructionDefs.SSwapPcI64 ->
      decoder(form(2)((operands, mods) => Instruction.SSwapPcI64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SSwappcB64 ->
      decoder(form(2)((operands, mods) => Instruction.SSwappcB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.STruncF16 ->
      decoder(form(2)((operands, mods) => Instruction.STruncF16(operands(0), operands(1), mods))),
    SOP1InstructionDefs.STruncF32 ->
      decoder(form(2)((operands, mods) => Instruction.STruncF32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SWakeupBarrier ->
      decoder(form(1)((operands, mods) => Instruction.SWakeupBarrier(operands(0), mods))),
    SOP1InstructionDefs.SWqmB32 ->
      decoder(form(2)((operands, mods) => Instruction.SWqmB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SWqmB64 ->
      decoder(form(2)((operands, mods) => Instruction.SWqmB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SXnorSaveexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SXnorSaveexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SXnorSaveexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SXnorSaveexecB64(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SXorSaveexecB32 ->
      decoder(form(2)((operands, mods) => Instruction.SXorSaveexecB32(operands(0), operands(1), mods))),
    SOP1InstructionDefs.SXorSaveexecB64 ->
      decoder(form(2)((operands, mods) => Instruction.SXorSaveexecB64(operands(0), operands(1), mods)))
  )
}
