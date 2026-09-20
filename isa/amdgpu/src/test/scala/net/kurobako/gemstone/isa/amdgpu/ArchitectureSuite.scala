package net.kurobako.gemstone.isa.amdgpu

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class ArchitectureSuite extends munit.FunSuite {
  private final case class GfxGeneration(name: String, family: Family, isa: ISA, target: Target)

  private val generations = Vector(
    GfxGeneration("gfx6", Family.GFX6, gfx6.isa, Target("gfx600")),
    GfxGeneration("gfx7", Family.GFX7, gfx7.isa, Target("gfx700")),
    GfxGeneration("gfx8", Family.GFX8, gfx8.isa, Target("gfx803")),
    GfxGeneration("gfx9", Family.GFX9, gfx9.isa, Target("gfx900")),
    GfxGeneration("gfx10", Family.GFX10, gfx10.isa, Target("gfx1010")),
    GfxGeneration("gfx11", Family.GFX11, gfx11.isa, Target("gfx1100")),
    GfxGeneration("gfx12", Family.GFX12, gfx12.isa, Target("gfx1200"))
  )

  generations.foreach { generation =>
    test(s"${generation.name} exposes family metadata and the common SOPP codec") {
      val instruction = Parser.parseInstruction("s_nop 0").toOption.get

      assertEquals(generation.target.family, generation.family)
      assertEquals(generation.isa.family, generation.family)
      assert(generation.isa.instructions.exists(_.opcode.value == "s_nop"))
      assert(generation.isa.instructions.forall(_.meta.supports(generation.family)))
      assertEquals(ISA.Printer.render(instruction), "s_nop 0")
      val encoded = generation.isa.encodeSOPP(instruction, generation.target)
      assert(encoded.isRight, s"${generation.name} did not encode s_nop: $encoded")
      assertEquals(encoded.flatMap(generation.isa.decodeSOPP(_, generation.target)), Right(instruction))

      val end  = Parser.parseInstruction("s_endpgm").toOption.get
      val code = encoded.toOption.get ++ generation.isa.encodeSOPP(end, generation.target).toOption.get
      assertEquals(DispatchInterpreter.validate(code, generation.target), Right(()))
    }
  }

  test("gfx10 retains its rdna1 and rdna2 metadata subfamilies") {
    assertEquals(
      gfx10.isa.instructions.flatMap(
        _.meta.encodings
      ).filter(_.family == Family.GFX10).map(_.target).distinct.sortBy(_.value).map(_.value).toSeq,
      Seq("rdna1", "rdna2")
    )
  }
}
