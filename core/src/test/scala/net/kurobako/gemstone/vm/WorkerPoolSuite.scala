package net.kurobako.gemstone.vm

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import scala.util.Using

class WorkerPoolSuite extends munit.FunSuite {
  test("bounded workgroups visit each task index exactly once") {
    Using.resource(WorkerPool(2, minGroups = 1, minWorkItems = 1)) { workgroups =>
      val visited = AtomicIntegerArray(32)
      val result  = workgroups.parallelFor(32L) { index =>
        assert(index >= 0L && index < 32L, s"out-of-range task index: $index")
        val _ = visited.incrementAndGet(index.raw.toInt)
        Right(())
      }
      assertEquals(result, Right(()): Either[Fault, Unit])
      (0 until 32).foreach(index => assertEquals(visited.get(index), 1, s"task index $index"))
    }
  }
  test("ExecutionContext submissions can join nested workgroups when every worker is occupied") {
    Using.resource(WorkerPool(2, minGroups = 1, minWorkItems = 1)) { pool =>
      val entered = java.util.concurrent.CountDownLatch(2)
      val visited = AtomicInteger()
      val tasks   = Vector.fill(2)(java.util.concurrent.FutureTask[Either[Fault, Unit]] { () =>
        entered.countDown()
        assert(entered.await(5L, java.util.concurrent.TimeUnit.SECONDS))
        pool.parallelFor(8L) { _ =>
          pool.parallelFor(3L) { _ =>
            val _ = visited.incrementAndGet()
            Right(())
          }
        }
      })
      tasks.foreach(pool.execute(_))
      tasks.foreach(task => assertEquals(task.get(5L, java.util.concurrent.TimeUnit.SECONDS), Right(())))
      assertEquals(visited.get(), 48)
      assertEquals(pool.parallelFor(1L)(_ => Left(Fault.Cancelled)), Left(Fault.Cancelled))
    }
  }

  test("ExecutionContext reports failed submissions and continues accepting work") {
    val observed = java.util.concurrent.atomic.AtomicReference(Option.empty[Throwable])
    val reported = java.util.concurrent.CountDownLatch(1)
    val error    = IllegalStateException("failed task")
    Using.resource(WorkerPool(
      1,
      reporter = failure => {
        observed.set(Some(failure))
        reported.countDown()
      }
    )) { pool =>
      pool.execute(() => throw error)
      assert(reported.await(5L, java.util.concurrent.TimeUnit.SECONDS))
      assertEquals(observed.get(), Some(error))
      val next = java.util.concurrent.FutureTask[Int](() => 42)
      pool.execute(next)
      assertEquals(next.get(5L, java.util.concurrent.TimeUnit.SECONDS), 42)
    }
  }

  test("workgroup measurements are supplied to the caller") {
    val measured = java.util.concurrent.atomic.AtomicReference(Option.empty[WorkerPool.Measurement])
    Using.resource(WorkerPool(2, measurement = Some(value => measured.set(Some(value))))) { pool =>
      assertEquals(pool.parallelFor(7L)(_ => Right(())), Right(()))
      val result = measured.get().getOrElse(fail("missing measurement"))
      assertEquals(result.taskCount.raw, 7L)
      assert(result.workers >= 1 && result.workers <= 2)
      assert(result.elapsedNanos >= 0L)
    }
  }
}
