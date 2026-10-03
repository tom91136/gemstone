package net.kurobako.gemstone.rpc

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.graphics.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*
import scala.annotation.tailrec
import scala.collection.immutable.ArraySeq
import scala.collection.mutable

/** Process-neutral program and memory receiver shared by every native API facade. */
class ExecutionSession(
    backends: List[RuntimeBackend],
    taskLimits: TaskControl.Limits = TaskControl.Limits.Default,
    executionContext: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.parasitic,
    parallelism: Int > 0 = 1,
    specialization: Option[RuntimeBackend.Specialization] = None,
    graphics: Option[GraphicsBackend] = None,
    maxPrintEvents: Int > 0 = 16384
) extends AutoCloseable {
  private val SyntheticAddressBase   = 0x400000000000L
  private val SyntheticAddressStride = 0x100000000L

  /** Allocation aliases share storage and its locks. Disjoint cache lines can progress independently; overlapping
    * scalar, bulk and atomic accesses acquire the same stripes in ascending order.
    */
  private class AllocationStorage(val bytes: Array[Byte]) {
    private val locks     = Array.fill(math.min(64, ((bytes.length.toLong + 63L) / 64L).toInt))(ReentrantLock())
    def lockAll(): Unit   = locks.foreach(_.lock())
    def unlockAll(): Unit = locks.reverseIterator.foreach(_.unlock())
    def access[A](offset: Int >= 0, size: Int >= 0)(operation: => A): A = {
      val start  = offset.raw
      val length = size.raw
      val first  = start / 64
      val last   = if (length == 0) first else (start + length - 1) / 64
      if (first == last) locks(first % locks.length).withLock(operation)
      else {
        val selected = if (last - first + 1 >= locks.length) locks
        else (first to last).map(_ % locks.length).sorted.map(locks(_)).toArray
        selected.foreach(_.lock())
        try operation
        finally selected.reverseIterator.foreach(_.unlock())
      }
    }
  }
  private case class RemoteAllocation(
      kind: AllocationKind,
      storage: AllocationStorage,
      address: Option[GuestAddress] = None,
      hostBacked: Boolean = false
  ) {
    def bytes: Array[Byte] = storage.bytes
    val size: Int > 0      = bytes.length.unsafe[Int > 0]
  }

  /** Program retention and binding reservations share the session state lock. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class ProgramEntry(val loaded: RuntimeBackend.Program) {
    private var retained                               = Set.empty[AllocationId]
    private var reservation: Option[Set[AllocationId]] = None
    private var unloading                              = false
    def allocations: Set[AllocationId]                 = retained
    def isBinding: Boolean                             = reservation.nonEmpty
    def isUnloading: Boolean                           = unloading
    def available: Boolean                             = !unloading
    def beginBinding(allocation: AllocationId): Unit   = {
      require(!isBinding && !unloading, "program cannot begin another binding")
      reservation = Some(retained)
      retained += allocation
    }
    def finishBinding(accepted: Boolean): Unit = {
      if (!accepted) reservation.foreach(previous => retained = previous)
      reservation = None
    }
    def beginUnload(): Unit = {
      require(!isBinding && !unloading, "program cannot begin unloading")
      unloading = true
    }
  }

  /** Dispatch state and host exchanges share the session state lock and lifetime. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Execution(
      val id: CompletionId,
      val program: Option[ProgramId],
      val execute: (
          Memory,
          TaskControl,
          Event => Either[Fault, Unit]
      ) => Either[ExecutionFailure, Option[GraphicsStatistics]],
      val allocations: Set[AllocationId],
      val control: TaskControl
  ) {
    private var currentGraphicsStatistics: Option[GraphicsStatistics] = None
    private var currentStatus: CompletionStatus                       = CompletionStatus.Pending
    private val relocations                                           = mutable.LinkedHashMap.empty[
      (KernelTask.RelocationSource, Long >= 0),
      KernelTask.PointerRelocation
    ]
    // Read only after successful completion, just like the dirty-range snapshot.
    private lazy val allPointerRelocations = Vec.from(relocations.values)
    private var output: Vec[Event.Print]   = Vec.empty

    def graphicsStatistics: Option[GraphicsStatistics] = currentGraphicsStatistics
    def status: CompletionStatus                       = currentStatus
    def pointerRelocations(offset: Int >= 0, limit: Int > 0): Vec[KernelTask.PointerRelocation] =
      if (offset.raw >= allPointerRelocations.length) Vec.empty
      else allPointerRelocations.slice(
        offset.raw,
        offset.raw + math.min(limit.raw, allPointerRelocations.length - offset.raw)
      )
    def prints: Vec[Event.Print]                                                              = output
    def finish(result: CompletionStatus, statistics: Option[GraphicsStatistics] = None): Unit = {
      require(result != CompletionStatus.Pending, "execution cannot finish as pending")
      if (currentStatus == CompletionStatus.Pending) {
        currentGraphicsStatistics = statistics
        currentStatus = result
      }
    }
    def record(relocation: KernelTask.PointerRelocation): Unit = {
      val key = (relocation.source, relocation.sourceOffset)
      // Replacing a pointer keeps the order of its final store.
      val _ = relocations.remove(key)
      relocations.update(key, relocation)
    }
    def record(event: Event.Print): Either[ExecutionFailure, Unit] =
      if (output.size >= maxPrintEvents.raw)
        Left(ExecutionFailure.Failed("device printf event limit exceeded"))
      else { output = output.appended(event); Right(()) }
    val hostMemory: HostMemoryMirror = HostMemoryMirror()
    // Each worker owns its intervals; merge only after all workgroups complete.
    private val dirtyWrites =
      java.util.concurrent.ConcurrentHashMap[Thread, mutable.HashMap[AllocationId, WrittenRanges]]()
    private var exchange: Option[HostSyncExchange]   = None
    private var readWaiters                          = 0
    def hostSync: Option[HostSyncExchange]           = exchange
    def hasReadWaiters: Boolean                      = readWaiters > 0
    def beginHostSync(value: HostSyncExchange): Unit = {
      require(exchange.isEmpty, "host synchronization is already pending")
      exchange = Some(value)
    }
    def endHostSync(): Unit   = exchange = None
    def beginReadWait(): Unit = readWaiters += 1
    def endReadWait(): Unit   = {
      require(readWaiters > 0, "no host read waiter to release")
      readWaiters -= 1
    }
    def markDirty(id: AllocationId, offset: Int >= 0, size: Int >= 0): Unit =
      dirtyWrites.computeIfAbsent(Thread.currentThread(), _ => mutable.HashMap.empty)
        .getOrElseUpdate(id, WrittenRanges()).add(offset, size)
    private lazy val allDirtyRanges: Vec[DirtyRange] = {
      val merged = mutable.HashMap.empty[AllocationId, WrittenRanges]
      dirtyWrites.values().forEach { writes =>
        writes.foreach { (id, ranges) =>
          val target = merged.getOrElseUpdate(id, WrittenRanges())
          ranges.chunks(65536).foreach((offset, size) => target.add(offset, size))
        }
      }
      Vec.from(merged.toArray.sortBy(_._1.raw).view.flatMap { (allocationId, written) =>
        written.chunks(65536).map { (offset, size) =>
          DirtyRange(allocationId, offset.toLong, size)
        }
      })
    }
    def dirtyRanges(offset: Int >= 0, limit: Int > 0): Vec[DirtyRange] =
      if (offset.raw >= allDirtyRanges.length) Vec.empty
      else allDirtyRanges.slice(
        offset.raw,
        offset.raw + math.min(limit.raw, allDirtyRanges.length - offset.raw)
      )
  }
  private case class State(
      allocations: Map[AllocationId, RemoteAllocation],
      programs: Map[ProgramId, ProgramEntry],
      completions: Map[CompletionId, Execution],
      nextAllocation: AllocationId,
      nextProgram: ProgramId,
      nextCompletion: CompletionId
  )

  private val state = AtomicReference(
    State(Map.empty, Map.empty, Map.empty, AllocationId(1L), ProgramId(1L), CompletionId(1L))
  )
  private def execution(id: CompletionId): Execution         = state.get().completions(id)
  private def hostMemory(id: CompletionId): HostMemoryMirror = execution(id).hostMemory
  private enum HostSyncState {
    case AwaitingIssue
    case AwaitingReply
    case Acknowledged(value: Long)
  }
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class HostSyncExchange(val request: HostMemorySync) {
    private var state: HostSyncState = HostSyncState.AwaitingIssue
    def awaitingIssue: Boolean       = state == HostSyncState.AwaitingIssue
    def observed: Option[Long]       = state match {
      case HostSyncState.Acknowledged(value) => Some(value)
      case _                                 => None
    }
    def issue(): HostMemorySync = {
      require(awaitingIssue, "host synchronization request was already issued")
      state = HostSyncState.AwaitingReply
      request
    }
    def acknowledge(ticket: Long, value: Long): Boolean =
      if (state != HostSyncState.AwaitingReply || request.ticket != ticket) false
      else { state = HostSyncState.Acknowledged(value); true }
  }
  private val hostSyncLock = ReentrantLock()
  // A CAS guess only; native storage validates it after host writes or address reuse.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var lastHostAtomic = Option.empty[HostMemoryAtomic]
  private val nextHostSync   = java.util.concurrent.atomic.AtomicLong()
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  @volatile private var hostMemoryEnabled = false

  private val stateLock            = ReentrantLock()
  private val stateChanged         = stateLock.newCondition()
  private val pendingExecutions    = mutable.Queue.empty[Execution]
  private val programLifecycleLock = ReentrantReadWriteLock()
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var executionWorkers = 0
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  @volatile private var closed = false

  def registerAllocation(
      size: Long > 0,
      kind: AllocationKind
  ): Either[ExecutionFailure, AllocationId] = stateLock.withLock {
    val current = state.get()
    if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
    else if (size.raw > Int.MaxValue.toLong)
      Left(ExecutionFailure.InvalidRequest(s"allocation size is outside the supported range: $size"))
    else {
      val id         = current.nextAllocation
      val allocation = RemoteAllocation(kind, AllocationStorage(Array.ofDim[Byte](size.raw.toInt)))
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
        case None             => Left(ExecutionFailure.NotFound(s"allocation ${id.toLong} is not registered"))
        case Some(allocation) =>
          val size = allocation.size.raw.toLong
          if (address.raw <= 0 || address.raw > Long.MaxValue - size)
            Left(ExecutionFailure.InvalidRequest("allocation address alias has an invalid or overflowing range"))
          else if (allocation.address.contains(address) && allocation.hostBacked == hostBacked) Right(())
          else if (allocation.address.nonEmpty)
            Left(ExecutionFailure.InvalidRequest("allocation address alias cannot be remapped"))
          else if (
            current.allocations.values.view.exists(other =>
              other.address.exists(base =>
                address.raw < base.raw + other.bytes.length && base.raw < address.raw + size
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
      Left(ExecutionFailure.NotFound(s"allocation ${id.toLong} is not registered"))
    else if (current.programs.values.view.exists(_.allocations.contains(id)))
      Left(ExecutionFailure.InvalidRequest(s"allocation ${id.toLong} is retained by a program global"))
    else if (current.completions.values.view.exists(_.allocations.contains(id)))
      Left(ExecutionFailure.InvalidRequest(s"allocation ${id.toLong} is retained by completion"))
    else {
      val storage = current.allocations(id).storage
      storage.lockAll()
      try {
        state.set(current.copy(allocations = current.allocations.removed(id)))
        Right(())
      } finally storage.unlockAll()
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
          backendFor(encoding).flatMap(backend => backend.load(image))
        }
        loaded match {
          case Left(error)    => Left(error)
          case Right(program) =>
            val accepted = program.bindFunctionPointerNamespace(id.toLong).flatMap { _ =>
              stateLock.withLock {
                if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
                else {
                  val current = state.get()
                  state.set(current.copy(programs = current.programs.updated(id, ProgramEntry(program))))
                  Right(())
                }
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

  def describeImage(
      encoding: String,
      image: Vec[Byte],
      name: NonEmpty[String]
  ): Either[ExecutionFailure, KernelMetadata] =
    backendFor(encoding).flatMap { backend =>
      backend.load(image) match {
        case Left(error)    => Left(error)
        case Right(program) =>
          try program.kernel(name)
          finally program.close()
      }
    }

  private def specializer: Either[ExecutionFailure, RuntimeBackend.Specialization] =
    specialization.toRight(ExecutionFailure.InvalidRequest("image specialization is unavailable"))

  def describeSpecialization(image: Vec[Byte]): Either[ExecutionFailure, Vec[SpecializationInfo]] =
    specializer.flatMap(_.describe(image))

  def prepareShader(
      image: Vec[Byte],
      entry: NonEmpty[String],
      stage: ShaderStageKind,
      constants: Vec[SpecializationConstant]
  ): Either[ExecutionFailure, PreparedShader] =
    graphics.toRight(ExecutionFailure.Unsupported("graphics backend is unavailable"))
      .flatMap(_.prepare(image, entry, stage, constants))

  def specializeImage(
      image: Vec[Byte],
      constants: Vec[SpecializationConstant]
  ): Either[ExecutionFailure, Vec[Byte]] =
    specializer.flatMap(_.specialize(image, constants))

  def unload(id: ProgramId): Either[ExecutionFailure, Unit] = {
    // Reserve unloading before entering the backend. Keep bound allocations retained
    // until close finishes, without holding the session state lock during backend work.
    val reserved = stateLock.withLock {
      val current = state.get()
      if (!current.programs.get(id).exists(_.available))
        Left(ExecutionFailure.NotFound(s"program ${id.toLong} is not loaded"))
      else if (current.programs(id).isBinding)
        Left(ExecutionFailure.InvalidRequest(s"program ${id.toLong} has a global binding in progress"))
      else if (
        current.completions.values.view.exists(record =>
          record.program.contains(id) && record.status == CompletionStatus.Pending
        )
      )
        Left(ExecutionFailure.InvalidRequest(s"program ${id.toLong} is retained by pending execution"))
      else {
        val program = current.programs(id)
        program.beginUnload()
        Right(program.loaded)
      }
    }
    reserved match {
      case Left(error)    => Left(error)
      case Right(program) =>
        try programLifecycleLock.writeLock().withLock(program.close())
        finally {
          stateLock.withLock {
            val current = state.get()
            state.set(current.copy(programs = current.programs.removed(id)))
          }
          cleanupClosedSession()
        }
        Right(())
    }
  }

  def kernel(id: ProgramId, name: NonEmpty[String]): Either[ExecutionFailure, KernelMetadata] =
    program(id).flatMap(_.kernel(name))

  def symbols(id: ProgramId): Either[ExecutionFailure, ProgramSymbols] =
    program(id).flatMap(_.symbols)

  def global(id: ProgramId, name: NonEmpty[String]): Either[ExecutionFailure, GlobalImage] =
    program(id).flatMap(_.global(name))

  /** Bind outside the state lock while reserving program/allocation ownership across backend preparation. */
  def bindGlobal(
      id: ProgramId,
      name: NonEmpty[String],
      allocation: AllocationId,
      offset: Long >= 0
  ): Either[ExecutionFailure, Unit] = {
    val result = programLifecycleLock.writeLock().withLock {
      val reserved = stateLock.withLock {
        val current = state.get()
        if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
        else if (!current.programs.get(id).exists(_.available))
          Left(ExecutionFailure.NotFound(s"program ${id.toLong} is not loaded"))
        else if (
          current.completions.values.view.exists(r => r.program.contains(id) && r.status == CompletionStatus.Pending)
        )
          Left(ExecutionFailure.InvalidRequest("cannot bind globals during pending execution"))
        else current.allocations.get(allocation) match {
          case None =>
            Left(ExecutionFailure.NotFound(s"allocation ${allocation.toLong} is not registered"))
          case Some(value) =>
            val allocationSize = value.size.toLong
            if (offset > allocationSize)
              Left(ExecutionFailure.InvalidRequest("global binding offset exceeds allocation"))
            else {
              val entry = current.programs(id)
              entry.beginBinding(allocation)
              Right(allocationSize -> entry)
            }
        }
      }
      reserved match {
        case Left(error)          => Left(error)
        case Right((size, entry)) =>
          val attempted = try program(id).flatMap { loaded =>
              loaded.bindGlobal(
                name,
                allocation,
                offset,
                size,
                BindingMemory
              )
            }
          catch {
            case scala.util.control.NonFatal(error) => Left(ExecutionFailure.Failed(error.toString))
          }
          stateLock.withLock {
            entry.finishBinding(attempted.isRight && !closed)
            if (closed) Left(ExecutionFailure.Failed("execution session is closed")) else attempted
          }
      }
    }
    cleanupClosedSession()
    result
  }

  private object BindingMemory extends AddressSpace {
    override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, ArraySeq[Byte]] =
      ExecutionSession.this.read(region, offset, size).left.map(memoryFault(_))
    override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
      ExecutionSession.this.write(region, offset, bytes).left.map(memoryFault(_))
    override def atomicModifyValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)(
        update: Long => Long
    ): Either[Fault, Long] =
      ExecutionSession.this.atomicModifyValue(region, offset, size)(update).left.map(memoryFault(_))
    override def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
      regionAddress(region, offset)
    override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] =
      locateAddress(address, size)
  }

  def writeGlobal(
      id: ProgramId,
      name: NonEmpty[String],
      offset: Long >= 0,
      bytes: Vec[Byte]
  ): Either[ExecutionFailure, Unit] = {
    val accepted = stateLock.withLock {
      val current = state.get()
      if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
      else if (!current.programs.get(id).exists(_.available))
        Left(ExecutionFailure.NotFound(s"program ${id.toLong} is not loaded"))
      else Right(())
    }
    accepted match {
      case Left(error) => Left(error)
      case Right(_)    => programLifecycleLock.writeLock().withLock {
          program(id).flatMap { loaded =>
            loaded.writeGlobal(
              name,
              offset,
              bytes
            )
          }
        }
    }
  }

  // Called after validation while holding stateLock; workers start after that lock is released.
  private def enqueue(
      current: State,
      program: Option[ProgramId],
      references: Set[AllocationId]
  )(execute: (
      Memory,
      TaskControl,
      Event => Either[Fault, Unit]
  ) => Either[ExecutionFailure, Option[GraphicsStatistics]])
      : CompletionId = {
    val id        = current.nextCompletion
    val execution = Execution(id, program, execute, references, TaskControl(taskLimits))
    state.set(current.copy(completions = current.completions.updated(id, execution), nextCompletion = id.next))
    pendingExecutions.enqueue(execution)
    id
  }

  def launch(
      program: ProgramId,
      task: KernelTask,
      descriptorBindings: Vec[DescriptorBinding] = Vec.empty
  ): Either[ExecutionFailure, CompletionId] = {
    val accepted = stateLock.withLock {
      val current = state.get()
      if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
      else if (!current.programs.get(program).exists(_.available))
        Left(ExecutionFailure.NotFound(s"program $program is not loaded"))
      else if (current.programs(program).isBinding)
        Left(ExecutionFailure.InvalidRequest("program global binding is in progress"))
      else {
        val loaded   = current.programs(program).loaded
        val prepared =
          if (!task.discoverArgumentRelocations) Right(task)
          else
            discoverArgumentRelocations(task, current, loaded).flatMap(relocations =>
              task.withPointerRelocations(relocations).left.map(ExecutionFailure.InvalidRequest(_))
            )
        prepared.flatMap { effective =>
          val images = if (descriptorBindings.isEmpty) Right(Set.empty[AllocationId])
          else graphics.toRight(ExecutionFailure.Unsupported("descriptor preparation backend is unavailable")).flatMap(
            _.validateDescriptorBindings(
              descriptorBindings,
              effective.arguments.size,
              id => current.allocations.get(id).map(_.bytes.length.toLong)
            )
              .left.map(ExecutionFailure.InvalidRequest(_))
          )
          for {
            _               <- validateTask(effective, current)
            imageReferences <- images
          } yield enqueue(
            current,
            Some(program),
            referencedAllocations(effective) ++ current.programs(program).allocations ++ imageReferences
          ) {
            (memory, control, handler) =>
              if (descriptorBindings.isEmpty) loaded.execute(effective, memory, control, handler).map(_ => None)
              else graphics.get.withDescriptorBindings(descriptorBindings, memory, control) {
                (preparedMemory, descriptors, relocations) =>
                  val replacements = descriptorBindings.zip(descriptors).map { (binding, range) =>
                    binding.argument.raw -> KernelTask.Argument.Memory(range.allocation, range.offset, Some(range.size))
                  }.toMap
                  effective.withArguments(Vec.from(effective.arguments.zipWithIndex.map { (argument, index) =>
                    replacements.getOrElse(index, argument)
                  })).flatMap(_.withPointerRelocations(relocations)).left.map(ExecutionFailure.InvalidRequest(
                    _
                  )).flatMap { prepared =>
                    loaded.execute(prepared, preparedMemory, control, handler).map(_ => None)
                  }
              }
          }
        }
      }
    }
    accepted.foreach(_ => startExecutionWorker())
    accepted
  }

  def submitGraphics(submission: GraphicsSubmission): Either[ExecutionFailure, CompletionId] = {
    val accepted = stateLock.withLock {
      val current = state.get()
      if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
      else graphics.toRight(ExecutionFailure.Unsupported("graphics backend is unavailable")).flatMap { backend =>
        backend.validate(submission, id => current.allocations.get(id).map(_.bytes.length.toLong))
          .left.map(ExecutionFailure.InvalidRequest(_)).map { references =>
            enqueue(current, None, references) { (memory, control, handler) =>
              backend.execute(submission, memory, control, handler).map(Some(_))
            }
          }
      }
    }
    accepted.foreach(_ => startExecutionWorker())
    accepted
  }

  def readGraphicsStatistics(id: CompletionId): Either[ExecutionFailure, GraphicsStatistics] = stateLock.withLock {
    completionRecord(id).flatMap { record =>
      if (record.status != CompletionStatus.Succeeded)
        Left(ExecutionFailure.InvalidRequest("graphics completion has not succeeded"))
      else record.graphicsStatistics.toRight(ExecutionFailure.InvalidRequest("completion is not a graphics submission"))
    }
  }

  private def startExecutionWorker(): Unit = {
    val startWorker = stateLock.withLock {
      if (closed || pendingExecutions.isEmpty || executionWorkers >= parallelism.raw) false
      else {
        executionWorkers += 1
        true
      }
    }
    if (startWorker)
      try executionContext.execute(() => runPendingExecutions())
      catch {
        case scala.util.control.NonFatal(error) => failPendingExecutions(
            ExecutionFailure.Failed(
              s"cannot schedule execution: ${throwables.detail(error)}"
            )
          )
      }
  }

  private def runPendingExecutions(): Unit = {
    def dequeueLocked(): Option[Execution] = {
      val value = pendingExecutions.dequeueFirst(_ => true)
      if (value.isEmpty) executionWorkers -= 1
      value
    }
    @tailrec def loop(next: Option[Execution]): Unit =
      next match {
        case None            => ()
        case Some(execution) =>
          val id     = execution.id
          val result =
            try
              programLifecycleLock.readLock().withLock {
                execution.execute(
                  CompletionMemory(id),
                  execution.control,
                  event => handleEvent(id, event)
                )
              }
            catch {
              case scala.util.control.NonFatal(error) =>
                Left(ExecutionFailure.Failed(s"program executor failed: ${throwables.detail(error)}"))
            }
          val following = stateLock.withLock {
            val current = state.get()
            current.completions.get(id).foreach { record =>
              result.fold(
                error => record.finish(CompletionStatus.Failed(error)),
                statistics => record.finish(CompletionStatus.Succeeded, statistics)
              )
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
      pendingExecutions.foreach(_.finish(CompletionStatus.Failed(error)))
      pendingExecutions.clear()
      executionWorkers -= 1
      stateChanged.signalAll()
    }
    cleanupClosedSession()
  }

  override def close(): Unit = {
    stateLock.withLock {
      if (!closed) {
        closed = true
        state.get().completions.values.view.foreach(_.control.cancel())
        pendingExecutions.foreach(_.finish(
          CompletionStatus.Failed(ExecutionFailure.Failed("execution session closed before dispatch"))
        ))
        pendingExecutions.clear()
        stateChanged.signalAll()
      }
    }
    cleanupClosedSession()
  }

  private def cleanupClosedSession(): Unit = {
    val programs = stateLock.withLock {
      if (
        !closed || executionWorkers != 0 ||
        state.get().programs.values.view.exists(entry => entry.isBinding || entry.isUnloading)
      ) None
      else {
        val current = state.get()
        state.set(current.copy(allocations = Map.empty, programs = Map.empty, completions = Map.empty))
        Some(current.programs.values.view.map(_.loaded).toVector)
      }
    }
    programs.foreach(loaded => programLifecycleLock.writeLock().withLock(loaded.foreach(_.close())))
  }

  def completion(id: CompletionId): Either[ExecutionFailure, CompletionStatus] =
    stateLock.withLock(completionRecord(id).map(_.status))

  private def completionRecord(id: CompletionId): Either[ExecutionFailure, Execution] =
    state.get().completions.get(id).toRight(ExecutionFailure.NotFound(s"completion ${id.toLong} does not exist"))

  private def succeededCompletion[A](
      id: CompletionId
  )(value: Execution => Either[ExecutionFailure, A]): Either[ExecutionFailure, A] =
    completionRecord(id).flatMap { record =>
      record.status match {
        case CompletionStatus.Pending =>
          Left(ExecutionFailure.InvalidRequest(s"completion ${id.toLong} is still pending"))
        case CompletionStatus.Failed(error) => Left(error)
        case CompletionStatus.Succeeded     => value(record)
      }
    }

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
          val writes = hostMemory(id).takeDirty()
          result = Some(Right(HostMemoryBatch(Vec.empty, writes, !hostMemory(id).hasDirty)))
        case Right(CompletionStatus.Pending) if closed =>
          result = Some(Left(ExecutionFailure.Failed("execution session is closed")))
        case Right(CompletionStatus.Pending) =>
          state.get().completions.get(id).flatMap(_.hostSync) match {
            case Some(exchange) if exchange.awaitingIssue =>
              val request        = exchange.request
              val (writes, sync) =
                if (request.relaxed && request.publish.isEmpty) {
                  (Vec.empty[HostMemoryBytes], Some(exchange.issue()))
                } else if (request.publish.nonEmpty) {
                  val selected = hostMemory(id).takeDirty(request.publish)
                  (selected, if (hostMemory(id).hasDirty(request.publish)) None else Some(exchange.issue()))
                } else {
                  val pending = hostMemory(id).takeDirty()
                  (pending, if (hostMemory(id).hasDirty) None else Some(exchange.issue()))
                }
              result = Some(Right(HostMemoryBatch(Vec.empty, writes, false, sync)))
            case Some(_) => stateChanged.await()
            case None    =>
              val reads = hostMemory(id).requests()
              if (execution(id).hasReadWaiters && reads.nonEmpty)
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
          hostMemory(id).supply(ranges).left.map(ExecutionFailure.InvalidRequest(_)).map { _ =>
            stateChanged.signalAll()
          }
        case _ => Left(ExecutionFailure.InvalidRequest("host memory completion is no longer pending"))
      }
    }

  def supplyHostSync(id: CompletionId, ticket: Long, observed: Long): Either[ExecutionFailure, Unit] =
    stateLock.withLock {
      state.get().completions.get(id).flatMap(_.hostSync) match {
        case Some(exchange) if exchange.acknowledge(ticket, observed) =>
          stateChanged.signalAll()
          Right(())
        case _ =>
          Left(ExecutionFailure.InvalidRequest("host synchronization reply has no matching outstanding request"))
      }
    }

  private def hostBacked(id: AllocationId): Boolean = state.get().allocations.get(id).exists(_.hostBacked)
  private def hostTransfers: Boolean = hostMemoryEnabled || state.get().allocations.values.exists(_.hostBacked)
  private def hostAccessible(address: GuestAddress, size: Int >= 0): Boolean = hostMemoryEnabled ||
    locateAddress(address, size).exists(location =>
      hostBacked(location.allocation)
    )

  // All host atomics linearize in the native process with CPU atomics. CAS also
  // supports arbitrary interpreter RMW functions without serializing Scala closures.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def atomicHostMemory(
      id: CompletionId,
      address: GuestAddress,
      size: Int in 1 ~ 8
  )(update: Long => Long): Either[Fault, Long] =
    if ((size.raw != 4 && size.raw != 8) || address.raw % size.raw != 0)
      Left(Fault.Memory("host atomics require aligned 32- or 64-bit storage"))
    else if (address.raw <= 0 || address.raw > Long.MaxValue - size.raw || !hostAccessible(address, size))
      Left(Fault.Memory("host atomic address is unavailable or overflows"))
    else hostSyncLock.withLock {
      val atomicSize: Int in (1, 2, 4, 8) = if (size.raw == 4) 4 else 8
      val mask                            = if (size.raw == 4) 0xffffffffL else -1L
      var expected = lastHostAtomic.filter(request => request.address == address && request.size.raw == size.raw)
        .fold(0L)(_.desired) & mask
      var result: Option[Either[Fault, Long]] = None
      while (result.isEmpty) {
        val desired  = update(expected) & mask
        val request  = HostMemoryAtomic(address, atomicSize, expected, desired)
        val observed = synchronizeHost(id, Some(request)).map(_ & mask)
        observed match {
          case Left(error)                       => result = Some(Left(error))
          case Right(value) if value == expected =>
            lastHostAtomic = Some(request)
            result = Some(Right(value))
          case Right(value) => expected = value
        }
      }
      result.get
    }

  private def atomicHostLoad(
      id: CompletionId,
      address: GuestAddress,
      size: Int in 1 ~ 8
  ): Either[Fault, Long] =
    size.raw.refine[Int in (1, 2, 4, 8)].left.map(_ =>
      Fault.Memory("host atomic loads require naturally aligned 8-, 16-, 32-, or 64-bit storage")
    ).flatMap { atomicSize =>
      if (address.raw % atomicSize.raw != 0)
        Left(Fault.Memory("host atomic loads require naturally aligned 8-, 16-, 32-, or 64-bit storage"))
      else if (address.raw <= 0 || address.raw > Long.MaxValue - size.raw || !hostAccessible(address, size))
        Left(Fault.Memory("host atomic address is unavailable or overflows"))
      else hostSyncLock.withLock {
        val request = HostMemoryAtomic(address, atomicSize, 0L, 0L, HostMemoryAtomicOperation.Load)
        val target  = Vec(HostMemoryRange(address, size))
        synchronizeHost(id, Some(request), target, relaxed = true).map { observed =>
          val mask  = if (size.raw == 8) -1L else (1L << (size.raw * 8)) - 1L
          val value = observed & mask
          stateLock.withLock(hostMemory(id).observeAtomic(address.raw, LittleEndian.toVec(value, size.scalarByteWidth)))
          value
        }
      }
    }

  private def synchronizeHost(
      id: CompletionId,
      atomic: Option[HostMemoryAtomic],
      publish: Vec[HostMemoryRange] = Vec.empty,
      atomicStores: Vec[HostMemoryAtomic] = Vec.empty,
      relaxed: Boolean = false
  ): Either[Fault, Long] =
    stateLock.withLock {
      val exchange =
        HostSyncExchange(HostMemorySync(nextHostSync.incrementAndGet(), atomic, publish, atomicStores, relaxed))
      execution(id).beginHostSync(exchange)
      stateChanged.signalAll()
      while (!closed && exchange.observed.isEmpty) stateChanged.await()
      execution(id).endHostSync()
      if (publish.isEmpty && !relaxed) hostMemory(id).invalidateClean()
      // A poller can consume the acknowledgement wakeup before this worker does.
      // Removing the exchange makes other workers' pending reads eligible again.
      stateChanged.signalAll()
      if (closed) Left(Fault.Cancelled) else Right(exchange.observed.get)
    }

  private def flushHostWrites(id: CompletionId): Either[Fault, Unit] = hostSyncLock.withLock {
    if (stateLock.withLock(hostMemory(id).hasDirty)) synchronizeHost(id, None).map(_ => ())
    else Right(())
  }

  private def flushHostMemory(id: CompletionId): Either[Fault, Unit] = stateLock.withLock {
    execution(id).beginReadWait()
    stateChanged.signalAll()
    while (!closed && hostMemory(id).hasPending) stateChanged.await()
    execution(id).endReadWait()
    if (closed) Left(Fault.Cancelled) else Right(())
  }

  private def readHostMemory(id: CompletionId, address: GuestAddress, size: Int >= 0): Memory.ReadResult[Vec[Byte]] =
    stateLock.withLock {
      if (!hostAccessible(address, size) || address.raw <= 0 || address.raw > Long.MaxValue - size.raw.toLong)
        Left(Fault.Memory(
          s"host memory read at 0x${address.raw.toHexString} (${size.raw} bytes) is unavailable or overflows"
        ))
      else hostMemory(id).read(address.raw, size.raw).toRight(Memory.Pending)
    }

  private def writeHostMemory(id: CompletionId, address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    stateLock.withLock {
      if (
        !hostAccessible(address, bytes.nonNegativeSize) || address.raw <= 0 ||
        address.raw > Long.MaxValue - bytes.size.toLong
      )
        Left(Fault.Memory(
          s"host memory write at 0x${address.raw.toHexString} (${bytes.size} bytes) is unavailable or overflows"
        ))
      else {
        val atomicWidth: Int in (0, 1, 2, 4, 8) =
          bytes.size.refine[Int in (1, 2, 4, 8)].toOption
            .filter(width => address.raw % width.raw == 0).fold[Int in (0, 1, 2, 4, 8)](0)(identity)
        hostMemory(id).write(address.raw, bytes, atomicWidth)
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

  def completionDirtyRanges(
      id: CompletionId,
      offset: Int >= 0,
      limit: Int > 0
  ): Either[ExecutionFailure, Vec[DirtyRange]] =
    stateLock.withLock {
      succeededCompletion(id)(execution => Right(execution.dirtyRanges(offset, limit)))
    }

  def completionPointerRelocations(
      id: CompletionId,
      offset: Int >= 0,
      limit: Int > 0
  ): Either[ExecutionFailure, Vec[KernelTask.PointerRelocation]] =
    stateLock.withLock(succeededCompletion(id)(record => Right(record.pointerRelocations(offset, limit))))

  def completionPrints(id: CompletionId): Either[ExecutionFailure, String] =
    stateLock.withLock(succeededCompletion(id)(record => renderPrints(record.prints)))

  private def renderPrints(prints: Vec[Event.Print]): Either[ExecutionFailure, String] =
    prints
      .foldLeft[Either[ExecutionFailure, StringBuilder]](Right(StringBuilder())) { (accumulated, event) =>
        accumulated.flatMap(builder =>
          PrintRenderer.render(event, maxOutputChars = 1024 * 1024).left.map(ExecutionFailure.Failed(_)).map(rendered =>
            builder.append(rendered)
          )
        )
      }
      .map(_.result())

  def releaseCompletion(id: CompletionId): Either[ExecutionFailure, Unit] = stateLock.withLock {
    completionRecord(id).flatMap { record =>
      if (record.status == CompletionStatus.Pending)
        Left(ExecutionFailure.InvalidRequest(s"completion ${id.toLong} is still pending"))
      else {
        val current = state.get()
        state.set(current.copy(completions = current.completions.removed(id)))
        Right(())
      }
    }
  }

  private def accessAllocation[A](
      id: AllocationId,
      offset: Long >= 0,
      size: Int >= 0,
      completion: Option[CompletionId] = None
  )(operation: (RemoteAllocation, Int >= 0) => A): Either[ExecutionFailure, A] =
    if (closed && completion.isEmpty) Left(ExecutionFailure.Failed("execution session is closed"))
    else state.get().allocations.get(id).toRight(
      ExecutionFailure.NotFound(s"allocation ${id.toLong} is not registered")
    ).flatMap { allocation =>
      checkedRange(allocation.bytes.length, offset, size).left.map(error =>
        ExecutionFailure.InvalidRequest(s"allocation ${id.toLong}: ${error.message}")
      ).flatMap { start =>
        allocation.storage.access(start, size) {
          if (!state.get().allocations.contains(id))
            Left(ExecutionFailure.NotFound(s"allocation ${id.toLong} is not registered"))
          else Right(operation(allocation, start))
        }
      }
    }

  def read(
      id: AllocationId,
      offset: Long >= 0,
      size: Int >= 0
  ): Either[ExecutionFailure, Vec[Byte]] =
    accessAllocation(id, offset, size) { (allocation, start) =>
      Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(allocation.bytes, start.raw, start.raw + size.raw))
    }

  def readValue(
      id: AllocationId,
      offset: Long >= 0,
      size: Int in 1 ~ 8
  ): Either[ExecutionFailure, Long] =
    accessAllocation(id, offset, size)((allocation, start) =>
      LittleEndian.decode(allocation.bytes, start.raw, size.scalarByteWidth)
    )

  def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[ExecutionFailure, Unit] =
    write(id, offset, bytes, None)

  def writeValue(
      id: AllocationId,
      offset: Long >= 0,
      value: Long,
      size: Int in 1 ~ 8
  ): Either[ExecutionFailure, Unit] = writeValue(id, offset, value, size, None)

  def atomicModifyValue(
      id: AllocationId,
      offset: Long >= 0,
      size: Int in 1 ~ 8
  )(update: Long => Long): Either[ExecutionFailure, Long] =
    atomicModifyValue(id, offset, size, None)(update)

  private class CompletionMemory(completion: CompletionId) extends AddressSpace {
    override def hasNativePointers(region: AllocationId): Boolean =
      state.get().allocations.get(region).exists(_.address.nonEmpty)

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
    override def prepareReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] = {
      val staged = ranges.flatMap((address, size) =>
        if (locateAddress(address, size).exists(location => !hostBacked(location.allocation)))
          Vec.empty[(GuestAddress, Int >= 0)]
        else segments(address, size)
      ).foldLeft[Either[Fault, Unit]](Right(())) {
        case (result, (address, size)) =>
          result.flatMap(_ =>
            withAllocation(address, size) { (id, _) =>
              if (id.exists(value => !hostBacked(value))) Right(())
              else readHostMemory(completion, address, size) match {
                case Left(Memory.Pending) => Right(())
                case Left(fault: Fault)   => Left(fault)
                case Right(_)             => Right(())
              }
            }
          )
      }
      staged.flatMap(_ => flushHostReads())
    }
    private def systemHostRange(address: GuestAddress, size: Int >= 0): Boolean =
      hostTransfers && hostAccessible(address, size) && segments(address, size).forall { (begin, length) =>
        findContainingAllocation(begin, length).forall((id, _) => hostBacked(id))
      }

    override def prepareSystemReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] =
      // System loads from host-backed storage use native atomic loads at the
      // actual scalar width. Do not first copy those control words by memcpy.
      prepareReads(Vec.from(ranges.filterNot((address, size) => systemHostRange(address, size))))
    override def flushHostWrites(): Either[Fault, Unit] = ExecutionSession.this.flushHostWrites(completion)
    override def invalidateHostReads(): Unit            = stateLock.withLock(hostMemory(completion).invalidateClean())
    override def defersHostReads: Boolean               = hostTransfers
    override def flushHostReads(): Either[Fault, Unit]  = flushHostMemory(completion)

    private inline def withAllocation[A, E >: Fault](
        address: GuestAddress,
        size: Int >= 0
    )(inline operation: (Option[AllocationId], Long >= 0) => Either[E, A]): Either[E, A] =
      findContainingAllocation(address, size) match {
        case Some((id, relative))       => operation(Some(id), relative)
        case None if address.unwrap > 0 => operation(None, address.unwrap.unsafe[Long >= 0])
        case None                       => Left(Fault.Memory(s"address $address is not bound"))
      }

    // Explicit registrations may share a view while using several non-overlapping
    // backing allocations. Split only at known boundaries; unknown gaps remain exact.
    private def segments(address: GuestAddress, size: Int >= 0): Vec[(GuestAddress, Int >= 0)] = {
      val end = address.raw + size.raw.toLong
      if (address.raw <= 0 || end < address.raw) Vec((address, size))
      else {
        val boundaries = state.get().allocations.toVector.flatMap { (id, allocation) =>
          val base = allocation.address.getOrElse(syntheticAddress(id)).raw
          Vector(base, base + allocation.size.raw)
        }.filter(point => point > address.raw && point < end)
        val points = (Vector(address.raw, end) ++ boundaries).distinct.sorted
        Vec.from(points.zip(points.drop(1)).map { (begin, end) =>
          (GuestAddress(begin), (end - begin).toInt.unsafe[Int >= 0])
        })
      }
    }

    private def readSegments(address: GuestAddress, size: Int >= 0): Memory.ReadResult[Vec[Byte]] =
      segments(address, size).foldLeft[Memory.ReadResult[Vec[Byte]]](Right(Vec.empty)) {
        case (result, (begin, length)) =>
          result.flatMap(bytes =>
            withAllocation(begin, length) { (id, offset) =>
              val part = if (id.isEmpty || id.exists(hostBacked(_))) readHostMemory(completion, begin, length)
              else ExecutionSession.this.read(id.get, offset, length).left.map(memoryFault(_))
              part.map(bytes ++ _)
            }
          )
      }

    private def writeSegments(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
      segments(address, bytes.nonNegativeSize).foldLeft[Either[Fault, Unit]](Right(())) {
        case (result, (begin, length)) =>
          result.flatMap(_ =>
            withAllocation(begin, length) { (id, offset) =>
              val part = bytes.slice((begin.raw - address.raw).toInt, (begin.raw - address.raw).toInt + length.raw)
              if (id.isEmpty || id.exists(hostBacked(_))) writeHostMemory(completion, begin, part)
              else ExecutionSession.this.write(id.get, offset, part, Some(completion)).left.map(memoryFault(_))
            }
          )
      }

    // Device atomics serialize against every lane using this address space. Host
    // CPU participation still requires a separate coherence/atomic protocol.
    private def atomicAddress(address: GuestAddress, size: Int in 1 ~ 8)(
        update: Long => Long
    ): Either[Fault, Long] = awaitRead {
      stateLock.withLock {
        readSegments(address, size.nonNegative).flatMap { bytes =>
          val previous = LittleEndian.decode(bytes, 0, size.scalarByteWidth)
          writeSegments(address, LittleEndian.toVec(update(previous), size.scalarByteWidth)).map(_ => previous)
        }
      }
    }

    private def crossesRegion(region: AllocationId, offset: Long >= 0, size: Int): Boolean =
      state.get().allocations.get(region)
        .exists(allocation =>
          allocation.address.nonEmpty && (size > allocation.size.raw || offset.raw > allocation.size.raw - size)
        )

    override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
      awaitRead(readDeferred(region, offset, size))

    override def readDeferred(region: AllocationId, offset: Long >= 0, size: Int >= 0): Memory.ReadResult[Vec[Byte]] =
      if (hostBacked(region) || crossesRegion(region, offset, size.raw))
        regionAddress(region, offset).flatMap(readSegments(_, size))
      else ExecutionSession.this.read(region, offset, size).left.map(memoryFault(_))

    override def readValue(
        region: AllocationId,
        offset: Long >= 0,
        size: Int in 1 ~ 8
    ): Either[Fault, Long] =
      awaitRead(readValueDeferred(region, offset, size))

    override def readValueDeferred(
        region: AllocationId,
        offset: Long >= 0,
        size: Int in 1 ~ 8
    ): Memory.ReadResult[Long] =
      if (hostBacked(region) || crossesRegion(region, offset, size.raw))
        readDeferred(region, offset, size.nonNegative).map(bytes => LittleEndian.decode(bytes, 0, size.scalarByteWidth))
      else ExecutionSession.this.readValue(region, offset, size).left.map(memoryFault(_))

    override def readSystemValue(
        address: GuestAddress,
        size: Int in 1 ~ 8
    ): Either[Fault, Long] =
      if (hostTransfers && systemHostRange(address, size.nonNegative))
        atomicHostLoad(completion, address, size)
      else readValue(address, size)

    override def write(
        region: AllocationId,
        offset: Long >= 0,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] =
      if (hostBacked(region) || crossesRegion(region, offset, bytes.size))
        regionAddress(region, offset).flatMap(writeSegments(_, bytes))
      else ExecutionSession.this.write(region, offset, bytes, Some(completion)).left.map(memoryFault(_))

    override def writeValue(
        region: AllocationId,
        offset: Long >= 0,
        value: Long,
        size: Int in 1 ~ 8
    ): Either[Fault, Unit] =
      if (hostBacked(region) || crossesRegion(region, offset, size.raw))
        write(region, offset, LittleEndian.toVec(value, size.scalarByteWidth))
      else ExecutionSession.this.writeValue(
        region,
        offset,
        value,
        size,
        Some(completion)
      ).left.map(memoryFault(_))

    override def writeSystemValue(
        address: GuestAddress,
        value: Long,
        size: Int in 1 ~ 8
    ): Either[Fault, Unit] = writeSystemBatch(Vec((address, value, size)))

    override def writeSystemBatch(
        writes: Vec[(GuestAddress, Long, Int in 1 ~ 8)]
    ): Either[Fault, Unit] = {
      val nativeAtomic = writes.flatMap { (address, value, size) =>
        size.raw.refine[Int in (1, 2, 4, 8)].toOption.filter { width =>
          hostTransfers && systemHostRange(address, width) && address.raw % width.raw == 0
        }.map(width => HostMemoryAtomic(address, width, 0L, value, HostMemoryAtomicOperation.Store))
      }
      val stored = writes.foldLeft[Either[Fault, Unit]](Right(())) {
        case (result, (address, value, size)) =>
          if (nativeAtomic.exists(_.address == address)) result
          else result.flatMap(_ => writeValue(address, value, size))
      }
      stored.flatMap { _ =>
        val selected = Vec.from(writes.flatMap { (address, _, size) =>
          segments(address, size.nonNegative).collect {
            case (begin, length)
                if withAllocation(begin, length)((id, _) =>
                  Right(id.isEmpty || id.exists(hostBacked(_)))
                ).getOrElse(false) => HostMemoryRange(begin, length.raw.unsafe[Int > 0])
          }
        })
        val atomicRanges = Vec.from(nativeAtomic.map(atom => HostMemoryRange(atom.address, atom.size)))
        if (!hostTransfers || (selected.isEmpty && nativeAtomic.isEmpty)) Right(())
        else hostSyncLock.withLock {
          synchronizeHost(
            completion,
            None,
            Vec.from((selected ++ atomicRanges).distinct),
            nativeAtomic,
            relaxed = true
          ).map { _ =>
            stateLock.withLock(nativeAtomic.foreach(atom =>
              hostMemory(completion).observeAtomic(
                atom.address.raw,
                LittleEndian.toVec(atom.desired, atom.size)
              )
            ))
          }
        }
      }
    }

    override def atomicModifyValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)(
        update: Long => Long
    ): Either[Fault, Long] =
      if (hostBacked(region))
        regionAddress(region, offset).flatMap(atomicHostMemory(completion, _, size)(update))
      else if (crossesRegion(region, offset, size.raw))
        regionAddress(region, offset).flatMap(atomicAddress(_, size)(update))
      else deviceAtomic(ExecutionSession.this.atomicModifyValue(
        region,
        offset,
        size,
        Some(completion)
      )(update).left.map(memoryFault(_)))

    override def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
      regionAddress(region, offset)

    override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] =
      locateAddress(address, size).orElse(
        if (
          address.raw > 0 &&
          (hostMemoryEnabled || segments(address, size).forall((begin, length) => locateAddress(begin, length).isRight))
        ) Right(AddressSpace.Location.Absolute(address))
        else Left(Fault.Memory(s"address $address is not bound"))
      )

    override def readDeferred(address: GuestAddress, size: Int >= 0): Memory.ReadResult[Vec[Byte]] =
      readSegments(address, size)

    override def read(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] =
      withAllocation(address, size)((id, offset) =>
        if (id.isEmpty || id.exists(hostBacked(_))) awaitRead(readSegments(address, size))
        else ExecutionSession.this.read(id.get, offset, size).left.map(memoryFault(_))
      )

    override def readValue(
        address: GuestAddress,
        size: Int in 1 ~ 8
    ): Either[Fault, Long] =
      withAllocation(address, size.nonNegative)((id, offset) =>
        if (id.isEmpty || id.exists(hostBacked(_))) awaitRead(readSegments(address, size.nonNegative)).map(bytes =>
          LittleEndian.decode(bytes, 0, size.scalarByteWidth)
        )
        else ExecutionSession.this.readValue(id.get, offset, size).left.map(memoryFault(_))
      )

    override def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
      withAllocation(address, bytes.nonNegativeSize)((id, offset) =>
        if (id.isEmpty || id.exists(hostBacked(_))) writeSegments(address, bytes)
        else ExecutionSession.this.write(id.get, offset, bytes, Some(completion)).left.map(memoryFault(_))
      )

    override def writeValue(
        address: GuestAddress,
        value: Long,
        size: Int in 1 ~ 8
    ): Either[Fault, Unit] =
      withAllocation(address, size.nonNegative)((id, offset) =>
        if (id.isEmpty || id.exists(hostBacked(_)))
          writeSegments(address, LittleEndian.toVec(value, size.scalarByteWidth))
        else ExecutionSession.this.writeValue(id.get, offset, value, size, Some(completion)).left.map(memoryFault(_))
      )

    override def atomicModifyValue(
        address: GuestAddress,
        size: Int in 1 ~ 8
    )(update: Long => Long): Either[Fault, Long] =
      withAllocation(address, size.nonNegative)((id, offset) =>
        if (id.isEmpty || id.exists(hostBacked(_))) atomicHostMemory(completion, address, size)(update)
        else deviceAtomic(ExecutionSession.this.atomicModifyValue(
          id.get,
          offset,
          size,
          Some(completion)
        )(update).left.map(memoryFault(_)))
      )
  }

  private def recordPointerRelocation(completion: CompletionId, relocation: KernelTask.PointerRelocation): Unit =
    stateLock.withLock {
      val current = state.get()
      current.completions.get(completion).foreach { record =>
        record.record(relocation)
      }
    }

  private def recordPrint(completion: CompletionId, event: Event.Print): Either[ExecutionFailure, Unit] =
    stateLock.withLock {
      val current = state.get()
      current.completions.get(completion) match {
        case None         => Left(ExecutionFailure.NotFound(s"completion $completion does not exist"))
        case Some(record) => record.record(event)
      }
    }

  private def write(
      id: AllocationId,
      offset: Long >= 0,
      bytes: Vec[Byte],
      completion: Option[CompletionId]
  ): Either[ExecutionFailure, Unit] =
    accessAllocation(id, offset, bytes.nonNegativeSize, completion) { (allocation, start) =>
      val _ = bytes.copyToArray(allocation.bytes, start.raw, bytes.length)
      if (bytes.nonEmpty) markDirty(completion, id, start, bytes.nonNegativeSize)
    }

  private def writeValue(
      id: AllocationId,
      offset: Long >= 0,
      value: Long,
      size: Int in 1 ~ 8,
      completion: Option[CompletionId]
  ): Either[ExecutionFailure, Unit] =
    accessAllocation(id, offset, size, completion) { (allocation, start) =>
      LittleEndian.write(value, allocation.bytes, start.raw, size.scalarByteWidth)
      markDirty(completion, id, start, size)
    }

  private def atomicModifyValue(
      id: AllocationId,
      offset: Long >= 0,
      size: Int in 1 ~ 8,
      completion: Option[CompletionId]
  )(update: Long => Long): Either[ExecutionFailure, Long] =
    accessAllocation(id, offset, size, completion) { (allocation, start) =>
      val width    = size.scalarByteWidth
      val previous = LittleEndian.decode(allocation.bytes, start.raw, width)
      LittleEndian.write(update(previous), allocation.bytes, start.raw, width)
      markDirty(completion, id, start, size)
      previous
    }

  private def markDirty(
      completion: Option[CompletionId],
      id: AllocationId,
      offset: Int >= 0,
      size: Int >= 0
  ): Unit = completion.foreach { completionId =>
    execution(completionId).markDirty(id, offset, size)
  }

  /** Scans pointer-bearing scalar arguments for values that name a registered allocation. The client sends raw argument
    * bytes; the service owns the allocation bases, so it is the single place that can resolve them.
    */
  private def discoverArgumentRelocations(
      task: KernelTask,
      current: State,
      loaded: RuntimeBackend.Program
  ): Either[ExecutionFailure, Vec[KernelTask.PointerRelocation]] =
    loaded.kernel(task.kernelName).map { metadata =>
      Vec.from(task.arguments.zip(Vec.indices(task.arguments.nonNegativeSize)).flatMap { case (argument, index) =>
        argument match {
          case KernelTask.Argument.Scalar(bytes) if metadata.parameters.lift(index.raw).exists(_.containsPointers) =>
            scanArgument(bytes, index, current)
          case _ => Vector.empty
        }
      })
    }

  private def scanArgument(
      bytes: Vec[Byte],
      index: Int >= 0,
      current: State
  ): Vector[KernelTask.PointerRelocation] = {
    val array = bytes.toArray
    (0 to bytes.size - 8).by(4).flatMap { offset =>
      val value = LittleEndian.decode(array, offset, 8)
      locateNativeAddress(value, current).map { case (allocation, targetOffset) =>
        KernelTask.PointerRelocation(
          KernelTask.RelocationSource.Argument(index),
          offset.toLong.unsafe[Long >= 0],
          allocation,
          targetOffset
        )
      }
    }.toVector
  }

  private def locateNativeAddress(value: Long, current: State): Option[(AllocationId, Long >= 0)] =
    current.allocations.view.flatMap { case (allocation, registered) =>
      registered.address match {
        case Some(base) if inAllocation(base.raw, registered.size.raw.toLong, value) =>
          Some(allocation -> (value - base.raw).unsafe[Long >= 0])
        case _ => None
      }
    }.headOption

  private def inAllocation(base: Long, size: Long, value: Long): Boolean =
    java.lang.Long.compareUnsigned(value, base) >= 0 && java.lang.Long.compareUnsigned(value - base, size) <= 0

  private def validateTask(task: KernelTask, current: State): Either[ExecutionFailure, Unit] =
    task.arguments.collectFirst {
      case KernelTask.Argument.Memory(region, _, _) if !current.allocations.contains(region) =>
        val id = region
        ExecutionFailure.NotFound(s"kernel argument refers to unregistered allocation ${id.toLong}")
      case KernelTask.Argument.Memory(region, offset, _)
          if offset.raw > current.allocations(region).bytes.length.toLong =>
        val id = region
        ExecutionFailure.InvalidRequest(
          s"kernel argument offset $offset is outside allocation ${id.toLong}"
        )
      case KernelTask.Argument.Memory(region, offset, Some(range))
          if range.raw > current.allocations(region).bytes.length.toLong - offset.raw =>
        ExecutionFailure.InvalidRequest("kernel argument range exceeds its allocation")
    }.toLeft(()).flatMap { _ =>
      task.pointerRelocations.collectFirst {
        case KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(region), _, _, _)
            if !current.allocations.contains(region) =>
          val source = region
          ExecutionFailure.NotFound(
            s"pointer relocation refers to unregistered source allocation $source"
          )
        case KernelTask.PointerRelocation(_, _, target, _)
            if !current.allocations.contains(target) =>
          val id = target
          ExecutionFailure.NotFound(
            s"pointer relocation refers to unregistered target allocation ${id.toLong}"
          )
        case KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(region), sourceOffset, _, _)
            if sourceOffset.raw >
              current.allocations(region).bytes.length.toLong - abi.MachineWordSize.toLong =>
          val source = region
          ExecutionFailure.InvalidRequest(
            s"pointer relocation source offset $sourceOffset exceeds allocation $source"
          )
        case KernelTask.PointerRelocation(_, _, target, targetOffset)
            if targetOffset.raw > current.allocations(target).bytes.length.toLong =>
          val id = target
          ExecutionFailure.InvalidRequest(
            s"pointer relocation target offset $targetOffset exceeds allocation ${id.toLong}"
          )
        case KernelTask.PointerRelocation(KernelTask.RelocationSource.Argument(index), sourceOffset, _, _)
            if sourceOffset.raw >
              (task.arguments(index.raw) match {
                case KernelTask.Argument.Scalar(bytes) => bytes.length.toLong - abi.MachineWordSize.toLong
                case _                                 => -1L
              }) =>
          ExecutionFailure.InvalidRequest(
            s"pointer relocation source offset $sourceOffset exceeds argument $index"
          )
      }.toLeft(())
    }

  private def referencedAllocations(task: KernelTask): Set[AllocationId] =
    task.arguments.collect {
      case KernelTask.Argument.Memory(region, _, _) => region
    }.toSet ++ task.pointerRelocations.foldLeft(Set.empty[AllocationId]) { (allocations, relocation) =>
      val withTarget = allocations + relocation.target
      relocation.source match {
        case KernelTask.RelocationSource.Memory(region) => withTarget + region
        case KernelTask.RelocationSource.Argument(_)    => withTarget
      }
    }

  private def program(id: ProgramId): Either[ExecutionFailure, RuntimeBackend.Program] =
    stateLock.withLock {
      state.get().programs.get(id).filter(_.available).map(_.loaded)
        .toRight(ExecutionFailure.NotFound(s"program ${id.toLong} is not loaded"))
    }

  private def backendFor(encoding: String): Either[ExecutionFailure, RuntimeBackend] =
    backends.find(_.encoding == encoding)
      .toRight(ExecutionFailure.Unsupported(s"unknown program encoding: $encoding"))

  private def memoryFault(error: ExecutionFailure): Fault = Fault.Memory(s"memory operation failed: ${error.message}")

  private def regionAddress(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
    state.get().allocations.get(region).toRight(Fault.Memory(s"memory region ${region.toLong} is absent"))
      .map(allocation => allocation.address.getOrElse(syntheticAddress(region)).offset(offset.raw))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneNoIterator.no-iterator"))
  private def findContainingAllocation(address: GuestAddress, size: Int >= 0): Option[(AllocationId, Long >= 0)] = {
    val allocations                               = state.get().allocations.iterator
    var result: Option[(AllocationId, Long >= 0)] = None
    while (allocations.hasNext && result.isEmpty) {
      val (id, allocation) = allocations.next()
      val base             = allocation.address.getOrElse(syntheticAddress(id))
      val relative         = address.raw - base.raw
      if (
        java.lang.Long.compareUnsigned(address.raw, base.raw) >= 0 &&
        relative >= 0L && size.raw.toLong <= allocation.size.raw.toLong &&
        relative <= allocation.size.raw.toLong - size.raw.toLong
      ) result = Some(id -> relative.unsafe[Long >= 0])
    }
    result
  }

  private def locateAddress(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location.Within] =
    findContainingAllocation(address, size) match {
      case Some((id, relative)) =>
        Right(AddressSpace.Location.Within(id, relative))
      case None => Left(Fault.Memory(s"address $address is not bound"))
    }

  private def syntheticAddress(id: AllocationId): GuestAddress =
    GuestAddress(SyntheticAddressBase + (id.toLong - 1L) * SyntheticAddressStride)

  private def handleEvent(completion: CompletionId, event: Event): Either[Fault, Unit] =
    event match {
      case event: Event.Print =>
        recordPrint(completion, event).left.map(error =>
          Fault.EventHandling(s"print capture failed: ${error.message}")
        )
      case Event.PointerRelocation(source, sourceOffset, target, targetOffset) =>
        recordPointerRelocation(
          completion,
          KernelTask.PointerRelocation(
            KernelTask.RelocationSource.Memory(source),
            sourceOffset,
            target,
            targetOffset
          )
        )
        Right(())
    }

  private def checkedRange(
      length: Int,
      offset: Long >= 0,
      size: Int >= 0
  ): Either[ExecutionFailure, Int >= 0] =
    ByteRange
      .start(length, offset.raw, size.raw)
      .toRight(ExecutionFailure.InvalidRequest(
        s"byte range $offset + $size exceeds allocation size $length"
      ))
}
