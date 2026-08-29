package net.kurobako.gemstone.isa.amdgpu

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.Col.*

final case class OracleCase(target: Target, assembly: String, bytes: Col[Byte], source: String)
final case class OracleDiagnostic(target: Target, assembly: String, message: String, source: String)

/** Checked-in, readable subset of the pinned LLVM MC AMDGPU oracle. */
object LLVMOracle {
  private final case class Loaded(cases: Col[OracleCase], diagnostics: Col[OracleDiagnostic])

  private val Root            = "/net/kurobako/gemstone/isa/amdgpu/llvm-oracle/"
  private val Encoding        = raw"// ENCODING: \[([^]]*)\]".r
  private val Error           = raw"// ERROR: (.*)".r
  private val SourceDirective = raw"// SOURCE: (.+)".r
  private val TargetDirective = raw"// TARGET: (.+)".r

  private lazy val manifest: Map[String, Col[String]] =
    lines("manifest.txt").filterNot(line => line.isBlank || line.startsWith("#")).map { line =>
      line.split("=", 2) match {
        case Array(key, value) => key -> value
        case _                 => throw IllegalArgumentException(s"invalid LLVM oracle manifest line: $line")
      }
    }.groupMap(_._1)(_._2)

  val sourceFileCount: Int            = property("source-files")
  val sourceEncodingCheckCount: Int   = property("source-encoding-checks")
  val sourceDiagnosticCheckCount: Int = property("source-diagnostic-checks")
  val sourceCaseCount: Int            = property("source-positive-cases")
  val sourceDiagnosticCount: Int      = property("source-diagnostic-cases")
  val selectedCaseCount: Int          = property("selected-positive-cases")
  val selectedDiagnosticCount: Int    = property("selected-diagnostic-cases")

  private lazy val loaded: Loaded =
    manifest.getOrElse("file", Col.empty[String]).map(load)
      .foldLeft(Loaded(Col.empty[OracleCase], Col.empty[OracleDiagnostic])) { (all, current) =>
        Loaded(all.cases ++ current.cases, all.diagnostics ++ current.diagnostics)
      }

  lazy val cases: Col[OracleCase]             = loaded.cases
  lazy val diagnostics: Col[OracleDiagnostic] = loaded.diagnostics
  lazy val targets: Col[Target]               = cases.map(_.target).distinct
  lazy val diagnosticTargets: Col[Target]     = diagnostics.map(_.target).distinct

  private def property(name: String): Int =
    manifest.get(name).flatMap(_.headOption).map(_.toInt)
      .getOrElse(throw IllegalArgumentException(s"missing LLVM oracle manifest property '$name'"))

  private def load(name: String): Loaded = {
    val content = lines(name)
    val target  = content.collectFirst { case TargetDirective(value) => Target.unsafe(value) }
      .getOrElse(throw IllegalArgumentException(s"missing target in LLVM oracle fixture $name"))
    val initial =
      (Option.empty[String], Option.empty[String], Col.empty[OracleCase], Col.empty[OracleDiagnostic])
    val (_, _, cases, diagnostics) = content.foldLeft(initial) {
      case ((_, pending, cases, diagnostics), SourceDirective(source)) =>
        (Some(source), pending, cases, diagnostics)
      case ((Some(source), Some(assembly), cases, diagnostics), Encoding(encoded)) =>
        val bytes = encoded.split(',').iterator.map(_.trim).filter(_.nonEmpty)
          .map(value => Integer.parseInt(value.drop(2), 16).toByte).toCol
        (Some(source), None, cases :+ OracleCase(target, assembly, bytes, source), diagnostics)
      case ((Some(source), Some(assembly), cases, diagnostics), Error(message)) =>
        (Some(source), None, cases, diagnostics :+ OracleDiagnostic(target, assembly, message, source))
      case ((source, pending, cases, diagnostics), line) if line.isBlank || line.startsWith("//") =>
        (source, pending, cases, diagnostics)
      case ((source @ Some(_), None, cases, diagnostics), assembly) =>
        (source, Some(assembly), cases, diagnostics)
      case ((_, Some(previous), _, _), assembly) =>
        throw IllegalArgumentException(s"missing oracle result for '$previous' before '$assembly' in $name")
      case ((None, None, _, _), assembly) =>
        throw IllegalArgumentException(s"missing source before '$assembly' in $name")
    }
    Loaded(cases, diagnostics)
  }

  private def lines(name: String): Col[String] = {
    val input = Option(getClass.getResourceAsStream(Root + name))
      .getOrElse(throw IllegalArgumentException(s"missing LLVM oracle resource $name"))
    val reader = BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8))
    try Iterator.continually(reader.readLine()).takeWhile(_ != null).toCol
    finally reader.close()
  }
}
