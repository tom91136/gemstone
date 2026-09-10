package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.Col.*

import net.kurobako.gemstone.abi.*
import net.kurobako.gemstone.cuda.*
import net.kurobako.gemstone.generated.{GeneratedCUDARPC, GeneratedCUDAWireCodec}
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.execution.ProgramExecutor
import net.kurobako.gemstone.execution.ExecutionLimits
import net.kurobako.gemstone.launcher.UsageSnapshot

import java.io.File
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
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.LongAdder
import java.util.concurrent.ConcurrentHashMap

object GemstoneRPCServer {
  private val RequestMagic        = 0x474d5331
  private val ResponseMagic       = 0x474d5231
  private val ProtocolVersion     = GeneratedCUDARPC.ProtocolVersion
  private val CallbackMagic       = 0x474d4331
  private val CallbackReplyMagic  = 0x474d4131
  private val FingerprintBytes    = 64
  private val TokenBytes          = 64
  private val MaximumNameBytes    = 128
  private val MaximumPayload      = 128 * 1024 * 1024
  private val SocketTimeoutMillis = 300000

  private final case class ExecutionSessionEntry(
      service: ExecutionService,
      cuda: CUDARPCDispatcher,
      cancellation: net.kurobako.gemstone.execution.CancellationToken.Source,
      callbacks: CallbackOwnership,
      connections: Int
  )

  /** Pending callbacks are serviced only by their originating CUDA connection. */
  private final class CallbackOwnership {
    private val unsupported =
      RPCResponse.Failure(CUDAError(801, "pending CUDA callbacks require their originating connection"))
    private val owner  = AtomicReference(Option.empty[CallbackConnection])
    private val closed = java.util.concurrent.atomic.AtomicBoolean(false)
    def dispatch(connection: CallbackConnection, request: RPCRequest)(run: => RPCResponse): RPCResponse = {
      val registration   = request.functionName == "cuLaunchHostFunc" || request.functionName == "cuStreamAddCallback"
      val draining       = request.functionName == "cuCtxSynchronize" || request.functionName == "cuCtxDestroy_v2"
      def differentOwner = owner.get().exists(current => (current ne connection) && !current.isClosed)
      if (!registration && !draining) run
      // Do not wait on the owner's monitor while its callback is awaiting a reply.
      else if (differentOwner) unsupported
      else synchronized {
        if (closed.get()) RPCResponse.Failure(CUDAError(1, "CUDA client session is closed"))
        else if (differentOwner) unsupported
        else {
          val response = run
          response match {
            case RPCResponse.Success(WireValue.CallResult(status, _)) if status.isSuccess =>
              if (registration) {
                owner.set(Some(connection))
                if (closed.get()) owner.getAndSet(None).foreach(_.cancel())
              } else owner.set(None)
            case _ => ()
          }
          response
        }
      }
    }
    def close(): Unit = {
      closed.set(true)
      owner.getAndSet(None).foreach(_.cancel())
    }
  }

  private final class UsageCounter {
    val cuda      = LongAdder()
    val execution = LongAdder()

    def snapshot: UsageSnapshot = UsageSnapshot(cuda.sum(), execution.sum())
  }

