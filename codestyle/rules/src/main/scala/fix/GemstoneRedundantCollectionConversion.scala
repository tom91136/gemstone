package fix

import scala.meta.*
import scalafix.v1.*

/** Removes identity conversions, preserving explicit widening outside equality assertions. */
class GemstoneRedundantCollectionConversion extends SemanticRule("GemstoneRedundantCollectionConversion") {
  override def fix(using document: SemanticDocument): Patch = document.tree.collect {
    case selection @ Term.Select(receiver, Term.Name(conversion))
        if collections.standardMethod(selection) && redundant(receiver, conversion, selection) =>
      Patch.replaceTree(selection, receiver.syntax) + Patch.lint(Diagnostic(
        "redundant-collection-conversion",
        "remove the identity collection conversion",
        selection.pos
      ))
  }.asPatch

  private def redundant(receiver: Term, conversion: String, selection: Term.Select)(using SemanticDocument): Boolean = {
    val exact = Map(
      "toSeq"    -> "scala/collection/immutable/Seq#",
      "toList"   -> "scala/collection/immutable/List#",
      "toVector" -> "scala/collection/immutable/Vector#"
    )
    exact.get(conversion).exists(collections.typeSymbol(receiver).contains) ||
    (conversion == "toSeq" && collections.strictSequence(receiver) && assertionArgument(selection))
  }

  private def assertionArgument(selection: Term.Select)(using SemanticDocument): Boolean =
    selection.parent.exists {
      case clause: Term.ArgClause => clause.parent.exists {
          case call: Term.Apply => call.fun.symbol.value.startsWith("munit/Assertions#assertEquals")
          case _                => false
        }
      case _ => false
    }
}
