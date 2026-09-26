package fix

import scala.meta.*

import scalafix.v1.*

/** Keeps unchecked Iron refinement visible as a deliberately suppressed exception.
  *
  * The rule is syntactic, so it also sees munit's `assume`. munit's assertions are excluded by receiver (`munit` or
  * `Assertions`) and, for the unqualified form, by test source path.
  */
final class GemstoneNoAssume extends SyntacticRule("GemstoneNoAssume") {
  override def fix(using document: SyntacticDocument): Patch = {
    val testSource = isTestSource(document.input)
    document.tree.collect {
      case select @ Term.Select(qualifier, Term.Name("assume")) if !isMunit(qualifier) =>
        diagnostic(select.name.pos)
      case application @ Term.Apply(Term.Name("assume"), _) if !testSource =>
        diagnostic(application.fun.pos)
      case application @ Term.Apply(Term.ApplyType(Term.Name("assume"), _), _) if !testSource =>
        diagnostic(application.fun.pos)
    }.asPatch
  }

  private def isMunit(qualifier: Term): Boolean = qualifier match {
    case Term.Name(name)                 => name == "munit" || name == "Assertions"
    case Term.Select(_, Term.Name(name)) => name == "munit" || name == "Assertions"
    case _                               => false
  }

  private def isTestSource(input: Input): Boolean = {
    val path = input match {
      case file: Input.File        => file.path.toString
      case file: Input.VirtualFile => file.path
      case _                       => return false
    }
    path.split("[/\\\\]").contains("test")
  }

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
