package net.kurobako.gemstone.isa

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.annotation.unchecked.uncheckedVariance

object ValidationOutcome {
  given CanEqual[ValidationOutcome[?], ValidationOutcome[?]] = CanEqual.derived

  def combineAll[Issue](outcomes: Vec[ValidationOutcome[Issue]]): ValidationOutcome[Issue] = {
    def issuesOf(select: ValidationOutcome[Issue] => Option[NonEmpty[Vec[Issue]]]): Option[NonEmpty[Vec[Issue]]] = {
      val issues = outcomes.flatMap(select).flatten
      Option.when(issues.nonEmpty)(issues.unsafe[NonEmpty[Vec[Issue]]])
    }
    issuesOf { case Rejected(values) => Some(values); case _ => None } match {
      case Some(issues) => Rejected(issues)
      case None         => issuesOf { case Incomplete(values) => Some(values); case _ => None } match {
          case Some(issues) => Incomplete(issues)
          case None         => Accepted
        }
    }
  }
}

/** Outcome of checking an ISA value against a selected target or environment.
  *
  * `Rejected` means the value violates a fully understood rule. `Incomplete` means validation cannot yet make a
  * complete claim for the value. Keeping these outcomes distinct prevents incomplete validators from accepting input.
  */
enum ValidationOutcome[+Issue] {
  case Accepted
  case Rejected(issues: NonEmpty[Vec[Issue @uncheckedVariance]])
  case Incomplete(issues: NonEmpty[Vec[Issue @uncheckedVariance]])
}
