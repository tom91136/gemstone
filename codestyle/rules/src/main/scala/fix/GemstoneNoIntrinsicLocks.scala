package fix

import scala.meta.*

import scalafix.v1.*

/** Prohibits JVM monitor locking in favour of explicit concurrent primitives. */
final class GemstoneNoIntrinsicLocks extends SyntacticRule("GemstoneNoIntrinsicLocks") {
  override def fix(using document: SyntacticDocument): Patch = document.tree.collect {
    case Term.Apply(select @ Term.Select(_, Term.Name("synchronized")), _) =>
      Patch.lint(
        Diagnostic(
          id = "intrinsic-lock",
          message = "use an explicit java.util.concurrent lock",
          position = select.name.pos
        )
      )
  }.asPatch
}
