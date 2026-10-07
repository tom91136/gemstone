package net.kurobako.gemstone.server.recording

import java.io.DataOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.locks.ReentrantLock
import java.util.zip.CRC32
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.server.protocol.*
import scala.collection.mutable
import scala.util.Using
import scala.util.control.NonFatal
import upickle.default.ReadWriter
import upickle.default.readBinary
import upickle.default.writeBinary
import upickle.implicits.key

/** Bounded MessagePack RPC archives. Client identities are normalised; application payloads remain sensitive data.
  * Record checksums detect accidental corruption; they are not security boundaries.
  */
object Journal {
  private val Format                 = "gemstone-rpc-journal"
  private val Version                = 4
  private val RecordFramingBytes     = 8L
  private val RecordOverheadBytes    = 4096
  private val MetadataRecordLimit    = 4096
  private val HeaderRecordLimit      = ExecutionRPC.MaximumSchemaBytes.raw + MetadataRecordLimit
  private val FooterReserve          = MetadataRecordLimit.toLong + RecordFramingBytes
  private val FailureReasonMaxLength = 512

  inline val MaximumFrameBytes   = 2147479551
  inline val MinimumArchiveBytes = 4104L

  type RecordedClientId = RecordedClientId.Type
  object RecordedClientId extends PositiveId

  type RecordedConnectionId = RecordedConnectionId.Type
  object RecordedConnectionId extends PositiveId

  type EventId = EventId.Type
  object EventId extends Newtype[Long >= 0] {
    extension (id: EventId) def next: EventId = apply(id.unwrap + 1L)
  }

  type FailureReason = FailureReason.Type
  object FailureReason extends Newtype[String] {
    override protected def violation(value: String): Option[NonEmpty[String]] =
      Option.unless[NonEmpty[String]](
        value.nonEmpty && value.length <= FailureReasonMaxLength && !value.contains('\u0000')
      )("failure reason must be nonempty, NUL-free and at most 512 characters")
  }

  type FrameByteSize = FrameByteSize.Type
  object FrameByteSize extends Newtype[Int in 1 ~ MaximumFrameBytes.type]

  type ArchiveByteSize = ArchiveByteSize.Type
  object ArchiveByteSize extends Newtype[Long > MinimumArchiveBytes.type]

  case class Limits(
      maxFrameBytes: FrameByteSize = FrameByteSize(134221824),
      maxArchiveBytes: ArchiveByteSize = ArchiveByteSize(8589934592L),
      maxEvents: Int > 0 = 1000000
  )
  case class ExecutionProfile(
      maxInstructions: Long > 0,
      parallelism: Int > 0
  )
  case class Header(
      executionProfile: ExecutionProfile,
      limits: Limits = Limits(),
      schema: String = ExecutionRPC.Schema.canonical
  )
  enum Kind(val code: Int, val hasFrame: Boolean) {
    case Request         extends Kind(1, true)
    case Response        extends Kind(2, true)
    case ConnectionClose extends Kind(3, false)
    case ClientClose     extends Kind(4, false)
  }
  case class Event(
      sequence: EventId,
      kind: Kind,
      client: RecordedClientId,
      connection: Option[RecordedConnectionId]
  )
  case class Summary(
      header: Header,
      events: Int >= 0,
      frames: Int >= 0,
      storedBytes: Long > 0,
      replayable: Boolean,
      reason: Option[FailureReason]
  )

  object Frames {
    def normalised(
        kind: Kind,
        bytes: Array[Byte],
        maximum: FrameByteSize
    ): Array[Byte] = {
      validate(kind = kind, bytes = bytes, maximum = maximum, normalised = false)
      if (kind != Kind.Request) bytes
      else {
        val result = bytes.clone()
        ByteBuffer.wrap(result).putLong(ExecutionRPC.RequestClientOffset.raw, 0L)
        result
      }
    }

    def validate(kind: Kind, bytes: Array[Byte], maximum: FrameByteSize, normalised: Boolean): Unit = {
      if (bytes.length > maximum.raw) throw IOException("RPC journal: frame exceeds archive bounds")
      Frame.validatePrefix(bytes, bytes.length.toLong)
      val expectedMagic = kind match {
        case Kind.Request  => ExecutionRPC.RequestMagic
        case Kind.Response => ExecutionRPC.ResponseMagic
        case _             => throw IOException("RPC journal: event has no frame")
      }
      if (Frame.magic(bytes) != expectedMagic) throw IOException("RPC journal: frame kind/magic differ")
      if (normalised && kind == Kind.Request) {
        if (ByteBuffer.wrap(bytes).getLong(ExecutionRPC.RequestClientOffset.raw) != 0L)
          throw IOException("RPC journal: request client is not normalised")
      }
    }
  }

