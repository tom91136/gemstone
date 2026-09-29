package fix

import scala.meta.*
import scalafix.v1.*

/** Restrict implementation access through private declarations or namespaces, rather than package qualifiers. */
class GemstoneNoPackagePrivate extends SemanticRule("GemstoneNoPackagePrivate") {
  override def fix(using document: SemanticDocument): Patch = document.tree.collect {
    case modifier @ Mod.Private(within: Name)
        if within.value.nonEmpty &&
          (within.symbol.info.exists(_.isPackage) || constructorPackage(modifier)) =>
      Patch.lint(Diagnostic(
        "package-private",
        "use plain private, no modifier, or a private namespace instead of package-qualified private",
        modifier.pos
      ))
  }.asPatch

  // Scala's SemanticDB may omit the qualifier occurrence on a primary constructor.
  private def constructorPackage(modifier: Mod.Private)(using SemanticDocument): Boolean =
    modifier.parent.flatMap {
      case constructor: Ctor.Primary => constructor.parent.collect {
          case owner: Defn.Class => owner.name.symbol
          case owner: Defn.Enum  => owner.name.symbol
        }
      case _ => None
    }.flatMap(_.info).map(_.signature).exists {
      case ClassSignature(_, _, _, declarations) =>
        declarations.exists(member =>
          member.isPrimary && member.within.flatMap(_.info).exists(_.isPackage)
        )
      case _ => false
    }
}
