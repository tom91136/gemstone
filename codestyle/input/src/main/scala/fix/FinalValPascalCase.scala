/*
rule = GemstoneFinalValPascalCase
 */
package fix

object FinalValPascalCase {
  final val lowerCase               = 1      // assert: GemstoneFinalValPascalCase.final-val-name
  private final val SCREAMING_SNAKE = 2      // assert: GemstoneFinalValPascalCase.final-val-name
  final val `3D`                    = 3      // assert: GemstoneFinalValPascalCase.final-val-name
  final val `has space`             = 4      // assert: GemstoneFinalValPascalCase.final-val-name
  final val First, second           = 5      // assert: GemstoneFinalValPascalCase.final-val-name
  final val (_, tupleValue)         = (6, 7) // assert: GemstoneFinalValPascalCase.final-val-name
  final lazy val lazyValue          = 13     // assert: GemstoneFinalValPascalCase.final-val-name
  final val MaximumBytes            = 8
  final val HTTP2Port               = 9
  final val M0                      = 10
  val ordinaryValue                 = 11
  final def ordinaryMethod: Int     = ordinaryValue

  @SuppressWarnings(Array("scalafix:GemstoneFinalValPascalCase"))
  final val external_name = 12
}

class FinalParameter(final val badName: Int) // assert: GemstoneFinalValPascalCase.final-val-name
class PascalParameter(final val GoodName: Int)
