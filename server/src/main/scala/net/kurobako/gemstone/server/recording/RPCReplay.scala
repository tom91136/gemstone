package net.kurobako.gemstone.server.recording

import net.kurobako.gemstone.PlatformTime
import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.rpc.ExecutionProtocol.CompletionStatus
import net.kurobako.gemstone.rpc.ExecutionRPC.Operation
import net.kurobako.gemstone.rpc.Wire
import net.kurobako.gemstone.server.transport.ServerControl
import net.kurobako.gemstone.server.transport.Transport.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.io.{DataInputStream, DataOutputStream, IOException}
import java.net.{InetAddress, InetSocketAddress, Socket}
import java.nio.ByteBuffer
import java.nio.file.Path
import scala.collection.mutable
import scala.util.Using

/** API-frame replay only: neither reverse instruction execution nor a scheduler for backend data races. */
object RPCReplay {
  final case class Target(port: RPCPort, closeFn: () => Unit) extends AutoCloseable {
    override def close(): Unit = closeFn()
  }
  final case class Options(
      archive: Path,
      protocolIdentity: RPCJournal.ProtocolIdentity,
      timeoutMillis: Int :| Positive = 300000,
      limits: RPCJournal.Limits = RPCJournal.Limits()
  )
  final case class Report(
      events: Int :| Positive0,
      requests: Int :| Positive0,
      extraCompletionPolls: Long :| Positive0
  )

  private def connect(endpoint: Target, timeoutMillis: Int :| Positive): ReplayConnection = {
    val socket = Socket()
    // Ownership transfers only after the connection and stream wrappers are established.
    try {
      socket.connect(InetSocketAddress(InetAddress.getLoopbackAddress, endpoint.port.value), timeoutMillis)
      socket.setSoTimeout(timeoutMillis)
      ReplayConnection(socket, DataInputStream(socket.getInputStream), DataOutputStream(socket.getOutputStream))
    } catch {
      case error: Exception =>
        try socket.close()
        catch { case closeError: Exception => error.addSuppressed(closeError) }
        throw error
    }
  }

  private final class ReplayConnection(
      val socket: Socket,
      val input: DataInputStream,
      val output: DataOutputStream
  ) extends AutoCloseable {
    def send(bytes: Array[Byte]): Unit = { output.write(bytes); output.flush() }
    def finish(): Unit                 =
      // Wait for the server to release this connection's process scope before opening a later one.
      scala.util.Using.resource(socket) { _ =>
        socket.shutdownOutput()
        if (input.read() != -1) throw IOException("RPC replay: unexpected bytes before connection close")
      }
    override def close(): Unit = socket.close()
  }
  // The request paired with the next recorded response advances with the connection.
  private final case class Active(
      wire: ReplayConnection,
      client: RPCJournal.RecordedClientId,
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      var request: Array[Byte]
  )

