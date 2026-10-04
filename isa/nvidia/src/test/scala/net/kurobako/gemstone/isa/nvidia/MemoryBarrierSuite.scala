package net.kurobako.gemstone.isa.nvidia

class MemoryBarrierSuite extends munit.FunSuite {
  test("arrival tokens remain incomplete until both arrivals and transactions complete") {
    val barrier = VMMemoryBarrier.create(2).toOption.get
    val first   = barrier.arrive(1, 32).toOption.get
    assert(!barrier.complete(first, parity = false))
    assertEquals(barrier.completeBytes(16), Right(()))
    assertEquals(barrier.arrive(1, 0), Right(first))
    assert(!barrier.complete(first, parity = false))
    assertEquals(barrier.completeBytes(16), Right(()))
    assert(barrier.complete(first, parity = false))
    assert(barrier.complete(0, parity = true))
    val second = barrier.arrive(2, 0).toOption.get
    assert(barrier.complete(second, parity = false))
    assert(barrier.complete(1, parity = true))
    assert(!barrier.complete(0, parity = true))
  }

  test("transactions can finish before their expected count is announced") {
    val barrier = VMMemoryBarrier.create(1).toOption.get
    assertEquals(barrier.completeBytes(64), Right(()))
    assert(!barrier.complete(0, parity = true))
    assertEquals(barrier.arrive(1, 64), Right(0L))
    assert(barrier.complete(0, parity = true))
  }

  test("invalid counts do not corrupt an existing phase") {
    assert(VMMemoryBarrier.create(0).isLeft)
    assert(VMMemoryBarrier.create(1L << 20).isLeft)
    val barrier = VMMemoryBarrier.create(1).toOption.get
    assert(barrier.arrive(2, 0).isLeft)
    assert(barrier.arrive(0, 0).isLeft)
    assert(barrier.arrive(1, -1).isLeft)
    assert(barrier.completeBytes(-1).isLeft)
    assertEquals(barrier.arrive(1, 0), Right(0L))
    assert(barrier.complete(0, parity = true))
  }
}
