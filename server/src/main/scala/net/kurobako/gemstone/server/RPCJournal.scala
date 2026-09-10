package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.generated.GeneratedCUDARPC
import java.io.{DataOutputStream, File, FileInputStream, FileOutputStream, IOException, RandomAccessFile}
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermissions
import scala.collection.mutable // scalafix:ok GemstoneRuntimeCollections.mutable-collection; bounded archive and connection indexes

/** Versioned, bounded RPC archives. Protocol credentials are removed; application payloads remain sensitive data.
  * Integrity hashes detect corruption; they are not authenticated capture signatures.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneRuntimeCollections.mutable-collection"))
object RPCJournal {
  private val Magic         = 0x47454d4a4e4c3031L // GEMJNL01
  val Version               = 2
  private val FooterReserve = 2048L
  final case class Limits(
      maxFrameBytes: Int = 128 * 1024 * 1024 + 4096,
      maxArchiveBytes: Long = 8L * 1024 * 1024 * 1024,
      maxEvents: Int = 1000000,
      maxBlobs: Int = 100000
  ) {
    require(maxFrameBytes > 0 && maxArchiveBytes > FooterReserve && maxEvents > 0 && maxBlobs > 0)
  }
  final case class ExecutionProfile(maxInstructions: Long, quantum: Int, parallelism: Int) {
    require(maxInstructions > 0L && quantum > 0 && parallelism > 0, "execution profile values must be positive")
  }
  final case class Header(
      schemaFingerprint: String,
      limits: Limits = Limits(),
      executionProfile: Option[ExecutionProfile] = None
  ) {
    require(schemaFingerprint.length == 64 &&
      schemaFingerprint.forall(c => (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')))
  }
  enum Kind(val code: Int, val hasFrame: Boolean) {
    case Request         extends Kind(1, true)
    case Response        extends Kind(2, true)
    case CallbackRequest extends Kind(3, true)
    case CallbackReply   extends Kind(4, true)
    case ConnectionClose extends Kind(5, false)
    case ClientClose     extends Kind(6, false)
  }
  final case class Event(sequence: Long, kind: Kind, client: Long, connection: Long, blob: Option[String])
  final case class Summary(
      header: Header,
      events: Int,
      blobs: Int,
      storedBytes: Long,
      replayable: Boolean,
      reason: String
  )
  private final class Blob(val offset: Long, val size: Int, val prefix: Array[Byte]) {
    var validatedKind: Option[Kind] = None
  }
  private final case class ConnectionState(
      client: Long,
      pending: Int = 0,
      callback: Boolean = false,
      closed: Boolean = false
  )

  private def invalid(message: String): Nothing                 = throw IOException(s"RPC journal: $message")
  private def ensure(condition: Boolean, message: String): Unit = if (!condition) invalid(message)
  private[server] def hex(bytes: Array[Byte]): String           = {
    val digits = "0123456789abcdef"
    val chars  = Array.ofDim[Char](bytes.length * 2)
    var i      = 0
    while (i < bytes.length) {
      chars(i * 2) = digits.charAt((bytes(i) & 0xff) >>> 4)
      chars(i * 2 + 1) = digits.charAt(bytes(i) & 15)
      i += 1
    }
    String(chars)
  }
  private def hash(bytes: Array[Byte]): Array[Byte]            = JournalSHA256.digest(bytes)
  private def digestFile(file: File, count: Long): Array[Byte] = {
    val digest = JournalSHA256()
    val input  = FileInputStream(file)
    try {
      val buffer    = Array.ofDim[Byte](8192)
      var remaining = count
      while (remaining > 0L) {
        val size = input.read(buffer, 0, math.min(remaining, buffer.length.toLong).toInt)
        ensure(size > 0, "truncated archive while hashing")
        digest.update(buffer, 0, size)
        remaining -= size
      }
      digest.digest()
    } finally input.close()
  }

  /** Tracks protocol exchange shape, not backend thread scheduling or instruction-level execution. */
  private final class Order {
    private val connections                            = mutable.Map.empty[Long, ConnectionState]
    private val closedClients                          = mutable.Set.empty[Long]
    def accept(event: Event, frame: Array[Byte]): Unit = {
      ensure(event.client > 0 && (event.kind == Kind.ClientClose || event.connection > 0), "invalid surrogate identity")
      if (event.kind == Kind.ClientClose) {
        ensure(event.connection == 0 && !closedClients(event.client), "duplicate or malformed client close")
        closedClients += event.client
      } else {
        val previous = connections.getOrElse(event.connection, ConnectionState(event.client))
        ensure(previous.client == event.client && !previous.closed, "connection changed client or was already closed")
        val next = event.kind match {
          case Kind.Request =>
            ensure(
              !closedClients(event.client) && previous.pending == 0,
              "request after client close or while another request is pending"
            )
            previous.copy(pending = RPCFrames.magic(frame))
          case Kind.Response =>
            ensure(previous.pending != 0 && !previous.callback, "response has no request or callback reply")
            val expected = if (previous.pending == RPCFrames.ExecutionRequest) RPCFrames.ExecutionResponse
            else RPCFrames.CUDAResponse
            ensure(RPCFrames.magic(frame) == expected, "response protocol differs from its request")
            previous.copy(pending = 0)
          case Kind.CallbackRequest =>
            ensure(
              previous.pending == RPCFrames.CUDARequest && !previous.callback,
              "callback has no pending CUDA request"
            )
            previous.copy(callback = true)
          case Kind.CallbackReply =>
            ensure(previous.callback, "callback reply has no callback request")
            previous.copy(callback = false)
          case Kind.ConnectionClose => previous.copy(closed = true)
          case Kind.ClientClose     => previous
        }
        connections.update(event.connection, next)
      }
    }
    def complete: Boolean = connections.valuesIterator.forall(s => s.closed && s.pending == 0 && !s.callback)
  }

  private final class HashingOutput(output: java.io.OutputStream, digest: JournalSHA256) extends java.io.OutputStream {
    override def write(value: Int): Unit = { output.write(value); digest.updateByte(value) }
    override def write(bytes: Array[Byte], offset: Int, count: Int): Unit = {
      output.write(bytes, offset, count); digest.update(bytes, offset, count)
    }
    override def flush(): Unit = output.flush()
  }

  final class Writer(file: File, val header: Header) extends AutoCloseable {
    Option(file.getParentFile).foreach(parent => Files.createDirectories(parent.toPath))
    ensure(file.createNewFile(), s"capture already exists: ${file.getName}")
    try Files.setPosixFilePermissions(file.toPath, PosixFilePermissions.fromString("rw-------"))
    catch { case _: UnsupportedOperationException => () }
    private val raw            = FileOutputStream(file)
    private val digest         = JournalSHA256()
    private val output         = DataOutputStream(HashingOutput(raw, digest))
    private val clients        = java.util.IdentityHashMap[AnyRef, java.lang.Long]()
    private val blobs          = mutable.Set.empty[String]
    private val order          = Order()
    private var nextClient     = 1L
    private var nextConnection = 1L
    private var sequence       = 0L
    private var written        = 0L
    private var closed         = false
    private var failure        = ""
    output.writeLong(Magic); output.writeInt(Version)
    output.writeUTF(header.schemaFingerprint)
    output.writeInt(header.limits.maxFrameBytes); output.writeLong(header.limits.maxArchiveBytes)
    output.writeInt(header.limits.maxEvents); output.writeInt(header.limits.maxBlobs)
    output.writeBoolean(header.executionProfile.nonEmpty)
    header.executionProfile.foreach { profile =>
      output.writeLong(profile.maxInstructions); output.writeInt(profile.quantum); output.writeInt(profile.parallelism)
    }
    written = 8 + 4 + 2 + 64 + 4 + 8 + 4 + 4 + 1 + (if (header.executionProfile.nonEmpty) 16 else 0)

    private def clientId(client: AnyRef): Long = {
      val found = clients.get(client)
      if (found != null) found.longValue()
      else { val id = nextClient; nextClient += 1; clients.put(client, id); id }
    }
    def connection(client: AnyRef): Connection = synchronized {
      ensure(!closed, "capture is closed")
      val id = nextConnection; nextConnection += 1
      Connection(this, clientId(client), id)
    }
    def clientClosed(client: AnyRef): Unit = synchronized {
      val found = clients.get(client)
      if (found != null) append(Kind.ClientClose, found.longValue(), 0, Array.emptyByteArray)
    }
    def markUnreplayable(reason: String): Unit = synchronized {
      if (failure.isEmpty) failure = reason.take(512)
    }
    private[RPCJournal] def append(kind: Kind, client: Long, connection: Long, bytes: Array[Byte]): Unit =
      synchronized {
        ensure(!closed, "capture is closed")
        try {
          ensure(sequence < header.limits.maxEvents, "event limit exceeded")
          val frame    = if (kind.hasFrame) RPCFrames.normalized(kind, bytes, header.limits.maxFrameBytes) else bytes
          val keyBytes = if (kind.hasFrame) hash(frame) else Array.emptyByteArray
          val key      = if (kind.hasFrame) Some(hex(keyBytes)) else None
          val isNew    = key.exists(k => !blobs(k))
          ensure(!isNew || blobs.size < header.limits.maxBlobs, "blob limit exceeded")
          val size = 1L + 8 + 1 + 8 + 8 + (if (kind.hasFrame) 32 else 0) +
            (if (isNew) 1L + 32 + 4 + frame.length else 0L)
          ensure(size <= header.limits.maxArchiveBytes - written - FooterReserve, "archive byte limit exceeded")
          val event = Event(sequence, kind, client, connection, key)
          order.accept(event, frame)
          if (isNew) {
            output.writeByte(1); output.write(keyBytes); output.writeInt(frame.length); output.write(frame)
            blobs += key.get
          }
          output.writeByte(2); output.writeLong(sequence); output.writeByte(kind.code)
          output.writeLong(client); output.writeLong(connection)
          if (kind.hasFrame) output.write(keyBytes)
          written += size; sequence += 1
        } catch {
          case error: Exception =>
            markUnreplayable("capture failed before all events could be written")
            throw error
        }
      }
    override def close(): Unit = synchronized {
      if (!closed) {
        closed = true
        if (!order.complete && failure.isEmpty) failure = "capture ended with an open or incomplete exchange"
        try {
          output.writeByte(3); output.writeLong(sequence); output.writeInt(blobs.size)
          output.writeBoolean(failure.isEmpty); output.writeUTF(failure); output.flush()
          val checksum = digest.digest()
          raw.write(checksum); raw.flush()
        } finally raw.close()
      }
    }
  }

  final class Connection private[RPCJournal] (writer: Writer, val client: Long, val id: Long) extends AutoCloseable {
    private var started                   = false
    private var closed                    = false
    def request(frame: Array[Byte]): Unit =
      synchronized { writer.append(Kind.Request, client, id, frame); started = true }
    def response(frame: Array[Byte]): Unit        = writer.append(Kind.Response, client, id, frame)
    def callbackRequest(frame: Array[Byte]): Unit = writer.append(Kind.CallbackRequest, client, id, frame)
    def callbackReply(frame: Array[Byte]): Unit   = writer.append(Kind.CallbackReply, client, id, frame)
    override def close(): Unit                    = synchronized {
      if (!closed) { closed = true; if (started) writer.append(Kind.ConnectionClose, client, id, Array.emptyByteArray) }
    }
  }

  final class Reader private[RPCJournal] (
      file: File,
      val summary: Summary,
      val events: Col[Event],
      blobs: Map[String, Blob]
  ) extends AutoCloseable {
    private val input                    = RandomAccessFile(file, "r")
    def frame(event: Event): Array[Byte] = synchronized {
      val key   = event.blob.getOrElse(invalid("event has no frame"))
      val blob  = blobs.getOrElse(key, invalid("missing blob"))
      val bytes = Array.ofDim[Byte](blob.size)
      input.seek(blob.offset); input.readFully(bytes)
      ensure(hex(hash(bytes)) == key, "blob changed after archive verification")
      bytes
    }
    override def close(): Unit = input.close()
  }

  def open(file: File, limits: Limits = Limits()): Reader = {
    ensure(file.length() <= limits.maxArchiveBytes, "archive exceeds reader byte limit")
    val input = RandomAccessFile(file, "r")
    try {
      ensure(input.readLong() == Magic && input.readInt() == Version, "unknown archive format/version")
      val fingerprint = input.readUTF()
      val declared    = Limits(input.readInt(), input.readLong(), input.readInt(), input.readInt())
      ensure(
        declared.maxFrameBytes <= limits.maxFrameBytes && declared.maxArchiveBytes <= limits.maxArchiveBytes &&
          declared.maxEvents <= limits.maxEvents && declared.maxBlobs <= limits.maxBlobs,
        "archive limits exceed reader limits"
      )
      ensure(file.length() <= declared.maxArchiveBytes, "archive exceeds its declared byte limit")
      val profile =
        if (input.readBoolean()) Some(ExecutionProfile(input.readLong(), input.readInt(), input.readInt())) else None
      val header     = Header(fingerprint, declared, profile)
      val events     = Col.newBuilder[Event]
      val blobs      = mutable.Map.empty[String, Blob]
      val order      = Order()
      var count      = 0
      var footer     = false
      var replayable = false
      var reason     = ""
      while (!footer)
        input.readUnsignedByte() match {
          case 1 =>
            ensure(blobs.size < declared.maxBlobs, "too many blobs")
            val keyBytes = Array.ofDim[Byte](32); input.readFully(keyBytes)
            val key      = hex(keyBytes); val size = input.readInt(); val offset = input.getFilePointer
            ensure(
              size >= 0 && size <= declared.maxFrameBytes && size.toLong <= file.length() - offset,
              "invalid blob size"
            )
            ensure(!blobs.contains(key), "duplicate blob definition")
            val digest    = JournalSHA256(); val chunk = Array.ofDim[Byte](8192)
            val prefix    = Array.ofDim[Byte](math.min(size, RPCFrames.MaximumHeaderBytes))
            var remaining = size
            while (remaining > 0) {
              val n = math.min(remaining, chunk.length); input.readFully(chunk, 0, n)
              if (remaining == size) System.arraycopy(chunk, 0, prefix, 0, prefix.length)
              digest.update(chunk, 0, n)
              remaining -= n
            }
            ensure(java.util.Arrays.equals(digest.digest(), keyBytes), "corrupt blob digest")
            blobs.update(key, Blob(offset, size, prefix))
          case 2 =>
            ensure(count < declared.maxEvents, "too many events")
            val sequence = input.readLong()
            val code     = input.readUnsignedByte()
            val kind     = Kind.values.find(_.code == code).getOrElse(invalid("unknown event kind"))
            val client   = input.readLong(); val connection = input.readLong()
            val key = if (kind.hasFrame) { val bytes = Array.ofDim[Byte](32); input.readFully(bytes); Some(hex(bytes)) }
            else None
            ensure(sequence == count.toLong, "event sequence is not contiguous")
            val event = Event(sequence, kind, client, connection, key)
            val frame = key.fold(Array.emptyByteArray) { value =>
              val blob = blobs.getOrElse(value, invalid("event refers to an undefined blob"))
              blob.validatedKind match {
                case Some(previous) => ensure(previous == kind, "blob is used with different frame kinds")
                case None           =>
                  RPCFrames.validatePrefix(kind, blob.prefix, blob.size, declared.maxFrameBytes, redacted = true)
                  blob.validatedKind = Some(kind)
              }
              blob.prefix
            }
            order.accept(event, frame)
            events += event; count += 1
          case 3 =>
            ensure(input.readLong() == count.toLong && input.readInt() == blobs.size, "footer totals differ")
            replayable = input.readBoolean(); reason = input.readUTF()
            val checksumOffset = input.getFilePointer
            val expected       = Array.ofDim[Byte](32); input.readFully(expected)
            ensure(input.getFilePointer == file.length(), "bytes follow archive footer")
            ensure(java.util.Arrays.equals(expected, digestFile(file, checksumOffset)), "archive checksum differs")
            ensure(
              !replayable || (order.complete && reason.isEmpty),
              "replayable footer describes incomplete exchanges"
            )
            footer = true
          case _ => invalid("unknown archive record")
        }
      Reader(file, Summary(header, count, blobs.size, file.length(), replayable, reason), events.result(), blobs.toMap)
    } catch {
      case error: java.io.EOFException     => throw IOException("RPC journal: incomplete archive/footer", error)
      case error: IllegalArgumentException => throw IOException("RPC journal: invalid archive header", error)
    } finally input.close()
  }
  def inspect(file: File, limits: Limits = Limits()): Summary = {
    val reader = open(file, limits)
    try reader.summary
    finally reader.close()
  }
}

