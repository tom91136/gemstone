package net.kurobako.gemstone.server

import java.io.{
  BufferedInputStream,
  ByteArrayInputStream,
  ByteArrayOutputStream,
  DataInputStream,
  DataOutputStream,
  PushbackInputStream
}
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import net.kurobako.gemstone.execution.ExecutionLimits
import net.kurobako.gemstone.generated.GeneratedCUDARPC

final class RPCRecordingSuite extends munit.FunSuite {
  private val token                                                      = "a" * 64
  private def start(file: java.io.File): GemstoneRPCServer.RunningServer = GemstoneRPCServer.start(
    token,
    ExecutionLimits.Default,
    Map("GEMSTONE_RECORD" -> file.toString, "GEMSTONE_EXECUTION_PARALLELISM" -> "1")
  )
  private def fresh(header: RPCJournal.Header): RPCReplay.Endpoint = {
    val profile = header.executionProfile.getOrElse(fail("recorded execution profile is missing"))
    val server  = GemstoneRPCServer.start(
      token,
      ExecutionLimits(profile.maxInstructions, profile.quantum),
      Map("GEMSTONE_EXECUTION_PARALLELISM" -> profile.parallelism.toString)
    )
    RPCReplay.Endpoint(server.port, token, () => server.close())
  }
  private final class Wire(port: Int, timeoutMillis: Int = 10000) extends AutoCloseable {
    private val socket = Socket("127.0.0.1", port)
    socket.setSoTimeout(timeoutMillis)
    val in  = DataInputStream(socket.getInputStream)
    val out = DataOutputStream(socket.getOutputStream)
    def execution(op: Int, bytes: Array[Byte], credential: String = token): (Int, Array[Byte]) = {
      out.writeInt(0x474d4531); out.writeInt(3); out.write(credential.getBytes(StandardCharsets.US_ASCII))
      out.writeLong(1); out.writeLong(2); out.writeInt(op); out.writeInt(bytes.length)
      if (bytes.nonEmpty) out.write(bytes)
      out.flush()
      response(0x474d4631)
    }
    def cuda(name: String, bytes: Array[Byte], credential: String = token, context: Long = 0L): Unit = {
      val method = GeneratedCUDARPC.Schema.methods.find(_.name == name).get
      out.writeInt(0x474d5331); out.writeInt(4)
      out.write(GeneratedCUDARPC.SchemaFingerprint.getBytes(StandardCharsets.US_ASCII))
      out.write(credential.getBytes(StandardCharsets.US_ASCII)); out.writeLong(1); out.writeLong(2)
      out.writeLong(context)
      out.writeLong(method.messageId.high); out.writeLong(method.messageId.low)
      out.writeInt(name.length); out.writeInt(bytes.length); out.write(name.getBytes(StandardCharsets.US_ASCII))
      if (bytes.nonEmpty) out.write(bytes)
      out.flush()
    }
    def response(magic: Int): (Int, Array[Byte]) = {
      assertEquals(in.readInt(), magic); assertEquals(in.readInt(), if magic == 0x474d4631 then 3 else 4)
      val status = in.readInt(); val messageSize = in.readInt(); val size = in.readInt()
      val _      = in.readNBytes(messageSize)
      status -> in.readNBytes(size)
    }
    def finish(): Unit         = { socket.shutdownOutput(); assertEquals(in.read(), -1); socket.close() }
    override def close(): Unit = socket.close()
  }

  test("CUDA version 3 framing is rejected before its shorter header is decoded") {
    val archive = Files.createTempDirectory("gemstone-journal-old-cuda").resolve("capture.bin").toFile
    val server  = start(archive)
    try {
      val socket = Socket("127.0.0.1", server.port)
      socket.setSoTimeout(5000)
      try {
        val output = DataOutputStream(socket.getOutputStream)
        output.writeInt(0x474d5331)
        output.writeInt(3)
        output.write(GeneratedCUDARPC.SchemaFingerprint.getBytes(StandardCharsets.US_ASCII))
        output.write(token.getBytes(StandardCharsets.US_ASCII))
        output.writeLong(1L); output.writeLong(2L)
        output.writeLong(11L); output.writeLong(12L)
        output.writeInt(1); output.writeInt(0); output.writeByte('x')
        output.flush()
        assertEquals(socket.getInputStream.read(), -1)
      } finally socket.close()
    } finally server.close()
  }

