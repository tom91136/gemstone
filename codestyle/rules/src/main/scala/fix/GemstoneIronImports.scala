package fix

import scala.meta.*

import scalafix.v1.*

/** Keeps Iron imports predictable and makes the available refinement vocabulary obvious. */
final class GemstoneIronImports extends SyntacticRule("GemstoneIronImports") {
  private val allowed = Set(
    List("io", "github", "iltotore", "iron"),
    List("io", "github", "iltotore", "iron", "constraint", "all")
  )

  override def fix(using document: SyntacticDocument): Patch = document.tree.collect {
    case importer: Importer if isIronImport(importer.ref) && !isAllowed(importer) =>
      Patch.lint(
        Diagnostic(
          id = "selective-import",
          message =
            "import Iron through io.github.iltotore.iron.* and io.github.iltotore.iron.constraint.all.*",
          position = importer.pos
        )
      )
  }.asPatch

  private def isAllowed(importer: Importer): Boolean =
    allowed(importPath(importer.ref)) &&
      (importer.importees match {
        case List(_: Importee.Wildcard) => true
        case _                          => false
      })

  private def isIronImport(reference: Term.Ref): Boolean =
    importPath(reference).startsWith(List("io", "github", "iltotore", "iron"))

  private def importPath(reference: Term.Ref): List[String] = reference match {
    case Term.Name(value)                       => List(value)
    case Term.Select(qualifier: Term.Ref, name) => importPath(qualifier) :+ name.value
    case _                                      => Nil
  }
}
