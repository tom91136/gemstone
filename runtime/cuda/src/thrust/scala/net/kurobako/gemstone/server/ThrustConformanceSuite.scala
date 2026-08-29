package net.kurobako.gemstone.server

import net.kurobako.gemstone.launcher.ComputeAPI
import net.kurobako.gemstone.launcher.Environment
import net.kurobako.gemstone.launcher.GemstoneProcess
import net.kurobako.gemstone.launcher.RuntimeLayout

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future as JavaFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*
import scala.util.control.NonFatal

/** Runs the unmodified upstream Thrust 1.15.0 CPP/CUDA unit-test translation units. */
class ThrustConformanceSuite extends munit.FunSuite {
  import ThrustConformanceSuite.*

  override val munitTimeout: Duration = 30.minutes

  private val root          = findRoot(Path.of(sys.props("user.dir")))
  private val vendorRoot    = root.resolve("conformance/cuda/vendor")
  private val thrustRoot    = vendorRoot.resolve("thrust")
  private val testing       = thrustRoot.resolve("testing")
  private val targetRoot    = Files.createDirectories(root.resolve("target/conformance/thrust-1.15.0"))
  private val libraryDir    = Files.createDirectories(targetRoot.resolve("gemstone-lib"))
  private val nvcc          = findNvcc().getOrElse(throw IllegalStateException("nvcc not found; set NVCC or CUDA_HOME"))
  private val sourceEpoch   = latestModification(vendorRoot)
  private val selectedCases = {
    val discovered = discoverCases(testing, sys.env.get("GEMSTONE_THRUST_INCLUDE_DISABLED").contains("1"))
    val pattern    = sys.env.get("GEMSTONE_THRUST_FILTER").filter(_.nonEmpty).map(_.r)
    pattern.fold(discovered)(regex => discovered.filter(testCase => regex.findFirstIn(testCase.name).nonEmpty))
  }
  private val sizes       = sys.env.getOrElse("GEMSTONE_THRUST_SIZES", "tiny")
  private val unitFilters = sys.env.get("GEMSTONE_THRUST_UNIT_FILTER").toVector
    .flatMap(_.split(',').iterator.map(_.trim).filter(_.nonEmpty))
  private val architecture = sys.env.getOrElse("GEMSTONE_THRUST_ARCH", "native")
  private val compileJobs  =
    sys.env.get("GEMSTONE_THRUST_JOBS").fold(Math.min(4, Runtime.getRuntime.availableProcessors))(
      _.toInt
    )
  private val compileFailures = ConcurrentHashMap[String, Throwable]()
  private var runExecutor     = Option.empty[ExecutorService]
  private var executions      = Map.empty[String, JavaFuture[Unit]]

  require(selectedCases.nonEmpty, "GEMSTONE_THRUST_FILTER selected no tests")

  override def beforeAll(): Unit = {
    require(
      Set("tiny", "small", "medium", "default", "large", "huge", "epic", "max").contains(sizes),
      s"invalid GEMSTONE_THRUST_SIZES value: $sizes"
    )
    require(compileJobs >= 1 && compileJobs <= 32, s"GEMSTONE_THRUST_JOBS must be between 1 and 32, found $compileJobs")
    buildGemstoneFacade(root, libraryDir)
    buildFramework()
    compileCases()
    val executor = Executors.newFixedThreadPool(compileJobs)
    runExecutor = Some(executor)
    executions = selectedCases.map { testCase =>
      val task: Callable[Unit] = () => runCase(testCase)
      testCase.name -> executor.submit(task)
    }.toMap
  }

  override def afterAll(): Unit =
    runExecutor.foreach(_.shutdown())

  selectedCases.foreach { testCase =>
    test(s"thrust/${testCase.name}")(await(testCase.name))
  }

