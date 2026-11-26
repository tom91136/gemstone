package net.kurobako.gemstone.isa.ptx

import Metadata.*
import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import scala.annotation.tailrec

// Validation expands <count> register names to check operand declarations, while VMCompiler parses directive operands; the same syntax serves different operand domains
@SuppressWarnings(Array("bonsai:duplicate-code"))
object Validation {
  enum Issue {
    case VersionTooOld(opcode: NonEmpty[String], required: Version, selected: Version)
    case TargetTooOld(opcode: NonEmpty[String], required: Target, selected: Target)
    case InvalidOperands(opcode: NonEmpty[String], detail: NonEmpty[String])
    case InvalidModifiers(opcode: NonEmpty[String], detail: NonEmpty[String])
    case InvalidOperandValue(opcode: NonEmpty[String], position: Int, violation: OperandViolation)
    case InvalidOperandType(opcode: NonEmpty[String], position: Int, violation: RegisterViolation)
    case Incomplete(detail: NonEmpty[String])

    def message: NonEmpty[String] = this match {
      case VersionTooOld(opcode, required, selected) => s"$opcode requires PTX ISA $required, module declares $selected"
      case TargetTooOld(opcode, required, selected)  =>
        s"$opcode requires ${required.render}, module targets ${selected.render}"
      case InvalidOperands(opcode, detail)                  => s"$opcode: $detail"
      case InvalidModifiers(opcode, detail)                 => s"$opcode: $detail"
      case InvalidOperandValue(opcode, position, violation) =>
        s"$opcode: operand ${position + 1}: ${violation.message}"
      case InvalidOperandType(opcode, position, violation) =>
        s"$opcode: operand ${position + 1}: ${violation.message}"
      case Incomplete(detail) => detail
    }
  }

  /** One `.version`-delimited module with the ISA version and target it declares. */
  case class Module(version: Option[Version], target: Option[Target], chunks: Vec[Chunk])

  /** The instructions of one block with the register declarations in scope there. */
  case class Chunk(registers: RegisterTypes.Scope, instructions: Vec[Instruction])

  def modules(program: Program): Vec[Module] = {
    val starts = program.statements.indices.filter(index =>
      program.statements(index) match {
        case Statement.Directive(path = path) => path.raw == Vec("version")
        case _                                => false
      }
    )
    val bounds = (0 +: starts.filter(_ > 0)).zip(starts.filter(_ > 0) :+ program.statements.size)
    Vec.from(bounds).map { (from, until) =>
      val statements = program.statements.slice(from, until)
      Module(
        declared(statements, "version")(version(_)),
        declared(statements, "target")(target(_)),
        chunks(statements, Map.empty)
      )
    }
  }

  private def chunks(statements: Vec[Statement], outer: RegisterTypes.Scope): Vec[Chunk] = {
    val scope  = outer ++ declarations(statements)
    val direct = statements.collect { case Statement.Instruction(value) => value }
    val nested = statements.flatMap {
      case Statement.Block(body)                 => chunks(body, scope)
      case Statement.Entry(body = Some(body))    => chunks(body, Map.empty)
      case Statement.Function(body = Some(body)) => chunks(body, Map.empty)
      case _                                     => Vec.empty[Chunk]
    }
    (if (direct.isEmpty) Vec.empty[Chunk] else Vec(Chunk(scope, direct))) ++ nested
  }

  /** The `.reg` declarations directly in `statements`, with their ranges expanded. */
  private def declarations(statements: Vec[Statement]): RegisterTypes.Scope =
    statements.flatMap {
      case Statement.Directive(path, arguments, _, _) if path.head.raw == "reg" =>
        val types = arguments.flatMap {
          case DirectiveArgument(_, Operand.Qualifier(parts)) => parts.raw.flatMap(part => PtxTypes(part.raw))
          case _                                              => Vec.empty[PtxType]
        }
        types.find(value => value.scalar || value.predicate).toVec.flatMap { tpe =>
          arguments.flatMap(argument => declaredNames(argument.operand)).flatMap(expand(_)).map(_.raw -> tpe)
        }
      case _ => Vec.empty[(String, PtxType)]
    }.toMap

  private def declaredNames(operand: Operand): Vec[NonEmpty[String]] = operand match {
    case Operand.Register(name, None) => Vec(name)
    case Operand.Symbol(name)         => Vec(name)
    case _                            => Vec.empty
  }

  private def expand(name: NonEmpty[String]): Vec[NonEmpty[String]] = name.raw match {
    case s"$base<$count>" if base.nonEmpty && count.toIntOption.exists(_ > 0) =>
      Vec.tabulate(count.toInt)(index => s"$base$index".unsafe[NonEmpty[String]])
    case _ => Vec(name)
  }

