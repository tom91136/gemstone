package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.isa.ValidationResult

/** Canonical AMDGPU assembly printer. */
object Printer {
  def render(program: Program): String = program.statements.map(renderStatement).mkString("\n")

  def render(instruction: Instruction): String = {
    val operands  = instruction.operands.map(renderOperand)
    val modifiers = instruction.mods.map { modifier =>
      modifier.value.fold(modifier.name)(value => s"${modifier.name}:$value")
    }
    val tail = operands ++ modifiers
    if (tail.isEmpty) instruction.opcode
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

/** Operations bound to one public AMDGPU architectural-family package. */
final class ISA private[amdgpu] (val family: Family) {
  final val instructions: Col[InstructionDef] = instructionsFor(family)

  final def validate(instruction: Instruction, target: Target): ValidationResult[ValidationIssue] =
    if (target.family == family) Validator.validate(instruction, target)
    else ValidationResult.Unsupported(Col(ValidationIssue.WrongFamily(target, family)))

  final def encode(instruction: Instruction, target: Target): Either[CodecError, Col[Byte]] =
    requireFamily(target).flatMap(_ => Binary.encode(instruction, target))

  final def encodeSOPP(instruction: Instruction, target: Target): Either[CodecError, Col[Byte]] =
    requireFamily(target).flatMap(_ => Binary.encodeSOPP(instruction, target))

  final def decodeSOPP(bytes: Col[Byte], target: Target): Either[CodecError, Instruction] =
    requireFamily(target).flatMap(_ => Binary.decodeSOPP(bytes, target))

  private def requireFamily(target: Target): Either[CodecError, Unit] =
    Either.cond(target.family == family, (), CodecError.WrongFamily(target, family))
}
