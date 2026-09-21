package net.kurobako.gemstone.server

import net.kurobako.gemstone.server.execution.RuntimeBackend
import net.kurobako.gemstone.server.execution.WorkerPool
import net.kurobako.gemstone.server.transport.ExecutionRPCProtocol
import net.kurobako.gemstone.vm.TaskLimits
import java.net.Socket
import java.util.concurrent.{CountDownLatch, TimeUnit}
import scala.util.Using

final class ServerLifecycleSuite extends munit.FunSuite {
  test("closed client sessions reject a connection whose first acquisition was delayed") {
    Using.Manager { use =>
      val pool     = use(WorkerPool.fromEnvironment(Map("GEMSTONE_EXECUTION_PARALLELISM" -> "1")))
      val sessions = use(GemstoneRPCServer.ExecutionSessions(
        RuntimeBackend.defaults,
        TaskLimits.Default,
        pool
      ))
      val existing = use(sessions.connection())
      val delayed  = use(sessions.connection())
      assert(existing.service(ExecutionRPCProtocol.SessionId(1, 1)).isRight)
      sessions.close()
      assert(existing.service(ExecutionRPCProtocol.SessionId(1, 1)).isLeft)
      assert(delayed.service(ExecutionRPCProtocol.SessionId(2, 1)).isLeft)
    }.get
  }

  private final class ObservedSocket(closed: CountDownLatch) extends Socket {
    override def close(): Unit = closed.countDown()
  }
  private final class RunningThreads(cancel: () => Unit, threads: Thread*) extends AutoCloseable {
    override def close(): Unit = {
      cancel()
      threads.foreach(_.join(2000))
      assert(threads.forall(!_.isAlive))
    }
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
    Using.resource(RunningThreads(() => cancelled.countDown(), worker, closer)) { _ =>
      assert(socketClosed.await(2, TimeUnit.SECONDS))
      assert(cancelled.await(2, TimeUnit.SECONDS), "shutdown waited for uncancelled work")
    }
  }
}
