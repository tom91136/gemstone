package net.kurobako.gemstone.codestyle

import scala.meta.*
import scalafix.v1.*

/** Import Gemstone namespaces once, and keep their package paths out of expressions and types. */
class NamespaceImports extends SemanticRule("NamespaceImports") {
  override def fix(using document: SemanticDocument): Patch = document.tree.collect {
    case importer: Importer
        if importer.parent.exists { case _: Import => true; case _ => false } &&
          gemstone(importer.ref) && importer.importees.exists {
            case Importee.Name(name) => !name.symbol.info.exists(_.isGiven)
            case _                   => false
          } =>
      lint(
        "namespace-import",
        "use a namespace wildcard import; retain rename, exclusion, and given selectors where needed",
        importer.pos
      )
    case reference: Term.Select if qualified(reference) && gemstone(reference) && codeReference(reference) =>
      qualifiedDiagnostic(reference)
    case reference: Type.Select if qualified(reference) && gemstone(reference) && codeReference(reference) =>
      qualifiedDiagnostic(reference)
  }.asPatch

  private def gemstone(tree: Tree)(using SemanticDocument): Boolean =
    tree.symbol.value.startsWith("net/kurobako/gemstone/")

  private def qualified(tree: Tree): Boolean =
    path(tree).dropWhile(_ == "_root_").startsWith(List("net", "kurobako", "gemstone"))

  private def path(tree: Tree): List[String] = tree match {
    case Term.Name(name)              => List(name)
    case Term.Select(qualifier, name) => path(qualifier) :+ name.value
    case Type.Select(qualifier, name) => path(qualifier) :+ name.value
    case _                            => Nil
  }

  private def codeReference(tree: Tree): Boolean = tree.parent match {
    case Some(_: Import | _: Export | _: Importer | _: Pkg) => false
    case Some(parent: Term.Select) if qualified(parent)     => false
    case Some(parent: Type.Select) if qualified(parent)     => false
    case _                                                  => true
  }

  private def qualifiedDiagnostic(tree: Tree): Patch = lint(
    "qualified-reference",
    "import the Gemstone namespace and use a short reference; use an alias for a conflicting name",
    tree.pos
  )
}
