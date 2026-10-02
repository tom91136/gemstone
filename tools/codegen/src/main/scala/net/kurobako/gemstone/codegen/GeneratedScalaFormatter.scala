package net.kurobako.gemstone.codegen

import java.nio.file.Path
import org.scalafmt.Scalafmt

/** Applies the repository formatter before generated Scala is compared or written. Generators emit the final runtime
  * vocabulary themselves; formatting is the only post-processing step.
  */
object GeneratedScalaFormatter {
  private val Config      = Path.of(".scalafmt.conf").toAbsolutePath.normalize
  private lazy val parsed = Scalafmt.parseHoconConfigFile(Config).toEither.fold(
    error => throw IllegalStateException(s"invalid Scalafmt configuration at $Config: $error"),
    value => value
  )

  def apply(path: Path, source: String): String =
    Scalafmt.format(source, parsed, Set.empty, path.toAbsolutePath.normalize.toString).toEither.fold(
      error => throw IllegalStateException(s"cannot format generated Scala source $path: ${error.getMessage}", error),
      value => value
    )
}
