package net.kurobako.gemstone.rpc

import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.given
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class ExecutionSessionSuite extends munit.FunSuite {
  private def addressSpace(memory: Memory): AddressSpace = memory match {
    case addresses: AddressSpace => addresses
    case _                       => fail("execution memory must expose its address space")
  }

  private val encoding = "test-image"

  private class Backend extends RuntimeBackend {
    val programClosed: AtomicBoolean = AtomicBoolean()
    override val encoding: String    = ExecutionSessionSuite.this.encoding

    override def load(
        image: Vec[Byte]
    ): Either[ExecutionFailure, RuntimeBackend.Program] = Right(Program(programClosed))
  }

  private class Program(closed: AtomicBoolean) extends RuntimeBackend.Program {
    override def symbols: Either[ExecutionFailure, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
    override def kernel(name: NonEmpty[String]): Either[ExecutionFailure, KernelMetadata] =
      KernelMetadata.validated(
        8,
        1,
        Vec(
          KernelMetadata.Parameter(
            8,
            0,
            KernelMetadata.ParameterKind.Allocation,
            false
          )
        )
      ).left.map(ExecutionFailure.InvalidRequest(_))

    override def execute(
        task: KernelTask,
        memory: Memory,
        control: TaskControl,
        handler: Event => Either[Fault, Unit]
    ): Either[ExecutionFailure, Unit] =
      task.arguments.headOption match {
        case Some(KernelTask.Argument.Memory(region, offset, _)) =>
          for {
            _ <- memory.write(region, offset, Vec[Byte](42)).left.map(ExecutionFailure.fromFault(_))
            _ <- handler(
              Event.Print("done %d", Vec(Event.PrintArgument.Integer(Vec(42L), 32)))
            ).left.map(ExecutionFailure.fromFault(_))
          } yield ()
        case _ => Left(ExecutionFailure.fromFault(Fault.InvalidTask("memory argument is required")))
      }

    override def close(): Unit = closed.set(true)
  }

  test("an atomic update does not block writes to an unrelated allocation") {
    Using.resource(ExecutionSession(List(Backend()))) { service =>
      val first   = service.registerAllocation(8L, AllocationKind.Device).toOption.get
      val second  = service.registerAllocation(8L, AllocationKind.Device).toOption.get
      val entered = CountDownLatch(1)
      val release = CountDownLatch(1)
      val written = CountDownLatch(1)
      val atomic  = FutureTask[Either[ExecutionFailure, Long]](() =>
        service.atomicModifyValue(first, 0L, 8) { previous =>
          entered.countDown()
          assert(release.await(5L, TimeUnit.SECONDS))
          previous + 1L
        }
      )
      val writer = FutureTask[Either[ExecutionFailure, Unit]] { () =>
        val result = service.writeValue(second, 0L, 42L, 8)
        written.countDown()
        result
      }
      val atomicThread = Thread(atomic, "paused-allocation-atomic")
      val writeThread  = Thread(writer, "unrelated-allocation-write")
      atomicThread.start()
      val independent = try {
        assert(entered.await(5L, TimeUnit.SECONDS))
        writeThread.start()
        written.await(1L, TimeUnit.SECONDS)
      } finally release.countDown()
      assertEquals(atomic.get(5L, TimeUnit.SECONDS), Right(0L))
      assertEquals(writer.get(5L, TimeUnit.SECONDS), Right(()))
      atomicThread.join()
      writeThread.join()
      assertEquals(service.readValue(second, 0L, 8), Right(42L))
      assert(independent, "one allocation's atomic update held the session lock across an unrelated write")
    }
  }

  test("an atomic update does not block a disjoint range in the same allocation") {
    Using.resource(ExecutionSession(List(Backend()))) { service =>
      val first   = service.registerAllocation(256L, AllocationKind.Device).toOption.get
      val second  = first
      val entered = CountDownLatch(1)
      val release = CountDownLatch(1)
      val written = CountDownLatch(1)
      val atomic  = FutureTask[Either[ExecutionFailure, Long]](() =>
        service.atomicModifyValue(first, 0L, 8) { previous =>
          entered.countDown()
          assert(release.await(5L, TimeUnit.SECONDS))
          previous + 1L
        }
      )
      val writer = FutureTask[Either[ExecutionFailure, Unit]] { () =>
        val result = service.writeValue(second, 128L, 42L, 8)
        written.countDown()
        result
      }
      val atomicThread = Thread(atomic, "paused-allocation-atomic")
      val writeThread  = Thread(writer, "unrelated-allocation-write")
      atomicThread.start()
      val independent = try {
        assert(entered.await(5L, TimeUnit.SECONDS))
        writeThread.start()
        written.await(1L, TimeUnit.SECONDS)
      } finally release.countDown()
      assertEquals(atomic.get(5L, TimeUnit.SECONDS), Right(0L))
      assertEquals(writer.get(5L, TimeUnit.SECONDS), Right(()))
      atomicThread.join()
      writeThread.join()
      assertEquals(service.readValue(second, 128L, 8), Right(42L))
      assert(independent, "one allocation's atomic update held the session lock across an unrelated write")
    }
  }

  test("service translates API ownership into a VM task and records VM events") {
    val backend = Backend()
    Using.resource(ExecutionSession(List(backend))) { service =>
      val allocation = service.registerAllocation(8L, AllocationKind.Shared).toOption.get
      val program    = service.load(encoding, Vec[Byte](1)).toOption.get
      val task       = KernelTask.validated(
        "write",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 2L))
      ).toOption.get
      val completion = service.launch(program, task).toOption.get

      assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
      assertEquals(service.read(allocation, 2L, 1), Right(Vec[Byte](42)))
      assertEquals(service.completionPrints(completion), Right("done 42"))
      assertEquals(
        service.completionDirtyRanges(completion, 0),
        Right(Vec(DirtyRange(allocation, 2L, 1)))
      )
    }
    assert(backend.programClosed.get())
  }

  test("sparse writeback pages preserve every written byte and exclude unwritten gaps") {
    val count = ExecutionRPC.MaximumElements.raw + 3
    class SparseBackend extends Backend {
      override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] =
        Right(SparseProgram())
      private class SparseProgram extends Program(AtomicBoolean()) {
        override def execute(
            task: KernelTask,
            memory: Memory,
            control: TaskControl,
            handler: Event => Either[Fault, Unit]
        ): Either[ExecutionFailure, Unit] = task.arguments.headOption match {
          case Some(KernelTask.Argument.Memory(region, _, _)) =>
            (0 until count).foldLeft[Either[ExecutionFailure, Unit]](Right(())) { (result, index) =>
              result.flatMap(_ =>
                memory.write(region, (index.toLong * 2).unsafe[Long >= 0], Vec[Byte](42))
                  .left.map(ExecutionFailure.fromFault(_))
              )
            }
          case _ => Left(ExecutionFailure.InvalidRequest("memory argument is required"))
        }
      }
    }
    Using.resource(ExecutionSession(List(SparseBackend()))) { service =>
      val allocation =
        service.registerAllocation((count.toLong * 2).unsafe[Long > 0], AllocationKind.Device).toOption.get
      val program = service.load(encoding, Vec[Byte](1)).toOption.get
      val task    = KernelTask.validated(
        "write",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      ).toOption.get
      val completion = service.launch(program, task).toOption.get
      assertEquals(service.awaitCompletion(completion), Right(()))
      val first  = service.completionDirtyRanges(completion, 0).toOption.get
      val second = service.completionDirtyRanges(completion, ExecutionRPC.MaximumElements).toOption.get
      assertEquals(first.length, ExecutionRPC.MaximumElements.raw)
      assertEquals(second.length, 3)
      assertEquals(service.completionDirtyRanges(completion, 0), Right(first))
      assertEquals(service.completionDirtyRanges(completion, count.unsafe[Int >= 0]), Right(Vec.empty[DirtyRange]))
      assertEquals(service.completionDirtyRanges(completion, Int.MaxValue), Right(Vec.empty[DirtyRange]))
      val ranges = first ++ second
      assertEquals(ranges.map(_.offset.raw), Vec.from(0 until count).map(_.toLong * 2))
      assert(ranges.forall(range => range.allocation == allocation && range.size.raw == 1))
      for (page <- Vec(first, second)) {
        val operation = ExecutionRPC.Operation.CompletionDirtyRanges(completion, 0)
        assert(operation.replyCodec.encode(page, ExecutionRPC.Limits).isRight)
      }
      val bytes = service.read(allocation, 0L, (count * 2).unsafe[Int >= 0]).toOption.get
      assertEquals(bytes, Vec.from(0 until count * 2).map(index => if (index % 2 == 0) 42.toByte else 0.toByte))
      assertEquals(service.releaseCompletion(completion), Right(()))
      assert(service.completionDirtyRanges(completion, 0).isLeft)
    }
  }

  test("parallel sparse writes and cross-stripe atomics retain exact writeback") {
    Using.resource(WorkerPool(4)) { workers =>
      class ParallelBackend extends Backend {
        override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] = Right(ParallelProgram())
        private class ParallelProgram extends Program(AtomicBoolean()) {
          override def execute(
              task: KernelTask,
              memory: Memory,
              control: TaskControl,
              handler: Event => Either[Fault, Unit]
          ): Either[ExecutionFailure, Unit] = {
            val KernelTask.Argument.Memory(region, _, _) = task.arguments.head: @unchecked
            workers.parallelFor(128L) { index =>
              val offset = ((index.raw * 31L % 128L) * 16L).unsafe[Long >= 0]
              for {
                _ <- memory.writeValue(region, offset, index.raw + 1L, 8)
                _ <- memory.atomicModifyValue(region, 4095L, 8)(_ + 1L)
              } yield ()
            }.left.map(ExecutionFailure.fromFault(_))
          }
        }
      }
      Using.resource(ExecutionSession(List(ParallelBackend()))) { service =>
        val allocation = service.registerAllocation(8192L, AllocationKind.Device).toOption.get
        val program    = service.load(encoding, Vec[Byte](1)).toOption.get
        val task       = KernelTask.validated(
          "parallel",
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(KernelTask.Argument.Memory(allocation, 0L))
        ).toOption.get
        val completion = service.launch(program, task).toOption.get
        assertEquals(service.awaitCompletion(completion), Right(()))
        assertEquals(service.readValue(allocation, 4095L, 8), Right(128L))
        val expected =
          Vec.from(0 until 128).map(index => DirtyRange(allocation, (index.toLong * 16L).unsafe[Long >= 0], 8))
            .appended(DirtyRange(allocation, 4095L, 8))
        assertEquals(service.completionDirtyRanges(completion, 0), Right(expected))
        (0 until 128).foreach { index =>
          val offset = ((index.toLong * 31L % 128L) * 16L).unsafe[Long >= 0]
          assertEquals(service.readValue(allocation, offset, 8), Right(index.toLong + 1L))
          assertEquals(service.readValue(allocation, (offset.raw + 8L).unsafe[Long >= 0], 8), Right(0L))
        }
      }
    }
  }

  test("pointer relocation metadata is paged without losing identities or offsets") {
    val count = ExecutionRPC.MaximumElements.raw + 1
    class RelocationBackend extends Backend {
      override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] = Right(RelocationProgram())
      private class RelocationProgram extends Program(AtomicBoolean()) {
        override def execute(
            task: KernelTask,
            memory: Memory,
            control: TaskControl,
            handler: Event => Either[Fault, Unit]
        ): Either[ExecutionFailure, Unit] = {
          val KernelTask.Argument.Memory(region, _, _) = task.arguments.head: @unchecked
          (0 until count).foldLeft[Either[ExecutionFailure, Unit]](Right(())) { (result, index) =>
            result.flatMap(_ =>
              handler(Event.PointerRelocation(region, (index.toLong * 16).unsafe[Long >= 0], region, 0L))
                .left.map(ExecutionFailure.fromFault(_))
            )
          }
        }
      }
    }
    Using.resource(ExecutionSession(List(RelocationBackend()))) { session =>
      val allocation =
        session.registerAllocation((count.toLong * 16).unsafe[Long > 0], AllocationKind.Shared).toOption.get
      val program = session.load(encoding, Vec[Byte](1)).toOption.get
      val task    = KernelTask.validated(
        "write",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      ).toOption.get
      val completion = session.launch(program, task).toOption.get
      assertEquals(session.awaitCompletion(completion), Right(()))
      val first  = session.completionPointerRelocations(completion, 0).toOption.get
      val second = session.completionPointerRelocations(completion, ExecutionRPC.MaximumElements).toOption.get
      assertEquals(first.size, ExecutionRPC.MaximumElements.raw)
      assertEquals(second.size, 1)
      assertEquals(session.completionPointerRelocations(completion, 0), Right(first))
      assertEquals((first ++ second).map(_.sourceOffset.raw), Vec.tabulate(count)(_.toLong * 16))
      assert((first ++ second).forall(value =>
        value.source == KernelTask.RelocationSource.Memory(allocation) &&
          value.target == allocation && value.targetOffset.raw == 0L
      ))
      for (page <- Vec(first, second))
        assert(ExecutionRPC.Operation.CompletionPointerRelocations(
          completion,
          0
        ).replyCodec.encode(page, ExecutionRPC.Limits).isRight)
      assertEquals(
        session.completionPointerRelocations(completion, count.unsafe[Int >= 0]),
        Right(Vec.empty[KernelTask.PointerRelocation])
      )
      assertEquals(
        session.completionPointerRelocations(completion, Int.MaxValue),
        Right(Vec.empty[KernelTask.PointerRelocation])
      )
      assertEquals(session.releaseCompletion(completion), Right(()))
      assert(session.completionPointerRelocations(completion, 0).isLeft)
    }
  }

  test("unloading retains bound allocations until backend close finishes, including failed close") {
    for (failClose <- List(false, true)) {
      val entered = CountDownLatch(1)
      val release = CountDownLatch(1)
      class ClosingBackend extends RuntimeBackend {
        override val encoding: String = ExecutionSessionSuite.this.encoding
        override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] = Right(ClosingProgram())
        private class ClosingProgram extends Program(AtomicBoolean()) {
          override def bindGlobal(
              name: NonEmpty[String],
              region: AllocationId,
              offset: Long >= 0,
              regionSize: Long > 0,
              memory: Memory
          ): Either[ExecutionFailure, Unit] = Right(())
          override def close(): Unit        = {
            entered.countDown()
            if (!release.await(5, TimeUnit.SECONDS)) throw IllegalStateException("backend close was not released")
            if (failClose) throw IllegalStateException("close failed")
          }
        }
      }
      Using.resource(ExecutionSession(List(ClosingBackend()))) { session =>
        val allocation = session.registerAllocation(8L, AllocationKind.Shared).toOption.get
        val program    = session.load(encoding, Vec[Byte](1)).toOption.get
        assertEquals(session.bindGlobal(program, "global", allocation, 0L), Right(()))
        val unloading = FutureTask(() => scala.util.Try(session.unload(program)))
        val worker    = Thread(unloading)
        worker.start()
        try {
          val started = entered.await(5, TimeUnit.SECONDS)
          assert(started, "backend close did not start")
          assert(session.symbols(program).isLeft, "unloading program remained visible")
          assert(session.releaseAllocation(allocation).isLeft, "allocation escaped backend close")
        } finally release.countDown()
        val result = unloading.get(5, TimeUnit.SECONDS)
        assertEquals(result.isFailure, failClose, result.toString)
        if (!failClose) assertEquals(result.get, Right(()))
        assertEquals(session.releaseAllocation(allocation), Right(()))
      }
    }
  }

  test("acknowledged host fences wake polling reads from another worker") {
    Using.resource(WorkerPool(2)) { workers =>
      class FenceBackend extends RuntimeBackend {
        override val encoding: String = ExecutionSessionSuite.this.encoding
        override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] =
          Right(ReadFenceProgram())
        private class ReadFenceProgram
            extends Program(AtomicBoolean()) {
          override def execute(
              task: KernelTask,
              memory: Memory,
              control: TaskControl,
              handler: Event => Either[Fault, Unit]
          ): Either[ExecutionFailure, Unit] = {
            val staged = CountDownLatch(1)
            workers.parallelFor(2L) { lane =>
              if (lane.raw == 0) {
                assertEquals(
                  addressSpace(memory).readDeferred(GuestAddress(0x1000L), 4),
                  Left(Memory.Pending)
                )
                staged.countDown()
                memory.flushHostReads()
              } else {
                assert(staged.await(5, TimeUnit.SECONDS))
                memory.fenceHostMemory()
              }
            }.left.map(ExecutionFailure.fromFault(_)).map(_ => ())
          }
        }
      }
      Using.resource(ExecutionSession(
        List(FenceBackend()),
        executionContext = workers,
        parallelism = workers.parallelism
      )) { service =>
        assertEquals(service.enableHostMemory(), Right(()))
        val program = service.load(encoding, Vec[Byte](1)).toOption.get
        val task    = KernelTask.validated(
          "read-and-fence",
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec.empty
        ).toOption.get
        val pump = FutureTask[Unit](() =>
          (0 until 512).foreach { _ =>
            val completion = service.launch(program, task).toOption.get
            @annotation.tailrec
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
    class WaitingProgram extends Program(AtomicBoolean()) {
      override def execute(
          task: KernelTask,
          memory: Memory,
          control: TaskControl,
          handler: Event => Either[Fault, Unit]
      ): Either[ExecutionFailure, Unit] =
        try {
          val result = if (sync) memory.fenceHostMemory()
          else {
            assertEquals(
              addressSpace(memory).readDeferred(GuestAddress(0x1000L), 4),
              Left(Memory.Pending)
            )
            memory.flushHostReads()
          }
          result.left.map(ExecutionFailure.fromFault(_)).map(_ => ())
        } finally finished.countDown()
      override def close(): Unit = disposed.countDown()
    }
    class WaitingBackend extends RuntimeBackend {
      override val encoding: String = ExecutionSessionSuite.this.encoding
      override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] = Right(WaitingProgram())
    }
    Using.resource(WorkerPool(1)) { workers =>
      Using.resource(ExecutionSession(
        List(WaitingBackend()),
        executionContext = workers,
        parallelism = workers.parallelism
      )) { service =>
        assertEquals(service.enableHostMemory(), Right(()))
        val allocation = service.registerAllocation(8L, AllocationKind.Shared).toOption.get
        val program    = service.load(encoding, Vec[Byte](1)).toOption.get
        val task       = KernelTask.validated(
          "wait",
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(KernelTask.Argument.Memory(allocation, 0L))
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

  test("a dispatch retains its arguments while unrelated address registrations can be retired and reused") {
    val entered = CountDownLatch(1)
    val release = CountDownLatch(1)
    class BlockingBackend extends Backend {
      override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] = Right(BlockingProgram())
      private class BlockingProgram extends Program(AtomicBoolean()) {
        override def execute(
            task: KernelTask,
            memory: Memory,
            control: TaskControl,
            handler: Event => Either[Fault, Unit]
        ): Either[ExecutionFailure, Unit] = {
          entered.countDown()
          if (release.await(5, TimeUnit.SECONDS)) super.execute(task, memory, control, handler)
          else Left(ExecutionFailure.Failed("dispatch release timed out"))
        }
      }
    }
    Using.resource(WorkerPool(1)) { workers =>
      Using.resource(ExecutionSession(
        List(BlockingBackend()),
        executionContext = workers,
        parallelism = workers.parallelism
      )) { service =>
        val used    = service.registerAllocation(8L, AllocationKind.Device).toOption.get
        val unused  = service.registerAllocation(8L, AllocationKind.Device).toOption.get
        val address = GuestAddress(4096L)
        assertEquals(service.registerAllocationAddress(unused, address), Right(()))
        val program = service.load(encoding, Vec[Byte](1)).toOption.get
        val task    = KernelTask.validated(
          "write",
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(KernelTask.Argument.Memory(used, 0L))
        ).toOption.get
        val completion = service.launch(program, task).toOption.get
        try {
          assert(entered.await(5, TimeUnit.SECONDS))
          assert(service.releaseAllocation(used).isLeft)
          assertEquals(service.releaseAllocation(unused), Right(()))
          val replacement = service.registerAllocation(8L, AllocationKind.Device).toOption.get
          assertEquals(service.registerAllocationAddress(replacement, address), Right(()))
        } finally release.countDown()
        assertEquals(service.awaitCompletion(completion), Right(()))
        assertEquals(service.read(used, 0L, 1), Right(Vec[Byte](42)))
        assertEquals(service.releaseCompletion(completion), Right(()))
        assertEquals(service.releaseAllocation(used), Right(()))
      }
    }
  }

  test("independent dispatches can rendezvous before either completes") {
    val entered = CountDownLatch(2)
    val release = CountDownLatch(1)
    class RendezvousBackend extends RuntimeBackend {
      override val encoding: String = ExecutionSessionSuite.this.encoding
      override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] = Right(RendezvousProgram())
      private class RendezvousProgram
          extends Program(AtomicBoolean()) {
        override def execute(
            task: KernelTask,
            memory: Memory,
            control: TaskControl,
            handler: Event => Either[Fault, Unit]
        ): Either[ExecutionFailure, Unit] = {
          entered.countDown()
          val released = release.await(5, TimeUnit.SECONDS)
          if (released) Right(())
          else Left(ExecutionFailure.Failed("rendezvous timed out"))
        }
      }
    }
    Using.resource(WorkerPool(2)) { workers =>
      Using.resource(ExecutionSession(
        List(RendezvousBackend()),
        executionContext = workers,
        parallelism = workers.parallelism
      )) { service =>
        val program = service.load(encoding, Vec[Byte](1)).toOption.get
        val task    = KernelTask.validated(
          "rendezvous",
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
    class IsolatedBackend extends RuntimeBackend {
      override val encoding: String = ExecutionSessionSuite.this.encoding
      override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] = Right(IsolatedProgram())
      private class IsolatedProgram
          extends Program(AtomicBoolean()) {
        override def execute(
            task: KernelTask,
            memory: Memory,
            control: TaskControl,
            handler: Event => Either[Fault, Unit]
        ): Either[ExecutionFailure, Unit] = {
          val flushed = memory.awaitRead(addressSpace(memory).readDeferred(GuestAddress(0x1000L), 1))
          flushed.flatMap { _ =>
            firstRead.countDown()
            if (!release.await(5, TimeUnit.SECONDS)) Left(Fault.Cancelled)
            else addressSpace(memory).read(GuestAddress(0x1000L), 1).flatMap { bytes =>
              handler(Event.Print(bytes.head.toString, Vec.empty))
            }
          }.left.map(ExecutionFailure.fromFault(_))
        }
      }
    }
    Using.resource(WorkerPool(2)) { workers =>
      Using.resource(ExecutionSession(
        List(IsolatedBackend()),
        executionContext = workers,
        parallelism = workers.parallelism
      )) { service =>
        assertEquals(service.enableHostMemory(), Right(()))
        val program = service.load(encoding, Vec[Byte](1)).toOption.get
        val task    = KernelTask.validated(
          "isolate",
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
        assertEquals(service.completionPrints(first), Right("11"))
        assertEquals(service.completionPrints(second), Right("22"))
      }
    }
  }

  test("pointer-bearing scalar arguments are relocated from registered allocation addresses") {
    val captured = AtomicReference(Vec.empty[KernelTask.PointerRelocation])
    class RelocationBackend extends RuntimeBackend {
      override val encoding: String = ExecutionSessionSuite.this.encoding
      override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] = Right(RelocationProgram())
      private class RelocationProgram extends RuntimeBackend.Program {
        override def symbols: Either[ExecutionFailure, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
        override def kernel(name: NonEmpty[String]): Either[ExecutionFailure, KernelMetadata] =
          KernelMetadata.validated(
            8,
            1,
            Vec(KernelMetadata.Parameter(
              8,
              0,
              KernelMetadata.ParameterKind.Scalar,
              containsPointers = true
            ))
          ).left.map(ExecutionFailure.InvalidRequest(_))

        override def execute(
            task: KernelTask,
            memory: Memory,
            control: TaskControl,
            handler: Event => Either[Fault, Unit]
        ): Either[ExecutionFailure, Unit] = {
          captured.set(task.pointerRelocations)
          Right(())
        }
        override def close(): Unit = ()
      }
    }
    Using.resource(ExecutionSession(List(RelocationBackend()))) { service =>
      val target = service.registerAllocation(64L, AllocationKind.Device).toOption.get
      assertEquals(service.registerAllocationAddress(target, GuestAddress(0x1000L)), Right(()))
      val program = service.load(encoding, Vec[Byte](1)).toOption.get
      val scalar  = Vec.from(LittleEndian.toVec(0x1008L, 8))
      val task    = KernelTask.validated(
        "relocate",
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
          target,
          8L
        ))
      )
    }
  }

  test("unknown encodings fail without invoking a backend") {
    val backend = Backend()
    Using.resource(ExecutionSession(List(backend))) { service =>
      val unknown = "unknown-image"
      assert(service.load(unknown, Vec.empty).left.exists {
        case ExecutionFailure.Unsupported(_) => true
        case _                               => false
      })
    }
    assert(!backend.programClosed.get())
  }
  test("address lookup rejects unsigned offsets that overflow signed subtraction") {
    class AddressBackend extends RuntimeBackend {
      override val encoding: String = ExecutionSessionSuite.this.encoding
      override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] =
        Right(AddressProgram())
      private class AddressProgram extends Program(AtomicBoolean()) {
        override def execute(
            task: KernelTask,
            memory: Memory,
            control: TaskControl,
            handler: Event => Either[Fault, Unit]
        ): Either[ExecutionFailure, Unit] = {
          val addresses = memory match {
            case value: AddressSpace => value
            case _                   => fail("execution memory must expose its address space")
          }
          val valid = addresses.locate(GuestAddress(0x1007L), 1).toOption.get
          valid match {
            case AddressSpace.Location.Within(_, offset) => assertEquals(offset.raw, 7L)
            case AddressSpace.Location.Absolute(_)       => fail("registered allocation must resolve relatively")
          }
          assert(addresses.locate(GuestAddress(0x1008L), 1).isLeft)
          assert(addresses.locate(GuestAddress(Long.MinValue + 0x2000L), 1).isLeft)
          assert(addresses.read(GuestAddress(Long.MinValue + 0x2000L), 1).isLeft)
          Right(())
        }
      }
    }
    Using.resource(ExecutionSession(List(AddressBackend()))) { service =>
      val allocation = service.registerAllocation(8L, AllocationKind.Shared).toOption.get
      assertEquals(service.registerAllocationAddress(allocation, GuestAddress(0x1000L)), Right(()))
      val program = service.load(encoding, Vec[Byte](1)).toOption.get
      val task    = KernelTask.validated(
        "address",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      ).toOption.get
      val completion = service.launch(program, task).toOption.get
      assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
    }
  }

  test("output relocations replace only the same allocation and source offset") {
    val events = AtomicReference(Vec.empty[Event])
    class RelocationBackend extends Backend {
      override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] = Right(EventProgram())
      private class EventProgram extends Program(programClosed) {
        override def execute(
            task: KernelTask,
            memory: Memory,
            control: TaskControl,
            handler: Event => Either[Fault, Unit]
        ): Either[ExecutionFailure, Unit] =
          events.get().foldLeft[Either[Fault, Unit]](Right(()))((result, event) => result.flatMap(_ => handler(event)))
            .left.map(ExecutionFailure.fromFault(_))
      }
    }
    Using.resource(ExecutionSession(List(RelocationBackend()))) { session =>
      val first  = session.registerAllocation(32L, AllocationKind.Shared).toOption.get
      val second = session.registerAllocation(32L, AllocationKind.Shared).toOption.get
      events.set(Vec(
        Event.PointerRelocation(first, 0L, second, 1L),
        Event.PointerRelocation(first, 8L, second, 2L),
        Event.PointerRelocation(second, 0L, first, 3L),
        Event.PointerRelocation(first, 0L, first, 4L)
      ))
      val program = session.load(encoding, Vec[Byte](1)).toOption.get
      val task    = KernelTask.validated(
        "relocations",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec.empty
      ).toOption.get
      val completion = session.launch(program, task).toOption.get
      assertEquals(session.awaitCompletion(completion), Right(()))
      assertEquals(
        session.completionPointerRelocations(completion, 0),
        Right(Vec(
          KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(first), 8L, second, 2L),
          KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(second), 0L, first, 3L),
          KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(first), 0L, first, 4L)
        ))
      )
      events.set(Vec.empty)
      val next = session.launch(program, task).toOption.get
      assertEquals(session.awaitCompletion(next), Right(()))
      assertEquals(session.completionPointerRelocations(next, 0), Right(Vec.empty[KernelTask.PointerRelocation]))
    }
  }

  test("executor exceptions fail one completion and release resources while the worker continues") {
    val entered = CountDownLatch(1)
    val release = CountDownLatch(1)
    class ThrowingBackend extends Backend {
      override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] = Right(ThrowingProgram())
      private class ThrowingProgram extends Program(programClosed) {
        override def execute(
            task: KernelTask,
            memory: Memory,
            control: TaskControl,
            handler: Event => Either[Fault, Unit]
        ): Either[ExecutionFailure, Unit] = {
          if (task.kernelName == "throw") {
            entered.countDown()
            if (!release.await(5, TimeUnit.SECONDS)) throw IllegalStateException("test release timed out")
            throw IllegalStateException("backend exploded")
          }
          super.execute(task, memory, control, handler)
        }
      }
    }
    val backend = ThrowingBackend()
    Using.resource(WorkerPool(1)) { workers =>
      Using.resource(ExecutionSession(List(backend), executionContext = workers, parallelism = workers.parallelism)) {
        session =>
          val allocation                   = session.registerAllocation(8L, AllocationKind.Shared).toOption.get
          val program                      = session.load(encoding, Vec[Byte](1)).toOption.get
          def task(name: NonEmpty[String]) = KernelTask.validated(
            name,
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec(KernelTask.Argument.Memory(allocation, 0L))
          ).toOption.get
          val failed = session.launch(program, task("throw")).toOption.get
          assert(entered.await(5, TimeUnit.SECONDS))
          val queued = try {
            assert(session.releaseAllocation(allocation).isLeft)
            session.launch(program, task("write")).toOption.get
          } finally release.countDown()
          session.awaitCompletion(failed) match {
            case Left(ExecutionFailure.Failed(message)) => assert(message.contains("backend exploded"))
            case other                                  => fail(s"expected failed execution, got $other")
          }
          assertEquals(session.awaitCompletion(queued), Right(()))
          assertEquals(session.read(allocation, 0L, 1), Right(Vec[Byte](42)))
          assertEquals(session.releaseCompletion(failed), Right(()))
          assertEquals(session.releaseCompletion(queued), Right(()))
          assertEquals(session.unload(program), Right(()))
          assert(backend.programClosed.get())
          assertEquals(session.releaseAllocation(allocation), Right(()))
      }
    }
  }

}
