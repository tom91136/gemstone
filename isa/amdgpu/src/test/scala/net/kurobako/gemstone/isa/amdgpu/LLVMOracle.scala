package net.kurobako.gemstone.isa.amdgpu

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import scala.util.Using

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*

/** Checked-in, readable subset of the pinned LLVM MC AMDGPU oracle. */
object LLVMOracle {
  final case class OracleCase(target: Target, assembly: String, bytes: Vec[Byte], source: String)
  final case class OracleDiagnostic(target: Target, assembly: String, message: String, source: String)

  private final case class Loaded(cases: Vec[OracleCase], diagnostics: Vec[OracleDiagnostic])

  private val Root = "/net/kurobako/gemstone/isa/amdgpu/llvm-oracle/"

  private lazy val manifest: Map[String, Vec[String]] =
    lines("manifest.txt").filterNot(line => line.isBlank || line.startsWith("#")).map { line =>
      line match {
        case s"$key=$value" => key -> value
        case _              => throw IllegalArgumentException(s"invalid LLVM oracle manifest line: $line")
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
    manifest.getOrElse("file", Vec.empty[String]).map(load(_))
      .foldLeft(Loaded(Vec.empty[OracleCase], Vec.empty[OracleDiagnostic])) { (all, current) =>
        Loaded(all.cases ++ current.cases, all.diagnostics ++ current.diagnostics)
      }

  lazy val cases: Vec[OracleCase]             = loaded.cases
  lazy val diagnostics: Vec[OracleDiagnostic] = loaded.diagnostics
  lazy val targets: Vec[Target]               = cases.map(_.target).distinct
  lazy val diagnosticTargets: Vec[Target]     = diagnostics.map(_.target).distinct

  private def property(name: String): Int =
    manifest.get(name).flatMap(_.headOption).map(_.toInt)
      .getOrElse(throw IllegalArgumentException(s"missing LLVM oracle manifest property '$name'"))

  private def load(name: String): Loaded = {
    val content = lines(name)
    val target  = content.collectFirst { case s"// TARGET: $value" =>
      Target.either(value.toLowerCase).fold(message => throw IllegalArgumentException(message), value => value)
    }
      .getOrElse(throw IllegalArgumentException(s"missing target in LLVM oracle fixture $name"))
    val initial =
      (Option.empty[String], Option.empty[String], Vec.empty[OracleCase], Vec.empty[OracleDiagnostic])
    val (_, _, cases, diagnostics) = content.foldLeft(initial) {
      case ((_, pending, cases, diagnostics), s"// SOURCE: $source") =>
        (Some(source), pending, cases, diagnostics)
      case ((Some(source), Some(assembly), cases, diagnostics), s"// ENCODING: [$encoded]") =>
        val bytes = encoded.split(',').iterator.map(_.trim).filter(_.nonEmpty)
          .map(value => Integer.parseInt(value.drop(2), 16).toByte).toVec
        (Some(source), None, cases :+ OracleCase(target, assembly, bytes, source), diagnostics)
      case ((Some(source), Some(assembly), cases, diagnostics), s"// ERROR: $message") =>
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

  /** BufferedReader uses null as its Java end-of-stream sentinel; keep it confined to this pattern match. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def lines(name: String): Vec[String] = {
    val input = Option(getClass.getResourceAsStream(Root + name))
      .getOrElse(throw IllegalArgumentException(s"missing LLVM oracle resource $name"))
    Using.resource(BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8))) { reader =>
      Iterator.unfold(()) { _ =>
        reader.readLine() match {
          case null => None
          case line => Some(line -> ())
        }
      }.toVec
    }
  }
}
