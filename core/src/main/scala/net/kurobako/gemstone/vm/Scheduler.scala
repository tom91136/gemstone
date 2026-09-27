package net.kurobako.gemstone.vm

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

trait Scheduler {
  def parallelism: Positive[Int]
  def shouldParallel(taskCount: NonNegative[Long], workItemsPerTask: NonNegative[Long]): Boolean
  def parallelFor(
      taskCount: NonNegative[Long]
  )(task: NonNegative[Long] => Either[Fault, Unit]): Either[Fault, Unit]

  /** Runs work that is not part of a parallel task iteration; defaults to the calling thread. */
  def schedule(runnable: Runnable): Unit = runnable.run()

  final def forEach(
      taskCount: NonNegative[Long],
      parallel: Boolean
  )(task: NonNegative[Long] => Either[Fault, Unit]): Either[Fault, Unit] =
    if (parallel) parallelFor(taskCount)(task)
    else {
      @scala.annotation.tailrec
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      def loop(index: NonNegative[Long]): Either[Fault, Unit] =
        if (index == taskCount) Right(())
        else task(index) match {
          case error @ Left(_) => error
          case Right(())       => loop(NonNegative.unsafe(index.raw + 1L))
        }
      loop(0L)
    }
}

object Scheduler {
  object Sequential extends Scheduler {
    override val parallelism: Positive[Int] = 1

    override def shouldParallel(
        taskCount: NonNegative[Long],
        workItemsPerTask: NonNegative[Long]
    ): Boolean = false

    override def parallelFor(
        taskCount: NonNegative[Long]
    )(task: NonNegative[Long] => Either[Fault, Unit]): Either[Fault, Unit] =
      forEach(taskCount, parallel = false)(task)
  }
}
