package net.kurobako.gemstone.codegen

class SASSFormatSuite extends munit.FunSuite {
  import SASSFormat.*

  test("retains typed operands, qualifier prefixes, and comma literals") {
    val parsed = parse(" PREDICATE @[!]Predicate(PT):Pg Opcode\nRegister:Rd\n','SImm(17)*:Sb;")
      .fold(error => fail(error.message), identity)
    val fields = descendants(parsed).collect { case field: Field => field }
    assertEquals(fields.map(_.binding), Vector("Pg", "Rd", "Sb"))
    assertEquals(fields.head.kind, "Predicate")
    assertEquals(fields.head.arguments, Some("PT"))
    assertEquals(fields.head.default, Some("PT"))
    assertEquals(fields.last.kind, "SImm")
    assertEquals(fields.last.arguments, Some("17"))
    assertEquals(fields.last.default, None)
    assertEquals(fields.last.suffix, "*")
    assert(descendants(parsed).contains(Literal("','", ",")))
  }

  test("preserves nested address syntax and optional scheduler structure") {
    val parsed =
      parse("C:Sb[UImm(5/0*):Sb_bank]* [SImm(17)*:Sb_addr] $( { '&' REQ:req '=' BITSET(6/0x0000):req_bit_set } )$")
        .fold(error => fail(error.message), identity)
    val fields = descendants(parsed).collect { case field: Field => field }
    assertEquals(fields.map(_.binding), Vector("Sb", "Sb_bank", "Sb_addr", "req", "req_bit_set"))
    assert(fields.exists(field => field.binding == "Sb_bank" && field.arguments.contains("5/0*")))
    val optional = descendants(parsed).collect { case node: Optional => node }
    assertEquals(optional.size, 1)
    assertEquals(optional.head.marker, Some("&"))
  }

  test("does not treat quoted colons as field bindings") {
    val parsed = parse("'text:literal' Register:Rd;").fold(error => fail(error.message), identity)
    assertEquals(descendants(parsed).collect { case field: Field => field.binding }, Vector("Rd"))
    assert(descendants(parsed).exists { case Literal("'text:literal'", "text:literal") => true; case _ => false })
  }

  test("does not confuse immediate widths with default values") {
    val parsed = parse("UImm(32):uimm; SImm(17)*:simm; UImm(8/0xf):defaulted;")
      .fold(error => fail(error.message), identity)
    val fields = descendants(parsed).collect { case field: Field => field }
    assertEquals(fields.map(_.default), Vector(None, None, Some("0xf")))
  }

  test("retains legacy @ field suffixes and their bindings") {
    val parsed = parse("PREDICATE @[!]Predicate(PT):Pg Opcode /Pipe2(HW)@:pipe /S(noS):sync")
      .fold(error => fail(error.message), identity)
    val fields = descendants(parsed).collect { case field: Field => field }
    assertEquals(fields.map(_.binding), Vector("Pg", "pipe", "sync"))
    assertEquals(
      fields(1),
      Field(
        kind = "Pipe2",
        binding = "pipe",
        arguments = Some("HW"),
        default = Some("HW"),
        suffix = "@",
        raw = "Pipe2(HW)@:pipe"
      )
    )
    assertEquals(fields(2).suffix, "")
  }

  test("preserves plain legacy optionals separately from REQ and scheduler optionals") {
    val parsed = parse(
      "$( RegisterFAU:Rd )$ ',' C:srcConst[UImm(5/0*):constBank]* [SImm(17)*:immConstOffset] $( { '&' REQ:req '=' BITSET(6/0x0000):req_sb_bitset } )$ $( { '?' USCHED_INFO:usched_info } )$"
    )
      .fold(error => fail(error.message), identity)
    val optionals = descendants(parsed).collect { case node: Optional => node }
    assertEquals(optionals.map(_.marker), Vector(None, Some("&"), Some("?")))
    assertEquals(descendants(optionals.head.children).collect { case field: Field => field.binding }, Vector("Rd"))
    assertEquals(
      descendants(parsed).collect { case field: Field => field.binding },
      Vector("Rd", "srcConst", "constBank", "immConstOffset", "req", "req_sb_bitset", "usched_info")
    )
  }

  test("recognises short legacy binding names") {
    val parsed = parse("Opcode /X:x /PO:po /ICOMP:icomp /LOP:lop")
      .fold(error => fail(error.message), identity)
    assertEquals(descendants(parsed).collect { case field: Field => field.binding }, Vector("x", "po", "icomp", "lop"))
  }

  test("reports unmatched structural groups") {
    assert(parse("Register:Rd [SImm(8):imm").isLeft)
  }

  private def descendants(nodes: Vector[Node]): Vector[Node] = nodes.flatMap {
    case node: Group    => node +: descendants(node.children)
    case node: Optional => node +: descendants(node.children)
    case node           => Vector(node)
  }
}
