import Paths.{given, *}
import java.nio.file.Files
import java.nio.file.Path
import sbt.internal.util.MessageOnlyException
import scala.language.implicitConversions
import scala.sys.process.Process
import scala.sys.process.ProcessLogger
import scala.util.Using

/** Minimal CMake/CTest command construction shared by the build. */
object CMake {
  private def run(command: Seq[String], directory: Path, output: Path, log: String => Unit): Unit = {
    Files.createDirectories(output.getParent)
    log(s"${command.head}: $directory (log: $output)")
    val status = Using.resource(Files.newBufferedWriter(output)) { writer =>
      def write(line: String): Unit = writer.synchronized {
        writer.write(line)
        writer.newLine()
        writer.flush()
        if (line.startsWith("[artifact]") || line.startsWith("Artifact build:")) log(line)
      }
      val child = Process(command, directory).run(ProcessLogger(write, write))
      try child.exitValue()
      finally child.destroy()
    }
    if (status != 0) {
      // Keep the complete log on disk; show enough context without flooding the terminal.
      Files.readString(output).linesIterator.toVector.takeRight(80).foreach(log)
      throw MessageOnlyException(s"${command.head} exited with $status; full output: $output")
    }
  }

  def build(
      source: Path,
      build: Path,
      cmake: String,
      arguments: Seq[String],
      parallelism: Int,
      log: String => Unit,
      target: Option[String] = None,
      jobserver: Seq[String] = Seq.empty
  ): Path = {
    require(cmake.nonEmpty && !cmake.contains('\u0000'), "CMake command must be nonempty and cannot contain NUL")
    require(parallelism > 0, s"CMake parallelism must be positive: $parallelism")
    require(
      target.forall(value => value.nonEmpty && !value.contains('\u0000')),
      "CMake target must be nonempty and cannot contain NUL"
    )
    val configure = Seq(cmake, "-S", s"$source", "-B", s"$build") ++ arguments
    run(configure, source, build.resolve("configure.log"), log)
    val compile =
      (if (jobserver.nonEmpty) jobserver ++ Seq("--jobs", parallelism.toString, "--", cmake, "--build", s"$build")
       else Seq(cmake, "--build", s"$build", "--parallel", s"$parallelism")) ++
        target.toSeq.flatMap(value => Seq("--target", value))
    run(compile, source, build.resolve("build.log"), log)
    build
  }

  def test(
      build: Path,
      ctest: String,
      parallelism: Int,
      log: String => Unit
  ): Unit = {
    require(ctest.nonEmpty && !ctest.contains('\u0000'), "CTest command must be nonempty and cannot contain NUL")
    require(parallelism > 0, s"CTest parallelism must be positive: $parallelism")
    val command = Seq(
      ctest,
      "--test-dir",
      s"$build",
      "--output-on-failure",
      "--parallel",
      s"$parallelism"
    )
    val output = build.resolve("test.log")
    run(command, build, output, log)
    Files.readString(output).linesIterator.filter(line =>
      line.contains("tests passed,") || line.startsWith("Total Test time")
    ).foreach(log)
  }
}
