package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

case class RawInstruction(opcodeNumber: SPIRVOpcodeNumber, operandWords: Vec[Word]) {
  def wordCount: Int > 0 = 1 + operandWords.nonNegativeSize
}
