package net.kurobako.gemstone.vm

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

trait Scheduler {
  def parallelism: Int :| Positive
  def shouldParallel(taskCount: Long :| Positive0, workItemsPerTask: Long :| Positive0): Boolean
  def parallelFor(
      taskCount: Long :| Positive0
  )(task: Long :| Positive0 => Either[Fault, Unit]): Either[Fault, Unit]

  /** Runs work that is not part of a parallel task iteration; defaults to the calling thread. */
  def schedule(runnable: Runnable): Unit = runnable.run()

  final def forEach(
      taskCount: Long :| Positive0,
      parallel: Boolean
  )(task: Long :| Positive0 => Either[Fault, Unit]): Either[Fault, Unit] =
    if (parallel) parallelFor(taskCount)(task)
    else {
      @scala.annotation.tailrec
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      def loop(index: Long :| Positive0): Either[Fault, Unit] =
        if (index == taskCount) Right(())
        else task(index) match {
          case error @ Left(_) => error
          case Right(())       => loop((index + 1L).assume[Positive0])
        }
      loop(0L)
    }
}

object Scheduler {
  object Sequential extends Scheduler {
    override val parallelism: Int :| Positive = 1

    override def shouldParallel(
        taskCount: Long :| Positive0,
        workItemsPerTask: Long :| Positive0
    ): Boolean = false

    override def parallelFor(
        taskCount: Long :| Positive0
    )(task: Long :| Positive0 => Either[Fault, Unit]): Either[Fault, Unit] =
      forEach(taskCount, parallel = false)(task)
  }
}
