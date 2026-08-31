package net.kurobako.gemstone.conformance

import ConformanceSupport.*
import net.kurobako.gemstone.launcher.ComputeAPI
import net.kurobako.gemstone.launcher.RuntimeLayout

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future as JavaFuture
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*

/** The Khronos OpenCL CTS quick inventory, built once against the stock ICD loader and run unchanged in both lanes. */
final class OpenCLCTSConformanceSuite extends munit.FunSuite {
  private val caseTimeout = sys.env.get("GEMSTONE_OPENCL_TIMEOUT_SECONDS").fold(30.minutes)(_.toLong.seconds)
  require(caseTimeout > Duration.Zero, s"GEMSTONE_OPENCL_TIMEOUT_SECONDS must be positive, found $caseTimeout")

  override val munitTimeout: Duration = caseTimeout + 30.seconds

  private final case class CTSCase(name: String, command: String)
  private final case class MatrixCase(testCase: CTSCase, target: String, identity: String)

  private val ctsVersion = version("opencl-cts")
  private val source     = prepareArchive(
    s"opencl-cts-$ctsVersion",
    s"https://github.com/KhronosGroup/OpenCL-CTS/archive/refs/tags/$ctsVersion.tar.gz",
    sha256("opencl-cts")
  )
  private val openCLHeaders = prepareArchive(
    s"opencl-headers-${version("opencl-headers")}",
    s"https://github.com/KhronosGroup/OpenCL-Headers/archive/refs/tags/v${version("opencl-headers")}.zip",
    sha256("opencl-headers")
  )
  private val spirvHeaders = prepareArchive(
    s"spirv-headers-${version("spirv-headers")}",
    s"https://github.com/KhronosGroup/SPIRV-Headers/archive/refs/tags/${version("spirv-headers")}.zip",
    sha256("spirv-headers")
  )
  private val buildRoot = target.resolve(s"opencl-cts-$ctsVersion/build")
  private val providerRoot = target.resolve("opencl-driver")
  private val loader       = ConformanceSupport.openCLLoader
  private val targetMatrix = amdgpuTargetMatrix

  configure(
    source,
    buildRoot,
    Vector(
      "-DCMAKE_BUILD_TYPE=Release",
      s"-DCL_INCLUDE_DIR=$openCLHeaders",
      s"-DCL_LIB_DIR=${loader.getParent}",
      s"-DOPENCL_LIBRARIES=$loader",
      s"-DSPIRV_INCLUDE_DIR=$spirvHeaders",
      "-DGL_IS_SUPPORTED=OFF",
      "-DGLES_IS_SUPPORTED=OFF",
      "-DVULKAN_IS_SUPPORTED=OFF"
    )
  )

  private val all = parseQuickInventory(source.resolve("test_conformance/opencl_conformance_tests_quick.csv"))
  private val includeStress = sys.env.get("GEMSTONE_OPENCL_INCLUDE_STRESS").contains("1")
  private val includePrintf = sys.env.get("GEMSTONE_OPENCL_INCLUDE_PRINTF").contains("1")
  private val includeRelationals = sys.env.get("GEMSTONE_OPENCL_INCLUDE_RELATIONALS").contains("1")
  private val durationExcluded = Set(
    "Basic",
    "API",
    "Compiler",
    "Common Functions",
    "Geometric Functions",
    "Thread Dimensions",
    "Atomics",
    "Events",
    "Allocations (single maximum)",
    "Allocations (total maximum)",
    "Buffers",
    "Select",
    "Contractions",
    "Math",
    "Integer Ops",
    "Half Ops",
    "C11 Atomics",
    "Non Uniform Work Groups",
    "SVM",
    "SPIRV new"
  )
  // Gemstone deliberately reports no image support. Do not spend the oracle
  // half of a paired run executing image tests that the provider must skip.
  private val supported = all.filterNot(unsupportedByProvider).filterNot { entry =>
    (!includeStress && durationExcluded.contains(entry.name)) || (!includePrintf && entry.name == "Printf") ||
    (!includeRelationals && entry.name == "Relationals")
  }
  private val matrixCases = targetMatrix.targets.flatMap { target =>
    supported.map(entry => MatrixCase(entry, target, s"$target/${entry.name}"))
  }
  private val selected = select(
    matrixCases.zipWithIndex.map { case (entry, index) => index -> entry.identity },
    "OPENCL"
  ).map { case (index, _) => matrixCases(index) }
  private val jobs = sys.env.get("GEMSTONE_OPENCL_JOBS")
    .fold(Math.min(8, Runtime.getRuntime.availableProcessors))(_.toInt)
  private val oracleEnabled = sys.env.get("GEMSTONE_OPENCL_ORACLE").forall(_ != "0")
  private val wimpy = sys.env.get("GEMSTONE_OPENCL_WIMPY").contains("1")
  private val basicCase = sys.env.get("GEMSTONE_OPENCL_BASIC_CASE").filter(_.nonEmpty)
  private val basicElements = sys.env.get("GEMSTONE_OPENCL_BASIC_ELEMENTS").filter(_.nonEmpty).getOrElse("1")
  private val caseArguments = sys.env.get("GEMSTONE_OPENCL_CASE_ARGS").filter(_.nonEmpty)
  private val gemstoneEnvironment = Map(
    // Intel's compiler/runtime must see these before its DSO is loaded when
    // the CTS exercises double precision through DP emulation.
    "OverrideDefaultFP64Settings" -> "1",
    "IGC_EnableDPEmulation"        -> "1"
  )
  private var executor   = Option.empty[ExecutorService]
  private var executions = Map.empty[String, JavaFuture[Unit]]

