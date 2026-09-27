package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.NonEmptyVec

import Program.*

import ISA.*

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.ValidationOutcome

final class ValidationSuite extends munit.FunSuite {
  private def instruction(source: String): Instruction =
    Parser.parseStatement(source).toOption.get match {
      case Statement.Instruction(value) => value
      case other                        => fail(s"expected instruction, found $other")
    }

  test("typed forms enforce modifier groups after decoding enforces operand counts") {
    assertEquals(
      Validator.validate(
        instruction("mul.wide.u32 %rd0, %r1, 4;"),
        Version(9, 3),
        Target(100)
      ),
      ValidationOutcome.Accepted
    )
    Validator.validate(
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
    Validator.validate(value, Version(9, 2), Target(100)) match {
      case ValidationOutcome.Rejected(errors) => assert(errors.exists {
          case Validator.Issue.VersionTooOld(_, _, _) => true
          case _                                      => false
        })
      case other => fail(s"expected version rejection, found $other")
    }
    Validator.validate(value, Version(9, 3), Target(70)) match {
      case ValidationOutcome.Rejected(errors) => assert(errors.exists {
          case Validator.Issue.TargetTooOld(_, _, _) => true
          case _                                     => false
        })
      case other => fail(s"expected target rejection, found $other")
    }
    assertEquals(
      Validator.validate(value, Version(9, 3), Target(80)),
      ValidationOutcome.Accepted
    )
  }

  test("conditional specification notes fail closed") {
    assertEquals(
      Validator.validate(
        instruction("add.u32 %r0, %r1, %r2;"),
        Version(9, 3),
        Target(100)
      ),
      ValidationOutcome.Incomplete(
        Vec.nonEmpty(Validator.Issue.Incomplete("syntax form for add.u32 is not fully normalized"))
      )
    )
  }

  test("unknown instructions remain explicit after parsing") {
    Parser.parseStatement("not_a_ptx_instruction.u32 %r0;") match {
      case Right(Statement.UnsupportedInstruction(opcode, operands, mods, None))
          if opcode == NonEmptyString("not_a_ptx_instruction") =>
        assertEquals(operands.size, 1)
        assertEquals(mods.map(_.name.raw).toSeq, Seq("u32"))
      case other => fail(s"unexpected parse result: $other")
    }
  }
}
