package net.kurobako.gemstone.server

import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.rpc.ExecutionProtocol.ExecutionFailure
import net.kurobako.gemstone.server.execution.*
import net.kurobako.gemstone.server.recording.*
import net.kurobako.gemstone.server.transport.{ClientId, ControlProtocol, ExecutionRPCProtocol, RPCPort}
import net.kurobako.gemstone.vm.TaskLimits

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.io.PushbackInputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong, AtomicReference, LongAdder}
import java.util.concurrent.locks.ReentrantLock
import scala.util.Using

private[gemstone] object GemstoneRPCServer {
  private val SocketTimeoutMillis: Int :| Positive = 300000
  private val RootClient: ClientId                 = ClientId(1L)

  private final case class ExecutionSessionEntry(
      service: ExecutionService,
      connections: Int :| Positive
  )

  private final class UsageCounter {
    val execution = LongAdder()

    def snapshot: ServerUsage = ServerUsage(
      execution.sum().refineEither[Positive0]
        .fold(message => throw IllegalStateException(s"execution request counter overflowed: $message"), value => value)
    )
  }

  private[server] final class ExecutionSessions(
      backends: BackendCatalog,
      taskLimits: TaskLimits,
      executionPool: InterpreterScheduler
  ) extends AutoCloseable {
    private val entries = AtomicReference(Map.empty[ExecutionRPCProtocol.SessionId, ExecutionSessionEntry])
    private val closed  = AtomicBoolean(false)
    private val lock    = ReentrantLock()

    def connection(): ExecutionSessionConnection = ExecutionSessionConnection(this)
    override def close(): Unit                   = lock.withLock {
      closed.set(true)
      entries.getAndSet(Map.empty).values.foreach { entry =>
        entry.service.close()
      }
    }

    private def acquire(session: ExecutionRPCProtocol.SessionId)
        : Either[ExecutionFailure, ExecutionSessionEntry] = lock.withLock {
      if (closed.get()) Left(ExecutionFailure.Failed("client session is closed"))
      else {
        val current = entries.get()
        Right(current.get(session) match {
          case Some(entry) =>
            entries.set(current.updated(
              session,
              entry.copy(
                connections = Math.addExact(entry.connections, 1).refineUnsafe[Positive]
              )
            ))
            entry
          case None =>
            val service = ExecutionService(
              backends,
              schedule = executionPool.schedule,
              taskLimits = taskLimits,
              scheduler = executionPool
            )
            val entry = ExecutionSessionEntry(service, 1)
            entries.set(current.updated(session, entry))
            entry
        })
      }
    }

    private def release(session: ExecutionRPCProtocol.SessionId): Unit = lock.withLock {
      val current = entries.get()
      current.get(session).foreach { entry =>
        if (entry.connections == 1) {
          entries.set(current.removed(session))
          entry.service.close()
        } else
          entries.set(current.updated(
            session,
            entry.copy(connections = (entry.connections - 1).refineUnsafe[Positive])
          ))
      }
    }

    final class ExecutionSessionConnection(sessions: ExecutionSessions)
        extends ExecutionRPCProtocol.SessionConnection {
      private val active = AtomicReference(Option.empty[ExecutionRPCProtocol.SessionId])
      private def entry(session: ExecutionRPCProtocol.SessionId)
          : Either[ExecutionFailure, ExecutionSessionEntry] =
        active.get() match {
          case Some(current) if current == session =>
            entries.get().get(
              session
            ).toRight(ExecutionFailure.NotFound("process session was closed"))
          case Some(_) => Left(ExecutionFailure.InvalidRequest(
              "a connection cannot change process identity"
            ))
          case None =>
            sessions.acquire(session) match {
              case Right(acquired) =>
                active.set(Some(session))
                Right(acquired)
              case Left(error) => Left(error)
            }
        }
      override def service(session: ExecutionRPCProtocol.SessionId) = entry(session).map(_.service)
      override def close(): Unit = active.getAndSet(None).foreach(sessions.release(_))
    }
  }

  private final class Client(val id: ClientId, val executions: ExecutionSessions, val usage: UsageCounter) {
    def close(): Unit = executions.close()
  }

  private final class ClientRegistry(
      backends: BackendCatalog,
      limits: TaskLimits,
      pool: InterpreterScheduler,
      rootUsage: UsageCounter,
      journal: Option[RPCJournal.Writer]
  ) {
    val root                               = Client(RootClient, ExecutionSessions(backends, limits, pool), rootUsage)
    private val clients                    = ConcurrentHashMap[java.lang.Long, Client]()
    private val next                       = AtomicLong(RootClient.value + 1L)
    def find(id: ClientId): Option[Client] =
      if (id == RootClient) Some(root) else Option(clients.get(id.value))
    def register(): ClientId = {
      val id = ClientId.either(next.getAndIncrement())
        .fold(message => throw IllegalStateException(s"invalid client identity: $message"), value => value)
      clients.put(id.value, Client(id, ExecutionSessions(backends, limits, pool), UsageCounter()))
      id
    }
    def usage(id: ClientId): ServerUsage = find(id).fold(ServerUsage(0L))(_.usage.snapshot)
    def release(id: ClientId): Unit      = if (id != RootClient) Option(clients.remove(id.value)).foreach { client =>
      client.close(); journal.foreach(_.clientClosed(client))
    }
    else ()
    def close(): Unit = {
      root.close()
      journal.foreach(_.clientClosed(root))
      clients.values().forEach { client => client.close(); journal.foreach(_.clientClosed(client)) }
      clients.clear()
    }
  }

  final class RunningServer private[GemstoneRPCServer] (
      val port: RPCPort,
      val client: ClientId,
      server: ServerSocket,
      acceptThread: Thread,
      connections: ClientConnections,
      usageCounter: UsageCounter,
      executionPool: InterpreterScheduler,
      clients: ClientRegistry,
      journal: Option[RPCJournal.Writer]
  ) extends AutoCloseable {
    def await(): Unit      = acceptThread.join()
    def usage: ServerUsage = usageCounter.snapshot

    private val closed         = AtomicBoolean(false)
    private val lock           = ReentrantLock()
    override def close(): Unit = lock.withLock {
      if (closed.compareAndSet(false, true)) {
        Using.Manager { use =>
          journal.foreach { writer =>
            val _ = use(writer)
          }
          val _ = use(executionPool)
          try {
            server.close()
            acceptThread.join()
            connections.closeAll(() => clients.close())
          } catch {
            case scala.util.control.NonFatal(error) =>
              journal.foreach(_.markUnreplayable(
                s"server cleanup failed: ${errorMessage(error)}"
              ))
              throw error
          }
        }.get
      }
    }
  }

  private[server] final class ClientConnections {
    private val sockets                           = ConcurrentHashMap[Socket, Thread]()
    def add(socket: Socket, thread: Thread): Unit = { val _ = sockets.put(socket, thread) }
    def remove(socket: Socket): Unit              = { val _ = sockets.remove(socket) }
    def closeAll(cancel: () => Unit): Unit        = {
      sockets.keySet().forEach(_.close())
      cancel()
      sockets.values().forEach(_.join())
    }
  }

  def start(): RunningServer =
    startWith(GemstoneBackendCatalog.default, configuredExecutionLimits(sys.env), SocketTimeoutMillis)

  /** Start the default server with an explicit per-dispatch execution profile. */
  def start(taskLimits: TaskLimits): RunningServer =
    startWith(GemstoneBackendCatalog.default, taskLimits, SocketTimeoutMillis)

  /** Use the launcher's effective environment for the pool as well as its execution limits. */
  def start(taskLimits: TaskLimits, environment: Map[String, String]): RunningServer =
    startWith(GemstoneBackendCatalog.default, taskLimits, SocketTimeoutMillis, environment)

  private[server] def start(
      backends: BackendCatalog,
      taskLimits: TaskLimits
  ): RunningServer =
    startWith(backends, taskLimits, SocketTimeoutMillis)

  private[server] def start(
      backends: BackendCatalog,
      taskLimits: TaskLimits,
      socketTimeoutMillis: Int :| Positive
  ): RunningServer =
    startWith(backends, taskLimits, socketTimeoutMillis)

  private def startWith(
      backends: BackendCatalog,
      taskLimits: TaskLimits,
      socketTimeoutMillis: Int :| Positive,
      environment: Map[String, String] = sys.env
  ): RunningServer = {
    // Resolve the actual pool before recording its profile, so replay does not inherit another machine's default.
    val executionPool = InterpreterScheduler.fromEnvironment(environment)
    val journal       = try environment.get("GEMSTONE_RECORD") match {
        case None       => None
        case Some(path) =>
          val profile = RPCJournal.ExecutionProfile(
            taskLimits.maxInstructions,
            taskLimits.quantum,
            executionPool.parallelism
          )
          val protocolIdentity = RPCJournal.ProtocolIdentity.either(ExecutionRPC.Identity)
            .fold(
              message => throw IllegalStateException(s"invalid execution protocol identity: $message"),
              value => value
            )
          Some(RPCJournal.Writer(
            Path.of(path),
            RPCJournal.Header(protocolIdentity, executionProfile = Some(profile))
          ))
      }
    catch {
      case scala.util.control.NonFatal(error) => executionPool.close(); throw error
    }
    val server = try ServerSocket()
    catch {
      case scala.util.control.NonFatal(error) =>
        executionPool.close()
        journal.foreach { writer => writer.markUnreplayable("server startup failed"); writer.close() }
        throw error
    }
    try {
      val connections = ClientConnections()
      val usage       = UsageCounter()
      val clients     = ClientRegistry(backends, taskLimits, executionPool, usage, journal)
      server.bind(InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0))
      val acceptThread = Thread(
        () => acceptConnections(server, clients, connections, socketTimeoutMillis, journal),
        "gemstone-rpc-accept"
      )
      acceptThread.setDaemon(true)
      acceptThread.start()
      RunningServer(
        RPCPort.either(server.getLocalPort)
          .fold(message => throw IllegalStateException(s"invalid bound port: $message"), value => value),
        RootClient,
        server,
        acceptThread,
        connections,
        usage,
        executionPool,
        clients,
        journal
      )
    } catch {
      case scala.util.control.NonFatal(error) =>
        server.close()
        executionPool.close()
        journal.foreach { writer => writer.markUnreplayable("server startup failed"); writer.close() }
        throw error
    }
  }

  private def configuredExecutionLimits(environment: Map[String, String]): TaskLimits =
    ExecutionConfiguration.limits(environment).fold(message => throw IllegalArgumentException(message), value => value)

  private def acceptConnections(
      server: ServerSocket,
      clients: ClientRegistry,
      connections: ClientConnections,
      socketTimeoutMillis: Int :| Positive,
      journal: Option[RPCJournal.Writer]
  ): Unit =
    try
      Iterator.continually(server.accept()).foreach { socket =>
        val thread = Thread(
          () => serve(socket, clients, connections, socketTimeoutMillis, journal, () => server.close()),
          s"gemstone-rpc-${socket.getPort}"
        )
        thread.setDaemon(true)
        connections.add(socket, thread)
        thread.start()
      }
    catch {
      case _: SocketException if server.isClosed => ()
    }

  private def serve(
      socket: Socket,
      clients: ClientRegistry,
      connections: ClientConnections,
      socketTimeoutMillis: Int :| Positive,
      journal: Option[RPCJournal.Writer],
      shutdown: () => Unit
  ): Unit = {
    type BoundConnection = (client: Client, execution: ExecutionSessions#ExecutionSessionConnection)
    try
      Using.resource(socket) { socket =>
        Using.Manager { use =>
          val bound     = AtomicReference(Option.empty[BoundConnection])
          val buffered  = java.io.BufferedInputStream(socket.getInputStream)
          val recording = journal match {
            case Some(writer) => Some(use(RPCRecording(writer, buffered, socket.getOutputStream)))
            case None         => None
          }
          val pushback = use(PushbackInputStream(
            recording.fold[java.io.InputStream](buffered)(_.input),
            ExecutionRPCProtocol.RequestIdentityBytesAfterMagic
          ))
          val input  = use(DataInputStream(pushback))
          val output = use(DataOutputStream(recording.fold[java.io.OutputStream](socket.getOutputStream)(_.output)))
          def client(input: DataInputStream): BoundConnection = {
            val id       = peekClientId(input, pushback)
            val selected = clients.find(id).getOrElse(throw IllegalArgumentException(s"unknown client identity $id"))
            bound.get() match {
              case Some(pair) if pair.client eq selected => pair
              case Some(_) => throw IllegalArgumentException("connection cannot change client identity")
              case None    =>
                val pair = (client = selected, execution = use(selected.executions.connection()))
                bound.set(Some(pair))
                pair
            }
          }
          socket.setTcpNoDelay(true)
          socket.setSoTimeout(socketTimeoutMillis)
          def exchange(): Boolean = try {
            recording.foreach(_.begin())
            input.readInt() match {
              case ControlProtocol.Magic =>
                input.readInt() match {
                  case ControlProtocol.RegisterClient => output.writeLong(clients.register().value)
                  case ControlProtocol.QueryUsage     =>
                    val usage = clients.usage(readClientId(input))
                    // Preserve the two-long control reply while retired clients disappear;
                    // all API evidence is now the shared execution count.
                    output.writeLong(0L)
                    output.writeLong(usage.executionRequests)
                  case ControlProtocol.ReleaseClient =>
                    clients.release(readClientId(input))
                    output.writeInt(0)
                  case ControlProtocol.Shutdown => output.writeInt(0); output.flush(); shutdown()
                  case other                    => throw IllegalArgumentException(s"unknown control operation $other")
                }
                output.flush()
                false
              case ExecutionRPC.RequestMagic =>
                val (owner, execution) = client(input)
                socket.setSoTimeout(0)
                val keep = ExecutionRPCProtocol.exchange(
                  input,
                  output,
                  owner.id,
                  execution,
                  () => { owner.usage.execution.increment(); recording.foreach(_.accepted(owner)) }
                )
                recording.foreach(_.response())
                keep
              case value => throw IllegalArgumentException(f"invalid request magic: 0x$value%08x")
            }
          } catch {
            case _: EOFException                       => false
            case _: SocketException if socket.isClosed => false
            case scala.util.control.NonFatal(error)    =>
              recording.foreach(_.failed(
                s"connection exchange failed: ${errorMessage(error)}"
              ))
              scribe.debug("RPC connection exchange failed", error)
              false
          }
          Iterator.continually(exchange()).takeWhile(value => value).foreach(_ => ())
        }.get
      }
    // The socket itself is managed above; this finally maintains the live-connection registry on every exit path.
    finally connections.remove(socket)
  }

  private[server] def peekClientId(
      input: DataInputStream,
      replay: java.io.PushbackInputStream
  ): ClientId = {
    // Keep the buffered socket unmarked: Native marked refills can repeat bytes.
    val prefix = readExactly(input, ExecutionRPCProtocol.RequestIdentityBytesAfterMagic)
    replay.unread(prefix)
    ClientId.either(
      ByteBuffer.wrap(prefix, ExecutionRPCProtocol.RequestClientOffsetAfterMagic, java.lang.Long.BYTES).getLong()
    ).fold(message => throw IllegalArgumentException(s"invalid client identity: $message"), value => value)
  }

  private def readClientId(input: DataInputStream): ClientId =
    ClientId.either(input.readLong())
      .fold(message => throw IllegalArgumentException(s"invalid client identity: $message"), value => value)

  private def readExactly(input: DataInputStream, size: Int): Array[Byte] = {
    val result = input.readNBytes(size)
    if (result.length != size) throw EOFException(s"expected $size bytes, received ${result.length}")
    result
  }

  private def errorMessage(error: Throwable): String =
    Option(error.getMessage).filter(_.nonEmpty).getOrElse(error.getClass.getName)

}
