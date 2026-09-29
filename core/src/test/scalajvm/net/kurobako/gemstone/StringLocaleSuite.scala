package net.kurobako.gemstone

import java.util.Locale
import net.kurobako.gemstone.string.*

class StringLocaleSuite extends munit.FunSuite {
  test("numeric formatting is independent of the default locale") {
    val previous = Locale.getDefault
    try {
      Locale.setDefault(Locale.FRANCE)
      assertEquals(string.formatRoot("%.1f", java.lang.Double.valueOf(1.5)), "1.5")
    } finally Locale.setDefault(previous)
  }

  test("case conversion is independent of the default locale") {
    val previous = Locale.getDefault
    try {
      Locale.setDefault(Locale.forLanguageTag("tr-TR"))
      assertEquals("identifier".toUpperCaseRoot, "IDENTIFIER")
      assertEquals("IDENTIFIER".toLowerCaseRoot, "identifier")
    } finally Locale.setDefault(previous)
  }

}
