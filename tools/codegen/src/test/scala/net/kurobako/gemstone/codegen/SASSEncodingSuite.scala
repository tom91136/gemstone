package net.kurobako.gemstone.codegen

class SASSEncodingSuite extends munit.FunSuite {
  private def parse(source: String): SASSDescription.Description =
    SASSDescription.parse(source).fold(error => fail(error.message), identity)

  test("legacy lookup arguments and typed enum constants retain exact values") {
    val enums = Map("Predicate" -> Map("PT" -> BigInt(7)))
    assertEquals(SASSEncoding.parseExpression("`Predicate@PT", enums), SASSEncoding.Expr.Constant(7, relaxed = false))
    assertEquals(SASSEncoding.parseExpression("* `Predicate@PT", enums), SASSEncoding.Expr.Constant(7, relaxed = true))
    assertEquals(
      SASSEncoding.parseExpression("VarLatOperandEnc( dst_wr_sb )", enums, Set("VarLatOperandEnc")),
      SASSEncoding.Expr.Lookup(
        "VarLatOperandEnc",
        Vector(SASSEncoding.Expr.Reference("dst_wr_sb", None, relaxed = false))
      )
    )
    assertEquals(
      SASSEncoding.parseExpression("* IntSize ( srcfmt )", enums, Set("IntSize")),
      SASSEncoding.Expr.Relaxed(SASSEncoding.Expr.Lookup(
        "IntSize",
        Vector(SASSEncoding.Expr.Reference("srcfmt", None, relaxed = false))
      ))
    )
  }

  test("uses named FUNIT masks for exact assignment positions") {
    val source =
      """FUNIT uC
        |ENCODING WIDTH 128;
        |  BITS_8_7_0_dst '..........................................................................................................................X...X.'
        |CLASS "move"
        |FORMAT Register:Rd;
        |OPCODES
        |  MOV = 0b1_010;
        |ENCODING
        |  BITS_8_7_0_dst = Rd;
        |""".stripMargin
    val compiled = SASSEncoding.compile(parse(source))
    assertEquals(compiled.width, 128)
    assertEquals(compiled.forms.head.opcodes, Vector("MOV" -> BigInt(10)))
    assertEquals(compiled.forms.head.fields.map(_.positions), Vector(Vector(1, 5)))
    assertEquals(
      compiled.forms.head.fields.map(_.expression),
      Vector(SASSEncoding.Expr.Reference("Rd", None, relaxed = false))
    )
  }

  test("parses expression references, relaxation, scaling, and lookup calls") {
    import SASSEncoding.*
    assertEquals(SASSEncoding.parseExpression("*0x2a"), Expr.Constant(42, relaxed = true))
    assertEquals(SASSEncoding.parseExpression("* 7"), Expr.Constant(7, relaxed = true))
    assertEquals(SASSEncoding.parseExpression("Rd"), Expr.Reference("Rd", None, relaxed = false))
    assertEquals(SASSEncoding.parseExpression("Ra@negate"), Expr.Reference("Ra", Some("negate"), relaxed = false))
    assertEquals(
      SASSEncoding.parseExpression("sImm SCALE 4"),
      Expr.Scale(Expr.Reference("sImm", None, relaxed = false), 4)
    )
    assertEquals(
      SASSEncoding.parseExpression("tsPtrIdx MULTIPLY 4 SCALE 4"),
      Expr.Scale(Expr.Multiply(Expr.Reference("tsPtrIdx", None, relaxed = false), 4), 4)
    )
    assertEquals(
      SASSEncoding.parseExpression("IDENTICAL(Rb,Rc)"),
      Expr.Identity(
        Expr.Reference("Rb", None, relaxed = false),
        Expr.Reference("Rc", None, relaxed = false)
      )
    )
    assertEquals(
      SASSEncoding.parseExpression("TABLES_opex_0(batch_t, usched_info)"),
      Expr.Lookup(
        "TABLES_opex_0",
        Vector(
          Expr.Reference("batch_t", None, relaxed = false),
          Expr.Reference("usched_info", None, relaxed = false)
        )
      )
    )
    assertEquals(
      SASSEncoding.parseExpression("*TABLES_mem_2(sem,sco,0)"),
      Expr.Relaxed(Expr.Lookup(
        "TABLES_mem_2",
        Vector(
          Expr.Reference("sem", None, relaxed = false),
          Expr.Reference("sco", None, relaxed = false),
          Expr.Constant(0, relaxed = false)
        )
      ))
    )
    assertEquals(
      SASSEncoding.parseExpression("VarLatOperandEnc(src_rel_sb)"),
      Expr.Unsupported("VarLatOperandEnc(src_rel_sb)")
    )
  }

