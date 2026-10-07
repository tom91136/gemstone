package net.kurobako.gemstone.server.recording

import java.nio.ByteBuffer
import java.nio.file.Path
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.server.recording.Journal.*
import net.kurobako.gemstone.testing.*
import scala.util.Using

private object journal {
  val profile: ExecutionProfile = ExecutionProfile(1000L, 1)
  val client: ClientId          = ClientId(5L)
  private val ping              = Wire.Protocol[Operation].encodePayload(Operation.Ping, ExecutionRPC.Limits)
    .fold(message => throw IllegalArgumentException(message), value => value)._1
  def execution(
      operation: String = ping,
      payload: Array[Byte] = Array.emptyByteArray,
      session: Long = 42
  ): Array[Byte] = connections.requestFrame(23L, session, 17L, operation, payload)
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
      case _ => CompletionStatus.Failed(ExecutionFailure.Failed("failed"))
    }
    val payload = summon[Wire.Codec[CompletionStatus]].encode(status, ExecutionRPC.Limits)
      .fold(message => throw IllegalArgumentException(message), value => value)
    response(payload = payload)
  }
  def temporary[A](body: Path => A): A =
    files.temporary(directory => body(directory.resolve("recording.gmj")))
  def capture(file: Path)(body: Journal.Writer => Unit): Unit =
    Using.resource(Journal.Writer(file, Journal.Header(profile)))(body)
}
