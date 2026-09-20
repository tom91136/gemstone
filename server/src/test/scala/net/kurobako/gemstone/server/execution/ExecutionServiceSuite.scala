package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.{PlatformTime, Vec}
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.AllocationKind

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import scala.collection.mutable
import scala.util.Using

final class ExecutionServiceSuite extends munit.FunSuite {
  private val openServices = mutable.ArrayBuffer.empty[ExecutionService]

  private def managed(service: ExecutionService): ExecutionService = {
    openServices += service
    service
  }

  override def afterEach(context: AfterEach): Unit = {
    val services = openServices.toVector
    openServices.clear()
    Using.Manager(use => services.foreach(service => use(service))).get
    super.afterEach(context)
  }
  private final class TestExecutor(
      loadFn: (ProgramId, ProgramFormat, Vec[Byte]) => Either[ExecutionFailure, Unit],
      unloadFn: ProgramId => Either[ExecutionFailure, Unit],
      launchFn: (KernelDispatch, ExecutionMemory, ExecutionControl) => Either[ExecutionFailure, Unit],
      writeGlobalFn: (ProgramId, GlobalName, Long :| Positive0, Vec[Byte]) => Either[ExecutionFailure, Unit] =
        (_, _, _, _) => Right(())
  ) extends ProgramExecutor {
    override def load(id: ProgramId, format: ProgramFormat, image: Vec[Byte]) = loadFn(id, format, image)
    override def unload(id: ProgramId)                                        = unloadFn(id)
    override def launch(dispatch: KernelDispatch, memory: ExecutionMemory, control: ExecutionControl) =
      launchFn(dispatch, memory, control)
    override def writeGlobal(id: ProgramId, name: GlobalName, offset: Long :| Positive0, bytes: Vec[Byte]) =
      writeGlobalFn(id, name, offset, bytes)
  }

  private def kernelName(value: String): KernelName =
    KernelName.either(value).fold(message => fail(message), result => result)

  test("program, allocation, dispatch, and completion lifecycle is process-neutral") {
    val loaded  = AtomicReference(Option.empty[ProgramId])
    val backend = TestExecutor(
      (id, format, image) =>
        if (format == ProgramFormat.SPIRVBinary && image == Vec[Byte](3, 2, 35, 7)) {
          loaded.set(Some(id))
          Right(())
        } else Left(ExecutionFailure.InvalidRequest("unexpected module")),
      id => Either.cond(loaded.get().contains(id), loaded.set(None), ExecutionFailure.NotFound("not loaded")),
      (dispatch, memory, _) =>
        dispatch.arguments match {
          case Vec(
                KernelArgument.Allocation(id, offset),
                KernelArgument.Scalar(bytes),
                KernelArgument.Local(64L)
              ) =>
            memory.write(id, offset, bytes)
          case _ => Left(ExecutionFailure.InvalidRequest("unexpected arguments"))
        }
    )
    val service    = managed(ExecutionService(backend, _.run()))
    val allocation = service.registerAllocation(8L, AllocationKind.Shared).toOption.get
    assertEquals(service.write(allocation, 0L, Vec[Byte](1, 2, 3, 4)), Right(()))

    val program  = service.load(ProgramFormat.SPIRVBinary, Vec[Byte](3, 2, 35, 7)).toOption.get
    val dispatch = KernelDispatch.validated(
      program,
      KernelName("write"),
      Dimensions.One,
      Dimensions.One,
      Vec(
        KernelArgument.Allocation(allocation, 2L),
        KernelArgument.Scalar(Vec[Byte](9, 8)),
        KernelArgument.Local(64L)
      )
    ).toOption.get
    val completion = service.launch(dispatch).toOption.get
    assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
    assertEquals(service.completionDirtyRanges(completion), Right(Vec(DirtyRange(allocation, 0L, 8))))
    assertEquals(service.read(allocation, 0L, 8), Right(Vec[Byte](1, 2, 9, 8, 0, 0, 0, 0)))
    assertEquals(service.releaseCompletion(completion), Right(()))
    assert(service.completionDirtyRanges(completion).isLeft)
    assertEquals(service.unload(program), Right(()))
    assertEquals(service.releaseAllocation(allocation), Right(()))
  }

