// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@Generated(Array("sbt codegen"))
private object ExportInstructionDefs {
  case object Exp extends Opcode("exp", 403)

  case object Export extends Opcode("export", 404)

  val definitions: Vec[Opcode] = Vec(Exp, Export)
}
