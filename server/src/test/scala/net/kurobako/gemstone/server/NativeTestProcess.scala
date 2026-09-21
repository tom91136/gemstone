package net.kurobako.gemstone.server

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.launcher.Environment

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration
import scala.util.Using

private object NativeTestProcess {
  private final class TemporaryOutput(
      val directory: Path,
      val stdout: Path,
      val stderr: Path
  ) extends AutoCloseable {
    override def close(): Unit = {
      val _ = Files.deleteIfExists(stdout)
      val _ = Files.deleteIfExists(stderr)
      val _ = Files.deleteIfExists(directory)
    }
  }

  private def temporaryOutput(): TemporaryOutput = {
    val directory = Files.createTempDirectory("gemstone-native-test-")
    TemporaryOutput(directory, directory.resolve("stdout.log"), directory.resolve("stderr.log"))
  }

  private final class RunningProcess(val process: Process) extends AutoCloseable {
    override def close(): Unit =
      if (process.isAlive) {
        process.descendants().forEach { handle =>
          val _ = handle.destroyForcibly()
        }
        val _ = process.destroyForcibly()
        val _ = process.waitFor()
      }
  }

  final case class Result(
      command: Vector[String :| NoNul] :| MinLength[1],
      exit: Int,
      stdout: String,
      stderr: String
  ) {
    def diagnostic: String =
      s"command exited with $exit: ${command.mkString(" ")}\nstdout:\n$stdout\nstderr:\n$stderr"

  }

  val root: Path = Iterator.unfold(Option(Path.of(sys.props("user.dir")).toAbsolutePath.normalize))(
    _.map(path => path -> Option(path.getParent))
  )
    .find(path => Files.isRegularFile(path.resolve("build.sbt")))
    .getOrElse(throw IllegalStateException("cannot locate Gemstone repository root"))

  lazy val nativeDirectory: Path = Path.of(
    sys.props.get("gemstone.native.directory")
      .orElse(sys.env.get("GEMSTONE_NATIVE_TEST_DIRECTORY"))
      .getOrElse(
        throw IllegalStateException("native test directory was not supplied by the build")
      )
  )

  def run(
      command: Vector[String :| NoNul] :| MinLength[1],
      directory: Path,
      environment: Environment,
      timeout: FiniteDuration
  ): Result =
    Using.resource(temporaryOutput()) { output =>
      val builder = ProcessBuilder(command*).directory(directory.toFile)
        .redirectOutput(output.stdout.toFile).redirectError(output.stderr.toFile)
      environment.applyTo(builder.environment())
      Using.resource(RunningProcess(builder.start())) { running =>
        val completed = running.process.waitFor(timeout.toMillis, TimeUnit.MILLISECONDS)
        if (!completed) running.close()
        Result(
          command,
          if (completed) running.process.exitValue() else -1,
          Files.readString(output.stdout, StandardCharsets.UTF_8),
          Files.readString(output.stderr, StandardCharsets.UTF_8) +
            (if (completed) "" else s"\nprocess timed out after $timeout\n")
        )
      }
    }
}
