import java.nio.file.Path
import sbt.internal.util.MessageOnlyException
import scala.sys.process.Process
import scala.sys.process.ProcessLogger

/** clang-format invocation shared by the native formatting tasks. */
object ClangFormat {
  def format(files: Seq[Path], executable: String, workingDirectory: Path, log: String => Unit): Unit = {
    val status = run(files, executable, Vector("-i"), workingDirectory, log)
    if (status != 0) throw MessageOnlyException(s"$executable exited with $status")
  }

  def check(files: Seq[Path], executable: String, workingDirectory: Path, log: String => Unit): Boolean =
    run(files, executable, Vector("--dry-run", "--Werror"), workingDirectory, log) == 0

  private def run(
      files: Seq[Path],
      executable: String,
      arguments: Vector[String],
      workingDirectory: Path,
      log: String => Unit
  ): Int = {
    require(
      executable.nonEmpty && !executable.contains('\u0000'),
      "clang-format must be nonempty and cannot contain NUL"
    )
    val command = Vector(executable) ++ arguments ++ files.map(_.toString)
    Process(command, workingDirectory.toFile).!(ProcessLogger(log, log))
  }
}
