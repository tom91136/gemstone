package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Constraint.NonEmptyString as ISAKernelName

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.isa.{KernelMetadata as ISAKernelMetadata, ProgramSymbols}
import net.kurobako.gemstone.memory.{AllocationKind, GuestAddress}
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import java.util.concurrent.{CountDownLatch, FutureTask, TimeUnit}
import scala.util.Using

final class ExecutionServiceSuite extends munit.FunSuite {
  private val encoding = "test-image"

  private final class Backend extends RuntimeBackend {
    val programClosed             = AtomicBoolean()
    override val encoding: String = ExecutionServiceSuite.this.encoding

    override def load(
        services: RuntimeBackend.Services,
        image: Vec[Byte]
    ): Either[ExecutionFailure, RuntimeBackend.Program] = Right(Program(services, programClosed))
  }

  private class Program(services: RuntimeBackend.Services, closed: AtomicBoolean) extends RuntimeBackend.Program {
    override def symbols: Either[ExecutionFailure, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
    override def kernel(name: ISAKernelName): Either[ExecutionFailure, ISAKernelMetadata] =
      ISAKernelMetadata.validated(
        8,
        1,
        Vec(
          ISAKernelMetadata.Parameter(
            8,
            0,
            ISAKernelMetadata.ParameterKind.Allocation,
            false
          )
        )
      ).left.map(ExecutionFailure.InvalidRequest(_))
    override val globals: Option[RuntimeBackend.Program.Globals] = None

    override def execute(
        task: KernelTask,
        memory: Memory,
        control: TaskControl
    ): Either[ExecutionFailure, Result] =
      task.arguments.headOption match {
        case Some(KernelTask.Argument.Memory(region, offset)) =>
          for {
            _ <- memory.write(region, offset, Vec[Byte](42)).left.map(ExecutionFailure.fromFault(_))
            _ <- services.observer.observe(
              control.observation,
              Event.Print("done %d", Vec(Event.PrintArgument.Integer(Vec(42L), 32)))
            ).left.map(error => ExecutionFailure.fromFault(Fault.Observer(error.message)))
          } yield Result(control.usedInstructions)
        case _ => Left(ExecutionFailure.fromFault(Fault.InvalidTask("memory argument is required")))
      }

    override def close(): Unit = closed.set(true)
  }

  test("service translates API ownership into a VM task and records VM events") {
    val backend = Backend()
    Using.resource(ExecutionService(List(backend))) { service =>
      val allocation = service.registerAllocation(8L, AllocationKind.Shared).toOption.get
      val program    = service.load(encoding, Vec[Byte](1)).toOption.get
      val task       = KernelTask.validated(
        ISAKernelName("write"),
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(MemoryRegion(allocation.raw), 2L))
      ).toOption.get
      val completion = service.launch(program, task).toOption.get

      assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
      assertEquals(service.read(allocation, 2L, 1), Right(Vec[Byte](42)))
      assertEquals(service.completionPrints(completion), Right("done 42"))
      assertEquals(
        service.completionDirtyRanges(completion),
        Right(Vec(DirtyRange(allocation, 2L, 1)))
      )
    }
    assert(backend.programClosed.get())
  }

