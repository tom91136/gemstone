package net.kurobako.gemstone.isa.amdgpu

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.string.*
import scala.util.Using

/** Checked-in, readable subset of the pinned LLVM MC AMDGPU oracle. */
private object oracle {
  case class OracleCase(target: Target, assembly: String, bytes: Vec[Byte], source: String, waveSize: Int in (32, 64))
  case class OracleDiagnostic(target: Target, assembly: String, message: String, source: String)

  private case class Loaded(cases: Vec[OracleCase], diagnostics: Vec[OracleDiagnostic])

  private lazy val manifest: Map[String, Vec[String]] =
    lines("manifest.txt").filterNot(line => line.isBlank || line.startsWith("#")).map {
      case s"$key=$value" => key -> value
      case invalid        => throw IllegalArgumentException(s"invalid LLVM oracle manifest line: $invalid")
    }.groupMap(_._1)(_._2)

  val selectedCaseCount: Int       = property("selected-positive-cases")
  val selectedDiagnosticCount: Int = property("selected-diagnostic-cases")

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
      value.toLowerCaseRoot.refine[Target].fold(
        message => throw IllegalArgumentException(message),
        value => value
      )
    }
      .getOrElse(throw IllegalArgumentException(s"missing target in LLVM oracle fixture $name"))
    val initial =
      (Option.empty[String], Option.empty[String], Vec.empty[OracleCase], Vec.empty[OracleDiagnostic])
    val (_, _, cases, diagnostics) = content.foldLeft(initial) {
      case ((_, pending, foundCases, foundDiagnostics), s"// SOURCE: $source") =>
        (Some(source), pending, foundCases, foundDiagnostics)
      case (
            (Some(source), Some(assembly), foundCases, foundDiagnostics),
            s"// ENCODING: [$encoded] WAVE_SIZE: $waveSize"
          ) =>
        val bytes = encoded.split(',').view.map(_.trim).filter(_.nonEmpty)
          .map(value => Integer.parseInt(value.drop(2), 16).toByte).toVec
        (
          Some(source),
          None,
          foundCases :+ OracleCase(target, assembly, bytes, source, waveSize.toInt.unsafe[Int in (32, 64)]),
          foundDiagnostics
        )
      case ((Some(source), Some(assembly), foundCases, foundDiagnostics), s"// ERROR: $message") =>
        (Some(source), None, foundCases, foundDiagnostics :+ OracleDiagnostic(target, assembly, message, source))
      case ((source, pending, foundCases, foundDiagnostics), line) if line.isBlank || line.startsWith("//") =>
        (source, pending, foundCases, foundDiagnostics)
      case ((source @ Some(_), None, foundCases, foundDiagnostics), assembly) =>
        (source, Some(assembly), foundCases, foundDiagnostics)
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
    val input = Option(getClass.getResourceAsStream(s"llvm-oracle/$name"))
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
