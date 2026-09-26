package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.isa.ValidationOutcome
import Metadata.*
import Program.*

/** Conservative target-aware validation. An outcome is `Accepted` only when all constraints for an implemented format
  * are enforced. Known but not-yet-normalized formats are fail-closed as `Incomplete`.
  */
object Validation {
  enum Issue {
    case WrongFamily(target: Target, expected: Family)
    case UnsupportedInstruction(opcode: Opcode, target: Target)
    case InvalidInstruction(opcode: Opcode, message: String)
    case Incomplete(detail: String)
  }

  def validate(
      program: Program,
      target: Target
  ): ValidationOutcome[Issue] = {
    val results = program.statements.collect {
      case Statement.Instruction(instruction) => validate(instruction, target)
      case Statement.InstructionPair(_, _)    => unsupported("paired VOPD validation is not implemented")
    }
    if (program.statements.nonEmpty && results.isEmpty)
      unsupported("directive and label validation is not implemented")
    else ValidationOutcome.combineAll(results)
  }

  def validate(
      instruction: Instruction,
      target: Target
  ): ValidationOutcome[Issue] = {
    val definitions = encodingsFor(instruction, target)
    if (definitions.isEmpty)
      ValidationOutcome.Rejected(
        Vec.nonEmpty(Issue.UnsupportedInstruction(instruction.opcode, target))
      )
    else {
      val formats = definitions.map(_.format.raw.toUpperCase(java.util.Locale.ROOT)).distinct
      if (formats.contains("SOPP")) validateSOPP(instruction, target, definitions)
      else unsupported(
        s"operand constraints are not normalized for ${formats.sorted.mkString(",")}"
      )
    }
  }

  private def validateSOPP(
      instruction: Instruction,
      target: Target,
      definitions: Vec[Encoding]
  ): ValidationOutcome[Issue] = {
    val explicitSignatures = definitions.map(_.operands.filter(!_.implicitOperand)).filter(_.nonEmpty).distinct
    if (instruction.mods.nonEmpty)
      invalid(instruction, "SOPP does not accept modifiers")
    else if (explicitSignatures.isEmpty)
      if (instruction.operands.isEmpty) availabilityUnknown(instruction, target)
      else unsupported(
        s"operand metadata is unavailable for ${instruction.opcode} on ${target.name}"
      )
    else if (explicitSignatures.size != 1)
      unsupported(
        s"multiple SOPP signatures exist for ${instruction.opcode} on ${target.name}"
      )
    else {
      val signature = explicitSignatures.head
      if (instruction.operands.size != signature.size)
        invalid(
          instruction,
          s"expected ${signature.size} operands, found ${instruction.operands.size}"
        )
      else {
        val errors = instruction.operands.zip(signature).flatMap { case (operand, info) =>
          validateSOPPOperand(operand, info).toVec
        }
        if (errors.isEmpty) availabilityUnknown(instruction, target)
        else invalid(instruction, errors.mkString("; "))
      }
    }
  }

  private def validateSOPPOperand(
      operand: Operand,
      info: OperandInfo
  ): Option[String] =
    if (info.kind.raw == "LABEL")
      operand match {
        case Operand.IntegerLiteral(value, _) if value >= -32768 && value <= 32767 => None
        case Operand.Identifier(_)                                                 => None
        case _                                                                     => Some(
            s"${info.field.getOrElse("label")} requires a label or signed 16-bit displacement"
          )
      }
    else
      operand match {
        case Operand.IntegerLiteral(value, _) if value >= 0 && value <= 65535 => None
        case _                                                                => Some(
            s"${info.field.getOrElse(info.kind)} requires an unsigned 16-bit immediate"
          )
      }

  private def invalid(
      instruction: Instruction,
      message: String
  ): ValidationOutcome[Issue] =
    ValidationOutcome.Rejected(
      Vec.nonEmpty(Issue.InvalidInstruction(instruction.opcode, message))
    )

  private def availabilityUnknown(instruction: Instruction, target: Target): ValidationOutcome[Issue] =
    unsupported(
      s"exact processor availability is not normalized for ${instruction.opcode} on ${target.name}"
    )

  private def unsupported(
      detail: String
  ): ValidationOutcome[Issue] =
    ValidationOutcome.Incomplete(
      Vec.nonEmpty(Issue.Incomplete(detail))
    )
}
