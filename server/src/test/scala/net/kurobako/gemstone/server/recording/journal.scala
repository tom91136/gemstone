package net.kurobako.gemstone.server.recording

import java.nio.ByteBuffer
import java.nio.file.Path
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.server.recording.Journal.*
import net.kurobako.gemstone.testing.*
import scala.util.Using

private object journal {
  val schema: ProtocolIdentity.Type = Journal.ProtocolIdentity.either(ExecutionRPC.Identity)
    .fold(message => throw IllegalStateException(message), value => value)
  private val ping = Wire.Protocol[Operation].encodePayload(Operation.Ping, ExecutionRPC.Limits)
    .fold(message => throw IllegalArgumentException(message), value => value)._1
  def execution(
      operation: String = ping,
      payload: Array[Byte] = Array.emptyByteArray,
      session: Long = 42
  ): Array[Byte] =
    ByteBuffer.allocate(
      ExecutionRPC.RequestHeaderBytes.raw + operation.getBytes(java.nio.charset.StandardCharsets.UTF_8).length +
        payload.length
    ).putInt(ExecutionRPC.RequestMagic)
      .putLong(23L).putLong(session).putLong(17)
      .putInt(operation.getBytes(java.nio.charset.StandardCharsets.UTF_8).length).putInt(payload.length)
      .put(operation.getBytes(java.nio.charset.StandardCharsets.UTF_8)).put(payload).array()
  def response(
      value: Int = 0,
      payload: Array[Byte] = Array.emptyByteArray,
      magic: Int = ExecutionRPC.ResponseMagic
  ): Array[Byte] =
    ByteBuffer.allocate(
      ExecutionRPC.ResponseHeaderBytes.raw + payload.length
    ).putInt(magic)
      .putInt(value).putInt(payload.length).put(payload).array()
  def query: Array[Byte] = {
    val (id, payload) = Wire.Protocol[Operation]
      .encodePayload(Operation.QueryCompletion(CompletionId(1L)), ExecutionRPC.Limits)
      .fold(message => throw IllegalArgumentException(message), value => value)
    execution(id, payload)
  }
  def completion(value: Int): Array[Byte] = {
    val status = value match {
      case 1 => CompletionStatus.Pending
      case 2 => CompletionStatus.Succeeded
      case _ => CompletionStatus.Failed(ExecutionProtocol.ExecutionFailure.Failed("failed"))
    }
    val payload = summon[Wire.Codec[CompletionStatus]].encode(status, ExecutionRPC.Limits)
      .fold(message => throw IllegalArgumentException(message), value => value)
    response(payload = payload)
  }
  def temporary[A](body: Path => A): A =
    files.temporary(directory => body(directory.resolve("recording.gmj")))
  def capture(file: Path)(body: Journal.Writer => Unit): Unit =
    Using.resource(Journal.Writer(file, Journal.Header(schema)))(body)
}
