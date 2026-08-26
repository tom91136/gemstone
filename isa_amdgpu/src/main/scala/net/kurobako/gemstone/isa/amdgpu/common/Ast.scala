package net.kurobako.gemstone.isa.amdgpu.common

private[common] def normalizeOpcode(value: String): String =
  value.map(character => if (character >= 'A' && character <= 'Z') (character + ('a' - 'A')).toChar else character)

/** A parsed AMDGPU assembly program. Source locations are deliberately kept out of the semantic tree. */
final case class Program(statements: Vector[Statement])

enum Statement {
  case Instruction(value: net.kurobako.gemstone.isa.amdgpu.common.Instruction)
  case InstructionPair(
      left: net.kurobako.gemstone.isa.amdgpu.common.Instruction,
      right: net.kurobako.gemstone.isa.amdgpu.common.Instruction
  )
  case Label(name: String)
  case Directive(name: String, arguments: String)
}

/** Raw assembly retains one instruction shape. [[KnownInstruction]] supplies the generated opcode-specific layer, while
  * architecture-specific validity and encodings live in [[InstructionDatabase]] rather than multiplying generation
  * variants into the Scala type hierarchy.
  */
final case class Instruction(
    opcode: Opcode,
    operands: Vector[Operand] = Vector.empty,
    modifiers: Vector[Modifier] = Vector.empty
)

/** Arguments shared by the generated per-opcode cases. Encoding and generation variants remain normalized metadata. */
final case class InstructionArguments(
    operands: Vector[Operand] = Vector.empty,
    modifiers: Vector[Modifier] = Vector.empty
) {
  def toSyntax(opcode: Opcode): Instruction = Instruction(opcode, operands, modifiers)
}

opaque type Opcode = String

object Opcode {
  private val Syntax = "[A-Za-z][A-Za-z0-9_.]*".r

  def fromString(value: String): Either[String, Opcode] = value match {
    case Syntax() => Right(normalizeOpcode(value))
    case _        => Left(s"invalid AMDGPU opcode '$value'")
  }

  def unsafe(value: String): Opcode =
    fromString(value).fold(message => throw new IllegalArgumentException(message), identity)

  extension (opcode: Opcode) def value: String = opcode
}

enum RegisterFile {
  case Scalar, Vector, Accumulator, TrapTemporary
}

enum Operand {
  case Register(file: RegisterFile, first: Int, last: Int)
  case SpecialRegister(name: String)
  case IntegerLiteral(value: BigInt, radix: Int)
  case DecimalLiteral(value: String)
  case Identifier(name: String)
  case Expression(text: String)
  case Negated(value: Operand)
  case Absolute(value: Operand)
}

final case class Modifier(name: String, value: Option[String] = None)

final case class ParseError(line: Int, column: Int, message: String) {
  override def toString: String = s"$line:$column: $message"
}
