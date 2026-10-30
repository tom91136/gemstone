package net.kurobako.gemstone.isa.ptx

import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.testing.Suite

class ValidationSuite extends Suite {
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
            case Validation.Issue.InvalidModifiers(_, _) => true
            case _                                       => false
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

  test("forms take their own domain definitions and destination-only register operands") {
    for (
      source <- Vec(
        "bfi.b32 %r0, %r1, %r2, 0, 8;",
        "redux.sync.add.s32 %r0, %r1, -1;",
        "redux.sync.and.b32 %r0, %r1, -1;",
        "redux.sync.max.u32 %r0, %r1, -1;"
      )
    ) assertEquals(
      validate(source),
      ValidationOutcome.Accepted,
      source
    )
    Validation.validate(instruction("redux.sync.add.b32 %r0, %r1, -1;"), Version(9, 3), Target(100)) match {
      case ValidationOutcome.Rejected(_) => ()
      case other                         => fail(s"bitwise-typed redux.sync.add accepted: $other")
    }
  }

  private def validate(source: String) = Validation.validate(instruction(source), Version(9, 3), Target(100))

  test("toolchain spellings of syntax forms validate") {
    for (
      source <- Vec(
        "mad.lo.cc.u32 %r0, %r1, %r2, %r3;",
        "madc.hi.cc.u32 %r0, %r1, %r2, %r3;",
        "mbarrier.arrive.expect_tx.shared::cta.b64 _, [%rd0], %r1;",
        "mbarrier.arrive.expect_tx.release.cluster.shared::cluster.b64 _, [%rd0], %r1;",
        "cp.async.bulk.tensor.2d.shared::cluster.global.tile.mbarrier::complete_tx::bytes [%r0], [%rd0, {%r1, %r2}], [%r3];",
        "cp.async.bulk.tensor.3d.shared::cta.global.im2col.mbarrier::complete_tx::bytes [%r0], [%rd0, {%r1, %r2, %r3}], [%r3], {%r4};",
        "ld.global.v4.b32 {%r0, %r1, %r2, %r3}, [%rd0];"
      )
    ) assertEquals(validate(source), ValidationOutcome.Accepted, source)
  }

  test("ldmatrix accepts shape, count and transpose in any order and keeps the rest ordered") {
    val operands = "{%r0, %r1, %r2, %r3}, [%rd0];"
    for (
      qualifiers <- Vec(
        "m8n8.x4.shared.b16",
        "x4.m8n8.shared.b16",
        "x4.trans.m8n8.shared.b16",
        "m8n8.x4.trans.shared::cta.b16",
        "trans.x4.m8n8.b16",
        "m8n8.x4.b16"
      )
    ) assertEquals(validate(s"ldmatrix.sync.aligned.$qualifiers $operands"), ValidationOutcome.Accepted, qualifiers)
    for (qualifiers <- Vec("m8n8.shared.b16", "m8n8.x4.shared", "b16.m8n8.x4", "x4.m8n8.x2.b16"))
      validate(s"ldmatrix.sync.aligned.$qualifiers $operands") match {
        case ValidationOutcome.Rejected(_) => ()
        case other                         => fail(s"ldmatrix.$qualifiers accepted: $other")
      }
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

  test("target notes restrict only the modifier combinations they name") {
    def outcome(source: String, target: Target) = Validation.validate(instruction(source), Version(9, 3), target)
    assertEquals(outcome("add.u32 %r0, %r1, %r2;", Target(20)), ValidationOutcome.Accepted)
    assertEquals(outcome("ld.global.v4.b32 {%r0, %r1, %r2, %r3}, [%rd0];", Target(80)), ValidationOutcome.Accepted)
    assertEquals(outcome("tanh.approx.f32 %f0, %f1;", Target(75)), ValidationOutcome.Accepted)
    assertEquals(outcome("cvt.rn.bf16.f32 %h0, %f0;", Target(80)), ValidationOutcome.Accepted)
    for (
      (source, target, required) <- Vec(
        ("ld.global.v8.b32 {%r0, %r1, %r2, %r3, %r4, %r5, %r6, %r7}, [%rd0];", Target(90), Target(100)),
        ("tanh.approx.bf16 %h0, %h1;", Target(80), Target(90)),
        ("cvt.rn.bf16.s32 %h0, %r0;", Target(80), Target(90)),
        ("add.u16x2 %r0, %r1, %r2;", Target(80), Target(90))
      )
    )
      outcome(source, target) match {
        case ValidationOutcome.Rejected(issues) => assert(
            issues.exists {
              case Validation.Issue.TargetTooOld(_, value, _) => value == required
              case _                                          => false
            },
            issues
          )
        case other => fail(s"$source accepted on ${target.render}: $other")
      }
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
