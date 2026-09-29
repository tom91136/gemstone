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
          } else input.readNBytes(body.length)
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

  test("malformed UTF-8 tags are rejected") {
    val frame = ByteBuffer.allocate(ExecutionRPC.RequestHeaderBytes.raw - 4 + 1)
      .putLong(1L).putLong(1L).putLong(1L).putInt(1).putInt(0).put(0xff.toByte).array()
    intercept[java.nio.charset.CharacterCodingException](readHeader(DataInputStream(ByteArrayInputStream(frame))))
  }

}
