/*
rule = GemstoneNoIterator
 */
package fix

object NoIterator {
  val scalaValues = List(1, 2, 3).iterator                // assert: GemstoneNoIterator.no-iterator
  val javaValues  = java.util.List.of(1, 2, 3).iterator() // assert: GemstoneNoIterator.no-iterator
  val mapKeys     = Map(1 -> 2).keysIterator              // assert: GemstoneNoIterator.no-iterator
  val mapValues   = Map(1 -> 2).valuesIterator            // assert: GemstoneNoIterator.no-iterator
  val lazyValues  = List(1, 2, 3).view
}
