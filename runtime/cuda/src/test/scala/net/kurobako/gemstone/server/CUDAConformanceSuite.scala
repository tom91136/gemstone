package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.launcher.ComputeAPI
import net.kurobako.gemstone.launcher.Environment
import net.kurobako.gemstone.launcher.GemstoneProcess
import net.kurobako.gemstone.launcher.RuntimeLayout

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.security.SecureRandom
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future as JavaFuture
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.isa.ptx.v9_3.Interpreter
import net.kurobako.gemstone.isa.ptx.v9_3.Argument
import net.kurobako.gemstone.isa.ptx.v9_3.ISA
import net.kurobako.gemstone.memory.ArrayCheckedMemory
import net.kurobako.gemstone.execution.Dimensions
import net.kurobako.gemstone.execution.LaunchGeometry
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*

class CUDAConformanceSuite extends munit.FunSuite {
  import CUDAConformanceSuite.*

  override val munitTimeout: Duration = 30.minutes

  private val root     = findRoot(Path.of(sys.props("user.dir")))
  private val allCases = discoverCases(root.resolve("conformance/cuda"))
  private val cases    = sys.env.get("GEMSTONE_CUDA_FILTER").filter(_.nonEmpty).fold(allCases) { expression =>
    val selected = allCases.filter(cudaCase => expression.r.findFirstIn(cudaCase.name).nonEmpty)
    require(selected.nonEmpty, s"GEMSTONE_CUDA_FILTER selected no CUDA cases: $expression")
    selected
  }
  private val targetRoot = Files.createDirectories(root.resolve("target/conformance"))
  private val runRoot    = Files.createTempDirectory(targetRoot, "cuda-scala.")
  private val libraryDir = runRoot.resolve("lib")
  private val jobs       =
    sys.env.get("GEMSTONE_TEST_JOBS").fold(Math.min(8, Runtime.getRuntime.availableProcessors))(_.toInt)
  private var executor   = Option.empty[ExecutorService]
  private var executions = Map.empty[String, JavaFuture[Unit]]

  private val work = {
    val conformance = cases.map(cudaCase => s"cuda/${cudaCase.name}" -> (() => runCase(cudaCase)))
    val semantics   = cases.filter(_.semantic.nonEmpty).map { cudaCase =>
      s"gemstone-ptx/${cudaCase.name}" -> (() => runSemanticCase(cudaCase))
    }
    conformance ++ semantics :+ ("gemstone/rejects-invalid-token" -> (() => rejectInvalidToken()))
  }

  override def beforeAll(): Unit = {
    require(jobs >= 1 && jobs <= 32, s"GEMSTONE_TEST_JOBS must be between 1 and 32, found $jobs")
    Files.createDirectories(libraryDir)
    buildGemstoneFacade(root, libraryDir)
    verifyGemstoneSurface(root, libraryDir, runRoot)
    val pool = Executors.newFixedThreadPool(jobs)
    executor = Some(pool)
    executions = work.map { case (name, run) =>
      val task: Callable[Unit] = () => run()
      name -> pool.submit(task)
    }.toMap
  }

  override def afterAll(): Unit =
    executor.foreach(_.shutdown())

  work.foreach { case (name, _) => test(name)(await(name)) }

  private def rejectInvalidToken(): Unit = {
    val token      = randomToken()
    val server     = GemstoneRPCServer.start(token)
    val executable = runRoot.resolve("rpc-auth-smoke")
    try {
      runChecked(
        Col(
          "cc",
          "-std=c17",
          "-Wall",
          "-Wextra",
          "-Werror",
          root.resolve("conformance/cuda/rpc-auth-smoke.c").toString,
          s"-L$libraryDir",
          s"-Wl,-rpath,$libraryDir",
          "-lcuda",
          "-o",
          executable.toString
        ),
        root,
        Map.empty
      )
      runChecked(
        Col(executable.toString),
        root,
        Map(
          "LD_PRELOAD"         -> libraryDir.resolve("libcuda.so.1").toString,
          "GEMSTONE_RPC_PORT"  -> server.port.toString,
          "GEMSTONE_RPC_TOKEN" -> "0" * 64
        )
      )
    } finally server.close()
  }

  private def await(name: String): Unit =
    try executions.getOrElse(name, fail(s"parallel test was not scheduled: $name")).get()
    catch {
      case error: ExecutionException => throw error.getCause
    }

