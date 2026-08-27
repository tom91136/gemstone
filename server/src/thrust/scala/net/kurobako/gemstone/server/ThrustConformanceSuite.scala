package net.kurobako.gemstone.server

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.security.SecureRandom
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*
import scala.util.control.NonFatal

/** Runs the unmodified upstream Thrust 1.15.0 CPP/CUDA unit-test translation units. */
class ThrustConformanceSuite extends munit.FunSuite {
  import ThrustConformanceSuite.*

  override val munitTimeout: Duration = 30.minutes

  private val root       = findRoot(Path.of(sys.props("user.dir")))
  private val vendorRoot = root.resolve("conformance/cuda/vendor")
  private val thrustRoot = vendorRoot.resolve("thrust")
  private val testing    = thrustRoot.resolve("testing")
  private val targetRoot = Files.createDirectories(root.resolve("target/conformance/thrust-1.15.0"))
  private val libraryDir = Files.createDirectories(targetRoot.resolve("gemstone-lib"))
  private val token      = randomToken()
  private val nvcc       = findNvcc().getOrElse(throw new IllegalStateException("nvcc not found; set NVCC or CUDA_HOME"))
  private val sourceEpoch = latestModification(vendorRoot)
  private val selectedBackends = selected("GEMSTONE_THRUST_BACKENDS", Backend.values.map(_.id).toSet, Set("cuda"))
  private val selectedCases = {
    val discovered = discoverCases(testing, sys.env.get("GEMSTONE_THRUST_INCLUDE_DISABLED").contains("1"))
    val pattern    = sys.env.get("GEMSTONE_THRUST_FILTER").filter(_.nonEmpty).map(_.r)
    pattern.fold(discovered)(regex => discovered.filter(testCase => regex.findFirstIn(testCase.name).nonEmpty))
  }
  private val sizes = sys.env.getOrElse("GEMSTONE_THRUST_SIZES", "tiny")
  private val unitFilters = sys.env.get("GEMSTONE_THRUST_UNIT_FILTER").toVector
    .flatMap(_.split(',').iterator.map(_.trim).filter(_.nonEmpty))
  private val architecture = sys.env.getOrElse("GEMSTONE_THRUST_ARCH", "native")
  private val compileJobs = sys.env.get("GEMSTONE_THRUST_JOBS").fold(Math.min(4, Runtime.getRuntime.availableProcessors))(
    _.toInt
  )
  private val compileFailures = new ConcurrentHashMap[String, Throwable]()
  private var server          = Option.empty[CUDARPCServer.RunningServer]

  require(selectedBackends.nonEmpty, "GEMSTONE_THRUST_BACKENDS selected no backends")
  require(selectedCases.nonEmpty, "GEMSTONE_THRUST_FILTER selected no tests")

  override def beforeAll(): Unit = {
    require(Set("tiny", "small", "medium", "default", "large", "huge", "epic", "max").contains(sizes),
      s"invalid GEMSTONE_THRUST_SIZES value: $sizes")
    require(compileJobs >= 1 && compileJobs <= 32, s"GEMSTONE_THRUST_JOBS must be between 1 and 32, found $compileJobs")
    if (selectedBackends.contains(Backend.Gemstone)) {
      buildGemstoneFacade(root, libraryDir)
      server = Some(CUDARPCServer.start(token))
    }
    buildFramework()
    compileCases()
  }

  override def afterAll(): Unit = server.foreach(_.close())

  for {
    testCase <- selectedCases
    backend  <- Backend.values
    if selectedBackends.contains(backend)
  } test(s"${backend.id}/${testCase.name}") {
    runCase(testCase, backend)
  }

