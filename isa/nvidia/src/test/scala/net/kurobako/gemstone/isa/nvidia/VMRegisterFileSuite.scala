package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.collection.*

class VMRegisterFileSuite extends munit.FunSuite {
  test("numbered registers map to stable dense slots without requiring a percent prefix") {
    assertEquals(VMRegisterFile.indexRaw("%rd0"), 0)
    assertEquals(VMRegisterFile.indexRaw("rd0"), 0)
    assertEquals(VMRegisterFile.indexRaw("%rs1023"), 2047)
    assertEquals(VMRegisterFile.indexRaw("%r0"), 2048)
    assertEquals(VMRegisterFile.indexRaw("%fd0"), 3072)
    assertEquals(VMRegisterFile.indexRaw("%f1023"), 5119)
    assertEquals(VMRegisterFile.indexRaw("%p0"), 5120)
  }

  test("non-numbered and out-of-range registers do not acquire dense slots") {
    Vec("", "%", "%r", "%r-1", "%r1024", "%r1x", "%named", "%x0").foreach(name =>
      assertEquals(VMRegisterFile.indexRaw(name), -1, name)
    )
  }
  test("valid architectural slots grow beyond the initial chunk allocation") {
    val registers = VMRegisterFile(1)
    registers.updateSlot(0, 17L)
    assertEquals(registers.getSlot(64), 0L)
    registers.updateSlot(64, 23L)
    registers.updateSlot(6143, 31L)
    assertEquals(registers.getSlot(0), 17L)
    assertEquals(registers.getSlot(64), 23L)
    assertEquals(registers.getSlot(6143), 31L)
    assertEquals(registers.get("%p1023"), 31L)
    registers.clear()
    assertEquals(registers.getSlot(0), 0L)
    assertEquals(registers.getSlot(64), 0L)
    assertEquals(registers.getSlot(6143), 0L)
    intercept[IllegalArgumentException](registers.getSlotRaw(-1))
    intercept[IllegalArgumentException](registers.updateSlotRaw(6144, 1L))
  }
}
