/*
rule = GemstoneNoTypeCasting
 */
package fix

object NoTypeCasting {
  val value: Any = 1
  val tested     = value.isInstanceOf[Int] // assert: GemstoneNoTypeCasting.no-type-casting
  val cast       = value.asInstanceOf[Int] // assert: GemstoneNoTypeCasting.no-type-casting
  val matched    = value match {
    case integer: Int => Some(integer)
    case _            => None
  }
}
