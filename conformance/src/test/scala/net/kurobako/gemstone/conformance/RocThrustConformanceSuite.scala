package net.kurobako.gemstone.conformance

import ConformanceSupport.*
import net.kurobako.gemstone.launcher.ComputeAPI
import net.kurobako.gemstone.launcher.RuntimeLayout

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future as JavaFuture
import scala.concurrent.duration.*

/** Every upstream-enabled rocThrust CTest instance; legacy Thrust-harness tests use upstream's tiny size tier.
  *
  * The vendor toolchain builds each artifact exactly once. The resulting executable is first run against the installed
  * ROCm stack as the oracle, then run unchanged through Gemstone's public HIP or HSA provider.
  */
final class RocThrustConformanceSuite extends munit.FunSuite {
  override val munitTimeout: Duration = 30.minutes

  private val rocThrustPin = pin("rocthrust")
  private val source       = prepareArchive(
    s"rocthrust-${rocThrustPin.commit}",
    s"https://github.com/ROCm/rocThrust/archive/${rocThrustPin.commit}.tar.gz",
    rocThrustPin.sha256
  )
  private val googleTestSource = prepareArchive(
    s"googletest-${version("googletest")}",
    s"https://github.com/google/googletest/archive/refs/tags/${version("googletest")}.tar.gz",
    sha256("googletest")
  )
  private val buildRoot         = target.resolve(s"rocthrust-${rocThrustPin.commit}/build-pristine")
  private val googleTestRoot    = target.resolve(s"googletest-${version("googletest")}")
  private val googleTestBuild   = googleTestRoot.resolve("build")
  private val googleTestInstall = googleTestRoot.resolve("install")
  private val sampleRoot        = target.resolve("rocm-official-samples")
  private val hipcc             = sys.env.get("GEMSTONE_HIPCC").filter(_.nonEmpty).map(Path.of(_))
    .getOrElse(Path.of("/usr/bin/hipcc"))
  private val amdgpuClang = sys.env.get("GEMSTONE_AMDGPU_CLANG").filter(_.nonEmpty).map(Path.of(_))
    .getOrElse(Path.of("/usr/lib64/rocm/llvm/bin/clang"))
  private val hsaInclude = sys.env.get("GEMSTONE_HSA_INCLUDE").filter(_.nonEmpty).map(Path.of(_))
    .getOrElse(Path.of("/usr/include/hsa"))
  private val vendorROCr = sys.env.get("GEMSTONE_ROCR_LIB").filter(_.nonEmpty).map(Path.of(_))
    .getOrElse(Path.of("/usr/lib64"))
  private val targetMatrix = amdgpuTargetMatrix
  private val gpuTargets   = targetMatrix.targets

  // These upstream cases intentionally submit ranges of 2^30 through 2^33
  // elements (or 10 billion constant-iterator elements).  They are useful
  // on a real GPU for checking 64-bit indexing, but cannot finish in the
  // bounded CPU emulator.  Filter individual cases instead of dropping the
  // whole testing executable.
  // Match the CUDA conformance convention: extensions are named as
  // comma-separated case:unit entries. This is for newly proven
  // emulator-impossible workloads only.
  private val emulationBlacklist: Map[String, Set[String]] = {
    val environment = sys.env.get("GEMSTONE_ROCTHRUST_EMULATION_BLACKLIST").toVector.flatMap { value =>
      value.split(',').iterator.map(_.trim).filter(_.nonEmpty).map { entry =>
        val separator = entry.indexOf(':')
        require(
          separator > 0 && separator < entry.length - 1,
          s"invalid GEMSTONE_ROCTHRUST_EMULATION_BLACKLIST entry: $entry"
        )
        entry.take(separator) -> entry.drop(separator + 1)
      }
    }
    environment.foldLeft(Map.empty[String, Set[String]]) { case (blacklist, (testCase, unit)) =>
      blacklist.updated(testCase, blacklist.getOrElse(testCase, Set.empty) + unit)
    }
  }

  private val defaultGtestUnreasonablePatterns = Vector(
    "*BigIndexes*",
    "*LargeNumberOfItems*",
    "*ReduceLargeInput*",
    "*ConstantIteratorIncrementBig*",
    "*test_large_indices_*",
    "*UniqueCopyByKeyLargeInput*",
    "*UniqueCopyByKeyLargeOutCount*",
    "*HostPathSimpleTest*",
    "*out_of_memory_recovery*"
  )

