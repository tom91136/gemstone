// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
// scalafix:off GemstoneNoAssume; values were validated by the generator
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec

private[amdgpu] object InterpolationInstructionDefs {
  case object VInterpP10F16F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_interp_p10_f16_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VInterpP10F16F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1917
      )
  case object VInterpP10F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_interp_p10_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VInterpP10F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1918
      )
  case object VInterpP10RtzF16F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_interp_p10_rtz_f16_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VInterpP10RtzF16F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1919
      )
  case object VInterpP2F16F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_interp_p2_f16_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VInterpP2F16F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1924
      )
  case object VInterpP2RtzF16F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_interp_p2_rtz_f16_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VInterpP2RtzF16F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1928
      )

  val definitions: Vec[InstructionDef] =
    Vec(VInterpP10F16F32, VInterpP10F32, VInterpP10RtzF16F32, VInterpP2F16F32, VInterpP2RtzF16F32)
}