  private def buildFramework(): Unit = {
    val frameworkRoot = Files.createDirectories(targetRoot.resolve("framework"))
    val sources       = Vector(
      testing.resolve("unittest/testframework.cu")      -> "testframework.o",
      testing.resolve("unittest/cuda/testframework.cu") -> "cuda-testframework.o"
    )
    sources.foreach { case (source, outputName) =>
      val output    = frameworkRoot.resolve(outputName)
      val signature = compileSignature(source, rdc = false, Vector.empty)
      compileIfNeeded(output, signature) {
        runChecked(
          nvccCommand ++ includeFlags ++ Vector("-c", source.toString, "-o", output.toString),
          frameworkRoot,
          Map.empty
        )
      }
    }
  }

  private def compileCases(): Unit = {
    val completed = AtomicInteger(0)
    val executor  = Executors.newFixedThreadPool(compileJobs)
    try {
      val futures = selectedCases.map { testCase =>
        val task: Callable[Unit] = () => {
          try compileCase(testCase)
          catch {
            case NonFatal(error) => val _ = compileFailures.put(testCase.name, error)
          }
          val count = completed.incrementAndGet()
          if (count % 10 == 0 || count == selectedCases.size)
            println(s"[thrust] compiled $count/${selectedCases.size}")
        }
        executor.submit(task)
      }
      futures.foreach(_.get())
    } finally executor.shutdown()
  }

  private def await(name: String): Unit =
    try executions.getOrElse(name, fail(s"parallel test was not scheduled: $name")).get()
    catch {
      case error: ExecutionException => throw error.getCause
    }

  private def compileCase(testCase: ThrustCase): Unit = {
    val caseRoot        = Files.createDirectories(targetRoot.resolve("vendor-build").resolve(testCase.name))
    val output          = caseRoot.resolve("test")
    val dependencyFile  = caseRoot.resolve("dependencies.d")
    val frameworkRoot   = targetRoot.resolve("framework")
    val commonFramework = frameworkRoot.resolve("testframework.o")
    val cudaFramework   = frameworkRoot.resolve("cuda-testframework.o")
    val sourceFlags     =
      (if (testCase.rdc) Vector("-rdc=true") else Vector.empty) ++
        (if (testCase.perThreadStream) Vector("--default-stream=per-thread") else Vector.empty)
    val signature = compileSignature(testCase.source, testCase.rdc, sourceFlags)

    compileIfNeeded(output, signature) {
      runChecked(
        nvccCommand ++ includeFlags ++ sourceFlags ++ Vector(
          "-MD",
          "-MF",
          dependencyFile.toString,
          testCase.source.toString,
          commonFramework.toString,
          cudaFramework.toString
        ) ++ Vector("-o", output.toString),
        caseRoot,
        Map.empty
      )
    }
    auditDependencies(dependencyFile)
  }

  private def runCase(testCase: ThrustCase): Unit = {
    Option(compileFailures.get(testCase.name)).foreach(error => throw error)
    val caseRoot = targetRoot.resolve("vendor-build").resolve(testCase.name)
    val command  = Vector(caseRoot.resolve("test").toString) ++ unitFilters ++ Vector(s"--sizes=$sizes", "--concise")
    val oracle   = capture(command, caseRoot, Environment(Map.empty, GemstoneEnvironment))
    assertPass(testCase, "oracle", oracle)
    val gemstone = GemstoneProcess.attested(
      ComputeAPI.CUDA,
      RuntimeLayout(cudaDriver = Some(libraryDir.resolve("libcuda.so.1"))),
      sys.env
    )(environment => capture(command, caseRoot, environment)).fold(
      error => fail(s"gemstone/${testCase.name}: ${error.message}"),
      identity
    )
    assertPass(testCase, "gemstone", gemstone)
  }

  private def assertPass(testCase: ThrustCase, lane: String, result: Captured): Unit = {
    val actual = stripTrailingNewlines(Files.readString(result.stdout, StandardCharsets.UTF_8))
    val errors = Files.readString(result.stderr, StandardCharsets.UTF_8)
    assertEquals(result.exit, 0, s"$lane/${testCase.name} failed\nstdout:\n$actual\nstderr:\n$errors")
    assertEquals(actual, "PASSED", s"$lane/${testCase.name}\nstderr:\n$errors")
  }

