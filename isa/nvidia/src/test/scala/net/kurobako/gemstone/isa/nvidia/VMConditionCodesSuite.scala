package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.Suite

class VMConditionCodesSuite extends Suite {
  import ConditionCodeReads.*

  test("ordinary carry writes supersede the two-bit IADD3 carry state") {
    val codes = VMConditionCodes()
    codes.writeThreeInputCarry(Some(2))
    codes.write(VMConditionCodes.Flag.Carry, Some(true))

    assertEquals(codes.read(VMConditionCodes.Flag.Carry), Some(true))
    assertEquals(codes.readThreeInputCarry, None)
  }

  test("clearing invalidates ordinary flags and the independent three-input carry") {
    val codes = VMConditionCodes()
    codes.write(VMConditionCodes.Flag.Zero, Some(true))
    codes.writeThreeInputCarry(Some(1))
    codes.clear()
    assertEquals(codes.read(VMConditionCodes.Flag.Zero), None)
    assertEquals(codes.readThreeInputCarry, None)
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
