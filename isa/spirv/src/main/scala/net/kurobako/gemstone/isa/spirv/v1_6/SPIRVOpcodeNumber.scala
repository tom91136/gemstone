package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.constraint.*

/** A SPIR-V opcode number, from zero to 65535. */
type SPIRVOpcodeNumber = SPIRVOpcodeNumber.Type
object SPIRVOpcodeNumber extends Newtype[Int where Between[0, 65535]] {
  extension (value: SPIRVOpcodeNumber) def toInt: Int = value.raw
}
