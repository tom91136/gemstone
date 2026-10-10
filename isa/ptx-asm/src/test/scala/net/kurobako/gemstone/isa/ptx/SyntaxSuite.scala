package net.kurobako.gemstone.isa.ptx

import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.ptx.asm.*

class SyntaxSuite extends munit.FunSuite {
  test("empty line comments are valid trivia") {
    assert(Parser.parse("//\n.version 9.2;").isRight)
  }

  test("entry launch directives parse between parameters and body") {
    assert(Parser.parse(".visible .entry kernel() .maxntid 256, 1, 1 { ret; }").isRight)
  }

  test("external function declarations parse without a body") {
    assert(Parser.parse(".extern .func __assertfail(.param .b64 message);\n").isRight)
  }

  test("weak entry declarations parse") {
    assert(Parser.parse(".weak .entry kernel() { ret; }\n").isRight)
    val parameterised =
      ".weak .entry kernel(\n" +
        ".param .align 8 .b8 kernel_param_0[24],\n" +
        ".param .u64 kernel_param_1\n" +
        ");\n"
    assert(Parser.parse(parameterised).isRight, Parser.parse(parameterised).left.toString)
  }

  test("nested instruction blocks parse") {
    assert(Parser.parse(".reg .b16 c;").isRight, Parser.parse(".reg .b16 c;").left.toString)
    assert(Parser.parse("cvt.rn.bf16.f32 %rs13, %f17;").isRight)
    assert(Parser.parse("{ cvt.rn.bf16.f32 %rs13, %f17; }").isRight)
    assert(Parser.parse("mov.b16 c, 0x3f80U;").isRight)
    assert(Parser.parse("fma.rn.bf16 %rs17, %rs13, c, %rs16;").isRight)
    assert(
      Parser.parse("{ .reg .b16 c; mov.b16 c, 0x3f80U; fma.rn.bf16 %rs17, %rs13, c, %rs16; }").isRight,
      Parser.parse("{ .reg .b16 c; mov.b16 c, 0x3f80U; fma.rn.bf16 %rs17, %rs13, c, %rs16; }").left.toString
    )
    val source =
      ".visible .func f() { { cvt.rn.bf16.f32 %rs13, %f17; } { .reg .b16 c; mov.b16 c, 0x3f80U; " +
        "fma.rn.bf16 %rs17, %rs13, c, %rs16; } ret; }"
    assert(Parser.parse(source).isRight, Parser.parse(source).left.toString)
  }

  private def statement(source: String): Statement =
    Parser.parseStatement(source) match {
      case Right(value) => value
      case Left(error)  => fail(s"failed to parse '$source': $error")
    }

  test("instruction operands retain modifiers, registers, vectors, and addresses") {
    statement("@p ld.global.nc.L1::evict_last.v2.f64 {%f0,%f1}, [%rd3+8];") match {
      case Statement.Instruction(
            instruction @ Instruction.LdGlobalNc(destination, address, None, modifiers, Some(predicate))
          ) =>
        assertEquals(predicate.negated, false)
        assertEquals(instruction.opcode, "ld.global.nc")
        assertEquals(modifiers.map(_.name), Vec("L1::evict_last", "v2", "f64"))
        assert(Operand.isVectorLiteral(destination))
        assert(Operand.isAddress(address))
      case other => fail(s"unexpected statement: $other")
    }
  }

  test("documented tcgen05 collector shorthand is canonicalised") {
    val parsed = statement(
      "tcgen05.mma.ws.cta_group::1.kind::i8.collector::b2:use [d], [a], b, idesc, p;"
    )
    parsed match {
      case Statement.Instruction(instruction) =>
        assert(instruction.mods.exists(_.name == "collector::b2::use"))
        assert(Printer.render(Program(Vec(parsed))).contains("collector::b2::use"))
      case other => fail(s"unexpected statement: $other")
    }
  }

  test("module declarations, entry parameters, labels, and instructions parse") {
    val entryFragments = Vec(
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
      case Statement.Entry(Linkage.Visible, name, parameters, Some(body), _) if name == "kernel" =>
        assertEquals(parameters.map(_.name), Vec("pointer"))
        assert(body.contains(Statement.Label("again")))
        assertEquals(body.collect { case Statement.Instruction(_) => 1 }.sum, 2)
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

  test("data declaration initialisers are accepted") {
    val sources = Vec(
      ".global table;",
      ".global .align;",
      ".global .align 4;",
      ".global .align 4 .b8;",
      ".global .align 4 .b8 table;",
      ".global .align 4 .b8 table[4];",
      ".global table = {1};",
      ".global .b8 table[4] = {1};",
      ".global .align 4 .b8 table = {1};",
      ".global .align 4 .b8 table[4] = {65, 144, 67, 60};"
    )
    sources.foreach(source => assert(Parser.parse(source).isRight, s"$source: ${Parser.parse(source).left}"))
  }

  test("data declarations retain structured storage, type, extent, and initialiser") {
    statement(".global .align 4 .b8 table[4] = {65, 144, 67, 60};") match {
      case Statement.Data(DataAddressSpace.Global, qualifiers, DataType.B8, name, Extent.Sized(count), initialiser)
          if name == "table" && count.raw == 4 =>
        assertEquals(qualifiers, Vec(DeclarationQualifier.Alignment(4)))
        assertEquals(initialiser.size, 4)
      case other => fail(s"unexpected declaration: $other")
    }
  }

  test("unsized external shared declarations retain structured storage") {
    statement(".extern .shared .align 16 .b8 shmem[];") match {
      case Statement.Data(DataAddressSpace.Shared, qualifiers, DataType.B8, name, Extent.Unsized, initialiser)
          if name == "shmem" =>
        assertEquals(
          qualifiers,
          Vec(
            DeclarationQualifier.Attribute("extern"),
            DeclarationQualifier.Alignment(16)
          )
        )
        assertEquals(initialiser, Vec.empty[Operand])
      case other => fail(s"unexpected declaration: $other")
    }
  }

  test("complex instruction forms round-trip to an identical AST") {
    val sources = Vec(
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
    assertEquals(error.position.line.raw, 1)
    assert(error.position.column.raw > 0)
  }
}
