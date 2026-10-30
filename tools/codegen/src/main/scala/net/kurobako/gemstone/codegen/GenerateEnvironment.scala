package net.kurobako.gemstone.codegen

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import scala.util.matching.Regex

/** One registry per project produces names for all language boundaries. Parsing behaviour remains authored. */
object GenerateEnvironment {
  private given CanEqual[Path, Path] = CanEqual.derived

  case class Entry(name: String, default: Option[Long]) {
    def symbol: String = name.split('_').drop(1).map { word =>
      word.head.toString + word.tail.map(_.toLower)
    }.mkString
  }

  def parse(prefix: String, arguments: Seq[String]): Vector[Entry] = {
    require(arguments.nonEmpty, "environment table must not be empty")
    val pattern = s"${prefix}_[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*".r
    val entries = arguments.grouped(2).map {
      case Seq(name, default) =>
        require(pattern.matches(name), s"invalid environment name: $name")
        val numeric = Option.when(default.nonEmpty) {
          require(default.forall(c => c >= '0' && c <= '9'), s"invalid numeric default: $default")
          default.toLongOption.getOrElse(throw IllegalArgumentException(s"numeric default overflows Long: $default"))
        }
        Entry(name = name, default = numeric)
      case fields =>
        throw IllegalArgumentException(s"expected name and optional numeric default, got ${fields.size} fields")
    }.toVector
    require(entries.map(_.name).distinct.size == entries.size, "duplicate environment name")
    require(entries.map(_.symbol).distinct.size == entries.size, "duplicate generated symbol")
    entries.sortBy(_.name)
  }

  lazy val gemstone: Vector[Entry] = parse(
    "GEMSTONE",
    Vector(
      "GEMSTONE_CONTAINER_EXE"                   -> "",
      "GEMSTONE_CUDA_CODE_MODE"                  -> "",
      "GEMSTONE_EXECUTION_MAX_INSTRUCTION_COUNT" -> "",
      "GEMSTONE_EXECUTION_THREAD_COUNT"          -> "",
      "GEMSTONE_FIXTURE_CUDA_11_8_89_DIR"        -> "",
      "GEMSTONE_FIXTURE_CUDA_11_8_89_IMAGE"      -> "",
      "GEMSTONE_FIXTURE_CUDA_13_3_33_DIR"        -> "",
      "GEMSTONE_FIXTURE_CUDA_13_3_33_IMAGE"      -> "",
      "GEMSTONE_FIXTURE_CUDA_13_3_73_DIR"        -> "",
      "GEMSTONE_FIXTURE_CUDA_13_3_73_IMAGE"      -> "",
      "GEMSTONE_FIXTURE_NVDISASM_11_8_86_EXE"    -> "",
      "GEMSTONE_FIXTURE_NVDISASM_13_2_78_EXE"    -> "",
      "GEMSTONE_FIXTURE_NVDISASM_13_3_73_EXE"    -> "",
      "GEMSTONE_FIXTURE_VERIFY_ON"               -> "",
      "GEMSTONE_HSA_PAGING_TIMEOUT_SECONDS"      -> "",
      "GEMSTONE_HSA_REJECTED_CODE_OBJECT_FILE"   -> "",
      "GEMSTONE_NATIVE_DIR"                      -> "",
      "GEMSTONE_OFFLINE_ON"                      -> "",
      "GEMSTONE_OPENCL_COMPILER_EXE"             -> "",
      "GEMSTONE_OPENCL_LLVM_LINK_EXE"            -> "",
      "GEMSTONE_OPENCL_LLVM_SPIRV_EXE"           -> "",
      "GEMSTONE_OPENCL_SCAN_BUFFER_POINTERS_ON"  -> "",
      "GEMSTONE_PROFILE_NAME"                    -> "",
      "GEMSTONE_RECORD_FILE"                     -> "",
      "GEMSTONE_RPC_CLIENT_ID"                   -> "",
      "GEMSTONE_RPC_ENDPOINT"                    -> "",
      "GEMSTONE_RPC_TIMEOUT_SECONDS"             -> "300",
      "GEMSTONE_SYSTEM_PAGING_ON"                -> "",
      "GEMSTONE_TARGET_ARCH"                     -> "",
      "GEMSTONE_TEST_CUDA_LIBRARY_FILE"          -> "",
      "GEMSTONE_TEST_MEMORY_ALLOCATION_MODE"     -> "",
      "GEMSTONE_TEST_MEMORY_ATOMIC_BITS"         -> "",
      "GEMSTONE_TEST_MEMORY_CONCURRENCY_DIR"     -> "",
      "GEMSTONE_TEST_MEMORY_PROBE_LIST"          -> "",
      "GEMSTONE_THREAD_COUNT"                    -> "",
      "GEMSTONE_TIMEOUT_SCALE"                   -> "1",
      "GEMSTONE_TRACE_LIST"                      -> "",
      "GEMSTONE_VULKAN_API_VERSION"              -> "",
      "GEMSTONE_VULKAN_DUMP_DIR"                 -> "",
      "GEMSTONE_VULKAN_VALIDATE_ALLOCATORS_ON"   -> ""
    ).flatMap((name, default) => Vector(name, default))
  )