  private def runCase(cudaCase: CUDACase): Unit = {
    val caseRoot = Files.createDirectories(runRoot.resolve("cuda").resolve(cudaCase.name))
    val output   = caseRoot.resolve("test")
    val cublas   = if (cudaCase.cublas) Some(findCUBLAS()) else None
    val nvcc = if (cudaCase.cublas) "" else findNvcc().getOrElse(fail("nvcc not found; set NVCC or CUDA_HOME")).toString
    val variables = Map(
      "nvcc"                 -> nvcc,
      "input"                -> cudaCase.source.toString,
      "output"               -> output.toString,
      "dependencies"         -> caseRoot.resolve("dependencies.d").toString,
      "cuda_vendor_includes" -> vendorIncludeFlags(root),
      "cuda_host_compat"     -> root.resolve("conformance/cuda/support/nvcc-libstdcxx-compat.h").toString,
      "cuda_driver_defines"  -> "",
      "cuda_driver_link"     -> "-lcuda",
      "cuda_runtime_link"    -> "",
      "cuda_host_compiler"   ->
        sys.env.get("GEMSTONE_CUDA_HOST_COMPILER").filter(_.nonEmpty).fold("")(value => s"--compiler-bindir $value"),
      "cuda_cublas_compiler" -> cublas.fold("")(_ => findCUBLASCompiler()),
      "cuda_cublas_include"  -> cublas.fold("")(_.includeFlag),
      "cuda_cublas_link"     -> cublas.fold("")(_.linkFlags)
    )
    require(cudaCase.commands.size == 2, s"${cudaCase.source} must contain exactly one compile and one run command")
    val compile = expand(cudaCase.commands.head.text, variables, cudaCase.source)
    runChecked(Col("bash", "-c", compile), caseRoot, Map.empty)
    val dependencies = caseRoot.resolve("dependencies.d")
    if (Files.isRegularFile(dependencies)) auditVendoredDependencies(cudaCase, dependencies, root)
    val execute           = Col("bash", "-c", expand(cudaCase.commands.last.text, variables, cudaCase.source))
    val vendorEnvironment = cublas.fold(Map.empty[String, String])(_.runtimeEnvironment(sys.env))
    if (oracleEnabled && (!cudaCase.cublas || !sys.env.get("GEMSTONE_CUBLAS_SKIP_ORACLE").contains("1"))) {
      val oracle = capture(
        execute,
        caseRoot,
        Environment(vendorEnvironment, GemstoneEnvironment -- vendorEnvironment.keySet)
      )
      assertResult(cudaCase, "oracle", cudaCase.commands.last, oracle)
    }
    val gemstone = GemstoneProcess.attested(
      ComputeAPI.CUDA,
      RuntimeLayout(cudaDriver = Some(libraryDir.resolve("libcuda.so.1"))),
      (sys.env - "GEMSTONE_NO_ORACLE") ++ vendorEnvironment
    )(environment => capture(execute, caseRoot, environment)).fold(
      error => fail(s"gemstone/${cudaCase.name}: ${error.message}"),
      identity
    )
    assertResult(cudaCase, "gemstone", cudaCase.commands.last, gemstone)
  }

