package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import programs.*

class VMStateSuite extends munit.FunSuite {
  test("lane registers and predicates grow on demand and distinguish undefined predicates") {
    val lane = VMLane(0, 0)
    assertEquals(lane.register(r(6143)), 0L)
    lane.setRegister(r(6143), 42L)
    lane.setRegister(r(0), 7L)
    assertEquals(lane.register(r(6143)), 42L)
    assertEquals(lane.register(r(0)), 7L)
    assertEquals(lane.register(r(6142)), 0L)
    for (index <- Vec(0, 63, 64, 127, 1023)) {
      assertEquals(lane.predicate(p(index)), None)
      lane.setPredicate(p(index), index % 2 == 0)
      assertEquals(lane.predicate(p(index)), Some(index % 2 == 0))
    }
    lane.setPredicate(p(64), true)
    assertEquals(lane.predicate(p(64)), Some(true))
    assertEquals(lane.predicate(p(65)), None)
    lane.clear()
    assertEquals(lane.register(r(6143)), 0L)
    assertEquals(lane.predicate(p(64)), None)
  }

  test("a reused lane holds no register written above a smaller routine's count") {
    val pool   = VMFramePool(1)
    val larger = VMIR.Routine("larger", VMIR.ExecutableProgram(Vec.empty), 16, 0)
    val first  = pool.acquire(larger)
    first(0).setRegister(r(12), 7L)
    pool.release(first)
    val smaller = pool.acquire(VMIR.Routine("smaller", VMIR.ExecutableProgram(Vec.empty), 4, 0))
    smaller(0).setRegister(r(10), 5L)
    pool.release(smaller)
    val again = pool.acquire(larger)
    assertEquals(again(0).register(r(10)), 0L)
    assertEquals(again(0).register(r(12)), 0L)
  }

  test("a program counter outside the routine faults") {
    for ((instruction, pc) <- Vec(VMIR.Instruction.Branch(VMIR.Pc(2)) -> 2, VMIR.Instruction.Fence -> 1))
      assertEquals(
        execute(Vec(instruction), RegionMemory()),
        Left(Fault.Backend(s"program counter $pc is outside a 1-instruction program")),
        clues(instruction)
      )
  }

  test("parameter byte storage preserves overlap, growth, zero fill and clearing") {
    val lane      = VMLane(0, 0)
    val parameter = VMIR.Parameter(0)
    lane.writeParameter(parameter, 4, 32, 0x44332211L)
    assertEquals(lane.readParameter(parameter, 0, 8), 0x4433221100000000L)
    lane.writeParameter(parameter, 6, 32, 0x88776655L)
    assertEquals(lane.readParameter(parameter, 4, 8), 0x887766552211L)
    lane.writeParameter(parameter, 5, 16, 0xbbaaL)
    assertEquals(lane.readParameter(parameter, 4, 8), 0x887766bbaa11L)
    assertEquals(lane.readParameter(parameter, 16, 8), 0L)
    assertEquals(lane.readParameter(VMIR.Parameter(1), 0, 8), 0L)
    val _ = intercept[ArithmeticException](lane.writeParameter(parameter, Int.MaxValue, 64, 1L))
    lane.clear()
    assertEquals(lane.readParameter(parameter, 4, 8), 0L)
  }

  test("workgroup reset clears shared, local, convergence and lane state") {
    val workgroup = VMWorkgroup(1024)
    val pool      = VMFramePool(1024)
    val used      = pool.acquire(emptyRoutine)
    val last      = WorkgroupLane.fromIndex(1023)
    used(last).setRegister(r(0), 42L)
    used(last).setPredicate(p(0), true)
    used(last).writeParameter(VMIR.Parameter(0), 0, 32, 7L)
    used(last).conditionCodes.write(VMConditionCodes.Flag.Zero, Some(true))
    used(last).carry = true
    val _ = used(last).returnStack += 19L
    workgroup.writeLocal(last, 0L, 32, 11L)
    workgroup.writeShared(VMAddressSpace.SharedBase, 32, 13L)
    val token = workgroup.createConvergence(992, 1L)
    workgroup.reset()
    pool.reset()
    val lanes = pool.acquire(emptyRoutine)
    for (lane <- Vec[WorkgroupLane](0, 1023)) {
      assertEquals(lanes(lane).register(r(0)), 0L)
      assertEquals(lanes(lane).predicate(p(0)), None)
      assertEquals(lanes(lane).readParameter(VMIR.Parameter(0), 0, 4), 0L)
      assertEquals(lanes(lane).conditionCodes.read(VMConditionCodes.Flag.Zero), None)
      assert(!lanes(lane).carry)
      assert(lanes(lane).returnStack.isEmpty)
      assertEquals(workgroup.readLocal(lane, 0L, 32), 0L)
    }
    assertEquals(workgroup.readShared(VMAddressSpace.SharedBase, 32), 0L)
    assertEquals(workgroup.convergenceMask(992, token), 0L)
  }

