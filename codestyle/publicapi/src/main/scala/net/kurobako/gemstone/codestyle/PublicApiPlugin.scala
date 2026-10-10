package net.kurobako.gemstone.codestyle

import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Flags
import dotty.tools.dotc.core.Symbols.Symbol
import dotty.tools.dotc.plugins.PluginPhase
import dotty.tools.dotc.plugins.StandardPlugin
import dotty.tools.dotc.report
import scala.collection.mutable

/** Code in the scope package may use only what a downstream user of every other package could: package nesting does not
  * grant it access to qualified-private declarations.
  */
class PublicApiPlugin extends StandardPlugin {
  val name        = "publicapi"
  val description = "Restrict a package to the public API of everything outside it"

  override def initialize(options: List[String])(using Context): List[PluginPhase] = options match {
    case Nil          => Nil
    case scope :: Nil => List(new PublicApiPhase(scope))
    case _            =>
      report.error(s"unknown publicapi options: ${options.mkString(" ")}")
      Nil
  }
}

private class PublicApiPhase(scope: String) extends PluginPhase {
  val phaseName           = "publicapi"
  override val runsAfter  = Set("posttyper")
  override val runsBefore = Set("pickler")

  private def inScope(name: String): Boolean = name == scope || name.startsWith(s"$scope.")

  private def inScope(symbol: Symbol)(using Context): Boolean = inScope(symbol.enclosingPackageClass.fullName.toString)

  private def checked(tree: tpd.Tree)(using Context): List[tpd.Tree] = tree match {
    case definition: tpd.PackageDef if inScope(definition.symbol.fullName.toString) => List(definition)
    case definition: tpd.PackageDef                                                 => definition.stats.flatMap(checked)
    case _                                                                          => Nil
  }

  private def encloses(boundary: Symbol)(using Context): Boolean = {
    val name = boundary.enclosingPackageClass.fullName.toString
    scope.startsWith(s"$name.")
  }

  private def internal(symbol: Symbol)(using Context): Option[Symbol] =
    if (!symbol.exists || symbol.is(Flags.Package) || inScope(symbol)) None
    else symbol.ownersIterator.takeWhile(!_.is(Flags.Package)).find(owner =>
      if (owner.privateWithin.exists) encloses(owner.privateWithin)
      else owner.is(Flags.Private) && encloses(owner.owner)
    )

  override def run(using context: Context): Unit = {
    val reported  = mutable.Set.empty[Int]
    val traverser = new tpd.TreeTraverser {
      override def traverse(tree: tpd.Tree)(using Context): Unit = tree match {
        case _: tpd.Import                => ()
        case inlined: tpd.Inlined         => traverse(inlined.call)
        case _: tpd.Ident | _: tpd.Select =>
          internal(tree.symbol).filter(_ => reported.add(tree.sourcePos.line)).foreach(owner =>
            report.error(s"${owner.showFullName} is not public API outside $scope", tree.srcPos)
          )
          traverseChildren(tree)
        case _ => traverseChildren(tree)
      }
    }
    checked(context.compilationUnit.tpdTree).foreach(traverser.traverse)
  }
}
