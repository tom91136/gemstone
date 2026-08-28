package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

class ArchitectureSuite extends munit.FunSuite {
  test("version packages expose generated family-scoped metadata") {
    val instruction = gfx10.Parser.parseInstruction("s_nop 0").toOption.get

    assertEquals(gfx10.Version, common.Generation.GFX10)
    assertEquals(gfx10.Printer.render(instruction), "s_nop 0")
    assert(gfx10.Instructions.exists(_.opcode == "s_nop"))
    assert(gfx10.Instructions.forall(_.meta.supports(gfx10.Version)))
    assertEquals(
      gfx10.Instructions.flatMap(
        _.meta.encodings
      ).filter(_.generation == gfx10.Version).map(_.target).distinct.sorted.toSeq,
      Seq("rdna1", "rdna2")
    )
    assert(gfx10.ISA.encodeSOPP(instruction, gfx10.Target.unsafe("gfx1010")).isRight)
  }

  test("architectural families do not share version-dependent opcode records") {
    assertEquals(gfx9.Version, common.Generation.GFX9)
    assertEquals(gfx11.Version, common.Generation.GFX11)
    assert(gfx9.Instructions.forall(_.meta.supports(gfx9.Version)))
    assert(gfx11.Instructions.forall(_.meta.supports(gfx11.Version)))
  }

  test("architectural lowering rejects a target from another family") {
    val instruction = gfx10.Parser.parseInstruction("s_nop 0").toOption.get
    assertEquals(
      gfx10.ISA.lower(instruction, gfx11.Target.unsafe("gfx1100")),
      Left(gfx10.LoweringError.WrongGeneration(gfx11.Target.unsafe("gfx1100"), common.Generation.GFX10))
    )
  }
}