  test("sparse shared and per-lane local addresses retain all address bits") {
    val workgroup = VMWorkgroup(2)
    val addresses = Vec(0L, 1L << 32, Long.MinValue)
    addresses.zipWithIndex.foreach { (address, index) =>
      workgroup.writeShared(address, 64, 11L + index)
      workgroup.writeLocal(0, address, 64, 21L + index)
      workgroup.writeLocal(1, address, 64, 31L + index)
    }
    addresses.zipWithIndex.foreach { (address, index) =>
      assertEquals(workgroup.readShared(address, 64), 11L + index)
      assertEquals(workgroup.readLocal(0, address, 64), 21L + index)
      assertEquals(workgroup.readLocal(1, address, 64), 31L + index)
    }
  }

  test("an invalid memory write prevents staged register writes from committing") {
    val workgroup = VMWorkgroup(1)
    val lanes     = frame(1)
    val effects   = VMWriteBuffer()
    val memory    = RegionMemory(AllocationId(1) -> 4)
    effects.stageRegister(0, r(0), 42L)
    effects.stageMemory(0, GuestAddress(RegionMemory.Base + 4L), 32, 7L)
    assert(effects.commit(lanes, workgroup, memory, VMIR.Pc.Entry).isLeft)
    assertEquals(lanes(0).register(r(0)), 0L)
    assertEquals(effects.commit(lanes, workgroup, memory, VMIR.Pc.Entry), Right(()))
  }

  test("the reusable effect buffer grows and preserves write order for the highest lane") {
    val workgroup = VMWorkgroup(1024)
    val lanes     = frame(1024)
    val effects   = VMWriteBuffer()
    val memory    = RegionMemory()
    (0 until 129).foreach(value => effects.stageRegister(1023, r(0), value.toLong))
    assertEquals(effects.commit(lanes, workgroup, memory, VMIR.Pc.Entry), Right(()))
    assertEquals(lanes(1023).register(r(0)), 128L)
    assertEquals(lanes(0).register(r(0)), 0L)
    effects.stageRegister(1023, r(0), 200L)
    effects.clear()
    assertEquals(effects.commit(lanes, workgroup, memory, VMIR.Pc.Entry), Right(()))
    assertEquals(lanes(1023).register(r(0)), 128L)
  }

  test("condition state is per lane and staged writes distinguish carry kinds") {
    val workgroup = VMWorkgroup(2)
    val lanes     = frame(2)
    val effects   = VMWriteBuffer()
    effects.stage(VMWrite.ConditionFlag(0, VMConditionCodes.Flag.Carry, Some(true)))
    effects.stage(VMWrite.AddThreeCarry(0, Some(2)))
    effects.stage(VMWrite.ConditionFlag(1, VMConditionCodes.Flag.Carry, Some(false)))
    assertEquals(effects.commit(lanes, workgroup, RegionMemory(), VMIR.Pc.Entry), Right(()))
    assertEquals(lanes(1).conditionCodes.read(VMConditionCodes.Flag.Carry), Some(false))
    assertEquals(lanes(0).conditionCodes.readThreeInputCarry.map(_.raw), Some(2))
    assertEquals(lanes(0).conditionCodes.read(VMConditionCodes.Flag.Carry), None)
    assertEquals(lanes(1).conditionCodes.readThreeInputCarry, None)
  }
}
