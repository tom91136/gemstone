package net.kurobako.gemstone

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

/** Portable text constraint for values that cross native string boundaries. */
final class NoNul
object NoNul {
  @scala.annotation.nowarn("msg=unused implicit parameter")
  final class NoNulConstraint[Implementation <: Constraint[String, Contain["\u0000"]]](using Implementation)
      extends Constraint[String, NoNul] {
    override inline def test(inline value: String): Boolean =
      !scala.compiletime.summonInline[Implementation].test(value)
    override inline def message: String = "Should not contain a NUL character"
  }

  inline given [Implementation <: Constraint[String, Contain["\u0000"]]](using
      inline implementation: Implementation
  ): NoNulConstraint[Implementation] = NoNulConstraint()

  given RuntimeConstraint[String, NoNul] =
    RuntimeConstraint(value => !value.iterator.contains('\u0000'), "Should not contain a NUL character")

  given minimumLength[Minimum <: Int & Singleton](using
      minimum: ValueOf[Minimum]
  ): RuntimeConstraint[String, MinLength[Minimum] & NoNul] = RuntimeConstraint(
    value => value.length >= minimum.value && !value.iterator.contains('\u0000'),
    s"Should have a minimum length of ${minimum.value} & Should not contain a NUL character"
  )
}
