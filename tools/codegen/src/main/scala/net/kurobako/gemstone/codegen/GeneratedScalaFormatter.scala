package net.kurobako.gemstone.codegen

import java.nio.file.Path
import org.scalafmt.Scalafmt

/** Applies the repository formatter before generated Scala is compared or written. */
object GeneratedScalaFormatter {
  private val Config      = Path.of(".scalafmt.conf").toAbsolutePath.normalize
  private lazy val parsed = Scalafmt.parseHoconConfigFile(Config).toEither.fold(
    error => throw IllegalStateException(s"invalid Scalafmt configuration at $Config: $error"),
    value => value
  )

  private val AnnotationImport = "import javax.annotation.processing.Generated"
  private val SystemRoots      = Set("java", "javax", "scala")

  private def withAnnotationImport(source: String): String =
    if (!source.contains("@Generated(") || source.contains(AnnotationImport)) source
    else {
      val lines   = source.split("\n", -1).toVector
      val imports = lines.zipWithIndex.filter((line, _) => line.startsWith("import "))
      val system  =
        imports.filter((line, _) => SystemRoots(line.stripPrefix("import ").takeWhile(_ != '.')))
      val (index, spaced) = system.find((line, _) => line > AnnotationImport).map((_, at) => at -> false)
        .orElse(system.lastOption.map((_, at) => (at + 1) -> false))
        .orElse(imports.headOption.map((_, at) => at -> true))
        .getOrElse(lines.indexWhere(_.nonEmpty, lines.indexWhere(_.startsWith("package ")) + 1) -> true)
      lines.patch(index, AnnotationImport +: (if (spaced) Vector("") else Vector.empty), 0).mkString("\n")
    }

  def apply(path: Path, source: String): String =
    Scalafmt.format(
      withAnnotationImport(source),
      parsed,
      Set.empty,
      path.toAbsolutePath.normalize.toString
    ).toEither.fold(
      error => throw IllegalStateException(s"cannot format generated Scala source $path: ${error.getMessage}", error),
      value => value
    )
}
