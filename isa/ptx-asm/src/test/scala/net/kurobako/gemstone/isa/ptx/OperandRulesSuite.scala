package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*

class OperandRulesSuite extends munit.FunSuite {
  private def validate(source: String): ValidationOutcome[Validation.Issue] =
    Parser.parseStatement(source) match {
      case Right(Program.Statement.Instruction(value)) => Validation.validate(value, Version(99, 0), Target(999))
      case other                                       => fail(s"not an instruction: $source: $other")
    }

  private def violations(source: String): Vec[OperandViolation] = validate(source) match {
    case ValidationOutcome.Rejected(issues) =>
      issues.collect { case Validation.Issue.InvalidOperandValue(_, _, violation) => violation }
    case _ => Vec.empty
  }

  private def accepted(source: String): Unit = assertEquals(validate(source), ValidationOutcome.Accepted, source)

  private def rejected(source: String)(check: PartialFunction[OperandViolation, Unit]): Unit = {
    val found = violations(source)
    assert(found.nonEmpty, s"$source was not rejected for an operand value: ${validate(source)}")
    assert(found.exists(check.isDefinedAt), s"$source: $found")
  }

  test("every prose rule has a section of the document and a check that names its limits") {
    assert(OperandRules.rules.nonEmpty)
    assert(OperandRules.rules.forall(rule => rule.section.nonEmpty && rule.opcodes.nonEmpty))
    assertEquals(
      OperandRules.rules.map(_.kind).distinct.sorted.toList,
      List("multiple", "oneof", "pow2", "range")
    )
  }

  test("bit field operands are restricted to the 8-bit value range") {
    accepted("bfe.u32 %r0, %r1, 8, 255;")
    rejected("bfe.u32 %r0, %r1, 8, 256;") { case OperandViolation.OutOfRange(_, 0, 255) => () }
    rejected("bfe.u32 %r0, %r1, -1, 8;") { case OperandViolation.OutOfRange(_, 0, 255) => () }
    accepted("bfi.b32 %r0, %r1, %r2, 0, 255;")
    rejected("bfi.b32 %r0, %r1, %r2, 0, 256;") { case OperandViolation.OutOfRange(_, 0, 255) => () }
  }

  test("the bitmask width limit depends on the mode") {
    accepted("bmsk.clamp.b32 %r0, %r1, 32;")
    rejected("bmsk.clamp.b32 %r0, %r1, 33;") { case OperandViolation.OutOfRange(_, 0, 32) => () }
    accepted("bmsk.wrap.b32 %r0, %r1, 31;")
    rejected("bmsk.wrap.b32 %r0, %r1, 32;") { case OperandViolation.OutOfRange(_, 0, 31) => () }
  }

  test("performance monitor events, barriers and allocation sizes are limited") {
    accepted("pmevent 15;")
    rejected("pmevent 16;") { case OperandViolation.OutOfRange(_, 0, 15) => () }
    accepted("bar.sync 15;")
    rejected("bar.sync 16;") { case OperandViolation.OutOfRange(_, 0, 15) => () }
    accepted("barrier.sync 0, 64;")
    rejected("barrier.sync 0, 48;") { case OperandViolation.NotMultiple(_, 32) => () }
    accepted("tcgen05.alloc.cta_group::1.sync.aligned.b32 [%r0], 64;")
    rejected("tcgen05.alloc.cta_group::1.sync.aligned.b32 [%r0], 48;") { case OperandViolation.NotPowerOfTwo(_, None) =>
      ()
    }
    rejected("tcgen05.alloc.cta_group::1.sync.aligned.b32 [%r0], 1024;") {
      case OperandViolation.OutOfRange(_, 32, 512) => ()
    }
  }

  test("copy sizes, alignments and descriptor ordinals follow the prose rules") {
    accepted("cp.async.ca.shared.global [%r0], [%rd0], 8;")
    rejected("cp.async.ca.shared.global [%r0], [%rd0], 12;") { case OperandViolation.NotOneOf(_, _) => () }
    accepted("st.bulk [%rd0], 16, 0;")
    rejected("st.bulk [%rd0], 12, 0;") { case OperandViolation.NotMultiple(_, 8) => () }
    accepted("alloca.u64 %rd0, 16, 64;")
    rejected("alloca.u64 %rd0, 16, 48;") { case OperandViolation.NotPowerOfTwo(_, Some(8388608)) => () }
    accepted("tensormap.replace.tile.global_dim.global.b1024.b32 [%rd0], 4, %r1;")
    rejected("tensormap.replace.tile.global_dim.global.b1024.b32 [%rd0], 5, %r1;") {
      case OperandViolation.OutOfRange(_, 0, 4) => ()
    }
  }

  test("rules constrain immediates only") {
    accepted("bfe.u32 %r0, %r1, %r2, %r3;")
    accepted("pmevent.mask 65535;")
  }

