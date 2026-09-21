package net.kurobako.gemstone.vm

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}
import scala.annotation.tailrec

final class TaskControl private (
    val limits: TaskLimits,
    val cancellation: TaskControl.Cancellation,
    val observation: ObservationContext,
    private val budget: TaskControl.Budget
) {
  def checkpoint(cost: Int :| Positive = 1): Either[Fault, Unit] =
    checkpoint(cost.toLong.refineUnsafe[Positive])

  def checkpoint(cost: Long :| Positive): Either[Fault, Unit] =
    if (cancellation.isCancelled) Left(Fault.Cancelled)
    else budget.consume(cost)

  def usedInstructions: Long :| Positive0 = budget.used.refineUnsafe[Positive0]

  private[gemstone] def remainingInstructions: Long :| Positive0 = budget.remaining.refineUnsafe[Positive0]
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
    TaskControl(limits, cancellation, observation, Budget(limits.maxInstructions, scheduler.parallelism > 1))

  def default: TaskControl = forScheduler()

  private sealed trait Budget {
    def limit: Long :| Positive
    def used: Long
    final def remaining: Long = limit - used
    def consume(cost: Long): Either[Fault, Unit]
  }

  private object Budget {
    private val Accepted: Either[Fault, Unit] = Right(())

    def apply(limit: Long :| Positive, concurrent: Boolean): Budget =
      if (concurrent) Concurrent(limit) else Sequential(limit)

    private final class Sequential(val limit: Long :| Positive) extends Budget {
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      private var consumed = 0L

      override def used: Long = consumed

      override def consume(cost: Long): Either[Fault, Unit] =
        if (cost > limit - consumed) Left(Fault.BudgetExceeded(limit))
        else {
          consumed += cost
          Accepted
        }
    }

    private final class Concurrent(val limit: Long :| Positive) extends Budget {
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
          Left(Fault.BudgetExceeded(limit))
        }
      }

      @tailrec
      private def reserve(increment: Long): Either[Fault, Unit] = {
        val current = consumed.get()
        if (increment > limit - current) Left(Fault.BudgetExceeded(limit))
        else if (consumed.compareAndSet(current, current + increment)) Accepted
        else reserve(increment)
      }
    }
  }
}
