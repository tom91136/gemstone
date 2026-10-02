// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object DualVectorInstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    DualVectorInstructionDefs.VDualAddF32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualAddF32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualAddF64 ->
      decoder(form(3)((operands, mods) => Instruction.VDualAddF64(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualAddNcU32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualAddNcU32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualAndB32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualAndB32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualAshrrevI32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualAshrrevI32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualBitop2B32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualBitop2B32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualCndmaskB32 -> decoder(
      form(3)((operands, mods) => Instruction.VDualCndmaskB32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.VDualCndmaskB32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    DualVectorInstructionDefs.VDualDot2accF32Bf16 -> decoder(form(3)((operands, mods) =>
      Instruction.VDualDot2accF32Bf16(operands(0), operands(1), operands(2), mods)
    )),
    DualVectorInstructionDefs.VDualDot2accF32F16 ->
      decoder(form(3)((operands, mods) => Instruction.VDualDot2accF32F16(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualFmaF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VDualFmaF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DualVectorInstructionDefs.VDualFmaF64 -> decoder(form(4)((operands, mods) =>
      Instruction.VDualFmaF64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DualVectorInstructionDefs.VDualFmaakF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VDualFmaakF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DualVectorInstructionDefs.VDualFmacF32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualFmacF32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualFmamkF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VDualFmamkF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DualVectorInstructionDefs.VDualLshlrevB32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualLshlrevB32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualLshrrevB32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualLshrrevB32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualMaxF32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualMaxF32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualMaxI32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualMaxI32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualMaxNumF32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualMaxNumF32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualMaxNumF64 ->
      decoder(form(3)((operands, mods) => Instruction.VDualMaxNumF64(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualMinF32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualMinF32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualMinI32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualMinI32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualMinNumF32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualMinNumF32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualMinNumF64 ->
      decoder(form(3)((operands, mods) => Instruction.VDualMinNumF64(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualMovB32 ->
      decoder(form(2)((operands, mods) => Instruction.VDualMovB32(operands(0), operands(1), mods))),
    DualVectorInstructionDefs.VDualMulDx9ZeroF32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualMulDx9ZeroF32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualMulF32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualMulF32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualMulF64 ->
      decoder(form(3)((operands, mods) => Instruction.VDualMulF64(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualSubF32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualSubF32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualSubNcU32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualSubNcU32(operands(0), operands(1), operands(2), mods))),
    DualVectorInstructionDefs.VDualSubrevF32 ->
      decoder(form(3)((operands, mods) => Instruction.VDualSubrevF32(operands(0), operands(1), operands(2), mods)))
  )
}
