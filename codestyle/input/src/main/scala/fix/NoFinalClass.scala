/*
rule = GemstoneNoFinalClass
 */
package fix

final class NoFinalClass                 // assert: GemstoneNoFinalClass.final-class
final case class FinalRecord(value: Int) // assert: GemstoneNoFinalClass.final-class

object ClassModifiers {
  private final class Nested // assert: GemstoneNoFinalClass.final-class
  class Plain
  case class Record(value: Int)
  open class Extensible
  sealed class Closed
  final val MaximumBytes = 64
  final def value: Int   = MaximumBytes

  @SuppressWarnings(Array("scalafix:GemstoneNoFinalClass"))
  final class Exception
}
