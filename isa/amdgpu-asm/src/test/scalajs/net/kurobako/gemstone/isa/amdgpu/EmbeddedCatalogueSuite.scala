package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

class EmbeddedCatalogueSuite extends munit.FunSuite {
  private val target = "gfx1030".refine[Target].fold(error => fail(error.raw), identity)

  // LLVM MC gfx1030: v_add_f32_e32 v0, v1, v2
  private val add = Vec[Byte](0x01, 0x05, 0x00, 0x06)

  test("the embedded catalogues decode and re-encode a known instruction") {
    val program = Disassembler(TargetMode(target)).decode(add).fold(error => fail(error.toString), identity)
    assertEquals(Printer.render(program).trim, "v_add_f32 v0, v1, v2")
    assertEquals(Assembler.encode(program, TargetMode(target)), Right(add))
  }

  test("assembly text encodes with the embedded layouts") {
    val program = Parser.parse("s_movk_i32 s5, 0xfffe").fold(error => fail(error.toString), identity)
    assertEquals(
      Assembler.encode(program, TargetMode(target)),
      Right(Vec[Byte](0xfe.toByte, 0xff.toByte, 0x05, 0xb0.toByte))
    )
  }
}
