package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.{AllocationKind, GuestAddress, MemoryValueByteWidth}
import net.kurobako.gemstone.rpc.ExecutionRPC

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.ExecutionContext

/** Process-neutral program and memory receiver shared by every native API facade. */
private[server] final class ExecutionService(
    executor: ProgramExecutor,
    schedule: Runnable => Unit = runnable => ExecutionContext.global.execute(runnable),
    executionLimits: ExecutionLimits = ExecutionLimits.Default,
    parallelism: ExecutionParallelism = ExecutionParallelism.Sequential
) extends AutoCloseable {
  private val DirtyChunkSize = 64L * 1024L

  /** Mutable byte storage is deliberately confined behind this service's state lock. Allocation traffic is the
    * simulator's hot data path; rebuilding a multi-megabyte immutable collection for every mirrored chunk creates
    * unbounded GC latency. Neither the array nor a mutable view crosses the ExecutionMemory boundary.
    */
  private final case class RemoteAllocation(
      kind: AllocationKind,
      bytes: Array[Byte],
      address: Option[GuestAddress] = None
  ) {
    val size: Long :| Positive = bytes.length.toLong.refineUnsafe[Positive]
  }
  private final case class CompletionRecord(
      status: CompletionStatus,
      program: ProgramId,
      allocations: Set[AllocationId],
      pointerRelocations: Vec[PointerRelocation],
      prints: Vec[PrintEvent],
      allocationAddresses: Vec[AllocationAddress],
      control: ExecutionControl
  )

  private final class CompletionOutput(completion: CompletionId) extends ExecutionOutput {
    override def print(event: PrintEvent): Either[ExecutionFailure, Unit] = recordPrint(completion, event)
  }
  private final case class State(
      allocations: Map[AllocationId, RemoteAllocation],
      programs: Set[ProgramId],
      completions: Map[CompletionId, CompletionRecord],
      nextAllocation: AllocationId,
      nextProgram: ProgramId,
      nextCompletion: CompletionId
  )

  private val state = AtomicReference(
    State(Map.empty, Set.empty, Map.empty, AllocationId(1L), ProgramId(1L), CompletionId(1L))
  )
  private val stateLock    = ReentrantLock()
  private val stateChanged = stateLock.newCondition()
  private val dirtyChunks  =
    mutable.HashMap.empty[CompletionId, mutable.HashMap[AllocationId, mutable.LongMap[Unit]]]
  private val pendingExecutions    = mutable.Queue.empty[(CompletionId, KernelDispatch, ExecutionControl)]
  private val programLifecycleLock = ReentrantLock()
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
          val size = allocation.size
          if (address.value <= 0 || address.value > Long.MaxValue - size)
            Left(ExecutionFailure.InvalidRequest("allocation address alias has an invalid or overflowing range"))
          else if (allocation.address.contains(address)) Right(())
          else if (allocation.address.nonEmpty)
            Left(ExecutionFailure.InvalidRequest("allocation address alias cannot be remapped"))
          else if (
            current.allocations.valuesIterator.exists(other =>
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
    else if (boundAllocations.valuesIterator.exists(_.contains(id)))
      Left(ExecutionFailure.InvalidRequest(s"allocation ${id.value} is retained by a program global"))
    else if (current.completions.valuesIterator.exists(_.allocations.contains(id)))
      Left(ExecutionFailure.InvalidRequest(s"allocation ${id.value} is retained by completion"))
    else {
      state.set(current.copy(allocations = current.allocations.removed(id)))
      Right(())
    }
  }

  def load(format: ProgramFormat, image: Vec[Byte]): Either[ExecutionFailure, ProgramId] = {
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
        programLifecycleLock.withLock(executor.load(id, format, image)) match {
          case Left(error) => Left(error)
          case Right(_)    =>
            val accepted = stateLock.withLock {
              if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
              else {
                val current = state.get()
                state.set(current.copy(programs = current.programs + id))
                Right(())
              }
            }
            accepted match {
              case Right(_)    => Right(id)
              case Left(error) =>
                val _ = programLifecycleLock.withLock(executor.unload(id))
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
        current.completions.valuesIterator.exists(record =>
          record.program == id && record.status == CompletionStatus.Pending
        )
      )
        Left(ExecutionFailure.InvalidRequest(s"program ${id.value} is retained by pending execution"))
      else {
        state.set(current.copy(programs = current.programs - id))
        Right(())
      }
    }
    reserved match {
      case Left(error) => Left(error)
      case Right(_)    =>
        val result = programLifecycleLock.withLock(executor.unload(id))
        result match {
          case Right(_) => stateLock.withLock { val _ = boundAllocations.remove(id) }
          case Left(_)  =>
            stateLock.withLock {
              val current = state.get()
              state.set(current.copy(programs = current.programs + id))
            }
            // If close raced this failed unload, make the restored program visible
            // to close cleanup and retry it through the same serialized path.
            cleanupClosedSession()
        }
        result
    }
  }

  def kernel(id: ProgramId, name: KernelName): Either[ExecutionFailure, KernelMetadata] =
    executor.kernel(id, name)

  def catalog(id: ProgramId): Either[ExecutionFailure, ProgramCatalog] =
    executor.catalog(id)

  def global(id: ProgramId, name: GlobalName): Either[ExecutionFailure, ProgramGlobal] =
    executor.global(id, name)

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
        else if (
          current.completions.valuesIterator.exists(r => r.program == id && r.status == CompletionStatus.Pending)
        )
          Left(ExecutionFailure.InvalidRequest("cannot bind globals during pending execution"))
        else current.allocations.get(allocation) match {
          case None =>
            Left(ExecutionFailure.NotFound(s"allocation ${allocation.value} is not registered"))
          case Some(value) =>
            val allocationSize = value.size
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
          val attempted = try executor.bindGlobal(id, name, allocation, offset, size, BindingMemory)
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

  private object BindingMemory extends ExecutionMemory {
    override def allocationAddresses: Vec[AllocationAddress] = stateLock.withLock {
      Vec.from(state.get().allocations.iterator.collect {
        case (id, allocation) if allocation.bytes.nonEmpty =>
          AllocationAddress(id, allocation.size, allocation.address)
      })
    }

    override def read(id: AllocationId, offset: Long :| Positive0, size: Int :| Positive0) =
      ExecutionService.this.read(id, offset, size)
    override def write(id: AllocationId, offset: Long :| Positive0, bytes: Vec[Byte]) =
      ExecutionService.this.write(id, offset, bytes)
    override def atomicModifyValue(id: AllocationId, offset: Long :| Positive0, size: MemoryValueByteWidth)(
        update: Long => Long
    ): Either[ExecutionFailure, Long] =
      ExecutionService.this.atomicModifyValue(id, offset, size)(update)
    override def recordPointerRelocation(relocation: PointerRelocation): Unit = ()
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
      case Right(_)    => programLifecycleLock.withLock(executor.writeGlobal(id, name, offset, bytes))
    }
  }

  def launch(dispatch: KernelDispatch): Either[ExecutionFailure, CompletionId] = {
    val accepted = stateLock.withLock {
      val current = state.get()
      if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
      else if (!current.programs.contains(dispatch.program))
        Left(ExecutionFailure.NotFound(s"program ${dispatch.program.value} is not loaded"))
      else if (bindingPrograms.contains(dispatch.program))
        Left(ExecutionFailure.InvalidRequest("program global binding is in progress"))
      else validateDispatch(dispatch, current) match {
        case Left(error) => Left(error)
        case Right(_)    =>
          val addresses = if (current.allocations.valuesIterator.exists(_.address.nonEmpty))
            Vec.from(current.allocations.iterator.collect {
              case (id, allocation) if allocation.bytes.nonEmpty =>
                AllocationAddress(id, allocation.size, allocation.address)
            })
          else Vec.empty[AllocationAddress]
          val id      = current.nextCompletion
          val control = ExecutionControl(
            executionLimits,
            CancellationToken.Source(),
            parallelism,
            CompletionOutput(id)
          )
          state.set(
            current.copy(
              completions = current.completions.updated(
                id,
                CompletionRecord(
                  CompletionStatus.Pending,
                  dispatch.program,
                  addresses.iterator.filter(_.address.nonEmpty).map(_.allocation).toSet ++
                    boundAllocations.getOrElse(dispatch.program, Set.empty) ++
                    dispatch.arguments.collect { case KernelArgument.Allocation(allocation, _) => allocation }.toSet ++
                    dispatch.pointerRelocations.foldLeft(Set.empty[AllocationId]) { (allocations, relocation) =>
                      relocation.source match {
                        case RelocationSource.Allocation(source) => allocations + relocation.target + source
                        case RelocationSource.Argument(_)        => allocations + relocation.target
                      }
                    },
                  Vec.empty,
                  Vec.empty,
                  addresses,
                  control
                )
              ),
              nextCompletion = current.nextCompletion.next
            )
          )
          dirtyChunks.update(id, mutable.HashMap.empty)
          Right(id -> control)
      }
    }
    accepted.foreach { case (id, control) =>
      val startWorker = stateLock.withLock {
        pendingExecutions.enqueue((id, dispatch, control))
        if (executionWorkerScheduled) false
        else {
          executionWorkerScheduled = true
          true
        }
      }
      if (startWorker)
        try schedule(() => runPendingExecutions())
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
    def dequeueLocked(): Option[(CompletionId, KernelDispatch, ExecutionControl)] = {
      val value = pendingExecutions.dequeueFirst(_ => true)
      if (value.isEmpty) executionWorkerScheduled = false
      value
    }
    @tailrec def loop(next: Option[(CompletionId, KernelDispatch, ExecutionControl)]): Unit = next match {
      case None                          => ()
      case Some((id, dispatch, control)) =>
        val status =
          try
            programLifecycleLock.withLock {
              executor.launch(dispatch, completionMemory(id), control)
            }
              .fold(error => CompletionStatus.Failed(error), _ => CompletionStatus.Succeeded)
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
        state.get().completions.valuesIterator.foreach(_.control.cancel())
        val cancelled = pendingExecutions.map(_._1).toSet
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
      if (!closed || executionWorkerScheduled || bindingPrograms.nonEmpty) Set.empty[ProgramId]
      else {
        val current = state.get()
        state.set(current.copy(allocations = Map.empty, programs = Set.empty, completions = Map.empty))
        dirtyChunks.clear()
        boundAllocations.clear()
        current.programs
      }
    }
    if (programs.nonEmpty)
      programLifecycleLock.withLock {
        programs.foreach { id =>
          val _ = executor.unload(id)
        }
      }
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
        val ranges = dirtyChunks.get(id).iterator
          .flatMap(_.iterator)
          .toArray
          .sortBy(_._1.value)
          .iterator
          .flatMap { case (allocationId, chunks) =>
            val allocationSize = current.allocations(allocationId).size
            chunks.keysIterator.toArray.sorted.iterator.map { chunk =>
              val offset = chunk * DirtyChunkSize
              DirtyRange(
                allocationId,
                offset.refineUnsafe[Positive0],
                math.min(DirtyChunkSize, allocationSize - offset).toInt.refineUnsafe[Positive]
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

  private final class CompletionMemory(completion: CompletionId) extends ExecutionMemory {
    override def allocationAddresses: Vec[AllocationAddress] =
      state.get().completions.get(completion).fold(Vec.empty[AllocationAddress])(_.allocationAddresses)

    override def read(
        id: AllocationId,
        offset: Long :| Positive0,
        size: Int :| Positive0
    ): Either[ExecutionFailure, Vec[Byte]] =
      ExecutionService.this.read(id, offset, size)

    override def readValue(
        id: AllocationId,
        offset: Long :| Positive0,
        size: MemoryValueByteWidth
    ): Either[ExecutionFailure, Long] =
      ExecutionService.this.readValue(id, offset, size)

    override def write(
        id: AllocationId,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] = ExecutionService.this.write(id, offset, bytes, Some(completion))

    override def writeValue(
        id: AllocationId,
        offset: Long :| Positive0,
        value: Long,
        size: MemoryValueByteWidth
    ): Either[ExecutionFailure, Unit] = ExecutionService.this.writeValue(id, offset, value, size, Some(completion))

    override def atomicModifyValue(id: AllocationId, offset: Long :| Positive0, size: MemoryValueByteWidth)(
        update: Long => Long
    ): Either[ExecutionFailure, Long] =
      ExecutionService.this.atomicModifyValue(id, offset, size, Some(completion))(update)

    override def recordPointerRelocation(relocation: PointerRelocation): Unit =
      ExecutionService.this.recordPointerRelocation(completion, relocation)
  }

  private def completionMemory(completion: CompletionId): ExecutionMemory = CompletionMemory(completion)

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

  private def validateDispatch(dispatch: KernelDispatch, current: State): Either[ExecutionFailure, Unit] =
    dispatch.arguments.collectFirst {
      case KernelArgument.Allocation(id, _) if !current.allocations.contains(id) =>
        ExecutionFailure.NotFound(s"kernel argument refers to unregistered allocation ${id.value}")
      case KernelArgument.Allocation(id, offset) if offset > current.allocations(id).bytes.length.toLong =>
        ExecutionFailure.InvalidRequest(
          s"kernel argument offset $offset is outside allocation ${id.value}"
        )
    }.toLeft(()).flatMap { _ =>
      dispatch.pointerRelocations.collectFirst {
        case PointerRelocation(RelocationSource.Allocation(source), sourceOffset, target, targetOffset)
            if !current.allocations.contains(source) =>
          ExecutionFailure.NotFound(
            s"pointer relocation refers to unregistered source allocation ${source.value}"
          )
        case PointerRelocation(_, _, target, _)
            if !current.allocations.contains(target) =>
          ExecutionFailure.NotFound(
            s"pointer relocation refers to unregistered target allocation ${target.value}"
          )
        case PointerRelocation(RelocationSource.Allocation(source), sourceOffset, _, _)
            if sourceOffset > current.allocations(source).bytes.length.toLong - 8L =>
          ExecutionFailure.InvalidRequest(
            s"pointer relocation source offset $sourceOffset exceeds allocation ${source.value}"
          )
        case PointerRelocation(_, _, target, targetOffset)
            if targetOffset > current.allocations(target).bytes.length.toLong =>
          ExecutionFailure.InvalidRequest(
            s"pointer relocation target offset $targetOffset exceeds allocation ${target.value}"
          )
        case PointerRelocation(RelocationSource.Argument(index), sourceOffset, _, _)
            if sourceOffset >
              (dispatch.arguments(index) match {
                case KernelArgument.Scalar(bytes) => bytes.length.toLong - 8L
                case _                            => -1L
              }) =>
          ExecutionFailure.InvalidRequest(
            s"pointer relocation source offset $sourceOffset exceeds argument $index"
          )
      }.toLeft(())
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
