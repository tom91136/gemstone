package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.AllocationKind

import java.util.concurrent.atomic.AtomicReference
import scala.collection.mutable // scalafix:ok GemstoneRuntimeCollections.mutable-collection; dirty-page index
import scala.concurrent.ExecutionContext

/** Process-neutral program and memory receiver shared by CUDA, HSA, and Level Zero frontends. */
@SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneRuntimeCollections.mutable-collection"))
final class ExecutionService(
    executor: ProgramExecutor,
    schedule: Runnable => Unit = runnable => ExecutionContext.global.execute(runnable),
    executionLimits: ExecutionLimits = ExecutionLimits.Default
) extends AutoCloseable {
  private val DirtyChunkSize = 64 * 1024L

  /** Mutable byte storage is deliberately confined behind this service's monitor. Allocation traffic is the simulator's
    * hot data path; rebuilding a multi-megabyte immutable collection for every mirrored chunk creates unbounded GC
    * latency. Neither the array nor a mutable view crosses the ExecutionMemory boundary.
    */
  private final case class RemoteAllocation(kind: AllocationKind, bytes: Array[Byte])
  private final case class CompletionRecord(
      status: CompletionStatus,
      program: ProgramId,
      allocations: Set[AllocationId],
      pointerRelocations: Col[PointerRelocation],
      control: ExecutionControl
  )
  private final case class State(
      allocations: Map[AllocationId, RemoteAllocation],
      programs: Set[ProgramId],
      completions: Map[CompletionId, CompletionRecord],
      nextProgram: Long,
      nextCompletion: Long
  )

  private val state       = AtomicReference(State(Map.empty, Set.empty, Map.empty, 1L, 1L))
  private val dirtyChunks =
    mutable.HashMap.empty[Long, mutable.HashMap[AllocationId, mutable.LongMap[Unit]]]
  private val pendingExecutions        = mutable.Queue.empty[(CompletionId, KernelDispatch, ExecutionControl)]
  private val programLifecycleLock     = Object()
  private var executionWorkerScheduled = false
  private var closed                   = false

  def registerAllocation(
      id: AllocationId,
      size: Long,
      kind: AllocationKind
  ): Either[ExecutionFailure, Unit] = synchronized {
    val current = state.get()
    if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
    else if (size < 0L || size > Int.MaxValue.toLong)
      Left(ExecutionFailure.InvalidRequest(s"allocation size is outside the supported range: $size"))
    else if (current.allocations.contains(id))
      Left(ExecutionFailure.InvalidRequest(s"allocation ${id.toLong} is already registered"))
    else {
      val allocation = RemoteAllocation(kind, Array.ofDim[Byte](size.toInt))
      state.set(current.copy(allocations = current.allocations.updated(id, allocation)))
      Right(())
    }
  }

  def releaseAllocation(id: AllocationId): Either[ExecutionFailure, Unit] = synchronized {
    val current = state.get()
    if (!current.allocations.contains(id))
      Left(ExecutionFailure.NotFound(s"allocation ${id.toLong} is not registered"))
    else if (current.completions.valuesIterator.exists(_.allocations.contains(id)))
      Left(ExecutionFailure.InvalidRequest(s"allocation ${id.toLong} is retained by completion"))
    else {
      state.set(current.copy(allocations = current.allocations.removed(id)))
      Right(())
    }
  }

  def load(format: ProgramFormat, image: Col[Byte]): Either[ExecutionFailure, ProgramId] = {
    // Reserve the identity under the service monitor, but do not hold that
    // monitor while decoding/preparing a potentially large code object. HSA
    // memory and AQL connections share this service; keeping the old monitor
    // around the executor call made hipMalloc wait behind code-object loading.
    val reserved = synchronized {
      val current = state.get()
      if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
      else {
        val id = ProgramId.unsafe(current.nextProgram)
        state.set(current.copy(nextProgram = Math.addExact(current.nextProgram, 1L)))
        Right(id)
      }
    }
    reserved.flatMap { id =>
      val loaded = programLifecycleLock.synchronized(executor.load(id, format, image))
      loaded.flatMap { _ =>
        val accepted = synchronized {
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
            val _ = programLifecycleLock.synchronized(executor.unload(id))
            Left(error)
        }
      }
    }
  }

  def unload(id: ProgramId): Either[ExecutionFailure, Unit] = {
    // Remove the program from the service state before entering the backend so
    // no new dispatch can race the unload. Backend lifecycle calls use their
    // own lock and never hold the service monitor while doing potentially slow
    // work.
    val reserved = synchronized {
      val current = state.get()
      if (!current.programs.contains(id)) Left(ExecutionFailure.NotFound(s"program ${id.toLong} is not loaded"))
      else if (
        current.completions.valuesIterator.exists(record =>
          record.program == id && record.status == CompletionStatus.Pending
        )
      )
        Left(ExecutionFailure.InvalidRequest(s"program ${id.toLong} is retained by pending execution"))
      else {
        state.set(current.copy(programs = current.programs - id))
        Right(())
      }
    }
    reserved.flatMap { _ =>
      val result = programLifecycleLock.synchronized(executor.unload(id))
      if (result.isLeft) {
        synchronized {
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

  def launch(dispatch: KernelDispatch): Either[ExecutionFailure, CompletionId] = {
    val accepted = synchronized {
      val current = state.get()
      if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
      else if (!current.programs.contains(dispatch.program))
        Left(ExecutionFailure.NotFound(s"program ${dispatch.program.toLong} is not loaded"))
      else
        validateDispatch(dispatch, current).map { _ =>
          val id      = CompletionId.unsafe(current.nextCompletion)
          val control = ExecutionControl.create(executionLimits, CancellationToken.Source())
          state.set(
            current.copy(
              completions = current.completions.updated(
                id,
                CompletionRecord(
                  CompletionStatus.Pending,
                  dispatch.program,
                  dispatch.arguments.collect { case KernelArgument.Allocation(allocation, _) => allocation }.toSet ++
                    dispatch.pointerRelocations.foldLeft(Set.empty[AllocationId]) { (allocations, relocation) =>
                      relocation.sourceAllocation.fold(allocations + relocation.target)(source =>
                        allocations + relocation.target + source
                      )
                    },
                  Col.empty,
                  control
                )
              ),
              nextCompletion = Math.addExact(current.nextCompletion, 1L)
            )
          )
          dirtyChunks.update(id.toLong, mutable.HashMap.empty)
          id -> control
        }
    }
    accepted.foreach { case (id, control) =>
      val startWorker = synchronized {
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
          case error: Throwable => failPendingExecutions(
              ExecutionFailure.Failed(
                s"cannot schedule execution: ${Option(error.getMessage).getOrElse(error.getClass.getName)}"
              )
            )
        }
    }
    accepted.map(_._1)
  }

  private def runPendingExecutions(): Unit = {
    var next = synchronized {
      val value = pendingExecutions.dequeueFirst(_ => true)
      if (value.isEmpty) {
        executionWorkerScheduled = false
      }
      value
    }
    while (next.nonEmpty) {
      val (id, dispatch, control) = next.get
      val status                  =
        try
          executor
            .launch(dispatch, completionMemory(id), control)
            .fold(CompletionStatus.Failed.apply, _ => CompletionStatus.Succeeded)
        catch {
          case error: Throwable =>
            CompletionStatus.Failed(
              ExecutionFailure.Failed(
                s"program executor failed: ${Option(error.getMessage).getOrElse(error.getClass.getName)}"
              )
            )
        }
      next = synchronized {
        val current = state.get()
        current.completions.get(id).foreach { record =>
          state.set(current.copy(completions = current.completions.updated(id, record.copy(status = status))))
        }
        pendingExecutions.dequeueFirst(_ => true) match {
          case None =>
            executionWorkerScheduled = false
            None
          case value => value
        }
      }
    }
    cleanupClosedSession()
  }

  private def failPendingExecutions(error: ExecutionFailure): Unit = {
    synchronized {
      val ids = pendingExecutions.map(_._1).toSet
      pendingExecutions.clear()
      executionWorkerScheduled = false
      val current = state.get()
      state.set(current.copy(completions = current.completions.map { case (id, record) =>
        if (ids.contains(id)) id -> record.copy(status = CompletionStatus.Failed(error)) else id -> record
      }))
    }
    cleanupClosedSession()
  }

  override def close(): Unit = {
    synchronized {
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
      }
    }
    cleanupClosedSession()
  }

  private def cleanupClosedSession(): Unit = {
    val programs = synchronized {
      if (!closed || executionWorkerScheduled) Set.empty[ProgramId]
      else {
        val current = state.get()
        state.set(current.copy(allocations = Map.empty, programs = Set.empty, completions = Map.empty))
        dirtyChunks.clear()
        current.programs
      }
    }
    if (programs.nonEmpty)
      programLifecycleLock.synchronized {
        programs.foreach { id =>
          val _ = executor.unload(id)
        }
      }
  }

  def completion(id: CompletionId): Either[ExecutionFailure, CompletionStatus] =
    state.get().completions.get(id).map(_.status)
      .toRight(ExecutionFailure.NotFound(s"completion ${id.toLong} does not exist"))

  def completionDirtyRanges(id: CompletionId): Either[ExecutionFailure, Col[DirtyRange]] = synchronized {
    val current = state.get()
    current.completions.get(id).map(_.status).toRight(
      ExecutionFailure.NotFound(s"completion ${id.toLong} does not exist")
    ).flatMap {
      case CompletionStatus.Pending =>
        Left(ExecutionFailure.InvalidRequest(s"completion ${id.toLong} is still pending"))
      case CompletionStatus.Failed(error) => Left(error)
      case CompletionStatus.Succeeded     =>
        val result = Col.newBuilder[DirtyRange]
        dirtyChunks.get(id.toLong).iterator.flatMap(_.iterator).toArray.sortBy(_._1.toLong).foreach {
          case (allocationId, chunks) =>
            val allocationSize = current.allocations(allocationId).bytes.length.toLong
            chunks.keysIterator.toArray.sorted.foreach { chunk =>
              val offset = chunk * DirtyChunkSize
              result += DirtyRange(
                allocationId,
                offset,
                math.min(DirtyChunkSize, allocationSize - offset).toInt
              )
            }
        }
        Right(Col.from(result.result()))
    }
  }

  def completionPointerRelocations(id: CompletionId): Either[ExecutionFailure, Col[PointerRelocation]] = synchronized {
    val current = state.get()
    current.completions.get(id).map(record => record.status -> record.pointerRelocations).toRight(
      ExecutionFailure.NotFound(s"completion ${id.toLong} does not exist")
    ).flatMap {
      case (CompletionStatus.Pending, _) =>
        Left(ExecutionFailure.InvalidRequest(s"completion ${id.toLong} is still pending"))
      case (CompletionStatus.Failed(error), _) => Left(error)
      case (CompletionStatus.Succeeded, value) => Right(value)
    }
  }

  def releaseCompletion(id: CompletionId): Either[ExecutionFailure, Unit] = synchronized {
    val current = state.get()
    current.completions.get(id) match {
      case None => Left(ExecutionFailure.NotFound(s"completion ${id.toLong} does not exist"))
      case Some(record) if record.status == CompletionStatus.Pending =>
        Left(ExecutionFailure.InvalidRequest(s"completion ${id.toLong} is still pending"))
      case Some(_) =>
        state.set(current.copy(completions = current.completions.removed(id)))
        val _ = dirtyChunks.remove(id.toLong)
        Right(())
    }
  }

  def read(id: AllocationId, offset: Long, size: Int): Either[ExecutionFailure, Col[Byte]] = synchronized {
    state.get().allocations.get(id).toRight(
      ExecutionFailure.NotFound(s"allocation ${id.toLong} is not registered")
    ).flatMap { allocation =>
      checkedRange(allocation.bytes.length, offset, size).left.map(error =>
        ExecutionFailure.InvalidRequest(s"allocation ${id.toLong}: $error")
      ).map { start =>
        Col.unsafeFromArray(java.util.Arrays.copyOfRange(allocation.bytes, start, start + size))
      }
    }
  }

  def readValue(id: AllocationId, offset: Long, size: Int): Either[ExecutionFailure, Long] = synchronized {
    state.get().allocations.get(id).toRight(
      ExecutionFailure.NotFound(s"allocation ${id.toLong} is not registered")
    ).flatMap { allocation =>
      checkedRange(allocation.bytes.length, offset, size).left.map(error =>
        ExecutionFailure.InvalidRequest(s"allocation ${id.toLong}: $error")
      ).map(start => LittleEndian.decode(allocation.bytes, start, size))
    }
  }

  def write(id: AllocationId, offset: Long, bytes: Col[Byte]): Either[ExecutionFailure, Unit] =
    write(id, offset, bytes, null)

  def writeValue(
      id: AllocationId,
      offset: Long,
      value: Long,
      size: Int
  ): Either[ExecutionFailure, Unit] = writeValue(id, offset, value, size, null)

  private final class CompletionMemory(completion: CompletionId) extends ExecutionMemory {
    override def read(id: AllocationId, offset: Long, size: Int): Either[ExecutionFailure, Col[Byte]] =
      ExecutionService.this.read(id, offset, size)

    override def readValue(id: AllocationId, offset: Long, size: Int): Either[ExecutionFailure, Long] =
      ExecutionService.this.readValue(id, offset, size)

    override def write(
        id: AllocationId,
        offset: Long,
        bytes: Col[Byte]
    ): Either[ExecutionFailure, Unit] = ExecutionService.this.write(id, offset, bytes, completion.toLong)

    override def writeValue(
        id: AllocationId,
        offset: Long,
        value: Long,
        size: Int
    ): Either[ExecutionFailure, Unit] = ExecutionService.this.writeValue(id, offset, value, size, completion.toLong)

    override def recordPointerRelocation(relocation: PointerRelocation): Unit =
      ExecutionService.this.recordPointerRelocation(completion, relocation)
  }

  private def completionMemory(completion: CompletionId): ExecutionMemory = CompletionMemory(completion)

  private def recordPointerRelocation(completion: CompletionId, relocation: PointerRelocation): Unit = synchronized {
    val current = state.get()
    current.completions.get(completion).foreach { record =>
      state.set(
        current.copy(
          completions = current.completions.updated(
            completion,
            record.copy(
              pointerRelocations = record.pointerRelocations
                .filterNot(existing =>
                  existing.sourceAllocation == relocation.sourceAllocation &&
                    existing.sourceArgument == relocation.sourceArgument &&
                    existing.sourceOffset == relocation.sourceOffset
                ) :+ relocation
            )
          )
        )
      )
    }
  }

  private def write(
      id: AllocationId,
      offset: Long,
      bytes: Col[Byte],
      completion: java.lang.Long | Null
  ): Either[ExecutionFailure, Unit] = synchronized {
    if (closed && completion == null) Left(ExecutionFailure.Failed("execution session is closed"))
    else state.get().allocations.get(id).toRight(
      ExecutionFailure.NotFound(s"allocation ${id.toLong} is not registered")
    ).flatMap { allocation =>
      checkedRange(allocation.bytes.length, offset, bytes.length).left.map(error =>
        ExecutionFailure.InvalidRequest(s"allocation ${id.toLong}: $error")
      ).map { start =>
        val _ = bytes.copyToArray(allocation.bytes, start, bytes.length)
        markDirty(completion, id, offset, bytes.length)
      }
    }
  }

  private def writeValue(
      id: AllocationId,
      offset: Long,
      value: Long,
      size: Int,
      completion: java.lang.Long | Null
  ): Either[ExecutionFailure, Unit] = synchronized {
    if (closed && completion == null) Left(ExecutionFailure.Failed("execution session is closed"))
    else state.get().allocations.get(id).toRight(
      ExecutionFailure.NotFound(s"allocation ${id.toLong} is not registered")
    ).flatMap { allocation =>
      checkedRange(allocation.bytes.length, offset, size).left.map(error =>
        ExecutionFailure.InvalidRequest(s"allocation ${id.toLong}: $error")
      ).map { start =>
        LittleEndian.write(value, allocation.bytes, start, size)
        markDirty(completion, id, offset, size)
      }
    }
  }

  private def markDirty(
      completion: java.lang.Long | Null,
      id: AllocationId,
      offset: Long,
      size: Int
  ): Unit =
    if (completion != null && size > 0) {
      val allocations = dirtyChunks.getOrElseUpdate(completion.longValue(), mutable.HashMap.empty)
      val chunks      = allocations.getOrElseUpdate(id, mutable.LongMap.empty)
      val first       = offset / DirtyChunkSize
      val last        = (offset + size.toLong - 1L) / DirtyChunkSize
      var chunk       = first
      while (chunk <= last) {
        chunks.update(chunk, ())
        chunk += 1L
      }
    }

  private def validateDispatch(dispatch: KernelDispatch, current: State): Either[ExecutionFailure, Unit] =
    dispatch.arguments.collectFirst {
      case KernelArgument.Allocation(id, _) if !current.allocations.contains(id) =>
        ExecutionFailure.NotFound(s"kernel argument refers to unregistered allocation ${id.toLong}")
      case KernelArgument.Allocation(id, offset)
          if offset < 0L || offset > current.allocations(id).bytes.length.toLong =>
        ExecutionFailure.InvalidRequest(s"kernel argument offset $offset is outside allocation ${id.toLong}")
      case KernelArgument.Local(size) if size < 0L =>
        ExecutionFailure.InvalidRequest(s"local-memory argument size must be non-negative: $size")
    }.toLeft(()).flatMap { _ =>
      dispatch.pointerRelocations.collectFirst {
        case PointerRelocation(Some(source), None, sourceOffset, target, targetOffset)
            if !current.allocations.contains(source) =>
          ExecutionFailure.NotFound(s"pointer relocation refers to unregistered source allocation ${source.toLong}")
        case PointerRelocation(_, _, _, target, _)
            if !current.allocations.contains(target) =>
          ExecutionFailure.NotFound(s"pointer relocation refers to unregistered target allocation ${target.toLong}")
        case PointerRelocation(Some(source), None, sourceOffset, _, _)
            if sourceOffset > current.allocations(source).bytes.length.toLong - 8L =>
          ExecutionFailure.InvalidRequest(
            s"pointer relocation source offset $sourceOffset exceeds allocation ${source.toLong}"
          )
        case PointerRelocation(_, _, _, target, targetOffset)
            if targetOffset > current.allocations(target).bytes.length.toLong =>
          ExecutionFailure.InvalidRequest(
            s"pointer relocation target offset $targetOffset exceeds allocation ${target.toLong}"
          )
        case PointerRelocation(None, Some(index), sourceOffset, _, _)
            if sourceOffset >
              (dispatch.arguments(index) match {
                case KernelArgument.Scalar(bytes) => bytes.length.toLong - 8L
                case _                            => -1L
              }) =>
          ExecutionFailure.InvalidRequest(s"pointer relocation source offset $sourceOffset exceeds argument $index")
      }.toLeft(())
    }

  private def checkedRange(length: Int, offset: Long, size: Int): Either[ExecutionFailure, Int] =
    ByteRange
      .start(length, offset, size)
      .toRight(ExecutionFailure.InvalidRequest(s"byte range $offset + $size exceeds allocation size $length"))
}
