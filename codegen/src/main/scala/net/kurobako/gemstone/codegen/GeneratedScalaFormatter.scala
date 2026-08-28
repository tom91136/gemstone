package net.kurobako.gemstone.codegen

import org.scalafmt.interfaces.Scalafmt

import java.nio.file.Path
import java.nio.file.Paths

/** Applies the repository formatter before generated Scala is compared or written, making formatted source the
  * canonical generator output rather than a separate post-processing state.
  */
object GeneratedScalaFormatter {
  private val Config         = Paths.get(".scalafmt.conf").toAbsolutePath.normalize
  private lazy val formatter =
    Scalafmt.create(getClass.getClassLoader).withRespectProjectFilters(false)

  def apply(path: Path, source: String): String =
    formatter.format(Config, path.toAbsolutePath.normalize, RuntimeCollections(source))
}
