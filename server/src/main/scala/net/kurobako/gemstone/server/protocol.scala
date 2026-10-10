package net.kurobako.gemstone.server

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.server.Transport.*
import scala.compiletime.error
import scala.util.Using

object protocol {
  type ClientId = ClientId.Type
  object ClientId extends PositiveId

  /** Reads exactly `size` bytes, failing with `failure` when the stream ends early. */
  def readExactly(input: DataInputStream, size: Int >= 0, failure: String => Throwable): Array[Byte] = {
    // Frame lengths are bounded by the caller. Read straight into the final array instead of
    // readNBytes(size)'s growing chunks and final concatenation for large module payloads.
    val result   = Array.ofDim[Byte](size.raw)
    val received = input.readNBytes(result, 0, size.raw)
    if (received != size.raw) throw failure(s"expected $size bytes, received $received")
    result
  }

  /** Parses a client identity, rejecting a non-positive wire value. */
  def clientId(value: Long): ClientId =
    value.refine[ClientId].fold(
      message => throw IllegalArgumentException(s"invalid client identity: $message"),
      identity
    )
  type SessionId = SessionId.Type

  /** A client process: its PID and an incarnation that changes when the PID is reused. */
  object SessionId extends Newtype[(Long, Long)] {
    override protected def violation(value: (Long, Long)): Option[NonEmpty[String]] =
      Option.when[NonEmpty[String]](value == (0L, 0L))("execution session identity cannot be the zero sentinel")

    inline def apply(inline process: Long, inline incarnation: Long): SessionId =
      inline if (process == 0L && incarnation == 0L) error("execution session identity cannot be the zero sentinel")
      else apply((process, incarnation))
  }

  object Frame {
    def magic(bytes: Array[Byte]): Int                = integer(bytes, 0)
    def integer(bytes: Array[Byte], offset: Int): Int = ByteBuffer.wrap(bytes, offset, 4).getInt()

    def operation(bytes: Array[Byte]): String = {
      val size = integer(bytes, ExecutionRPC.RequestTagSizeOffset.raw)
      StandardCharsets.UTF_8.newDecoder()
        .decode(ByteBuffer.wrap(bytes, ExecutionRPC.RequestHeaderBytes.raw, size)).toString
    }

    def payload(bytes: Array[Byte]): Array[Byte] =
      java.util.Arrays.copyOfRange(
        bytes,
        ExecutionRPC.RequestHeaderBytes.raw + integer(bytes, ExecutionRPC.RequestTagSizeOffset.raw),
        bytes.length
      )

    /** Checks framing without requiring the payload to be retained. Callers impose their own size budget. */
    def validatePrefix(bytes: Array[Byte], frameSize: Long): Unit = {
      def check(value: Boolean): Unit = if (!value) throw IOException("invalid RPC frame")
      check(bytes.length >= ExecutionRPC.ResponseHeaderBytes.raw && bytes.length <= frameSize)
      val (header, payload) = magic(bytes) match {
        case ExecutionRPC.RequestMagic =>
          check(bytes.length >= ExecutionRPC.RequestHeaderBytes.raw)
          val tagSize = integer(bytes, ExecutionRPC.RequestTagSizeOffset.raw)
          check(validTagSize(tagSize))
          (ExecutionRPC.RequestHeaderBytes.raw + tagSize, integer(bytes, ExecutionRPC.RequestPayloadSizeOffset.raw))
        case ExecutionRPC.ResponseMagic =>
          val status = integer(bytes, ExecutionRPC.ResponseStatusOffset.raw)
          check(status == 0 || status == 1)
          (ExecutionRPC.ResponseHeaderBytes.raw, integer(bytes, ExecutionRPC.ResponsePayloadSizeOffset.raw))
        case _ => throw IOException("invalid RPC frame magic")
      }
      check(payload >= 0 && header.toLong + payload == frameSize)
    }

