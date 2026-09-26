/*
rule = GemstoneNoAssume
 */
package fix

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

object NoAssume {
  val rejected: Int :| Positive                = 1.assume[Positive] // assert: GemstoneNoAssume.unchecked-refinement
  val checked: Either[String, Int :| Positive] = 1.refineEither[Positive]
  val suppressed: Int :| Positive              = 1.assume[Positive] // scalafix:ok GemstoneNoAssume
}

type RefinedValue = RefinedValue.T
object RefinedValue extends RefinedType[Int, Positive] {
  val rejected: RefinedValue = assume(1) // assert: GemstoneNoAssume.unchecked-refinement
}

@SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
object TrustedGeneratedData {
  val value: Int :| Positive = 1.assume[Positive]
}

object NoAssumeMunit {
  object munit {
    object Assertions {
      def assume(condition: Boolean): Boolean = condition
    }
  }
  val accepted: Boolean = munit.Assertions.assume(true)
}
