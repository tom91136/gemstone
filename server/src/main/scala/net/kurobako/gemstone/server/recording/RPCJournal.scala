package net.kurobako.gemstone.server.recording

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}
import scala.compiletime.{constValue, error}
import net.kurobako.gemstone.CompileTime
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.rpc.ExecutionRPC
import upickle.default.{ReadWriter, macroRW, readBinary, writeBinary}
import upickle.implicits.key
import java.io.{DataOutputStream, IOException, RandomAccessFile}
import java.nio.ByteBuffer
import java.nio.file.{FileAlreadyExistsException, Files, Path}
import java.nio.file.attribute.{PosixFileAttributeView, PosixFilePermissions}
import java.util.concurrent.locks.ReentrantLock
import java.util.zip.CRC32
import scala.collection.mutable
import scala.util.Using
import scala.util.control.NonFatal

/** Bounded MessagePack RPC archives. Client identities are normalized; application payloads remain sensitive data.
  * Record checksums detect accidental corruption; they are not security boundaries.
  */
object RPCJournal {
  private val Format                 = "gemstone-rpc-journal"
  val Version                        = 1
  private val RecordFramingBytes     = 8L
  private val RecordOverheadBytes    = 4096
  private val MetadataRecordLimit    = 4096
  private val FooterReserve          = MetadataRecordLimit.toLong + RecordFramingBytes
  private val FailureReasonMaxLength = 512

  private inline val MaximumFrameBytes   = 2147479551
  private inline val MinimumArchiveBytes = 4104L

  opaque type ProtocolIdentity = String

  object ProtocolIdentity {
    inline def either(value: String): Either[String, ProtocolIdentity] =
      if (value.matches("[!-~]{1,128}")) Right(value)
      else Left("protocol identity must be 1 to 128 printable ASCII characters")

    inline def unsafe(value: String): ProtocolIdentity =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: String & Singleton](inline value: A)(using
        CompileTime.PrintableIdentity[A]
    ): ProtocolIdentity =
      value

