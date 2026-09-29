package fix

import scala.meta.*
import scalafix.v1.*

class GemstoneRedundantCaseClassVal extends SyntacticRule("GemstoneRedundantCaseClassVal") {
  override def fix(using document: SyntacticDocument): Patch = document.tree.collect {
    case definition: Defn.Class if definition.mods.exists(_.isInstanceOf[Mod.Case]) =>
      definition.ctor.paramClauses.headOption.toList.flatMap(_.values).collect {
        case parameter if parameter.mods.size == 1 =>
          parameter.mods.head match {
            case modifier: Mod.ValParam =>
              Patch.removeTokens(modifier.tokens) + Patch.lint(Diagnostic(
                "redundant-case-class-val",
                "case-class parameters already define public vals",
                modifier.pos
              ))
            case _ => Patch.empty
          }
      }.asPatch
  }.asPatch
}
