package net.kurobako.gemstone.isa.amdgpu

class ArchitectureSuite extends munit.FunSuite {
  test("version packages expose the same syntax vocabulary with family-scoped catalogs") {
    val instruction = gfx10.Parser.parseInstruction("s_nop 0").toOption.get

    assertEquals(gfx10.Isa.version, "gfx10")
    assertEquals(gfx10.Printer.render(instruction), "s_nop 0")
    assert(gfx10.Isa.catalog.lookup("s_nop").nonEmpty)
    assert(gfx10.Isa.catalog.instructions.forall(_.generation.number == "gfx10"))
    assertEquals(gfx10.Isa.catalog.instructions.map(_.target).distinct.sorted, Vector("rdna1", "rdna2"))
    assert(gfx10.Isa.encodeSopp(instruction, gfx10.Target.unsafe("gfx1010")).isRight)
  }

  test("architectural families do not share version-dependent opcode records") {
    assertEquals(gfx9.Isa.version, "gfx9")
    assertEquals(gfx11.Isa.version, "gfx11")
    assert(gfx9.Isa.catalog.instructions.forall(_.generation.number == "gfx9"))
    assert(gfx11.Isa.catalog.instructions.forall(_.generation.number == "gfx11"))
  }

  test("architectural lowering rejects a target from another family") {
    val instruction = gfx10.Parser.parseInstruction("s_nop 0").toOption.get
    assertEquals(
      gfx10.Isa.lower(instruction, gfx11.Target.unsafe("gfx1100")),
      Left(gfx10.LoweringError.WrongGeneration(gfx11.Target.unsafe("gfx1100"), common.Generation.Gfx10))
    )
  }
}
