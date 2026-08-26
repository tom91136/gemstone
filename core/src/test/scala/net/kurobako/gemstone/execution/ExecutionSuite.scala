package net.kurobako.gemstone.execution

class ExecutionSuite extends munit.FunSuite {
  private enum TestWrite extends Write {
    case Value(value: Int)
  }

  test("an executable program is immutable and bounds checked") {
    val program = ExecutableProgram(Vector("first", "second"))
    assertEquals(program.at(Pc.Entry), Right("first"))
    assert(program.at(Pc.unsafe(2)).left.exists(_.isInstanceOf[ExecutionFault.InvalidProgram]))
  }

  test("lane masks handle wave64 without shifting by 64") {
    val mask = LaneMask.all(64).toOption.get
    assertEquals(mask.size, 64)
    assert(mask.contains(0))
    assert(mask.contains(63))
  }

  test("effects validate every write before committing any") {
    val effects = new Effects[TestWrite]
    val applied = Vector.newBuilder[Int]
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
    assertEquals(applied.result(), Vector.empty)
    assert(effects.isEmpty)
  }

  test("budgets and cancellation are explicit") {
    val budget = ExecutionBudget(2).toOption.get
    assertEquals(budget.consume(), Right(()))
    assertEquals(budget.consume(), Right(()))
    assertEquals(budget.consume(), Left(ExecutionFault.BudgetExceeded(2)))

    val cancellation = new CancellationToken.Source
    assert(!cancellation.isCancelled)
    cancellation.cancel()
    assert(cancellation.isCancelled)
  }
}
