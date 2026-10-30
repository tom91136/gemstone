package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import VMIR.Instruction.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import programs.*

class MBarrierStorageSuite extends Suite {
  private val address = VMAddressSpace.SharedBase + 16L
  private val alias   = VMAddressSpace.SharedWindowBase + 16L
  // ptxas initial TRANS64 representation for one arrival; subsequent hardware phase bits remain opaque.
  private val initial = 0x7ffff800001ffffeL

  private def run(instructions: Vec[Instruction]): Either[Fault, Long] = {
    val memory = RegionMemory(AllocationId(1) -> 4)
    val output = RegionMemory.Base
    val body   = instructions ++ Vec(Store(immediate(output), predicate(0), 32, MemoryAccess.Global(false)), Return)
    execute(body, memory).map(_ => memory.getInt(AllocationId(1), 0).toLong)
  }

  test("stored barrier initialisation is shared by canonical and hardware-window aliases") {
    assertEquals(
      run(Vec(
        Store(Value.Immediate(address), Value.Immediate(initial), 64, MemoryAccess.Shared),
        MBarrierLoad(Value.Immediate(alias)),
        MBarrierArrive(None, Value.Immediate(address), Value.Immediate(1), Value.Immediate(0)),
        MBarrierTest(p(0), Value.Immediate(alias), Value.Immediate(0), true)
      )),
      Right(1L)
    )
  }

  test("ordinary partial stores and shared atomics invalidate cached phases even when bits are unchanged") {
    for (
      overwrite <- Vec[Instruction](
        Store(Value.Immediate(alias + 4), Value.Immediate(initial >>> 32), 32, MemoryAccess.Shared),
        Atomic(
          Some(r(0)),
          Value.Immediate(alias),
          Value.Immediate(initial),
          64,
          MemorySpace.Shared,
          AtomicOperation.Exchange
        )
      )
    ) assertEquals(
      run(Vec(
        Store(Value.Immediate(address), Value.Immediate(initial), 64, MemoryAccess.Shared),
        MBarrierLoad(Value.Immediate(address)),
        MBarrierArrive(None, Value.Immediate(address), Value.Immediate(1), Value.Immediate(0)),
        overwrite,
        MBarrierLoad(Value.Immediate(alias)),
        MBarrierTest(p(0), Value.Immediate(address), Value.Immediate(0), true)
      )),
      Right(0L)
    )
  }

  test("explicit invalidation blocks resurrection until guest storage is rewritten") {
    val invalidate = Vec[Instruction](
      Store(Value.Immediate(address), Value.Immediate(initial), 64, MemoryAccess.Shared),
      MBarrierLoad(Value.Immediate(address)),
      MBarrierInvalidate(Value.Immediate(alias))
    )
    assert(run(invalidate :+ MBarrierLoad(Value.Immediate(address))).isLeft)
    assertEquals(
      run(invalidate ++ Vec(
        Store(Value.Immediate(alias), Value.Immediate(initial), 64, MemoryAccess.Shared),
        MBarrierLoad(Value.Immediate(address)),
        MBarrierTest(p(0), Value.Immediate(alias), Value.Immediate(0), true)
      )),
      Right(0L)
    )
  }

  test("PTX operations do not implicitly initialise a stored SASS descriptor") {
    assert(run(Vec(
      Store(Value.Immediate(address), Value.Immediate(initial), 64, MemoryAccess.Shared),
      MBarrierArrive(None, Value.Immediate(address), Value.Immediate(1), Value.Immediate(0))
    )).isLeft)
  }

  test("stored descriptors reject zero, reserved bits and inconsistent words") {
    for (bits <- Vec(0L, initial | 1L, initial ^ (1L << 32), 0x8000000000200000L))
      assert(MBarrier.fromStoredInitialState(bits).isLeft, clues(bits))
    for (count <- Vec(1L, 3L, MBarrier.MaximumCount)) {
      val q       = MBarrier.MaximumCount + 1L - count
      val bits    = ((q << 11) << 32) | (q << 1)
      val barrier = MBarrier.fromStoredInitialState(bits).toOption.get
      assertEquals(barrier.arrive(count, 0), Right(bits))
      assert(barrier.complete(0))
    }
  }

  test("all barrier entrypoints reject non-shared, unaligned and unsupported cluster addresses") {
    for (
      invalid <- Vec(
        0L,
        address + 1,
        VMAddressSpace.SharedLimit - 4,
        VMAddressSpace.SharedWindowBase + VMAddressSpace.SharedWindowSize
      )
    ) {
      val pointer = Value.Immediate(invalid)
      for (
        operation <- Vec[Instruction](
          MBarrierLoad(pointer),
          MBarrierInit(pointer, Value.Immediate(1)),
          MBarrierArrive(None, pointer, Value.Immediate(1), Value.Immediate(0)),
          MBarrierTest(p(0), pointer, Value.Immediate(0), true),
          MBarrierInvalidate(pointer)
        )
      ) assert(run(Vec(operation)).isLeft, clues(invalid, operation))
    }
  }
}
