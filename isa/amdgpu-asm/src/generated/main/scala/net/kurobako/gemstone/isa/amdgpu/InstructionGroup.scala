// Generated from the pinned AMDGPU machine metadata. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import javax.annotation.processing.Generated

import upickle.default.*

object InstructionGroup {

  given ReadWriter[InstructionGroup] = readwriter[String].bimap(_.toString, valueOf(_))
  private lazy val byName: Map[String, InstructionGroup] = values.view.map(value => value.toString -> value).toMap
  def fromName(name: String): Option[InstructionGroup] = byName.get(name)
}

@Generated(Array("sbt codegen"))
enum InstructionGroup derives CanEqual {
  case `ATOMIC`
  case `SWMMAC`
  case `WMMA`

}
