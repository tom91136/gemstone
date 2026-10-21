package net.kurobako.gemstone.isa.amdgpu

import assembly.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

/** GFX11 true16 operands, which upstream LLVM 22 `llc -mcpu=gfx1100` emits for half arithmetic (`v_add_f16_e32 v0.l,
  * v0.l, v0.h`): `.l` and `.h` name a VGPR's low and high halves, which the 32-bit encodings select by bit 7 of the
  * register number and VOP3 by OP_SEL. Bytes from ROCm 7.1.1 `llvm-mc -triple=amdgcn -mcpu=gfx1100 -mattr=+real-true16
  * -show-encoding`, but with the unused SRC2 of a promoted VOP2 encoded as the pinned LLVM oracle encodes it, inline 0
  * rather than v0.
  */
class True16SyntaxSuite extends munit.FunSuite {
  private val target = Target("gfx1100")

  for (
    (source, bytes) <- Seq(
      ("v_add_f16 v0.h, v1.l, v2.h", "01 05 01 65"),
      ("v_add_f16_e64 v0.h, v1.h, v2.l", "00 48 32 d5 01 05 02 02"),
      ("v_mov_b16 v0.h, v1.l", "01 39 00 7f"),
      ("v_cvt_f32_f16 v0, v1.h", "81 17 00 7e"),
      ("v_fma_f16 v0.l, v1.h, v2.l, v3.h", "00 28 48 d6 01 05 0e 04")
    )
  )
    test(s"$source assembles and round-trips") {
      val expected = hex(bytes)
      assertEquals(Binary.encodeStatement(parseStatement(source), TargetMode(target, 32)), Right(expected))
      val decoded =
        Binary.decodeStatementAt(expected, 0, TargetMode(target, 32)).fold(error => fail(error.toString), identity)
      val printed = Printer.render(Program(Vec(decoded.statement)))
      assertEquals(Binary.encodeStatement(parseStatement(printed), TargetMode(target, 32)), Right(expected), printed)
    }

  test("true16 halves reach only the first 128 VGPRs in 32-bit encodings, so others take VOP3") {
    assert(Binary.encodeStatement(parseStatement("v_add_f16_e32 v0.h, v128.l, v2.h"), TargetMode(target, 32)).isLeft)
    assertEquals(
      Binary.encodeStatement(parseStatement("v_add_f16 v0.h, v128.l, v2.h"), TargetMode(target, 32)),
      Right(hex("00 50 32 d5 80 05 02 02"))
    )
  }
}
