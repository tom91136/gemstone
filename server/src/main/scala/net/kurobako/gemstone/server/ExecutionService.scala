package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.ByteRange
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
) extends ExecutionMemory,
      AutoCloseable {
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

  def load(format: ProgramFormat, image: Col[Byte]): Either[ExecutionFailure, ProgramId] = synchronized {
    val current = state.get()
    if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
    else {
      val id = ProgramId.unsafe(current.nextProgram)
      executor.load(id, format, image).map { _ =>
        state.set(
          current.copy(
            programs = current.programs + id,
            nextProgram = Math.addExact(current.nextProgram, 1L)
          )
        )
        id
      }
    }
  }

  def unload(id: ProgramId): Either[ExecutionFailure, Unit] = synchronized {
    val current = state.get()
    if (!current.programs.contains(id)) Left(ExecutionFailure.NotFound(s"program ${id.toLong} is not loaded"))
    else if (
      current.completions.valuesIterator.exists(record =>
        record.program == id && record.status == CompletionStatus.Pending
      )
    )
      Left(ExecutionFailure.InvalidRequest(s"program ${id.toLong} is retained by pending execution"))
    else
      executor.unload(id).map { _ =>
        state.set(current.copy(programs = current.programs - id))
      }
  }

  def launch(dispatch: KernelDispatch): Either[ExecutionFailure, CompletionId] = {
    val accepted = synchronized {
      val current = state.get()
      if (closed) Left(ExecutionFailure.Failed("execution session is closed"))
      else if (!current.programs.contains(dispatch.program))
        Left(ExecutionFailure.NotFound(s"program ${dispatch.program.toLong} is not loaded"))
      else
        validateArguments(dispatch.arguments, current).map { _ =>
          val id      = CompletionId.unsafe(current.nextCompletion)
          val control = ExecutionControl.create(executionLimits, CancellationToken.Source())
          state.set(
            current.copy(
              completions = current.completions.updated(
                id,
                CompletionRecord(
                  CompletionStatus.Pending,
                  dispatch.program,
                  dispatch.arguments.collect { case KernelArgument.Allocation(allocation, _) => allocation }.toSet,
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
        if (closed) cleanupClosedSession()
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
            if (closed) cleanupClosedSession()
            None
          case value => value
        }
      }
    }
  }

  private def failPendingExecutions(error: ExecutionFailure): Unit = synchronized {
    val ids = pendingExecutions.map(_._1).toSet
    pendingExecutions.clear()
    executionWorkerScheduled = false
    val current = state.get()
    state.set(current.copy(completions = current.completions.map { case (id, record) =>
      if (ids.contains(id)) id -> record.copy(status = CompletionStatus.Failed(error)) else id -> record
    }))
    if (closed) cleanupClosedSession()
  }

  override def close(): Unit = synchronized {
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
      if (!executionWorkerScheduled) cleanupClosedSession()
    }
  }

  private def cleanupClosedSession(): Unit = {
    val current = state.get()
    current.programs.foreach { id =>
      val _ = executor.unload(id)
    }
    state.set(current.copy(allocations = Map.empty, programs = Set.empty, completions = Map.empty))
    dirtyChunks.clear()
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

  override def read(id: AllocationId, offset: Long, size: Int): Either[ExecutionFailure, Col[Byte]] = synchronized {
    state.get().allocations.get(id).toRight(
      ExecutionFailure.NotFound(s"allocation ${id.toLong} is not registered")
    ).flatMap { allocation =>
      checkedRange(allocation.bytes.length, offset, size).map { start =>
        Col.unsafeFromArray(java.util.Arrays.copyOfRange(allocation.bytes, start, start + size))
      }
    }
  }

  override def write(id: AllocationId, offset: Long, bytes: Col[Byte]): Either[ExecutionFailure, Unit] =
    write(id, offset, bytes, null)

  private final class CompletionMemory(completion: CompletionId) extends ExecutionMemory {
    override def read(id: AllocationId, offset: Long, size: Int): Either[ExecutionFailure, Col[Byte]] =
      ExecutionService.this.read(id, offset, size)

    override def write(
        id: AllocationId,
        offset: Long,
        bytes: Col[Byte]
    ): Either[ExecutionFailure, Unit] = ExecutionService.this.write(id, offset, bytes, completion.toLong)
  }

  private def completionMemory(completion: CompletionId): ExecutionMemory = CompletionMemory(completion)

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
      checkedRange(allocation.bytes.length, offset, bytes.length).map { start =>
        val _ = bytes.copyToArray(allocation.bytes, start, bytes.length)
        if (completion != null && bytes.nonEmpty) {
          val allocations = dirtyChunks.getOrElseUpdate(completion.longValue(), mutable.HashMap.empty)
          val chunks      = allocations.getOrElseUpdate(id, mutable.LongMap.empty)
          val first       = offset / DirtyChunkSize
          val last        = (offset + bytes.length.toLong - 1L) / DirtyChunkSize
          var chunk       = first
          while (chunk <= last) {
            chunks.update(chunk, ())
            chunk += 1L
          }
        }
      }
    }
  }

  private def validateArguments(arguments: Col[KernelArgument], current: State): Either[ExecutionFailure, Unit] =
    arguments.collectFirst {
      case KernelArgument.Allocation(id, _) if !current.allocations.contains(id) =>
        ExecutionFailure.NotFound(s"kernel argument refers to unregistered allocation ${id.toLong}")
      case KernelArgument.Allocation(id, offset)
          if offset < 0L || offset > current.allocations(id).bytes.length.toLong =>
        ExecutionFailure.InvalidRequest(s"kernel argument offset $offset is outside allocation ${id.toLong}")
      case KernelArgument.Local(size) if size < 0L =>
        ExecutionFailure.InvalidRequest(s"local-memory argument size must be non-negative: $size")
    }.toLeft(())

  private def checkedRange(length: Int, offset: Long, size: Int): Either[ExecutionFailure, Int] =
    ByteRange
      .start(length, offset, size)
      .toRight(ExecutionFailure.InvalidRequest(s"byte range $offset + $size exceeds allocation size $length"))
}
