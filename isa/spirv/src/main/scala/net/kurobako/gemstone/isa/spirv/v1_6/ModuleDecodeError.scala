package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.NonNegative

enum ModuleDecodeError {
  case Instruction(index: NonNegative[Int], error: InstructionDecodeError)

  def message: String = this match {
    case Instruction(index, error) => s"instruction $index: ${error.message}"
  }
}
