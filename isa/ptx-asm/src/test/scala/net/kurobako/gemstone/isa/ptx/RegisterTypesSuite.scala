package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.testing.*

class RegisterTypesSuite extends Suite {
  private def source(declarations: String, instructions: String): String =
    s""".version 9.0
       |.target sm_100
       |.address_size 64
       |.visible .entry kernel() {
       |  $declarations
       |  $instructions
       |}
       |""".stripMargin

  private def outcome(declarations: String, instructions: String): ValidationOutcome[Validation.Issue] =
    Parser.parse(source(declarations, instructions)) match {
      case Left(error)    => fail(error.diagnostic.raw)
      case Right(program) => Validation.validate(program, forms = true)
    }

  private def accepted(declarations: String, instructions: String): Unit =
    assertEquals(outcome(declarations, instructions), ValidationOutcome.Accepted, instructions)

  private def rejected(declarations: String, instructions: String)(checks: String*): Unit =
    outcome(declarations, instructions) match {
      case ValidationOutcome.Rejected(issues) =>
        val message = issues.raw.map(_.message.raw).mkString("; ")
        checks.foreach(check => assert(message.contains(check), s"$instructions: $message"))
      case other => fail(s"$instructions was not rejected: $other")
    }

  test("exact type checking rejects an integer register for a floating-point instruction") {
    rejected(".reg .u32 %r<2>; .reg .f32 %f;", "add.f32 %f, %r1, %f;")("operand 2", ".u32", ".f32")
    rejected(".reg .u32 r; .reg .f32 f;", "add.f32 f, r, f;")("operand 2", ".u32", ".f32")
  }

  test("exact type checking accepts a bit-size register wherever it has the instruction size") {
    accepted(".reg .b32 %r<2>; .reg .f32 %f;", "add.f32 %f, %r1, %f;")
    accepted(".reg .f32 %f; .reg .u32 %u;", "mov.b32 %f, %u;")
  }

  test("bit-size instructions reject registers of another size") {
    rejected(".reg .f64 %f; .reg .u32 %u;", "mov.b32 %f, %u;")("operand 1", ".f64", ".b32")
  }

  test("relaxed source operands may be wider than the instruction type") {
    accepted(".reg .b64 %b; .reg .u64 %rd;", "st.global.b32 [%rd], %b;")
    rejected(".reg .u32 %u; .reg .u64 %rd;", "st.global.f32 [%rd], %u;")("operand 2", ".u32", ".f32")
  }

  test("relaxed destination operands may be wider only when the tables allow it") {
    accepted(".reg .u64 %rd;", "ld.global.u32 %rd, [%rd];")
    rejected(".reg .u32 %u; .reg .u64 %rd;", "ld.global.u64 %u, [%rd];")("operand 1", ".u32", ".u64")
  }

  test("cvt checks its result against the first type and its source against the second") {
    accepted(".reg .u64 %d; .reg .u64 %a;", "cvt.u32.u64 %d, %a;")
    rejected(".reg .u16 %d; .reg .u64 %a;", "cvt.u32.u64 %d, %a;")("operand 1", ".u16", ".u32")
    accepted(".reg .b16 %d; .reg .f32 %a;", "cvt.rn.f16.f32 %d, %a;")
    rejected(".reg .u32 %d; .reg .f32 %a;", "cvt.rn.f16.f32 %d, %a;")("operand 1", ".u32", ".f16")
  }

  test("wide products take a destination twice the width of their sources") {
    accepted(".reg .u64 %d; .reg .u32 %a, %b;", "mul.wide.u32 %d, %a, %b;")
    rejected(".reg .u32 %d; .reg .u32 %a, %b;", "mul.wide.u32 %d, %a, %b;")("operand 1", ".u32", ".u64")
  }

  test("declarations are scoped to the block that holds them") {
    val nested =
      """.reg .b32 %r;
        |  {
        |    .reg .u32 %r;
        |    add.f32 %f, %r, %f;
        |  }""".stripMargin
    rejected(s".reg .f32 %f; $nested", "add.f32 %f, %f, %f;")("operand 2", ".u32")
    accepted(".reg .f32 %f; .reg .b32 %r;", "add.f32 %f, %r, %f;")
  }
}
