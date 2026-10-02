// Generated from the pinned AMDGPU machine metadata. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import upickle.default.*

object InstructionGroup {

  given ReadWriter[InstructionGroup] = readwriter[String].bimap(_.toString, valueOf(_))
  private lazy val byName: Map[String, InstructionGroup] = values.view.map(value => value.toString -> value).toMap
  def fromName(name: String): Option[InstructionGroup] = byName.get(name)
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum InstructionGroup {
  case `ATOMIC`
  case `BUFFER`
  case `BVH`
  case `DATA_SHARE`
  case `FLAT`
  case `FLOATING_POINT`
  case `LOAD`
  case `MFMA`
  case `NOT_ASSIGNED`
  case `SAMPLE`
  case `STATIC`
  case `STORE`
  case `SWMMAC`
  case `TEXTURE`
  case `TRANSCENDENTAL`
  case `WMMA`

}
