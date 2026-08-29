package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import cats.data.Chain

import scala.jdk.CollectionConverters.*
import scala.util.matching.Regex

/** Selects a bounded, readable conformance oracle from the exhaustive LLVM MC AMDGPU tests. */
object GenerateAMDGPUOracle {
  private final case class Golden(target: String, assembly: String, bytes: Vector[Int], source: String) {
    val opcode: String     = assembly.takeWhile(!_.isWhitespace)
    val sourceFile: String = source.take(source.lastIndexOf(':'))
  }

  private final case class Diagnostic(target: String, assembly: String, message: String, source: String) {
    val sourceFile: String = source.take(source.lastIndexOf(':'))
  }

  private final case class Corpus(
      positiveSeen: Set[(String, String, Vector[Int])],
      diagnosticSeen: Set[(String, String, String)],
      positives: Chain[Golden],
      diagnostics: Chain[Diagnostic],
      encodingChecks: Int,
      diagnosticChecks: Int
  )

  private object Corpus {
    val empty: Corpus = Corpus(Set.empty, Set.empty, Chain.empty, Chain.empty, 0, 0)
  }

  private final case class Selection(positives: Vector[Golden], diagnostics: Vector[Diagnostic])

  private val Run: Regex           = raw"\s*//\s*RUN:\s*(.*)".r
  private val EncodingCheck: Regex =
    raw"\s*//\s*([A-Za-z0-9_-]+):\s*(.*?)\s*;\s*encoding:\s*\[([^]]*)\].*".r
  private val DiagnosticCheck: Regex = raw"\s*//\s*([A-Za-z0-9_-]+):\s*(.*\berror:\s*.*)".r
  private val Mcpu: Regex            = raw"(?:^|\s)-mcpu(?:=|\s+)([A-Za-z0-9_]+)".r
  private val CheckPrefixes: Regex   = raw"--?check-prefix(?:es)?(?:=|\s+)([A-Za-z0-9_,-]+)".r
  private val LineReference: Regex   = raw"\[\[@LINE(?:([+-])([0-9]+))?\]\]".r
  private val CheckDirective: Regex  = raw"-(?:NEXT|SAME|DAG|LABEL|EMPTY|NOT|COUNT-[0-9]+)$$".r
  private val Number: Regex          = raw"(?i)(?:0x[0-9a-f]+|\b[0-9]+\b)".r
  private val Quoted: Regex          = "'[^']*'|\"[^\"]*\"".r
  private val GPUName: Regex         = raw"(?i)\bgfx[0-9a-z]+\b".r

  def main(arguments: Array[String]): Unit = arguments.toVector match {
    case Vector(references, output) => generate(Paths.get(references), Paths.get(output))
    case _                          =>
      throw IllegalArgumentException("usage: GenerateAMDGPUOracle <prepared-references> <output-directory>")
  }

  private[codegen] def generate(references: Path, output: Path): Unit = {
    val root = references.resolve("llvm/llvm/test/MC/AMDGPU")
    require(Files.isDirectory(root), s"missing LLVM AMDGPU tests: $root")
    val stream = Files.walk(root)
    val files  =
      try
        stream.iterator().asScala.filter(path => Files.isRegularFile(path) && path.toString.endsWith(".s")).toVector
          .sortBy(_.toString)
      finally stream.close()

    val corpus      = extract(root, files)
    val positives   = corpus.positives.iterator.toVector.sorted(using goldenOrdering)
    val diagnostics = corpus.diagnostics.iterator.toVector.sorted(using diagnosticOrdering)
    require(positives.size >= 10000, s"expected at least 10,000 exact-target encoding cases, found ${positives.size}")
    require(diagnostics.size >= 1000, s"expected at least 1,000 exact-target diagnostics, found ${diagnostics.size}")

    val selected = select(positives, diagnostics)
    require(selected.positives.size >= 2500, s"oracle selection is unexpectedly small: ${selected.positives.size}")
    require(selected.positives.size <= 7000, s"oracle selection is unexpectedly large: ${selected.positives.size}")
    require(
      selected.diagnostics.size >= 300,
      s"diagnostic selection is unexpectedly small: ${selected.diagnostics.size}"
    )
    require(
      selected.diagnostics.size <= 2000,
      s"diagnostic selection is unexpectedly large: ${selected.diagnostics.size}"
    )
    write(output, files.size, corpus, selected)
  }

