package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

enum InstructionDecodeError {
  case UnknownOpcode(opcodeNumber: SPIRVOpcodeNumber)
  case InvalidOperandCount(opcode: NonEmpty[String], detail: NonEmpty[String])
  case InvalidEnumerant(kind: OperandKind, value: Long)
  case UnterminatedString(opcode: NonEmpty[String])
  case DecoderOpcodeMismatch(expected: SPIRVOpcodeNumber, actual: SPIRVOpcodeNumber)
  case NoMatchingDefinition(opcodeNumber: SPIRVOpcodeNumber, candidates: NonEmpty[Vec[NonEmpty[String]]])
  case AmbiguousDefinition(opcodeNumber: SPIRVOpcodeNumber, matches: NonEmpty[Vec[NonEmpty[String]]])

  def message: NonEmpty[String] = this match {
    case UnknownOpcode(number)                    => s"unknown SPIR-V opcode $number"
    case InvalidOperandCount(opcode, detail)      => s"$opcode operands: $detail"
    case InvalidEnumerant(kind, value)            => s"invalid $kind enumerant $value"
    case UnterminatedString(opcode)               => s"$opcode contains an unterminated literal string"
    case DecoderOpcodeMismatch(expected, actual)  => s"decoder for opcode $expected cannot decode opcode $actual"
    case NoMatchingDefinition(number, candidates) =>
      s"opcode $number matched no definition among ${candidates.mkString(", ")}"
    case AmbiguousDefinition(number, matches) =>
      s"opcode $number ambiguously matched ${matches.mkString(", ")}"
  }
}
