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
    val rejected = outcomes.collect { case Rejected(issues) => issues }.flatten
    rejected.refineOption[MinLength[1]] match {
      case Some(issues) => Rejected(issues)
      case None         =>
        outcomes.collect { case Incomplete(issues) => issues }.flatten.refineOption[MinLength[1]] match {
          case Some(issues) => Incomplete(issues)
          case None         => Accepted
        }
    }
  }
}
