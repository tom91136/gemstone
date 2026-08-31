package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.Col.*

import net.kurobako.gemstone.abi.*
import net.kurobako.gemstone.cuda.*
import net.kurobako.gemstone.generated.GeneratedCUDARPC
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.execution.ProgramExecutor
import net.kurobako.gemstone.launcher.UsageSnapshot

import java.io.ByteArrayOutputStream
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
import java.nio.file.StandardOpenOption
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.LongAdder
import java.util.concurrent.ConcurrentHashMap

object GemstoneRPCServer {
  private val RequestMagic        = 0x474d5331
  private val ResponseMagic       = 0x474d5231
  private val ProtocolVersion     = 2
  private val CallbackMagic       = 0x474d4331
  private val CallbackReplyMagic  = 0x474d4131
  private val FingerprintBytes    = 64
  private val TokenBytes          = 64
  private val MaximumNameBytes    = 128
  private val MaximumPayload      = 128 * 1024 * 1024
  private val SocketTimeoutMillis = 300000

  private final case class ExecutionSessionEntry(service: ExecutionService, connections: Int)

  private final class UsageCounter {
    val cuda      = LongAdder()
    val execution = LongAdder()

    def snapshot: UsageSnapshot = UsageSnapshot(cuda.sum(), execution.sum())
  }

  private final class ExecutionSessions(executor: () => ProgramExecutor) {
    private val entries = AtomicReference(Map.empty[ExecutionRPCProtocol.SessionId, ExecutionSessionEntry])

    def connection(): ExecutionRPCProtocol.SessionConnection = ExecutionSessionConnection(this)

    private def acquire(session: ExecutionRPCProtocol.SessionId): ExecutionService = synchronized {
      val current = entries.get()
      current.get(session) match {
        case Some(entry) =>
          entries.set(current.updated(session, entry.copy(connections = Math.addExact(entry.connections, 1))))
          entry.service
        case None =>
          val service = ExecutionService(executor())
          entries.set(current.updated(session, ExecutionSessionEntry(service, 1)))
          service
      }
    }

    private def release(session: ExecutionRPCProtocol.SessionId): Unit = synchronized {
      val current = entries.get()
      current.get(session).foreach { entry =>
        if (entry.connections == 1) {
          entries.set(current.removed(session))
          entry.service.close()
        } else entries.set(current.updated(session, entry.copy(connections = entry.connections - 1)))
      }
    }

    private final class ExecutionSessionConnection(sessions: ExecutionSessions)
        extends ExecutionRPCProtocol.SessionConnection {
      private val active = AtomicReference(Option.empty[ExecutionRPCProtocol.SessionId])

      override def service(
          session: ExecutionRPCProtocol.SessionId
      ): Either[net.kurobako.gemstone.execution.ExecutionFailure, ExecutionService] = active.get() match {
        case Some(current) if current == session =>
          Right(entries.get()(session).service)
        case Some(_) =>
          Left(net.kurobako.gemstone.execution.ExecutionFailure.InvalidRequest(
            "an execution connection cannot change session identity"
          ))
        case None =>
          active.set(Some(session))
          Right(sessions.acquire(session))
      }

      override def close(): Unit = active.getAndSet(None).foreach(sessions.release)
    }
  }

  final class RunningServer private[GemstoneRPCServer] (
      val port: Int,
      server: ServerSocket,
      acceptThread: Thread,
      connections: ClientConnections,
      usageCounter: UsageCounter
  ) extends AutoCloseable {
    def await(): Unit        = acceptThread.join()
    def usage: UsageSnapshot = usageCounter.snapshot

    override def close(): Unit = {
      server.close()
      connections.closeAll()
      acceptThread.join()
    }
  }

  private final class ClientConnections {
    private val sockets = ConcurrentHashMap.newKeySet[Socket]()

    def add(socket: Socket): Unit    = { val _ = sockets.add(socket) }
    def remove(socket: Socket): Unit = { val _ = sockets.remove(socket) }
    def closeAll(): Unit             = sockets.forEach(_.close())
  }

