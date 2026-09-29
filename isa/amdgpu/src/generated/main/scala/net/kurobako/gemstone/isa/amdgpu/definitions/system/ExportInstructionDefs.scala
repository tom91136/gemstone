// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*
import Instruction.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private object ExportInstructionDefs {
  case object Exp extends Definition(
        "exp",
        decoder(form(5)((operands, mods) =>
          Instruction.Exp(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        402
      )
  case object Export extends Definition(
        "export",
        decoder(form(5)((operands, mods) =>
          Instruction.Export(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        403
      )

  val definitions: Vec[Definition] = Vec(Exp, Export)
}
