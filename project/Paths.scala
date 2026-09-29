import StringCase.*
import java.io.File
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import sbt.io.IO
import sbt.nio.file.FileTreeView
import sbt.nio.file.Glob
import scala.Conversion
import scala.language.implicitConversions

/** Small NIO vocabulary shared by the meta-build. */
object Paths {
  given pathToFile: Conversion[Path, File] = _.toFile
  given fileToPath: Conversion[File, Path] = _.toPath

  extension (context: StringContext) {
    def path(arguments: Any*): Path = Path.of(context.s(arguments*))
  }

  def containsPathSegments(path: Path, first: String, rest: String*): Boolean = {
    val segments = first +: rest
    (0 to path.getNameCount - segments.size).exists { start =>
      segments.iterator.zipWithIndex.forall { case (segment, offset) =>
        s"${path.getName(start + offset)}" == segment
      }
    }
  }

  extension (path: Path) {
    def /(child: String): Path = path.resolve(child)
    def /(child: Path): Path   = path.resolve(child)
    def fileName: String       =
      Option(path.getFileName).map(name => s"$name").filter(_.nonEmpty)
        .getOrElse(throw IllegalArgumentException(s"path has no nonempty file name: $path"))

    /** Final suffix, without the dot; dotfiles and trailing dots have no extension. */
    def ext: Option[String] = Option(path.getFileName).flatMap { file =>
      val name = file.toString
      val dot  = name.lastIndexOf('.')
      Option.when(dot > 0 && dot < name.length - 1)(name.substring(dot + 1).toLowerCaseRoot)
    }

    /** Lexical containment only; this does not resolve filesystem symlinks. */
    def resolveWithin(relative: Path): Path = {
      val root   = path.absolute
      val target = (root / relative).normalize
      require(!relative.isAbsolute && target.startsWith(root), s"path escapes destination $path: $relative")
      target
    }
    def withTempDirectory[A](prefix: String)(use: Path => A): A = {
      val directory = Files.createTempDirectory(path.createDirectories(), prefix)
      try use(directory)
      finally IO.delete(directory.toFile)
    }
    def parent: Option[Path]      = Option(path.getParent)
    def absolute: Path            = path.toAbsolutePath.normalize
    def createDirectories(): Path = Files.createDirectories(path)
    def exists: Boolean           = Files.exists(path)
    def isDirectory: Boolean      = Files.isDirectory(path)
    def isFile: Boolean           = Files.isRegularFile(path)

    /** Regular files beneath this path, without following symlinks. The root itself is never pruned. */
    def walk(skipDirectory: Path => Boolean = _ => false): Vector[Path] = {
      val root  = path.absolute
      val files = Vector.newBuilder[Path]
      if (root.exists)
        Files.walkFileTree(
          root,
          new SimpleFileVisitor[Path] {
            override def preVisitDirectory(directory: Path, attributes: BasicFileAttributes): FileVisitResult =
              if (directory != root && skipDirectory(directory)) FileVisitResult.SKIP_SUBTREE
              else FileVisitResult.CONTINUE
            override def visitFile(file: Path, attributes: BasicFileAttributes): FileVisitResult = {
              if (attributes.isRegularFile) files += file
              FileVisitResult.CONTINUE
            }
          }
        )
      files.result()
    }
    def descendants(glob: String): Seq[Path] =
      FileTreeView.default.list(Glob(path = path, relative = s"**/$glob")).map(_._1)
  }
}
