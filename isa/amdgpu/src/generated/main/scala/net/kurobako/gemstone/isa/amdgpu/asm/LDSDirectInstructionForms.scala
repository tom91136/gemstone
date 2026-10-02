// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object LDSDirectInstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    LDSDirectInstructionDefs.LdsDirectLoad ->
      decoder(form(1)((operands, mods) => Instruction.LdsDirectLoad(operands(0), mods))),
    LDSDirectInstructionDefs.LdsParamLoad ->
      decoder(form(2)((operands, mods) => Instruction.LdsParamLoad(operands(0), operands(1), mods)))
  )
}
