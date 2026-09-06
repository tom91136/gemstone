package net.kurobako.gemstone.conformance

import ConformanceSupport.*
import net.kurobako.gemstone.launcher.ComputeAPI
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
  * The suite intentionally remains opt-in. Unlike the focused API suites, the vendor containers and provider DSOs are
  * environment-dependent. A selected artifact is compiled once and the exact same command is paired with the vendor
  * runtime and Gemstone's provider.
  */
final class MiniappsConformanceSuite extends munit.FunSuite {
  override val munitTimeout: Duration = 30.minutes

  private enum Workload(val id: String, val executableSuffix: String) {
    case BabelStream extends Workload("babelstream", "stream")
    case MiniBUDE    extends Workload("minibude", "bude")
    case CloverLeaf  extends Workload("cloverleaf", "cloverleaf")
    case TeaLeaf     extends Workload("tealeaf", "tealeaf")
  }

  private enum Port(val id: String, val model: String, val api: ComputeAPI, val backend: String) {
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

  private enum Provider(val id: String, val api: ComputeAPI, val syclSelector: Option[String]) {
    case CUDA          extends Provider("cuda", ComputeAPI.CUDA, None)
    case HIP           extends Provider("hip", ComputeAPI.HIP, None)
    case HSA           extends Provider("hsa", ComputeAPI.HSA, None)
    case OpenCL        extends Provider("opencl", ComputeAPI.OpenCL, None)
    case SYCLOpenCL    extends Provider("sycl-opencl", ComputeAPI.SYCL, Some("opencl:gpu"))
    case SYCLLevelZero extends Provider("sycl-levelzero", ComputeAPI.SYCL, Some("level_zero:gpu"))
  }

