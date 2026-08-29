package fix

import scala.meta.*

import scalafix.v1.*

final class GemstoneRuntimeCollections extends SyntacticRule("GemstoneRuntimeCollections") {
  private val forbiddenTypes       = Set("Vector", "List", "Seq", "IArray", "ArraySeq")
  private val forbiddenConversions = Set("toVector", "toList", "toSeq")
  private val forbiddenMutable     =
    Set("mutable", "ArrayBuffer", "ArrayBuilder", "ArrayDeque", "HashMap", "HashSet", "LinkedHashMap", "ListBuffer")

  override def fix(implicit document: SyntacticDocument): Patch = document.tree.collect {
    case name: Name if forbiddenTypes(name.value) =>
      Patch.lint(
        Diagnostic(
          "collection-type",
          s"runtime collections must be expressed and constructed through Col, not ${name.value}",
          name.pos
        )
      )
    case name: Term.Name if forbiddenConversions(name.value) =>
      Patch.lint(
        Diagnostic(
          "collection-conversion",
          s"runtime collections must be converted through Col.toCol, not ${name.value}",
          name.pos
        )
      )
    case name: Name if forbiddenMutable(name.value) =>
      Patch.lint(
        Diagnostic(
          "mutable-collection",
          s"mutable collection ${name.value} is reserved for explicitly suppressed simulation hot paths",
          name.pos
        )
      )
  }.asPatch
}
