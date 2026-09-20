package fix

object ExplicitEtaExpansion {
  final case class Box(value: Int)

  private def increment(value: Int): Int    = value + 1
  private val incrementFunction: Int => Int = value => value + 1
  extension (value: Int) {
    def parity: Int            = value & 1
    def plus(amount: Int): Int = value + amount
  }

  // format: off
  val rejected: List[Int]          = List(1).map(increment(_))
  val constructor: List[Box]       = List(1).map(Box.apply(_))
  val extensionMethod: List[Int]   = List(1).map(1.plus(_))
  // format: on
  val accepted: List[Int]          = List(1).map(increment(_))
  val functionValue: List[Int]     = List(1).map(incrementFunction)
  val extensionAccessor: List[Int] = List(1).filter(value => value.parity == 1)
}
