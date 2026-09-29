package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.constraint.*

enum ModuleDecodeError {
  case Instruction(index: Int >= 0, error: InstructionDecodeError)

  def message: NonEmpty[String] = this match {
    case Instruction(index, error) => s"instruction $index: ${error.message}"
  }
}
