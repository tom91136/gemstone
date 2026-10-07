package net.kurobako.gemstone.codegen

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

/** One registry produces names for all language boundaries. Parsing behaviour remains authored. */
object GenerateEnvironment {
  case class Entry(name: String, default: Option[Long]) {
    def symbol: String = name.stripPrefix("GEMSTONE_").split('_').map { word =>
      word.head.toString + word.tail.map(_.toLower)
    }.mkString
  }

  def parse(arguments: Seq[String]): Vector[Entry] = {
    require(arguments.nonEmpty, "environment table must not be empty")
    val entries = arguments.grouped(2).map {
      case Seq(name, default) =>
        require(name.matches("GEMSTONE_[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*"), s"invalid environment name: $name")
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

  lazy val table: Vector[Entry] = parse(Vector(
    "GEMSTONE_CUDA_CODE"                    -> "",
    "GEMSTONE_CUDA_11_8_89"                 -> "",
    "GEMSTONE_CUDA_11_8_89_IMAGE"           -> "",
    "GEMSTONE_CUDA_13_3_33"                 -> "",
    "GEMSTONE_CUDA_13_3_33_IMAGE"           -> "",
    "GEMSTONE_CUDA_13_3_73"                 -> "",
    "GEMSTONE_CUDA_13_3_73_IMAGE"           -> "",
    "GEMSTONE_OFFLINE"                      -> "",
    "GEMSTONE_EXECUTION_MAX_INSTRUCTIONS"   -> "",
    "GEMSTONE_EXECUTION_PARALLELISM"        -> "",
    "GEMSTONE_HSA_PAGING_TIMEOUT_SECONDS"   -> "",
    "GEMSTONE_HSA_REJECTED_CODE_OBJECT"     -> "",
    "GEMSTONE_NATIVE_DIRECTORY"             -> "",
    "GEMSTONE_NVDISASM_11_8_86"             -> "",
    "GEMSTONE_NVDISASM_13_2_78"             -> "",
    "GEMSTONE_NVDISASM_13_3_73"             -> "",
    "GEMSTONE_OPENCL_COMPILER"              -> "",
    "GEMSTONE_OPENCL_LLVM_LINK"             -> "",
    "GEMSTONE_OPENCL_LLVM_SPIRV"            -> "",
    "GEMSTONE_OPENCL_LOCAL_MEM_SIZE"        -> "65536",
    "GEMSTONE_OPENCL_MAX_MEM_ALLOC_SIZE"    -> "268435456",
    "GEMSTONE_OPENCL_MAX_WORK_GROUP_SIZE"   -> "64",
    "GEMSTONE_OPENCL_SCAN_BUFFER_POINTERS"  -> "",
    "GEMSTONE_PROFILE"                      -> "",
    "GEMSTONE_RECORD"                       -> "",
    "GEMSTONE_RPC_CLIENT"                   -> "",
    "GEMSTONE_RPC_ENDPOINT"                 -> "",
    "GEMSTONE_RPC_TIMEOUT_SECONDS"          -> "300",
    "GEMSTONE_SYSTEM_PAGING"                -> "",
    "GEMSTONE_TARGET_ARCH"                  -> "",
    "GEMSTONE_TRACE"                        -> "",
    "GEMSTONE_VERIFY_FIXTURES"              -> "",
    "GEMSTONE_VULKAN_API_VERSION"           -> "",
    "GEMSTONE_VULKAN_VALIDATE_ALLOCATORS"   -> "",
    "GEMSTONE_VULKAN_DUMP"                  -> "",
    "GEMSTONE_MEMORY_ALLOCATION_KIND"       -> "",
    "GEMSTONE_MEMORY_ATOMIC_BITS"           -> "",
    "GEMSTONE_MEMORY_CONCURRENCY_DIRECTORY" -> "",
    "GEMSTONE_MEMORY_PROBE"                 -> "",
    "GEMSTONE_TEST_LIBRARY"                 -> ""
  ).flatMap((name, default) => Vector(name, default)))

  private def outputs(root: Path, entries: Vector[Entry]): Vector[(Path, String)] = {
    val source    = "by GenerateEnvironment.table"
    val scalaPath = root.resolve("core/src/generated/main/scala/net/kurobako/gemstone/environment.scala")
    val scala     =
      GeneratedHeader.scala(source) + "package net.kurobako.gemstone\n\nimport net.kurobako.gemstone.constraint.*\n\n" +
        "object environment {\n" +
        entries.map { entry =>
          s"  val ${entry.symbol}: String = \"${entry.name}\"\n" +
            entry.default.map(value =>
              s"  val ${entry.symbol}Default: Long ${if (value > 0) ">" else ">="} 0 = ${value}L\n"
            ).getOrElse("")
        }.mkString + "}\n"
    val cpp = GeneratedHeader.cpp(source) +
      "#pragma once\n\n#include <cstdint>\n\nnamespace gemstone::environment {\n" +
      entries.map { entry =>
        s"inline constexpr char ${entry.symbol}[] = \"${entry.name}\";\n" +
          entry.default.map(value => s"inline constexpr uint64_t ${entry.symbol}Default = $value;\n").getOrElse("")
      }.mkString + "} // namespace gemstone::environment\n"
    Vector(
      scalaPath                                             -> GeneratedScalaFormatter(scalaPath, scala),
      root.resolve("native/generated/environment_keys.hpp") -> cpp
    )
  }

  def generate(root: Path, entries: Vector[Entry], check: Boolean): Unit = {
    val files = outputs(root, entries)
    if (check) files.foreach { (path, expected) =>
      require(
        Files.isRegularFile(path) && Files.readString(path) == expected,
        s"stale environment output: $path; run codegen"
      )
    }
    else files.foreach(GeneratedFiles.write(_, _))
  }

  /** Reject stray owned names at actual environment accesses, without mistaking CMake/compiler definitions for them. */
  def checkNames(root: Path, entries: Vector[Entry]): Unit = {
    val known    = entries.map(_.name).toSet
    val quoted   = "\"(GEMSTONE_[A-Z0-9_]+)\"".r
    val access   = "(?:\\$ENV\\{|\\$(?!\\{))(GEMSTONE_[A-Z0-9_]+)".r
    val paths    = Vector.newBuilder[Path]
    val excluded = Set("target", ".git", ".sbt", "generated", "intellij_report2")
    class Visitor extends SimpleFileVisitor[Path] {
      override def preVisitDirectory(path: Path, attributes: BasicFileAttributes): FileVisitResult =
        if (path == root.resolve("conformance") || (path != root && excluded(path.getFileName.toString)))
          FileVisitResult.SKIP_SUBTREE
        else FileVisitResult.CONTINUE
      override def visitFile(path: Path, attributes: BasicFileAttributes): FileVisitResult = {
        if (attributes.isRegularFile) paths += path
        FileVisitResult.CONTINUE
      }
    }
    val _       = Files.walkFileTree(root, Visitor())
    val tomlKey = "\\b(GEMSTONE_[A-Z0-9_]+)\\s*=".r
    val invalid = paths.result().filter { path =>
      Set("scala", "sbt", "cpp", "hpp", "h", "cmake", "toml", "txt")(path.getFileName.toString.split('.').last)
    }.flatMap { path =>
      Files.readString(path).linesIterator.zipWithIndex.flatMap { (line, index) =>
        (quoted.findAllMatchIn(line) ++ access.findAllMatchIn(line) ++
          (if (path.toString.endsWith(".toml")) tomlKey.findAllMatchIn(line) else Iterator.empty)).map(_.group(
          1
        )).filterNot(known).map { name =>
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
    generate(root, table, check)
    if (check) checkNames(root, table)
  }
}
