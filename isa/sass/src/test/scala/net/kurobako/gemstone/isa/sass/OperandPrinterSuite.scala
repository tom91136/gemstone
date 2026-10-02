package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*

class OperandPrinterSuite extends munit.FunSuite {
  private def roundTrip(value: Operand): String = {
    val text = OperandPrinter.print(value).fold(fail(_), identity)
    assertEquals(SASSOperandParser.parse(text), Right(value))
    text
  }

  test("prints registers, predicates, symbols, groups, and qualifiers") {
    assertEquals(roundTrip(Operand.Register(RegisterBank.UR, 17)), "UR17")
    assertEquals(roundTrip(Operand.Register(RegisterBank.R, 255)), "RZ")
    assertEquals(roundTrip(Operand.Register(RegisterBank.UR, 63)), "URZ")
    assertEquals(roundTrip(Operand.Predicate(Some(3), uniform = true, negated = true)), "!UP3")
    assertEquals(roundTrip(Operand.Predicate(None, uniform = false, negated = false)), "PT")
    assertEquals(roundTrip(Operand.SpecialRegister(SpecialRegister.SR_TID_X)), "SR_TID.X")
    assertEquals(roundTrip(Operand.Symbol("shared_mem")), "shared_mem")
    assertEquals(
      roundTrip(Operand.Group(Vec(Operand.Register(RegisterBank.R, 0), Operand.Integer(12)))),
      "{R0, 12}"
    )
    assertEquals(
      roundTrip(Operand.Qualified(Operand.Register(RegisterBank.R, 3), Vec(OperandSelector.H0_H0))),
      "R3.H0_H0"
    )
  }

  test("prints numeric and raw immediates without changing their kind or payload") {
    assertEquals(roundTrip(Operand.Integer(42)), "42")
    assertEquals(roundTrip(Operand.Bits(0x2a, 0)), "0x2a")
    assertEquals(roundTrip(Operand.Bits(0x2a, 8)), "bits<8>(0x2a)")
    assertEquals(
      roundTrip(Operand.FloatingBits(
        FloatEncoding.F32Imm,
        32,
        BigInt("7fc12345", 16)
      )),
      "bits<F32Imm>:32(0x7fc12345)"
    )
    assertEquals(
      roundTrip(Operand.FloatingBits(FloatEncoding.E6M9Imm, 16, 0x7f01)),
      "bits<E6M9Imm>:16(0x7f01)"
    )
  }

  test("prints signed floating zero, decorated values, and constant-bank addresses") {
    val negativeZero = Operand.Floating(BigDecimal(0), "0.0", negativeZero = true)
    assertEquals(OperandPrinter.print(negativeZero), Right("-0.0"))
    assertEquals(OperandPrinter.print(Operand.Integer(-3)), Right("-3"))
    assertEquals(
      SASSOperandParser.parse(OperandPrinter.print(Operand.Integer(-3)).toOption.get),
      Right(Operand.Unary(Operand.Integer(3), negative = true, not = false, absolute = false))
    )
    assertEquals(
      roundTrip(Operand.Unary(Operand.Register(RegisterBank.R, 2), negative = true, not = true, absolute = true)),
      "!- |R2|".replace(" ", "")
    )
    assertEquals(
      roundTrip(Operand.Address(
        Some(Operand.Register(RegisterBank.R, 0)),
        Some(Operand.Register(RegisterBank.UR, 6)),
        -16,
        1,
        None,
        None
      )),
      "[R0+UR6-0x10]"
    )
    assertEquals(
      roundTrip(Operand.Address(Some(Operand.Register(RegisterBank.R, 4)), None, 0x28, 8, Some(AddressMode.X8), None)),
      "[R4.X8+0x28]"
    )
    assertEquals(
      roundTrip(Operand.ConstantBank(
        0,
        Operand.Address(Some(Operand.Register(RegisterBank.R, 2)), None, 0x28, 1, None, None)
      )),
      "c[0][R2+0x28]"
    )
  }

  test("rejects operand values without a lossless textual spelling") {
    assert(OperandPrinter.print(Operand.Symbol("R0")).isLeft)
    assert(OperandPrinter.print(Operand.Address(
      None,
      Some(Operand.Register(RegisterBank.R, 1)),
      0,
      1,
      None,
      None
    )).isLeft)
    assert(OperandPrinter.print(Operand.Address(
      Some(Operand.Register(RegisterBank.R, 0)),
      None,
      0,
      4,
      None,
      None
    )).isLeft)
    assert(OperandPrinter.print(Operand.FloatingBits(FloatEncoding.F32Imm, 16, 1)).isLeft)
  }

  test("signed normalization composes through nested operands without losing floating payloads") {
    val negative = Operand.Integer(-3)
    val group    = Operand.Group(Vec(
      negative,
      Operand.Group(Vec(
        Operand.Floating(BigDecimal(0), "0.0", negativeZero = true),
        Operand.FloatingSymbol(FloatingConstant.`-INF`),
        Operand.FloatingBits(FloatEncoding.F32Imm, 32, BigInt("7fc12345", 16))
      ))
    ))
    Vec(
      group                             -> "{-3, {-0.0, -INF, bits<F32Imm>:32(0x7fc12345)}}",
      Operand.ConstantBank(0, negative) -> "c[0][-3]",
      Operand.Qualified(Operand.ConstantBank(0, negative), Vec(OperandSelector.H0)) -> "c[0][-3].H0",
      Operand.Unary(negative, negative = false, not = false, absolute = true)       -> "|-3|"
    ).foreach { (value, text) =>
      assertEquals(OperandPrinter.print(value), Right(text))
      val parsed = SASSOperandParser.parse(text).fold(fail(_), identity(_))
      assertEquals(OperandPrinter.print(parsed), Right(text))
    }
  }

  test("composite normalization still rejects changed operand kinds and invalid payloads") {
    assert(OperandPrinter.print(Operand.Group(Vec(Operand.Symbol("R0")))).isLeft)
    assert(OperandPrinter.print(Operand.ConstantBank(0, Operand.Symbol("R0"))).isLeft)
    assert(OperandPrinter.print(Operand.Group(Vec(Operand.FloatingBits(FloatEncoding.F32Imm, 16, 1)))).isLeft)
    assert(OperandPrinter.print(Operand.Group(Vec(Operand.Bits(256, 8)))).isLeft)
  }
}
