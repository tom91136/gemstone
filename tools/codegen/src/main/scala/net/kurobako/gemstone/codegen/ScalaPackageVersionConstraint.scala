package net.kurobako.gemstone.codegen

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[codegen] final class ScalaPackageVersionConstraint
private[codegen] object ScalaPackageVersionConstraint {
  given RuntimeConstraint[String, ScalaPackageVersionConstraint] = RuntimeConstraint(
    value =>
      value.refineOption[MinLength[4]].exists {
        case s"v$numbers" => numbers.split('_').forall(part => part.nonEmpty && part.forall(_.isDigit))
        case _            => false
      },
    "Should be a version package such as v1_6"
  )
}
