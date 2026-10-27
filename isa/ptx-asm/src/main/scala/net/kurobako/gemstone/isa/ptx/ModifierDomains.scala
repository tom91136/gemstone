package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

/** The modifier slots of every instruction definition and the operands each form names, generated from the syntax
  * blocks of the PTX document.
  */
object ModifierDomains {
  private lazy val data: ujson.Value = resources.json[ujson.Value](getClass, "modifier-domains.json")

  private lazy val domains: Vec[ModifierDomain] = Vec.from(data("domains").arr.map { row =>
    val name = row(0).str
    ModifierDomain(
      name,
      ModifierSlot(name),
      row.arr.drop(1).map(value => Modifier(value.str.unsafe[NonEmpty[String]])).toSet
    )
  })

  private val constraints = "RAVSXU"

  private lazy val byDefinition: Map[String, Vec[ModifierForm]] = data("definitions").arr.map { row =>
    row(0).str -> Vec.from(row(2).arr.map { form =>
      ModifierForm(
        Vec.from(form(0).arr.map(index => domains(index.num.toInt))),
        Vec.from(form(1).arr.map(index => domains(index.num.toInt))),
        form(2).arr.map(value => Modifier(value.str.unsafe[NonEmpty[String]])).toSet,
        form(3).num.toInt == 1,
        Vec.from(form(4).arr.map { operand =>
          OperandSyntax(
            operand(0).str,
            Metadata.OperandConstraint.fromOrdinal(constraints.indexOf(operand(1).str.head)),
            operand(2).num.toInt == 1
          )
        })
      )
    })
  }.toMap

  /** The syntax alternatives of `definition`. */
  def forms(definition: Instruction.Definition): Vec[ModifierForm] =
    byDefinition.getOrElse(definition.toString, Vec.empty)

  /** The alternatives of `definition` that take all of `modifiers`. */
  def matching(definition: Instruction.Definition, modifiers: Vec[Modifier]): Vec[ModifierForm] =
    forms(definition).filter(attempt(_, modifiers).violation.isEmpty)

  /** The slot domain values of every definition, for coverage and display. */
  lazy val all: Vec[ModifierDomain] = domains

  /** Where `modifiers` stop fitting the best-matching alternative of `definition`; none when some alternative takes
    * them all, or no alternative describes the definition.
    */
  def diagnose(definition: Instruction.Definition, modifiers: Vec[Modifier]): Option[ModifierViolation] = {
    val attempts = forms(definition).map(attempt(_, modifiers))
    if (attempts.isEmpty || attempts.exists(_.violation.isEmpty)) None
    else attempts.maxBy(_.reached).violation
  }

  private case class Attempt(reached: Int, violation: Option[ModifierViolation])

  private def attempt(form: ModifierForm, actual: Vec[Modifier]): Attempt = {
    val ordered                         = canonical(form, actual)
    val known                           = (form.required ++ form.optional).flatMap(_.values).toSet
    val start                           = (Set(0), 0, Option.empty[ModifierViolation])
    val (positions, reached, violation) = ordered.foldLeft(start) { case ((positions, reached, violation), modifier) =>
      if (violation.nonEmpty) (positions, reached, violation)
      else {
        val next = positions.flatMap { position =>
          val required = form.required.lift(position).exists(_.values.contains(modifier))
          Option.when(required)(position + 1).toSet ++
            Option.when(form.optional.exists(_.values.contains(modifier)))(position).toSet
        }
        if (next.nonEmpty) (next, reached + 1, None)
        else {
          val expected = positions.flatMap(form.required.lift(_)).toVec ++ form.optional
          (
            positions,
            reached,
            Some(if (known(modifier)) ModifierViolation.Misplaced(modifier, expected)
            else ModifierViolation.Unknown(modifier))
          )
        }
      }
    }
    if (violation.nonEmpty) Attempt(reached, violation)
    else if (positions.contains(form.required.size)) Attempt(reached, None)
    else Attempt(reached, positions.flatMap(form.required.lift(_)).headOption.map(ModifierViolation.Missing(_)))
  }

  private def canonical(form: ModifierForm, actual: Vec[Modifier]): Vec[Modifier] =
    if (form.unordered.isEmpty) actual
    else {
      def rank(modifier: Modifier): Int =
        form.required.indexWhere(domain =>
          domain.values.contains(modifier) && domain.values.subsetOf(form.unordered)
        ) match {
          case -1    => form.required.size
          case index => index
        }
      val (done, run) = actual.foldLeft((Vector.empty[Modifier], Vector.empty[Modifier])) {
        case ((done, run), modifier) =>
          if (form.unordered.contains(modifier)) (done, run :+ modifier)
          else (done ++ run.sortBy(rank(_)) :+ modifier, Vector.empty[Modifier])
      }
      Vec.from(done ++ run.sortBy(rank(_)))
    }
}
