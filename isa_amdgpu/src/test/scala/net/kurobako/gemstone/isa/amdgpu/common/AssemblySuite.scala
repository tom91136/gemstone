package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.Col

class AssemblySuite extends munit.FunSuite {
  private def roundTrip(source: String): Unit = {
    val first = Assembly.parseInstruction(source).fold(error => fail(error.toString), identity)
    val text  = Assembly.print(first)
    val next  = Assembly.parseInstruction(text).fold(error => fail(error.toString), identity)
    assertEquals(Assembly.print(next), text)
  }

  test("parse and canonically print typed operands") {
    val instruction =
      Assembly.parseInstruction("V_ADD_F32_e32 v0, |v1|, -v2 clamp").fold(error => fail(error.toString), identity)
    assertEquals(instruction.opcode, "v_add_f32")
    assertEquals(instruction.operands.size, 3)
    assertEquals(instruction.mods.toSeq, Seq(Modifier("e32"), Modifier("clamp")))
    assertEquals(Assembly.print(instruction), "v_add_f32 v0, |v1|, -v2 e32 clamp")
  }

  test("parse register ranges, literals, expressions, and named modifiers") {
    roundTrip("image_atomic_add v[4:5], v192, s[28:35] dmask:0x3 unorm glc")
    roundTrip("v_pk_fmac_f16_dpp v5, v1, v2 quad_perm:[0,1,2,3] row_mask:0xf bank_mask:0xf")
    roundTrip("s_mov_b32 flat_scratch_lo, -0x2a")
    roundTrip("v_pk_add_f32 v4, v8, v12 neg_lo:[1,0] neg_hi:[1,0]")
  }

  test("parse programs without retaining comments") {
    val source = ".text\nentry:\n  s_nop 0 // delay\n  s_endpgm"
    val parsed = Assembly.parse(source).fold(error => fail(error.toString), identity)
    assertEquals(Assembly.print(parsed), ".text\nentry:\ns_nop 0\ns_endpgm")
  }

  test("reject malformed delimiters and empty operands") {
    assert(Assembly.parseInstruction("v_add_f32 v0,, v1").isLeft)
    assert(Assembly.parseInstruction("v_add_f32 v[0:1, v1").isLeft)
    assert(Assembly.parseInstruction("1bad v0").isLeft)
    assert(Assembly.parseInstruction("gemstone_unknown v0").isLeft)
  }
}
