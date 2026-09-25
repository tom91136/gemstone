package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.abi.ABI

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
import java.util.concurrent.locks.{ReentrantLock, ReentrantReadWriteLock}
import scala.annotation.tailrec
import scala.collection.mutable

/** Process-neutral program and memory receiver shared by every native API facade. */
final class ExecutionService(
    backends: List[RuntimeBackend],
    taskLimits: TaskLimits = TaskLimits.Default,
    scheduler: Scheduler = Scheduler.Sequential
) extends AutoCloseable {
  private val SyntheticAddressBase   = 0x400000000000L
  private val SyntheticAddressStride = 0x100000000L

  /** Mutable byte storage is deliberately confined behind this service's state lock. Allocation traffic is the
    * simulator's hot data path; rebuilding a multi-megabyte immutable collection for every mirrored chunk creates
    * unbounded GC latency. Neither the array nor a mutable view crosses the VM memory boundary.
    */
  private final case class RemoteAllocation(
      kind: AllocationKind,
      bytes: Array[Byte],
      address: Option[GuestAddress] = None,
      hostBacked: Boolean = false
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
  // An acquire in one dispatch must not import another dispatch's unpublished
  // writes or retain its in-flight read reply. Aliases within a dispatch share a view.
  private val hostMemories                                   = mutable.Map.empty[CompletionId, HostMemoryMirror]
  private def hostMemory(id: CompletionId): HostMemoryMirror = hostMemories.getOrElseUpdate(id, HostMemoryMirror())
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private final class HostSyncExchange(val request: HostMemorySync) {
    var issued                 = false
    var observed: Option[Long] = None
  }
  private val hostSyncLock      = ReentrantLock()
  private val hostSyncs         = mutable.Map.empty[Long, HostSyncExchange]
  private val nextHostSync      = java.util.concurrent.atomic.AtomicLong()
  private val hostRequestsReady = mutable.Map.empty[Long, Int]
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  @volatile private var hostMemoryEnabled = false

  private val stateLock    = ReentrantLock()
  private val stateChanged = stateLock.newCondition()
  private val dirtyWrites  =
    mutable.HashMap.empty[CompletionId, mutable.HashMap[AllocationId, WrittenRanges]]
  private val pendingExecutions = mutable.Queue.empty[(CompletionId, RuntimeBackend.Program, KernelTask, TaskControl)]
  private val programLifecycleLock = ReentrantReadWriteLock()
  private val observer             = CompletionObserver()
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var executionWorkers = 0
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
  def registerAllocationAddress(
      id: AllocationId,
      address: GuestAddress,
      hostBacked: Boolean = false
  ): Either[ExecutionFailure, Unit] =
    stateLock.withLock {
      val current = state.get()
      if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
      else current.allocations.get(id) match {
        case None             => Left(ExecutionFailure.NotFound(s"allocation ${id.value} is not registered"))
        case Some(allocation) =>
          val size = allocation.size.toLong
          if (address.value <= 0 || address.value > Long.MaxValue - size)
            Left(ExecutionFailure.InvalidRequest("allocation address alias has an invalid or overflowing range"))
          else if (allocation.address.contains(address) && allocation.hostBacked == hostBacked) Right(())
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
              current.allocations.updated(id, allocation.copy(address = Some(address), hostBacked = hostBacked))
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
        val loaded = programLifecycleLock.writeLock().withLock {
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
                programLifecycleLock.writeLock().withLock(program.close())
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
        programLifecycleLock.writeLock().withLock(program.close())
        stateLock.withLock { val _ = boundAllocations.remove(id) }
        Right(())
    }
  }

  def kernel(id: ProgramId, name: KernelName): Either[ExecutionFailure, KernelMetadata] =
    program(id).flatMap(_.kernel(net.kurobako.gemstone.isa.KernelName(name.value)).left.map(runtimeFailure(_)))
      .flatMap(toWireKernel(_))

  def catalog(id: ProgramId): Either[ExecutionFailure, ProgramCatalog] =
    program(id).flatMap(_.symbols.left.map(runtimeFailure(_))).flatMap(toWireCatalog(_))

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
    val result = programLifecycleLock.writeLock().withLock {
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
      case Right(_)    => programLifecycleLock.writeLock().withLock {
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
              dirtyWrites.update(id, mutable.HashMap.empty)
              pendingExecutions.enqueue((id, loaded, task, control))
              id
          }
      }
    }
    accepted.foreach { _ =>
      val startWorker = stateLock.withLock {
        if (closed || pendingExecutions.isEmpty || executionWorkers >= scheduler.parallelism) false
        else {
          executionWorkers += 1
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
    accepted
  }

  private def runPendingExecutions(): Unit = {
    def dequeueLocked(): Option[(CompletionId, RuntimeBackend.Program, KernelTask, TaskControl)] = {
      val value = pendingExecutions.dequeueFirst(_ => true)
      if (value.isEmpty) executionWorkers -= 1
      value
    }
    @tailrec def loop(next: Option[(CompletionId, RuntimeBackend.Program, KernelTask, TaskControl)]): Unit =
      next match {
        case None                               => ()
        case Some((id, program, task, control)) =>
          val status =
            try
              programLifecycleLock.readLock().withLock {
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
      executionWorkers -= 1
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
      if (!closed || executionWorkers != 0 || bindingPrograms.nonEmpty) None
      else {
        val current = state.get()
        state.set(current.copy(allocations = Map.empty, programs = Map.empty, completions = Map.empty))
        dirtyWrites.clear()
        hostMemories.clear()
        boundAllocations.clear()
        Some(current.programs.values.toVector)
      }
    }
    programs.foreach(loaded => programLifecycleLock.writeLock().withLock(loaded.foreach(_.close())))
  }

  def completion(id: CompletionId): Either[ExecutionFailure, CompletionStatus] =
    state.get().completions.get(id).map(_.status)
      .toRight(ExecutionFailure.NotFound(s"completion ${id.value} does not exist"))

  def enableHostMemory(): Either[ExecutionFailure, Unit] = stateLock.withLock {
    if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
    else { hostMemoryEnabled = true; Right(()) }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def pollHostMemory(id: CompletionId): Either[ExecutionFailure, HostMemoryBatch] = stateLock.withLock {
    var result: Option[Either[ExecutionFailure, HostMemoryBatch]] = None
    while (result.isEmpty)
      completion(id) match {
        case Left(error)                           => result = Some(Left(error))
        case Right(CompletionStatus.Failed(error)) => result = Some(Left(error))
        case Right(CompletionStatus.Succeeded)     =>
          val writes = hostMemory(id).takeDirty(id.value)
          result = Some(Right(HostMemoryBatch(Vec.empty, writes, !hostMemory(id).hasDirty(id.value))))
        case Right(CompletionStatus.Pending) if closed =>
          result = Some(Left(ExecutionFailure.Failed("execution session is closed")))
        case Right(CompletionStatus.Pending) =>
          hostSyncs.get(id.value) match {
            case Some(exchange) if !exchange.issued =>
              val writes = hostMemory(id).takeDirty(id.value)
              val atomic = if (hostMemory(id).hasDirty(id.value)) None
              else {
                exchange.issued = true
                Some(exchange.request)
              }
              result = Some(Right(HostMemoryBatch(Vec.empty, writes, false, atomic)))
            case Some(_) => stateChanged.await()
            case None    =>
              val reads = hostMemory(id).requests(id.value)
              if (hostRequestsReady.contains(id.value) && reads.nonEmpty)
                result = Some(Right(HostMemoryBatch(reads, Vec.empty, false)))
              else stateChanged.await()
          }
      }
    result.get
  }

  def supplyHostMemory(id: CompletionId, ranges: Vec[HostMemoryBytes]): Either[ExecutionFailure, Unit] =
    stateLock.withLock {
      completion(id).flatMap {
        case CompletionStatus.Pending =>
          hostMemory(id).supply(id.value, ranges).left.map(ExecutionFailure.InvalidRequest(_)).map { _ =>
            stateChanged.signalAll()
          }
        case _ => Left(ExecutionFailure.InvalidRequest("host memory completion is no longer pending"))
      }
    }

  def supplyHostSync(id: CompletionId, ticket: Long, observed: Long): Either[ExecutionFailure, Unit] =
    stateLock.withLock {
      hostSyncs.get(id.value) match {
        case Some(exchange) if exchange.issued && exchange.request.ticket == ticket && exchange.observed.isEmpty =>
          exchange.observed = Some(observed)
          stateChanged.signalAll()
          Right(())
        case _ =>
          Left(ExecutionFailure.InvalidRequest("host synchronization reply has no matching outstanding request"))
      }
    }

  private def hostBacked(id: AllocationId): Boolean = state.get().allocations.get(id).exists(_.hostBacked)
  private def hostTransfers: Boolean = hostMemoryEnabled || state.get().allocations.values.exists(_.hostBacked)
  private def hostAccessible(address: GuestAddress, size: Int): Boolean = hostMemoryEnabled ||
    locateAddress(address, size.refineUnsafe[Positive0]).exists(location => hostBacked(allocationId(location.region)))

  // All host atomics linearize in the native process with CPU atomics. CAS also
  // supports arbitrary interpreter RMW functions without serializing Scala closures.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def atomicHostMemory(
      id: CompletionId,
      address: GuestAddress,
      size: MemoryValueByteWidth
  )(update: Long => Long): Either[Fault, Long] =
    if ((size.value != 4 && size.value != 8) || address.value % size.value != 0)
      Left(Fault.Memory("host atomics require aligned 32- or 64-bit storage"))
    else if (address.value <= 0 || address.value > Long.MaxValue - size.value || !hostAccessible(address, size.value))
      Left(Fault.Memory("host atomic address is unavailable or overflows"))
    else hostSyncLock.withLock {
      val mask                                = if (size.value == 4) 0xffffffffL else -1L
      var expected                            = 0L
      var result: Option[Either[Fault, Long]] = None
      while (result.isEmpty) {
        val desired  = update(expected) & mask
        val observed = synchronizeHost(id, Some(HostMemoryAtomic(address, size.value, expected, desired))).map(_ & mask)
        observed match {
          case Left(error)                       => result = Some(Left(error))
          case Right(value) if value == expected => result = Some(Right(value))
          case Right(value)                      => expected = value
        }
      }
      result.get
    }

  private def synchronizeHost(id: CompletionId, atomic: Option[HostMemoryAtomic]): Either[Fault, Long] =
    stateLock.withLock {
      val exchange = HostSyncExchange(HostMemorySync(nextHostSync.incrementAndGet(), atomic))
      hostSyncs.update(id.value, exchange)
      stateChanged.signalAll()
      while (!closed && exchange.observed.isEmpty) stateChanged.await()
      val _ = hostSyncs.remove(id.value)
      hostMemory(id).invalidateClean()
      // A poller can consume the acknowledgement wakeup before this worker does.
      // Removing the exchange makes other workers' pending reads eligible again.
      stateChanged.signalAll()
      if (closed) Left(Fault.Cancelled) else Right(exchange.observed.get)
    }

  private def flushHostWrites(id: CompletionId): Either[Fault, Unit] = hostSyncLock.withLock {
    if (stateLock.withLock(hostMemory(id).hasDirty(id.value))) synchronizeHost(id, None).map(_ => ())
    else Right(())
  }

  private def flushHostMemory(id: CompletionId): Either[Fault, Unit] = stateLock.withLock {
    hostRequestsReady.update(id.value, hostRequestsReady.getOrElse(id.value, 0) + 1)
    stateChanged.signalAll()
    while (!closed && hostMemory(id).hasPending(id.value)) stateChanged.await()
    if (hostRequestsReady(id.value) == 1) { val _ = hostRequestsReady.remove(id.value) }
    else hostRequestsReady.update(id.value, hostRequestsReady(id.value) - 1)
    if (closed) Left(Fault.Cancelled) else Right(())
  }

  private def readHostMemory(
      id: CompletionId,
      address: GuestAddress,
      size: Int :| Positive0,
      defer: Boolean
  ): Either[Fault, Vec[Byte]] = {
    val result = stateLock.withLock {
      if (!hostAccessible(address, size) || address.value <= 0 || address.value > Long.MaxValue - size.toLong)
        Left(Fault.Memory("host memory address is unavailable or overflows"))
      else hostMemory(id).read(id.value, address.value, size).toRight(Fault.PendingMemory)
    }
    result match {
      case Left(Fault.PendingMemory) if !defer =>
        flushHostMemory(id).flatMap(_ => readHostMemory(id, address, size, false))
      case other => other
    }
  }

  private def writeHostMemory(id: CompletionId, address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    stateLock.withLock {
      if (
        !hostAccessible(address, bytes.size) || address.value <= 0 || address.value > Long.MaxValue - bytes.size.toLong
      )
        Left(Fault.Memory("host memory address is unavailable or overflows"))
      else {
        val atomicWidth = if ((bytes.size == 4 || bytes.size == 8) && address.value % bytes.size == 0) bytes.size else 0
        hostMemory(id).write(id.value, address.value, bytes, atomicWidth)
        Right(())
      }
    }

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
        val ranges = dirtyWrites.get(id).toList.view
          .flatMap(_.view)
          .toArray
          .sortBy(_._1.value)
          .view
          .flatMap { case (allocationId, written) =>
            written.chunks.map { (offset, size) =>
              DirtyRange(allocationId, offset.toLong.refineUnsafe[Positive0], size.refineUnsafe[Positive])
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
        val _ = dirtyWrites.remove(id)
        val _ = hostMemories.remove(id)
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
    override def hasNativePointers(region: MemoryRegion): Boolean =
      hostRegion(region) || state.get().allocations.get(allocationId(region)).exists(_.address.nonEmpty)

    // The synchronization word can be device-owned while the ordered payload is
    // host-owned. Publication/acquisition must cover both kinds of backing.
    private def deviceAtomic(operation: => Either[Fault, Long]): Either[Fault, Long] =
      if (!hostTransfers) operation
      else flushHostWrites().flatMap(_ => operation).map { value =>
        invalidateHostReads()
        value
      }

    override def fenceHostMemory(): Either[Fault, Unit] =
      if (hostTransfers) hostSyncLock.withLock(synchronizeHost(completion, None).map(_ => ()))
      else Right(())
    override def prepareReads(ranges: Vec[(GuestAddress, Int :| Positive0)]): Either[Fault, Unit] = {
      val staged = ranges.flatMap((address, size) =>
        if (locateAddress(address, size).exists(location => !hostBacked(allocationId(location.region))))
          Vec.empty[(GuestAddress, Int :| Positive0)]
        else segments(address, size)
      ).foldLeft[Either[Fault, Unit]](Right(())) {
        case (result, (address, size)) =>
          result.flatMap(_ =>
            withAllocation(address, size) { (id, _) =>
              if (id.exists(value => !hostBacked(value))) Right(())
              else readHostMemory(completion, address, size, true) match {
                case Left(Fault.PendingMemory) => Right(())
                case other                     => other.map(_ => ())
              }
            }
          )
      }
      staged.flatMap(_ => flushHostReads())
    }
    override def flushHostWrites(): Either[Fault, Unit]   = ExecutionService.this.flushHostWrites(completion)
    override def invalidateHostReads(): Unit              = stateLock.withLock(hostMemory(completion).invalidateClean())
    override def defersHostReads: Boolean                 = hostTransfers
    override def flushHostReads(): Either[Fault, Unit]    = flushHostMemory(completion)
    private def hostRegion(region: MemoryRegion): Boolean = region.value == 0L

    @SuppressWarnings(Array(
      "scalafix:DisableSyntax.var",
      "scalafix:GemstoneNoAssume",
      "scalafix:GemstoneNoIterator.no-iterator"
    ))
    private inline def withAllocation[A](
        address: GuestAddress,
        size: Int :| Positive0
    )(inline operation: (Option[AllocationId], Long :| Positive0) => Either[Fault, A]): Either[Fault, A] = {
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
      if (foundId == 0L && address.value > 0) operation(None, address.value.assume[Positive0])
      else if (foundId == 0L) Left(Fault.Memory(s"address ${address.value} is not bound"))
      else operation(Some(AllocationId.assume(foundId)), foundOffset.assume[Positive0])
    }

    // Explicit registrations may share a view while using several non-overlapping
    // backing allocations. Split only at known boundaries; unknown gaps remain exact.
    private def segments(address: GuestAddress, size: Int :| Positive0): Vec[(GuestAddress, Int :| Positive0)] = {
      val end = address.value + size.toLong
      if (address.value <= 0 || end < address.value) Vec((address, size))
      else {
        val boundaries = state.get().allocations.toVector.flatMap { (id, allocation) =>
          val base = allocation.address.getOrElse(syntheticAddress(id)).value
          Vector(base, base + allocation.size)
        }.filter(point => point > address.value && point < end)
        val points = (Vector(address.value, end) ++ boundaries).distinct.sorted
        Vec.from(points.zip(points.drop(1)).map { (begin, end) =>
          (GuestAddress(begin), (end - begin).toInt.refineUnsafe[Positive0])
        })
      }
    }

    private def readSegments(address: GuestAddress, size: Int :| Positive0, defer: Boolean): Either[Fault, Vec[Byte]] =
      segments(address, size).foldLeft[Either[Fault, Vec[Byte]]](Right(Vec.empty)) { case (result, (begin, length)) =>
        result.flatMap(bytes =>
          withAllocation(begin, length) { (id, offset) =>
            val part = if (id.isEmpty || id.exists(hostBacked(_))) readHostMemory(completion, begin, length, defer)
            else ExecutionService.this.read(id.get, offset, length).left.map(memoryFault(_))
            part.map(bytes ++ _)
          }
        )
      }

    private def writeSegments(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
      segments(address, bytes.nonNegativeSize).foldLeft[Either[Fault, Unit]](Right(())) {
        case (result, (begin, length)) =>
          result.flatMap(_ =>
            withAllocation(begin, length) { (id, offset) =>
              val part = bytes.slice((begin.value - address.value).toInt, (begin.value - address.value).toInt + length)
              if (id.isEmpty || id.exists(hostBacked(_))) writeHostMemory(completion, begin, part)
              else ExecutionService.this.write(id.get, offset, part, Some(completion)).left.map(memoryFault(_))
            }
          )
      }

    // Device atomics serialize against every lane using this address space. Host
    // CPU participation still requires a separate coherence/atomic protocol.
    private def atomicAddress(address: GuestAddress, size: MemoryValueByteWidth, defer: Boolean)(
        update: Long => Long
    ): Either[Fault, Long] = {
      val result = stateLock.withLock {
        readSegments(address, size.nonNegative, true).flatMap { bytes =>
          val previous = LittleEndian.decode(bytes, 0, size.scalarByteWidth)
          writeSegments(address, LittleEndian.toVec(update(previous), size.scalarByteWidth)).map(_ => previous)
        }
      }
      result match {
        case Left(Fault.PendingMemory) if !defer =>
          flushHostMemory(completion).flatMap(_ => atomicAddress(address, size, false)(update))
        case other => other
      }
    }

    private def crossesRegion(region: MemoryRegion, offset: Long :| Positive0, size: Int): Boolean =
      !hostRegion(region) && state.get().allocations.get(allocationId(region))
        .exists(allocation =>
          allocation.address.nonEmpty && (size > allocation.size || offset > allocation.size - size)
        )

    override def read(
        region: MemoryRegion,
        offset: Long :| Positive0,
        size: Int :| Positive0
    ): Either[Fault, Vec[Byte]] =
      if (hostRegion(region)) readSegments(GuestAddress(offset), size, true)
      else if (hostBacked(allocationId(region)) || crossesRegion(region, offset, size))
        regionAddress(region, offset).flatMap(readSegments(_, size, true))
      else ExecutionService.this.read(allocationId(region), offset, size).left.map(memoryFault(_))

    override def readValue(
        region: MemoryRegion,
        offset: Long :| Positive0,
        size: MemoryValueByteWidth
    ): Either[Fault, Long] =
      if (hostRegion(region) || hostBacked(allocationId(region)) || crossesRegion(region, offset, size.value))
        read(region, offset, size.nonNegative)
          .map(bytes => LittleEndian.decode(bytes, 0, size.scalarByteWidth))
      else ExecutionService.this.readValue(allocationId(region), offset, size).left.map(memoryFault(_))

    override def write(
        region: MemoryRegion,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] =
      if (hostRegion(region)) writeSegments(GuestAddress(offset), bytes)
      else if (hostBacked(allocationId(region)) || crossesRegion(region, offset, bytes.size))
        regionAddress(region, offset).flatMap(writeSegments(_, bytes))
      else ExecutionService.this.write(allocationId(region), offset, bytes, Some(completion)).left.map(memoryFault(_))

    override def writeValue(
        region: MemoryRegion,
        offset: Long :| Positive0,
        value: Long,
        size: MemoryValueByteWidth
    ): Either[Fault, Unit] =
      if (hostRegion(region) || hostBacked(allocationId(region)) || crossesRegion(region, offset, size.value))
        write(region, offset, LittleEndian.toVec(value, size.scalarByteWidth))
      else ExecutionService.this.writeValue(
        allocationId(region),
        offset,
        value,
        size,
        Some(completion)
      ).left.map(memoryFault(_))

    override def atomicModifyValue(region: MemoryRegion, offset: Long :| Positive0, size: MemoryValueByteWidth)(
        update: Long => Long
    ): Either[Fault, Long] =
      if (hostRegion(region)) atomicHostMemory(completion, GuestAddress(offset), size)(update)
      else if (hostBacked(allocationId(region)))
        regionAddress(region, offset).flatMap(atomicHostMemory(completion, _, size)(update))
      else if (crossesRegion(region, offset, size.value))
        regionAddress(region, offset).flatMap(atomicAddress(_, size, true)(update))
      else deviceAtomic(ExecutionService.this.atomicModifyValue(
        allocationId(region),
        offset,
        size,
        Some(completion)
      )(update).left.map(memoryFault(_)))

    override def address(region: MemoryRegion, offset: Long :| Positive0): Either[Fault, GuestAddress] =
      if (hostRegion(region)) Right(GuestAddress(offset)) else regionAddress(region, offset)

    override def locate(address: GuestAddress, size: Int :| Positive0): Either[Fault, AddressSpace.Location] =
      locateAddress(address, size).orElse(
        if (
          address.value > 0 &&
          (hostMemoryEnabled || segments(address, size).forall((begin, length) => locateAddress(begin, length).isRight))
        ) Right(AddressSpace.Location(MemoryRegion(0), address.value.refineUnsafe[Positive0]))
        else Left(Fault.Memory(s"address ${address.value} is not bound"))
      )

    override def read(memory: Memory, address: GuestAddress, size: Int :| Positive0): Either[Fault, Vec[Byte]] =
      withAllocation(address, size)((id, offset) =>
        if (id.isEmpty || id.exists(hostBacked(_))) readSegments(address, size, false)
        else ExecutionService.this.read(id.get, offset, size).left.map(memoryFault(_))
      )

    override def readValue(
        memory: Memory,
        address: GuestAddress,
        size: MemoryValueByteWidth
    ): Either[Fault, Long] =
      withAllocation(address, size.nonNegative)((id, offset) =>
        if (id.isEmpty || id.exists(hostBacked(_))) readSegments(address, size.nonNegative, false).map(bytes =>
          LittleEndian.decode(bytes, 0, size.scalarByteWidth)
        )
        else ExecutionService.this.readValue(id.get, offset, size).left.map(memoryFault(_))
      )

    override def write(memory: Memory, address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
      withAllocation(address, bytes.nonNegativeSize)((id, offset) =>
        if (id.isEmpty || id.exists(hostBacked(_))) writeSegments(address, bytes)
        else ExecutionService.this.write(id.get, offset, bytes, Some(completion)).left.map(memoryFault(_))
      )

    override def writeValue(
        memory: Memory,
        address: GuestAddress,
        value: Long,
        size: MemoryValueByteWidth
    ): Either[Fault, Unit] =
      withAllocation(address, size.nonNegative)((id, offset) =>
        if (id.isEmpty || id.exists(hostBacked(_)))
          writeSegments(address, LittleEndian.toVec(value, size.scalarByteWidth))
        else ExecutionService.this.writeValue(id.get, offset, value, size, Some(completion)).left.map(memoryFault(_))
      )

    override def atomicModifyValue(
        memory: Memory,
        address: GuestAddress,
        size: MemoryValueByteWidth
    )(update: Long => Long): Either[Fault, Long] =
      withAllocation(address, size.nonNegative)((id, offset) =>
        if (id.isEmpty || id.exists(hostBacked(_))) atomicHostMemory(completion, address, size)(update)
        else deviceAtomic(ExecutionService.this.atomicModifyValue(
          id.get,
          offset,
          size,
          Some(completion)
        )(update).left.map(memoryFault(_)))
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
    val allocations = dirtyWrites.getOrElseUpdate(completionId, mutable.HashMap.empty)
    allocations.getOrElseUpdate(id, WrittenRanges()).add(offset, size)
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
            if sourceOffset >
              current.allocations(allocationId(region)).bytes.length.toLong - ABI.MachineWordSize.toLong =>
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
                case KernelTask.Argument.Scalar(bytes) => bytes.length.toLong - ABI.MachineWordSize.toLong
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

  private def toWireKernel(metadata: net.kurobako.gemstone.isa.KernelMetadata)
      : Either[ExecutionFailure, KernelMetadata] =
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
    ).left.map(ExecutionFailure.Failed(_))

  private def toWireCatalog(symbols: net.kurobako.gemstone.isa.ProgramSymbols)
      : Either[ExecutionFailure, ProgramCatalog] = {
    val kernels = symbols.kernels.map(kernel =>
      ProgramKernel(KernelName(kernel.name.value), LinkageName(kernel.linkageName.value))
    )
    symbols.globals
      .foldLeft[Either[ExecutionFailure, Vec[ProgramGlobalMetadata]]](Right(Vec.empty)) { (accumulated, global) =>
        for {
          globals  <- accumulated
          metadata <- ProgramGlobalMetadata
            .validated(GlobalName(global.name.value), global.virtualAddress, global.size, global.pointerOffsets)
            .left.map(ExecutionFailure.Failed(_))
        } yield globals :+ metadata
      }
      .map(globals => ProgramCatalog(kernels, globals))
  }

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
