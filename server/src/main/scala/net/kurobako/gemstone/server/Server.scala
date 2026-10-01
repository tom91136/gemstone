package net.kurobako.gemstone.server

import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.backend.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.server.recording.*
import net.kurobako.gemstone.server.transport.*
import net.kurobako.gemstone.vm.*
import scala.jdk.CollectionConverters.*
import scala.util.control.NonFatal

object Server {
  object Settings {
    private def hostBound: Int      = math.max(1, math.min(64, sys.runtime.availableProcessors()))
    def defaultParallelism: Int > 0 = math.max(1, hostBound - 2).unsafe[Int > 0]

    /** Parse and validate before acquiring any server resources. */
    def fromEnvironment(values: Map[String, String]): Either[String, Settings] =
      try {
        val instructions =
          values.get(env.ExecutionMaxInstructions).fold(TaskControl.Limits.Default.maxInstructions) {
            value =>
              configuration.positiveLong(value).getOrElse(throw IllegalArgumentException(
                s"${env.ExecutionMaxInstructions} must be a positive integer: $value"
              )).refine[Long > 0].fold(
                message => throw IllegalArgumentException(s"${env.ExecutionMaxInstructions}: $message"),
                identity
              )
          }
        val parallelism = values.get(env.ExecutionParallelism).fold(defaultParallelism) { value =>
          configuration.positiveLong(value).filter(_ <= Int.MaxValue).map(_.toInt).getOrElse(
            throw IllegalArgumentException(
              s"${env.ExecutionParallelism} must be a positive integer: $value"
            )
          ).refine[Int > 0].fold(
            message => throw IllegalArgumentException(s"${env.ExecutionParallelism}: $message"),
            identity
          )
        }
        require(
          parallelism.raw <= hostBound,
          s"${env.ExecutionParallelism}=$parallelism exceeds host bound $hostBound"
        )
        Right(Settings(
          limits = TaskControl.Limits(instructions),
          parallelism = parallelism,
          requestTimeoutMillis = values.get(env.RpcTimeoutSeconds).fold(
            Math.toIntExact(env.RpcTimeoutSecondsDefault * 1000).unsafe[Int > 0]
          ) { value =>
            (configuration.positiveLong(value).filter(_ <= Int.MaxValue / 1000)
              .getOrElse(throw IllegalArgumentException(
                s"${env.RpcTimeoutSeconds} must be positive seconds fitting an Int millisecond timeout: $value"
              )) * 1000).toInt.unsafe[Int > 0]
          },
          record = values.get(env.Record).map(Path.of(_)),
          tracePerformance = values.get(env.Trace).exists(configuration.traceEnabled(_, "perf"))
        ))
      } catch { case NonFatal(error) => Left(throwables.detail(error)) }
  }

  case class Settings(
      limits: TaskControl.Limits = TaskControl.Limits.Default,
      parallelism: Int > 0 = Settings.defaultParallelism,
      requestTimeoutMillis: Int > 0 = Math.toIntExact(env.RpcTimeoutSecondsDefault * 1000).unsafe[Int > 0],
      record: Option[Path] = None,
      tracePerformance: Boolean = false,
      bind: Bind = Bind.Tcp()
  )

