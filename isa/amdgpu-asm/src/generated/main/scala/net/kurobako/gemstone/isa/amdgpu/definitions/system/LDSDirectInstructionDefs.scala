// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private object LDSDirectInstructionDefs {
  case object LdsDirectLoad extends Opcode("lds_direct_load", 826)

  case object LdsParamLoad extends Opcode("lds_param_load", 827)

  val definitions: Vec[Opcode] = Vec(LdsDirectLoad, LdsParamLoad)
}
