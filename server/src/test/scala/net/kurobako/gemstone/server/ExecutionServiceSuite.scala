package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.AllocationKind

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import scala.collection.mutable

class ExecutionServiceSuite extends munit.FunSuite {
  private final class TestExecutor(
      loadFn: (ProgramId, ProgramFormat, Col[Byte]) => Either[ExecutionFailure, Unit],
      unloadFn: ProgramId => Either[ExecutionFailure, Unit],
      launchFn: (KernelDispatch, ExecutionMemory, ExecutionControl) => Either[ExecutionFailure, Unit],
      writeGlobalFn: (ProgramId, String, Long, Col[Byte]) => Either[ExecutionFailure, Unit] = (_, _, _, _) => Right(())
  ) extends ProgramExecutor {
    override def load(id: ProgramId, format: ProgramFormat, image: Col[Byte]) = loadFn(id, format, image)
    override def unload(id: ProgramId)                                        = unloadFn(id)
    override def launch(dispatch: KernelDispatch, memory: ExecutionMemory, control: ExecutionControl) =
      launchFn(dispatch, memory, control)
    override def writeGlobal(id: ProgramId, name: String, offset: Long, bytes: Col[Byte]) =
      writeGlobalFn(id, name, offset, bytes)
  }

  test("program, allocation, dispatch, and completion lifecycle is process-neutral") {
    val loaded  = AtomicReference(Option.empty[ProgramId])
    val backend = TestExecutor(
      (id, format, image) =>
        if (format == ProgramFormat.SPIRV && image == Col[Byte](3, 2, 35, 7)) {
          loaded.set(Some(id))
          Right(())
        } else Left(ExecutionFailure.InvalidRequest("unexpected module")),
      id => Either.cond(loaded.get().contains(id), loaded.set(None), ExecutionFailure.NotFound("not loaded")),
      (dispatch, memory, _) =>
        dispatch.arguments match {
          case Col(
                KernelArgument.Allocation(id, offset),
                KernelArgument.Scalar(bytes),
                KernelArgument.Local(64L)
              ) =>
            memory.write(id, offset, bytes)
          case _ => Left(ExecutionFailure.InvalidRequest("unexpected arguments"))
        }
    )
    val service    = ExecutionService(backend, _.run())
    val allocation = AllocationId.fromLong(7L).toOption.get
    assertEquals(service.registerAllocation(allocation, 8L, AllocationKind.Shared), Right(()))
    assertEquals(service.write(allocation, 0L, Col[Byte](1, 2, 3, 4)), Right(()))

    val program  = service.load(ProgramFormat.SPIRV, Col[Byte](3, 2, 35, 7)).toOption.get
    val dispatch = KernelDispatch.create(
      program,
      "write",
      Dimensions.One,
      Dimensions.One,
      Col(
        KernelArgument.Allocation(allocation, 2L),
        KernelArgument.Scalar(Col[Byte](9, 8)),
        KernelArgument.Local(64L)
      )
    ).toOption.get
    val completion = service.launch(dispatch).toOption.get
    assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
    assertEquals(service.completionDirtyRanges(completion), Right(Col(DirtyRange(allocation, 0L, 8))))
    assertEquals(service.read(allocation, 0L, 8), Right(Col[Byte](1, 2, 9, 8, 0, 0, 0, 0)))
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
    val service    = ExecutionService(backend, _.run())
    val program    = service.load(ProgramFormat.PTX, Col.empty).toOption.get
    val dispatch   = KernelDispatch.create(program, "k", Dimensions.One, Dimensions.One, Col.empty).toOption.get
    val completion = service.launch(dispatch).toOption.get
    assertEquals(
      service.completion(completion),
      Right(CompletionStatus.Failed(ExecutionFailure.Unsupported("barrier")))
    )
  }

