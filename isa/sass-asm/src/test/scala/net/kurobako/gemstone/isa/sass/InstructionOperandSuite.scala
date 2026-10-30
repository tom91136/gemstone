package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.testing.Suite

class InstructionOperandSuite extends Suite {
  private def wrapped(value: Operand): Operand =
    Operand.Qualified(Operand.Unary(value, negative = true, not = false, absolute = true), Vec(OperandSelector.H0_H0))

  test("refinements retain the complete wrapped value and accept their leaf kinds") {
    val register  = wrapped(Operand.Register(RegisterBank.UR, 17))
    val immediate = wrapped(Operand.FloatingBits(FloatEncoding.F32Imm, 32, BigInt("7fc12345", 16)))
    val group     = wrapped(Operand.Group(Vec(Operand.Integer(3))))

    assertEquals(InstructionOperand.Register.from(register).map[Operand](identity), Right(register))
    assertEquals(InstructionOperand.Immediate.from(immediate).map[Operand](identity), Right(immediate))
    assertEquals(InstructionOperand.Group.from(group).map[Operand](identity), Right(group))
    assertEquals(InstructionOperand.Register.unapply(register).map[Operand](identity), Some(register))
  }

  test("refinements reject a wrong leaf kind even through wrappers") {
    val decoratedPredicate = wrapped(Operand.Predicate(Some(2), uniform = true, negated = false))
    val decoratedSymbol    = wrapped(Operand.Symbol("label"))

    assert(InstructionOperand.Register.from(decoratedPredicate).isLeft)
    assert(InstructionOperand.Predicate.from(decoratedSymbol).isLeft)
    assert(InstructionOperand.Address.from(decoratedSymbol).isLeft)
    assertEquals(InstructionOperand.Register.unapply(decoratedPredicate), None)
  }

  test("immediates include numeric and floating-symbol leaves") {
    val accepted = Vec(
      Operand.Integer(4),
      Operand.Bits(0x2a, 8),
      Operand.FloatingBits(FloatEncoding.F32Imm, 32, 0x1234),
      Operand.Floating(BigDecimal("1.5"), "1.5", negativeZero = false),
      Operand.FloatingSymbol(FloatingConstant.NAN)
    )

    assert(accepted.forall(value => InstructionOperand.Immediate.from(value).isRight))
    assert(InstructionOperand.Immediate.from(Operand.Symbol("NAN")).isLeft)
  }
}
