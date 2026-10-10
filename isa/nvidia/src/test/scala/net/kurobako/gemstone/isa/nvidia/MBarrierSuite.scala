package net.kurobako.gemstone.isa.nvidia

class MBarrierSuite extends munit.FunSuite {
  test("arrival tokens remain incomplete until both arrivals and transactions complete") {
    val barrier = MBarrier.create(2).toOption.get
    val first   = barrier.arrive(count = 1, expectedBytes = 32).toOption.get
    assert(!barrier.complete(first, parity = false))
    assertEquals(barrier.completeBytes(16L), Right(()))
    assertEquals(barrier.arrive(count = 1, expectedBytes = 0), Right(first))
    assert(!barrier.complete(first, parity = false))
    assertEquals(barrier.completeBytes(16L), Right(()))
    assert(barrier.complete(first, parity = false))
    assert(barrier.complete(0, parity = true))
    val second = barrier.arrive(count = 2, expectedBytes = 0).toOption.get
    assert(barrier.complete(second, parity = false))
    assert(barrier.complete(1, parity = true))
    assert(!barrier.complete(0, parity = true))
  }

  test("transactions can finish before their expected count is announced") {
    val barrier = MBarrier.create(1).toOption.get
    assertEquals(barrier.completeBytes(64L), Right(()))
    assert(!barrier.complete(0, parity = true))
    assertEquals(barrier.arrive(count = 1, expectedBytes = 64), Right(0L))
    assert(barrier.complete(0, parity = true))
  }

  test("invalid counts do not corrupt an existing phase") {
    assert(MBarrier.create(0).isLeft)
    assert(MBarrier.create(1L << 20).isLeft)
    assert(MBarrier.transactionBytes(-1).isLeft)
    val barrier = MBarrier.create(1).toOption.get
    assert(barrier.arrive(count = 2, expectedBytes = 0).isLeft)
    assert(barrier.arrive(count = 0, expectedBytes = 0).isLeft)
    assert(barrier.arrive(count = 1, expectedBytes = -1).isLeft)
    assertEquals(barrier.arrive(count = 1, expectedBytes = 0), Right(0L))
    assert(barrier.complete(0, parity = true))
  }
}