  private def runSemanticCase(cudaCase: CUDACase): Unit = {
    val semantic = cudaCase.semantic.getOrElse(fail(s"${cudaCase.source} has no PTX semantic metadata"))
    val source   = Files.readString(cudaCase.source, StandardCharsets.UTF_8)
    val ptxStart = source.indexOf("R\"ptx(")
    val ptxEnd   = source.indexOf(")ptx\"", ptxStart + 6)
    assert(ptxStart >= 0 && ptxEnd > ptxStart, s"${cudaCase.source} has no embedded R\"ptx(...)ptx\" module")
    val ptx    = source.substring(ptxStart + 6, ptxEnd)
    val kernel = ISA.compile(ptx).fold(error => fail(s"${cudaCase.source}: $error"), identity)
    assertEquals(kernel.name, semantic.kernel)

    val names     = semantic.inputs.map(_._1) :+ semantic.output._1
    val stride    = Math.max(64, semantic.output._2.size * 4)
    val memory    = ArrayCheckedMemory.allocate(stride * names.size, provenance = cudaCase.name)
    val addresses = names.zipWithIndex.map { case (name, index) =>
      name -> memory.allocation.start.offset(index.toLong * stride)
    }.toMap
    semantic.inputs.foreach { case (name, values) =>
      values.zipWithIndex.foreach { case (value, index) =>
        memory.writeInt32(addresses(name).offset(index.toLong * 4L), value).fold(
          error => fail(error.message),
          identity
        )
      }
    }
    val _ = Interpreter.execute(
      kernel,
      LaunchGeometry.create(
        Dimensions.create(semantic.threads, 1, 1).fold(error => fail(error), identity),
        Dimensions.create(semantic.blocks, 1, 1).fold(error => fail(error), identity)
      ).fold(error => fail(error), identity),
      addresses.view.mapValues(address => Argument(address.value)).toMap,
      memory
    ).fold(error => fail(error.message), identity)
    val actual = semantic.output._2.indices.map { index =>
      memory.readInt32(addresses(semantic.output._1).offset(index.toLong * 4L)).fold(
        error => fail(error.message),
        identity
      )
    }.toVector
    assert(actual.sameElements(semantic.output._2))
  }

  private def assertResult(cudaCase: CUDACase, lane: String, command: Command, result: Captured): Unit = {
    val actual = stripTrailingNewlines(Files.readString(result.stdout, StandardCharsets.UTF_8))
    val errors = Files.readString(result.stderr, StandardCharsets.UTF_8)
    assertEquals(result.exit, 0, s"$lane/${cudaCase.name} failed\nstdout:\n$actual\nstderr:\n$errors")
    command.expected.foreach(expected => assertEquals(actual, expected, s"$lane/${cudaCase.name}\nstderr:\n$errors"))
  }

  private def oracleEnabled: Boolean = !sys.env.get("GEMSTONE_NO_ORACLE").exists { value =>
    Set("1", "true", "yes").contains(value.trim.toLowerCase)
  }
}

object CUDAConformanceSuite {
  private val GemstoneEnvironment = Set(
    "GEMSTONE_RPC_PORT",
    "GEMSTONE_RPC_TOKEN",
    "GEMSTONE_REQUIRED",
    "GEMSTONE_RECORD",
    "LD_PRELOAD"
  )
  private final case class Command(text: String, expected: Option[String])
  private final case class Captured(exit: Int, stdout: Path, stderr: Path)
  private final case class CUDACase(
      name: String,
      source: Path,
      cublas: Boolean,
      vendored: Set[String],
      commands: Col[Command],
      semantic: Option[SemanticCase]
  )
  private final case class CUBLASInstallation(include: Path, library: Path) {
    def includeFlag: String = s"-I${include.toString}"
    def linkFlags: String   = s"${library.toString} -L${library.getParent} -lcudart"
    def runtimeEnvironment(inherited: Map[String, String]): Map[String, String] =
      Map("LD_LIBRARY_PATH" ->
        (Vector(
          library.getParent.toString
        ) ++ inherited.get("LD_LIBRARY_PATH").toVector).mkString(java.io.File.pathSeparator))
  }
  private final case class SemanticCase(
      kernel: String,
      blocks: Int,
      threads: Int,
      inputs: Col[(String, Col[Int])],
      output: (String, Col[Int])
  )

  private def discoverCases(suiteRoot: Path): Col[CUDACase] = {
    val stream  = Files.walk(suiteRoot)
    val sources =
      try
        stream.iterator.asScala.filter(path =>
          Files.isRegularFile(path) && path.getFileName.toString.matches("check_.*\\.cu")
        )
          .toVector
          .sortBy(_.toString)
      finally stream.close()
    require(sources.nonEmpty, s"no CUDA conformance tests found under $suiteRoot")
    val cases = sources.map(parseCase)
    val names = cases.map(_.name)
    require(
      names.distinct.size == names.size,
      s"duplicate CUDA case names: ${names.diff(names.distinct).mkString(", ")}"
    )
    Col.from(cases)
  }

