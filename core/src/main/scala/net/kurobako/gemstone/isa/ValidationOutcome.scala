package net.kurobako.gemstone.isa

import net.kurobako.gemstone.Vec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.annotation.unchecked.uncheckedVariance

/** Outcome of checking an ISA value against a selected target or environment.
  *
  * `Rejected` means the value violates a fully understood rule. `Incomplete` means validation cannot yet make a
  * complete claim for the value. Keeping these outcomes distinct prevents incomplete validators from accepting input.
  */
enum ValidationOutcome[+Issue] {
  case Accepted
  case Rejected(issues: Vec[Issue @uncheckedVariance] :| MinLength[1])
  case Incomplete(issues: Vec[Issue @uncheckedVariance] :| MinLength[1])
}

object ValidationOutcome {
  def combineAll[Issue](outcomes: Vec[ValidationOutcome[Issue]]): ValidationOutcome[Issue] = {
    def issuesOf(select: ValidationOutcome[Issue] => Option[Vec[Issue]]): Option[Vec[Issue] :| MinLength[1]] =
      outcomes.flatMap(select).flatten.refineOption[MinLength[1]]
    issuesOf { case Rejected(values) => Some(values); case _ => None } match {
      case Some(issues) => Rejected(issues)
      case None         => issuesOf { case Incomplete(values) => Some(values); case _ => None } match {
          case Some(issues) => Incomplete(issues)
          case None         => Accepted
        }
    }
  }
}