  test("actual mixed execution/CUDA/callback frames replay after graceful close, without credentials") {
    val archive = Files.createTempDirectory("gemstone-journal-mixed").resolve("capture.bin").toFile
    val server  = start(archive)
    try {
      val wire = Wire(server.port)
      try {
        assertEquals(wire.execution(1, ByteBuffer.allocate(20).putLong(7).putInt(1).putLong(4).array())._1, 0)
        assertEquals(
          wire.execution(3, ByteBuffer.allocate(24).putLong(7).putLong(0).putInt(4).putInt(0x12345678).array())._1,
          0
        )
        val read = wire.execution(4, ByteBuffer.allocate(20).putLong(7).putLong(0).putInt(4).array())
        assertEquals(read._1, 0); assertEquals(read._2.toSeq, Array[Byte](0x12, 0x34, 0x56, 0x78).toSeq)
        wire.cuda("cuInit", ByteBuffer.allocate(4).putInt(0).array())
        assertEquals(wire.response(0x474d5231)._1, 0)
        wire.cuda("cuCtxCreate_v2", ByteBuffer.allocate(8).putInt(0).putInt(0).array())
        assertEquals(wire.response(0x474d5231)._1, 0)
        wire.cuda("cuLaunchHostFunc", ByteBuffer.allocate(24).putLong(0).putLong(9).putLong(9).array(), context = 1)
        assertEquals(wire.response(0x474d5231)._1, 0)
        wire.cuda("cuCtxSynchronize", Array.emptyByteArray, context = 1)
        assertEquals(wire.in.readInt(), 0x474d4331); assertEquals(wire.in.readInt(), 4)
        assertEquals(wire.in.readLong(), 9L); assertEquals(wire.in.readInt(), 1); assertEquals(wire.in.readInt(), 0)
        wire.out.writeInt(0x474d4131); wire.out.writeInt(4); wire.out.writeInt(0)
        wire.out.writeInt(0); wire.out.writeInt(0); wire.out.flush()
        assertEquals(wire.response(0x474d5231)._1, 0)
        wire.finish()
      } finally wire.close()
    } finally server.close()
    try {
      val summary = RPCJournal.inspect(archive)
      assert(summary.replayable, summary.reason)
      assert(!String(Files.readAllBytes(archive.toPath), StandardCharsets.ISO_8859_1).contains(token))
      val replay = RPCReplay.run(RPCReplay.Options(archive, GeneratedCUDARPC.SchemaFingerprint), fresh)
      assertEquals(replay.requests, 7)
      assertEquals(replay.callbacks, 1)
    } finally { val _ = archive.delete(); val _ = archive.getParentFile.delete() }
  }

