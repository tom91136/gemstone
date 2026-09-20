package net.kurobako.gemstone.isa.amdgpu

import io.github.iltotore.iron.*

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.ValidationOutcome

/** Canonical AMDGPU assembly printer. */
object ISA {
  object Printer {
    def render(program: Program): String = program.statements.map(renderStatement(_)).mkString("\n")

    def render(instruction: Instruction): String = {
      val operands  = instruction.operands.map(renderOperand(_))
      val modifiers = instruction.mods.map { modifier =>
        modifier.value.fold(modifier.name)(value => s"${modifier.name}:$value")
      }
      val tail = operands ++ modifiers
      if (tail.isEmpty) instruction.opcode.value
      else {
        val operandText  = operands.mkString(", ")
        val modifierText = modifiers.mkString(" ")
        val separator    = if (operands.nonEmpty && modifiers.nonEmpty) " " else ""
        s"${instruction.opcode} $operandText$separator$modifierText"
      }
    }

    private def renderStatement(statement: Statement): String = statement match {
      case Statement.Instruction(value)                              => render(value)
      case Statement.InstructionPair(left, right)                    => s"${render(left)} :: ${render(right)}"
      case Statement.Label(name)                                     => s"$name:"
      case Statement.Directive(name, arguments) if arguments.isEmpty => name
      case Statement.Directive(name, arguments)                      => s"$name $arguments"
    }

    private def renderOperand(operand: Operand): String = operand match {
      case Operand.Register(file, first, last) =>
        val prefix = file match {
          case RegisterFile.Scalar         => "s"
          case RegisterFile.VectorRegister => "v"
          case RegisterFile.Accumulator    => "a"
          case RegisterFile.TrapTemporary  => "ttmp"
        }
        if (first == last) s"$prefix$first" else s"$prefix[$first:$last]"
      case Operand.SpecialRegister(name)     => name
      case Operand.IntegerLiteral(value, 16) =>
        if (value.signum < 0) s"-0x${(-value).toString(16)}" else s"0x${value.toString(16)}"
      case Operand.IntegerLiteral(value, _) => value.toString
      case Operand.DecimalLiteral(value)    => value
      case Operand.Identifier(name)         => name
      case Operand.Expression(text)         => text
      case Operand.Negated(value)           => s"-${renderOperand(value)}"
      case Operand.Absolute(value)          => s"|${renderOperand(value)}|"
    }
  }
}

/** Operations bound to one public AMDGPU architectural-family package. */
final class ISA private[amdgpu] (val family: Family) {
  final val instructions: Vec[InstructionDef] = instructionsFor(family)

  final def validate(instruction: Instruction, target: Target): ValidationOutcome[Validation.Issue] =
    if (target.family == family) Validation.validate(instruction, target)
    else ValidationOutcome.Incomplete(Vec.nonEmpty(Validation.Issue.WrongFamily(target, family)))

  final def encode(instruction: Instruction, target: Target): Either[Binary.CodecError, Vec[Byte]] =
    requireFamily(target).flatMap(_ => Binary.encode(instruction, target))

  final def encodeSOPP(instruction: Instruction, target: Target): Either[Binary.CodecError, Vec[Byte]] =
    requireFamily(target).flatMap(_ => Binary.encodeSOPP(instruction, target))

  final def decodeSOPP(bytes: Vec[Byte], target: Target): Either[Binary.CodecError, Instruction] =
    requireFamily(target).flatMap(_ => Binary.decodeSOPP(bytes, target))

  private def requireFamily(target: Target): Either[Binary.CodecError, Unit] =
    Either.cond(target.family == family, (), Binary.CodecError.WrongFamily(target, family))
}
