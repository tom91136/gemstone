package net.kurobako.gemstone.server

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.server.backend.*
import net.kurobako.gemstone.server.recording.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class ServerLifecycleSuite extends munit.FunSuite {
  test("startup failure closes the journal and marks it unreplayable") {
    files.temporary { directory =>
      val archive = directory.resolve("failed.msgpack")
      val failure = intercept[IllegalStateException] {
        Server.start(
          Server.Settings(parallelism = 1, record = Some(archive)),
          _ => throw IllegalStateException("backend startup failed")
        )
      }
      assertEquals(failure.getMessage, "backend startup failed")
      Using.resource(Journal.open(archive))(reader => assert(!reader.summary.replayable))
    }
  }

  test("server shutdown cancels execution before joining connection and worker threads") {
    val started   = CountDownLatch(1)
    val cancelled = CountDownLatch(1)
    class WaitingProgram extends Backend.Program {
      override def symbols: Either[Fault, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
      override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
        KernelMetadata.validated(0, 1, Vec.empty).left.map(Fault.InvalidRequest(_))
      override def execute(
          task: KernelTask,
          memory: AddressSpace,
          control: TaskControl,
          handler: Event => Either[Fault, Unit]
      ): Either[Fault, Unit] = {
        started.countDown()
        val deadline = platform.monotonicNanos + TimeUnit.SECONDS.toNanos(5)
        try {
          while (!control.isCancelled && platform.monotonicNanos < deadline) Thread.sleep(1)
          Left(Fault.Backend("stopped"))
        } finally if (control.isCancelled) cancelled.countDown()
      }
    }
    class WaitingBackend extends Backend {
      override val format                                                 = ProgramFormat.PTXText
      override def load(image: Vec[Byte]): Either[Fault, Backend.Program] = Right(WaitingProgram())
    }
    Using.resource(Server.start(Server.Settings(parallelism = 1), _ => Backends(List(WaitingBackend())))) { server =>
      Using.resource(connections.Client(server.endpoint, server.client)) { client =>
        val program    = client.call(Operation.LoadProgram(ProgramEncoding.PTXText, Vec.empty)).toOption.get
        val one        = LaunchGeometry.Dimensions.One
        val completion = client.call(Operation.Launch(
          program,
          "wait",
          one,
          one,
          one.toGlobalDimensions,
          1,
          LaunchGeometry.Coordinates.origin,
          0,
          0,
          Vec.empty,
          Vec.empty
        )).toOption.get
        assert(started.await(2, TimeUnit.SECONDS))
        val waiter = Thread(() =>
          try { val _ = client.awaitCompletion(completion) }
          catch { case _: java.io.IOException => () }
        )
        waiter.start()
        val closer = Thread(() => server.close())
        closer.start()
        closer.join(3000)
        waiter.join(3000)
        assert(!closer.isAlive, "shutdown waited before cancelling execution")
        assert(!waiter.isAlive)
        assertEquals(cancelled.getCount, 0L)
      }
    }
  }
}
