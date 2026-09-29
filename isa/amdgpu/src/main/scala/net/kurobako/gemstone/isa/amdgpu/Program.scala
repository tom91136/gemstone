package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.Instruction as AMDGPUInstruction

object Program {

  enum Statement {
    case Instruction(value: AMDGPUInstruction)
    case InstructionPair(
        left: AMDGPUInstruction,
        right: AMDGPUInstruction
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

  case class Modifier(name: NonEmpty[String], value: Option[NonEmpty[String]] = None)

  case class ParseError(
      line: Int > 0,
      column: Int > 0,
      message: NonEmpty[String]
  ) {
    override def toString: String = s"$line:$column: $message"
  }
}

/** A parsed AMDGPU assembly program. Source locations are deliberately kept out of the semantic tree. */
case class Program(statements: Vec[Program.Statement])