  private def extract(root: Path, files: Vector[Path]): Corpus =
    files.foldLeft(Corpus.empty) { (corpus, file) =>
      val lines         = Files.readAllLines(file, StandardCharsets.UTF_8).asScala.toVector
      val prefixTargets = targetsByCheckPrefix(lines)
      val relative      = root.relativize(file).toString
      lines.zipWithIndex.foldLeft(corpus) {
        case (state, (EncodingCheck(rawPrefix, assembly, encoded), index)) =>
          val source = s"$relative:${index + 1}"
          parseBytes(encoded).fold(state.copy(encodingChecks = state.encodingChecks + 1)) { bytes =>
            prefixTargets.getOrElse(normalizePrefix(rawPrefix), Set.empty).toVector.sorted.foldLeft(
              state.copy(encodingChecks = state.encodingChecks + 1)
            ) { (current, target) =>
              val canonical = assembly.trim
              val key       = (target, canonical, bytes)
              if (canonical.nonEmpty && !containsFileCheckExpression(canonical) && !current.positiveSeen(key))
                current.copy(
                  positiveSeen = current.positiveSeen + key,
                  positives = current.positives.append(Golden(target, canonical, bytes, source))
                )
              else current
            }
          }
        case (state, (DiagnosticCheck(rawPrefix, check), index)) =>
          val counted = state.copy(diagnosticChecks = state.diagnosticChecks + 1)
          referencedLine(
            check,
            index
          ).flatMap(lines.lift).map(stripComment).filter(validDiagnosticSource).fold(counted) {
            assembly =>
              val message = check.drop(check.indexOf("error:") + "error:".length).trim
              val source  = s"$relative:${index + 1}"
              prefixTargets.getOrElse(normalizePrefix(rawPrefix), Set.empty).toVector.sorted.foldLeft(counted) {
                (current, target) =>
                  val key = (target, assembly, message)
                  if (!containsFileCheckExpression(assembly) && !current.diagnosticSeen(key))
                    current.copy(
                      diagnosticSeen = current.diagnosticSeen + key,
                      diagnostics = current.diagnostics.append(Diagnostic(target, assembly, message, source))
                    )
                  else current
              }
          }
        case (state, _) => state
      }
    }

  /** Coverage is structural rather than random: every upstream opcode and contributing source file is represented, then
    * per-family/per-target samples and syntax-feature witnesses fill architectural gaps.
    */
  private def select(positives: Vector[Golden], diagnostics: Vector[Diagnostic]): Selection = {
    val positiveCoverage =
      firstBy(positives)(value => canonicalOpcode(value.opcode)) ++
        firstBy(positives)(_.sourceFile) ++
        positives.groupBy(value => familyForTarget(value.target)).values.toVector.flatMap { familyCases =>
          evenly(firstBy(familyCases.sorted(using goldenOrdering))(value => canonicalOpcode(value.opcode)), 128)
        } ++
        positives.groupBy(_.target).values.toVector.flatMap { targetCases =>
          evenly(firstBy(targetCases.sorted(using goldenOrdering))(value => canonicalOpcode(value.opcode)), 16)
        } ++
        firstByMany(positives)(value => syntaxFeatures(value).map(feature => (value.target, feature)))

    val diagnosticCoverage =
      evenly(firstBy(diagnostics)(value => diagnosticCategory(value.message)), 256) ++
        firstBy(diagnostics)(_.sourceFile) ++
        diagnostics.groupBy(value => familyForTarget(value.target)).values.toVector.flatMap { familyCases =>
          evenly(
            firstBy(familyCases.sorted(using diagnosticOrdering))(value => diagnosticCategory(value.message)),
            32
          )
        } ++
        diagnostics.groupBy(_.target).values.toVector.flatMap { targetCases =>
          evenly(
            firstBy(targetCases.sorted(using diagnosticOrdering))(value => diagnosticCategory(value.message)),
            8
          )
        }

    Selection(
      positiveCoverage.distinctBy(value => (value.target, value.assembly, value.bytes)).sorted(using goldenOrdering),
      diagnosticCoverage.distinctBy(value => (value.target, value.assembly, value.message)).sorted(
        using diagnosticOrdering
      )
    )
  }

  private def firstBy[A, K](values: Vector[A])(key: A => K): Vector[A] =
    values.foldLeft((Set.empty[K], Chain.empty[A])) { case ((seen, selected), value) =>
      val current = key(value)
      if (seen(current)) (seen, selected) else (seen + current, selected.append(value))
    }._2.iterator.toVector

