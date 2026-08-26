package net.kurobako.gemstone.isa.spirv.v1_6

opaque type Word = Int

object Word {
  def apply(bits: Int): Word = bits

  def unsigned(value: Long): Either[String, Word] =
    if (value < 0L || value > 0xffffffffL) Left(s"word is outside unsigned 32-bit range: $value")
    else Right(value.toInt)

  extension (word: Word) {
    def bits: Int        = word
    def toUnsigned: Long = word.toLong & 0xffffffffL
  }
}

opaque type Opcode = Int

object Opcode {
  def apply(number: Int): Either[String, Opcode] =
    if (number < 0 || number > 0xffff) Left(s"opcode is outside unsigned 16-bit range: $number")
    else Right(number)

  def unsafe(number: Int): Opcode = apply(number).fold(message => throw new IllegalArgumentException(message), identity)

  extension (opcode: Opcode) {
    def number: Int          = opcode
    def name: Option[String] = InstructionCatalog.byOpcode.get(opcode).map(_.name)
  }
}

enum Endianness {
  case Little
  case Big
}

final case class Version(major: Int, minor: Int) {
  require(major >= 0 && major <= 0xff)
  require(minor >= 0 && minor <= 0xff)

  def word: Word = Word((major << 16) | (minor << 8))
}

object Version {
  val V1_6: Version = Version(1, 6)

  def fromWord(word: Word): Version = Version((word.bits >>> 16) & 0xff, (word.bits >>> 8) & 0xff)
}

final case class Header(version: Version, generator: Word, bound: Word, schema: Word)

final case class Instruction(opcode: Opcode, operands: Vector[Word]) {
  def wordCount: Int = operands.size + 1
  def name: String   = opcode.name.getOrElse(s"Op<${opcode.number}>")
}

final case class Module(header: Header, instructions: Vector[Instruction], endianness: Endianness)

final case class OperandInfo(kind: String, name: Option[String], quantifier: Option[String])

final case class InstructionInfo(
    opcode: Opcode,
    name: String,
    instructionClass: String,
    version: Option[String],
    capabilities: Vector[String],
    operands: Vector[OperandInfo]
) {
  def minimumOperandCount: Int =
    operands.count(info => !info.quantifier.contains("?") && !info.quantifier.contains("*"))
}

object InstructionCatalog {
  val instructions: Vector[InstructionInfo]  = generated.Catalog.instructions
  val byOpcode: Map[Opcode, InstructionInfo] = instructions.iterator.map(info => info.opcode -> info).toMap
  val byName: Map[String, InstructionInfo]   = instructions.iterator.map(info => info.name -> info).toMap
}
