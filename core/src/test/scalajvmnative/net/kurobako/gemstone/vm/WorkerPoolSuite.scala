package net.kurobako.gemstone.vm

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import scala.util.Using

class WorkerPoolSuite extends munit.FunSuite {
  private given CanEqual[Thread.State, Thread.State] = CanEqual.derived

  // Blocking waits must run outside the suite monitor held by MUnit's by-name assert.
  test("large dispatches yield to queued work within a bounded number of groups") {
    val entered = java.util.concurrent.CountDownLatch(1)
    val release = java.util.concurrent.CountDownLatch(1)
    val visited = AtomicInteger()
    Using.resource(WorkerPool(1)) { pool =>
      val large = java.util.concurrent.FutureTask[Either[Fault, Unit]](() =>
        pool.parallelFor(65536L) { index =>
          if (index == 0L) {
            entered.countDown()
            assertEquals(release.await(5L, java.util.concurrent.TimeUnit.SECONDS), true)
          }
          val _ = visited.incrementAndGet()
          Right(())
        }
      )
      val caller = Thread(large, "large-dispatch-regression")
      caller.start()
      val short = java.util.concurrent.FutureTask[Int](() => visited.get())
      try {
        assertEquals(entered.await(5L, java.util.concurrent.TimeUnit.SECONDS), true)
        pool.execute(short)
      } finally release.countDown()
      val precedingGroups = short.get(5L, java.util.concurrent.TimeUnit.SECONDS)
      assertEquals(large.get(5L, java.util.concurrent.TimeUnit.SECONDS), Right(()))
      caller.join()
      assert(precedingGroups <= 256, s"queued work waited for $precedingGroups groups from one dispatch")
    }
  }

  test("joining workgroups does not enter an unrelated launch that can block the caller") {
    val entered       = java.util.concurrent.CountDownLatch(1)
    val releaseGroup  = java.util.concurrent.CountDownLatch(1)
    val releaseLaunch = java.util.concurrent.CountDownLatch(1)
    Using.resource(WorkerPool(1)) { pool =>
      val outer = java.util.concurrent.FutureTask[Either[Fault, Unit]](() =>
        pool.parallelFor(3L) { index =>
          if (index == 0L) {
            entered.countDown()
            assertEquals(releaseGroup.await(5L, java.util.concurrent.TimeUnit.SECONDS), true)
          }
          Right(())
        }
      )
      val unrelated = java.util.concurrent.FutureTask[Boolean](() =>
        releaseLaunch.await(5L, java.util.concurrent.TimeUnit.SECONDS)
      )
      pool.execute(outer)
      val completed = try {
        assertEquals(entered.await(5L, java.util.concurrent.TimeUnit.SECONDS), true)
        pool.execute(unrelated)
        releaseGroup.countDown()
        try { outer.get(1L, java.util.concurrent.TimeUnit.SECONDS); true }
        catch { case _: java.util.concurrent.TimeoutException => false }
      } finally {
        releaseGroup.countDown()
        releaseLaunch.countDown()
      }
      assertEquals(outer.get(5L, java.util.concurrent.TimeUnit.SECONDS), Right(()))
      assertEquals(unrelated.get(5L, java.util.concurrent.TimeUnit.SECONDS), true)
      assert(completed, "a workgroup join entered an unrelated launch before returning to its own caller")
    }
  }

  test("a workgroup join cannot be trapped in another dispatch's long group") {
    val entered      = java.util.concurrent.CountDownLatch(1)
    val releaseGroup = java.util.concurrent.CountDownLatch(1)
    val releaseOther = java.util.concurrent.CountDownLatch(1)
    Using.resource(WorkerPool(1)) { pool =>
      val outer = java.util.concurrent.FutureTask[Either[Fault, Unit]](() =>
        pool.parallelFor(3L) { index =>
          if (index == 0L) {
            entered.countDown()
            assertEquals(releaseGroup.await(5L, java.util.concurrent.TimeUnit.SECONDS), true)
          }
          Right(())
        }
      )
      val other = java.util.concurrent.FutureTask[Either[Fault, Unit]](() =>
        pool.parallelFor(1L) { _ =>
          assertEquals(releaseOther.await(5L, java.util.concurrent.TimeUnit.SECONDS), true)
          Right(())
        }
      )
      val caller = Thread(other, "independent-workgroup-caller")
      pool.execute(outer)
      val completed = try {
        assertEquals(entered.await(5L, java.util.concurrent.TimeUnit.SECONDS), true)
        caller.start()
        // WAITING here means parallelFor has enqueued its job and is awaiting completion.
        val deadline = platform.monotonicNanos + java.util.concurrent.TimeUnit.SECONDS.toNanos(5L)
        while (caller.getState != Thread.State.WAITING && platform.monotonicNanos < deadline) Thread.`yield`()
        assertEquals(caller.getState, Thread.State.WAITING)
        releaseGroup.countDown()
        try { outer.get(1L, java.util.concurrent.TimeUnit.SECONDS); true }
        catch { case _: java.util.concurrent.TimeoutException => false }
      } finally {
        releaseGroup.countDown()
        releaseOther.countDown()
      }
      assertEquals(outer.get(5L, java.util.concurrent.TimeUnit.SECONDS), Right(()))
      assertEquals(other.get(5L, java.util.concurrent.TimeUnit.SECONDS), Right(()))
      caller.join()
      assert(completed, "a completed dispatch's caller was executing another dispatch's long group")
    }
  }

  test("bounded workgroups visit each task index exactly once") {
    Using.resource(WorkerPool(2, minGroups = 1, minWorkItems = 1)) { workgroups =>
      val visited = AtomicIntegerArray(32)
      val result  = workgroups.parallelFor(32L) { index =>
        assert(index >= 0L && index < 32L, s"out-of-range task index: $index")
        val _ = visited.incrementAndGet(index.toInt)
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
        assertEquals(entered.await(5L, java.util.concurrent.TimeUnit.SECONDS), true)
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
      assertEquals(reported.await(5L, java.util.concurrent.TimeUnit.SECONDS), true)
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
  test("fatal workgroup errors wake callers with failure and reject subsequent submissions") {
    val escaped  = java.util.concurrent.CountDownLatch(1)
    val observed = java.util.concurrent.atomic.AtomicReference[Throwable]()
    val fatal    = LinkageError("broken workgroup linkage")
    Using.resource(WorkerPool(1)) { pool =>
      val result = pool.parallelFor(1L) { _ =>
        Thread.currentThread().setUncaughtExceptionHandler { (_, error) =>
          observed.set(error)
          escaped.countDown()
        }
        throw fatal
      }
      assert(result.isLeft)
      assert(escaped.await(5L, java.util.concurrent.TimeUnit.SECONDS))
      assert(observed.get() eq fatal)
      assert(pool.parallelFor(1L)(_ => Right(())).isLeft)
      intercept[java.util.concurrent.RejectedExecutionException](pool.execute(() => ()))
    }
  }

}
