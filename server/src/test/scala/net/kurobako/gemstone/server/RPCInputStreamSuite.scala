package net.kurobako.gemstone.server

import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.InputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.protocol.*
import scala.util.Using

class RPCInputStreamSuite extends munit.FunSuite {
  private class Cleanup(action: () => Unit) extends AutoCloseable {
    override def close(): Unit = action()
  }
  private class InputFixture(val input: InputStream, cleanup: () => Unit) extends AutoCloseable {
    override def close(): Unit = Using.Manager { use =>
      val _ = use(Cleanup(cleanup))
      val _ = use(input)
    }.get
  }

  private class FragmentedInput(source: InputStream) extends InputStream {
    def read(): Int                                                      = source.read()
    override def read(bytes: Array[Byte], offset: Int, length: Int): Int =
      source.read(bytes, offset, math.min(length, 511))
    override def available(): Int = 0
  }

  for {
    fully  <- Seq(false, true)
    socket <- Seq(false, true)
  }
    test(s"header parsing preserves fragmented large consecutive frames readFully=$fully socket=$socket") {
      val client = ClientId(0x0102030405060708L)
      val tag    = "Launch".getBytes(java.nio.charset.StandardCharsets.UTF_8)
      val prefix = ByteBuffer.allocate(ExecutionRPC.RequestHeaderBytes.raw - java.lang.Integer.BYTES + tag.length)
        .putLong(client.toLong).putLong(1L).putLong(2L).putInt(tag.length).putInt(17003).put(tag).array()
      val body    = Array.tabulate[Byte](17003)(index => (index * 17 + 3).toByte)
      val frame   = prefix ++ body
      val data    = frame ++ frame
      val fixture = if (socket) {
        val listener = ServerSocket(0, 1, InetAddress.getLoopbackAddress)
        val sender   = Socket(InetAddress.getLoopbackAddress, listener.getLocalPort)
        val receiver = listener.accept()
        listener.close()
        receiver.setSoTimeout(5000)
        val writer = Thread(() =>
          Using.resource(sender) { sender =>
            val output = sender.getOutputStream
            data.grouped(511).foreach(bytes => output.write(bytes))
            sender.shutdownOutput()
          }
        )
        writer.setDaemon(true)
        writer.start()
        InputFixture(
          receiver.getInputStream,
          () => {
            sender.close()
            writer.join(5000)
            assert(!writer.isAlive, "fragmented socket writer did not terminate")
          }
        )
      } else InputFixture(ByteArrayInputStream(data), () => ())
      Using.resource(fixture) { source =>
        val fragmented = FragmentedInput(source.input)
        val input      = DataInputStream(BufferedInputStream(fragmented, 256))
        for (_ <- 0 until 2) {
          val header = readHeader(input)
          assertEquals(header.client, client)
          val actual = if (fully) {
            val bytes = Array.ofDim[Byte](body.length)
            input.readFully(bytes)
            bytes
          } else readPayload(input, header)
          assert(java.util.Arrays.equals(actual, body), "header parsing changed payload bytes or stream position")
        }
        assertEquals(input.read(), -1)
      }
    }
  test("invalid tag and payload lengths are rejected before reading variable bytes") {
    for (
      (tagSize, payloadSize) <- Seq(
        (0, 0),
        (-1, 0),
        (ExecutionRPC.MaximumTagBytes.raw + 1, 0),
        (4, -1),
        (4, ExecutionRPC.MaximumPayload.raw + 1)
      )
    ) {
      val prefix = ByteBuffer.allocate(ExecutionRPC.RequestHeaderBytes.raw - 4)
        .putLong(1L).putLong(1L).putLong(1L).putInt(tagSize).putInt(payloadSize).array()
      intercept[IllegalArgumentException](readHeader(DataInputStream(ByteArrayInputStream(prefix))))
    }
  }

  test("exact reads preserve empty frames, partial-read diagnostics, and stream position") {
    val input = DataInputStream(FragmentedInput(ByteArrayInputStream(Array[Byte](1, 2, 3))))
    assertEquals(readExactly(input, 0, java.io.EOFException(_)).toSeq, Seq.empty[Byte])
    assertEquals(readExactly(input, 2, java.io.EOFException(_)).toSeq, Seq[Byte](1, 2))
    val error = intercept[java.io.EOFException](readExactly(input, 2, java.io.EOFException(_)))
    assertEquals(error.getMessage, "expected 2 bytes, received 1")
    assert(scala.compiletime.testing.typeCheckErrors("""
      import java.io.*
      import net.kurobako.gemstone.server.protocol.*
      readExactly(DataInputStream(ByteArrayInputStream(Array.emptyByteArray)), -1, EOFException(_))
    """).nonEmpty)
  }

  test("malformed UTF-8 tags are rejected") {
    val frame = ByteBuffer.allocate(ExecutionRPC.RequestHeaderBytes.raw - 4 + 1)
      .putLong(1L).putLong(1L).putLong(1L).putInt(1).putInt(0).put(0xff.toByte).array()
    intercept[java.nio.charset.CharacterCodingException](readHeader(DataInputStream(ByteArrayInputStream(frame))))
  }

  test("response framing rejects the wrong magic without waiting for a header") {
    val input = DataInputStream(ByteArrayInputStream(ByteBuffer.allocate(Integer.BYTES).putInt(0).array()))
    val error = intercept[java.io.IOException](Frame.readResponse(input, 64))
    assertEquals(error.getMessage, "unexpected RPC response magic")
  }

  test("response framing rejects invalid headers before reading or allocating payloads") {
    for ((status, size, maximum) <- Seq((2, 0, 64), (0, -1, 64), (0, Int.MaxValue, 64), (0, 65, 64))) {
      val prefix = ByteBuffer.allocate(ExecutionRPC.ResponseHeaderBytes.raw)
        .putInt(ExecutionRPC.ResponseMagic).putInt(status).putInt(size).array()
      val input = DataInputStream(ByteArrayInputStream(prefix))
      val error = intercept[java.io.IOException](Frame.readResponse(input, maximum))
      assertEquals(error.getClass, classOf[java.io.IOException])
    }
  }

  test("response framing retains the complete frame and rejects truncated payloads") {
    val frame = ByteBuffer.allocate(ExecutionRPC.ResponseHeaderBytes.raw + 3)
      .putInt(ExecutionRPC.ResponseMagic).putInt(0).putInt(3).put(Array[Byte](4, 5, 6)).array()
    assertEquals(
      Frame.readResponse(DataInputStream(ByteArrayInputStream(frame)), frame.length).toSeq,
      frame.toSeq
    )
    intercept[java.io.EOFException] {
      Frame.readResponse(DataInputStream(ByteArrayInputStream(frame.dropRight(1))), frame.length)
    }
  }

}
