package net.kurobako.gemstone.isa.ptx

import asm.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class OperandDomainsSuite extends munit.FunSuite {
  test("known register parts and selectors have closed typed cases") {
    assertEquals(OperandDomains.RegisterPart.fromSpelling("hi"), OperandDomains.RegisterPart.Hi)
    assertEquals(OperandDomains.QualifiedOperand.fromSpelling("z"), OperandDomains.QualifiedOperand.Z)
    assertEquals(OperandDomains.QualifiedOperand.fromSpelling("x"), OperandDomains.QualifiedOperand.X)
  }

  test("unknown operand spellings are retained exactly") {
    val spelling: NonEmpty[String] = "vendor_part"
    assertEquals(OperandDomains.RegisterPart.fromSpelling(spelling), OperandDomains.RegisterPart.Unknown(spelling))
    assertEquals(OperandDomains.QualifiedOperand.fromSpelling(spelling).spelling, spelling)
  }

  test("printer preserves unknown register parts and qualified operands") {
    val part: NonEmpty[String]      = "ufoo"
    val qualifier: NonEmpty[String] = "vendor"
    assertEquals(
      Printer.render(Operand.Register("%r1", Some(OperandDomains.RegisterPart.Unknown(part)))),
      "%r1.ufoo"
    )
    assertEquals(
      Printer.render(Operand.Qualified(
        Operand.Address(Vec(Operand.Symbol("p"))),
        OperandDomains.QualifiedOperand.Unknown(qualifier)
      )),
      "[p].vendor"
    )
  }

  test("parser and printer round-trip unknown register parts and address qualifiers") {
    val statement = Parser.parseStatement("unknown %r1.ufoo, [%rd1].vendor;").toOption.get
    val rendered  = Printer.render(statement)
    assertEquals(Parser.parseStatement(rendered), Right(statement))
  }
}
