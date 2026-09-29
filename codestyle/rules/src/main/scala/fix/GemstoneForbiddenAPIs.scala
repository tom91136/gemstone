package fix

import scala.meta.*
import scalafix.v1.*

/** Repository-wide JVM API policy, matched by resolved symbols rather than source spellings. */
class GemstoneForbiddenAPIs extends SemanticRule("GemstoneForbiddenAPIs") {
  private val bannedClasses = Map(
    "java/lang/System#"     -> "use Scala APIs or a narrow platform abstraction instead of java.lang.System",
    "java/lang/Runtime#"    -> "use scala.sys.runtime instead of java.lang.Runtime",
    "java/util/ArrayDeque#" -> "use scala.collection.mutable.ArrayDeque instead of java.util.ArrayDeque"
  )

  private val bannedMembers = Vector(
    "java/nio/file/Files#setPosixFilePermissions(" ->
      "use portable file handling and conditionally apply attributes supported by the file store"
  )

  override def fix(using document: SemanticDocument): Patch = document.tree.collect {
    case reference: Term.Ref => diagnostic(reference, reference.symbol.value)
    case reference: Type.Ref => diagnostic(reference, reference.symbol.value)
  }.flatten.asPatch

  private def diagnostic(tree: Tree, symbol: String): Option[Patch] =
    bannedClasses.get(symbol)
      .orElse(
        bannedMembers.collectFirst {
          case (prefix, message) if symbol.startsWith(prefix) && isOutermostSelection(tree) => message
        }
      )
      .map(message => Patch.lint(Diagnostic("forbidden-api", message, tree.pos)))

  private def isOutermostSelection(tree: Tree): Boolean = tree.parent match {
    case Some(_: Term.Select) => false
    case Some(_: Type.Select) => false
    case _                    => true
  }
}
