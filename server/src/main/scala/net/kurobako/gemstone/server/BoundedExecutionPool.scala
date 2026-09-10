package net.kurobako.gemstone.server

import net.kurobako.gemstone.execution.*
import scala.util.control.NonFatal

/** One fixed CPU pool. Jobs rotate after each workgroup, and nested dispatch callers help the same queue. */
@SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneRuntimeCollections.mutable-collection"))
private[server] final class BoundedExecutionPool private[server] (
    val parallelism: Int,
    minGroups: Long = 16L,
    minWorkItems: Long = 64L
) extends ExecutionParallelism with AutoCloseable {
  require(parallelism > 0)
  private final class Job(val count: Long, val task: Long => Either[ExecutionFailure, Unit]) {
    var next                              = 0L
    var active                            = 0
    var failure: Option[ExecutionFailure] = None
    @volatile var finished                = count == 0L
    val observed                          = if (trace) java.util.HashSet[String]() else null
  }
  private val trace            = sys.env.contains("GEMSTONE_PERF_TRACE")
  private val monitor          = Object()
  private val queue            = java.util.ArrayDeque[Job]()
  private val inWorker         = ThreadLocal.withInitial[Boolean](() => false)
  @volatile private var closed = false
  private val threads          = Array.tabulate(parallelism) { index =>
    val thread = Thread(() => workerLoop(), s"gemstone-worker-$index")
    thread.setDaemon(true)
    thread.start()
    thread
  }

  private def enqueue(count: Long, task: Long => Either[ExecutionFailure, Unit]): Job = monitor.synchronized {
    if (closed) throw java.util.concurrent.RejectedExecutionException("execution pool is closed")
    val job = Job(count, task)
    if (count > 0L) queue.addLast(job)
    monitor.notifyAll()
    job
  }

  private def take(until: Job | Null): Option[(Job, Long)] = monitor.synchronized {
    while (!closed && queue.isEmpty && (until == null || !until.finished)) monitor.wait()
    if (closed || (until != null && until.finished)) None
    else {
      val job   = queue.removeFirst()
      val index = job.next
      job.next += 1L
      job.active += 1
      if (job.observed != null) { val _ = job.observed.add(Thread.currentThread().getName) }
      if (job.next < job.count) queue.addLast(job)
      Some(job -> index)
    }
  }

  private def execute(work: (Job, Long)): Unit = {
    val (job, index) = work
    val result       = try job.task(index)
    catch {
      case NonFatal(error) => Left(ExecutionFailure.Failed(Option(error.getMessage).getOrElse(error.getClass.getName)))
    }
    monitor.synchronized {
      job.active -= 1
      result.left.toOption.foreach { error =>
        if (job.failure.isEmpty) job.failure = Some(error)
        val _ = queue.remove(job)
        job.next = job.count
      }
      if (job.active == 0 && job.next == job.count) job.finished = true
      monitor.notifyAll()
    }
  }

  private def workerLoop(): Unit = {
    inWorker.set(true)
    try while (!closed) take(null).foreach(execute)
    catch { case _: InterruptedException => Thread.currentThread().interrupt() }
    finally inWorker.remove()
  }

  private def await(job: Job): Either[ExecutionFailure, Unit] = {
    if (inWorker.get()) {
      while (!job.finished && !closed) take(job).foreach(execute)
    } else monitor.synchronized {
      while (!job.finished && !closed) monitor.wait()
    }
    if (!job.finished) Left(ExecutionFailure.Failed("execution pool closed before the job completed"))
    else job.failure.toLeft(())
  }

  def schedule(runnable: Runnable): Unit = {
    val _ = enqueue(1L, _ => { runnable.run(); Right(()) })
  }

  def call[A](action: () => A): A =
    if (inWorker.get()) action()
    else {
      val result = java.util.concurrent.atomic.AtomicReference(Option.empty[A])
      val job    = enqueue(1L, _ => { result.set(Some(action())); Right(()) })
      await(job).fold(error => throw IllegalStateException(error.message), _ => result.get().get)
    }

  override def shouldParallel(taskCount: Long, workItemsPerTask: Long): Boolean =
    parallelism > 1 && taskCount > 1L && (taskCount >= minGroups || workItemsPerTask >= minWorkItems)

  override def parallelFor(taskCount: Long)(task: Long => Either[ExecutionFailure, Unit])
      : Either[ExecutionFailure, Unit] = {
    require(taskCount >= 0L, s"parallel task count must be non-negative: $taskCount")
    val started = if (trace) System.nanoTime() else 0L
    val job     = enqueue(taskCount, task)
    val result  = await(job)
    if (job.observed != null)
      System.err.println(
        s"Gemstone parallel groups=$taskCount configured=$parallelism workers=${job.observed.size()} elapsed_ns=${System.nanoTime() -
            started}"
      )
    result
  }

  override def close(): Unit = {
    monitor.synchronized { closed = true; queue.clear(); monitor.notifyAll() }
    threads.foreach(_.interrupt())
  }
}

private[server] object BoundedExecutionPool {
  val EnvironmentName = "GEMSTONE_EXECUTION_PARALLELISM"
  private val Maximum = 64

  def fromEnvironment(environment: Map[String, String]): BoundedExecutionPool = {
    val available  = hostBound
    val configured = environment.get(EnvironmentName) match {
      // Leave a small amount of host capacity for RPC, launcher, and GC work;
      // the remaining logical CPUs are available to the bounded interpreter.
      case None        => defaultParallelism
      case Some(value) =>
        value.toIntOption match {
          case Some(parsed) if parsed > 0 => parsed
          case Some(_) => throw IllegalArgumentException(s"$EnvironmentName must be positive: $value")
          case None    => throw IllegalArgumentException(s"$EnvironmentName must be a positive integer: $value")
        }
    }
    if (configured > available)
      throw IllegalArgumentException(s"$EnvironmentName=$configured exceeds host bound $available")
    BoundedExecutionPool(configured, minGroups = configured.toLong * 4L, minWorkItems = 64L)
  }

  private def hostBound: Int = math.max(1, math.min(Maximum, Runtime.getRuntime.availableProcessors()))

  private def defaultParallelism: Int = math.max(1, hostBound - 2)
}
