import java.nio.file.Path
import scala.language.implicitConversions
import scala.sys.process.{Process, ProcessLogger}

import Paths.{given, *}

/** Minimal CMake/CTest command construction shared by the build. */
object CMake {
  def build(
      source: Path,
      build: Path,
      cmake: String,
      arguments: Seq[String],
      parallelism: Int,
      log: String => Unit,
      target: Option[String] = None
  ): Path = {
    require(cmake.nonEmpty && !cmake.contains('\u0000'), "CMake command must be nonempty and cannot contain NUL")
    require(parallelism > 0, s"CMake parallelism must be positive: $parallelism")
    require(
      target.forall(value => value.nonEmpty && !value.contains('\u0000')),
      "CMake target must be nonempty and cannot contain NUL"
    )
    val processLogger = ProcessLogger(log, log)
    val configure     = Seq(cmake, "-S", s"$source", "-B", s"$build") ++ arguments
    require(Process(configure, source).!(processLogger) == 0, s"CMake configure failed: ${configure.mkString(" ")}")
    val compile = Seq(cmake, "--build", s"$build", "--parallel", s"$parallelism") ++
      target.toSeq.flatMap(value => Seq("--target", value))
    require(Process(compile, source).!(processLogger) == 0, s"CMake build failed: ${compile.mkString(" ")}")
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
    require(Process(command, build).!(ProcessLogger(log, log)) == 0, s"CTest failed: ${command.mkString(" ")}")
  }
}
