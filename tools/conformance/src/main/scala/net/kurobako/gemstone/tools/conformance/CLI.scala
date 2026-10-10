package net.kurobako.gemstone.tools.conformance

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.*
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*
import scala.sys.process.Process
import scala.sys.process.ProcessLogger
import scala.util.Try
import scala.util.Using
import scala.util.control.NonFatal

object CLI {
  case class Suite(executable: Path, artifacts: Path, output: Path, root: Path)

  case class Server(
      java: Path,
      classpath: String,
      javaOptions: Vector[String],
      native: Path,
      jfr: Option[Path],
      listen: Option[String]
  )

  case class Emulation(
      suite: Suite,
      server: Server,
      workloads: Path,
      routes: Path,
      targetSet: String,
      workload: String,
      selectors: Vector[String]
  )

  private val MainClass          = "net.kurobako.gemstone.server.CLI"
  private val MaxInstructions    = "100000000000"
  private val RetainedRecordings = 5
  private val UnsafeAccess       = "--sun-misc-unsafe-memory-access=allow"

  private def path(name: String, help: String): Opts[Path] = Opts.option[String](name, help).map(Path.of(_))

  private val suite: Opts[Suite] = (
    path("executable", "Conformance driver"),
    path("artifacts", "Conformance artifact directory"),
    path("output", "Report directory"),
    path("root", "Repository root")
  ).mapN(Suite(_, _, _, _))

  private val server: Opts[Server] = (
    path("java", "Java executable for the Gemstone server and clients"),
    Opts.option[String]("classpath", "Gemstone server classpath"),
    Opts.options[String]("server-java-option", "Additional server JVM option").orEmpty.map(_.toVector),
    path("native", "Gemstone native runtime directory"),
    path("jfr", "Directory for server JFR recordings").orNone,
    Opts.option[String]("listen", "Server listen address; defaults to a per-run Unix socket").orNone
  ).mapN(Server(_, _, _, _, _, _))

  private val emulation: Opts[Emulation] = (
    suite,
    server,
    path("workloads", "Conformance workload directory"),
    path("routes", "Targets.cmake route script"),
    Opts.option[String]("target-set", "Conformance target set"),
    Opts.option[String]("workload", "Conformance workload").withDefault("compact"),
    Opts.arguments[String]("selector").orEmpty.map(_.toVector)
  ).mapN(Emulation(_, _, _, _, _, _, _))

  private val command = Command("conformance", "Run the GPU conformance suite") {
    Opts.subcommand("run", "Run artifacts directly on host devices") {
      (suite, path("profiles", "Machine profile directory"), Opts.arguments[String]("argument").orEmpty)
        .mapN((suite, profiles, arguments) => () => run(suite, Vector("--profiles", s"$profiles") ++ arguments))
    }.orElse(Opts.subcommand("emulate", "Run artifacts through Gemstone")(emulation.map(emulation =>
      () => emulate(emulation)
    )))
  }

  def main(arguments: Array[String]): Unit = command.parse(arguments.toVector, sys.env) match {
    case Left(help) =>
      Console.err.println(help)
      sys.exit(2)
    case Right(action) => action() match {
        case 0      => ()
        case status => sys.exit(status)
      }
  }

  private def run(suite: Suite, arguments: Vector[String], variables: Map[String, String] = Map.empty): Int = {
    val command =
      Vector(s"${suite.executable}", "run", "--artifacts", s"${suite.artifacts}", "--output", s"${suite.output}") ++
        arguments
    Process(command, suite.root.toFile, variables.toSeq*).!
  }

  private def positive(name: String, default: Int): Int = sys.env.get(name).fold(default) { value =>
    value.toIntOption.filter(
      _ > 0
    ).getOrElse(throw IllegalArgumentException(s"$name must be a positive integer: $value"))
  }

  private def routes(emulation: Emulation): Vector[String] = {
    val inventory =
      Process(Vector("cmake", s"-DCONFORMANCE_TARGET_SET=${emulation.targetSet}", "-P", s"${emulation.routes}"))
        .!!.linesIterator.filter(_.nonEmpty).toVector
    require(inventory.nonEmpty, s"conformance target set ${emulation.targetSet} has no routes")
    def matches(route: String, selector: String): Boolean = {
      val artifact = route.replaceFirst("^cuda-(ptx|sass)/", "cuda/")
      Vector(route, route.takeWhile(_ != '/'), artifact, artifact.takeWhile(_ != '/')).contains(selector)
    }
    val selectors = emulation.selectors.filterNot(_ == "all")
    require(
      !emulation.selectors.contains("all") || emulation.selectors.sizeIs == 1,
      "all cannot be combined with target selectors"
    )
    selectors.foreach(selector =>
      require(
        inventory.exists(matches(_, selector)),
        s"unknown emulation target $selector; available: ${inventory.mkString(", ")}"
      )
    )
    inventory.filter(route => selectors.isEmpty || selectors.exists(matches(route, _)))
  }

