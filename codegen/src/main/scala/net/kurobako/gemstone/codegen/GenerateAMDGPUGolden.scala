package net.kurobako.gemstone.codegen

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Arrays
import java.util.zip.GZIPOutputStream
import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import scala.util.matching.Regex

/** Extracts exhaustive, exact-target positive and diagnostic corpora from LLVM MC checks. */
object GenerateAMDGPUGolden {
  private final case class Golden(target: String, assembly: String, bytes: Vector[Int], source: String)
  private final case class Diagnostic(target: String, assembly: String, message: String, source: String)

  private val Run: Regex           = raw"\s*//\s*RUN:\s*(.*)".r
  private val EncodingCheck: Regex =
    raw"\s*//\s*([A-Za-z0-9_-]+):\s*(.*?)\s*;\s*encoding:\s*\[([^]]*)\].*".r
  private val DiagnosticCheck: Regex = raw"\s*//\s*([A-Za-z0-9_-]+):\s*(.*\berror:\s*.*)".r
  private val Mcpu: Regex            = raw"(?:^|\s)-mcpu(?:=|\s+)([A-Za-z0-9_]+)".r
  private val CheckPrefixes: Regex   =
    raw"--?check-prefix(?:es)?(?:=|\s+)([A-Za-z0-9_,-]+)".r
  private val LineReference: Regex  = raw"\[\[@LINE(?:([+-])([0-9]+))?\]\]".r
  private val CheckDirective: Regex = raw"-(?:NEXT|SAME|DAG|LABEL|EMPTY|NOT|COUNT-[0-9]+)$$".r

  def main(arguments: Array[String]): Unit = arguments.toVector match {
    case Vector(references, output, dataRoot) =>
      write(Paths.get(output), generate(Paths.get(references), Paths.get(dataRoot)))
    case _ =>
      throw new IllegalArgumentException(
        "usage: GenerateAMDGPUGolden <prepared-references> <output.scala> <data-root>"
      )
  }

  private[codegen] def generate(references: Path, dataRoot: Path): String = {
    val root = references.resolve("llvm/llvm/test/MC/AMDGPU")
    require(Files.isDirectory(root), s"missing LLVM AMDGPU tests: $root")
    val stream = Files.walk(root)
    val files  =
      try
        stream.iterator().asScala.filter(path =>
          Files.isRegularFile(path) && path.toString.endsWith(".s")
        ).toVector.sortBy(_.toString)
      finally stream.close()

    val positiveSeen     = mutable.HashSet.empty[(String, String, Vector[Int])]
    val diagnosticSeen   = mutable.HashSet.empty[(String, String, String)]
    val positives        = Vector.newBuilder[Golden]
    val diagnostics      = Vector.newBuilder[Diagnostic]
    var encodingChecks   = 0
    var diagnosticChecks = 0

    files.foreach { file =>
      val lines         = Files.readAllLines(file, StandardCharsets.UTF_8).asScala.toVector
      val prefixTargets = targetsByCheckPrefix(lines)
      val relative      = root.relativize(file).toString
      lines.zipWithIndex.foreach {
        case (EncodingCheck(rawPrefix, assembly, encoded), index) =>
          encodingChecks += 1
          val source = s"$relative:${index + 1}"
          parseBytes(encoded).foreach { bytes =>
            prefixTargets.getOrElse(normalizePrefix(rawPrefix), Set.empty).toVector.sorted.foreach { target =>
              val canonical = assembly.trim
              val key       = (target, canonical, bytes)
              if (canonical.nonEmpty && !containsFileCheckExpression(canonical) && positiveSeen.add(key))
                positives += Golden(target, canonical, bytes, source)
            }
          }
        case (DiagnosticCheck(rawPrefix, check), index) =>
          diagnosticChecks += 1
          referencedLine(check, index).flatMap(lines.lift).map(stripComment).filter(validDiagnosticSource).foreach {
            assembly =>
              val message = check.drop(check.indexOf("error:") + "error:".length).trim
              val source  = s"$relative:${index + 1}"
              prefixTargets.getOrElse(normalizePrefix(rawPrefix), Set.empty).toVector.sorted.foreach { target =>
                val key = (target, assembly, message)
                if (!containsFileCheckExpression(assembly) && diagnosticSeen.add(key))
                  diagnostics += Diagnostic(target, assembly, message, source)
              }
          }
        case _ => ()
      }
    }

    val positiveCases = positives.result()
      .sortBy(value => (value.target, value.source, value.assembly, value.bytes.mkString(",")))
    val diagnosticCases =
      diagnostics.result().sortBy(value => (value.target, value.source, value.assembly, value.message))
    require(
      positiveCases.size >= 10000,
      s"expected at least 10,000 exact-target encoding cases, found ${positiveCases.size}"
    )
    require(
      diagnosticCases.size >= 1000,
      s"expected at least 1,000 exact-target diagnostics, found ${diagnosticCases.size}"
    )
    writeGzip(dataRoot.resolve("positive.tsv.gz"), positiveCases.map(positiveRow))
    writeGzip(dataRoot.resolve("diagnostics.tsv.gz"), diagnosticCases.map(diagnosticRow))
    render(positiveCases.size, diagnosticCases.size, files.size, encodingChecks, diagnosticChecks)
  }

