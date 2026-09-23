package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.{NoNul, Vec}
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.memory.{AllocationKind, GuestAddress, MemoryValueByteWidth}
import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.{
  AddressSpace,
  Event,
  Fault,
  KernelTask,
  Memory,
  MemoryRegion,
  ObservationContext,
  Scheduler,
  TaskControl,
  TaskLimits
}

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import scala.annotation.tailrec
import scala.collection.mutable

/** Process-neutral program and memory receiver shared by every native API facade. */
final class ExecutionService(
    backends: List[RuntimeBackend],
    taskLimits: TaskLimits = TaskLimits.Default,
    scheduler: Scheduler = Scheduler.Sequential
) extends AutoCloseable {
  private val DirtyChunkSize         = 64L * 1024L
  private val SyntheticAddressBase   = 0x400000000000L
  private val SyntheticAddressStride = 0x100000000L

  /** Mutable byte storage is deliberately confined behind this service's state lock. Allocation traffic is the
    * simulator's hot data path; rebuilding a multi-megabyte immutable collection for every mirrored chunk creates
    * unbounded GC latency. Neither the array nor a mutable view crosses the VM memory boundary.
    */
  private final case class RemoteAllocation(
      kind: AllocationKind,
      bytes: Array[Byte],
      address: Option[GuestAddress] = None
  ) {
    val size: Int :| Positive = bytes.length.refineUnsafe[Positive]
  }
  private final case class CompletionRecord(
      status: CompletionStatus,
      program: ProgramId,
      allocations: Set[AllocationId],
      pointerRelocations: Vec[PointerRelocation],
      prints: Vec[PrintEvent],
      cancellation: TaskControl.Cancellation.Source
  )
  private final case class State(
      allocations: Map[AllocationId, RemoteAllocation],
      programs: Map[ProgramId, RuntimeBackend.Program],
      completions: Map[CompletionId, CompletionRecord],
      nextAllocation: AllocationId,
      nextProgram: ProgramId,
      nextCompletion: CompletionId
  )

  private val state = AtomicReference(
    State(Map.empty, Map.empty, Map.empty, AllocationId(1L), ProgramId(1L), CompletionId(1L))
  )
  private val stateLock    = ReentrantLock()
  private val stateChanged = stateLock.newCondition()
  private val dirtyChunks  =
    mutable.HashMap.empty[CompletionId, mutable.HashMap[AllocationId, mutable.LongMap[Unit]]]
  private val pendingExecutions = mutable.Queue.empty[(CompletionId, RuntimeBackend.Program, KernelTask, TaskControl)]
  private val programLifecycleLock = ReentrantLock()
  private val observer             = CompletionObserver()
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var executionWorkerScheduled = false
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var closed           = false
  private val boundAllocations = mutable.Map.empty[ProgramId, Set[AllocationId]]
  private val bindingPrograms  = mutable.Set.empty[ProgramId]

  def registerAllocation(
      size: Long :| Positive,
      kind: AllocationKind
  ): Either[ExecutionFailure, AllocationId] = stateLock.withLock {
    val current = state.get()
    if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
    else if (size > Int.MaxValue.toLong)
      Left(ExecutionFailure.InvalidRequest(s"allocation size is outside the supported range: $size"))
    else {
      val id         = current.nextAllocation
      val allocation = RemoteAllocation(kind, Array.ofDim[Byte](size.toInt))
      state.set(
        current.copy(
          allocations = current.allocations.updated(id, allocation),
          nextAllocation = current.nextAllocation.next
        )
      )
      Right(id)
    }
  }

  /** Register an immutable client-address alias; storage and lifetime remain owned by the allocation. */
  def registerAllocationAddress(id: AllocationId, address: GuestAddress): Either[ExecutionFailure, Unit] =
    stateLock.withLock {
      val current = state.get()
      if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
      else current.allocations.get(id) match {
        case None             => Left(ExecutionFailure.NotFound(s"allocation ${id.value} is not registered"))
        case Some(allocation) =>
          val size = allocation.size.toLong
          if (address.value <= 0 || address.value > Long.MaxValue - size)
            Left(ExecutionFailure.InvalidRequest("allocation address alias has an invalid or overflowing range"))
          else if (allocation.address.contains(address)) Right(())
          else if (allocation.address.nonEmpty)
            Left(ExecutionFailure.InvalidRequest("allocation address alias cannot be remapped"))
          else if (
            current.allocations.values.view.exists(other =>
              other.address.exists(base =>
                address.value < base.value + other.bytes.length && base.value < address.value + size
              )
            )
          ) Left(ExecutionFailure.InvalidRequest("allocation address aliases overlap"))
          else {
            state.set(current.copy(allocations =
              current.allocations.updated(id, allocation.copy(address = Some(address)))
            ))
            Right(())
          }
      }
    }

  def releaseAllocation(id: AllocationId): Either[ExecutionFailure, Unit] = stateLock.withLock {
    val current = state.get()
    if (!current.allocations.contains(id))
      Left(ExecutionFailure.NotFound(s"allocation ${id.value} is not registered"))
    else if (boundAllocations.values.view.exists(_.contains(id)))
      Left(ExecutionFailure.InvalidRequest(s"allocation ${id.value} is retained by a program global"))
    else if (current.completions.values.view.exists(_.allocations.contains(id)))
      Left(ExecutionFailure.InvalidRequest(s"allocation ${id.value} is retained by completion"))
    else {
      state.set(current.copy(allocations = current.allocations.removed(id)))
      Right(())
    }
  }

  def load(encoding: String, image: Vec[Byte]): Either[ExecutionFailure, ProgramId] = {
    // Reserve the identity under the state lock, but do not hold that lock
    // while decoding/preparing a potentially large code object. HSA memory
    // and AQL connections share this service; keeping the old lock
    // around the executor call made hipMalloc wait behind code-object loading.
    val reserved = stateLock.withLock {
      val current = state.get()
      if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
      else {
        val id = current.nextProgram
        state.set(current.copy(nextProgram = current.nextProgram.next))
        Right(id)
      }
    }
    reserved match {
      case Left(error) => Left(error)
      case Right(id)   =>
        val loaded = programLifecycleLock.withLock {
          backendFor(encoding).flatMap(backend => backend.load(RuntimeBackend.Services(observer, scheduler), image))
        }.left.map(runtimeFailure(_))
        loaded match {
          case Left(error)    => Left(error)
          case Right(program) =>
            program.bindProgramId(id.value)
            val accepted = stateLock.withLock {
              if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
              else {
                val current = state.get()
                state.set(current.copy(programs = current.programs.updated(id, program)))
                Right(())
              }
            }
            accepted match {
              case Right(_)    => Right(id)
              case Left(error) =>
                programLifecycleLock.withLock(program.close())
                Left(error)
            }
        }
    }
  }

  def unload(id: ProgramId): Either[ExecutionFailure, Unit] = {
    // Remove the program from the service state before entering the backend so
    // dispatch creation cannot race the unload. Backend lifecycle calls use their
    // own lock and never hold the state lock while doing potentially slow
    // work.
    val reserved = stateLock.withLock {
      val current = state.get()
      if (!current.programs.contains(id))
        Left(ExecutionFailure.NotFound(s"program ${id.value} is not loaded"))
      else if (bindingPrograms.contains(id))
        Left(ExecutionFailure.InvalidRequest(s"program ${id.value} has a global binding in progress"))
      else if (
        current.completions.values.view.exists(record =>
          record.program == id && record.status == CompletionStatus.Pending
        )
      )
        Left(ExecutionFailure.InvalidRequest(s"program ${id.value} is retained by pending execution"))
      else {
        val program = current.programs(id)
        state.set(current.copy(programs = current.programs.removed(id)))
        Right(program)
      }
    }
    reserved match {
      case Left(error)    => Left(error)
      case Right(program) =>
        programLifecycleLock.withLock(program.close())
        stateLock.withLock { val _ = boundAllocations.remove(id) }
        Right(())
    }
  }

  def kernel(id: ProgramId, name: KernelName): Either[ExecutionFailure, KernelMetadata] =
    program(id).flatMap(_.kernel(net.kurobako.gemstone.isa.KernelName(name.value)).left.map(runtimeFailure(_)))
      .map(toWireKernel(_))

  def catalog(id: ProgramId): Either[ExecutionFailure, ProgramCatalog] =
    program(id).flatMap(_.symbols.left.map(runtimeFailure(_))).map(toWireCatalog(_))

  def global(id: ProgramId, name: GlobalName): Either[ExecutionFailure, ProgramGlobal] =
    program(id).flatMap { loaded =>
      loaded.globals.toRight(ExecutionFailure.Unsupported("program global metadata is unavailable"))
        .flatMap(_.image(net.kurobako.gemstone.isa.GlobalName(name.value)).left.map(runtimeFailure(_)))
        .flatMap(image => ProgramGlobal.validated(image.size, image.initial).left.map(ExecutionFailure.Failed(_)))
    }

  /** Bind outside the state lock while reserving program/allocation ownership across backend preparation. */
  def bindGlobal(
      id: ProgramId,
      name: GlobalName,
      allocation: AllocationId,
      offset: Long :| Positive0
  ): Either[ExecutionFailure, Unit] = {
    val result = programLifecycleLock.withLock {
      val reserved = stateLock.withLock {
        val current = state.get()
        if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
        else if (!current.programs.contains(id))
          Left(ExecutionFailure.NotFound(s"program ${id.value} is not loaded"))
        else if (current.completions.values.view.exists(r => r.program == id && r.status == CompletionStatus.Pending))
          Left(ExecutionFailure.InvalidRequest("cannot bind globals during pending execution"))
        else current.allocations.get(allocation) match {
          case None =>
            Left(ExecutionFailure.NotFound(s"allocation ${allocation.value} is not registered"))
          case Some(value) =>
            val allocationSize = value.size.toLong.refineUnsafe[Positive]
            if (offset > allocationSize)
              Left(ExecutionFailure.InvalidRequest("global binding offset exceeds allocation"))
            else {
              val previous = boundAllocations.getOrElse(id, Set.empty)
              boundAllocations.update(id, previous + allocation)
              bindingPrograms += id
              Right(allocationSize -> previous)
            }
        }
      }
      reserved match {
        case Left(error)             => Left(error)
        case Right((size, previous)) =>
          val attempted = try program(id).flatMap { loaded =>
              loaded.globals.toRight(ExecutionFailure.Unsupported("program globals cannot be bound"))
                .flatMap(_.bind(
                  net.kurobako.gemstone.isa.GlobalName(name.value),
                  MemoryRegion(allocation.value),
                  offset,
                  size,
                  BindingMemory
                ).left.map(runtimeFailure(_)))
            }
          catch {
            case scala.util.control.NonFatal(error) => Left(ExecutionFailure.Failed(error.toString))
          }
          stateLock.withLock {
            bindingPrograms -= id
            if (attempted.isLeft || closed) {
              if (previous.isEmpty) { val _ = boundAllocations.remove(id) }
              else boundAllocations.update(id, previous)
            }
            if (closed) Left(ExecutionFailure.Failed("execution session is closed")) else attempted
          }
      }
    }
    cleanupClosedSession()
    result
  }

  private object BindingMemory extends Memory with AddressSpace {
    override def read(region: MemoryRegion, offset: Long :| Positive0, size: Int :| Positive0) =
      ExecutionService.this.read(allocationId(region), offset, size).left.map(memoryFault(_))
    override def write(region: MemoryRegion, offset: Long :| Positive0, bytes: Vec[Byte]) =
      ExecutionService.this.write(allocationId(region), offset, bytes).left.map(memoryFault(_))
    override def atomicModifyValue(region: MemoryRegion, offset: Long :| Positive0, size: MemoryValueByteWidth)(
        update: Long => Long
    ): Either[Fault, Long] =
      ExecutionService.this.atomicModifyValue(allocationId(region), offset, size)(update).left.map(memoryFault(_))
    override def address(region: MemoryRegion, offset: Long :| Positive0): Either[Fault, GuestAddress] =
      regionAddress(region, offset)
    override def locate(address: GuestAddress, size: Int :| Positive0): Either[Fault, AddressSpace.Location] =
      locateAddress(address, size)
  }

  def writeGlobal(
      id: ProgramId,
      name: GlobalName,
      offset: Long :| Positive0,
      bytes: Vec[Byte]
  ): Either[ExecutionFailure, Unit] = {
    val accepted = stateLock.withLock {
      val current = state.get()
      if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
      else if (!current.programs.contains(id))
        Left(ExecutionFailure.NotFound(s"program ${id.value} is not loaded"))
      else Right(())
    }
    accepted match {
      case Left(error) => Left(error)
      case Right(_)    => programLifecycleLock.withLock {
          program(id).flatMap { loaded =>
            loaded.globals.toRight(ExecutionFailure.Unsupported("program globals are not writable"))
              .flatMap(_.write(
                net.kurobako.gemstone.isa.GlobalName(name.value),
                offset,
                bytes
              ).left.map(runtimeFailure(_)))
          }
        }
    }
  }

  def launch(program: ProgramId, task: KernelTask): Either[ExecutionFailure, CompletionId] = {
    val accepted = stateLock.withLock {
      val current = state.get()
      if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
      else if (!current.programs.contains(program))
        Left(ExecutionFailure.NotFound(s"program ${program.value} is not loaded"))
      else if (bindingPrograms.contains(program))
        Left(ExecutionFailure.InvalidRequest("program global binding is in progress"))
      else validateTask(task, current) match {
        case Left(error) => Left(error)
        case Right(_)    =>
          val id           = current.nextCompletion
          val cancellation = TaskControl.Cancellation.Source()
          val control      = TaskControl.forScheduler(taskLimits, cancellation, ObservationContext(id.value), scheduler)
          val loaded       = current.programs(program)
          val addressed    = current.allocations.view.collect {
            case (allocation, value) if value.address.nonEmpty => allocation
          }.toSet
          observer.register(ObservationContext(id.value))(event => observe(id, event)).left.map(runtimeFailure(_)).map {
            _ =>
              state.set(
                current.copy(
                  completions = current.completions.updated(
                    id,
                    CompletionRecord(
                      CompletionStatus.Pending,
                      program,
                      addressed ++ referencedAllocations(task) ++ boundAllocations.getOrElse(program, Set.empty),
                      Vec.empty,
                      Vec.empty,
                      cancellation
                    )
                  ),
                  nextCompletion = current.nextCompletion.next
                )
              )
              dirtyChunks.update(id, mutable.HashMap.empty)
              (id, loaded, task, control)
          }
      }
    }
    accepted.foreach { case (id, loaded, task, control) =>
      val startWorker = stateLock.withLock {
        pendingExecutions.enqueue((id, loaded, task, control))
        if (executionWorkerScheduled) false
        else {
          executionWorkerScheduled = true
          true
        }
      }
      if (startWorker)
        try scheduler.schedule(() => runPendingExecutions())
        catch {
          case scala.util.control.NonFatal(error) => failPendingExecutions(
              ExecutionFailure.Failed(
                s"cannot schedule execution: ${Option(error.getMessage).getOrElse(error.getClass.getName)}"
              )
            )
        }
    }
    accepted.map(_._1)
  }

  private def runPendingExecutions(): Unit = {
    def dequeueLocked(): Option[(CompletionId, RuntimeBackend.Program, KernelTask, TaskControl)] = {
      val value = pendingExecutions.dequeueFirst(_ => true)
      if (value.isEmpty) executionWorkerScheduled = false
      value
    }
    @tailrec def loop(next: Option[(CompletionId, RuntimeBackend.Program, KernelTask, TaskControl)]): Unit =
      next match {
        case None                               => ()
        case Some((id, program, task, control)) =>
          val status =
            try
              programLifecycleLock.withLock {
                program.execute(task, completionMemory(id), control)
              }
                .fold(error => CompletionStatus.Failed(runtimeFailure(error)), _ => CompletionStatus.Succeeded)
            catch {
              case scala.util.control.NonFatal(error) =>
                CompletionStatus.Failed(
                  ExecutionFailure.Failed(
                    s"program executor failed: ${Option(error.getMessage).getOrElse(error.getClass.getName)}"
                  )
                )
            }
          val following = stateLock.withLock {
            val current = state.get()
            current.completions.get(id).foreach { record =>
              state.set(current.copy(completions = current.completions.updated(id, record.copy(status = status))))
              observer.remove(ObservationContext(id.value))
              stateChanged.signalAll()
            }
            dequeueLocked()
          }
          loop(following)
      }
    loop(stateLock.withLock(dequeueLocked()))
    cleanupClosedSession()
  }

  private def failPendingExecutions(error: ExecutionFailure): Unit = {
    stateLock.withLock {
      val ids = pendingExecutions.map(_._1).toSet
      ids.foreach(id => observer.remove(ObservationContext(id.value)))
      pendingExecutions.clear()
      executionWorkerScheduled = false
      val current = state.get()
      state.set(current.copy(completions = current.completions.map { case (id, record) =>
        if (ids.contains(id)) id -> record.copy(status = CompletionStatus.Failed(error)) else id -> record
      }))
      stateChanged.signalAll()
    }
    cleanupClosedSession()
  }

  override def close(): Unit = {
    stateLock.withLock {
      if (!closed) {
        closed = true
        state.get().completions.values.view.foreach(_.cancellation.cancel())
        val cancelled = pendingExecutions.map(_._1).toSet
        cancelled.foreach(id => observer.remove(ObservationContext(id.value)))
        pendingExecutions.clear()
        val current = state.get()
        state.set(current.copy(completions = current.completions.map { case (id, record) =>
          if (cancelled.contains(id))
            id -> record.copy(status =
              CompletionStatus.Failed(ExecutionFailure.Failed("execution session closed before dispatch"))
            )
          else id -> record
        }))
        stateChanged.signalAll()
      }
    }
    cleanupClosedSession()
  }

  private def cleanupClosedSession(): Unit = {
    val programs = stateLock.withLock {
      if (!closed || executionWorkerScheduled || bindingPrograms.nonEmpty) None
      else {
        val current = state.get()
        state.set(current.copy(allocations = Map.empty, programs = Map.empty, completions = Map.empty))
        dirtyChunks.clear()
        boundAllocations.clear()
        Some(current.programs.values.toVector)
      }
    }
    programs.foreach(loaded => programLifecycleLock.withLock(loaded.foreach(_.close())))
  }

  def completion(id: CompletionId): Either[ExecutionFailure, CompletionStatus] =
    state.get().completions.get(id).map(_.status)
      .toRight(ExecutionFailure.NotFound(s"completion ${id.value} does not exist"))

  def awaitCompletion(id: CompletionId): Either[ExecutionFailure, Unit] = stateLock.withLock {
    try {
      @tailrec def await(result: Either[ExecutionFailure, CompletionStatus]): Either[ExecutionFailure, Unit] =
        if (result.contains(CompletionStatus.Pending) && !closed) {
          stateChanged.await()
          await(completion(id))
        } else
          result.flatMap {
            case CompletionStatus.Succeeded     => Right(())
            case CompletionStatus.Failed(error) => Left(error)
            case CompletionStatus.Pending       =>
              Left(ExecutionFailure.Failed("execution session closed before completion"))
          }
      await(completion(id))
    } catch {
      case _: InterruptedException =>
        Thread.currentThread().interrupt()
        Left(ExecutionFailure.Failed("completion wait was interrupted"))
    }
  }

  def completionDirtyRanges(id: CompletionId): Either[ExecutionFailure, Vec[DirtyRange]] = stateLock.withLock {
    val current = state.get()
    current.completions.get(id).map(_.status) match {
      case None                           => Left(ExecutionFailure.NotFound(s"completion ${id.value} does not exist"))
      case Some(CompletionStatus.Pending) =>
        Left(ExecutionFailure.InvalidRequest(s"completion ${id.value} is still pending"))
      case Some(CompletionStatus.Failed(error)) => Left(error)
      case Some(CompletionStatus.Succeeded)     =>
        val ranges = dirtyChunks.get(id).toList.view
          .flatMap(_.view)
          .toArray
          .sortBy(_._1.value)
          .view
          .flatMap { case (allocationId, chunks) =>
            val allocationSize = current.allocations(allocationId).size
            chunks.keys.toArray.sorted.view.map { chunk =>
              val offset = chunk * DirtyChunkSize
              DirtyRange(
                allocationId,
                offset.refineUnsafe[Positive0],
                math.min(DirtyChunkSize, allocationSize.toLong - offset).toInt.refineUnsafe[Positive]
              )
            }
          }
        Right(Vec.from(ranges))
    }
  }

  def completionPointerRelocations(id: CompletionId): Either[ExecutionFailure, Vec[PointerRelocation]] =
    stateLock.withLock {
      val current = state.get()
      current.completions.get(id).map(record => record.status -> record.pointerRelocations).toRight(
        ExecutionFailure.NotFound(s"completion ${id.value} does not exist")
      ).flatMap {
        case (CompletionStatus.Pending, _) =>
          Left(ExecutionFailure.InvalidRequest(s"completion ${id.value} is still pending"))
        case (CompletionStatus.Failed(error), _) => Left(error)
        case (CompletionStatus.Succeeded, value) => Right(value)
      }
    }

  def completionPrints(id: CompletionId): Either[ExecutionFailure, Vec[PrintEvent]] =
    stateLock.withLock {
      val current = state.get()
      current.completions.get(id).map(record => record.status -> record.prints).toRight(
        ExecutionFailure.NotFound(s"completion ${id.value} does not exist")
      ).flatMap {
        case (CompletionStatus.Pending, _) =>
          Left(ExecutionFailure.InvalidRequest(s"completion ${id.value} is still pending"))
        case (CompletionStatus.Failed(error), _) => Left(error)
        case (CompletionStatus.Succeeded, value) => Right(value)
      }
    }

  def releaseCompletion(id: CompletionId): Either[ExecutionFailure, Unit] = stateLock.withLock {
    val current = state.get()
    current.completions.get(id) match {
      case None => Left(ExecutionFailure.NotFound(s"completion ${id.value} does not exist"))
      case Some(record) if record.status == CompletionStatus.Pending =>
        Left(ExecutionFailure.InvalidRequest(s"completion ${id.value} is still pending"))
      case Some(_) =>
        state.set(current.copy(completions = current.completions.removed(id)))
        val _ = dirtyChunks.remove(id)
        Right(())
    }
  }

  def read(
      id: AllocationId,
      offset: Long :| Positive0,
      size: Int :| Positive0
  ): Either[ExecutionFailure, Vec[Byte]] = stateLock.withLock {
    state.get().allocations.get(id).toRight(
      ExecutionFailure.NotFound(s"allocation ${id.value} is not registered")
    ).flatMap { allocation =>
      checkedRange(allocation.bytes.length, offset, size).left.map(error =>
        ExecutionFailure.InvalidRequest(s"allocation ${id.value}: ${error.message}")
      ).map { start =>
        Vec.unsafeFromArray(java.util.Arrays.copyOfRange(allocation.bytes, start, start + size))
      }
    }
  }

  def readValue(
      id: AllocationId,
      offset: Long :| Positive0,
      size: MemoryValueByteWidth
  ): Either[ExecutionFailure, Long] = stateLock.withLock {
    state.get().allocations.get(id).toRight(
      ExecutionFailure.NotFound(s"allocation ${id.value} is not registered")
    ).flatMap { allocation =>
      checkedRange(allocation.bytes.length, offset, size.value).left.map(error =>
        ExecutionFailure.InvalidRequest(s"allocation ${id.value}: ${error.message}")
      ).map(start => LittleEndian.decode(allocation.bytes, start, size.scalarByteWidth))
    }
  }

  def write(id: AllocationId, offset: Long :| Positive0, bytes: Vec[Byte]): Either[ExecutionFailure, Unit] =
    write(id, offset, bytes, None)

  def writeValue(
      id: AllocationId,
      offset: Long :| Positive0,
      value: Long,
      size: MemoryValueByteWidth
  ): Either[ExecutionFailure, Unit] = writeValue(id, offset, value, size, None)

  def atomicModifyValue(
      id: AllocationId,
      offset: Long :| Positive0,
      size: MemoryValueByteWidth
  )(update: Long => Long): Either[ExecutionFailure, Long] =
    atomicModifyValue(id, offset, size, None)(update)

  private final class CompletionMemory(completion: CompletionId) extends Memory with AddressSpace {
    @SuppressWarnings(Array(
      "scalafix:DisableSyntax.var",
      "scalafix:GemstoneNoAssume",
      "scalafix:GemstoneNoIterator.no-iterator"
    ))
    private inline def withAllocation[A](
        address: GuestAddress,
        size: Int :| Positive0
    )(inline operation: (AllocationId, Long :| Positive0) => Either[Fault, A]): Either[Fault, A] = {
      val allocations = state.get().allocations.iterator
      var foundId     = 0L
      var foundOffset = 0L
      while (allocations.hasNext && foundId == 0L) {
        val (id, allocation) = allocations.next()
        val base             = allocation.address.getOrElse(syntheticAddress(id))
        val relative         = address.value - base.value
        if (
          java.lang.Long.compareUnsigned(address.value, base.value) >= 0 &&
          size <= allocation.size && relative <= allocation.size.toLong - size.toLong
        ) {
          foundId = id.value
          foundOffset = relative
        }
      }
      if (foundId == 0L) Left(Fault.Memory(s"address ${address.value} is not bound"))
      else operation(AllocationId.assume(foundId), foundOffset.assume[Positive0])
    }

    override def read(
        region: MemoryRegion,
        offset: Long :| Positive0,
        size: Int :| Positive0
    ): Either[Fault, Vec[Byte]] =
      ExecutionService.this.read(allocationId(region), offset, size).left.map(memoryFault(_))

    override def readValue(
        region: MemoryRegion,
        offset: Long :| Positive0,
        size: MemoryValueByteWidth
    ): Either[Fault, Long] =
      ExecutionService.this.readValue(allocationId(region), offset, size).left.map(memoryFault(_))

    override def write(
        region: MemoryRegion,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] =
      ExecutionService.this.write(allocationId(region), offset, bytes, Some(completion)).left.map(memoryFault(_))

    override def writeValue(
        region: MemoryRegion,
        offset: Long :| Positive0,
        value: Long,
        size: MemoryValueByteWidth
    ): Either[Fault, Unit] = ExecutionService.this.writeValue(
      allocationId(region),
      offset,
      value,
      size,
      Some(completion)
    ).left.map(memoryFault(_))

    override def atomicModifyValue(region: MemoryRegion, offset: Long :| Positive0, size: MemoryValueByteWidth)(
        update: Long => Long
    ): Either[Fault, Long] =
      ExecutionService.this.atomicModifyValue(
        allocationId(region),
        offset,
        size,
        Some(completion)
      )(update).left.map(memoryFault(_))

    override def address(region: MemoryRegion, offset: Long :| Positive0): Either[Fault, GuestAddress] =
      regionAddress(region, offset)

    override def locate(address: GuestAddress, size: Int :| Positive0): Either[Fault, AddressSpace.Location] =
      locateAddress(address, size)

    override def read(memory: Memory, address: GuestAddress, size: Int :| Positive0): Either[Fault, Vec[Byte]] =
      withAllocation(address, size)((id, offset) =>
        ExecutionService.this.read(id, offset, size).left.map(memoryFault(_))
      )

    override def readValue(
        memory: Memory,
        address: GuestAddress,
        size: MemoryValueByteWidth
    ): Either[Fault, Long] =
      withAllocation(address, size.nonNegative)((id, offset) =>
        ExecutionService.this.readValue(id, offset, size).left.map(memoryFault(_))
      )

    override def write(memory: Memory, address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
      withAllocation(address, bytes.nonNegativeSize)((id, offset) =>
        ExecutionService.this.write(id, offset, bytes, Some(completion)).left.map(memoryFault(_))
      )

    override def writeValue(
        memory: Memory,
        address: GuestAddress,
        value: Long,
        size: MemoryValueByteWidth
    ): Either[Fault, Unit] =
      withAllocation(address, size.nonNegative)((id, offset) =>
        ExecutionService.this.writeValue(id, offset, value, size, Some(completion)).left.map(memoryFault(_))
      )

    override def atomicModifyValue(
        memory: Memory,
        address: GuestAddress,
        size: MemoryValueByteWidth
    )(update: Long => Long): Either[Fault, Long] =
      withAllocation(address, size.nonNegative)((id, offset) =>
        ExecutionService.this.atomicModifyValue(id, offset, size, Some(completion))(update).left.map(memoryFault(_))
      )
  }

  private def completionMemory(completion: CompletionId): Memory = CompletionMemory(completion)

  private def recordPointerRelocation(completion: CompletionId, relocation: PointerRelocation): Unit =
    stateLock.withLock {
      val current = state.get()
      current.completions.get(completion).foreach { record =>
        state.set(
          current.copy(
            completions = current.completions.updated(
              completion,
              record.copy(
                pointerRelocations = record.pointerRelocations
                  .filterNot(existing =>
                    existing.source == relocation.source &&
                      existing.sourceOffset == relocation.sourceOffset
                  ) :+ relocation
              )
            )
          )
        )
      }
    }

  private def recordPrint(completion: CompletionId, event: PrintEvent): Either[ExecutionFailure, Unit] =
    stateLock.withLock {
      val current = state.get()
      current.completions.get(completion) match {
        case None => Left(ExecutionFailure.NotFound(s"completion ${completion.value} does not exist"))
        case Some(record) if record.prints.size >= ExecutionRPC.MaximumElements =>
          Left(ExecutionFailure.Failed("device printf event limit exceeded"))
        case Some(record) =>
          state.set(
            current.copy(completions =
              current.completions.updated(
                completion,
                record.copy(prints = record.prints.appended(event))
              )
            )
          )
          Right(())
      }
    }

  private def write(
      id: AllocationId,
      offset: Long :| Positive0,
      bytes: Vec[Byte],
      completion: Option[CompletionId]
  ): Either[ExecutionFailure, Unit] = stateLock.withLock {
    if (closed && completion.isEmpty) Left(ExecutionFailure.Failed("execution session is closed"))
    else state.get().allocations.get(id) match {
      case None             => Left(ExecutionFailure.NotFound(s"allocation ${id.value} is not registered"))
      case Some(allocation) =>
        checkedRange(allocation.bytes.length, offset, bytes.nonNegativeSize) match {
          case Left(error) =>
            Left(ExecutionFailure.InvalidRequest(s"allocation ${id.value}: ${error.message}"))
          case Right(start) =>
            val _ = bytes.copyToArray(allocation.bytes, start, bytes.length)
            bytes.refineOption[MinLength[1]].foreach(nonEmpty =>
              markDirty(completion, id, start, nonEmpty.positiveSize)
            )
            Right(())
        }
    }
  }

  private def writeValue(
      id: AllocationId,
      offset: Long :| Positive0,
      value: Long,
      size: MemoryValueByteWidth,
      completion: Option[CompletionId]
  ): Either[ExecutionFailure, Unit] = stateLock.withLock {
    if (closed && completion.isEmpty) Left(ExecutionFailure.Failed("execution session is closed"))
    else state.get().allocations.get(id) match {
      case None             => Left(ExecutionFailure.NotFound(s"allocation ${id.value} is not registered"))
      case Some(allocation) =>
        checkedRange(allocation.bytes.length, offset, size.value) match {
          case Left(error) =>
            Left(ExecutionFailure.InvalidRequest(s"allocation ${id.value}: ${error.message}"))
          case Right(start) =>
            LittleEndian.write(value, allocation.bytes, start, size.scalarByteWidth)
            markDirty(completion, id, start, size.value)
            Right(())
        }
    }
  }

  private def atomicModifyValue(
      id: AllocationId,
      offset: Long :| Positive0,
      size: MemoryValueByteWidth,
      completion: Option[CompletionId]
  )(update: Long => Long): Either[ExecutionFailure, Long] = stateLock.withLock {
    if (closed && completion.isEmpty) Left(ExecutionFailure.Failed("execution session is closed"))
    else state.get().allocations.get(id) match {
      case None             => Left(ExecutionFailure.NotFound(s"allocation ${id.value} is not registered"))
      case Some(allocation) =>
        checkedRange(allocation.bytes.length, offset, size.value) match {
          case Left(error) =>
            Left(ExecutionFailure.InvalidRequest(s"allocation ${id.value}: ${error.message}"))
          case Right(start) =>
            val width    = size.scalarByteWidth
            val previous = LittleEndian.decode(allocation.bytes, start, width)
            LittleEndian.write(update(previous), allocation.bytes, start, width)
            markDirty(completion, id, start, size.value)
            Right(previous)
        }
    }
  }

  private def markDirty(
      completion: Option[CompletionId],
      id: AllocationId,
      offset: Int,
      size: Int
  ): Unit = completion.foreach { completionId =>
    val allocations = dirtyChunks.getOrElseUpdate(completionId, mutable.HashMap.empty)
    val chunks      = allocations.getOrElseUpdate(id, mutable.LongMap.empty)
    val first       = offset.toLong / DirtyChunkSize
    val last        = (offset.toLong + size.toLong - 1L) / DirtyChunkSize
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var chunk = first
    while (chunk <= last) {
      chunks.update(chunk, ())
      chunk += 1L
    }
  }

  private def validateTask(task: KernelTask, current: State): Either[ExecutionFailure, Unit] =
    task.arguments.collectFirst {
      case KernelTask.Argument.Memory(region, _) if !current.allocations.contains(allocationId(region)) =>
        val id = allocationId(region)
        ExecutionFailure.NotFound(s"kernel argument refers to unregistered allocation ${id.value}")
      case KernelTask.Argument.Memory(region, offset)
          if offset > current.allocations(allocationId(region)).bytes.length.toLong =>
        val id = allocationId(region)
        ExecutionFailure.InvalidRequest(
          s"kernel argument offset $offset is outside allocation ${id.value}"
        )
    }.toLeft(()).flatMap { _ =>
      task.pointerRelocations.collectFirst {
        case KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(region), _, _, _)
            if !current.allocations.contains(allocationId(region)) =>
          val source = allocationId(region)
          ExecutionFailure.NotFound(
            s"pointer relocation refers to unregistered source allocation ${source.value}"
          )
        case KernelTask.PointerRelocation(_, _, target, _)
            if !current.allocations.contains(allocationId(target)) =>
          val id = allocationId(target)
          ExecutionFailure.NotFound(
            s"pointer relocation refers to unregistered target allocation ${id.value}"
          )
        case KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(region), sourceOffset, _, _)
            if sourceOffset > current.allocations(allocationId(region)).bytes.length.toLong - 8L =>
          val source = allocationId(region)
          ExecutionFailure.InvalidRequest(
            s"pointer relocation source offset $sourceOffset exceeds allocation ${source.value}"
          )
        case KernelTask.PointerRelocation(_, _, target, targetOffset)
            if targetOffset > current.allocations(allocationId(target)).bytes.length.toLong =>
          val id = allocationId(target)
          ExecutionFailure.InvalidRequest(
            s"pointer relocation target offset $targetOffset exceeds allocation ${id.value}"
          )
        case KernelTask.PointerRelocation(KernelTask.RelocationSource.Argument(index), sourceOffset, _, _)
            if sourceOffset >
              (task.arguments(index) match {
                case KernelTask.Argument.Scalar(bytes) => bytes.length.toLong - 8L
                case _                                 => -1L
              }) =>
          ExecutionFailure.InvalidRequest(
            s"pointer relocation source offset $sourceOffset exceeds argument $index"
          )
      }.toLeft(())
    }

  private def referencedAllocations(task: KernelTask): Set[AllocationId] =
    task.arguments.collect {
      case KernelTask.Argument.Memory(region, _) => allocationId(region)
    }.toSet ++ task.pointerRelocations.foldLeft(Set.empty[AllocationId]) { (allocations, relocation) =>
      val withTarget = allocations + allocationId(relocation.target)
      relocation.source match {
        case KernelTask.RelocationSource.Memory(region) => withTarget + allocationId(region)
        case KernelTask.RelocationSource.Argument(_)    => withTarget
      }
    }

  private def program(id: ProgramId): Either[ExecutionFailure, RuntimeBackend.Program] =
    state.get().programs.get(id).toRight(ExecutionFailure.NotFound(s"program ${id.value} is not loaded"))

  private def backendFor(encoding: String): Either[RuntimeBackend.Error, RuntimeBackend] =
    backends.find(_.encoding == encoding)
      .toRight(RuntimeBackend.Error.Unsupported(s"unknown program encoding: $encoding"))

  private def runtimeFailure(error: RuntimeBackend.Error): ExecutionFailure = error match {
    case RuntimeBackend.Error.Invalid(detail)     => ExecutionFailure.InvalidRequest(detail)
    case RuntimeBackend.Error.NotFound(detail)    => ExecutionFailure.NotFound(detail)
    case RuntimeBackend.Error.Unsupported(detail) => ExecutionFailure.Unsupported(detail)
    case RuntimeBackend.Error.Failed(detail)      => ExecutionFailure.Failed(detail)
    case RuntimeBackend.Error.Execution(fault)    => faultFailure(fault)
  }

  private def faultFailure(fault: Fault): ExecutionFailure = fault match {
    case Fault.InvalidTask(detail) => ExecutionFailure.InvalidRequest(detail)
    case Fault.Unsupported(detail) => ExecutionFailure.Unsupported(detail)
    case _                         => ExecutionFailure.Failed(fault.message)
  }

  private def memoryFault(error: ExecutionFailure): Fault = Fault.Memory(error.message)

  private def allocationId(region: MemoryRegion): AllocationId =
    AllocationId.either(region.value).fold(message => throw IllegalStateException(message), identity)

  private def regionAddress(region: MemoryRegion, offset: Long :| Positive0): Either[Fault, GuestAddress] =
    state.get().allocations.get(allocationId(region)).toRight(Fault.Memory(s"memory region ${region.value} is absent"))
      .map(allocation => allocation.address.getOrElse(syntheticAddress(allocationId(region))).offset(offset))

  @SuppressWarnings(Array(
    "scalafix:DisableSyntax.null",
    "scalafix:DisableSyntax.var",
    "scalafix:GemstoneNoIterator.no-iterator"
  ))
  private def locateAddress(address: GuestAddress, size: Int :| Positive0): Either[Fault, AddressSpace.Location] = {
    val allocations                          = state.get().allocations.iterator
    var result: AddressSpace.Location | Null = null
    while (allocations.hasNext && result == null) {
      val (id, allocation) = allocations.next()
      val base             = allocation.address.getOrElse(syntheticAddress(id))
      val relative         = address.value - base.value
      if (
        java.lang.Long.compareUnsigned(address.value, base.value) >= 0 &&
        size <= allocation.size && relative <= allocation.size.toLong - size.toLong
      ) result = AddressSpace.Location(MemoryRegion(id.value), relative.refineUnsafe[Positive0])
    }
    if (result == null) Left(Fault.Memory(s"address ${address.value} is not bound")) else Right(result.nn)
  }

  private def syntheticAddress(id: AllocationId): GuestAddress =
    GuestAddress(SyntheticAddressBase + (id.value - 1L) * SyntheticAddressStride)

  private def observe(completion: CompletionId, event: Event): Either[net.kurobako.gemstone.vm.Observer.Error, Unit] =
    event match {
      case Event.Print(format, arguments) =>
        recordPrint(
          completion,
          PrintEvent(
            format,
            arguments.map {
              case Event.PrintArgument.Integer(bits, width, signed) => PrintArgument.Integer(bits, width, signed)
              case Event.PrintArgument.Floating(bits, width)        => PrintArgument.Floating(bits, width)
              case Event.PrintArgument.Pointer(address)             => PrintArgument.Pointer(address)
              case Event.PrintArgument.Text(value)                  => PrintArgument.Text(value)
            }
          )
        ).left.map(error => net.kurobako.gemstone.vm.Observer.Error(error.message))
      case Event.PointerRelocation(source, sourceOffset, target, targetOffset) =>
        recordPointerRelocation(
          completion,
          PointerRelocation(
            RelocationSource.Allocation(allocationId(source)),
            sourceOffset,
            allocationId(target),
            targetOffset
          )
        )
        Right(())
      case _ => Right(())
    }

  private def toWireKernel(metadata: net.kurobako.gemstone.isa.KernelMetadata): KernelMetadata =
    KernelMetadata.validated(
      metadata.argumentBlockSize,
      metadata.explicitParameterCount,
      metadata.parameters.map { parameter =>
        KernelParameterMetadata(
          parameter.width,
          parameter.offset,
          parameter.kind match {
            case net.kurobako.gemstone.isa.KernelMetadata.ParameterKind.Scalar => KernelParameterKind.Scalar
            case net.kurobako.gemstone.isa.KernelMetadata.ParameterKind.Memory => KernelParameterKind.Allocation
            case net.kurobako.gemstone.isa.KernelMetadata.ParameterKind.Local  => KernelParameterKind.Local
          },
          parameter.containsPointers
        )
      }
    ).fold(message => throw IllegalStateException(message), identity)

  private def toWireCatalog(symbols: net.kurobako.gemstone.isa.ProgramSymbols): ProgramCatalog =
    ProgramCatalog(
      symbols.kernels.map(kernel =>
        ProgramKernel(KernelName(kernel.name.value), LinkageName(kernel.linkageName.value))
      ),
      symbols.globals.map { global =>
        ProgramGlobalMetadata.validated(
          GlobalName(global.name.value),
          global.virtualAddress,
          global.size,
          global.pointerOffsets
        ).fold(message => throw IllegalStateException(message), identity)
      }
    )

  private def checkedRange(
      length: Int,
      offset: Long :| Positive0,
      size: Int
  ): Either[ExecutionFailure, Int] =
    ByteRange
      .start(length, offset, size)
      .toRight(ExecutionFailure.InvalidRequest(
        s"byte range $offset + $size exceeds allocation size $length"
      ))
}

