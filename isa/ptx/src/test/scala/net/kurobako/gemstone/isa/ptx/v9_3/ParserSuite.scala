package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class ParserSuite extends munit.FunSuite {
  private val modules = Vec(
    "// comment\n.version 9.3\n.target sm_100, texmode_independent\n.address_size 64",
    ".extern .func __assertfail(.param .b64 message);",
    ".weak .entry kernel(.param .align 8 .b8 value[24], .param .u64 pointer);",
    ".visible .entry kernel(.param .u64 pointer) .maxntid 256, 1, 1 { " +
      ".reg .b32 %r<2>; again: mov.u32 %r0, %tid.x; ret; }",
    ".global .align 4 .b8 table[4] = {65, 144, 67, 60};",
    ".extern .shared .align 16 .b8 shmem[];",
    "{ cvt.rn.bf16.f32 %rs13, %f17; mov.b16 c, 0x3f80U; fma.rn.bf16 %rs17, %rs13, c, %rs16; }",
    "@p ld.global.nc.L1::evict_last.v2.f64 {%f0,%f1}, [%rd3+8];",
    "tcgen05.mma.ws.cta_group::1.kind::i8.collector::b2:use [d], [a], b, idesc, p;",
    ".target sm_100, texmode_independent;\n.file 1 \"a\\\"b.ptx\";",
    "cp.async.bulk.tensor.2d.shared::cluster.global.mbarrier::complete_tx::bytes " +
      "[dst], [tmap, {x, y}], [mbar];",
    "shfl.sync.up.b32 Ry|p, -Rx, 0x1, 0x1f, 0xffffffff;"
  )

  private val statementKinds = Vec(
    Vec("Directive", "Directive", "Directive"),
    Vec("Function"),
    Vec("Entry"),
    Vec("Entry"),
    Vec("Data"),
    Vec("Data"),
    Vec("Block"),
    Vec("Instruction"),
    Vec("Instruction"),
    Vec("Directive", "Directive"),
    Vec("Instruction"),
    Vec("Instruction")
  )

  modules.zipWithIndex.foreach { case (source, index) =>
    test(s"parses representative module $index") {
      val parsed = Parser.parse(source).fold(error => fail(error.message), identity)
      assertEquals(parsed.statements.map(_.productPrefix), statementKinds(index))
      assert(Parser.parse(source + "\n/* unterminated").isLeft)
    }
  }

  test("parses a large generated module") {
    val source = StringBuilder(".version 8.0\n.target sm_80\n.address_size 64\n")
    (0 until 256).foreach { index =>
      source.append(s".visible .entry kernel_$index() { .reg .b32 %r<2>; mov.u32 %r0, %tid.x; ")
      (0 until 64).foreach(value => source.append(s"add.u32 %r1, %r0, ${value + 1}; "))
      source.append("ret; }\n")
    }
    val program = Parser.parse(source.result()).fold(error => fail(error.message), identity)
    assertEquals(program.statements.size, 259)
    val entries = program.statements.collect { case entry: Statement.Entry => entry }
    assertEquals(entries.map(_.name.raw), Vec.from((0 until 256).map(index => s"kernel_$index")))
    entries.foreach { entry =>
      assertEquals(entry.parameters, Vec.empty[Parameter])
      val instructions = entry.body.collect { case Statement.Instruction(value) => value }
      assertEquals(entry.body.size, 67, entry.name)
      assertEquals(instructions.size, 66, entry.name)
      assertEquals(
        instructions.head,
        Instruction.Mov(
          Operand.Register("%r0", None),
          Operand.Register("%tid.x", None),
          Vec(Modifier("u32")),
          None
        )
      )
      (0 until 64).foreach { index =>
        assertEquals(
          instructions(index + 1),
          Instruction.Add(
            Operand.Register("%r1", None),
            Operand.Register("%r0", None),
            Operand.Immediate((index + 1).toString.unsafe[NonEmpty[String]]),
            Vec(Modifier("u32")),
            None
          ),
          s"${entry.name}, add $index"
        )
      }
      assertEquals(instructions.last, Instruction.Ret(Vec.empty, None))
    }
    assert(Parser.parse(source.result() + "/* unterminated").isLeft)
  }
  test("lexer errors retain positions across lines and comments") {
    val comment      = ".version 9.3\n  /* unfinished"
    val commentError = Parser.parse(comment).swap.toOption.get
    assertEquals(commentError.offset.raw, comment.indexOf("/*"))
    assertEquals(commentError.line.raw, 2)
    assertEquals(commentError.column.raw, 3)

    val quoted     = ".version 9.3\n// ignored\n.file 1 \"unfinished"
    val quoteError = Parser.parse(quoted).swap.toOption.get
    assertEquals(quoteError.offset.raw, quoted.indexOf('"'))
    assertEquals(quoteError.line.raw, 3)
    assertEquals(quoteError.column.raw, 9)
  }

  test("diagnostics use original source positions including leading whitespace and trailing comments") {
    val unclosed     = "\n  /* unfinished"
    val commentError = Parser.parse(unclosed).swap.toOption.get
    assertEquals(commentError.offset.raw, 3)
    assertEquals(commentError.line.raw, 2)
    assertEquals(commentError.column.raw, 3)

    val source   = "\n{\n  ret;\n// still inside the block\n  "
    val endError = Parser.parse(source).swap.toOption.get
    assertEquals(endError.offset.raw, source.length)
    assertEquals(endError.line.raw, 5)
    assertEquals(endError.column.raw, 3)
    assertEquals(Parser.parse("\n // comment\n /* complete */ \n"), Right(Program(Vec.empty)))
  }

  test("NUL bytes are rejected at their original position even at source edges or inside comments") {
    val sources = Seq("\u0000ret;", "ret;\u0000", "\n// comment\u0000", ".file 1 \"a\u0000b\";")
    sources.foreach { source =>
      val error = Parser.parse(source).swap.toOption.getOrElse(fail("NUL source was accepted"))
      assertEquals(error.offset.raw, source.indexOf('\u0000'), clues(source))
    }
  }

  test("indexed operands preserve one index and reject missing or extra indices") {
    val parsed = Parser.parseStatement("mov.u32 %r0, table[4];").fold(error => fail(error.message), identity)
    parsed match {
      case Statement.Instruction(instruction) =>
        assertEquals(
          instruction.operands,
          Vec(
            Operand.Register("%r0", None),
            Operand.Index(Operand.Symbol("table"), Operand.Immediate("4"))
          )
        )
      case other => fail(s"expected an instruction, found $other")
    }
    Seq("table[]", "table[1,2]", "table[1,]", "table[1").foreach { operand =>
      assert(Parser.parseStatement(s"mov.u32 %r0, $operand;").isLeft, clues(operand))
    }
  }

}
