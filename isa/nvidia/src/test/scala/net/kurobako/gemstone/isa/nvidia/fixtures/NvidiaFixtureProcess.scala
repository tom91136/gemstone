package net.kurobako.gemstone.isa.nvidia.fixtures

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*

object NvidiaFixtureProcess {
  private def compilerEnvironment(ptxas: String): (String, String) = ptxas match {
    case "11.8.89" => env.Cuda11889 -> env.Cuda11889Image
    case "13.3.33" => env.Cuda13333 -> env.Cuda13333Image
    case "13.3.73" => env.Cuda13373 -> env.Cuda13373Image
    case other     => throw IllegalArgumentException(s"Unregistered CUDA fixture version: $other")
  }

  private def disassemblerEnvironment(nvdisasm: String): String = nvdisasm match {
    case "11.8.86" => env.Nvdisasm11886
    case "13.2.78" => env.Nvdisasm13278
    case "13.3.73" => env.Nvdisasm13373
    case other     => throw IllegalArgumentException(s"Unregistered nvdisasm fixture version: $other")
  }

  private def quote(value: String): String = "'" + value.replace("'", "'\"'\"'") + "'"

  def run(
      tools: (ptxas: String, nvdisasm: Option[String]),
      script: Vec[String],
      directory: Path,
      log: Path
  ): Unit = {
    val (key, imageKey) = compilerEnvironment(tools.ptxas)
    val toolkit         = sys.env.get(key).map(value => Path.of(value).toAbsolutePath)
    val image           = sys.env.get(imageKey)
    val disassembler    = tools.nvdisasm.flatMap(version =>
      sys.env.get(disassemblerEnvironment(version)).map(value => Path.of(value).toAbsolutePath)
    )
    require(toolkit.isEmpty || image.isEmpty, s"Set either $key or $imageKey, not both")

    val compilerWrappers = toolkit.fold("") { root =>
      Vec("nvcc", "ptxas").map { tool =>
        s"$tool() { ${quote(root.resolve("bin").resolve(tool).toString)} \"$$@\"; }"
      }.mkString("\n")
    }
    val disassemblerWrapper = disassembler.fold("") { path =>
      val executable = if (image.isDefined) "/fixture-nvdisasm" else path.toString
      s"nvdisasm() { ${quote(executable)} \"$$@\"; }"
    }
    val preflight = """set -euo pipefail
      |require_version() {
      |  local actual
      |  actual=$("$1" --version)
      |  if [[ "$actual" != *"V$2"$'\n'* && "$actual" != *"V$2" ]]; then
      |    printf 'Expected %s version %s, got:\n%s\n' "$1" "$2" "$actual" >&2
      |    exit 1
      |  fi
      |}
      |""".stripMargin
    val commonChecks = s"require_version ptxas ${quote(tools.ptxas)}\n" +
      s"require_version nvcc ${quote(tools.ptxas)}\n"
    val disassemblerCheck = tools.nvdisasm.fold("")(version =>
      s"require_version nvdisasm ${quote(version)}\n"
    )
    val scriptPath = Files.createTempFile(directory, "recipe-", ".sh")
    val _          = Files.writeString(
      scriptPath,
      preflight + compilerWrappers + "\n" + disassemblerWrapper + "\n" + commonChecks + disassemblerCheck +
        script.mkString("\n") + "\n"
    )
    val command = image.fold(Vec("bash", scriptPath.getFileName.toString)) { name =>
      val mounts = disassembler.fold(Vec.empty[String])(path =>
        Vec("--volume", s"$path:/fixture-nvdisasm:ro")
      )
      Vec(
        "podman",
        "run",
        "--rm",
        "--pull=never",
        "--network=none",
        "--timeout=90",
        "--security-opt",
        "label=disable",
        "--volume",
        s"$directory:/work:Z",
        "--workdir",
        "/work"
      ) ++ mounts ++ Vec(name, "bash", scriptPath.getFileName.toString)
    }
    val result = process.run(command, directory, Map.empty, Set.empty, 100.seconds)
    val _      = Files.writeString(log, result.diagnostic, StandardOpenOption.CREATE, StandardOpenOption.APPEND)
    require(result.exit == 0, s"NVIDIA fixture recipe exited with status ${result.exit}")
  }
}
