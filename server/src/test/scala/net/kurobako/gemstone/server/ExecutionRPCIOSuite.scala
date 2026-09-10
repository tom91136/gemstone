package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, DataInputStream, DataOutputStream}
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class ExecutionRPCIOSuite extends munit.FunSuite {
  private final class FixedConnection(executionService: ExecutionService)
      extends ExecutionRPCProtocol.SessionConnection {
    def service(id: ExecutionRPCProtocol.SessionId) = Right(executionService)
    def close(): Unit                               = ()
  }
  private final class RejectEmptyWrites extends ByteArrayOutputStream {
    override def write(value: Array[Byte], offset: Int, length: Int): Unit = {
      require(length > 0, "socket implementation rejects empty writes")
      super.write(value, offset, length)
    }
  }

  for (authenticated <- Seq(true, false))
    test(s"execution response avoids empty socket writes, authenticated=$authenticated") {
      val token   = ("a" * 64).getBytes(StandardCharsets.US_ASCII)
      val sent    = if authenticated then token else ("b" * 64).getBytes(StandardCharsets.US_ASCII)
      val request = ByteBuffer.allocate(92).putInt(3).put(sent).putLong(1L).putLong(2L).putInt(0).putInt(0).array()
      val executionService =
        ExecutionService(RegisteredProgramExecutor.create(Col.empty).fold(message => fail(message), identity))
      val connection = FixedConnection(executionService)
      val bytes      = RejectEmptyWrites()
      try {
        assert(ExecutionRPCProtocol.exchange(
          DataInputStream(ByteArrayInputStream(request)),
          DataOutputStream(bytes),
          token,
          connection
        ))
        val response = ByteBuffer.wrap(bytes.toByteArray)
        assertEquals(response.getInt(), 0x474d4631)
        assertEquals(response.getInt(), 3)
        assertEquals(response.getInt(), if authenticated then 0 else 1)
        val messageSize = response.getInt()
        assertEquals(response.getInt(), 0)
        assertEquals(response.remaining(), messageSize)
        assertEquals(messageSize == 0, authenticated)
      } finally executionService.close()
    }
}
