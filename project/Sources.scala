import Paths.*
import StringCase.*
import java.nio.file.Path

/** Pruning file discovery shared by the codestyle checks. */
object Sources {
  private val ignoredSegments =
    Set(
      ".bloop",
      ".bsp",
      ".cache",
      ".git",
      ".metals",
      ".scala-build",
      "build",
      "licenses",
      "target",
      "third_party",
      "vendor"
    )

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
    roots.toVector.flatMap(_.walk(path => ignoredSegments(path.fileName)))
      .filter(path => path.ext.exists(nativeExtensions) && !exclude(path))
      .distinct.sortBy(_.toString)

  def authored(root: Path, exclude: Path => Boolean = _ => false): Vector[Path] =
    root.walk(path => ignoredSegments(path.fileName)).filter { path =>
      val name      = path.fileName
      val upper     = name.toUpperCaseRoot
      val generated = containsPathSegments(root.absolute.relativize(path), "generated")
      !generated && !exclude(path) && !upper.startsWith("LICENSE") && !upper.startsWith("LICENCE") &&
      (textFileNames(name) || path.ext.exists(textExtensions))
    }.sortBy(_.toString)
}
