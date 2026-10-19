package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*

class ProgramTraversalSuite extends munit.FunSuite {
  private val program = Parser.parse(
    """.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .func helper() {
      |  .reg .b32 %r<2>;
      |  add.u32 %r1, %r0, 3;
      |  ret;
      |}
      |.visible .entry kernel() {
      |  .reg .b32 %r<2>;
      |  {
      |    add.u32 %r1, %r0, 3;
      |  }
      |  ret;
      |}
      |""".stripMargin
  ).fold(error => fail(error.message.raw), identity)

  private def adds(program: Program): Vec[String] = {
    val found = Vec.newBuilder[String]
    val _     = program.mapInstructions {
      case add @ Instruction(_: Form.Add, _, _) => found += asm.Printer.render(add).raw; add
      case other                                => other
    }
    found.result()
  }

  test("instructions in functions, entries and nested blocks are all reached") {
    val edited = program.mapInstructions {
      case add @ Instruction(Form.Add(dest, srcA, Operand.Immediate(_)), _, _) =>
        add.copy(form = Form.Add(dest, srcA, Operand.Immediate("5")))
      case other => other
    }
    assertEquals(adds(edited), Vec("add.u32 %r1, %r0, 5;", "add.u32 %r1, %r0, 5;"))
  }

  test("bodies are edited innermost first") {
    val visited = Vec.newBuilder[Int]
    val _       = program.mapBodies { body => visited += body.size; body }
    assertEquals(visited.result(), Vec(3, 1, 3))
  }
}
