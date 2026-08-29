package net.kurobako.gemstone.isa.amdgpu

class ArchitectureSuite extends munit.FunSuite {
  test("architecture packages expose generated family-scoped metadata") {
    val instruction = Parser.parseInstruction("s_nop 0").toOption.get

    assertEquals(gfx10.family, Family.GFX10)
    assertEquals(Printer.render(instruction), "s_nop 0")
    assert(gfx10.isa.instructions.exists(_.opcode == "s_nop"))
    assert(gfx10.isa.instructions.forall(_.meta.supports(gfx10.family)))
    assertEquals(
      gfx10.isa.instructions.flatMap(
        _.meta.encodings
      ).filter(_.family == gfx10.family).map(_.target).distinct.sorted.toSeq,
      Seq("rdna1", "rdna2")
    )
    assert(gfx10.isa.encodeSOPP(instruction, Target.unsafe("gfx1010")).isRight)
  }

  test("architectural families do not share family-dependent opcode records") {
    assertEquals(gfx9.family, Family.GFX9)
    assertEquals(gfx11.family, Family.GFX11)
    assert(gfx9.isa.instructions.forall(_.meta.supports(gfx9.family)))
    assert(gfx11.isa.instructions.forall(_.meta.supports(gfx11.family)))
  }
}
