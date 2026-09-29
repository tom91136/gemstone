package net.kurobako.gemstone.vm

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.annotation.tailrec

object TaskControl {
  object Limits {
    val Default: Limits = Limits(maxInstructions = 100000000L)
  }

  case class Limits(maxInstructions: Long > 0)
}

/** One execution's irreversible cancellation and shared instruction allowance. A rejected charge consumes nothing;
  * cancellation is permanent. Already-running instructions stop at their next checkpoint.
  */
class TaskControl(val limits: TaskControl.Limits = TaskControl.Limits.Default) {
  private val cancelled = AtomicBoolean()
  private val consumed  = AtomicLong()

  def cancel(): Unit       = cancelled.set(true)
  def isCancelled: Boolean = cancelled.get()

  def checkpoint(cost: Int > 0 = 1): Either[Fault, Unit] = checkpoint(cost.toLong)

  @tailrec
  final def checkpoint(cost: Long > 0): Either[Fault, Unit] = {
    val current = consumed.get()
    if (isCancelled) Left(Fault.Cancelled)
    else if (cost.raw > limits.maxInstructions.raw - current) Left(Fault.BudgetExceeded(limits.maxInstructions))
    else if (consumed.compareAndSet(current, current + cost.raw)) Right(())
    else checkpoint(cost)
  }

  def usedInstructions: Long >= 0      = consumed.get().unsafe[Long >= 0]
  def remainingInstructions: Long >= 0 = limits.maxInstructions - usedInstructions
}
