/*
rule = GemstoneSafeStringCase
 */
package fix

import java.util.Locale

object SafeStringCase {
  val lower          = "ABC".toLowerCase                        // assert: GemstoneSafeStringCase.safe-string-case
  val upper          = "abc".toUpperCase                        // assert: GemstoneSafeStringCase.safe-string-case
  val lowerParens    = "ABC".toLowerCase()                      // assert: GemstoneSafeStringCase.safe-string-case
  val upperParens    = "abc".toUpperCase()                      // assert: GemstoneSafeStringCase.safe-string-case
  val lowerRoot      = "ABC".toLowerCase(Locale.ROOT)           // assert: GemstoneSafeStringCase.safe-string-case
  val upperRoot      = "abc".toUpperCase(Locale.ROOT)           // assert: GemstoneSafeStringCase.safe-string-case
  val lowerQualified = "ABC".toLowerCase(java.util.Locale.ROOT) // assert: GemstoneSafeStringCase.safe-string-case
  val upperQualified = "abc".toUpperCase(java.util.Locale.ROOT) // assert: GemstoneSafeStringCase.safe-string-case
  val lowerOther     = "ABC".toLowerCase(Locale.ENGLISH)        // assert: GemstoneSafeStringCase.safe-string-case
  val upperOther     = "abc".toUpperCase(Locale.ENGLISH)        // assert: GemstoneSafeStringCase.safe-string-case
  val lowerReference: Locale => String = "ABC".toLowerCase // assert: GemstoneSafeStringCase.safe-string-case
  val upperReference: Locale => String = "abc".toUpperCase // assert: GemstoneSafeStringCase.safe-string-case

  val characterLower = 'A'.toLower
  val characterUpper = Character.toUpperCase('a')

  private object Unrelated {
    def toLowerCase(): String               = "lower"
    def toUpperCase(locale: Locale): String = "upper"
  }
  val unrelatedLower = Unrelated.toLowerCase()
  val unrelatedUpper = Unrelated.toUpperCase(Locale.ROOT)

  @SuppressWarnings(Array("scalafix:GemstoneSafeStringCase"))
  def implementationBoundary(value: String): String = value.toLowerCase(Locale.ROOT)
}