  /** Explicit settings keep startup independent of the process environment. */
  def start(
      settings: Settings = Settings(),
      backends: Workgroups => Backends = Backends.default(_),
      listen: Bind => Listener = transport.listen(_)
  ): Server = {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var rollback                                = List.empty[() => Unit]
    def own[A <: AutoCloseable](resource: A): A = {
      rollback = (() => resource.close()) :: rollback
      resource
    }
    try {
      val pool = own(WorkerPool(
        parallelism = settings.parallelism,
        minGroups = settings.parallelism.raw.toLong,
        minWorkItems = 32L,
        reporter = error => scribe.error(error),
        measurement = Option.when(settings.tracePerformance) { measured =>
          scribe.info(
            s"Gemstone parallel groups=${measured.taskCount} configured=${settings.parallelism} workers=${measured.workers} elapsed_ns=${measured.elapsedNanos}"
          )
        }
      ))
      val journal = settings.record.map { path =>
        own(Journal.Writer(
          path,
          Journal.Header(
            protocolIdentity = Journal.ProtocolIdentity(ExecutionRPC.Identity),
            executionProfile = Some(Journal.ExecutionProfile(settings.limits.maxInstructions, pool.parallelism))
          )
        ))
      }
      rollback = (() => journal.foreach(_.markUnreplayable("server startup failed"))) :: rollback
      val capabilities = backends(pool)
      val clients      = own(Clients(
        () =>
          ExecutionSession(
            backends = capabilities.programs,
            taskLimits = settings.limits,
            executionContext = pool,
            parallelism = pool.parallelism,
            specialization = capabilities.specialization,
            graphics = capabilities.graphics
          ),
        owner => journal.foreach(_.clientClosed(owner))
      ))
      val listener = own(listen(settings.bind))
      val server   =
        Server(listener, pool, clients, journal, settings.requestTimeoutMillis)
      server.acceptThread.start()
      server
    } catch {
      case NonFatal(error) =>
        try throwables.closeAll(rollback)
        catch { case NonFatal(cleanup) if cleanup ne error => error.addSuppressed(cleanup) }
        throw error
    }
  }
}

class Server private (
    listener: Listener,
    pool: WorkerPool,
    clients: Clients,
    journal: Option[Journal.Writer],
    requestTimeoutMillis: Int > 0
) extends AutoCloseable {
  val endpoint: Endpoint   = listener.endpoint
  val client: ClientId     = Clients.Root
  private val closed       = AtomicBoolean(false)
  private val connections  = ConcurrentHashMap[Channel, Thread]()
  private val acceptThread = Thread(() => accept(), "gemstone-rpc-accept")
  acceptThread.setDaemon(true)

  def await(): Unit = acceptThread.join()
  def usage: Long   = clients.usage(client)

  private def accept(): Unit =
    try
      while (!listener.isClosed) {
        val connection = listener.accept()
        val thread     = Thread(
          () =>
            try Connection.serve(connection, clients, requestTimeoutMillis, journal, () => listener.close())
            finally { val _ = connections.remove(connection) },
          "gemstone-rpc-connection"
        )
        thread.setDaemon(true)
        connections.put(connection, thread)
        try thread.start()
        catch {
          case NonFatal(error) =>
            connections.remove(connection)
            try connection.close()
            catch { case NonFatal(cleanup) => error.addSuppressed(cleanup) }
            throw error
        }
      }
    catch {
      case NonFatal(_) if closed.get() || listener.isClosed => ()
      case NonFatal(error) => scribe.error("RPC accept loop terminated unexpectedly", error)
    }

  override def close(): Unit = if (closed.compareAndSet(false, true)) {
    // Stop accepting first, then snapshot threads before listener closure can remove them.
    def shutdownConnections(): Unit = {
      val active = connections.entrySet().asScala.toList.map(entry => entry.getKey -> entry.getValue)
      throwables.closeAll(active.map((channel, _) => () => channel.close()) ++
        List(() => clients.close()) ++ active.map((_, thread) => () => thread.join()))
    }
    try throwables.closeAll(List(
        () => listener.close(),
        () => acceptThread.join(),
        () => shutdownConnections(),
        () => pool.close()
      ))
    catch {
      case NonFatal(error) =>
        try journal.foreach(_.markUnreplayable(s"server cleanup failed: ${throwables.detail(error)}"))
        catch { case NonFatal(recording) => error.addSuppressed(recording) }
        try journal.foreach(_.close())
        catch { case NonFatal(cleanup) => error.addSuppressed(cleanup) }
        throw error
    }
    journal.foreach(_.close())
  }
}
