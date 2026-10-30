package net.kurobako.gemstone.text

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.Suite

class DelimitedSuite extends Suite {
  private def split(text: String, separator: Char = ',') =
    Delimited.splitTopLevel(text, _ == separator).map(_.map(segment => segment.offset.raw -> segment.text))

  test("separators inside nested delimiters do not split") {
    assertEquals(split("a, [b, c], f(d, {e, g})"), Right(Vec(0 -> "a", 2 -> " [b, c]", 10 -> " f(d, {e, g})")))
    assertEquals(split(""), Right(Vec(0 -> "")))
    assertEquals(split("a,,b"), Right(Vec(0 -> "a", 2 -> "", 3 -> "b")))
    assertEquals(split("x:y:z", ':'), Right(Vec(0 -> "x", 2 -> "y", 4 -> "z")))
  }

  test("mismatched and unclosed delimiters report their offset") {
    assertEquals(split("a, (b]"), Left(Delimited.Unbalanced(5, "unmatched closing delimiter")))
    assertEquals(split(")("), Left(Delimited.Unbalanced(0, "unmatched closing delimiter")))
    assertEquals(split("[a, (b"), Left(Delimited.Unbalanced(4, "unclosed delimiter")))
  }
}
