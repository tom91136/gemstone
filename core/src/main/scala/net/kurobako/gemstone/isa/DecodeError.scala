package net.kurobako.gemstone.isa

import net.kurobako.gemstone.constraint.*

/** Why `opcode` did not decode in the instruction set named `isa`, shared by the assembly front ends. */
case class DecodeError(opcode: String, problem: DecodeProblem) {
  def message(isa: String): NonEmpty[String] = problem match {
    case DecodeProblem.UnknownOpcode                         => s"unknown $isa opcode '$opcode'"
    case DecodeProblem.InvalidOperandCount(expected, actual) =>
      s"$opcode expects ${expected.mkString(" or ")} operands, found $actual"
    case DecodeProblem.AmbiguousDefinitions(definitions) => s"$opcode ambiguously matched ${definitions.mkString(", ")}"
  }
}
