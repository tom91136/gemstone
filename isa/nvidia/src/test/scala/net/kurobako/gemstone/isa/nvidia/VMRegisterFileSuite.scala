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
}
