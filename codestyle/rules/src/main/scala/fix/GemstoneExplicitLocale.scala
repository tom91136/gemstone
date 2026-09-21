package fix

import scala.meta.*
import scalafix.v1.*

/** `toLowerCase`/`toUpperCase` must pass an explicit Locale; the default-locale overloads depend on host locale. */
final class GemstoneExplicitLocale extends SyntacticRule("GemstoneExplicitLocale") {
  private val names = Set("toLowerCase", "toUpperCase")

  override def fix(using document: SyntacticDocument): Patch = document.tree.collect {
    case select @ Term.Select(_, Term.Name(name)) if names(name) && !hasArguments(select) =>
      Patch.lint(Diagnostic("explicit-locale", s"pass an explicit Locale (use Locale.ROOT) to $name", select.pos))
  }.asPatch

  private def hasArguments(select: Term.Select): Boolean = select.parent match {
    case Some(Term.Apply(`select`, arguments)) => arguments.nonEmpty
    case _                                     => false
  }
}
