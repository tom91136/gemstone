package net.kurobako.gemstone.execution

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class ExecutionSuite extends munit.FunSuite {
  test("execution control owns one bounded, cancellable dispatch budget") {
    val cancellation = CancellationToken.Source()
    val limits       = ExecutionLimits(2, 1)
    val control      = ExecutionControl(limits, cancellation)
    assertEquals(control.checkpoint(), Right(()))
    assertEquals(control.checkpoint(), Right(()))
    assertEquals(control.checkpoint(), Left(ExecutionControlFailure.BudgetExceeded(2)))

    val cancelled = ExecutionControl(limits, cancellation)
    cancellation.cancel()
    assertEquals(cancelled.checkpoint(), Left(ExecutionControlFailure.Cancelled))
  }

  test("execution-limit runtime inputs are refined at their boundary") {
    val limits = for {
      instructions <- 2000000000L.refineEither[Positive]
      quantum      <- 8192.refineEither[Positive]
    } yield ExecutionLimits(instructions, quantum)
    assertEquals(limits, Right(ExecutionLimits(2000000000L, 8192)))
    assert(0L.refineEither[Positive].isLeft)
    assert(0.refineEither[Positive].isLeft)
  }

  test("concurrent instruction tickets preserve the exact budget") {
    val budget         = ExecutionBudget(10000L, concurrent = true)
    val successes      = java.util.concurrent.atomic.AtomicInteger()
    val start          = java.util.concurrent.CountDownLatch(1)
    val mutableWorkers = Vector.newBuilder[Thread]
    (0 until 8).foreach { _ =>
      val worker = Thread { () =>
        start.await()
        (0 until 2000).foreach { _ =>
          if (budget.consume().isRight) { val _ = successes.incrementAndGet() }
        }
      }
      val _ = mutableWorkers += worker
    }
    val workers = mutableWorkers.result()
    workers.foreach(_.start())
    start.countDown()
    workers.foreach(_.join())
    assertEquals(successes.get(), 10000)
    assertEquals(budget.used, 10000L)
    assertEquals(budget.remaining, 0L)
    assert(budget.consume(2).isLeft)
    assertEquals(budget.used, 10000L)
  }

  test("instruction tickets and general costs share one exact counter") {
    val budget = ExecutionBudget(5L, concurrent = true)
    assertEquals(budget.consume(), Right(()))
    assertEquals(budget.consume(3), Right(()))
    assert(budget.consume(2).isLeft)
    assertEquals(budget.remaining, 1L)
    assertEquals(budget.consume(), Right(()))
    assert(budget.consume().isLeft)
    assertEquals(budget.used, 5L)
    val large = ExecutionBudget(2147483648L, concurrent = true)
    assertEquals(large.consume(Int.MaxValue), Right(()))
    assertEquals(large.consume(), Right(()))
    assert(large.consume().isLeft)
    assertEquals(large.remaining, 0L)
  }

}
