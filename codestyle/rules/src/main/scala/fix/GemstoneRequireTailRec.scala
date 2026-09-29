package fix

import scala.meta.*
import scalafix.v1.*

/** Enforces @annotation.tailrec on self-recursive methods whose recursive calls are all in tail positions. */
class GemstoneRequireTailRec extends SemanticRule("GemstoneRequireTailRec") {
  override def fix(using document: SemanticDocument): Patch = document.tree.collect {
    case defn: Defn.Def
        if isEligible(defn) &&
          !defn.mods.exists(_.isInstanceOf[Mod.Inline]) &&
          !hasTailrec(defn) &&
          isTailRecursive(defn) =>
      val indent = " " * defn.pos.startColumn
      Patch.addLeft(defn, s"@annotation.tailrec\n$indent") +
        Patch.lint(
          Diagnostic(
            id = "missing-tailrec",
            message = s"method `${defn.name.value}` is tail-recursive but missing @annotation.tailrec",
            position = defn.name.pos
          )
        )
  }.asPatch

  private def hasTailrec(defn: Defn.Def): Boolean = defn.mods.exists {
    case Mod.Annot(init) =>
      init.tpe match {
        case Type.Name("tailrec")                 => true
        case Type.Select(_, Type.Name("tailrec")) => true
        case _                                    => false
      }
    case _ => false
  }

  private def isEligible(defn: Defn.Def): Boolean =
    defn.mods.exists(m => m.isInstanceOf[Mod.Private] || m.isInstanceOf[Mod.Final]) ||
      isLocalDef(defn) ||
      isInsideObject(defn)

  private def isLocalDef(defn: Defn.Def): Boolean =
    defn.parent.exists {
      case _: Term.Block | _: Defn.Def => true
      case _                           => false
    }

  private def isInsideObject(defn: Defn.Def): Boolean = {
    var curr: Option[Tree] = defn.parent
    while (curr.isDefined)
      curr.get match {
        case _: Defn.Object                               => return true
        case _: Defn.Class | _: Defn.Trait | _: Defn.Enum => return false
        case _                                            => curr = curr.get.parent
      }
    false
  }

  private def isTailRecursive(defn: Defn.Def)(using doc: SemanticDocument): Boolean = {
    val methodSymbol = defn.name.symbol
    if (methodSymbol.isNone) false
    else {
      val tailExprs = tailExpressions(defn.body)
      val tailCalls = tailExprs.filter(t => isCallToSymbol(t, methodSymbol))

      if (tailCalls.isEmpty) false
      else {
        val tailCallPositions = tailCalls.flatMap(t => extractCallRef(t, methodSymbol).map(_.pos)).toSet
        val allReferences     = defn.body.collect {
          case ref: Term.Ref
              if ref.symbol == methodSymbol && !ref.parent.exists(_.isInstanceOf[Pat.Var]) =>
            ref
        }
        allReferences.forall(ref => tailCallPositions.contains(ref.pos))
      }
    }
  }

  private def tailExpressions(term: Term): List[Term] = term match {
    case Term.Block(stats) =>
      stats.lastOption match {
        case Some(t: Term) => tailExpressions(t)
        case _             => Nil
      }
    case conditional: Term.If =>
      tailExpressions(conditional.thenp) ++ tailExpressions(conditional.elsep)
    case matched: Term.Match =>
      matched.casesBlock.cases.flatMap(c => tailExpressions(c.body))
    case other =>
      List(other)
  }

  private def isCallToSymbol(term: Term, methodSymbol: Symbol)(using doc: SemanticDocument): Boolean =
    extractCallRef(term, methodSymbol).isDefined

  @scala.annotation.tailrec
  private def extractCallRef(term: Term, methodSymbol: Symbol)(using doc: SemanticDocument): Option[Term.Ref] =
    term match {
      case call: Term.Apply => call.fun match {
          case ref: Term.Ref if ref.symbol == methodSymbol => Some(ref)
          case _                                           => extractCallRef(call.fun, methodSymbol)
        }
      case _ => None
    }
}
