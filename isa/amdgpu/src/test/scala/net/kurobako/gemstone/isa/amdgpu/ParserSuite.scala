package net.kurobako.gemstone.isa.amdgpu

import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class ParserSuite extends munit.FunSuite {
  private def roundTrip(source: String): Unit = {
    val first = Parser.parseInstruction(source).fold(error => fail(error.toString), value => value)
    val text  = Printer.render(first)
    val next  = Parser.parseInstruction(text).fold(error => fail(error.toString), value => value)
    assertEquals(Printer.render(next), text)
  }

  test("parse and canonically print typed operands") {
    val instruction =
      Parser.parseInstruction("V_ADD_F32_e32 v0, |v1|, -v2 clamp").fold(error => fail(error.toString), value => value)
    assertEquals(instruction.opcode, "v_add_f32")
    assertEquals(instruction.operands.size, 3)
    assertEquals(
      instruction.operands,
      Vec(
        Operand.Register(RegisterFile.VectorRegister, 0, 0),
        Operand.Absolute(Operand.Register(RegisterFile.VectorRegister, 1, 1)),
        Operand.Negated(Operand.Register(RegisterFile.VectorRegister, 2, 2))
      )
    )
    assertEquals(
      instruction.mods,
      Seq(Modifier("e32"), Modifier("clamp"))
    )
    assertEquals(Printer.render(instruction), "v_add_f32 v0, |v1|, -v2 e32 clamp")
  }

  test("parse register ranges, literals, expressions, and named modifiers") {
    roundTrip("image_atomic_add v[4:5], v192, s[28:35] dmask:0x3 unorm glc")
    roundTrip("v_pk_fmac_f16_dpp v5, v1, v2 quad_perm:[0,1,2,3] row_mask:0xf bank_mask:0xf")
    roundTrip("s_mov_b32 flat_scratch_lo, -0x2a")
    roundTrip("v_pk_add_f32 v4, v8, v12 neg_lo:[1,0] neg_hi:[1,0]")
  }

  test("parse programs without retaining comments") {
    val source = ".text\nentry:\n  s_nop 0 // delay\n  s_endpgm"
    val parsed = Parser.parse(source).fold(error => fail(error.toString), value => value)
    assertEquals(Printer.render(parsed), ".text\nentry:\ns_nop 0\ns_endpgm")
  }

  test("operand parsing preserves register identity, numeric value, and expression structure") {
    val cases = Seq(
      "s[4:4]"        -> Operand.Register(RegisterFile.Scalar, 4, 4),
      "v[4:5]"        -> Operand.Register(RegisterFile.VectorRegister, 4, 5),
      "a12"           -> Operand.Register(RegisterFile.Accumulator, 12, 12),
      "ttmp3"         -> Operand.Register(RegisterFile.TrapTemporary, 3, 3),
      "EXEC_LO"       -> Operand.SpecialRegister("exec_lo"),
      "0"             -> Operand.IntegerLiteral(BigInt(0), 10),
      "-42"           -> Operand.IntegerLiteral(BigInt(-42), 10),
      "+42"           -> Operand.IntegerLiteral(BigInt(42), 10),
      "0X0F"          -> Operand.IntegerLiteral(BigInt(15), 16),
      "-0x2a"         -> Operand.IntegerLiteral(BigInt(-42), 16),
      "+0x2a"         -> Operand.IntegerLiteral(BigInt(42), 16),
      "1.25"          -> Operand.DecimalLiteral("1.25"),
      "-1.25"         -> Operand.DecimalLiteral("-1.25"),
      "target"        -> Operand.Identifier("target"),
      "target.offset" -> Operand.Identifier("target.offset"),
      "(target + 4)"  -> Operand.Expression("(target + 4)"),
      "(lo:hi)"       -> Operand.Expression("(lo:hi)")
    )
    cases.foreach { (source, expected) =>
      val instruction = Parser.parseInstruction(s"s_mov_b32 s0, $source").fold(error => fail(error.toString), identity)
      assertEquals(instruction.operands, Vec(Operand.Register(RegisterFile.Scalar, 0, 0), expected), clues(source))
      assertEquals(instruction.mods, Vec.empty[Modifier], clues(source))
    }
  }

  test("named and flag modifiers stay distinct from operands") {
    val instruction = Parser.parseInstruction("v_mov_b32 v0, v1 QUAD_PERM:[0,1,2,3] CLAMP CuStOm Opaque(MySymbol)")
      .fold(error => fail(error.toString), identity)
    assertEquals(
      instruction.operands,
      Vec(
        Operand.Register(RegisterFile.VectorRegister, 0, 0),
        Operand.Register(RegisterFile.VectorRegister, 1, 1)
      )
    )
    assertEquals(
      instruction.mods,
      Vec(
        Modifier("quad_perm", Some("[0,1,2,3]")),
        Modifier("clamp"),
        Modifier("custom"),
        Modifier("Opaque(MySymbol)")
      )
    )
  }

  test("nested source modifiers retain their order and register identity") {
    val register = Operand.Register(RegisterFile.VectorRegister, 1, 1)
    Seq(
      "-|v1|" -> Operand.Negated(Operand.Absolute(register)),
      "|-v1|" -> Operand.Absolute(Operand.Negated(register)),
      "--v1"  -> Operand.Negated(Operand.Negated(register))
    ).foreach { (source, expected) =>
      val instruction = Parser.parseInstruction(s"v_mov_b32 v0, $source").fold(error => fail(error.toString), identity)
      assertEquals(instruction.operands, Vec(Operand.Register(RegisterFile.VectorRegister, 0, 0), expected))
      assertEquals(instruction.mods, Vec.empty[Modifier])
    }
  }

  test("uninterpreted expressions remain intact instead of becoming partial numbers or register ranges") {
    Seq("0x", "0x1G", "v[+1:2]", "v[1:+2]", "|v1", "v1|", "||", "-").foreach { source =>
      val instruction = Parser.parseInstruction(s"s_mov_b32 s0, $source").fold(error => fail(error.toString), identity)
      assertEquals(
        instruction.operands,
        Vec(Operand.Register(RegisterFile.Scalar, 0, 0), Operand.Expression(source)),
        clues(source)
      )
      assertEquals(instruction.mods, Vec.empty[Modifier], clues(source))
    }
  }

  test("colons nested in parentheses or brackets remain operand text") {
    Seq("[lo:hi]", "(lo:hi)", "[base + (hi:lo)]", "(base + [hi:lo])").foreach { source =>
      val instruction = Parser.parseInstruction(s"s_mov_b32 s0, $source").fold(error => fail(error.toString), identity)
      assertEquals(
        instruction.operands,
        Vec(Operand.Register(RegisterFile.Scalar, 0, 0), Operand.Expression(source)),
        clues(source)
      )
      assertEquals(instruction.mods, Vec.empty[Modifier], clues(source))
    }
  }

  test("programs preserve directives, labels, and instruction pairs while dropping whole comments") {
    val source =
      "# comment # again\n// comment // again\n\n.text\n.p2align 8\nentry:\ns_nop 0 :: s_endpgm // stop # ignored"
    val nop = Parser.parseInstruction("s_nop 0").fold(error => fail(error.toString), identity)
    val end = Parser.parseInstruction("s_endpgm").fold(error => fail(error.toString), identity)
    assertEquals(
      Parser.parse(source),
      Right(Program(Vec(
        Statement.Directive(".text", ""),
        Statement.Directive(".p2align", "8"),
        Statement.Label("entry"),
        Statement.InstructionPair(nop, end)
      )))
    )
    assert(Parser.parseInstruction("").isLeft)
    assert(Parser.parseInstruction("s_nop 0\ns_endpgm").isLeft)
    assert(Parser.parseInstruction("s_nop").isLeft)
    assert(Parser.parse("1bad:").isLeft)
    assert(Parser.parse(".1bad").isLeft)
  }

  test("reject every unmatched, unclosed, or crossed delimiter and trailing empty operand") {
    Seq("v0)", "v0]", "(v0", "[v0", "([v0)]", "v0,").foreach { operand =>
      assert(Parser.parseInstruction(s"s_mov_b32 s0, $operand").isLeft, clues(operand))
    }
    assert(Parser.parseInstruction("s_mov_b32 s0, ([target + 4])").isRight)
  }

  test("reject malformed delimiters and empty operands") {
    assert(Parser.parseInstruction("v_add_f32 v0,, v1").isLeft)
    assert(Parser.parseInstruction("v_add_f32 v[0:1, v1").isLeft)
    assert(Parser.parseInstruction("1bad v0").isLeft)
    assert(Parser.parseInstruction("gemstone_unknown v0").isLeft)
  }

  test("do not throw on register numbers that do not fit the register index") {
    val sources = Seq(
      "v_mov_b32 v999999999999, v0",
      "v_mov_b32 v[0:999999999999], v0"
    )
    sources.foreach { source =>
      try Parser.parseInstruction(source)
      catch case error: NumberFormatException => fail(s"parser overflowed while reading '$source': $error")
    }
  }
  test("errors retain line and column through operand splitting") {
    val cases = Seq(
      ("v_add_f32 v0,, v1", 4, "empty operand"),
      ("v_add_f32 v0], v1", 3, "unmatched closing delimiter"),
      ("v_add_f32 v[0:1, v1", 9, "unclosed delimiter")
    )
    cases.foreach { (instruction, column, detail) =>
      val error = Parser.parse(s".text\ns_nop 0\n$instruction").swap.toOption.get
      assertEquals(error.line.raw, 3)
      assertEquals(error.column.raw, column)
      assertEquals(error.message, detail)
    }
  }

}
