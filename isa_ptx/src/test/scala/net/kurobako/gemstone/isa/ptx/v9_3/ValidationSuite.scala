package net.kurobako.gemstone.isa.ptx.v9_3

final class ValidationSuite extends munit.FunSuite {
  private def instruction(source: String): Instruction =
    Parser.parseStatement(source).toOption.get match {
      case Statement.Instruction(value) => value
      case other                        => fail(s"expected instruction, found $other")
    }

  test("typed forms enforce modifier groups after decoding enforces operand counts") {
    assertEquals(
      Validator.validate(instruction("mul.wide.u32 %rd0, %r1, 4;"), PtxVersion(9, 3), SmTarget(100)),
      ValidationResult.Valid
    )
    assert(Validator.validate(instruction("mul.u32 %r0, %r1, %r2;"), PtxVersion(9, 3), SmTarget(100)).isInstanceOf[
      ValidationResult.Invalid
    ])
    assert(Parser.parseStatement("mul.wide.u32 %r0, %r1;").isLeft)
  }

  test("instruction introduction and minimum SM are enforced") {
    val value = instruction("clmad.lo.u64 %rd0, %rd1, %rd2, %rd3;")
    Validator.validate(value, PtxVersion(9, 2), SmTarget(100)) match {
      case ValidationResult.Invalid(errors) => assert(errors.exists(_.isInstanceOf[ValidationError.VersionTooOld]))
      case other                            => fail(s"expected version rejection, found $other")
    }
    Validator.validate(value, PtxVersion(9, 3), SmTarget(70)) match {
      case ValidationResult.Invalid(errors) => assert(errors.exists(_.isInstanceOf[ValidationError.TargetTooOld]))
      case other                            => fail(s"expected target rejection, found $other")
    }
    assertEquals(Validator.validate(value, PtxVersion(9, 3), SmTarget(80)), ValidationResult.Valid)
  }

  test("conditional specification notes fail closed") {
    assertEquals(
      Validator.validate(instruction("add.u32 %r0, %r1, %r2;"), PtxVersion(9, 3), SmTarget(100)),
      ValidationResult.Unsupported("syntax form for add.u32 is not fully normalized")
    )
  }

  test("unknown instructions fail during decoding") {
    assert(Parser.parseStatement("not_a_ptx_instruction.u32 %r0;").isLeft)
  }
}
