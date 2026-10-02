// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object SOP2InstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    SOP2InstructionDefs.SAbsdiffI32 ->
      decoder(form(3)((operands, mods) => Instruction.SAbsdiffI32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAddCoCiU32 ->
      decoder(form(3)((operands, mods) => Instruction.SAddCoCiU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAddCoI32 ->
      decoder(form(3)((operands, mods) => Instruction.SAddCoI32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAddCoU32 ->
      decoder(form(3)((operands, mods) => Instruction.SAddCoU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAddF16 ->
      decoder(form(3)((operands, mods) => Instruction.SAddF16(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAddF32 ->
      decoder(form(3)((operands, mods) => Instruction.SAddF32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAddI32 ->
      decoder(form(3)((operands, mods) => Instruction.SAddI32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAddNcU64 ->
      decoder(form(3)((operands, mods) => Instruction.SAddNcU64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAddU32 ->
      decoder(form(3)((operands, mods) => Instruction.SAddU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAddcU32 ->
      decoder(form(3)((operands, mods) => Instruction.SAddcU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAndB32 ->
      decoder(form(3)((operands, mods) => Instruction.SAndB32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAndB64 ->
      decoder(form(3)((operands, mods) => Instruction.SAndB64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAndNot1B32 ->
      decoder(form(3)((operands, mods) => Instruction.SAndNot1B32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAndNot1B64 ->
      decoder(form(3)((operands, mods) => Instruction.SAndNot1B64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAndn2B32 ->
      decoder(form(3)((operands, mods) => Instruction.SAndn2B32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAndn2B64 ->
      decoder(form(3)((operands, mods) => Instruction.SAndn2B64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAshrI32 ->
      decoder(form(3)((operands, mods) => Instruction.SAshrI32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SAshrI64 ->
      decoder(form(3)((operands, mods) => Instruction.SAshrI64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SBfeI32 ->
      decoder(form(3)((operands, mods) => Instruction.SBfeI32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SBfeI64 ->
      decoder(form(3)((operands, mods) => Instruction.SBfeI64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SBfeU32 ->
      decoder(form(3)((operands, mods) => Instruction.SBfeU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SBfeU64 ->
      decoder(form(3)((operands, mods) => Instruction.SBfeU64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SBfmB32 ->
      decoder(form(3)((operands, mods) => Instruction.SBfmB32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SBfmB64 ->
      decoder(form(3)((operands, mods) => Instruction.SBfmB64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SCbranchGFork ->
      decoder(form(2)((operands, mods) => Instruction.SCbranchGFork(operands(0), operands(1), mods))),
    SOP2InstructionDefs.SCselectB32 ->
      decoder(form(3)((operands, mods) => Instruction.SCselectB32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SCselectB64 ->
      decoder(form(3)((operands, mods) => Instruction.SCselectB64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SCvtPkRtzF16F32 ->
      decoder(form(3)((operands, mods) => Instruction.SCvtPkRtzF16F32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SFmaakF32 -> decoder(form(4)((operands, mods) =>
      Instruction.SFmaakF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    SOP2InstructionDefs.SFmacF16 ->
      decoder(form(3)((operands, mods) => Instruction.SFmacF16(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SFmacF32 ->
      decoder(form(3)((operands, mods) => Instruction.SFmacF32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SFmamkF32 -> decoder(form(4)((operands, mods) =>
      Instruction.SFmamkF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    SOP2InstructionDefs.SLshl1AddU32 ->
      decoder(form(3)((operands, mods) => Instruction.SLshl1AddU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SLshl2AddU32 ->
      decoder(form(3)((operands, mods) => Instruction.SLshl2AddU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SLshl3AddU32 ->
      decoder(form(3)((operands, mods) => Instruction.SLshl3AddU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SLshl4AddU32 ->
      decoder(form(3)((operands, mods) => Instruction.SLshl4AddU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SLshlB32 ->
      decoder(form(3)((operands, mods) => Instruction.SLshlB32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SLshlB64 ->
      decoder(form(3)((operands, mods) => Instruction.SLshlB64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SLshrB32 ->
      decoder(form(3)((operands, mods) => Instruction.SLshrB32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SLshrB64 ->
      decoder(form(3)((operands, mods) => Instruction.SLshrB64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMaxF16 ->
      decoder(form(3)((operands, mods) => Instruction.SMaxF16(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMaxF32 ->
      decoder(form(3)((operands, mods) => Instruction.SMaxF32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMaxI32 ->
      decoder(form(3)((operands, mods) => Instruction.SMaxI32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMaxNumF16 ->
      decoder(form(3)((operands, mods) => Instruction.SMaxNumF16(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMaxNumF32 ->
      decoder(form(3)((operands, mods) => Instruction.SMaxNumF32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMaxU32 ->
      decoder(form(3)((operands, mods) => Instruction.SMaxU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMaximumF16 ->
      decoder(form(3)((operands, mods) => Instruction.SMaximumF16(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMaximumF32 ->
      decoder(form(3)((operands, mods) => Instruction.SMaximumF32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMinF16 ->
      decoder(form(3)((operands, mods) => Instruction.SMinF16(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMinF32 ->
      decoder(form(3)((operands, mods) => Instruction.SMinF32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMinI32 ->
      decoder(form(3)((operands, mods) => Instruction.SMinI32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMinNumF16 ->
      decoder(form(3)((operands, mods) => Instruction.SMinNumF16(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMinNumF32 ->
      decoder(form(3)((operands, mods) => Instruction.SMinNumF32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMinU32 ->
      decoder(form(3)((operands, mods) => Instruction.SMinU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMinimumF16 ->
      decoder(form(3)((operands, mods) => Instruction.SMinimumF16(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMinimumF32 ->
      decoder(form(3)((operands, mods) => Instruction.SMinimumF32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMulF16 ->
      decoder(form(3)((operands, mods) => Instruction.SMulF16(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMulF32 ->
      decoder(form(3)((operands, mods) => Instruction.SMulF32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMulHiI32 ->
      decoder(form(3)((operands, mods) => Instruction.SMulHiI32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMulHiU32 ->
      decoder(form(3)((operands, mods) => Instruction.SMulHiU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMulI32 ->
      decoder(form(3)((operands, mods) => Instruction.SMulI32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SMulU64 ->
      decoder(form(3)((operands, mods) => Instruction.SMulU64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SNandB32 ->
      decoder(form(3)((operands, mods) => Instruction.SNandB32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SNandB64 ->
      decoder(form(3)((operands, mods) => Instruction.SNandB64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SNorB32 ->
      decoder(form(3)((operands, mods) => Instruction.SNorB32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SNorB64 ->
      decoder(form(3)((operands, mods) => Instruction.SNorB64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SOrB32 ->
      decoder(form(3)((operands, mods) => Instruction.SOrB32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SOrB64 ->
      decoder(form(3)((operands, mods) => Instruction.SOrB64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SOrNot1B32 ->
      decoder(form(3)((operands, mods) => Instruction.SOrNot1B32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SOrNot1B64 ->
      decoder(form(3)((operands, mods) => Instruction.SOrNot1B64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SOrn2B32 ->
      decoder(form(3)((operands, mods) => Instruction.SOrn2B32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SOrn2B64 ->
      decoder(form(3)((operands, mods) => Instruction.SOrn2B64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SPackHhB32B16 ->
      decoder(form(3)((operands, mods) => Instruction.SPackHhB32B16(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SPackHlB32B16 ->
      decoder(form(3)((operands, mods) => Instruction.SPackHlB32B16(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SPackLhB32B16 ->
      decoder(form(3)((operands, mods) => Instruction.SPackLhB32B16(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SPackLlB32B16 ->
      decoder(form(3)((operands, mods) => Instruction.SPackLlB32B16(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SRfeRestoreB64 ->
      decoder(form(2)((operands, mods) => Instruction.SRfeRestoreB64(operands(0), operands(1), mods))),
    SOP2InstructionDefs.SSubCoCiU32 ->
      decoder(form(3)((operands, mods) => Instruction.SSubCoCiU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SSubCoI32 ->
      decoder(form(3)((operands, mods) => Instruction.SSubCoI32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SSubCoU32 ->
      decoder(form(3)((operands, mods) => Instruction.SSubCoU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SSubF16 ->
      decoder(form(3)((operands, mods) => Instruction.SSubF16(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SSubF32 ->
      decoder(form(3)((operands, mods) => Instruction.SSubF32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SSubI32 ->
      decoder(form(3)((operands, mods) => Instruction.SSubI32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SSubNcU64 ->
      decoder(form(3)((operands, mods) => Instruction.SSubNcU64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SSubU32 ->
      decoder(form(3)((operands, mods) => Instruction.SSubU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SSubbU32 ->
      decoder(form(3)((operands, mods) => Instruction.SSubbU32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SXnorB32 ->
      decoder(form(3)((operands, mods) => Instruction.SXnorB32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SXnorB64 ->
      decoder(form(3)((operands, mods) => Instruction.SXnorB64(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SXorB32 ->
      decoder(form(3)((operands, mods) => Instruction.SXorB32(operands(0), operands(1), operands(2), mods))),
    SOP2InstructionDefs.SXorB64 ->
      decoder(form(3)((operands, mods) => Instruction.SXorB64(operands(0), operands(1), operands(2), mods)))
  )
}
