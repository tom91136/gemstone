package net.kurobako.gemstone.isa.ptx.v9_3

import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

class StateEffectsSuite extends munit.FunSuite {
  test("an invalid memory write prevents staged register writes from committing") {
    val state                         = VMBlockState(1, VMLaneMask.all(1).toOption.get)
    val memory                        = ArrayCheckedMemory.allocate(4)
    val destination: NonEmpty[String] = "%r0"

    state.effects.stageRegister(0, destination, 42L)
    state.effects.stageMemory(0, memory.allocation.start.offset(4L), 32: Int where (Between[8, 64] & MultipleOf[8]), 7L)

    assert(state.effects.commit(state, memory, Pc.Entry).isLeft)
    assertEquals(state.registers(0)(destination), 0L)
    assert(state.effects.isEmpty)
  }

  test("the reusable effect buffer grows and preserves register write order") {
    val state                         = VMBlockState(1, VMLaneMask.all(1).toOption.get)
    val memory                        = ArrayCheckedMemory.allocate(0)
    val destination: NonEmpty[String] = "%r0"

    (0 until 129).foreach(value => state.effects.stageRegister(0, destination, value.toLong))

    assertEquals(state.effects.commit(state, memory, Pc.Entry), Right(()))
    assertEquals(state.registers(0)(destination), 128L)

    state.effects.stageRegister(0, destination, 200L)
    state.effects.clear()
    assertEquals(state.effects.commit(state, memory, Pc.Entry), Right(()))
    assertEquals(state.registers(0)(destination), 128L)
  }
}
