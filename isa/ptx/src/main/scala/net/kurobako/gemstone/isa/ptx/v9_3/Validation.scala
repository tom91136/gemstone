package net.kurobako.gemstone.isa.ptx.v9_3

import ISA.*
import Metadata.*
import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*

object Validation {
  enum Issue {
    case VersionTooOld(opcode: NonEmpty[String], required: Version, selected: Version)
    case TargetTooOld(opcode: NonEmpty[String], required: Target, selected: Target)
    case InvalidOperands(
        opcode: NonEmpty[String],
        message: NonEmpty[String]
    )
    case InvalidModifiers(
        opcode: NonEmpty[String],
        message: NonEmpty[String]
    )
    case Incomplete(detail: NonEmpty[String])
  }

  def validate(
      instruction: Instruction,
      version: Version,
      target: Target
  ): ValidationOutcome[Issue] = {
    val candidates = instruction.meta.entries
    val available  = candidates.filter(info => version >= info.introduced && target >= info.minimumTarget)
    if (available.isEmpty) {
      val versionErrors = candidates.filter(_.introduced > version).map(info =>
        Issue.VersionTooOld(instruction.opcode, info.introduced, version)
      )
      val targetErrors = candidates.filter(_.minimumTarget > target).map(info =>
        Issue.TargetTooOld(instruction.opcode, info.minimumTarget, target)
      )
      ValidationOutcome.Rejected((versionErrors ++ targetErrors).distinct.unsafe)
    } else validateForms(instruction, available.flatMap(_.forms))
  }

  def validate(
      program: Program,
      version: Version,
      target: Target
  ): ValidationOutcome[Issue] = {
    val instructions = collectInstructions(program.statements)
    ValidationOutcome.combineAll(instructions.map(validate(_, version, target)))
  }

  private def validateForms(
      instruction: Instruction,
      forms: Vec[InstructionForm]
  ): ValidationOutcome[Issue] =
    if (forms.isEmpty)
      unsupported(s"no normalized syntax form for ${instruction.qualifiedOpcode}")
    else {
      val sameCount = forms.filter(_.operands.size == instruction.operands.size)
      if (sameCount.isEmpty)
        ValidationOutcome.Rejected(
          Vec.nonEmpty(
            Issue.InvalidOperands(
              instruction.opcode,
              s"expected ${forms.map(_.operands.size).distinct.sorted(using Ordering.Int).mkString(" or ")} operands, found ${instruction.operands.size}"
            )
          )
        )
      else {
        val modifiers    = instruction.mods.map(_.name).toSet
        val matchingMods = sameCount.filter(form => modifiersMatch(modifiers, form))
        if (matchingMods.isEmpty)
          ValidationOutcome.Rejected(
            Vec.nonEmpty(Issue.InvalidModifiers(
              instruction.opcode,
              s"unsupported modifier set: ${modifiers.view.toArray.sorted.mkString(".")}"
            ))
          )
        else {
          val complete = matchingMods.filter(_.complete)
          complete.find(form => operandsMatch(instruction.operands, form.operands)) match {
            case Some(_)                   => ValidationOutcome.Accepted
            case None if complete.nonEmpty =>
              ValidationOutcome.Rejected(
                Vec.nonEmpty(Issue.InvalidOperands(
                  instruction.opcode,
                  "operand kinds do not match the syntax form"
                ))
              )
            case None =>
              unsupported(
                s"syntax form for ${instruction.qualifiedOpcode} is not fully normalized"
              )
          }
        }
      }
    }

  private def modifiersMatch(actual: Set[NonEmpty[String]], form: InstructionForm): Boolean = {
    val allowed  = form.requiredModifierGroups.flatMap(_.raw).toSet ++ form.optionalModifiers
    val required = form.requiredModifierGroups.forall(group => actual.count(group.raw.contains) == 1)
    required && actual.subsetOf(allowed)
  }

  private def unsupported(
      detail: NonEmpty[String]
  ): ValidationOutcome[Issue] =
    ValidationOutcome.Incomplete(Vec.nonEmpty(Issue.Incomplete(detail)))

  private def operandsMatch(actual: Vec[Operand], expected: Vec[OperandConstraint]): Boolean =
    actual.zip(expected).forall {
      case (Operand.Register(_, _), OperandConstraint.Register)        => true
      case (Operand.WithPredicate(_, _), OperandConstraint.Register)   => true
      case (Operand.Qualified(base, _), constraint)                    => operandsMatch(Vec(base), Vec(constraint))
      case (_: Operand.Address, OperandConstraint.Address)             => true
      case (_: Operand.VectorLiteral, OperandConstraint.VectorOperand) => true
      case (_: Operand.Symbol | _: Operand.Qualifier, OperandConstraint.Symbol) => true
      case (_, OperandConstraint.Value)                                         => true
      case (_, OperandConstraint.Unknown(_))                                    => false
      case _                                                                    => false
    }

  private def collectInstructions(statements: Vec[Statement]): Vec[Instruction] =
    statements.flatMap {
      case Statement.Instruction(value)            => Vec(value)
      case Statement.Block(body)                   => collectInstructions(body)
      case Statement.Entry(_, _, _, body, _)       => body.view.flatMap(collectInstructions(_)).toVec
      case Statement.Function(_, _, _, _, body, _) => body.view.flatMap(collectInstructions(_)).toVec
      case _                                       => Vec.empty[Instruction]
    }
}
