// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.Vec
import Instruction.*
import Program.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private[amdgpu] object ExportInstructionDefs {
  case object Exp extends Definition(
        NonEmptyString("exp"),
        decoder(form(5)((operands, mods) =>
          Instruction.Exp(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        402
      )
  case object Export extends Definition(
        NonEmptyString("export"),
        decoder(form(5)((operands, mods) =>
          Instruction.Export(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        403
      )

  val definitions: Vec[Definition] = Vec(Exp, Export)
}
