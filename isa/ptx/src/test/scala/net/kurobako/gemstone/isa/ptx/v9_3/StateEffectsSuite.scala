package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.{NonEmptyString, NonEmptyVec, NonNegativeInt, NonNegativeLong, PositiveInt, PositiveLong}

import Program.*
import VMExecution.*
import VMIR.*

import net.kurobako.gemstone.memory.ArrayCheckedMemory

final class StateEffectsSuite extends munit.FunSuite {
  test("an invalid memory write prevents staged register writes from committing") {
    val state       = VMState(1, VMLaneMask.all(1).toOption.get)
    val memory      = ArrayCheckedMemory.allocate(NonNegativeInt(4))
    val destination = RegisterName("%r0")

    state.effects.stageRegister(0, destination, 42L)
    state.effects.stageMemory(0, memory.allocation.start.offset(4L), BitWidth(32), 7L)

    assert(state.effects.commit(state, memory, Pc.Entry).isLeft)
    assertEquals(state.registers(0)(destination), 0L)
    assert(state.effects.isEmpty)
  }

  test("the reusable effect buffer grows and preserves register write order") {
    val state       = VMState(1, VMLaneMask.all(1).toOption.get)
    val memory      = ArrayCheckedMemory.allocate(NonNegativeInt(0))
    val destination = RegisterName("%r0")

    (0 until 129).foreach(value => state.effects.stageRegister(0, destination, value.toLong))

    assertEquals(state.effects.commit(state, memory, Pc.Entry), Right(()))
    assertEquals(state.registers(0)(destination), 128L)

    state.effects.stageRegister(0, destination, 200L)
    state.effects.clear()
    assertEquals(state.effects.commit(state, memory, Pc.Entry), Right(()))
    assertEquals(state.registers(0)(destination), 128L)
  }
}
