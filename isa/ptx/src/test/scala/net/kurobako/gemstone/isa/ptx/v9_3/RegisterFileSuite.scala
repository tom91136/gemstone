package net.kurobako.gemstone.isa.ptx.v9_3

final class RegisterFileSuite extends munit.FunSuite {
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
    Vector("", "%", "%r", "%r-1", "%r1024", "%r1x", "%named", "%x0").foreach(name =>
      assertEquals(VMRegisterFile.indexRaw(name), -1, name)
    )
  }

  test("compiled kernels size the dense register directory from numbered slots") {
    val namedOnly = VMCompiler.compile(""".version 8.0
.target sm_80
.address_size 64
.visible .entry named() { .reg .u32 %value; mov.u32 %value,1; ret; }""").toOption.get
    val numbered = VMCompiler.compile(""".version 8.0
.target sm_80
.address_size 64
.visible .entry numbered() { .reg .u32 %r<18>; mov.u32 %r17,1; ret; }""").toOption.get

    assertEquals(namedOnly.registerChunkCount, 1)
    assertEquals(numbered.registerChunkCount, 33, numbered.program.instructions.toString)
  }
}
