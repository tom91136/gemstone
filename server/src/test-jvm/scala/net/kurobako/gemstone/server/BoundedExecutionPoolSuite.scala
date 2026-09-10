package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.execution.*

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray
import scala.jdk.CollectionConverters.*

final class BoundedExecutionPoolSuite extends munit.FunSuite {
  private val groupCounts = Vector(1, 2, 3, 5, 9)

  for {
    workers <- Vector(1, 4)
    groups  <- groupCounts
  } {
    test(s"$groups workgroups use at most $workers workers and execute exactly once") {
      val pool    = BoundedExecutionPool(workers)
      val entered = CountDownLatch(math.min(workers, groups))
      val release = CountDownLatch(1)
      val visits  = AtomicIntegerArray(groups)
      val threads = ConcurrentHashMap.newKeySet[String]()
      val active  = AtomicInteger()
      val maximum = AtomicInteger()
      try {
        val result = CompletableFuture.supplyAsync(() =>
          pool.parallelFor(groups) { group =>
            val running = active.incrementAndGet()
            val _       = maximum.accumulateAndGet(running, (left, right) => math.max(left, right))
            val _       = threads.add(Thread.currentThread().getName)
            val _       = visits.incrementAndGet(group.toInt)
            entered.countDown()
            try
              if (release.await(10, TimeUnit.SECONDS)) Right(())
              else Left(ExecutionFailure.Failed("test workgroup release timed out"))
            finally { val _ = active.decrementAndGet() }
          }
        )
        assert(entered.await(10, TimeUnit.SECONDS), "configured workers did not start")
        assertEquals(threads.size(), math.min(workers, groups))
        release.countDown()
        assertEquals(result.get(10, TimeUnit.SECONDS), Right(()))
        assertEquals((0 until groups).map(visits.get).toVector, Vector.fill(groups)(1))
        assertEquals(active.get(), 0)
        assert(maximum.get() <= workers)
      } finally { release.countDown(); pool.close() }
    }

    test(s"nested dispatches progress for $groups groups with $workers workers") {
      val pool   = BoundedExecutionPool(workers)
      val visits = AtomicIntegerArray(groups * 3)
      try {
        val result = CompletableFuture.supplyAsync(() =>
          pool.call(() =>
            pool.parallelFor(groups) { group =>
              pool.parallelFor(3) { child =>
                val _ = visits.incrementAndGet(group.toInt * 3 + child.toInt)
                Right(())
              }
            }
          )
        )
        assertEquals(result.get(10, TimeUnit.SECONDS), Right(()))
        assertEquals((0 until groups * 3).map(visits.get).toVector, Vector.fill(groups * 3)(1))
      } finally pool.close()
    }
  }

  test("a short job progresses before a nine-group job completes on one worker") {
    val pool      = BoundedExecutionPool(1)
    val first     = CountDownLatch(1)
    val release   = CountDownLatch(1)
    val shortDone = CountDownLatch(1)
    val order     = ConcurrentLinkedQueue[String]()
    try {
      val large = CompletableFuture.supplyAsync(() =>
        pool.parallelFor(9) { group =>
          if (group == 0) {
            first.countDown()
            require(release.await(10, TimeUnit.SECONDS), "test release timed out")
          }
          val _ = order.add(s"group-$group")
          Right(())
        }
      )
      assert(first.await(10, TimeUnit.SECONDS))
      pool.schedule { () =>
        val _ = order.add("short"); shortDone.countDown()
      }
      release.countDown()
      assert(shortDone.await(10, TimeUnit.SECONDS))
      assertEquals(large.get(10, TimeUnit.SECONDS), Right(()))
      val events = order.iterator().asScala.toVector
      assert(events.indexOf("short") < events.indexOf("group-8"), events.toString)
    } finally { release.countDown(); pool.close() }
  }

  test("a failed workgroup does not cancel another job or poison the worker") {
    val pool      = BoundedExecutionPool(1)
    val first     = CountDownLatch(1)
    val release   = CountDownLatch(1)
    val otherDone = CountDownLatch(1)
    val visits    = AtomicInteger()
    val failure   = ExecutionFailure.Failed("intentional group failure")
    try {
      val failed = CompletableFuture.supplyAsync(() =>
        pool.parallelFor(9) { _ =>
          val _ = visits.incrementAndGet()
          first.countDown()
          require(release.await(10, TimeUnit.SECONDS), "test release timed out")
          Left(failure)
        }
      )
      assert(first.await(10, TimeUnit.SECONDS))
      pool.schedule(() => otherDone.countDown())
      release.countDown()
      assertEquals(failed.get(10, TimeUnit.SECONDS), Left(failure))
      assert(otherDone.await(10, TimeUnit.SECONDS))
      assertEquals(visits.get(), 1)
      assertEquals(CompletableFuture.supplyAsync(() => pool.call(() => 42)).get(10, TimeUnit.SECONDS), 42)
    } finally { release.countDown(); pool.close() }
  }

