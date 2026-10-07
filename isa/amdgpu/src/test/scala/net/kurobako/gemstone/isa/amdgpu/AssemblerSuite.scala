package net.kurobako.gemstone.isa.amdgpu

import assembly.*
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
    val expected = hex("04 00 82 bf  ff 03 80 be 78 56 34 12  00 00 80 bf  fb ff 82 bf  00 00 81 bf")
    assertEquals(assemble(source), Right(expected))
  }

  test("data expressions use labels, the current position and explicit padding") {
    assertEquals(
      assemble("start:\n.long end-start\n.short -1\n.balign 8, 170\nend:\n.byte end-., 255"),
      Right(hex("08 00 00 00 ff ff aa aa 00 ff"))
    )
    assertEquals(assemble(".equ length, 3\n.zero length\n.byte 7"), Right(hex("00 00 00 07")))
  }

  test("align uses byte alignment while p2align uses its exponent") {
    // LLVM MC gfx1030 emits a four-byte NOP to align the second instruction to byte 8.
    val expected = Right(hex("00 00 80 bf 00 00 80 bf 02"))
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
