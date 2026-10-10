package net.kurobako.gemstone.codegen

class SASSDescriptionSuite extends munit.FunSuite {
  test("conditional scope survives class boundaries and duplicate names") {
    val source = """##IF DEFINED(ARCH_SEMANTICS)
                   |CLASS "LDG"
                   |FORMAT Register:Rd;
                   |CLASS "LDG"
                   |FORMAT Register:Ra;
                   |##ENDIF
                   |CLASS "NOP"
                   |FORMAT Opcode;
                   |""".stripMargin
    val parsed = SASSDescription.parse(source).fold(e => fail(e.message), identity)
    assertEquals(parsed.classes.map(_.conditional), Vector(true, true, false))
    assert(SASSDescription.parse(source.replace("##ENDIF", "")).isLeft)
  }

  test("headers inside comments and multiline strings are data") {
    val source = """ARCHITECTURE "Test"
                   |// CLASS "comment"
                   |CONSTANTS
                   | Text = "hello
                   |CLASS \"string\"
                   |FORMAT ignored";
                   |CLASS "real"
                   |FORMAT Register:R;
                   |CONDITIONS
                   | ERROR (R == 1) : "OPCODES";
                   |OPCODES
                   | REAL = 1;
                   |ENCODING
                   | R = 1;
                   |""".stripMargin
    val parsed = SASSDescription.parse(source).fold(e => fail(e.message), identity)
    assertEquals(parsed.classes.map(_.name), Vector("real"))
    assert(parsed.classes.head.section("CONDITIONS").exists(_.body.contains("ERROR")))
    assertEquals(parsed.preamble + parsed.classes.map(_.raw).mkString, source)
  }

  test("unterminated literals fail rather than disappearing") {
    assert(SASSDescription.parse("CLASS \"unterminated").isLeft)
  }

  test("singular CONDITION and future sections stay out of FORMAT") {
    val source = """CLASS "LEPC"
                   |FORMAT Register:Rd;
                   |CONDITION
                   | OOR_REG_ERROR (Rd < 10) : "bad register";
                   |FUTURE_SECTION
                   | unknown: rule;
                   |""".stripMargin
    val cls = SASSDescription.parse(source).fold(e => fail(e.message), identity).classes.head
    assertEquals(cls.section("FORMAT").map(_.body.trim), Some("Register:Rd;"))
    assert(cls.section("CONDITION").nonEmpty)
  }

  test("REMAP is a distinct lossless section and cannot leak assignments into ENCODING") {
    val source =
      """CLASS "remapped"
        |FORMAT Register:Rd;
        |OPCODES
        | MOV = 1;
        |ENCODING
        | BITS = Rd;
        |REMAP "@%%Predicate(Pg) MOV %%Register(Rd) &%%REQ(req)=req_bit_set";
        |PROPERTIES
        | INSTRUCTION_TYPE = INST_TYPE_SIMPLE;
        |""".stripMargin
    val parsed = SASSDescription.parse(source).fold(error => fail(error.message), identity)
    val cls    = parsed.classes.head
    assertEquals(cls.section("ENCODING").map(_.body.trim), Some("BITS = Rd;"))
    assertEquals(
      cls.section("REMAP").map(_.body.trim),
      Some("\"@%%Predicate(Pg) MOV %%Register(Rd) &%%REQ(req)=req_bit_set\";")
    )
    assertEquals(parsed.preamble + parsed.classes.map(_.raw).mkString, source)
  }

  test("quoted and dotted opcode names survive nested FORMAT groups") {
    val source = """CLASS "bindings"
                   |FORMAT Opcode $( Register:Ra/Partsel("32"):asel )$ ',' SImm(32)*:imm
                   | /F32.F16ONLY:dstfmt.srcfmt $( { '&' REQ:req '=' BITSET(6/0x0):req_bit_set } )$;
                   |OPCODES
                   | "1D" = 0b1;
                   | UIADD3.64 = 0b10;
                   |ENCODING
                   | Dest = Ra;
                   |""".stripMargin
    val parsed = SASSDescription.parse(source).fold(e => fail(e.message), identity)
    assertEquals(parsed.classes.head.opcodes, Vector("1D", "UIADD3.64"))
  }

  test("reads classes, opcodes, and balanced encoding expressions") {
    val source =
      """ARCHITECTURE "Test"
        |CONSTANTS
        |  FLAG = 1;
        |CLASS "IADD3"
        |  FORMAT PREDICATE @[!]Predicate(PT):Pg Register:Rd ',' Register:Ra ',' UImm(20):uImm;
        |  OPCODES
        |    IADD3 = 0b0001_01;
        |  ENCODING
        |    Dest = Rd;
        |    Imm = (uImm & 0x1f) | (1 << 7);
        |    Text = "a; b";
        |    !NencIADD3;
        |ALTERNATE CLASS "IADD3 immediate"
        |  FORMAT Register:Rd ',' UImm(20):uImm;
        |  OPCODES IADD3 = 0b10;
        |""".stripMargin

    val parsed = SASSDescription.parse(source).toOption.get
    assertEquals(parsed.architecture, Some("Test"))
    assert(parsed.globals.exists(_.name == "CONSTANTS"))
    assertEquals(parsed.classes.map(_.name), Vector("IADD3", "IADD3 immediate"))
    assertEquals(parsed.classes.map(_.alternate), Vector(false, true))
    val cls = parsed.classes.head
    assertEquals(cls.opcodes, Vector("IADD3"))
    val encoding = cls.section("ENCODING").get
    assert(encoding.body.contains("(uImm & 0x1f) | (1 << 7)"))
    assert(encoding.body.contains("Text = \"a; b\";"))
    assert(encoding.body.contains("!NencIADD3;"))
    assert(parsed.classes(1).section("OPCODES").get.body.contains("IADD3 = 0b10"))
  }

  test("retains unknown sections and non assignment directives verbatim") {
    val source =
      """CLASS "future"
        |  FUTURE_RULES
        |    not understood { keep; this; }
        |  FORMAT Register:R;
        |""".stripMargin
    val cls = SASSDescription.parse(source).toOption.get.classes.head
    assert(cls.raw.contains("FUTURE_RULES"))
    assert(cls.raw.contains("not understood { keep; this; }"))
    assert(cls.sections.exists(_.name == "FORMAT"))
    assert(!cls.sections.exists(_.name == "FUTURE_RULES"))
  }

  test("treats quoted punctuation as data and rejects an unbalanced description") {
    val quoted = "CLASS \"quoted\"\n FORMAT SImm(8):imm ',' Register:R;\n OPCODES\n X = 1;\n ENCODING\n X = '[';\n"
    assert(SASSDescription.parse(quoted).isRight)
    val malformed = "CLASS \"broken\"\n FORMAT Register:R;\n ENCODING\n X = (1;\n"
    assert(SASSDescription.parse(malformed).isLeft)
  }
}
