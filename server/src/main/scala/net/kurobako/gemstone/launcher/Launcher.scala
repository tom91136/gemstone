package net.kurobako.gemstone.launcher

import net.kurobako.gemstone.server.execution.ExecutionService
import net.kurobako.gemstone.server.transport.ServerControl
import net.kurobako.gemstone.server.transport.Transport.*
import net.kurobako.gemstone.server.GemstoneRPCServer
import net.kurobako.gemstone.{NoNul, Vec}

import cats.syntax.all.*
import com.monovore.decline.{Argument, Opts}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.nio.file.{Files, Path}
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import scala.util.Using

object Launcher {
  type Executable = Executable.T
  object Executable extends RefinedType[String, MinLength[1] & NoNul]

  final case class Options(
      apis: Set[ComputeAPI],
      serverPort: Option[RPCPort],
      layout: RuntimeLayout,
      executable: Executable,
      arguments: Vec[String :| NoNul]
  )

  private final class RunningProcess(val process: Process) extends AutoCloseable {
    private val stopping     = AtomicBoolean(false)
    private val shutdownHook = Thread(() => stop(), "gemstone-launcher-cleanup")
    sys.runtime.addShutdownHook(shutdownHook)

    private def stop(): Unit =
      if (stopping.compareAndSet(false, true)) {
        val descendants = process.descendants()
        try descendants.forEach { descendant =>
            val _ = descendant.destroyForcibly()
          }
        finally descendants.close()
        val _ = process.destroyForcibly()
        try {
          val _ = process.waitFor(2, TimeUnit.SECONDS)
        } catch case _: InterruptedException => Thread.currentThread().interrupt()
      }

    override def close(): Unit = {
      try sys.runtime.removeShutdownHook(shutdownHook)
      catch case _: IllegalStateException => ()
      stop()
    }
  }

  private given Argument[Set[ComputeAPI]] = Argument.from("apis") { value =>
    parseAPIs(value).toValidatedNel
  }

  private given Argument[RPCPort] = Argument.from("port") { value =>
    value.toIntOption
      .toValidNel(s"invalid server port: $value")
      .andThen(port => RPCPort.either(port).leftMap(message => s"invalid server port: $message").toValidatedNel)
  }

  private val invocation = Opts.arguments[String]("command").mapValidated { command =>
    (for {
      executable <- Executable.either(command.head)
        .leftMap(_ => "command executable must be non-empty and contain no NUL characters")
      arguments <- command.tail.zipWithIndex.traverse { case (argument, index) =>
        argument.refineEither[NoNul]
          .leftMap(_ => s"command argument ${index + 1} contains a NUL character")
      }
    } yield executable -> Vec.from(arguments)).toValidatedNel
  }

  val options: Opts[Options] = (
    Opts.option[Set[ComputeAPI]]("apis", help = "Comma-separated APIs to provide").orNone,
    Opts.option[RPCPort]("server-port", help = "Connect to an existing Gemstone server").orNone,
    invocation
  ).mapN { (apis, serverPort, command) =>
    val directory = sys.env.get("GEMSTONE_NATIVE_DIRECTORY").filter(_.nonEmpty).map(Path.of(_))
      .getOrElse(Path.of("native-build"))
    Options(
      apis.getOrElse(ComputeAPI.defaults),
      serverPort,
      RuntimeLayout(directory),
      command._1,
      command._2
    )
  }

  def run(options: Options): Int = {
    val layout  = options.layout.absolute
    val missing = options.apis.view.map(layout.library).filterNot(Files.isRegularFile(_)).toVector
    if (missing.nonEmpty) {
      Console.err.println(s"gemstone: runtime provider is not a regular file: ${missing.head}")
      2
    } else {
      val launched = launch(options.apis, layout, sys.env, options.serverPort) { environment =>
        val builder = ProcessBuilder((options.executable.value +: options.arguments)*).inheritIO()
        environment.applyTo(builder.environment())
        Using.resource(RunningProcess(builder.start()))(_.process.waitFor())
      }
      launched match {
        case Left(error)   => Console.err.println(s"gemstone: $error"); 86
        case Right(status) => status
      }
    }
  }