  override def beforeAll(): Unit = {
    require(jobs >= 1 && jobs <= 32, s"GEMSTONE_OPENCL_JOBS must be between 1 and 32, found $jobs")
    configure(root.resolve("runtime/opencl"), providerRoot, Vector("-DBUILD_TESTING=OFF"), gemstoneEnvironment)
    build(providerRoot, "gemstone_opencl", gemstoneEnvironment)
    val icdDirectory = Files.createDirectories(providerRoot.resolve("icd"))
    Files.writeString(icdDirectory.resolve("gemstone.icd"), providerRoot.resolve("libOpenCL_gemstone.so").toString + "\n")
    runChecked(Vector("cmake", "--build", buildRoot.toString, "--parallel"), root)
    val pool = Executors.newFixedThreadPool(jobs)
    executor = Some(pool)
    executions = selected.map { entry =>
      val task: Callable[Unit] = () => runCase(entry)
      entry.identity -> pool.submit(task)
    }.toMap
  }

  override def afterAll(): Unit = executor.foreach(_.shutdown())

  selected.foreach { entry =>
    test(s"opencl/${entry.identity}") {
      await(entry.identity)
    }
  }

  private def await(name: String): Unit =
    try executions.getOrElse(name, fail(s"parallel test was not scheduled: $name")).get()
    catch {
      case error: ExecutionException => throw error.getCause
    }

  private def runCase(entry: MatrixCase): Unit = {
    val command = Vector("bash", "-c", commandLine(entry.testCase))
    val layout  = RuntimeLayout(
      openCLICD = Some(providerRoot.resolve("icd")),
      openCLLoader = Some(loader)
    )
    val environment = gemstoneEnvironment ++ Map(
      "CL_DEVICE_TYPE"         -> "CL_DEVICE_TYPE_GPU",
      "GEMSTONE_AMDGPU_TARGET" -> entry.target
    )
    if (oracleEnabled) {
      val result = runPaired(
        ComputeAPI.OpenCL,
        layout,
        command,
        buildRoot.resolve("test_conformance"),
        oracleEnvironment = Map("CL_DEVICE_TYPE" -> "CL_DEVICE_TYPE_GPU"),
        gemstoneEnvironment = environment,
        timeout = caseTimeout
      )
      if result.oracleRan then
        require(result.oracle.exit == 0, s"oracle/${entry.identity}:\n${result.oracle.diagnostic}")
      require(result.gemstone.exit == 0, s"gemstone/${entry.identity}:\n${result.gemstone.diagnostic}")
    } else {
      val result = runGemstone(
        ComputeAPI.OpenCL,
        layout,
        command,
        buildRoot.resolve("test_conformance"),
        gemstoneEnvironment = environment,
        timeout = caseTimeout
      )
      require(result.exit == 0, s"gemstone/${entry.identity}:\n${result.diagnostic}")
    }
  }

  private def commandLine(entry: CTSCase): String = {
    val base =
      if (entry.name == "SPIRV new")
        "cd spirv_new && ./test_spirv_new --spirv-binaries-path spirv_bin"
      else if (entry.name == "Compiler")
        "cd compiler && ./test_compiler"
      else if (entry.name == "Integer Ops")
        "integer_ops/test_integer_ops 'integer_*' 'quick_*'"
      else if (entry.name == "Basic" && basicCase.isDefined)
        s"${entry.command} ${basicCase.get} $basicElements"
      else if (wimpy && entry.name == "Basic") s"${entry.command} -w"
      else entry.command
    caseArguments.fold(base)(arguments => s"$base $arguments")
  }

  private def parseQuickInventory(path: Path): Vector[CTSCase] = {
    val entries = Files.readAllLines(path, StandardCharsets.UTF_8).asScala.iterator.flatMap { raw =>
      val line = raw.trim
      if (line.isEmpty || line.startsWith("#")) None
      else
        line.split("\\s*,\\s*", 3) match {
          case Array(_, command) if command.startsWith("gl/") => None
          case Array(name, command)                           => Some(CTSCase(name, command))
          case Array(device, name, command)
              if device == "CL_DEVICE_TYPE_GPU" && !command.startsWith("gl/") =>
            Some(CTSCase(name, command))
          case Array(device, _, _) if device.startsWith("CL_DEVICE_TYPE_") => None
          case fields => throw IllegalArgumentException(s"invalid OpenCL CTS quick entry: ${fields.mkString("|")}")
        }
    }.toVector
    require(entries.size == 57, s"expected 57 supported OpenCL CTS quick cases, found ${entries.size}")
    require(entries.map(_.name).distinct.size == entries.size, "OpenCL CTS quick case names are not unique")
    entries
  }

  private def unsupportedByProvider(entry: CTSCase): Boolean =
    entry.name.startsWith("Images") || entry.name.startsWith("Mipmaps")
}