  private def gtestUnreasonableFilter(testCaseName: String): String = {
    val extra = emulationBlacklist.getOrElse(testCaseName, Set.empty).toVector.map(name => s"*$name*")
    s"-${(defaultGtestUnreasonablePatterns ++ extra).distinct.mkString(":")}"
  }

  private def isEmulatorUnreasonable(testName: String): Boolean =
    testName.contains("BigIndexes") ||
      testName.contains("LargeNumberOfItems") ||
      testName == "TestReduceLargeInput" ||
      testName == "TestConstantIteratorIncrementBig" ||
      testName.startsWith("test_large_indices_") ||
      testName.contains("UniqueCopyByKeyLargeInput") ||
      testName.contains("UniqueCopyByKeyLargeOutCount") ||
      testName.endsWith("HostPathSimpleTest") ||
      testName.contains("out_of_memory_recovery")

  private def isEmulatorUnreasonable(testCaseName: String, testName: String): Boolean =
    isEmulatorUnreasonable(testName) || emulationBlacklist.get(testCaseName).exists(_.contains(testName))

  // These entries contain only compile-time and host-side assertions. They still run through
  // Gemstone's injected HIP provider, but cannot produce an execution RPC.
  private val hostOnly = Set("testing/address_stability", "test/address_stability.hip")

  require(Files.isExecutable(hipcc), s"HIP compiler is absent: $hipcc; set GEMSTONE_HIPCC")
  require(Files.isExecutable(amdgpuClang), s"AMDGPU Clang is absent: $amdgpuClang; set GEMSTONE_AMDGPU_CLANG")
  require(Files.isDirectory(hsaInclude), s"HSA headers are absent: $hsaInclude; set GEMSTONE_HSA_INCLUDE")
  prepareGoogleTest()
  configure(
    source,
    buildRoot,
    Vector(
      "-DBUILD_TEST=ON",
      "-DENABLE_UPSTREAM_TESTS=ON",
      "-DUSE_SYSTEM_LIB=OFF",
      "-DBUILD_OFFLOAD_COMPRESS=OFF",
      "-DCMAKE_POLICY_VERSION_MINIMUM=3.5",
      s"-DCMAKE_PREFIX_PATH=$googleTestInstall",
      s"-DCMAKE_CXX_COMPILER=$hipcc",
      s"-DGPU_TARGETS=${targetMatrix.targetList}",
      s"-DGPU_BUILD_TARGETS=${targetMatrix.targetList}",
      "-DCMAKE_BUILD_TYPE=Release"
    )
  )

  private val all = inventory(buildRoot)
  require(all.size == 348, s"expected 348 upstream rocThrust CTest instances, found ${all.size}")
  require(!all.exists(_.disabled), "disabled rocThrust tests must be omitted from the enabled inventory")
  private val upstreamIndexed = all.zipWithIndex.map { case (testCase, index) =>
    index -> s"${relativeWorkingDirectory(testCase)}/${testCase.name}"
  }
  private val samples = discoverSourceCases(root.resolve("conformance/rocthrust/samples"), Set(".cpp", ".hip"))
  require(samples.size == 5, s"expected five ROCm sample cases, found ${samples.size}")
  private val baseIndexed = upstreamIndexed ++ samples.zipWithIndex.map { case (sample, index) =>
    (all.size + index) -> s"samples/${sample.name}"
  }
  require(baseIndexed.map(_._2).distinct.size == baseIndexed.size, "ROCm conformance case identities are not unique")
  private final case class MatrixCase(target: String, baseIndex: Int, identity: String)
  private val matrixCases = gpuTargets.flatMap { target =>
    baseIndexed.map { case (baseIndex, identity) => MatrixCase(target, baseIndex, s"$target/$identity") }
  }
  private val selected =
    select(matrixCases.zipWithIndex.map { case (testCase, index) => index -> testCase.identity }, "ROCTHRUST")
      .map { case (index, _) => matrixCases(index) }
  private val jobs = sys.env.get("GEMSTONE_ROCTHRUST_JOBS")
    .fold(Math.min(8, Runtime.getRuntime.availableProcessors))(_.toInt)
  private var executor   = Option.empty[ExecutorService]
  private var executions = Map.empty[String, JavaFuture[Unit]]

