package net.kurobako.gemstone.server

import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.server.transport.{ClientId, ExecutionRPCProtocol}

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.io.{BufferedInputStream, ByteArrayInputStream, DataInputStream, InputStream, PushbackInputStream}
import java.nio.ByteBuffer
import java.net.{InetAddress, ServerSocket, Socket}
import scala.util.Using

final class RPCInputStreamSuite extends munit.FunSuite {
  private final class Cleanup(action: () => Unit) extends AutoCloseable {
    override def close(): Unit = action()
  }
  private final class InputFixture(val input: InputStream, cleanup: () => Unit) extends AutoCloseable {
    override def close(): Unit = Using.Manager { use =>
      val _ = use(Cleanup(cleanup))
      val _ = use(input)
    }.get
  }

  private final class FragmentedInput(source: InputStream) extends InputStream {
    def read(): Int                                                      = source.read()
    override def read(bytes: Array[Byte], offset: Int, length: Int): Int =
      source.read(bytes, offset, math.min(length, 511))
    override def available(): Int = 0
  }

  for {
    fully  <- Seq(false, true)
    socket <- Seq(false, true)
  }
    test(s"client identity peek preserves fragmented large consecutive frames readFully=$fully socket=$socket") {
      val client = ClientId(0x0102030405060708L)
      val prefix = ByteBuffer.allocate(ExecutionRPCProtocol.RequestIdentityBytesAfterMagic)
        .putLong(ExecutionRPC.Schema.fingerprint).putLong(client.value).array()
      val body    = Array.tabulate[Byte](17003)(index => (index * 17 + 3).toByte)
      val frame   = prefix ++ body
      val data    = frame ++ frame
      val fixture = if socket then {
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
        val replay     = PushbackInputStream(
          BufferedInputStream(fragmented, 256),
          ExecutionRPCProtocol.RequestIdentityBytesAfterMagic
        )
        val input = DataInputStream(replay)
        for (_ <- 0 until 2) {
          assertEquals(GemstoneRPCServer.peekClientId(input, replay), client)
          val actual = if fully then {
            val bytes = Array.ofDim[Byte](frame.length)
            input.readFully(bytes)
            bytes
          } else input.readNBytes(frame.length)
          assert(java.util.Arrays.equals(actual, frame), "client identity peek changed frame bytes or stream position")
        }
        assertEquals(input.read(), -1)
      }
    }
}
