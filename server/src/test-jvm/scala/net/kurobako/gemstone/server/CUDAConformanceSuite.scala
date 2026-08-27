package net.kurobako.gemstone.server

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.security.SecureRandom
import net.kurobako.gemstone.isa.ptx.v9_3.PTXInterpreter
import net.kurobako.gemstone.isa.ptx.v9_3.PTXArgument
import net.kurobako.gemstone.isa.ptx.v9_3.PTXParser
import net.kurobako.gemstone.memory.ArrayCheckedMemory
import net.kurobako.gemstone.scheduler.LaunchShape
import scala.jdk.CollectionConverters.*

class CUDAConformanceSuite extends munit.FunSuite {
  import CUDAConformanceSuite.*

  private val root       = findRoot(Path.of(sys.props("user.dir")))
  private val cases      = discoverCases(root.resolve("conformance/cuda"))
  private val targetRoot = Files.createDirectories(root.resolve("target/conformance"))
  private val runRoot    = Files.createTempDirectory(targetRoot, "cuda-scala.")
  private val libraryDir = runRoot.resolve("lib")
  private val token      = randomToken()
  private var server     = Option.empty[CUDARPCServer.RunningServer]

  override def beforeAll(): Unit = {
    Files.createDirectories(libraryDir)
    buildGemstoneFacade(root, libraryDir)
    verifyGemstoneSurface(root, libraryDir, runRoot)
    server = Some(CUDARPCServer.start(token))
  }

  override def afterAll(): Unit = server.foreach(_.close())

  for {
    cudaCase <- cases
    backend  <- Backend.values
    if cudaCase.targets.contains(backend.id)
  } test(s"${backend.id}/${cudaCase.name}") {
    runCase(cudaCase, backend)
  }

  cases.filter(_.semantic.nonEmpty).foreach { cudaCase =>
    test(s"gemstone-ptx/${cudaCase.name}") {
      runSemanticCase(cudaCase)
    }
  }

