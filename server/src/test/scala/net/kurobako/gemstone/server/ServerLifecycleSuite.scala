package net.kurobako.gemstone.server

import net.kurobako.gemstone.execution.ExecutionLimits
import java.net.Socket
import java.util.concurrent.{CountDownLatch, TimeUnit}
import java.util.concurrent.atomic.AtomicInteger

class ServerLifecycleSuite extends munit.FunSuite {
  test("closed client sessions reject a connection whose first acquisition was delayed") {
    val created  = AtomicInteger()
    val pool     = BoundedExecutionPool.fromEnvironment(Map("GEMSTONE_EXECUTION_PARALLELISM" -> "1"))
    val sessions = GemstoneRPCServer.ExecutionSessions(
      () => { created.incrementAndGet(); RegisteredProgramExecutor.default },
      ExecutionLimits.Default,
      pool
    )
    val existing = sessions.connection()
    val delayed  = sessions.connection()
    try {
      assert(existing.service(ExecutionRPCProtocol.SessionId(1, 1)).isRight)
      sessions.close()
      assert(existing.service(ExecutionRPCProtocol.SessionId(1, 1)).isLeft)
      assert(delayed.service(ExecutionRPCProtocol.SessionId(2, 1)).isLeft)
      assertEquals(created.get(), 1)
    } finally { existing.close(); delayed.close(); sessions.close(); pool.close() }
  }

  test("normal callback retirement does not signal socket EOF before scope cleanup") {
    val closed     = AtomicInteger()
    val connection = GemstoneRPCServer.CallbackConnection(
      java.io.DataInputStream(java.io.ByteArrayInputStream(Array.emptyByteArray)),
      java.io.DataOutputStream(java.io.ByteArrayOutputStream()),
      None,
      () => { closed.incrementAndGet(); () }
    )
    connection.retire()
    assert(connection.isClosed)
    assertEquals(closed.get(), 0)
  }

  private final class ObservedSocket(closed: CountDownLatch) extends Socket {
    override def close(): Unit = closed.countDown()
  }
  test("connection shutdown cancels work before waiting for request threads") {
    val cancelled    = CountDownLatch(1)
    val ready        = CountDownLatch(1)
    val socketClosed = CountDownLatch(1)
    val worker       = Thread { () => ready.countDown(); cancelled.await() }
    worker.setDaemon(true); worker.start()
    assert(ready.await(2, TimeUnit.SECONDS))
    val connections = GemstoneRPCServer.ClientConnections()
    connections.add(ObservedSocket(socketClosed), worker)
    val closer = Thread(() => connections.closeAll(() => cancelled.countDown()))
    closer.setDaemon(true); closer.start()
    try {
      assert(socketClosed.await(2, TimeUnit.SECONDS))
      assert(cancelled.await(2, TimeUnit.SECONDS), "shutdown waited for uncancelled work")
    } finally {
      cancelled.countDown()
      worker.join(2000); closer.join(2000)
      assert(!worker.isAlive && !closer.isAlive)
    }
  }
}
