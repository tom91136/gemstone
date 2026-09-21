package fix

import scala.meta.*

import scalafix.v1.*

/** Prevents single-use collection traversal from leaking into repository code. */
final class GemstoneNoIterator extends SyntacticRule("GemstoneNoIterator") {
  private val forbiddenSelections = Set("iterator", "keysIterator", "valuesIterator")

  override def fix(using document: SyntacticDocument): Patch = document.tree.collect {
    case selection @ Term.Select(_, Term.Name(name)) if forbiddenSelections(name) =>
      Patch.lint(
        Diagnostic(
          "no-iterator",
          "use .view for lazy collection traversal or adapt the Java collection boundary",
          selection.pos
        )
      )
  }.asPatch
}
