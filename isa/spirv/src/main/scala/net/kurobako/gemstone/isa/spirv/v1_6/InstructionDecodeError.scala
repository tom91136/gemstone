package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.NonEmptyVec

enum InstructionDecodeError {
  case UnknownOpcode(opcodeNumber: SPIRVOpcodeNumber)
  case InvalidOperandCount(opcode: NonEmptyString, detail: String)
  case InvalidEnumerant(kind: NonEmptyString, value: Long)
  case UnterminatedString(opcode: NonEmptyString)
  case DecoderOpcodeMismatch(expected: SPIRVOpcodeNumber, actual: SPIRVOpcodeNumber)
  case NoMatchingDefinition(opcodeNumber: SPIRVOpcodeNumber, candidates: NonEmptyVec[NonEmptyString])
  case AmbiguousDefinition(opcodeNumber: SPIRVOpcodeNumber, matches: NonEmptyVec[NonEmptyString])

  def message: String = this match {
    case UnknownOpcode(number)                    => s"unknown SPIR-V opcode $number"
    case InvalidOperandCount(opcode, detail)      => s"$opcode operands: $detail"
    case InvalidEnumerant(kind, value)            => s"invalid $kind enumerant $value"
    case UnterminatedString(opcode)               => s"$opcode contains an unterminated literal string"
    case DecoderOpcodeMismatch(expected, actual)  => s"decoder for opcode $expected cannot decode opcode $actual"
    case NoMatchingDefinition(number, candidates) =>
      s"opcode $number matched no definition among ${candidates.toVector.mkString(", ")}"
    case AmbiguousDefinition(number, matches) =>
      s"opcode $number ambiguously matched ${matches.toVector.mkString(", ")}"
  }
}
