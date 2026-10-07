package net.kurobako.gemstone.rpc

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
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
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.vm.*
import scala.annotation.tailrec
import scala.collection.mutable

object ExecutionSession {
  private val SyntheticAddressBase   = 0x400000000000L
  private val SyntheticAddressStride = 0x100000000L

  private val Closed = ExecutionFailure.Failed("execution session is closed")

  private def notRegistered(id: AllocationId): ExecutionFailure =
    ExecutionFailure.NotFound(s"allocation $id is not registered")

  private def notLoaded(id: ProgramId): ExecutionFailure = ExecutionFailure.NotFound(s"program $id is not loaded")

  private def syntheticAddress(id: AllocationId): GuestAddress =
    GuestAddress(SyntheticAddressBase + (id.raw - 1L) * SyntheticAddressStride)

  private def failure(fault: Fault): ExecutionFailure = fault match {
    case Fault.InvalidRequest(detail) => ExecutionFailure.InvalidRequest(detail)
    case Fault.NotFound(detail)       => ExecutionFailure.NotFound(detail)
    case Fault.Unsupported(detail)    => ExecutionFailure.Unsupported(detail)
    case _                            => ExecutionFailure.Failed(fault.message)
  }

  private def memoryFault(error: ExecutionFailure): Fault = Fault.Memory(s"memory operation failed: ${error.message}")