  test("integer constants must fit the width of the operand type") {
    accepted("add.s32 %r0, %r1, -2147483648;")
    accepted("add.u32 %r0, %r1, 0xFFFFFFFF;")
    rejected("add.s32 %r0, %r1, 0x1FFFFFFFF;") { case OperandViolation.TooWide(_, "s32") => () }
    rejected("add.u16 %r0, %r1, 65536;") { case OperandViolation.TooWide(_, "u16") => () }
  }

  test("constants take the kind of the operand type") {
    accepted("add.f32 %f0, %f1, 0f3F800000;")
    accepted("add.f32 %f0, %f1, 1.5;")
    accepted("add.f64 %fd0, %fd1, 0d3FF0000000000000;")
    accepted("setp.eq.f32 %p0, %f1, 0;")
    rejected("add.f64 %fd0, %fd1, 0f3F800000;") { case OperandViolation.WrongKind(_, "f64") => () }
    rejected("add.s32 %r0, %r1, 0f3F800000;") { case OperandViolation.WrongKind(_, "s32") => () }
    rejected("add.s32 %r0, %r1, 1.5;") { case OperandViolation.WrongKind(_, "s32") => () }
  }

  test("constants follow the grammar of the document") {
    for (raw <- Vec("0x1F", "0b101", "017", "42U", "0f3F800000", "0d3FF0000000000000", "1.", ".5", "1e3", "2.5E-3"))
      assert(Literal.parse(raw).nonEmpty, raw)
    for (raw <- Vec("0f3F80", "0xZZ", "089", "0b102", "1.2.3"))
      assertEquals(Literal.parse(raw), None, raw)
    assertEquals(
      Literal.parse("0xFFFFFFFFFFFFFFFF"),
      Some(Literal.Integer(BigInt("18446744073709551615"), unsigned = true))
    )
    assertEquals(Literal.parse("5"), Some(Literal.Integer(5, false)))
  }

  test("types follow the fundamental, packed and alternate tables") {
    assertEquals(PtxTypes("b32").map(_.bits), Some(32))
    assertEquals(PtxTypes("f16x2").map(value => (value.elements, value.declared)), Some((2, Vec("f16x2", "b32"))))
    assertEquals(PtxTypes("e4m3x2").map(_.declared), Some(Vec("b16")))
    assertEquals(PtxTypes("bf16").map(_.declared), Some(Vec("b16")))
    assertEquals(PtxTypes("u8x4").map(_.declared), Some(Vec("b32")))
    assert(PtxTypes("pred").exists(_.predicate))
  }

  test("operand types agree by kind and size") {
    def agrees(instruction: String, operand: String) = PtxTypes.agrees(PtxTypes(instruction).get, PtxTypes(operand).get)
    assert(agrees("b32", "f32"))
    assert(agrees("s32", "u32"))
    assert(agrees("f32", "b32"))
    assert(!agrees("s32", "f32"))
    assert(!agrees("f32", "s32"))
    assert(!agrees("s32", "u64"))
  }

  test("relaxed rules widen the operands of ld, st and cvt only as the tables say") {
    def source(instruction: String, operand: String) = PtxTypes.source(PtxTypes(instruction).get, PtxTypes(operand).get)
    assertEquals(source("b32", "b64"), Some(Widening.Chop))
    assertEquals(source("b32", "b32"), Some(Widening.Exact))
    assertEquals(source("b32", "b16"), Some(Widening.Invalid))
    assertEquals(PtxTypes.destination(PtxTypes("s16").get, PtxTypes("s32").get), Some(Widening.Extend))
  }

  test("special registers carry their declared types") {
    assertEquals(PtxTypes.special.get("%tid.x").map(_.tpe.name), Some("u32"))
    assertEquals(PtxTypes.special.get("%gridid").map(_.tpe.name), Some("u64"))
    assertEquals(PtxTypes.special.get("%tid").map(_.vector), Some(true))
    assertEquals(PtxTypes.special.get("%is_explicit_cluster").map(_.tpe.name), Some("pred"))
  }

  test("operand types come from the table rows, then the names a, b, c and d") {
    def types(opcode: String, modifiers: Vec[String], names: Vec[String]) =
      PtxTypes.operandTypes(opcode, modifiers, names.map(Some(_))).map(_.map(_.name))
    assertEquals(types("shl", Vec("b32"), Vec("d", "a", "b")), Vec(Some("b32"), Some("b32"), Some("u32")))
    assertEquals(types("setp", Vec("eq", "s32"), Vec("p", "a", "b")), Vec(Some("pred"), Some("s32"), Some("s32")))
    assertEquals(types("popc", Vec("b64"), Vec("d", "a")), Vec(Some("u32"), Some("b64")))
    assertEquals(types("add", Vec("f32"), Vec("d", "a", "count")), Vec(Some("f32"), Some("f32"), None))
  }
}