    def readResponse(input: DataInputStream, maximum: Int): Array[Byte] = {
      val receivedMagic = input.readInt()
      if (receivedMagic != ExecutionRPC.ResponseMagic) throw IOException("unexpected RPC response magic")
      val prefix = ByteBuffer.allocate(ExecutionRPC.ResponseHeaderBytes.raw).putInt(receivedMagic).array()
      input.readFully(prefix, Integer.BYTES, prefix.length - Integer.BYTES)
      val payloadSize = integer(prefix, ExecutionRPC.ResponsePayloadSizeOffset.raw)
      val frameSize   = ExecutionRPC.ResponseHeaderBytes.raw.toLong + payloadSize
      validatePrefix(prefix, frameSize)
      if (frameSize > maximum) throw IOException("RPC response exceeds frame bounds")
      val frame = java.util.Arrays.copyOf(prefix, frameSize.toInt)
      input.readFully(frame, prefix.length, payloadSize)
      frame
    }
  }

  private def validTagSize(size: Int): Boolean = size > 0 && size <= ExecutionRPC.MaximumTagBytes.raw

  case class Header(client: ClientId, session: SessionId, operation: NonEmpty[String], payloadSize: Int >= 0)

  /** Reads the execution header after its magic. The stream is never marked or rewound. */
  def readHeader(input: DataInputStream): Header = {
    val client      = clientId(input.readLong())
    val process     = input.readLong()
    val incarnation = input.readLong()
    val session     =
      (process, incarnation).refine[SessionId].fold(message => throw IllegalArgumentException(message), identity)
    val tagSize = input.readInt().refine[Int > 0]
      .fold(message => throw IllegalArgumentException(s"invalid operation tag size: $message"), identity)
    require(tagSize.raw <= ExecutionRPC.MaximumTagBytes.raw, s"invalid operation tag size: $tagSize")
    val size = input.readInt().refine[Int >= 0]
      .fold(message => throw IllegalArgumentException(s"invalid execution payload size: $message"), identity)
    require(size.raw <= ExecutionRPC.MaximumPayload.raw, s"invalid execution payload size: $size")
    val tag       = readExactly(input, tagSize, EOFException(_))
    val operation = StandardCharsets.UTF_8.newDecoder().decode(java.nio.ByteBuffer.wrap(tag)).toString
      .refine[NonEmpty[String]].fold(message => throw IllegalArgumentException(message), identity)
    Header(client, session, operation, size)
  }

  def readPayload(input: DataInputStream, header: Header): Array[Byte] =
    readExactly(input, header.payloadSize, EOFException(_))

  def writeResponse(output: DataOutputStream, result: Either[ExecutionFailure, Array[Byte]]): Unit = {
    val (status, payload) = result match {
      case Right(bytes) => 0 -> bytes
      case Left(error)  =>
        val encoded = summon[Wire.Codec[ExecutionFailure]].encode(error, ExecutionRPC.Limits)
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
    val size = input.readInt().refine[Int > 0]
      .fold(message => throw IllegalArgumentException(s"invalid execution schema size: $message"), identity)
    require(size.raw <= ExecutionRPC.MaximumSchemaBytes.raw, s"invalid execution schema size: $size")
    val received = readExactly(input, size, EOFException(_))
    val accepted = java.util.Arrays.equals(received, ExecutionRPC.Schema.canonical.getBytes(StandardCharsets.UTF_8))
    output.writeInt(ExecutionRPC.HelloResponseMagic)
    output.writeInt(if (accepted) 0 else 1)
    output.flush()
    accepted
  }

  object Control {
    val Magic          = 0x474d4331
    val RegisterClient = 1
    val ReleaseClient  = 2
    val Shutdown       = 3
    val Finish         = 4
    val ResponseMagic  = 0x474d5331

    private val TimeoutMillis: Int > 0 = 5000

    def register(endpoint: Endpoint, timeoutMillis: Int > 0 = TimeoutMillis): ClientId =
      exchange(endpoint, timeoutMillis, RegisterClient, None) { input =>
        input.readLong().refine[ClientId]
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
      Using.resource(Sockets.connect(endpoint, timeoutMillis)) { channel =>
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
      client.foreach(value => output.writeLong(value.raw))
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
      val size: Int > 0 = if (operation == RegisterClient) 8 else 4
      if (input.readInt() != size.raw) throw IOException("invalid control response size")
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
