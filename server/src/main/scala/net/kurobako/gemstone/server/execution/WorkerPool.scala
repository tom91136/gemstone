package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.launcher.EnvironmentNames
import net.kurobako.gemstone.server.Throwables
import net.kurobako.gemstone.PlatformTime
import net.kurobako.gemstone.vm.{Fault, Scheduler}

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.locks.ReentrantLock
import scala.collection.mutable
import scala.util.control.NonFatal

/** One fixed CPU pool. Jobs rotate after each chunk, and nested dispatch callers help the same queue. A launch only
  * stays on the calling thread when it is too small to amortise queue traffic; claiming a range per dequeue keeps that
  * threshold low.
  */
final class WorkerPool(
    val parallelism: Int :| Positive,
    minGroups: Long = 16L,
    minWorkItems: Long = 64L,
    trace: Boolean = EnvironmentNames.traceEnabled(sys.env, "perf")
) extends Scheduler with AutoCloseable {
  private final class Job(
      val count: Long :| Positive0,
      val grain: Int,
      val task: Long :| Positive0 => Either[Fault, Unit]
  ) {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var next = 0L
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var active = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var failure: Option[Fault] = None
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    @volatile var finished                    = count == 0L
    val observed: Option[mutable.Set[String]] = Option.when(trace)(mutable.Set.empty)
  }
  private val lock        = ReentrantLock()
  private val workReady   = lock.newCondition()
  private val jobComplete = lock.newCondition()
  private val queue       = mutable.ArrayDeque.empty[Job]
  private val inWorker    = ThreadLocal.withInitial[Boolean](() => false)
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  @volatile private var closed = false
  private val threads          = Array.tabulate(parallelism) { index =>
    val thread = Thread(() => workerLoop(), s"gemstone-worker-$index")
    thread.setDaemon(true)
    thread.start()
    thread
  }

  /** Roughly four claims per worker keeps the queue touched a bounded number of times without starving a worker. */
  private def grainFor(count: Long): Int = {
    val perWorker = parallelism.toLong * 4L
    val grain     = if (count <= perWorker) 1L else (count - 1L) / perWorker + 1L
    math.max(1L, math.min(count, math.min(grain, Int.MaxValue.toLong))).toInt
  }

  private def enqueue(
      count: Long :| Positive0,
      task: Long :| Positive0 => Either[Fault, Unit]
  ): Job = lock.withLock {
    if (closed) throw java.util.concurrent.RejectedExecutionException("execution pool is closed")
    val job = Job(count, grainFor(count), task)
    if (count > 0L) {
      queue.append(job)
      workReady.signal()
    }
    job
  }

  private def take(until: Option[Job]): Option[(Job, Long, Int)] = lock.withLock {
    while (!closed && queue.isEmpty && until.forall(!_.finished)) workReady.await()
    if (closed || until.exists(_.finished)) None
    else {
      val job   = queue.removeHead()
      val start = job.next
      val end   = math.min(job.count, start + job.grain.toLong)
      job.next = end
      job.active += 1
      job.observed.foreach(_ += Thread.currentThread().getName)
      if (job.next < job.count) {
        queue.append(job)
        workReady.signal()
      }
      Some((job, start, (end - start).toInt))
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def execute(work: (Job, Long, Int)): Unit = {
    val (job, start, length)         = work
    var outcome: Either[Fault, Unit] = Right(())
    try {
      var offset = 0
      while (offset < length && outcome.isRight) {
        outcome = job.task((start + offset.toLong).refineUnsafe[Positive0])
        offset += 1
      }
    } catch {
      case NonFatal(error) =>
        val detail = Throwables.detail(error)
        outcome = Left(Fault.Backend(detail))
    } finally {
      // A task may throw a fatal error such as InterruptedException; the job
      // still has to be accounted for or it never completes.
      val completed = outcome
      lock.withLock {
        job.active -= 1
        completed.left.toOption.foreach { error =>
          if (job.failure.isEmpty) job.failure = Some(error)
          queue.filterInPlace(_ ne job)
          job.next = job.count
        }
        if (job.active == 0 && job.next == job.count) {
          job.finished = true
          jobComplete.signalAll()
          workReady.signalAll()
        }
      }
    }
  }

  private def workerLoop(): Unit = {
    inWorker.set(true)
    // ThreadLocal membership is worker state rather than an AutoCloseable resource.
    try while (!closed) take(None).foreach(execute(_))
    catch { case _: InterruptedException => Thread.currentThread().interrupt() }
    finally inWorker.remove()
  }

  private def await(job: Job): Either[Fault, Unit] = {
    if (inWorker.get()) {
      while (!job.finished && !closed) take(Some(job)).foreach(execute(_))
    } else lock.withLock {
      while (!job.finished && !closed) jobComplete.await()
    }
    if (!job.finished) Left(Fault.Backend("execution pool closed before the job completed"))
    else job.failure.toLeft(())
  }

  override def schedule(runnable: Runnable): Unit = {
    val _ = enqueue(1L, _ => { runnable.run(); Right(()) })
  }

  override def shouldParallel(taskCount: Long :| Positive0, workItemsPerTask: Long :| Positive0): Boolean =
    parallelism > 1 && taskCount > 1L && (taskCount >= minGroups || workItemsPerTask >= minWorkItems)

  override def parallelFor(taskCount: Long :| Positive0)(
      task: Long :| Positive0 => Either[Fault, Unit]
  ): Either[Fault, Unit] = {
    val started  = if (trace) PlatformTime.monotonicNanos else 0L
    val enqueued =
      try Right(enqueue(taskCount, task))
      catch {
        case error: java.util.concurrent.RejectedExecutionException =>
          Left(Fault.Backend(Option(error.getMessage).getOrElse("execution pool is closed")))
      }
    enqueued match {
      case Left(fault) => Left(fault)
      case Right(job)  =>
        val result = await(job)
        job.observed.foreach { observed =>
          scribe.info(
            s"Gemstone parallel groups=$taskCount configured=$parallelism workers=${observed.size} elapsed_ns=${PlatformTime.monotonicNanos -
                started}"
          )
        }
        result
    }
  }

  override def close(): Unit = {
    lock.withLock {
      closed = true
      queue.clear()
      workReady.signalAll()
      jobComplete.signalAll()
    }
    threads.foreach(_.interrupt())
    threads.view.filterNot(_ eq Thread.currentThread()).foreach(_.join())
  }
}

object WorkerPool {
  val EnvironmentName                                 = EnvironmentNames.ExecutionParallelism
  private val Maximum                                 = 64
  private given (Interval.Closed[1, 64] ==> Positive) = Implication()

  def fromEnvironment(environment: Map[String, String]): WorkerPool = {
    val available                   = hostBound
    val configured: Int :| Positive = environment.get(EnvironmentName) match {
      // Leave a small amount of host capacity for RPC, launcher, and GC work;
      // the remaining logical CPUs are available to the bounded interpreter.
      case None        => defaultParallelism
      case Some(value) =>
        value.toIntOption
          .toRight(s"$EnvironmentName must be a positive integer: $value")
          .flatMap(_.refineEither[Positive].left.map(_ => s"$EnvironmentName must be positive: $value"))
          .fold(message => throw IllegalArgumentException(message), value => value)
    }
    if (configured > available)
      throw IllegalArgumentException(s"$EnvironmentName=$configured exceeds host bound $available")
    WorkerPool(
      configured,
      minGroups = configured.toLong,
      minWorkItems = 32L,
      trace = EnvironmentNames.traceEnabled(environment, "perf")
    )
  }

  private def hostBound: Int :| Interval.Closed[1, 64] =
    math.max(1, math.min(Maximum, sys.runtime.availableProcessors())).refineUnsafe[Interval.Closed[1, 64]]

  private def defaultParallelism: Int :| Interval.Closed[1, 64] =
    math.max(1, hostBound - 2).refineUnsafe[Interval.Closed[1, 64]]
}