  private def includeFlags: Vector[String] = Vector(
    "-include",
    root.resolve("conformance/cuda/support/nvcc-libstdcxx-compat.h").toString,
    s"-I$thrustRoot",
    s"-I${vendorRoot.resolve("cub")}",
    s"-I$testing"
  )

  private def nvccCommand: Vector[String] = Vector(
    nvcc.toString,
    "--allow-unsupported-compiler",
    "-std=c++14",
    "-O0",
    "-no-compress",
    s"-arch=$architecture",
    "-Xcompiler=-Wno-template-body",
    "-DCUDA_FORCE_CDP1_IF_SUPPORTED"
  )

  private def compileSignature(source: Path, rdc: Boolean, extraFlags: Vector[String]): String =
    Vector(
      "thrust=1.15.0",
      "commit=67d6622e48ea44cfaa462d44772cc7663026c952",
      s"nvcc=$nvcc",
      s"nvcc-version=${nvccVersionText(nvcc)}",
      s"source=${testing.relativize(source)}",
      s"source-epoch=$sourceEpoch",
      "build=vendor-standard",
      s"rdc=$rdc",
      s"command=${(nvccCommand ++ includeFlags ++ extraFlags).mkString(" ")}"
    ).mkString("\n") + "\n"

  private def compileIfNeeded(output: Path, signature: String)(compile: => Unit): Unit = {
    val stamp = output.resolveSibling(output.getFileName.toString + ".signature")
    if (!Files.isRegularFile(output) || !Files.isRegularFile(stamp) || Files.readString(stamp) != signature) {
      compile
      val _ = output.toFile.setExecutable(true)
      val _ = Files.writeString(
        stamp,
        signature,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING
      )
    }
  }

  private def auditDependencies(dependencyFile: Path): Unit = {
    val text         = Files.readString(dependencyFile, StandardCharsets.UTF_8).replace("\\\n", " ")
    val dependencies = text.split("\\s+").iterator
      .map(_.stripSuffix("\\").stripSuffix(":"))
      .filter(_.startsWith("/"))
      .map(Path.of(_).normalize)
      .toVector
    val expectedThrust = thrustRoot.resolve("thrust").normalize
    assert(dependencies.exists(_.startsWith(expectedThrust)), s"no vendored Thrust header in $dependencyFile")
    Vector("thrust", "cub").foreach { component =>
      val expected = vendorRoot.resolve(component).resolve(component).normalize
      val leaked   = dependencies.filter { dependency =>
        val normalized = dependency.toString.replace('\\', '/')
        normalized.contains(s"/include/$component/") && !dependency.startsWith(expected)
      }
      assert(leaked.isEmpty, s"non-vendored $component headers in $dependencyFile:\n${leaked.mkString("\n")}")
    }
  }
}

object ThrustConformanceSuite {
  private val GemstoneEnvironment = Set(
    "GEMSTONE_RPC_PORT",
    "GEMSTONE_RPC_TOKEN",
    "GEMSTONE_REQUIRED",
    "GEMSTONE_RECORD",
    "LD_PRELOAD"
  )
  private final case class ThrustCase(name: String, source: Path, rdc: Boolean, perThreadStream: Boolean)
  private final case class Captured(exit: Int, stdout: Path, stderr: Path)

