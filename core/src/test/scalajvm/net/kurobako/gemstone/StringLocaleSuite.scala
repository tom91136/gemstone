package net.kurobako.gemstone

import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.testing.Suite

class StringLocaleSuite extends Suite {
  test("numeric formatting is independent of the default locale") {
    val previous = java.util.Locale.getDefault
    try {
      java.util.Locale.setDefault(java.util.Locale.FRANCE)
      // Exercise the Java Formatter Object-argument boundary under a non-root locale.
      @SuppressWarnings(Array("scalafix:NoBoxedPrimitives"))
      val rendered = string.formatRoot("%.1f", java.lang.Double.valueOf(1.5))
      assertEquals(rendered, "1.5")
    } finally java.util.Locale.setDefault(previous)
  }

  test("case conversion is independent of the default locale") {
    val previous = java.util.Locale.getDefault
    try {
      java.util.Locale.setDefault(java.util.Locale.forLanguageTag("tr-TR"))
      assertEquals("identifier".toUpperCaseRoot, "IDENTIFIER")
      assertEquals("IDENTIFIER".toLowerCaseRoot, "identifier")
    } finally java.util.Locale.setDefault(previous)
  }

}
