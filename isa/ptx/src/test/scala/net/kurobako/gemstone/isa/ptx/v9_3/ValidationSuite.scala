package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*

class ValidationSuite extends munit.FunSuite {
  private def instruction(source: String): Instruction =
    Parser.parseStatement(source).toOption.get match {
      case Statement.Instruction(value) => value
      case other                        => fail(s"expected instruction, found $other")
    }

  test("modifier validation preserves ordered required groups and repeated types") {
    def validate(source: String) = Validation.validate(instruction(source), Version(9, 3), Target(100))
    validate("cvt.rn.f32.f32 %d, %a;") match {
      case ValidationOutcome.Rejected(issues) => fail(s"repeated source/destination type rejected: $issues")
      case _                                  => ()
    }
    for (source <- Vec("mul.u32.wide %d, %a, %b;", "mul.wide.u32.u32 %d, %a, %b;"))
      validate(source) match {
        case ValidationOutcome.Rejected(issues) => assert(issues.exists {
            case _: Validation.Issue.InvalidModifiers => true
            case _                                    => false
          })
        case other => fail(s"invalid modifier sequence accepted: $source: $other")
      }
  }

  test("typed forms enforce modifier groups after decoding enforces operand counts") {
    assertEquals(
      Validation.validate(
        instruction("mul.wide.u32 %rd0, %r1, 4;"),
        Version(9, 3),
        Target(100)
      ),
      ValidationOutcome.Accepted
    )
    Validation.validate(
      instruction("mul.u32 %r0, %r1, %r2;"),
      Version(9, 3),
      Target(100)
    ) match {
      case ValidationOutcome.Rejected(_) => ()
      case other                         => fail(s"expected invalid instruction, found $other")
    }
    assert(Parser.parseStatement("mul.wide.u32 %r0, %r1;").isLeft)
  }

  test("instruction introduction and minimum SM are enforced") {
    val value = instruction("clmad.lo.u64 %rd0, %rd1, %rd2, %rd3;")
    Validation.validate(value, Version(9, 2), Target(100)) match {
      case ValidationOutcome.Rejected(errors) => assert(errors.exists {
          case Validation.Issue.VersionTooOld(_, _, _) => true
          case _                                       => false
        })
      case other => fail(s"expected version rejection, found $other")
    }
    Validation.validate(value, Version(9, 3), Target(70)) match {
      case ValidationOutcome.Rejected(errors) => assert(errors.exists {
          case Validation.Issue.TargetTooOld(_, _, _) => true
          case _                                      => false
        })
      case other => fail(s"expected target rejection, found $other")
    }
    assertEquals(
      Validation.validate(value, Version(9, 3), Target(80)),
      ValidationOutcome.Accepted
    )
  }

  test("conditional specification notes fail closed") {
    assertEquals(
      Validation.validate(
        instruction("add.u32 %r0, %r1, %r2;"),
        Version(9, 3),
        Target(100)
      ),
      ValidationOutcome.Incomplete(
        Vec.nonEmpty(Validation.Issue.Incomplete("syntax form for add.u32 is not fully normalized"))
      )
    )
  }

  test("unknown instructions remain explicit after parsing") {
    Parser.parseStatement("not_a_ptx_instruction.u32 %r0;") match {
      case Right(Statement.UnsupportedInstruction(opcode, operands, mods, None))
          if opcode == "not_a_ptx_instruction" =>
        assertEquals(operands.size, 1)
        assertEquals(mods.map(_.name), Vec("u32"))
      case other => fail(s"unexpected parse result: $other")
    }
  }
}