  def main(arguments: Array[String]): Unit = {
    val options  = parseArguments(arguments.toCol)
    val portFile = Path.of(required(options, "port-file"))
    val token    = required(options, "token")
    require(validToken(token), "--token must contain exactly 64 lowercase hexadecimal characters")

    val running = start(token)
    Option(portFile.getParent).foreach(Files.createDirectories(_))
    Files.writeString(
      portFile,
      s"${running.port}\n",
      StandardCharsets.US_ASCII,
      StandardOpenOption.CREATE,
      StandardOpenOption.TRUNCATE_EXISTING,
      StandardOpenOption.WRITE
    )
    System.out.println(s"gemstone RPC server listening on 127.0.0.1:${running.port}")
    System.out.flush()
    running.await()
  }

  def start(token: String): RunningServer =
    startWith(token, () => ISATypedProgramExecutor.default)

  private[server] def start(token: String, executor: ProgramExecutor): RunningServer =
    startWith(token, () => executor)

  private def startWith(token: String, executor: () => ProgramExecutor): RunningServer = {
    require(validToken(token), "token must contain exactly 64 lowercase hexadecimal characters")
    val tokenBytes = token.getBytes(StandardCharsets.US_ASCII)
    val server     = ServerSocket()
    // Execution resources belong to an authenticated process session. HSA
    // deliberately uses separate connections for memory and AQL execution;
    // both carry the same session identity and therefore share one service.
    val executions  = ExecutionSessions(executor)
    val connections = ClientConnections()
    val usage       = UsageCounter()
    server.bind(InetSocketAddress(InetAddress.getLoopbackAddress, 0))
    val acceptThread = Thread(
      () => acceptConnections(server, tokenBytes, executions, connections, usage),
      "gemstone-rpc-accept"
    )
    acceptThread.setDaemon(true)
    acceptThread.start()
    RunningServer(server.getLocalPort, server, acceptThread, connections, usage)
  }

  private def acceptConnections(
      server: ServerSocket,
      token: Array[Byte],
      executions: ExecutionSessions,
      connections: ClientConnections,
      usage: UsageCounter
  ): Unit =
    try
      Iterator.continually(server.accept()).foreach { socket =>
        connections.add(socket)
        val thread = Thread(
          () => serve(socket, token, executions, connections, usage),
          s"gemstone-rpc-${socket.getPort}"
        )
        thread.setDaemon(true)
        thread.start()
      }
    catch {
      case _: SocketException if server.isClosed => ()
    }

  private def serve(
      socket: Socket,
      expectedToken: Array[Byte],
      executions: ExecutionSessions,
      connections: ClientConnections,
      usage: UsageCounter
  ): Unit = {
    val execution = executions.connection()
    try {
      socket.setTcpNoDelay(true)
      socket.setSoTimeout(SocketTimeoutMillis)
      val server              = CUDAServer.emulated
      val input               = DataInputStream(socket.getInputStream)
      val output              = DataOutputStream(socket.getOutputStream)
      val callbacks           = CallbackConnection(input, output)
      def exchange(): Boolean =
        try
          input.readInt() match {
            case RequestMagic =>
              val response = readRequest(input, expectedToken, callbacks, usage) match {
                case Right(request) =>
                  server.dispatch(request)
                case Left(error) => RPCResponse.Failure(error)
              }
              writeResponse(output, response)
              true
            case ExecutionRPCProtocol.RequestMagic =>
              ExecutionRPCProtocol.exchange(input, output, expectedToken, execution, () => usage.execution.increment())
            case value => throw IllegalArgumentException(f"invalid request magic: 0x$value%08x")
          }
        catch {
          case _: EOFException                       => false
          case _: SocketException if socket.isClosed => false
          case _: Throwable                          => false
        }
      Iterator.continually(exchange()).takeWhile(identity).foreach(_ => ())
    } finally {
      execution.close()
      connections.remove(socket)
      socket.close()
    }
  }

  private def validToken(token: String): Boolean =
    token.length == TokenBytes && token.forall(character =>
      (character >= '0' && character <= '9') || (character >= 'a' && character <= 'f')
    )

