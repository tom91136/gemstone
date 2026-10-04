package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import VMIR.Instruction.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class MemoryBarrierStorageSuite extends munit.FunSuite {
  private val address = VMAddressSpace.SharedBase + 16L
  private val alias   = VMAddressSpace.SharedWindowBase + 16L
  // ptxas initial TRANS64 representation for one arrival; subsequent hardware phase bits remain opaque.
  private val initial = 0x7ffff800001ffffeL

  private def run(instructions: Vec[Instruction]): Either[VMFault, Long] = {
    val memory = ArrayCheckedMemory.allocate(4, GuestAddress(0x10000000000L))
    val body   = instructions ++ Vec(
      Store(Value.Immediate(memory.allocation.start.raw), Value.Predicate("%p0"), 32, MemoryAccess.Global(false)),
      Return
    )
    val kernel   = Kernel("stored_barrier", Vec.empty, ExecutableProgram(body.map(PredicatedInstruction(_, None))))
    val geometry = LaunchGeometry.forLaunch(Dimensions.One, Dimensions.One).toOption.get
    VMInterpreter.execute(kernel, geometry, Map.empty, memory).map(_ =>
      memory.readInt32(memory.allocation.start).toOption.get.toLong
    )
  }

  test("stored barrier initialization is shared by canonical and hardware-window aliases") {
    assertEquals(
      run(Vec(
        Store(Value.Immediate(address), Value.Immediate(initial), 64, MemoryAccess.Shared),
        MemoryBarrierLoad(Value.Immediate(alias)),
        MemoryBarrierArrive(None, Value.Immediate(address), Value.Immediate(1), Value.Immediate(0)),
        MemoryBarrierTest("%p0", Value.Immediate(alias), Value.Immediate(0), true)
      )),
      Right(1L)
    )
  }

  test("ordinary partial stores and shared atomics invalidate cached phases even when bits are unchanged") {
    for (
      overwrite <- Vec[Instruction](
        Store(Value.Immediate(alias + 4), Value.Immediate(initial >>> 32), 32, MemoryAccess.Shared),
        AtomicExchange("%r0", Value.Immediate(alias), Value.Immediate(initial), 64, AtomicSpace.Shared)
      )
    ) assertEquals(
      run(Vec(
        Store(Value.Immediate(address), Value.Immediate(initial), 64, MemoryAccess.Shared),
        MemoryBarrierLoad(Value.Immediate(address)),
        MemoryBarrierArrive(None, Value.Immediate(address), Value.Immediate(1), Value.Immediate(0)),
        overwrite,
        MemoryBarrierLoad(Value.Immediate(alias)),
        MemoryBarrierTest("%p0", Value.Immediate(address), Value.Immediate(0), true)
      )),
      Right(0L)
    )
  }

  test("explicit invalidation blocks resurrection until guest storage is rewritten") {
    val invalidate = Vec[Instruction](
      Store(Value.Immediate(address), Value.Immediate(initial), 64, MemoryAccess.Shared),
      MemoryBarrierLoad(Value.Immediate(address)),
      MemoryBarrierInvalidate(Value.Immediate(alias))
    )
    assert(run(invalidate :+ MemoryBarrierLoad(Value.Immediate(address))).isLeft)
    assertEquals(
      run(invalidate ++ Vec(
        Store(Value.Immediate(alias), Value.Immediate(initial), 64, MemoryAccess.Shared),
        MemoryBarrierLoad(Value.Immediate(address)),
        MemoryBarrierTest("%p0", Value.Immediate(alias), Value.Immediate(0), true)
      )),
      Right(0L)
    )
  }

  test("PTX operations do not implicitly initialize a stored SASS descriptor") {
    assert(run(Vec(
      Store(Value.Immediate(address), Value.Immediate(initial), 64, MemoryAccess.Shared),
      MemoryBarrierArrive(None, Value.Immediate(address), Value.Immediate(1), Value.Immediate(0))
    )).isLeft)
  }

  test("stored descriptors reject zero, reserved bits and inconsistent words") {
    for (bits <- Vec(0L, initial | 1L, initial ^ (1L << 32), 0x8000000000200000L))
      assert(VMMemoryBarrier.fromStoredInitialState(bits).isLeft, clues(bits))
    for (count <- Vec(1L, 3L, VMMemoryBarrier.MaximumCount)) {
      val q       = VMMemoryBarrier.MaximumCount + 1L - count
      val bits    = ((q << 11) << 32) | (q << 1)
      val barrier = VMMemoryBarrier.fromStoredInitialState(bits).toOption.get
      assertEquals(barrier.arrive(count, 0), Right(0L))
      assert(barrier.complete(0, parity = true))
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
          MemoryBarrierLoad(pointer),
          MemoryBarrierInit(pointer, Value.Immediate(1)),
          MemoryBarrierArrive(None, pointer, Value.Immediate(1), Value.Immediate(0)),
          MemoryBarrierTest("%p0", pointer, Value.Immediate(0), true),
          MemoryBarrierInvalidate(pointer)
        )
      ) assert(run(Vec(operation)).isLeft, clues(invalid, operation))
    }
  }
}
