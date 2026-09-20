// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
// scalafix:off GemstoneNoAssume; values were validated by the generator
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec

private[amdgpu] object LDSDirectInstructionDefs {
  case object LdsDirectLoad extends InstructionDef(
        AMDGPUOpcode.assume("lds_direct_load"),
        decoder(form(1)((operands, mods) => Instruction.LdsDirectLoad(operands(0), mods))),
        825
      )
  case object LdsParamLoad extends InstructionDef(
        AMDGPUOpcode.assume("lds_param_load"),
        decoder(form(2)((operands, mods) => Instruction.LdsParamLoad(operands(0), operands(1), mods))),
        826
      )

  val definitions: Vec[InstructionDef] = Vec(LdsDirectLoad, LdsParamLoad)
}
