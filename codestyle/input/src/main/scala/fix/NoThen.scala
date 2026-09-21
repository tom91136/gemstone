/*
rule = GemstoneNoThen
 */
package fix

object NoThen {
  val accepted = if (1 > 0) "positive" else "non-positive"
  val rejected = if 1 > 0 then "positive" else "non-positive" // assert: GemstoneNoThen.no-then
}
