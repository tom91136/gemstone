package net.kurobako.gemstone.codestyle

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

private object Architecture {
  enum Relation {
    case Inherits, Holds, Exposes, Calls, Constructs, References, Qualifier, MacroExpansion
  }
  case class Node(name: String, path: String, family: String) {
    def category: String = {
      val parts = path.replace('\\', '/').split('/').toSet
      if (parts("generated")) { if (parts("test")) "generated-test" else "generated" }
      else if (parts("test")) "test" else "handwritten"
    }
  }
  case class Edge(from: String, to: String, relation: Relation, path: String, line: Int, synthetic: Boolean)

  def write(
      output: Path,
      nodes: Map[String, Node],
      edges: Vector[Edge],
      sources: Vector[String],
      requestedSources: Int
  ): Unit = {
    Files.createDirectories(output)
    def writeFile(name: String, lines: Iterable[String]): Unit = {
      Files.writeString(output.resolve(name), lines.mkString("", "\n", "\n"), StandardCharsets.UTF_8)
      ()
    }
    // This is a snapshot of this compiler batch, never a merge with stale incremental output.
    writeFile(
      "scope.txt",
      Vector(
        "Scope: current compiler batch, including resumed macro passes; use a fresh compilation for a complete module scan.",
        "Coupling counts authored references only; excludes macro expansion and namespace qualifiers. Families group nested types with their outer declaration. Full edges, including synthetic references, remain in dependencies.tsv.",
        s"Sources traversed: ${sources.size}/$requestedSources. Counts exclude types outside these requested source files. Untraversed sources have incomplete outgoing dependencies."
      ) ++ sources
    )
    val authored = edges.filter(edge =>
      !edge.synthetic && edge.relation != Relation.MacroExpansion && edge.relation != Relation.Qualifier
    )
    def coupling(name: String, entries: Map[String, Node], dependencies: Vector[(String, String)]): Unit = {
      val graph = entries.keys.map(_ -> Set.empty[String]).toMap ++
        dependencies.filter((from, to) => from != to).groupMap(_._1)(_._2).view.mapValues(_.toSet).toMap
      val incoming =
        dependencies.filter((from, to) => from != to).groupMap(_._2)(_._1).view.mapValues(_.toSet.size).toMap
      val reachable = DependencyGraph.reachability(graph)
      val ranked    = entries.values.toVector.sortBy(node =>
        (node.category, -graph(node.name).size, -reachable(node.name), node.name)
      )
      writeFile(
        name,
        Vector("category\ttype\tdirect_out\tdirect_in\ttransitive_out\tsource") ++ ranked.map { node =>
          s"${node.category}\t${node.name}\t${graph(node.name).size}\t${incoming.getOrElse(node.name, 0)}\t${reachable(node.name)}\t${node.path}"
        }
      )
    }
    coupling("coupling.tsv", nodes, authored.map(edge => (edge.from, edge.to)))
    val families = nodes.values.map(node => node.family -> node.copy(name = node.family)).toMap
    coupling("families.tsv", families, authored.map(edge => (nodes(edge.from).family, nodes(edge.to).family)))
    writeFile(
      "dependencies.tsv",
      Vector("from\tto\trelation\tsource\tline\torigin") ++
        edges.sortBy(e => (e.from, e.to, e.relation.toString, e.line, e.synthetic)).map { edge =>
          s"${edge.from}\t${edge.to}\t${edge.relation}\t${edge.path}\t${edge.line}\t${
              if (edge.synthetic) "synthetic" else "authored"
            }"
        }
    )
  }
}