  private def quoted(value: String): String = "'" + value.replace("'", "'\"'\"'") + "'"

  private def launcher(client: Vector[String], variables: Map[String, String]): String = {
    val inherited = sys.env.filter((key, _) => key.startsWith("GEMSTONE_"))
    val exports   = (inherited ++ variables).toVector.sortBy(_._1).map((key, value) => s"export $key=${quoted(value)}")
    val routing   =
      s"""unset CONFORMANCE_DEVICE
         |case "$$CONFORMANCE_TARGET_BACKEND" in
         |  cuda|hip|hsa)
         |    if [ -z "$$${environment.TargetArch}" ] && [ -n "$$CONFORMANCE_TARGET_ARCHITECTURE" ]; then
         |      export ${environment.TargetArch}="$$CONFORMANCE_TARGET_ARCHITECTURE"
         |    fi ;;
         |  levelzero|vulkan) export CONFORMANCE_DEVICE='*Gemstone*' ;;
         |esac
         |if [ "$$CONFORMANCE_TARGET_BACKEND" = cuda ]; then
         |  case "$$CONFORMANCE_CUDA_CODE" in
         |    ptx|sass) export ${environment.CudaCode}="$$CONFORMANCE_CUDA_CODE" ;;
         |    *) echo 'CUDA conformance requires an explicit ptx or sass route' >&2; exit 2 ;;
         |  esac
         |fi
         |toolchain="$$CONFORMANCE_SUPPORT_ROOT/opencl-toolchain"
         |if [ -z "$$${environment.OpenclCompiler}" ] && [ -x "$$toolchain/bin/compiler/clang" ]; then
         |  export ${environment.OpenclCompiler}="$$toolchain/bin/compiler/clang"
         |fi
         |if [ -z "$$${environment.OpenclLlvmSpirv}" ] && [ -x "$$toolchain/bin/compiler/llvm-spirv" ]; then
         |  export ${environment.OpenclLlvmSpirv}="$$toolchain/bin/compiler/llvm-spirv"
         |fi
         |if [ -z "$$${environment.OpenclLlvmLink}" ] && [ -x "$$toolchain/bin/compiler/llvm-link" ]; then
         |  export ${environment.OpenclLlvmLink}="$$toolchain/bin/compiler/llvm-link"
         |fi
         |""".stripMargin
    (exports :+ routing :+ s"exec ${client.map(quoted(_)).mkString(" ")} \"$$@\"").mkString("\n") + "\n"
  }

  private def endpoint(file: Path, server: Process): String = {
    val deadline             = 30.seconds.fromNow
    def read: Option[String] =
      Option.when(Files.isRegularFile(file))(Try(ujson.read(Files.readString(file))("endpoint").str).toOption).flatten
    LazyList.continually { Thread.sleep(25L); read }
      .takeWhile(_ => server.isAlive() && deadline.hasTimeLeft())
      .collectFirst { case Some(value) => value }
      .orElse(read)
      .getOrElse(throw IllegalStateException(
        if (server.isAlive()) "Gemstone conformance server did not publish an endpoint"
        else s"Gemstone conformance server exited with ${server.exitValue()}"
      ))
  }

  private def prune(directory: Path, retained: Int): Unit = {
    val recordings = Using.resource(Files.list(directory))(_.toList.asScala.toVector)
      .filter(path => Files.isDirectory(path) && path.getFileName.toString.startsWith("emulate-"))
      .sortBy(path => Files.getLastModifiedTime(path).toMillis)
    recordings.dropRight(retained).foreach { path =>
      Using.resource(Files.walk(path))(_.toList.asScala.toVector).reverse.foreach(Files.deleteIfExists(_))
    }
  }

