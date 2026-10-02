package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.isa.amdgpu.asm.*
import scala.compiletime.testing.typeChecks

class ISASuite extends munit.FunSuite {
  private val processors = Vector(
    ISA.GFX6    -> Target("gfx600"),
    ISA.GFX7    -> Target("gfx700"),
    ISA.GFX8    -> Target("gfx803"),
    ISA.GFX9    -> Target("gfx900"),
    ISA.RDNA1   -> Target("gfx1010"),
    ISA.RDNA2   -> Target("gfx1030"),
    ISA.RDNA3   -> Target("gfx1100"),
    ISA.RDNA3_5 -> Target("gfx1150"),
    ISA.RDNA4   -> Target("gfx1200"),
    ISA.CDNA1   -> Target("gfx908"),
    ISA.CDNA2   -> Target("gfx90a"),
    ISA.CDNA3   -> Target("gfx942"),
    ISA.CDNA4   -> Target("gfx950"),
    ISA.CDNA5   -> Target("gfx1250"),
    ISA.GFX11_7 -> Target("gfx1170")
  )

  processors.foreach { (isa, target) =>
    test(s"$isa selects its catalog and round-trips SOPP for $target") {
      assertEquals(target.isa, isa)
      assertEquals(Target.either(target.raw), Right(target))
      val instruction = Parser.parseInstruction("s_nop 0").toOption.get
      assert(isa.instructions.contains(instruction.definition))
      val encoded = Binary.encodeSOPP(instruction, target)
      assert(encoded.isRight, s"$isa did not encode s_nop: $encoded")
      assertEquals(encoded.flatMap(Binary.decodeSOPP(_, target)), Right(instruction))
      val end  = Parser.parseInstruction("s_endpgm").toOption.get
      val code = encoded.toOption.get ++ Binary.encodeSOPP(end, target).toOption.get
      assertEquals(VMCompiler.validate(code, target), Right(()))
    }
  }

  test("each ISA has catalog metadata, with a supported processor") {
    ISA.values.foreach { isa =>
      assert(isa.instructions.nonEmpty, isa.toString)
      assert(isa.instructions.forall(_.meta.supports(isa)))
    }
    assertEquals(processors.map(_._1).toSet, ISA.values.toSet)
  }

  test("RDNA1 and RDNA2 retain distinct instruction availability and opcode lookup") {
    val definition = Opcode.forOpcode("v_fma_legacy_f32").head
    assert(!ISA.RDNA1.instructions.contains(definition))
    assert(ISA.RDNA2.instructions.contains(definition))
    val encoding = Metadata.encodingsFor(definition, ISA.RDNA2).head
    assert(OpcodeIndex.definitions(encoding.format, encoding.opcode, Target("gfx1030")).contains(definition))
    assert(!OpcodeIndex.definitions(encoding.format, encoding.opcode, Target("gfx1010")).contains(definition))
  }

  test("literal and runtime target validation reject unlisted processor names") {
    assert(!typeChecks("""net.kurobako.gemstone.isa.amdgpu.Target("gfx1099")"""))
    assert(!typeChecks("""net.kurobako.gemstone.isa.amdgpu.Target("gfx1159")"""))
    assert(Target.either("gfx1099").isLeft)
    assert(Target.either("gfx1159").isLeft)
    assertEquals(Target("tahiti").isa, ISA.GFX6)
    assertEquals(Target("gfx1036").isa, ISA.RDNA2)
    assertEquals(Target("gfx1103").isa, ISA.RDNA3)
    assertEquals(Target("gfx1170").isa, ISA.GFX11_7)
    assertEquals(Target("gfx90c").isa, ISA.GFX9)
    assertEquals(Target("gfx1201").isa, ISA.RDNA4)
    assertEquals(Target.either("gfx90c").map(_.isa), Right(ISA.GFX9))
    assertEquals(Target.either("gfx1201").map(_.isa), Right(ISA.RDNA4))
  }
}
