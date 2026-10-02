package fix

import scala.meta.*
import scalafix.v1.*

/** Keeps domain traversal explicit instead of depending on case-class field layout. */
class GemstoneNoProductTraversal extends SyntacticRule("GemstoneNoProductTraversal") {
  private val productTypes     = Set("Product", "scala.Product", "_root_.scala.Product")
  private val traversalMembers =
    Set("productIterator", "productElement", "productElementNames", "productElementName", "productArity")

  private def diagnostic(tree: Tree): Patch = Patch.lint(
    Diagnostic(
      "product-traversal",
      "match the domain's typed cases explicitly; reserve Product traversal for generic derivation boundaries",
      tree.pos
    )
  )

  override def fix(using document: SyntacticDocument): Patch = document.tree.collect {
    case pattern @ Pat.Typed(_, tpe) if productTypes(tpe.syntax)               => diagnostic(pattern)
    case selection @ Term.Select(_, Term.Name(name)) if traversalMembers(name) => diagnostic(selection)
  }.asPatch
}
