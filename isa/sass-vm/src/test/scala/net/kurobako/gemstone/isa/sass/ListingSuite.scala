package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.either.*

class ListingSuite extends Suite {
  private def parse(text: String, sm: Int = 86): Either[String, Vec[DecodedInstruction]] =
    Listing.parse(text, "kernel", sm)

  private def statement(text: String, address: Int = 0, word: Option[BigInt] = None): DecodedInstruction =
    Listing.program(text).value.instructions.head.copy(address = address.unsafe, encodingWord = word)

  test("text guards preserve negation and require the execution prefix") {
    assertEquals(
      parse("kernel:\n/*0000*/ @!P0 NOP;").map(_.head.guard),
      Right(Some(Instruction.Guard(Operand.Predicate(Some(0), uniform = false, negated = true))))
    )
    assertEquals(parse("kernel:\n/*0000*/ @!PT NOP;").map(_.head.guard.map(_.predicate.negated)), Right(Some(true)))
    assertEquals(
      parse("kernel:\n/*0000*/ !P0 NOP;"),
      Left("invalid nvdisasm text: unrecognised instruction record in nvdisasm output at 9")
    )
  }

  test("text decoding rejects a negative instruction address") {
    assert(parse("kernel:\n/*-001*/ NOP;").isLeft)
  }

  test("text records preserve guards, braces, semicolons, encodings, and tab whitespace") {
    val text =
      "kernel:\n.Ltarget:\n    /*0000*/ { @!P0 MOV\tR1, c[0x0] [0x20] ; /* 0x0000000000000010 */\n /* 0x0000000000000001 */\n"
    assertEquals(parse(text), Right(Vec(statement("@!P0 MOV R1, c[0x0][0x20]", word = Some((BigInt(1) << 64) | 16)))))
  }

  test("typed parsing resolves a forward branch label to its instruction address") {
    val text = "kernel:\n    /*0000*/ BRA `(.Ltarget);\n.Ltarget:\n    /*0010*/ @!P0 NOP;\n"
    assertEquals(parse(text), Right(Vec(statement("BRA 0x10"), statement("@!P0 NOP", 16))))
  }

  test("consecutive and trailing labels bind to the following instruction address") {
    val text = """kernel:
      /*0000*/ BRA `(.Lfirst);
      /*0010*/ BRA `(.Lsecond);
      .Lfirst:
      // labels may be separated by non-instruction lines
      .Lsecond:
      /*0020*/ BRA `(.Lend);
      .Lend:
      """
    assertEquals(
      parse(text, 89).value.map(_.operands),
      Vec(32, 32, 48).map(address => Vec[Operand](Operand.Bits(address, 0)))
    )
  }

  for ((sm, slots) <- Vec((35, 7), (50, 3)))
    test(s"SM$sm trailing labels skip the next bundle control word") {
      val records = Vec.tabulate(slots) { index =>
        val instruction =
          if (index == 0) "BRA `(.Llast);"
          else if (index == slots - 1) "BRA `(.Lend);"
          else "NOP;"
        val label = if (index == slots - 1) ".Llast:\n" else ""
        f"$label/*${(index + 1) * 8}%04x*/ $instruction"
      }
      val instructions = parse("kernel:\n" + records.mkString("\n") + "\n.Lend:\n", sm).value
      assertEquals(instructions.head.operands, Vec[Operand](Operand.Bits(slots * 8, 0)))
      assertEquals(instructions.last.operands, Vec[Operand](Operand.Bits((slots + 2) * 8, 0)))
    }

  test("repeated listing labels retain the last definition") {
    val instructions = parse("kernel:\n.Ltarget:\n/*0000*/ BRA `(.Ltarget);\n.Ltarget:\n/*0010*/ NOP;", 89).value
    assertEquals(instructions.head.operands, Vec[Operand](Operand.Bits(16, 0)))
  }

