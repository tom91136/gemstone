package net.kurobako.gemstone.codestyle

import dotty.tools.dotc.CompilationUnit
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Flags
import dotty.tools.dotc.core.Symbols.NoSymbol
import dotty.tools.dotc.core.Symbols.Symbol
import dotty.tools.dotc.core.Types.*
import dotty.tools.dotc.plugins.PluginPhase
import dotty.tools.dotc.plugins.StandardPlugin
import dotty.tools.dotc.report
import dotty.tools.dotc.util.NoSource
import dotty.tools.dotc.util.SourcePosition
import scala.collection.mutable

class AcyclicPlugin extends StandardPlugin {
  val name        = "acyclic"
  val description = "Check source cycles"

  private val analysis = new Analysis

  override def initialize(options: List[String])(using Context): List[PluginPhase] = {
    options.filterNot(_ == "warn").foreach(option => report.error(s"unknown acyclic option: $option"))
    List(new AcyclicPhase(analysis, warningsOnly = options.contains("warn")))
  }
}

private class Analysis {
  val paths = mutable.Set.empty[String]
  // (from file, to file, through macro expansion) -> first reference
  val edges = mutable.Map.empty[(String, String, Boolean), SourcePosition]

  def clear(): Unit = {
    paths.clear()
    edges.clear()
  }
}

private class AcyclicPhase(analysis: Analysis, warningsOnly: Boolean) extends PluginPhase {
  val phaseName                         = "acyclic"
  override val runsAfter                = Set("posttyper")
  override val runsBefore               = Set("pickler")
  override def run(using Context): Unit = ()

  override def runOn(units: List[CompilationUnit])(using ctx: Context): List[CompilationUnit] = {
    val sources = units.filterNot(_.isJava).sortBy(_.source.path)
    // Macro expansion can resume suspended files in further runs of the same compilation.
    if (!ctx.run.isCompilingSuspended) analysis.clear()
    analysis.paths ++= ctx.run.files.map(_.path)
    val currentPaths = sources.map(_.source.path).toSet
    analysis.edges.filterInPlace((key, _) => !currentPaths(key._1))
    val paths = analysis.paths
    val edges = analysis.edges

    def valid(symbol: Symbol): Boolean =
      symbol != NoSymbol && symbol.source != null && symbol.source != NoSource && paths(symbol.source.path)

    for (unit <- sources) {
      var macroDeclaration                          = false
      var macroCall                                 = false
      def add(symbol: Symbol, tree: tpd.Tree): Unit =
        if (valid(symbol) && symbol.source.path != unit.source.path)
          edges.getOrElseUpdate((unit.source.path, symbol.source.path, macroCall), tree.sourcePos)
      def addType(tree: tpd.Tree): Unit = {
        val traverser = new TypeTraverser {
          def traverse(tpe: Type): Unit = {
            add(tpe.typeSymbol, tree)
            tpe match {
              case named: NamedType => traverse(named.prefix)
              case _                => traverseChildren(tpe)
            }
          }
        }
        if (tree.hasType) traverser.traverse(tree.tpe)
      }

      val traverser = new tpd.TreeTraverser {
        override def traverse(tree: tpd.Tree)(using Context): Unit = {
          if (tree.isEmpty) return
          val savedDeclaration = macroDeclaration
          val savedCall        = macroCall
          try {
            tree match {
              case definition: tpd.DefDef            => macroDeclaration = definition.symbol.is(Flags.Macro)
              case _: tpd.Splice if macroDeclaration => macroCall = true
              case _: tpd.Quote                      => macroCall = false
              case _                                 => ()
            }
            tree match {
              case _: tpd.Import                                              => ()
              case definition: tpd.MemberDef if definition.symbol == NoSymbol => traverseChildren(tree)
              case definition: tpd.MemberDef                                  =>
                definition.symbol.annotations.foreach(annotation => traverse(annotation.tree))
                traverseChildren(tree)
              case template: tpd.Template =>
                traverse(template.constr)
                template.parents.foreach { parent =>
                  if (parent.hasType) add(parent.tpe.typeSymbol, parent)
                  traverse(parent)
                }
                traverse(template.self)
                template.body.foreach(traverse)
              case select: tpd.Select =>
                add(select.symbol, tree)
                addType(tree)
                traverse(select.qualifier)
              case _ =>
                add(tree.symbol, tree)
                addType(tree)
                traverseChildren(tree)
            }
          } finally {
            macroDeclaration = savedDeclaration
            macroCall = savedCall
          }
        }
      }
      traverser.traverse(unit.tpdTree)
    }

    val byPair  = edges.toVector.groupBy { case ((from, to, _), _) => (from, to) }
    val checked = byPair.filterNot { case (_, dependencies) =>
      // Removing compile-time edges exempts exactly the cycles that cross a macro boundary.
      dependencies.forall(_._1._3)
    }
    val graph = paths.map(path => path -> Set.empty[String]).toMap ++
      checked.keys.toVector.groupMap(_._1)(_._2).view.mapValues(_.toSet).toMap
    for (group <- DependencyGraph.components(graph).filter(_.size > 1)) {
      val members  = group.toSet
      val internal = checked.toVector.filter { case ((from, to), _) => members(from) && members(to) }.sortBy(_._1)
      val details  = internal.map { case ((from, to), dependencies) =>
        s"  $from:${dependencies.minBy(_._2.start)._2.line + 1} -> $to"
      }
      val remainder = if (details.size > 12) s"\n  ${details.size - 12} more dependencies" else ""
      val message   = s"Cyclic source dependency (${group.size} files):\n${details.take(12).mkString("\n")}$remainder"
      val position  = internal.head._2.head._2
      if (warningsOnly) report.warning(message, position)
      else report.error(message, position)
    }
    units
  }
}
