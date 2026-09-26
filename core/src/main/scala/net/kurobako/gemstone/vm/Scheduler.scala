package net.kurobako.gemstone.vm

import net.kurobako.gemstone.{NonNegativeLong, PositiveInt}

trait Scheduler {
  def parallelism: PositiveInt
  def shouldParallel(taskCount: NonNegativeLong, workItemsPerTask: NonNegativeLong): Boolean
  def parallelFor(
      taskCount: NonNegativeLong
  )(task: NonNegativeLong => Either[Fault, Unit]): Either[Fault, Unit]

  /** Runs work that is not part of a parallel task iteration; defaults to the calling thread. */
  def schedule(runnable: Runnable): Unit = runnable.run()

  final def forEach(
      taskCount: NonNegativeLong,
      parallel: Boolean
  )(task: NonNegativeLong => Either[Fault, Unit]): Either[Fault, Unit] =
    if (parallel) parallelFor(taskCount)(task)
    else {
      @scala.annotation.tailrec
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      def loop(index: NonNegativeLong): Either[Fault, Unit] =
        if (index == taskCount) Right(())
        else task(index) match {
          case error @ Left(_) => error
          case Right(())       => loop(NonNegativeLong.unsafe(index.raw + 1L))
        }
      loop(NonNegativeLong.Zero)
    }
}

object Scheduler {
  object Sequential extends Scheduler {
    override val parallelism: PositiveInt = PositiveInt.One

    override def shouldParallel(
        taskCount: NonNegativeLong,
        workItemsPerTask: NonNegativeLong
    ): Boolean = false

    override def parallelFor(
        taskCount: NonNegativeLong
    )(task: NonNegativeLong => Either[Fault, Unit]): Either[Fault, Unit] =
      forEach(taskCount, parallel = false)(task)
  }
}
