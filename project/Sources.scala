import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.Locale

import Paths.*

/** Pruning file discovery shared by the codestyle checks. */
object Sources {
  private val ignoredSegments = Set(".bloop", ".git", ".metals", "licenses", "target", "third_party", "vendor")
  private val ignoredNames    = Set("PLAN.md", "CLEAN_UP_PLAN.md")

  private val nativeExtensions =
    Set("c", "cc", "cpp", "cxx", "cu", "h", "hh", "hip", "hpp", "hxx", "inc", "inl")

  private val textExtensions = Set(
    "c",
    "cc",
    "cmake",
    "conf",
    "cpp",
    "cu",
    "cxx",
    "h",
    "hh",
    "hip",
    "hpp",
    "hxx",
    "inc",
    "inl",
    "java",
    "json",
    "kt",
    "md",
    "py",
    "sbt",
    "scala",
    "sh",
    "toml",
    "txt",
    "yaml",
    "yml"
  )

  private val textFileNames = Set(".clang-format", ".scalafix.conf", ".scalafmt.conf", "CMakeLists.txt")

  def native(roots: Seq[Path], exclude: Path => Boolean = _ => false): Vector[Path] =
    walk(roots, path => extension(path).exists(nativeExtensions) && !exclude(path))

  def authored(root: Path, exclude: Path => Boolean = _ => false): Vector[Path] =
    walk(
      Seq(root),
      path => {
        val name  = path.fileName
        val upper = name.toUpperCase(Locale.ROOT)
        !exclude(path) && !ignoredNames(name) && !upper.startsWith("LICENSE") && !upper.startsWith("LICENCE") &&
        (textFileNames(name) || extension(path).exists(textExtensions))
      }
    )

  private def walk(roots: Seq[Path], accept: Path => Boolean): Vector[Path] = {
    val files = Vector.newBuilder[Path]
    roots.foreach { root =>
      if (root.isDirectory)
        Files.walkFileTree(
          root,
          new SimpleFileVisitor[Path] {
            override def preVisitDirectory(directory: Path, attributes: BasicFileAttributes): FileVisitResult = {
              val name = Option(directory.getFileName).map(_.toString).getOrElse("")
              if (directory != root && ignoredSegments(name)) FileVisitResult.SKIP_SUBTREE
              else FileVisitResult.CONTINUE
            }
            override def visitFile(file: Path, attributes: BasicFileAttributes): FileVisitResult = {
              if (attributes.isRegularFile && accept(file.absolute)) files += file.absolute
              FileVisitResult.CONTINUE
            }
          }
        )
    }
    files.result().sortBy(path => s"$path")
  }

  private def extension(path: Path): Option[String] = {
    val name = path.fileName
    val dot  = name.lastIndexOf('.')
    Option.when(dot >= 0)(name.substring(dot + 1).toLowerCase(Locale.ROOT))
  }
}