  test("callbacks stay on their owner connection and closed owners are retired without IO") {
    JournalFixtures.temporary { archive =>
      val server = start(archive)
      val owner  = Wire(server.port, 1000)
      val other  = Wire(server.port, 1000)
      try {
        owner.cuda("cuCtxCreate_v2", ByteBuffer.allocate(8).putInt(0).putInt(0).array())
        assertEquals(owner.response(RPCFrames.CUDAResponse)._1, 0)
        other.cuda("cuInit", ByteBuffer.allocate(4).putInt(0).array())
        assertEquals(other.response(RPCFrames.CUDAResponse)._1, 0)
        owner.cuda("cuLaunchHostFunc", ByteBuffer.allocate(24).putLong(0).putLong(9).putLong(9).array(), context = 1)
        assertEquals(owner.response(RPCFrames.CUDAResponse)._1, 0)
        other.cuda("cuCtxSynchronize", Array.emptyByteArray, context = 1)
        assertEquals(other.response(RPCFrames.CUDAResponse)._1, 801)
        other.cuda("cuInit", ByteBuffer.allocate(4).putInt(0).array())
        assertEquals(other.response(RPCFrames.CUDAResponse)._1, 0)
        owner.cuda("cuCtxSynchronize", Array.emptyByteArray, context = 1)
        assertEquals(owner.in.readInt(), RPCFrames.CallbackRequest)
        assertEquals(owner.in.readInt(), 4)
        assertEquals(owner.in.readLong(), 9L)
        assertEquals(owner.in.readInt(), 1); assertEquals(owner.in.readInt(), 0)
        // Rejection must not wait for the owner's currently unanswered callback.
        other.cuda("cuCtxSynchronize", Array.emptyByteArray, context = 1)
        assertEquals(other.response(RPCFrames.CUDAResponse)._1, 801)
        owner.out.writeInt(RPCFrames.CallbackReply); owner.out.writeInt(4); owner.out.writeInt(0)
        owner.out.writeInt(0); owner.out.writeInt(0); owner.out.flush()
        assertEquals(owner.response(RPCFrames.CUDAResponse)._1, 0)
        owner.cuda("cuLaunchHostFunc", ByteBuffer.allocate(24).putLong(0).putLong(10).putLong(10).array(), context = 1)
        assertEquals(owner.response(RPCFrames.CUDAResponse)._1, 0)
        owner.finish()
        other.cuda("cuCtxSynchronize", Array.emptyByteArray, context = 1)
        assertEquals(other.response(RPCFrames.CUDAResponse)._1, 0)
        other.finish()
      } finally { owner.close(); other.close(); server.close() }
      assert(RPCJournal.inspect(archive).replayable)
      val replay = RPCReplay.run(RPCReplay.Options(archive, GeneratedCUDARPC.SchemaFingerprint), fresh)
      assertEquals(replay.callbacks, 1)
    }
  }

  test("client revocation cancels an unanswered callback without waiting for its channel") {
    JournalFixtures.temporary { archive =>
      val server       = start(archive)
      val registration = Wire(server.port, 1000)
      val owner        = Wire(server.port, 1000)
      val revocation   = Wire(server.port, 1000)
      try {
        registration.out.writeInt(0x474d4330); registration.out.write(token.getBytes(StandardCharsets.US_ASCII))
        registration.out.writeInt(1); registration.out.flush()
        val child = registration.in.readUTF()
        registration.close()
        owner.cuda("cuCtxCreate_v2", ByteBuffer.allocate(8).putInt(0).putInt(0).array(), child)
        assertEquals(owner.response(RPCFrames.CUDAResponse)._1, 0)
        owner.cuda("cuLaunchHostFunc", ByteBuffer.allocate(24).putLong(0).putLong(9).putLong(9).array(), child, 1)
        assertEquals(owner.response(RPCFrames.CUDAResponse)._1, 0)
        owner.cuda("cuCtxSynchronize", Array.emptyByteArray, child, 1)
        assertEquals(owner.in.readInt(), RPCFrames.CallbackRequest)
        assertEquals(owner.in.readInt(), 4); assertEquals(owner.in.readLong(), 9L)
        assertEquals(owner.in.readInt(), 1); assertEquals(owner.in.readInt(), 0)
        revocation.out.writeInt(0x474d4330); revocation.out.write(token.getBytes(StandardCharsets.US_ASCII))
        revocation.out.writeInt(3); revocation.out.write(child.getBytes(StandardCharsets.US_ASCII));
        revocation.out.flush()
        assertEquals(revocation.in.readInt(), 0)
        assertEquals(owner.in.read(), -1)
      } finally { registration.close(); owner.close(); revocation.close(); server.close() }
      assert(!RPCJournal.inspect(archive).replayable, "interrupted callback must not become a complete capture")
    }
  }