/** Framing only: API value codecs remain owned by the protocol implementation/generated schema. */
private[server] object RPCFrames {
  val CUDARequest                                   = 0x474d5331
  val CUDAResponse                                  = 0x474d5231
  val ExecutionRequest                              = 0x474d4531
  val ExecutionResponse                             = 0x474d4631
  val CallbackRequest                               = 0x474d4331
  val CallbackReply                                 = 0x474d4131
  def magic(bytes: Array[Byte]): Int                = ByteBuffer.wrap(bytes).getInt()
  def integer(bytes: Array[Byte], offset: Int): Int = ByteBuffer.wrap(bytes, offset, 4).getInt()
  def tokenOffset(bytes: Array[Byte]): Int          = if (magic(bytes) == CUDARequest) 72 else 8
  def normalized(kind: RPCJournal.Kind, bytes: Array[Byte], maximum: Int): Array[Byte] = {
    validate(kind, bytes, maximum, redacted = false)
    if (kind != RPCJournal.Kind.Request) bytes
    else {
      val result = bytes.clone(); val offset = tokenOffset(result)
      java.util.Arrays.fill(result, offset, offset + 64, 0.toByte); result
    }
  }
  val MaximumHeaderBytes                                                                         = 184
  def validate(kind: RPCJournal.Kind, bytes: Array[Byte], maximum: Int, redacted: Boolean): Unit =
    validatePrefix(kind, bytes, bytes.length, maximum, redacted)

  private[server] def validatePrefix(
      kind: RPCJournal.Kind,
      bytes: Array[Byte],
      frameSize: Int,
      maximum: Int,
      redacted: Boolean
  ): Unit = {
    def check(value: Boolean): Unit = if (!value) throw IOException("RPC journal: invalid RPC frame")
    check(bytes.length >= 8 && bytes.length <= frameSize && frameSize <= maximum)
    val version = magic(bytes) match {
      case CUDARequest | CUDAResponse | CallbackRequest | CallbackReply => GeneratedCUDARPC.ProtocolVersion
      case ExecutionRequest | ExecutionResponse                         => 3
      case _                                                            => -1
    }
    check(integer(bytes, 4) == version)
    val (header, first, second) = kind match {
      case RPCJournal.Kind.Request if magic(bytes) == CUDARequest =>
        check(bytes.length >= 184); (184, integer(bytes, 176), integer(bytes, 180))
      case RPCJournal.Kind.Request if magic(bytes) == ExecutionRequest =>
        check(bytes.length >= 96); (96, 0, integer(bytes, 92))
      case RPCJournal.Kind.CallbackRequest if magic(bytes) == CallbackRequest =>
        check(bytes.length >= 24); (24, 0, integer(bytes, 20))
      case RPCJournal.Kind.Response if magic(bytes) == CUDAResponse || magic(bytes) == ExecutionResponse =>
        check(bytes.length >= 20); (20, integer(bytes, 12), integer(bytes, 16))
      case RPCJournal.Kind.CallbackReply if magic(bytes) == CallbackReply =>
        check(bytes.length >= 20); (20, integer(bytes, 12), integer(bytes, 16))
      case _ => throw IOException("RPC journal: frame kind/magic differ")
    }
    check(first >= 0 && second >= 0 && header.toLong + first + second == frameSize.toLong)
    if (redacted && kind == RPCJournal.Kind.Request) {
      val offset = tokenOffset(bytes)
      check(bytes.iterator.slice(offset, offset + 64).forall(_ == 0))
    }
  }
}
