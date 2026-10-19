package net.kurobako.gemstone.isa.nvidia

class MBarrierSuite extends munit.FunSuite {
  import MBarrier.*

  private def pendingCount(token: Long): Long = MaximumCount + 1L - ((token >>> 43) & MaximumCount)

  test("arrival tokens remain incomplete until both arrivals and transactions complete") {
    val barrier = create(2).toOption.get
    val first   = barrier.arrive(count = 1, expectedBytes = 32).toOption.get
    assert(!barrier.complete(tokenParity(first)))
    assertEquals(barrier.completeBytes(16L), Right(()))
    val second = barrier.arrive(count = 1, expectedBytes = 0).toOption.get
    assertEquals(tokenParity(second), tokenParity(first))
    assert(!barrier.complete(tokenParity(first)))
    assertEquals(barrier.completeBytes(16L), Right(()))
    assert(barrier.complete(tokenParity(first)))
    assert(barrier.complete(0))
    val third = barrier.arrive(count = 2, expectedBytes = 0).toOption.get
    assertEquals(tokenParity(third), 1L)
    assert(barrier.complete(tokenParity(third)))
    assert(barrier.complete(1))
    assert(!barrier.complete(0))
  }

  // ptxas decodes mbarrier.pending_count from bits 43..62 of the token as 2^20 minus the outstanding arrivals.
  test("tokens hold the TRANS64 state before the arrival") {
    val barrier = create(3).toOption.get
    val q       = MaximumCount + 1L - 3L
    assertEquals(barrier.state, (q << 43) | (q << 1))
    val first = barrier.arrive(count = 1, expectedBytes = 0).toOption.get
    assertEquals(first, (q << 43) | (q << 1))
    assertEquals(pendingCount(first), 3L)
    val second = barrier.arrive(count = 2, expectedBytes = 0).toOption.get
    assertEquals(pendingCount(second), 2L)
    assertEquals(tokenParity(barrier.state), 1L)
    assertEquals(pendingCount(barrier.state), 3L)
  }

  test("transactions can finish before their expected count is announced") {
    val barrier = create(1).toOption.get
    assertEquals(barrier.completeBytes(64L), Right(()))
    assert(!barrier.complete(0))
    assertEquals(barrier.arrive(count = 1, expectedBytes = 64).map(tokenParity), Right(0L))
    assert(barrier.complete(0))
  }

  test("expected transactions hold the phase open until completed") {
    val barrier = create(1).toOption.get
    assertEquals(barrier.expectBytes(32L), Right(()))
    assert(barrier.arrive(count = 1, expectedBytes = 0).isRight)
    assert(!barrier.complete(0))
    assertEquals(barrier.completeBytes(32L), Right(()))
    assert(barrier.complete(0))
  }

  test("dropped arrivals complete the phase and lower later phases' counts") {
    val barrier = create(2).toOption.get
    assert(barrier.arrive(count = 1, expectedBytes = 0, drop = true).isRight)
    assert(barrier.arrive(count = 1, expectedBytes = 0).isRight)
    assert(barrier.complete(0))
    assertEquals(pendingCount(barrier.state), 1L)
    assert(barrier.arrive(count = 1, expectedBytes = 0).isRight)
    assert(barrier.complete(1))
  }

  test("arrivals without completion may not finish the phase") {
    val barrier = create(2).toOption.get
    assert(barrier.arrive(count = 2, expectedBytes = 0, complete = false).isLeft)
    assert(barrier.arrive(count = 1, expectedBytes = 0, complete = false).isRight)
    assert(!barrier.complete(0))
  }

  test("invalid counts do not corrupt an existing phase") {
    assert(create(0).isLeft)
    assert(create(1L << 20).isLeft)
    assert(transactionBytes(-1).isLeft)
    val barrier = create(1).toOption.get
    assert(barrier.arrive(count = 2, expectedBytes = 0).isLeft)
    assert(barrier.arrive(count = 0, expectedBytes = 0).isLeft)
    assert(barrier.arrive(count = 1, expectedBytes = -1).isLeft)
    assert(barrier.arrive(count = 1, expectedBytes = 0).isRight)
    assert(barrier.complete(0))
  }
}
