package net.kurobako.gemstone.isa.amdgpu.asm

import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.amdgpu.*

/** Operand checking shares the encoder; exact processor availability remains incomplete. */
object Validation {
  enum Issue {
    case UnsupportedInstruction(opcode: NonEmpty[String], target: Target)
    case InvalidInstruction(opcode: NonEmpty[String], message: NonEmpty[String])
    case Incomplete(detail: NonEmpty[String])
  }

  def validate(program: Program, target: Target): ValidationOutcome[Issue] = {
    val results = program.statements.collect {
      case Statement.Instruction(instruction)        => validate(instruction, target)
      case pair @ Statement.InstructionPair(left, _) => result(left, target, Binary.encodeStatement(pair, target))
    }
    if (program.statements.nonEmpty && results.isEmpty)
      incomplete("directive and label validation is not implemented")
    else ValidationOutcome.combineAll(results)
  }

  def validate(instruction: Instruction, target: Target): ValidationOutcome[Issue] = {
    // Symbol resolution belongs to the assembler. Check the remaining operands using a representable displacement.
    val branch   = Instruction.branchesRelative(instruction.definition, target)
    val operands = instruction.operands.map {
      case Operand.Identifier(_) | Operand.Expression(_) if branch => Operand.IntegerLiteral(BigInt(0), 10)
      case other                                                   => other
    }
    Instruction.decode(instruction.definition, operands, instruction.mods) match {
      case Right(value) => result(instruction, target, Binary.encode(value, target))
      case Left(_)      => incomplete(s"invalid operand form for ${instruction.opcode}")
    }
  }

  private def result(
      instruction: Instruction,
      target: Target,
      encoded: Either[CodecError, Vec[Byte]]
  ): ValidationOutcome[Issue] = encoded match {
    case Right(_) =>
      incomplete(s"exact processor availability is not normalised for ${instruction.opcode} on ${target.name}")
    case Left(CodecError.UnsupportedInstruction(opcode, target)) =>
      ValidationOutcome.Rejected(Vec.nonEmpty(Issue.UnsupportedInstruction(opcode, target)))
    case Left(CodecError.InvalidOperand(message)) =>
      ValidationOutcome.Rejected(Vec.nonEmpty(Issue.InvalidInstruction(instruction.opcode, message)))
    case Left(CodecError.InvalidEncoding(message)) =>
      ValidationOutcome.Rejected(Vec.nonEmpty(Issue.InvalidInstruction(instruction.opcode, message)))
    case Left(error) => incomplete(s"operand validation for ${instruction.opcode}: $error")
  }

  private def incomplete(detail: NonEmpty[String]): ValidationOutcome[Issue] =
    ValidationOutcome.Incomplete(Vec.nonEmpty(Issue.Incomplete(detail)))
}
