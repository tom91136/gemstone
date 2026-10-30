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
      launcher: Seq[String]
  ): Path = {
    val configure = Seq("cmake", "-S", s"$source", "-B", s"$directory", "-G", "Ninja") ++
      Seq("-DCMAKE_MESSAGE_LOG_LEVEL=WARNING") ++ arguments
    val signature = directory / "configure.arguments"
    if (
      !(directory / "CMakeCache.txt").isFile || !signature.isFile ||
      Files.readString(signature) != configure.mkString("\n")
    ) {
      run(configure, source, log)
      val _ = Files.writeString(signature, configure.mkString("\n"))
    }
    run(
      launcher ++ Seq("cmake", "--build", s"$directory") ++
        (if (buildTargets.isEmpty) Seq.empty else "--target" +: buildTargets) ++
        Seq("--", "--quiet"),
      source,
      log
    )
    directory
  }

  def test(directory: Path, log: String => Unit): Unit =
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
      log
    )

  private def run(command: Seq[String], directory: Path, log: String => Unit): Unit = {
    val child  = Process(command, directory.toFile).run(ProcessLogger(log, log))
    val status =
      try child.exitValue()
      finally child.destroy()
    if (status != 0) throw MessageOnlyException(s"${command.mkString(" ")} exited with $status")
  }
}
