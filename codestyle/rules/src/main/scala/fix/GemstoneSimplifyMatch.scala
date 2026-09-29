package fix

import scala.meta.*
import scalafix.v1.*

/** Simplifies anonymous functions matching on their single parameter to pattern matching anonymous functions. */
class GemstoneSimplifyMatch extends SyntacticRule("GemstoneSimplifyMatch") {
  override def fix(using document: SyntacticDocument): Patch = document.tree.collect {
    case fn: Term.Function
        if fn.paramClause.values.size == 1 &&
          fn.paramClause.values.head.decltpe.isEmpty && fn.paramClause.values.head.mods.isEmpty =>
      val param = fn.paramClause.values.head
      val body  = fn.body
      param.name match {
        case name: Term.Name =>
          extractMatch(body) match {
            case Some(matchTerm) if matchTerm.expr.syntax == name.value && matchTerm.mods.isEmpty =>
              val cases            = matchTerm.casesBlock.cases
              val usesParamInCases = cases.exists { c =>
                c.collect {
                  case n: Term.Name if n.value == name.value => n
                }.nonEmpty
              }
              val rewrite = if (!usesParamInCases) {
                val casesSyntax = cases.map(_.syntax).mkString("\n")
                fn.parent match {
                  case Some(clause: Term.ArgClause) if clause.values == List(fn) =>
                    clause.parent match {
                      case Some(call: Term.Apply) => Patch.replaceTree(call, s"${call.fun.syntax} {\n$casesSyntax\n}")
                      case _                      => Patch.replaceTree(fn, s"{\n$casesSyntax\n}")
                    }
                  case _ =>
                    Patch.replaceTree(fn, s"{\n$casesSyntax\n}")
                }
              } else {
                Patch.empty
              }
              if (usesParamInCases) Patch.empty
              else rewrite + Patch.lint(
                Diagnostic(
                  id = "simplify-match",
                  message =
                    s"anonymous function with pattern match `${name.value} => ${name.value} match { ... }` can be simplified to `{ case ... }`",
                  position = fn.pos
                )
              )
            case _ => Patch.empty
          }
        case _ => Patch.empty
      }
  }.asPatch

  private def extractMatch(body: Term): Option[Term.Match] = body match {
    case m: Term.Match                   => Some(m)
    case Term.Block(List(m: Term.Match)) => Some(m)
    case _                               => None
  }
}