  test("parses named global lookup rows and preserves unsupported instructions") {
    val source =
      """FUNIT uC
        |ENCODING WIDTH 128;
        | BITS_test '........................................................................................................................XXXXXXXX'
        |TABLES_demo
        |0 0 -> 3
        |1 0 -> 7
        |;
        |CLASS "lookup"
        |FORMAT Register:Rd;
        |OPCODES
        | LOOK = 0x2;
        |ENCODING
        | BITS_test = TABLES_demo(Rd, 0);
        | !BITS_test;
        |""".stripMargin
    val compiled = SASSEncoding.compile(parse(source))
    assertEquals(
      compiled.tables,
      Vector(SASSEncoding.LookupTable(
        "TABLES_demo",
        Vector(Vector(BigInt(0), BigInt(0)) -> BigInt(3), Vector(BigInt(1), BigInt(0)) -> BigInt(7))
      ))
    )
    assertEquals(compiled.forms.head.fields.map(_.name), Vector("BITS_test", "!BITS_test"))
    assertEquals(
      compiled.forms.head.fields.head.expression,
      SASSEncoding.Expr.Lookup(
        "TABLES_demo",
        Vector(
          SASSEncoding.Expr.Reference("Rd", None, relaxed = false),
          SASSEncoding.Expr.Constant(0, relaxed = false)
        )
      )
    )
    assertEquals(compiled.forms.head.fields.last.expression, SASSEncoding.Expr.Constant(0, relaxed = true))
    assert(!compiled.forms.head.issues.exists(_.contains("unused-field directive")))
  }

  test("never silently drops an encoding expression or unresolved FUNIT mask") {
    val source =
      """FUNIT uC
        |ENCODING WIDTH 128;
        | BITS_known '........................................................................................................................XXXXXXXX'
        |CLASS "future"
        |FORMAT Register:Rd;
        |ENCODING
        | BITS_missing = convertFloatType(Rd, F16Imm);
        |""".stripMargin
    val form = SASSEncoding.compile(parse(source)).forms.head
    assertEquals(form.fields.head.positions, Vector.empty)
    assertEquals(form.fields.head.expression, SASSEncoding.Expr.Unsupported("convertFloatType(Rd, F16Imm)"))
    assertEquals(form.issues.size, 2)
  }

  test("trims whitespace around unused-field assignments before resolving their mask") {
    val mask   = "." * 80 + "XXXXXXXX"
    val source =
      s"""FUNIT uC
         |ENCODING WIDTH 88;
         | RegA '$mask'
         |CLASS "unused-register"
         |FORMAT Opcode;
         |OPCODES
         | UNUSED = 1;
         |ENCODING
         | !RegA ;
         |""".stripMargin
    val form = SASSEncoding.compile(parse(source)).forms.head
    assertEquals(form.fields.map(_.name), Vector("!RegA"))
    assertEquals(form.fields.last.positions, (0 until 8).toVector)
    assertEquals(form.fields.last.expression, SASSEncoding.Expr.Constant(0, relaxed = true))
    assertEquals(form.issues, Vector.empty)
  }

  test("reports unresolved lookup tables and argument arity mismatches") {
    val source =
      """FUNIT uC
        |ENCODING WIDTH 128;
        | BITS_test '........................................................................................................................XXXXXXXX'
        |TABLES_demo
        |0 0 -> 3
        |;
        |CLASS "future"
        |FORMAT Register:Rd;
        |ENCODING
        | BITS_test = TABLES_demo(Rd);
        |""".stripMargin
    val form = SASSEncoding.compile(parse(source)).forms.head
    assert(form.issues.exists(_.contains("expects 2 arguments, got 1")))
    assertEquals(
      SASSEncoding.parseExpression("TABLES_absent(Rd)"),
      SASSEncoding.Expr.Lookup(
        "TABLES_absent",
        Vector(SASSEncoding.Expr.Reference("Rd", None, relaxed = false))
      )
    )
  }

