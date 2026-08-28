package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.Col.*

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.GZIPInputStream
import scala.collection.mutable.ArrayBuffer

// Generated exhaustively from pinned LLVM MC/AMDGPU checks. Do not edit.
final case class AMDGPUGoldenCase(target: Target, assembly: String, bytes: Col[Byte], source: String)
final case class AMDGPUDiagnosticCase(target: Target, assembly: String, message: String, source: String)

object GoldenCorpus {
  val sourceFileCount: Int = 770
  val sourceEncodingCheckCount: Int = 379260
  val sourceDiagnosticCheckCount: Int = 114008
  val caseCount: Int = 308161
  val diagnosticCount: Int = 98730

  lazy val cases: Col[AMDGPUGoldenCase] = load("positive.tsv.gz").map { row =>
    val fields = row.split("\t", -1)
    AMDGPUGoldenCase(
      Target.unsafe(fields(0)),
      fields(1),
      fields(2).split(',').toCol.map(_.toInt.toByte),
      fields(3)
    )
  }

  lazy val diagnostics: Col[AMDGPUDiagnosticCase] = load("diagnostics.tsv.gz").map { row =>
    val fields = row.split("\t", -1)
    AMDGPUDiagnosticCase(Target.unsafe(fields(0)), fields(1), fields(2), fields(3))
  }

  private def load(name: String): Col[String] = {
    val root = repositoryRoot(Paths.get(sys.props("user.dir")))
    val path = root.resolve(
      "isa_amdgpu/src/generated/test/resources/net/kurobako/gemstone/isa/amdgpu/common"
    ).resolve(name)
    val input = new GZIPInputStream(Files.newInputStream(path))
    val reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))
    val result = ArrayBuffer.empty[String]
    try {
      var line = reader.readLine()
      while (line != null) {
        result += line
        line = reader.readLine()
      }
    } finally reader.close()
    result.toCol
  }

  private def repositoryRoot(start: Path): Path = {
    var cursor = start.toAbsolutePath.normalize()
    while (cursor != null && !Files.isRegularFile(cursor.resolve("build.sbt")))
      cursor = cursor.getParent
    require(cursor != null, "cannot locate Gemstone repository from " + start)
    cursor
  }
}
