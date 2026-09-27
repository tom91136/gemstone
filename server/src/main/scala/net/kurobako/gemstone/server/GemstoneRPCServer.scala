package net.kurobako.gemstone.server

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.concurrent.withLock

import net.kurobako.gemstone.launcher.EnvironmentNames
import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.rpc.ExecutionProtocol.ExecutionFailure
import net.kurobako.gemstone.server.execution.*
import net.kurobako.gemstone.server.recording.*
import net.kurobako.gemstone.server.transport.ExecutionRPCProtocol
import net.kurobako.gemstone.server.transport.ServerControl
import net.kurobako.gemstone.server.transport.Transport.*
import net.kurobako.gemstone.vm.TaskLimits

import java.io.PushbackInputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import scala.jdk.CollectionConverters.*
import java.nio.ByteBuffer
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong, AtomicReference, LongAdder}
import java.util.concurrent.locks.ReentrantLock
import scala.util.Using

object GemstoneRPCServer {
  private val SocketTimeoutMillis: Positive[Int] = 300000
  private val RootClient: ClientId               = ClientId(1L)

  private case class ExecutionSessionEntry(
      service: ExecutionService,
      connections: Positive[Int]
  )

  final class ExecutionSessions(
      backends: List[RuntimeBackend],
      taskLimits: TaskLimits,
      executionPool: WorkerPool
  ) extends AutoCloseable {
    private val entries = AtomicReference(Map.empty[ExecutionRPCProtocol.SessionId, ExecutionSessionEntry])
    private val closed  = AtomicBoolean(false)
    private val lock    = ReentrantLock()

    def connection(): ExecutionSessionConnection = ExecutionSessionConnection(this)
    override def close(): Unit                   = lock.withLock {
      closed.set(true)
      Throwables.closeAllQuietly(entries.getAndSet(Map.empty).values.map(entry => () => entry.service.close()).toList)
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
                connections = Positive.unsafe(Math.addExact(entry.connections.raw, 1))
              )
            ))
            entry
          case None =>
            val service = ExecutionService(
              backends,
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
        if (entry.connections.raw == 1) {
          entries.set(current.removed(session))
          entry.service.close()
        } else
          entries.set(current.updated(
            session,
            entry.copy(connections = Positive.unsafe(entry.connections.raw - 1))
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

  private final class Client(val id: ClientId, val executions: ExecutionSessions) {
    private val usage = LongAdder()

    def countUsage(): Unit = usage.increment()

    def executionCount: Long =
      NonNegative.either(usage.sum())
        .fold(
          message => throw IllegalStateException(s"execution request counter overflowed: $message"),
          value => value.raw
        )

    def close(): Unit = executions.close()
  }

  private final class ClientRegistry(
      backends: List[RuntimeBackend],
      limits: TaskLimits,
      pool: WorkerPool,
      journal: Option[RPCJournal.Writer]
  ) {
    val root                               = Client(RootClient, ExecutionSessions(backends, limits, pool))
    private val clients                    = ConcurrentHashMap[java.lang.Long, Client]()
    private val next                       = AtomicLong(RootClient.raw + 1L)
    def find(id: ClientId): Option[Client] =
      if (id == RootClient) Some(root) else Option(clients.get(id.raw))
    def register(): ClientId = {
      val id = clientId(next.getAndIncrement())
      clients.put(id.raw, Client(id, ExecutionSessions(backends, limits, pool)))
      id
    }
    def usage(id: ClientId): Long   = find(id).fold(0L)(_.executionCount)
    def release(id: ClientId): Unit = if (id != RootClient) Option(clients.remove(id.raw)).foreach { client =>
      client.close(); journal.foreach(_.clientClosed(client))
    }
    else ()
    def close(): Unit = {
      Throwables.closeAllQuietly(List { () => root.close(); journal.foreach(_.clientClosed(root)) })
      Throwables.closeAllQuietly(
        clients.values().asScala.map { client => () => client.close(); journal.foreach(_.clientClosed(client)) }.toList
      )
      clients.clear()
    }
  }

  final class RunningServer private[GemstoneRPCServer] (
      val port: RPCPort,
      val client: ClientId,
      server: ServerSocket,
      acceptThread: Thread,
      connections: ClientConnections,
      executionPool: WorkerPool,
      clients: ClientRegistry,
      journal: Option[RPCJournal.Writer]
  ) extends AutoCloseable {
    def await(): Unit = acceptThread.join()
    def usage: Long   = clients.usage(RootClient)

    private val closed         = AtomicBoolean(false)
    override def close(): Unit =
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
                s"server cleanup failed: ${Throwables.detail(error)}"
              ))
              throw error
          }
        }.get
      }
  }

  final class ClientConnections {
    private val sockets                           = ConcurrentHashMap[Socket, Thread]()
    def add(socket: Socket, thread: Thread): Unit = { val _ = sockets.put(socket, thread) }
    def remove(socket: Socket): Unit              = { val _ = sockets.remove(socket) }
    def closeAll(cancel: () => Unit): Unit        =
      Throwables.closeAllQuietly(
        sockets.keySet().asScala.map(socket => () => socket.close()).toList ++
          List(() => cancel()) ++
          sockets.values().asScala.map(thread => () => thread.join()).toList
      )
  }

  def start(): RunningServer =
    startWith(
      RuntimeBackend.defaults,
      ExecutionService.requireLimits(sys.env),
      SocketTimeoutMillis
    )

  /** Use the launcher's effective environment for the pool as well as its execution limits. */
  def start(taskLimits: TaskLimits, environment: Map[String, String]): RunningServer =
    startWith(RuntimeBackend.defaults, taskLimits, SocketTimeoutMillis, environment)

  private[server] def startWith(
      backends: List[RuntimeBackend],
      taskLimits: TaskLimits,
      socketTimeoutMillis: Positive[Int],
      environment: Map[String, String] = sys.env
  ): RunningServer = {
    // Resolve the actual pool before recording its profile, so replay does not inherit another machine's default.
    val executionPool = WorkerPool.fromEnvironment(environment)
    val journal       = try environment.get(EnvironmentNames.Record) match {
        case None       => None
        case Some(path) =>
          val profile = RPCJournal.ExecutionProfile(
            taskLimits.maxInstructions,
            taskLimits.quantum,
            executionPool.parallelism
          )
          val protocolIdentity = RPCJournal.ProtocolIdentity.either(ExecutionRPC.Identity.raw)
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
    def abort(error: Throwable): Nothing = {
      executionPool.close()
      journal.foreach { writer => writer.markUnreplayable("server startup failed"); writer.close() }
      throw error
    }
    val server = try ServerSocket()
    catch { case scala.util.control.NonFatal(error) => abort(error) }
    try {
      val connections = ClientConnections()
      val clients     = ClientRegistry(backends, taskLimits, executionPool, journal)
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
        executionPool,
        clients,
        journal
      )
    } catch {
      case scala.util.control.NonFatal(error) =>
        server.close()
        abort(error)
    }
  }

  private def acceptConnections(
      server: ServerSocket,
      clients: ClientRegistry,
      connections: ClientConnections,
      socketTimeoutMillis: Positive[Int],
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
      case scala.util.control.NonFatal(error)    =>
        scribe.error("RPC accept loop terminated unexpectedly", error)
    }

  private def serve(
      socket: Socket,
      clients: ClientRegistry,
      connections: ClientConnections,
      socketTimeoutMillis: Positive[Int],
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
          socket.setSoTimeout(socketTimeoutMillis.raw)
          def exchange(): Boolean = try {
            recording.foreach(_.begin())
            input.readInt() match {
              case ServerControl.Magic =>
                input.readInt() match {
                  case ServerControl.RegisterClient => output.writeLong(clients.register().raw)
                  case ServerControl.ReleaseClient  =>
                    clients.release(readClientId(input))
                    output.writeInt(0)
                  case ServerControl.Shutdown => output.writeInt(0); output.flush(); shutdown()
                  case other                  => throw IllegalArgumentException(s"unknown control operation $other")
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
                  () => { owner.countUsage(); recording.foreach(_.accepted(owner)) }
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
                s"connection exchange failed: ${Throwables.detail(error)}"
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

  def peekClientId(
      input: DataInputStream,
      replay: java.io.PushbackInputStream
  ): ClientId = {
    // Keep the buffered socket unmarked: Native marked refills can repeat bytes.
    val prefix = readExactly(input, ExecutionRPCProtocol.RequestIdentityBytesAfterMagic, EOFException(_))
    replay.unread(prefix)
    clientId(ByteBuffer.wrap(
      prefix,
      ExecutionRPCProtocol.RequestClientOffsetAfterMagic,
      java.lang.Long.BYTES
    ).getLong())
  }

  private def readClientId(input: DataInputStream): ClientId = clientId(input.readLong())

}