  test("acknowledged host fences wake polling reads from another worker") {
    Using.resource(WorkerPool(2)) { workers =>
      final class FenceBackend extends RuntimeBackend {
        override val encoding: String                                          = ExecutionServiceSuite.this.encoding
        override def load(services: RuntimeBackend.Services, image: Vec[Byte]) =
          Right(ReadFenceProgram(services))
        private final class ReadFenceProgram(services: RuntimeBackend.Services)
            extends Program(services, AtomicBoolean()) {
          override def execute(task: KernelTask, memory: Memory, control: TaskControl) = {
            val staged = CountDownLatch(1)
            services.scheduler.parallelFor(2L) { lane =>
              if (lane.raw == 0) {
                assertEquals(
                  memory.read(MemoryRegion(0), NonNegative.unsafe(0x1000L), 4),
                  Left(Fault.PendingMemory)
                )
                staged.countDown()
                memory.flushHostReads()
              } else {
                assert(staged.await(5, TimeUnit.SECONDS))
                memory.fenceHostMemory()
              }
            }.left.map(ExecutionFailure.fromFault(_)).map(_ => Result(control.usedInstructions))
          }
        }
      }
      Using.resource(ExecutionService(List(FenceBackend()), scheduler = workers)) { service =>
        assertEquals(service.enableHostMemory(), Right(()))
        val program = service.load(encoding, Vec[Byte](1)).toOption.get
        val task    = KernelTask.validated(
          ISAKernelName("read-and-fence"),
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec.empty
        ).toOption.get
        val pump = FutureTask[Unit](() =>
          (0 until 512).foreach { _ =>
            val completion   = service.launch(program, task).toOption.get
            def poll(): Unit = {
              val batch = service.pollHostMemory(completion).fold(error => fail(error.message), identity)
              batch.sync.foreach(sync => assertEquals(service.supplyHostSync(completion, sync.ticket, 0L), Right(())))
              if (batch.reads.nonEmpty)
                assertEquals(
                  service.supplyHostMemory(
                    completion,
                    batch.reads.map(range =>
                      HostMemoryBytes(range.address, Vec.fill(range.size.raw)(1.toByte))
                    )
                  ),
                  Right(())
                )
              if (!batch.completed) poll()
            }
            poll()
            assertEquals(service.releaseCompletion(completion), Right(()))
          }
        )
        val thread = Thread(pump, "host-fence-read-regression")
        thread.setDaemon(true)
        thread.start()
        try pump.get(15, TimeUnit.SECONDS)
        finally {
          service.close()
          thread.join(5000)
        }
      }
    }
  }

  for (sync <- List(false, true)) test(s"closing a session cancels pending host memory and queued work, fence=$sync") {
    val finished = CountDownLatch(1)
    val disposed = CountDownLatch(1)
    final class WaitingProgram(services: RuntimeBackend.Services) extends Program(services, AtomicBoolean()) {
      override def execute(task: KernelTask, memory: Memory, control: TaskControl) =
        try {
          val result = if (sync) memory.fenceHostMemory()
          else {
            assertEquals(
              memory.read(MemoryRegion(0), NonNegative.unsafe(0x1000L), 4),
              Left(Fault.PendingMemory)
            )
            memory.flushHostReads()
          }
          result.left.map(ExecutionFailure.fromFault(_)).map(_ => Result(control.usedInstructions))
        } finally finished.countDown()
      override def close(): Unit = disposed.countDown()
    }
    final class WaitingBackend extends RuntimeBackend {
      override val encoding: String                                          = ExecutionServiceSuite.this.encoding
      override def load(services: RuntimeBackend.Services, image: Vec[Byte]) = Right(WaitingProgram(services))
    }
    Using.resource(WorkerPool(1)) { workers =>
      Using.resource(ExecutionService(List(WaitingBackend()), scheduler = workers)) { service =>
        assertEquals(service.enableHostMemory(), Right(()))
        val allocation = service.registerAllocation(8L, AllocationKind.Shared).toOption.get
        val program    = service.load(encoding, Vec[Byte](1)).toOption.get
        val task       = KernelTask.validated(
          ISAKernelName("wait"),
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(KernelTask.Argument.Memory(MemoryRegion(allocation.raw), 0L))
        ).toOption.get
        val first = service.launch(program, task).toOption.get
        val batch = service.pollHostMemory(first).toOption.get
        assertEquals(batch.sync.nonEmpty, sync)
        assertEquals(batch.reads.nonEmpty, !sync)
        val queued = service.launch(program, task).toOption.get
        assert(service.releaseAllocation(allocation).isLeft)
        assert(service.unload(program).isLeft)
        assert(service.releaseCompletion(first).isLeft)
        service.close()
        assert(finished.await(5, TimeUnit.SECONDS), "host-memory waiter survived session close")
        assert(disposed.await(5, TimeUnit.SECONDS), "program outlived cancelled dispatches")
        assert(service.awaitCompletion(first).isLeft)
        assert(service.awaitCompletion(queued).isLeft)
        assert(service.supplyHostMemory(first, Vec.empty).isLeft)
      }
    }
  }

