package net.kurobako.gemstone.vm

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import scala.util.Using

class TaskControlConcurrencySuite extends munit.FunSuite {
  test("instruction budgets are shared across workers and reject an oversized charge without consuming it") {
    val control  = TaskControl(TaskControl.Limits(1000L))
    val accepted = AtomicInteger()
    Using.resource(threads.Pool(4)) { pool =>
      val task: Runnable = () =>
        while (!Thread.currentThread().isInterrupted && control.checkpoint().isRight) {
          val _ = accepted.incrementAndGet()
        }
      val workers = Vector.fill(4)(pool.executor.submit(task))
      workers.foreach(_.get(5, TimeUnit.SECONDS))
    }
    assertEquals(accepted.get(), 1000)
    assertEquals(control.usedInstructions.raw, 1000L)
    assertEquals(control.remainingInstructions.raw, 0L)

    val limited = TaskControl(TaskControl.Limits(Long.MaxValue))
    assertEquals(limited.checkpoint(Long.MaxValue - 1L), Right(()))
    assertEquals(limited.checkpoint(2), Left(Fault.BudgetExceeded(Long.MaxValue)))
    assertEquals(limited.remainingInstructions.raw, 1L)
    assertEquals(limited.checkpoint(), Right(()))
  }

  test("a lease left by an idle worker is reclaimed before a charge is rejected") {
    val control              = TaskControl(TaskControl.Limits(1000L))
    def on[A](body: => A): A = {
      val result = java.util.concurrent.FutureTask[A](() => body)
      val worker = Thread(result)
      worker.start()
      try result.get(5, TimeUnit.SECONDS)
      finally worker.join(5000L)
    }
    assertEquals(on(control.checkpoint()), Right(()))
    assertEquals(on((0 until 999).count(_ => control.checkpoint().isRight)), 999)
    assertEquals(on(control.checkpoint()), Left(Fault.BudgetExceeded(1000L)))
    assertEquals(control.usedInstructions.raw, 1000L)
    val cancelled = TaskControl()
    assertEquals(cancelled.checkpoint(), Right(()))
    cancelled.cancel()
    assertEquals(cancelled.checkpoint(), Left(Fault.Cancelled))
    assertEquals(cancelled.usedInstructions.raw, 1L)
  }
}