    extension (value: ProtocolIdentity) {
      inline def raw: String = value
    }
  }

  opaque type RecordedClientId = Long

  object RecordedClientId {
    inline def either(value: Long): Either[String, RecordedClientId] =
      if (value > 0L) Right(value) else Left(s"client id must be strictly positive: $value")

    inline def unsafe(value: Long): RecordedClientId =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: Long & Singleton](inline value: A): RecordedClientId =
      inline if (constValue[A] > 0L) value else error("client id must be strictly positive")

    extension (id: RecordedClientId) {
      inline def raw: Long       = id
      def next: RecordedClientId = unsafe(Math.addExact(id, 1L))
    }
  }

  opaque type RecordedConnectionId = Long

  object RecordedConnectionId {
    inline def either(value: Long): Either[String, RecordedConnectionId] =
      if (value > 0L) Right(value) else Left(s"connection id must be strictly positive: $value")

    inline def unsafe(value: Long): RecordedConnectionId =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: Long & Singleton](inline value: A): RecordedConnectionId =
      inline if (constValue[A] > 0L) value else error("connection id must be strictly positive")

    extension (id: RecordedConnectionId) {
      inline def raw: Long           = id
      def next: RecordedConnectionId = unsafe(Math.addExact(id, 1L))
    }
  }

  opaque type EventId = Long

  object EventId {
    inline def either(value: Long): Either[String, EventId] =
      if (value >= 0L) Right(value) else Left(s"event id must be positive or zero: $value")

    inline def unsafe(value: Long): EventId =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: Long & Singleton](inline value: A): EventId =
      inline if (constValue[A] >= 0L) value else error("event id must be positive or zero")

    extension (id: EventId) {
      inline def raw: Long = id
      def next: EventId    = unsafe(Math.addExact(id, 1L))
    }
  }

  opaque type FailureReason = String

  object FailureReason {
    inline def either(value: String): Either[String, FailureReason] =
      if (value.isEmpty) Left("failure reason must not be empty")
      else if (value.length > FailureReasonMaxLength) Left(s"failure reason exceeds $FailureReasonMaxLength characters")
      else if (value.view.contains('\u0000')) Left("failure reason must not contain a NUL character")
      else Right(value)

    inline def unsafe(value: String): FailureReason =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: String & Singleton](inline value: A)(using
        CompileTime.BoundedNonEmptyNoNul[A, 512]
    ): FailureReason =
      value

    extension (value: FailureReason) {
      inline def raw: String = value
    }
  }

  opaque type FrameByteSize = Int

  object FrameByteSize {
    inline def either(value: Int): Either[String, FrameByteSize] =
      if (value >= 1 && value <= MaximumFrameBytes) Right(value)
      else Left(s"frame byte size must be between 1 and $MaximumFrameBytes: $value")

    inline def unsafe(value: Int): FrameByteSize =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: Int & Singleton](inline value: A): FrameByteSize =
      inline if (constValue[A] >= 1 && constValue[A] <= MaximumFrameBytes) value
      else error("frame byte size out of range")

    extension (value: FrameByteSize) {
      inline def raw: Int = value
    }
  }

  opaque type ArchiveByteSize = Long

  object ArchiveByteSize {
    inline def either(value: Long): Either[String, ArchiveByteSize] =
      if (value > MinimumArchiveBytes) Right(value)
      else Left(s"archive byte size must exceed $MinimumArchiveBytes: $value")

    inline def unsafe(value: Long): ArchiveByteSize =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: Long & Singleton](inline value: A): ArchiveByteSize =
      inline if (constValue[A] > MinimumArchiveBytes) value else error("archive byte size below minimum")

    extension (value: ArchiveByteSize) {
      inline def raw: Long = value
    }
  }

  case class Limits(
      maxFrameBytes: FrameByteSize = FrameByteSize(134221824),
      maxArchiveBytes: ArchiveByteSize = ArchiveByteSize(8589934592L),
      maxEvents: Positive[Int] = 1000000
  )
  case class ExecutionProfile(
      maxInstructions: Positive[Long],
      quantum: Positive[Int],
      parallelism: Positive[Int]
  )
  case class Header(
      protocolIdentity: ProtocolIdentity,
      limits: Limits = Limits(),
      executionProfile: Option[ExecutionProfile] = None
  )
  enum Kind(val code: Int, val hasFrame: Boolean) {
    case Request         extends Kind(1, true)
    case Response        extends Kind(2, true)
    case ConnectionClose extends Kind(5, false)
    case ClientClose     extends Kind(6, false)
  }
  case class Event(
      sequence: EventId,
      kind: Kind,
      client: RecordedClientId,
      connection: Option[RecordedConnectionId]
  )
  case class Summary(
      header: Header,
      events: NonNegative[Int],
      frames: NonNegative[Int],
      storedBytes: Positive[Long],
      replayable: Boolean,
      reason: Option[FailureReason]
  )

  object Frames {
    def magic(bytes: Array[Byte]): Int                = ByteBuffer.wrap(bytes).getInt()
    def integer(bytes: Array[Byte], offset: Int): Int = ByteBuffer.wrap(bytes, offset, 4).getInt()
    def long(bytes: Array[Byte], offset: Int): Long   = ByteBuffer.wrap(bytes, offset, 8).getLong()

    def normalized(
        kind: Kind,
        bytes: Array[Byte],
        maximum: FrameByteSize
    ): Array[Byte] = {
      validate(kind, bytes, maximum, normalized = false)
      if (kind != Kind.Request) bytes
      else {
        val result = bytes.clone()
        ByteBuffer.wrap(result).putLong(ExecutionRPC.RequestClientOffset.raw, 0L)
        result
      }
    }

    def validate(
        kind: Kind,
        bytes: Array[Byte],
        maximum: FrameByteSize,
        normalized: Boolean
    ): Unit =
      validatePrefix(kind, bytes, bytes.length, maximum, normalized)

    def validatePrefix(
        kind: Kind,
        bytes: Array[Byte],
        frameSize: Int,
        maximum: FrameByteSize,
        normalized: Boolean
    ): Unit = {
      def check(value: Boolean): Unit = if (!value) throw IOException("RPC journal: invalid RPC frame")
      check(bytes.length >= 12 && bytes.length <= frameSize && frameSize <= maximum)
      check(long(bytes, ExecutionRPC.RequestFingerprintOffset.raw) == ExecutionRPC.Schema.fingerprint)
      val (header, payload) = kind match {
        case Kind.Request if magic(bytes) == ExecutionRPC.RequestMagic =>
          check(bytes.length >= ExecutionRPC.RequestHeaderBytes.raw)
          (ExecutionRPC.RequestHeaderBytes.raw, integer(bytes, ExecutionRPC.RequestPayloadSizeOffset.raw))
        case Kind.Response if magic(bytes) == ExecutionRPC.ResponseMagic =>
          check(bytes.length >= ExecutionRPC.ResponseHeaderBytes.raw)
          check(integer(bytes, ExecutionRPC.ResponseStatusOffset.raw) == 0 ||
            integer(bytes, ExecutionRPC.ResponseStatusOffset.raw) == 1)
          (ExecutionRPC.ResponseHeaderBytes.raw, integer(bytes, ExecutionRPC.ResponsePayloadSizeOffset.raw))
        case _ => throw IOException("RPC journal: frame kind/magic differ")
      }
      check(payload >= 0 && header.toLong + payload == frameSize.toLong)
      if (normalized && kind == Kind.Request) {
        check(ByteBuffer.wrap(bytes).getLong(ExecutionRPC.RequestClientOffset.raw) == 0L)
      }
    }
  }

  private sealed trait Record
  @key("header")
  private case class HeaderRecord(
      format: String,
      version: Int,
      protocolIdentity: String,
      maxFrameBytes: Int,
      maxArchiveBytes: Long,
      maxEvents: Int,
      executionProfile: Option[ExecutionProfileRecord]
  ) extends Record
  private case class ExecutionProfileRecord(maxInstructions: Long, quantum: Int, parallelism: Int)
  @key("event")
  private case class EventRecord(
      sequence: Long,
      kind: Int,
      client: Long,
      connection: Long,
      frame: Option[Array[Byte]]
  ) extends Record
  @key("footer")
  private case class FooterRecord(
      events: Int,
      frames: Int,
      replayable: Boolean,
      reason: Option[String]
  ) extends Record
  private object Record {
    given ReadWriter[ExecutionProfileRecord] = macroRW
    given ReadWriter[HeaderRecord]           = macroRW
    given ReadWriter[EventRecord]            = macroRW
    given ReadWriter[FooterRecord]           = macroRW
    given ReadWriter[Record]                 = ReadWriter.merge(
      summon[ReadWriter[HeaderRecord]],
      summon[ReadWriter[EventRecord]],
      summon[ReadWriter[FooterRecord]]
    )
  }

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

  private def restrictToOwner(file: Path): Unit =
    Option(Files.getFileAttributeView(file, classOf[PosixFileAttributeView])).foreach { view =>
      view.setPermissions(PosixFilePermissions.fromString("rw-------"))
    }

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
    @SuppressWarnings(Array("scalafix:GemstoneNoExplicitNew"))
    def apply(file: Path, header: Header): Writer = {
      Option(file.getParent).foreach(Files.createDirectories(_))
      try Files.createFile(file)
      catch { case _: FileAlreadyExistsException => invalid(s"capture already exists: ${file.getFileName}") }
      // Ownership transfers to Writer on success, so a lexical Using scope cannot close this stream here.
      val output = try {
        restrictToOwner(file)
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
  private final class Order {
    private val connections                            = mutable.Map.empty[RecordedConnectionId, ConnectionState]
    private val closedClients                          = mutable.Set.empty[RecordedClientId]
    def accept(event: Event, frame: Array[Byte]): Unit =
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
            ensure(
              RPCJournal.Frames.magic(frame) == ExecutionRPC.ResponseMagic,
              "response protocol differs from its request"
            )
            previous.copy(requestPending = false)
          case Kind.ConnectionClose => previous.copy(closed = true)
          case Kind.ClientClose     => previous
        }
        connections.update(connection, next)
      }
    def complete: Boolean = connections.values.view.forall(state => state.closed && !state.requestPending)
  }

  // Archive state advances monotonically behind Writer's lock.
  final class Writer private (val header: Header, output: DataOutputStream) extends AutoCloseable {
    private val lock = ReentrantLock()
    // Recording owners are capabilities: equal objects must remain distinct clients. Scala's AnyRefMap uses
    // value equality, so IdentityHashMap is intentional here.
    private val clients = java.util.IdentityHashMap[AnyRef, java.lang.Long]()
    private val order   = Order()
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var nextClient = RecordedClientId(1L)
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var nextConnection = RecordedConnectionId(1L)
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var sequence = EventId(0L)
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var frames = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var written = 0L
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var closed = false
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var failure = Option.empty[FailureReason]

    private val encodedHeader = encode(HeaderRecord(
      Format,
      Version,
      header.protocolIdentity,
      header.limits.maxFrameBytes,
      header.limits.maxArchiveBytes,
      header.limits.maxEvents.raw,
      header.executionProfile.map(profile =>
        ExecutionProfileRecord(profile.maxInstructions.raw, profile.quantum.raw, profile.parallelism.raw)
      )
    ))
    ensure(encodedHeader.length <= MetadataRecordLimit, "archive header exceeds its byte limit")
    written = Math.addExact(written, writeRecord(output, encodedHeader))

    private def clientId(client: AnyRef): RecordedClientId = Option(clients.get(client)) match {
      case Some(found) => RecordedClientId.unsafe(found.longValue())
      case None        =>
        val id = nextClient
        nextClient = RecordedClientId.unsafe(Math.addExact(nextClient, 1L))
        clients.put(client, id)
        id
    }
    def connection(client: AnyRef): Connection = lock.withLock {
      ensure(!closed, "capture is closed")
      val id = nextConnection
      nextConnection = RecordedConnectionId.unsafe(Math.addExact(nextConnection, 1L))
      Connection(this, clientId(client), id)
    }
    def clientClosed(client: AnyRef): Unit = lock.withLock {
      Option(clients.get(client)).foreach(found =>
        append(
          Kind.ClientClose,
          RecordedClientId.unsafe(found.longValue()),
          None,
          Array.emptyByteArray
        )
      )
    }
    def markUnreplayable(reason: String): Unit = lock.withLock {
      if (failure.isEmpty) {
        val sanitized = reason.filterNot(_ == '\u0000').take(FailureReasonMaxLength)
        val detail    = Option.when(sanitized.nonEmpty)(sanitized).getOrElse("unspecified recording failure")
        failure = Some(FailureReason.either(detail)
          .fold(message => invalid(s"invalid failure reason: $message"), value => value))
      }
    }
    private[RPCJournal] def append(
        kind: Kind,
        client: RecordedClientId,
        connection: Option[RecordedConnectionId],
        bytes: Array[Byte]
    ): Unit =
      lock.withLock {
        ensure(!closed, "capture is closed")
        try {
          ensure(sequence < header.limits.maxEvents.raw, "event limit exceeded")
          val frame = Option.when(kind.hasFrame)(RPCJournal.Frames.normalized(kind, bytes, header.limits.maxFrameBytes))
          val eventId = sequence
          val record  = encode(EventRecord(
            eventId,
            kind.code,
            client,
            connection.getOrElse(0L),
            frame
          ))
          val encodedBytes = record.length.toLong + RecordFramingBytes
          ensure(
            encodedBytes <= header.limits.maxArchiveBytes - written - FooterReserve,
            "archive byte limit exceeded"
          )
          val event = Event(eventId, kind, client, connection)
          order.accept(event, frame.getOrElse(Array.emptyByteArray))
          written = Math.addExact(written, writeRecord(output, record))
          if (kind.hasFrame) frames += 1
          sequence = EventId.unsafe(Math.addExact(sequence, 1L))
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
          val footer = encode(FooterRecord(sequence.toInt, frames, failure.isEmpty, failure))
          ensure(footer.length <= MetadataRecordLimit, "archive footer exceeds its byte limit")
          ensure(
            footer.length.toLong + RecordFramingBytes <= header.limits.maxArchiveBytes - written,
            "archive byte limit exceeded by footer"
          )
          written = Math.addExact(written, writeRecord(output, footer))
          output.flush()
        }
      }
    }
  }

  // A connection records its two-step request/close lifecycle in place.
  final class Connection private[RPCJournal] (
      writer: Writer,
      val client: RecordedClientId,
      val id: RecordedConnectionId
  ) extends AutoCloseable {
    private val lock = ReentrantLock()
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var started = false
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var closed                    = false
    def request(frame: Array[Byte]): Unit =
      lock.withLock { writer.append(Kind.Request, client, Some(id), frame); started = true }
    def response(frame: Array[Byte]): Unit = writer.append(Kind.Response, client, Some(id), frame)
    override def close(): Unit             = lock.withLock {
      if (!closed) {
        closed = true
        if (started) writer.append(Kind.ConnectionClose, client, Some(id), Array.emptyByteArray)
      }
    }
  }

  final class Reader private[RPCJournal] (
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
        summary.header.limits.maxFrameBytes + RecordOverheadBytes
      ) match {
        case Some((_, EventRecord(sequence, kind, client, connection, Some(frame)))) =>
          ensure(
            sequence == event.sequence && kind == event.kind.code && client == event.client &&
              connection == event.connection.getOrElse(0L),
            "event changed after archive verification"
          )
          RPCJournal.Frames.validate(event.kind, frame, summary.header.limits.maxFrameBytes, normalized = true)
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
      ensure(input.length() <= limits.maxArchiveBytes, "archive exceeds reader byte limit")
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
    val headerRecord = readRecord(input, MetadataRecordLimit) match {
      case Some((_, value: HeaderRecord)) => value
      case _                              => invalid("archive does not start with a header")
    }
    ensure(headerRecord.format == Format && headerRecord.version == Version, "unknown archive format/version")
    val maxFrameBytes = FrameByteSize.either(headerRecord.maxFrameBytes)
      .fold(message => invalid(s"invalid frame byte limit: $message"), value => value)
    val maxArchiveBytes = ArchiveByteSize.either(headerRecord.maxArchiveBytes)
      .fold(message => invalid(s"invalid archive byte limit: $message"), value => value)
    val maxEvents = Positive.either(headerRecord.maxEvents)
      .fold(message => invalid(s"invalid event limit: $message"), value => value)
    val declared = Limits(maxFrameBytes, maxArchiveBytes, maxEvents)
    ensure(
      declared.maxFrameBytes <= limits.maxFrameBytes &&
        declared.maxArchiveBytes <= limits.maxArchiveBytes &&
        declared.maxEvents <= limits.maxEvents,
      "archive limits exceed reader limits"
    )
    ensure(input.length() <= declared.maxArchiveBytes, "archive exceeds its declared byte limit")
    val profile = headerRecord.executionProfile.map { profile =>
      val maxInstructions = Positive.either(profile.maxInstructions)
        .fold(message => invalid(s"invalid maximum instruction count: $message"), value => value)
      val quantum = Positive.either(profile.quantum)
        .fold(message => invalid(s"invalid execution quantum: $message"), value => value)
      val parallelism = Positive.either(profile.parallelism)
        .fold(message => invalid(s"invalid execution parallelism: $message"), value => value)
      ExecutionProfile(maxInstructions, quantum, parallelism)
    }
    val protocolIdentity = ProtocolIdentity.either(headerRecord.protocolIdentity)
      .fold(message => invalid(s"invalid protocol identity: $message"), value => value)
    Header(protocolIdentity, declared, profile) -> declared
  }

  private def readEvents(
      input: RandomAccessFile,
      header: Header,
      declared: Limits
  ): (Vec[Event], Map[EventId, RecordLocation], Summary) = {
    val events      = Vec.newBuilder[Event]
    val locations   = mutable.Map.empty[EventId, RecordLocation]
    val order       = Order()
    val recordLimit = declared.maxFrameBytes + RecordOverheadBytes
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var count = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var frames = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var summary = Option.empty[Summary]
    while (summary.isEmpty)
      readRecord(input, math.max(recordLimit, MetadataRecordLimit)) match {
        case Some((location, EventRecord(sequence, code, client, connection, frame))) =>
          ensure(count < declared.maxEvents.raw, "too many events")
          val kind = Kind.values.find(_.code == code).getOrElse(invalid("unknown event kind"))
          ensure(sequence == count.toLong, "event sequence is not contiguous")
          ensure(frame.nonEmpty == kind.hasFrame, "event frame does not match its kind")
          frame.foreach(bytes => RPCJournal.Frames.validate(kind, bytes, declared.maxFrameBytes, normalized = true))
          val eventId = EventId.either(sequence)
            .fold(message => invalid(s"invalid event identity: $message"), value => value)
          val clientId = RecordedClientId.either(client)
            .fold(message => invalid(s"invalid client identity: $message"), value => value)
          val connectionId = if (kind == Kind.ClientClose) {
            ensure(connection == 0L, "client close has a connection identity")
            None
          } else Some(RecordedConnectionId.either(connection)
            .fold(message => invalid(s"invalid connection identity: $message"), value => value))
          val event = Event(eventId, kind, clientId, connectionId)
          order.accept(event, frame.getOrElse(Array.emptyByteArray))
          if (kind.hasFrame) { frames += 1; locations.update(eventId, location) }
          events += event
          count += 1
        case Some((location, FooterRecord(expectedEvents, expectedFrames, replayable, reason))) =>
          ensure(location.payloadBytes <= MetadataRecordLimit, "archive footer exceeds its byte limit")
          ensure(expectedEvents == count && expectedFrames == frames, "footer totals differ")
          val failure: Option[FailureReason] = reason.map(raw =>
            FailureReason.either(raw)
              .fold(message => invalid(s"invalid failure reason: $message"), value => value)
          )
          ensure(replayable == failure.isEmpty, "footer status and reason differ")
          ensure(input.getFilePointer == input.length(), "bytes follow archive footer")
          ensure(
            !replayable || (order.complete && failure.isEmpty),
            "replayable footer describes incomplete exchanges"
          )
          val storedBytes = Positive.either(input.length())
            .fold(message => invalid(s"invalid archive size: $message"), value => value)
          summary = Some(Summary(
            header,
            NonNegative.unsafe(count),
            NonNegative.unsafe(frames),
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