  test("unauthenticated traffic creates no recorded client, request or response") {
    val archive = Files.createTempDirectory("gemstone-journal-auth").resolve("capture.bin").toFile
    val server  = start(archive)
    val wire    = Wire(server.port)
    try { assert(wire.execution(0, Array.emptyByteArray, "b" * 64)._1 != 0); wire.finish() }
    finally { wire.close(); server.close() }
    try {
      val summary = RPCJournal.inspect(archive)
      assert(summary.replayable)
      assertEquals(summary.events, 0)
    } finally { val _ = archive.delete(); val _ = archive.getParentFile.delete() }
  }

  test("server close joins active idle connections before the replayable footer") {
    val archive = Files.createTempDirectory("gemstone-journal-shutdown").resolve("capture.bin").toFile
    val server  = start(archive)
    val wire    = Wire(server.port)
    try {
      assertEquals(wire.execution(0, Array.emptyByteArray)._1, 0)
      server.close()
      assert(RPCJournal.inspect(archive).replayable)
      assertEquals(
        RPCReplay.run(RPCReplay.Options(archive, GeneratedCUDARPC.SchemaFingerprint), fresh).requests,
        1
      )
    } finally { wire.close(); server.close(); val _ = archive.delete(); val _ = archive.getParentFile.delete() }
  }

  test("authenticated shutdown control stops accepting and lets cleanup finish the journal") {
    val archive = Files.createTempDirectory("gemstone-journal-control").resolve("capture.bin").toFile
    val server  = start(archive)
    val invalid = Wire(server.port)
    try {
      invalid.out.writeInt(0x474d4330)
      invalid.out.write(("b" * 64).getBytes(StandardCharsets.US_ASCII))
      invalid.out.writeInt(4); invalid.out.flush()
      assertEquals(invalid.in.read(), -1)
      val valid = Wire(server.port)
      try { assertEquals(valid.execution(0, Array.emptyByteArray)._1, 0); valid.finish() }
      finally valid.close()
      GemstoneRPCServer.main(Array("shutdown", "--port", server.port.toString, "--token", token))
      server.await()
      server.close()
      assert(RPCJournal.inspect(archive).replayable)
    } finally { invalid.close(); server.close(); val _ = archive.delete(); val _ = archive.getParentFile.delete() }
  }

  test("shutdown during an unanswered callback preserves an explicitly unreplayable footer") {
    val archive = Files.createTempDirectory("gemstone-journal-interrupted").resolve("capture.bin").toFile
    val server  = start(archive)
    val wire    = Wire(server.port)
    try {
      wire.cuda("cuInit", ByteBuffer.allocate(4).putInt(0).array())
      assertEquals(wire.response(0x474d5231)._1, 0)
      wire.cuda("cuCtxCreate_v2", ByteBuffer.allocate(8).putInt(0).putInt(0).array())
      assertEquals(wire.response(0x474d5231)._1, 0)
      wire.cuda("cuLaunchHostFunc", ByteBuffer.allocate(24).putLong(0).putLong(9).putLong(9).array(), context = 1)
      assertEquals(wire.response(0x474d5231)._1, 0)
      wire.cuda("cuCtxSynchronize", Array.emptyByteArray, context = 1)
      assertEquals(wire.in.readInt(), 0x474d4331)
      server.close()
      val summary = RPCJournal.inspect(archive)
      assert(!summary.replayable)
      assert(summary.reason.nonEmpty)
      val _ = intercept[java.io.IOException](RPCReplay.run(
        RPCReplay.Options(archive, GeneratedCUDARPC.SchemaFingerprint),
        _ => fail("must reject before starting replay")
      ))
    } finally { wire.close(); server.close(); val _ = archive.delete(); val _ = archive.getParentFile.delete() }
  }

