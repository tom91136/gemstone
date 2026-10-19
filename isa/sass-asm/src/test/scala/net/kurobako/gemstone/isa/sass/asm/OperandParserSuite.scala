package net.kurobako.gemstone.isa.sass.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.*

class OperandParserSuite extends munit.FunSuite {
  test("preserves register classes and SASS zero-register sentinels") {
    assertEquals(OperandParser.parse("R17"), Right(Operand.Register(RegisterBank.R, 17)))
    assertEquals(OperandParser.parse("URZ"), Right(Operand.Register(RegisterBank.UR, 255)))
    assertEquals(OperandParser.parse("RZ"), Right(Operand.Register(RegisterBank.R, 255)))
    assertEquals(OperandParser.parse("R255"), Right(Operand.Register(RegisterBank.R, 255)))
    assert(OperandParser.parse("R256").isLeft)
    assert(OperandParser.parse("UR4294967296").isLeft)
    assert(OperandParser.parse("[R300+0x4]").isLeft)
    assertEquals(OperandParser.parse("!UP2"), Right(Operand.Predicate(Some(2), uniform = true, negated = true)))
    assertEquals(OperandParser.parse("PT"), Right(Operand.Predicate(None, uniform = false, negated = false)))
  }

  test("preserves vendor operand-domain spellings without assigning them semantics") {
    val vendorRegister = Operand.Register(RegisterBank.fromSpelling("XR"), 7)
    val vendorFloat    = Operand.FloatingBits(FloatEncoding.fromSpelling("FP8Vendor"), 16, 0x1234)
    val vendorSpecial  = Operand.SpecialRegister(SpecialRegister.fromSpelling("SR_VENDOR"))
    assertEquals(OperandPrinter.print(vendorRegister), Right("XR7"))
    assertEquals(OperandPrinter.print(vendorFloat), Right("bits<FP8Vendor>:16(0x1234)"))
    assertEquals(OperandPrinter.print(vendorSpecial), Right("SR_VENDOR"))
    assertEquals(OperandParser.parse("XR7"), Right(vendorRegister))
    assertEquals(OperandParser.parse("bits<FP8Vendor>:16(0x1234)"), Right(vendorFloat))
    assertEquals(OperandParser.parse("SR_VENDOR"), Right(vendorSpecial))
    assertEquals(RegisterBank.capacity(RegisterBank.fromSpelling("XR")), None)
  }

  test("keeps decimal values distinct from explicit bit-pattern immediates") {
    assertEquals(OperandParser.parse("1"), Right(Operand.Integer(1)))
    assertEquals(OperandParser.parse("0x1"), Right(Operand.Bits(1, 0)))
    assertEquals(
      OperandParser.parse("-0.0"),
      Right(Operand.Unary(Operand.Floating(BigDecimal(0), "0.0", negativeZero = false), true, false, false))
    )
  }

  test("preserves fractional and exponent spellings") {
    assertEquals(
      OperandParser.parse("12.5e-3"),
      Right(Operand.Floating(BigDecimal("12.5e-3"), "12.5e-3", negativeZero = false))
    )
    assertEquals(
      OperandParser.parse("4E+2"),
      Right(Operand.Floating(BigDecimal("4E+2"), "4E+2", negativeZero = false))
    )
    assert(OperandParser.parse("12.5e-").isLeft)
  }

  test("a selector follows an immediate as it follows a register") {
    val half = Vec(OperandSelector.H0)
    assertEquals(OperandParser.parse("1.H0"), Right(Operand.Qualified(Operand.Integer(1), half)))
    assertEquals(
      OperandParser.parse("0x3f80.H0"),
      Right(Operand.Qualified(Operand.Bits(0x3f80, 0), half))
    )
    assertEquals(
      OperandParser.parse("1.5.H0"),
      Right(Operand.Qualified(Operand.Floating(BigDecimal("1.5"), "1.5", negativeZero = false), half))
    )
    assertEquals(
      OperandParser.parse("1.e2"),
      Right(Operand.Floating(BigDecimal("1.e2"), "1.e2", negativeZero = false))
    )
    val bits = Operand.FloatingBits(FloatEncoding.F32Imm, 32, 0x3f800000L)
    assertEquals(
      OperandParser.parse("bits<F32Imm>:32(0x3f800000).H1"),
      Right(Operand.Qualified(bits, Vec(OperandSelector.H1)))
    )
    assertEquals(
      OperandParser.parseMany("R0, R4, 1.H0"),
      Right(Vec(
        Operand.Register(RegisterBank.R, 0),
        Operand.Register(RegisterBank.R, 4),
        Operand.Qualified(Operand.Integer(1), half)
      ))
    )
  }