  test("gemstone/rejects-invalid-token") {
    val executable = runRoot.resolve("rpc-auth-smoke")
    runChecked(
      Vector(
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
      gemstoneEnvironment(token = "0" * 64, recording = None)
    )
    runChecked(Vector(executable.toString), root, gemstoneEnvironment(token = "0" * 64, recording = None))
  }

  private def runCase(cudaCase: CUDACase, backend: Backend): Unit = {
    val nvcc      = findNvcc().getOrElse(fail("nvcc not found; set NVCC or CUDA_HOME"))
    val caseRoot  = Files.createDirectories(runRoot.resolve(backend.id).resolve(cudaCase.name))
    val output    = caseRoot.resolve("test")
    val recording = backend match {
      case Backend.CUDA     => None
      case Backend.Gemstone => Some(caseRoot.resolve("api-recording.jsonl"))
    }
    recording.foreach(path => Files.writeString(path, "", StandardCharsets.UTF_8))
    val environment = backend match {
      case Backend.CUDA     => Map.empty[String, String]
      case Backend.Gemstone => gemstoneEnvironment(token, recording)
    }
    val variables = Map(
      "nvcc"                 -> nvcc.toString,
      "input"                -> cudaCase.source.toString,
      "output"               -> output.toString,
      "dependencies"         -> caseRoot.resolve("dependencies.d").toString,
      "cuda_vendor_includes" -> vendorIncludeFlags(root),
      "cuda_host_compat"     -> root.resolve("conformance/cuda/support/nvcc-libstdcxx-compat.h").toString,
      "cuda_driver_defines"  -> backend.driverDefines,
      "cuda_driver_link"     -> backend.driverLink(libraryDir),
      "cuda_runtime_link"    -> backend.runtimeLink(libraryDir)
    )

    cudaCase.commands.zipWithIndex.foreach { case (command, index) =>
      val expanded = variables.foldLeft(command.text) { case (value, (name, replacement)) =>
        value.replace(s"{$name}", replacement)
      }
      if (expanded.contains('{') || expanded.contains('}')) {
        fail(s"${cudaCase.source} contains an unresolved command variable: $expanded")
      }
      val stdout = caseRoot.resolve(s"stdout.$index")
      val stderr = caseRoot.resolve(s"stderr.$index")
      val exit   = run(Vector("bash", "-c", expanded), caseRoot, environment, stdout, stderr)
      val actual = stripTrailingNewlines(Files.readString(stdout, StandardCharsets.UTF_8))
      val errors = Files.readString(stderr, StandardCharsets.UTF_8)
      assertEquals(
        exit,
        0,
        s"${backend.id}/${cudaCase.name} failed\ncommand: $expanded\nstdout:\n$actual\nstderr:\n$errors"
      )
      command.expected.foreach(expected => assertEquals(actual, expected, s"command: $expanded\nstderr:\n$errors"))
      val dependencies = caseRoot.resolve("dependencies.d")
      if (Files.isRegularFile(dependencies)) auditVendoredDependencies(cudaCase, dependencies, root)
    }
    recording.foreach { path =>
      val records = Files.readAllLines(path, StandardCharsets.UTF_8).asScala.filter(_.nonEmpty)
      assert(records.nonEmpty, s"${backend.id}/${cudaCase.name} made no recorded RPC calls")
      assert(records.forall(_.contains("\"status\":0")), s"failed RPC recorded in $path")
    }
  }

  private def runSemanticCase(cudaCase: CUDACase): Unit = {
    val semantic = cudaCase.semantic.getOrElse(fail(s"${cudaCase.source} has no PTX semantic metadata"))
    val source   = Files.readString(cudaCase.source, StandardCharsets.UTF_8)
    val ptxStart = source.indexOf("R\"ptx(")
    val ptxEnd   = source.indexOf(")ptx\"", ptxStart + 6)
    assert(ptxStart >= 0 && ptxEnd > ptxStart, s"${cudaCase.source} has no embedded R\"ptx(...)ptx\" module")
    val ptx    = source.substring(ptxStart + 6, ptxEnd)
    val kernel = PTXParser.parse(ptx).fold(error => fail(s"${cudaCase.source}: $error"), identity)
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
    val _ = PTXInterpreter.execute(
      kernel,
      LaunchShape.create(semantic.blocks, semantic.threads).fold(error => fail(error), identity),
      addresses.view.mapValues(address => PTXArgument(address.value)).toMap,
      memory
    ).fold(error => fail(error.message), identity)
    val actual = semantic.output._2.indices.map { index =>
      memory.readInt32(addresses(semantic.output._1).offset(index.toLong * 4L)).fold(
        error => fail(error.message),
        identity
      )
    }.toVector
    assertEquals(actual, semantic.output._2)
  }

  private def gemstoneEnvironment(token: String, recording: Option[Path]): Map[String, String] = {
    val currentLibraryPath = sys.env.get("LD_LIBRARY_PATH").filter(_.nonEmpty).map(value => s":$value").getOrElse("")
    Map(
      "LD_LIBRARY_PATH"    -> s"$libraryDir$currentLibraryPath",
      "GEMSTONE_RPC_PORT"  -> server.fold(throw new IllegalStateException("server not started"))(_.port.toString),
      "GEMSTONE_RPC_TOKEN" -> token
    ) ++ recording.map(path => "GEMSTONE_RECORD" -> path.toString)
  }
}

object CUDAConformanceSuite {
  private final case class Command(text: String, expected: Option[String])
  private final case class CUDACase(
      name: String,
      source: Path,
      targets: Set[String],
      vendored: Set[String],
      commands: Vector[Command],
      semantic: Option[SemanticCase]
  )
  private final case class SemanticCase(
      kernel: String,
      blocks: Int,
      threads: Int,
      inputs: Vector[(String, Vector[Int])],
      output: (String, Vector[Int])
  )

  private enum Backend(val id: String, val driverDefines: String) {
    case CUDA     extends Backend("cuda", "")
    case Gemstone extends Backend("gemstone", "-DGEMSTONE_CUDA_ABI=1")

    def driverLink(libraryDir: Path): String = this match {
      case CUDA     => "-lcuda"
      case Gemstone => s"-L$libraryDir -Xlinker=-rpath -Xlinker=$libraryDir -lcuda"
    }

    def runtimeLink(libraryDir: Path): String = this match {
      case CUDA     => ""
      case Gemstone => s"--cudart=none -L$libraryDir -Xlinker=-rpath -Xlinker=$libraryDir -lcuda"
    }
  }

  private def discoverCases(suiteRoot: Path): Vector[CUDACase] = {
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
    cases
  }