  test("failed execution is retained on its completion") {
    val backend = TestExecutor(
      (_, _, _) => Right(()),
      _ => Right(()),
      (_, _, _) => Left(ExecutionFailure.Unsupported("barrier"))
    )
    val service  = managed(ExecutionService(backend, _.run()))
    val program  = service.load(ProgramFormat.PTXText, Vec.empty).toOption.get
    val dispatch = KernelDispatch.validated(
      program,
      KernelName("k"),
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get
    val completion = service.launch(dispatch).toOption.get
    assertEquals(
      service.completion(completion),
      Right(CompletionStatus.Failed(ExecutionFailure.Unsupported("barrier")))
    )
  }

  test("named program-global writes are routed only to loaded programs") {
    val observed = AtomicReference(Option.empty[(ProgramId, GlobalName, Long :| Positive0, Vec[Byte])])
    val backend  = TestExecutor(
      (_, _, _) => Right(()),
      _ => Right(()),
      (_, _, _) => Right(()),
      (id, name, offset, bytes) => {
        observed.set(Some((id, name, offset, bytes)))
        Right(())
      }
    )
    val service = managed(ExecutionService(backend))
    val program = service.load(ProgramFormat.HSACodeObjectBinary, Vec[Byte](1)).toOption.get

    assertEquals(
      service.writeGlobal(program, GlobalName("constant_buffer"), 8L, Vec[Byte](4, 3, 2, 1)),
      Right(())
    )
    assertEquals(
      observed.get(),
      Some((program, GlobalName("constant_buffer"), (8L: Long :| Positive0), Vec[Byte](4, 3, 2, 1)))
    )
    assertEquals(
      service.writeGlobal(ProgramId(99L), GlobalName("constant_buffer"), 0L, Vec[Byte](1)),
      Left(ExecutionFailure.NotFound("program 99 is not loaded"))
    )
  }

  test("program loading does not block allocation registration") {
    val loadStarted = CountDownLatch(1)
    val releaseLoad = CountDownLatch(1)
    val backend     = TestExecutor(
      (_, _, _) => {
        loadStarted.countDown()
        val _ = releaseLoad.await(5L, TimeUnit.SECONDS)
        Right(())
      },
      _ => Right(()),
      (_, _, _) => Right(())
    )
    val service = managed(ExecutionService(backend))
    val loaded  = AtomicReference(Option.empty[Either[ExecutionFailure, ProgramId]])
    val loading = Thread(() => loaded.set(Some(service.load(ProgramFormat.HSACodeObjectBinary, Vec.empty))))
    loading.start()
    assert(loadStarted.await(5L, TimeUnit.SECONDS), "program load did not start")

    val registered   = AtomicReference(Option.empty[Either[ExecutionFailure, AllocationId]])
    val completed    = CountDownLatch(1)
    val registration = Thread { () =>
      registered.set(Some(service.registerAllocation(8L, AllocationKind.Shared)))
      completed.countDown()
    }
    registration.start()
    val responsive = completed.await(1L, TimeUnit.SECONDS)
    releaseLoad.countDown()
    loading.join(5000L)
    registration.join(5000L)

    assert(responsive, "allocation registration waited for program loading")
    assert(!loading.isAlive, "program load did not finish")
    assert(!registration.isAlive, "allocation registration did not finish")
    assertEquals(registered.get().map(_.isRight), Some(true))
    assertEquals(loaded.get().map(_.isRight), Some(true))
  }

  test("backend program load and explicit unload are serialized") {
    val loadCount     = AtomicInteger()
    val loadStarted   = CountDownLatch(1)
    val releaseLoad   = CountDownLatch(1)
    val unloadStarted = CountDownLatch(1)
    val backend       = TestExecutor(
      (_, _, _) => {
        if (loadCount.incrementAndGet() == 2) {
          loadStarted.countDown()
          val _ = releaseLoad.await(5L, TimeUnit.SECONDS)
        }
        Right(())
      },
      _ => {
        unloadStarted.countDown()
        Right(())
      },
      (_, _, _) => Right(())
    )
    val service = managed(ExecutionService(backend))
    val first   = service.load(ProgramFormat.SPIRVBinary, Vec.empty).toOption.get
    val loading = Thread { () =>
      val _ = service.load(ProgramFormat.SPIRVBinary, Vec.empty)
      ()
    }
    loading.start()
    assert(loadStarted.await(5L, TimeUnit.SECONDS), "second program load did not start")

    val unloading = Thread { () =>
      val _ = service.unload(first)
      ()
    }
    unloading.start()
    assert(!unloadStarted.await(200L, TimeUnit.MILLISECONDS), "backend unload overlapped backend load")
    releaseLoad.countDown()
    loading.join(5000L)
    unloading.join(5000L)

    assert(!loading.isAlive, "program load did not finish")
    assert(!unloading.isAlive, "program unload did not finish")
    assertEquals(unloadStarted.getCount, 0L)
  }

  test("backend program load and close cleanup are serialized") {
    val loadCount     = AtomicInteger()
    val loadStarted   = CountDownLatch(1)
    val releaseLoad   = CountDownLatch(1)
    val unloadStarted = CountDownLatch(1)
    val backend       = TestExecutor(
      (_, _, _) => {
        if (loadCount.incrementAndGet() == 2) {
          loadStarted.countDown()
          val _ = releaseLoad.await(5L, TimeUnit.SECONDS)
        }
        Right(())
      },
      _ => {
        unloadStarted.countDown()
        Right(())
      },
      (_, _, _) => Right(())
    )
    val service = managed(ExecutionService(backend))
    val _       = service.load(ProgramFormat.SPIRVBinary, Vec.empty).toOption.get
    val loading = Thread { () =>
      val _ = service.load(ProgramFormat.SPIRVBinary, Vec.empty)
      ()
    }
    loading.start()
    assert(loadStarted.await(5L, TimeUnit.SECONDS), "second program load did not start")

    val closing = Thread(() => service.close())
    closing.start()
    assert(!unloadStarted.await(200L, TimeUnit.MILLISECONDS), "close cleanup overlapped backend load")
    releaseLoad.countDown()
    loading.join(5000L)
    closing.join(5000L)

    assert(!loading.isAlive, "program load did not finish")
    assert(!closing.isAlive, "session close did not finish")
    assertEquals(unloadStarted.getCount, 0L)
  }

  test("launch returns a pending completion before scheduled execution") {
    val scheduled = AtomicReference(Option.empty[Runnable])
    val backend   = TestExecutor((_, _, _) => Right(()), _ => Right(()), (_, _, _) => Right(()))
    val service   = managed(ExecutionService(backend, runnable => scheduled.set(Some(runnable))))
    val program   = service.load(ProgramFormat.SPIRVBinary, Vec.empty).toOption.get
    val dispatch  = KernelDispatch.validated(
      program,
      KernelName("k"),
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get

    val completion = service.launch(dispatch).toOption.get
    assertEquals(service.completion(completion), Right(CompletionStatus.Pending))
    scheduled.get().get.run()
    assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
  }

  test("completion wait blocks until scheduled execution publishes its final status") {
    val scheduled = AtomicReference(Option.empty[Runnable])
    val backend   = TestExecutor((_, _, _) => Right(()), _ => Right(()), (_, _, _) => Right(()))
    val service   = managed(ExecutionService(backend, runnable => scheduled.set(Some(runnable))))
    val program   = service.load(ProgramFormat.SPIRVBinary, Vec.empty).toOption.get
    val dispatch  = KernelDispatch.validated(
      program,
      KernelName("k"),
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get
    val completion = service.launch(dispatch).toOption.get
    val started    = CountDownLatch(1)
    val result     = AtomicReference(Option.empty[Either[ExecutionFailure, Unit]])
    val waiting    = Thread { () =>
      started.countDown()
      result.set(Some(service.awaitCompletion(completion)))
    }

    waiting.start()
    assert(started.await(5L, TimeUnit.SECONDS), "completion waiter did not start")
    waiting.join(100L)
    assert(waiting.isAlive, "completion wait returned while execution was pending")
    scheduled.get().get.run()
    waiting.join(5000L)

    assert(!waiting.isAlive, "completion wait was not notified")
    assertEquals(result.get(), Some(Right(())))
  }

  test("accepted dispatch retains its program and allocations until safe release") {
    val scheduled  = AtomicReference(Option.empty[Runnable])
    val backend    = TestExecutor((_, _, _) => Right(()), _ => Right(()), (_, _, _) => Right(()))
    val service    = managed(ExecutionService(backend, runnable => scheduled.set(Some(runnable))))
    val allocation = service.registerAllocation(8L, AllocationKind.Shared).toOption.get
    val program    = service.load(ProgramFormat.SPIRVBinary, Vec.empty).toOption.get
    val dispatch   = KernelDispatch.validated(
      program,
      KernelName("retained"),
      Dimensions.One,
      Dimensions.One,
      Vec(KernelArgument.Allocation(allocation, 0L))
    ).toOption.get
    val completion = service.launch(dispatch).toOption.get

    assertEquals(
      service.releaseCompletion(completion),
      Left(ExecutionFailure.InvalidRequest(s"completion ${completion.value} is still pending"))
    )
    assertEquals(
      service.unload(program),
      Left(ExecutionFailure.InvalidRequest(s"program ${program.value} is retained by pending execution"))
    )
    assertEquals(
      service.releaseAllocation(allocation),
      Left(ExecutionFailure.InvalidRequest(s"allocation ${allocation.value} is retained by completion"))
    )

    scheduled.get().get.run()
    assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
    assertEquals(service.unload(program), Right(()))
    assertEquals(
      service.releaseAllocation(allocation),
      Left(ExecutionFailure.InvalidRequest(s"allocation ${allocation.value} is retained by completion"))
    )
    assertEquals(service.releaseCompletion(completion), Right(()))
    assertEquals(service.releaseAllocation(allocation), Right(()))
  }

  test("accepted dispatch retains allocations referenced only by pointer relocations") {
    val scheduled = AtomicReference(Option.empty[Runnable])
    val source    = AllocationId(1L)
    val target    = AllocationId(2L)
    val backend   = TestExecutor(
      (_, _, _) => Right(()),
      _ => Right(()),
      (_, memory, _) => {
        memory.recordPointerRelocation(PointerRelocation(
          source = RelocationSource.Allocation(source),
          sourceOffset = 0L,
          target = target,
          targetOffset = 0L
        ))
        Right(())
      }
    )
    val service = managed(ExecutionService(backend, runnable => scheduled.set(Some(runnable))))
    assertEquals(service.registerAllocation(8L, AllocationKind.Shared), Right(source))
    assertEquals(service.registerAllocation(8L, AllocationKind.Shared), Right(target))
    val program    = service.load(ProgramFormat.SPIRVBinary, Vec.empty).toOption.get
    val relocation = PointerRelocation(
      source = RelocationSource.Allocation(source),
      sourceOffset = 0L,
      target = target,
      targetOffset = 0L
    )
    val dispatch = KernelDispatch
      .validated(
        program,
        KernelName("relocate"),
        Dimensions.One,
        Dimensions.One,
        Vec.empty,
        pointerRelocations = Vec(relocation)
      )
      .toOption
      .get
    val completion = service.launch(dispatch).toOption.get

    assert(service.releaseAllocation(source).isLeft)
    assert(service.releaseAllocation(target).isLeft)
    scheduled.get().get.run()
    assertEquals(
      service.completionPointerRelocations(completion),
      Right(Vec(PointerRelocation(
        source = RelocationSource.Allocation(source),
        sourceOffset = 0L,
        target = target,
        targetOffset = 0L
      )))
    )
    assertEquals(service.releaseCompletion(completion), Right(()))
    assertEquals(service.releaseAllocation(source), Right(()))
    assertEquals(service.releaseAllocation(target), Right(()))
  }

  test("dispatches in one execution session run in submission order") {
    val scheduled = mutable.ArrayBuffer.empty[Runnable]
    val launched  = mutable.ArrayBuffer.empty[String]
    val backend   = TestExecutor(
      (_, _, _) => Right(()),
      _ => Right(()),
      (dispatch, _, _) => {
        launched += dispatch.kernelName.value
        Right(())
      }
    )
    val service                = managed(ExecutionService(backend, scheduled += _))
    val program                = service.load(ProgramFormat.SPIRVBinary, Vec.empty).toOption.get
    def dispatch(name: String) =
      KernelDispatch.validated(program, kernelName(name), Dimensions.One, Dimensions.One, Vec.empty).toOption.get

    val first  = service.launch(dispatch("first")).toOption.get
    val second = service.launch(dispatch("second")).toOption.get
    assertEquals(scheduled.size, 1)
    assertEquals(service.completion(first), Right(CompletionStatus.Pending))
    assertEquals(service.completion(second), Right(CompletionStatus.Pending))

    scheduled.head.run()
    assertEquals(launched.toList, List("first", "second"))
    assertEquals(service.completion(first), Right(CompletionStatus.Succeeded))
    assertEquals(service.completion(second), Right(CompletionStatus.Succeeded))
  }

  test("closing a session cancels the dispatch currently occupying its worker") {
    val started = AtomicReference(false)
    val stopped = AtomicReference(false)
    val backend = TestExecutor(
      (_, _, _) => Right(()),
      _ => Right(()),
      (_, _, control) => {
        started.set(true)
        while (!control.cancellation.isCancelled) Thread.sleep(1L)
        stopped.set(true)
        control.checkpoint().left.map(fault => ExecutionFailure.Failed(fault.message))
      }
    )
    val worker  = AtomicReference(Option.empty[Thread])
    val service = managed(ExecutionService(
      backend,
      runnable => {
        val thread = Thread(runnable, "gemstone-execution-cancellation-test")
        worker.set(Some(thread))
        thread.start()
      }
    ))
    val program  = service.load(ProgramFormat.SPIRVBinary, Vec.empty).toOption.get
    val dispatch = KernelDispatch.validated(
      program,
      KernelName("spin"),
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get
    val _        = service.launch(dispatch).toOption.get
    val deadline = PlatformTime.monotonicNanos + 5_000_000_000L
    while (!started.get() && PlatformTime.monotonicNanos < deadline) Thread.sleep(1L)
    assert(started.get(), "execution worker did not start")

    service.close()
    worker.get().get.join(5000L)
    assert(stopped.get(), "running dispatch did not observe session cancellation")
    assert(!worker.get().get.isAlive, "execution worker remained alive after cancellation")
  }

  for (groups <- Vector(1, 2, 3, 5, 9))
    test(s"$groups queued follow-up launches keep separate budgets and continue after a failed dispatch") {
      val scheduled = mutable.ArrayBuffer.empty[Runnable]
      val accepted  = mutable.ArrayBuffer.empty[(String, Long)]
      val backend   = TestExecutor(
        (_, _, _) => Right(()),
        _ => Right(()),
        (dispatch, _, control) => {
          val requested = if (dispatch.kernelName.value == "exhaust") groups * 7 + 1 else groups * 7
          @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
          var result: Either[ExecutionFailure, Unit] = Right(())
          (0 until requested).foreach { _ =>
            if (result.isRight)
              result = control.checkpoint().left.map(fault => ExecutionFailure.Failed(fault.message))
          }
          accepted += dispatch.kernelName.value -> control.usedInstructions
          result
        }
      )
      val maximum     = (groups * 7L).refineEither[Positive].fold(message => fail(message), value => value)
      val limits      = ExecutionLimits(maximum, 1)
      val service     = managed(ExecutionService(backend, scheduled += _, limits))
      val program     = service.load(ProgramFormat.SPIRVBinary, Vec.empty).toOption.get
      val names       = Vector("exhaust") ++ Vector.tabulate(groups)(i => s"next-$i")
      val completions = Vector.newBuilder[CompletionId]
      names.foreach { name =>
        val dispatch = KernelDispatch
          .validated(program, kernelName(name), Dimensions.One, Dimensions.One, Vec.empty)
          .toOption.get
        completions += service.launch(dispatch).toOption.get
      }
      val launched = completions.result()
      assertEquals(scheduled.size, 1)
      launched.foreach(id => assertEquals(service.completion(id), Right(CompletionStatus.Pending)))
      scheduled.head.run()
      assert(service.completion(launched.head).exists {
        case CompletionStatus.Failed(_) => true
        case _                          => false
      })
      launched.tail.foreach(id => assertEquals(service.completion(id), Right(CompletionStatus.Succeeded)))
      assertEquals(accepted.toVector, names.map(_ -> (groups * 7).toLong))
      launched.foreach(id => assertEquals(service.releaseCompletion(id), Right(())))
    }

}
