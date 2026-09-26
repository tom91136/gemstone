package net.kurobako.gemstone.isa

import net.kurobako.gemstone.{NonEmptyVec, Vec}

import scala.annotation.unchecked.uncheckedVariance

/** Outcome of checking an ISA value against a selected target or environment.
  *
  * `Rejected` means the value violates a fully understood rule. `Incomplete` means validation cannot yet make a
  * complete claim for the value. Keeping these outcomes distinct prevents incomplete validators from accepting input.
  */
enum ValidationOutcome[+Issue] {
  case Accepted
  case Rejected(issues: NonEmptyVec[Issue @uncheckedVariance])
  case Incomplete(issues: NonEmptyVec[Issue @uncheckedVariance])
}

object ValidationOutcome {
  def combineAll[Issue](outcomes: Vec[ValidationOutcome[Issue]]): ValidationOutcome[Issue] = {
    def issuesOf(select: ValidationOutcome[Issue] => Option[NonEmptyVec[Issue]]): Option[NonEmptyVec[Issue]] =
      NonEmptyVec.fromVec(outcomes.flatMap(select).flatMap(_.toVector)).toOption
    issuesOf { case Rejected(values) => Some(values); case _ => None } match {
      case Some(issues) => Rejected(issues)
      case None         => issuesOf { case Incomplete(values) => Some(values); case _ => None } match {
          case Some(issues) => Incomplete(issues)
          case None         => Accepted
        }
    }
  }
}