  private def buildFramework(): Unit = {
    val frameworkRoot = Files.createDirectories(targetRoot.resolve("framework"))
    val sources = Vector(
      testing.resolve("unittest/testframework.cu")      -> "testframework.o",
      testing.resolve("unittest/cuda/testframework.cu") -> "cuda-testframework.o"
    )
    sources.foreach { case (source, outputName) =>
      val output    = frameworkRoot.resolve(outputName)
      val signature = compileSignature(source, Backend.CUDA, rdc = false, Vector.empty)
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
    val work = for {
      testCase <- selectedCases
      backend  <- Backend.values
      if selectedBackends.contains(backend)
    } yield testCase -> backend
    val completed = new AtomicInteger(0)
    val executor  = Executors.newFixedThreadPool(compileJobs)
    try {
      val futures = work.map { case (testCase, backend) =>
        executor.submit(new Callable[Unit] {
          override def call(): Unit = {
            try compileCase(testCase, backend)
            catch {
              case NonFatal(error) => val _ = compileFailures.put(compileKey(testCase, backend), error)
            }
            val count = completed.incrementAndGet()
            if (count % 10 == 0 || count == work.size) println(s"[thrust] compiled $count/${work.size}")
          }
        })
      }
      futures.foreach(_.get())
    } finally executor.shutdown()
  }

  private def compileKey(testCase: ThrustCase, backend: Backend): String = s"${backend.id}/${testCase.name}"

  private def compileCase(testCase: ThrustCase, backend: Backend): Unit = {
    val caseRoot = Files.createDirectories(targetRoot.resolve(backend.id).resolve(testCase.name))
    val output   = caseRoot.resolve("test")
    val dependencyFile = caseRoot.resolve("dependencies.d")
    val frameworkRoot  = targetRoot.resolve("framework")
    val commonFramework = frameworkRoot.resolve("testframework.o")
    val cudaFramework   = frameworkRoot.resolve("cuda-testframework.o")
    val sourceFlags =
      (if (testCase.rdc) Vector("-rdc=true") else Vector.empty) ++
        (if (testCase.perThreadStream) Vector("--default-stream=per-thread") else Vector.empty)
    val signature = compileSignature(testCase.source, backend, testCase.rdc, sourceFlags)

    compileIfNeeded(output, signature) {
      runChecked(
        nvccCommand ++ includeFlags ++ sourceFlags ++ Vector(
          "-MD",
          "-MF",
          dependencyFile.toString,
          testCase.source.toString,
          commonFramework.toString,
          cudaFramework.toString
        ) ++ backend.linkFlags(libraryDir) ++ Vector("-o", output.toString),
        caseRoot,
        backend.environment(libraryDir, server, token)
      )
    }
    auditDependencies(dependencyFile)
  }

  private def runCase(testCase: ThrustCase, backend: Backend): Unit = {
    Option(compileFailures.get(compileKey(testCase, backend))).foreach(error => throw error)
    val caseRoot = targetRoot.resolve(backend.id).resolve(testCase.name)
    val output   = caseRoot.resolve("test")
    val stdout = caseRoot.resolve("stdout")
    val stderr = caseRoot.resolve("stderr")
    val recording = caseRoot.resolve("api-recording.jsonl")
    Files.writeString(recording, "", StandardCharsets.UTF_8)
    val exit = run(
      Vector(output.toString) ++ unitFilters ++ Vector(s"--sizes=$sizes", "--concise"),
      caseRoot,
      backend.environment(libraryDir, server, token) ++ Map("GEMSTONE_RECORD" -> recording.toString),
      stdout,
      stderr
    )
    val actual = stripTrailingNewlines(Files.readString(stdout, StandardCharsets.UTF_8))
    val errors = Files.readString(stderr, StandardCharsets.UTF_8)
    assertEquals(exit, 0, s"${backend.id}/${testCase.name} failed\nstdout:\n$actual\nstderr:\n$errors")
    assertEquals(actual, "PASSED", s"${backend.id}/${testCase.name}\nstderr:\n$errors")
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

  private def compileSignature(source: Path, backend: Backend, rdc: Boolean, extraFlags: Vector[String]): String =
    Vector(
      "thrust=1.15.0",
      "commit=67d6622e48ea44cfaa462d44772cc7663026c952",
      s"nvcc=$nvcc",
      s"nvcc-version=${nvccVersionText(nvcc)}",
      s"source=${testing.relativize(source)}",
      s"source-epoch=$sourceEpoch",
      s"backend=${backend.id}",
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
    val text = Files.readString(dependencyFile, StandardCharsets.UTF_8).replace("\\\n", " ")
    val dependencies = text.split("\\s+").iterator
      .map(_.stripSuffix("\\").stripSuffix(":"))
      .filter(_.startsWith("/"))
      .map(Path.of(_).normalize)
      .toVector
    val expectedThrust = thrustRoot.resolve("thrust").normalize
    assert(dependencies.exists(_.startsWith(expectedThrust)), s"no vendored Thrust header in $dependencyFile")
    Vector("thrust", "cub").foreach { component =>
      val expected = vendorRoot.resolve(component).resolve(component).normalize
      val leaked = dependencies.filter { dependency =>
        val normalized = dependency.toString.replace('\\', '/')
        normalized.contains(s"/include/$component/") && !dependency.startsWith(expected)
      }
      assert(leaked.isEmpty, s"non-vendored $component headers in $dependencyFile:\n${leaked.mkString("\n")}")
    }
  }
}

object ThrustConformanceSuite {
  private final case class ThrustCase(name: String, source: Path, rdc: Boolean, perThreadStream: Boolean)

  private enum Backend(val id: String) {
    case CUDA     extends Backend("cuda")
    case Gemstone extends Backend("gemstone")

    def linkFlags(libraryDir: Path): Vector[String] = this match {
      case CUDA     => Vector.empty
      case Gemstone => Vector(
          "--cudart=none",
          s"-L$libraryDir",
          s"-Xlinker=-rpath",
          s"-Xlinker=$libraryDir",
          "-lcuda"
        )
    }

    def environment(
        libraryDir: Path,
        server: Option[CUDARPCServer.RunningServer],
        token: String
    ): Map[String, String] = this match {
      case CUDA => Map.empty
      case Gemstone =>
        val current = sys.env.get("LD_LIBRARY_PATH").filter(_.nonEmpty).map(value => s":$value").getOrElse("")
        Map(
          "LD_LIBRARY_PATH"    -> s"$libraryDir$current",
          "GEMSTONE_RPC_PORT"  -> server.fold(throw new IllegalStateException("server not started"))(_.port.toString),
          "GEMSTONE_RPC_TOKEN" -> token
        )
    }
  }

  private def discoverCases(testing: Path, includeDisabled: Boolean): Vector[ThrustCase] = {
    val stream = Files.walk(testing)
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

  private def selected(name: String, allowed: Set[String], default: Set[String]): Set[Backend] = {
    val names = sys.env.get(name).map(_.split(',').iterator.map(_.trim).filter(_.nonEmpty).toSet).getOrElse(default)
    require(names.subsetOf(allowed), s"$name contains invalid values: ${names.diff(allowed).mkString(",")}")
    Backend.values.filter(backend => names.contains(backend.id)).toSet
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
    val process = new ProcessBuilder(path.toString, "--version").redirectErrorStream(true).start()
    val output  = new String(process.getInputStream.readAllBytes(), StandardCharsets.UTF_8)
    require(process.waitFor() == 0, s"failed to query $path")
    output
  }

  private def findRoot(start: Path): Path =
    Iterator.iterate(start.toAbsolutePath.normalize)(_.getParent)
      .takeWhile(_ != null)
      .find(path =>
        Files.isDirectory(path.resolve("conformance/cuda/vendor/thrust/testing")) &&
          Files.isRegularFile(path.resolve("build.sbt"))
      )
      .getOrElse(throw new IllegalStateException(s"cannot locate Gemstone root from $start"))

  private def buildGemstoneFacade(root: Path, libraryDir: Path): Unit = {
    val generated = root.resolve("generated/c")
    val dispatch  = root.resolve("native/dispatch")
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
        dispatch.resolve("gemstone_rpc_boundary.c").toString,
        s"-I$generated",
        s"-I$dispatch",
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

  private def randomToken(): String = {
    val bytes = new Array[Byte](32)
    new SecureRandom().nextBytes(bytes)
    bytes.iterator.map(byte => f"${byte & 0xff}%02x").mkString
  }

  private def runChecked(command: Vector[String], directory: Path, environment: Map[String, String]): Unit = {
    val stdout = Files.createTempFile(directory, "command-", ".stdout")
    val stderr = Files.createTempFile(directory, "command-", ".stderr")
    val exit   = run(command, directory, environment, stdout, stderr)
    if (exit != 0) {
      val output = Files.readString(stdout, StandardCharsets.UTF_8)
      val errors = Files.readString(stderr, StandardCharsets.UTF_8)
      throw new AssertionError(s"command failed ($exit): ${command.mkString(" ")}\nstdout:\n$output\nstderr:\n$errors")
    }
  }

  private def run(
      command: Vector[String],
      directory: Path,
      environment: Map[String, String],
      stdout: Path,
      stderr: Path
  ): Int = {
    val builder = new ProcessBuilder(command*)
      .directory(directory.toFile)
      .redirectOutput(stdout.toFile)
      .redirectError(stderr.toFile)
    environment.foreach { case (name, value) => builder.environment().put(name, value) }
    builder.start().waitFor()
  }

  private def stripTrailingNewlines(value: String): String =
    value.reverse.dropWhile(character => character == '\n' || character == '\r').reverse
}
