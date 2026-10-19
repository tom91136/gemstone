package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.ptx.asm.*

class TextSuite extends munit.FunSuite {
  test("PTX text parses, validates and prints stably") {
    val source = """.version 8.0
                   |.target sm_80
                   |.address_size 64
                   |.visible .entry kernel(.param .u64 input) {
                   |  .reg .u32 %r<2>;
                   |  mov.u32 %r0, %tid.x;
                   |  add.u32 %r1, %r0, 1;
                   |  ret;
                   |}
                   |""".stripMargin
    val program = Parser.parse(source).fold(error => fail(error.message), identity)
    assertEquals(ISA.validate(program), ValidationOutcome.Accepted)
    val printed = Printer.render(program)
    assertEquals(Parser.parse(printed).map(Printer.render(_)), Right(printed))
  }
}
