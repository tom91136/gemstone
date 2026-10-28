// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@Generated(Array("sbt codegen"))
private object InterpolationInstructionDefs {
  case object VInterpP10F16F32 extends Opcode("v_interp_p10_f16_f32", 1963)

  case object VInterpP10F16F32Inreg extends Opcode("v_interp_p10_f16_f32_inreg", 1964)

  case object VInterpP10F32 extends Opcode("v_interp_p10_f32", 1965)

  case object VInterpP10F32Inreg extends Opcode("v_interp_p10_f32_inreg", 1966)

  case object VInterpP10RtzF16F32 extends Opcode("v_interp_p10_rtz_f16_f32", 1967)

  case object VInterpP10RtzF16F32Inreg extends Opcode("v_interp_p10_rtz_f16_f32_inreg", 1968)

  case object VInterpP2F16F32 extends Opcode("v_interp_p2_f16_f32", 1973)

  case object VInterpP2F16F32Inreg extends Opcode("v_interp_p2_f16_f32_inreg", 1974)

  case object VInterpP2F32Inreg extends Opcode("v_interp_p2_f32_inreg", 1976)

  case object VInterpP2RtzF16F32 extends Opcode("v_interp_p2_rtz_f16_f32", 1979)

  case object VInterpP2RtzF16F32Inreg extends Opcode("v_interp_p2_rtz_f16_f32_inreg", 1980)

  val definitions: Vec[Opcode] = Vec(
    VInterpP10F16F32,
    VInterpP10F16F32Inreg,
    VInterpP10F32,
    VInterpP10F32Inreg,
    VInterpP10RtzF16F32,
    VInterpP10RtzF16F32Inreg,
    VInterpP2F16F32,
    VInterpP2F16F32Inreg,
    VInterpP2F32Inreg,
    VInterpP2RtzF16F32,
    VInterpP2RtzF16F32Inreg
  )
}
