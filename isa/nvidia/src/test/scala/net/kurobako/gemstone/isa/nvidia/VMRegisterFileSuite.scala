package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.collection.*

class VMRegisterFileSuite extends munit.FunSuite {
  test("unwritten registers stay zero and clear preserves that for reused files") {
    val registers = VMRegisterFile()
    registers.clear()
    assertEquals(registers.getSlot(0), 0L)
    assertEquals(registers.getSlot(6143), 0L)
    registers("named") = 7L
    registers.updateSlot(0, 11L)
    registers.updateSlot(6143, 13L)
    assertEquals(registers("named"), 7L)
    assertEquals(registers.getSlot(0), 11L)
    assertEquals(registers.getSlot(6143), 13L)
    registers.clear()
    assertEquals(registers("named"), 0L)
    assertEquals(registers.getSlot(0), 0L)
    assertEquals(registers.getSlot(6143), 0L)
    registers.updateSlot(6143, 19L)
    assertEquals(registers.getSlot(6143), 19L)
  }

  test("numbered registers map to stable dense slots without requiring a percent prefix") {
    assertEquals(VMRegisterFile.indexRaw("%rd0"), 0)
    assertEquals(VMRegisterFile.indexRaw("rd0"), 0)
    assertEquals(VMRegisterFile.indexRaw("%rs1023"), 2047)
    assertEquals(VMRegisterFile.indexRaw("%r0"), 2048)
    assertEquals(VMRegisterFile.indexRaw("%fd0"), 3072)
    assertEquals(VMRegisterFile.indexRaw("%f1023"), 5119)
    assertEquals(VMRegisterFile.indexRaw("%p0"), 5120)
  }

  test("all canonical banks preserve their slots with optional prefixes and leading zeroes") {
    // The six register banks occupy disjoint ranges of 1024 slots.
    Vec("rd", "rs", "r", "fd", "f", "p").zipWithIndex.foreach { case (bank, bankIndex) =>
      (0 until 1024).foreach { index =>
        val expected = bankIndex * 1024 + index
        assertEquals(VMRegisterFile.indexRaw(s"$bank$index"), expected)
        assertEquals(VMRegisterFile.indexRaw(s"%$bank$index"), expected)
        assertEquals(VMRegisterFile.indexRaw(s"%${bank}000000000000000000$index"), expected)
      }
    }
  }

  test("non-numbered and out-of-range registers do not acquire dense slots") {
    Vec(
      "",
      "%",
      "%r",
      "%rd",
      "%rs",
      "%fd",
      "%f",
      "%p",
      "%r-1",
      "%r1024",
      "%r1x",
      "%named",
      "%x0",
      "%rD0",
      "%Rd0",
      "%F0",
      "%P0",
      "%r+1",
      "%r 1",
      "%r1.0",
      "%r١",
      "%r０",
      "%ｒ0",
      "%%r0",
      "%r999999999999999999999999"
    ).foreach(name =>
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
    val _ = intercept[IllegalArgumentException](registers.getSlotRaw(-1))
    intercept[IllegalArgumentException](registers.updateSlotRaw(6144, 1L))
  }

  test("register names stay case-sensitive across dense and named storage") {
    val registers = VMRegisterFile()
    registers("%r0") = 11L
    registers("%R0") = 17L
    assertEquals(registers("%r0"), 11L)
    assertEquals(registers("%R0"), 17L)
    registers.clear()
    assertEquals(registers("%r0"), 0L)
    assertEquals(registers("%R0"), 0L)
  }

}
