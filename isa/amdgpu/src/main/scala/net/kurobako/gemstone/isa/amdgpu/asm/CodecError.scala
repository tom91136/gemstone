package net.kurobako.gemstone.isa.amdgpu.asm

import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*

enum CodecError {
  case UnsupportedInstruction(opcode: NonEmpty[String], target: Target)
  case UnsupportedFormat(opcode: NonEmpty[String], target: Target, formats: Vec[EncodingFormat])
  case UnsupportedOperandSyntax(opcode: NonEmpty[String], target: Target, operands: Vec[Operand])
  case AmbiguousInstruction(opcode: NonEmpty[String], target: Target)
  case InvalidOperand(message: NonEmpty[String])
  case InvalidEncoding(message: NonEmpty[String])
}
