package net.kurobako.gemstone.codegen

class SASSInstructionModelSuite extends munit.FunSuite {
  private def form(name: String, format: String, opcode: String): SASSDescription.InstructionClass = {
    val formatSection = SASSDescription.Section("FORMAT", format, format, Vector.empty)
    val opcodeSection = SASSDescription.Section(
      "OPCODES",
      opcode,
      opcode,
      Vector(SASSDescription.Entry(opcode, "", opcode, 0, opcode.length))
    )
    SASSDescription.InstructionClass(
      name,
      alternate = false,
      conditional = false,
      raw = "",
      sections = Vector(formatSection, opcodeSection)
    )
  }

  test("modifier vocabularies stop at operands and do not include guards or operand selectors") {
    val row = form("F", "@Predicate:Pg Opcode Rounding:rnd DataType:type Register:Rd, Register:Ra Partsel:part", "MOV")
    assertEquals(SASSInstructionModel.modifierKinds(Vector(row)), Set("Rounding", "DataType"))
  }

  test("projects commas in text and quoted literals, and preserves separate fields within a chunk") {
    val row = form("F", "Opcode Register:Rd, Register:Ra Register:Rc ',' SImm(17):imm", "MOV")
    assertEquals(
      SASSInstructionModel.formShape(row).head.slots,
      Vector(
        SASSInstructionModel.Slot("Rd", "Register", optional = false),
        SASSInstructionModel.Slot("Ra", "Register", optional = false),
        SASSInstructionModel.Slot("Rc", "Register", optional = false),
        SASSInstructionModel.Slot("imm", "Immediate", optional = false)
      )
    )
  }

  test("uses nested bracket groups as one address and preserves constant-bank identity") {
    val row = form(
      "F",
      "Opcode Register:Rd, [Register:base [Register:index] SImm(17):disp], [C:bank UImm(5):selector SImm(17):offset]",
      "LD"
    )
    assertEquals(
      SASSInstructionModel.formShape(row).head.slots,
      Vector(
        SASSInstructionModel.Slot("Rd", "Register", optional = false),
        SASSInstructionModel.Slot("base", "Address", optional = false),
        SASSInstructionModel.Slot("bank", "ConstantBank", optional = false)
      )
    )
  }

  test("coalesces constant-bank offsets that decode as addresses and descriptor address triples") {
    val constant = form("C", "Opcode C:constant UImm:bank [Register:RZ]", "LD")
    assertEquals(
      SASSInstructionModel.formShape(constant).head.slots,
      Vector(SASSInstructionModel.Slot("constant", "ConstantBank", optional = false))
    )
    val descriptor = form("D", "Opcode DESC:desc [UniformRegister:ureg] [Register:main]", "LD")
    assertEquals(
      SASSInstructionModel.formShape(descriptor).head.slots,
      Vector(SASSInstructionModel.Slot("main", "Address", optional = false))
    )
    val cbu = form("S", "Opcode [CBU_STATE:state]", "LD")
    assertEquals(
      SASSInstructionModel.formShape(cbu).head.slots,
      Vector(SASSInstructionModel.Slot("state", "Address|Symbol", optional = false))
    )
    val scalarCbu = form("SS", "Opcode CBU_STATE:state", "LD")
    assertEquals(
      SASSInstructionModel.formShape(scalarCbu).head.slots,
      Vector(SASSInstructionModel.Slot("state", "Register|Symbol", optional = false))
    )
  }

  test("marks default predicates optional, retains special registers, and models brace groups") {
    val row =
      form("F", "@[!]Predicate(PT):Pg Opcode Register:Rd, SpecialRegister:SR, { Register:Ra Register:Rb }", "OP")
    assertEquals(
      SASSInstructionModel.formShape(row).head.slots,
      Vector(
        SASSInstructionModel.Slot("Rd", "Register", optional = false),
        SASSInstructionModel.Slot("SR", "SpecialRegister", optional = false),
        SASSInstructionModel.Slot("Ra", "Group", optional = false)
      )
    )
    val optionalPredicate = form("P", "Opcode Predicate(PT):pred", "OP")
    assertEquals(
      SASSInstructionModel.formShape(optionalPredicate).head.slots,
      Vector(SASSInstructionModel.Slot("pred", "Predicate", optional = true))
    )
  }

  test("removes the redundant P2R PR slot and deduplicates layouts while preserving forms") {
    val row = form("P", "Opcode Predicate:Rd, PR:PR, Register:Ra, Register:Rb", "P2R")
    val p2r = SASSInstructionModel.formShape(row).head
    assertEquals(p2r.slots.map(_.name), Vector("Rd", "Ra", "Rb"))
    val same = form("P2", "Opcode Predicate:Rd, PR:PR, Register:Ra, Register:Rb", "P2R")
    assertEquals(SASSInstructionModel.shapes(Vector(row, same)).head.forms.map(_.form), Vector("P", "P2"))
  }
  test("condition-register bank forms preserve CC and the normalised P2R predicate-bank layout") {
    val read   = form("Imm_P2R", "Opcode Register:Rd, CCPR:Pr, Register:Ra, SImm(20):uImm", "P2R")
    val write  = form("Imm_R2P", "Opcode CCPR:Pr, Register:Ra, UImm(20):uImm", "R2P")
    val shapes = SASSInstructionModel.shapes(Vector(read, write))
    val cases  = SASSInstructionRenderer.cases(shapes)
    assertEquals(cases.find(_.name == "P2rRegSymbolRegImm").get.forms, Vector("Imm_P2R"))
    assertEquals(cases.find(_.name == "P2rRegRegImm").get.forms, Vector("Imm_P2R"))
    assertEquals(cases.find(_.name == "R2pSymbolRegImm").get.forms, Vector("Imm_R2P"))
    assertEquals(cases.find(_.name == "R2pRegImm").get.forms, Vector("Imm_R2P"))
  }

  test("display omissions and condition-code operands retain their typed forms") {
    val vote = form("VOTE", "Opcode { Register(RZ):Rd ',' } Predicate:Pd ',' Predicate:Pa", "VOTE")
    assertEquals(SASSInstructionModel.formShape(vote).head.slots.map(_.optional), Vector(true, false, false))
    val atom = form("ATOM", "Opcode Register:Rd ',' [Register:Ra] ',' Register:Rb ',' Register(RZ)@:Rc", "ATOM")
    assert(SASSInstructionModel.formShape(atom).head.slots.last.optional)
    val branch = form("BRA", "Opcode { CC(CC):TestCC/Test(T):CCTest ',' } RSImm(24):target", "BRA")
    assertEquals(SASSInstructionModel.formShape(branch).head.slots.map(_.kind), Vector("Symbol", "Immediate"))
    val barrier = form("DEPBAR", "Opcode BITSET(6/0):mask", "DEPBAR")
    assertEquals(SASSInstructionModel.formShape(barrier).head.slots.head.kind, "Immediate|Group")
  }

  test("qualified opcodes and descriptor prefixes share the canonical logical definition") {
    val qualified = form("hfma", "Opcode Register:Rd ',' Register:Ra ',' Register:Rb ',' Register:Rc", "HFMA2.MMA")
    assertEquals(SASSInstructionModel.formShape(qualified).head.opcode, "HFMA2")
    val descriptor = form(
      "stg",
      "Opcode /EONLY:e /SZ(32):sz DESC:descriptor[UniformRegister:URa][Register:Ra + SImm(24/0):offset] ',' Register:Rb",
      "STG"
    )
    assertEquals(SASSInstructionModel.formShape(descriptor).head.slots.map(_.kind), Vector("Address", "Register"))
  }

}