  test("registered client lifetimes replay separately with colliding session and allocation IDs") {
    val archive = Files.createTempDirectory("gemstone-journal-clients").resolve("capture.bin").toFile
    val server  = start(archive)
    def control(operation: Int, client: Option[String] = None): String = {
      val wire = Wire(server.port)
      try {
        wire.out.writeInt(0x474d4330); wire.out.write(token.getBytes(StandardCharsets.US_ASCII))
        wire.out.writeInt(operation)
        client.foreach(value => wire.out.write(value.getBytes(StandardCharsets.US_ASCII)))
        wire.out.flush()
        if (operation == 1) wire.in.readUTF()
        else { assertEquals(wire.in.readInt(), 0); "" }
      } finally wire.close()
    }
    val firstToken  = control(1)
    val secondToken = control(1)
    val first       = Wire(server.port)
    val second      = Wire(server.port)
    try {
      for ((wire, credential, value) <- Seq((first, firstToken, 11), (second, secondToken, 22))) {
        assertEquals(
          wire.execution(1, ByteBuffer.allocate(20).putLong(7).putInt(1).putLong(4).array(), credential)._1,
          0
        )
        assertEquals(
          wire.execution(
            3,
            ByteBuffer.allocate(24).putLong(7).putLong(0).putInt(4).putInt(value).array(),
            credential
          )._1,
          0
        )
      }
      for ((wire, credential, expected) <- Seq((first, firstToken, 11), (second, secondToken, 22))) {
        val result = wire.execution(4, ByteBuffer.allocate(20).putLong(7).putLong(0).putInt(4).array(), credential)
        assertEquals(result._1, 0); assertEquals(ByteBuffer.wrap(result._2).getInt(), expected)
        wire.finish()
        val _ = control(3, Some(credential))
      }
    } finally { first.close(); second.close(); server.close() }
    try {
      val bytes = String(Files.readAllBytes(archive.toPath), StandardCharsets.ISO_8859_1)
      assert(!bytes.contains(firstToken) && !bytes.contains(secondToken))
      assert(RPCJournal.inspect(archive).replayable)
      assertEquals(
        RPCReplay.run(RPCReplay.Options(archive, GeneratedCUDARPC.SchemaFingerprint), fresh).requests,
        6
      )
    } finally { val _ = archive.delete(); val _ = archive.getParentFile.delete() }
  }

  test("capture records resolved nondefault limits and parallelism for replay") {
    val archive = Files.createTempDirectory("gemstone-journal-profile").resolve("capture.bin").toFile
    val profile = RPCJournal.ExecutionProfile(2000000000L, 73, 2)
    val server  = GemstoneRPCServer.start(
      token,
      ExecutionLimits(profile.maxInstructions, profile.quantum),
      Map("GEMSTONE_RECORD" -> archive.toString, "GEMSTONE_EXECUTION_PARALLELISM" -> profile.parallelism.toString)
    )
    val wire = Wire(server.port)
    try { assertEquals(wire.execution(0, Array.emptyByteArray)._1, 0); wire.finish() }
    finally { wire.close(); server.close() }
    try {
      assertEquals(RPCJournal.inspect(archive).header.executionProfile, Some(profile))
      assertEquals(
        RPCReplay.run(
          RPCReplay.Options(archive, GeneratedCUDARPC.SchemaFingerprint),
          header => {
            assertEquals(header.executionProfile, Some(profile)); fresh(header)
          }
        ).requests,
        1
      )
    } finally { val _ = archive.delete(); val _ = archive.getParentFile.delete() }
  }

  private final class ChunkedInput(bytes: Array[Byte]) extends ByteArrayInputStream(bytes) {
    override def read(destination: Array[Byte], offset: Int, length: Int): Int =
      super.read(destination, offset, math.min(length, 7))
  }

