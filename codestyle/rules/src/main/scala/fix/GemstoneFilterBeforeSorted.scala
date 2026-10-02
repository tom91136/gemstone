package fix

import scala.meta.*
import scalafix.v1.*

/** Reports collection pipelines that sort before filtering, without rewriting evaluation order. */
class GemstoneFilterBeforeSorted extends SemanticRule("GemstoneFilterBeforeSorted") {
  override def fix(using document: SemanticDocument): Patch = document.tree.collect {
    case call: Term.Apply => call.fun match {
        case selection @ Term.Select(sorted, Term.Name("filter"))
            if call.argClause.values.size == 1 && collections.standardMethod(selection) && sortedCall(sorted) =>
          Patch.lint(Diagnostic(
            "filter-before-sorted",
            "prefer filter(...).sorted when changing predicate and ordering evaluation is safe",
            call.pos
          ))
        case _ => Patch.empty
      }
  }.asPatch

  private def sortedCall(term: Term)(using SemanticDocument): Boolean =
    methodSelection(term).exists(selection =>
      selection.name.value == "sorted" && collections.standardMethod(selection)
    )

  @scala.annotation.tailrec
  private def methodSelection(term: Term): Option[Term.Select] = term match {
    case selection: Term.Select      => Some(selection)
    case application: Term.Apply     => methodSelection(application.fun)
    case application: Term.ApplyType => methodSelection(application.fun)
    case _                           => None
  }
}
