// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*
import Instruction.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private object LDSDirectInstructionDefs {
  case object LdsDirectLoad extends Definition(
        "lds_direct_load",
        decoder(form(1)((operands, mods) => Instruction.LdsDirectLoad(operands(0), mods))),
        825
      )
  case object LdsParamLoad extends Definition(
        "lds_param_load",
        decoder(form(2)((operands, mods) => Instruction.LdsParamLoad(operands(0), operands(1), mods))),
        826
      )

  val definitions: Vec[Definition] = Vec(LdsDirectLoad, LdsParamLoad)
}
