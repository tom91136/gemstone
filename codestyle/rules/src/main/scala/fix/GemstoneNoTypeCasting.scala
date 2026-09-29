package fix

import scala.meta.*
import scalafix.v1.*

/** Requires explicit case or type-pattern matching at runtime type boundaries. */
class GemstoneNoTypeCasting extends SyntacticRule("GemstoneNoTypeCasting") {
  private val forbiddenSelections = Set("isInstanceOf", "asInstanceOf")

  override def fix(using document: SyntacticDocument): Patch = document.tree.collect {
    case selection @ Term.Select(_, Term.Name(name)) if forbiddenSelections(name) =>
      Patch.lint(
        Diagnostic(
          "no-type-casting",
          "use constructor matching, or a type pattern at an open or foreign boundary",
          selection.pos
        )
      )
  }.asPatch
}