  val ConformanceKeys: String = "conformance/include/conformance/environment_keys.hpp"

  private val ConformanceKey = "inline constexpr char ([A-Za-z0-9]+)\\[\\] = \"([A-Z0-9_]+)\";".r

  /** Conformance owns its names; Gemstone only reads them. */
  def conformance(root: Path): Vector[Entry] = {
    val declared = ConformanceKey.findAllMatchIn(Files.readString(root.resolve(ConformanceKeys)))
      .map(found => found.group(1) -> found.group(2)).toVector
    val entries = parse("CONFORMANCE", declared.flatMap((_, name) => Vector(name, "")))
    declared.foreach((symbol, name) =>
      require(
        Entry(name, None).symbol == symbol,
        s"$ConformanceKeys: $name must be declared as ${Entry(name, None).symbol}"
      )
    )
    entries
  }

  /** `Plain` defaults are bare `Long`s, for projects that cannot depend on core's constraint types. */
  private enum Defaults(val imports: String) derives CanEqual {
    case Refined extends Defaults("import net.kurobako.gemstone.constraint.*\n\n")
    case Plain   extends Defaults("")

    def bound(value: Long): String = this match {
      case Refined => if (value > 0) " > 0" else " >= 0"
      case Plain   => ""
    }
  }

  private def scalaObject(
      path: Path,
      pkg: String,
      declaration: String,
      entries: Vector[Entry],
      defaults: Defaults
  ): (Path, String) =
    path -> GeneratedScalaFormatter(
      path,
      GeneratedHeader.scala("by GenerateEnvironment") + s"package $pkg\n\n" +
        (if (entries.exists(_.default.nonEmpty)) defaults.imports else "") +
        s"$declaration {\n" +
        entries.map { entry =>
          s"  val ${entry.symbol}: String = \"${entry.name}\"\n" +
            entry.default.fold("")(value =>
              s"  val ${entry.symbol}Default: Long${defaults.bound(value)} = ${value}L\n"
            )
        }.mkString + "}\n"
    )

  private def cppHeader(path: Path, namespace: String, entries: Vector[Entry]): (Path, String) =
    path ->
      (GeneratedHeader.cpp("by GenerateEnvironment") +
        s"#pragma once\n\n#include <cstdint>\n\nnamespace $namespace {\n" +
        entries.map { entry =>
          s"inline constexpr char ${entry.symbol}[] = \"${entry.name}\";\n" +
            entry.default.fold("")(value => s"inline constexpr uint64_t ${entry.symbol}Default = $value;\n")
        }.mkString + s"} // namespace $namespace\n")

