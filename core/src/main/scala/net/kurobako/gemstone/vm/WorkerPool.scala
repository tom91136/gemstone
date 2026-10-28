package net.kurobako.gemstone.vm

import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

object WorkerPool {
  trait Task extends Runnable {
    def reject(error: Throwable): Unit
  }

  case class Measurement(taskCount: Long >= 0, workers: Int, elapsedNanos: Long)
}

/** One fixed CPU pool. Jobs rotate after each chunk; nested callers execute only their awaited job. A launch only stays
  * on the calling thread when it is too small to amortise queue traffic; claiming a range per dequeue keeps that
  * threshold low.
  */
class WorkerPool(
    val parallelism: Int > 0,
    // Small groups can still contain long-running kernels. Do not raise the serial threshold
    // with the machine core count: even two independent groups can keep another worker busy.
    minGroups: Long >= 0 = 2L,
    minWorkItems: Long >= 0 = 32L,
    reporter: Throwable => Unit = _.printStackTrace(),
    measurement: Option[WorkerPool.Measurement => Unit] = None
) extends Workgroups with ExecutionContext with AutoCloseable {
  private class Job(
      val count: Long >= 0,
      val grain: Int in 1 ~ 128,
      val task: Long >= 0 => Either[Fault, Unit],
      val rejected: Option[Throwable => Unit]
  ) {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var next: Long >= 0 = 0L
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var active = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var failure: Option[Fault] = None
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    @volatile var finished: Boolean           = count == 0L
    val observed: Option[mutable.Set[String]] = measurement.map(_ => mutable.Set.empty[String])
  }
  private val lock        = ReentrantLock()
  private val workReady   = lock.newCondition()
  private val jobComplete = lock.newCondition()
  private val queue       = mutable.ArrayDeque.empty[Job]
  private val inWorker    = ThreadLocal.withInitial[Boolean](() => false)
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  @volatile private var closed = false
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  @volatile private var failure: Option[Throwable] = None
  private val threads                              = Array.tabulate(parallelism) { index =>
    val thread = Thread(() => workerLoop(), s"gemstone-worker-$index")
    thread.setDaemon(true)
    thread.start()
    thread
  }

  /** Bound each claim so a large dispatch cannot monopolise workers while another client waits. */
  private def grainFor(count: Long >= 0): Int in 1 ~ 128 = {
    val perWorker = parallelism.toLong * 4L
    val grain     = if (count <= perWorker) 1L else (count.raw - 1L) / perWorker + 1L
    math.max(1L, math.min(count, math.min(grain, 128L))).toInt.unsafe[Int in 1 ~ 128]
  }

  private def enqueue(
      count: Long >= 0,
      task: Long >= 0 => Either[Fault, Unit],
      rejected: Option[Throwable => Unit] = None
  ): Job = lock.withLock {
    if (closed) throw RejectedExecutionException("execution pool is closed", failure.orNull)
    val job = Job(count, grainFor(count), task, rejected)
    if (count > 0L) { queue.append(job); workReady.signalAll() }
    job
  }

  private def take(until: Option[Job]): Option[(Job, Long >= 0, Int >= 0)] = lock.withLock {
    // Helping another job can trap this caller in a long group even after its own
    // dispatch completes. Idle workers still share the full queue.
    def eligible: Int = queue.indexWhere(job => until.forall(_ eq job))
    while (!closed && eligible < 0 && until.forall(!_.finished)) workReady.await()
    if (closed || until.exists(_.finished)) None
    else {
      val job    = queue.remove(eligible)
      val start  = job.next
      val length = (job.count - start).min(job.grain)
      val end    = start + length
      job.next = end
      job.active += 1
      job.observed.foreach(_ += Thread.currentThread().getName)
      if (job.next < job.count) { queue.append(job); workReady.signal() }
      Some((job, start, length.toIntExact))
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def execute(work: (Job, Long >= 0, Int >= 0)): Unit = {
    val (job, start, length)         = work
    var outcome: Either[Fault, Unit] = Right(())
    try {
      var offset: Int >= 0 = 0
      while (offset < length && outcome.isRight) {
        outcome = job.task(start + offset.widen[Long >= 0])
        offset += 1
      }
    } catch {
      case NonFatal(error) =>
        val detail = throwables.detail(error)
        outcome = Left(Fault.Backend(detail))
      case error: Throwable =>
        outcome = Left(Fault.Backend(s"execution worker failed: ${throwables.detail(error)}"))
        throw error
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
    catch {
      case _: InterruptedException if closed => Thread.currentThread().interrupt()
      case error: Throwable                  =>
        fail(error)
        throw error
    } finally inWorker.remove()
  }

  private def fail(error: Throwable): Unit = {
    val rejected = lock.withLock {
      failure = Some(error)
      closed = true
      val callbacks = queue.view.flatMap(_.rejected).toList
      val fault     = Fault.Backend(s"execution worker failed: ${throwables.detail(error)}")
      queue.foreach { job =>
        job.failure = Some(fault)
        job.finished = true
      }
      queue.clear()
      workReady.signalAll()
      jobComplete.signalAll()
      callbacks
    }
    // Session cleanup can acquire other locks; never run it under the pool lock.
    rejected.foreach { reject =>
      try reject(error)
      catch { case cleanup: Throwable if cleanup ne error => error.addSuppressed(cleanup) }
    }
  }

  private def await(job: Job): Either[Fault, Unit] = {
    if (inWorker.get()) {
      while (!job.finished && !closed) take(Some(job)).foreach(execute(_))
    } else lock.withLock {
      while (!job.finished && !closed) jobComplete.await()
    }
    if (!job.finished)
      Left(Fault.Backend(failure.fold[NonEmpty[String]]("execution pool closed before the job completed")(error =>
        s"execution worker failed: ${throwables.detail(error)}"
      )))
    else job.failure.toLeft(())
  }

  override def execute(runnable: Runnable): Unit = {
    val _ = enqueue(
      1L,
      _ => {
        try runnable.run()
        catch { case NonFatal(error) => reportFailure(error) }
        Right(())
      },
      runnable match {
        case task: WorkerPool.Task => Some(task.reject(_))
        case _                     => None
      }
    )
  }

  override def reportFailure(cause: Throwable): Unit = reporter(cause)

  override def shouldParallel(taskCount: Long >= 0, workItemsPerTask: Long >= 0): Boolean =
    parallelism > 1 && taskCount > 1L &&
      (taskCount >= minGroups || workItemsPerTask >= minWorkItems)

  override def parallelFor(taskCount: Long >= 0)(
      task: Long >= 0 => Either[Fault, Unit]
  ): Either[Fault, Unit] = {
    val started  = if (measurement.nonEmpty) platform.monotonicNanos else 0L
    val enqueued =
      try Right(enqueue(taskCount, task))
      catch {
        case error: RejectedExecutionException =>
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
