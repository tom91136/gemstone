package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.Col

private[common] def normalizeOpcode(value: String): String =
  value.map(character => if (character >= 'A' && character <= 'Z') (character + ('a' - 'A')).toChar else character)

/** A parsed AMDGPU assembly program. Source locations are deliberately kept out of the semantic tree. */
final case class Program(statements: Col[Statement])

enum Statement {
  case Instruction(value: net.kurobako.gemstone.isa.amdgpu.common.Instruction)
  case InstructionPair(
      left: net.kurobako.gemstone.isa.amdgpu.common.Instruction,
      right: net.kurobako.gemstone.isa.amdgpu.common.Instruction
  )
  case Label(name: String)
  case Directive(name: String, arguments: String)
}

enum RegisterFile {
  case Scalar, VectorRegister, Accumulator, TrapTemporary
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
