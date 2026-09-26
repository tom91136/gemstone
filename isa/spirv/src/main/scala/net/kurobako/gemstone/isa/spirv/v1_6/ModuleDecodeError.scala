package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.NonNegativeInt

enum ModuleDecodeError {
  case Instruction(index: NonNegativeInt, error: InstructionDecodeError)

  def message: String = this match {
    case Instruction(index, error) => s"instruction $index: ${error.message}"
  }
}