  private[server] final class ExecutionSessions(
      executor: () => ProgramExecutor,
      executionLimits: ExecutionLimits,
      executionPool: BoundedExecutionPool
  ) {
    private val entries = AtomicReference(Map.empty[ExecutionRPCProtocol.SessionId, ExecutionSessionEntry])
    private val closed  = java.util.concurrent.atomic.AtomicBoolean(false)

    def connection(): ExecutionSessionConnection = ExecutionSessionConnection(this)
    private def executeCUDA(
        entry: ExecutionSessionEntry,
        connection: CallbackConnection,
        context: Long,
        request: RPCRequest
    ): RPCResponse =
      entry.callbacks.dispatch(connection, request) {
        if (request.functionName == "cuLaunchKernel") executionPool.call(() => entry.cuda.dispatch(request, context))
        else entry.cuda.dispatch(request, context)
      }

    def close(): Unit = synchronized {
      closed.set(true)
      entries.getAndSet(Map.empty).values.foreach { entry =>
        entry.callbacks.close()
        entry.cancellation.cancel()
        entry.service.close()
      }
    }

    private def acquire(session: ExecutionRPCProtocol.SessionId)
        : Either[net.kurobako.gemstone.execution.ExecutionFailure, ExecutionSessionEntry] = synchronized {
      if (closed.get()) Left(net.kurobako.gemstone.execution.ExecutionFailure.Failed("client session is closed"))
      else {
        val current = entries.get()
        Right(current.get(session) match {
          case Some(entry) =>
            entries.set(current.updated(session, entry.copy(connections = Math.addExact(entry.connections, 1))))
            entry
          case None =>
            val cancellation = net.kurobako.gemstone.execution.CancellationToken.Source()
            val service      = ExecutionService(
              executor(),
              schedule = executionPool.schedule,
              executionLimits = executionLimits,
              parallelism = executionPool
            )
            val cuda = CUDARPCDispatcher.emulated(() =>
              net.kurobako.gemstone.execution.ExecutionControl.create(
                executionLimits,
                cancellation,
                executionPool
              )
            )
            val entry = ExecutionSessionEntry(service, cuda, cancellation, CallbackOwnership(), 1)
            entries.set(current.updated(session, entry))
            entry
        })
      }
    }

    private def release(session: ExecutionRPCProtocol.SessionId): Unit = synchronized {
      val current = entries.get()
      current.get(session).foreach { entry =>
        if (entry.connections == 1) {
          entries.set(current.removed(session))
          entry.callbacks.close()
          entry.cancellation.cancel()
          entry.service.close()
        } else entries.set(current.updated(session, entry.copy(connections = entry.connections - 1)))
      }
    }

    final class ExecutionSessionConnection(sessions: ExecutionSessions)
        extends ExecutionRPCProtocol.SessionConnection {
      private val active = AtomicReference(Option.empty[ExecutionRPCProtocol.SessionId])
      private def entry(session: ExecutionRPCProtocol.SessionId)
          : Either[net.kurobako.gemstone.execution.ExecutionFailure, ExecutionSessionEntry] =
        active.get() match {
          case Some(current) if current == session =>
            entries.get().get(
              session
            ).toRight(net.kurobako.gemstone.execution.ExecutionFailure.NotFound("process session was closed"))
          case Some(_) => Left(net.kurobako.gemstone.execution.ExecutionFailure.InvalidRequest(
              "a connection cannot change process identity"
            ))
          case None =>
            sessions.acquire(session).map { acquired => active.set(Some(session)); acquired }
        }
      override def service(session: ExecutionRPCProtocol.SessionId) = entry(session).map(_.service)
      private[GemstoneRPCServer] def cuda(
          session: ExecutionRPCProtocol.SessionId,
          connection: CallbackConnection,
          context: Long,
          request: RPCRequest
      ): RPCResponse =
        entry(session).fold(
          error => RPCResponse.Failure(CUDAError(1, error.message)),
          value => sessions.executeCUDA(value, connection, context, request)
        )
      override def close(): Unit = active.getAndSet(None).foreach(sessions.release)
    }
  }

  private final class Client(val token: Array[Byte], val executions: ExecutionSessions, val usage: UsageCounter) {
    def close(): Unit = executions.close()
  }

  private final class ClientRegistry(
      rootToken: Array[Byte],
      executor: () => ProgramExecutor,
      limits: ExecutionLimits,
      pool: BoundedExecutionPool,
      rootUsage: UsageCounter,
      journal: Option[RPCJournal.Writer]
  ) {
    val root            = Client(rootToken, ExecutionSessions(executor, limits, pool), rootUsage)
    private val clients = ConcurrentHashMap[String, Client]()
    def find(token: Array[Byte]): Option[Client] =
      if (RPCAuthentication.matches(rootToken, token)) Some(root)
      else Option(clients.get(String(token, StandardCharsets.US_ASCII)))
    def register(): String = {
      val token = RPCAuthentication.token()
      clients.put(
        token,
        Client(token.getBytes(StandardCharsets.US_ASCII), ExecutionSessions(executor, limits, pool), UsageCounter())
      )
      token
    }
    def usage(token: String): UsageSnapshot = Option(clients.get(token)).fold(UsageSnapshot(0L, 0L))(_.usage.snapshot)
    def release(token: String): Unit        = Option(clients.remove(token)).foreach { client =>
      client.close(); journal.foreach(_.clientClosed(client))
    }
    def close(): Unit = {
      root.close()
      journal.foreach(_.clientClosed(root))
      clients.values().forEach { client => client.close(); journal.foreach(_.clientClosed(client)) }
      clients.clear()
    }
  }

