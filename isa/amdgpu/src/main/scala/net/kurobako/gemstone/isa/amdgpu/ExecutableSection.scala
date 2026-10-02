package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object ExecutableSection {
  case class Symbol(name: NonEmpty[String], offset: Int >= 0, size: Int >= 0)
}

case class ExecutableSection(name: String, address: Long, bytes: Vec[Byte], symbols: Vec[ExecutableSection.Symbol])
