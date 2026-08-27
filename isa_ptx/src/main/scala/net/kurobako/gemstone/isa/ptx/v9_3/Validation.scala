package net.kurobako.gemstone.isa.ptx.v9_3

final case class PtxVersion(major: Int, minor: Int) extends Ordered[PtxVersion] {
  override def compare(that: PtxVersion): Int = Ordering[(Int, Int)].compare((major, minor), (that.major, that.minor))
  override def toString: String               = s"$major.$minor"
}

object PtxVersion {
  def parse(value: String): Either[String, PtxVersion] = value.split('.').toVector match {
    case Vector(major, minor) =>
      for {
        majorValue <- major.toIntOption.toRight(s"invalid PTX version '$value'")
        minorValue <- minor.toIntOption.toRight(s"invalid PTX version '$value'")
      } yield PtxVersion(majorValue, minorValue)
    case _ => Left(s"invalid PTX version '$value'")
  }
}

final case class SmTarget(computeCapability: Int) extends Ordered[SmTarget] {
  override def compare(that: SmTarget): Int = computeCapability.compare(that.computeCapability)
  override def toString: String             = s"sm_$computeCapability"
}

object SmTarget {
  def parse(value: String): Either[String, SmTarget] = {
    val normalized = value.toLowerCase.stripPrefix("sm_").stripSuffix("a").stripSuffix("f")
    normalized.toIntOption.filter(_ >= 10).map(SmTarget.apply).toRight(s"invalid SM target '$value'")
  }
}

enum OperandConstraint {
  case Register
  case Address
  case Vector
  case Symbol
  case Value
  case Unknown(sourceName: String)
}

/** One normalized alternative from an instruction's Syntax rubric. Each required modifier group contributes exactly one
  * member; optional modifiers may appear independently.
  */
final case class InstructionForm(
    opcode: String,
    operands: Vector[OperandConstraint],
    requiredModifierGroups: Vector[Set[String]],
    optionalModifiers: Set[String],
    complete: Boolean
)

enum ValidationError {
  case VersionTooOld(opcode: String, required: PtxVersion, selected: PtxVersion)
  case TargetTooOld(opcode: String, required: SmTarget, selected: SmTarget)
  case InvalidOperands(opcode: String, message: String)
  case InvalidModifiers(opcode: String, message: String)
}

enum ValidationResult {
  case Valid
  case Invalid(errors: Vector[ValidationError])
  case Unsupported(reason: String)
}

object Validator {
  def validate(
      instruction: Instruction,
      version: PtxVersion,
      target: SmTarget
  ): ValidationResult = {
    val candidates = instruction.meta.entries
    val available  = candidates.filter(info => version >= info.introduced && target >= info.minimumTarget)
    if (available.isEmpty) {
      val versionErrors = candidates.filter(_.introduced > version).map(info =>
        ValidationError.VersionTooOld(instruction.opcode, info.introduced, version)
      )
      val targetErrors = candidates.filter(_.minimumTarget > target).map(info =>
        ValidationError.TargetTooOld(instruction.opcode, info.minimumTarget, target)
      )
      ValidationResult.Invalid((versionErrors ++ targetErrors).distinct)
    } else validateForms(instruction, available.flatMap(_.forms))
  }

  def validate(
      program: Program,
      version: PtxVersion,
      target: SmTarget
  ): ValidationResult = {
    val instructions = collectInstructions(program.statements)
    val results      = instructions.map(validate(_, version, target))
    val errors       = results.collect { case ValidationResult.Invalid(values) => values }.flatten
    if (errors.nonEmpty) ValidationResult.Invalid(errors)
    else results.collectFirst { case value: ValidationResult.Unsupported => value }.getOrElse(ValidationResult.Valid)
  }

  private def validateForms(instruction: Instruction, forms: Vector[InstructionForm]): ValidationResult = {
    if (forms.isEmpty)
      return ValidationResult.Unsupported(s"no normalized syntax form for ${instruction.qualifiedOpcode}")
    val sameCount = forms.filter(_.operands.size == instruction.operands.size)
    if (sameCount.isEmpty)
      return ValidationResult.Invalid(
        Vector(
          ValidationError.InvalidOperands(
            instruction.opcode,
            s"expected ${forms.map(_.operands.size).distinct.sorted.mkString(" or ")} operands, found ${instruction.operands.size}"
          )
        )
      )

    val modifiers    = instruction.mods.map(_.name).toSet
    val matchingMods = sameCount.filter(form => modifiersMatch(modifiers, form))
    if (matchingMods.isEmpty)
      return ValidationResult.Invalid(
        Vector(ValidationError.InvalidModifiers(
          instruction.opcode,
          s"unsupported modifier set: ${modifiers.toVector.sorted.mkString(".")}"
        ))
      )

    val complete = matchingMods.filter(_.complete)
    complete.find(form => operandsMatch(instruction.operands, form.operands)) match {
      case Some(_)                   => ValidationResult.Valid
      case None if complete.nonEmpty =>
        ValidationResult.Invalid(
          Vector(ValidationError.InvalidOperands(
            instruction.opcode,
            "operand kinds do not match the syntax form"
          ))
        )
      case None =>
        ValidationResult.Unsupported(s"syntax form for ${instruction.qualifiedOpcode} is not fully normalized")
    }
  }

  private def modifiersMatch(actual: Set[String], form: InstructionForm): Boolean = {
    val allowed  = form.requiredModifierGroups.flatten.toSet ++ form.optionalModifiers
    val required = form.requiredModifierGroups.forall(group => actual.count(group.contains) == 1)
    required && actual.subsetOf(allowed)
  }

  private def operandsMatch(actual: Vector[Operand], expected: Vector[OperandConstraint]): Boolean =
    actual.zip(expected).forall {
      case (Operand.Register(_, _), OperandConstraint.Register)                 => true
      case (Operand.WithPredicate(_, _), OperandConstraint.Register)            => true
      case (_: Operand.Address, OperandConstraint.Address)                      => true
      case (_: Operand.VectorLiteral, OperandConstraint.Vector)                 => true
      case (_: Operand.Symbol | _: Operand.Qualifier, OperandConstraint.Symbol) => true
      case (_, OperandConstraint.Value)                                         => true
      case (_, OperandConstraint.Unknown(_))                                    => false
      case _                                                                    => false
    }

  private def collectInstructions(statements: Vector[Statement]): Vector[Instruction] =
    statements.flatMap {
      case Statement.Instruction(value)         => Vector(value)
      case Statement.Block(body)                => collectInstructions(body)
      case Statement.Entry(_, _, _, body)       => collectInstructions(body)
      case Statement.Function(_, _, _, _, body) => body.toVector.flatMap(collectInstructions)
      case _                                    => Vector.empty
    }
}
