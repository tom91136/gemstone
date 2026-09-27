package net.kurobako.gemstone.vm

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}
import scala.annotation.tailrec

final class TaskControl private (
    val limits: TaskLimits,
    val cancellation: TaskControl.Cancellation,
    val observation: ObservationContext,
    private val budget: TaskControl.Budget
) {
  def checkpoint(cost: Positive[Int] = 1): Either[Fault, Unit] = checkpoint(cost.widen[Positive[Long]])

  def checkpoint(cost: Positive[Long]): Either[Fault, Unit] =
    if (cancellation.isCancelled) Left(Fault.Cancelled)
    else budget.consume(cost.raw)

  def usedInstructions: NonNegative[Long] = NonNegative.unsafe(budget.used)

  private[gemstone] def remainingInstructions: NonNegative[Long] = NonNegative.unsafe(budget.remaining)
}

object TaskControl {
  trait Cancellation {
    def isCancelled: Boolean
  }

  object Cancellation {
    object Never extends Cancellation {
      override val isCancelled: Boolean = false
    }

    final class Source extends Cancellation {
      private val cancelled = AtomicBoolean()

      override def isCancelled: Boolean = cancelled.get()
      def cancel(): Unit                = cancelled.set(true)
    }
  }

  def forScheduler(
      limits: TaskLimits = TaskLimits.Default,
      cancellation: Cancellation = Cancellation.Never,
      observation: ObservationContext = ObservationContext.Unscoped,
      scheduler: Scheduler = Scheduler.Sequential
  ): TaskControl =
    TaskControl(
      limits,
      cancellation,
      observation,
      Budget(limits.maxInstructions.raw, scheduler.parallelism.raw > 1)
    )

  def default: TaskControl = forScheduler()

  private sealed trait Budget {
    def limit: Long
    def used: Long
    final def remaining: Long = limit - used
    def consume(cost: Long): Either[Fault, Unit]
  }

  private object Budget {
    private val Accepted: Either[Fault, Unit] = Right(())

    def apply(limit: Long, concurrent: Boolean): Budget =
      if (concurrent) Concurrent(limit) else Sequential(limit)

    private final class Sequential(val limit: Long) extends Budget {
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      private var consumed = 0L

      override def used: Long = consumed

      override def consume(cost: Long): Either[Fault, Unit] =
        if (cost > limit - consumed) Left(Fault.BudgetExceeded(Positive.unsafe(limit)))
        else {
          consumed += cost
          Accepted
        }
    }

    private final class Concurrent(val limit: Long) extends Budget {
      private val consumed = AtomicLong()

      override def used: Long = math.min(limit, consumed.get())

      override def consume(cost: Long): Either[Fault, Unit] =
        if (cost == 1L && limit < Long.MaxValue) reserveOne()
        else reserve(cost)

      private def reserveOne(): Either[Fault, Unit] = {
        val previous = consumed.getAndIncrement()
        if (previous < limit) Accepted
        else {
          val _ = consumed.decrementAndGet()
          Left(Fault.BudgetExceeded(Positive.unsafe(limit)))
        }
      }

      @tailrec
      private def reserve(increment: Long): Either[Fault, Unit] = {
        val current = consumed.get()
        if (increment > limit - current) Left(Fault.BudgetExceeded(Positive.unsafe(limit)))
        else if (consumed.compareAndSet(current, current + increment)) Accepted
        else reserve(increment)
      }
    }
  }
}