  private def targetsByCheckPrefix(lines: Vector[String]): Map[String, Set[String]] = {
    val pairs = lines.flatMap {
      case Run(command) if command.contains("llvm-mc") && !command.contains("-disassemble") =>
        Mcpu.findFirstMatchIn(command).toVector.flatMap { targetMatch =>
          val target = targetMatch.group(1).toLowerCase
          generationForTarget(target).toVector.flatMap { _ =>
            val prefixes = CheckPrefixes.findFirstMatchIn(command)
              .map(_.group(1).split(',').toVector)
              .getOrElse(Vector("CHECK"))
            prefixes.map(prefix => normalizePrefix(prefix) -> target)
          }
        }
      case _ => Vector.empty
    }
    pairs.groupMap(_._1)(_._2).view.mapValues(_.toSet).toMap
  }

  private def generationForTarget(target: String): Option[String] = target match {
    case "tahiti" | "verde" | "gfx600" | "gfx601"                         => Some("GFX6")
    case "bonaire" | "hawaii" | "kabini" | "kaveri" | "gfx700" | "gfx701" => Some("GFX7")
    case "carrizo" | "fiji" | "iceland" | "stoney" | "tonga" | "gfx800" | "gfx801" | "gfx802" | "gfx803" |
        "gfx810" => Some("GFX8")
    case "gfx908"                                    => Some("CDNA1")
    case "gfx90a"                                    => Some("CDNA2")
    case "gfx940" | "gfx941" | "gfx942"              => Some("CDNA3")
    case "gfx950"                                    => Some("CDNA4")
    case "gfx9" | "gfx900" | "gfx904" | "gfx906"     => Some("GFX9")
    case value if value.startsWith("gfx10")          => Some("GFX10")
    case value if value.startsWith("gfx11")          => Some("GFX11")
    case "gfx12" | "gfx1200" | "gfx1250" | "gfx1251" => Some("GFX12")
    case _                                           => None
  }

  private def normalizePrefix(prefix: String): String = CheckDirective.replaceFirstIn(prefix.toUpperCase, "")

  private def referencedLine(check: String, checkIndex: Int): Option[Int] =
    LineReference.findFirstMatchIn(check).map { reference =>
      val distance = Option(reference.group(2)).map(_.toInt).getOrElse(0)
      val offset   = Option(reference.group(1)) match {
        case Some("-") => -distance
        case Some("+") => distance
        case _         => 0
      }
      checkIndex + offset
    }

  private def stripComment(value: String): String = value.take(value.indexOf("//") match {
    case -1    => value.length
    case index => index
  }).trim

  private def validDiagnosticSource(value: String): Boolean =
    value.nonEmpty && !value.startsWith("//") && !value.startsWith("#")

  private def containsFileCheckExpression(value: String): Boolean = value.contains("{{") || value.contains("[[")

  private def parseBytes(value: String): Option[Vector[Int]] = {
    val fields = value.split(',').iterator.map(_.trim).filter(_.nonEmpty).toVector
    if (fields.forall(_.matches("0[xX][0-9a-fA-F]{1,2}")))
      Some(fields.map(field => Integer.parseInt(field.drop(2), 16)))
    else None
  }

