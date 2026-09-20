package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.execution.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray
import scala.jdk.CollectionConverters.*
import scala.util.Using

final class InterpreterSchedulerSuite extends munit.FunSuite {
  // The try/finally blocks below release latches and maintain test counters; those synchronization states are not
  // AutoCloseable resources.
  private val groupCounts: Vector[Int :| Positive] = Vector(1, 2, 3, 5, 9)

  for {
    workers <- Vector[Int :| Positive](1, 4)
    groups  <- groupCounts
  } {
    test(s"$groups workgroups use at most $workers workers and execute exactly once") {
      Using.resource(InterpreterScheduler(workers)) { pool =>
        val entered = CountDownLatch(math.min(workers, groups))
        val release = CountDownLatch(1)
        val visits  = AtomicIntegerArray(groups)
        val threads = ConcurrentHashMap.newKeySet[String]()
        val active  = AtomicInteger()
        val maximum = AtomicInteger()
        try {
          val result = CompletableFuture.supplyAsync(() =>
            pool.parallelFor(groups.toLong.refineUnsafe[Positive0]) { group =>
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
          assertEquals((0 until groups).map(visits.get(_)).toVector, Vector.fill(groups)(1))
          assertEquals(active.get(), 0)
          assert(maximum.get() <= workers)
        } finally release.countDown()
      }
    }

    test(s"nested dispatches progress for $groups groups with $workers workers") {
      Using.resource(InterpreterScheduler(workers)) { pool =>
        val visits = AtomicIntegerArray(groups * 3)
        val result = CompletableFuture.supplyAsync(() =>
          pool.call(() =>
            pool.parallelFor(groups.toLong.refineUnsafe[Positive0]) { group =>
              pool.parallelFor(3) { child =>
                val _ = visits.incrementAndGet(group.toInt * 3 + child.toInt)
                Right(())
              }
            }
          )
        )
        assertEquals(result.get(10, TimeUnit.SECONDS), Right(()))
        assertEquals((0 until groups * 3).map(visits.get(_)).toVector, Vector.fill(groups * 3)(1))
      }
    }
  }

  test("a short job progresses before a nine-group job completes on one worker") {
    Using.resource(InterpreterScheduler(1)) { pool =>
      val first     = CountDownLatch(1)
      val release   = CountDownLatch(1)
      val shortDone = CountDownLatch(1)
      val order     = ConcurrentLinkedQueue[String]()
      try {
        val large = CompletableFuture.supplyAsync(() =>
          pool.parallelFor(9) { group =>
            val released = if (group == 0) {
              first.countDown()
              release.await(10, TimeUnit.SECONDS)
            } else true
            if (released) {
              val _ = order.add(s"group-$group")
              Right(())
            } else Left(ExecutionFailure.Failed("test release timed out"))
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
      } finally release.countDown()
    }
  }

  test("a failed workgroup does not cancel another job or poison the worker") {
    Using.resource(InterpreterScheduler(1)) { pool =>
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
            if (release.await(10, TimeUnit.SECONDS)) Left(failure)
            else Left(ExecutionFailure.Failed("test release timed out"))
          }
        )
        assert(first.await(10, TimeUnit.SECONDS))
        pool.schedule(() => otherDone.countDown())
        release.countDown()
        assertEquals(failed.get(10, TimeUnit.SECONDS), Left(failure))
        assert(otherDone.await(10, TimeUnit.SECONDS))
        assertEquals(visits.get(), 1)
        assertEquals(CompletableFuture.supplyAsync(() => pool.call(() => 42)).get(10, TimeUnit.SECONDS), 42)
      } finally release.countDown()
    }
  }

  test("workgroups share an exact budget while another dispatch keeps its own allowance") {
    Using.resource(InterpreterScheduler(4)) { pool =>
      for (groups <- groupCounts) {
        val limit            = groups * 7 - 1
        val constrainedLimit = limit.toLong.refineEither[Positive].fold(message => fail(message), value => value)
        val constrained      = ExecutionControl(ExecutionLimits(constrainedLimit, 1), parallelism = pool)
        val independentLimit = (groups * 7L).refineEither[Positive].fold(message => fail(message), value => value)
        val independent      = ExecutionControl(
          ExecutionLimits(independentLimit, 1),
          parallelism = pool
        )
        val accepted                                                 = AtomicInteger()
        val otherAccepted                                            = AtomicInteger()
        val entered                                                  = CountDownLatch(2)
        val release                                                  = CountDownLatch(1)
        def execute(control: ExecutionControl, count: AtomicInteger) =
          CompletableFuture.supplyAsync(() =>
            pool.parallelFor(groups.toLong.refineUnsafe[Positive0]) { group =>
              val released = if (group == 0) {
                entered.countDown()
                release.await(10, TimeUnit.SECONDS)
              } else true
              if (released) {
                (0 until 7).foreach { _ =>
                  if (control.checkpoint().isRight) { val _ = count.incrementAndGet() }
                }
                Right(())
              } else Left(ExecutionFailure.Failed("budget job release timed out"))
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
    }
  }

  test("closing one session cancels its running and queued launches while another session progresses") {
    val entered              = CountDownLatch(1)
    val release              = CountDownLatch(1)
    val stopped              = CountDownLatch(1)
    val drained              = CountDownLatch(1)
    val independentDone      = CountDownLatch(1)
    val cancelledLaunches    = AtomicInteger()
    val observedCancellation = java.util.concurrent.atomic.AtomicBoolean()
    final class Backend(cancelledSession: Boolean) extends ProgramExecutor {
      override def load(id: ProgramId, format: ProgramFormat, image: Vec[Byte])                         = Right(())
      override def unload(id: ProgramId)                                                                = Right(())
      override def launch(dispatch: KernelDispatch, memory: ExecutionMemory, control: ExecutionControl) =
        if (cancelledSession) {
          val _ = cancelledLaunches.incrementAndGet()
          entered.countDown()
          try
            if (release.await(10, TimeUnit.SECONDS)) {
              observedCancellation.set(control.cancellation.isCancelled)
              control.checkpoint().left.map(fault => ExecutionFailure.Failed(fault.message))
            } else Left(ExecutionFailure.Failed("test release timed out"))
          finally stopped.countDown()
        } else {
          val result = control.checkpoint().left.map(fault => ExecutionFailure.Failed(fault.message))
          independentDone.countDown()
          result
        }
    }
    Using.Manager { use =>
      val pool      = use(InterpreterScheduler(4))
      val cancelled = use(ExecutionService(
        Backend(true),
        runnable =>
          pool.schedule(() =>
            try runnable.run()
            finally drained.countDown()
          ),
        parallelism = pool
      ))
      val independent = use(ExecutionService(Backend(false), pool.schedule, parallelism = pool))
      try {
        val firstProgram                 = cancelled.load(ProgramFormat.SPIRVBinary, Vec.empty).toOption.get
        val otherProgram                 = independent.load(ProgramFormat.SPIRVBinary, Vec.empty).toOption.get
        def dispatch(program: ProgramId) =
          KernelDispatch.validated(program, KernelName("k"), Dimensions.One, Dimensions.One, Vec.empty).toOption.get
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
      } finally release.countDown()
    }.get
  }

  test("environment configuration uses one shared default and rejects host oversubscription") {
    val hostBound = math.min(64, math.max(1, sys.runtime.availableProcessors()))
    Using.resource(InterpreterScheduler.fromEnvironment(Map.empty)) { defaultScheduler =>
      assertEquals(defaultScheduler.parallelism, math.max(1, hostBound - 2))
    }

    Using.resource(InterpreterScheduler.fromEnvironment(Map(
      InterpreterScheduler.EnvironmentName -> hostBound.toString
    ))) { explicitScheduler =>
      assertEquals(explicitScheduler.parallelism, hostBound)
    }

    val invalidZero = intercept[IllegalArgumentException](InterpreterScheduler.fromEnvironment(Map(
      InterpreterScheduler.EnvironmentName -> "0"
    )))
    assert(invalidZero.getMessage.contains("must be positive"))
    val invalidOversubscription = intercept[IllegalArgumentException](InterpreterScheduler.fromEnvironment(Map(
      InterpreterScheduler.EnvironmentName -> (hostBound + 1).toString
    )))
    assert(invalidOversubscription.getMessage.contains("exceeds host bound"))
  }

}
