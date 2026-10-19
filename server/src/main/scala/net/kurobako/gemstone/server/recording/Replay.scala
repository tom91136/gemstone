package net.kurobako.gemstone.server.recording

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.file.Path
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.protocol.*
import scala.collection.mutable
import scala.util.Using

/** Replays recorded API frames; device execution can still vary with scheduling. */
object Replay {
  class Target(val endpoint: Endpoint, closeFn: () => Unit) extends AutoCloseable {
    override def close(): Unit = closeFn()
  }
  case class Options(
      archive: Path,
      timeoutMillis: Int > 0 = 300000,
      limits: Journal.Limits = Journal.Limits()
  )
  case class Report(
      events: Int >= 0,
      requests: Int >= 0,
      extraCompletionPolls: Long >= 0
  )

  private def connect(target: Target, timeoutMillis: Int > 0): ReplayConnection = {
    val channel = Sockets.connect(target.endpoint, timeoutMillis)
    try {
      val input  = DataInputStream(channel.input)
      val output = DataOutputStream(channel.output)
      protocol.hello(input, output)
      ReplayConnection(channel, input, output)
    } catch {
      case error: Exception => channel.close(); throw error
    }
  }

  private class ReplayConnection(
      channel: Channel,
      val input: DataInputStream,
      val output: DataOutputStream
  ) extends AutoCloseable {
    def send(bytes: Array[Byte]): Unit = { output.write(bytes); output.flush() }
    def finish(): Unit                 = Using.resource(channel)(_ => Control.finish(input, output))
    override def close(): Unit         = channel.close()
  }
  // The request paired with the next recorded response advances with the connection.
  private class Active(
      val wire: ReplayConnection,
      val client: Journal.RecordedClientId,
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      var request: Array[Byte]
  )

  private class Session(options: Options, reader: Journal.Reader, target: Target)
      extends AutoCloseable {
    private val clients     = mutable.Map.empty[Journal.RecordedClientId, ClientId]
    private val connections = mutable.Map.empty[Journal.RecordedConnectionId, Active]
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var cursor: Int >= 0 = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var requests: Int >= 0 = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var polls: Long >= 0 = 0L
    private def report: Report   =
      Report(cursor, requests, polls)

    private def assignedClient(client: Journal.RecordedClientId): ClientId = clients.get(client) match {
      case Some(assigned) => assigned
      case None           =>
        val assigned = protocol.Control.register(target.endpoint, options.timeoutMillis)
        clients.update(client, assigned)
        assigned
    }
    private def identified(frame: Array[Byte], client: Journal.RecordedClientId): Array[Byte] = {
      val result = frame.clone()
      ByteBuffer.wrap(result).putLong(ExecutionRPC.RequestClientOffset, assignedClient(client).raw)
      result
    }
    private def isQuery(frame: Array[Byte]): Boolean = {
      val operationTag = Frame.operation(frame)
      val payload      = Frame.payload(frame)
      Wire.Protocol[Operation].decodePayload(operationTag, payload, ExecutionRPC.Limits).exists { decoded =>
        decoded.value match {
          case Operation.QueryCompletion(_) => true
          case _                            => false
        }
      }
    }
    private def completion(frame: Array[Byte]): Option[CompletionStatus] =
      Option.when(
        frame.length >= ExecutionRPC.ResponseHeaderBytes &&
          Frame.magic(frame) == ExecutionRPC.ResponseMagic &&
          Frame.integer(frame, ExecutionRPC.ResponseStatusOffset) == 0
      ) {
        val payload = java.util.Arrays.copyOfRange(frame, ExecutionRPC.ResponseHeaderBytes, frame.length)
        summon[Wire.Codec[CompletionStatus]].decode(payload, ExecutionRPC.Limits).toOption
      }.flatten
    private def compare(event: Journal.Event, expected: Array[Byte], active: Active): Unit = {
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      var actual = Frame.readResponse(active.wire.input, options.limits.maxFrameBytes.raw)
      if (event.kind == Journal.Kind.Response && isQuery(active.request)) {
        if (!completion(expected).contains(CompletionStatus.Pending)) {
          val deadline = platform.monotonicNanos + options.timeoutMillis.toLong * 1000000L
          while (completion(actual).contains(CompletionStatus.Pending)) {
            if (platform.monotonicNanos >= deadline)
              throw IOException(s"RPC replay: completion remained pending at event ${event.sequence}")
            Thread.sleep(1L)
            active.wire.send(identified(active.request, active.client))
            actual = Frame.readResponse(active.wire.input, options.limits.maxFrameBytes.raw)
            polls += 1
          }
        }
        // Earlier completion is permitted, but an unexpected failure is still a real mismatch.
        if (
          completion(expected).contains(CompletionStatus.Pending) &&
          completion(actual).contains(CompletionStatus.Succeeded)
        ) actual = expected
      }
      if (!java.util.Arrays.equals(expected, actual)) {
        val mismatch = expected.indices.find(i => i >= actual.length || expected(i) != actual(i)).getOrElse(math.min(
          expected.length,
          actual.length
        ))
        throw IOException(
          s"RPC replay: frame differs at event ${event.sequence}, byte $mismatch (expected ${expected.length}, received ${actual.length} bytes)"
        )
      }
    }

    private def replay(event: Journal.Event): Unit = {
      val frame = if (event.kind.hasFrame) reader.frame(event) else Array.emptyByteArray
      event.kind match {
        case Journal.Kind.Request =>
          val connection = event.connection.getOrElse(throw IOException("RPC replay: request has no connection"))
          val active     = connections.get(connection) match {
            case Some(existing) if existing.client != event.client =>
              throw IOException("RPC replay: connection changed client")
            case Some(existing) => existing.request = frame; existing
            case None           =>
              val created = Active(connect(target, options.timeoutMillis), event.client, frame)
              connections.update(connection, created)
              created
          }
          active.wire.send(identified(frame, event.client)); requests += 1
        case Journal.Kind.Response =>
          val connection = event.connection.getOrElse(throw IOException("RPC replay: response has no connection"))
          val active     = connections.getOrElse(connection, throw IOException("RPC replay: missing connection"))
          compare(event, frame, active)
        case Journal.Kind.ConnectionClose =>
          val connection = event.connection.getOrElse(throw IOException("RPC replay: close has no connection"))
          connections.remove(connection).foreach(_.wire.finish())
        case Journal.Kind.ClientClose =>
          clients.remove(event.client).foreach(client =>
            protocol.Control.release(target.endpoint, client, options.timeoutMillis)
          )
      }
      cursor += 1
    }
    def run(): Report          = { reader.events.foreach(replay(_)); report }
    override def close(): Unit =
      throwables.closeAll(connections.values.toList.map(active => () => active.wire.close()))
  }

  def run(options: Options, startServer: Journal.Header => Target): Report =
    Using.Manager { use =>
      val reader = use(Journal.open(options.archive, options.limits))
      if (!reader.summary.replayable)
        throw IOException(
          s"RPC replay: archive is not replayable: ${reader.summary.reason.getOrElse("unknown reason")}"
        )
      if (reader.summary.header.schema != ExecutionRPC.Schema.canonical)
        throw IOException("RPC replay: execution schema differs")
      val target = use(startServer(reader.summary.header))
      use(Session(options, reader, target)).run()
    }.get
}
