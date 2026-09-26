package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.{PositiveInt, Vec}

final case class RawInstruction(opcodeNumber: SPIRVOpcodeNumber, operandWords: Vec[Word]) {
  def wordCount: PositiveInt = PositiveInt.unsafe(operandWords.size + 1)
}
