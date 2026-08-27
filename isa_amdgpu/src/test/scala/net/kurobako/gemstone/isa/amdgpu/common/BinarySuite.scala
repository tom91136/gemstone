package net.kurobako.gemstone.isa.amdgpu.common

class BinarySuite extends munit.FunSuite {
  test("encode and decode the reference SOPP layout") {
    val instruction = Assembly.parseInstruction("s_nop 0x1234").fold(error => fail(error.toString), identity)
    val target      = Target.unsafe("gfx1010")
    val encoded     = Binary.encodeSOPP(instruction, target)
    assertEquals(encoded, Right(Vector(0x34, 0x12, 0x80, 0xbf).map(_.toByte)))
    val decoded = encoded.flatMap(bytes => Binary.decodeSOPP(bytes, target))
    assertEquals(decoded.map(Assembly.print), Right("s_nop 4660"))
  }

  test("reject a non-SOPP instruction instead of guessing an encoding") {
    val instruction = Assembly.parseInstruction("v_add_f32 v0, v1, v2").fold(error => fail(error.toString), identity)
    assert(Binary.encodeSOPP(instruction, Target.unsafe("gfx1010")).isLeft)
  }

  test("encode the GFX11 SOPP opcode renumbering from official XML") {
    val instruction = Assembly.parseInstruction("s_branch 0x1234").fold(error => fail(error.toString), identity)
    assertEquals(
      Binary.encodeSOPP(instruction, Target.unsafe("gfx1100")),
      Right(Vector(0x34, 0x12, 0xa0, 0xbf).map(_.toByte))
    )
  }
}