  private def parseCase(source: Path): CUDACase = {
    val CasePrefix     = "#pragma region case: "
    val TargetsPrefix  = "#pragma region targets: "
    val VendoredPrefix = "#pragma region vendored: "
    val DoPrefix       = "#pragma region do: "
    val RequiresPrefix = "#pragma region requires: "
    val KernelPrefix   = "#pragma region ptx-kernel: "
    val ShapePrefix    = "#pragma region ptx-shape: "
    val InputPrefix    = "#pragma region ptx-input: "
    val OutputPrefix   = "#pragma region ptx-output: "
    var name           = Option.empty[String]
    var targets        = Backend.values.map(_.id).toSet
    var vendored       = Set.empty[String]
    var commands       = Vector.empty[Command]
    var kernel         = Option.empty[String]
    var shape          = Option.empty[(Int, Int)]
    var inputs         = Vector.empty[(String, Vector[Int])]
    var output         = Option.empty[(String, Vector[Int])]

    Files.readAllLines(source, StandardCharsets.UTF_8).asScala.foreach {
      case line if line.startsWith(CasePrefix) =>
        require(name.isEmpty, s"$source declares more than one case")
        name = Some(line.drop(CasePrefix.length))
      case line if line.startsWith(TargetsPrefix) =>
        targets = line.drop(TargetsPrefix.length).split(',').iterator.map(_.trim).filter(_.nonEmpty).toSet
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
        line.drop(ShapePrefix.length).split(',').map(_.trim.toInt).toVector match {
          case Vector(blocks, threads) => shape = Some(blocks -> threads)
          case _                       => throw new IllegalArgumentException(s"$source has invalid PTX shape")
        }
      case line if line.startsWith(InputPrefix) => inputs :+= parseSemanticValues(line.drop(InputPrefix.length), source)
      case line if line.startsWith(OutputPrefix) =>
        output = Some(parseSemanticValues(line.drop(OutputPrefix.length), source))
      case _ => ()
    }
    require(
      targets.nonEmpty && targets.subsetOf(Backend.values.map(_.id).toSet),
      s"$source has invalid targets: $targets"
    )
    require(vendored.subsetOf(Set("thrust", "cub")), s"$source has invalid vendored dependencies: $vendored")
    require(commands.size >= 2, s"$source must declare at least compile and run commands")
    val semanticFields = Vector(kernel.isDefined, shape.isDefined, inputs.nonEmpty, output.isDefined)
    require(
      semanticFields.forall(identity) || semanticFields.forall(!_),
      s"$source has incomplete PTX semantic metadata"
    )
    CUDACase(
      name.getOrElse(throw new IllegalArgumentException(s"$source has no case pragma")),
      source,
      targets,
      vendored,
      commands,
      kernel.flatMap(name =>
        shape.flatMap { case (blocks, threads) =>
          output.map(result => SemanticCase(name, blocks, threads, inputs, result))
        }
      )
    )
  }

  private def parseSemanticValues(value: String, source: Path): (String, Vector[Int]) = {
    val separator = value.indexOf('=')
    require(separator > 0, s"$source has invalid PTX semantic values: $value")
    val name   = value.take(separator).trim
    val values = value.drop(separator + 1).split(',').toVector.map(_.trim.toInt)
    require(name.nonEmpty && values.nonEmpty, s"$source has invalid PTX semantic values: $value")
    name -> values
  }

  private def vendorIncludeFlags(root: Path): String = {
    val vendor = root.resolve("conformance/cuda/vendor")
    Vector(vendor.resolve("thrust"), vendor.resolve("cub")).map(path => s"-I$path").mkString(" ")
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
      Vector(
        "c++",
        "-std=c++17",
        "-Wall",
        "-Wextra",
        "-Werror",
        root.resolve("conformance/cuda/support/surface-smoke.cpp").toString,
        s"-I${root.resolve("generated/c")}",
        "-ldl",
        "-o",
        executable.toString
      ),
      root,
      Map.empty
    )
    val inherited = sys.env.get("LD_LIBRARY_PATH").filter(_.nonEmpty).map(value => s":$value").getOrElse("")
    runChecked(
      Vector(executable.toString),
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
        .getOrElse(throw new IllegalStateException("pinned CUDA 13.3 headers were not prepared"))
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
      val discovered = Vector(Path.of("/usr/local"), Path.of("/opt/nvidia/hpc_sdk"))
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

  private def nvccVersion(path: Path): Option[(Int, Int)] = {
    val process = new ProcessBuilder(path.toString, "--version").redirectErrorStream(true).start()
    val output  = new String(process.getInputStream.readAllBytes(), StandardCharsets.UTF_8)
    if (process.waitFor() != 0) None
    else {
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
      .getOrElse(throw new IllegalStateException(s"cannot locate Gemstone root from $start"))

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
