package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.Col.*

enum ValidationError {
  case UnsupportedInstruction(opcode: String, target: Target)
  case InvalidInstruction(opcode: String, message: String)
}

enum ValidationResult {
  case Valid
  case Invalid(errors: Col[ValidationError])
  case Unsupported(reason: String)
}

/** Conservative target-aware validation. A result is `Valid` only when all constraints for an implemented format are
  * enforced. Known but not-yet-normalized formats are fail-closed as `Unsupported`.
  */
object Validator {
  def validate(
      program: Program,
      target: Target
  ): ValidationResult = {
    val results = program.statements.collect {
      case Statement.Instruction(instruction) => validate(instruction, target)
      case Statement.InstructionPair(_, _) => ValidationResult.Unsupported("paired VOPD validation is not implemented")
    }
    val errors = results.collect { case ValidationResult.Invalid(values) => values }.flatten
    if (program.statements.nonEmpty && results.isEmpty)
      ValidationResult.Unsupported("directive and label validation is not implemented")
    else if (errors.nonEmpty) ValidationResult.Invalid(errors)
    else results.collectFirst { case unsupported: ValidationResult.Unsupported => unsupported }.getOrElse(
      ValidationResult.Valid
    )
  }

  def validate(
      instruction: Instruction,
      target: Target
  ): ValidationResult = {
    val definitions = encodingsFor(instruction, target)
    if (definitions.isEmpty)
      ValidationResult.Invalid(Col(ValidationError.UnsupportedInstruction(instruction.opcode, target)))
    else {
      val formats = definitions.map(_.format.toUpperCase).distinct
      if (formats.contains("SOPP")) validateSOPP(instruction, target, definitions)
      else ValidationResult.Unsupported(s"operand constraints are not normalized for ${formats.sorted.mkString(",")}")
    }
  }

  private def validateSOPP(
      instruction: Instruction,
      target: Target,
      definitions: Col[Encoding]
  ): ValidationResult = {
    val explicitSignatures = definitions.map(_.operands.filter(!_.implicitOperand)).filter(_.nonEmpty).distinct
    if (instruction.mods.nonEmpty)
      invalid(instruction, "SOPP does not accept modifiers")
    else if (explicitSignatures.isEmpty)
      if (instruction.operands.isEmpty) availabilityUnknown(instruction, target)
      else ValidationResult.Unsupported(
        s"operand metadata is unavailable for ${instruction.opcode} on ${target.name}"
      )
    else if (explicitSignatures.size != 1)
      ValidationResult.Unsupported(s"multiple SOPP signatures exist for ${instruction.opcode} on ${target.name}")
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

  private def invalid(instruction: Instruction, message: String): ValidationResult =
    ValidationResult.Invalid(Col(ValidationError.InvalidInstruction(instruction.opcode, message)))

  private def availabilityUnknown(instruction: Instruction, target: Target): ValidationResult =
    ValidationResult.Unsupported(
      s"exact processor availability is not normalized for ${instruction.opcode} on ${target.name}"
    )
}
