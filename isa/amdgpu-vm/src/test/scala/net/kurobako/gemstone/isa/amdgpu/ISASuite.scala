package net.kurobako.gemstone.isa.amdgpu

import assembly.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.testing.either.*
import scala.compiletime.testing.typeChecks

class ISASuite extends munit.FunSuite {
  test("default wave width follows processor features including CDNA5") {
    for (
      (target, width) <- Vec(
        Target("gfx900")  -> 64,
        Target("gfx90a")  -> 64,
        Target("gfx1001") -> 32,
        Target("gfx1036") -> 32,
        Target("gfx1200") -> 32,
        Target("gfx12")   -> 32,
        Target("gfx1250") -> 32,
        Target("gfx1251") -> 32
      )
    ) assertEquals(TargetDefaults.waveSize(target).raw, width)
  }

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

  test("each ISA catalogue round-trips SOPP and validates for a supported processor") {
    assertEquals(processors.map(_._1).toSet, ISA.values.toSet)
    processors.foreach { (isa, target) =>
      assertEquals(target.isa, isa)
      assertEquals(target.raw.refine[Target], Right(target))
      val instruction = parse("s_nop 0")
      val nop         = encoded(instruction, target)
      assertEquals(Binary.decode(nop, TargetMode(target)), Right(instruction))
      assertEquals(fixtures.validate(nop ++ encoded(parse("s_endpgm"), target), target), Right(()))
    }
  }

  test("RDNA1 and RDNA2 retain distinct instruction availability and opcode lookup") {
    val instruction = parse("v_fma_legacy_f32 v0, v1, v2, v3")
    assert(instruction.definition.encodings(Target("gfx1010")).isEmpty)
    val code = encoded(instruction, Target("gfx1030"))
    assertEquals(disassembled(code, Target("gfx1030")).definition, instruction.definition)
    assert(!Binary.decode(code, TargetMode(Target("gfx1010"))).exists(_.definition == instruction.definition))
  }

  test("SOPK literal instructions decode through their literal layout") {
    val target      = Target("gfx803")
    val instruction = parse("s_setreg_imm32_b32 hwreg(1, 0, 32), 0x1234")
    val decoded     =
      InstructionDecoder.decode(CodeStream(Vec.toArray(encoded(instruction, target)), TargetMode(target)), 0).value
    assertEquals(decoded.definition, instruction.definition)
    assertEquals(decoded.format, EncodingFormat.SOPK_INST_LITERAL)
    assertEquals(decoded.size.raw, 8)
    assertEquals(decoded.literal, Some(0x1234L))
  }

  test("gfx906 fmac assembly and disassembly respect processor availability") {
    val target = Target("gfx906")
    for (suffix <- Vector("e32", "e64")) {
      val instruction = parse(s"v_fmac_f32_$suffix v2, v3, v4")
      val code        = encoded(instruction, target)
      assertEquals(Binary.encode(disassembled(code, target), TargetMode(target)), Right(code))
      for (unsupported <- Vector(Target("gfx9"), Target("gfx900"), Target("gfx904"), Target("gfx90c"))) {
        assert(Binary.encode(instruction, TargetMode(unsupported)).isLeft, unsupported.name)
        assert(Binary.decode(code, TargetMode(unsupported)).isLeft, unsupported.name)
      }
    }
  }

  test("literal and runtime target validation reject unlisted processor names") {
    assert(!typeChecks("""net.kurobako.gemstone.isa.amdgpu.Target("gfx1099")"""))
    assert(!typeChecks("""net.kurobako.gemstone.isa.amdgpu.Target("gfx1159")"""))
    assert("gfx1099".refine[Target].isLeft)
    assert("gfx1159".refine[Target].isLeft)
    assertEquals(Target("tahiti").isa, ISA.GFX6)
    assertEquals(Target("gfx1036").isa, ISA.RDNA2)
    assertEquals(Target("gfx1103").isa, ISA.RDNA3)
    assertEquals(Target("gfx1170").isa, ISA.GFX11_7)
    assertEquals(Target("gfx90c").isa, ISA.GFX9)
    assertEquals(Target("gfx1201").isa, ISA.RDNA4)
    assertEquals("gfx90c".refine[Target].map(_.isa), Right(ISA.GFX9))
    assertEquals("gfx1201".refine[Target].map(_.isa), Right(ISA.RDNA4))
  }
}
