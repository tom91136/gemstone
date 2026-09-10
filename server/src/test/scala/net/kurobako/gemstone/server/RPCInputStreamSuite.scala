package net.kurobako.gemstone.server

import java.io.{BufferedInputStream, ByteArrayInputStream, DataInputStream, InputStream, PushbackInputStream}
import java.nio.ByteBuffer
import java.net.{InetAddress, ServerSocket, Socket}

class RPCInputStreamSuite extends munit.FunSuite {
  private final class FragmentedInput(source: InputStream) extends InputStream {
    def read(): Int                                                      = source.read()
    override def read(bytes: Array[Byte], offset: Int, length: Int): Int =
      source.read(bytes, offset, math.min(length, 511))
    override def available(): Int = 0
  }

  for {
    cuda   <- Seq(false, true)
    fully  <- Seq(false, true)
    socket <- Seq(false, true)
  }
    test(s"credential peek preserves fragmented large consecutive frames cuda=$cuda readFully=$fully socket=$socket") {
      val token  = Array.fill[Byte](64)(97)
      val prefix = ByteBuffer.allocate(4 + (if cuda then 64 else 0) + 64)
        .putInt(if cuda then 4 else 3).put(Array.fill[Byte](if cuda then 64 else 0)(98)).put(token).array()
      val body              = Array.tabulate[Byte](17003)(index => (index * 17 + 3).toByte)
      val frame             = prefix ++ body
      val data              = frame ++ frame
      val (source, cleanup) = if socket then {
        val listener = ServerSocket(0, 1, InetAddress.getLoopbackAddress)
        val sender   = Socket(InetAddress.getLoopbackAddress, listener.getLocalPort)
        val receiver = listener.accept()
        listener.close()
        receiver.setSoTimeout(5000)
        val writer = Thread(() =>
          try {
            val output = sender.getOutputStream
            data.grouped(511).foreach(bytes => output.write(bytes))
            sender.shutdownOutput()
          } finally sender.close()
        )
        writer.setDaemon(true)
        writer.start()
        (
          receiver.getInputStream,
          () => {
            receiver.close()
            sender.close()
            writer.join(5000)
            assert(!writer.isAlive, "fragmented socket writer did not terminate")
          }
        )
      } else (ByteArrayInputStream(data), () => ())
      try {
        val fragmented = FragmentedInput(source)
        val replay     = PushbackInputStream(BufferedInputStream(fragmented, 256), 132)
        val input      = DataInputStream(replay)
        for (_ <- 0 until 2) {
          assertEquals(GemstoneRPCServer.peekClientToken(input, replay, cuda).toSeq, token.toSeq)
          val actual = if fully then {
            val bytes = Array.ofDim[Byte](frame.length)
            input.readFully(bytes)
            bytes
          } else input.readNBytes(frame.length)
          assert(java.util.Arrays.equals(actual, frame), "credential peek changed frame bytes or stream position")
        }
        assertEquals(input.read(), -1)
      } finally { source.close(); cleanup() }
    }
}