  private def parseCase(source: Path): CUDACase = {
    val CasePrefix     = "#pragma region case: "
    val CublasPrefix   = "#pragma region cublas"
    val VendoredPrefix = "#pragma region vendored: "
    val DoPrefix       = "#pragma region do: "
    val RequiresPrefix = "#pragma region requires: "
    val KernelPrefix   = "#pragma region ptx-kernel: "
    val ShapePrefix    = "#pragma region ptx-shape: "
    val InputPrefix    = "#pragma region ptx-input: "
    val OutputPrefix   = "#pragma region ptx-output: "
    var name           = Option.empty[String]
    var cublas         = false
    var vendored       = Set.empty[String]
    var commands       = Col.empty[Command]
    var kernel         = Option.empty[String]
    var shape          = Option.empty[(Int, Int)]
    var inputs         = Col.empty[(String, Col[Int])]
    var output         = Option.empty[(String, Col[Int])]

    Files.readAllLines(source, StandardCharsets.UTF_8).asScala.foreach {
      case line if line.startsWith(CasePrefix) =>
        require(name.isEmpty, s"$source declares more than one case")
        name = Some(line.drop(CasePrefix.length))
      case line if line == CublasPrefix            => cublas = true
      case line if line.startsWith(VendoredPrefix) =>
        vendored = line.drop(VendoredPrefix.length).split(',').iterator.map(_.trim).filter(_.nonEmpty).toSet
      case line if line.startsWith(DoPrefix) =>
        commands :+= Command(line.drop(DoPrefix.length), None)
      case line if line.startsWith(RequiresPrefix) =>
        require(commands.nonEmpty, s"$source has requires: before do:")
        require(commands.last.expected.isEmpty, s"$source has multiple requires: lines for one command")
        commands =
          commands.updated(commands.size - 1, commands.last.copy(expected = Some(line.drop(RequiresPrefix.length))))
      case line if line.startsWith(KernelPrefix) => kernel = Some(line.drop(KernelPrefix.length).trim)
      case line if line.startsWith(ShapePrefix)  =>
        line.drop(ShapePrefix.length).split(',').map(_.trim.toInt) match {
          case Array(blocks, threads) => shape = Some(blocks -> threads)
          case _                      => throw IllegalArgumentException(s"$source has invalid PTX shape")
        }
      case line if line.startsWith(InputPrefix) => inputs :+= parseSemanticValues(line.drop(InputPrefix.length), source)
      case line if line.startsWith(OutputPrefix) =>
        output = Some(parseSemanticValues(line.drop(OutputPrefix.length), source))
      case _ => ()
    }
    require(vendored.subsetOf(Set("thrust", "cub")), s"$source has invalid vendored dependencies: $vendored")
    require(commands.size >= 2, s"$source must declare at least compile and run commands")
    val semanticFields = Col(kernel.isDefined, shape.isDefined, inputs.nonEmpty, output.isDefined)
    require(
      semanticFields.forall(identity) || semanticFields.forall(!_),
      s"$source has incomplete PTX semantic metadata"
    )
    CUDACase(
      name.getOrElse(throw IllegalArgumentException(s"$source has no case pragma")),
      source,
      cublas,
      vendored,
      commands,
      kernel.flatMap(name =>
        shape.flatMap { case (blocks, threads) =>
          output.map(result => SemanticCase(name, blocks, threads, inputs, result))
        }
      )
    )
  }

  private def parseSemanticValues(value: String, source: Path): (String, Col[Int]) = {
    val separator = value.indexOf('=')
    require(separator > 0, s"$source has invalid PTX semantic values: $value")
    val name   = value.take(separator).trim
    val values = Col.from(value.drop(separator + 1).split(',').iterator.map(_.trim.toInt))
    require(name.nonEmpty && values.nonEmpty, s"$source has invalid PTX semantic values: $value")
    name -> values
  }

  private def vendorIncludeFlags(root: Path): String = {
    val vendor = root.resolve("conformance/cuda/vendor")
    Col(vendor.resolve("thrust"), vendor.resolve("cub")).map(path => s"-I$path").mkString(" ")
  }