  test("preserves compound field assignments that do not fit the single-field model") {
    val source =
      """FUNIT uC
        |ENCODING WIDTH 128;
        | BITS_a '............................................................................................................................XXXX'
        | BITS_b '........................................................................................................................XXXX....'
        |CLASS "compound"
        |FORMAT Register:Ra;
        |ENCODING
        | BITS_a,BITS_b = FutureAddress(Ra, offset);
        |""".stripMargin
    val form = SASSEncoding.compile(parse(source)).forms.head
    assertEquals(form.fields.map(_.name), Vector("BITS_a,BITS_b"))
    assertEquals(form.fields.head.expression, SASSEncoding.Expr.Unsupported("FutureAddress(Ra, offset)"))
    assert(form.issues.exists(_.contains("unsupported compound or malformed assignment")))
  }

  test("expands verified constant-bank builtins into bank and scaled-address fields") {
    val source =
      """FUNIT uC
        |ENCODING WIDTH 128;
        | BITS_bank '............................................................................................................................XXXX'
        | BITS_addr '........................................................................................................................XXXXXXXX'
        | BITS_bank0 '............................................................................................................................XXXX'
        | BITS_addr0 '........................................................................................................................XXXXXXXX'
        |CLASS "constant-bank-two"
        |FORMAT Register:Ra;
        |ENCODING
        | BITS_bank,BITS_addr = ConstBankAddress2(Sb_bank,Sb_addr);
        |CLASS "constant-bank-zero"
        |FORMAT Register:Ra;
        |ENCODING
        | BITS_bank0,BITS_addr0 = ConstBankAddress0(Sa_bank,Ra_offset);
        |""".stripMargin
    val forms = SASSEncoding.compile(parse(source)).forms
    assertEquals(
      forms.map(_.fields.map(_.expression)),
      Vector(
        Vector(
          SASSEncoding.Expr.Reference("Sb_bank", None, relaxed = false),
          SASSEncoding.Expr.Scale(SASSEncoding.Expr.Reference("Sb_addr", None, relaxed = false), 4)
        ),
        Vector(
          SASSEncoding.Expr.Reference("Sa_bank", None, relaxed = false),
          SASSEncoding.Expr.Scale(SASSEncoding.Expr.Reference("Ra_offset", None, relaxed = false), 1)
        )
      )
    )
    assert(forms.forall(_.issues.isEmpty))
  }

  test("rejects missing or unsupported FUNIT widths instead of guessing") {
    val source =
      """FUNIT uC
        |ENCODING WIDTH 80;
        |CLASS "wrong-width"
        |FORMAT Register:Rd;
        |""".stripMargin
    val _ = intercept[IllegalArgumentException](SASSEncoding.compile(parse(source)))
    intercept[IllegalArgumentException](SASSEncoding.compile(parse("CLASS \"missing-width\"\nFORMAT Register:Rd;\n")))
  }

  test("supports all widths present in the pinned ISA descriptions") {
    Vector(64, 88, 128).foreach { width =>
      val mask   = "." * (width - 4) + "XXXX"
      val source =
        s"""FUNIT uC
           |ENCODING WIDTH $width;
           | BITS_test '$mask'
           |CLASS "width-$width"
           |FORMAT Register:Rd;
           |OPCODES
           | TEST = 1;
           |ENCODING
           | BITS_test = Rd;
           |""".stripMargin
      val compiled = SASSEncoding.compile(parse(source))
      assertEquals(compiled.width, width)
      assertEquals(compiled.forms.head.fields.head.positions, Vector(0, 1, 2, 3))
    }
  }

  test("compiles float immediate selectors into explicit enum-value cases") {
    import SASSEncoding.*
    val source =
      """FUNIT uC
        |ENCODING WIDTH 128;
        | BITS_f2f '........................................................................................................................XXXXXXXX'
        | BITS_hfma '........................................................................................................................XXXXXXXX'
        | BITS_mufu '........................................................................................................................XXXXXXXX'
        | DSTFMT_SRCFMT_F32F16_F32E6M9 "F32.F16"=10 , "F32.E6M9"=34;
        | OFMT "F16_V2"=0 , "F32"=1 , "E8M7_V2"=2 , "E6M9_V2"=3 , "BF16_V2"=2;
        | MUFU_OP "RCP"=4 , "RSQ"=5 , "RCP64H"=6 , "RSQ64H"=7;
        |CLASS "float-conversions"
        |FORMAT Register:Rd;
        |OPCODES
        | TEST = 1;
        |ENCODING
        | BITS_f2f = Sb convertFloatType(dstfmt.srcfmt == `DSTFMT_SRCFMT_F32F16_F32E6M9@F32.E6M9, E6M9Imm, F16Imm);
        | BITS_hfma = Sb convertFloatType(ofmt == `OFMT@E8M7_V2 || ofmt == `OFMT@BF16_V2, E8M7Imm, ofmt == `OFMT@E6M9_V2, E6M9Imm, F16Imm);
        | BITS_mufu = Sb convertFloatType(mufuop == `MUFU_OP@RCP64H || mufuop == `MUFU_OP@RSQ64H, F64Imm, F32Imm);
        |""".stripMargin
    val fields = SASSEncoding.compile(parse(source)).forms.head.fields
    assertEquals(
      fields.map(_.expression),
      Vector(
        Expr.FloatConvert(
          Expr.Reference("Sb", None, relaxed = false),
          "dstfmt.srcfmt",
          Vector(Vector(BigInt(34)) -> "E6M9Imm"),
          "F16Imm"
        ),
        Expr.FloatConvert(
          Expr.Reference("Sb", None, relaxed = false),
          "ofmt",
          Vector(Vector(BigInt(2)) -> "E8M7Imm", Vector(BigInt(3)) -> "E6M9Imm"),
          "F16Imm"
        ),
        Expr.FloatConvert(
          Expr.Reference("Sb", None, relaxed = false),
          "mufuop",
          Vector(Vector(BigInt(6), BigInt(7)) -> "F64Imm"),
          "F32Imm"
        )
      )
    )
    assert(fields.forall(_.positions.nonEmpty))
  }

