package net.kurobako.gemstone.server

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import net.kurobako.gemstone.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.protocol.*

private object connections {

  /** A complete execution request frame; parsers read the header after its magic. */
  def requestFrame(
      client: Long,
      process: Long,
      incarnation: Long,
      operation: String,
      payload: Array[Byte] = Array.emptyByteArray
  ): Array[Byte] = {
    val tag = operation.getBytes(java.nio.charset.StandardCharsets.UTF_8)
    java.nio.ByteBuffer.allocate(ExecutionRPC.RequestHeaderBytes.raw + tag.length + payload.length)
      .putInt(ExecutionRPC.RequestMagic).putLong(client).putLong(process).putLong(incarnation)
      .putInt(tag.length).putInt(payload.length).put(tag).put(payload).array()
  }

  class Client(
      endpoint: Endpoint,
      client: ClientId,
      process: Long = 42L,
      incarnation: Long = 17L
  ) extends AutoCloseable {
    private val socket = Sockets.connect(endpoint, 10000)
    private val input  = DataInputStream(socket.input)
    private val output = DataOutputStream(socket.output)

    protocol.hello(input, output)

    def call[A](operation: Operation[A]): Either[ExecutionFailure, A] = {
      val (operationTag, payload) = Wire.Protocol[Operation].encodePayload(operation, ExecutionRPC.Limits)
        .fold(message => throw IllegalArgumentException(message), value => value)
      output.write(requestFrame(client.raw, process, incarnation, operationTag, payload))
      output.flush()

      if (input.readInt() != ExecutionRPC.ResponseMagic) throw IOException("unexpected execution response magic")
      val status = input.readInt()
      val body   = input.readNBytes(input.readInt())
      status match {
        case 0 => operation.replyCodec.decode(body, ExecutionRPC.Limits)
            .left.map(message => ExecutionFailure.InvalidRequest(s"invalid reply: $message"))
        case 1 => summon[Wire.Codec[ExecutionFailure]].decode(body, ExecutionRPC.Limits)
            .fold(message => Left(ExecutionFailure.InvalidRequest(s"invalid failure: $message")), error => Left(error))
        case value =>
          Left(ExecutionFailure.InvalidRequest(s"invalid execution response status: $value"))
      }
    }

    /** Poll until the completion leaves Pending; the RPC has no blocking wait. */
    @annotation.tailrec
    final def awaitCompletion(completion: CompletionId): Either[ExecutionFailure, CompletionStatus] =
      call(Operation.QueryCompletion(completion)) match {
        case Right(CompletionStatus.Pending) =>
          Thread.sleep(1L)
          awaitCompletion(completion)
        case other => other
      }

    def finish(): Unit = Control.finish(input, output)

    override def close(): Unit = socket.close()
  }

}