  override def beforeAll(): Unit = {
    require(jobs >= 1 && jobs <= 32, s"GEMSTONE_ROCTHRUST_JOBS must be between 1 and 32, found $jobs")
    prepareCodeObjects()
    buildSelectedUpstream()
    val pool = Executors.newFixedThreadPool(jobs)
    executor = Some(pool)
    executions = selected.map { testCase =>
      val task: Callable[Unit] = () =>
        if (testCase.baseIndex < all.size) runUpstream(testCase.baseIndex, testCase.target, testCase.identity)
        else runSample(samples(testCase.baseIndex - all.size), testCase.target, testCase.identity)
      testCase.identity -> pool.submit(task)
    }.toMap
  }

  override def afterAll(): Unit = executor.foreach(_.shutdown())

  selected.foreach { testCase =>
    test(s"rocthrust/${testCase.identity}") {
      await(testCase.identity)
    }
  }

  private def await(identity: String): Unit =
    try executions.getOrElse(identity, fail(s"parallel test was not scheduled: $identity")).get()
    catch {
      case error: ExecutionException => throw error.getCause
    }

  private def prepareGoogleTest(): Unit = {
    val library = googleTestInstall.resolve("lib/libgtest.a")
    if (!Files.isRegularFile(library)) {
      configure(
        googleTestSource,
        googleTestBuild,
        Vector(
          "-DCMAKE_POLICY_VERSION_MINIMUM=3.5",
          "-DCMAKE_BUILD_TYPE=Release",
          "-DBUILD_GMOCK=OFF",
          "-DBUILD_SHARED_LIBS=OFF",
          "-DCMAKE_POSITION_INDEPENDENT_CODE=ON",
          s"-DCMAKE_INSTALL_PREFIX=$googleTestInstall"
        )
      )
      runChecked(
        Vector("cmake", "--build", googleTestBuild.toString, "--parallel", "--target", "install"),
        root
      )
    }
  }

  private def prepareCodeObjects(): Unit = {
    Files.createDirectories(sampleRoot)
    val resourceDirectory = run(Vector(amdgpuClang.toString, "--print-resource-dir"), root)
    require(resourceDirectory.exit == 0, resourceDirectory.diagnostic)
    val resources = Path.of(resourceDirectory.stdout.trim)
    gpuTargets.foreach { target =>
      val output = codeObject(target)
      if (!Files.isRegularFile(output)) {
        runChecked(
          Vector(
            amdgpuClang.toString,
            "-x",
            "cl",
            "-target",
            "amdgcn-amd-amdhsa",
            "-include",
            resources.resolve("include/opencl-c.h").toString,
            s"-mcpu=$target",
            s"--hip-device-lib-path=${resources.resolve("lib/amdgcn/bitcode")}",
            "-cl-std=CL2.0",
            "-mcode-object-version=4",
            root.resolve("conformance/rocthrust/samples/hsa/vector_copy_kernel.cl").toString,
            "-o",
            output.toString
          ),
          root
        )
      }
    }
  }

  private def runUpstream(index: Int, target: String, identity: String): Unit = {
    val testCase  = all(index)
    val refreshed = inventory(buildRoot)(index)
    val command   = commandFor(
      testCase,
      refreshed.command.getOrElse(fail(s"CTest did not resolve the executable for $identity"))
    )
    val result = runPaired(
      ComputeAPI.HIP,
      runtimeLayout,
      command,
      refreshed.workingDirectory,
      oracleEnvironment,
      Map("GEMSTONE_AMDGPU_TARGET" -> target),
      requireExecution = !hostOnly.contains(relativeWorkingDirectory(testCase) + "/" + testCase.name)
    )
    if result.oracleRan then require(result.oracle.exit == 0, s"oracle/$identity:\n${result.oracle.diagnostic}")
    require(result.gemstone.exit == 0, s"gemstone/$identity:\n${result.gemstone.diagnostic}")
  }

  private def buildSelectedUpstream(): Unit = {
    val targets = selected.iterator
      .filter(_.baseIndex < all.size)
      .map(testCase => buildTarget(all(testCase.baseIndex)))
      .toVector
      .distinct
    if targets.nonEmpty then
      runChecked(
        Vector("cmake", "--build", buildRoot.toString, "--parallel", jobs.toString, "--target") ++ targets,
        root
      )
  }

