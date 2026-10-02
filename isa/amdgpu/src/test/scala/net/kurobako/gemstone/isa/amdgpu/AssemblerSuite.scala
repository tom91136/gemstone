package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

class AssemblerSuite extends munit.FunSuite {
  private val target                   = Target("gfx1030")
  private def assemble(source: String) = Parser.parse(source).left.map(_.toString)
    .flatMap(Assembler.encode(_, target).left.map(_.toString))

  test("forward and backward branches account for literal words and instruction alignment") {
    val source =
      ".text\nentry:\ns_branch end\ns_mov_b32 s0, lit(0x12345678)\n.p2align 4\ns_branch entry\nend:\ns_endpgm"
    // SOPP branch offsets are signed words relative to the following instruction.
    val expected = Vec(
      0x04, 0x00, 0x82, 0xbf,
      0xff, 0x03, 0x80, 0xbe, 0x78, 0x56, 0x34, 0x12,
      0x00, 0x00, 0x80, 0xbf,
      0xfb, 0xff, 0x82, 0xbf,
      0x00, 0x00, 0x81, 0xbf
    ).map(_.toByte)
    assertEquals(assemble(source), Right(expected))
  }

  test("data expressions use labels, the current position and explicit padding") {
    assertEquals(
      assemble("start:\n.long end-start\n.short -1\n.balign 8, 170\nend:\n.byte end-., 255"),
      Right(Vec(8, 0, 0, 0, 255, 255, 170, 170, 0, 255).map(_.toByte))
    )
    assertEquals(assemble(".equ length, 3\n.zero length\n.byte 7"), Right(Vec[Byte](0, 0, 0, 7)))
  }

  test("align uses byte alignment while p2align uses its exponent") {
    // LLVM MC gfx1030 emits a four-byte NOP to align the second instruction to byte 8.
    val expected = Right(Vec(0, 0, 0x80, 0xbf, 0, 0, 0x80, 0xbf, 2).map(_.toByte))
    assertEquals(assemble("s_nop 0\n.align 8\n.byte 2"), expected)
    assertEquals(assemble("s_nop 0\n.p2align 3\n.byte 2"), expected)
  }

  test("invalid symbols, branch ranges and data widths fail without an output image") {
    Vec(
      "x:\nx:\ns_endpgm",
      "s_branch missing",
      "s_branch end\n.byte 0\nend:\ns_endpgm",
      "s_branch end\n.zero 131072\nend:\ns_endpgm",
      ".byte 256",
      ".short -32769",
      ".long missing",
      ".p2align 31",
      ".align 3",
      ".balign 3",
      ".word 1",
      ".balign 4, 256",
      ".data\n.long 1",
      ".equ x, 1\n.equ x, 2"
    ).foreach(source => assert(assemble(source).isLeft, source))
  }
}
