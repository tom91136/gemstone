package net.kurobako.gemstone.server

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.protocol.*
import scala.util.Using

object Launch {
  case class Environment(set: Map[String, String], unset: Set[String]) {
    def applyTo(target: java.util.Map[String, String]): Unit = {
      unset.foreach(target.remove(_))
      set.foreach { case (name, value) => target.put(name, value) }
    }
  }

  type Executable = Executable.Type
  object Executable extends Newtype[NonEmpty[String]] {
    override protected def violation(value: NonEmpty[String]): Option[NonEmpty[String]] =
      Option.when[NonEmpty[String]](value.contains('\u0000'))("executable must not contain a NUL character")
  }

  type Argument = Argument.Type
  object Argument extends Newtype[String] {
    override protected def violation(value: String): Option[NonEmpty[String]] =
      Option.when[NonEmpty[String]](value.contains('\u0000'))("command argument contains a NUL character")
  }

  case class Options(
      apis: NonEmpty[Set[API]],
      serverEndpoint: Option[Endpoint],
      directory: Path,
      executable: Executable,
      arguments: Vec[Argument]
  )

  private class RunningProcess(val process: Process) extends AutoCloseable {
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

  // Workaround for Scala Native, whose blocking waitFor path spins in a future
  // wait while the child runs. Polling with a short sleep avoids that spin.
  private def waitForChild(process: Process): Int = {
    while (process.isAlive()) Thread.sleep(20L)
    process.exitValue()
  }

  def validate(options: Options): Either[String, Unit] =
    options.apis.toList.map(api => options.directory.resolve(api.lib)).find(path => !Files.isRegularFile(path))
      .toLeft(()).left.map(path => s"runtime provider is not a regular file: $path")

  /** Run a process. Call `validate` first to report a missing runtime provider as a usage error. */
  def run(options: Options): Either[String, Int] =
    launch(options.apis, options.directory, sys.env, options.serverEndpoint) { changes =>
      val builder = ProcessBuilder((options.executable.raw +: options.arguments.map(_.raw))*).inheritIO()
      changes.applyTo(builder.environment())
      Using.resource(RunningProcess(builder.start()))(running => waitForChild(running.process))
    }

  private class Registration(val endpoint: Endpoint) extends AutoCloseable {
    val client: ClientId        = Control.register(endpoint)
    private val released        = AtomicBoolean(false)
    private val releaseFinished = CountDownLatch(1)
    private def release(): Unit =
      if (released.compareAndSet(false, true)) {
        try Control.release(endpoint, client)
        finally releaseFinished.countDown()
      } else releaseFinished.await()

    // A timed-out child must not strand execution workers in the shared server.
    // Bound shutdown even when the remote server no longer responds.
    private val shutdownHook = Thread(
      () => {
        val cleanup = Thread(
          () =>
            try release()
            catch { case scala.util.control.NonFatal(_) => () },
          "gemstone-client-release"
        )
        cleanup.setDaemon(true)
        cleanup.start()
        try cleanup.join(500L)
        catch { case _: InterruptedException => Thread.currentThread().interrupt() }
      },
      "gemstone-client-cleanup"
    )
    sys.runtime.addShutdownHook(shutdownHook)

    override def close(): Unit =
      try release()
      finally
        try { val _ = sys.runtime.removeShutdownHook(shutdownHook) }
        catch { case _: IllegalStateException => () }
  }

  def launch[A](
      apis: NonEmpty[Set[API]],
      directory: Path,
      inherited: Map[String, String],
      serverEndpoint: Option[Endpoint] = None
  )(child: Launch.Environment => A): Either[String, A] =
    try
      serverEndpoint match {
        case Some(endpoint) => Using.resource(Registration(endpoint)) { registration =>
            Right(child(prepareEnvironment(apis, directory, endpoint, registration.client, inherited)))
          }
        case None => Server.Settings.fromEnvironment(inherited)
            .left.map(message => s"invalid execution profile: $message").map { settings =>
              Using.resource(Server.start(settings)) { running =>
                child(prepareEnvironment(apis, directory, running.endpoint, running.client, inherited))
              }
            }
      }
    catch { case scala.util.control.NonFatal(error) => Left(throwables.detail(error)) }

  def prepareEnvironment(
      apis: NonEmpty[Set[API]],
      directory: Path,
      endpoint: Endpoint,
      client: ClientId,
      inherited: Map[String, String]
  ): Launch.Environment = {
    val root    = directory.toAbsolutePath.normalize()
    val preload = API.values.view.filter(apis).collect {
      case api @ (API.CUDA | API.HIP | API.HSA | API.OpenCL) => root.resolve(api.lib)
    }.toVector
    val directories = (preload ++ Option.when(apis(API.Vulkan))(root.resolve(API.Vulkan.lib))).flatMap(path =>
      Option(path.getParent)
    ).distinct
    val inheritedPreload     = inherited.get("LD_PRELOAD").filter(_.nonEmpty).toVector
    val inheritedLibraryPath = inherited.get("LD_LIBRARY_PATH").filter(_.nonEmpty).toVector
    val common               = Map[String, String](
      env.RpcEndpoint -> endpoint.toString,
      env.RpcClient   -> s"$client"
    )
    val dynamic                                                                      = Map.newBuilder[String, String]
    def searchPath(name: String, own: Vector[Path], inherited: Vector[String]): Unit =
      if (own.nonEmpty) dynamic += name -> (own.map(path => s"$path") ++ inherited).mkString(":")
    searchPath("LD_PRELOAD", preload, inheritedPreload)
    searchPath("LD_LIBRARY_PATH", directories, inheritedLibraryPath)
    if (apis(API.OpenCL)) {
      dynamic += "OCL_ICD_FILENAMES" -> s"${root.resolve(API.OpenCL.lib)}"
      dynamic += "OCL_ICD_VENDORS"   -> s"${root.resolve(API.OpenCL.lib)}"
    }
    if (apis(API.LevelZero)) {
      dynamic += "ZE_ENABLE_ALT_DRIVERS"        -> s"${root.resolve(API.LevelZero.lib)}"
      dynamic += "ZE_ENABLE_LOADER_DEBUG_TRACE" -> "0"
    }
    Launch.Environment(common ++ dynamic.result(), RemovedOverrides)
  }

  private val RemovedOverrides = Set(
    env.Record,
    "HSA_ENABLE_DTIF",
    "HSA_TOOLS_LIB",
    "OCL_ICD_VENDORS",
    "OCL_ICD_FILENAMES",
    "ONEAPI_DEVICE_SELECTOR",
    "SYCL_DEVICE_FILTER",
    "ZE_ENABLE_ALT_DRIVERS"
  )
}
