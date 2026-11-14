import java.nio.file.Files
import java.nio.file.Path
import sbt.internal.util.MessageOnlyException
import sbt.io.IO
import scala.sys.process.Process
import scala.sys.process.ProcessLogger

/** The native toolchain image, which holds the compilers and sysroot the shipped libraries build with. */
final case class ToolchainContainer(
    executable: String,
    image: String,
    root: Path,
    fixtures: Path,
    environment: Map[String, String] = Map.empty
) {
  import Paths.*

  /** Runs the command in the image with the repository mounted at its host path, so paths in either agree. */
  def wrap(command: Seq[String], directory: Path): Seq[String] =
    Seq(executable, "run", "--rm", "--security-opt", "label=disable") ++ ToolchainContainer.identity(executable) ++
      Seq(
        "--mount",
        s"type=bind,source=$root,target=$root",
        "--workdir",
        s"${directory.absolute}",
        "--env",
        "HOME=/tmp",
        "--env",
        s"CMAKE_BUILD_PARALLEL_LEVEL=${BuildEnvironment.threadCount}"
      ) ++ environment.toSeq.flatMap((name, value) => Seq("--env", s"$name=$value")) ++
      Seq(image) ++ command

  /** Copies a directory out of the image so host and container runs use the same pinned tools. */
  def extract(source: String, destination: Path, log: String => Unit): Unit = {
    val stamp = destination / ".image"
    if (!Files.isRegularFile(stamp) || Files.readString(stamp).trim != image) {
      IO.delete(destination.toFile)
      Files.createDirectories(destination)
      val command =
        Seq(executable, "run", "--rm", "--security-opt", "label=disable") ++ ToolchainContainer.identity(executable) ++
          Seq(
            "--mount",
            s"type=bind,source=${destination.toAbsolutePath},target=/output",
            image,
            "cp",
            "-a",
            "--no-preserve=ownership",
            s"$source/.",
            "/output/"
          )
      if (Process(command).!(ProcessLogger(log, log)) != 0)
        throw MessageOnlyException(s"cannot extract $source from $image")
      val _ = Files.writeString(stamp, image)
    }
  }
}

object ToolchainContainer {
  import Paths.*

  /** Builds the image and the memory fixtures it cannot build itself, which are passed to native as prebuilt. */
  def prepare(root: Path, executable: String, rocmVersion: String, log: String => Unit): ToolchainContainer = {
    val directory = CMake.build(
      source = root / "native/container",
      directory = root / "target/native/container",
      arguments = Seq(s"-DCONTAINER_EXECUTABLE=$executable", s"-DROCM_VERSION=$rocmVersion"),
      log = log,
      buildTargets = Seq("toolchain_image", "gemstone_memory_fixtures"),
      launcher = Seq.empty
    )
    ToolchainContainer(
      executable,
      Files.readString(directory / "toolchain-image").trim,
      root.absolute,
      directory / "rpc/memory-fixtures"
    )
  }

  // matches container_identity in ContainerBuild.cmake
  private def identity(executable: String): Seq[String] = {
    val user = s"${Process(Seq("id", "-u")).!!.trim}:${Process(Seq("id", "-g")).!!.trim}"
    if (Path.of(executable).getFileName.toString == "podman") Seq("--userns", "keep-id", "--user", user)
    else Seq("--user", user)
  }
}