  private final case class App(
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

  private val enabled    = truthy(sys.env.get("GEMSTONE_MINIAPPS"))
  private val oracleOnly = ConformanceSupport.oracleOnly
  private val teaTimeout = envInt("GEMSTONE_MINIAPP_TEA_TIMEOUT_SECONDS", 300, minimum = 1).seconds
  private val apps       = Vector(
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
  private val KokkosRepository = "https://github.com/kokkos/kokkos.git"
  private val KokkosCommit     = "62d2b6c879b74b6ae7bd06eb3e5e80139c4708e6"
  private val targetMatrix = if enabled then Some(amdgpuTargetMatrix) else None
  private val selectedApps = requested("GEMSTONE_MINIAPP_APPS", apps.map(_.workload.id)).flatMap { name =>
    apps.find(_.workload.id == name).toVector
  }
  private val requestedPorts = sys.env.get("GEMSTONE_MINIAPP_PORTS").filter(_.nonEmpty).map { raw =>
    raw.split(",").iterator.map(_.trim.toLowerCase).filter(_.nonEmpty).map(parsePort).toVector.distinct
  }
  private val unavailableRequestedPorts = requestedPorts.toVector.flatten.toSet.filterNot(portAvailable)
  private val selectedPortDefinitions   = selectedApps.flatMap { app =>
    app.ports
      .filter(port => requestedPorts.fold(true)(_.contains(port)))
      .filter(portAvailable)
      .map(port => app -> port)
  }
  private val allPlanned = selectedPortDefinitions.flatMap { case (app, port) =>
    providers(port).flatMap { provider =>
      targets(provider).map { target =>
        val targetPart = target.fold("")(value => s"/$value")
        Planned(app, port, provider, target, s"${app.workload.id}/${port.id}${targetPart}/${provider.id}")
      }
    }
  }
  private val planned =
    if enabled then
      select(allPlanned.zipWithIndex.map { case (testCase, index) => index -> testCase.identity }, "MINIAPP")
        .map { case (index, _) => allPlanned(index) }
    else Vector.empty

  private val artifactRoot = target.resolve("miniapps/artifacts")
  private val runRoot      = target.resolve("miniapps/runs")
  private val deckRoot     = target.resolve("miniapps/decks")
  private val buildScript  = root.resolve("conformance/miniapps/tools/container-build.sh")
  private var artifacts    = Map.empty[(String, String), Artifact]
  private var decks        = Map.empty[Workload, Path]

  if !enabled then
    test("miniapps/opt-in") {
      ()
    }
  else
    planned.foreach { testCase =>
      test(s"miniapps/${testCase.identity}") {
        runCase(testCase)
      }
    }

  test("miniapps/matrix-model-aliases") {
    require(sourceModel(Workload.BabelStream, Port.StdPar) == "std")
    require(sourceModel(Workload.BabelStream, Port.OMPTarget) == "omp")
    require(sourceModel(Workload.MiniBUDE, Port.OMPTarget) == "omp")
    require(sourceModel(Workload.CloverLeaf, Port.OMPTarget) == "omp-target")
    require(sourceModel(Workload.TeaLeaf, Port.OMPTarget) == "omp-target")
    require(sourceModel(Workload.MiniBUDE, Port.StdPar) == "std-indices")
    require(sourceModel(Workload.TeaLeaf, Port.Kokkos) == "kokkos")
  }

  test("miniapps/matrix-definitions") {
    val expected = Map(
      Workload.BabelStream -> Set("cuda", "hip", "opencl", "sycl", "omp-target", "kokkos", "openacc", "std-indices"),
      Workload.MiniBUDE    -> Set("cuda", "hip", "opencl", "sycl", "omp-target", "kokkos", "openacc", "std-indices"),
      Workload.CloverLeaf  -> Set("cuda", "hip", "sycl-acc", "sycl-usm", "omp-target", "kokkos", "openacc", "std-indices"),
      Workload.TeaLeaf     -> Set("cuda", "hip", "sycl-acc", "sycl-usm", "omp-target", "kokkos", "std-indices")
    )
    require(apps.forall(app => app.ports.map(_.id).toSet == expected(app.workload)))
    require(apps.flatMap(_.ports).count(_ == Port.OpenACC) == 3)
    require(apps.flatMap(_.ports).count(_ == Port.StdPar) == 4)
    require(apps.flatMap(_.ports).count(_ == Port.OMPTarget) == 4)
    require(apps.flatMap(_.ports).count(_ == Port.Kokkos) == 4)
  }

  test("miniapps/artifact-keys-are-portable") {
    val hipDefinitions = apps.flatMap(app => app.ports.filter(_.backend == "hip").map(app -> _)).distinct
    val allKey         = artifactKey("hip", hipDefinitions)
    val reducedKey     = artifactKey("hip", hipDefinitions.take(1))
    require(allKey.length < 100)
    require(reducedKey.length < 100)
    require(allKey != reducedKey)
  }

  test("miniapps/container-build-keeps-model-compilers-isolated") {
    val script = Files.readString(buildScript, StandardCharsets.UTF_8)
    require(script.contains("model_host_cxx=\"$host_cxx\""))
    require(script.contains("model_host_cxx=/usr/bin/amdclang++"))
    require(!script.linesIterator.exists(_.trim == "host_cxx=/usr/bin/amdclang++"))
    require(script.contains("build_one \"$specification\""))
    require(!script.contains("build_one \"$specification\" &"))
    require(script.contains("default_build_jobs=\"$(nproc)\""))
    require(script.contains("-G Ninja"))
    require(script.contains("cmake --build \"$build\" --parallel \"$build_jobs\""))
    require(script.contains("GEMSTONE_MINIAPP_BUILD_JOBS"))
    require(script.contains("kokkos_source=\"/work/source/kokkos/$app-$model\""))
  }

  test("miniapps/hip-models-use-safe-interpreter-profile") {
    require(
      executionEnvironment(Port.Kokkos).get("GEMSTONE_EXECUTION_PARALLELISM").contains("1"),
      "Kokkos mini-apps retain serial interpreter dispatch until their cross-kernel ordering is validated"
    )
    require(
      executionEnvironment(Port.StdPar).get("GEMSTONE_EXECUTION_PARALLELISM").contains("1"),
      "StdPar mini-apps retain serial interpreter dispatch until their cross-kernel ordering is validated"
    )
  }

  override def beforeAll(): Unit = if enabled then {
    require(selectedApps.nonEmpty, "GEMSTONE_MINIAPP_APPS selected no known mini-app")
    require(
      unavailableRequestedPorts.isEmpty,
      s"requested mini-app ports are unavailable in this environment: ${unavailableRequestedPorts.map(_.id).toVector.sorted.mkString(", ")}; set GEMSTONE_MINIAPP_OPENACC_IMAGE for OpenACC"
    )
    require(selectedPortDefinitions.nonEmpty, "no requested mini-app port was selected")
    require(planned.nonEmpty, "no mini-app cases remain after provider discovery/filtering")
    require(
      sys.env.get("GEMSTONE_MINIAPP_JOBS").fold(1)(_.toInt) == 1,
      "GEMSTONE_MINIAPP_JOBS is currently limited to 1 to keep provider runs isolated"
    )
    Files.createDirectories(runRoot)
    decks = Map(
      Workload.CloverLeaf -> writeCloverDeck(),
      Workload.TeaLeaf    -> writeTeaDeck()
    )
    if selectedPortDefinitions.exists(_._2 == Port.Kokkos) then {
      val _ = prepareKokkos()
    }
    artifacts = buildArtifacts(planned.map(testCase => testCase.app -> testCase.port).distinct)
    installDecks()
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

  private def sourceModel(workload: Workload, port: Port): String = (workload, port) match {
    case (Workload.BabelStream, Port.StdPar)    => "std"
    case (Workload.BabelStream, Port.OMPTarget) => "omp"
    case (Workload.MiniBUDE, Port.OMPTarget)    => "omp"
    case (_, Port.OMPTarget)                   => "omp-target"
    case (_, Port.StdPar)                      => "std-indices"
    case (_, Port.OpenACC)                    => "acc"
    case (_, other)                            => other.model
  }

  private def artifactKey(backend: String, definitions: Vector[(App, Port)]): String = {
    val models       = definitions.map { case (app, port) => s"${app.workload.id}-${port.model}" }.sorted.mkString("_")
    val architecture = backend match {
      case "cuda" | "openacc" => cudaArchitecture
      case "hip"               => targetMatrix.map(_.targets.mkString("-")).getOrElse("default")
      case _                    => "default"
    }
    val digest = MessageDigest
      .getInstance("SHA-256")
      .digest(s"$backend\n$architecture\n$models".getBytes(StandardCharsets.UTF_8))
      .take(8)
      .map(byte => f"${byte & 0xff}%02x")
      .mkString
    val legacy = s"$architecture-$models"
    if legacy.length <= 200 then legacy else s"$architecture-$digest"
  }

  private def containerBuildEnvironment(backend: String): Map[String, String] = {
    val architecture = backend match {
      case "cuda" | "openacc" => Map("GEMSTONE_MINIAPP_CUDA_ARCH" -> cudaArchitecture)
      case "hip"               =>
        Map("GEMSTONE_MINIAPP_HIP_ARCHITECTURES" -> targetMatrix.map(_.targets.mkString(",")).getOrElse("gfx900"))
      case _                    => Map.empty
    }
    architecture
  }

  private def runCase(testCase: Planned): Unit = {
    val artifact = artifacts.getOrElse(
      testCase.app.workload.id -> testCase.port.id,
      fail(s"mini-app artifact was not prepared: ${testCase.identity}")
    )
    val caseRoot = runRoot.resolve(testCase.identity.replace('/', File.separatorChar))
    Files.createDirectories(caseRoot)
    val oracleRoot   = caseRoot.resolve("oracle")
    val gemstoneRoot = caseRoot.resolve("gemstone")
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
      val _ = Files.copy(
        reference,
        caseRoot.resolve("tea.problems"),
        java.nio.file.StandardCopyOption.REPLACE_EXISTING
      )
    }
    val command       = runCommand(artifact, gemstoneRoot)
    val oracleCommand = runCommand(artifact, oracleRoot)
    val paired        = runPaired(
      testCase.provider.api,
      runtimeLayout(testCase.provider, artifact),
      command,
      caseRoot,
      oracleEnvironment(testCase.app.workload, testCase.port, testCase.provider, artifact),
      gemstoneEnvironment(testCase.target, testCase.port, testCase.provider, artifact),
      timeout = testCase.app.workload match {
        case Workload.TeaLeaf => teaTimeout
        case _                => defaultTestTimeout
      },
      oracleCommand = Some(oracleCommand)
    )
    if paired.oracleRan then {
      require(paired.oracle.exit == 0, s"oracle/${testCase.identity}:\n${paired.oracle.diagnostic}")
      validateOutput(testCase.app.workload, paired.oracle.stdout, oracleRoot, "oracle/" + testCase.identity)
    }
    if paired.gemstoneRan then {
      require(paired.gemstone.exit == 0, s"gemstone/${testCase.identity}:\n${paired.gemstone.diagnostic}")
      validateOutput(
        testCase.app.workload,
        paired.gemstone.stdout,
        gemstoneRoot,
        "gemstone/" + testCase.identity,
        if paired.oracleRan then Some(readReportFor(testCase.app.workload, oracleRoot, "oracle/" + testCase.identity))
        else None
      )
    }
  }

  private def runCommand(artifact: Artifact, caseRoot: Path): Vector[String] = artifact.app.workload match {
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
        caseRoot.resolve("clover.out").toString
      )
    case Workload.TeaLeaf =>
      Vector(
        artifact.executable.toString,
        "--file",
        artifact.root.resolve("share/miniapps/decks/tealeaf.in").toString,
        "--out",
        caseRoot.resolve("tea.out").toString
      )
  }

  private val Number         = "[+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][+-]?\\d+)?"
  private val CloverPressure = "(?:" + Number + "|[+-]?(?:nan|inf(?:inity)?))"
  private val CloverFinal    =
    ("(?m)^\\s*step:\\s*2\\s+(" + Number + ")\\s+(" + Number + ")\\s+(" + Number +
      ")\\s+(" + CloverPressure + ")\\s+(" + Number + ")\\s+(" + Number + ")\\s+(" + Number + ")\\s*$").r
  // The kinetic energy is accumulated through GPU atomics, so a provider may
  // legally expose a different reduction order from the native oracle. Keep
  // this tolerance tight enough to reject a materially different solution.
  private val CloverKineticTolerance = 2.0e-5
  private val TeaExpected            = ("(?m)^\\s*Expected\\s+(" + Number + ")\\s*$").r
  private val TeaActual              = ("(?m)^\\s*Actual\\s+(" + Number + ")\\s*$").r

  private def validateOutput(
      workload: Workload,
      stdout: String,
      caseRoot: Path,
      identity: String,
      oracleReport: Option[String] = None
  ): Unit = {
    val report = readReportFor(workload, caseRoot, identity)
    val output = s"$stdout\n$report"
    workload match {
      case Workload.BabelStream =>
        require(!output.contains("FAILED validation"), s"$identity reported failed validation")
      case Workload.MiniBUDE =>
        require(output.contains("valid: true"), s"$identity did not report valid: true")
      case Workload.CloverLeaf =>
        require(output.contains("- Outcome: PASSED"), s"$identity did not report PASSED")
        CloverFinal.findFirstMatchIn(report) match {
          case Some(values) =>
            val actual = (1 to 7).map(index => if index == 4 then Double.NaN else values.group(index).toDouble)
            actual.zip(Vector(100.0, 28.75, 0.2875, Double.NaN, 45.06096, 0.09022083, 45.15118)).zipWithIndex.foreach {
              case ((value, expected), index) =>
                if index != 3 then {
                  require(java.lang.Double.isFinite(value), s"$identity final field ${index + 1} is not finite")
                  requireNear(
                    s"$identity final field ${index + 1}",
                    value,
                    expected,
                    relativeTolerance = if index == 5 then CloverKineticTolerance else 1.0e-5
                  )
                }
            }
          case None =>
            throw IllegalArgumentException(
              s"$identity has no finite final CloverLeaf summary in ${caseRoot.resolve("clover.out")}"
            )
        }
      case Workload.TeaLeaf =>
        require(output.contains("- Problem: 64x64@1"), s"$identity did not run the reference TeaLeaf case")
        require(output.contains("- Outcome: PASSED"), s"$identity did not report PASSED")
        require(!output.contains("expected value will be invalid"), s"$identity used an invalid TeaLeaf reference")
        val expected = TeaExpected.findFirstMatchIn(report).map(_.group(1).toDouble).getOrElse {
          throw IllegalArgumentException(s"$identity has no TeaLeaf expected value in ${caseRoot.resolve("tea.out")}")
        }
        val actual = TeaActual.findFirstMatchIn(report).map(_.group(1).toDouble).getOrElse {
          throw IllegalArgumentException(s"$identity has no TeaLeaf actual value in ${caseRoot.resolve("tea.out")}")
        }
        requireNear(s"$identity TeaLeaf result", actual, expected, relativeTolerance = 1.0e-7)
        oracleReport.foreach { oracle =>
          val oracleActual = TeaActual.findFirstMatchIn(oracle).map(_.group(1).toDouble).getOrElse {
            throw IllegalArgumentException(s"$identity oracle has no TeaLeaf actual value")
          }
          requireNear(s"$identity TeaLeaf oracle match", actual, oracleActual, relativeTolerance = 1.0e-7)
        }
    }
  }

  private def readReportFor(workload: Workload, caseRoot: Path, identity: String): String = workload match {
    case Workload.CloverLeaf => readReport(caseRoot.resolve("clover.out"), identity)
    case Workload.TeaLeaf    => readReport(caseRoot.resolve("tea.out"), identity)
    case _                   => ""
  }

  private def readReport(path: Path, identity: String): String = {
    require(Files.isRegularFile(path), s"$identity did not produce report $path")
    Files.readString(path, StandardCharsets.UTF_8)
  }

  private def requireNear(name: String, actual: Double, expected: Double, relativeTolerance: Double = 1.0e-5): Unit = {
    require(java.lang.Double.isFinite(actual), s"$name is not finite: $actual")
    val tolerance = relativeTolerance * math.max(1.0, math.abs(expected))
    require(math.abs(actual - expected) <= tolerance, s"$name expected $expected, got $actual")
  }

  private def runtimeLayout(provider: Provider, artifact: Artifact): RuntimeLayout =
    if oracleOnly then RuntimeLayout()
    else
      provider match {
        case Provider.CUDA => RuntimeLayout(cudaDriver = Some(providerPath("GEMSTONE_CUDA_DRIVER", "libcuda.so.1").get))
        case Provider.HIP  => RuntimeLayout(
            hipRuntime = Some(providerPath("GEMSTONE_HIP_RUNTIME", "libamdhip64.so.7").get),
            hsaRuntime = providerPath("GEMSTONE_HSA_RUNTIME", "libhsa-runtime64.so.1")
          )
        case Provider.HSA => RuntimeLayout(
            hsaRuntime = Some(providerPath("GEMSTONE_HSA_RUNTIME", "libhsa-runtime64.so.1").get)
          )
        case Provider.OpenCL | Provider.SYCLOpenCL => RuntimeLayout(
            openCLICD = Some(openCLICD.get),
            openCLLoader = Some(artifactLibrary(artifact, "libOpenCL.so.1").orElse(openCLLoader).get)
          )
        case Provider.SYCLLevelZero =>
          RuntimeLayout(levelZeroDriver = Some(providerPath("GEMSTONE_LEVELZERO_DRIVER", "libze_gemstone.so.1").get))
      }

  private def oracleEnvironment(
      workload: Workload,
      port: Port,
      provider: Provider,
      artifact: Artifact
  ): Map[String, String] = {
    val fp64CpuFallback = provider match {
      case Provider.SYCLOpenCL | Provider.SYCLLevelZero =>
        workload == Workload.CloverLeaf || workload == Workload.TeaLeaf
      case _ => false
    }
    val oracleProvider = if fp64CpuFallback then Provider.SYCLOpenCL else provider
    artifactEnvironment(artifact) ++
      executionEnvironment(port) ++
      (if fp64CpuFallback then Some("opencl:cpu") else oracleProvider.syclSelector)
        .map(selector => "ONEAPI_DEVICE_SELECTOR" -> selector) ++
      syclAdapter(oracleProvider, artifact).map(path => "UR_ADAPTERS_FORCE_LOAD" -> path.toString)
  }

  private def gemstoneEnvironment(
      target: Option[String],
      port: Port,
      provider: Provider,
      artifact: Artifact
  ): Map[String, String] =
    artifactEnvironment(artifact) ++ executionEnvironment(port) ++ target.map("GEMSTONE_AMDGPU_TARGET" -> _).toMap ++
      syclAdapter(provider, artifact).map(path => "UR_ADAPTERS_FORCE_LOAD" -> path.toString)

  private def executionEnvironment(port: Port): Map[String, String] = port match {
    case Port.Kokkos => Map("HSA_XNACK" -> "1", "GEMSTONE_EXECUTION_PARALLELISM" -> "1")
    case Port.StdPar => Map("GEMSTONE_EXECUTION_PARALLELISM" -> "1")
    case _           => Map.empty
  }

  private def artifactEnvironment(artifact: Artifact): Map[String, String] =
    Some(artifact.root.resolve("lib")).filter(path => Files.isDirectory(path)).map(path =>
      "LD_LIBRARY_PATH" -> path.toString
    ).toMap

  private def artifactLibrary(artifact: Artifact, name: String): Option[Path] =
    Some(artifact.root.resolve("lib").resolve(name)).filter(path => Files.isRegularFile(path))

  private def syclAdapter(provider: Provider, artifact: Artifact): Option[Path] = provider match {
    case Provider.SYCLOpenCL    => findLibrary(artifact.root.resolve("lib"), "libur_adapter_opencl")
    case Provider.SYCLLevelZero => findLibrary(artifact.root.resolve("lib"), "libur_adapter_level_zero")
    case _                      => None
  }

  private def findLibrary(directory: Path, prefix: String): Option[Path] = if !Files.isDirectory(directory) then None
  else {
    val entries = Files.list(directory)
    try entries.iterator.asScala.find(path => Files.isRegularFile(path) && path.getFileName.toString.startsWith(prefix))
    finally entries.close()
  }

  private def providers(port: Port): Vector[Provider] = port match {
    case Port.CUDA => if oracleOnly || providerPath("GEMSTONE_CUDA_DRIVER", "libcuda.so.1").nonEmpty then
        Vector(Provider.CUDA)
      else Vector.empty
    case Port.HIP => if oracleOnly || providerPath("GEMSTONE_HIP_RUNTIME", "libamdhip64.so.7").nonEmpty then
        Vector(Provider.HIP)
      else Vector.empty
    case Port.OMPTarget => if oracleOnly || providerPath("GEMSTONE_HSA_RUNTIME", "libhsa-runtime64.so.1").nonEmpty then
        Vector(Provider.HSA)
      else Vector.empty
    case Port.Kokkos | Port.StdPar => if oracleOnly || providerPath("GEMSTONE_HIP_RUNTIME", "libamdhip64.so.7").nonEmpty then
        Vector(Provider.HIP)
      else Vector.empty
    case Port.OpenACC => if oracleOnly || providerPath("GEMSTONE_CUDA_DRIVER", "libcuda.so.1").nonEmpty then
        Vector(Provider.CUDA)
      else Vector.empty
    case Port.OpenCL =>
      if oracleOnly || openCLICD.nonEmpty then Vector(Provider.OpenCL) else Vector.empty
    case Port.SYCL | Port.SYCLAcc | Port.SYCLUSM =>
      Vector(
        if oracleOnly || openCLICD.nonEmpty then Some(Provider.SYCLOpenCL) else None,
        if oracleOnly || providerPath("GEMSTONE_LEVELZERO_DRIVER", "libze_gemstone.so.1").nonEmpty then
          Some(Provider.SYCLLevelZero)
        else None
      ).flatten
  }

  private def targets(provider: Provider): Vector[Option[String]] = provider match {
    case Provider.CUDA | Provider.OpenCL => Vector(None)
    case _             => targetMatrix.toVector.flatMap(_.targets.map(Some(_)))
  }

  private def portAvailable(port: Port): Boolean = port match {
    case Port.OpenACC => sys.env.get("GEMSTONE_MINIAPP_OPENACC_IMAGE").exists(_.nonEmpty)
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
    case "cuda"                  => Port.CUDA
    case "hip"                   => Port.HIP
    case "ocl" | "opencl"        => Port.OpenCL
    case "sycl"                  => Port.SYCL
    case "sycl-acc" | "sycl_acc" => Port.SYCLAcc
    case "sycl-usm" | "sycl_usm" => Port.SYCLUSM
    case "omp-target" | "omp_target" | "omptarget" => Port.OMPTarget
    case "kokkos"                => Port.Kokkos
    case "openacc" | "acc"      => Port.OpenACC
    case "std" | "stdpar" | "std-indices" | "std_indices" => Port.StdPar
    case other                   => throw IllegalArgumentException(s"unknown GEMSTONE_MINIAPP_PORTS entry: $other")
  }

  private def providerPath(variable: String, fileName: String): Option[Path] =
    sys.env.get(variable).filter(_.nonEmpty).map(Path.of(_)).filter(path => Files.isRegularFile(path)).orElse {
      val direct = Vector(
        target.resolve(fileName),
        target.resolve(s"cuda-gemstone/lib/$fileName"),
        root.resolve(s"target/out/jvm/scala-3.9.0/gemstone-runtime-hip/cmake/$fileName"),
        root.resolve(s"target/out/jvm/scala-3.9.0/gemstone-runtime-levelzero/cmake/$fileName"),
        root.resolve(s"target/out/jvm/scala-3.9.0/gemstone-runtime-opencl/cmake/$fileName")
      ).find(path => Files.isRegularFile(path))
      direct.orElse(findFile(root.resolve("target/out"), fileName)).orElse(findFile(target, fileName))
    }

  private lazy val openCLICD: Option[Path] =
    sys.env.get("GEMSTONE_OPENCL_ICD").filter(_.nonEmpty).map(Path.of(_)).filter(path =>
      Files.isDirectory(path)
    ).orElse {
      Vector(target.resolve("opencl-driver/icd")).find(path => Files.isDirectory(path))
    }

  private lazy val openCLLoader: Option[Path] =
    sys.env.get("GEMSTONE_OPENCL_LOADER").filter(_.nonEmpty).map(Path.of(_)).filter(path =>
      Files.isRegularFile(path)
    ).orElse {
      Vector(
        Path.of("/usr/lib64/libOpenCL.so.1"),
        Path.of("/usr/lib/x86_64-linux-gnu/libOpenCL.so.1"),
        Path.of("/usr/local/cuda-11.6/targets/x86_64-linux/lib/libOpenCL.so.1")
      ).find(path => Files.isRegularFile(path))
    }

  private def findFile(directory: Path, fileName: String): Option[Path] = if !Files.isDirectory(directory) then None
  else {
    val stream = Files.walk(directory)
    try stream.iterator.asScala.find(path => Files.isRegularFile(path) && path.getFileName.toString == fileName)
    finally stream.close()
  }

  private def deleteOwned(path: Path): Unit = if Files.exists(path) then {
    val stream = Files.walk(path)
    try stream.sorted(java.util.Comparator.reverseOrder()).iterator.asScala.foreach(Files.delete)
    finally stream.close()
  }

  private def cudaArchitecture: String = {
    val value = sys.env.get("GEMSTONE_CUDA_ARCH").filter(_.nonEmpty).getOrElse("sm_75")
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

  private def truthy(value: Option[String]): Boolean =
    value.exists(raw => Set("1", "true", "yes").contains(raw.trim.toLowerCase))
}
