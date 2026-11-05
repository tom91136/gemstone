package net.kurobako.gemstone.vm

import net.kurobako.gemstone.constraint.*
import scala.annotation.tailrec

object Workgroups {
  object Sequential extends Workgroups {
    override def concurrency: Int > 0 = 1

    override def shouldParallel(
        taskCount: Long >= 0,
        workItemsPerTask: Long >= 0
    ): Boolean = false

    override def parallelFor(
        taskCount: Long >= 0
    )(task: Long >= 0 => Either[Fault, Unit]): Either[Fault, Unit] =
      forEach(taskCount, parallel = false)(task)
  }
}

trait Workgroups {

  /** The most tasks of one [[parallelFor]] that run at once. */
  def concurrency: Int > 0

  /** The most tasks of one [[parallelFor]] that run at once once a task has called [[coschedule]]. */
  def residency: Int > 0 = concurrency

  /** Called from a running task that waits on its peers: starts the rest of its [[parallelFor]] alongside it. */
  def coschedule(): Unit = ()

  def shouldParallel(taskCount: Long >= 0, workItemsPerTask: Long >= 0): Boolean
  def parallelFor(
      taskCount: Long >= 0
  )(task: Long >= 0 => Either[Fault, Unit]): Either[Fault, Unit]

  final def forEach(
      taskCount: Long >= 0,
      parallel: Boolean
  )(task: Long >= 0 => Either[Fault, Unit]): Either[Fault, Unit] =
    if (parallel) parallelFor(taskCount)(task)
    else {
      @tailrec
      def loop(index: Long >= 0): Either[Fault, Unit] =
        if (index == taskCount) Right(())
        else task(index) match {
          case error @ Left(_) => error
          case Right(())       => loop(index + 1L)
        }
      loop(0L)
    }
}
