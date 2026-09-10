package net.kurobako.gemstone.execution

import net.kurobako.gemstone.Col

class ExecutionSuite extends munit.FunSuite {
  private enum TestWrite extends Write {
    case Value(value: Int)
  }

  test("an executable program is immutable and bounds checked") {
    val program = ExecutableProgram(Col("first", "second"))
    assertEquals(program.at(Pc.Entry), Right("first"))
    assert(program.at(Pc.unsafe(2)).left.exists(_.isInstanceOf[ExecutionFault.InvalidProgram]))
  }

  test("lane masks handle wave64 without shifting by 64") {
    val mask = LaneMask.all(64).toOption.get
    assertEquals(mask.size, 64)
    assert(mask.contains(0))
    assert(mask.contains(63))
  }

  test("lane masks scale to CUDA-sized blocks") {
    val mask = LaneMask.all(256).toOption.get
    assertEquals(mask.size, 256)
    assert(mask.contains(0))
    assert(mask.contains(255))

    val selected = mask.filterLanes(_ % 32 == 0)
    assertEquals(selected.size, 8)
    assert(selected.contains(0))
    assert(selected.contains(224))
    assert(!selected.contains(255))
  }

  test("lane mask set operations preserve membership") {
    val all   = LaneMask.all(4).toOption.get
    val low   = all.filterLanes(_ < 2)
    val high  = all.subtract(low)
    val union = low.union(high)
    assertEquals(low.size, 2)
    assertEquals(high.size, 2)
    assertEquals(union.size, 4)
    assertEquals(low.intersect(high).size, 0)
    assert(low.contains(0) && low.contains(1))
    assert(high.contains(2) && high.contains(3))
  }

  test("lane mask exposes the active bits for each warp") {
    val lanes = LaneMask.all(96).toOption.get
    assertEquals(lanes.warpBits(0), 0xffffffffL)
    assertEquals(lanes.warpBits(32), 0xffffffffL)
    assertEquals(lanes.warpBits(64), 0xffffffffL)
    assertEquals(lanes.filterLanes(_ != 70).warpBits(64), 0xffffffffL & ~(1L << 6))
  }

  test("effects validate every write before committing any") {
    val effects = Effects[TestWrite]
    val applied = Col.newBuilder[Int]
    effects.stage(TestWrite.Value(1))
    effects.stage(TestWrite.Value(-1))
    val result = effects.commit(
      {
        case TestWrite.Value(value) if value >= 0 => Right(())
        case TestWrite.Value(value)               => Left(ExecutionFault.InvalidState(s"negative write: $value"))
      },
      { case TestWrite.Value(value) => applied += value }
    )
    assert(result.isLeft)
    assertEquals(applied.result().toSeq, Seq.empty)
    assert(effects.isEmpty)
  }

  test("execution control owns one bounded, cancellable dispatch budget") {
    val cancellation = CancellationToken.Source()
    val limits       = ExecutionLimits.create(2, 1).toOption.get
    val control      = ExecutionControl.create(limits, cancellation)
    assertEquals(control.checkpoint(), Right(()))
    assertEquals(control.checkpoint(), Right(()))
    assertEquals(control.checkpoint(), Left(ExecutionFault.BudgetExceeded(2)))

    val cancelled = ExecutionControl.create(limits, cancellation)
    cancellation.cancel()
    assertEquals(cancelled.checkpoint(), Left(ExecutionFault.Cancelled))
  }

  test("execution profiles keep the safe default unless explicitly configured") {
    assertEquals(ExecutionLimits.fromEnvironment(Map.empty), Right(ExecutionLimits.Default))
    assertEquals(
      ExecutionLimits.fromEnvironment(
        Map(
          ExecutionLimits.MaxInstructionsEnvironment -> "2000000000",
          ExecutionLimits.QuantumEnvironment         -> "8192"
        )
      ),
      Right(ExecutionLimits(maxInstructions = 2000000000L, quantum = 8192))
    )
  }

  test("execution profile parsing fails closed for invalid values") {
    val invalidMax = ExecutionLimits.fromEnvironment(
      Map(ExecutionLimits.MaxInstructionsEnvironment -> "0")
    )
    val invalidQuantum = ExecutionLimits.fromEnvironment(
      Map(ExecutionLimits.QuantumEnvironment -> "not-a-number")
    )
    assert(invalidMax.isLeft)
    assert(invalidQuantum.isLeft)
  }

  test("concurrent instruction tickets preserve the exact budget") {
    val budget    = ExecutionBudget.fromLong(10000L, threadSafe = true).toOption.get
    val successes = java.util.concurrent.atomic.AtomicInteger()
    val start     = java.util.concurrent.CountDownLatch(1)
    val workers   = (0 until 8).map { _ =>
      Thread { () =>
        start.await()
        (0 until 2000).foreach { _ =>
          if (budget.consume().isRight) { val _ = successes.incrementAndGet() }
        }
      }
    }
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
    val budget = ExecutionBudget.fromLong(5L, threadSafe = true).toOption.get
    assertEquals(budget.consume(), Right(()))
    assertEquals(budget.consume(3), Right(()))
    assert(budget.consume(2).isLeft)
    assertEquals(budget.remaining, 1L)
    assertEquals(budget.consume(), Right(()))
    assert(budget.consume().isLeft)
    assertEquals(budget.used, 5L)
    val large = ExecutionBudget.fromLong(Int.MaxValue.toLong + 1L, threadSafe = true).toOption.get
    assertEquals(large.consume(Int.MaxValue), Right(()))
    assertEquals(large.consume(), Right(()))
    assert(large.consume().isLeft)
    assertEquals(large.remaining, 0L)
  }

}
