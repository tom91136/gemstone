package net.kurobako.gemstone.vm

import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import scala.collection.mutable
import scala.util.control.NonFatal

object WorkerPool {
  case class Measurement(taskCount: Long >= 0, workers: Int, elapsedNanos: Long)
}

/** One fixed CPU pool. Jobs rotate after each chunk, and nested dispatch callers help the same queue. A launch only
  * stays on the calling thread when it is too small to amortise queue traffic; claiming a range per dequeue keeps that
  * threshold low.
  */
class WorkerPool(
    val parallelism: Int > 0,
    minGroups: Long = 16L,
    minWorkItems: Long = 64L,
    reporter: Throwable => Unit = _.printStackTrace(),
    measurement: Option[WorkerPool.Measurement => Unit] = None
) extends Workgroups with scala.concurrent.ExecutionContext with AutoCloseable {
  private class Job(
      val count: Long >= 0,
      val grain: Int,
      val task: Long >= 0 => Either[Fault, Unit]
  ) {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var next = 0L
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var active = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var failure: Option[Fault] = None
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    @volatile var finished: Boolean           = count.raw == 0L
    val observed: Option[mutable.Set[String]] = measurement.map(_ => mutable.Set.empty[String])
  }
  private val lock        = ReentrantLock()
  private val workReady   = lock.newCondition()
  private val jobComplete = lock.newCondition()
  private val queue       = mutable.ArrayDeque.empty[Job]
  private val inWorker    = ThreadLocal.withInitial[Boolean](() => false)
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  @volatile private var closed = false
  private val threads          = Array.tabulate(parallelism.raw) { index =>
    val thread = Thread(() => workerLoop(), s"gemstone-worker-$index")
    thread.setDaemon(true)
    thread.start()
    thread
  }

  /** Roughly four claims per worker keeps the queue touched a bounded number of times without starving a worker. */
  private def grainFor(count: Long): Int = {
    val perWorker = parallelism.raw.toLong * 4L
    val grain     = if (count <= perWorker) 1L else (count - 1L) / perWorker + 1L
    math.max(1L, math.min(count, math.min(grain, Int.MaxValue.toLong))).toInt
  }

  private def enqueue(
      count: Long >= 0,
      task: Long >= 0 => Either[Fault, Unit]
  ): Job = lock.withLock {
    if (closed) throw java.util.concurrent.RejectedExecutionException("execution pool is closed")
    val job = Job(count, grainFor(count.raw), task)
    if (count.raw > 0L) {
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
      val end   = math.min(job.count.raw, start + job.grain.toLong)
      job.next = end
      job.active += 1
      job.observed.foreach(_ += Thread.currentThread().getName)
      if (job.next < job.count.raw) {
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
        outcome = job.task((start + offset.toLong).unsafe[Long >= 0])
        offset += 1
      }
    } catch {
      case NonFatal(error) =>
        val detail = throwables.detail(error)
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
          job.next = job.count.raw
        }
        if (job.active == 0 && job.next == job.count.raw) {
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

  override def execute(runnable: Runnable): Unit = {
    val _ = enqueue(
      1L,
      _ => {
        try runnable.run()
        catch { case NonFatal(error) => reportFailure(error) }
        Right(())
      }
    )
  }

  override def reportFailure(cause: Throwable): Unit = reporter(cause)

  override def shouldParallel(taskCount: Long >= 0, workItemsPerTask: Long >= 0): Boolean =
    parallelism.raw > 1 && taskCount.raw > 1L &&
      (taskCount.raw >= minGroups || workItemsPerTask.raw >= minWorkItems)

  override def parallelFor(taskCount: Long >= 0)(
      task: Long >= 0 => Either[Fault, Unit]
  ): Either[Fault, Unit] = {
    val started  = if (measurement.nonEmpty) platform.monotonicNanos else 0L
    val enqueued =
      try Right(enqueue(taskCount, task))
      catch {
        case error: java.util.concurrent.RejectedExecutionException =>
          Left(Fault.Backend(s"execution pool rejected task: ${throwables.detail(error)}"))
      }
    enqueued match {
      case Left(fault) => Left(fault)
      case Right(job)  =>
        val result = await(job)
        measurement.foreach { observe =>
          val workers = lock.withLock(job.observed.fold(0)(_.size))
          observe(WorkerPool.Measurement(taskCount, workers, platform.monotonicNanos - started))
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
