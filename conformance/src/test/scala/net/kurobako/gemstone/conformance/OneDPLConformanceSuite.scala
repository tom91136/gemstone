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

/** Every upstream-enabled oneDPL CTest instance, using upstream's bounded TEST_LONG_RUN=0 configuration. */
final class OneDPLConformanceSuite extends munit.FunSuite {
  override val munitTimeout: Duration = 30.minutes

  private enum Backend(val id: String, val selector: String) {
    case LevelZero extends Backend("levelzero", "level_zero:gpu")
    case OpenCL    extends Backend("opencl", "opencl:gpu")
  }

  private final case class Work(index: Int, backend: Backend, name: String)

  private val source = {
    val value = pin("onedpl")
    prepareArchive(
      s"onedpl-${value.commit}",
      s"https://github.com/oneapi-src/oneDPL/archive/${value.commit}.tar.gz",
      value.sha256
    )
  }
  private val buildRoot  = target.resolve(s"onedpl-${pin("onedpl").commit}/build")
  private val driverRoot = target.resolve("levelzero-driver")
  private val sampleRoot = target.resolve("onedpl-official-samples")
  private val compiler   = sys.env.get("GEMSTONE_DPCPP").filter(_.nonEmpty).map(Path.of(_))
    .getOrElse(Path.of("/opt/intel/oneapi/compiler/latest/bin/icpx"))

  require(Files.isExecutable(compiler), s"DPC++ compiler is absent: $compiler; set GEMSTONE_DPCPP")
  configure(
    source,
    buildRoot,
    Vector(
      s"-DCMAKE_CXX_COMPILER=$compiler",
      "-DCMAKE_BUILD_TYPE=Release",
      "-DONEDPL_BACKEND=dpcpp_only",
      "-DONEDPL_ENABLE_SIMD=OFF",
      "-DONEDPL_TEST_ENABLE_KT_ESIMD=OFF",
      "-DONEDPL_TEST_ENABLE_KT_SYCL=OFF"
    )
  )

  private val all = inventory(buildRoot)
  require(all.size == 902, s"expected 902 upstream oneDPL CTest instances, found ${all.size}")
  require(all.map(_.name).distinct.size == all.size, "oneDPL produced duplicate CTest names")
  require(!all.exists(_.disabled), "disabled oneDPL tests must be omitted from the enabled inventory")
  private val samples = discoverSourceCases(root.resolve("conformance/onedpl/samples"), Set(".cpp"))
  require(samples.size == 3, s"expected three oneDPL/SYCL sample cases, found ${samples.size}")
  private val cases = all.zipWithIndex.map { case (testCase, index) => index -> testCase.name } ++
    samples.zipWithIndex.map { case (sample, index) => (all.size + index) -> s"samples/${sample.name}" }
  private val work = cases.flatMap { case (index, name) =>
    Backend.values.map(backend => Work(index, backend, s"${backend.id}/$name"))
  }
  private val selectedNames = select(work.zipWithIndex.map { case (entry, index) => index -> entry.name }, "ONEDPL")
  private val selected      = selectedNames.map { case (index, _) => work(index) }
  private val jobs     = sys.env.get("GEMSTONE_ONEDPL_JOBS")
    .fold(Math.min(8, Runtime.getRuntime.availableProcessors))(_.toInt)
  private val buildLock  = Object()
  private var executor   = Option.empty[ExecutorService]
  private var executions = Map.empty[String, JavaFuture[Unit]]

  override def beforeAll(): Unit = {
    require(jobs >= 1 && jobs <= 32, s"GEMSTONE_ONEDPL_JOBS must be between 1 and 32, found $jobs")
    configure(root.resolve("runtime/levelzero"), driverRoot, Vector("-DBUILD_TESTING=OFF"))
    build(driverRoot, "gemstone_levelzero")
    selected.collect { case Work(index, _, _) if index >= all.size => samples(index - all.size) }.distinct
      .foreach(compileSample)
    val pool = Executors.newFixedThreadPool(jobs)
    executor = Some(pool)
    executions = selected.map { entry =>
      val task: Callable[Unit] = () => runSelected(entry)
      entry.name -> pool.submit(task)
    }.toMap
  }

  override def afterAll(): Unit = {
    executor.foreach(_.shutdown())
  }

  selected.foreach { entry =>
    test(s"onedpl/${entry.name}") {
      await(entry.name)
    }
  }

