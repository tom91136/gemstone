package fix

import scala.meta.*
import scala.meta.tokens.Token
import scalafix.v1.*

/** Conditionals keep a single syntax: the optional Scala 3 `then` keyword is not used. */
class GemstoneNoThen extends SyntacticRule("GemstoneNoThen") {
  override def fix(using document: SyntacticDocument): Patch =
    document.tree.tokens.collect { case keyword: Token.KwThen =>
      Patch.lint(Diagnostic("no-then", "omit `then`; use parentheses or braces for conditionals", keyword.pos))
    }.asPatch
}
