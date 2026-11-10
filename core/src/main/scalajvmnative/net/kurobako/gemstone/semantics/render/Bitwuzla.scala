package net.kurobako.gemstone.semantics.render

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.environment as env
import scala.concurrent.duration.FiniteDuration

/** Equivalence through an external Bitwuzla process. The binary comes from `GEMSTONE_BITWUZLA_EXE`, else `PATH`. */
object Bitwuzla {
  def executable: Option[Path] = sys.env.get(env.BitwuzlaExe).filter(_.nonEmpty) match {
    case Some(path) => Some(Path.of(path)).filter(Files.isExecutable(_))
    case None       =>
      sys.env.getOrElse("PATH", "").split(File.pathSeparator).toVector.filter(_.nonEmpty)
        .map(directory => Path.of(directory, "bitwuzla")).find(Files.isExecutable(_))
  }

  def equivalence(left: Definition, right: Definition, limit: FiniteDuration): Verdict =
    SmtBackend.equivalence(left, right) match {
      case Left(reason) => Verdict.Undecided(reason)
      case Right(query) => executable.fold(Verdict.Unavailable)(solve(_, query, limit))
    }

  private def solve(solver: Path, query: String, limit: FiniteDuration): Verdict = {
    val directory = Files.createTempDirectory("gemstone-equivalence")
    val input     = directory.resolve("query.smt2")
    val stdout    = directory.resolve("stdout.txt")
    val stderr    = directory.resolve("stderr.txt")
    try {
      Files.writeString(input, query, StandardCharsets.UTF_8)
      val process = ProcessBuilder(solver.toString, "-m", "-t", limit.toMillis.toString, input.toString)
        .redirectOutput(stdout.toFile)
        .redirectError(stderr.toFile)
        .start()
      if (process.waitFor(limit.toMillis, TimeUnit.MILLISECONDS))
        interpret(Files.readString(stdout, StandardCharsets.UTF_8), Files.readString(stderr, StandardCharsets.UTF_8))
      else {
        val _ = process.destroyForcibly()
        Verdict.Undecided(s"no verdict within $limit")
      }
    } finally
      Seq(input, stdout, stderr, directory).foreach { path =>
        val _ = Files.deleteIfExists(path)
      }
  }

  private def interpret(output: String, errors: String): Verdict = output.linesIterator.map(_.trim).toList match {
    case "unsat" :: _   => Verdict.Equal
    case "sat" :: model => Verdict.Different(model.mkString("\n"))
    case "unknown" :: _ => Verdict.Undecided("solver returned unknown")
    case _              => Verdict.Undecided(if (errors.isBlank) "solver gave no verdict" else errors.trim)
  }
}
