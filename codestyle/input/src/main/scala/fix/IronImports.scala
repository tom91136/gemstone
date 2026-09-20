/*
rule = GemstoneIronImports
 */
package fix

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.RefinedType             // assert: GemstoneIronImports.selective-import
import io.github.iltotore.iron.constraint.all.Positive // scalafix:ok GemstoneIronImports

object IronImports {
  val value: Int :| Positive = 1
}
