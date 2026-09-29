package fix

import scala.meta.*
import scalafix.v1.*

/** Centralize String case conversion in the locale-independent core extensions. */
class GemstoneSafeStringCase extends SemanticRule("GemstoneSafeStringCase") {
  private val replacements = Map(
    "toUpperCase" -> "toUpperCaseRoot",
    "toLowerCase" -> "toLowerCaseRoot"
  )

  override def fix(using document: SemanticDocument): Patch = document.tree.collect {
    case name: Term.Name
        if replacements.contains(name.value) &&
          name.symbol.value.startsWith(s"java/lang/String#${name.value}(") =>
      Patch.lint(Diagnostic(
        "safe-string-case",
        s"import net.kurobako.gemstone.string.* and use ${replacements(name.value)}",
        name.pos
      ))
  }.asPatch
}
