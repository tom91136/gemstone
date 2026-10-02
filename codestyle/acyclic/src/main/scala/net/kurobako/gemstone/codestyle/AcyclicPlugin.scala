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
import java.nio.file.Files
import java.nio.file.Path
import scala.collection.mutable

class AcyclicPlugin extends StandardPlugin {
  val name        = "acyclic"
  val description = "Check source cycles and report structural coupling"

  private val analysis = new Analysis

  override def initialize(options: List[String])(using Context): List[PluginPhase] = {
    options.filterNot(_ == "warn").foreach(option => report.error(s"unknown acyclic option: $option"))
    List(new AcyclicPhase(analysis, warningsOnly = options.contains("warn")))
  }
}

private class Analysis {
  val paths     = mutable.Set.empty[String]
  val processed = mutable.Set.empty[String]
  val nodes     = mutable.Map.empty[String, Architecture.Node]
  val edges     = mutable.Map.empty[(String, String, Architecture.Relation, Boolean), Architecture.Edge]
  val fileEdges = mutable.Map.empty[(String, String, Architecture.Relation), SourcePosition]

  def clear(): Unit = {
    paths.clear()
    processed.clear()
    nodes.clear()
    edges.clear()
    fileEdges.clear()
  }
}

private class AcyclicPhase(analysis: Analysis, warningsOnly: Boolean) extends PluginPhase {
  import Architecture.*

  val phaseName                         = "acyclic"
  override val runsAfter                = Set("posttyper")
  override val runsBefore               = Set("pickler")
  override def run(using Context): Unit = ()

