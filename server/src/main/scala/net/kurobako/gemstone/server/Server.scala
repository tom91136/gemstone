package net.kurobako.gemstone.server

import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.backend.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.server.recording.*
import net.kurobako.gemstone.vm.*
import scala.jdk.CollectionConverters.*
import scala.util.control.NonFatal

object Server {
  private val AcceptStopMillis = 5000L

  object Settings {
    private def hostBound(threads: Long): Int    = math.max(1L, math.min(64L, threads)).toInt
    private def parallelism(bound: Int): Int > 0 = math.max(1, bound - 2).unsafe[Int > 0]
    private def availableThreads: Long > 0 = math.max(1L, sys.runtime.availableProcessors().toLong).unsafe[Long > 0]
    def defaultParallelism: Int > 0        = parallelism(hostBound(availableThreads.raw))

    /** Parse and validate before acquiring any server resources. */
    def fromEnvironment(values: Map[String, String]): Either[String, Settings] = {
      def positive(
          name: String,
          default: Long > 0,
          maximum: Long = Long.MaxValue,
          expected: String = "a positive integer"
      ): Either[String, Long > 0] = configuration.lookup(values, name).fold[Either[String, Long > 0]](Right(default)) {
        value =>
          configuration.positiveLong(value).filter(_.raw <= maximum).toRight(s"$name must be $expected: $value")
      }
      for {
        threads <- positive(env.ThreadCount, availableThreads)
        bound = hostBound(threads.raw)
        instructions <- positive(env.ExecutionMaxInstructionCount, TaskControl.Limits.Default.maxInstructions)
        workers      <- positive(
          name = env.ExecutionThreadCount,
          default = parallelism(bound).widen[Long > 0],
          maximum = bound,
          expected = s"a positive integer at most the host bound $bound"
        )
        timeout <- positive(
          name = env.RpcTimeoutSeconds,
          default = env.RpcTimeoutSecondsDefault,
          maximum = Int.MaxValue / 1000,
          expected = "positive seconds fitting an Int millisecond timeout"
        )
        record <- configuration.lookup(values, env.RecordFile)
          .fold[Either[String, Option[Path]]](Right(None))(paths.parse(env.RecordFile, _).map(Some(_)))
      } yield Settings(
        limits = TaskControl.Limits(instructions),
        parallelism = workers.toIntExact,
        requestTimeoutMillis = (timeout * 1000L).toIntExact,
        record = record,
        tracePerformance = configuration.lookup(values, env.TraceList).exists(configuration.traceEnabled(_, "perf"))
      )
    }

    /** A recorded profile goes through the same validation, so a capture from a wider host is rejected. */
    def replay(values: Map[String, String], profile: Journal.ExecutionProfile): Either[String, Settings] =
      fromEnvironment(
        (values - env.RecordFile)
          .updated(env.ExecutionMaxInstructionCount, profile.maxInstructions.toString)
          .updated(env.ExecutionThreadCount, profile.parallelism.toString)
      )
  }

  case class Settings(
      limits: TaskControl.Limits = TaskControl.Limits.Default,
      parallelism: Int > 0 = Settings.defaultParallelism,
      requestTimeoutMillis: Int > 0 = (env.RpcTimeoutSecondsDefault * 1000L).toIntExact,
      record: Option[Path] = None,
      tracePerformance: Boolean = false,
      bind: Bind = Bind.Tcp()
  )

  /** Explicit settings keep startup independent of the process environment. */
  def start(settings: Settings = Settings(), backends: Workgroups => Backends = Backends.default(_)): Server = {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var rollback                                = List.empty[() => Unit]
    def own[A <: AutoCloseable](resource: A): A = {
      rollback = (() => resource.close()) :: rollback
      resource
    }
    try {
      val pool = own(WorkerPool(
        parallelism = settings.parallelism,
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
          Journal.Header(Journal.ExecutionProfile(settings.limits.maxInstructions, pool.parallelism))
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
        client => journal.foreach(_.clientClosed(client))
      ))
      val listener = own(Sockets.listen(settings.bind))
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
    def stopAccepting(): Unit = {
      acceptThread.join(Server.AcceptStopMillis)
      if (acceptThread.isAlive) throw IllegalStateException("RPC accept thread did not stop after the listener closed")
    }
    try throwables.closeAll(List(
        () => listener.close(),
        () => stopAccepting(),
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
