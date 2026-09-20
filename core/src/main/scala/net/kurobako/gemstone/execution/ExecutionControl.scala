package net.kurobako.gemstone.execution

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import scala.annotation.tailrec

enum ExecutionControlFailure {
  case BudgetExceeded(limit: Long :| Positive)
  case Cancelled

  def message: String = this match {
    case BudgetExceeded(limit) => s"instruction budget of $limit was exhausted"
    case Cancelled             => "execution was cancelled"
  }
}

private[execution] sealed trait ExecutionBudget {
  def limit: Long :| Positive
  def used: Long
  final def remaining: Long = limit - used
  def consume(cost: Int = 1): Either[ExecutionControlFailure, Unit]
}

private[execution] object ExecutionBudget {
  private val Accepted: Either[ExecutionControlFailure, Unit] = Right(())

  def apply(limit: Long :| Positive, concurrent: Boolean): ExecutionBudget =
    if (concurrent) Concurrent(limit) else Sequential(limit)

  private final class Sequential(val limit: Long :| Positive) extends ExecutionBudget {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var consumed = 0L

    override def used: Long = consumed

    override def consume(cost: Int): Either[ExecutionControlFailure, Unit] = {
      val increment = cost.toLong
      if (increment > limit - consumed) Left(ExecutionControlFailure.BudgetExceeded(limit))
      else {
        consumed += increment
        Accepted
      }
    }
  }

  private final class Concurrent(val limit: Long :| Positive) extends ExecutionBudget {
    private val consumed = AtomicLong()

    override def used: Long = math.min(limit, consumed.get())

    override def consume(cost: Int): Either[ExecutionControlFailure, Unit] =
      if (cost == 1 && limit < Long.MaxValue) reserveOne()
      else reserve(cost.toLong)

    private def reserveOne(): Either[ExecutionControlFailure, Unit] = {
      val previous = consumed.getAndIncrement()
      if (previous < limit) Accepted
      else {
        val _ = consumed.decrementAndGet()
        Left(ExecutionControlFailure.BudgetExceeded(limit))
      }
    }

    @tailrec
    private def reserve(increment: Long): Either[ExecutionControlFailure, Unit] = {
      val current = consumed.get()
      if (increment > limit - current) Left(ExecutionControlFailure.BudgetExceeded(limit))
      else if (consumed.compareAndSet(current, current + increment)) Accepted
      else reserve(increment)
    }
  }

}

trait CancellationToken {
  def isCancelled: Boolean
}

object CancellationToken {
  object Never extends CancellationToken {
    override val isCancelled: Boolean = false
  }

  final class Source extends CancellationToken {
    private val cancelled = AtomicBoolean()

    override def isCancelled: Boolean = cancelled.get()
    def cancel(): Unit                = cancelled.set(true)
  }
}

/** Per-dispatch progress control shared by every ISA executor. A control instance must belong to exactly one launch. */
final class ExecutionControl(
    val limits: ExecutionLimits = ExecutionLimits.Default,
    val cancellation: CancellationToken = CancellationToken.Never,
    val parallelism: ExecutionParallelism = ExecutionParallelism.Sequential,
    val output: ExecutionOutput = ExecutionOutput.Discard
) {
  private val budget = ExecutionBudget(limits.maxInstructions, concurrent = parallelism.parallelism > 1)

  def checkpoint(cost: Int :| Positive = 1): Either[ExecutionControlFailure, Unit] =
    if (cancellation.isCancelled) Left(ExecutionControlFailure.Cancelled)
    else budget.consume(cost)

  def usedInstructions: Long :| Positive0 = budget.used.refineUnsafe[Positive0]

  private[gemstone] def remainingInstructions: Long :| Positive0 = budget.remaining.refineUnsafe[Positive0]

  private[gemstone] def cancel(): Unit = cancellation match {
    case source: CancellationToken.Source => source.cancel()
    case _                                => ()
  }
}

object ExecutionControl {
  def default: ExecutionControl = ExecutionControl()
}
