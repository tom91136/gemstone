package fix

import scala.meta.*
import scalafix.v1.*

/** Keep companions before their types, except a Newtype's alias precedes its defining object. */
class GemstoneCompanionOrder extends SyntacticRule("GemstoneCompanionOrder") {
  override def fix(using document: SyntacticDocument): Patch = document.tree.collect {
    case source: Source     => check(source.stats)
    case pkg: Pkg           => check(pkg.body.stats)
    case template: Template => check(template.body.stats)
    case block: Term.Block  => check(block.stats)
  }.asPatch

  private def check(stats: List[Stat]): Patch = {
    val types = stats.collect {
      case definition: Defn.Class => definition.name.value -> definition
      case definition: Defn.Trait => definition.name.value -> definition
      case definition: Defn.Enum  => definition.name.value -> definition
      case definition: Defn.Type  => definition.name.value -> definition
    }.toMap
    stats.collect { case companion: Defn.Object =>
      types.get(companion.name.value).fold(Patch.empty) { definition =>
        val aliasFirst = definition match {
          case _: Defn.Type => companion.templ.inits.exists(init => isNewtype(init.tpe))
          case _            => false
        }
        val objectFirst = companion.pos.start < definition.pos.start
        if (objectFirst != aliasFirst) Patch.empty
        else Patch.lint(Diagnostic(
          "companion-order",
          if (aliasFirst) s"place type ${companion.name.value} before its Newtype object"
          else s"place object ${companion.name.value} before its companion type",
          companion.name.pos
        ))
      }
    }.asPatch
  }

  @scala.annotation.tailrec
  private def isNewtype(tpe: Type): Boolean = tpe match {
    case application: Type.Apply              => isNewtype(application.tpe)
    case Type.Name("Newtype")                 => true
    case Type.Select(_, Type.Name("Newtype")) => true
    case _                                    => false
  }
}
