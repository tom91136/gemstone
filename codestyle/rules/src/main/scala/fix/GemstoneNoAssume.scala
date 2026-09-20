package fix

import scala.meta.*

import scalafix.v1.*

/** Keeps unchecked Iron refinement visible as a deliberately suppressed exception. */
final class GemstoneNoAssume extends SyntacticRule("GemstoneNoAssume") {
  override def fix(using document: SyntacticDocument): Patch = document.tree.collect {
    case select @ Term.Select(_, Term.Name("assume")) =>
      diagnostic(select.name.pos)
    case application @ Term.Apply(Term.Name("assume"), _) =>
      diagnostic(application.fun.pos)
    case application @ Term.Apply(Term.ApplyType(Term.Name("assume"), _), _) =>
      diagnostic(application.fun.pos)
  }.asPatch

  private def diagnostic(position: Position): Patch =
    Patch.lint(
      Diagnostic(
        id = "unchecked-refinement",
        message =
          "refine runtime values at their boundary; suppress only an immediately visible proof or measured hot path",
        position = position
      )
    )
}
