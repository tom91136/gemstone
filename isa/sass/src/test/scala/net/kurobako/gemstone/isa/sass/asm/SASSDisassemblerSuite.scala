package net.kurobako.gemstone.isa.sass.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction

class SASSDisassemblerSuite extends munit.FunSuite {
  test("text guards preserve negation and require the execution prefix") {
    val decoded = ListingDecoder.parse("kernel:\n/*0000*/ @!P0 NOP;", "kernel")
    assertEquals(
      decoded.map(_.head.instructions.head.guard),
      Right(Some(SASSInstruction.Guard(Operand.Predicate(Some(0), uniform = false, negated = true))))
    )
    assertEquals(
      ListingDecoder.parse("kernel:\n/*0000*/ !P0 NOP;", "kernel"),
      Left("invalid nvdisasm text: unrecognized instruction record in nvdisasm output at 9")
    )
  }
  test("text decoding rejects a negative instruction address") {
    val result = ListingDecoder.parse("kernel:\n/*-001*/ NOP;", "kernel")
    assert(result.isLeft)
  }

  test("text records preserve guards, braces, semicolons, encodings, and tab whitespace") {
    val text   = "kernel:\n.Ltarget:\n    /*0000*/ { @!P0 MOV\tR1, c[0x0] [0x20] ; /* 0x0000000000000010 */\n"
    val result = ListingDecoder.parse(text, "kernel")
    assertEquals(
      result.map(_.head.instructions),
      Right(Vec(
        SASSFixture.instruction(
          0,
          "MOV",
          Vec("R1", "c[0x0][0x20]"),
          Some("@!P0"),
          Some(16L)
        )
      ))
    )
  }

  test("typed parsing resolves a forward branch label to its instruction address") {
    val text =
      "kernel:\n" +
        "    /*0000*/ BRA `(.Ltarget);\n" +
        ".Ltarget:\n" +
        "    /*0010*/ @!P0 NOP; /* 0x0000000000000010 */\n"
    val decoded        = ListingDecoder.parse(text, "kernel", 86)
    val expectedBranch = SASSFixture.instruction(0, "BRA", Vec("0x10"))
    val expectedNop    = SASSFixture.instruction(
      16,
      "NOP",
      Vec.empty,
      Some("@!P0"),
      Some(16L)
    )
    assertEquals(decoded.map(_.head.instructions), Right(Vec(expectedBranch, expectedNop)))
  }

  test("consecutive and trailing labels bind to the following instruction address") {
    val text         = """kernel:
      /*0000*/ BRA `(.Lfirst);
      /*0010*/ BRA `(.Lsecond);
      .Lfirst:
      // labels may be separated by non-instruction lines
      .Lsecond:
      /*0020*/ BRA `(.Lend);
      .Lend:
      """
    val instructions = ListingDecoder.parse(text, "kernel", 89).fold(fail(_), identity).head.instructions
    assertEquals(instructions.map(_.operands), Vec(32, 32, 48).map(address => Vec[Operand](Operand.Bits(address, 0))))
  }

  for ((sm, slots) <- Vec(35 -> 7, 50 -> 3))
    test(s"SM$sm trailing labels skip the next bundle control word") {
      val records = Vec.tabulate(slots) { index =>
        val address     = (index + 1) * 8
        val instruction = if (index == 0) "BRA `(.Llast);"
        else if (index == slots - 1) "BRA `(.Lend);"
        else "NOP;"
        val label = if (index == slots - 1) ".Llast:\n" else ""
        f"$label/*$address%04x*/ $instruction"
      }
      val text         = "kernel:\n" + records.mkString("\n") + "\n.Lend:\n"
      val instructions = ListingDecoder.parse(text, "kernel", sm).fold(fail(_), identity).head.instructions
      assertEquals(instructions.head.operands, Vec[Operand](Operand.Bits(slots * 8, 0)))
      assertEquals(instructions.last.operands, Vec[Operand](Operand.Bits((slots + 2) * 8, 0)))
    }

  test("repeated listing labels retain the last definition") {
    val text         = "kernel:\n.Ltarget:\n/*0000*/ BRA `(.Ltarget);\n.Ltarget:\n/*0010*/ NOP;"
    val instructions = ListingDecoder.parse(text, "kernel", 89).fold(fail(_), identity).head.instructions
    assertEquals(instructions.head.operands, Vec[Operand](Operand.Bits(16, 0)))
  }

  test("typed parsing preserves P2R normalization and trailing-comma compatibility") {
    val bodies = Vec(
      "P2R R0, PR, RZ, 0x1;" ->
        (
          "P2R",
          Vec[Operand](
            Operand.Register(RegisterBank.R, 0),
            Operand.Register(RegisterBank.R, 255),
            Operand.Bits(BigInt(1), 0)
          )
        ),
      "P2R R0, PR, RZ, 0x1,;" ->
        (
          "P2R",
          Vec[Operand](
            Operand.Register(RegisterBank.R, 0),
            Operand.Register(RegisterBank.R, 255),
            Operand.Bits(BigInt(1), 0)
          )
        ),
      "MOV R0,R1,,,;" ->
        ("MOV", Vec[Operand](Operand.Register(RegisterBank.R, 0), Operand.Register(RegisterBank.R, 1))),
      "NOP ,,,;" -> ("NOP", Vec.empty[Operand])
    )
    bodies.foreach { case (body, (mnemonic, operands)) =>
      val parsed = ListingDecoder.parse(s"kernel:\n/*0000*/ $body", "kernel", 89)
      assertEquals(parsed.map(_.head.instructions.head.operation.mnemonic.raw), Right(mnemonic), clues(body))
      assertEquals(parsed.map(_.head.instructions.head.operation.operands), Right(operands), clues(body))
    }
  }

  test("typed parsing preserves normalized error offsets and missing-kernel diagnostics") {
    val invalidAddress = ListingDecoder.parse("kernel:\n/*0000*/ MOV R0,   [R1 ];", "kernel", 89)
    assertEquals(
      invalidAddress,
      Left("kernel+0x0 (sm_89): MOV R0, [R1 ]: invalid SASS operand list at 7")
    )
    val invalidHex = ListingDecoder.parse("kernel:\n/*0000*/ MOV R0 ,  0xZZ;", "kernel", 89)
    assertEquals(
      invalidHex,
      Left("kernel+0x0 (sm_89): MOV R0, 0xZZ: invalid SASS operand list at 6")
    )
    assertEquals(
      ListingDecoder.parse("kernel:\n/*0000*/ NOP;", "missing"),
      Left("nvdisasm did not return kernel missing")
    )
  }

  test("record guards accept the uniform prefix and require separation from the opcode") {
    val parsed = ListingDecoder.parse("kernel:\n/*0000*/ @UP0 NOP;", "kernel")
    assertEquals(
      parsed.map(_.head.instructions.head.guard),
      Right(Some(SASSInstruction.Guard(Operand.Predicate(Some(0), uniform = true, negated = false))))
    )
    assert(ListingDecoder.parse("kernel:\n/*0000*/ @P0NOP;", "kernel").isLeft)
    assert(ListingDecoder.parse("kernel:\n/*0000*/ NOP; trailing", "kernel").isLeft)
  }

  test("malformed addressed records and unresolved label references fail") {
    assert(ListingDecoder.parse("kernel:\n/*00xz*/ NOP;", "kernel").isLeft)
    assert(ListingDecoder.parse("kernel:\n/*0000*/ NOP", "kernel").isLeft)
    assert(ListingDecoder.parse("kernel:\n/*0000*/ BRA `(missing);", "kernel").isLeft)
  }
}