  test("credential pushback across buffer refills records every consumed byte exactly once") {
    val archive = Files.createTempDirectory("gemstone-journal-buffer").resolve("capture.bin").toFile
    val frames  = (1 to 2).map(id =>
      ByteBuffer.allocate(96).putInt(0x474d4531).putInt(3)
        .put(token.getBytes(StandardCharsets.US_ASCII)).putLong(id.toLong).putLong(999).putInt(0).putInt(0).array()
    )
    val writer    = RPCJournal.Writer(archive, RPCJournal.Header(GeneratedCUDARPC.SchemaFingerprint))
    val recording =
      RPCRecording(writer, BufferedInputStream(ChunkedInput(frames.flatten.toArray), 8), ByteArrayOutputStream())
    val pushback = PushbackInputStream(recording.input, 132)
    val input    = DataInputStream(pushback)
    val owner    = Object()
    try {
      frames.foreach { frame =>
        recording.begin()
        assertEquals(input.readInt(), 0x474d4531)
        val peek = input.readNBytes(68)
        assertEquals(peek.length, 68)
        pushback.unread(peek)
        assertEquals(input.readNBytes(92).toSeq, frame.drop(4).toSeq)
        recording.accepted(owner)
        recording.output.write(
          ByteBuffer.allocate(20).putInt(0x474d4631).putInt(3).putInt(0).putInt(0).putInt(0).array()
        )
        recording.response()
      }
      assertEquals(input.read(), -1)
      recording.close(); writer.clientClosed(owner)
    } finally writer.close()
    val reader = RPCJournal.open(archive)
    try {
      assert(reader.summary.replayable)
      val requests = reader.events.filter(_.kind == RPCJournal.Kind.Request)
      assertEquals(requests.length, 2)
      requests.zip(frames).foreach { case (event, frame) =>
        val expected = frame.clone()
        java.util.Arrays.fill(expected, 8, 72, 0.toByte)
        assertEquals(reader.frame(event).toSeq, expected.toSeq)
      }
    } finally { reader.close(); val _ = archive.delete(); val _ = archive.getParentFile.delete() }
  }

  test("launcher owns recording and removes its archive path from the inherited child environment") {
    val directory = Files.createTempDirectory("gemstone-journal-launcher")
    val archive   = directory.resolve("capture.bin").toFile
    val driver    = directory.resolve("libcuda.so")
    val _         = Files.write(driver, Array.emptyByteArray)
    try {
      val result = net.kurobako.gemstone.launcher.GemstoneProcess.attested(
        net.kurobako.gemstone.launcher.ComputeAPI.CUDA,
        net.kurobako.gemstone.launcher.RuntimeLayout(cudaDriver = Some(driver)),
        Map("GEMSTONE_RECORD" -> archive.toString, "GEMSTONE_EXECUTION_PARALLELISM" -> "1")
      ) { environment =>
        val inherited = java.util.HashMap[String, String]()
        val _         = inherited.put("GEMSTONE_RECORD", archive.toString)
        environment.applyTo(inherited)
        assert(!inherited.containsKey("GEMSTONE_RECORD"))
        assert(!environment.set.contains("GEMSTONE_RECORD"))
        val wire = Wire(environment.set("GEMSTONE_RPC_PORT").toInt)
        try {
          wire.cuda("cuInit", ByteBuffer.allocate(4).putInt(0).array(), environment.set("GEMSTONE_RPC_TOKEN"))
          assertEquals(wire.response(0x474d5231)._1, 0)
          wire.finish()
        } finally wire.close()
        42
      }
      assertEquals(result, Right(42))
      assert(RPCJournal.inspect(archive).replayable)
      assertEquals(RPCReplay.run(RPCReplay.Options(archive, GeneratedCUDARPC.SchemaFingerprint), fresh).requests, 1)
    } finally { val _ = archive.delete(); Files.delete(driver); Files.delete(directory) }
  }

  test("launcher platform classification is locale-free on JVM and Native") {
    import net.kurobako.gemstone.launcher.RuntimePlatform
    for (name <- Seq("Linux", "LINUX", "linux")) assertEquals(RuntimePlatform.fromOSName(name), RuntimePlatform.Linux)
    for (name <- Seq("Windows 11", "WINDOWS 10", "windows"))
      assertEquals(RuntimePlatform.fromOSName(name), RuntimePlatform.Windows)
    for (name <- Seq("Darwin", "Mac OS X", "FreeBSD"))
      assertEquals(RuntimePlatform.fromOSName(name), RuntimePlatform.Other)
  }

}