  private def render(
      positiveCount: Int,
      diagnosticCount: Int,
      fileCount: Int,
      encodingCheckCount: Int,
      diagnosticCheckCount: Int
  ): String =
    s"""|package net.kurobako.gemstone.isa.amdgpu.common
        |
        |import java.io.BufferedReader
        |import java.io.InputStreamReader
        |import java.nio.charset.StandardCharsets
        |import java.nio.file.Files
        |import java.nio.file.Path
        |import java.nio.file.Paths
        |import java.util.zip.GZIPInputStream
        |import scala.collection.mutable.ArrayBuffer
        |
        |// Generated exhaustively from pinned LLVM MC/AMDGPU checks. Do not edit.
        |final case class AMDGPUGoldenCase(target: Target, assembly: String, bytes: Vector[Byte], source: String)
        |final case class AMDGPUDiagnosticCase(target: Target, assembly: String, message: String, source: String)
        |
        |object GoldenCorpus {
        |  val sourceFileCount: Int = $fileCount
        |  val sourceEncodingCheckCount: Int = $encodingCheckCount
        |  val sourceDiagnosticCheckCount: Int = $diagnosticCheckCount
        |  val caseCount: Int = $positiveCount
        |  val diagnosticCount: Int = $diagnosticCount
        |
        |  lazy val cases: Vector[AMDGPUGoldenCase] = load("positive.tsv.gz").map { row =>
        |    val fields = row.split("\\t", -1)
        |    AMDGPUGoldenCase(
        |      Target.unsafe(fields(0)), fields(1), fields(2).split(',').toVector.map(_.toInt.toByte), fields(3)
        |    )
        |  }
        |
        |  lazy val diagnostics: Vector[AMDGPUDiagnosticCase] = load("diagnostics.tsv.gz").map { row =>
        |    val fields = row.split("\\t", -1)
        |    AMDGPUDiagnosticCase(Target.unsafe(fields(0)), fields(1), fields(2), fields(3))
        |  }
        |
        |  private def load(name: String): Vector[String] = {
        |    val root = repositoryRoot(Paths.get(sys.props("user.dir")))
        |    val path = root.resolve(
        |      "isa_amdgpu/src/generated/test/resources/net/kurobako/gemstone/isa/amdgpu/common"
        |    ).resolve(name)
        |    val input  = new GZIPInputStream(Files.newInputStream(path))
        |    val reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))
        |    val result = ArrayBuffer.empty[String]
        |    try {
        |      var line = reader.readLine()
        |      while (line != null) {
        |        result += line
        |        line = reader.readLine()
        |      }
        |    } finally reader.close()
        |    result.toVector
        |  }
        |
        |  private def repositoryRoot(start: Path): Path = {
        |    var cursor = start.toAbsolutePath.normalize()
        |    while (cursor != null && !Files.isRegularFile(cursor.resolve("build.sbt")))
        |      cursor = cursor.getParent
        |    require(cursor != null, "cannot locate Gemstone repository from " + start)
        |    cursor
        |  }
        |}
        |""".stripMargin

  private def positiveRow(value: Golden): String =
    Vector(value.target, safeField(value.assembly), value.bytes.mkString(","), safeField(value.source)).mkString("\t")

  private def diagnosticRow(value: Diagnostic): String =
    Vector(value.target, safeField(value.assembly), safeField(value.message), safeField(value.source)).mkString("\t")

  private def writeGzip(output: Path, rows: Vector[String]): Unit = {
    val bytes = {
      val buffer = new ByteArrayOutputStream()
      val gzip   = new GZIPOutputStream(buffer)
      try gzip.write((rows.mkString("\n") + "\n").getBytes(StandardCharsets.UTF_8))
      finally gzip.close()
      buffer.toByteArray
    }
    val current = if (Files.isRegularFile(output)) Some(Files.readAllBytes(output)) else None
    if (!current.exists(existing => Arrays.equals(existing, bytes))) {
      val _ = Files.createDirectories(output.getParent)
      val _ = Files.write(output, bytes)
    }
  }

  private def safeField(value: String): String = {
    require(!value.exists(char => char == '\t' || char == '\r' || char == '\n') && !value.contains("\"\"\""))
    value
  }

  private def write(output: Path, content: String): Unit = {
    val formatted = GeneratedScalaFormatter(output, content)
    val current   = if (Files.isRegularFile(output)) Some(Files.readString(output, StandardCharsets.UTF_8)) else None
    if (!current.contains(formatted)) {
      val _ = Files.createDirectories(output.getParent)
      val _ = Files.writeString(output, formatted, StandardCharsets.UTF_8)
    }
  }
}