  override def runOn(units: List[CompilationUnit])(using ctx: Context): List[CompilationUnit] = {
    val sources = units.filterNot(_.isJava).sortBy(_.source.path)
    // Macro expansion can resume suspended files in further runs of the same compilation.
    if (!ctx.run.isCompilingSuspended) analysis.clear()
    analysis.paths ++= ctx.run.files.map(_.path)
    analysis.processed ++= sources.map(_.source.path)
    val currentPaths = sources.map(_.source.path).toSet
    analysis.edges.filterInPlace((_, edge) => !currentPaths(edge.path))
    analysis.fileEdges.filterInPlace((key, _) => !currentPaths(key._1))
    val paths     = analysis.paths
    val nodes     = analysis.nodes
    val edges     = analysis.edges
    val fileEdges = analysis.fileEdges

    def valid(symbol: Symbol): Boolean =
      symbol != NoSymbol && symbol.source != null && symbol.source != NoSource && paths(symbol.source.path)

    def enclosingType(symbol: Symbol): Symbol =
      if (symbol == NoSymbol || symbol.is(Flags.Package) || symbol.isTopLevelDefinitionsObject) NoSymbol
      else if (symbol.isClass && !symbol.isAnonymousClass) symbol
      else enclosingType(symbol.owner)

    def qualifiedName(symbol: Symbol): String =
      if (symbol.is(Flags.Package)) symbol.fullName.toString
      else if (symbol.isTopLevelDefinitionsObject) qualifiedName(symbol.owner)
      else {
        val prefix = qualifiedName(symbol.owner)
        val name   = symbol.effectiveName.toString
        if (prefix == "<empty>") name else s"$prefix.$name"
      }

    def family(symbol: Symbol): Symbol = {
      val parent = enclosingType(symbol.owner)
      if (parent == NoSymbol) symbol else family(parent)
    }

    def exposed(symbol: Symbol, initial: Boolean = true): Boolean =
      if (symbol == NoSymbol) false
      else if (symbol.is(Flags.Package)) true
      else if (symbol.is(Flags.Private) || symbol.privateWithin != NoSymbol) false
      else if (!initial && symbol.is(Flags.Method) && !symbol.isConstructor) false
      else exposed(symbol.owner, false)

    def node(symbol: Symbol): Option[Node] = {
      val owner =
        if (symbol != NoSymbol && symbol.isType && !symbol.isClass && symbol.owner.isTopLevelDefinitionsObject) symbol
        else enclosingType(symbol)
      Option.when(valid(owner)) {
        val name = qualifiedName(owner)
        nodes.getOrElseUpdate(name, Node(name, owner.source.path, qualifiedName(family(owner))))
      }
    }

    for (unit <- sources) {
      var owner: Symbol                                             = NoSymbol
      var macroDeclaration                                          = false
      var macroCall                                                 = false
      var relation                                                  = Relation.References
      var synthetic                                                 = false
      def add(symbol: Symbol, tree: tpd.Tree, kind: Relation): Unit =
        if (valid(symbol)) {
          val actualKind = if (macroCall) Relation.MacroExpansion else kind
          val targetPath = symbol.source.path
          if (targetPath != unit.source.path) {
            fileEdges.getOrElseUpdate((unit.source.path, targetPath, actualKind), tree.sourcePos)
          }
          for {
            from <- node(owner)
            to   <- node(symbol) if from.name != to.name
          }
            edges.getOrElseUpdate(
              (from.name, to.name, actualKind, synthetic),
              Edge(from.name, to.name, actualKind, unit.source.path, tree.sourcePos.line + 1, synthetic)
            )
        }
      def addType(tree: tpd.Tree, kind: Relation): Unit = {
        val traverser = new TypeTraverser {
          private var qualifier         = false
          def traverse(tpe: Type): Unit = {
            add(tpe.typeSymbol, tree, if (qualifier) Relation.Qualifier else kind)
            tpe match {
              case named: NamedType =>
                val saved = qualifier
                qualifier ||= named.prefix.termSymbol.is(Flags.Module) || named.prefix.typeSymbol.is(Flags.ModuleClass)
                traverse(named.prefix)
                qualifier = saved
              case _ => traverseChildren(tpe)
            }
          }
        }
        if (tree.hasType) traverser.traverse(tree.tpe)
      }

      val traverser = new tpd.TreeTraverser {
        def withRelation(kind: Relation)(body: => Unit): Unit = {
          val saved = relation
          relation = kind
          try body
          finally relation = saved
        }
        override def traverse(tree: tpd.Tree)(using Context): Unit = {
          if (tree.isEmpty) return
          val savedOwner       = owner
          val savedDeclaration = macroDeclaration
          val savedCall        = macroCall
          val savedSynthetic   = synthetic
          try {
            tree match {
              case definition: tpd.MemberDef
                  if definition.symbol.is(Flags.Synthetic) &&
                    !definition.symbol.is(Flags.ParamAccessor) && !definition.symbol.isConstructor &&
                    !definition.symbol.isTopLevelDefinitionsObject && !definition.symbol.isAnonymousFunction &&
                    !definition.symbol.isAnonymousClass => synthetic = true
              case _ => ()
            }
            tree match {
              case definition: tpd.TypeDef if definition.symbol.isClass =>
                owner = definition.symbol
                node(owner)
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
                tree match {
                  case value: tpd.ValDef =>
                    val kind = if (value.symbol.owner.isClass) Relation.Holds else relation
                    withRelation(kind)(traverse(value.tpt))
                    if (exposed(value.symbol) && value.symbol.owner.isClass)
                      addType(value.tpt, Relation.Exposes)
                    traverse(value.rhs)
                  case method: tpd.DefDef =>
                    withRelation(if (exposed(method.symbol)) Relation.Exposes else Relation.References) {
                      method.paramss.flatten.foreach(traverse)
                      traverse(method.tpt)
                    }
                    traverse(method.rhs)
                  case definition: tpd.TypeDef if !definition.symbol.isClass =>
                    withRelation(if (exposed(definition.symbol)) Relation.Exposes else Relation.References) {
                      traverseChildren(tree)
                    }
                  case _ => traverseChildren(tree)
                }
              case template: tpd.Template =>
                traverse(template.constr)
                template.parents.foreach { parent =>
                  if (parent.hasType) add(parent.tpe.typeSymbol, parent, Relation.Inherits)
                  traverse(parent)
                }
                traverse(template.self)
                template.body.foreach(traverse)
              case application: tpd.Apply =>
                add(
                  application.fun.symbol,
                  tree,
                  if (application.fun.symbol.isConstructor) Relation.Constructs else Relation.Calls
                )
                addType(tree, relation)
                traverseChildren(tree)
              case select: tpd.Select =>
                val kind = if (select.symbol.is(Flags.Method)) {
                  if (select.symbol.isConstructor) Relation.Constructs else Relation.Calls
                } else relation
                add(select.symbol, tree, kind)
                addType(tree, relation)
                withRelation(if (select.qualifier.symbol.is(Flags.Module)) Relation.Qualifier else Relation.References) {
                  traverse(select.qualifier)
                }
              case _ =>
                val kind = if (tree.symbol.is(Flags.Method)) {
                  if (tree.symbol.isConstructor) Relation.Constructs else Relation.Calls
                } else relation
                add(tree.symbol, tree, kind)
                addType(tree, relation)
                traverseChildren(tree)
            }
          } finally {
            owner = savedOwner
            macroDeclaration = savedDeclaration
            macroCall = savedCall
            synthetic = savedSynthetic
          }
        }
      }
      traverser.traverse(unit.tpdTree)
    }

    val byPair  = fileEdges.toVector.groupBy { case ((from, to, _), _) => (from, to) }
    val checked = byPair.filterNot { case (_, dependencies) =>
      // Removing compile-time edges exempts exactly the cycles that cross a macro boundary.
      dependencies.forall(_._1._3 == Relation.MacroExpansion)
    }
    val graph = paths.map(path => path -> Set.empty[String]).toMap ++
      checked.keys.toVector.groupMap(_._1)(_._2).view.mapValues(_.toSet).toMap
    val output      = Path.of(ctx.settings.outputDir.value.path)
    val destination = output.resolveSibling(output.getFileName.toString + "-architecture")
    Architecture.write(destination, nodes.toMap, edges.values.toVector, analysis.processed.toVector.sorted, paths.size)
    report.echo(
      s"Architecture: ${nodes.size} types, ${checked.size} file dependencies; report: $destination (current compiler batch)"
    )
    val groups    = DependencyGraph.components(graph).filter(_.size > 1)
    val cycleRows = Vector.newBuilder[String]
    cycleRows += "group\tfrom\tto\trelations\tline"
    for ((group, index) <- groups.zipWithIndex) {
      val members  = group.toSet
      val internal = checked.toVector.filter { case ((from, to), _) => members(from) && members(to) }.sortBy(_._1)
      val details  = internal.map { case ((from, to), dependencies) =>
        val position = dependencies.minBy(_._2.start)._2
        val kinds    = dependencies.map(_._1._3.toString).distinct.sorted.mkString(", ")
        cycleRows += s"${index + 1}\t$from\t$to\t$kinds\t${position.line + 1}"
        s"  $from:${position.line + 1} -> $to [$kinds]"
      }
      val summary   = details.take(12).mkString("\n")
      val remainder = if (details.size > 12)
        s"\n  ${details.size - 12} more dependencies; complete group: ${destination.resolve("cycles.tsv")}"
      else ""
      val message  = s"Cyclic source dependency (${group.size} files):\n$summary$remainder"
      val position = internal.head._2.head._2
      if (warningsOnly) report.warning(message, position)
      else report.error(message, position)
    }
    Files.writeString(destination.resolve("cycles.tsv"), cycleRows.result().mkString("", "\n", "\n"))
    units
  }
}
