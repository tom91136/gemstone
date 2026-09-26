package net.kurobako.gemstone

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

/** Portable text constraint for values that cross native string boundaries. */
final class NoNul
object NoNul {
  private inline val NoNulMessage = "Should not contain a NUL character"

  @scala.annotation.nowarn("msg=unused implicit parameter")
  final class NoNulConstraint[Implementation <: Constraint[String, Contain["\u0000"]]](using Implementation)
      extends Constraint[String, NoNul] {
    override inline def test(inline value: String): Boolean =
      !scala.compiletime.summonInline[Implementation].test(value)
    override inline def message: String = NoNulMessage
  }

  inline given [Implementation <: Constraint[String, Contain["\u0000"]]](using
      inline implementation: Implementation
  ): NoNulConstraint[Implementation] = NoNulConstraint()

  given RuntimeConstraint[String, NoNul] =
    RuntimeConstraint(value => !value.view.contains('\u0000'), NoNulMessage)

  given minimumLength[Minimum <: Int & Singleton](using
      minimum: ValueOf[Minimum]
  ): RuntimeConstraint[String, MinLength[Minimum] & NoNul] = RuntimeConstraint(
    value => value.length >= minimum.value && !value.view.contains('\u0000'),
    s"Should have a minimum length of ${minimum.value} & $NoNulMessage"
  )
}