  private def readRequest(
      input: DataInputStream,
      expectedToken: Array[Byte],
      callbacks: CallbackConnection,
      usage: UsageCounter
  ): Either[CUDAError, RPCRequest] = {
    if (input.readInt() != ProtocolVersion) throw IllegalArgumentException("unsupported protocol version")
    val fingerprint = String(readExactly(input, FingerprintBytes), StandardCharsets.US_ASCII)
    val token       = readExactly(input, TokenBytes)
    val messageId   = MessageId(input.readLong(), input.readLong())
    val nameSize    = input.readInt()
    val payloadSize = input.readInt()
    if (nameSize <= 0 || nameSize > MaximumNameBytes)
      throw IllegalArgumentException(s"invalid function-name size: $nameSize")
    if (payloadSize < 0 || payloadSize > MaximumPayload)
      throw IllegalArgumentException(s"invalid payload size: $payloadSize")
    val functionName = String(readExactly(input, nameSize), StandardCharsets.US_ASCII)
    val payload      = readExactly(input, payloadSize)

    if (!MessageDigest.isEqual(token, expectedToken)) Left(CUDAError(1, "RPC authentication failed"))
    else if (fingerprint != GeneratedCUDARPC.SchemaFingerprint) {
      Left(CUDAError(1, "CUDA RPC schema fingerprint mismatch"))
    } else {
      GeneratedCUDARPC.Schema.methods.find(method =>
        method.messageId == messageId && method.name == functionName
      ) match {
        case None         => Left(CUDAError(1, s"unknown or mismatched CUDA RPC function: $functionName"))
        case Some(method) =>
          usage.cuda.increment()
          decodeArguments(method, payload, callbacks).map(arguments => RPCRequest(messageId, functionName, arguments))
      }
    }
  }

  private def decodeArguments(
      method: RPCMethod,
      payload: Array[Byte],
      callbacks: CallbackConnection
  ): Either[CUDAError, Col[WireValue]] =
    try {
      val buffer    = ByteBuffer.wrap(payload).order(ByteOrder.BIG_ENDIAN)
      val arguments = method.parameters
        .filter(_.direction != WireDirection.Out)
        .map(parameter => decodeValue(parameter.wireType, buffer, callbacks))
      if (buffer.hasRemaining) Left(CUDAError(1, s"trailing payload bytes for ${method.name}: ${buffer.remaining}"))
      else Right(arguments)
    } catch {
      case error: Throwable => Left(CUDAError(1, s"invalid arguments for ${method.name}: ${error.getMessage}"))
    }

