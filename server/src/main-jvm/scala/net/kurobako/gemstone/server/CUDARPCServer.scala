package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.Col.*

import net.kurobako.gemstone.abi.*
import net.kurobako.gemstone.cuda.*
import net.kurobako.gemstone.generated.GeneratedCUDARPC
import net.kurobako.gemstone.rpc.*

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

object CUDARPCServer {
  private val RequestMagic       = 0x474d5331
  private val ResponseMagic      = 0x474d5231
  private val ProtocolVersion    = 2
  private val CallbackMagic      = 0x474d4331
  private val CallbackReplyMagic = 0x474d4131
  private val FingerprintBytes   = 64
  private val TokenBytes         = 64
  private val MaximumNameBytes   = 128
  private val MaximumPayload     = 64 * 1024 * 1024

  final class RunningServer private[CUDARPCServer] (
      val port: Int,
      server: ServerSocket,
      acceptThread: Thread
  ) extends AutoCloseable {
    def await(): Unit = acceptThread.join()

    override def close(): Unit = {
      server.close()
      acceptThread.join()
    }
  }

  def main(arguments: Array[String]): Unit = {
    val options  = parseArguments(arguments.toCol)
    val portFile = Path.of(required(options, "port-file"))
    val token    = required(options, "token")
    require(token.matches("[0-9a-f]{64}"), "--token must contain exactly 64 lowercase hexadecimal characters")

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
    System.out.println(s"gemstone CUDA RPC server listening on 127.0.0.1:${running.port}")
    System.out.flush()
    running.await()
  }