  private sealed trait Record derives ReadWriter
  @key("header")
  private case class HeaderRecord(
      format: String,
      version: Int,
      schema: String,
      maxFrameBytes: Int,
      maxArchiveBytes: Long,
      maxEvents: Int,
      executionProfile: ExecutionProfileRecord
  ) extends Record derives ReadWriter
  private case class ExecutionProfileRecord(maxInstructions: Long, parallelism: Int) derives ReadWriter
  @key("event")
  private case class EventRecord(
      sequence: Long,
      kind: Int,
      client: Long,
      connection: Long,
      frame: Option[Array[Byte]]
  ) extends Record derives ReadWriter
  @key("footer")
  private case class FooterRecord(
      events: Int,
      frames: Int,
      replayable: Boolean,
      reason: Option[String]
  ) extends Record derives ReadWriter

  private case class RecordLocation(
      offset: Long,
      payloadBytes: Int
  )
  private case class ConnectionState(
      client: RecordedClientId,
      requestPending: Boolean = false,
      closed: Boolean = false
  )

  private def invalid(message: String): Nothing                 = throw IOException(s"RPC journal: $message")
  private def ensure(condition: Boolean, message: String): Unit = if (!condition) invalid(message)
  private def crc32(bytes: Array[Byte]): Long                   = {
    val checksum = CRC32()
    checksum.update(bytes)
    checksum.getValue
  }
  private def encode(record: Record): Array[Byte] = writeBinary(record)
  private def decode(bytes: Array[Byte]): Record  =
    try readBinary[Record](bytes)
    catch { case NonFatal(error) => throw IOException("RPC journal: invalid MessagePack record", error) }

  private def writeRecord(output: DataOutputStream, bytes: Array[Byte]): Long = {
    output.writeInt(bytes.length)
    output.write(bytes)
    output.writeInt(crc32(bytes).toInt)
    bytes.length.toLong + RecordFramingBytes
  }

  private def readRecord(
      input: RandomAccessFile,
      maximumPayloadBytes: Int
  ): Option[(RecordLocation, Record)] = {
    val offset    = input.getFilePointer
    val remaining = input.length() - offset
    if (remaining == 0L) None
    else {
      ensure(remaining >= RecordFramingBytes, "incomplete record framing")
      val size = input.readInt()
      ensure(size > 0 && size <= maximumPayloadBytes, "record exceeds its byte limit")
      ensure(size.toLong + 4L <= input.length() - input.getFilePointer, "incomplete record payload")
      val bytes = Array.ofDim[Byte](size)
      input.readFully(bytes)
      val expected = Integer.toUnsignedLong(input.readInt())
      ensure(crc32(bytes) == expected, "record checksum differs")
      Some(RecordLocation(offset, size) -> decode(bytes))
    }
  }

  object Writer {
    @SuppressWarnings(Array("scalafix:BannedSyntax.explicit-new"))
    def apply(file: Path, header: Header): Writer = {
      Option(file.getParent).foreach(Files.createDirectories(_))
      try Files.createFile(file)
      catch { case _: FileAlreadyExistsException => invalid(s"capture already exists: ${file.getFileName}") }
      // Ownership transfers to Writer on success, so a lexical Using scope cannot close this stream here.
      val output = try {
        PrivateFile.restrict(file)
        DataOutputStream(Files.newOutputStream(file))
      } catch {
        case NonFatal(error) =>
          try { val _ = Files.deleteIfExists(file) }
          catch { case NonFatal(deleteError) => error.addSuppressed(deleteError) }
          throw error
      }
      try new Writer(header, output)
      catch {
        case NonFatal(error) =>
          try output.close()
          catch { case NonFatal(closeError) => error.addSuppressed(closeError) }
          try { val _ = Files.deleteIfExists(file) }
          catch { case NonFatal(deleteError) => error.addSuppressed(deleteError) }
          throw error
      }
    }
  }

