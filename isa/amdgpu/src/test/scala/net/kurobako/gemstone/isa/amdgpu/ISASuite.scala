package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
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
    ) assertEquals(VMIR.waveSize(target).raw, width)
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
    val encoding = Metadata.encodingsFor(definition, Target("gfx1030")).head
    assert(OpcodeIndex.definitions(encoding.format, encoding.opcode, Target("gfx1030")).contains(definition))
    assert(!OpcodeIndex.definitions(encoding.format, encoding.opcode, Target("gfx1010")).contains(definition))
  }

  test("SOPK literal instructions resolve through their base format") {
    val target     = Target("gfx803")
    val definition = Opcode.forOpcode("s_setreg_imm32_b32").head
    val encoding   = Metadata.encodingsFor(definition, target).find(_.format == EncodingFormat.SOPK_INST_LITERAL).get
    assertEquals(OpcodeIndex.resolve(EncodingFormat.SOPK, encoding.opcode, target), Right(definition))
  }

  test("gfx906 fmac assembly and disassembly respect processor availability") {
    val target = Target("gfx906")
    for (suffix <- Vector("e32", "e64")) {
      val instruction = Parser.parseInstruction(s"v_fmac_f32_$suffix v2, v3, v4").toOption.get
      val encoded     = Binary.encode(instruction, target).toOption.get
      val decoded     = Binary.decode(encoded, target).toOption.get
      assertEquals(Binary.encode(decoded, target), Right(encoded))
      for (unsupported <- Vector(Target("gfx9"), Target("gfx900"), Target("gfx904"), Target("gfx90c"))) {
        assert(Binary.encode(instruction, unsupported).isLeft, unsupported.name)
        assert(Binary.decode(encoded, unsupported).isLeft, unsupported.name)
      }
    }
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
