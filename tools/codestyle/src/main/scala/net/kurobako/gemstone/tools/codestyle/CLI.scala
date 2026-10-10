package net.kurobako.gemstone.tools.codestyle

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import net.kurobako.gemstone.string.*
import scala.jdk.CollectionConverters.*
import scala.sys.process.Process
import scala.sys.process.ProcessLogger
import scala.util.Try
import scala.util.matching.Regex

object CLI {
  case class Violation(path: Path, line: Int, message: String)

  type Rule = (Path, Vector[String]) => Vector[Violation]

  private val ExcludedSegments = Set("generated", "licences", "third_party", "vendor")
  private val NativeExtensions = Set("c", "cc", "cpp", "cxx", "cu", "h", "hh", "hip", "hpp", "hxx", "inc", "inl")
  private val TextExtensions   = NativeExtensions ++
    Set("cmake", "conf", "java", "json", "kt", "md", "py", "sbt", "scala", "sh", "toml", "txt", "yaml", "yml")
  private val TextFileNames = Set(".clang-format", ".scalafix.conf", ".scalafmt.conf", "CMakeLists.txt")
  private val TestCaseMacro = raw"^[A-Z_]*TEST_CASE[A-Z_]*\s*\(".r
  // git's own test: binary files have a NUL byte early on
  private val BinaryProbeBytes = 8000
  private val IncludeDirective = raw"^\s*#\s*include\b".r

  private val NativeRules: Vector[Rule] = Vector(
    forbidden(
      raw"\bstd::(?:size_t|ptrdiff_t|max_align_t|u?int(?:8|16|32|64)_t|u?int(?:ptr|max)_t|u?int_(?:least|fast)(?:8|16|32|64)_t)\b".r,
      value => s"forbidden numeric type '$value'"
    ),
    forbidden(
      raw"\bU?INT(?:8|16|32|64|MAX|PTR)_C\b".r,
      value => s"forbidden integer constant macro '$value'; use static_cast"
    ),
    (path, lines) =>
      lines.zipWithIndex.collect {
        case (line, index)
            if index > 0 && TestCaseMacro.findPrefixOf(line).nonEmpty && lines(index - 1).trim.nonEmpty =>
          Violation(path, index + 1, "Catch test cases must be preceded by a blank line")
      },
    (path, lines) =>
      lines.zipWithIndex.collect {
        case (line, index) if IncludeDirective.findPrefixOf(line).nonEmpty && needsSeparator(lines.drop(index + 1)) =>
          Violation(path, index + 2, "include blocks must be followed by a blank line")
      }
  )

  def main(arguments: Array[String]): Unit = arguments.toVector match {
    case "format" +: roots =>
      require(clangFormat(nativeSources(roots.map(Path.of(_))), Vector("-i")), "clang-format failed")
    case "check" +: "--compile-database" +: database +: roots =>
      val sources = check(roots)
      require(
        clangTidy(sources, Path.of(database).toAbsolutePath.normalize),
        "identifiers violate the naming convention in .clang-tidy"
      )
    case "check" +: roots               => val _ = check(roots)
    case "terminology" +: root +: terms =>
      enforce(
        authoredSources(Path.of(root).toAbsolutePath.normalize),
        terms.map(term => forbidden(variants(term), _ => "prohibited vague terminology"))
      )
    case Vector("binaries", root) =>
      val base     = Path.of(root).toAbsolutePath.normalize
      val binaries = repositoryFiles(base).filter(binary)
      binaries.foreach(path =>
        Console.err.println(
          s"${base.relativize(path)}: binary file; embed it as text with the commands that produce it"
        )
      )
      require(binaries.isEmpty, s"found ${binaries.size} binary files")
    case _ => throw IllegalArgumentException(
        "usage: format <root>... | check [--compile-database <build>] <root>... | terminology <root> <term>... | " +
          "binaries <root>"
      )
  }

  private def check(roots: Vector[String]): Vector[Path] = {
    val sources = nativeSources(roots.map(Path.of(_)))
    require(clangFormat(sources, Vector("--dry-run", "--Werror")), "sources are not clang-formatted; run format")
    enforce(sources, NativeRules)
    sources
  }

