package net.kurobako.gemstone.conformance

import ConformanceSupport.*
import net.kurobako.gemstone.launcher.{ComputeAPI, HIPRuntimeRoute, RuntimePlatform}
import net.kurobako.gemstone.launcher.RuntimeLayout

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*

/** The four small HPC applications imported from Polyregion's mini-app lane.
  *
  * The catalog supplies the explicit integration project. Unlike the focused API suites, the vendor containers and
  * provider DSOs are environment-dependent. A selected artifact is compiled once and the exact same command is paired
  * with the vendor runtime and Gemstone's provider.
  */
private[conformance] final class MiniappCatalog {

  private[conformance] enum Workload(val id: String, val executableSuffix: String) {
    case BabelStream extends Workload("babelstream", "stream")
    case MiniBUDE    extends Workload("minibude", "bude")
    case CloverLeaf  extends Workload("cloverleaf", "cloverleaf")
    case TeaLeaf     extends Workload("tealeaf", "tealeaf")
  }

  private[conformance] enum Port(val id: String, val model: String, val api: ComputeAPI, val backend: String) {
    case CUDA      extends Port("cuda", "cuda", ComputeAPI.CUDA, "cuda")
    case HIP       extends Port("hip", "hip", ComputeAPI.HIP, "hip")
    case OpenCL    extends Port("opencl", "ocl", ComputeAPI.OpenCL, "opencl")
    case SYCL      extends Port("sycl", "sycl", ComputeAPI.SYCL, "sycl")
    case SYCLAcc   extends Port("sycl-acc", "sycl-acc", ComputeAPI.SYCL, "sycl")
    case SYCLUSM   extends Port("sycl-usm", "sycl-usm", ComputeAPI.SYCL, "sycl")
    case OMPTarget extends Port("omp-target", "omp-target", ComputeAPI.HSA, "hip")
    case Kokkos    extends Port("kokkos", "kokkos", ComputeAPI.HIP, "hip")
    case OpenACC   extends Port("openacc", "acc", ComputeAPI.CUDA, "openacc")
    case StdPar    extends Port("std-indices", "std-indices", ComputeAPI.HIP, "hip")
  }

  private[conformance] enum Provider(val id: String, val api: ComputeAPI, val syclSelector: Option[String]) {
    case CUDA          extends Provider("cuda", ComputeAPI.CUDA, None)
    case HIP           extends Provider("hip", ComputeAPI.HIP, None)
    case HIPHSA        extends Provider("hip-hsa", ComputeAPI.HIP, None)
    case HSA           extends Provider("hsa", ComputeAPI.HSA, None)
    case OpenCL        extends Provider("opencl", ComputeAPI.OpenCL, None)
    case SYCLOpenCL    extends Provider("sycl-opencl", ComputeAPI.SYCL, Some("opencl:gpu"))
    case SYCLLevelZero extends Provider("sycl-levelzero", ComputeAPI.SYCL, Some("level_zero:gpu"))

    def oracleProviderId: String = if (this == HIPHSA) "hip" else id
  }

  private[conformance] final case class App(
      workload: Workload,
      repository: String,
      commit: String,
      ports: Vector[Port]
  )

  private final case class Planned(
      app: App,
      port: Port,
      provider: Provider,
      target: Option[String],
      identity: String
  )

  private final case class Artifact(
      app: App,
      port: Port,
      root: Path,
      executable: Path
  )

  private val teaTimeout        = envInt("GEMSTONE_MINIAPP_TEA_TIMEOUT_SECONDS", 300, minimum = 1).seconds
  private[conformance] val apps = Vector(
    App(
      Workload.BabelStream,
      "https://github.com/UoB-HPC/BabelStream.git",
      "17ab377b0e919e14fd3df2b67268761fdac8abb3",
      Vector(
        Port.CUDA,
        Port.HIP,
        Port.OpenCL,
        Port.SYCL,
        Port.OMPTarget,
        Port.Kokkos,
        Port.OpenACC,
        Port.StdPar
      )
    ),
    App(
      Workload.MiniBUDE,
      "https://github.com/UoB-HPC/miniBUDE.git",
      "570f66c1fd0c29b7f3ab6fa96fba87f4561aa44c",
      Vector(
        Port.CUDA,
        Port.HIP,
        Port.OpenCL,
        Port.SYCL,
        Port.OMPTarget,
        Port.Kokkos,
        Port.OpenACC,
        Port.StdPar
      )
    ),
    App(
      Workload.CloverLeaf,
      "https://github.com/UoB-HPC/CloverLeaf.git",
      "3e10ff9268d3704a744e5a2a4ea3c62831078082",
      Vector(
        Port.CUDA,
        Port.HIP,
        Port.SYCLAcc,
        Port.SYCLUSM,
        Port.OMPTarget,
        Port.Kokkos,
        Port.OpenACC,
        Port.StdPar
      )
    ),
    App(
      Workload.TeaLeaf,
      "https://github.com/UoB-HPC/TeaLeaf.git",
      "e70261c0be40537da75b258108ed2898f84f3c58",
      Vector(
        Port.CUDA,
        Port.HIP,
        Port.SYCLAcc,
        Port.SYCLUSM,
        Port.OMPTarget,
        Port.Kokkos,
        Port.StdPar
      )
    )
  )
  private val KokkosRepository  = "https://github.com/kokkos/kokkos.git"
  private val KokkosCommit      = "62d2b6c879b74b6ae7bd06eb3e5e80139c4708e6"
  private lazy val targetMatrix = amdgpuTargetMatrix
  private lazy val selectedApps = requested("GEMSTONE_MINIAPP_APPS", apps.map(_.workload.id)).flatMap { name =>
    apps.find(_.workload.id == name).toVector
  }
  private lazy val requestedPorts = sys.env.get("GEMSTONE_MINIAPP_PORTS").filter(_.nonEmpty).map { raw =>
    raw.split(",").iterator.map(_.trim.toLowerCase).filter(_.nonEmpty).map(parsePort).toVector.distinct
  }
  private lazy val unavailableRequestedPorts = requestedPorts.toVector.flatten.toSet.filterNot(portAvailable)
  private lazy val selectedPortDefinitions   = selectedApps.flatMap { app =>
    app.ports
      .filter(port => requestedPorts.fold(true)(_.contains(port)))
      .filter(portAvailable)
      .map(port => app -> port)
  }
  private lazy val allPlanned = selectedPortDefinitions.flatMap { case (app, port) =>
    providers(port).flatMap { provider =>
      targets(provider).map { target =>
        val targetPart = target.fold("")(value => s"/$value")
        Planned(app, port, provider, target, s"${app.workload.id}/${port.id}${targetPart}/${provider.id}")
      }
    }
  }
  private lazy val planned =
    select(allPlanned.zipWithIndex.map { case (testCase, index) => index -> s"miniapps/${testCase.identity}" })
      .map { case (index, _) => allPlanned(index) }

  private val artifactRoot = sys.env.get("GEMSTONE_CONFORMANCE_ARTIFACTS").fold(target.resolve("miniapps/artifacts"))(
    value => Path.of(value).resolve("miniapps")
  )
  private var runRoot                  = target.resolve("miniapps/runs")
  private val deckRoot                 = target.resolve("miniapps/decks")
  private[conformance] val buildScript = root.resolve("conformance/miniapps/tools/container-build.sh")
  private var artifacts                = Map.empty[(String, String), Artifact]
  private var decks                    = Map.empty[Workload, Path]

  def emitCTest(matrix: CTestMatrix): Unit = {
    runRoot = matrix.directory.resolve("miniapps")
    prepareRun()
    planned.foreach { testCase =>
      try prepareCase(testCase, matrix)
      catch {
        case scala.util.control.NonFatal(error) =>
          matrix.add(
            s"miniapps/${testCase.identity}",
            testCase.provider.id,
            testCase.provider.api,
            RuntimeLayout(),
            Vector.empty,
            runRoot,
            Map.empty,
            120.0,
            buildFailure = Some(error.toString)
          )
      }
    }
  }

  private def prepareRun(): Unit = {
    require(selectedApps.nonEmpty, "GEMSTONE_MINIAPP_APPS selected no known mini-app")
    require(
      unavailableRequestedPorts.isEmpty,
      s"requested mini-app ports are unavailable in this environment: ${unavailableRequestedPorts.map(_.id).toVector.sorted.mkString(", ")}; set GEMSTONE_OPENACC_IMAGE for OpenACC"
    )
    require(selectedPortDefinitions.nonEmpty, "no requested mini-app port was selected")
    Files.createDirectories(runRoot)
    decks = Map(
      Workload.CloverLeaf -> writeCloverDeck(),
      Workload.TeaLeaf    -> writeTeaDeck()
    )

  }

  private def buildArtifacts(definitions: Vector[(App, Port)]): Map[(String, String), Artifact] = {
    definitions.foreach { case (app, _) =>
      val source = prepareSource(app)
      require(Files.isDirectory(source.resolve("src")), s"${app.workload.id} source has no src directory")
    }
    val outputs = definitions.groupBy(_._2.backend).map { case (backend, backendDefinitions) =>
      val output  = artifactRoot.resolve(backend).resolve(artifactKey(backend, backendDefinitions))
      val missing = backendDefinitions.filterNot { case (app, port) =>
        Files.isExecutable(output.resolve("bin").resolve(executableName(app, port)))
      }
      if missing.nonEmpty then {
        require(Files.isRegularFile(buildScript), s"mini-app container build script is missing: $buildScript")
        val specifications = missing.map { case (app, port) => s"${app.workload.id}:${port.model}" }
        runChecked(
          Vector("bash", buildScript.toString, backend, target.resolve("miniapps/sources").toString, output.toString) ++
            specifications,
          root,
          containerBuildEnvironment(backend)
        )
      }
      backendDefinitions.foreach { case (app, port) =>
        val executable = output.resolve("bin").resolve(executableName(app, port))
        require(Files.isExecutable(executable), s"relocated mini-app executable was not built: $executable")
      }
      backend -> output
    }
    definitions.map { case (app, port) =>
      val output = outputs(port.backend)
      (app.workload.id, port.id) ->
        Artifact(app, port, output, output.resolve("bin").resolve(executableName(app, port)))
    }.toMap
  }

  private def installDecks(): Unit = artifacts.values.toVector.distinct.foreach { artifact =>
    val destination = artifact.root.resolve("share/miniapps/decks")
    Files.createDirectories(destination)
    artifact.app.workload match {
      case Workload.CloverLeaf => Files.copy(
          decks(Workload.CloverLeaf),
          destination.resolve("cloverleaf.in"),
          java.nio.file.StandardCopyOption.REPLACE_EXISTING
        )
      case Workload.TeaLeaf => Files.copy(
          decks(Workload.TeaLeaf),
          destination.resolve("tealeaf.in"),
          java.nio.file.StandardCopyOption.REPLACE_EXISTING
        )
      case _ => ()
    }
  }

  private def executableName(app: App, port: Port): String =
    s"${app.workload.id}-${port.model}-${app.workload.executableSuffix}"

  private[conformance] def artifactKey(backend: String, definitions: Vector[(App, Port)]): String = {
    val models       = definitions.map { case (app, port) => s"${app.workload.id}-${port.model}" }.sorted.mkString("_")
    val architecture = backend match {
      case "cuda" | "openacc" => cudaArchitecture
      case "hip"              => targetMatrix.targets.mkString("-")
      case _                  => "default"
    }
    val pins        = definitions.map { case (app, port) => s"${app.commit}:${port.model}" }.sorted.mkString("\n")
    val buildInputs = Vector(
      buildScript,
      root.resolve("conformance/miniapps/tools/verify-openmp-target.py"),
      root.resolve("conformance/tools/container-common.sh"),
      root.resolve("conformance/containers/Containerfile"),
      root.resolve("conformance/tools/runtime-bundle.sh"),
      root.resolve("conformance/tools/fixture-run.py")
    ).map(Files.readString(_)).mkString("\n")
    val imageBackend = if backend == "opencl" then "HOST" else backend.toUpperCase
    val image        = sys.env.getOrElse(s"GEMSTONE_${imageBackend}_IMAGE", "pinned")
    val digest       = MessageDigest
      .getInstance("SHA-256")
      .digest(
        s"$backend\n$architecture\n$models\n$pins\n$KokkosCommit\n$image\n$buildInputs".getBytes(StandardCharsets.UTF_8)
      )
      .take(8)
      .map(byte => f"${byte & 0xff}%02x")
      .mkString
    s"$architecture-$digest"
  }

  private def containerBuildEnvironment(backend: String): Map[String, String] = {
    val architecture = backend match {
      case "cuda" | "openacc" => Map("GEMSTONE_CUDA_ARCH" -> cudaArchitecture)
      case "hip"              =>
        Map("GEMSTONE_AMDGPU_TARGETS" -> targetMatrix.targets.mkString(","))
      case _ => Map.empty
    }
    architecture
  }

  private var fixtureFailures = Map.empty[(String, String), String]

  private def prepareCase(testCase: Planned, matrix: CTestMatrix): Unit = {
    val key = testCase.app.workload.id -> testCase.port.id
    fixtureFailures.get(key).foreach(message => throw IllegalStateException(s"fixture previously failed: $message"))
    if !artifacts.contains(key) then {
      try {
        if testCase.port == Port.Kokkos then { val _ = prepareKokkos() }
        artifacts = artifacts ++ buildArtifacts(Vector(testCase.app -> testCase.port))
        installDecks()
      } catch {
        case scala.util.control.NonFatal(error) =>
          fixtureFailures = fixtureFailures.updated(key, error.toString)
          throw error
      }
    }
    val artifact = artifacts.getOrElse(
      testCase.app.workload.id -> testCase.port.id,
      throw IllegalStateException(s"mini-app artifact was not prepared: ${testCase.identity}")
    )
    val caseRoot = runRoot.resolve(testCase.identity.replace('/', File.separatorChar))
    Files.createDirectories(caseRoot)
    val nativeProvider = testCase.provider.oracleProviderId
    val nativeIdentity = (testCase.identity.split('/').toVector.dropRight(1) :+ nativeProvider).mkString("/")
    val oracleRoot     = runRoot.resolve(nativeIdentity.replace('/', File.separatorChar)).resolve("oracle")
    val gemstoneRoot   = caseRoot.resolve("gemstone")
    Files.createDirectories(oracleRoot)
    Files.createDirectories(gemstoneRoot)
    testCase.app.workload match {
      case Workload.CloverLeaf =>
        Files.deleteIfExists(oracleRoot.resolve("clover.out"))
        Files.deleteIfExists(gemstoneRoot.resolve("clover.out"))
      case Workload.TeaLeaf =>
        Files.deleteIfExists(oracleRoot.resolve("tea.out"))
        Files.deleteIfExists(gemstoneRoot.resolve("tea.out"))
      case _ => ()
    }
    if testCase.app.workload == Workload.TeaLeaf then {
      val reference = target.resolve("miniapps/sources/tealeaf/tea.problems")
      require(Files.isRegularFile(reference), s"TeaLeaf reference file is missing: $reference")
      Vector(oracleRoot, gemstoneRoot).foreach { directory =>
        val _ =
          Files.copy(reference, directory.resolve("tea.problems"), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
      }
    }
    val command = runCommand(artifact)
    val layout  = runtimeLayout(testCase.provider, artifact)
    matrix.add(
      s"miniapps/${testCase.identity}",
      testCase.provider.id,
      testCase.provider.api,
      layout,
      command,
      caseRoot,
      gemstoneEnvironment(testCase.target, testCase.port, testCase.provider, artifact),
      (if testCase.app.workload == Workload.TeaLeaf then teaTimeout else defaultTestTimeout).toSeconds.toDouble,
      oracleExtra = oracleEnvironment(testCase.app.workload, testCase.port, testCase.provider, artifact),
      validator = Some(testCase.app.workload.id),
      reportDirectories = Some(oracleRoot -> gemstoneRoot),
      oracleName = Some(s"miniapps/$nativeIdentity"),
      oracleProvider = Some(nativeProvider),
      runtimeRoute = runtimeProvenance(layout)
    )
  }

  private def runCommand(artifact: Artifact): Vector[String] = artifact.app.workload match {
    case Workload.BabelStream =>
      Vector(
        artifact.executable.toString,
        "-s",
        envInt("GEMSTONE_MINIAPP_STREAM_SIZE", 4096, minimum = 1).toString,
        "-n",
        envInt("GEMSTONE_MINIAPP_STREAM_ITERATIONS", 2, minimum = 2).toString
      ) ++ (if artifact.port == Port.SYCL then Vector("--float") else Vector.empty)
    case Workload.MiniBUDE =>
      Vector(
        artifact.executable.toString,
        "--deck",
        artifact.root.resolve("share/miniapps/minibude/data/bm1").toString,
        "-n",
        envInt("GEMSTONE_MINIAPP_BUDE_POSES", 1, minimum = 1).toString,
        "-i",
        envInt("GEMSTONE_MINIAPP_BUDE_ITERATIONS", 1, minimum = 1).toString,
        "-p",
        "1",
        "-w",
        "1"
      )
    case Workload.CloverLeaf =>
      Vector(
        artifact.executable.toString,
        "--file",
        artifact.root.resolve("share/miniapps/decks/cloverleaf.in").toString,
        "--out",
        "clover.out"
      )
    case Workload.TeaLeaf =>
      Vector(
        artifact.executable.toString,
        "--file",
        artifact.root.resolve("share/miniapps/decks/tealeaf.in").toString,
        "--out",
        "tea.out"
      )
  }

  private def runtimeLayout(provider: Provider, artifact: Artifact): RuntimeLayout =
    provider match {
      case Provider.CUDA => RuntimeLayout(cudaDriver = Some(providerPath("GEMSTONE_CUDA_DRIVER", "libcuda.so.1").get))
      case Provider.HIP  => RuntimeLayout(
          hipRuntime = Some(providerPath("GEMSTONE_HIP_RUNTIME", "libamdhip64.so.7").get)
        )
      case Provider.HIPHSA =>
        val vendor = artifact.root.resolve("lib/libamdhip64.so.7")
        require(Files.isRegularFile(vendor), s"the pinned artifact has no vendor HIP runtime: $vendor")
        RuntimeLayout(
          hipRuntime = Some(vendor),
          hsaRuntime = Some(providerPath("GEMSTONE_HSA_RUNTIME", "libhsa-runtime64.so.1").get),
          hipRoute = HIPRuntimeRoute.VendorHIPOnHSA
        )
      case Provider.HSA => RuntimeLayout(
          hsaRuntime = Some(providerPath("GEMSTONE_HSA_RUNTIME", "libhsa-runtime64.so.1").get)
        )
      case Provider.OpenCL | Provider.SYCLOpenCL => RuntimeLayout(
          openCLICD = Some(openCLICD.get),
          openCLLoader = Some(preferredOpenCLLoader(artifact.root).get)
        )
      case Provider.SYCLLevelZero =>
        RuntimeLayout(levelZeroDriver = Some(providerPath("GEMSTONE_LEVELZERO_DRIVER", "libze_gemstone.so.1").get))
    }

  private def oracleEnvironment(
      workload: Workload,
      port: Port,
      provider: Provider,
      artifact: Artifact
  ): Map[String, String] =
    artifactEnvironment(artifact) ++ executionEnvironment(port) ++
      provider.syclSelector.map(selector => "ONEAPI_DEVICE_SELECTOR" -> selector) ++
      syclAdapter(provider, artifact).map(path => "UR_ADAPTERS_FORCE_LOAD" -> path.toString)

  private def gemstoneEnvironment(
      target: Option[String],
      port: Port,
      provider: Provider,
      artifact: Artifact
  ): Map[String, String] =
    artifactEnvironment(artifact) ++ executionEnvironment(port) ++ target.map("GEMSTONE_AMDGPU_TARGET" -> _).toMap ++
      syclAdapter(provider, artifact).map(path => "UR_ADAPTERS_FORCE_LOAD" -> path.toString)

  private[conformance] def executionEnvironment(port: Port): Map[String, String] = port match {
    case Port.Kokkos    => Map("HSA_XNACK" -> "1")
    case Port.OMPTarget => Map("OMP_TARGET_OFFLOAD" -> "MANDATORY")
    case _              => Map.empty
  }

  private def artifactEnvironment(artifact: Artifact): Map[String, String] =
    Some(artifact.root.resolve("lib")).filter(path => Files.isDirectory(path)).map(path =>
      "LD_LIBRARY_PATH" -> path.toString
    ).toMap

  private def syclAdapter(provider: Provider, artifact: Artifact): Option[Path] = provider match {
    case Provider.SYCLOpenCL    => ConformanceSupport.syclAdapter(artifact.root, opencl = true)
    case Provider.SYCLLevelZero => ConformanceSupport.syclAdapter(artifact.root, opencl = false)
    case _                      => None
  }

  private[conformance] def supportedProviders(port: Port, platform: RuntimePlatform): Vector[Provider] = port match {
    case Port.HIP | Port.Kokkos | Port.StdPar => HIPRuntimeRoute.supported(platform).toVector.map {
        case HIPRuntimeRoute.GemstoneHIP    => Provider.HIP
        case HIPRuntimeRoute.VendorHIPOnHSA => Provider.HIPHSA
      }
    case Port.OMPTarget                          => Vector(Provider.HSA)
    case Port.CUDA | Port.OpenACC                => Vector(Provider.CUDA)
    case Port.OpenCL                             => Vector(Provider.OpenCL)
    case Port.SYCL | Port.SYCLAcc | Port.SYCLUSM => Vector(Provider.SYCLOpenCL, Provider.SYCLLevelZero)
  }

  private def providers(port: Port): Vector[Provider] = supportedProviders(port, RuntimePlatform.current).filter {
    case Provider.CUDA                         => providerPath("GEMSTONE_CUDA_DRIVER", "libcuda.so.1").nonEmpty
    case Provider.HIP                          => providerPath("GEMSTONE_HIP_RUNTIME", "libamdhip64.so.7").nonEmpty
    case Provider.HSA | Provider.HIPHSA        => providerPath("GEMSTONE_HSA_RUNTIME", "libhsa-runtime64.so.1").nonEmpty
    case Provider.OpenCL | Provider.SYCLOpenCL => openCLICD.nonEmpty
    case Provider.SYCLLevelZero => providerPath("GEMSTONE_LEVELZERO_DRIVER", "libze_gemstone.so.1").nonEmpty
  }

  private[conformance] def targets(provider: Provider): Vector[Option[String]] = provider match {
    case Provider.HIP | Provider.HIPHSA | Provider.HSA => targetMatrix.targets.map(Some(_))
    case _                                             => Vector(None)
  }

  private def portAvailable(port: Port): Boolean = port match {
    case Port.OpenACC => sys.env.get("GEMSTONE_OPENACC_IMAGE").exists(_.nonEmpty)
    case _            => true
  }

  private def prepareSource(app: App): Path = {
    val source = target.resolve("miniapps/sources").resolve(app.workload.id)
    val marker = source.resolve(".gemstone-commit")
    if !Files.isRegularFile(marker) || Files.readString(marker).trim != app.commit then {
      deleteOwned(source)
      Files.createDirectories(source.getParent)
      runChecked(Vector("git", "init", "-q", source.toString), root)
      runChecked(Vector("git", "-C", source.toString, "remote", "add", "origin", app.repository), root)
      runChecked(Vector("git", "-C", source.toString, "fetch", "-q", "--depth", "1", "origin", app.commit), root)
      runChecked(Vector("git", "-C", source.toString, "checkout", "-q", "--detach", app.commit), root)
      val _ = Files.writeString(marker, app.commit + "\n", StandardCharsets.UTF_8)
    }
    source
  }

  private def prepareKokkos(): Path = {
    val source = target.resolve("miniapps/sources/kokkos")
    val marker = source.resolve(".gemstone-commit")
    if !Files.isRegularFile(marker) || Files.readString(marker).trim != KokkosCommit then {
      deleteOwned(source)
      Files.createDirectories(source.getParent)
      runChecked(Vector("git", "init", "-q", source.toString), root)
      runChecked(Vector("git", "-C", source.toString, "remote", "add", "origin", KokkosRepository), root)
      runChecked(
        Vector("git", "-C", source.toString, "fetch", "-q", "--depth", "1", "origin", KokkosCommit),
        root
      )
      runChecked(Vector("git", "-C", source.toString, "checkout", "-q", "--detach", KokkosCommit), root)
      val _ = Files.writeString(marker, KokkosCommit + "\n", StandardCharsets.UTF_8)
    }
    source
  }

  private def writeCloverDeck(): Path = {
    val deck = deckRoot.resolve("clover-32x32.in")
    writeIfChanged(
      deck,
      """*clover
 state 1 density=0.2 energy=1.0
 state 2 density=1.0 energy=2.5 geometry=rectangle xmin=0.0 xmax=5.0 ymin=0.0 ymax=2.0
 x_cells=32
 y_cells=32
 xmin=0.0
 ymin=0.0
 xmax=10.0
 ymax=10.0
 initial_timestep=0.04
 max_timestep=0.04
 end_step=2
*endclover
"""
    )
    deck
  }

  private def writeTeaDeck(): Path = {
    val deck = deckRoot.resolve("tea-64x64.in")
    writeIfChanged(
      deck,
      s"""*tea
state 1 density=100.0 energy=0.0001
state 2 density=0.1 energy=25.0 geometry=rectangle xmin=0.0 xmax=1.0 ymin=1.0 ymax=2.0
state 3 density=0.1 energy=0.1 geometry=rectangle xmin=1.0 xmax=6.0 ymin=1.0 ymax=2.0
state 4 density=0.1 energy=0.1 geometry=rectangle xmin=5.0 xmax=6.0 ymin=1.0 ymax=8.0
state 5 density=0.1 energy=0.1 geometry=rectangle xmin=5.0 xmax=10.0 ymin=7.0 ymax=8.0
x_cells=64
y_cells=64
xmin=0.0
ymin=0.0
xmax=10.0
ymax=10.0
initial_timestep=0.004
end_step=1
max_iters=${envInt("GEMSTONE_MINIAPP_TEA_MAX_ITERS", 1000, minimum = 1)}
use_cg
eps ${envDouble("GEMSTONE_MINIAPP_TEA_EPS", 1.0e-8, minimum = 0.0)}
use_c_kernels
*endtea
"""
    )
    deck
  }

  private def writeIfChanged(path: Path, value: String): Unit = {
    Files.createDirectories(path.getParent)
    if !Files.isRegularFile(path) || Files.readString(path) != value then {
      val _ = Files.writeString(path, value, StandardCharsets.UTF_8)
    }
  }

  private def requested(name: String, defaults: Vector[String]): Vector[String] =
    sys.env.get(
      name
    ).filter(_.nonEmpty).fold(defaults)(_.split(",").iterator.map(_.trim.toLowerCase).filter(_.nonEmpty).toVector)

  private def parsePort(value: String): Port = value match {
    case "cuda"                                           => Port.CUDA
    case "hip"                                            => Port.HIP
    case "ocl" | "opencl"                                 => Port.OpenCL
    case "sycl"                                           => Port.SYCL
    case "sycl-acc" | "sycl_acc"                          => Port.SYCLAcc
    case "sycl-usm" | "sycl_usm"                          => Port.SYCLUSM
    case "omp-target" | "omp_target" | "omptarget"        => Port.OMPTarget
    case "kokkos"                                         => Port.Kokkos
    case "openacc" | "acc"                                => Port.OpenACC
    case "std" | "stdpar" | "std-indices" | "std_indices" => Port.StdPar
    case other => throw IllegalArgumentException(s"unknown GEMSTONE_MINIAPP_PORTS entry: $other")
  }

  private def deleteOwned(path: Path): Unit = if Files.exists(path) then {
    val stream = Files.walk(path)
    try stream.sorted(java.util.Comparator.reverseOrder()).iterator.asScala.foreach(Files.delete)
    finally stream.close()
  }

  private def cudaArchitecture: String = {
    val value = TestProfile.environment.get("GEMSTONE_CUDA_ARCH").filter(_.nonEmpty).getOrElse("sm_75")
    if value.startsWith("sm_") then value else s"sm_$value"
  }

  private def envInt(name: String, default: Int, minimum: Int): Int = {
    val value = sys.env.get(name).filter(_.nonEmpty).fold(default)(_.toInt)
    require(value >= minimum, s"$name must be at least $minimum, found $value")
    value
  }

  private def envDouble(name: String, default: Double, minimum: Double): Double = {
    val value = sys.env.get(name).filter(_.nonEmpty).fold(default)(_.toDouble)
    require(
      java.lang.Double.isFinite(value) && value >= minimum,
      s"$name must be finite and at least $minimum, found $value"
    )
    value
  }

}