  private def await(name: String): Unit =
    try executions.getOrElse(name, fail(s"parallel test was not scheduled: $name")).get()
    catch {
      case error: ExecutionException => throw error.getCause
    }

  private def runSelected(work: Work): Unit =
    if (work.index < all.size) {
      val targetName = all(work.index).name
      buildLock.synchronized(build(buildRoot, targetName))
      val command = Vector(
        "ctest",
        "--test-dir",
        buildRoot.toString,
        "--output-on-failure",
        "--no-tests=error",
        "-I",
        s"${work.index + 1},${work.index + 1}"
      )
      val result = runPaired(
        ComputeAPI.SYCL,
        runtimeLayout(work.backend),
        command,
        root,
        oracleEnvironment = oracleEnvironment(work.backend),
        gemstoneEnvironment = sharedEnvironment
      )
      require(result.oracle.exit == 0, s"oracle/${work.name}:\n${result.oracle.diagnostic}")
      require(result.gemstone.exit == 0, s"gemstone/${work.name}:\n${result.gemstone.diagnostic}")
    } else {
      val sample = samples(work.index - all.size)
      runPairedSourceCase(sample, work.backend)
    }

  private def runtimeLayout(backend: Backend): RuntimeLayout = backend match {
    case Backend.LevelZero => RuntimeLayout(levelZeroDriver = Some(driverRoot.resolve("libze_gemstone.so")))
    case Backend.OpenCL    => RuntimeLayout(
        openCLICD = sys.env.get("GEMSTONE_OPENCL_ICD").filter(_.nonEmpty).map(Path.of(_))
      )
  }

  private def sharedEnvironment: Map[String, String] = Map(
    "SYCL_CACHE_PERSISTENT" -> "1",
    "LD_LIBRARY_PATH"       -> libraryPath(Vector(
        driverRoot,
        compiler.getParent.getParent.resolve("lib"),
        Path.of("/opt/intel/oneapi/umf/latest/lib")
      ))
  )

  private def oracleEnvironment(backend: Backend): Map[String, String] = sharedEnvironment ++ Map(
    "ONEAPI_DEVICE_SELECTOR" -> backend.selector
  )

  private def sampleVariables(sample: SourceCase): Map[String, String] = Map(
    "input"          -> sample.source.toString,
    "output"         -> sampleRoot.resolve(sample.name).resolve("sample").toString,
    "dpcpp"          -> compiler.toString,
    "onedpl_include" -> source.resolve("include").toString
  )

  private def compileSample(sample: SourceCase): Unit = {
    val caseRoot = Files.createDirectories(sampleRoot.resolve(sample.name))
    require(sample.commands.size == 2, s"${sample.source} must contain exactly one compile and one run command")
    runChecked(Vector("bash", "-c", expand(sample.commands.head.text, sampleVariables(sample))), caseRoot)
  }

  private def runPairedSourceCase(
      sample: SourceCase,
      backend: Backend
  ): Unit = {
    val caseRoot = sampleRoot.resolve(sample.name)
    val execute = expand(sample.commands.last.text, sampleVariables(sample))
    val result  = runPaired(
      ComputeAPI.SYCL,
      runtimeLayout(backend),
      Vector("bash", "-c", execute),
      caseRoot,
      oracleEnvironment(backend),
      sharedEnvironment
    )
    require(result.oracle.exit == 0, s"oracle/${backend.id}/${sample.name}:\n${result.oracle.diagnostic}")
    require(result.gemstone.exit == 0, s"gemstone/${backend.id}/${sample.name}:\n${result.gemstone.diagnostic}")
    sample.commands.last.expected.foreach { expected =>
      require(stripOutput(result.oracle.stdout) == expected, s"oracle/${sample.name}: expected '$expected'")
      require(stripOutput(result.gemstone.stdout) == expected, s"gemstone/${sample.name}: expected '$expected'")
    }
  }

  private def expand(command: String, variables: Map[String, String]): String = {
    val expanded = variables.foldLeft(command) { case (value, (name, replacement)) =>
      value.replace(s"{$name}", replacement)
    }
    require(!expanded.exists(character => character == '{' || character == '}'), s"unresolved variable: $expanded")
    expanded
  }

  private def stripOutput(value: String): String =
    value.reverse.dropWhile(character => character == '\n' || character == '\r').reverse
}
