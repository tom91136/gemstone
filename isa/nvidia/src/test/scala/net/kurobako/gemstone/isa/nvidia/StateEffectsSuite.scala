package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

class StateEffectsSuite extends munit.FunSuite {
  test("parameter buffer sizes and offsets retain their domain") {
    assert(scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def invalid(state: VMBlockState) = state.parameterBufferRange(0L, -1)
    """).nonEmpty)
    assertEquals(scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def range(state: VMBlockState): Option[(Array[Byte], Int >= 0)] =
        state.parameterBufferRange(0L, 1)
    """), Nil)
    assert(scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def invalid(state: VMBlockState) = state.readParameterBufferBytes(0L, -1)
    """).nonEmpty)
    assert(scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def invalid(state: VMBlockState) = state.allocateParameterBuffer(-1)
    """).nonEmpty)
    val state = VMBlockState(1, VMLaneMask.all(1).toOption.get)
    val base = state.allocateParameterBuffer(4)
    assertEquals(state.parameterBufferRange(base + 4, 0).map(_._2.raw), Some(4))
    assert(state.parameterBufferRange(base + 5, 0).isEmpty)
    assert(state.parameterBufferRange(base + 4, 1).isEmpty)
    assertEquals(state.readParameterBufferBytes(base, 4).map(_.length), Some(4))
  }

  test("an invalid memory write prevents staged register writes from committing") {
    val state                         = VMBlockState(1, VMLaneMask.all(1).toOption.get)
    val memory                        = ArrayCheckedMemory.allocate(4)
    val destination: NonEmpty[String] = "%r0"

    state.effects.stageRegister(0, destination, 42L)
    state.effects.stageMemory(0, memory.allocation.start.offset(4L), 32, 7L)

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
  test("effect buffer growth preserves the highest workgroup lane") {
    val active = VMLaneMask.one(1023).toOption.get
    val state  = VMBlockState(1024, active)
    val memory = ArrayCheckedMemory.allocate(0)
    active.foreachLane { lane =>
      (0 until 129).foreach(value => state.effects.stageRegister(lane, "%r0", value.toLong))
    }
    assertEquals(state.effects.commit(state, memory, Pc.Entry), Right(()))
    assertEquals(state.registers(1023)("%r0"), 128L)
    assertEquals(state.registers(0)("%r0"), 0L)
  }

}