  // Replay is stateful internally but exposed only as a run-to-completion operation.
  private final class Session(options: Options, reader: RPCJournal.Reader, endpoint: Target)
      extends AutoCloseable {
    private val clients     = mutable.Map.empty[RPCJournal.RecordedClientId, ClientId]
    private val connections = mutable.Map.empty[RPCJournal.RecordedConnectionId, Active]
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var cursor = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var requests = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var polls = 0L
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var closed = false
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var failed         = false
    private def report: Report =
      Report(cursor.refineUnsafe[Positive0], requests.refineUnsafe[Positive0], polls.refineUnsafe[Positive0])

    private def assignedClient(client: RPCJournal.RecordedClientId): ClientId = clients.get(client) match {
      case Some(assigned) => assigned
      case None           =>
        val assigned = ServerControl.register(endpoint.port, options.timeoutMillis)
        clients.update(client, assigned)
        assigned
    }
    private def identified(frame: Array[Byte], client: RPCJournal.RecordedClientId): Array[Byte] = {
      val result = frame.clone()
      ByteBuffer.wrap(result).putLong(ExecutionRPC.RequestClientOffset, assignedClient(client).value)
      result
    }
    private def receive(active: Active, kind: RPCJournal.Kind): Array[Byte] = {
      val input       = active.wire.input
      val magic       = input.readInt()
      val fingerprint = input.readLong()
      val prefix      = java.nio.ByteBuffer.allocate(ExecutionRPC.ResponseHeaderBytes)
      prefix.putInt(magic); prefix.putLong(fingerprint)
      val payloadBytes = if (magic == ExecutionRPC.ResponseMagic) {
        prefix.putInt(input.readInt())
        val payload = input.readInt()
        prefix.putInt(payload)
        payload
      } else throw IOException(f"RPC replay: unexpected response magic 0x$magic%08x")
      if (payloadBytes < 0 || payloadBytes > options.limits.maxFrameBytes - ExecutionRPC.ResponseHeaderBytes)
        throw IOException("RPC replay: response exceeds frame bounds")
      val size  = payloadBytes.refineUnsafe[Positive0]
      val frame = Array.ofDim[Byte](ExecutionRPC.ResponseHeaderBytes + size)
      Array.copy(prefix.array(), 0, frame, 0, ExecutionRPC.ResponseHeaderBytes)
      input.readFully(frame, ExecutionRPC.ResponseHeaderBytes, size)
      RPCJournal.Frames.validate(kind, frame, options.limits.maxFrameBytes, normalized = false)
      frame
    }
    private def isQuery(frame: Array[Byte]): Boolean = {
      val operationId = RPCJournal.Frames.long(frame, ExecutionRPC.RequestOperationOffset)
      val payload     = java.util.Arrays.copyOfRange(frame, ExecutionRPC.RequestHeaderBytes, frame.length)
      Wire.Protocol[Operation].decodePayload(operationId, payload, ExecutionRPC.Limits).exists { decoded =>
        decoded.value match {
          case Operation.QueryCompletion(_) => true
          case _                            => false
        }
      }
    }
    private def completion(frame: Array[Byte]): Option[CompletionStatus] =
      Option.when(
        frame.length >= ExecutionRPC.ResponseHeaderBytes &&
          RPCJournal.Frames.magic(frame) == ExecutionRPC.ResponseMagic &&
          RPCJournal.Frames.integer(frame, ExecutionRPC.ResponseStatusOffset) == 0
      ) {
        val payload = java.util.Arrays.copyOfRange(frame, ExecutionRPC.ResponseHeaderBytes, frame.length)
        summon[Wire.Codec[CompletionStatus]].decode(payload, ExecutionRPC.Limits).toOption
      }.flatten
    private def compare(event: RPCJournal.Event, expected: Array[Byte], active: Active): Unit = {
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      var actual = receive(active, event.kind)
      if (event.kind == RPCJournal.Kind.Response && isQuery(active.request)) {
        if (!completion(expected).contains(CompletionStatus.Pending)) {
          val deadline = PlatformTime.monotonicNanos + options.timeoutMillis.toLong * 1000000L
          while (completion(actual).contains(CompletionStatus.Pending)) {
            if (PlatformTime.monotonicNanos >= deadline)
              throw IOException(s"RPC replay: completion remained pending at event ${event.sequence}")
            Thread.sleep(1L)
            active.wire.send(identified(active.request, active.client))
            actual = receive(active, RPCJournal.Kind.Response)
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

    private def replay(event: RPCJournal.Event): Unit = {
      if (closed || failed) throw IOException("RPC replay: session is closed or failed")
      try {
        val frame = if (event.kind.hasFrame) reader.frame(event) else Array.emptyByteArray
        event.kind match {
          case RPCJournal.Kind.Request =>
            val connection = event.connection.getOrElse(throw IOException("RPC replay: request has no connection"))
            val active     = connections.get(connection) match {
              case Some(existing) if existing.client != event.client =>
                throw IOException("RPC replay: connection changed client")
              case Some(existing) => existing.request = frame; existing
              case None           =>
                val created = Active(connect(endpoint, options.timeoutMillis), event.client, frame)
                connections.update(connection, created)
                created
            }
            active.wire.send(identified(frame, event.client)); requests += 1
          case RPCJournal.Kind.Response =>
            val connection = event.connection.getOrElse(throw IOException("RPC replay: response has no connection"))
            val active     = connections.getOrElse(connection, throw IOException("RPC replay: missing connection"))
            compare(event, frame, active)
          case RPCJournal.Kind.ConnectionClose =>
            val connection = event.connection.getOrElse(throw IOException("RPC replay: close has no connection"))
            connections.remove(connection).foreach(_.wire.finish())
          case RPCJournal.Kind.ClientClose =>
            clients.remove(event.client).foreach(client =>
              ServerControl.release(endpoint.port, client, options.timeoutMillis)
            )
        }
        cursor += 1
      } catch { case error: Exception => failed = true; throw error }
    }
    def run(): Report          = { reader.events.foreach(replay(_)); report }
    override def close(): Unit =
      if (!closed) {
        closed = true
        Using.Manager { use =>
          val _ = use(endpoint)
          val _ = use(reader)
          connections.values.view.foreach { active =>
            val _ = use(active.wire)
          }
          connections.clear()
        }
          .get
      }
  }

  private def open(options: Options, startServer: RPCJournal.Header => Target): Session = {
    val reader = RPCJournal.open(options.archive, options.limits)
    // Reader and endpoint ownership transfer in stages; each acquisition failure closes only what it owns so far.
    try {
      if (!reader.summary.replayable)
        throw IOException(
          s"RPC replay: archive is not replayable: ${reader.summary.reason.map(_.value).getOrElse("unknown reason")}"
        )
      if (reader.summary.header.protocolIdentity != options.protocolIdentity)
        throw IOException("RPC replay: execution protocol identity differs")
      val endpoint = startServer(reader.summary.header)
      try Session(options, reader, endpoint)
      catch {
        case error: Exception =>
          try endpoint.close()
          catch { case closeError: Exception => error.addSuppressed(closeError) }
          throw error
      }
    } catch {
      case error: Exception =>
        try reader.close()
        catch { case closeError: Exception => error.addSuppressed(closeError) }
        throw error
    }
  }
  def run(options: Options, startServer: RPCJournal.Header => Target): Report =
    Using.resource(open(options, startServer))(_.run())
}
