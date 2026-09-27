package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.Positive

import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.Vec

/** A parsed AMDGPU assembly program. Source locations are deliberately kept out of the semantic tree. */
case class Program(statements: Vec[Program.Statement]) extends net.kurobako.gemstone.isa.Program

object Program {

  private[amdgpu] def normalizeOpcode(value: String): String = value.toLowerCase(java.util.Locale.ROOT)

  enum Statement {
    case Instruction(value: net.kurobako.gemstone.isa.amdgpu.Instruction)
    case InstructionPair(
        left: net.kurobako.gemstone.isa.amdgpu.Instruction,
        right: net.kurobako.gemstone.isa.amdgpu.Instruction
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

  case class Modifier(name: NonEmptyString, value: Option[NonEmptyString] = None)

  case class ParseError(
      line: Positive[Int],
      column: Positive[Int],
      message: String
  ) extends net.kurobako.gemstone.isa.DecodeError {
    override def toString: String = s"$line:$column: $message"
  }
}
