package fix

import scala.meta.*
import scalafix.v1.*

/** Keeps named top-level declarations in the file named after them.
  *
  * This is intentionally a linter rather than a rewrite: grouped declarations are a structural decision and need to be
  * reviewed before they are split. A single diagnostic is emitted per file so the first pass is actionable even for
  * generated files that contain a large number of declarations.
  */
class GemstoneFilenameConventions extends SyntacticRule("GemstoneFilenameConventions") {
  private case class Declaration(name: String, position: Position)

  override def fix(using document: SyntacticDocument): Patch =
    sourceName(document.input).fold(Patch.empty) { fileName =>
      val expected     = fileName.stripSuffix(".scala")
      val declarations = topLevelDeclarations(document.tree)
      val mismatches   = declarations.filterNot(_.name == expected)
      if (mismatches.isEmpty) Patch.empty
      else {
        val names = mismatches.map(_.name).distinct.sorted.mkString(", ")
        Patch.lint(
          Diagnostic(
            id = "top-level-filename",
            message =
              s"named top-level declarations in $fileName must match the file name '$expected.scala'; found: $names",
            position = mismatches.head.position
          )
        )
      }
    }

  private def sourceName(input: Input): Option[String] = input match {
    case file: Input.File        => file.path.toString.split("[/\\\\]").lastOption.filter(_.nonEmpty)
    case file: Input.VirtualFile => file.path.split("[/\\\\]").lastOption.filter(_.nonEmpty)
    case _                       => None
  }

  private def topLevelDeclarations(tree: Tree): List[Declaration] = tree match {
    case Source(stats) => stats.flatMap(packageStats).flatMap(declarations)
    case _             => Nil
  }

  private def packageStats(stat: Stat): List[Stat] = stat match {
    case pkg: Pkg => pkg.body.stats.flatMap(packageStats)
    case other    => List(other)
  }

  private def declarations(stat: Stat): List[Declaration] = stat match {
    case value: Defn.Val =>
      value.pats.flatMap(pattern => patternName(pattern).map(name => Declaration(name, pattern.pos)))
    case value: Decl.Val =>
      value.pats.flatMap(pattern => patternName(pattern).map(name => Declaration(name, pattern.pos)))
    case value: Defn.Var =>
      value.pats.flatMap(pattern => patternName(pattern).map(name => Declaration(name, pattern.pos)))
    case value: Decl.Var =>
      value.pats.flatMap(pattern => patternName(pattern).map(name => Declaration(name, pattern.pos)))
    case value: Defn.Def                                => List(Declaration(value.name.value, value.name.pos))
    case value: Decl.Def                                => List(Declaration(value.name.value, value.name.pos))
    case value: Defn.Type                               => List(Declaration(value.name.value, value.name.pos))
    case value: Decl.Type                               => List(Declaration(value.name.value, value.name.pos))
    case value: Defn.Class                              => List(Declaration(value.name.value, value.name.pos))
    case value: Defn.Trait                              => List(Declaration(value.name.value, value.name.pos))
    case value: Defn.Object                             => List(Declaration(value.name.value, value.name.pos))
    case value: Defn.Enum                               => List(Declaration(value.name.value, value.name.pos))
    case value: Defn.Given if value.name.value.nonEmpty => List(Declaration(value.name.value, value.name.pos))
    case _                                              => Nil
  }

  private def patternName(pattern: Pat): Option[String] = pattern match {
    case Pat.Var(name) => Some(name.value)
    case _             => None
  }
}
