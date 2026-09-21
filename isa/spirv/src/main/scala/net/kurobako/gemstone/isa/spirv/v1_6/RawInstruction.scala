package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class RawInstruction(opcodeNumber: SPIRVOpcodeNumber, operandWords: Vec[Word]) {
  def wordCount: Int :| Positive = (operandWords.size + 1).refineUnsafe[Positive]
}
