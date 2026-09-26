package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.NonEmptyString
import net.kurobako.gemstone.CompileTime
import net.kurobako.gemstone.PositiveInt
import net.kurobako.gemstone.Vec

/** A parsed AMDGPU assembly program. Source locations are deliberately kept out of the semantic tree. */
final case class Program(statements: Vec[Program.Statement]) extends net.kurobako.gemstone.isa.Program

object Program {

  opaque type ModifierName = String
  object ModifierName {
    inline def either(value: String): Either[String, ModifierName] = NonEmptyString.either(value).map(_ => value)

    inline def unsafe(value: String): ModifierName =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): ModifierName =
      value

    extension (value: ModifierName) {
      inline def raw: String = value
    }
  }

  opaque type ModifierValue = String
  object ModifierValue {
    inline def either(value: String): Either[String, ModifierValue] = NonEmptyString.either(value).map(_ => value)

    inline def unsafe(value: String): ModifierValue =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): ModifierValue =
      value

    extension (value: ModifierValue) {
      inline def raw: String = value
    }
  }

  opaque type Opcode = String
  object Opcode {
    inline def either(value: String): Either[String, Opcode] = NonEmptyString.either(value).map(_ => value)

    inline def unsafe(value: String): Opcode =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): Opcode =
      value

    extension (value: Opcode) {
      inline def raw: String = value
    }

    private[amdgpu] def normalize(value: String): String = value.toLowerCase(java.util.Locale.ROOT)
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
      line: PositiveInt,
      column: PositiveInt,
      message: String
  ) {
    override def toString: String = s"$line:$column: $message"
  }
}
