package net.kurobako.gemstone.codestyle

import scala.meta.*
import scalafix.v1.*

/** Repository-wide JVM API policy, matched by resolved symbols rather than source spellings. */
class ForbiddenAPIs extends SemanticRule("ForbiddenAPIs") {
  private val bannedClasses = Map(
    "java/lang/System#"     -> "use Scala APIs or a narrow platform abstraction instead of java.lang.System",
    "java/lang/Runtime#"    -> "use scala.sys.runtime instead of java.lang.Runtime",
    "java/util/ArrayDeque#" -> "use scala.collection.mutable.ArrayDeque instead of java.util.ArrayDeque"
  )

  private val bannedMembers = Vector(
    "java/nio/file/Files#setPosixFilePermissions(" ->
      "use portable file handling and conditionally apply attributes supported by the file store",
    "java/lang/String#toUpperCase(" -> "import net.kurobako.gemstone.string.* and use toUpperCaseRoot",
    "java/lang/String#toLowerCase(" -> "import net.kurobako.gemstone.string.* and use toLowerCaseRoot"
  )

  override def fix(using document: SemanticDocument): Patch = document.tree.collect {
    case reference: Term.Ref => diagnostic(reference, reference.symbol.value)
    case reference: Type.Ref => diagnostic(reference, reference.symbol.value)
  }.flatten.asPatch

  private def diagnostic(tree: Tree, symbol: String): Option[Patch] =
    bannedClasses.get(symbol)
      .orElse(
        bannedMembers.collectFirst {
          case (prefix, message) if symbol.startsWith(prefix) && !references.isSelectionName(tree) => message
        }
      )
      .map(lint("forbidden-api", _, tree.pos))
}
