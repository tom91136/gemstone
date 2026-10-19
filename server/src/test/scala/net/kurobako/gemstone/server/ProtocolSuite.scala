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

class ProtocolSuite extends munit.FunSuite {
  private val Booleans = Seq(false, true)

  private class FragmentedInput(source: InputStream) extends InputStream {
    def read(): Int                                                      = source.read()
    override def read(bytes: Array[Byte], offset: Int, length: Int): Int =
      source.read(bytes, offset, math.min(length, 511))
    override def available(): Int = 0
  }

  for {
    fully  <- Booleans
    socket <- Booleans
  }
    test(s"header parsing preserves fragmented large consecutive frames readFully=$fully socket=$socket") {
      val client = ClientId(0x0102030405060708L)
      val body   = Array.tabulate[Byte](17003)(index => (index * 17 + 3).toByte)
      val frame  =
        connections.requestFrame(client.raw, process = 1L, incarnation = 2L, "Launch", body).drop(Integer.BYTES)
      val data                                     = frame ++ frame
      def withSource[A](read: InputStream => A): A =
        if (!socket) read(ByteArrayInputStream(data))
        else {
          val listener = ServerSocket(0, 1, InetAddress.getLoopbackAddress)
          val sender   = Socket(InetAddress.getLoopbackAddress, listener.getLocalPort)
          val receiver = try listener.accept()
          finally listener.close()
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
          try Using.resource(receiver.getInputStream)(read)
          finally {
            sender.close()
            writer.join(5000)
            assert(!writer.isAlive, "fragmented socket writer did not terminate")
          }
        }
      withSource { source =>
        val fragmented = FragmentedInput(source)
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
      val prefix = ByteBuffer.allocate(ExecutionRPC.RequestHeaderBytes - 4)
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
  }

  test("malformed UTF-8 tags are rejected") {
    val frame = ByteBuffer.allocate(ExecutionRPC.RequestHeaderBytes - 4 + 1)
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
      val prefix = ByteBuffer.allocate(ExecutionRPC.ResponseHeaderBytes)
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
