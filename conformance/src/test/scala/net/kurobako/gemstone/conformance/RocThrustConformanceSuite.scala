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
  * The vendor toolchain builds each artifact exactly once. The resulting executable is first run against the
  * installed ROCm stack as the oracle, then run unchanged through Gemstone's public HIP or HSA provider.
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
  private val buildRoot       = target.resolve(s"rocthrust-${rocThrustPin.commit}/build-pristine")
  private val googleTestRoot  = target.resolve(s"googletest-${version("googletest")}")
  private val googleTestBuild = googleTestRoot.resolve("build")
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
      "-DGPU_TARGETS=gfx900",
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
  private val indexed = upstreamIndexed ++ samples.zipWithIndex.map { case (sample, index) =>
    (all.size + index) -> s"samples/${sample.name}"
  }
  require(indexed.map(_._2).distinct.size == indexed.size, "ROCm conformance case identities are not unique")
  private val selected = select(indexed, "ROCTHRUST")
  private val jobs     = sys.env.get("GEMSTONE_ROCTHRUST_JOBS")
    .fold(Math.min(8, Runtime.getRuntime.availableProcessors))(_.toInt)
  private val buildLock  = Object()
  private var executor   = Option.empty[ExecutorService]
  private var executions = Map.empty[String, JavaFuture[Unit]]

  override def beforeAll(): Unit = {
    require(jobs >= 1 && jobs <= 32, s"GEMSTONE_ROCTHRUST_JOBS must be between 1 and 32, found $jobs")
    prepareCodeObject()
    val pool = Executors.newFixedThreadPool(jobs)
    executor = Some(pool)
    executions = selected.map { case (index, identity) =>
      val task: Callable[Unit] = () =>
        if (index < all.size) runUpstream(index, identity)
        else runSample(samples(index - all.size))
      identity -> pool.submit(task)
    }.toMap
  }

  override def afterAll(): Unit = executor.foreach(_.shutdown())

  selected.foreach { case (_, identity) =>
    test(s"rocthrust/$identity") {
      await(identity)
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
          s"-DCMAKE_INSTALL_PREFIX=$googleTestInstall"
        )
      )
      runChecked(
        Vector("cmake", "--build", googleTestBuild.toString, "--parallel", "--target", "install"),
        root
      )
    }
  }

  private def prepareCodeObject(): Unit = {
    Files.createDirectories(sampleRoot)
    val output = sampleRoot.resolve("vector-copy-gfx900.hsaco")
    if (!Files.isRegularFile(output)) {
      val resourceDirectory = run(Vector(amdgpuClang.toString, "--print-resource-dir"), root)
      require(resourceDirectory.exit == 0, resourceDirectory.diagnostic)
      val resources = Path.of(resourceDirectory.stdout.trim)
      runChecked(
        Vector(
          amdgpuClang.toString,
          "-x",
          "cl",
          "-target",
          "amdgcn-amd-amdhsa",
          "-include",
          resources.resolve("include/opencl-c.h").toString,
          "-mcpu=gfx900",
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

  private def runUpstream(index: Int, identity: String): Unit = {
    val testCase = all(index)
    buildLock.synchronized(build(buildRoot, buildTarget(testCase)))
    val refreshed = inventory(buildRoot)(index)
    val command   = refreshed.command.getOrElse(fail(s"CTest did not resolve the executable for $identity")) ++
      Option.when(relativeWorkingDirectory(testCase).startsWith("testing"))(
        Vector("--sizes=tiny", "--concise")
      ).getOrElse(Vector.empty)
    val result = runPaired(
      ComputeAPI.HIP,
      runtimeLayout,
      command,
      refreshed.workingDirectory,
      oracleEnvironment,
      Map.empty
    )
    require(result.oracle.exit == 0, s"oracle/$identity:\n${result.oracle.diagnostic}")
    require(result.gemstone.exit == 0, s"gemstone/$identity:\n${result.gemstone.diagnostic}")
  }

  private def runSample(sample: SourceCase): Unit = {
    val caseRoot = sampleRoot.resolve(sample.name)
    Files.createDirectories(caseRoot)
    require(sample.commands.size == 2, s"${sample.source} must contain exactly one compile and one run command")
    val variables = Map(
      "input"           -> sample.source.toString,
      "output"          -> caseRoot.resolve("sample").toString,
      "hipcc"           -> hipcc.toString,
      "cxx"             -> sys.env.getOrElse("CXX", "c++"),
      "hsa_include"     -> hsaInclude.toString,
      "rocr_lib"        -> vendorROCr.toString,
      "hsa_vector_copy" -> sampleRoot.resolve("vector-copy-gfx900.hsaco").toString
    )
    runChecked(Vector("bash", "-c", expand(sample.commands.head.text, variables)), caseRoot, oracleEnvironment)
    val api    = if (sample.source.getParent.getFileName.toString == "hip") ComputeAPI.HIP else ComputeAPI.HSA
    val result = runPaired(
      api,
      runtimeLayout,
      Vector("bash", "-c", expand(sample.commands.last.text, variables)),
      caseRoot,
      oracleEnvironment,
      Map.empty
    )
    require(result.oracle.exit == 0, s"oracle/${sample.name}:\n${result.oracle.diagnostic}")
    require(result.gemstone.exit == 0, s"gemstone/${sample.name}:\n${result.gemstone.diagnostic}")
    sample.commands.last.expected.foreach { expected =>
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

  private def buildTarget(testCase: CTestCase): String = relativeWorkingDirectory(testCase) match {
    case "test"                         => testCase.name
    case "testing"                      => s"test_thrust_${testCase.name}"
    case "testing/async/exclusive_scan" => s"test_thrust_exclusive_${testCase.name}"
    case "testing/async/inclusive_scan" => s"test_thrust_inclusive_${testCase.name}"
    case directory => throw IllegalArgumentException(s"unknown rocThrust CTest directory: $directory")
  }
}
