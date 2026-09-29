package fix

import scala.meta.*
import scalafix.v1.*

/** Enforces Scala 3 creator applications. */
class GemstoneNoExplicitNew extends SyntacticRule("GemstoneNoExplicitNew") {
  override def fix(using document: SyntacticDocument): Patch = document.tree.collect {
    case term: Term.New =>
      Patch.lint(
        Diagnostic(
          id = "explicit-new",
          message = "use Scala 3 creator application syntax instead of explicit new",
          position = term.pos
        )
      )
    case term: Term.NewAnonymous =>
      Patch.lint(
        Diagnostic(
          id = "anonymous-allocation",
          message = "replace the anonymous allocation with a named implementation or a function value",
          position = term.pos
        )
      )
  }.asPatch
}
