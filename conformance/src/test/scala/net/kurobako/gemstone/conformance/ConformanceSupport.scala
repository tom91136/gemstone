package net.kurobako.gemstone.conformance

import net.kurobako.gemstone.launcher.ComputeAPI
import net.kurobako.gemstone.launcher.Environment
import net.kurobako.gemstone.launcher.GemstoneProcess
import net.kurobako.gemstone.launcher.RuntimeLayout

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpClient.Redirect
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.util.Properties
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*
import upickle.default.ReadWriter
import upickle.default.read
import upickle.default.readwriter

private[conformance] object ConformanceSupport {
  private val GemstoneEnvironment = Set(
    "GEMSTONE_RPC_PORT",
    "GEMSTONE_RPC_TOKEN",
    "GEMSTONE_REQUIRED",
    "GEMSTONE_RECORD",
    "GEMSTONE_CUDA_DRIVER",
    "GEMSTONE_HIP_RUNTIME",
    "GEMSTONE_HSA_RUNTIME",
    "GEMSTONE_AMDGPU_TARGETS",
    "GEMSTONE_AMDGPU_ORACLE_TARGET",
    "GEMSTONE_AMDGPU_TARGET",
    "GEMSTONE_NO_ORACLE",
    "GEMSTONE_OPENCL_ICD",
    "GEMSTONE_OPENCL_LOADER",
    "GEMSTONE_LEVELZERO_DRIVER",
    "HSA_ENABLE_DTIF",
    "HSA_TOOLS_LIB",
    "LD_PRELOAD",
    "OCL_ICD_VENDORS",
    "OCL_ICD_FILENAMES",
    "ZE_ENABLE_ALT_DRIVERS",
    "ONEAPI_DEVICE_SELECTOR",
    "SYCL_DEVICE_FILTER"
  )

  final case class Pin(commit: String, sha256: String)
  final case class CTestProperty(name: String, value: ujson.Value) derives ReadWriter
  final case class CTestCase(
      name: String,
      command: Option[Vector[String]] = None,
      properties: Vector[CTestProperty] = Vector.empty
  ) derives ReadWriter {
    def property(name: String): Option[ujson.Value] = properties.find(_.name == name).map(_.value)
    def disabled: Boolean                           = property("DISABLED").contains(ujson.Bool(true))
    def workingDirectory: Path                      = property("WORKING_DIRECTORY") match {
      case Some(ujson.Str(value)) => Path.of(value)
      case _                      => throw IllegalArgumentException(s"CTest case $name has no working directory")
    }
  }
  final case class CTestInventory(tests: Vector[CTestCase]) derives ReadWriter
  final case class SourceCommand(text: String, expected: Option[String])
  final case class SourceCase(name: String, source: Path, commands: Vector[SourceCommand])

  final case class AMDGPUTargetMatrix(targets: Vector[String], oracleTarget: String) {
    val targetList: String  = targets.mkString(",")
    val targetFlags: String = targets.map(target => s"--offload-arch=$target").mkString(" ")
  }

  object AMDGPUTargetMatrix {
    private val DefaultTarget = "gfx900"
    private val TargetPattern = "gfx[0-9]+[a-z]?".r

    def fromEnvironment(environment: Map[String, String]): AMDGPUTargetMatrix = {
      val rawTargets = environment.get("GEMSTONE_AMDGPU_TARGETS").filter(_.nonEmpty).getOrElse(DefaultTarget)
      val targets    = rawTargets.split(',').iterator.map(_.trim.toLowerCase).filter(_.nonEmpty).toVector
      require(targets.nonEmpty, "GEMSTONE_AMDGPU_TARGETS must contain at least one target")
      require(targets.distinct.size == targets.size, s"GEMSTONE_AMDGPU_TARGETS contains duplicates: $rawTargets")
      require(
        targets.forall(_.matches(TargetPattern.regex)),
        s"invalid GFX target in GEMSTONE_AMDGPU_TARGETS: $rawTargets"
      )
      val oracleTarget =
        environment.get(
          "GEMSTONE_AMDGPU_ORACLE_TARGET"
        ).filter(_.nonEmpty).map(_.trim.toLowerCase).getOrElse(DefaultTarget)
      require(
        oracleTarget.matches(TargetPattern.regex),
        s"invalid GFX target in GEMSTONE_AMDGPU_ORACLE_TARGET: $oracleTarget"
      )
      require(
        targets.contains(oracleTarget),
        s"oracle target $oracleTarget is absent from GEMSTONE_AMDGPU_TARGETS"
      )
      AMDGPUTargetMatrix(targets, oracleTarget)
    }
  }

  def amdgpuTargetMatrix: AMDGPUTargetMatrix = AMDGPUTargetMatrix.fromEnvironment(sys.env)

  val root: Path   = findRoot(Path.of(sys.props("user.dir")))
  val target: Path = Files.createDirectories(root.resolve("target/conformance"))
  val cache: Path  = Files.createDirectories(
    sys.env.get("XDG_CACHE_HOME").filter(_.nonEmpty).map(Path.of(_))
      .getOrElse(Path.of(sys.props("user.home"), ".cache"))
      .resolve("gemstone/conformance")
  )

  lazy val openCLLoader: Path = {
    val configured = sys.env.get("GEMSTONE_OPENCL_LOADER").filter(_.nonEmpty).map(Path.of(_))
    val candidates = Vector(
      Path.of("/usr/lib64/libOpenCL.so.1"),
      Path.of("/usr/lib64/libOpenCL.so"),
      Path.of("/lib64/libOpenCL.so.1"),
      Path.of("/usr/lib/x86_64-linux-gnu/libOpenCL.so.1"),
      Path.of("/usr/lib/aarch64-linux-gnu/libOpenCL.so.1")
    )
    configured.filter(Files.isRegularFile(_)).orElse(candidates.find(Files.isRegularFile(_))).getOrElse {
      throw IllegalStateException("OpenCL loader is absent; set GEMSTONE_OPENCL_LOADER")
    }
  }

  private val pins = {
    val properties = Properties()
    val stream     = Option(getClass.getResourceAsStream("/gemstone-conformance.properties"))
      .getOrElse(throw IllegalStateException("generated conformance pins are absent"))
    try properties.load(stream)
    finally stream.close()
    properties
  }

  def pin(name: String): Pin        = Pin(pins.getProperty(s"$name.commit"), pins.getProperty(s"$name.sha256"))
  def version(name: String): String = pins.getProperty(s"$name.version")
  def sha256(name: String): String  = pins.getProperty(s"$name.sha256")

  def prepareArchive(name: String, url: String, sha256: String): Path = {
    val archiveSuffix =
      if (url.endsWith(".tar.xz")) ".tar.xz"
      else if (url.endsWith(".zip")) ".zip"
      else ".tar.gz"
    val archive = cache.resolve(s"$name$archiveSuffix")
    if (!Files.isRegularFile(archive) || digest(archive) != sha256) download(url, archive)
    require(digest(archive) == sha256, s"$name archive hash differs from its pin")
    val source   = target.resolve(s"sources/$name")
    val marker   = source.resolve(".gemstone-prepared")
    val identity = s"$sha256\n"
    if (!Files.isRegularFile(marker) || Files.readString(marker) != identity) {
      recreateDirectory(source)
      if (archiveSuffix == ".zip") {
        val expanded = source.resolve("expanded")
        Files.createDirectories(expanded)
        runChecked(Vector("unzip", "-q", archive.toString, "-d", expanded.toString), root)
        val children  = Files.list(expanded)
        val extracted = try children.iterator.asScala.toVector
        finally children.close()
        require(extracted.size == 1 && Files.isDirectory(extracted.head), s"$name ZIP has no single source root")
        val entries = Files.list(extracted.head)
        try entries.iterator.asScala.foreach(path => Files.move(path, source.resolve(path.getFileName)))
        finally entries.close()
        Files.delete(extracted.head)
        Files.delete(expanded)
      } else
        runChecked(Vector("tar", "-xf", archive.toString, "-C", source.toString, "--strip-components=1"), root)
      val _ = Files.writeString(marker, identity, StandardCharsets.UTF_8)
    }
    source
  }

  def configure(
      source: Path,
      build: Path,
      arguments: Vector[String],
      environment: Map[String, String] = Map.empty
  ): Unit = {
    Files.createDirectories(build)
    runChecked(
      Vector("cmake", "-S", source.toString, "-B", build.toString, "-G", "Ninja") ++ arguments,
      root,
      environment
    )
  }

  def build(
      build: Path,
      targetName: String,
      environment: Map[String, String] = Map.empty
  ): Unit =
    runChecked(
      Vector("cmake", "--build", build.toString, "--parallel", "--target", targetName),
      root,
      environment
    )

  def inventory(build: Path): Vector[CTestCase] = {
    val result = run(Vector("ctest", "--test-dir", build.toString, "--show-only=json-v1"), root)
    require(result.exit == 0, result.diagnostic)
    read[CTestInventory](result.stdout).tests
  }

  def runCTest(build: Path, oneBasedIndex: Int, environment: Map[String, String]): Unit =
    runChecked(
      Vector(
        "ctest",
        "--test-dir",
        build.toString,
        "--output-on-failure",
        "--no-tests=error",
        "-I",
        s"$oneBasedIndex,$oneBasedIndex"
      ),
      root,
      environment
    )

  def runChecked(
      command: Vector[String],
      directory: Path,
      environment: Map[String, String] = Map.empty
  ): Unit = {
    val result = run(command, directory, environment)
    require(result.exit == 0, result.diagnostic)
  }

  def select(cases: Vector[(Int, String)], prefix: String): Vector[(Int, String)] = {
    val filtered = sys.env.get(s"GEMSTONE_${prefix}_FILTER").filter(_.nonEmpty).fold(cases) { expression =>
      val pattern = expression.r
      cases.filter { case (_, name) => pattern.findFirstIn(name).nonEmpty }
    }
    val selected = sys.env.get(s"GEMSTONE_${prefix}_SHARD").filter(_.nonEmpty).fold(filtered) { value =>
      value.split('/') match {
        case Array(indexValue, countValue) =>
          val index = indexValue.toInt
          val count = countValue.toInt
          require(count > 0 && index >= 0 && index < count, s"invalid GEMSTONE_${prefix}_SHARD=$value")
          filtered.zipWithIndex.collect { case (entry, position) if position % count == index => entry }
        case _ => throw IllegalArgumentException(s"GEMSTONE_${prefix}_SHARD must be index/count")
      }
    }
    require(selected.nonEmpty, s"GEMSTONE_${prefix}_FILTER/SHARD selected no tests")
    selected
  }

  def discoverSourceCases(suite: Path, extensions: Set[String]): Vector[SourceCase] = {
    val stream  = Files.walk(suite)
    val sources = try
      stream.iterator.asScala
        .filter { path =>
          val filename = path.getFileName.toString
          Files.isRegularFile(path) && filename.startsWith("check_") && extensions.exists(filename.endsWith)
        }
        .toVector
        .sortBy(_.toString)
    finally stream.close()
    require(sources.nonEmpty, s"no sample sources found under $suite")
    val cases = sources.map(parseSourceCase)
    require(cases.map(_.name).distinct.size == cases.size, s"duplicate sample case names under $suite")
    cases
  }

  def runSourceCase(
      sample: SourceCase,
      caseRoot: Path,
      variables: Map[String, String],
      environment: Map[String, String]
  ): Unit = {
    Files.createDirectories(caseRoot)
    sample.commands.foreach { command =>
      val expanded = variables.foldLeft(command.text) { case (value, (name, replacement)) =>
        value.replace(s"{$name}", replacement)
      }
      require(!expanded.exists(character => character == '{' || character == '}'), s"unresolved variable: $expanded")
      val result = run(Vector("bash", "-c", expanded), caseRoot, environment)
      require(result.exit == 0, result.diagnostic)
      command.expected.foreach { expected =>
        require(
          stripTrailingNewlines(result.stdout) == expected,
          s"${sample.name}: expected '$expected', got '${result.stdout}'"
        )
      }
    }
  }

  final case class ProcessResult(command: Vector[String], exit: Int, stdout: String, stderr: String) {
    def diagnostic: String =
      s"command exited with $exit: ${command.mkString(" ")}\nstdout:\n$stdout\nstderr:\n$stderr"
  }

  final case class PairedResult(oracle: ProcessResult, gemstone: ProcessResult, oracleRan: Boolean)

  def oracleEnabled: Boolean = !sys.env.get("GEMSTONE_NO_ORACLE").exists { value =>
    Set("1", "true", "yes").contains(value.trim.toLowerCase)
  }

  /**
    * Bound an individual vendor/emulator test process.  Build and configure
    * steps deliberately keep their longer infrastructure timeout; this value
    * is for the workload process itself so a deadlocked provider cannot occupy
    * a worker for the duration of a full conformance run.
    */
  val defaultTestTimeout: FiniteDuration =
    sys.env.get("GEMSTONE_CONFORMANCE_TIMEOUT_SECONDS").fold(120.seconds)(_.toLong.seconds)
  require(defaultTestTimeout > Duration.Zero, s"GEMSTONE_CONFORMANCE_TIMEOUT_SECONDS must be positive, found $defaultTestTimeout")

  def runPaired(
      api: ComputeAPI,
      layout: RuntimeLayout,
      command: Vector[String],
      directory: Path,
      oracleEnvironment: Map[String, String] = Map.empty,
      gemstoneEnvironment: Map[String, String] = Map.empty,
      requireExecution: Boolean = true,
      timeout: FiniteDuration = defaultTestTimeout,
      oracleCommand: Option[Vector[String]] = None
  ): PairedResult = {
    val oracleRan = oracleEnabled
    val oracle    =
      if oracleRan then
        runConfigured(
          oracleCommand.getOrElse(command),
          directory,
          Environment(oracleEnvironment, GemstoneEnvironment -- oracleEnvironment.keySet),
          timeout
        )
      else ProcessResult(Vector("<oracle skipped>"), 0, "", "oracle skipped by GEMSTONE_NO_ORACLE\n")
    var observed = Option.empty[ProcessResult]
    val runGemstone = (injected: Environment) => {
      val result = runConfigured(
        command,
        directory,
        Environment(gemstoneEnvironment ++ injected.set, injected.unset -- gemstoneEnvironment.keySet),
        timeout
      )
      observed = Some(result)
      result
    }
    val gemstoneAttempt =
      if requireExecution then
        GemstoneProcess.attested(api, layout, (sys.env - "GEMSTONE_NO_ORACLE") ++ gemstoneEnvironment)(runGemstone)
      else
        GemstoneProcess.injected(api, layout, (sys.env - "GEMSTONE_NO_ORACLE") ++ gemstoneEnvironment)(runGemstone)
    val gemstone = gemstoneAttempt.fold(
      error => {
        val result = observed.getOrElse(ProcessResult(command, -1, "", ""))
        ProcessResult(command, 86, result.stdout, error.message + "\n" + result.stderr)
      },
      identity
    )
    PairedResult(oracle, gemstone, oracleRan)
  }

  def runGemstone(
      api: ComputeAPI,
      layout: RuntimeLayout,
      command: Vector[String],
      directory: Path,
      gemstoneEnvironment: Map[String, String] = Map.empty,
      timeout: FiniteDuration = defaultTestTimeout
  ): ProcessResult = {
    var observed = Option.empty[ProcessResult]
    GemstoneProcess.attested(api, layout, sys.env ++ gemstoneEnvironment) { injected =>
      val result = runConfigured(
        command,
        directory,
        Environment(gemstoneEnvironment ++ injected.set, injected.unset -- gemstoneEnvironment.keySet),
        timeout
      )
      observed = Some(result)
      result
    }.fold(
      error => {
        val result = observed.getOrElse(ProcessResult(command, -1, "", ""))
        ProcessResult(command, 86, result.stdout, error.message + "\n" + result.stderr)
      },
      identity
    )
  }

  def run(
      command: Vector[String],
      directory: Path,
      environment: Map[String, String] = Map.empty,
      timeout: FiniteDuration = 30.minutes
  ): ProcessResult =
    runConfigured(command, directory, Environment(environment, Set.empty), timeout)

  private def runConfigured(
      command: Vector[String],
      directory: Path,
      environment: Environment,
      timeout: FiniteDuration
  ): ProcessResult = {
    require(timeout > Duration.Zero, s"process timeout must be positive, found $timeout")
    val scoped = Vector(
      "systemd-run",
      "--user",
      "--scope",
      "-p",
      "MemoryMax=32G",
      "-p",
      "MemorySwapMax=0"
    ) ++ command
    val builder = ProcessBuilder(scoped*)
    builder.directory(directory.toFile)
    environment.applyTo(builder.environment())
    val process   = builder.start()
    val stdout    = ByteArrayOutputStream()
    val stderr    = ByteArrayOutputStream()
    val outThread = Thread(() => transfer(process.getInputStream, stdout), "gemstone-conformance-stdout")
    val errThread = Thread(() => transfer(process.getErrorStream, stderr), "gemstone-conformance-stderr")
    outThread.start()
    errThread.start()
    val completed = process.waitFor(timeout.toMillis, TimeUnit.MILLISECONDS)
    if (!completed) {
      process.descendants().forEach { descendant =>
        val _ = descendant.destroyForcibly()
      }
      process.destroyForcibly()
      val _ = process.waitFor()
    }
    outThread.join()
    errThread.join()
    ProcessResult(
      scoped,
      if (completed) process.exitValue() else -1,
      stdout.toString(StandardCharsets.UTF_8),
      stderr.toString(StandardCharsets.UTF_8) + (if (completed) "" else s"process timed out after $timeout\n")
    )
  }

  def libraryPath(entries: Vector[Path]): String =
    (entries.map(_.toString) ++ sys.env.get("LD_LIBRARY_PATH").filter(_.nonEmpty)).mkString(":")

  private def parseSourceCase(source: Path): SourceCase = {
    val CasePrefix     = "#pragma region case: "
    val DoPrefix       = "#pragma region do: "
    val RequiresPrefix = "#pragma region requires: "
    var name           = Option.empty[String]
    var commands       = Vector.empty[SourceCommand]
    Files.readAllLines(source, StandardCharsets.UTF_8).asScala.foreach {
      case line if line.startsWith(CasePrefix) =>
        require(name.isEmpty, s"$source declares more than one case")
        name = Some(line.drop(CasePrefix.length).trim)
      case line if line.startsWith(DoPrefix)       => commands :+= SourceCommand(line.drop(DoPrefix.length), None)
      case line if line.startsWith(RequiresPrefix) =>
        require(commands.nonEmpty && commands.last.expected.isEmpty, s"$source has misplaced requires metadata")
        commands =
          commands.updated(commands.size - 1, commands.last.copy(expected = Some(line.drop(RequiresPrefix.length))))
      case _ => ()
    }
    require(commands.size >= 2, s"$source must declare compile and run commands")
    SourceCase(name.getOrElse(throw IllegalArgumentException(s"$source has no case pragma")), source, commands)
  }

  private def stripTrailingNewlines(value: String): String =
    value.reverse.dropWhile(character => character == '\n' || character == '\r').reverse

  private def download(url: String, target: Path): Unit = {
    val temporary = Files.createTempFile(cache, s"${target.getFileName}.", ".part")
    try {
      val request  = HttpRequest.newBuilder(URI.create(url)).GET().build()
      val client   = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build()
      val response = client.send(request, HttpResponse.BodyHandlers.ofFile(temporary))
      require(response.statusCode() / 100 == 2, s"download returned HTTP ${response.statusCode()}: $url")
      val _ = Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
    } finally {
      val _ = Files.deleteIfExists(temporary)
    }
  }

  private def digest(path: Path): String = {
    val md     = MessageDigest.getInstance("SHA-256")
    val stream = Files.newInputStream(path)
    try {
      val buffer = Array.ofDim[Byte](1024 * 1024)
      Iterator.continually(stream.read(buffer)).takeWhile(_ >= 0).foreach(size => md.update(buffer, 0, size))
    } finally stream.close()
    md.digest().map(value => f"${value & 0xff}%02x").mkString
  }

  private def recreateDirectory(path: Path): Unit = {
    if (Files.exists(path)) {
      val stream = Files.walk(path)
      try stream.sorted(java.util.Comparator.reverseOrder()).iterator().asScala.foreach(Files.delete)
      finally stream.close()
    }
    val _ = Files.createDirectories(path)
  }

  private def transfer(input: InputStream, output: ByteArrayOutputStream): Unit = {
    val buffer = Array.ofDim[Byte](64 * 1024)
    Iterator.continually(input.read(buffer)).takeWhile(_ >= 0).foreach(size => output.write(buffer, 0, size))
  }

  private def findRoot(start: Path): Path =
    Iterator.iterate(start.toAbsolutePath.normalize)(_.getParent)
      .takeWhile(_ != null)
      .find(path => Files.isRegularFile(path.resolve("build.sbt")) && Files.isDirectory(path.resolve("conformance")))
      .getOrElse(throw IllegalStateException(s"cannot locate Gemstone root from $start"))
}
