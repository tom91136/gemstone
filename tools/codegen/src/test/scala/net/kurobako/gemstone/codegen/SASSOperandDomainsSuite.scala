package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.testing.Suite

class SASSOperandDomainsSuite extends Suite {
  private def mask(offset: Int, bits: Int): String = "." * offset + "X" * bits + "." * (128 - offset - bits)

  private val description =
    s"""ARCHITECTURE "Volta"
       |PARAMETERS
       |  MAX_REG_COUNT = 10
       |CONSTANTS
       |  UNUSED = 1
       |STRING_MAP
       |FUNIT uC
       |  ENCODING WIDTH 128;
       |  Dest '${mask(offset = 0, bits = 4)}'
       |  Bar '${mask(offset = 4, bits = 3)}'
       |  Imm '${mask(offset = 8, bits = 5)}'
       |  Scaled '${mask(offset = 16, bits = 4)}'
       |  Op '${mask(offset = 24, bits = 4)}'
       |REGISTERS
       |  Register R(0..14), RZ = 15;
       |  ZeroRegister RZ = 15;
       |  BD B(0..15);
       |TABLES
       |CLASS "operands"
       |FORMAT Opcode Register:Rd ',' BD:bar ',' UImm(6):imm ',' SImm(8):scaled ;
       |CONDITIONS
       |  ERROR ((Rd) == `Register@RZ) || ((Rd) <= (%MAX_REG_COUNT - 1)) : "Register Rd is out of range"
       |OPCODES
       | ADD = 0b0001;
       |ENCODING
       | Op = Opcode;
       | Dest = Rd;
       | Bar = bar;
       | Imm = imm;
       | Scaled = scaled SCALE 4;
       |""".stripMargin

  private val derived = SASSOperandDomains.derive(SASSDescription.parse(description).fold(
    error => fail(error.message),
    identity
  ))

  private def fieldOf(binding: String): SASSOperandDomains.FieldDomain =
    derived.forms.head.fields.find(_.binding == binding).getOrElse(fail(s"no field $binding"))

  test("register conditions bound the vocabulary and keep the zero register") {
    assertEquals(
      fieldOf("Rd").domain,
      OperandDomain.Union(Vector(
        OperandDomain.Registers("r", first = 0, last = 9, code = 0),
        OperandDomain.Named("RZ", 15)
      ))
    )
    assert(derived.findings.exists(_.check == "register-limit"))
  }

  test("a field stores fewer bits than its vocabulary names") {
    assertEquals(fieldOf("bar").domain, OperandDomain.Registers("b", first = 0, last = 7, code = 0))
    assert(derived.findings.exists(finding => finding.check == "field-width" && finding.subject == "BD"))
  }

  test("immediates take the declared width, narrowed by the stored width after scaling") {
    assertEquals(fieldOf("imm").domain, OperandDomain.Immediate(5, signed = false))
    assertEquals(fieldOf("scaled").domain, OperandDomain.Immediate(6, signed = true))
    assert(derived.findings.exists(finding => finding.check == "immediate-width" && finding.subject == "UImm"))
  }

  test("bank sizes count the zero register") {
    assertEquals(derived.banks, Map("R" -> 16, "B" -> 16))
  }

  test("no finding of the sample is unresolved") {
    assertEquals(GenerateSASSDomains.unresolved(Vector(derived)), Vector.empty)
  }

  test("the rendered tables intern equal domains and list each form once per table") {
    val digest = "0" * 64
    val output = GenerateSASSDomains.render(
      Vector(digest -> SASSDescription.parse(description).fold(error => fail(error.message), identity)),
      Vector("SM90" -> digest)
    )
    val json = ujson.read(output.json)
    assertEquals(json("tables").arr.size, 1)
    assertEquals(json("tables")(0)("forms").arr.size, 1)
    assertEquals(
      json("banks").arr.map(row => row("bank").str -> row("size").num.toInt).toMap,
      Map("R" -> 16, "B" -> 16)
    )
    assert(output.report.contains("| 00000000 | SM90 | 1 |"))
  }
}