  private def outputs(root: Path, gemstone: Vector[Entry], conformance: Vector[Entry]): Vector[(Path, String)] =
    Vector(
      scalaObject(
        root.resolve("core/src/generated/main/scala/net/kurobako/gemstone/environment.scala"),
        "net.kurobako.gemstone",
        "object environment",
        gemstone,
        Defaults.Refined
      ),
      scalaObject(
        root.resolve("test-support/src/generated/main/scala/net/kurobako/gemstone/testing/environmentKeys.scala"),
        "net.kurobako.gemstone.testing",
        "private[testing] object environmentKeys",
        gemstone,
        Defaults.Plain
      ),
      cppHeader(root.resolve("native/generated/environment_keys.hpp"), "gemstone::environment", gemstone),
      scalaObject(
        root.resolve(
          "tools/conformance/src/generated/main/scala/net/kurobako/gemstone/tools/conformance/conformanceEnvironment.scala"
        ),
        "net.kurobako.gemstone.tools.conformance",
        "object conformanceEnvironment",
        conformance,
        Defaults.Refined
      )
    )

  def generate(root: Path, gemstone: Vector[Entry], conformance: Vector[Entry], check: Boolean): Unit = {
    val files = outputs(root, gemstone, conformance)
    if (check) files.foreach { (path, expected) =>
      require(
        Files.isRegularFile(path) && Files.readString(path) == expected,
        s"stale environment output: $path; run codegen"
      )
    }
    else files.foreach(GeneratedFiles.write(_, _))
  }

  private val ExcludedDirectories = Set("target", ".git", ".sbt", "generated", "intellij_report2")
  private val SourceExtensions    = Set("scala", "sbt", "cpp", "hpp", "h", "cmake", "toml", "txt")

  private case class Audit(names: String, known: Set[String]) {
    val access: Regex  = s"(?:\\$$ENV\\{|\\$$(?!\\{))((?:$names)_[A-Z0-9_]+)".r
    val quoted: Regex  = s"\"((?:$names)_[A-Z0-9_]+)\"".r
    val tomlKey: Regex = s"\\b((?:$names)_[A-Z0-9_]+)\\s*=".r
  }

  /** Reject stray owned names at actual environment accesses, without mistaking CMake/compiler definitions for them.
    * Conformance stands alone, so its sources may only use its own names.
    */
  def checkNames(root: Path, gemstone: Vector[Entry], conformance: Vector[Entry]): Unit = {
    val standalone = root.resolve("conformance")
    val owned      = Audit("CONFORMANCE", conformance.map(_.name).toSet)
    val all        = Audit("GEMSTONE|CONFORMANCE", (gemstone ++ conformance).map(_.name).toSet)
    val paths      = Vector.newBuilder[Path]
    class Visitor extends SimpleFileVisitor[Path] {
      override def preVisitDirectory(path: Path, attributes: BasicFileAttributes): FileVisitResult =
        if (path != root && ExcludedDirectories(path.getFileName.toString)) FileVisitResult.SKIP_SUBTREE
        else FileVisitResult.CONTINUE
      override def visitFile(path: Path, attributes: BasicFileAttributes): FileVisitResult = {
        if (attributes.isRegularFile) paths += path
        FileVisitResult.CONTINUE
      }
    }
    val _       = Files.walkFileTree(root, Visitor())
    val invalid = paths.result().filter { path =>
      SourceExtensions(path.getFileName.toString.split('.').last)
    }.flatMap { path =>
      val audit = if (path.startsWith(standalone)) owned else all
      Files.readString(path).linesIterator.zipWithIndex.flatMap { (line, index) =>
        (audit.quoted.findAllMatchIn(line) ++ audit.access.findAllMatchIn(line) ++
          (if (path.toString.endsWith(".toml")) audit.tomlKey.findAllMatchIn(line) else Iterator.empty))
          .map(_.group(1)).filterNot(audit.known).map { name =>
            s"${root.relativize(path)}:${index + 1}: undeclared environment variable $name"
          }
      }
    }
    require(invalid.isEmpty, invalid.distinct.mkString("\n"))
  }

  private val command = Command("GenerateEnvironment", "Generate environment constants from the table")((
    Opts.argument[String]("repository").map(Path.of(_).toAbsolutePath.normalize),
    Opts.flag("write", "Write the generated outputs").as(false)
      .orElse(Opts.flag("check", "Check output freshness and declared names").as(true))
  ).tupled)

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) { (root, check) =>
    val owned = conformance(root)
    generate(root, gemstone, owned, check)
    if (check) checkNames(root, gemstone, owned)
  }
}