object ExecutionService {
  val MaxInstructionsEnvironment: String :| (MinLength[1] & NoNul) = "GEMSTONE_EXECUTION_MAX_INSTRUCTIONS"
  val QuantumEnvironment: String :| (MinLength[1] & NoNul)         = "GEMSTONE_EXECUTION_QUANTUM"

  def limits(environment: Map[String, String]): Either[String, TaskLimits] = {
    val maximum = environment.get(MaxInstructionsEnvironment) match {
      case None        => Right(TaskLimits.Default.maxInstructions)
      case Some(value) => value.toLongOption
          .toRight(s"$MaxInstructionsEnvironment must be a positive integer: $value")
          .flatMap(_.refineEither[Positive].left.map(_ =>
            s"$MaxInstructionsEnvironment must be positive: $value"
          ))
    }
    val quantum = environment.get(QuantumEnvironment) match {
      case None        => Right(TaskLimits.Default.quantum)
      case Some(value) => value.toIntOption
          .toRight(s"$QuantumEnvironment must be a positive integer: $value")
          .flatMap(_.refineEither[Positive].left.map(_ =>
            s"$QuantumEnvironment must be positive: $value"
          ))
    }
    for {
      maxInstructions <- maximum
      quantumValue    <- quantum
    } yield TaskLimits(maxInstructions, quantumValue)
  }
}