  test("independent dispatches can rendezvous before either completes") {
    val entered = CountDownLatch(2)
    val release = CountDownLatch(1)
    final class RendezvousBackend extends RuntimeBackend {
      override val encoding: String                                          = ExecutionServiceSuite.this.encoding
      override def load(services: RuntimeBackend.Services, image: Vec[Byte]) = Right(RendezvousProgram(services))
      private final class RendezvousProgram(services: RuntimeBackend.Services)
          extends Program(services, AtomicBoolean()) {
        override def execute(task: KernelTask, memory: Memory, control: TaskControl) = {
          entered.countDown()
          val released = release.await(5, TimeUnit.SECONDS)
          if (released) Right(Result(control.usedInstructions))
          else Left(ExecutionFailure.Failed("rendezvous timed out"))
        }
      }
    }
    Using.resource(WorkerPool(2)) { workers =>
      Using.resource(ExecutionService(List(RendezvousBackend()), scheduler = workers)) { service =>
        val program = service.load(encoding, Vec[Byte](1)).toOption.get
        val task    = KernelTask.validated(
          ISAKernelName("rendezvous"),
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec.empty
        ).toOption.get
        val first      = service.launch(program, task).toOption.get
        val second     = service.launch(program, task).toOption.get
        val concurrent = entered.await(1, TimeUnit.SECONDS)
        val retained   = service.unload(program).isLeft
        release.countDown()
        assertEquals(service.awaitCompletion(first), Right(()))
        assertEquals(service.awaitCompletion(second), Right(()))
        assert(retained, "program retired while dispatches were running")
        assert(concurrent, "one dispatch prevented the other from entering execution")
      }
    }
  }

  test("overlapping dispatches keep host read replies and cached bytes separate") {
    val firstRead = CountDownLatch(1)
    val release   = CountDownLatch(1)
    val observed  = java.util.concurrent.ConcurrentHashMap[Long, Byte]()
    final class IsolatedBackend extends RuntimeBackend {
      override val encoding: String                                          = ExecutionServiceSuite.this.encoding
      override def load(services: RuntimeBackend.Services, image: Vec[Byte]) = Right(IsolatedProgram(services))
      private final class IsolatedProgram(services: RuntimeBackend.Services)
          extends Program(services, AtomicBoolean()) {
        override def execute(task: KernelTask, memory: Memory, control: TaskControl) = {
          val staged  = memory.read(MemoryRegion(0), NonNegative.unsafe(0x1000L), 1)
          val flushed = if (staged == Left(Fault.PendingMemory)) memory.flushHostReads() else staged.map(_ => ())
          flushed.flatMap { _ =>
            firstRead.countDown()
            if (!release.await(5, TimeUnit.SECONDS)) Left(Fault.Cancelled)
            else memory.read(MemoryRegion(0), NonNegative.unsafe(0x1000L), 1).map { bytes =>
              val _ = observed.put(control.observation.value, bytes.head)
              Result(control.usedInstructions)
            }
          }.left.map(ExecutionFailure.fromFault(_))
        }
      }
    }
    Using.resource(WorkerPool(2)) { workers =>
      Using.resource(ExecutionService(List(IsolatedBackend()), scheduler = workers)) { service =>
        assertEquals(service.enableHostMemory(), Right(()))
        val program = service.load(encoding, Vec[Byte](1)).toOption.get
        val task    = KernelTask.validated(
          ISAKernelName("isolate"),
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec.empty
        ).toOption.get
        val first   = service.launch(program, task).toOption.get
        val initial = service.pollHostMemory(first).toOption.get
        assertEquals(initial.reads.size, 1)
        assertEquals(
          service.supplyHostMemory(first, Vec(HostMemoryBytes(initial.reads.head.address, Vec[Byte](11)))),
          Right(())
        )
        assert(firstRead.await(2, TimeUnit.SECONDS))
        val second  = service.launch(program, task).toOption.get
        val polling = FutureTask(() => service.pollHostMemory(second))
        val pump    = Thread(polling, "gemstone-isolation-test")
        pump.start()
        try {
          val batch = polling.get(2, TimeUnit.SECONDS).toOption.get
          assertEquals(batch.reads.size, 1)
          assertEquals(
            service.supplyHostMemory(second, Vec(HostMemoryBytes(batch.reads.head.address, Vec[Byte](22)))),
            Right(())
          )
        } finally release.countDown()
        assertEquals(service.awaitCompletion(first), Right(()))
        assertEquals(service.awaitCompletion(second), Right(()))
        pump.join(5000)
        assertEquals(observed.get(first.raw), 11.toByte)
        assertEquals(observed.get(second.raw), 22.toByte)
      }
    }
  }

