package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.Col.*
import net.kurobako.gemstone.isa.ValidationResult

enum ValidationIssue {
  case WrongFamily(target: Target, expected: Family)
  case UnsupportedInstruction(opcode: String, target: Target)
  case InvalidInstruction(opcode: String, message: String)
  case Incomplete(detail: String)
}

/** Conservative target-aware validation. A result is `Valid` only when all constraints for an implemented format are
  * enforced. Known but not-yet-normalized formats are fail-closed as `Unsupported`.
  */
object Validator {
  def validate(
      program: Program,
      target: Target
  ): ValidationResult[ValidationIssue] = {
    val results = program.statements.collect {
      case Statement.Instruction(instruction) => validate(instruction, target)
      case Statement.InstructionPair(_, _)    => unsupported("paired VOPD validation is not implemented")
    }
    val errors = results.collect { case ValidationResult.Invalid(values) => values }.flatten
    if (program.statements.nonEmpty && results.isEmpty)
      unsupported("directive and label validation is not implemented")
    else if (errors.nonEmpty) ValidationResult.Invalid(errors)
    else results.collectFirst { case unsupported @ ValidationResult.Unsupported(_) => unsupported }.getOrElse(
      ValidationResult.Valid
    )
  }

  def validate(
      instruction: Instruction,
      target: Target
  ): ValidationResult[ValidationIssue] = {
    val definitions = encodingsFor(instruction, target)
    if (definitions.isEmpty)
      ValidationResult.Invalid(Col(ValidationIssue.UnsupportedInstruction(instruction.opcode, target)))
    else {
      val formats = definitions.map(_.format.toUpperCase).distinct
      if (formats.contains("SOPP")) validateSOPP(instruction, target, definitions)
      else unsupported(s"operand constraints are not normalized for ${formats.sorted.mkString(",")}")
    }
  }

  private def validateSOPP(
      instruction: Instruction,
      target: Target,
      definitions: Col[Encoding]
  ): ValidationResult[ValidationIssue] = {
    val explicitSignatures = definitions.map(_.operands.filter(!_.implicitOperand)).filter(_.nonEmpty).distinct
    if (instruction.mods.nonEmpty)
      invalid(instruction, "SOPP does not accept modifiers")
    else if (explicitSignatures.isEmpty)
      if (instruction.operands.isEmpty) availabilityUnknown(instruction, target)
      else unsupported(
        s"operand metadata is unavailable for ${instruction.opcode} on ${target.name}"
      )
    else if (explicitSignatures.size != 1)
      unsupported(s"multiple SOPP signatures exist for ${instruction.opcode} on ${target.name}")
    else {
      val signature = explicitSignatures.head
      if (instruction.operands.size != signature.size)
        invalid(instruction, s"expected ${signature.size} operands, found ${instruction.operands.size}")
      else {
        val errors = instruction.operands.zip(signature).flatMap { case (operand, info) =>
          validateSOPPOperand(operand, info).toCol
        }
        if (errors.isEmpty) availabilityUnknown(instruction, target)
        else invalid(instruction, errors.mkString("; "))
      }
    }
  }

  private def validateSOPPOperand(operand: Operand, info: OperandInfo): Option[String] =
    if (info.kind == "LABEL")
      operand match {
        case Operand.IntegerLiteral(value, _) if value >= -32768 && value <= 32767 => None
        case Operand.Identifier(_)                                                 => None
        case _ => Some(s"${info.field.getOrElse("label")} requires a label or signed 16-bit displacement")
      }
    else
      operand match {
        case Operand.IntegerLiteral(value, _) if value >= 0 && value <= 65535 => None
        case _ => Some(s"${info.field.getOrElse(info.kind)} requires an unsigned 16-bit immediate")
      }

  private def invalid(instruction: Instruction, message: String): ValidationResult[ValidationIssue] =
    ValidationResult.Invalid(Col(ValidationIssue.InvalidInstruction(instruction.opcode, message)))

  private def availabilityUnknown(instruction: Instruction, target: Target): ValidationResult[ValidationIssue] =
    unsupported(s"exact processor availability is not normalized for ${instruction.opcode} on ${target.name}")

  private def unsupported(detail: String): ValidationResult[ValidationIssue] =
    ValidationResult.Unsupported(Col(ValidationIssue.Incomplete(detail)))
}
