package net.kurobako.gemstone.isa.sass.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.*

class SASSFormatParserSuite extends munit.FunSuite {
  import NVDisasm.*
  import NVDisasm.Expression.*
  import NVDisasm.Syntax.*

  private def makeDatabase(
      format: Vec[Syntax],
      fields: Vec[Assignment],
      opcodes: Vec[OpcodeValue],
      symbols: Vec[Vocabulary] = Vec.empty
  ): (Database, Form) = {
    val form = Form(None, "test", false, opcodes, fields, Vec.empty, format)
    Database("SM89", "", 64, Vec(form), Vec.empty, symbols = symbols) -> form
  }

  test("parses opcode aliases, optional predicates, attributes, and predicate defaults") {
    val (database, form) = makeDatabase(
      Vec(
        Text(" PREDICATE @"),
        Field("Predicate", "Pg", None, Some("PT")),
        Text(" Opcode")
      ),
      Vec(
        Assignment("opcode", Vec.empty, Reference("Opcode", None, relaxed = false)),
        Assignment("predicate", Vec.empty, Reference("Pg", None, relaxed = false)),
        Assignment("not", Vec.empty, Reference("Pg", Some("not"), relaxed = false))
      ),
      Vec(OpcodeValue("MOV", 3)),
      Vec(Vocabulary("Predicate", Vec(SymbolValue("P2", 2), SymbolValue("PT", 7))))
    )

    assertEquals(
      SASSFormatParser.parse(database, form, 0, "@!P2 MOV"),
      Right(Map("Opcode" -> BigInt(3), "Pg" -> BigInt(2), "Pg@not" -> BigInt(1)))
    )
    assertEquals(
      SASSFormatParser.parse(database, form, 0, "MOV"),
      Right(Map("Opcode" -> BigInt(3), "Pg" -> BigInt(7), "Pg@not" -> BigInt(0)))
    )
  }

  test("parses signed immediates and applies field defaults") {
    val (database, form) = makeDatabase(
      Vec(Text("Opcode "), Field("SImm", "offset", Some("8"), None)),
      Vec(
        Assignment("opcode", Vec.empty, Reference("Opcode", None, relaxed = false)),
        Assignment("offset", Vec.empty, Reference("offset", None, relaxed = false))
      ),
      Vec(OpcodeValue("ADDI", 1))
    )
    assertEquals(
      SASSFormatParser.parse(database, form, 0, "ADDI -0x1"),
      Right(Map("Opcode" -> BigInt(1), "offset" -> BigInt(255)))
    )

    val (defaultDatabase, defaultForm) = makeDatabase(
      Vec(Text("Opcode"), Field("UImm", "selector", Some("8"), Some("0"))),
      Vec(
        Assignment("opcode", Vec.empty, Reference("Opcode", None, relaxed = false)),
        Assignment("selector", Vec.empty, Reference("selector", None, relaxed = false))
      ),
      Vec(OpcodeValue("NOP", 2))
    )
    assertEquals(
      SASSFormatParser.parse(defaultDatabase, defaultForm, 0, "NOP"),
      Right(Map("Opcode" -> BigInt(2), "selector" -> BigInt(0)))
    )
  }

  test("resolves relative branch text against the instruction address") {
    val (database, form) = makeDatabase(
      Vec(Text("Opcode "), Field("RSImm", "target", Some("16"), None)),
      Vec(
        Assignment("opcode", Vec.empty, Reference("Opcode", None, relaxed = false)),
        Assignment("target", Vec.empty, Reference("target", None, relaxed = false))
      ),
      Vec(OpcodeValue("BRA", 4))
    )
    assertEquals(
      SASSFormatParser.parse(database, form, 8, "BRA 0x20"),
      Right(Map("Opcode" -> BigInt(4), "target" -> BigInt(16)))
    )
  }

