package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.Bits

opaque type Word = Int

object Word {
  def apply(bits: Int): Word = bits

  def unsigned(value: Long): Either[String, Word] =
    if (value < 0L || value > Bits.Mask32) Left(s"word is outside unsigned 32-bit range: $value")
    else Right(value.toInt)

  extension (word: Word) {
    def bits: Int        = word
    def toUnsigned: Long = Bits.unsigned32(word)
  }
}

enum Endianness {
  case Little
  case Big
}

final case class SPIRVVersion(major: Int, minor: Int) {
  require(major >= 0 && major <= 0xff)
  require(minor >= 0 && minor <= 0xff)

  def word: Word = Word((major << 16) | (minor << 8))
}

object SPIRVVersion {
  def fromWord(word: Word): SPIRVVersion = SPIRVVersion((word.bits >>> 16) & 0xff, (word.bits >>> 8) & 0xff)
}

final case class Header(version: SPIRVVersion, generator: Word, bound: Word, schema: Word)

/** Lossless binary-layer instruction. Opcode numbers unknown to this grammar deliberately live only here. */
final case class RawInstruction(opcodeNumber: Int, operandWords: Col[Word]) {
  require(opcodeNumber >= 0 && opcodeNumber <= 0xffff, s"opcode is outside unsigned 16-bit range: $opcodeNumber")
  def wordCount: Int = operandWords.size + 1
}

/** Lossless binary-layer module used by binary tools and forward-compatible recorders. */
final case class RawModule(header: Header, instructions: Col[RawInstruction], endianness: Endianness)

/** One grammar operand. Multi-word strings, pairs, masks with parameters, and context-dependent literals stay atomic.
  */
final case class Operand(kind: String, words: Col[Word]) {
  require(kind.nonEmpty, "operand kind must be non-empty")
  require(words.nonEmpty, s"$kind operand must contain at least one word")

  def word: Word = {
    require(words.size == 1, s"$kind contains ${words.size} words")
    words.head
  }

  def bits: Int        = word.bits
  def toUnsigned: Long = word.toUnsigned
}

/** Strict typed module. Every instruction is a generated `Instruction` enum case. */
final case class Module(header: Header, instructions: Col[Instruction], endianness: Endianness)

enum OperandQuantifier {
  case One
  case Optional
  case Repeated
}

final case class OperandShape(kind: String, name: String, quantifier: OperandQuantifier)
final case class OperandEnumerant(value: Long, parameterKinds: Col[String])
final case class OperandKindInfo(kind: String, category: String, enumerants: Col[OperandEnumerant])

final case class Metadata(
    instructionClass: String,
    since: Option[String],
    capabilities: Col[String],
    operands: Col[OperandShape]
)

enum InstructionDecodeError {
  case UnknownOpcode(opcodeNumber: Int)
  case InvalidOperandCount(opcode: String, detail: String)
  case InvalidEnumerant(kind: String, value: Long)
  case UnterminatedString(opcode: String)
  case DecoderOpcodeMismatch(expected: Int, actual: Int)
  case NoMatchingDefinition(opcodeNumber: Int, candidates: Col[String])
  case AmbiguousDefinition(opcodeNumber: Int, matches: Col[String])

  def message: String = this match {
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

enum ModuleDecodeError {
  case Instruction(index: Int, error: InstructionDecodeError)

  def message: String = this match {
    case Instruction(index, error) => s"instruction $index: ${error.message}"
  }
}