  private def clangTidy(sources: Vector[Path], database: Path): Boolean = {
    val units = ujson.read(Files.readString(database.resolve("compile_commands.json"))).arr
      .map(entry => Path.of(entry("file").str).normalize).toSet
    val (checked, rest) = sources.partition(units)
    val skipped         = rest.count(path => extension(path).exists(Set("c", "cc", "cpp", "cu", "cxx", "hip")))
    Console.out.println(s"clang-tidy: ${checked.size} translation units; $skipped outside the compile database skipped")
    val pool = Executors.newFixedThreadPool(sys.runtime.availableProcessors())
    try {
      val runs = pool.invokeAll(checked.map { file =>
        (() => {
          val output  = Vector.newBuilder[String]
          val command =
            Vector("clang-tidy", "--quiet", s"-p=$database", "--extra-arg=-w", s"$file")
          val status = Process(command).!(ProcessLogger(output += _, output += _))
          (status, output.result())
        }): Callable[(Int, Vector[String])]
      }.asJava).asScala.toVector.map(_.get)
      runs.foreach((status, output) => if (status != 0) output.foreach(Console.err.println))
      runs.forall(_._1 == 0)
    } finally pool.shutdown()
  }

  private def forbidden(pattern: Regex, describe: String => String): Rule =
    (path, lines) =>
      lines.zipWithIndex.flatMap((line, index) =>
        pattern.findFirstIn(line).map(value => Violation(path, index + 1, describe(value)))
      )

  private def variants(stem: String): Regex =
    s"(?:\\b${stem}s?\\b|${stem.capitalize}s?(?=\\z|[^a-z])|${stem.toUpperCaseRoot}s?(?=\\z|[^A-Z]))".r

  private def needsSeparator(following: Vector[String]): Boolean =
    following.takeWhile(_.trim.nonEmpty).collectFirst {
      case line if IncludeDirective.findPrefixOf(line).nonEmpty || line.trim.matches("#\\s*(else|elif)\\b.*") => false
      case line if !line.trim.startsWith("#")                                                                 => true
    }.getOrElse(false)

  private def enforce(files: Vector[Path], rules: Vector[Rule]): Unit = {
    val root       = Path.of("").toAbsolutePath
    val violations = files.flatMap { file =>
      val display = if (file.startsWith(root)) root.relativize(file) else file
      Try(Files.readString(file, StandardCharsets.UTF_8).linesIterator.toVector).toOption.toVector
        .flatMap(lines => rules.flatMap(rule => rule(display, lines)))
    }
    violations.foreach(violation => Console.err.println(s"${violation.path}:${violation.line}: ${violation.message}"))
    require(violations.isEmpty, s"found ${violations.size} codestyle violations")
  }

  private def clangFormat(files: Vector[Path], options: Vector[String]): Boolean =
    files.isEmpty || Process(Vector("clang-format") ++ options ++ files.map(_.toString)).! == 0

  private def extension(path: Path): Option[String] = {
    val name = path.getFileName.toString
    val dot  = name.lastIndexOf('.')
    Option.when(dot > 0 && dot < name.length - 1)(name.substring(dot + 1).toLowerCaseRoot)
  }

  private def nativeSources(roots: Vector[Path]): Vector[Path] =
    roots.map(_.toAbsolutePath.normalize).flatMap(root =>
      files(root).filter(path => extension(path).exists(NativeExtensions))
    ).distinct.sortBy(_.toString)

  private def authoredSources(root: Path): Vector[Path] =
    files(root).filter { path =>
      val name  = path.getFileName.toString
      val upper = name.toUpperCaseRoot
      name != "build.sbt" && !upper.startsWith("LICENSE") && !upper.startsWith("LICENCE") &&
      (TextFileNames(name) || extension(path).exists(TextExtensions))
    }.sortBy(_.toString)

  private def binary(path: Path): Boolean = {
    val stream = Files.newInputStream(path)
    try stream.readNBytes(BinaryProbeBytes).contains(0.toByte)
    finally stream.close()
  }

  private def files(root: Path): Vector[Path] =
    listed(root).filterNot(_.split('/').exists(ExcludedSegments)).map(root.resolve(_)).filter(Files.isRegularFile(_))

  private def repositoryFiles(root: Path): Vector[Path] =
    listed(root).map(root.resolve(_)).filter(Files.isRegularFile(_))

  private def listed(root: Path): Vector[String] =
    Process(Vector("git", "-C", s"$root", "ls-files", "--cached", "--others", "--exclude-standard")).!!
      .linesIterator.toVector
}