  test("pointer-bearing scalar arguments are relocated from registered allocation addresses") {
    val captured = AtomicReference(Vec.empty[KernelTask.PointerRelocation])
    final class RelocationBackend extends RuntimeBackend {
      override val encoding: String                                          = ExecutionServiceSuite.this.encoding
      override def load(services: RuntimeBackend.Services, image: Vec[Byte]) = Right(RelocationProgram(services))
      private final class RelocationProgram(services: RuntimeBackend.Services) extends RuntimeBackend.Program {
        override def symbols: Either[ExecutionFailure, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
        override def kernel(name: ISAKernelName): Either[ExecutionFailure, ISAKernelMetadata] =
          ISAKernelMetadata.validated(
            8,
            1,
            Vec(ISAKernelMetadata.Parameter(
              8,
              0,
              ISAKernelMetadata.ParameterKind.Scalar,
              containsPointers = true
            ))
          ).left.map(ExecutionFailure.InvalidRequest(_))
        override val globals: Option[RuntimeBackend.Program.Globals]                 = None
        override def execute(task: KernelTask, memory: Memory, control: TaskControl) = {
          captured.set(task.pointerRelocations)
          Right(Result(control.usedInstructions))
        }
        override def close(): Unit = ()
      }
    }
    Using.resource(ExecutionService(List(RelocationBackend()))) { service =>
      val target = service.registerAllocation(64L, AllocationKind.Device).toOption.get
      assertEquals(service.registerAllocationAddress(target, GuestAddress(0x1000L)), Right(()))
      val program = service.load(encoding, Vec[Byte](1)).toOption.get
      val scalar  = Vec.from(LittleEndian.toVec(0x1008L, 8))
      val task    = KernelTask.validated(
        ISAKernelName("relocate"),
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Scalar(scalar)),
        discoverArgumentRelocations = true
      ).toOption.get
      val completion = service.launch(program, task).toOption.get
      assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
      assertEquals(
        captured.get(),
        Vec(KernelTask.PointerRelocation(
          KernelTask.RelocationSource.Argument(0),
          0L,
          MemoryRegion(target.raw),
          8L
        ))
      )
    }
  }

  test("unknown encodings fail without invoking a backend") {
    val backend = Backend()
    Using.resource(ExecutionService(List(backend))) { service =>
      val unknown = "unknown-image"
      assert(service.load(unknown, Vec.empty).left.exists {
        case ExecutionFailure.Unsupported(_) => true
        case _                               => false
      })
    }
    assert(!backend.programClosed.get())
  }
}
