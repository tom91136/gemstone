package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.collection.*

case class InstructionSignature(opcode: Opcode, modifiers: Vec[Modifier]) {
  def matches(expected: Opcode, qualifiers: Modifier*): Boolean = opcode == expected && modifiers == qualifiers
}
