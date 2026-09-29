package net.kurobako.gemstone.codegen

import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

/** One registry produces names for all language boundaries. Parsing behavior remains authored. */
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

  private def outputs(root: Path, entries: Vector[Entry]): Vector[(Path, String)] = {
    val banner    = "Generated from build.sbt: environmentVariables. Regenerate with: sbt codegen"
    val scalaPath = root.resolve("core/src/generated/main/scala/net/kurobako/gemstone/environment.scala")
    val scala     = s"// $banner\npackage net.kurobako.gemstone\n\nobject environment {\n" + entries.map { entry =>
      s"  val ${entry.symbol}: String = \"${entry.name}\"\n" +
        entry.default.map(value => s"  val ${entry.symbol}Default: Long = ${value}L\n").getOrElse("")
    }.mkString + "}\n"
    val cpp = s"// $banner\n#pragma once\n\n#include <cstdint>\n\nnamespace gemstone::environment {\n" +
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
    else files.foreach { (path, content) =>
      val _ = GeneratedFiles.write(path, content)
    }
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

  def main(arguments: Array[String]): Unit = {
    require(
      arguments.length >= 2 && Set("--write", "--check")(arguments(1)),
      "usage: GenerateEnvironment repository --write|--check [name numeric-default-or-empty]..."
    )
    val root    = Path.of(arguments(0)).toAbsolutePath.normalize()
    val entries = parse(arguments.drop(2).toVector)
    val check   = arguments(1) == "--check"
    generate(root, entries, check)
    if (check) checkNames(root, entries)
  }
}