  def start(token: String): RunningServer = {
    require(token.matches("[0-9a-f]{64}"), "token must contain exactly 64 lowercase hexadecimal characters")
    val tokenBytes = token.getBytes(StandardCharsets.US_ASCII)
    val server     = new ServerSocket
    server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress, 0))
    val acceptThread = new Thread(
      () => acceptConnections(server, tokenBytes),
      "gemstone-cuda-rpc-accept"
    )
    acceptThread.setDaemon(true)
    acceptThread.start()
    new RunningServer(server.getLocalPort, server, acceptThread)
  }

  private def acceptConnections(server: ServerSocket, token: Array[Byte]): Unit = {
    var accepting = true
    while (accepting)
      try {
        val socket = server.accept()
        val thread = new Thread(
          () => serve(socket, token),
          s"gemstone-cuda-rpc-${socket.getPort}"
        )
        thread.setDaemon(true)
        thread.start()
      } catch {
        case _: SocketException if server.isClosed => accepting = false
      }
  }

  private def serve(socket: Socket, expectedToken: Array[Byte]): Unit =
    try {
      socket.setTcpNoDelay(true)
      socket.setSoTimeout(30000)
      val server    = CUDAServer.emulated
      val input     = new DataInputStream(socket.getInputStream)
      val output    = new DataOutputStream(socket.getOutputStream)
      val callbacks = new CallbackConnection(input, output)
      var open      = true
      while (open)
        try {
          val response = readRequest(input, expectedToken, callbacks) match {
            case Right(request) =>
              server.dispatch(request)
            case Left(error) => RPCResponse.Failure(error)
          }
          writeResponse(output, response)
        } catch {
          case _: EOFException  => open = false
          case error: Throwable =>
            writeResponse(output, RPCResponse.Failure(CUDAError(1, s"malformed request: ${error.getMessage}")))
            open = false
        }
    } finally socket.close()

  private def readRequest(
      input: DataInputStream,
      expectedToken: Array[Byte],
      callbacks: CallbackConnection
  ): Either[CUDAError, RPCRequest] = {
    if (input.readInt() != RequestMagic) throw new IllegalArgumentException("invalid request magic")
    if (input.readInt() != ProtocolVersion) throw new IllegalArgumentException("unsupported protocol version")
    val fingerprint = new String(readExactly(input, FingerprintBytes), StandardCharsets.US_ASCII)
    val token       = readExactly(input, TokenBytes)
    val messageId   = MessageId(input.readLong(), input.readLong())
    val nameSize    = input.readInt()
    val payloadSize = input.readInt()
    if (nameSize <= 0 || nameSize > MaximumNameBytes)
      throw new IllegalArgumentException(s"invalid function-name size: $nameSize")
    if (payloadSize < 0 || payloadSize > MaximumPayload)
      throw new IllegalArgumentException(s"invalid payload size: $payloadSize")
    val functionName = new String(readExactly(input, nameSize), StandardCharsets.US_ASCII)
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
      WireValue.FunctionNameValue(FunctionName(new String(readByteArray(buffer), StandardCharsets.UTF_8)))
    case WireType.KernelFunction =>
      throw new IllegalArgumentException("KernelFunction is a response-only wire type")
    case WireType.KernelLaunch =>
      val gridX       = UInt32(Integer.toUnsignedLong(buffer.getInt()))
      val gridY       = UInt32(Integer.toUnsignedLong(buffer.getInt()))
      val gridZ       = UInt32(Integer.toUnsignedLong(buffer.getInt()))
      val blockX      = UInt32(Integer.toUnsignedLong(buffer.getInt()))
      val blockY      = UInt32(Integer.toUnsignedLong(buffer.getInt()))
      val blockZ      = UInt32(Integer.toUnsignedLong(buffer.getInt()))
      val sharedBytes = UInt32(Integer.toUnsignedLong(buffer.getInt()))
      val count       = buffer.getInt()
      if (count < 0 || count > 4096) throw new IllegalArgumentException(s"invalid kernel argument count: $count")
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
      if (count < 0 || count > 4096) throw new IllegalArgumentException(s"invalid kernel argument count: $count")
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
    val bytes  = new ByteArrayOutputStream
    val output = new DataOutputStream(bytes)
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
        throw new IllegalArgumentException("KernelLaunch is a request-only wire type")
      case WireValue.StreamValue(value)       => output.writeLong(value.handle)
      case WireValue.ByteBufferValue(value)   => writeByteArray(output, value)
      case WireValue.KernelParametersValue(_) =>
        throw new IllegalArgumentException("KernelParameters is a request-only wire type")
      case WireValue.NullableOpaquePointerValue(value) => output.writeBoolean(value)
      case WireValue.CallbackDataValue(_)              =>
        throw new IllegalArgumentException("CallbackData is a request-only wire type")
      case WireValue.CUDAHostCallbackValue(_) =>
        throw new IllegalArgumentException("CUDAHostCallback is a request-only wire type")
      case WireValue.CUDAStreamCallbackValue(_) =>
        throw new IllegalArgumentException("CUDAStreamCallback is a request-only wire type")
      case WireValue.CUDAStatusValue(value) => output.writeInt(value.code)
      case WireValue.CallResult(_, _)       =>
        throw new IllegalArgumentException("CallResult must be encoded as an RPC response")
    }
    output.flush()
    bytes.toByteArray
  }

  private def readByteArray(buffer: ByteBuffer): Array[Byte] = {
    val size = buffer.getInt()
    if (size < 0 || size > MaximumPayload || size > buffer.remaining) {
      throw new IllegalArgumentException(s"invalid byte-array size: $size")
    }
    val result = new Array[Byte](size)
    buffer.get(result)
    result
  }

  private def writeByteArray(output: DataOutputStream, value: Col[Byte]): Unit = {
    output.writeInt(value.length)
    value.iterator.foreach(byte => output.writeByte(byte.toInt))
  }

  private def nonNegativeLong(buffer: ByteBuffer, label: String): Long = {
    val value = buffer.getLong()
    if (value < 0L) throw new IllegalArgumentException(s"$label exceeds signed 64-bit range")
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
              val message = new String(readExactly(input, messageSize), StandardCharsets.UTF_8)
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
    if (result.length != size) throw new EOFException(s"expected $size bytes, received ${result.length}")
    result
  }

  private def parseArguments(arguments: Col[String]): Map[String, String] = arguments match {
    case Col()                                            => Map.empty
    case Col(name, value, rest*) if name.startsWith("--") => parseArguments(rest.toCol) + (name.drop(2) -> value)
    case _ => throw new IllegalArgumentException("usage: CUDARPCServer --port-file <path> --token <64-hex>")
  }

  private def required(options: Map[String, String], name: String): String =
    options.getOrElse(name, throw new IllegalArgumentException(s"missing --$name"))
}
