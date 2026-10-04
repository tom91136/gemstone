package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

class StateEffectsSuite extends munit.FunSuite {
  test("parameter buffer sizes and offsets retain their domain") {
    assert(scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def invalid(state: VMBlockState) = state.writeParameterBytes(0, "value", -1, 32, 1L)
    """).nonEmpty)
    assert(scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def invalid(state: VMBlockState) = state.readParameterBytes(0, "value", -1, 4)
    """).nonEmpty)
    assert(scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def invalid(state: VMBlockState) = state.parameterBufferRange(0L, -1)
    """).nonEmpty)
    assertEquals(
      scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def range(state: VMBlockState): Option[(Array[Byte], Int >= 0)] =
        state.parameterBufferRange(0L, 1)
    """),
      Nil
    )
    assert(scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def invalid(state: VMBlockState) = state.readParameterBufferBytes(0L, -1)
    """).nonEmpty)
    assert(scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def invalid(state: VMBlockState) = state.allocateParameterBuffer(-1)
    """).nonEmpty)
    val state = VMBlockState(1, VMLaneMask.all(1).toOption.get)
    val base  = state.allocateParameterBuffer(4)
    assertEquals(state.parameterBufferRange(base + 4, 0).map(_._2.raw), Some(4))
    assert(state.parameterBufferRange(base + 5, 0).isEmpty)
    assert(state.parameterBufferRange(base + 4, 1).isEmpty)
    assertEquals(state.readParameterBufferBytes(base, 4).map(_.length), Some(4))
  }

  test("reset clears used lanes and permits previously inactive lanes to enter") {
    val state = VMBlockState(1024, VMLaneMask.ofLane(1023))
    state.registers(1023)("%r0") = 42L
    state.predicates(1023)("%p0") = true
    state.parameters(1023)("value") = 7L
    state.writeParameterBytes(1023, "value", 0, 32, 7L)
    state.writeLocal(1023, 0L, 32, 11L)
    state.conditionCodes(1023).write(VMConditionCodes.Flag.Zero, Some(true))
    state.carry(1023) = true
    state.sassReturns(1023) += 19
    state.reset(VMLaneMask.ofLane(0))
    for (lane <- Vec[WorkgroupLane](0, 1023)) {
      assertEquals(state.registers(lane)("%r0"), 0L)
      assertEquals(state.predicates(lane).get("%p0"), None)
      assert(state.parameters(lane).isEmpty)
      assertEquals(state.readParameterBytes(lane, "value", 0, 4), 0L)
      assertEquals(state.readLocal(lane, 0L, 32), 0L)
      assertEquals(state.conditionCodes(lane).read(VMConditionCodes.Flag.Zero), None)
      assert(!state.carry(lane.raw))
      assert(state.sassReturns(lane).isEmpty)
    }
    state.registers(0)("%r0") = 23L
    assertEquals(state.registers(1023)("%r0"), 0L)
    assertEquals(state.registers(0)("%r0"), 23L)
  }

  test("sparse shared and per-lane local addresses retain all address bits and reset independently") {
    val active    = VMLaneMask.all(2).toOption.get
    val state     = VMBlockState(2, active)
    val addresses = Vec(0L, 1L << 32, Long.MinValue)
    addresses.zipWithIndex.foreach { (address, index) =>
      state.writeShared(address, 64, 11L + index)
      state.writeLocal(0, address, 64, 21L + index)
      state.writeLocal(1, address, 64, 31L + index)
    }
    addresses.zipWithIndex.foreach { (address, index) =>
      assertEquals(state.readShared(address, 64), 11L + index)
      assertEquals(state.readLocal(0, address, 64), 21L + index)
      assertEquals(state.readLocal(1, address, 64), 31L + index)
    }
    state.reset(active)
    addresses.foreach { address =>
      assertEquals(state.readShared(address, 64), 0L)
      assertEquals(state.readLocal(0, address, 64), 0L)
      assertEquals(state.readLocal(1, address, 64), 0L)
    }
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
