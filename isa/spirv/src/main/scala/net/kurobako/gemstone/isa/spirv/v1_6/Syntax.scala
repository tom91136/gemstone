package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.Bits

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type SPIRVOpcode = SPIRVOpcode.T
object SPIRVOpcode extends RefinedType[String, MinLength[1] & NoNul]

type SPIRVOpcodeNumber = SPIRVOpcodeNumber.T
object SPIRVOpcodeNumber extends RefinedType[Int, Interval.Closed[0, 65535]]

type SPIRVOperandKind = SPIRVOperandKind.T
object SPIRVOperandKind extends RefinedType[String, MinLength[1] & NoNul]

type Word = Word.T
object Word extends RefinedType[Int, Pure] {
  def unsigned(value: Long): Either[String, Word] =
    if (value < 0L || value > Bits.Mask32)
      Left(s"word is outside unsigned 32-bit range: $value")
    else Right(Word(value.toInt))

  extension (word: Word) {
    def bits: Int                                            = word.value
    def toUnsigned: Long :| Interval.Closed[0L, 4294967295L] = Bits.unsigned32(word.value)
  }
}

enum Endianness {
  case Little
  case Big
}

final case class SPIRVVersion(
    major: Int :| Interval.Closed[0, 255],
    minor: Int :| Interval.Closed[0, 255]
) {
  def word: Word = Word((major << 16) | (minor << 8))
}

object SPIRVVersion {
  def fromWord(word: Word): SPIRVVersion =
    SPIRVVersion(
      ((word.bits >>> 16) & 0xff).refineUnsafe[Interval.Closed[0, 255]],
      ((word.bits >>> 8) & 0xff).refineUnsafe[Interval.Closed[0, 255]]
    )
}

final case class Header(version: SPIRVVersion, generator: Word, bound: Word, schema: Word)

/** Lossless binary-layer instruction. Opcode numbers unknown to this grammar deliberately live only here. */
final case class RawInstruction(opcodeNumber: SPIRVOpcodeNumber, operandWords: Vec[Word]) {
  def wordCount: Int :| Positive = (operandWords.size + 1).refineUnsafe[Positive]
}

/** Lossless binary-layer module used by binary tools and forward-compatible recorders. */
final case class RawModule(header: Header, instructions: Vec[RawInstruction], endianness: Endianness)

/** One grammar operand. Multi-word strings, pairs, masks with parameters, and context-dependent literals stay atomic.
  */
final case class Operand(kind: SPIRVOperandKind, words: Vec[Word] :| MinLength[1]) {
  def singleWord: Either[String, Word] =
    Either.cond(
      words.size == 1,
      words.head,
      s"$kind contains ${words.size} words"
    )
}

/** Strict typed module. Every instruction is a generated `Instruction` enum case. */
final case class Module(header: Header, instructions: Vec[Instruction], endianness: Endianness)

enum OperandQuantifier {
  case One
  case Optional
  case Repeated
}

final case class OperandShape(
    kind: SPIRVOperandKind,
    name: String :| (MinLength[1] & NoNul),
    quantifier: OperandQuantifier
)
final case class OperandEnumerant(value: Long, parameterKinds: Vec[SPIRVOperandKind])
final case class OperandKindInfo(
    kind: SPIRVOperandKind,
    category: String :| (MinLength[1] & NoNul),
    enumerants: Vec[OperandEnumerant]
)

final case class Metadata(
    instructionClass: String :| (MinLength[1] & NoNul),
    since: Option[String :| (MinLength[1] & NoNul)],
    capabilities: Vec[String :| (MinLength[1] & NoNul)],
    operands: Vec[OperandShape]
)

enum InstructionDecodeError {
  case UnknownOpcode(opcodeNumber: SPIRVOpcodeNumber)
  case InvalidOperandCount(
      opcode: SPIRVOpcode,
      detail: String
  )
  case InvalidEnumerant(kind: SPIRVOperandKind, value: Long)
  case UnterminatedString(opcode: SPIRVOpcode)
  case DecoderOpcodeMismatch(expected: SPIRVOpcodeNumber, actual: SPIRVOpcodeNumber)
  case NoMatchingDefinition(
      opcodeNumber: SPIRVOpcodeNumber,
      candidates: Vec[SPIRVOpcode] :| MinLength[1]
  )
  case AmbiguousDefinition(
      opcodeNumber: SPIRVOpcodeNumber,
      matches: Vec[SPIRVOpcode] :| MinLength[1]
  )

  def message: String = (this match {
    case UnknownOpcode(number)                    => s"unknown SPIR-V opcode $number"
    case InvalidOperandCount(opcode, detail)      => s"$opcode operands: $detail"
    case InvalidEnumerant(kind, value)            => s"invalid $kind enumerant $value"
    case UnterminatedString(opcode)               => s"$opcode contains an unterminated literal string"
    case DecoderOpcodeMismatch(expected, actual)  => s"decoder for opcode $expected cannot decode opcode $actual"
    case NoMatchingDefinition(number, candidates) =>
      s"opcode $number matched no definition among ${candidates.mkString(", ")}"
    case AmbiguousDefinition(number, matches) =>
      s"opcode $number ambiguously matched ${matches.mkString(", ")}"
  }).refineUnsafe[MinLength[1]]
}

enum ModuleDecodeError {
  case Instruction(index: Int :| Positive0, error: InstructionDecodeError)

  def message: String = (this match {
    case Instruction(index, error) => s"instruction $index: ${error.message}"
  }).refineUnsafe[MinLength[1]]
}
