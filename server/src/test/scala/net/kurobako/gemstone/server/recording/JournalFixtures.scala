package net.kurobako.gemstone.server.recording

import net.kurobako.gemstone.rpc.ExecutionProtocol.{CompletionId, CompletionStatus}
import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.rpc.ExecutionRPC.Operation
import net.kurobako.gemstone.rpc.Wire

import java.nio.ByteBuffer
import java.nio.file.{Files, Path}
import scala.util.Using

private object JournalFixtures {
  private final class TemporaryJournal(val directory: Path, val file: Path) extends AutoCloseable {
    override def close(): Unit = {
      Using.resource(Files.list(directory))(_.forEach { path =>
        val _ = Files.deleteIfExists(path)
      })
      val _ = Files.deleteIfExists(directory)
    }
  }

  private def temporaryJournal(): TemporaryJournal = {
    val directory = Files.createTempDirectory("gemstone-rpc-journal-")
    TemporaryJournal(directory, directory.resolve("recording.gmj"))
  }

  val schema = RPCJournal.ProtocolIdentity.either(ExecutionRPC.Identity)
    .fold(message => throw IllegalStateException(message), value => value)
  private val ping = Wire.Protocol[Operation].encodePayload(Operation.Ping, ExecutionRPC.Limits)
    .fold(message => throw IllegalArgumentException(message), value => value)._1
  def execution(operation: Long = ping, payload: Array[Byte] = Array.emptyByteArray, session: Long = 42): Array[Byte] =
    ByteBuffer.allocate(
      ExecutionRPC.RequestHeaderBytes + payload.length
    ).putInt(ExecutionRPC.RequestMagic).putLong(ExecutionRPC.Schema.fingerprint)
      .putLong(23L).putLong(session).putLong(17)
      .putLong(operation).putInt(payload.length).put(payload).array()
  def response(
      value: Int = 0,
      payload: Array[Byte] = Array.emptyByteArray,
      magic: Int = ExecutionRPC.ResponseMagic
  ): Array[Byte] =
    ByteBuffer.allocate(
      ExecutionRPC.ResponseHeaderBytes + payload.length
    ).putInt(magic).putLong(ExecutionRPC.Schema.fingerprint)
      .putInt(value).putInt(payload.length).put(payload).array()
  def query: Array[Byte] = {
    val (id, payload) = Wire.Protocol[Operation]
      .encodePayload(Operation.QueryCompletion(CompletionId(1)), ExecutionRPC.Limits)
      .fold(message => throw IllegalArgumentException(message), value => value)
    execution(id, payload)
  }
  def completion(value: Int): Array[Byte] = {
    val status = value match {
      case 1 => CompletionStatus.Pending
      case 2 => CompletionStatus.Succeeded
      case _ => CompletionStatus.Failed(net.kurobako.gemstone.rpc.ExecutionProtocol.ExecutionFailure.Failed("failed"))
    }
    val payload = summon[Wire.Codec[CompletionStatus]].encode(status, ExecutionRPC.Limits)
      .fold(message => throw IllegalArgumentException(message), value => value)
    response(payload = payload)
  }
  def temporary[A](body: Path => A): A =
    Using.resource(temporaryJournal())(temporary => body(temporary.file))
  def capture(file: Path)(body: RPCJournal.Writer => Unit): Unit =
    Using.resource(RPCJournal.Writer(file, RPCJournal.Header(schema)))(body)
}