  test("SM35 branch FORMAT omits the default condition group and uses signed 24-bit byte offsets") {
    val database = NVDisasm.forTarget("SM35").getOrElse(fail("missing SM35 decoder"))
    val form     = database.forms.find(_.name == "BRA").getOrElse(fail("missing SM35 BRA form"))
    Vec(
      (16, "BRA 8;", BigInt("fffff0", 16)),
      (8, "BRA 72;", BigInt(56)),
      (0, "BRA 8388615;", BigInt("7fffff", 16)),
      (8388600, "BRA 0;", BigInt("800000", 16))
    ).foreach { (address, source, displacement) =>
      val bindings = SASSFormatParser.parse(database, form, address, source).fold(fail(_), identity(_))
      assertEquals(bindings.get("sImm"), Some(displacement))
      assertEquals(bindings.get("Test"), Some(BigInt(15)))
    }
    assert(SASSFormatParser.parse(database, form, 0, "BRA 8388616;").isLeft)
    assert(SASSFormatParser.parse(database, form, 8388601, "BRA 0;").isLeft)
    val conditional = SASSFormatParser.parse(database, form, 16, "BRA CC.EQU, 8;")
      .fold(fail(_), identity(_))
    assertEquals(conditional.get("Test"), Some(BigInt(10)))
    assertEquals(conditional.get("sImm"), Some(BigInt("fffff0", 16)))
    assert(SASSFormatParser.parse(database, form, 16, "BRA CC.EQU 8;").isLeft)
  }

  test("SM50 omits the default condition and its external comma together") {
    val database = NVDisasm.forTarget("SM50").getOrElse(fail("missing SM50 decoder"))
    val form     = database.forms.find(_.name == "BRA").getOrElse(fail("missing SM50 BRA form"))
    val plain    = SASSFormatParser.parse(database, form, 8, "BRA 40;").fold(fail(_), identity(_))
    assertEquals(plain.get("sImm"), Some(BigInt(24)))
    assertEquals(plain.get("Test"), Some(BigInt(15)))
    val conditional = SASSFormatParser.parse(database, form, 8, "BRA CC.EQU, 40;").fold(fail(_), identity(_))
    assertEquals(conditional.get("Test"), Some(BigInt(10)))
    assert(SASSFormatParser.parse(database, form, 8, "BRA CC.EQU 40;").isLeft)
  }

  test("SM89 omits the default operand predicate and its comma together") {
    val database = NVDisasm.forTarget("SM89").getOrElse(fail("missing SM89 decoder"))
    val form     = database.forms.find(_.name == "bra_").getOrElse(fail("missing SM89 BRA form"))
    val plain    = SASSFormatParser.parse(database, form, 0, "BRA 16;").fold(fail(_), identity(_))
    assertEquals(plain.get("sImm"), Some(BigInt(0)))
    assertEquals(plain.get("Pp"), Some(BigInt(7)))
    val guarded = SASSFormatParser.parse(database, form, 0, "@!P0 BRA 32;").fold(fail(_), identity(_))
    assertEquals(guarded.get("Pg"), Some(BigInt(0)))
    assertEquals(guarded.get("Pg@not"), Some(BigInt(1)))
    assertEquals(guarded.get("sImm"), Some(BigInt(16)))
    val predicate = SASSFormatParser.parse(database, form, 0, "BRA P0, 32;").fold(fail(_), identity(_))
    assertEquals(predicate.get("Pp"), Some(BigInt(0)))
    assert(SASSFormatParser.parse(database, form, 0, "BRA P0 32;").isLeft)
  }

  test("an explicit modifier overrides its FORMAT default") {
    val (database, form) = makeDatabase(
      Vec(Text("Opcode "), Field("Mode", "mode", None, Some("OFF"))),
      Vec(
        Assignment("opcode", Vec.empty, Reference("Opcode", None, relaxed = false)),
        Assignment("mode", Vec.empty, Reference("mode", None, relaxed = false))
      ),
      Vec(OpcodeValue("MOV", 3)),
      Vec(Vocabulary("Mode", Vec(SymbolValue("OFF", 0), SymbolValue("ON", 1))))
    )
    assertEquals(
      SASSFormatParser.parse(database, form, 0, "MOV ON"),
      Right(Map("Opcode" -> BigInt(3), "mode" -> BigInt(1)))
    )
  }

  test("parses an unannotated MOV against the generated SM89 format") {
    val database = NVDisasm.forTarget("SM89").getOrElse(fail("missing SM89 decoder"))
    val form     = database.forms.find(form =>
      form.name == "mov__RR" && form.opcodes.exists(_.name == "MOV")
    ).getOrElse(fail("missing generated MOV register-register format"))
    assert(SASSFormatParser.parse(database, form, 0, "MOV R3, R7;").isRight)
  }
}