  /** Tracks protocol exchange shape, not backend thread scheduling or instruction-level execution. */
  private class Order {
    private val connections        = mutable.Map.empty[RecordedConnectionId, ConnectionState]
    private val closedClients      = mutable.Set.empty[RecordedClientId]
    def accept(event: Event): Unit =
      if (event.kind == Kind.ClientClose) {
        ensure(event.connection.isEmpty && !closedClients(event.client), "duplicate or malformed client close")
        closedClients += event.client
      } else {
        val connection = event.connection.getOrElse(invalid("event has no connection identity"))
        val previous   = connections.getOrElse(connection, ConnectionState(event.client))
        ensure(previous.client == event.client && !previous.closed, "connection changed client or was already closed")
        val next = event.kind match {
          case Kind.Request =>
            ensure(
              !closedClients(event.client) && !previous.requestPending,
              "request after client close or while another request is pending"
            )
            previous.copy(requestPending = true)
          case Kind.Response =>
            ensure(previous.requestPending, "response has no request")
            previous.copy(requestPending = false)
          case Kind.ConnectionClose => previous.copy(closed = true)
          case Kind.ClientClose     => previous
        }
        connections.update(connection, next)
      }
    def complete: Boolean = connections.values.view.forall(state => state.closed && !state.requestPending)
  }

  // Archive state advances monotonically behind Writer's lock.
  class Writer private (val header: Header, output: DataOutputStream) extends AutoCloseable {
    private val lock    = ReentrantLock()
    private val clients = mutable.Map.empty[ClientId, RecordedClientId]
    private val order   = Order()
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var nextClient = RecordedClientId(1L)
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var nextConnection = RecordedConnectionId(1L)
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var sequence = EventId(0L)
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var frames: Int >= 0 = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var written = 0L
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var closed = false
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var failure = Option.empty[FailureReason]

    private val encodedHeader = encode(HeaderRecord(
      Format,
      Version,
      header.schema,
      header.limits.maxFrameBytes.raw,
      header.limits.maxArchiveBytes.raw,
      header.limits.maxEvents.raw,
      ExecutionProfileRecord(header.executionProfile.maxInstructions.raw, header.executionProfile.parallelism.raw)
    ))
    ensure(encodedHeader.length <= HeaderRecordLimit, "archive header exceeds its byte limit")
    ensure(
      encodedHeader.length.toLong + RecordFramingBytes + FooterReserve <= header.limits.maxArchiveBytes.raw,
      "archive byte limit cannot hold its header and footer"
    )
    written = Math.addExact(written, writeRecord(output, encodedHeader))

    private def recorded(client: ClientId): RecordedClientId = clients.getOrElseUpdate(
      client, {
        val id = nextClient
        nextClient = nextClient.next
        id
      }
    )
    def connection(client: ClientId): Connection = lock.withLock {
      ensure(!closed, "capture is closed")
      val id = nextConnection
      nextConnection = nextConnection.next
      Connection(this, recorded(client), id)
    }
    def clientClosed(client: ClientId): Unit = lock.withLock {
      clients.get(client).foreach(append(Kind.ClientClose, _, None, Array.emptyByteArray))
    }
    def markUnreplayable(reason: String): Unit = lock.withLock {
      if (failure.isEmpty) {
        val sanitised = reason.filterNot(_ == '\u0000').take(FailureReasonMaxLength)
        val detail    = Option.when(sanitised.nonEmpty)(sanitised).getOrElse("unspecified recording failure")
        failure = Some(detail.refine[FailureReason]
          .fold(message => invalid(s"invalid failure reason: $message"), value => value))
      }
    }
    private[Journal] def append(
        kind: Kind,
        client: RecordedClientId,
        connection: Option[RecordedConnectionId],
        bytes: Array[Byte]
    ): Unit =
      lock.withLock {
        ensure(!closed, "capture is closed")
        try {
          ensure(sequence.raw < header.limits.maxEvents.raw, "event limit exceeded")
          val frame   = Option.when(kind.hasFrame)(Journal.Frames.normalised(kind, bytes, header.limits.maxFrameBytes))
          val eventId = sequence
          val record  = encode(EventRecord(
            eventId.raw,
            kind.code,
            client.raw,
            connection.fold(0L)(_.raw),
            frame
          ))
          val encodedBytes = record.length.toLong + RecordFramingBytes
          ensure(
            encodedBytes <= header.limits.maxArchiveBytes.raw - written - FooterReserve,
            "archive byte limit exceeded"
          )
          val event = Event(eventId, kind, client, connection)
          order.accept(event)
          written = Math.addExact(written, writeRecord(output, record))
          if (kind.hasFrame) frames += 1
          sequence = sequence.next
        } catch {
          case error: Exception =>
            markUnreplayable("capture failed before all events could be written")
            throw error
        }
      }
    override def close(): Unit = lock.withLock {
      if (!closed) {
        closed = true
        if (!order.complete && failure.isEmpty)
          failure = Some(FailureReason("capture ended with an open or incomplete exchange"))
        Using.resource(output) { output =>
          val footer =
            encode(FooterRecord(Math.toIntExact(sequence.raw), frames.raw, failure.isEmpty, failure.map(_.raw)))
          ensure(footer.length <= MetadataRecordLimit, "archive footer exceeds its byte limit")
          ensure(
            footer.length.toLong + RecordFramingBytes <= header.limits.maxArchiveBytes.raw - written,
            "archive byte limit exceeded by footer"
          )
          written = Math.addExact(written, writeRecord(output, footer))
          output.flush()
        }
      }
    }
  }