  test("preserves compile-time float formats and relaxed lookup expressions") {
    import SASSEncoding.*
    val source =
      """FUNIT uC
        |ENCODING WIDTH 128;
        | BITS_float '........................................................................................................................XXXXXXXX'
        | BITS_mem '........................................................................................................................XXXXXXXX'
        |TABLES_mem_2
        |0 0 0 -> 4
        |;
        |CLASS "constant-float-format"
        |FORMAT Register:Sb;
        |OPCODES
        | TEST = 1;
        |ENCODING
        | BITS_float = Sb convertFloatType(1==1,E8M7Imm,E8M7Imm);
        | BITS_mem = *TABLES_mem_2(sem,sco,0);
        |""".stripMargin
    val form = SASSEncoding.compile(parse(source)).forms.head
    assertEquals(
      form.fields.map(_.expression),
      Vector(
        Expr.FixedFloat(Expr.Reference("Sb", None, relaxed = false), "E8M7Imm"),
        Expr.Relaxed(Expr.Lookup(
          "TABLES_mem_2",
          Vector(
            Expr.Reference("sem", None, relaxed = false),
            Expr.Reference("sco", None, relaxed = false),
            Expr.Constant(0, relaxed = false)
          )
        ))
      )
    )
    assertEquals(form.issues, Vector.empty)
  }

  test("resolves named tables, enum cells, and quoted character cells without losing bad rows") {
    val source =
      """FUNIT uC
        |ENCODING WIDTH 128;
        | BITS_test '........................................................................................................................XXXXXXXX'
        |TABLES
        | MODE "wide"=4 , "narrow"=2;
        | VarLatOperandEnc
        | 0 -> 0
        | 1 -> 1
        | 0xffff -> 7
        | ;
        | DemoMap
        | 'A' MODE@"wide" -> 9
        | - 0 - -> 6
        | Missing@Unknown -> 8
        | ;
        |CLASS "named-table"
        |FORMAT Register:Rd;
        |OPCODES
        | LOOK = 1;
        |ENCODING
        | BITS_test = DemoMap(65, 4);
        |""".stripMargin
    val compiled = SASSEncoding.compile(parse(source))
    assertEquals(
      compiled.tables.map(table => table.name -> table.rows),
      Vector(
        "VarLatOperandEnc" -> Vector(
          Vector(BigInt(0))     -> BigInt(0),
          Vector(BigInt(1))     -> BigInt(1),
          Vector(BigInt(65535)) -> BigInt(7)
        ),
        "DemoMap" -> Vector(Vector(BigInt(65), BigInt(4)) -> BigInt(9))
      )
    )
    assertEquals(compiled.tables.head.issues, Vector.empty)
    assert(compiled.tables(1).issues.exists(_.contains("- 0 - -> 6")))
    assert(compiled.tables(1).issues.exists(_.contains("Missing@Unknown")))
    assertEquals(
      compiled.forms.head.fields.head.expression,
      SASSEncoding.Expr.Lookup(
        "DemoMap",
        Vector(
          SASSEncoding.Expr.Constant(65, relaxed = false),
          SASSEncoding.Expr.Constant(4, relaxed = false)
        )
      )
    )
    assert(compiled.forms.head.issues.exists(_.contains("unsupported rows")))
  }
}
