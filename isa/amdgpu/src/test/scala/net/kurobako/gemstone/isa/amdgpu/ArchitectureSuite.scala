package net.kurobako.gemstone.isa.amdgpu

import Program.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class ArchitectureSuite extends munit.FunSuite {
  private final case class GfxGeneration(name: String, family: Family, isa: ISA, target: Target)

  private val generations = Vector(
    GfxGeneration("gfx6", Family.GFX6, Architectures.gfx6, Target("gfx600")),
    GfxGeneration("gfx7", Family.GFX7, Architectures.gfx7, Target("gfx700")),
    GfxGeneration("gfx8", Family.GFX8, Architectures.gfx8, Target("gfx803")),
    GfxGeneration("gfx9", Family.GFX9, Architectures.gfx9, Target("gfx900")),
    GfxGeneration("gfx10", Family.GFX10, Architectures.gfx10, Target("gfx1010")),
    GfxGeneration("gfx11", Family.GFX11, Architectures.gfx11, Target("gfx1100")),
    GfxGeneration("gfx12", Family.GFX12, Architectures.gfx12, Target("gfx1200"))
  )

  generations.foreach { generation =>
    test(s"${generation.name} exposes family metadata and the common SOPP codec") {
      val instruction = Parser.parseInstruction("s_nop 0").toOption.get

      assertEquals(generation.target.family, generation.family)
      assertEquals(generation.isa.family, generation.family)
      assert(generation.isa.instructions.exists(_.opcode == SOPPInstructionDefs.SNop.opcode))
      assert(generation.isa.instructions.forall(_.meta.supports(generation.family)))
      assertEquals(Printer.render(instruction), "s_nop 0")
      val encoded = generation.isa.encodeSOPP(instruction, generation.target)
      assert(encoded.isRight, s"${generation.name} did not encode s_nop: $encoded")
      assertEquals(encoded.flatMap(generation.isa.decodeSOPP(_, generation.target)), Right(instruction))

      val end  = Parser.parseInstruction("s_endpgm").toOption.get
      val code = encoded.toOption.get ++ generation.isa.encodeSOPP(end, generation.target).toOption.get
      assertEquals(VMCompiler.validate(code, generation.target), Right(()))
    }
  }

  test("gfx10 retains its rdna1 and rdna2 metadata subfamilies") {
    assertEquals(
      Architectures.gfx10.instructions.flatMap(
        _.meta.encodings
      ).filter(_.family == Family.GFX10).map(_.target).distinct.sortBy(_.value).map(_.value).toSeq,
      Seq("rdna1", "rdna2")
    )
  }

  test("processor targets have exact family and metadata classifications") {
    val rdna1 = Target("gfx1013")
    val rdna2 = Target("gfx1036")
    val rdna3 = Target("gfx1103")
    val cdna3 = Target("gfx942")

    assertEquals((rdna1.family, rdna1.metadataTarget.value), (Family.GFX10, "rdna1"))
    assertEquals((rdna2.family, rdna2.metadataTarget.value), (Family.GFX10, "rdna2"))
    assertEquals((rdna3.family, rdna3.metadataTarget.value), (Family.GFX11, "rdna3"))
    assertEquals((cdna3.family, cdna3.metadataTarget.value), (Family.CDNA3, "cdna3"))
    assert(Target.either("gfx1099").isLeft)
    assert(Target.either("gfx1159").isLeft)
  }
}