  private def firstByMany[A, K](values: Vector[A])(keys: A => Vector[K]): Vector[A] =
    values.foldLeft((Set.empty[K], Chain.empty[A])) { case ((seen, selected), value) =>
      val novel = keys(value).filterNot(seen)
      if (novel.isEmpty) (seen, selected) else (seen ++ novel, selected.append(value))
    }._2.iterator.toVector

  private def evenly[A](values: Vector[A], limit: Int): Vector[A] =
    if (values.size <= limit) values
    else Vector.tabulate(limit)(index => values(index * (values.size - 1) / (limit - 1)))

  private def syntaxFeatures(value: Golden): Vector[String] = {
    val assembly = value.assembly
    Vector(
      Some(s"bytes-${value.bytes.size}"),
      Option.when(assembly.contains("::"))("paired"),
      Option.when(assembly.contains("_e32"))("e32"),
      Option.when(assembly.contains("_e64"))("e64"),
      Option.when(assembly.contains("_dpp8"))("dpp8"),
      Option.when(assembly.contains("_dpp"))("dpp"),
      Option.when(assembly.contains("_sdwa"))("sdwa"),
      Option.when(assembly.contains("s["))("sgpr-range"),
      Option.when(assembly.contains("v["))("vgpr-range"),
      Option.when(assembly.matches(".*\\bv[0-9]+.*"))("vgpr"),
      Option.when(assembly.matches(".*\\bs[0-9]+.*"))("sgpr"),
      Option.when(assembly.contains("ttmp"))("temporary-register"),
      Option.when(assembly.contains("vcc"))("vcc"),
      Option.when(assembly.contains("exec"))("exec"),
      Option.when(assembly.contains("src_"))("pseudo-source"),
      Option.when(assembly.matches(".*(?:^|[ ,])[-+]?[0-9]+(?:[ ,]|$).*"))("integer-immediate"),
      Option.when(assembly.matches(".*(?:^|[ ,])[-+]?[0-9]+\\.[0-9]+.*"))("floating-immediate"),
      Option.when(assembly.contains("0x"))("hex-literal"),
      Option.when(assembly.contains("-|") || assembly.contains("|-"))("absolute-negate"),
      Option.when(assembly.contains("op_sel"))("op-sel"),
      Option.when(assembly.contains("clamp"))("clamp"),
      Option.when(assembly.contains("row_mask") || assembly.contains("bank_mask"))("lane-mask"),
      Option.when(assembly.contains("offset"))("offset"),
      Option.when(assembly.contains(" gds"))("gds"),
      Option.when(assembly.contains(" glc") || assembly.contains(" slc") || assembly.contains(" dlc"))("cache-policy")
    ).flatten
  }

  private def diagnosticCategory(message: String): String = {
    val withoutQuoted  = Quoted.replaceAllIn(message.toLowerCase, "<value>")
    val withoutGPUName = GPUName.replaceAllIn(withoutQuoted, "<gpu>")
    Number.replaceAllIn(withoutGPUName, "<number>").replaceAll("\\s+", " ").trim
  }

  private def canonicalOpcode(opcode: String): String =
    EncodingSuffixes.collectFirst { case suffix if opcode.endsWith(suffix) => opcode.dropRight(suffix.length) } match {
      case Some(value) => canonicalOpcode(value)
      case None        => opcode
    }

