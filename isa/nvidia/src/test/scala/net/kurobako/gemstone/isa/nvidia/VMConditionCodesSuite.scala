package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

class VMConditionCodesSuite extends munit.FunSuite {
  test("condition state is per lane and staged writes distinguish carry kinds") {
    val state  = VMBlockState(2, VMLaneMask.all(2).toOption.get)
    val memory = ArrayCheckedMemory.allocate(0)

    state.effects.stage(VMWrite.ConditionFlag(0, VMConditionCodes.Flag.Carry, Some(true)))
    state.effects.stage(VMWrite.AddThreeCarry(0, Some(2)))
    state.effects.stage(VMWrite.ConditionFlag(1, VMConditionCodes.Flag.Carry, Some(false)))

    assertEquals(state.effects.commit(state, memory, Pc.Entry), Right(()))
    assertEquals(state.conditionCodes(1).read(VMConditionCodes.Flag.Carry), Some(false))
    assertEquals(state.conditionCodes(0).readThreeInputCarry, Some(2))
    assertEquals(state.conditionCodes(0).read(VMConditionCodes.Flag.Carry), None)
    assertEquals(state.conditionCodes(1).readThreeInputCarry, None)
  }

  test("ordinary carry writes supersede the two-bit IADD3 carry state") {
    val codes = VMConditionCodes()
    codes.writeThreeInputCarry(Some(2))
    codes.write(VMConditionCodes.Flag.Carry, Some(true))

    assertEquals(codes.read(VMConditionCodes.Flag.Carry), Some(true))
    assertEquals(codes.readThreeInputCarry, None)
  }

  test("reset invalidates ordinary flags and the independent three-input carry") {
    val state = VMBlockState(1, VMLaneMask.all(1).toOption.get)

    state.conditionCodes(0).write(VMConditionCodes.Flag.Zero, Some(true))
    state.conditionCodes(0).writeThreeInputCarry(Some(1))
    state.reset(VMLaneMask.all(1).toOption.get)

    assertEquals(state.conditionCodes(0).read(VMConditionCodes.Flag.Zero), None)
    assertEquals(state.conditionCodes(0).readThreeInputCarry, None)
  }

  test("flags independently retain false, true and undefined through overwrites") {
    val codes = VMConditionCodes()
    VMConditionCodes.Flag.values.foreach { flag =>
      assertEquals(codes.read(flag), None)
      codes.write(flag, Some(true))
    }
    codes.write(VMConditionCodes.Flag.Zero, Some(false))
    codes.write(VMConditionCodes.Flag.Sign, None)
    VMConditionCodes.Flag.values.foreach { flag =>
      val expected = flag match {
        case VMConditionCodes.Flag.Zero => Some(false)
        case VMConditionCodes.Flag.Sign => None
        case _                          => Some(true)
      }
      assertEquals(codes.read(flag), expected)
    }
    codes.clear()
    VMConditionCodes.Flag.values.foreach(flag => assertEquals(codes.read(flag), None))
    codes.write(VMConditionCodes.Flag.Sign, Some(false))
    assertEquals(codes.read(VMConditionCodes.Flag.Sign), Some(false))
  }

}
