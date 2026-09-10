package net.kurobako.gemstone.server

import java.io.{DataInputStream, DataOutputStream, File, IOException, RandomAccessFile}
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.Files

private[server] object JournalFixtures {
  val schema     = "a" * 64
  val credential = "b" * 64
  def execution(operation: Int = 0, payload: Array[Byte] = Array.emptyByteArray, session: Long = 42): Array[Byte] =
    ByteBuffer.allocate(96 + payload.length).putInt(RPCFrames.ExecutionRequest).putInt(3)
      .put(credential.getBytes(StandardCharsets.US_ASCII)).putLong(session).putLong(17)
      .putInt(operation).putInt(payload.length).put(payload).array()
  def response(
      value: Int = 0,
      payload: Array[Byte] = Array.emptyByteArray,
      magic: Int = RPCFrames.ExecutionResponse
  ): Array[Byte] =
    ByteBuffer.allocate(
      20 + payload.length
    ).putInt(magic).putInt(if magic == RPCFrames.ExecutionResponse then 3 else 4)
      .putInt(value).putInt(0).putInt(payload.length).put(payload).array()
  def query: Array[Byte]                  = execution(8, ByteBuffer.allocate(8).putLong(1).array())
  def completion(value: Int): Array[Byte] = response(payload = ByteBuffer.allocate(4).putInt(value).array())
  def cuda: Array[Byte]                   = {
    val name = "callback".getBytes(StandardCharsets.US_ASCII)
    ByteBuffer.allocate(184 + name.length).putInt(RPCFrames.CUDARequest).putInt(4)
      .put(schema.getBytes(StandardCharsets.US_ASCII)).put(credential.getBytes(StandardCharsets.US_ASCII))
      .putLong(42).putLong(17).putLong(0).putLong(11).putLong(12).putInt(name.length).putInt(0).put(name).array()
  }
  def callback: Array[Byte] =
    ByteBuffer.allocate(24).putInt(RPCFrames.CallbackRequest).putInt(4).putLong(123).putInt(1).putInt(0).array()
  def callbackReply: Array[Byte]       = response(magic = RPCFrames.CallbackReply)
  def temporary[A](body: File => A): A = {
    val directory = Files.createTempDirectory("gemstone-rpc-journal-").toFile
    val file      = File(directory, "recording.gmj")
    try body(file)
    finally { Option(directory.listFiles()).foreach(_.foreach(_.delete())); val _ = directory.delete() }
  }
  def capture(file: File)(body: RPCJournal.Writer => Unit): Unit = {
    val writer = RPCJournal.Writer(file, RPCJournal.Header(schema))
    try body(writer)
    finally writer.close()
  }
}

final class RPCJournalSuite extends munit.FunSuite {
  import JournalFixtures.*
  test("archive preserves complete bytes, deduplicates blobs and redacts both credential headers") {
    temporary { file =>
      val owner = Object()
      capture(file) { writer =>
        val generic = writer.connection(owner)
        for (_ <- 0 until 2) { generic.request(execution()); generic.response(response()) }
        generic.close()
        val cudaConnection = writer.connection(owner)
        cudaConnection.request(cuda)
        cudaConnection.callbackRequest(callback)
        cudaConnection.callbackReply(callbackReply)
        cudaConnection.response(response(magic = RPCFrames.CUDAResponse))
        cudaConnection.close(); writer.clientClosed(owner)
      }
      val bytes = Files.readAllBytes(file.toPath)
      assert(!String(bytes, StandardCharsets.ISO_8859_1).contains(credential))
      val reader = RPCJournal.open(file)
      try {
        assert(reader.summary.replayable)
        assertEquals(reader.events.size, 11)
        assertEquals(reader.summary.blobs, 6)
        assertEquals(reader.events.filter(_.kind == RPCJournal.Kind.Request).map(_.client).toSet, Set(1L))
        val first = reader.frame(reader.events.head)
        assertEquals(first.toSeq, RPCFrames.normalized(RPCJournal.Kind.Request, execution(), 4096).toSeq)
        first(0) = 0
        assertEquals(RPCFrames.magic(reader.frame(reader.events.head)), RPCFrames.ExecutionRequest)
      } finally reader.close()
      val _ = intercept[IOException](RPCJournal.Writer(file, RPCJournal.Header(schema)))
    }
  }
  test("execution profile survives the archive and reaches the fresh endpoint factory") {
    temporary { file =>
      val profile = RPCJournal.ExecutionProfile(2000000000L, 8192, 8)
      val writer  = RPCJournal.Writer(file, RPCJournal.Header(schema, executionProfile = Some(profile)))
      writer.close()
      var observed = Option.empty[RPCJournal.ExecutionProfile]
      var closed   = false
      assertEquals(
        RPCReplay.run(
          RPCReplay.Options(file, schema),
          header => {
            observed = header.executionProfile; RPCReplay.Endpoint(1, schema, () => closed = true)
          }
        ).events,
        0
      )
      assertEquals(observed, Some(profile)); assert(closed)
    }
    val _ = intercept[IllegalArgumentException](RPCJournal.ExecutionProfile(0L, 1, 1))
    val _ = intercept[IllegalArgumentException](RPCJournal.ExecutionProfile(1L, 0, 1))
    val _ = intercept[IllegalArgumentException](RPCJournal.ExecutionProfile(1L, 1, 0))
  }