  test("named program-global writes are routed only to loaded programs") {
    val observed = AtomicReference(Option.empty[(ProgramId, String, Long, Col[Byte])])
    val backend  = TestExecutor(
      (_, _, _) => Right(()),
      _ => Right(()),
      (_, _, _) => Right(()),
      (id, name, offset, bytes) => {
        observed.set(Some((id, name, offset, bytes)))
        Right(())
      }
    )
    val service = ExecutionService(backend)
    val program = service.load(ProgramFormat.AMDGPUCodeObject, Col[Byte](1)).toOption.get

    assertEquals(service.writeGlobal(program, "constant_buffer", 8L, Col[Byte](4, 3, 2, 1)), Right(()))
    assertEquals(observed.get(), Some((program, "constant_buffer", 8L, Col[Byte](4, 3, 2, 1))))
    assertEquals(
      service.writeGlobal(ProgramId.fromLong(99L).toOption.get, "constant_buffer", 0L, Col[Byte](1)),
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
    val service = ExecutionService(backend)
    val loaded  = AtomicReference(Option.empty[Either[ExecutionFailure, ProgramId]])
    val loading = Thread(() => loaded.set(Some(service.load(ProgramFormat.AMDGPUCodeObject, Col.empty))))
    loading.start()
    assert(loadStarted.await(5L, TimeUnit.SECONDS), "program load did not start")

    val allocation   = AllocationId.fromLong(23L).toOption.get
    val registered   = AtomicReference(Option.empty[Either[ExecutionFailure, Unit]])
    val completed    = CountDownLatch(1)
    val registration = Thread { () =>
      registered.set(Some(service.registerAllocation(allocation, 8L, AllocationKind.Shared)))
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
    assertEquals(registered.get(), Some(Right(())))
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
    val service = ExecutionService(backend)
    val first   = service.load(ProgramFormat.SPIRV, Col.empty).toOption.get
    val loading = Thread { () =>
      val _ = service.load(ProgramFormat.SPIRV, Col.empty)
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
    val service = ExecutionService(backend)
    val _       = service.load(ProgramFormat.SPIRV, Col.empty).toOption.get
    val loading = Thread { () =>
      val _ = service.load(ProgramFormat.SPIRV, Col.empty)
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
    val service   = ExecutionService(backend, runnable => scheduled.set(Some(runnable)))
    val program   = service.load(ProgramFormat.SPIRV, Col.empty).toOption.get
    val dispatch  = KernelDispatch.create(program, "k", Dimensions.One, Dimensions.One, Col.empty).toOption.get

    val completion = service.launch(dispatch).toOption.get
    assertEquals(service.completion(completion), Right(CompletionStatus.Pending))
    scheduled.get().get.run()
    assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
  }

  test("accepted dispatch retains its program and allocations until safe release") {
    val scheduled  = AtomicReference(Option.empty[Runnable])
    val backend    = TestExecutor((_, _, _) => Right(()), _ => Right(()), (_, _, _) => Right(()))
    val service    = ExecutionService(backend, runnable => scheduled.set(Some(runnable)))
    val allocation = AllocationId.fromLong(17L).toOption.get
    assertEquals(service.registerAllocation(allocation, 8L, AllocationKind.Shared), Right(()))
    val program  = service.load(ProgramFormat.SPIRV, Col.empty).toOption.get
    val dispatch = KernelDispatch.create(
      program,
      "retained",
      Dimensions.One,
      Dimensions.One,
      Col(KernelArgument.Allocation(allocation, 0L))
    ).toOption.get
    val completion = service.launch(dispatch).toOption.get

    assertEquals(
      service.releaseCompletion(completion),
      Left(ExecutionFailure.InvalidRequest(s"completion ${completion.toLong} is still pending"))
    )
    assertEquals(
      service.unload(program),
      Left(ExecutionFailure.InvalidRequest(s"program ${program.toLong} is retained by pending execution"))
    )
    assertEquals(
      service.releaseAllocation(allocation),
      Left(ExecutionFailure.InvalidRequest(s"allocation ${allocation.toLong} is retained by completion"))
    )

    scheduled.get().get.run()
    assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
    assertEquals(service.unload(program), Right(()))
    assertEquals(
      service.releaseAllocation(allocation),
      Left(ExecutionFailure.InvalidRequest(s"allocation ${allocation.toLong} is retained by completion"))
    )
    assertEquals(service.releaseCompletion(completion), Right(()))
    assertEquals(service.releaseAllocation(allocation), Right(()))
  }

  test("accepted dispatch retains allocations referenced only by pointer relocations") {
    val scheduled = AtomicReference(Option.empty[Runnable])
    val source    = AllocationId.fromLong(18L).toOption.get
    val target    = AllocationId.fromLong(19L).toOption.get
    val backend   = TestExecutor(
      (_, _, _) => Right(()),
      _ => Right(()),
      (_, memory, _) => {
        memory.recordPointerRelocation(PointerRelocation(Some(source), None, 0L, target, 0L))
        Right(())
      }
    )
    val service = ExecutionService(backend, runnable => scheduled.set(Some(runnable)))
    assertEquals(service.registerAllocation(source, 8L, AllocationKind.Shared), Right(()))
    assertEquals(service.registerAllocation(target, 8L, AllocationKind.Shared), Right(()))
    val program    = service.load(ProgramFormat.SPIRV, Col.empty).toOption.get
    val relocation = PointerRelocation(Some(source), None, 0L, target, 0L)
    val dispatch   = KernelDispatch
      .create(program, "relocate", Dimensions.One, Dimensions.One, Col.empty, pointerRelocations = Col(relocation))
      .toOption
      .get
    val completion = service.launch(dispatch).toOption.get

    assert(service.releaseAllocation(source).isLeft)
    assert(service.releaseAllocation(target).isLeft)
    scheduled.get().get.run()
    assertEquals(
      service.completionPointerRelocations(completion),
      Right(Col(PointerRelocation(Some(source), None, 0L, target, 0L)))
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
        launched += dispatch.kernelName
        Right(())
      }
    )
    val service                = ExecutionService(backend, scheduled += _)
    val program                = service.load(ProgramFormat.SPIRV, Col.empty).toOption.get
    def dispatch(name: String) =
      KernelDispatch.create(program, name, Dimensions.One, Dimensions.One, Col.empty).toOption.get

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
    val service = ExecutionService(
      backend,
      runnable => {
        val thread = Thread(runnable, "gemstone-execution-cancellation-test")
        worker.set(Some(thread))
        thread.start()
      }
    )
    val program  = service.load(ProgramFormat.SPIRV, Col.empty).toOption.get
    val dispatch = KernelDispatch.create(program, "spin", Dimensions.One, Dimensions.One, Col.empty).toOption.get
    val _        = service.launch(dispatch).toOption.get
    val deadline = System.nanoTime() + 5_000_000_000L
    while (!started.get() && System.nanoTime() < deadline) Thread.sleep(1L)
    assert(started.get(), "execution worker did not start")

    service.close()
    worker.get().get.join(5000L)
    assert(stopped.get(), "running dispatch did not observe session cancellation")
    assert(!worker.get().get.isAlive, "execution worker remained alive after cancellation")
  }
}