  final class RunningServer private[GemstoneRPCServer] (
      val port: Int,
      server: ServerSocket,
      acceptThread: Thread,
      connections: ClientConnections,
      usageCounter: UsageCounter,
      executionPool: BoundedExecutionPool,
      clients: ClientRegistry,
      journal: Option[RPCJournal.Writer]
  ) extends AutoCloseable {
    def await(): Unit        = acceptThread.join()
    def usage: UsageSnapshot = usageCounter.snapshot

    private val closed         = java.util.concurrent.atomic.AtomicBoolean(false)
    override def close(): Unit = synchronized {
      if (closed.compareAndSet(false, true)) {
        server.close()
        acceptThread.join()
        try {
          connections.closeAll(() => clients.close())
          executionPool.close()
        } catch {
          case scala.util.control.NonFatal(error) =>
            journal.foreach(_.markUnreplayable(s"server cleanup failed: ${error.getMessage}"))
            throw error
        } finally journal.foreach(_.close())
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

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def main(arguments: Array[String]): Unit = arguments.toCol match {
    case Col("inspect", archive) => println(RPCJournal.inspect(File(archive)))
    case Col("replay", archive)  =>
      println(RPCReplay.run(RPCReplay.Options(File(archive), GeneratedCUDARPC.SchemaFingerprint), replayEndpoint))
    case Col("step", archive) =>
      val session = RPCReplay.open(RPCReplay.Options(File(archive), GeneratedCUDARPC.SchemaFingerprint), replayEndpoint)
      try {
        var proceed = true
        while (proceed && session.next.nonEmpty) {
          println(s"${session.next.get}; Enter to execute, q to quit")
          val command = scala.io.StdIn.readLine()
          if (command == null || command == "q") proceed = false
          else println(session.step().get)
        }
        println(session.report)
      } finally session.close()
    case Col("shutdown", rest*) =>
      val options = parseArguments(Col.from(rest))
      val token   = options.getOrElse("token", throw IllegalArgumentException("shutdown requires --token"))
      require(validToken(token), "invalid shutdown token")
      val socket = Socket()
      try {
        socket.connect(InetSocketAddress(InetAddress.getLoopbackAddress, options("port").toInt), 5000)
        socket.setSoTimeout(5000)
        val output = DataOutputStream(socket.getOutputStream)
        output.writeInt(0x474d4330)
        output.write(token.getBytes(StandardCharsets.US_ASCII))
        output.writeInt(4)
        output.flush()
        require(DataInputStream(socket.getInputStream).readInt() == 0, "shutdown failed")
      } finally socket.close()
    case Col("run", _*)      => net.kurobako.gemstone.launcher.GemstoneLauncher.main(arguments)
    case Col("serve", rest*) => run(Col.from(rest))
    case _                   => run(arguments.toCol)
  }

  private def replayEndpoint(header: RPCJournal.Header): RPCReplay.Endpoint = {
    val token   = RPCAuthentication.token()
    val profile = header.executionProfile.getOrElse(throw IllegalArgumentException("capture has no execution profile"))
    val environment = (sys.env - "GEMSTONE_RECORD").updated(
      "GEMSTONE_EXECUTION_PARALLELISM",
      profile.parallelism.toString
    )
    val running = start(token, ExecutionLimits(profile.maxInstructions, profile.quantum), environment)
    RPCReplay.Endpoint(running.port, token, () => running.close())
  }

  private def run(arguments: Col[String]): Unit = {
    val options      = parseArguments(arguments)
    val portFile     = options.get("port-file").map(Path.of(_))
    val endpointFile = options.get("endpoint-file").map(Path.of(_))
    require(portFile.nonEmpty || endpointFile.nonEmpty, "provide --port-file or --endpoint-file")
    val token = options.getOrElse(
      "token",
      RPCAuthentication.token()
    )
    require(validToken(token), "--token must contain exactly 64 lowercase hexadecimal characters")
    val environment = options.get("record").fold(sys.env)(path => sys.env.updated("GEMSTONE_RECORD", path))
    val running     = start(token, configuredExecutionLimits(environment), environment)
    try {
      portFile.foreach { file =>
        Option(file.getParent).foreach(Files.createDirectories(_))
        Files.writeString(file, s"${running.port}\n", StandardCharsets.US_ASCII)
      }
      endpointFile.foreach { file =>
        Option(file.getParent).foreach(Files.createDirectories(_))
        Files.writeString(file, s"""{"port":${running.port},"token":"$token"}""", StandardCharsets.US_ASCII)
        Files.setPosixFilePermissions(file, java.nio.file.attribute.PosixFilePermissions.fromString("rw-------"))
      }
      System.out.println(s"gemstone RPC server listening on 127.0.0.1:${running.port}")
      System.out.flush()
      running.await()
    } finally running.close()
  }

  def start(token: String): RunningServer =
    startWith(token, () => RegisteredProgramExecutor.default, configuredExecutionLimits(sys.env), SocketTimeoutMillis)

  /** Start the default server with an explicit per-dispatch execution profile. */
  def start(token: String, executionLimits: ExecutionLimits): RunningServer =
    startWith(token, () => RegisteredProgramExecutor.default, executionLimits, SocketTimeoutMillis)

  /** Use the launcher's effective environment for the pool as well as its execution limits. */
  def start(token: String, executionLimits: ExecutionLimits, environment: Map[String, String]): RunningServer =
    startWith(token, () => RegisteredProgramExecutor.default, executionLimits, SocketTimeoutMillis, environment)

  private[server] def start(token: String, executor: ProgramExecutor): RunningServer =
    startWith(token, () => executor, ExecutionLimits.Default, SocketTimeoutMillis)

  private[server] def start(
      token: String,
      executor: ProgramExecutor,
      executionLimits: ExecutionLimits
  ): RunningServer =
    startWith(token, () => executor, executionLimits, SocketTimeoutMillis)

  private[server] def start(
      token: String,
      executor: ProgramExecutor,
      executionLimits: ExecutionLimits,
      socketTimeoutMillis: Int
  ): RunningServer = {
    require(socketTimeoutMillis >= 0, "socket timeout must be non-negative")
    startWith(token, () => executor, executionLimits, socketTimeoutMillis)
  }

  private def startWith(
      token: String,
      executor: () => ProgramExecutor,
      executionLimits: ExecutionLimits,
      socketTimeoutMillis: Int,
      environment: Map[String, String] = sys.env
  ): RunningServer = {
    require(validToken(token), "token must contain exactly 64 lowercase hexadecimal characters")
    // Resolve the actual pool before recording its profile, so replay does not inherit another machine's default.
    val executionPool = BoundedExecutionPool.fromEnvironment(environment)
    val journal       = try environment.get("GEMSTONE_RECORD").map { path =>
        val profile = RPCJournal.ExecutionProfile(
          executionLimits.maxInstructions,
          executionLimits.quantum,
          executionPool.parallelism
        )
        RPCJournal.Writer(
          File(path),
          RPCJournal.Header(GeneratedCUDARPC.SchemaFingerprint, executionProfile = Some(profile))
        )
      }
    catch {
      case scala.util.control.NonFatal(error) => executionPool.close(); throw error
    }
    val tokenBytes = token.getBytes(StandardCharsets.US_ASCII)
    val server     = try ServerSocket()
    catch {
      case scala.util.control.NonFatal(error) =>
        executionPool.close()
        journal.foreach { writer => writer.markUnreplayable("server startup failed"); writer.close() }
        throw error
    }
    try {
      val connections = ClientConnections()
      val usage       = UsageCounter()
      val clients     = ClientRegistry(tokenBytes, executor, executionLimits, executionPool, usage, journal)
      server.bind(InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0))
      val acceptThread = Thread(
        () => acceptConnections(server, tokenBytes, clients, connections, socketTimeoutMillis, journal),
        "gemstone-rpc-accept"
      )
      acceptThread.setDaemon(true)
      acceptThread.start()
      RunningServer(server.getLocalPort, server, acceptThread, connections, usage, executionPool, clients, journal)
    } catch {
      case scala.util.control.NonFatal(error) =>
        server.close()
        executionPool.close()
        journal.foreach { writer => writer.markUnreplayable("server startup failed"); writer.close() }
        throw error
    }
  }

  private def configuredExecutionLimits(environment: Map[String, String]): ExecutionLimits =
    ExecutionLimits.fromEnvironment(environment).fold(message => throw IllegalArgumentException(message), identity)

  private def acceptConnections(
      server: ServerSocket,
      token: Array[Byte],
      clients: ClientRegistry,
      connections: ClientConnections,
      socketTimeoutMillis: Int,
      journal: Option[RPCJournal.Writer]
  ): Unit =
    try
      Iterator.continually(server.accept()).foreach { socket =>
        val thread = Thread(
          () => serve(socket, token, clients, connections, socketTimeoutMillis, journal, () => server.close()),
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
      expectedToken: Array[Byte],
      clients: ClientRegistry,
      connections: ClientConnections,
      socketTimeoutMillis: Int,
      journal: Option[RPCJournal.Writer],
      shutdown: () => Unit
  ): Unit = {
    val bound     = AtomicReference(Option.empty[(Client, ExecutionSessions#ExecutionSessionConnection)])
    val buffered  = java.io.BufferedInputStream(socket.getInputStream)
    val recording = journal.map(writer => RPCRecording(writer, buffered, socket.getOutputStream))
    val pushback  =
      PushbackInputStream(recording.fold[java.io.InputStream](buffered)(_.input), 4 + FingerprintBytes + TokenBytes)
    val input     = DataInputStream(pushback)
    val output    = DataOutputStream(recording.fold[java.io.OutputStream](socket.getOutputStream)(_.output))
    val callbacks = CallbackConnection(input, output, recording, () => socket.close())
    def client(input: DataInputStream, cuda: Boolean): (Client, ExecutionSessions#ExecutionSessionConnection) = {
      val token    = peekClientToken(input, pushback, cuda)
      val selected = clients.find(token).getOrElse(clients.root)
      bound.get() match {
        case Some(pair) if pair._1 eq selected => pair
        case Some(_) => throw IllegalArgumentException("connection cannot change client credentials")
        case None    =>
          val pair = selected -> selected.executions.connection()
          bound.set(Some(pair))
          pair
      }
    }
    try {
      socket.setTcpNoDelay(true)
      socket.setSoTimeout(socketTimeoutMillis)
      def exchange(): Boolean = try {
        recording.foreach(_.begin())
        input.readInt() match {
          case 0x474d4330 =>
            val authenticated = readExactly(input, TokenBytes)
            require(RPCAuthentication.matches(authenticated, expectedToken), "control authentication failed")
            input.readInt() match {
              case 1 => output.writeUTF(clients.register())
              case 2 =>
                val usage = clients.usage(String(readExactly(input, TokenBytes), StandardCharsets.US_ASCII))
                output.writeLong(usage.cudaRequests)
                output.writeLong(usage.executionRequests)
              case 3 =>
                clients.release(String(readExactly(input, TokenBytes), StandardCharsets.US_ASCII))
                output.writeInt(0)
              case 4     => output.writeInt(0); output.flush(); shutdown()
              case other => throw IllegalArgumentException(s"unknown control operation $other")
            }
            output.flush()
            false
          case RequestMagic =>
            val (owner, execution) = client(input, cuda = true)
            val response           = readRequest(
              input,
              owner.token,
              callbacks,
              owner.usage,
              () => recording.foreach(_.accepted(owner))
            ) match {
              case Right((session, context, request)) => execution.cuda(session, callbacks, context, request)
              case Left(error)                        => RPCResponse.Failure(error)
            }
            writeResponse(output, response)
            recording.foreach(_.response())
            true
          case ExecutionRPCProtocol.RequestMagic =>
            val (owner, execution) = client(input, cuda = false)
            socket.setSoTimeout(0)
            val keep = ExecutionRPCProtocol.exchange(
              input,
              output,
              owner.token,
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
          recording.foreach(_.failed(s"connection exchange failed: ${error.getMessage}"))
          if sys.env.contains("GEMSTONE_RPC_DEBUG") then error.printStackTrace()
          false
      }
      Iterator.continually(exchange()).takeWhile(identity).foreach(_ => ())
    } finally
      try {
        callbacks.retire()
        bound.get().foreach(_._2.close())
        recording.foreach(_.close())
      } finally
        try socket.close()
        finally connections.remove(socket)
  }

  private[server] def peekClientToken(
      input: DataInputStream,
      replay: java.io.PushbackInputStream,
      cuda: Boolean
  ): Array[Byte] = {
    // Keep the buffered socket unmarked: Native marked refills can repeat bytes.
    val prefixSize = (if cuda then 4 + FingerprintBytes else 4) + TokenBytes
    val prefix     = readExactly(input, prefixSize)
    replay.unread(prefix)
    java.util.Arrays.copyOfRange(prefix, prefixSize - TokenBytes, prefixSize)
  }

  private def validToken(token: String): Boolean =
    token.length == TokenBytes && token.forall(character =>
      (character >= '0' && character <= '9') || (character >= 'a' && character <= 'f')
    )

  private def readRequest(
      input: DataInputStream,
      expectedToken: Array[Byte],
      callbacks: CallbackConnection,
      usage: UsageCounter,
      onAuthenticated: () => Unit
  ): Either[CUDAError, (ExecutionRPCProtocol.SessionId, Long, RPCRequest)] = {
    if (input.readInt() != ProtocolVersion) throw IllegalArgumentException("unsupported protocol version")
    val fingerprint = String(readExactly(input, FingerprintBytes), StandardCharsets.US_ASCII)
    val token       = readExactly(input, TokenBytes)
    val session     = ExecutionRPCProtocol.SessionId(input.readLong(), input.readLong())
    val context     = input.readLong()
    val messageId   = MessageId(input.readLong(), input.readLong())
    val nameSize    = input.readInt()
    val payloadSize = input.readInt()
    if (nameSize <= 0 || nameSize > MaximumNameBytes)
      throw IllegalArgumentException(s"invalid function-name size: $nameSize")
    if (payloadSize < 0 || payloadSize > MaximumPayload)
      throw IllegalArgumentException(s"invalid payload size: $payloadSize")
    val functionName = String(readExactly(input, nameSize), StandardCharsets.US_ASCII)
    val payload      = readExactly(input, payloadSize)

    if (!RPCAuthentication.matches(token, expectedToken)) Left(CUDAError(1, "RPC authentication failed"))
    else {
      onAuthenticated()
      if (fingerprint != GeneratedCUDARPC.SchemaFingerprint) {
        Left(CUDAError(1, "CUDA RPC schema fingerprint mismatch"))
      } else {
        GeneratedCUDARPC.Schema.methods.find(method =>
          method.messageId == messageId && method.name == functionName
        ) match {
          case None         => Left(CUDAError(1, s"unknown or mismatched CUDA RPC function: $functionName"))
          case Some(method) =>
            usage.cuda.increment()
            decodeArguments(method, payload, callbacks).map(arguments =>
              (session, context, RPCRequest(messageId, functionName, arguments))
            )
        }
      }
    }

  }

  private def decodeArguments(method: RPCMethod, payload: Array[Byte], callbacks: CallbackConnection)
      : Either[CUDAError, Col[WireValue]] =
    GeneratedCUDAWireCodec.decodeArguments(method, payload, callbacks, MaximumPayload)

  private def writeResponse(output: DataOutputStream, response: RPCResponse): Unit = {
    val encoded = response match {
      case RPCResponse.Success(WireValue.CallResult(status, values)) => encodeValues(values).map(status.code -> _)
      case RPCResponse.Success(value)                                => encodeValues(Col(value)).map(0 -> _)
      case RPCResponse.Failure(error)                                => Left(error)
    }
    val (status, message, payload) = encoded.fold(
      error =>
        (error.code, error.message.getBytes(StandardCharsets.UTF_8).take(MaximumNameBytes * 8), Array.emptyByteArray),
      result => (result._1, Array.emptyByteArray, result._2)
    )
    output.writeInt(ResponseMagic)
    output.writeInt(ProtocolVersion)
    output.writeInt(status)
    output.writeInt(message.length)
    output.writeInt(payload.length)
    if message.nonEmpty then output.write(message)
    if payload.nonEmpty then output.write(payload)
    output.flush()
  }

  private def encodeValues(values: Col[WireValue]): Either[CUDAError, Array[Byte]] =
    GeneratedCUDAWireCodec.encodedSize(values, MaximumPayload).flatMap { size =>
      val bytes = Array.ofDim[Byte](size)
      GeneratedCUDAWireCodec.writeValues(values, ByteBuffer.wrap(bytes), MaximumPayload).map(_ => bytes)
    }

  private[server] final class CallbackConnection(
      input: DataInputStream,
      output: DataOutputStream,
      recording: Option[RPCRecording],
      closeChannel: () => Unit
  ) extends GeneratedCUDAWireCodec.CallbackResolver {
    private val closed     = java.util.concurrent.atomic.AtomicBoolean(false)
    def isClosed: Boolean  = closed.get()
    def retire(): Unit     = closed.set(true)
    def cancel(): Unit     = if (closed.compareAndSet(false, true)) closeChannel()
    private val HostKind   = 1
    private val StreamKind = 2

    def host(callbackId: Long): CUhostFn = {
      require(callbackId != 0L, "host callback ID must be non-zero")
      CallbackTypedef.remote["CUhostFn", Tuple1[CallbackData], Unit] { arguments =>
        invoke(callbackId, arguments._1, HostKind, Array.emptyByteArray)
      }
    }

    def stream(callbackId: Long): CUstreamCallback = {
      require(callbackId != 0L, "stream callback ID must be non-zero")
      CallbackTypedef.remote["CUstreamCallback", (Stream, CUDAStatus, CallbackData), Unit] { arguments =>
        val payload = ByteBuffer.allocate(12).order(ByteOrder.BIG_ENDIAN)
          .putLong(arguments._1.handle)
          .putInt(arguments._2.code)
          .array()
        invoke(callbackId, arguments._3, StreamKind, payload)
      }
    }

    private def invoke(
        callbackId: Long,
        callbackData: CallbackData,
        kind: Int,
        payload: Array[Byte]
    ): Either[CallbackFailure, Unit] = synchronized {
      if (closed.get()) Right(())
      else if (callbackData.capabilityId != callbackId) {
        Left(CallbackFailure.Protocol("callback userdata does not belong to this callback capability"))
      } else
        try {
          output.writeInt(CallbackMagic)
          output.writeInt(ProtocolVersion)
          output.writeLong(callbackId)
          output.writeInt(kind)
          output.writeInt(payload.length)
          if payload.nonEmpty then output.write(payload)
          output.flush()
          recording.foreach(_.callbackRequest())

          if (input.readInt() != CallbackReplyMagic)
            Left(CallbackFailure.Protocol("invalid native callback reply magic"))
          else if (input.readInt() != ProtocolVersion)
            Left(CallbackFailure.Protocol("native callback protocol version mismatch"))
          else {
            val status       = input.readInt()
            val messageSize  = input.readInt()
            val responseSize = input.readInt()
            if (messageSize < 0 || messageSize > 1024 || responseSize < 0 || responseSize > MaximumPayload) {
              Left(CallbackFailure.Protocol("invalid native callback reply size"))
            } else {
              val message = String(readExactly(input, messageSize), StandardCharsets.UTF_8)
              val _       = readExactly(input, responseSize)
              recording.foreach(_.callbackReply())
              if (status == 0 && responseSize == 0) Right(())
              else Left(CallbackFailure.Failed(if (message.nonEmpty) message else s"native callback failed: $status"))
            }
          }
        } catch {
          case scala.util.control.NonFatal(error) => Left(CallbackFailure.Unavailable(error.getMessage))
        }
    }
  }

  private def readExactly(input: DataInputStream, size: Int): Array[Byte] = {
    val result = input.readNBytes(size)
    if (result.length != size) throw EOFException(s"expected $size bytes, received ${result.length}")
    result
  }

  private def parseArguments(arguments: Col[String]): Map[String, String] = arguments match {
    case Col()                                            => Map.empty
    case Col(name, value, rest*) if name.startsWith("--") => parseArguments(rest.toCol) + (name.drop(2) -> value)
    case _                                                => throw IllegalArgumentException(
        "usage: GemstoneRPCServer run --api <api> [provider options] -- <command>; [serve] --endpoint-file <path> [--record <archive>] [--token <64-hex>]; replay|inspect|step <archive>; shutdown --port <port> --token <64-hex>"
      )
  }

}