  private def auditVendoredDependencies(cudaCase: CUDACase, dependencyFile: Path, root: Path): Unit =
    if (cudaCase.vendored.nonEmpty) {
      val dependencyText = Files.readString(dependencyFile, StandardCharsets.UTF_8).replace("\\\n", " ")
      val dependencies   = dependencyText.split("\\s+").iterator
        .map(_.stripSuffix("\\").stripSuffix(":"))
        .filter(_.startsWith("/"))
        .map(Path.of(_).normalize)
        .toVector
      val vendorRoot = root.resolve("conformance/cuda/vendor").normalize

      cudaCase.vendored.foreach { component =>
        val expectedRoot = vendorRoot.resolve(component).resolve(component)
        assert(
          dependencies.exists(_.startsWith(expectedRoot)),
          s"${cudaCase.source} did not compile against vendored $component headers; see $dependencyFile"
        )
        val leaked = dependencies.filter { dependency =>
          val normalized = dependency.toString.replace('\\', '/')
          normalized.contains(s"/include/$component/") && !dependency.startsWith(expectedRoot)
        }
        assert(
          leaked.isEmpty,
          s"${cudaCase.source} used non-vendored $component headers:\n${leaked.mkString("\n")}"
        )
      }
    }

  private def buildGemstoneFacade(root: Path, libraryDir: Path): Unit = {
    val generated = root.resolve("runtime/cuda/generated/c")
    val dispatch  = root.resolve("runtime/cuda/src/main/c")
    val library   = libraryDir.resolve("libcuda.so.1")
    runChecked(
      Col(
        "cc",
        "-std=c17",
        "-fPIC",
        "-Wall",
        "-Wextra",
        "-Werror",
        "-shared",
        generated.resolve("gemstone_cuda_rpc_client.c").toString,
        generated.resolve("gemstone_cuda_driver_stubs.c").toString,
        dispatch.resolve("gemstone_cuda_compat.c").toString,
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

  private def verifyGemstoneSurface(root: Path, libraryDir: Path, runRoot: Path): Unit = {
    val executable = runRoot.resolve("surface-smoke")
    runChecked(
      Col(
        "c++",
        "-std=c++17",
        "-Wall",
        "-Wextra",
        "-Werror",
        root.resolve("conformance/cuda/support/surface-smoke.cpp").toString,
        s"-I${root.resolve("runtime/cuda/generated/c")}",
        "-ldl",
        "-o",
        executable.toString
      ),
      root,
      Map.empty
    )
    val inherited = sys.env.get("LD_LIBRARY_PATH").filter(_.nonEmpty).map(value => s":$value").getOrElse("")
    runChecked(
      Col(executable.toString),
      root,
      Map("LD_LIBRARY_PATH" -> s"$libraryDir$inherited")
    )
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

  private def findNvcc(): Option[Path] = {
    val explicit = sys.env.get("NVCC").map(Path.of(_)).filter(Files.isExecutable(_))
    val cudaHome = sys.env.get("CUDA_HOME").map(home => Path.of(home).resolve("bin/nvcc")).filter(Files.isExecutable(_))
    val pathCandidates = sys.env.getOrElse("PATH", "").split(java.io.File.pathSeparator).iterator
      .filter(_.nonEmpty)
      .map(directory => Path.of(directory).resolve("nvcc"))
      .toVector
    explicit.orElse(cudaHome).orElse {
      val discovered = Col(Path.of("/usr/local"), Path.of("/opt/nvidia/hpc_sdk"))
        .filter(Files.isDirectory(_))
        .flatMap { directory =>
          val stream = Files.walk(directory)
          try stream.iterator.asScala.filter(path =>
              path.getFileName.toString == "nvcc" && Files.isExecutable(path)
            ).toVector
          finally stream.close()
        }
      (pathCandidates.filter(Files.isExecutable(_)) ++ discovered).distinct
        .flatMap(path => nvccVersion(path).map(version => version -> path))
        .sortBy(_._1)
        .lastOption
        .map(_._2)
    }
  }

  private def findCUBLAS(): CUBLASInstallation = {
    val explicitLibrary = sys.env.get("GEMSTONE_CUBLAS_LIBRARY").filter(_.nonEmpty).map(Path.of(_)).toVector
    val cudaHomes       =
      sys.env.get("GEMSTONE_CUDA_HOME").filter(_.nonEmpty).map(Path.of(_)).toVector ++
        sys.env.get("CUDA_HOME").filter(_.nonEmpty).map(Path.of(_)).toVector
    val standardHomes = Vector(Path.of("/usr/local/cuda")) ++ {
      val directory = Path.of("/usr/local")
      if (!Files.isDirectory(directory)) Vector.empty
      else {
        val stream = Files.list(directory)
        try stream.iterator.asScala.filter(path =>
            Files.isDirectory(path) && path.getFileName.toString.startsWith("cuda-")
          ).toVector.sortBy(_.toString).reverse
        finally stream.close()
      }
    }
    val homes      = (cudaHomes ++ standardHomes).distinct
    val candidates = explicitLibrary ++ homes.flatMap { home =>
      Vector(
        home.resolve("targets/x86_64-linux/lib/libcublas.so"),
        home.resolve("lib64/libcublas.so"),
        home.resolve("lib/libcublas.so")
      )
    }
    val library = candidates.find(Files.isRegularFile(_)).map(_.toAbsolutePath.normalize).getOrElse {
      throw IllegalStateException(
        "cuBLAS library is absent; set GEMSTONE_CUBLAS_LIBRARY to a libcublas.so from the installed CUDA toolkit"
      )
    }
    val explicitInclude   = sys.env.get("GEMSTONE_CUBLAS_INCLUDE").filter(_.nonEmpty).map(Path.of(_)).toVector
    val includeCandidates = explicitInclude ++ Vector(
      library.getParent.resolve("../include").normalize,
      library.getParent.resolve("../../include").normalize
    )
    val include = includeCandidates.find(path => Files.isRegularFile(path.resolve("cublas_v2.h"))).getOrElse {
      throw IllegalStateException(
        s"cuBLAS headers are absent; set GEMSTONE_CUBLAS_INCLUDE to the CUDA include directory (library: $library)"
      )
    }
    CUBLASInstallation(include, library)
  }

  private def findCUBLASCompiler(): String =
    sys.env.get("GEMSTONE_CUBLAS_CXX").filter(_.nonEmpty).orElse(sys.env.get("CXX").filter(_.nonEmpty)).getOrElse("c++")

  private def nvccVersion(path: Path): Option[(Int, Int)] = {
    val process = ProcessBuilder(path.toString, "--version").redirectErrorStream(true).start()
    if (!process.waitFor(30, TimeUnit.SECONDS)) {
      terminate(process)
      None
    } else if (process.exitValue() != 0) None
    else {
      val output  = String(process.getInputStream.readAllBytes(), StandardCharsets.UTF_8)
      val Release = """release\s+(\d+)\.(\d+)""".r
      Release.findFirstMatchIn(output).map(result => result.group(1).toInt -> result.group(2).toInt)
    }
  }

  private def findRoot(start: Path): Path =
    Iterator.iterate(start.toAbsolutePath.normalize)(_.getParent)
      .takeWhile(_ != null)
      .find(path =>
        Files.isDirectory(path.resolve("conformance/cuda")) && Files.isRegularFile(path.resolve("build.sbt"))
      )
      .getOrElse(throw IllegalStateException(s"cannot locate Gemstone root from $start"))

  private def randomToken(): String = {
    val bytes = Array.ofDim[Byte](32)
    SecureRandom().nextBytes(bytes)
    bytes.iterator.map(byte => f"${byte & 0xff}%02x").mkString
  }

  private def expand(command: String, variables: Map[String, String], source: Path): String = {
    val expanded = variables.foldLeft(command) { case (value, (name, replacement)) =>
      value.replace(s"{$name}", replacement)
    }
    require(
      !expanded.exists(character => character == '{' || character == '}'),
      s"$source has unresolved variables: $expanded"
    )
    expanded
  }

  private def capture(command: Col[String], directory: Path, environment: Environment): Captured = {
    val stdout = Files.createTempFile(directory, "command-", ".stdout")
    val stderr = Files.createTempFile(directory, "command-", ".stderr")
    Captured(run(command, directory, environment, stdout, stderr), stdout, stderr)
  }

  private def runChecked(command: Col[String], directory: Path, environment: Map[String, String]): Unit = {
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
      command: Col[String],
      directory: Path,
      environment: Map[String, String],
      stdout: Path,
      stderr: Path
  ): Int = run(command, directory, Environment(environment, Set.empty), stdout, stderr)

  private def run(
      command: Col[String],
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
      terminate(process)
      -1
    }
  }

  private def terminate(process: Process): Unit = {
    process.descendants().forEach { descendant =>
      val _ = descendant.destroyForcibly()
    }
    val _ = process.destroyForcibly()
    val _ = process.waitFor()
  }

  private def stripTrailingNewlines(value: String): String =
    value.reverse.dropWhile(character => character == '\n' || character == '\r').reverse
}