  private def write(output: Path, sourceFileCount: Int, corpus: Corpus, selected: Selection): Unit = {
    val grouped =
      (selected.positives.map(value => value.target -> Left(value)) ++
        selected.diagnostics.map(value => value.target -> Right(value))).groupMap(_._1)(_._2)
    val files = grouped.toVector.sortBy(_._1).map { case (target, cases) =>
      val name = s"$target.s"
      val body = cases.groupBy {
        case Left(value)  => value.sourceFile
        case Right(value) => value.sourceFile
      }.toVector.sortBy(_._1).map { case (sourceFile, sourceCases) =>
        val rendered = sourceCases.sortBy {
          case Left(value)  => (value.assembly, "0", value.source)
          case Right(value) => (value.assembly, "1", value.source)
        }.map {
          case Left(value) =>
            val bytes = value.bytes.map(byte => f"0x$byte%02x").mkString(", ")
            s"${value.assembly}\n// ENCODING: [$bytes]"
          case Right(value) =>
            s"${value.assembly}\n// ERROR: ${value.message}"
        }.mkString("\n\n")
        s"// SOURCE: $sourceFile\n\n$rendered"
      }.mkString("\n\n")
      name ->
        s"""|// Generated from pinned LLVM MC/AMDGPU checks. Do not edit.
            |// TARGET: $target
            |
            |$body
            |""".stripMargin
    }

    val manifest =
      s"""|# Generated from pinned LLVM MC/AMDGPU checks. Do not edit.
          |source-files=$sourceFileCount
          |source-encoding-checks=${corpus.encodingChecks}
          |source-diagnostic-checks=${corpus.diagnosticChecks}
          |source-positive-cases=${corpus.positives.size.toLong}
          |source-diagnostic-cases=${corpus.diagnostics.size.toLong}
          |selected-positive-cases=${selected.positives.size}
          |selected-diagnostic-cases=${selected.diagnostics.size}
          |${files.map { case (name, _) => s"file=$name" }.mkString("\n")}
          |""".stripMargin

    val _ = Files.createDirectories(output)
    writeText(output.resolve("manifest.txt"), manifest)
    files.foreach { case (name, content) => writeText(output.resolve(name), content) }
    val expected = files.map(_._1).toSet + "manifest.txt"
    val stream   = Files.list(output)
    try
      stream.iterator().asScala.filter(path => Files.isRegularFile(path))
        .filterNot(path => expected(path.getFileName.toString)).foreach(path => Files.delete(path))
    finally stream.close()
    println(
      s"selected ${selected.positives.size}/${corpus.positives.size.toLong} positive and " +
        s"${selected.diagnostics.size}/${corpus.diagnostics.size.toLong} diagnostic LLVM AMDGPU cases"
    )
  }

  private def writeText(output: Path, content: String): Unit = {
    val _ = GeneratedFiles.write(output, content)
  }

  private def targetsByCheckPrefix(lines: Vector[String]): Map[String, Set[String]] = {
    val pairs = lines.flatMap {
      case Run(command) if command.contains("llvm-mc") && !command.contains("-disassemble") =>
        Mcpu.findFirstMatchIn(command).toVector.flatMap { targetMatch =>
          val target = targetMatch.group(1).toLowerCase
          familyForTargetOption(target).toVector.flatMap { _ =>
            CheckPrefixes.findFirstMatchIn(command).map(_.group(1).split(',').toVector).getOrElse(Vector("CHECK"))
              .map(prefix => normalizePrefix(prefix) -> target)
          }
        }
      case _ => Vector.empty
    }
    pairs.groupMap(_._1)(_._2).view.mapValues(_.toSet).toMap
  }

  private def familyForTarget(target: String): String =
    familyForTargetOption(target).getOrElse(throw IllegalArgumentException(s"unsupported target $target"))

  private def familyForTargetOption(target: String): Option[String] = target match {
    case "tahiti" | "verde" | "gfx600" | "gfx601"                         => Some("gfx6")
    case "bonaire" | "hawaii" | "kabini" | "kaveri" | "gfx700" | "gfx701" => Some("gfx7")
    case "carrizo" | "fiji" | "iceland" | "stoney" | "tonga" | "gfx800" | "gfx801" | "gfx802" | "gfx803" |
        "gfx810" => Some("gfx8")
    case "gfx908"                                    => Some("cdna1")
    case "gfx90a"                                    => Some("cdna2")
    case "gfx940" | "gfx941" | "gfx942"              => Some("cdna3")
    case "gfx950"                                    => Some("cdna4")
    case "gfx9" | "gfx900" | "gfx904" | "gfx906"     => Some("gfx9")
    case value if value.startsWith("gfx10")          => Some("gfx10")
    case value if value.startsWith("gfx11")          => Some("gfx11")
    case "gfx12" | "gfx1200" | "gfx1250" | "gfx1251" => Some("gfx12")
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
    Option.when(
      fields.forall(field =>
        field.length >= 3 && field.length <= 4 && field.take(2).equalsIgnoreCase("0x") &&
          field.drop(2).forall(character => Character.digit(character, 16) >= 0)
      )
    )(fields.map(field => Integer.parseInt(field.drop(2), 16)))
  }

  private def goldenKey(value: Golden): (String, String, String, String) =
    (value.target, value.opcode, value.source, value.assembly)

  private def diagnosticKey(value: Diagnostic): (String, String, String, String) =
    (value.target, value.source, value.assembly, value.message)

  private val goldenOrdering: Ordering[Golden]         = Ordering.by(goldenKey)
  private val diagnosticOrdering: Ordering[Diagnostic] = Ordering.by(diagnosticKey)
  private val EncodingSuffixes                         = Vector("_dpp8", "_sdwa", "_dpp", "_e64", "_e32")
}