  /** Program retention and binding reservations share the session state lock. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class ProgramEntry(val loaded: Backend.Program) {
    private var retained                               = Set.empty[AllocationId]
    private var reservation: Option[Set[AllocationId]] = None
    private var unloading                              = false
    def allocations: Set[AllocationId]                 = retained
    def isBinding: Boolean                             = reservation.nonEmpty
    def isUnloading: Boolean                           = unloading
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

  private case class State(
      allocations: Map[AllocationId, StoredAllocation],
      programs: Map[ProgramId, ProgramEntry],
      completions: Map[CompletionId, Completion],
      nextAllocation: AllocationId,
      nextProgram: ProgramId,
      nextCompletion: CompletionId,
      addresses: AllocationAddressIndex = AllocationAddressIndex(Map.empty),
      hasHostBackedAllocations: Boolean = false
  ) {
    def withAllocations(updated: Map[AllocationId, StoredAllocation]): State = copy(
      allocations = updated,
      hasHostBackedAllocations = updated.values.exists(_.hostBacked),
      addresses = AllocationAddressIndex(updated)
    )

    def loaded(id: ProgramId): Either[ExecutionFailure, ProgramEntry] =
      programs.get(id).filterNot(_.isUnloading).toRight(notLoaded(id))

    def hasPendingCompletion(program: ProgramId): Boolean =
      completions.values.exists(completion => completion.program.contains(program) && completion.isPending)
  }
}

/** Process-neutral program and memory receiver shared by every native API facade. */
class ExecutionSession(
    backends: List[Backend],
    taskLimits: TaskControl.Limits = TaskControl.Limits.Default,
    executionContext: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.parasitic,
    parallelism: Int > 0 = 1,
    specialization: Option[Backend.Specialization] = None,
    graphics: Option[GraphicsBackend] = None,
    maxPrintEvents: Int > 0 = 16384
) extends AutoCloseable {
  import ExecutionSession.*

  private val state = AtomicReference(
    State(Map.empty, Map.empty, Map.empty, AllocationId(1L), ProgramId(1L), CompletionId(1L))
  )

  private val stateLock            = ReentrantLock()
  private val stateChanged         = stateLock.newCondition()
  private val pendingCompletions   = mutable.Queue.empty[Completion]
  private val programLifecycleLock = ReentrantReadWriteLock()
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var executionWorkers = 0
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  @volatile private var closed = false

  private val host = HostCoherence(
    stateLock,
    stateChanged,
    id => state.get().completions.get(id),
    () => closed,
    (address, size) => state.get().addresses.find(address, size).exists((id, _) => hostBacked(id)),
    () => state.get().hasHostBackedAllocations
  )

  private def hostBacked(id: AllocationId): Boolean = state.get().allocations.get(id).exists(_.hostBacked)

  /** The current state of an open session; callers hold the state lock. */
  private def open: Either[ExecutionFailure, State] = if (closed) Left(Closed) else Right(state.get())

  def registerAllocation(size: Int > 0): Either[ExecutionFailure, AllocationId] = stateLock.withLock {
    open.map { current =>
      val id         = current.nextAllocation
      val allocation = StoredAllocation(StoredAllocation.Storage(size), syntheticAddress(id))
      state.set(current.withAllocations(current.allocations.updated(id, allocation)).copy(nextAllocation = id.next))
      id
    }
  }

  /** Register an immutable client-address alias; storage and lifetime remain owned by the allocation. */
  def registerAllocationAddress(
      id: AllocationId,
      address: GuestAddress,
      hostBacked: Boolean = false
  ): Either[ExecutionFailure, Unit] = stateLock.withLock {
    open.flatMap { current =>
      current.allocations.get(id).toRight(notRegistered(id)).flatMap { allocation =>
        val size = allocation.size.raw.toLong
        if (address.raw <= 0 || address.raw > Long.MaxValue - size)
          Left(ExecutionFailure.InvalidRequest("allocation address alias has an invalid or overflowing range"))
        else if (allocation.aliased && allocation.base == address && allocation.hostBacked == hostBacked) Right(())
        else if (allocation.aliased)
          Left(ExecutionFailure.InvalidRequest("allocation address alias cannot be remapped"))
        else if (
          current.allocations.values.exists(other =>
            other.aliased && address.raw < other.base.raw + other.size.raw && other.base.raw < address.raw + size
          )
        ) Left(ExecutionFailure.InvalidRequest("allocation address aliases overlap"))
        else {
          val aliased = allocation.copy(base = address, aliased = true, hostBacked = hostBacked)
          state.set(current.withAllocations(current.allocations.updated(id, aliased)))
          Right(())
        }
      }
    }
  }

  def releaseAllocation(id: AllocationId): Either[ExecutionFailure, Unit] = stateLock.withLock {
    val current = state.get()
    current.allocations.get(id) match {
      case None                                                                  => Left(notRegistered(id))
      case Some(_) if current.programs.values.exists(_.allocations.contains(id)) =>
        Left(ExecutionFailure.InvalidRequest(s"allocation $id is retained by a program global"))
      case Some(_) if current.completions.values.exists(_.allocations.contains(id)) =>
        Left(ExecutionFailure.InvalidRequest(s"allocation $id is retained by completion"))
      case Some(allocation) =>
        allocation.storage.lockAll()
        try {
          state.set(current.withAllocations(current.allocations.removed(id)))
          Right(())
        } finally allocation.storage.unlockAll()
    }
  }

  def load(encoding: ProgramEncoding, image: Vec[Byte]): Either[ExecutionFailure, ProgramId] = {
    // Reserve the identity under the state lock, but decode outside it: allocation
    // and dispatch requests must not wait behind loading a large code object.
    val reserved = stateLock.withLock {
      open.map { current =>
        state.set(current.copy(nextProgram = current.nextProgram.next))
        current.nextProgram
      }
    }
    for {
      id      <- reserved
      program <-
        programLifecycleLock.writeLock().withLock(backendFor(encoding).flatMap(_.load(image).left.map(failure(_))))
      _ <- program.bindFunctionPointerNamespace(id).left.map(failure(_)).flatMap { _ =>
        stateLock.withLock {
          open.map(current => state.set(current.copy(programs = current.programs.updated(id, ProgramEntry(program)))))
        }
      }.left.map { error =>
        programLifecycleLock.writeLock().withLock(program.close())
        error
      }
    } yield id
  }

  def describeImage(
      encoding: ProgramEncoding,
      image: Vec[Byte],
      name: NonEmpty[String]
  ): Either[ExecutionFailure, KernelMetadata] =
    backendFor(encoding).flatMap { backend =>
      backend.load(image).left.map(failure(_)).flatMap { program =>
        try program.kernel(name).left.map(failure(_))
        finally program.close()
      }
    }

  private def specializer: Either[ExecutionFailure, Backend.Specialization] =
    specialization.toRight(ExecutionFailure.InvalidRequest("image specialization is unavailable"))

  private def graphicsBackend: Either[ExecutionFailure, GraphicsBackend] =
    graphics.toRight(ExecutionFailure.Unsupported("graphics backend is unavailable"))

  def describeSpecialization(image: Vec[Byte]): Either[ExecutionFailure, Vec[SpecializationInfo]] =
    specializer.flatMap(_.describe(image).left.map(failure(_)))

  def prepareShader(
      image: Vec[Byte],
      entry: NonEmpty[String],
      stage: ShaderStageKind,
      constants: Vec[SpecializationConstant]
  ): Either[ExecutionFailure, PreparedShader] =
    graphicsBackend.flatMap(_.prepare(image, entry, stage, constants).left.map(failure(_)))

  def specializeImage(image: Vec[Byte], constants: Vec[SpecializationConstant]): Either[ExecutionFailure, Vec[Byte]] =
    specializer.flatMap(_.specialize(image, constants).left.map(failure(_)))

  def unload(id: ProgramId): Either[ExecutionFailure, Unit] = {
    // Reserve unloading before entering the backend. Keep bound allocations retained
    // until close finishes, without holding the session state lock during backend work.
    val reserved = stateLock.withLock {
      val current = state.get()
      current.loaded(id).flatMap { entry =>
        if (entry.isBinding) Left(ExecutionFailure.InvalidRequest(s"program $id has a global binding in progress"))
        else if (current.hasPendingCompletion(id))
          Left(ExecutionFailure.InvalidRequest(s"program $id is retained by pending execution"))
        else {
          entry.beginUnload()
          Right(entry.loaded)
        }
      }
    }
    reserved.map { program =>
      try programLifecycleLock.writeLock().withLock(program.close())
      finally {
        stateLock.withLock {
          val current = state.get()
          state.set(current.copy(programs = current.programs.removed(id)))
        }
        cleanupClosedSession()
      }
    }
  }

  def kernel(id: ProgramId, name: NonEmpty[String]): Either[ExecutionFailure, KernelMetadata] =
    describe(id)(_.kernel(name))

  def symbols(id: ProgramId): Either[ExecutionFailure, ProgramSymbols] = describe(id)(_.symbols)

  def global(id: ProgramId, name: NonEmpty[String]): Either[ExecutionFailure, ProgramGlobal] =
    describe(id)(_.global(name))

  // Unload and session cleanup make a program unavailable, then close it under the write lock.
  private def describe[A](id: ProgramId)(query: Backend.Program => Either[Fault, A]): Either[ExecutionFailure, A] =
    program(id).flatMap(_ =>
      programLifecycleLock.readLock().withLock(program(id).flatMap(query(_).left.map(failure(_))))
    )

  /** Bind outside the state lock while reserving program/allocation ownership across backend preparation. */
  def bindGlobal(
      id: ProgramId,
      name: NonEmpty[String],
      allocation: AllocationId,
      offset: Long >= 0
  ): Either[ExecutionFailure, Unit] = {
    val result = programLifecycleLock.writeLock().withLock {
      val reserved = stateLock.withLock {
        for {
          current <- open
          entry   <- current.loaded(id)
          _       <- Either.cond(
            !current.hasPendingCompletion(id),
            (),
            ExecutionFailure.InvalidRequest("cannot bind globals during pending execution")
          )
          stored <- current.allocations.get(allocation).toRight(notRegistered(allocation))
        } yield {
          entry.beginBinding(allocation)
          stored.size -> entry
        }
      }
      reserved.flatMap { (size, entry) =>
        val attempted =
          try
            program(id).flatMap { loaded =>
              loaded.global(name)
                .flatMap(image => GlobalBinding.validated(name, allocation, offset, image.size, size.toLong))
                .flatMap(loaded.bindGlobal(name, _, BindingMemory))
                .left.map(failure(_))
            }
          catch {
            case scala.util.control.NonFatal(error) =>
              Left(ExecutionFailure.Failed(s"program global binding failed: ${throwables.detail(error)}"))
          }
        stateLock.withLock {
          entry.finishBinding(attempted.isRight && !closed)
          open.flatMap(_ => attempted)
        }
      }
    }
    cleanupClosedSession()
    result
  }

  private object Storage extends CompletionMemory.Storage {
    override def lock: ReentrantLock                                    = stateLock
    override def allocation(id: AllocationId): Option[StoredAllocation] = state.get().allocations.get(id)
    override def allocations: Iterable[StoredAllocation]                = state.get().allocations.values
    override def find(address: GuestAddress, size: Int >= 0): Option[(AllocationId, Long >= 0)] =
      state.get().addresses.find(address, size)
    override def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
      ExecutionSession.this.read(id, offset, size).left.map(memoryFault(_))
    override def readValue(id: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8): Either[Fault, Long] =
      ExecutionSession.this.readValue(id, offset, size).left.map(memoryFault(_))
    override def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte], completion: CompletionId)
        : Either[Fault, Unit] =
      ExecutionSession.this.write(id, offset, bytes, Some(completion)).left.map(memoryFault(_))
    override def writeValue(
        id: AllocationId,
        offset: Long >= 0,
        value: Long,
        size: Int in 1 ~ 8,
        completion: CompletionId
    ): Either[Fault, Unit] =
      ExecutionSession.this.writeValue(id, offset, value, size, Some(completion)).left.map(memoryFault(_))
    override def atomicModifyValue(id: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8, completion: CompletionId)(
        update: Long => Long
    ): Either[Fault, Long] =
      ExecutionSession.this.atomicModifyValue(id, offset, size, Some(completion))(update).left.map(memoryFault(_))
  }

  private object BindingMemory extends AddressSpace {
    override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
      Storage.read(region, offset, size)
    override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
      ExecutionSession.this.write(region, offset, bytes).left.map(memoryFault(_))
    override def atomicModifyValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)(
        update: Long => Long
    ): Either[Fault, Long] =
      ExecutionSession.this.atomicModifyValue(region, offset, size)(update).left.map(memoryFault(_))
    override def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
      Storage.address(region, offset)
    override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] =
      Storage.locate(address, size)
  }

  def writeGlobal(
      id: ProgramId,
      name: NonEmpty[String],
      offset: Long >= 0,
      bytes: Vec[Byte]
  ): Either[ExecutionFailure, Unit] =
    stateLock.withLock(open.flatMap(_.loaded(id))).flatMap(_ =>
      programLifecycleLock.writeLock().withLock(program(id).flatMap(_.writeGlobal(
        name,
        offset,
        bytes
      ).left.map(failure(_))))
    )

  // Called after validation while holding stateLock; workers start after that lock is released.
  private def enqueue(current: State, program: Option[ProgramId], references: Set[AllocationId])(
      execute: Completion.Execute
  ): CompletionId = {
    val id         = current.nextCompletion
    val completion = Completion(id, program, execute, references, TaskControl(taskLimits), maxPrintEvents)
    state.set(current.copy(completions = current.completions.updated(id, completion), nextCompletion = id.next))
    pendingCompletions.enqueue(completion)
    id
  }

  def launch(
      program: ProgramId,
      task: KernelTask,
      descriptorBindings: Vec[DescriptorBinding] = Vec.empty
  ): Either[ExecutionFailure, CompletionId] = {
    val accepted = stateLock.withLock {
      for {
        current <- open
        entry   <- current.loaded(program)
        _ <- Either.cond(!entry.isBinding, (), ExecutionFailure.InvalidRequest("program global binding is in progress"))
        loaded = entry.loaded
        effective <-
          if (!task.discoverArgumentRelocations) Right(task)
          else
            loaded.kernel(task.kernelName).left.map(failure(_)).flatMap(metadata =>
              task.withPointerRelocations(TaskValidation.discover(task, metadata, current.addresses))
                .left.map(ExecutionFailure.InvalidRequest(_))
            )
        _ <- TaskValidation.validate(effective, current.allocations, descriptorBindings)
        _ <- effective.argumentBuffer.fold[Either[ExecutionFailure, Unit]](Right(())) { buffer =>
          loaded.kernel(effective.kernelName).left.map(failure(_)).flatMap(metadata =>
            Either.cond(
              metadata.argumentBlockSize.raw.toLong <=
                current.allocations(buffer.region).size.raw.toLong - buffer.offset.raw,
              (),
              ExecutionFailure.InvalidRequest("kernel argument block exceeds its allocation")
            )
          )
        }
        images <-
          if (descriptorBindings.isEmpty) Right(Set.empty[AllocationId])
          else graphics.toRight(ExecutionFailure.Unsupported("descriptor preparation backend is unavailable")).flatMap(
            _.validateDescriptorBindings(
              descriptorBindings,
              effective.arguments.nonNegativeSize,
              id => current.allocations.get(id).map(_.size)
            ).left.map(message => ExecutionFailure.InvalidRequest(s"invalid descriptor bindings: $message"))
          )
      } yield enqueue(current, Some(program), TaskValidation.referenced(effective) ++ entry.allocations ++ images) {
        (memory, control, handler) =>
          if (descriptorBindings.isEmpty) loaded.execute(effective, memory, control, handler).map(_ => None)
          else graphics.toRight(Fault.Unsupported("graphics backend is unavailable")).flatMap(
            _.withDescriptorBindings(descriptorBindings, memory, control) {
              (preparedMemory, descriptors, relocations) =>
                val replacements = descriptorBindings.zip(descriptors).map { (binding, range) =>
                  binding.argument.raw -> KernelTask.Argument.Memory(range.allocation, range.offset, Some(range.size))
                }.toMap
                effective.withArguments(Vec.from(effective.arguments.zipWithIndex.map { (argument, index) =>
                  replacements.getOrElse(index, argument)
                })).flatMap(_.withPointerRelocations(relocations)).left.map(Fault.InvalidRequest(_)).flatMap(prepared =>
                  loaded.execute(prepared, preparedMemory, control, handler).map(_ => None)
                )
            }
          )
      }
    }
    accepted.foreach(_ => startExecutionWorker())
    accepted
  }

  def submitGraphics(submission: GraphicsSubmission): Either[ExecutionFailure, CompletionId] = {
    val accepted = stateLock.withLock {
      for {
        current    <- open
        backend    <- graphicsBackend
        references <- backend.validate(submission, id => current.allocations.get(id).map(_.size))
          .left.map(message => ExecutionFailure.InvalidRequest(s"invalid graphics submission: $message"))
      } yield enqueue(current, None, references) { (memory, control, handler) =>
        backend.execute(submission, memory, control, handler).map(Some(_))
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

  private class ExecutionWorker extends WorkerPool.Task {
    override def run(): Unit                    = runPendingCompletions()
    override def reject(error: Throwable): Unit = failPendingCompletions(
      ExecutionFailure.Failed(s"execution worker failed: ${throwables.detail(error)}")
    )
  }

  private def startExecutionWorker(): Unit = {
    val startWorker = stateLock.withLock {
      if (closed || pendingCompletions.isEmpty || executionWorkers >= parallelism.raw) false
      else {
        executionWorkers += 1
        true
      }
    }
    if (startWorker)
      try executionContext.execute(ExecutionWorker())
      catch {
        case scala.util.control.NonFatal(error) =>
          failPendingCompletions(ExecutionFailure.Failed(s"cannot schedule execution: ${throwables.detail(error)}"))
      }
  }

  private def runPendingCompletions(): Unit = {
    def dequeueLocked(): Option[Completion] = {
      val value = pendingCompletions.dequeueFirst(_ => true)
      if (value.isEmpty) executionWorkers -= 1
      value
    }
    @tailrec def loop(next: Option[Completion]): Unit =
      next match {
        case None             => ()
        case Some(completion) =>
          val id     = completion.id
          val result =
            try
              programLifecycleLock.readLock().withLock {
                completion.execute(CompletionMemory(Storage, host, id), completion.control, handleEvent(id, _))
                  .left.map(failure(_))
              }
            catch {
              case scala.util.control.NonFatal(error) =>
                Left(ExecutionFailure.Failed(s"program executor failed: ${throwables.detail(error)}"))
              case error: Throwable =>
                stateLock.withLock {
                  state.get().completions.get(id).foreach(_.finish(
                    CompletionStatus.Failed(
                      ExecutionFailure.Failed(s"program executor failed: ${throwables.detail(error)}")
                    )
                  ))
                  stateChanged.signalAll()
                }
                throw error
            }
          val following = stateLock.withLock {
            state.get().completions.get(id).foreach { record =>
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
    try loop(stateLock.withLock(dequeueLocked()))
    catch {
      case error: Throwable =>
        try failPendingCompletions(ExecutionFailure.Failed(s"execution worker failed: ${throwables.detail(error)}"))
        catch { case cleanup: Throwable if cleanup ne error => error.addSuppressed(cleanup) }
        throw error
    }
    cleanupClosedSession()
  }

  private def failPendingCompletions(error: ExecutionFailure): Unit = {
    stateLock.withLock {
      pendingCompletions.foreach(_.finish(CompletionStatus.Failed(error)))
      pendingCompletions.clear()
      executionWorkers -= 1
      stateChanged.signalAll()
    }
    cleanupClosedSession()
  }

  override def close(): Unit = {
    stateLock.withLock {
      if (!closed) {
        closed = true
        state.get().completions.values.foreach(_.control.cancel())
        pendingCompletions.foreach(_.finish(
          CompletionStatus.Failed(ExecutionFailure.Failed("execution session closed before dispatch"))
        ))
        pendingCompletions.clear()
        stateChanged.signalAll()
      }
    }
    cleanupClosedSession()
  }

  private def cleanupClosedSession(): Unit = {
    val programs = stateLock.withLock {
      val current = state.get()
      if (
        !closed || executionWorkers != 0 ||
        current.programs.values.exists(entry => entry.isBinding || entry.isUnloading)
      )
        None
      else {
        state.set(current.withAllocations(Map.empty).copy(programs = Map.empty, completions = Map.empty))
        Some(current.programs.values.map(_.loaded).toVector)
      }
    }
    programs.foreach(loaded => programLifecycleLock.writeLock().withLock(loaded.foreach(_.close())))
  }

  def completion(id: CompletionId): Either[ExecutionFailure, CompletionStatus] =
    stateLock.withLock(completionRecord(id).map(_.status))

  private def completionRecord(id: CompletionId): Either[ExecutionFailure, Completion] =
    state.get().completions.get(id).toRight(ExecutionFailure.NotFound(s"completion $id does not exist"))

  private def succeededCompletion[A](id: CompletionId)(value: Completion => A): Either[ExecutionFailure, A] =
    stateLock.withLock {
      completionRecord(id).flatMap { record =>
        record.status match {
          case CompletionStatus.Pending => Left(ExecutionFailure.InvalidRequest(s"completion $id is still pending"))
          case CompletionStatus.Failed(error) => Left(error)
          case CompletionStatus.Succeeded     => Right(value(record))
        }
      }
    }

  def enableHostMemory(): Either[ExecutionFailure, Unit] = stateLock.withLock(open.map(_ => host.enable()))

  def pollHostMemory(id: CompletionId): Either[ExecutionFailure, HostMemoryBatch] = host.poll(id)

  def supplyHostMemory(id: CompletionId, ranges: Vec[HostMemoryBytes]): Either[ExecutionFailure, Unit] =
    host.supply(id, ranges)

  def supplyHostSync(id: CompletionId, ticket: Long, observed: Long): Either[ExecutionFailure, Unit] =
    host.supplySync(id, ticket, observed)

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
            case CompletionStatus.Pending => Left(ExecutionFailure.Failed("execution session closed before completion"))
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
    succeededCompletion(id)(_.dirtyRanges(offset, limit))

  def completionPointerRelocations(
      id: CompletionId,
      offset: Int >= 0,
      limit: Int > 0
  ): Either[ExecutionFailure, Vec[KernelTask.PointerRelocation]] =
    succeededCompletion(id)(_.pointerRelocations(offset, limit))

  def completionPrints(id: CompletionId): Either[ExecutionFailure, String] =
    succeededCompletion(id)(_.prints).flatMap { prints =>
      prints.foldLeft[Either[ExecutionFailure, StringBuilder]](Right(StringBuilder())) { (accumulated, event) =>
        accumulated.flatMap(builder =>
          PrintRenderer.render(event, maxOutputChars = 1024 * 1024).left.map(message =>
            ExecutionFailure.Failed(s"printf rendering failed: $message")
          )
            .map(builder.append)
        )
      }.map(_.result())
    }

  def releaseCompletion(id: CompletionId): Either[ExecutionFailure, Unit] = stateLock.withLock {
    completionRecord(id).flatMap { record =>
      if (record.isPending) Left(ExecutionFailure.InvalidRequest(s"completion $id is still pending"))
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
  )(operation: (StoredAllocation, Int >= 0) => A): Either[ExecutionFailure, A] =
    if (closed && completion.isEmpty) Left(Closed)
    else state.get().allocations.get(id).toRight(notRegistered(id)).flatMap { allocation =>
      ByteRange.start(allocation.size.raw, offset.raw, size.raw)
        .toRight(ExecutionFailure.InvalidRequest(
          s"allocation $id: byte range $offset + $size exceeds allocation size ${allocation.size}"
        ))
        .flatMap { start =>
          allocation.storage.access(start, size) {
            if (!state.get().allocations.contains(id)) Left(notRegistered(id))
            else Right(operation(allocation, start))
          }
        }
    }

  def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[ExecutionFailure, Vec[Byte]] =
    accessAllocation(id, offset, size) { (allocation, start) =>
      Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(allocation.bytes, start.raw, start.raw + size.raw))
    }

  def readValue(id: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8): Either[ExecutionFailure, Long] =
    accessAllocation(id, offset, size)((allocation, start) =>
      LittleEndian.decode(allocation.bytes, start.raw, size.widen[Int in 0 ~ 8])
    )

  def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[ExecutionFailure, Unit] =
    write(id, offset, bytes, None)

  def writeValue(id: AllocationId, offset: Long >= 0, value: Long, size: Int in 1 ~ 8): Either[ExecutionFailure, Unit] =
    writeValue(id, offset, value, size, None)

  def atomicModifyValue(id: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)(
      update: Long => Long
  ): Either[ExecutionFailure, Long] = atomicModifyValue(id, offset, size, None)(update)

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
      LittleEndian.write(value, allocation.bytes, start.raw, size.widen[Int in 0 ~ 8])
      markDirty(completion, id, start, size)
    }

  private def atomicModifyValue(
      id: AllocationId,
      offset: Long >= 0,
      size: Int in 1 ~ 8,
      completion: Option[CompletionId]
  )(update: Long => Long): Either[ExecutionFailure, Long] =
    accessAllocation(id, offset, size, completion) { (allocation, start) =>
      val width    = size.widen[Int in 0 ~ 8]
      val previous = LittleEndian.decode(allocation.bytes, start.raw, width)
      LittleEndian.write(update(previous), allocation.bytes, start.raw, width)
      markDirty(completion, id, start, size)
      previous
    }

  private def markDirty(completion: Option[CompletionId], id: AllocationId, offset: Int >= 0, size: Int >= 0): Unit =
    completion.foreach(state.get().completions(_).markDirty(id, offset, size))

  private def program(id: ProgramId): Either[ExecutionFailure, Backend.Program] =
    stateLock.withLock(state.get().loaded(id).map(_.loaded))

  private def backendFor(encoding: ProgramEncoding): Either[ExecutionFailure, Backend] =
    backends.find(_.encoding == encoding).toRight(ExecutionFailure.Unsupported(s"unknown program encoding: $encoding"))

  private def handleEvent(completion: CompletionId, event: Event): Either[Fault, Unit] = event match {
    case event: Event.Print =>
      stateLock.withLock {
        state.get().completions.get(completion)
          .toRight(ExecutionFailure.NotFound(s"completion $completion does not exist"))
          .flatMap(_.record(event))
      }.left.map(error => Fault.EventHandling(s"print capture failed: ${error.message}"))
    case Event.PointerRelocation(source, sourceOffset, target, targetOffset) =>
      stateLock.withLock {
        state.get().completions.get(completion).foreach(_.record(
          KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(source), sourceOffset, target, targetOffset)
        ))
      }
      Right(())
  }
}
