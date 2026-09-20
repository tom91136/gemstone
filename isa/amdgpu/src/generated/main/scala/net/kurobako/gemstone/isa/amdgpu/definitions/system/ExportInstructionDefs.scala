// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
// scalafix:off GemstoneNoAssume; values were validated by the generator
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec

private[amdgpu] object ExportInstructionDefs {
  case object Exp extends InstructionDef(
        AMDGPUOpcode.assume("exp"),
        decoder(form(5)((operands, mods) =>
          Instruction.Exp(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        402
      )
  case object Export extends InstructionDef(
        AMDGPUOpcode.assume("export"),
        decoder(form(5)((operands, mods) =>
          Instruction.Export(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        403
      )

  val definitions: Vec[InstructionDef] = Vec(Exp, Export)
}
