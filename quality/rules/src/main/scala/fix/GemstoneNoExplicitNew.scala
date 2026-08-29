package fix

import scala.meta.*

import scalafix.v1.*

/** Enforces Scala 3 creator applications throughout handwritten and generated Scala sources. */
final class GemstoneNoExplicitNew extends SyntacticRule("GemstoneNoExplicitNew") {
  override def fix(implicit document: SyntacticDocument): Patch = document.tree.collect {
    case term: Term.New =>
      Patch.lint(
        Diagnostic(
          "explicit-new",
          "use Scala 3 creator application syntax instead of explicit new",
          term.pos
        )
      )
    case term: Term.NewAnonymous =>
      Patch.lint(
        Diagnostic(
          "explicit-new-anonymous",
          "replace the anonymous allocation with a named implementation or a function value",
          term.pos
        )
      )
  }.asPatch
}
