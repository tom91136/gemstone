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
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class ExecutionSessionSuite extends munit.FunSuite {
  private val encoding = ProgramEncoding.PTXText

  private type Execute = (KernelTask, AddressSpace, Event => Either[Fault, Unit]) => Either[Fault, Unit]

  /** Writes 42 to the first memory argument and prints it. */
  private val writeFirstArgument: Execute = (task, memory, handler) =>
    task.arguments.headOption match {
      case Some(KernelTask.Argument.Memory(region, offset, _)) =>
        for {
          _ <- memory.write(region, offset, Vec[Byte](42))
          _ <- handler(Event.Print("done %d", Vec(Event.PrintArgument.Integer(Vec(42L), 32))))
        } yield ()
      case _ => Left(Fault.InvalidRequest("memory argument is required"))
    }

  private class TestBackend(execute: Execute = writeFirstArgument) extends Backend {
    val programClosed: AtomicBoolean                                    = AtomicBoolean()
    override val encoding: ProgramEncoding                              = ExecutionSessionSuite.this.encoding
    override def load(image: Vec[Byte]): Either[Fault, Backend.Program] = Right(Program(programClosed, execute))
  }

  private class Program(closed: AtomicBoolean, run: Execute = writeFirstArgument) extends Backend.Program {
    override def symbols: Either[Fault, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
    override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
      KernelMetadata.validated(
        8,
        1,
        Vec(KernelMetadata.Parameter(8, 0, KernelMetadata.ParameterKind.Allocation, false))
      )
        .left.map(Fault.InvalidRequest(_))
    override def execute(
        task: KernelTask,
        memory: AddressSpace,
        control: TaskControl,
        handler: Event => Either[Fault, Unit]
    ): Either[Fault, Unit]     = run(task, memory, handler)
    override def close(): Unit = closed.set(true)
  }

  private class ProgramBackend(program: => Backend.Program) extends Backend {
    override val encoding: ProgramEncoding                              = ExecutionSessionSuite.this.encoding
    override def load(image: Vec[Byte]): Either[Fault, Backend.Program] = Right(program)
  }

  private def task(name: NonEmpty[String], arguments: KernelTask.Argument*): KernelTask =
    KernelTask.validated(name, LaunchGeometry.Dimensions.One, LaunchGeometry.Dimensions.One, Vec.from(arguments))
      .toOption.get

  private def pooled(workers: WorkerPool, backend: Backend): ExecutionSession =
    ExecutionSession(List(backend), executionContext = workers, parallelism = workers.parallelism)

  test("printf retention accepts its limit and fails on the next event") {
    for (events <- List(2, 3)) {
      val backend = TestBackend((_, _, handler) =>
        (0 until events).foldLeft[Either[Fault, Unit]](Right(()))((result, _) =>
          result.flatMap(_ => handler(Event.Print("hello", Vec.empty)))
        )
      )
      Using.resource(ExecutionSession(backends = List(backend), maxPrintEvents = 2)) { service =>
        val allocation = service.registerAllocation(8).toOption.get
        val program    = service.load(encoding, Vec[Byte](1)).toOption.get
        val completion = service.launch(program, task("print", KernelTask.Argument.Memory(allocation, 0L))).toOption.get
        if (events == 2) {
          assertEquals(service.awaitCompletion(completion), Right(()))
          assertEquals(service.completionPrints(completion), Right("hellohello"))
        } else {
          val failure = service.awaitCompletion(completion).swap.toOption.get
          assert(failure.message.contains("device printf event limit exceeded"))
          assertEquals(service.completion(completion), Right(CompletionStatus.Failed(failure)))
        }
      }
    }
  }

  test("host transfer requirements follow allocation registration and release") {
    val observed = AtomicBoolean()
    Using.resource(ExecutionSession(List(TestBackend((_, memory, _) => Right(observed.set(memory.defersHostReads)))))) {
      service =>
        val device                         = service.registerAllocation(8).toOption.get
        val program                        = service.load(encoding, Vec[Byte](1)).toOption.get
        val probe                          = task("probe", KernelTask.Argument.Memory(device, 0L))
        def check(expected: Boolean): Unit = {
          val completion = service.launch(program, probe).toOption.get
          assertEquals(service.awaitCompletion(completion), Right(()))
          assertEquals(observed.get(), expected)
          assertEquals(service.releaseCompletion(completion), Right(()))
        }
        check(false)
        val host = service.registerAllocation(8).toOption.get
        assertEquals(service.registerAllocationAddress(host, GuestAddress(0x1000L), hostBacked = true), Right(()))
        check(true)
        assert(service.registerAllocationAddress(host, GuestAddress(0x1000L), hostBacked = false).isLeft)
        check(true)
        val secondHost = service.registerAllocation(8).toOption.get
        assertEquals(service.registerAllocationAddress(secondHost, GuestAddress(0x2000L), hostBacked = true), Right(()))
        check(true)
        assertEquals(service.releaseAllocation(host), Right(()))
        check(true)
        assertEquals(service.releaseAllocation(secondHost), Right(()))
        check(false)
        assertEquals(service.enableHostMemory(), Right(()))
        check(true)
    }
  }

  test("image specialization delegates to the configured specializer and fails without one") {
    val unavailable = Left(ExecutionFailure.InvalidRequest("image specialization is unavailable"))
    Using.resource(ExecutionSession(List.empty)) { service =>
      assertEquals(service.describeSpecialization(Vec.empty), unavailable)
      assertEquals(service.specializeImage(Vec.empty, Vec.empty), unavailable)
    }
    val image     = Vec[Byte](1, 2, 3)
    val constants = Vec(SpecializationConstant(Binary32(7L), Vec[Byte](9)))
    object Specializer extends Backend.Specialization {
      override def describe(input: Vec[Byte]): Either[Fault, Vec[SpecializationInfo]] =
        Either.cond(input == image, Vec(SpecializationInfo(Binary32(7L), 1)), Fault.InvalidRequest("image"))
      override def specialize(input: Vec[Byte], supplied: Vec[SpecializationConstant]): Either[Fault, Vec[Byte]] =
        Either.cond(
          input == image && supplied == constants,
          input ++ supplied.head.value,
          Fault.InvalidRequest("image")
        )
    }
    Using.resource(ExecutionSession(List.empty, specialization = Some(Specializer))) { service =>
      assertEquals(service.describeSpecialization(image), Right(Vec(SpecializationInfo(Binary32(7L), 1))))
      assertEquals(service.specializeImage(image, constants), Right(Vec[Byte](1, 2, 3, 9)))
      assertEquals(service.describeSpecialization(Vec.empty), Left(ExecutionFailure.InvalidRequest("image")))
    }
  }

  test("allocation address aliases are immutable and non-overlapping") {
    Using.resource(ExecutionSession(List.empty)) { service =>
      val first  = service.registerAllocation(16).toOption.get
      val second = service.registerAllocation(16).toOption.get
      assertEquals(service.registerAllocationAddress(first, GuestAddress(0x1000L)), Right(()))
      assertEquals(service.registerAllocationAddress(first, GuestAddress(0x1000L)), Right(()))
      assert(service.registerAllocationAddress(first, GuestAddress(0x2000L)).isLeft)
      assert(service.registerAllocationAddress(second, GuestAddress(0x1008L)).isLeft)
      assertEquals(service.registerAllocationAddress(second, GuestAddress(0x2000L)), Right(()))
    }
  }

  for ((name, size, secondOffset) <- List(("an unrelated allocation", 8, None), ("a disjoint range", 256, Some(128L))))
    test(s"an atomic update does not block writes to $name") {
      Using.resource(ExecutionSession(List(TestBackend()))) { service =>
        val first   = service.registerAllocation(size.unsafe[Int > 0]).toOption.get
        val second  = if (secondOffset.isEmpty) service.registerAllocation(8).toOption.get else first
        val offset  = secondOffset.getOrElse(0L).unsafe[Long >= 0]
        val entered = CountDownLatch(1)
        val release = CountDownLatch(1)
        val written = CountDownLatch(1)
        val atomic  = FutureTask[Either[ExecutionFailure, Long]](() =>
          service.atomicModifyValue(first, 0L, 8) { previous =>
            entered.countDown()
            if (!release.await(5L, TimeUnit.SECONDS)) throw IllegalStateException("atomic update was not released")
            previous + 1L
          }
        )
        val writer = FutureTask[Either[ExecutionFailure, Unit]] { () =>
          val result = service.writeValue(second, offset, 42L, 8)
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
        assertEquals(service.readValue(second, offset, 8), Right(42L))
        assert(independent, "one allocation's atomic update held the session lock across an unrelated write")
      }
    }

  test("service translates API ownership into a VM task and records VM events") {
    val backend = TestBackend()
    Using.resource(ExecutionSession(List(backend))) { service =>
      val allocation = service.registerAllocation(8).toOption.get
      val program    = service.load(encoding, Vec[Byte](1)).toOption.get
      val completion = service.launch(program, task("write", KernelTask.Argument.Memory(allocation, 2L))).toOption.get
      assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
      assertEquals(service.read(allocation, 2L, 1), Right(Vec[Byte](42)))
      assertEquals(service.completionPrints(completion), Right("done 42"))
      assertEquals(
        service.completionDirtyRanges(id = completion, offset = 0, limit = ExecutionRPC.MaximumElements),
        Right(Vec(DirtyRange(allocation, 2L, 1)))
      )
    }
    assert(backend.programClosed.get())
  }

  test("sparse writeback pages preserve every written byte and exclude unwritten gaps") {
    val count   = ExecutionRPC.MaximumElements.raw + 3
    val backend = TestBackend((task, memory, _) =>
      task.arguments.headOption match {
        case Some(KernelTask.Argument.Memory(region, _, _)) =>
          (0 until count).foldLeft[Either[Fault, Unit]](Right(())) { (result, index) =>
            result.flatMap(_ => memory.write(region, (index.toLong * 2).unsafe[Long >= 0], Vec[Byte](42)))
          }
        case _ => Left(Fault.InvalidRequest("memory argument is required"))
      }
    )
    Using.resource(ExecutionSession(List(backend))) { service =>
      val allocation = service.registerAllocation((count * 2).unsafe[Int > 0]).toOption.get
      val program    = service.load(encoding, Vec[Byte](1)).toOption.get
      val completion = service.launch(program, task("write", KernelTask.Argument.Memory(allocation, 0L))).toOption.get
      assertEquals(service.awaitCompletion(completion), Right(()))
      val first =
        service.completionDirtyRanges(id = completion, offset = 0, limit = ExecutionRPC.MaximumElements).toOption.get
      val second = service.completionDirtyRanges(
        id = completion,
        offset = ExecutionRPC.MaximumElements,
        limit = ExecutionRPC.MaximumElements
      ).toOption.get
      assertEquals(first.length, ExecutionRPC.MaximumElements.raw)
      assertEquals(second.length, 3)
      assertEquals(
        service.completionDirtyRanges(id = completion, offset = 0, limit = ExecutionRPC.MaximumElements),
        Right(first)
      )
      assertEquals(
        service.completionDirtyRanges(
          id = completion,
          offset = count.unsafe[Int >= 0],
          limit = ExecutionRPC.MaximumElements
        ),
        Right(Vec.empty[DirtyRange])
      )
      assertEquals(
        service.completionDirtyRanges(id = completion, offset = Int.MaxValue, limit = ExecutionRPC.MaximumElements),
        Right(Vec.empty[DirtyRange])
      )
      val ranges = first ++ second
      assertEquals(service.completionDirtyRanges(id = completion, offset = 1, limit = 2), Right(ranges.slice(1, 3)))
      assertEquals(
        service.completionDirtyRanges(id = completion, offset = (count - 1).unsafe[Int >= 0], limit = 2),
        Right(ranges.takeRight(1))
      )
      assertEquals(
        service.completionDirtyRanges(id = completion, offset = 1, limit = Int.MaxValue),
        Right(ranges.drop(1))
      )
      assertEquals(ranges.map(_.offset.raw), Vec.from(0 until count).map(_.toLong * 2))
      assert(ranges.forall(range => range.allocation == allocation && range.size.raw == 1))
      for (page <- Vec(first, second)) {
        val operation = ExecutionRPC.Operation.CompletionDirtyRanges(completion, 0)
        assert(operation.replyCodec.encode(page, ExecutionRPC.Limits).isRight)
      }
      val bytes = service.read(allocation, 0L, (count * 2).unsafe[Int >= 0]).toOption.get
      assertEquals(bytes, Vec.from(0 until count * 2).map(index => if (index % 2 == 0) 42.toByte else 0.toByte))
      assertEquals(service.releaseCompletion(completion), Right(()))
      assert(service.completionDirtyRanges(id = completion, offset = 0, limit = ExecutionRPC.MaximumElements).isLeft)
    }
  }

  test("parallel sparse writes and cross-stripe atomics retain exact writeback") {
    Using.resource(WorkerPool(4)) { workers =>
      val backend = TestBackend { (task, memory, _) =>
        val KernelTask.Argument.Memory(region, _, _) = task.arguments.head: @unchecked
        workers.parallelFor(128L) { index =>
          val offset = ((index.raw * 31L % 128L) * 16L).unsafe[Long >= 0]
          for {
            _ <- memory.writeValue(region, offset, index.raw + 1L, 8)
            _ <- memory.atomicModifyValue(region, 4095L, 8)(_ + 1L)
          } yield ()
        }
      }
      Using.resource(ExecutionSession(List(backend))) { service =>
        val allocation = service.registerAllocation(8192).toOption.get
        val program    = service.load(encoding, Vec[Byte](1)).toOption.get
        val completion =
          service.launch(program, task("parallel", KernelTask.Argument.Memory(allocation, 0L))).toOption.get
        assertEquals(service.awaitCompletion(completion), Right(()))
        assertEquals(service.readValue(allocation, 4095L, 8), Right(128L))
        val expected =
          Vec.from(0 until 128).map(index => DirtyRange(allocation, (index.toLong * 16L).unsafe[Long >= 0], 8))
            .appended(DirtyRange(allocation, 4095L, 8))
        assertEquals(
          service.completionDirtyRanges(id = completion, offset = 0, limit = ExecutionRPC.MaximumElements),
          Right(expected)
        )
        (0 until 128).foreach { index =>
          val offset = ((index.toLong * 31L % 128L) * 16L).unsafe[Long >= 0]
          assertEquals(service.readValue(allocation, offset, 8), Right(index.toLong + 1L))
          assertEquals(service.readValue(allocation, (offset.raw + 8L).unsafe[Long >= 0], 8), Right(0L))
        }
      }
    }
  }

  test("pointer relocation metadata is paged without losing identities or offsets") {
    val count   = ExecutionRPC.MaximumElements.raw + 1
    val backend = TestBackend { (task, _, handler) =>
      val KernelTask.Argument.Memory(region, _, _) = task.arguments.head: @unchecked
      (0 until count).foldLeft[Either[Fault, Unit]](Right(())) { (result, index) =>
        result.flatMap(_ => handler(Event.PointerRelocation(region, (index.toLong * 16).unsafe[Long >= 0], region, 0L)))
      }
    }
    Using.resource(ExecutionSession(List(backend))) { session =>
      val allocation = session.registerAllocation((count * 16).unsafe[Int > 0]).toOption.get
      val program    = session.load(encoding, Vec[Byte](1)).toOption.get
      val completion = session.launch(program, task("write", KernelTask.Argument.Memory(allocation, 0L))).toOption.get
      assertEquals(session.awaitCompletion(completion), Right(()))
      val first =
        session.completionPointerRelocations(id = completion, offset = 0, limit = ExecutionRPC.MaximumElements)
          .toOption.get
      val second = session.completionPointerRelocations(
        id = completion,
        offset = ExecutionRPC.MaximumElements,
        limit = ExecutionRPC.MaximumElements
      ).toOption.get
      assertEquals(first.size, ExecutionRPC.MaximumElements.raw)
      assertEquals(second.size, 1)
      assertEquals(
        session.completionPointerRelocations(id = completion, offset = 0, limit = ExecutionRPC.MaximumElements),
        Right(first)
      )
      assertEquals((first ++ second).map(_.sourceOffset.raw), Vec.tabulate(count)(_.toLong * 16))
      assertEquals(
        session.completionPointerRelocations(id = completion, offset = 1, limit = 2),
        Right(first.slice(1, 3))
      )
      assertEquals(
        session.completionPointerRelocations(id = completion, offset = (count - 1).unsafe[Int >= 0], limit = 2),
        Right(second)
      )
      assertEquals(
        session.completionPointerRelocations(id = completion, offset = 1, limit = Int.MaxValue),
        Right((first ++ second).drop(1))
      )
      assert((first ++ second).forall(value =>
        value.source == KernelTask.RelocationSource.Memory(allocation) &&
          value.target == allocation && value.targetOffset.raw == 0L
      ))
      for (page <- Vec(first, second))
        assert(
          ExecutionRPC.Operation.CompletionPointerRelocations(completion, 0).replyCodec
            .encode(page, ExecutionRPC.Limits).isRight
        )
      for (offset <- Vec(count.unsafe[Int >= 0], Int.MaxValue.unsafe[Int >= 0]))
        assertEquals(
          session.completionPointerRelocations(id = completion, offset = offset, limit = ExecutionRPC.MaximumElements),
          Right(Vec.empty[KernelTask.PointerRelocation])
        )
      assertEquals(session.releaseCompletion(completion), Right(()))
      assert(
        session.completionPointerRelocations(id = completion, offset = 0, limit = ExecutionRPC.MaximumElements).isLeft
      )
    }
  }

  test("unloading retains bound allocations until backend close finishes, including failed close") {
    for (failClose <- List(false, true)) {
      val entered = CountDownLatch(1)
      val release = CountDownLatch(1)
      class ClosingProgram extends Program(AtomicBoolean()) {
        override def global(name: NonEmpty[String]): Either[Fault, ProgramGlobal] =
          ProgramGlobal.validated(8L, Vec.empty).left.map(Fault.InvalidRequest(_))
        override def bindGlobal(name: NonEmpty[String], binding: GlobalBinding, memory: AddressSpace)
            : Either[Fault, Unit]  = Right(())
        override def close(): Unit = {
          entered.countDown()
          if (!release.await(5, TimeUnit.SECONDS)) throw IllegalStateException("backend close was not released")
          if (failClose) throw IllegalStateException("close failed")
        }
      }
      Using.resource(ExecutionSession(List(ProgramBackend(ClosingProgram())))) { session =>
        val allocation = session.registerAllocation(8).toOption.get
        val program    = session.load(encoding, Vec[Byte](1)).toOption.get
        assertEquals(session.bindGlobal(program, "global", allocation, 0L), Right(()))
        val unloading = FutureTask(() => scala.util.Try(session.unload(program)))
        val worker    = Thread(unloading)
        worker.start()
        try {
          assert(entered.await(5, TimeUnit.SECONDS), "backend close did not start")
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
      val backend = TestBackend { (_, memory, _) =>
        val staged = CountDownLatch(1)
        workers.parallelFor(2L) { lane =>
          if (lane.raw == 0) {
            assertEquals(memory.readDeferred(GuestAddress(0x1000L), 4), Left(AddressSpace.Pending))
            staged.countDown()
            memory.flushHostReads()
          } else {
            if (!staged.await(5, TimeUnit.SECONDS)) throw IllegalStateException("host read was not staged")
            memory.fenceHostMemory()
          }
        }
      }
      Using.resource(pooled(workers, backend)) { service =>
        assertEquals(service.enableHostMemory(), Right(()))
        val program = service.load(encoding, Vec[Byte](1)).toOption.get
        val pump    = FutureTask[Unit](() =>
          (0 until 512).foreach { _ =>
            val completion = service.launch(program, task("read-and-fence")).toOption.get
            @annotation.tailrec
            def poll(): Unit = {
              val batch = service.pollHostMemory(completion).fold(error => fail(error.message), identity)
              batch.sync.foreach(sync => assertEquals(service.supplyHostSync(completion, sync.ticket, 0L), Right(())))
              if (batch.reads.nonEmpty)
                assertEquals(
                  service.supplyHostMemory(
                    completion,
                    batch.reads.map(range => HostMemoryBytes(range.address, Vec.fill(range.size.raw)(1.toByte)))
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
          memory: AddressSpace,
          control: TaskControl,
          handler: Event => Either[Fault, Unit]
      ): Either[Fault, Unit] =
        try
          if (sync) memory.fenceHostMemory()
          else {
            assertEquals(memory.readDeferred(GuestAddress(0x1000L), 4), Left(AddressSpace.Pending))
            memory.flushHostReads()
          }
        finally finished.countDown()
      override def close(): Unit = disposed.countDown()
    }
    Using.resource(WorkerPool(1)) { workers =>
      Using.resource(pooled(workers, ProgramBackend(WaitingProgram()))) { service =>
        assertEquals(service.enableHostMemory(), Right(()))
        val allocation = service.registerAllocation(8).toOption.get
        val program    = service.load(encoding, Vec[Byte](1)).toOption.get
        val waiting    = task("wait", KernelTask.Argument.Memory(allocation, 0L))
        val first      = service.launch(program, waiting).toOption.get
        val batch      = service.pollHostMemory(first).toOption.get
        assertEquals(batch.sync.nonEmpty, sync)
        assertEquals(batch.reads.nonEmpty, !sync)
        val queued = service.launch(program, waiting).toOption.get
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

  test("argument buffers reject missing allocations and out-of-bounds kernel blocks before dispatch") {
    Using.resource(ExecutionSession(List(TestBackend()))) { service =>
      val allocation = service.registerAllocation(16).toOption.get
      val program    = service.load(encoding, Vec[Byte](1)).toOption.get
      val retired    = service.registerAllocation(16).toOption.get
      assertEquals(service.releaseAllocation(retired), Right(()))
      val invalid = Vec(
        KernelTask.ArgumentBuffer(retired, 0L),
        KernelTask.ArgumentBuffer(allocation, 16L),
        KernelTask.ArgumentBuffer(allocation, 9L)
      )
      for (buffer <- invalid) {
        val task = KernelTask.validated(
          kernelName = "write",
          groupSize = LaunchGeometry.Dimensions.One,
          groupCount = LaunchGeometry.Dimensions.One,
          arguments = Vec.empty,
          argumentBuffer = Some(buffer)
        ).toOption.get
        assert(service.launch(program, task).isLeft, s"accepted invalid buffer $buffer")
      }
      assertEquals(service.releaseAllocation(allocation), Right(()))
    }
  }

  test("descriptor slots without a binding are rejected before dispatch") {
    val invoked = AtomicBoolean()
    val backend = TestBackend { (_, _, _) =>
      invoked.set(true)
      Right(())
    }
    Using.resource(ExecutionSession(List(backend))) { service =>
      val program = service.load(encoding, Vec[Byte](1)).toOption.get
      assertEquals(
        service.launch(program, task("write", KernelTask.Argument.Local(8L), KernelTask.Argument.Descriptor)),
        Left(ExecutionFailure.InvalidRequest("kernel argument 1 has no descriptor binding"))
      )
      assert(!invoked.get())
    }
  }

  for (bufferBacked <- Vec(false, true))
    test(s"dispatch retains argument allocation (buffer=$bufferBacked) while unrelated addresses can be reused") {
      val entered = CountDownLatch(1)
      val release = CountDownLatch(1)
      val backend = TestBackend { (task, memory, handler) =>
        entered.countDown()
        if (!release.await(5, TimeUnit.SECONDS)) Left(Fault.Backend("dispatch release timed out"))
        else task.argumentBuffer match {
          case None         => writeFirstArgument(task, memory, handler)
          case Some(buffer) => memory.write(buffer.region, buffer.offset, Vec[Byte](42))
        }
      }
      Using.resource(WorkerPool(1)) { workers =>
        Using.resource(pooled(workers, backend)) { service =>
          val used    = service.registerAllocation(16).toOption.get
          val unused  = service.registerAllocation(8).toOption.get
          val address = GuestAddress(4096L)
          assertEquals(service.registerAllocationAddress(unused, address), Right(()))
          val program = service.load(encoding, Vec[Byte](1)).toOption.get
          val task    = KernelTask.validated(
            kernelName = "write",
            groupSize = LaunchGeometry.Dimensions.One,
            groupCount = LaunchGeometry.Dimensions.One,
            arguments = if (bufferBacked) Vec.empty else Vec(KernelTask.Argument.Memory(used, 8L)),
            argumentBuffer = Option.when(bufferBacked)(KernelTask.ArgumentBuffer(used, 8L))
          ).toOption.get
          val completion = service.launch(program, task).toOption.get
          try {
            assert(entered.await(5, TimeUnit.SECONDS))
            assert(service.releaseAllocation(used).isLeft)
            assertEquals(service.releaseAllocation(unused), Right(()))
            val replacement = service.registerAllocation(8).toOption.get
            assertEquals(service.registerAllocationAddress(replacement, address), Right(()))
          } finally release.countDown()
          assertEquals(service.awaitCompletion(completion), Right(()))
          assertEquals(service.read(used, 8L, 1), Right(Vec[Byte](42)))
          assertEquals(service.releaseCompletion(completion), Right(()))
          assertEquals(service.releaseAllocation(used), Right(()))
        }
      }
    }

  test("independent dispatches can rendezvous before either completes") {
    val entered = CountDownLatch(2)
    val release = CountDownLatch(1)
    val backend = TestBackend { (_, _, _) =>
      entered.countDown()
      if (release.await(5, TimeUnit.SECONDS)) Right(()) else Left(Fault.Backend("rendezvous timed out"))
    }
    Using.resource(WorkerPool(2)) { workers =>
      Using.resource(pooled(workers, backend)) { service =>
        val program    = service.load(encoding, Vec[Byte](1)).toOption.get
        val first      = service.launch(program, task("rendezvous")).toOption.get
        val second     = service.launch(program, task("rendezvous")).toOption.get
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
    val backend   = TestBackend { (_, memory, handler) =>
      memory.awaitRead(memory.readDeferred(GuestAddress(0x1000L), 1)).flatMap { _ =>
        firstRead.countDown()
        if (!release.await(5, TimeUnit.SECONDS)) Left(Fault.Cancelled)
        else
          memory.read(GuestAddress(0x1000L), 1).flatMap(bytes => handler(Event.Print(bytes.head.toString, Vec.empty)))
      }
    }
    Using.resource(WorkerPool(2)) { workers =>
      Using.resource(pooled(workers, backend)) { service =>
        assertEquals(service.enableHostMemory(), Right(()))
        val program = service.load(encoding, Vec[Byte](1)).toOption.get
        val first   = service.launch(program, task("isolate")).toOption.get
        val initial = service.pollHostMemory(first).toOption.get
        assertEquals(initial.reads.size, 1)
        assertEquals(
          service.supplyHostMemory(first, Vec(HostMemoryBytes(initial.reads.head.address, Vec[Byte](11)))),
          Right(())
        )
        assert(firstRead.await(2, TimeUnit.SECONDS))
        val second  = service.launch(program, task("isolate")).toOption.get
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

  test("pointer-bearing scalar arguments are relocated from client aliases, including one past the end") {
    val captured = AtomicReference(Vec.empty[KernelTask.PointerRelocation])
    class PointerProgram
        extends Program(AtomicBoolean(), (task, _, _) => Right(captured.set(task.pointerRelocations))) {
      override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
        KernelMetadata.validated(16, 1, Vec(KernelMetadata.Parameter(16, 0, KernelMetadata.ParameterKind.Scalar, true)))
          .left.map(Fault.InvalidRequest(_))
    }
    Using.resource(ExecutionSession(List(ProgramBackend(PointerProgram())))) { service =>
      val target = service.registerAllocation(64).toOption.get
      val next   = service.registerAllocation(64).toOption.get
      assertEquals(service.registerAllocationAddress(target, GuestAddress(0x1000L)), Right(()))
      assertEquals(service.registerAllocationAddress(next, GuestAddress(0x1040L)), Right(()))
      val program = service.load(encoding, Vec[Byte](1)).toOption.get
      val scalar  = LittleEndian.toVec(0x1008L, 8) ++ LittleEndian.toVec(0x1080L, 8)
      val task    = KernelTask.validated(
        "relocate",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Scalar(scalar.unsafe)),
        discoverArgumentRelocations = true
      ).toOption.get
      val completion = service.launch(program, task).toOption.get
      assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
      assertEquals(
        captured.get(),
        Vec(
          KernelTask.PointerRelocation(KernelTask.RelocationSource.Argument(0), 0L, target, 8L),
          KernelTask.PointerRelocation(KernelTask.RelocationSource.Argument(0), 8L, next, 64L)
        )
      )
    }
  }

  test("unknown encodings fail without invoking a backend") {
    val backend = TestBackend()
    Using.resource(ExecutionSession(List(backend))) { service =>
      assert(service.load(ProgramEncoding.CUDACubin, Vec.empty).left.exists {
        case ExecutionFailure.Unsupported(_) => true
        case _                               => false
      })
    }
    assert(!backend.programClosed.get())
  }

  test("address lookup rejects unsigned offsets that overflow signed subtraction") {
    val backend = TestBackend { (_, memory, _) =>
      memory.locate(GuestAddress(0x1007L), 1).toOption.get match {
        case AddressSpace.Location.Within(_, offset) => assertEquals(offset.raw, 7L)
        case AddressSpace.Location.Absolute(_)       => fail("registered allocation must resolve relatively")
      }
      assert(memory.locate(GuestAddress(0x1008L), 1).isLeft)
      assert(memory.locate(GuestAddress(Long.MinValue + 0x2000L), 1).isLeft)
      assert(memory.read(GuestAddress(Long.MinValue + 0x2000L), 1).isLeft)
      Right(())
    }
    Using.resource(ExecutionSession(List(backend))) { service =>
      val allocation = service.registerAllocation(8).toOption.get
      assertEquals(service.registerAllocationAddress(allocation, GuestAddress(0x1000L)), Right(()))
      val program    = service.load(encoding, Vec[Byte](1)).toOption.get
      val completion = service.launch(program, task("address", KernelTask.Argument.Memory(allocation, 0L))).toOption.get
      assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
    }
  }

  test("output relocations replace only the same allocation and source offset") {
    val events  = AtomicReference(Vec.empty[Event])
    val backend = TestBackend((_, _, handler) =>
      events.get().foldLeft[Either[Fault, Unit]](Right(()))((result, event) => result.flatMap(_ => handler(event)))
    )
    Using.resource(ExecutionSession(List(backend))) { session =>
      val first  = session.registerAllocation(32).toOption.get
      val second = session.registerAllocation(32).toOption.get
      events.set(Vec(
        Event.PointerRelocation(first, 0L, second, 1L),
        Event.PointerRelocation(first, 8L, second, 2L),
        Event.PointerRelocation(second, 0L, first, 3L),
        Event.PointerRelocation(first, 0L, first, 4L)
      ))
      val program    = session.load(encoding, Vec[Byte](1)).toOption.get
      val completion = session.launch(program, task("relocations")).toOption.get
      assertEquals(session.awaitCompletion(completion), Right(()))
      assertEquals(
        session.completionPointerRelocations(id = completion, offset = 0, limit = ExecutionRPC.MaximumElements),
        Right(Vec(
          KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(first), 8L, second, 2L),
          KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(second), 0L, first, 3L),
          KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(first), 0L, first, 4L)
        ))
      )
      events.set(Vec.empty)
      val next = session.launch(program, task("relocations")).toOption.get
      assertEquals(session.awaitCompletion(next), Right(()))
      assertEquals(
        session.completionPointerRelocations(id = next, offset = 0, limit = ExecutionRPC.MaximumElements),
        Right(Vec.empty[KernelTask.PointerRelocation])
      )
    }
  }

  test("executor exceptions fail one completion and release resources while the worker continues") {
    val entered = CountDownLatch(1)
    val release = CountDownLatch(1)
    val backend = TestBackend { (task, memory, handler) =>
      if (task.kernelName == "throw") {
        entered.countDown()
        if (!release.await(5, TimeUnit.SECONDS)) throw IllegalStateException("test release timed out")
        throw IllegalStateException("backend exploded")
      }
      writeFirstArgument(task, memory, handler)
    }
    Using.resource(WorkerPool(1)) { workers =>
      Using.resource(pooled(workers, backend)) { session =>
        val allocation = session.registerAllocation(8).toOption.get
        val program    = session.load(encoding, Vec[Byte](1)).toOption.get
        val failed     = session.launch(program, task("throw", KernelTask.Argument.Memory(allocation, 0L))).toOption.get
        assert(entered.await(5, TimeUnit.SECONDS))
        val queued = try {
          assert(session.releaseAllocation(allocation).isLeft)
          session.launch(program, task("write", KernelTask.Argument.Memory(allocation, 0L))).toOption.get
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

  test("fatal execution fails current and queued sessions without swallowing the original error") {
    val entered  = CountDownLatch(1)
    val release  = CountDownLatch(1)
    val escaped  = CountDownLatch(1)
    val observed = AtomicReference[Throwable]()
    val fatal    = LinkageError("broken backend linkage")
    val backend  = TestBackend { (_, _, _) =>
      Thread.currentThread().setUncaughtExceptionHandler { (_, error) =>
        observed.set(error)
        escaped.countDown()
      }
      entered.countDown()
      if (!release.await(5, TimeUnit.SECONDS)) throw IllegalStateException("test release timed out")
      throw fatal
    }
    val otherBackend = TestBackend()
    Using.resource(WorkerPool(1)) { workers =>
      Using.resource(pooled(workers, backend)) { first =>
        Using.resource(pooled(workers, otherBackend)) { second =>
          val firstProgram  = first.load(encoding, Vec[Byte](1)).toOption.get
          val secondProgram = second.load(encoding, Vec[Byte](1)).toOption.get
          val current       = first.launch(firstProgram, task("probe")).toOption.get
          val queued        = try {
            assert(entered.await(5, TimeUnit.SECONDS))
            (
              first.launch(firstProgram, task("probe")).toOption.get,
              second.launch(secondProgram, task("probe")).toOption.get
            )
          } finally release.countDown()
          assert(escaped.await(5, TimeUnit.SECONDS))
          assert(observed.get() eq fatal)
          List((first, current), (first, queued._1), (second, queued._2)).foreach { (session, completion) =>
            session.completion(completion) match {
              case Right(CompletionStatus.Failed(ExecutionFailure.Failed(message))) =>
                assert(message.contains("broken backend linkage"))
              case other => fail(s"expected terminal failure, got $other")
            }
            assert(session.awaitCompletion(completion).isLeft)
            assert(session.pollHostMemory(completion).isLeft)
            assertEquals(session.releaseCompletion(completion), Right(()))
          }
          val rejected = second.launch(secondProgram, task("probe")).toOption.get
          assert(second.awaitCompletion(rejected).isLeft)
          assertEquals(second.releaseCompletion(rejected), Right(()))
          assertEquals(first.unload(firstProgram), Right(()))
          assert(backend.programClosed.get())
        }
        assert(otherBackend.programClosed.get())
      }
    }
  }
}
