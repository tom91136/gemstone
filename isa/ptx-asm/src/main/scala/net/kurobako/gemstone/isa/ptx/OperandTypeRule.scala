package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*

/** One row of the operand type table: the positions of an instruction's operands whose types the instruction type does
  * not fix.
  */
case class OperandTypeRule(
    opcodes: Set[String],
    any: Set[String],
    fill: Option[String],
    at: Map[Int, String],
    section: String
)