  private def declared[A](statements: Vec[Statement], directive: String)(parse: Vec[Operand] => Option[A]): Option[A] =
    statements.collectFirst {
      case Statement.Directive(path, arguments, _, _) if path.raw == Vec(directive) => arguments.map(_.operand)
    }.flatMap(parse)

  private def version(arguments: Vec[Operand]): Option[Version] = arguments.headOption.collect {
    case Operand.Immediate(raw) => raw.raw
  }.flatMap {
    case s"$major.$minor" =>
      for {
        major <- major.toIntOption.flatMap(_.refine[Int > 0].toOption)
        minor <- minor.toIntOption.flatMap(_.refine[Int >= 0].toOption)
      } yield Version(major, minor)
    case _ => None
  }

  private def target(arguments: Vec[Operand]): Option[Target] = arguments.view.collect {
    case Operand.Symbol(name) if name.raw.startsWith("sm_") => name.raw
  }.flatMap(Target.parse(_).toOption).headOption

  /** Whether any specification section defining the instruction is available to the declared version and target;
    * sections whose syntax admits the instruction's modifiers decide when there are any.
    */
  def available(instruction: Instruction, version: Version, target: Target): ValidationOutcome[Issue] = {
    val admitting  = instruction.meta.entries.filter(_.forms.exists(modifiersMatch(instruction.mods, _)))
    val candidates = if (admitting.isEmpty) instruction.meta.entries.raw else admitting
    def required(entry: Entry): Target = requiredTarget(instruction, entry)
    if (candidates.exists(entry => version >= entry.introduced && target >= required(entry)))
      ValidationOutcome.Accepted
    else
      ValidationOutcome.Rejected((candidates.filter(_.introduced > version).map(entry =>
        Issue.VersionTooOld(instruction.qualifiedOpcode, entry.introduced, version)
      ) ++ candidates.filter(required(_) > target).map(entry =>
        Issue.TargetTooOld(instruction.qualifiedOpcode, required(entry), target)
      )).distinct.unsafe)
  }

  def validate(instruction: Instruction, version: Version, target: Target): ValidationOutcome[Issue] =
    validate(instruction, version, target, Map.empty)

  def validate(
      instruction: Instruction,
      version: Version,
      target: Target,
      registers: RegisterTypes.Scope
  ): ValidationOutcome[Issue] =
    available(instruction, version, target) match {
      case ValidationOutcome.Accepted =>
        validateForms(
          instruction,
          instruction.meta.entries.filter(entry =>
            version >= entry.introduced && target >= requiredTarget(instruction, entry)
          ).flatMap(_.forms)
        ) match {
          case ValidationOutcome.Accepted =>
            operandValues(instruction) match {
              case ValidationOutcome.Accepted => operandTypes(instruction, registers)
              case other                      => other
            }
          case other => other
        }
      case other => other
    }

  /** The register operands of `instruction` that break the type checking tables. */
  private def operandTypes(
      instruction: Instruction,
      registers: RegisterTypes.Scope
  ): ValidationOutcome[Issue] =
    RegisterTypes.violations(instruction, registers).map((position, violation) =>
      Issue.InvalidOperandType(instruction.opcode, position, violation)
    ).refine[NonEmpty[Vec[Issue]]].toOption.fold[ValidationOutcome[Issue]](ValidationOutcome.Accepted)(
      ValidationOutcome.Rejected(_)
    )

  /** The immediates of `instruction` that break the operand rules of the document. */
  private def operandValues(instruction: Instruction): ValidationOutcome[Issue] =
    OperandRules.violations(instruction).map((position, violation) =>
      Issue.InvalidOperandValue(instruction.opcode, position, violation)
    ).refine[NonEmpty[Vec[Issue]]].toOption.fold[ValidationOutcome[Issue]](ValidationOutcome.Accepted)(
      ValidationOutcome.Rejected(_)
    )

  /** A combination is superseded by a more specific matching one, so `cvt.f32.bf16` takes its own note over the note
    * for `.bf16` destinations.
    */
  private def requiredTarget(instruction: Instruction, entry: Entry): Target = {
    val modifiers = (instruction.fixedModifiers ++ instruction.mods).toSet
    val matching  = entry.modifierTargets.filter((combination, _) => combination.subsetOf(modifiers))
    matching.collect {
      case (combination, target)
          if !matching.keys.exists(other => other != combination && combination.subsetOf(other)) =>
        target
    }.foldLeft(entry.minimumTarget)((left, right) => if (right > left) right else left)
  }

