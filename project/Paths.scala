import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import sbt.nio.file.FileTreeView
import sbt.nio.file.Glob
import scala.Conversion
import scala.language.implicitConversions

/** Small NIO vocabulary shared by the meta-build. */
object Paths {
  given pathToFile: Conversion[Path, File] = _.toFile

  extension (context: StringContext) {
    def path(arguments: Any*): Path = Path.of(context.s(arguments*))
  }

  extension (path: Path) {
    def /(child: String): Path = path.resolve(child)
    def /(child: Path): Path   = path.resolve(child)
    def fileName: String       =
      Option(path.getFileName).map(name => s"$name").filter(_.nonEmpty)
        .getOrElse(throw IllegalArgumentException(s"path has no nonempty file name: $path"))
    def parent: Option[Path]                 = Option(path.getParent)
    def absolute: Path                       = path.toAbsolutePath.normalize
    def createDirectories(): Path            = Files.createDirectories(path)
    def exists: Boolean                      = Files.exists(path)
    def isDirectory: Boolean                 = Files.isDirectory(path)
    def isFile: Boolean                      = Files.isRegularFile(path)
    def glob: Glob                           = Glob(path)
    def descendants(glob: String): Seq[Path] =
      FileTreeView.default.list(Glob(path = path, relative = s"**/$glob")).map(_._1)
  }
}
