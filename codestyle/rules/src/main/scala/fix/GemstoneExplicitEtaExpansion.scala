package fix

import scala.meta.*
import scala.util.control.NonFatal
import scalafix.v1.*

/** Requires method values to show their arity instead of relying on invisible eta expansion. */
class GemstoneExplicitEtaExpansion extends SemanticRule("GemstoneExplicitEtaExpansion") {
  override def fix(using document: SemanticDocument): Patch = document.tree.collect {
    case reference: Term.Ref if isArgument(reference) =>
      explicitApplication(reference).map { application =>
        Patch.replaceTree(reference, application) + Patch.lint(
          Diagnostic(
            id = "method-value",
            message = "write an explicit lambda or placeholder application so the method value's arity is visible",
            position = reference.pos
          )
        )
      }
  }.flatten.asPatch

  private def isArgument(reference: Term.Ref): Boolean = reference.parent match {
    case Some(arguments: Term.ArgClause)                                 => arguments.values.exists(_.eq(reference))
    case Some(assignment @ Term.Assign(_, value)) if value.eq(reference) =>
      assignment.parent.exists {
        case arguments: Term.ArgClause => arguments.values.exists(_.eq(assignment))
        case _                         => false
      }
    case _ => false
  }

  private def explicitApplication(reference: Term.Ref)(using SemanticDocument): Option[String] =
    try
      reference.symbol.info.flatMap(info =>
        info.signature match {
          case signature: MethodSignature =>
            val parameterLists = reference match {
              case Term.Select(qualifier: Term.Ref, _)
                  if info.owner.value.endsWith(".") && qualifier.symbol != info.owner =>
                signature.parameterLists match {
                  case receiver :: rest => receiver.drop(1) :: rest
                  case Nil              => Nil
                }
              case _ => signature.parameterLists
            }
            parameterLists.find(_.nonEmpty).map { parameters =>
              val placeholders = List.fill(parameters.size)("_").mkString(", ")
              s"${reference.syntax}($placeholders)"
            }
          case _ => None
        }
      )
    catch {
      case NonFatal(_) => None
    }
}
