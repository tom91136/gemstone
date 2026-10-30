package net.kurobako.gemstone.isa.ptx

import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.ptx.asm.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.text.*
import scala.collection.mutable

class ParserSuite extends Suite {
  private def roundTrip(source: String): Program = {
    val parsed = Parser.parse(source).fold(error => fail(error.toString), identity)
    assertEquals(Parser.parse(Printer.render(parsed)), Right(parsed))
    parsed
  }

  test("linkage, prototypes, and launch directives survive the first parse") {
    val source = ".extern .func external(.param .b64 p);\n" +
      ".weak .entry declared();\n" +
      ".visible .entry defined() .maxntid 32, 2, 1 .minnctapersm 2 { ret; }"
    val program = roundTrip(source)
    program.statements match {
      case Vec(
            Statement.Function(linkage = Linkage.Extern, name = "external", body = None),
            Statement.Entry(Linkage.Weak, "declared", _, None, _),
            Statement.Entry(Linkage.Visible, "defined", _, Some(_), directives)
          ) =>
        assertEquals(directives.map(_.path.raw), Vec(Vec("maxntid"), Vec("minnctapersm")))
        assertEquals(
          directives.head.arguments.map(_.operand),
          Vec(
            Operand.Immediate("32"),
            Operand.Immediate("2"),
            Operand.Immediate("1")
          )
        )
      case other => fail(s"lost declaration information: $other")
    }
    val entry = program.statements(1) match {
      case entry: Statement.Entry => entry
      case _                      => fail("expected entry declaration")
    }
    assert(VMCompiler.module(program).flatMap(VMCompiler.compile(_, entry.name, Map.empty)).isLeft)
  }

  test("scalar, unsized, and sized storage retain distinct syntax and initialiser values") {
    val program = roundTrip(".global .u32 scalar;\n.extern .shared .b8 dynamic[];\n.global .u32 fixed[2] = {1, 2};")
    val declarations = program.statements.collect { case value: Statement.Data => value }
    assertEquals(declarations.map(_.extent), Vec(Extent.Scalar, Extent.Unsized, Extent.Sized(2)))
    assertEquals(declarations.last.initialiser, Vec(Operand.Immediate("1"), Operand.Immediate("2")))
    val directive = roundTrip(".custom symbol = {1, 2};").statements.head
    directive match {
      case Statement.Directive(initialiser = initialiser) =>
        assertEquals(initialiser, Vec(Operand.VectorLiteral(Vec(Operand.Immediate("1"), Operand.Immediate("2")))))
      case other => fail(s"expected directive: $other")
    }
  }

  test("generic address initialisers preserve symbols and byte offsets") {
    val program = roundTrip(".global .u64 ptr[3] = {generic($LC0), generic($LC0)+4, $LC0-8};")
    val values  = program.statements.collect { case declaration: Statement.Data => declaration.initialiser }.head
    assertEquals(
      values,
      Vec(
        Operand.GenericAddress(Operand.Symbol("$LC0")),
        Operand.Add(Operand.GenericAddress(Operand.Symbol("$LC0")), Operand.Immediate("4")),
        Operand.Subtract(Operand.Symbol("$LC0"), Operand.Immediate("8"))
      )
    )
  }

  test("debug line directives do not consume the following instructions") {
    val source = ".file 1 \"kernel.cu\"\n.visible .entry kernel() {\n" +
      ".loc 1 42 7\nmov.u32 %r0, 1;\n.loc 1 43 0\nret;\n}"
    val program = roundTrip(source)
    program.statements match {
      case Vec(Statement.Directive(_, _, _, _), Statement.Entry(_, _, _, Some(body), _)) =>
        assertEquals(body.map(_.productPrefix), Vec("Directive", "Instruction", "Directive", "Instruction"))
        assertEquals(body.collect { case Statement.Instruction(value) => value }.size, 2)
      case other => fail(s"lost debug directives or instructions: $other")
    }
    val pointer = roundTrip(".extern .func f(.param .u64 .ptr .global .align 16 p);")
    assert(Printer.render(pointer).contains(".param .u64 .ptr .global .align 16 p"))
  }

