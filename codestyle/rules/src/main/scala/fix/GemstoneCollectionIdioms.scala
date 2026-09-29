package fix

import scala.meta.*
import scalafix.v1.*

/** Collection rewrites with bounded arithmetic and unchanged evaluation of user expressions. */
class GemstoneCollectionIdioms extends SemanticRule("GemstoneCollectionIdioms") {
  override def fix(using document: SemanticDocument): Patch = document.tree.collect {
    case call: Term.Apply => rewrite(call)
  }.asPatch

  private def rewrite(call: Term.Apply)(using SemanticDocument): Patch =
    (call.fun, call.argClause.values) match {
      case (selection @ Term.Select(receiver, Term.Name(method @ ("exists" | "forall"))), List(predicate))
          if collections.standardMethod(selection) && collections.strictSequence(receiver) =>
        constantTarget(predicate, if (method == "exists") "==" else "!=").map { target =>
          val replacement = s"${receiver.syntax}.contains(${target.syntax})"
          replace(
            call,
            if (method == "exists") replacement else s"!($replacement)",
            if (method == "exists") "exists-to-contains" else "forall-to-not-contains",
            "use contains for equality with a constant"
          )
        }.getOrElse(Patch.empty)

      case (outer @ Term.Select(inner: Term.Apply, Term.Name("getOrElse")), List(default))
          if outer.symbol.value.startsWith("scala/Option#getOrElse") =>
        (inner.fun, inner.argClause.values) match {
          case (get @ Term.Select(receiver, Term.Name("get")), List(key))
              if get.symbol.value.startsWith("scala/collection/") &&
                Set(
                  "scala/collection/immutable/Map#",
                  "scala/collection/immutable/HashMap#",
                  "scala/collection/immutable/TreeMap#"
                ).exists(collections.typeSymbol(receiver).contains) =>
            replace(
              call,
              s"${receiver.syntax}.getOrElse(${key.syntax}, ${default.syntax})",
              "get-get-or-else",
              "use getOrElse(key, default)"
            )
          case _ => Patch.empty
        }

      case (take @ Term.Select(inner: Term.Apply, Term.Name("take")), List(Lit.Int(length)))
          if length >= 0 && collections.standardMethod(take) =>
        (inner.fun, inner.argClause.values) match {
          case (drop @ Term.Select(receiver, Term.Name("drop")), List(Lit.Int(from)))
              if from >= 0 && from.toLong + length <= Int.MaxValue &&
                collections.standardMethod(drop) && collections.strictSequence(receiver) =>
            replace(
              call,
              s"${receiver.syntax}.slice($from, ${from + length})",
              "drop-take-to-slice",
              "use slice for constant nonnegative bounds"
            )
          case _ => Patch.empty
        }
      case _ => Patch.empty
    }

  private def constantTarget(predicate: Term, operator: String): Option[Lit] = {
    def comparison(body: Term, parameter: Term => Boolean): Option[Lit] = body match {
      case infix: Term.ApplyInfix if infix.op.value == operator && parameter(infix.lhs) =>
        infix.argClause.values match {
          // Null and custom equality are deliberately excluded. Literals cannot capture
          // the predicate parameter, throw, or introduce extra evaluations on an empty input.
          case List(value @ (_: Lit.Int | _: Lit.Long | _: Lit.String | _: Lit.Char | _: Lit.Boolean)) => Some(value)
          case _                                                                                       => None
        }
      case _ => None
    }
    predicate match {
      case Term.AnonymousFunction(body) => comparison(body, { case _: Term.Placeholder => true; case _ => false })
      case function: Term.Function      => function.paramClause.values match {
          case List(parameter) => comparison(
              function.body,
              {
                case name: Term.Name => name.value == parameter.name.value
                case _               => false
              }
            )
          case _ => None
        }
      case _ => None
    }
  }

  private def replace(tree: Tree, replacement: String, id: String, message: String): Patch =
    Patch.replaceTree(tree, replacement) + Patch.lint(Diagnostic(id, message, tree.pos))
}