  private def commandFor(testCase: CTestCase, executable: Vector[String]): Vector[String] =
    relativeWorkingDirectory(testCase) match {
      case "test" => executable :+ s"--gtest_filter=${gtestUnreasonableFilter(testCase.name)}"
      case directory if directory.startsWith("testing") =>
        val listing = run(executable :+ "--list", testCase.workingDirectory)
        require(listing.exit == 0, s"could not list ${testCase.name}:\n${listing.diagnostic}")
        val names       = listing.stdout.linesIterator.map(_.trim).filter(_.nonEmpty).toVector
        val blacklisted = names.filter(name => isEmulatorUnreasonable(testCase.name, name))
        val sizes       = Vector("--sizes=tiny", "--concise")
        if (blacklisted.isEmpty) executable ++ sizes
        else {
          val runnable = names.filterNot(name => isEmulatorUnreasonable(testCase.name, name))
          require(runnable.nonEmpty, s"blacklist removed every test from ${testCase.name}")
          executable ++ runnable ++ sizes
        }
      case _ => executable
    }

  private def runSample(sample: SourceCase, target: String, identity: String): Unit = {
    val caseRoot = sampleRoot.resolve(target).resolve(sample.name)
    Files.createDirectories(caseRoot)
    require(sample.commands.size == 2, s"${sample.source} must contain exactly one compile and one run command")
    val variables = Map(
      "input"           -> sample.source.toString,
      "output"          -> caseRoot.resolve("sample").toString,
      "hipcc"           -> hipcc.toString,
      "cxx"             -> sys.env.getOrElse("CXX", "c++"),
      "hsa_include"     -> hsaInclude.toString,
      "rocr_lib"        -> vendorROCr.toString,
      "gpu_target"      -> target,
      "gpu_targets"     -> targetMatrix.targetFlags,
      "hsa_vector_copy" -> codeObject(target).toString
    )
    runChecked(Vector("bash", "-c", expand(sample.commands.head.text, variables)), caseRoot, oracleEnvironment)
    val api    = if (sample.source.getParent.getFileName.toString == "hip") ComputeAPI.HIP else ComputeAPI.HSA
    val result = runPaired(
      api,
      runtimeLayout,
      Vector("bash", "-c", expand(sample.commands.last.text, variables)),
      caseRoot,
      oracleEnvironment,
      Map("GEMSTONE_AMDGPU_TARGET" -> target),
      oracleCommand = Some(Vector(
        "bash",
        "-c",
        expand(
          sample.commands.last.text,
          variables.updated("hsa_vector_copy", codeObject(targetMatrix.oracleTarget).toString)
        )
      ))
    )
    if result.oracleRan then require(result.oracle.exit == 0, s"oracle/${sample.name}:\n${result.oracle.diagnostic}")
    require(result.gemstone.exit == 0, s"gemstone/${sample.name}:\n${result.gemstone.diagnostic}")
    sample.commands.last.expected.foreach { expected =>
      if result.oracleRan then
        require(stripOutput(result.oracle.stdout) == expected, s"oracle/${sample.name}: expected '$expected'")
      require(stripOutput(result.gemstone.stdout) == expected, s"gemstone/${sample.name}: expected '$expected'")
    }
  }

  private def runtimeLayout: RuntimeLayout = RuntimeLayout(
    hipRuntime = sys.env.get("GEMSTONE_HIP_RUNTIME").filter(_.nonEmpty).map(Path.of(_)),
    hsaRuntime = sys.env.get("GEMSTONE_HSA_RUNTIME").filter(_.nonEmpty).map(Path.of(_))
  )

  private def oracleEnvironment: Map[String, String] = Map("HSA_MAX_QUEUES" -> "8")

  private def expand(command: String, variables: Map[String, String]): String = {
    val expanded = variables.foldLeft(command) { case (value, (name, replacement)) =>
      value.replace(s"{$name}", replacement)
    }
    require(!expanded.exists(character => character == '{' || character == '}'), s"unresolved variable: $expanded")
    expanded
  }

  private def stripOutput(value: String): String =
    value.reverse.dropWhile(character => character == '\n' || character == '\r').reverse

  private def relativeWorkingDirectory(testCase: CTestCase): String =
    buildRoot.relativize(testCase.workingDirectory).toString.replace('\\', '/')

  private def codeObject(target: String): Path = sampleRoot.resolve(s"vector-copy-$target.hsaco")

  private def buildTarget(testCase: CTestCase): String = relativeWorkingDirectory(testCase) match {
    case "test"                         => testCase.name
    case "testing"                      => s"test_thrust_${testCase.name}"
    case "testing/async/exclusive_scan" => s"test_thrust_exclusive_${testCase.name}"
    case "testing/async/inclusive_scan" => s"test_thrust_inclusive_${testCase.name}"
    case directory => throw IllegalArgumentException(s"unknown rocThrust CTest directory: $directory")
  }
}
