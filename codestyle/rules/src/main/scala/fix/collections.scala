package fix

import scala.meta.*
import scalafix.v1.*

private object collections {
  // Restrict rewrites to known strict implementations. In particular, arbitrary Iterable,
  // Set, Iterator, lazy collections and application methods need not share these semantics.
  private val strictSequences = Set(
    "scala/collection/immutable/List#",
    "scala/collection/immutable/Vector#",
    "scala/collection/immutable/ArraySeq#",
    "scala/collection/immutable/Range#",
    "scala/collection/immutable/Range.Inclusive#"
  )

  def standardMethod(selection: Term.Select)(using SemanticDocument): Boolean =
    selection.symbol.value.startsWith("scala/collection/")

  def strictSequence(term: Term)(using SemanticDocument): Boolean =
    typeSymbol(term).exists(strictSequences)

  def typeSymbol(term: Term)(using SemanticDocument): Option[String] = {
    @scala.annotation.tailrec
    def resolve(tpe: SemanticType, visited: Set[Symbol]): Option[String] = tpe match {
      case TypeRef(_, symbol, _) if !visited(symbol) =>
        symbol.info.map(_.signature) match {
          case Some(TypeSignature(_, lower, upper)) if lower == upper => resolve(upper, visited + symbol)
          case _                                                      => Some(symbol.value)
        }
      case _ => None
    }
    val information = term match {
      case reference: Term.Name   => reference.symbol.info
      case selection: Term.Select => selection.symbol.info
      case _                      => None
    }
    information.map(_.signature).flatMap {
      case ValueSignature(tpe)          => resolve(tpe, Set.empty)
      case MethodSignature(_, Nil, tpe) => resolve(tpe, Set.empty)
      case _                            => None
    }
  }
}
