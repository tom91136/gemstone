package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.testing.Suite

class EmbeddedDecoderSuite extends Suite {
  private val target = Target.parse("sm_89").fold(fail(_), identity)

  // cuobjdump: EXIT ; /* 0x000000000000794d */ /* 0x000fea0003800000 */
  private val exit = Vec[Byte](0x4d, 0x79, 0, 0, 0, 0, 0, 0, 0, 0, 0x80.toByte, 0x03, 0, 0xea.toByte, 0x0f, 0)

  test("the embedded decoder tables decode a known instruction") {
    val program = Disassembler(target).decode(exit).fold(error => fail(error.message), identity)
    val text    = Printer.print(program).fold(error => fail(error.message), identity)
    assert(text.linesIterator.exists(_.startsWith("EXIT;")), text)
  }

  test("assembled text decodes to the same instruction") {
    val bytes   = Assembler.assemble("IADD3 R7, R7, 0x1, RZ;", target).fold(fail(_), identity)
    val decoded = Disassembler(target).decode(Vec.unsafeWrapArray(bytes)).fold(error => fail(error.message), identity)
    val text    = decoded.statements.collect { case Program.Statement.Operation(instruction, _, _) =>
      Printer.print(instruction).fold(fail(_), identity)
    }
    assertEquals(text, Vec("IADD3 R7, R7, 0x1, RZ;"))
  }
}
