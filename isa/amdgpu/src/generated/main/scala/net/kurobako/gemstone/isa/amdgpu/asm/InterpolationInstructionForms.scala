// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object InterpolationInstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    InterpolationInstructionDefs.VInterpP10F16F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VInterpP10F16F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    InterpolationInstructionDefs.VInterpP10F16F32Inreg -> decoder(form(4)((operands, mods) =>
      Instruction.VInterpP10F16F32Inreg(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    InterpolationInstructionDefs.VInterpP10F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VInterpP10F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    InterpolationInstructionDefs.VInterpP10F32Inreg -> decoder(form(4)((operands, mods) =>
      Instruction.VInterpP10F32Inreg(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    InterpolationInstructionDefs.VInterpP10RtzF16F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VInterpP10RtzF16F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    InterpolationInstructionDefs.VInterpP10RtzF16F32Inreg -> decoder(form(4)((operands, mods) =>
      Instruction.VInterpP10RtzF16F32Inreg(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    InterpolationInstructionDefs.VInterpP2F16F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VInterpP2F16F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    InterpolationInstructionDefs.VInterpP2F16F32Inreg -> decoder(form(4)((operands, mods) =>
      Instruction.VInterpP2F16F32Inreg(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    InterpolationInstructionDefs.VInterpP2F32Inreg -> decoder(form(4)((operands, mods) =>
      Instruction.VInterpP2F32Inreg(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    InterpolationInstructionDefs.VInterpP2RtzF16F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VInterpP2RtzF16F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    InterpolationInstructionDefs.VInterpP2RtzF16F32Inreg -> decoder(form(4)((operands, mods) =>
      Instruction.VInterpP2RtzF16F32Inreg(operands(0), operands(1), operands(2), operands(3), mods)
    ))
  )
}
