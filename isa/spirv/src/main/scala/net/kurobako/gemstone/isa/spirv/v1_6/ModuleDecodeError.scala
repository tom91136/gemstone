package net.kurobako.gemstone.isa.spirv.v1_6

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

enum ModuleDecodeError {
  case Instruction(index: Int :| Positive0, error: InstructionDecodeError)

  def message: String = (this match {
    case Instruction(index, error) => s"instruction $index: ${error.message}"
  }).refineUnsafe[MinLength[1]]
}
