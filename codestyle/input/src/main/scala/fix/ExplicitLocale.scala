/*
rule = GemstoneExplicitLocale
 */
package fix

import java.util.Locale

object ExplicitLocale {
  val rejected      = "abc".toLowerCase // assert: GemstoneExplicitLocale.explicit-locale
  val rejectedUpper = "abc".toUpperCase // assert: GemstoneExplicitLocale.explicit-locale
  val accepted      = "abc".toLowerCase(Locale.ROOT)
  val acceptedUpper = "abc".toUpperCase(Locale.ROOT)
}