  /** Checks every module against the version and target it declares; modules without `.version` are not checked and a
    * missing `.target` admits every architecture.
    */
  def validate(program: Program, forms: Boolean): ValidationOutcome[Issue] =
    ValidationOutcome.combineAll(modules(program).flatMap { module =>
      module.version.fold(Vec.empty[ValidationOutcome[Issue]]) { version =>
        val target = module.target.getOrElse(Target(Int.MaxValue))
        module.chunks.flatMap { chunk =>
          chunk.instructions.map(instruction =>
            if (forms) validate(instruction, version, target, chunk.registers)
            else available(instruction, version, target)
          )
        }
      }
    })

  private def validateForms(instruction: Instruction, forms: Vec[InstructionForm]): ValidationOutcome[Issue] =
    if (forms.isEmpty) unsupported(s"no normalised syntax form for ${instruction.qualifiedOpcode}")
    else {
      val sameCount = forms.filter(_.operands.size == instruction.operands.size)
      if (sameCount.isEmpty)
        ValidationOutcome.Rejected(Vec.nonEmpty(Issue.InvalidOperands(
          instruction.opcode,
          s"expected ${forms.map(_.operands.size).distinct.sorted(using Ordering.Int).mkString(" or ")} operands, found ${instruction.operands.size}"
        )))
      else {
        val matchingMods = sameCount.filter(form => modifiersMatch(instruction.mods, form))
        if (matchingMods.isEmpty)
          ValidationOutcome.Rejected(Vec.nonEmpty(Issue.InvalidModifiers(
            instruction.opcode,
            s"unsupported modifier sequence: ${instruction.mods.map(_.name.raw).mkString(".")}" +
              ModifierDomains.diagnose(instruction.definition, instruction.mods).fold("")(why => s" (${why.message})")
          )))
        else {
          val complete = matchingMods.filter(_.complete)
          complete.find(form => operandsMatch(instruction.operands, form.operands)) match {
            case Some(_)                   => ValidationOutcome.Accepted
            case None if complete.nonEmpty =>
              ValidationOutcome.Rejected(Vec.nonEmpty(Issue.InvalidOperands(
                instruction.opcode,
                "operand kinds do not match the syntax form"
              )))
            case None => unsupported(s"syntax form for ${instruction.qualifiedOpcode} is not fully normalised")
          }
        }
      }
    }

  private def modifiersMatch(actual: Vec[Modifier], form: InstructionForm): Boolean = {
    val ordered = canonicalOrder(actual, form)
    // Keep both interpretations when a token is optional and can fill the next required group.
    // Each occurrence consumes at most one group, including repeated source/destination types.
    val matched = ordered.foldLeft(Set(0)) { (positions, modifier) =>
      positions.flatMap { position =>
        val required = form.requiredModifierGroups.lift(position).exists(_.raw.contains(modifier))
        Option.when(required)(position + 1).toSet ++
          Option.when(form.optionalModifiers.contains(modifier))(position).toSet
      }
    }
    matched.contains(form.requiredModifierGroups.size)
  }

  private def canonicalOrder(actual: Vec[Modifier], form: InstructionForm): Vec[Modifier] =
    if (form.unorderedModifiers.isEmpty) actual
    else {
      def rank(modifier: Modifier): Int = form.requiredModifierGroups.indexWhere(group =>
        group.raw.contains(modifier) && group.raw.subsetOf(form.unorderedModifiers)
      ) match {
        case -1    => form.requiredModifierGroups.size
        case index => index
      }
      @tailrec def loop(rest: List[Modifier], sorted: Vector[Modifier]): Vector[Modifier] = rest match {
        case Nil                                                 => sorted
        case head :: _ if form.unorderedModifiers.contains(head) =>
          val (run, tail) = rest.span(form.unorderedModifiers.contains)
          loop(tail, sorted ++ run.sortBy(rank(_)))
        case head :: tail => loop(tail, sorted :+ head)
      }
      Vec.from(loop(actual.toList, Vector.empty))
    }

  private def unsupported(detail: NonEmpty[String]): ValidationOutcome[Issue] =
    ValidationOutcome.Incomplete(Vec.nonEmpty(Issue.Incomplete(detail)))

  private def operandsMatch(actual: Vec[Operand], expected: Vec[OperandConstraint]): Boolean =
    actual.zip(expected).forall {
      case (Operand.Register(_, _) | Operand.Symbol(_) | Operand.VectorLiteral(_), OperandConstraint.Register) => true
      case (Operand.WithPredicate(_, _) | Operand.Not(_), OperandConstraint.Register)                          => true
      case (Operand.Qualified(base, _), constraint)                    => operandsMatch(Vec(base), Vec(constraint))
      case (Operand.Address(_), OperandConstraint.Address)             => true
      case (Operand.VectorLiteral(_), OperandConstraint.VectorOperand) => true
      case (Operand.Symbol(_) | Operand.Qualifier(_), OperandConstraint.Symbol) => true
      case (_, OperandConstraint.Value)                                         => true
      case _                                                                    => false
    }
}