  test("parses addresses, dynamic constant offsets, vectors, and selectors structurally") {
    assertEquals(
      OperandParser.parse("[R4.64+0x28]"),
      Right(Operand.Address(Some(Operand.Register(RegisterBank.R, 4)), None, 0x28, 1, Some(AddressMode.`64`), None))
    )
    assertEquals(
      OperandParser.parse("c[0][R2+0x28]"),
      Right(Operand.ConstantBank(
        0,
        Operand.Address(Some(Operand.Register(RegisterBank.R, 2)), None, 0x28, 1, None, None)
      ))
    )
    assertEquals(
      OperandParser.parse("c[0x0][0x8].H1"),
      Right(Operand.Qualified(Operand.ConstantBank(0, Operand.Bits(8, 0)), Vec.nonEmpty(OperandSelector.H1)))
    )
    assertEquals(
      OperandParser.parse("[R0+UR6+-0x10]"),
      Right(Operand.Address(
        Some(Operand.Register(RegisterBank.R, 0)),
        Some(Operand.Register(RegisterBank.UR, 6)),
        -16,
        1,
        None,
        None
      ))
    )
    assertEquals(
      OperandParser.parse("{R0, R1, R2, R3}"),
      Right(Operand.Group(Vec.nonEmpty(
        Operand.Register(RegisterBank.R, 0),
        Operand.Register(RegisterBank.R, 1),
        Operand.Register(RegisterBank.R, 2),
        Operand.Register(RegisterBank.R, 3)
      )))
    )
    assertEquals(
      OperandParser.parse("R3.H0_H0"),
      Right(Operand.Qualified(Operand.Register(RegisterBank.R, 3), Vec.nonEmpty(OperandSelector.H0_H0)))
    )
  }

  test("rejects floating address terms instead of dropping their displacement") {
    Vec.nonEmpty(
      "[1.5]",
      "[1e2]",
      "[R0+1.5]",
      "[R0-2E+3]",
      "c[0][R0+1.5]",
      "desc[UR6][R0+1e2]"
    ).foreach(source => assert(OperandParser.parse(source).isLeft, source))
  }

  test("rejects index-register selectors instead of silently discarding them") {
    Vec.nonEmpty(
      "[R0+UR1.X4]",
      "[R0+UR1.reuse]",
      "c[0][R0+UR1.U32]",
      "desc[UR6][R0+UR1.64]"
    ).foreach(source => assert(OperandParser.parse(source).isLeft, source))
  }

  test("address modes retain base qualifiers and reject conflicting modes") {
    assertEquals(
      OperandParser.parse("[R0.X4.reuse+UR1]"),
      Right(Operand.Qualified(
        Operand.Address(
          Some(Operand.Register(RegisterBank.R, 0)),
          Some(Operand.Register(RegisterBank.UR, 1)),
          0,
          4,
          Some(AddressMode.X4),
          None
        ),
        Vec.nonEmpty(OperandSelector.reuse)
      ))
    )
    assertEquals(OperandParser.parse("[R0.X4.X4]"), OperandParser.parse("[R0.X4]"))
    assert(OperandParser.parse("[R0.X4.64]").isLeft)
  }

  test("preserves signed integer address terms and explicit descriptors") {
    assertEquals(
      OperandParser.parse("[R4-0x10+0b11+20]"),
      Right(Operand.Address(Some(Operand.Register(RegisterBank.R, 4)), None, 7, 1, None, None))
    )
    assertEquals(
      OperandParser.parse("[-0x10]"),
      Right(Operand.Address(None, None, -16, 1, None, None))
    )
    assertEquals(
      OperandParser.parse("desc[UR6][R4.X4+0b10-5]"),
      Right(Operand.Address(
        Some(Operand.Register(RegisterBank.R, 4)),
        None,
        -3,
        4,
        Some(AddressMode.X4),
        Some(Operand.Register(RegisterBank.UR, 6))
      ))
    )
  }

  test("groups share operand-list comma rules and preserve nesting") {
    val source = "R0 , {R1, R2}, c[0][R3+4]"
    assertEquals(
      OperandParser.parse(s"{$source}"),
      OperandParser.parseMany(source).map(Operand.Group(_))
    )
    Vec.nonEmpty("R0,", "R0 R1", "R0 ").foreach { source =>
      assert(OperandParser.parseMany(source).isLeft, source)
      assert(OperandParser.parse(s"{$source}").isLeft, source)
    }
    assert(OperandParser.parse("{}").isLeft)
  }

  test("retains combined unary decorations and signed floating symbols") {
    assertEquals(
      OperandParser.parse("-|R2|"),
      Right(Operand.Unary(Operand.Register(RegisterBank.R, 2), negative = true, not = false, absolute = true))
    )
    assertEquals(OperandParser.parse("+INF"), Right(Operand.FloatingSymbol(FloatingConstant.INF)))
    assertEquals(
      OperandParser.parse("-INF"),
      Right(Operand.Unary(Operand.FloatingSymbol(FloatingConstant.INF), negative = true, not = false, absolute = false))
    )
    assertEquals(OperandParser.parse("SR_TID.X"), Right(Operand.SpecialRegister(SpecialRegister.SR_TID_X)))
    assertEquals(
      OperandParser.parse("1."),
      Right(Operand.Floating(BigDecimal("1."), "1.", negativeZero = false))
    )
  }

  test("preserves selector and delimiter validation") {
    assert(OperandParser.parse("SR_TID.").isLeft)
    assert(OperandParser.parse("R0.").isLeft)
    assert(OperandParser.parse("[R0 ]").isLeft)
    assert(OperandParser.parse("{R0 }").isLeft)
    assert(OperandParser.parseMany("R0 ").isLeft)
    assert(OperandParser.parseMany("R0 , R1").isRight)
  }

  test("requires complete input rather than accepting an operand prefix") {
    assert(OperandParser.parse("R0 trailing").isLeft)
    assert(OperandParser.parse("[R0+").isLeft)
    assert(OperandParser.parse("[R0-R1]").isLeft)
    assert(OperandParser.parse("[R0+R1+R2]").isLeft)
    assert(OperandParser.parse("[R0.64.X8]").isLeft)
    assert(OperandParser.parseMany("R0, {R1, R2}, c[0][R3+4]").isRight)
  }
}
