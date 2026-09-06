package net.kurobako.gemstone.server

import net.kurobako.gemstone.execution.*

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinTask
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

import scala.util.control.NonFatal

/** One bounded pool for all emulated work in a JVM server.
  *
  * The pool owns a small number of worker tasks which pull work-group indices from an atomic cursor. A launch may
  * contain millions of groups without creating a task or thread per group. The same pool is also used to start each
  * session's serialized dispatch queue, so independent sessions cannot multiply the host's worker count.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
private[server] final class BoundedExecutionPool private[server] (
    val parallelism: Int,
    minGroups: Long = 16L,
    minWorkItems: Long = 64L
) extends ExecutionParallelism
    with AutoCloseable {
  private val pool = ForkJoinPool(parallelism)

  def schedule(runnable: Runnable): Unit = pool.execute(runnable)

  override def shouldParallel(taskCount: Long, workItemsPerTask: Long): Boolean =
    parallelism > 1 && taskCount > 1L && (taskCount >= minGroups || workItemsPerTask >= minWorkItems)

  override def parallelFor(
      taskCount: Long
  )(task: Long => Either[ExecutionFailure, Unit]): Either[ExecutionFailure, Unit] = {
    require(taskCount >= 0L, s"parallel task count must be non-negative: $taskCount")
    if (taskCount <= 1L || parallelism == 1)
      return ExecutionParallelism.Sequential.parallelFor(taskCount)(task)

    // A few more logical chunks than workers keeps uneven workgroups balanced
    // without making the task count proportional to the launch dimensions.
    val workerCount = math.min(taskCount, math.multiplyExact(parallelism.toLong, 4L)).toInt
    val next        = AtomicLong(0L)
    val failure     = AtomicReference[ExecutionFailure | Null](null)
    val workers     = Array.ofDim[ForkJoinTask[Unit]](workerCount)
    var worker      = 0
    while (worker < workerCount) {
      workers(worker) = pool.submit { () =>
        var index = next.getAndIncrement()
        while (index < taskCount && failure.get() == null) {
          try
            task(index) match {
              case Left(error) =>
                val _ = failure.compareAndSet(null, error)
              case Right(()) => ()
            }
          catch {
            case NonFatal(error) =>
              val message = Option(error.getMessage).getOrElse(error.getClass.getName)
              val _       = failure.compareAndSet(null, ExecutionFailure.Failed(s"parallel execution failed: $message"))
          }
          index = next.getAndIncrement()
        }
        ()
      }
      worker += 1
    }
    worker = 0
    while (worker < workers.length) {
      workers(worker).join()
      worker += 1
    }
    Option(failure.get()).toLeft(())
  }

  override def close(): Unit = pool.shutdown()
}

private[server] object BoundedExecutionPool {
  val EnvironmentName             = "GEMSTONE_EXECUTION_PARALLELISM"
  val MinimumGroupsEnvironment    = "GEMSTONE_EXECUTION_PARALLEL_MIN_GROUPS"
  val MinimumWorkItemsEnvironment = "GEMSTONE_EXECUTION_PARALLEL_MIN_WORK_ITEMS"
  private val Maximum             = 64

  def fromEnvironment(environment: Map[String, String], backend: Option[String] = None): BoundedExecutionPool = {
    val available = hostBound
    val automatic = backend match {
      case Some("hip") | Some("hsa") => math.min(defaultParallelism, 2)
      case _                         => defaultParallelism
    }
    val configured = environment.get(EnvironmentName) match {
      // Leave a small amount of host capacity for RPC, launcher, and GC work;
      // the remaining logical CPUs are available to the bounded interpreter.
      case None        => automatic
      case Some(value) =>
        value.toIntOption match {
          case Some(parsed) if parsed > 0 => parsed
          case Some(_) => throw IllegalArgumentException(s"$EnvironmentName must be positive: $value")
          case None    => throw IllegalArgumentException(s"$EnvironmentName must be a positive integer: $value")
        }
    }
    if (configured > available)
      throw IllegalArgumentException(s"$EnvironmentName=$configured exceeds host bound $available")
    def positiveLong(name: String, default: Long): Long = environment.get(name) match {
      case None        => default
      case Some(value) =>
        value.toLongOption match {
          case Some(parsed) if parsed > 0L => parsed
          case Some(_)                     => throw IllegalArgumentException(s"$name must be positive: $value")
          case None => throw IllegalArgumentException(s"$name must be a positive integer: $value")
        }
    }
    val minGroups    = positiveLong(MinimumGroupsEnvironment, configured.toLong * 4L)
    val minWorkItems = positiveLong(MinimumWorkItemsEnvironment, 64L)
    BoundedExecutionPool(configured, minGroups, minWorkItems)
  }

  private def hostBound: Int = math.max(1, math.min(Maximum, Runtime.getRuntime.availableProcessors()))

  private def defaultParallelism: Int = math.max(1, hostBound - 2)
}