  private def decodeValue(
      wireType: WireType,
      buffer: ByteBuffer,
      callbacks: CallbackConnection
  ): WireValue = wireType match {
    case WireType.UnitValue    => WireValue.UnitValue
    case WireType.Int32        => WireValue.Int32Value(buffer.getInt())
    case WireType.UInt8        => WireValue.UInt8Value(UInt8(buffer.get() & 0xff))
    case WireType.UInt32       => WireValue.UInt32Value(UInt32(Integer.toUnsignedLong(buffer.getInt())))
    case WireType.SizeT        => WireValue.SizeTValue(SizeT(nonNegativeLong(buffer, "SizeT")))
    case WireType.Device       => WireValue.DeviceValue(Device(buffer.getInt()))
    case WireType.DevicePtr    => WireValue.DevicePtrValue(DevicePtr(buffer.getLong()))
    case WireType.Context      => WireValue.ContextValue(Context(buffer.getLong()))
    case WireType.CUDAModule   => WireValue.CUDAModuleValue(CUDAModule(buffer.getLong()))
    case WireType.CUDAFunction => WireValue.CUDAFunctionValue(CUDAFunction(buffer.getLong()))
    case WireType.HostInput    => WireValue.HostInputValue(HostInput(Col.unsafeFromArray(readByteArray(buffer))))
    case WireType.HostOutput   => WireValue.HostOutputValue(HostOutput(Col.unsafeFromArray(readByteArray(buffer))))
    case WireType.ModuleImage  => WireValue.ModuleImageValue(ModuleImage(Col.unsafeFromArray(readByteArray(buffer))))
    case WireType.FunctionName =>
      WireValue.FunctionNameValue(FunctionName(String(readByteArray(buffer), StandardCharsets.UTF_8)))
    case WireType.KernelFunction =>
      throw IllegalArgumentException("KernelFunction is a response-only wire type")
    case WireType.KernelLaunch =>
      val gridX       = UInt32(Integer.toUnsignedLong(buffer.getInt()))
      val gridY       = UInt32(Integer.toUnsignedLong(buffer.getInt()))
      val gridZ       = UInt32(Integer.toUnsignedLong(buffer.getInt()))
      val blockX      = UInt32(Integer.toUnsignedLong(buffer.getInt()))
      val blockY      = UInt32(Integer.toUnsignedLong(buffer.getInt()))
      val blockZ      = UInt32(Integer.toUnsignedLong(buffer.getInt()))
      val sharedBytes = UInt32(Integer.toUnsignedLong(buffer.getInt()))
      val count       = buffer.getInt()
      if (count < 0 || count > 4096) throw IllegalArgumentException(s"invalid kernel argument count: $count")
      val arguments = Col.unsafeFromArray(Array.tabulate(count) { _ =>
        KernelArgument(Col.unsafeFromArray(readByteArray(buffer)))
      })
      WireValue.KernelLaunchValue(
        KernelLaunch(gridX, gridY, gridZ, blockX, blockY, blockZ, sharedBytes, arguments)
      )
    case WireType.Stream           => WireValue.StreamValue(Stream(buffer.getLong()))
    case WireType.ByteBuffer       => WireValue.ByteBufferValue(Col.unsafeFromArray(readByteArray(buffer)))
    case WireType.KernelParameters =>
      val count = buffer.getInt()
      if (count < 0 || count > 4096) throw IllegalArgumentException(s"invalid kernel argument count: $count")
      WireValue.KernelParametersValue(
        KernelParameters(Col.unsafeFromArray(Array.tabulate(count) { _ =>
          KernelArgument(Col.unsafeFromArray(readByteArray(buffer)))
        }))
      )
    case WireType.NullableOpaquePointer => WireValue.NullableOpaquePointerValue(buffer.get() != 0)
    case WireType.CallbackData          => WireValue.CallbackDataValue(CallbackData.remote(buffer.getLong()))
    case WireType.CUDAHostCallback      => WireValue.CUDAHostCallbackValue(callbacks.host(buffer.getLong()))
    case WireType.CUDAStreamCallback    => WireValue.CUDAStreamCallbackValue(callbacks.stream(buffer.getLong()))
    case WireType.CUDAStatus            => WireValue.CUDAStatusValue(CUDAStatus(buffer.getInt()))
  }

  private def writeResponse(output: DataOutputStream, response: RPCResponse): Unit = {
    val (status, message, payload) = response match {
      case RPCResponse.Success(WireValue.CallResult(status, outputs)) =>
        (status.code, Array.emptyByteArray, Col.toArray(outputs.flatMap(value => encodeValue(value))))
      case RPCResponse.Success(value) => (0, Array.emptyByteArray, encodeValue(value))
      case RPCResponse.Failure(error) =>
        val encoded = error.message.getBytes(StandardCharsets.UTF_8)
        (error.code, encoded.take(MaximumNameBytes * 8), Array.emptyByteArray)
    }
    output.writeInt(ResponseMagic)
    output.writeInt(ProtocolVersion)
    output.writeInt(status)
    output.writeInt(message.length)
    output.writeInt(payload.length)
    output.write(message)
    output.write(payload)
    output.flush()
  }