  /** One connection's request/response/close lifecycle, driven by its connection thread. */
  class Connection private[Journal] (
      writer: Writer,
      val client: RecordedClientId,
      val id: RecordedConnectionId
  ) extends AutoCloseable {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var started = false
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var closed                     = false
    def request(frame: Array[Byte]): Unit  = { writer.append(Kind.Request, client, Some(id), frame); started = true }
    def response(frame: Array[Byte]): Unit = writer.append(Kind.Response, client, Some(id), frame)
    override def close(): Unit             =
      if (!closed) {
        closed = true
        if (started) writer.append(Kind.ConnectionClose, client, Some(id), Array.emptyByteArray)
      }
  }

  class Reader private[Journal] (
      input: RandomAccessFile,
      val summary: Summary,
      val events: Vec[Event],
      locations: Map[EventId, RecordLocation]
  ) extends AutoCloseable {
    private val lock                     = ReentrantLock()
    def frame(event: Event): Array[Byte] = lock.withLock {
      ensure(event.kind.hasFrame, "event has no frame")
      val location = locations.getOrElse(event.sequence, invalid("missing frame record"))
      input.seek(location.offset)
      readRecord(
        input,
        summary.header.limits.maxFrameBytes.raw + RecordOverheadBytes
      ) match {
        case Some((_, EventRecord(sequence, kind, client, connection, Some(frame)))) =>
          ensure(
            sequence == event.sequence.raw && kind == event.kind.code && client == event.client.raw &&
              connection == event.connection.fold(0L)(_.raw),
            "event changed after archive verification"
          )
          Journal.Frames.validate(
            kind = event.kind,
            bytes = frame,
            maximum = summary.header.limits.maxFrameBytes,
            normalised = true
          )
          frame
        case _ => invalid("frame record changed after archive verification")
      }
    }
    override def close(): Unit = input.close()
  }

  // Parse one bounded MessagePack record at a time and retain only event metadata and file locations.
  def open(file: Path, limits: Limits = Limits()): Reader = {
    val input = RandomAccessFile(file.toFile, "r")
    // Ownership transfers to Reader after validation; failures close the not-yet-transferred handle below.
    try {
      ensure(input.length() <= limits.maxArchiveBytes.raw, "archive exceeds reader byte limit")
      val (header, declared)           = readHeader(input, limits)
      val (events, locations, summary) = readEvents(input, header, declared)
      Reader(input, summary, events, locations)
    } catch {
      case error: IOException              => closeAfterFailure(input, error)
      case error: IllegalArgumentException =>
        closeAfterFailure(input, IOException("RPC journal: invalid archive header", error))
      case error: ArithmeticException =>
        closeAfterFailure(input, IOException("RPC journal: invalid archive limits", error))
      case NonFatal(error) => closeAfterFailure(input, error)
    }
  }

