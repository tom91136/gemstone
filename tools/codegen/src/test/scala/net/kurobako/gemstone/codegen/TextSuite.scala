package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.testing.Suite

class TextSuite extends Suite {
  test("text helpers tokenise fixed generator formats without regex") {
    assertEquals(Text.normaliseWhitespace("  alpha\t beta\n"), "alpha beta")
    assertEquals(Text.stripLineComment("value // note"), "value")
    assertEquals(Text.stripLeadingSignGroup("{!}predicate"), "predicate")
    assertEquals(Text.splitWords("alpha, beta|gamma", Set(',', '|')), Vector("alpha", "beta", "gamma"))
    assertEquals(Text.identifierWords("OpType-Image.2D"), Vector("OpType", "Image", "2D"))
    assertEquals(Text.splitDelimited("a\t\tb\t", '\t'), Vector("a", "", "b", ""))
    assert(Text.registerToken("v17", 'v'))
    assert(!Text.registerToken("vcc", 'v'))
    assert(Text.signedInteger("-42"))
    assert(!Text.signedInteger("42.5"))
    assert(Text.startsDecimalNumber("+42.5_suffix"))
    assert(!Text.startsDecimalNumber("42"))
  }
}
