package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.Vec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

/** A parsed AMDGPU assembly program. Source locations are deliberately kept out of the semantic tree. */
final case class Program(statements: Vec[Program.Statement]) extends net.kurobako.gemstone.isa.Program

object Program {
  type ModifierName = ModifierName.T
  object ModifierName extends RefinedType[String, MinLength[1] & NoNul]

  type ModifierValue = ModifierValue.T
  object ModifierValue extends RefinedType[String, MinLength[1] & NoNul]

  type Opcode = Opcode.T
  object Opcode extends RefinedType[String, MinLength[1] & NoNul] {
    private[amdgpu] def normalize(value: String): String =
      value.map(character => if (character >= 'A' && character <= 'Z') (character + ('a' - 'A')).toChar else character)
  }

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

  final case class Modifier(name: ModifierName, value: Option[ModifierValue] = None)

  final case class ParseError(
      line: Int :| Positive,
      column: Int :| Positive,
      message: String
  ) {
    override def toString: String = s"$line:$column: $message"
  }
}