  test("typed parsing preserves P2R normalisation and trailing-comma compatibility") {
    val p2r = Vec[Operand](
      Operand.Register(RegisterBank.R, 0),
      Operand.Register(RegisterBank.R, 255),
      Operand.Bits(BigInt(1), 0)
    )
    Vec(
      "P2R R0, PR, RZ, 0x1;"  -> ("P2R", p2r),
      "P2R R0, PR, RZ, 0x1,;" -> ("P2R", p2r),
      "MOV R0,R1,,,;"         ->
        ("MOV", Vec[Operand](Operand.Register(RegisterBank.R, 0), Operand.Register(RegisterBank.R, 1))),
      "NOP ,,,;" -> ("NOP", Vec.empty[Operand])
    ).foreach { case (body, (mnemonic, operands)) =>
      val parsed = parse(s"kernel:\n/*0000*/ $body", 89)
      assertEquals(parsed.map(_.head.operation.mnemonic.raw), Right(mnemonic), clues(body))
      assertEquals(parsed.map(_.head.operation.operands), Right(operands), clues(body))
    }
  }

  test("typed parsing preserves normalised error offsets and missing-kernel diagnostics") {
    assertEquals(
      parse("kernel:\n/*0000*/ MOV R0,   [R1 ];", 89),
      Left("kernel+0x0 (sm_89): MOV R0, [R1 ]: invalid SASS operand list at 7")
    )
    assertEquals(
      parse("kernel:\n/*0000*/ MOV R0 ,  0xZZ;", 89),
      Left("kernel+0x0 (sm_89): MOV R0, 0xZZ: invalid SASS operand list at 6")
    )
    assertEquals(Listing.parse("kernel:\n/*0000*/ NOP;", "missing", 86), Left("nvdisasm did not return kernel missing"))
  }

  test("unsupported instructions preserve the kernel, architecture, offset and opcode in diagnostics") {
    assert(parse(
      "kernel:\n/*0000*/ NOP;\n/*0010*/ NOP;\n/*0020*/ UNSUPPORTED;"
    ).left.exists(_.contains("kernel+0x20 (sm_86): UNSUPPORTED")))
  }

  test("record guards accept the uniform prefix and require separation from the opcode") {
    assertEquals(
      parse("kernel:\n/*0000*/ @UP0 NOP;").map(_.head.guard),
      Right(Some(Instruction.Guard(Operand.Predicate(Some(0), uniform = true, negated = false))))
    )
    assert(parse("kernel:\n/*0000*/ @P0NOP;").isLeft)
    assert(parse("kernel:\n/*0000*/ NOP; trailing").isLeft)
  }

  test("malformed addressed records and unresolved label references fail") {
    assert(parse("kernel:\n/*00xz*/ NOP;").isLeft)
    assert(parse("kernel:\n/*0000*/ NOP").isLeft)
    assert(parse("kernel:\n/*0000*/ BRA `(missing);").isLeft)
  }

  test("text disassembly preserves zero-fill operands, register banks and local branch targets") {
    val text = """kernel:
      |/*0000*/ @P0 LDG.E R3, [R4], !P1;
      |/*0010*/ P2R R6, PR, RZ, 0x78;
      |/*0020*/ BRA `(.L_end);
      |.L_end:
      |/*0030*/ RET.ABS R8 0x20;
      |""".stripMargin
    val decoded = parse(text).value
    assertEquals(decoded.head.operands, operands("R3, [R4], !P1"))
    assertEquals(
      decoded.head.guard,
      Some(Instruction.Guard(Operand.Predicate(Some(0), uniform = false, negated = false)))
    )
    assertEquals(decoded(1).operands, operands("R6, RZ, 0x78"))
    assertEquals(decoded(2).operands, operands("0x30"))
    assertEquals(decoded(3).operands, operands("R8, 0x20"))
    assert(parse(text.replace("/*0010*/", "/*0018*/")).isLeft)
  }

  test("nvdisasm hex output preserves the full FSEL instruction word") {
    val text = """kernel:
      |        /*0000*/                   FSEL R13, RZ, +QNAN , P0 ; /* 0x7ff80000ff0d7808 */
      |                                                   /* 0x000fc60001000000 */
      |        /*0010*/                   EXIT ; /* 0x000000000000794d */
      |                                                   /* 0x000fea0003800000 */
      |""".stripMargin
    assertEquals(parse(text).value.head.encodingWord, Some(BigInt("000fc600010000007ff80000ff0d7808", 16)))
  }

  test("preserved vendor modifiers and operand qualifiers do not acquire execution semantics") {
    Vec("MOV.FUTURE R0, R1", "MOV R0, R1.FUTURE", "MOV R0, XR1").foreach { source =>
      assert(Listing.program(source).flatMap(function => Kernel(86, "kernel", Vec(function)).lower()).isLeft, source)
    }
  }
}
