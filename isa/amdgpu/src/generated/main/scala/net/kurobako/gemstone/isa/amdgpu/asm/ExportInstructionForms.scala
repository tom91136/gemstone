// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object ExportInstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    ExportInstructionDefs.Exp -> decoder(form(5)((operands, mods) =>
      Instruction.Exp(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    ExportInstructionDefs.Export -> decoder(form(5)((operands, mods) =>
      Instruction.Export(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    ))
  )
}
