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
}
