package fix

import scala.meta.*
import scalafix.v1.*

/** Prefer Scala 3's unmodified classes; use open for classes designed for extension. */
class GemstoneNoFinalClass extends SyntacticRule("GemstoneNoFinalClass") {
  override def fix(using document: SyntacticDocument): Patch = document.tree.collect {
    case definition: Defn.Class => definition.mods.collect {
        case modifier: Mod.Final => Patch.lint(Diagnostic(
            "final-class",
            "remove final from the class declaration; use open when extension is intended",
            modifier.pos
          ))
      }.asPatch
  }.asPatch
}