  test("truncated and corrupt archives fail instead of replaying a prefix") {
    temporary { file =>
      capture(file) { w =>
        val c = w.connection(Object()); c.request(execution()); c.response(response()); c.close()
      }
      val original = Files.readAllBytes(file.toPath)
      val broken   = RandomAccessFile(file, "rw")
      try broken.setLength(original.length - 1L)
      finally broken.close()
      val _ = intercept[IOException](RPCJournal.open(file))
      Files.write(file.toPath, original)
      val footer = RandomAccessFile(file, "rw")
      try { footer.seek(original.length - 35L); footer.writeBoolean(false) }
      finally footer.close()
      val _ = intercept[IOException](RPCJournal.open(file))
      Files.write(file.toPath, original)
      val corrupt = RandomAccessFile(file, "rw")
      try { corrupt.seek(140); val value = corrupt.readByte(); corrupt.seek(140); corrupt.writeByte(value ^ 1) }
      finally corrupt.close()
      val _ = intercept[IOException](RPCJournal.open(file))
    }
  }
  test("unfinished exchanges and capture limits are explicitly unreplayable") {
    temporary { file =>
      capture(file) { w =>
        val c = w.connection(Object()); c.request(execution()); c.close()
      }
      val summary = RPCJournal.inspect(file)
      assert(!summary.replayable)
      assert(summary.reason.contains("incomplete"))
      var started = false
      val _       = intercept[IOException](RPCReplay.open(
        RPCReplay.Options(file, schema),
        _ => { started = true; RPCReplay.Endpoint(1, schema, () => ()) }
      ))
      assert(!started)
    }
    temporary { file =>
      val writer     = RPCJournal.Writer(file, RPCJournal.Header(schema, RPCJournal.Limits(maxFrameBytes = 100)))
      val connection = writer.connection(Object())
      val _          = intercept[IOException](connection.request(execution(3, Array.ofDim[Byte](5))))
      writer.close()
      assert(!RPCJournal.inspect(file).replayable)
    }
  }
  test("frame bounds include headers and lifecycle ordering is validated") {
    temporary { file =>
      val writer = RPCJournal.Writer(file, RPCJournal.Header(schema, RPCJournal.Limits(maxEvents = 2)))
      val c      = writer.connection(Object())
      c.request(execution()); c.response(response())
      val _ = intercept[IOException](c.close())
      writer.close()
      assert(!RPCJournal.inspect(file).replayable)
    }
    temporary { file =>
      val writer = RPCJournal.Writer(file, RPCJournal.Header(schema))
      val c      = writer.connection(Object())
      val _      = intercept[IOException](c.response(response()))
      writer.close()
      assert(!RPCJournal.inspect(file).replayable)
    }
  }
}
