package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.constraint.*

/** An operand of the instruction at `instruction` in its module that breaks `constraint`. */
case class OperandViolation(instruction: Int, opcode: String, operand: String, constraint: OperandConstraint) {
  def message: NonEmpty[String] = s"instruction $instruction $opcode $operand must be ${constraint.describe}"
}
