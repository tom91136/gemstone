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

  test("matrix fragments are held in packed or wide registers, not element types") {
    accepted(
      ".reg .b32 %r<9>;",
      "mma.sync.aligned.m16n8k16.row.col.f16.f16.f16.f16 {%r1,%r2}, {%r3,%r4,%r5,%r6}, {%r7,%r8}, {%r1,%r2};"
    )
    accepted(
      ".reg .b32 %r<14>;",
      "mma.sp.sync.aligned.m16n8k16.row.col.f32.f16.f16.f32 {%r1,%r2,%r3,%r4}, {%r5,%r6}, {%r7,%r8}, {%r9,%r10,%r11,%r12}, %r13, 0x0;"
    )
    accepted(
      ".reg .b32 %r<21>;",
      "wmma.mma.sync.aligned.row.col.m16n16k16.s32.s8.s8.s32 {%r1, %r2, %r3, %r4, %r5, %r6, %r7, %r8}, {%r9, %r10}, {%r11, %r12}, {%r13, %r14, %r15, %r16, %r17, %r18, %r19, %r20};"
    )
    accepted(".reg .b32 %r<2>;", "movmatrix.sync.aligned.m8n8.trans.b16 %r1, %r2;")
  }

  test("mov packs and unpacks vector elements at the instruction bit size") {
    accepted(".reg .b32 %r<3>; .reg .b64 %rd;", "mov.b64 %rd, {%r1, %r2}; mov.b64 {%r1, %r2}, %rd;")
    accepted(".reg .b16 %rs<3>; .reg .b32 %r;", "mov.b32 %r, {%rs1, %rs2}; mov.b32 {%rs1, %rs2}, %r;")
    accepted(".reg .b8 %rb<5>; .reg .b32 %r;", "mov.b32 %r, {%rb1, %rb2, %rb3, %rb4};")
  }

  test("barrier reductions take the prose types, not the type of their predicate result") {
    accepted(".reg .pred %p<3>; .reg .b32 %r<2>;", "bar.red.or.pred %p1, 0, %r1, %p2;")
    accepted(".reg .pred %p<3>; .reg .b32 %r<2>;", "bar.red.or.pred %p1, 0, %p2; bar.red.popc.u32 %r1, 0, %p2;")
    accepted(".reg .pred %p; .reg .b32 %r<2>;", "bar.red.popc.u32 %r1, 0, %r2, %p;")
  }

  test("match writes a 32-bit mask whatever its comparison type") {
    accepted(".reg .b32 %r<3>; .reg .b64 %rd;", "match.any.sync.b64 %r1, %rd, %r2;")
    accepted(".reg .b32 %r<3>; .reg .b64 %rd; .reg .pred %p;", "match.all.sync.b64 %r1|%p, %rd, %r2;")
  }

  test("cache-qualified definitions use the relaxed load rules") {
    accepted(".reg .b16 %rs<2>; .reg .b64 %rd<2>;", "ld.global.nc.b8 %rs1, [%rd1];")
  }

  test("cvt.pack names its own operand types") {
    accepted(".reg .b32 %r<5>;", "cvt.pack.sat.u8.s32.b32 %r1, %r2, %r3, %r4;")
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
