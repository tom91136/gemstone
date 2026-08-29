package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

class BinarySuite extends munit.FunSuite {
  test("encode and decode the reference SOPP layout") {
    val instruction = Parser.parseInstruction("s_nop 0x1234").fold(error => fail(error.toString), identity)
    val target      = Target.unsafe("gfx1010")
    val encoded     = Binary.encodeSOPP(instruction, target)
    assert(encoded.exists(_.sameElements(Seq(0x34, 0x12, 0x80, 0xbf).map(_.toByte))))
    val decoded = encoded.flatMap(bytes => Binary.decodeSOPP(bytes, target))
    assertEquals(decoded.map(Printer.render), Right("s_nop 4660"))
  }

  test("reject a non-SOPP instruction instead of guessing an encoding") {
    val instruction = Parser.parseInstruction("v_add_f32 v0, v1, v2").fold(error => fail(error.toString), identity)
    assert(Binary.encodeSOPP(instruction, Target.unsafe("gfx1010")).isLeft)
  }

  test("encode the GFX11 SOPP opcode renumbering from official XML") {
    val instruction = Parser.parseInstruction("s_branch 0x1234").fold(error => fail(error.toString), identity)
    assert(
      Binary
        .encodeSOPP(instruction, Target.unsafe("gfx1100"))
        .exists(_.sameElements(Seq(0x34, 0x12, 0xa0, 0xbf).map(_.toByte)))
    )
  }
}
