package net.kurobako.gemstone.testing

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration
import scala.util.Using

/** Bounded child processes with separate captured streams. Timeouts terminate the child and its descendants. */
object process {
  private class RunningProcess(val process: Process) extends AutoCloseable {
    override def close(): Unit =
      if (process.isAlive) {
        Using.resource(process.descendants())(_.forEach { handle =>
          val _ = handle.destroyForcibly()
        })
        val _ = process.destroyForcibly()
        val _ = process.waitFor()
      }
  }

  case class Result(
      command: Seq[String],
      exit: Int,
      stdout: String,
      stderr: String
  ) {
    def diagnostic: String =
      s"command exited with $exit: ${command.toVector.mkString(" ")}\nstdout:\n$stdout\nstderr:\n$stderr"

  }

  def run(
      command: Seq[String],
      directory: Path,
      set: Map[String, String],
      unset: Set[String],
      timeout: FiniteDuration
  ): Result =
    Using.resource(files.Directory()) { output =>
      val stdout = output.path.resolve("stdout.log")
      val stderr = output.path.resolve("stderr.log")
      require(command.nonEmpty, "process command must not be empty")
      require(command.toVector.forall(!_.contains('\u0000')), "process arguments must not contain a NUL character")
      val builder = ProcessBuilder(command.toVector*).directory(directory.toFile)
        .redirectOutput(stdout.toFile).redirectError(stderr.toFile)
      unset.foreach(builder.environment().remove(_))
      set.foreach { case (name, value) => builder.environment().put(name, value) }
      Using.resource(RunningProcess(builder.start())) { running =>
        val completed = running.process.waitFor(timeout.toMillis, TimeUnit.MILLISECONDS)
        if (!completed) running.close()
        Result(
          command,
          if (completed) running.process.exitValue() else -1,
          Files.readString(stdout, StandardCharsets.UTF_8),
          Files.readString(stderr, StandardCharsets.UTF_8) +
            (if (completed) "" else s"\nprocess timed out after $timeout\n")
        )
      }
    }
}