  test("workgroups share an exact budget while another dispatch keeps its own allowance") {
    val pool = BoundedExecutionPool(4)
    try
      for (groups <- groupCounts) {
        val limit         = groups * 7 - 1
        val constrained   = ExecutionControl.create(ExecutionLimits(limit, 1), parallelism = pool)
        val independent   = ExecutionControl.create(ExecutionLimits(groups * 7, 1), parallelism = pool)
        val accepted      = AtomicInteger()
        val otherAccepted = AtomicInteger()
        val entered       = CountDownLatch(2)
        val release       = CountDownLatch(1)
        def execute(control: ExecutionControl, count: AtomicInteger) =
          CompletableFuture.supplyAsync(() =>
            pool.parallelFor(groups) { group =>
              if (group == 0) {
                entered.countDown()
                require(release.await(10, TimeUnit.SECONDS), "budget job release timed out")
              }
              (0 until 7).foreach { _ =>
                if (control.checkpoint().isRight) { val _ = count.incrementAndGet() }
              }
              Right(())
            }
          )
        val a = execute(constrained, accepted)
        val b = execute(independent, otherAccepted)
        try assert(entered.await(10, TimeUnit.SECONDS), "both independent jobs must overlap")
        finally release.countDown()
        assertEquals(a.get(10, TimeUnit.SECONDS), Right(()))
        assertEquals(b.get(10, TimeUnit.SECONDS), Right(()))
        assertEquals(accepted.get(), limit)
        assertEquals(otherAccepted.get(), groups * 7)
        assertEquals(constrained.usedInstructions, limit.toLong)
        assertEquals(constrained.remainingInstructions, 0L)
      }
    finally pool.close()
  }

  test("closing one session cancels its running and queued launches while another session progresses") {
    val pool                 = BoundedExecutionPool(4)
    val entered              = CountDownLatch(1)
    val release              = CountDownLatch(1)
    val stopped              = CountDownLatch(1)
    val drained              = CountDownLatch(1)
    val independentDone      = CountDownLatch(1)
    val cancelledLaunches    = AtomicInteger()
    val observedCancellation = java.util.concurrent.atomic.AtomicBoolean()
    final class Backend(cancelledSession: Boolean) extends ProgramExecutor {
      override def load(id: ProgramId, format: ProgramFormat, image: Col[Byte])                         = Right(())
      override def unload(id: ProgramId)                                                                = Right(())
      override def launch(dispatch: KernelDispatch, memory: ExecutionMemory, control: ExecutionControl) =
        if (cancelledSession) {
          val _ = cancelledLaunches.incrementAndGet()
          entered.countDown()
          try {
            require(release.await(10, TimeUnit.SECONDS), "test release timed out")
            observedCancellation.set(control.cancellation.isCancelled)
            control.checkpoint().left.map(fault => ExecutionFailure.Failed(fault.message))
          } finally stopped.countDown()
        } else {
          val result = control.checkpoint().left.map(fault => ExecutionFailure.Failed(fault.message))
          independentDone.countDown()
          result
        }
    }
    val cancelled = ExecutionService(
      Backend(true),
      runnable =>
        pool.schedule(() =>
          try runnable.run()
          finally drained.countDown()
        ),
      parallelism = pool
    )
    val independent = ExecutionService(Backend(false), pool.schedule, parallelism = pool)
    try {
      val firstProgram                 = cancelled.load(ProgramFormat.SPIRV, Col.empty).toOption.get
      val otherProgram                 = independent.load(ProgramFormat.SPIRV, Col.empty).toOption.get
      def dispatch(program: ProgramId) =
        KernelDispatch.create(program, "k", Dimensions.One, Dimensions.One, Col.empty).toOption.get
      (0 until 9).foreach(_ => assert(cancelled.launch(dispatch(firstProgram)).isRight))
      assert(entered.await(10, TimeUnit.SECONDS))
      assert(independent.launch(dispatch(otherProgram)).isRight)
      assert(independentDone.await(10, TimeUnit.SECONDS), "other session waited for the blocked dispatch")
      cancelled.close()
      release.countDown()
      assert(stopped.await(10, TimeUnit.SECONDS))
      assert(drained.await(10, TimeUnit.SECONDS), "cancelled session worker did not drain")
      assert(observedCancellation.get())
      assertEquals(cancelledLaunches.get(), 1, "queued launches executed after session cancellation")
    } finally { release.countDown(); cancelled.close(); independent.close(); pool.close() }
  }

}
