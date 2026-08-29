package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Col

final class SyntaxSuite extends munit.FunSuite {
  test("empty line comments are valid trivia") {
    assert(Parser.parse("//\n.version 9.2;").isRight)
  }

  test("entry launch directives parse between parameters and body") {
    assert(Parser.parse(".visible .entry kernel() .maxntid 256, 1, 1 { ret; }").isRight)
  }

  private def statement(source: String): Statement =
    Parser.parseStatement(source) match {
      case Right(value) => value
      case Left(error)  => fail(s"failed to parse '$source': $error")
    }

  test("version and instruction specifications are generated at package level") {
    assertEquals(Instruction.version, PTXVersion(9, 3))
    assert(Instruction.definitions.size >= 170)
  }

  test("instruction operands retain modifiers, registers, vectors, and addresses") {
    statement("@p ld.global.nc.L1::evict_last.v2.f64 {%f0,%f1}, [%rd3+8];") match {
      case Statement.Instruction(
            instruction @ Instruction.LdGlobalNc(destination, address, None, modifiers, Some(predicate))
          ) =>
        assertEquals(predicate.negated, false)
        assertEquals(instruction.opcode, "ld.global.nc")
        assertEquals(modifiers.map(_.name).toSeq, Seq("L1::evict_last", "v2", "f64"))
        assert(destination.isInstanceOf[Operand.VectorLiteral])
        assert(address.isInstanceOf[Operand.Address])
      case other => fail(s"unexpected statement: $other")
    }
  }

  test("module declarations, entry parameters, labels, and instructions parse") {
    val entryFragments = Col(
      ".visible .entry kernel() {}",
      ".visible .entry kernel(.param .u64 pointer) {}",
      ".visible .entry kernel(.param .u64 pointer) { .reg .b32 %r<2>; }",
      ".visible .entry kernel(.param .u64 pointer) { again: mov.u32 %r0, %tid.x; ret; }"
    )
    entryFragments.foreach { fragment =>
      Parser.parse(fragment) match {
        case Right(_)    => ()
        case Left(error) => fail(s"$fragment\n$error")
      }
    }
    val source =
      """|.version 9.3
         |.target sm_100
         |.address_size 64
         |.visible .entry kernel(.param .u64 pointer) {
         |  .reg .b32 %r<2>;
         |again:
         |  mov.u32 %r0, %tid.x;
         |  ret;
         |}
         |""".stripMargin
    val statements = Parser.parse(source) match {
      case Right(value) => value
      case Left(error)  => fail(error.toString)
    }
    assertEquals(statements.statements.size, 4)
    statements.statements.last match {
      case Statement.Entry(true, "kernel", parameters, body) =>
        assertEquals(parameters.map(_.name).toSeq, Seq("pointer"))
        assert(body.contains(Statement.Label("again")))
        assertEquals(body.collect { case _: Statement.Instruction => 1 }.sum, 2)
      case other => fail(s"unexpected statement: $other")
    }
  }

  test("directive comma separators and string escapes survive canonical round-trip") {
    val source  = ".target sm_100, texmode_independent;\n.file 1 \"a\\\"b.ptx\";"
    val first   = Parser.parse(source).toOption.get
    val printed = Printer.render(first)
    assertEquals(Parser.parse(printed).map(Printer.render), Right(printed))
    assert(printed.contains(", "))
    assert(printed.contains("a\\\"b.ptx"))
  }

  test("complex instruction forms round-trip to an identical AST") {
    val sources = Col(
      "@!p mbarrier.arrive.release.cta.shared::cta.b64 state, [addr];",
      "cp.async.bulk.tensor.2d.shared::cluster.global.mbarrier::complete_tx::bytes [dst], [tmap, {x, y}], [mbar];",
      "st.global.v4.u32 [%rd6+16], {%r1, %r2, %r3, %r4};",
      "shfl.sync.up.b32 Ry|p, -Rx, 0x1, 0x1f, 0xffffffff;"
    )
    sources.foreach { source =>
      val first   = Parser.parse(source).toOption.get
      val printed = Printer.render(first)
      assertEquals(Parser.parse(printed).map(Printer.render), Right(printed), printed)
    }
  }

  test("syntax errors report a source location") {
    val error = Parser.parse("add.u32 %r0, [;").left.toOption.get
    assertEquals(error.line, 1)
    assert(error.column > 0)
  }
}
