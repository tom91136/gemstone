package net.kurobako.gemstone.isa.amdgpu

import assembly.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.isa.amdgpu.asm.Program.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.text.*

class ParserSuite extends Suite {
  private def roundTrip(source: String): Unit = {
    val first = parse(source)
    val text  = Printer.render(first)
    val next  = parse(text)
    assertEquals(Printer.render(next), text)
  }

  test("parse and canonically print typed operands") {
    val instruction =
      parse("V_ADD_F32_e32 v0, |v1|, -v2 clamp")
    assertEquals(instruction.opcode, "v_add_f32")
    assertEquals(instruction.operands.size, 3)
    assertEquals(
      instruction.operands,
      Vec(
        Operand.Register(RegisterFile.VectorRegister, first = 0, last = 0),
        Operand.Absolute(Operand.Register(RegisterFile.VectorRegister, first = 1, last = 1)),
        Operand.Negated(Operand.Register(RegisterFile.VectorRegister, first = 2, last = 2))
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

  test("program comments survive canonical printing") {
    val source = ".text\nentry:\n  s_nop 0 // delay\n  s_endpgm"
    val parsed = Parser.parse(source).value
    assertEquals(Printer.render(parsed), ".text\nentry:\ns_nop 0\n// delay\ns_endpgm")
    assertEquals(Parser.parse(Printer.render(parsed)), Right(parsed))
    val quoted = Parser.parse(".ascii \"http://gpu#source\" # retained").value
    assertEquals(
      quoted.statements,
      Vec(Statement.Directive(".ascii", "\"http://gpu#source\""), Statement.Comment("# retained"))
    )
    assertEquals(Parser.parse(Printer.render(quoted)), Right(quoted))
  }

  test("operand parsing preserves register identity, numeric value, and expression structure") {
    val cases = Seq(
      "s[4:4]"        -> Operand.Register(RegisterFile.Scalar, first = 4, last = 4),
      "v[4:5]"        -> Operand.Register(RegisterFile.VectorRegister, first = 4, last = 5),
      "a12"           -> Operand.Register(RegisterFile.Accumulator, first = 12, last = 12),
      "ttmp3"         -> Operand.Register(RegisterFile.TrapTemporary, first = 3, last = 3),
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
      val instruction = parse(s"s_mov_b32 s0, $source")
      assertEquals(
        instruction.operands,
        Vec(Operand.Register(RegisterFile.Scalar, first = 0, last = 0), expected),
        clues(source)
      )
      assertEquals(instruction.mods, Vec.empty[Modifier], clues(source))
    }
  }

  test("named and flag modifiers stay distinct from operands") {
    val instruction = parse("v_mov_b32 v0, v1 QUAD_PERM:[0,1,2,3] CLAMP CuStOm Opaque(MySymbol)")
    assertEquals(
      instruction.operands,
      Vec(
        Operand.Register(RegisterFile.VectorRegister, first = 0, last = 0),
        Operand.Register(RegisterFile.VectorRegister, first = 1, last = 1)
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
    val register = Operand.Register(RegisterFile.VectorRegister, first = 1, last = 1)
    Seq(
      "-|v1|" -> Operand.Negated(Operand.Absolute(register)),
      "|-v1|" -> Operand.Absolute(Operand.Negated(register)),
      "--v1"  -> Operand.Negated(Operand.Negated(register))
    ).foreach { (source, expected) =>
      val instruction = parse(s"v_mov_b32 v0, $source")
      assertEquals(
        instruction.operands,
        Vec(Operand.Register(RegisterFile.VectorRegister, first = 0, last = 0), expected)
      )
      assertEquals(instruction.mods, Vec.empty[Modifier])
    }
  }

  test("uninterpreted text and colons nested in delimiters remain whole expression operands") {
    Seq(
      "0x",
      "0x1G",
      "v[+1:2]",
      "v[1:+2]",
      "|v1",
      "v1|",
      "||",
      "-",
      "[lo:hi]",
      "(lo:hi)",
      "[base + (hi:lo)]",
      "(base + [hi:lo])"
    ).foreach { source =>
      val instruction = parse(s"s_mov_b32 s0, $source")
      assertEquals(
        instruction.operands,
        Vec(Operand.Register(RegisterFile.Scalar, first = 0, last = 0), Operand.Expression(source)),
        clues(source)
      )
      assertEquals(instruction.mods, Vec.empty[Modifier], clues(source))
    }
  }

  test("programs preserve directives, labels, instruction pairs, and whole comments") {
    val source =
      "# comment # again\n// comment // again\n\n.text\n.p2align 8\nentry:\ns_nop 0 :: s_endpgm // stop # ignored"
    val nop = parse("s_nop 0")
    val end = parse("s_endpgm")
    assertEquals(
      Parser.parse(source),
      Right(Program(Vec(
        Statement.Comment("# comment # again"),
        Statement.Comment("// comment // again"),
        Statement.Directive(".text", ""),
        Statement.Directive(".p2align", "8"),
        Statement.Label("entry"),
        Statement.InstructionPair(nop, end),
        Statement.Comment("// stop # ignored")
      )))
    )
    assert(parseInstruction("").isLeft)
    assert(parseInstruction("s_nop 0\ns_endpgm").isLeft)
    assert(parseInstruction("s_nop").isLeft)
    assert(Parser.parse("1bad:").isLeft)
    assert(Parser.parse(".1bad").isLeft)
  }

  test("reject every unmatched, unclosed, or crossed delimiter and trailing empty operand") {
    Seq("v0)", "v0]", "(v0", "[v0", "([v0)]", "v0,").foreach { operand =>
      assert(parseInstruction(s"s_mov_b32 s0, $operand").isLeft, clues(operand))
    }
    assert(parseInstruction("s_mov_b32 s0, ([target + 4])").isRight)
    assert(parseInstruction("gemstone_unknown v0").isLeft)
  }

  test("do not throw on register numbers that do not fit the register index") {
    val sources = Seq(
      "v_mov_b32 v999999999999, v0",
      "v_mov_b32 v[0:999999999999], v0"
    )
    sources.foreach { source =>
      try parseInstruction(source)
      catch case error: NumberFormatException => fail(s"parser overflowed while reading '$source': $error")
    }
  }
  test("errors retain line and column through operand splitting") {
    val cases = Seq(
      ("v_add_f32 v0,, v1", 14, "empty operand"),
      ("v_add_f32 v0], v1", 13, "unmatched closing delimiter"),
      ("v_add_f32 v[0:1, v1", 12, "unclosed delimiter"),
      ("  1bad v0", 3, "invalid AMDGPU opcode '1bad'"),
      ("s_nop 0 :: 1bad v0", 12, "invalid AMDGPU opcode '1bad'")
    )
    cases.foreach { (instruction, column, detail) =>
      val error = Parser.parse(s".text\ns_nop 0\n$instruction").leftValue
      assertEquals(error.position, SourcePosition(3, column.unsafe[Int > 0]), instruction)
      assertEquals(error.message, detail)
    }
  }

}
