package net.kurobako.gemstone.server

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.rpc.RuntimeBackend.*
import net.kurobako.gemstone.server.backend.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.server.recording.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class ServerLifecycleSuite extends munit.FunSuite {
  test("server sessions supply SPIR-V specialization independently of program loading") {
    val words = Vec[Long](
      spv.SPIRV.Magic.toLong,
      0x00010600L,
      0L,
      3L,
      0L,
      (4L << 16) | spv.Instruction.Definition.Decorate.opcodeNumber.toInt.toLong,
      2L,
      1L,
      7L,
      (4L << 16) | spv.Instruction.Definition.TypeInt.opcodeNumber.toInt.toLong,
      1L,
      32L,
      0L,
      (4L << 16) | spv.Instruction.Definition.SpecConstant.opcodeNumber.toInt.toLong,
      1L,
      2L,
      12L
    )
    val images = Vec(Binary32(7L), Binary32(0L), Binary32(0x80000000L), Binary32(0xffffffffL)).map { specId =>
      // Word 8 is the SpecId decoration literal in this fixture.
      specId ->
        words.updated(8, specId.toLong).flatMap(word => Vec.from((0 until 4).map(byte => (word >>> (byte * 8)).toByte)))
    }
    Using.Manager { use =>
      val pool       = use(WorkerPool(1))
      val backends   = Backends.default(pool)
      val clients    = use(Clients(() => ExecutionSession(backends.programs, specialization = backends.specialization)))
      val connection = use(clients.acquire(Clients.Root, SessionId(3, 1)).toOption.get)
      val service    = connection.service.toOption.get
      images.foreach { case (specId, image) =>
        assertEquals(service.describeSpecialization(image), Right(Vec(SpecializationInfo(specId, 4))))
        val value = Vec[Byte](99, 0, 0, 0)
        assertEquals(
          service.specializeImage(image, Vec(SpecializationConstant(specId, value))),
          Right(image.dropRight(4) ++ value)
        )
        assert(service.specializeImage(image, Vec(SpecializationConstant(specId, Vec[Byte](1)))).isLeft)
      }
    }.get
  }

  test("released clients revoke existing leases and reject delayed acquisition") {
    Using.resource(Clients(() => ExecutionSession(Nil))) { clients =>
      val id       = clients.register()
      val existing = clients.acquire(id, SessionId(1, 1)).toOption.get
      assert(existing.service.isRight)
      clients.release(id)
      assert(existing.service.isLeft)
      assert(clients.acquire(id, SessionId(2, 1)).isLeft)
      existing.close()
    }
  }

  test("connections share a process session until its last lease closes") {
    Using.resource(Clients(() => ExecutionSession(Nil))) { clients =>
      val first   = clients.acquire(Clients.Root, SessionId(1, 1)).toOption.get
      val second  = clients.acquire(Clients.Root, SessionId(1, 1)).toOption.get
      val service = first.service.toOption.get
      assert(second.service.toOption.get eq service)
      first.close()
      first.close()
      assert(first.service.isLeft)
      assert(second.service.toOption.get eq service)
      second.close()
      val next = clients.acquire(Clients.Root, SessionId(1, 1)).toOption.get
      assert(!(next.service.toOption.get eq service))
      next.close()
    }
  }

  test("failure notifying one closed client does not skip other clients or leave leases live") {
    val notifications = java.util.concurrent.atomic.AtomicInteger()
    val clients       = Clients(
      () => ExecutionSession(Nil),
      _ => {
        notifications.incrementAndGet()
        throw IllegalStateException("notification failed")
      }
    )
    val ids     = List(Clients.Root, clients.register(), clients.register())
    val leases  = ids.map(id => clients.acquire(id, SessionId(1, 1)).toOption.get)
    val failure = intercept[IllegalStateException](clients.close())
    assertEquals(notifications.get(), 3)
    assertEquals(failure.getSuppressed.length, 2)
    assert(leases.forall(_.service.isLeft))
    assert(ids.forall(id => clients.acquire(id, SessionId(2, 1)).isLeft))
    leases.foreach(_.close())
    clients.close()
  }

  test("a slow client cleanup does not hold the registry lock") {
    val entered = CountDownLatch(1)
    val finish  = CountDownLatch(1)
    val clients = Clients(
      () => ExecutionSession(Nil),
      _ => {
        entered.countDown()
        if (!finish.await(5, TimeUnit.SECONDS)) throw IllegalStateException("cleanup was not released")
      }
    )
    val id      = clients.register()
    val cleanup = Thread(() => clients.release(id))
    cleanup.start()
    try {
      assert(entered.await(2, TimeUnit.SECONDS))
      val acquired = java.util.concurrent.atomic.AtomicBoolean()
      val other    = Thread { () =>
        val _ = clients.register(); acquired.set(true)
      }
      other.start()
      other.join(1000)
      assert(acquired.get(), "registry access waited for resource cleanup")
    } finally {
      finish.countDown()
      cleanup.join(5000)
      clients.close()
    }
  }

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
    class WaitingProgram extends RuntimeBackend.Program {
      override def symbols: Either[ExecutionFailure, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
      override def kernel(name: NonEmpty[String]): Either[ExecutionFailure, KernelMetadata] =
        KernelMetadata.validated(0, 1, Vec.empty).left.map(ExecutionFailure.InvalidRequest(_))
      override def execute(
          task: KernelTask,
          memory: Memory,
          control: TaskControl,
          handler: Event => Either[Fault, Unit]
      ): Either[ExecutionFailure, Unit] = {
        started.countDown()
        val deadline = platform.monotonicNanos + TimeUnit.SECONDS.toNanos(5)
        try {
          while (!control.isCancelled && platform.monotonicNanos < deadline) Thread.sleep(1)
          Left(ExecutionFailure.Failed("stopped"))
        } finally if (control.isCancelled) cancelled.countDown()
      }
    }
    class WaitingBackend extends RuntimeBackend {
      override val encoding                                                  = "waiting"
      override def load(image: Vec[Byte]): Either[ExecutionFailure, Program] = Right(WaitingProgram())
    }
    Using.resource(Server.start(Server.Settings(parallelism = 1), _ => Backends(List(WaitingBackend())))) { server =>
      Using.resource(connections.Client(server.endpoint, server.client)) { client =>
        val program    = client.call(Operation.LoadProgram("waiting", Vec.empty)).toOption.get
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
          try { val _ = client.call(Operation.AwaitCompletion(completion)) }
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