  test("indirect-call prototypes retain labels, parameters, and ABI directives") {
    val program = roundTrip("Fproto: .callprototype (.param .b32 _) _ (.param .b8 _[]) .abi_preserve 4;")
    program.statements match {
      case Vec(Statement.CallPrototype("Fproto", returns, parameters, directives)) =>
        assertEquals(returns.head.elementType, DataType.B32)
        assertEquals(parameters.head.dimensions, Vec(None))
        assertEquals(directives.head.path.raw, Vec("abi_preserve"))
      case other => fail(s"lost call prototype: $other")
    }
  }

  test("comments survive normalisation without consuming instructions or quoted delimiters") {
    val source = "// .globl kernel\n.visible .entry kernel() {\n" +
      "/* block\ncomment */ .reg .u32 %r;\n" +
      "mov.u32 %r/* inline */, 1; // trailing\n" +
      ".pragma \"// not a comment /* either */\";\nret;\n// tail\n}\n// module tail"
    val program                                           = roundTrip(source)
    def comments(statements: Vec[Statement]): Vec[String] = statements.flatMap {
      case Statement.Comment(text)      => Vec(text.raw)
      case Statement.Entry(body = body) => body.toSeq.flatMap(comments(_)).toVec
      case Statement.Block(body)        => comments(body)
      case _                            => Vec.empty
    }
    assertEquals(
      comments(program.statements),
      Vec(
        "// .globl kernel",
        "/* block\ncomment */",
        "/* inline */",
        "// trailing",
        "// tail",
        "// module tail"
      )
    )
    assert(VMCompiler.compile(Printer.render(program)).isRight)
    val printed = Printer.render(program)
    assert(printed.contains("\"// not a comment /* either */\""))
  }

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
    Vec("Comment", "Directive", "Directive", "Directive"),
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
      assertEquals(Parser.parse(Printer.render(parsed)), Right(parsed))
      assert(Parser.parse(source + "\n/* unterminated").isLeft)
    }
  }

  test("parses a large generated module") {
    val source = mutable.StringBuilder(".version 8.0\n.target sm_80\n.address_size 64\n")
    (0 until 256).foreach { index =>
      source.append(s".visible .entry kernel_$index() { .reg .b32 %r<2>; mov.u32 %r0, %tid.x; ")
      (0 until 64).foreach(value => source.append(s"add.u32 %r1, %r0, ${value + 1}; "))
      source.append("ret; }\n")
    }
    val program = Parser.parse(source.result()).fold(error => fail(error.message), identity)
    assertEquals(program.statements.size, 259)
    val entries = program.statements.collect { case entry: Statement.Entry => entry }
    assertEquals(entries.map(_.name.raw), Vec.from((0 until 256).map(index => s"kernel_$index")))
    val r0   = Operand.Register("%r0", None)
    val r1   = Operand.Register("%r1", None)
    val tidX = Operand.Register("%tid.x", None)
    val move = Instruction(
      Form.Mov(r0, tidX),
      Vec(Modifier("u32".refine[NonEmpty[String]].fold(error => fail(error.raw), identity))),
      None
    )
    entries.foreach { entry =>
      assertEquals(entry.parameters, Vec.empty[Parameter])
      val instructions = entry.body.toSeq.flatten.collect { case Statement.Instruction(value) => value }
      assertEquals(entry.body.toSeq.flatten.size, 67, entry.name)
      assertEquals(instructions.size, 66, entry.name)
      assertEquals(instructions.head, move)
      (0 until 64).foreach { index =>
        assertEquals(
          instructions(index + 1),
          Instruction(
            Form.Add(
              r1,
              r0,
              Operand.Immediate(
                (index + 1).toString.refine[NonEmpty[String]].fold(error => fail(error.raw), identity)
              )
            ),
            Vec(Modifier("u32".refine[NonEmpty[String]].fold(error => fail(error.raw), identity))),
            None
          ),
          s"${entry.name}, add $index"
        )
      }
      assertEquals(instructions.last, Instruction(Form.Ret(), Vec.empty, None))
    }
    assert(Parser.parse(source.result() + "/* unterminated").isLeft)
  }
  test("lexer errors retain positions across lines and comments") {
    val comment      = ".version 9.3\n  /* unfinished"
    val commentError = Parser.parse(comment).swap.toOption.get
    assertEquals(commentError.position.line.raw, 2)
    assertEquals(commentError.position.column.raw, 3)

    val quoted     = ".version 9.3\n// ignored\n.file 1 \"unfinished"
    val quoteError = Parser.parse(quoted).swap.toOption.get
    assertEquals(quoteError.position.line.raw, 3)
    assertEquals(quoteError.position.column.raw, 9)
  }

  test("offset-based diagnostics retain UTF-16 columns and LF line boundaries") {
    val sources = Vec(
      "// \ud83d\ude00\r\n\t/* unfinished",
      ".file 1 \"\ud83d\ude00\"; /* unfinished",
      "// header\r\n{\r\n ret;\r\n\t",
      "// header\r\n\t\u0000"
    )
    sources.foreach { source =>
      val error  = Parser.parse(source).swap.toOption.getOrElse(fail("malformed source was accepted"))
      val offset = if (source.contains("/*")) source.indexOf("/*")
      else if (source.contains('\u0000')) source.indexOf('\u0000')
      else source.length
      assertEquals(error.position.line.raw, source.take(offset).count(_ == '\n') + 1)
      assertEquals(error.position.column.raw, offset - source.lastIndexOf('\n', offset - 1))
    }
  }

  test("word scanning distinguishes literal slashes, comments, and Unicode whitespace") {
    val source    = "mov.u32\u2003%r0/* comment */,\u2003value/path;"
    val statement = Parser.parseStatement(source).fold(error => fail(error.message), identity)
    statement match {
      case Statement.Instruction(instruction) =>
        assertEquals(instruction.operands, Vec(Operand.Register("%r0", None), Operand.Symbol("value/path")))
      case other => fail(s"expected instruction, found $other")
    }
  }

  test("line directives stop at LF inside comments and preserve adjacent instructions") {
    Vec("\n", "\r\n").foreach { newline =>
      val source  = s".file 1 \"kernel.cu\" /* comment${newline}continues */ ret;"
      val program = Parser.parse(source).fold(error => fail(error.message), identity)
      assertEquals(
        program.statements.collect { case Statement.Instruction(value) => value },
        Vec(Instruction(Form.Ret(), Vec.empty, None))
      )
      val directives = program.statements.collect { case value: Statement.Directive => value }
      assertEquals(
        directives.head.arguments.map(_.operand),
        Vec(Operand.Immediate("1"), Operand.StringLiteral("kernel.cu"))
      )
    }
  }

  test("diagnostics use original source positions including leading whitespace and trailing comments") {
    val unclosed     = "\n  /* unfinished"
    val commentError = Parser.parse(unclosed).swap.toOption.get
    assertEquals(commentError.position.line.raw, 2)
    assertEquals(commentError.position.column.raw, 3)

    val source   = "\n{\n  ret;\n// still inside the block\n  "
    val endError = Parser.parse(source).swap.toOption.get
    assertEquals(endError.position.line.raw, 5)
    assertEquals(endError.position.column.raw, 3)
    assertEquals(
      Parser.parse("\n // comment\n /* complete */ \n"),
      Right(Program(Vec(Statement.Comment("// comment"), Statement.Comment("/* complete */"))))
    )
  }

  test("NUL bytes are rejected at their original position even at source edges or inside comments") {
    val sources = Vec("\u0000ret;", "ret;\u0000", "\n// comment\u0000", ".file 1 \"a\u0000b\";")
    sources.foreach { source =>
      val error = Parser.parse(source).swap.toOption.getOrElse(fail("NUL source was accepted"))
      assertEquals(error.position, SourcePosition.at(source, source.indexOf('\u0000').unsafe[Int >= 0]), clues(source))
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
    Vec("table[]", "table[1,2]", "table[1,]", "table[1").foreach { operand =>
      assert(Parser.parseStatement(s"mov.u32 %r0, $operand;").isLeft, clues(operand))
    }
  }

}
