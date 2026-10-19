package net.kurobako.gemstone.testing

import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** Requests parallel dispatch whenever more than one task exists and counts the requests. Tasks run on the caller. */
class RecordingWorkgroups extends Workgroups {
  val calls: AtomicInteger = AtomicInteger()

  override def shouldParallel(tasks: Long >= 0, workItems: Long >= 0): Boolean = tasks > 1L

  override def parallelFor(tasks: Long >= 0)(task: Long >= 0 => Either[Fault, Unit]): Either[Fault, Unit] = {
    val _ = calls.incrementAndGet()
    run(tasks)(task)
  }

  protected def run(tasks: Long >= 0)(task: Long >= 0 => Either[Fault, Unit]): Either[Fault, Unit] =
    Workgroups.Sequential.parallelFor(tasks)(task)
}
