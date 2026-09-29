package net.kurobako.gemstone.server

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.IOException
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.server.transport.*
import scala.compiletime.error
import scala.util.Using

object protocol {
  type ClientId = ClientId.Type
  object ClientId extends Newtype[Long > 0] {
    extension (value: ClientId) def toLong: Long = value.raw
  }

  /** Reads exactly `size` bytes, failing with `failure` when the stream ends early. */
  def readExactly(input: DataInputStream, size: Int, failure: String => Throwable): Array[Byte] = {
    val result = input.readNBytes(size)
    if (result.length != size) throw failure(s"expected $size bytes, received ${result.length}")
    result
  }

  /** Parses a client identity, rejecting a non-positive wire value. */
  def clientId(value: Long): ClientId =
    ClientId.either(value).fold(
      message => throw IllegalArgumentException(s"invalid client identity: $message"),
      identity
    )
  type SessionId = SessionId.Type

  object SessionId extends Newtype[(Long, Long)] {
    override protected def validate(value: (Long, Long)): Unit =
      require(value._1 != 0L || value._2 != 0L, "execution session identity cannot be the zero sentinel")

    inline def apply(inline high: Long, inline low: Long): SessionId =
      inline if (high == 0L && low == 0L) error("execution session identity cannot be the zero sentinel")
      else apply((high, low))

  }

  case class Header(client: ClientId, session: SessionId, operation: String, payloadSize: Int >= 0)

  /** Reads the execution header after its magic. The stream is never marked or rewound. */
  def readHeader(input: DataInputStream): Header = {
    val client  = clientId(input.readLong())
    val high    = input.readLong()
    val low     = input.readLong()
    val session = SessionId.either((high, low)).fold(message => throw IllegalArgumentException(message), identity)
    val tagSize = input.readInt()
    require(tagSize > 0 && tagSize <= ExecutionRPC.MaximumTagBytes.raw, s"invalid operation tag size: $tagSize")
    val size = input.readInt()
    require(size >= 0 && size <= ExecutionRPC.MaximumPayload.raw, s"invalid execution payload size: $size")
    val tag       = readExactly(input, tagSize, EOFException(_))
    val operation = StandardCharsets.UTF_8.newDecoder().decode(java.nio.ByteBuffer.wrap(tag)).toString
    Header(client, session, operation, size.unsafe[Int >= 0])
  }

  def readPayload(input: DataInputStream, header: Header): Array[Byte] =
    readExactly(input, header.payloadSize.raw, EOFException(_))

  def writeResponse(output: DataOutputStream, result: Either[ExecutionFailure, Array[Byte]]): Unit = {
    val (status, payload) = result match {
      case Right(bytes) => 0 -> bytes
      case Left(error)  =>
        val encoded = summon[Wire.Codec[ExecutionFailure]].encode(bounded(error), ExecutionRPC.Limits)
          .fold(message => throw IllegalArgumentException(s"cannot encode execution failure: $message"), value => value)
        1 -> encoded
    }
    if (payload.length > ExecutionRPC.MaximumPayload.raw)
      throw IllegalArgumentException(s"execution response exceeds ${ExecutionRPC.MaximumPayload} bytes")
    output.writeInt(ExecutionRPC.ResponseMagic)
    output.writeInt(status)
    output.writeInt(payload.length)
    if (payload.nonEmpty) output.write(payload)
    output.flush()
  }

  /** Establish exact schema agreement before any execution requests. */
  def hello(input: DataInputStream, output: DataOutputStream): Unit = {
    val schema = ExecutionRPC.Schema.canonical.getBytes(StandardCharsets.UTF_8)
    require(schema.length <= ExecutionRPC.MaximumSchemaBytes.raw, "execution schema exceeds its byte limit")
    output.writeInt(ExecutionRPC.HelloMagic)
    output.writeInt(schema.length)
    output.write(schema)
    output.flush()
    if (input.readInt() != ExecutionRPC.HelloResponseMagic || input.readInt() != 0)
      throw IOException("execution RPC schema differs from the server")
  }