  private def emulate(emulation: Emulation): Int = {
    val root       = emulation.suite.root
    val processors = sys.runtime.availableProcessors()
    val jobs       = positive("CONFORMANCE_JOBS", math.max(1, math.min(16, processors / 2)))
    val workers    = positive(environment.ExecutionParallelism, math.max(1, math.min(64, processors / 2)))
    val settings   = Map(
      "CONFORMANCE_OPENCL_CTS_ATOMIC_ITERATIONS" -> "16",
      "CONFORMANCE_OPENCL_CTS_ATOMIC_THREADS"    -> "128",
      "CONFORMANCE_OPENCL_CTS_ELEMENTS"          -> "256",
      "CONFORMANCE_OPENCL_CTS_WIMPY"             -> "1",
      "CONFORMANCE_OPENCL_CTS_TIMEOUT"           -> "600",
      "OMP_NUM_TEAMS_DEV"                        -> s"$workers",
      "OMP_TEAMS_THREAD_LIMIT_DEV"               -> "2",
      environment.ExecutionMaxInstructions       -> MaxInstructions
    ).map((key, default) => key -> sys.env.getOrElse(key, default)) ++ Map(
      "CONFORMANCE_JOBS"               -> s"$jobs",
      environment.ExecutionParallelism -> s"$workers",
      environment.NativeDirectory      -> s"${emulation.server.native.toAbsolutePath}"
    )
    val heap      = s"-Xmx${math.max(8L, math.min(28L, 2L + jobs.toLong * 6L))}g"
    val directory = Files.createDirectories(root.resolve("target/conformance"))
    val session   = Files.createTempDirectory(directory, "server-")
    val recording = emulation.server.jfr.map { jfr =>
      prune(Files.createDirectories(jfr), RetainedRecordings - 1)
      Files.createTempDirectory(jfr, "emulate-").resolve("server.jfr")
    }
    val java      = Vector(s"${emulation.server.java}")
    val serverJvm = Vector(heap, UnsafeAccess) ++ emulation.server.javaOptions ++ recording.toVector.flatMap(file =>
      Vector(
        s"-XX:StartFlightRecording=settings=profile,disk=true,maxsize=512m,dumponexit=true,filename=$file",
        "-Xlog:jfr+startup=off"
      )
    )
    // the probe reads device JSON from stdout, so JVM diagnostics must not land there
    val clientJvm = Vector(
      "-Xms32m",
      "-Xmx256m",
      UnsafeAccess,
      "-XX:+UseSerialGC",
      "-XX:ActiveProcessorCount=1",
      "-XX:-UsePerfData",
      "-Xlog:all=warning:stderr"
    )
    def gemstone(jvm: Vector[String], arguments: String*): Vector[String] =
      java ++ jvm ++ Vector("-cp", emulation.server.classpath, MainClass) ++ arguments
    val endpointFile = session.resolve("endpoint.json")
    val listen       =
      emulation.server.listen.getOrElse(s"unix://${session.resolve("rpc.sock").toUri.getRawPath}")
    val script = session.resolve("launcher.sh")
    recording.foreach(file => Console.println(s"Conformance JFR recording: $file"))
    val process = Process(
      gemstone(serverJvm, "serve", "--listen", listen, "--endpoint-file", s"$endpointFile"),
      root.toFile,
      settings.toSeq*
    ).run(ProcessLogger(Console.println(_), Console.err.println(_)))
    val _ = sys.addShutdownHook(process.destroy())
    try {
      val address = endpoint(endpointFile, process)
      val _       = Files.writeString(
        script,
        launcher(gemstone(clientJvm, "run", "--server", address), settings),
        StandardCharsets.UTF_8
      )
      try
        run(
          emulation.suite,
          Vector("--direct", "--workloads", s"${emulation.workloads}", "--workload", emulation.workload) ++
            routes(emulation) ++ Vector("--isolated-launcher", "--launcher", "--", "/bin/sh", s"$script"),
          settings
        )
      finally {
        val stopped =
          try Process(gemstone(clientJvm, "shutdown", "--server", address), root.toFile, settings.toSeq*).! == 0
          catch { case NonFatal(_) => false }
        if (!stopped) process.destroy()
      }
    } finally {
      if (process.isAlive()) process.destroy()
      val _ = process.exitValue()
      recording.foreach(file => Console.println(s"Conformance JFR written to: $file"))
      Vector(endpointFile, script, session.resolve("rpc.sock"), session).foreach(Files.deleteIfExists(_))
    }
  }
}
