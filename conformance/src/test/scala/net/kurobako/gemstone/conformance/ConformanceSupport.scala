package net.kurobako.gemstone.conformance

import net.kurobako.gemstone.launcher.ComputeAPI
import net.kurobako.gemstone.launcher.Environment
import net.kurobako.gemstone.launcher.GemstoneProcess
import net.kurobako.gemstone.launcher.RuntimeLayout

import java.nio.file.Files
import java.nio.file.Path
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*

private[conformance] object ConformanceSupport {
  final case class AMDGPUTargetMatrix(targets: Vector[String], oracleTarget: String) {
    val targetList: String  = targets.mkString(",")
    val targetFlags: String = targets.map(target => s"--offload-arch=$target").mkString(" ")
  }

  object AMDGPUTargetMatrix {
    private val DefaultTarget = "gfx900"
    private val TargetPattern = "gfx[0-9]+[a-z]?".r

    def fromEnvironment(environment: Map[String, String]): AMDGPUTargetMatrix = {
      val rawTargets = environment.get("GEMSTONE_AMDGPU_TARGETS").filter(_.nonEmpty).getOrElse(DefaultTarget)
      val targets    = rawTargets.split(',').iterator.map(_.trim.toLowerCase).filter(_.nonEmpty).toVector
      require(targets.nonEmpty, "GEMSTONE_AMDGPU_TARGETS must contain at least one target")
      require(targets.distinct.size == targets.size, s"GEMSTONE_AMDGPU_TARGETS contains duplicates: $rawTargets")
      require(
        targets.forall(_.matches(TargetPattern.regex)),
        s"invalid GFX target in GEMSTONE_AMDGPU_TARGETS: $rawTargets"
      )
      val oracleTarget =
        environment.get(
          "GEMSTONE_AMDGPU_ORACLE_TARGET"
        ).filter(_.nonEmpty).map(_.trim.toLowerCase).getOrElse(targets.head)
      require(
        oracleTarget.matches(TargetPattern.regex),
        s"invalid GFX target in GEMSTONE_AMDGPU_ORACLE_TARGET: $oracleTarget"
      )
      require(
        targets.contains(oracleTarget),
        s"oracle target $oracleTarget is absent from GEMSTONE_AMDGPU_TARGETS"
      )
      AMDGPUTargetMatrix(targets, oracleTarget)
    }
  }

  def amdgpuTargetMatrix: AMDGPUTargetMatrix = AMDGPUTargetMatrix.fromEnvironment(TestProfile.environment)

  val root: Path   = findRoot(Path.of(sys.props("user.dir")))
  val target: Path = Files.createDirectories(root.resolve("target/conformance"))
  def runChecked(
      command: Vector[String],
      directory: Path,
      environment: Map[String, String] = Map.empty
  ): Unit = {
    val result = run(command, directory, environment)
    require(result.exit == 0, result.diagnostic)
  }

  def runtimeProvenance(layout: RuntimeLayout): Option[ujson.Obj] = layout.hipRuntime.map { library =>
    val value = ujson.Obj("id" -> layout.hipRoute.id, "hip_library" -> library.toAbsolutePath.normalize().toString)
    layout.hsaRuntime.foreach(path => value("hsa_library") = path.toAbsolutePath.normalize().toString)
    value
  }

  def maySelectPrefix(prefix: String): Boolean =
    sys.env.get("GEMSTONE_CONFORMANCE_FILTER").filter(_.nonEmpty).forall(prefixMayMatch(prefix, _))

  private[conformance] def prefixMayMatch(prefix: String, expression: String): Boolean =
    if !expression.startsWith("^") then true
    else {
      val metacharacters = Set('(', '[', '{', '.', '*', '+', '?', '|', '$', '\\')
      val literal        = expression.drop(1).takeWhile(character => !metacharacters.contains(character))
      literal.isEmpty || literal.startsWith(prefix) || prefix.startsWith(literal)
    }

  def select(cases: Vector[(Int, String)]): Vector[(Int, String)] = {
    val filtered = sys.env.get(s"GEMSTONE_CONFORMANCE_FILTER").filter(_.nonEmpty).fold(cases) { expression =>
      val pattern = expression.r
      cases.filter { case (_, name) => pattern.findFirstIn(name).nonEmpty }
    }
    filtered
  }

  final case class ProcessResult(command: Vector[String], exit: Int, stdout: String, stderr: String) {
    def diagnostic: String =
      s"command exited with $exit: ${command.mkString(" ")}\nstdout:\n$stdout\nstderr:\n$stderr"
  }

  /** Bound an individual vendor/emulator test process. Build and configure steps deliberately keep their longer
    * infrastructure timeout; this value is for the workload process itself so a deadlocked provider cannot occupy a
    * worker for the duration of a full conformance run.
    */
  val defaultTestTimeout: FiniteDuration =
    sys.env.get("GEMSTONE_CONFORMANCE_TIMEOUT_SECONDS").fold(120.seconds)(_.toLong.seconds)
  require(
    defaultTestTimeout > Duration.Zero,
    s"GEMSTONE_CONFORMANCE_TIMEOUT_SECONDS must be positive, found $defaultTestTimeout"
  )

  def run(
      command: Vector[String],
      directory: Path,
      environment: Map[String, String] = Map.empty,
      timeout: FiniteDuration = 30.minutes
  ): ProcessResult =
    runConfigured(command, directory, Environment(environment, Set.empty), timeout)

  private def runConfigured(
      command: Vector[String],
      directory: Path,
      environment: Environment,
      timeout: FiniteDuration
  ): ProcessResult = ProcessCapture.run(command, directory, environment, timeout)

  def configuredPath(
      name: String,
      directory: Boolean = false,
      environment: Map[String, String] = sys.env
  ): Option[Path] =
    environment.get(name).filter(_.nonEmpty).map { value =>
      val path = Path.of(value).toAbsolutePath.normalize
      require(
        if directory then Files.isDirectory(path) else Files.isRegularFile(path),
        s"$name does not name a valid path: $path"
      )
      path
    }

  private lazy val builtRuntimes: Map[String, Path] =
    Option(getClass.getResourceAsStream("/native-runtimes.properties")).map { stream =>
      val properties = java.util.Properties()
      try properties.load(stream)
      finally stream.close()
      properties.stringPropertyNames().asScala.iterator.map(name => name -> Path.of(properties.getProperty(name))).toMap
    }.getOrElse(Map.empty)

  def providerPath(variable: String, fileName: String): Option[Path] =
    configuredPath(variable).orElse {
      val runtime = fileName match {
        case "libcuda.so.1"          => "cuda"
        case "libamdhip64.so.7"      => "hip"
        case "libhsa-runtime64.so.1" => "hsa"
        case "libOpenCL_gemstone.so" => "opencl"
        case "libze_gemstone.so.1"   => "levelzero"
        case _                       => ""
      }
      builtRuntimes.get(runtime).map { directory =>
        val path = directory.resolve(fileName)
        require(Files.isRegularFile(path), s"sbt did not build the requested provider: $path")
        path
      }
    }

  lazy val openCLICD: Option[Path] =
    configuredPath("GEMSTONE_OPENCL_ICD", directory = true).orElse {
      providerPath("GEMSTONE_OPENCL_DRIVER", "libOpenCL_gemstone.so").map { driver =>
        val directory = Files.createDirectories(target.resolve("opencl-icd"))
        Files.writeString(directory.resolve("gemstone.icd"), driver.toAbsolutePath.toString + "\n")
        directory
      }
    }

  lazy val openCLLoader: Option[Path] =
    configuredPath("GEMSTONE_OPENCL_LOADER").orElse {
      Vector(
        Path.of("/usr/lib64/libOpenCL.so.1"),
        Path.of("/usr/lib/x86_64-linux-gnu/libOpenCL.so.1"),
        Path.of("/usr/local/cuda-11.6/targets/x86_64-linux/lib/libOpenCL.so.1")
      ).find(path => Files.isRegularFile(path))
    }

  def preferredOpenCLLoader(artifact: Path): Option[Path] =
    configuredPath("GEMSTONE_OPENCL_LOADER").orElse(findFile(
      artifact.resolve("lib"),
      "libOpenCL.so.1"
    )).orElse(openCLLoader)

  def syclAdapter(artifact: Path, opencl: Boolean): Option[Path] =
    findFile(artifact.resolve("lib"), if opencl then "libur_adapter_opencl.so" else "libur_adapter_level_zero.so")

  def findFile(directory: Path, fileName: String): Option[Path] = if !Files.isDirectory(directory) then None
  else {
    val stream = Files.walk(directory)
    try stream.iterator.asScala.find(path => Files.isRegularFile(path) && path.getFileName.toString == fileName)
    finally stream.close()
  }

  private def findRoot(start: Path): Path =
    Iterator.iterate(start.toAbsolutePath.normalize)(_.getParent)
      .takeWhile(_ != null)
      .find(path => Files.isRegularFile(path.resolve("build.sbt")) && Files.isDirectory(path.resolve("conformance")))
      .getOrElse(throw IllegalStateException(s"cannot locate Gemstone root from $start"))
}
