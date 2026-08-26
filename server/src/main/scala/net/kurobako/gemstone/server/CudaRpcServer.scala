package net.kurobako.gemstone.server

import net.kurobako.gemstone.cuda.*
import net.kurobako.gemstone.generated.GeneratedCudaRpc
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.vendor.CUDAEmulatorServer

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

object CudaRpcServer {
  private val RequestMagic     = 0x474d5331
  private val ResponseMagic    = 0x474d5231
  private val ProtocolVersion  = 1
  private val FingerprintBytes = 64
  private val TokenBytes       = 64
  private val MaximumNameBytes = 128
  private val MaximumPayload   = 64 * 1024 * 1024

  final class RunningServer private[CudaRpcServer] (
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
    val options  = parseArguments(arguments.toVector)
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
      val implementation = new CUDAEmulatorServer
      val input          = new DataInputStream(socket.getInputStream)
      val output         = new DataOutputStream(socket.getOutputStream)
      var open           = true
      while (open)
        try {
          val response = readRequest(input, expectedToken) match {
            case Right(request) =>
              try GeneratedCudaRpc.Dispatcher.dispatch(request, implementation)
              catch {
                case error: Throwable => RpcResponse.Failure(CudaError(999, s"server failure: ${error.getMessage}"))
              }
            case Left(error) => RpcResponse.Failure(error)
          }
          writeResponse(output, response)
        } catch {
          case _: EOFException  => open = false
          case error: Throwable =>
            writeResponse(output, RpcResponse.Failure(CudaError(1, s"malformed request: ${error.getMessage}")))
            open = false
        }
    } finally socket.close()

  private def readRequest(input: DataInputStream, expectedToken: Array[Byte]): Either[CudaError, RpcRequest] = {
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

    if (!MessageDigest.isEqual(token, expectedToken)) Left(CudaError(1, "RPC authentication failed"))
    else if (fingerprint != GeneratedCudaRpc.SchemaFingerprint) {
      Left(CudaError(1, "CUDA RPC schema fingerprint mismatch"))
    } else {
      GeneratedCudaRpc.Schema.methods.find(method =>
        method.messageId == messageId && method.name == functionName
      ) match {
        case None         => Left(CudaError(1, s"unknown or mismatched CUDA RPC function: $functionName"))
        case Some(method) =>
          decodeArguments(method, payload).map(arguments => RpcRequest(messageId, functionName, arguments))
      }
    }
  }

  private def decodeArguments(method: RpcMethod, payload: Array[Byte]): Either[CudaError, Vector[WireValue]] =
    try {
      val buffer    = ByteBuffer.wrap(payload).order(ByteOrder.BIG_ENDIAN)
      val arguments = method.parameters.map(parameter => decodeValue(parameter.wireType, buffer))
      if (buffer.hasRemaining) Left(CudaError(1, s"trailing payload bytes for ${method.name}: ${buffer.remaining}"))
      else Right(arguments)
    } catch {
      case error: Throwable => Left(CudaError(1, s"invalid arguments for ${method.name}: ${error.getMessage}"))
    }

  private def decodeValue(wireType: WireType, buffer: ByteBuffer): WireValue = wireType match {
    case WireType.UnitValue  => WireValue.UnitValue
    case WireType.Int32      => WireValue.Int32Value(buffer.getInt())
    case WireType.UInt8      => WireValue.UInt8Value(UInt8(buffer.get() & 0xff))
    case WireType.UInt32     => WireValue.UInt32Value(UInt32(Integer.toUnsignedLong(buffer.getInt())))
    case WireType.SizeT      => WireValue.SizeTValue(SizeT(nonNegativeLong(buffer, "SizeT")))
    case WireType.Device     => WireValue.DeviceValue(Device(buffer.getInt()))
    case WireType.DevicePtr  => WireValue.DevicePtrValue(DevicePtr(buffer.getLong()))
    case WireType.Context    => WireValue.ContextValue(Context(buffer.getLong()))
    case WireType.HostInput  => WireValue.HostInputValue(HostInput(IArray.unsafeFromArray(readByteArray(buffer))))
    case WireType.HostOutput => WireValue.HostOutputValue(HostOutput(IArray.unsafeFromArray(readByteArray(buffer))))
  }

  private def writeResponse(output: DataOutputStream, response: RpcResponse): Unit = {
    val (status, message, payload) = response match {
      case RpcResponse.Success(value) => (0, Array.emptyByteArray, encodeValue(value))
      case RpcResponse.Failure(error) =>
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
      case WireValue.UnitValue              => ()
      case WireValue.Int32Value(value)      => output.writeInt(value)
      case WireValue.UInt8Value(value)      => output.writeByte(value.toInt)
      case WireValue.UInt32Value(value)     => output.writeInt(value.toLong.toInt)
      case WireValue.SizeTValue(value)      => output.writeLong(value.toLong)
      case WireValue.DeviceValue(value)     => output.writeInt(value.ordinal)
      case WireValue.DevicePtrValue(value)  => output.writeLong(value.address)
      case WireValue.ContextValue(value)    => output.writeLong(value.handle)
      case WireValue.HostInputValue(value)  => writeByteArray(output, value.bytes)
      case WireValue.HostOutputValue(value) => writeByteArray(output, value.bytes)
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

  private def writeByteArray(output: DataOutputStream, value: IArray[Byte]): Unit = {
    output.writeInt(value.length)
    value.iterator.foreach(byte => output.writeByte(byte.toInt))
  }

  private def nonNegativeLong(buffer: ByteBuffer, label: String): Long = {
    val value = buffer.getLong()
    if (value < 0L) throw new IllegalArgumentException(s"$label exceeds signed 64-bit range")
    value
  }

  private def readExactly(input: DataInputStream, size: Int): Array[Byte] = {
    val result = input.readNBytes(size)
    if (result.length != size) throw new EOFException(s"expected $size bytes, received ${result.length}")
    result
  }

  private def parseArguments(arguments: Vector[String]): Map[String, String] = arguments match {
    case Vector()                                            => Map.empty
    case Vector(name, value, rest*) if name.startsWith("--") => parseArguments(rest.toVector) + (name.drop(2) -> value)
    case _ => throw new IllegalArgumentException("usage: CudaRpcServer --port-file <path> --token <64-hex>")
  }

  private def required(options: Map[String, String], name: String): String =
    options.getOrElse(name, throw new IllegalArgumentException(s"missing --$name"))
}