  def acceptHello(input: DataInputStream, output: DataOutputStream): Boolean = {
    val size = input.readInt()
    require(size > 0 && size <= ExecutionRPC.MaximumSchemaBytes.raw, s"invalid execution schema size: $size")
    val received = readExactly(input, size, EOFException(_))
    val accepted = java.util.Arrays.equals(received, ExecutionRPC.Schema.canonical.getBytes(StandardCharsets.UTF_8))
    output.writeInt(ExecutionRPC.HelloResponseMagic)
    output.writeInt(if (accepted) 0 else 1)
    output.flush()
    accepted
  }

  private def bounded(error: ExecutionFailure): ExecutionFailure =
    error.withDetail(truncateUtf8(error.message, ExecutionRPC.MaximumTextBytes.raw))

  private def truncateUtf8(
      value: String,
      maximumBytes: Int
  ): String =
    if (value.getBytes(StandardCharsets.UTF_8).length <= maximumBytes) value
    else {
      val result = StringBuilder()
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      var index = 0
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      var bytes = 0
      while (index < value.length) {
        val codePoint = value.codePointAt(index)
        val width     =
          if (codePoint <= 0x7f) 1
          else if (codePoint <= 0x7ff) 2
          else if (codePoint <= 0xffff) 3
          else 4
        if (bytes + width > maximumBytes) index = value.length
        else {
          result.appendAll(Character.toChars(codePoint))
          bytes += width
          index += Character.charCount(codePoint)
        }
      }
      result.result()
    }

  object Control {
    val Magic          = 0x474d4331
    val RegisterClient = 1
    val ReleaseClient  = 3
    val Shutdown       = 4
    val Finish         = 5
    val ResponseMagic  = 0x474d5331

    private val TimeoutMillis: Int > 0 = 5000

    def register(endpoint: Endpoint, timeoutMillis: Int > 0 = TimeoutMillis): ClientId =
      exchange(endpoint, timeoutMillis, RegisterClient, None) { input =>
        ClientId.either(input.readLong())
          .fold(
            message => throw IOException(s"Gemstone server returned an invalid client identity: $message"),
            value => value
          )
      }

    def release(endpoint: Endpoint, client: ClientId, timeoutMillis: Int > 0 = TimeoutMillis): Unit =
      exchange(endpoint, timeoutMillis, ReleaseClient, Some(client)) { input =>
        if (input.readInt() != 0) throw IOException("Gemstone server rejected client release")
      }

    def shutdown(endpoint: Endpoint): Unit =
      exchange(endpoint, TimeoutMillis, Shutdown, None) { input =>
        if (input.readInt() != 0) throw IOException("Gemstone server rejected shutdown")
      }

    private def exchange[A](endpoint: Endpoint, timeoutMillis: Int > 0, operation: Int, client: Option[ClientId])(
        read: DataInputStream => A
    ): A =
      Using.resource(transport.connect(endpoint, timeoutMillis)) { channel =>
        val output = DataOutputStream(channel.output)
        writeRequest(output, operation, client)
        val input   = DataInputStream(channel.input)
        val payload = readResponse(input, operation)
        read(DataInputStream(java.io.ByteArrayInputStream(payload)))
      }

    def writeRequest(output: DataOutputStream, operation: Int, client: Option[ClientId] = None): Unit = {
      output.writeInt(Magic)
      output.writeInt(operation)
      output.writeInt(if (client.isDefined) 8 else 0)
      client.foreach(value => output.writeLong(value.toLong))
      output.flush()
    }

    def writeResponse(output: DataOutputStream, operation: Int, value: Long = 0L): Unit = {
      output.writeInt(ResponseMagic)
      output.writeInt(operation)
      output.writeInt(if (operation == RegisterClient) 8 else 4)
      if (operation == RegisterClient) output.writeLong(value) else output.writeInt(value.toInt)
      output.flush()
    }

    def readResponse(input: DataInputStream, operation: Int): Array[Byte] = {
      if (input.readInt() != ResponseMagic || input.readInt() != operation)
        throw IOException("invalid control response")
      val size = input.readInt()
      if (size != (if (operation == RegisterClient) 8 else 4)) throw IOException("invalid control response size")
      readExactly(input, size, IOException(_))
    }

    /** Acknowledgement follows lease release; transport EOF alone is not a lifetime barrier. */
    def finish(input: DataInputStream, output: DataOutputStream): Unit = {
      writeRequest(output, Finish)
      val payload = readResponse(input, Finish)
      if (java.nio.ByteBuffer.wrap(payload).getInt() != 0) throw IOException("RPC connection finish failed")
    }
  }
}
