package net.kurobako.gemstone.execution

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

/** Bounded work-item/work-group scheduling supplied by the hosting runtime. */
trait ExecutionParallelism {
  def parallelism: Int :| Positive
  def shouldParallel(taskCount: Long :| Positive0, workItemsPerTask: Long :| Positive0): Boolean
  def parallelFor(
      taskCount: Long :| Positive0
  )(task: Long :| Positive0 => Either[ExecutionFailure, Unit]): Either[ExecutionFailure, Unit]

  /** Run a bounded task range with identical fail-fast semantics in sequential and parallel mode. */
  final def forEach(
      taskCount: Long :| Positive0,
      parallel: Boolean
  )(task: Long :| Positive0 => Either[ExecutionFailure, Unit]): Either[ExecutionFailure, Unit] =
    if (parallel) parallelFor(taskCount)(task)
    else {
      @scala.annotation.tailrec
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      def loop(index: Long :| Positive0): Either[ExecutionFailure, Unit] =
        if (index == taskCount) Right(())
        else task(index) match {
          case error @ Left(_) => error
          case Right(())       => loop((index + 1L).assume[Positive0])
        }
      loop(0L)
    }
}

object ExecutionParallelism {
  object Sequential extends ExecutionParallelism {
    override val parallelism: Int :| Positive = 1

    override def shouldParallel(taskCount: Long :| Positive0, workItemsPerTask: Long :| Positive0): Boolean = false

    override def parallelFor(
        taskCount: Long :| Positive0
    )(task: Long :| Positive0 => Either[ExecutionFailure, Unit]): Either[ExecutionFailure, Unit] =
      forEach(taskCount, parallel = false)(task)
  }
}
