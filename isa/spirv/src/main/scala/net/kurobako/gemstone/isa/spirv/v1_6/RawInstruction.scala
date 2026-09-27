package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.Positive
import net.kurobako.gemstone.Vec

case class RawInstruction(opcodeNumber: SPIRVOpcodeNumber, operandWords: Vec[Word]) {
  def wordCount: Positive[Int] = Positive.unsafe(operandWords.size + 1)
}
