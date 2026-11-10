import Paths.*
import java.nio.file.Files
import java.nio.file.Path
import sbt.internal.util.MessageOnlyException
import scala.sys.process.Process
import scala.sys.process.ProcessLogger

object CMake {
  def build(source: Path, directory: Path, arguments: Seq[String], log: String => Unit): Path =
    build(source, directory, arguments, log, Seq.empty, Seq.empty)

  def build(
      source: Path,
      directory: Path,
      arguments: Seq[String],
      log: String => Unit,
      buildTargets: Seq[String],
      launcher: Seq[String],
      container: Option[ToolchainContainer] = None
  ): Path = {
    val configure = Seq("cmake", "-S", s"$source", "-B", s"$directory", "-G", "Ninja") ++
      Seq("-DCMAKE_MESSAGE_LOG_LEVEL=WARNING") ++ arguments
    val signature = directory / "configure.arguments"
    if (
      !(directory / "CMakeCache.txt").isFile || !signature.isFile ||
      Files.readString(signature) != configure.mkString("\n")
    ) {
      run(configure, source, log, container)
      val _ = Files.writeString(signature, configure.mkString("\n"))
    }
    run(
      launcher ++ Seq("cmake", "--build", s"$directory") ++
        (if (buildTargets.isEmpty) Seq.empty else "--target" +: buildTargets) ++
        Seq("--"),
      source,
      log,
      container
    )
    directory
  }

  def test(directory: Path, log: String => Unit, container: Option[ToolchainContainer] = None): Unit =
    run(
      Seq(
        "ctest",
        "--test-dir",
        s"$directory",
        "--output-on-failure",
        "--parallel",
        s"${BuildEnvironment.threadCount}"
      ),
      directory,
      log,
      container
    )

  private def run(
      command: Seq[String],
      directory: Path,
      log: String => Unit,
      container: Option[ToolchainContainer]
  ): Unit = {
    val child =
      Process(container.fold(command)(_.wrap(command, directory)), directory.toFile).run(ProcessLogger(log, log))
    val status =
      try child.exitValue()
      finally child.destroy()
    if (status != 0) throw MessageOnlyException(s"${command.mkString(" ")} exited with $status")
  }
}