  private def discoverCases(testing: Path, includeDisabled: Boolean): Vector[ThrustCase] = {
    val stream  = Files.walk(testing)
    val sources =
      try stream.iterator.asScala.filter(path =>
          Files.isRegularFile(path) && Set("cu", "cpp").contains(extension(path))
        ).toVector
      finally stream.close()

    val enabled = sources.filter { source =>
      val relative = testing.relativize(source)
      relative.getNameCount == 1 ||
      (relative.getName(0).toString == "async" && relative.getNameCount == 3) ||
      (relative.getName(0).toString == "cuda" && relative.getNameCount == 2) ||
      (includeDisabled && relative.getName(0).toString == "regression" && relative.getNameCount == 2)
    }
    val cases = enabled.map { source =>
      val relative = testing.relativize(source)
      val name     = stripExtension(relative.toString.replace('\\', '.').replace('/', '.'))
      val cudaRdc  = relative.getNameCount > 1 && relative.getName(0).toString == "cuda"
      ThrustCase(name, source, rdc = cudaRdc, perThreadStream = name == "cuda.stream_per_thread")
    }.sortBy(_.name)
    val expected = if (includeDisabled) 246 else 237
    require(cases.size == expected, s"expected $expected official Thrust tests, found ${cases.size}")
    require(cases.map(_.name).distinct.size == cases.size, "duplicate official Thrust test names")
    cases
  }

  private def extension(path: Path): String = {
    val name  = path.getFileName.toString
    val index = name.lastIndexOf('.')
    if (index < 0) "" else name.drop(index + 1)
  }

  private def stripExtension(value: String): String = {
    val index = value.lastIndexOf('.')
    if (index < 0) value else value.take(index)
  }

  private def latestModification(root: Path): Long = {
    val stream = Files.walk(root)
    try stream.iterator.asScala.filter(Files.isRegularFile(_)).map(Files.getLastModifiedTime(_).toMillis).max
    finally stream.close()
  }

  private def findNvcc(): Option[Path] = {
    val explicit = sys.env.get("NVCC").map(Path.of(_)).filter(Files.isExecutable(_))
    val cudaHome = sys.env.get("CUDA_HOME").map(home => Path.of(home).resolve("bin/nvcc")).filter(Files.isExecutable(_))
    val fromPath = sys.env.getOrElse("PATH", "").split(java.io.File.pathSeparator).iterator
      .filter(_.nonEmpty)
      .map(directory => Path.of(directory).resolve("nvcc"))
      .filter(Files.isExecutable(_))
      .toVector
    explicit.orElse(cudaHome).orElse {
      val discovered = Vector(Path.of("/usr/local"), Path.of("/opt/nvidia/hpc_sdk"))
        .filter(Files.isDirectory(_))
        .flatMap { directory =>
          val stream = Files.walk(directory)
          try stream.iterator.asScala.filter(path =>
              path.getFileName.toString == "nvcc" && Files.isExecutable(path)
            ).toVector
          finally stream.close()
        }
      (fromPath ++ discovered).distinct
        .flatMap(path => nvccVersion(path).map(version => version -> path))
        .sortBy(_._1)
        .lastOption
        .map(_._2)
    }
  }

  private def nvccVersion(path: Path): Option[(Int, Int)] = {
    val text    = nvccVersionText(path)
    val Release = """release\s+(\d+)\.(\d+)""".r
    Release.findFirstMatchIn(text).map(result => result.group(1).toInt -> result.group(2).toInt)
  }

  private def nvccVersionText(path: Path): String = {
    val process = ProcessBuilder(path.toString, "--version").redirectErrorStream(true).start()
    if (!process.waitFor(30, TimeUnit.SECONDS)) {
      process.descendants().forEach { descendant =>
        val _ = descendant.destroyForcibly()
      }
      val _ = process.destroyForcibly()
      val _ = process.waitFor()
      throw IllegalStateException(s"timed out querying $path")
    }
    require(process.exitValue() == 0, s"failed to query $path")
    String(process.getInputStream.readAllBytes(), StandardCharsets.UTF_8)
  }

  private def findRoot(start: Path): Path =
    Iterator.iterate(start.toAbsolutePath.normalize)(_.getParent)
      .takeWhile(_ != null)
      .find(path =>
        Files.isDirectory(path.resolve("conformance/cuda/vendor/thrust/testing")) &&
          Files.isRegularFile(path.resolve("build.sbt"))
      )
      .getOrElse(throw IllegalStateException(s"cannot locate Gemstone root from $start"))