  def launch[A](
      apis: Set[ComputeAPI],
      layout: RuntimeLayout,
      inherited: Map[String, String],
      serverPort: Option[RPCPort] = None
  )(child: Environment => A): Either[String, A] = serverPort match {
    case Some(port) =>
      val client = ServerControl.register(port)
      try launchWith(apis, layout, inherited, port, client, child)
      finally ServerControl.release(port, client)
    case None =>
      ExecutionService.limits(inherited)
        .leftMap(message => s"invalid execution profile: $message")
        .flatMap { limits =>
          Using.resource(GemstoneRPCServer.start(limits, inherited)) { running =>
            launchWith(apis, layout, inherited, running.port, running.client, child)
          }
        }
  }

  def plan(
      apis: Set[ComputeAPI],
      layout: RuntimeLayout,
      port: RPCPort,
      client: ClientId,
      inherited: Map[String, String]
  ): Either[String, Environment] =
    if (apis.isEmpty) Left("at least one compute API must be enabled")
    else {
      val runtime = layout.absolute
      val preload = ComputeAPI.values.view.filter(apis).collect {
        case api @ (ComputeAPI.CUDA | ComputeAPI.HIP | ComputeAPI.HSA | ComputeAPI.OpenCL) => runtime.library(api)
      }.toVector
      val directories          = preload.flatMap(path => Option(path.getParent)).distinct
      val inheritedPreload     = inherited.get("LD_PRELOAD").filter(_.nonEmpty).toVector
      val inheritedLibraryPath = inherited.get("LD_LIBRARY_PATH").filter(_.nonEmpty).toVector
      val common               = Map(
        "GEMSTONE_RPC_PORT"   -> s"${port.value}",
        "GEMSTONE_RPC_CLIENT" -> s"${client.value}"
      )
      val dynamic           = Map.newBuilder[String, String]
      val conformanceTarget = for {
        backend      <- inherited.get("CONFORMANCE_TARGET_BACKEND")
        architecture <- inherited.get("CONFORMANCE_TARGET_ARCHITECTURE").filter(_.nonEmpty)
      } yield backend -> architecture
      conformanceTarget.foreach {
        case ("cuda", architecture)
            if apis(ComputeAPI.CUDA) && !inherited.contains("GEMSTONE_CUDA_TARGET") &&
              !inherited.contains("GEMSTONE_CUDA_TARGET_ARCH") =>
          dynamic += "GEMSTONE_CUDA_TARGET" -> architecture
        case ("hip" | "hsa", architecture)
            if (apis(ComputeAPI.HIP) || apis(ComputeAPI.HSA)) && !inherited.contains("GEMSTONE_AMDGPU_TARGET") =>
          dynamic += "GEMSTONE_AMDGPU_TARGET" -> architecture
        case _ =>
      }
      if (preload.nonEmpty) dynamic += "LD_PRELOAD" -> (preload.map(path => s"$path") ++ inheritedPreload).mkString(":")
      if (directories.nonEmpty)
        dynamic += "LD_LIBRARY_PATH" -> (directories.map(path => s"$path") ++ inheritedLibraryPath).mkString(":")
      if (apis(ComputeAPI.OpenCL))
        dynamic += "OCL_ICD_FILENAMES" -> s"${runtime.library(ComputeAPI.OpenCL)}"
      if (apis(ComputeAPI.LevelZero)) {
        dynamic += "ZE_ENABLE_ALT_DRIVERS"        -> s"${runtime.library(ComputeAPI.LevelZero)}"
        dynamic += "ZE_ENABLE_LOADER_DEBUG_TRACE" -> "0"
      }
      Right(Environment(common ++ dynamic.result(), RemovedOverrides))
    }

  private def launchWith[A](
      apis: Set[ComputeAPI],
      layout: RuntimeLayout,
      inherited: Map[String, String],
      port: RPCPort,
      client: ClientId,
      child: Environment => A
  ): Either[String, A] =
    plan(apis, layout, port, client, inherited).map(environment => child(environment))

  private def parseAPIs(value: String): Either[String, Set[ComputeAPI]] = {
    val names = value.split(",", -1).toVector
    if (names.exists(_.isEmpty)) Left("--apis must be a comma-separated non-empty API list")
    else names.traverse(name => ComputeAPI(name)).map(_.toSet)
  }

  private val RemovedOverrides = Set(
    "CONFORMANCE_DEVICE",
    "GEMSTONE_RECORD",
    "HSA_ENABLE_DTIF",
    "HSA_TOOLS_LIB",
    "OCL_ICD_VENDORS",
    "OCL_ICD_FILENAMES",
    "ONEAPI_DEVICE_SELECTOR",
    "SYCL_DEVICE_FILTER",
    "ZE_ENABLE_ALT_DRIVERS"
  )
}
