package fix

import scala.meta.*
import scalafix.v1.*

class GemstoneCollectionNonEmpty extends SemanticRule("GemstoneCollectionNonEmpty") {
  override def fix(using document: SemanticDocument): Patch = document.tree.collect {
    case comparison: Term.ApplyInfix if comparison.op.value == ">" =>
      (comparison.lhs, comparison.argClause.values) match {
        case (size @ Term.Select(_, Term.Name("size")), List(Lit.Int(0)))
            if collections.standardMethod(size) && !size.symbol.value.startsWith("scala/collection/StringOps") =>
          Patch.lint(Diagnostic(
            "size-to-non-empty",
            "use nonEmpty instead of size > 0 on a collection",
            comparison.pos
          )) +
            Patch.replaceTree(size.name, "nonEmpty") + Patch.removeTokens(
              comparison.tokens.drop(size.tokens.length)
            )
        case _ => Patch.empty
      }
  }.asPatch
}