  private def buildGemstoneFacade(root: Path, libraryDir: Path): Unit = {
    val generated = root.resolve("runtime/cuda/generated/c")
    val dispatch  = root.resolve("runtime/cuda/src/main/c")
    val library   = libraryDir.resolve("libcuda.so.1")
    runChecked(
      Vector(
        "cc",
        "-std=c17",
        "-fPIC",
        "-Wall",
        "-Wextra",
        "-Werror",
        "-shared",
        generated.resolve("gemstone_cuda_rpc_client.c").toString,
        generated.resolve("gemstone_cuda_driver_stubs.c").toString,
        dispatch.resolve("gemstone_rpc_transport.c").toString,
        s"-I$generated",
        s"-I$dispatch",
        s"-I${cudaHeaderInclude(root)}",
        "-Wl,-soname,libcuda.so.1",
        s"-Wl,--version-script=${generated.resolve("gemstone_cuda.map")}",
        "-pthread",
        "-o",
        library.toString
      ),
      root,
      Map.empty
    )
    val link = libraryDir.resolve("libcuda.so")
    Files.deleteIfExists(link)
    val _ = Files.createSymbolicLink(link, library.getFileName)
  }

  private def cudaHeaderInclude(root: Path): Path = {
    val target = root.resolve("target/out")
    require(Files.isDirectory(target), s"prepared API reference root is absent: $target")
    val stream = Files.find(
      target,
      20,
      (path, attributes) =>
        attributes.isRegularFile && path.getFileName.toString == "cuda.h" &&
          path.toString.contains("/gemstone-codegen/isa-references/") &&
          path.toString.contains("/nvidia/cuda/usr/local/cuda-13.3/targets/")
    )
    try
      stream.iterator.asScala.toVector
        .maxByOption(path => Files.getLastModifiedTime(path).toMillis)
        .map(_.getParent)
        .getOrElse(throw IllegalStateException("pinned CUDA 13.3 headers were not prepared"))
    finally stream.close()
  }

  private def runChecked(command: Vector[String], directory: Path, environment: Map[String, String]): Unit = {
    val stdout = Files.createTempFile(directory, "command-", ".stdout")
    val stderr = Files.createTempFile(directory, "command-", ".stderr")
    val exit   = run(command, directory, environment, stdout, stderr)
    if (exit != 0) {
      val output = Files.readString(stdout, StandardCharsets.UTF_8)
      val errors = Files.readString(stderr, StandardCharsets.UTF_8)
      throw AssertionError(s"command failed ($exit): ${command.mkString(" ")}\nstdout:\n$output\nstderr:\n$errors")
    }
  }

  private def run(
      command: Vector[String],
      directory: Path,
      environment: Map[String, String],
      stdout: Path,
      stderr: Path
  ): Int = run(command, directory, Environment(environment, Set.empty), stdout, stderr)

  private def capture(command: Vector[String], directory: Path, environment: Environment): Captured = {
    val stdout = Files.createTempFile(directory, "command-", ".stdout")
    val stderr = Files.createTempFile(directory, "command-", ".stderr")
    Captured(run(command, directory, environment, stdout, stderr), stdout, stderr)
  }

  private def run(
      command: Vector[String],
      directory: Path,
      environment: Environment,
      stdout: Path,
      stderr: Path
  ): Int = {
    val builder = ProcessBuilder(command*)
      .directory(directory.toFile)
      .redirectOutput(stdout.toFile)
      .redirectError(stderr.toFile)
    environment.applyTo(builder.environment())
    val process = builder.start()
    if (process.waitFor(20, TimeUnit.MINUTES)) process.exitValue()
    else {
      process.descendants().forEach { descendant =>
        val _ = descendant.destroyForcibly()
      }
      val _ = process.destroyForcibly()
      val _ = process.waitFor()
      -1
    }
  }

  private def stripTrailingNewlines(value: String): String =
    value.reverse.dropWhile(character => character == '\n' || character == '\r').reverse
}
