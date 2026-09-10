package net.kurobako.gemstone.server

import java.io.{DataInputStream, DataOutputStream, File, IOException}
import java.net.{InetAddress, InetSocketAddress, Socket}
import java.nio.charset.StandardCharsets
import scala.collection.mutable // scalafix:ok GemstoneRuntimeCollections.mutable-collection; bounded archive and connection indexes

/** API-frame replay only: neither reverse instruction execution nor a scheduler for backend data races. */
@SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneRuntimeCollections.mutable-collection"))
object RPCReplay {
  final case class Endpoint(port: Int, masterToken: String, closeFn: () => Unit) extends AutoCloseable {
    require(port > 0 && port <= 65535)
    require(masterToken.length == 64 && masterToken.forall(c => (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')))
    override def close(): Unit = closeFn()
  }
  final case class Options(
      archive: File,
      schemaFingerprint: String,
      timeoutMillis: Int = 300000,
      limits: RPCJournal.Limits = RPCJournal.Limits()
  ) { require(timeoutMillis > 0) }
  final case class StepResult(sequence: Long, kind: RPCJournal.Kind, client: Long, connection: Long, frameBytes: Int)
  final case class Report(events: Int, requests: Int, callbacks: Int, extraCompletionPolls: Long)

  private final class Wire(endpoint: Endpoint, timeoutMillis: Int) extends AutoCloseable {
    val socket = Socket()
    try {
      socket.connect(InetSocketAddress(InetAddress.getLoopbackAddress, endpoint.port), timeoutMillis)
      socket.setSoTimeout(timeoutMillis)
    } catch { case error: Exception => socket.close(); throw error }
    val input                          = DataInputStream(socket.getInputStream)
    val output                         = DataOutputStream(socket.getOutputStream)
    def send(bytes: Array[Byte]): Unit = { output.write(bytes); output.flush() }
    def finish(): Unit                 =
      // Wait for the server to release this connection's process scope before opening a later one.
      try {
        socket.shutdownOutput()
        if (input.read() != -1) throw IOException("RPC replay: unexpected bytes before connection close")
      } finally socket.close()
    override def close(): Unit = socket.close()
  }
  private final case class Active(wire: Wire, client: Long, var request: Array[Byte])

  /** Keeps the fresh endpoint alive for explicit API stepping. Caller must close even after a failed step. */
  final class Session private[RPCReplay] (options: Options, reader: RPCJournal.Reader, endpoint: Endpoint)
      extends AutoCloseable {
    private val clients                                     = mutable.Map.empty[Long, String]
    private val connections                                 = mutable.Map.empty[Long, Active]
    private var cursor                                      = 0
    private var requests                                    = 0
    private var callbacks                                   = 0
    private var polls                                       = 0L
    private var closed                                      = false
    private var failed                                      = false
    def next: Option[RPCJournal.Event]                      = reader.events.lift(cursor)
    def recordedFrame(event: RPCJournal.Event): Array[Byte] = reader.frame(event)
    def report: Report                                      = Report(cursor, requests, callbacks, polls)

    private def control(operation: Int, token: Option[String] = None): String = {
      val wire = Wire(endpoint, options.timeoutMillis)
      try {
        wire.output.writeInt(0x474d4330)
        wire.output.write(endpoint.masterToken.getBytes(StandardCharsets.US_ASCII))
        wire.output.writeInt(operation)
        token.foreach(value => wire.output.write(value.getBytes(StandardCharsets.US_ASCII)))
        wire.output.flush()
        if (operation == 1) wire.input.readUTF()
        else {
          if (wire.input.readInt() != 0) throw IOException("RPC replay: client close was rejected")
          ""
        }
      } finally wire.close()
    }
    private def token(client: Long): String = clients.getOrElseUpdate(client, control(1))
    private def authenticated(frame: Array[Byte], client: Long): Array[Byte] = {
      val result = frame.clone()
      val bytes  = token(client).getBytes(StandardCharsets.US_ASCII)
      if (bytes.length != 64) throw IOException("RPC replay: endpoint returned an invalid client credential")
      System.arraycopy(bytes, 0, result, RPCFrames.tokenOffset(result), bytes.length)
      result
    }
    private def receive(active: Active, kind: RPCJournal.Kind): Array[Byte] = {
      val input   = active.wire.input
      val magic   = input.readInt()
      val version = input.readInt()
      val prefix  = java.nio.ByteBuffer.allocate(24)
      prefix.putInt(magic); prefix.putInt(version)
      val (header, first, second) = if (magic == RPCFrames.CallbackRequest) {
        prefix.putLong(input.readLong()); prefix.putInt(input.readInt())
        val size = input.readInt(); prefix.putInt(size); (24, 0, size)
      } else if (magic == RPCFrames.ExecutionResponse || magic == RPCFrames.CUDAResponse) {
        prefix.putInt(input.readInt())
        val message = input.readInt(); val payload = input.readInt()
        prefix.putInt(message); prefix.putInt(payload); (20, message, payload)
      } else throw IOException(f"RPC replay: unexpected response magic 0x$magic%08x")
      val size = first.toLong + second.toLong
      if (first < 0 || second < 0 || size > options.limits.maxFrameBytes.toLong - header)
        throw IOException("RPC replay: response exceeds frame bounds")
      val frame = Array.ofDim[Byte](header + size.toInt)
      System.arraycopy(prefix.array(), 0, frame, 0, header)
      input.readFully(frame, header, size.toInt)
      RPCFrames.validate(kind, frame, options.limits.maxFrameBytes, redacted = false)
      frame
    }
    private def isQuery(frame: Array[Byte]): Boolean =
      RPCFrames.magic(frame) == RPCFrames.ExecutionRequest && RPCFrames.integer(frame, 88) == 8
    private def completion(frame: Array[Byte]): Option[Int] =
      Option.when(frame.length == 24 && RPCFrames.magic(frame) == RPCFrames.ExecutionResponse &&
        RPCFrames.integer(frame, 8) == 0 && RPCFrames.integer(frame, 12) == 0 && RPCFrames.integer(frame, 16) == 4)(
        RPCFrames.integer(frame, 20)
      )
    private def compare(event: RPCJournal.Event, expected: Array[Byte], active: Active): Unit = {
      var actual = receive(active, event.kind)
      if (event.kind == RPCJournal.Kind.Response && isQuery(active.request)) {
        if (!completion(expected).contains(1)) {
          val deadline = System.nanoTime() + options.timeoutMillis.toLong * 1000000L
          while (completion(actual).contains(1)) {
            if (System.nanoTime() >= deadline)
              throw IOException(s"RPC replay: completion remained pending at event ${event.sequence}")
            Thread.sleep(1L)
            active.wire.send(authenticated(active.request, active.client))
            actual = receive(active, RPCJournal.Kind.Response)
            polls += 1
          }
        }
        // Earlier completion is permitted, but an unexpected failure is still a real mismatch.
        if (completion(expected).contains(1) && completion(actual).contains(2)) actual = expected
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

    def step(): Option[StepResult] = {
      if (closed || failed) throw IOException("RPC replay: session is closed or failed")
      next.map { event =>
        try {
          val frame = if (event.kind.hasFrame) reader.frame(event) else Array.emptyByteArray
          event.kind match {
            case RPCJournal.Kind.Request =>
              val active = connections.getOrElseUpdate(
                event.connection,
                Active(Wire(endpoint, options.timeoutMillis), event.client, Array.emptyByteArray)
              )
              if (active.client != event.client) throw IOException("RPC replay: connection changed client")
              active.request = frame
              active.wire.send(authenticated(frame, event.client)); requests += 1
            case RPCJournal.Kind.Response | RPCJournal.Kind.CallbackRequest =>
              val active = connections.getOrElse(event.connection, throw IOException("RPC replay: missing connection"))
              compare(event, frame, active)
              if (event.kind == RPCJournal.Kind.CallbackRequest) callbacks += 1
            case RPCJournal.Kind.CallbackReply =>
              connections(
                event.connection
              ).wire.send(frame) // Recorded reply only; external callback code is never invoked.
            case RPCJournal.Kind.ConnectionClose =>
              connections.remove(event.connection).foreach(_.wire.finish())
            case RPCJournal.Kind.ClientClose =>
              clients.remove(event.client).foreach(value => control(3, Some(value)))
          }
          cursor += 1
          StepResult(event.sequence, event.kind, event.client, event.connection, frame.length)
        } catch { case error: Exception => failed = true; throw error }
      }
    }
    def run(): Report          = { while (next.nonEmpty) { val _ = step() }; report }
    override def close(): Unit =
      if (!closed) {
        closed = true
        var failure: Exception | Null      = null
        def closing(action: => Unit): Unit = try action
        catch {
          case error: Exception =>
            if (failure == null) failure = error else failure.addSuppressed(error)
        }
        connections.valuesIterator.foreach(active => closing(active.wire.close()))
        connections.clear()
        closing(reader.close()); closing(endpoint.close())
        if (failure != null) throw failure
      }
  }

  def open(options: Options, startServer: RPCJournal.Header => Endpoint): Session = {
    val reader = RPCJournal.open(options.archive, options.limits)
    try {
      if (!reader.summary.replayable)
        throw IOException(s"RPC replay: archive is not replayable: ${reader.summary.reason}")
      if (reader.summary.header.schemaFingerprint != options.schemaFingerprint)
        throw IOException("RPC replay: CUDA schema fingerprint differs")
      val endpoint = startServer(reader.summary.header)
      try Session(options, reader, endpoint)
      catch { case error: Exception => endpoint.close(); throw error }
    } catch { case error: Exception => reader.close(); throw error }
  }
  def run(options: Options, startServer: RPCJournal.Header => Endpoint): Report =
    scala.util.Using.resource(open(options, startServer))(_.run())
}