  private def readHeader(input: RandomAccessFile, limits: Limits): (Header, Limits) = {
    val headerRecord = readRecord(input, HeaderRecordLimit) match {
      case Some((_, value: HeaderRecord)) => value
      case _                              => invalid("archive does not start with a header")
    }
    ensure(headerRecord.format == Format && headerRecord.version == Version, "unknown archive format/version")
    val maxFrameBytes = headerRecord.maxFrameBytes.refine[FrameByteSize]
      .fold(message => invalid(s"invalid frame byte limit: $message"), value => value)
    val maxArchiveBytes = headerRecord.maxArchiveBytes.refine[ArchiveByteSize]
      .fold(message => invalid(s"invalid archive byte limit: $message"), value => value)
    val maxEvents = headerRecord.maxEvents.refine[Int > 0]
      .fold(message => invalid(s"invalid event limit: $message"), value => value)
    val declared = Limits(maxFrameBytes, maxArchiveBytes, maxEvents)
    ensure(
      declared.maxFrameBytes.raw <= limits.maxFrameBytes.raw &&
        declared.maxArchiveBytes.raw <= limits.maxArchiveBytes.raw &&
        declared.maxEvents <= limits.maxEvents,
      "archive limits exceed reader limits"
    )
    ensure(input.length() <= declared.maxArchiveBytes.raw, "archive exceeds its declared byte limit")
    val maxInstructions = headerRecord.executionProfile.maxInstructions.refine[Long > 0]
      .fold(message => invalid(s"invalid maximum instruction count: $message"), value => value)
    val parallelism = headerRecord.executionProfile.parallelism.refine[Int > 0]
      .fold(message => invalid(s"invalid execution parallelism: $message"), value => value)
    Header(ExecutionProfile(maxInstructions, parallelism), declared, headerRecord.schema) -> declared
  }

  private def readEvents(
      input: RandomAccessFile,
      header: Header,
      declared: Limits
  ): (Vec[Event], Map[EventId, RecordLocation], Summary) = {
    val events      = Vec.newBuilder[Event]
    val locations   = mutable.Map.empty[EventId, RecordLocation]
    val order       = Order()
    val recordLimit = declared.maxFrameBytes.raw + RecordOverheadBytes
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var count: Int >= 0 = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var frames: Int >= 0 = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var summary = Option.empty[Summary]
    while (summary.isEmpty)
      readRecord(input, math.max(recordLimit, MetadataRecordLimit)) match {
        case Some((location, EventRecord(sequence, code, client, connection, frame))) =>
          ensure(count.raw < declared.maxEvents.raw, "too many events")
          val kind = Kind.values.find(_.code == code).getOrElse(invalid("unknown event kind"))
          ensure(sequence == count.toLong.raw, "event sequence is not contiguous")
          ensure(frame.nonEmpty == kind.hasFrame, "event frame does not match its kind")
          frame.foreach(bytes =>
            Journal.Frames.validate(kind = kind, bytes = bytes, maximum = declared.maxFrameBytes, normalised = true)
          )
          val eventId = sequence.refine[EventId]
            .fold(message => invalid(s"invalid event identity: $message"), value => value)
          val clientId = client.refine[RecordedClientId]
            .fold(message => invalid(s"invalid client identity: $message"), value => value)
          val connectionId = if (kind == Kind.ClientClose) {
            ensure(connection == 0L, "client close has a connection identity")
            None
          } else Some(connection.refine[RecordedConnectionId]
            .fold(message => invalid(s"invalid connection identity: $message"), value => value))
          val event = Event(eventId, kind, clientId, connectionId)
          order.accept(event)
          if (kind.hasFrame) { frames += 1; locations.update(eventId, location) }
          events += event
          count += 1
        case Some((location, FooterRecord(expectedEvents, expectedFrames, replayable, reason))) =>
          ensure(location.payloadBytes <= MetadataRecordLimit, "archive footer exceeds its byte limit")
          ensure(expectedEvents == count.raw && expectedFrames == frames.raw, "footer totals differ")
          val failure: Option[FailureReason] = reason.map(raw =>
            raw.refine[FailureReason]
              .fold(message => invalid(s"invalid failure reason: $message"), value => value)
          )
          ensure(replayable == failure.isEmpty, "footer status and reason differ")
          ensure(input.getFilePointer == input.length(), "bytes follow archive footer")
          ensure(
            !replayable || (order.complete && failure.isEmpty),
            "replayable footer describes incomplete exchanges"
          )
          val storedBytes = input.length().refine[Long > 0]
            .fold(message => invalid(s"invalid archive size: $message"), value => value)
          summary = Some(Summary(
            header,
            count,
            frames,
            storedBytes,
            replayable,
            failure
          ))
        case Some((_, _: HeaderRecord)) => invalid("duplicate archive header")
        case None                       => invalid("incomplete archive/footer")
      }
    (events.result(), locations.toMap, summary.getOrElse(invalid("archive footer is absent")))
  }

  private def closeAfterFailure(input: RandomAccessFile, error: Throwable): Nothing =
    Using.resource(input)(_ => throw error)

  def inspect(file: Path, limits: Limits = Limits()): Summary =
    Using.resource(open(file, limits))(_.summary)
}