  private def encodeValue(value: WireValue): Array[Byte] = {
    val bytes  = ByteArrayOutputStream()
    val output = DataOutputStream(bytes)
    value match {
      case WireValue.UnitValue                => ()
      case WireValue.Int32Value(value)        => output.writeInt(value)
      case WireValue.UInt8Value(value)        => output.writeByte(value.toInt)
      case WireValue.UInt32Value(value)       => output.writeInt(value.toLong.toInt)
      case WireValue.SizeTValue(value)        => output.writeLong(value.toLong)
      case WireValue.DeviceValue(value)       => output.writeInt(value.ordinal)
      case WireValue.DevicePtrValue(value)    => output.writeLong(value.address)
      case WireValue.ContextValue(value)      => output.writeLong(value.handle)
      case WireValue.CUDAModuleValue(value)   => output.writeLong(value.handle)
      case WireValue.CUDAFunctionValue(value) => output.writeLong(value.handle)
      case WireValue.HostInputValue(value)    => writeByteArray(output, value.bytes)
      case WireValue.HostOutputValue(value)   => writeByteArray(output, value.bytes)
      case WireValue.ModuleImageValue(value)  => writeByteArray(output, value.bytes)
      case WireValue.FunctionNameValue(value) =>
        writeByteArray(output, Col.unsafeFromArray(value.value.getBytes(StandardCharsets.UTF_8)))
      case WireValue.KernelFunctionValue(value) =>
        output.writeLong(value.handle.handle)
        output.writeInt(value.parameterWidths.length)
        value.parameterWidths.iterator.foreach(width => output.writeByte(width.toInt))
      case WireValue.KernelLaunchValue(_) =>
        throw IllegalArgumentException("KernelLaunch is a request-only wire type")
      case WireValue.StreamValue(value)       => output.writeLong(value.handle)
      case WireValue.ByteBufferValue(value)   => writeByteArray(output, value)
      case WireValue.KernelParametersValue(_) =>
        throw IllegalArgumentException("KernelParameters is a request-only wire type")
      case WireValue.NullableOpaquePointerValue(value) => output.writeBoolean(value)
      case WireValue.CallbackDataValue(_)              =>
        throw IllegalArgumentException("CallbackData is a request-only wire type")
      case WireValue.CUDAHostCallbackValue(_) =>
        throw IllegalArgumentException("CUDAHostCallback is a request-only wire type")
      case WireValue.CUDAStreamCallbackValue(_) =>
        throw IllegalArgumentException("CUDAStreamCallback is a request-only wire type")
      case WireValue.CUDAStatusValue(value) => output.writeInt(value.code)
      case WireValue.CallResult(_, _)       =>
        throw IllegalArgumentException("CallResult must be encoded as an RPC response")
    }
    output.flush()
    bytes.toByteArray
  }

  private def readByteArray(buffer: ByteBuffer): Array[Byte] = {
    val size = buffer.getInt()
    if (size < 0 || size > MaximumPayload || size > buffer.remaining) {
      throw IllegalArgumentException(s"invalid byte-array size: $size")
    }
    val result = Array.ofDim[Byte](size)
    buffer.get(result)
    result
  }

  private def writeByteArray(output: DataOutputStream, value: Col[Byte]): Unit = {
    output.writeInt(value.length)
    value.iterator.foreach(byte => output.writeByte(byte.toInt))
  }

  private def nonNegativeLong(buffer: ByteBuffer, label: String): Long = {
    val value = buffer.getLong()
    if (value < 0L) throw IllegalArgumentException(s"$label exceeds signed 64-bit range")
    value
  }

  private final class CallbackConnection(input: DataInputStream, output: DataOutputStream) {
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
      if (callbackData.capabilityId != callbackId) {
        Left(CallbackFailure.Protocol("callback userdata does not belong to this callback capability"))
      } else
        try {
          output.writeInt(CallbackMagic)
          output.writeInt(ProtocolVersion)
          output.writeLong(callbackId)
          output.writeInt(kind)
          output.writeInt(payload.length)
          output.write(payload)
          output.flush()

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
              if (status == 0 && responseSize == 0) Right(())
              else Left(CallbackFailure.Failed(if (message.nonEmpty) message else s"native callback failed: $status"))
            }
          }
        } catch {
          case error: Throwable => Left(CallbackFailure.Unavailable(error.getMessage))
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
    case _ => throw IllegalArgumentException("usage: GemstoneRPCServer --port-file <path> --token <64-hex>")
  }

  private def required(options: Map[String, String], name: String): String =
    options.getOrElse(name, throw IllegalArgumentException(s"missing --$name"))
}
