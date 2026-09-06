package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.isa.ValidationResult

final class ValidationSuite extends munit.FunSuite {
  private def instruction(source: String): Instruction =
    Parser.parseStatement(source).toOption.get match {
      case Statement.Instruction(value) => value
      case other                        => fail(s"expected instruction, found $other")
    }

  test("typed forms enforce modifier groups after decoding enforces operand counts") {
    assertEquals(
      Validator.validate(instruction("mul.wide.u32 %rd0, %r1, 4;"), PTXVersion(9, 3), SMTarget(100)),
      ValidationResult.Valid
    )
    Validator.validate(instruction("mul.u32 %r0, %r1, %r2;"), PTXVersion(9, 3), SMTarget(100)) match {
      case ValidationResult.Invalid(_) => ()
      case other                       => fail(s"expected invalid instruction, found $other")
    }
    assert(Parser.parseStatement("mul.wide.u32 %r0, %r1;").isLeft)
  }

  test("instruction introduction and minimum SM are enforced") {
    val value = instruction("clmad.lo.u64 %rd0, %rd1, %rd2, %rd3;")
    Validator.validate(value, PTXVersion(9, 2), SMTarget(100)) match {
      case ValidationResult.Invalid(errors) => assert(errors.exists(_.isInstanceOf[ValidationIssue.VersionTooOld]))
      case other                            => fail(s"expected version rejection, found $other")
    }
    Validator.validate(value, PTXVersion(9, 3), SMTarget(70)) match {
      case ValidationResult.Invalid(errors) => assert(errors.exists(_.isInstanceOf[ValidationIssue.TargetTooOld]))
      case other                            => fail(s"expected target rejection, found $other")
    }
    assertEquals(Validator.validate(value, PTXVersion(9, 3), SMTarget(80)), ValidationResult.Valid)
  }

  test("conditional specification notes fail closed") {
    assertEquals(
      Validator.validate(instruction("add.u32 %r0, %r1, %r2;"), PTXVersion(9, 3), SMTarget(100)),
      ValidationResult.Unsupported(Col(ValidationIssue.Incomplete("syntax form for add.u32 is not fully normalized")))
    )
  }

  test("unknown instructions remain explicit after parsing") {
    Parser.parseStatement("not_a_ptx_instruction.u32 %r0;") match {
      case Right(Statement.UnsupportedInstruction("not_a_ptx_instruction", operands, mods, None)) =>
        assertEquals(operands.size, 1)
        assertEquals(mods.map(_.name).toSeq, Seq("u32"))
      case other => fail(s"unexpected parse result: $other")
    }
  }
}
