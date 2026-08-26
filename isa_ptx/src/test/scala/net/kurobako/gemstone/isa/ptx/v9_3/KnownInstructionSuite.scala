package net.kurobako.gemstone.isa.ptx.v9_3

final class KnownInstructionSuite extends munit.FunSuite {
  test("every documented opcode has a generated typed case and lossless syntax conversion") {
    val mnemonics = Isa.catalog.instructions.flatMap(_.mnemonics).distinct.sorted

    assertEquals(mnemonics.size, 129)
    mnemonics.foreach { mnemonic =>
      val syntax = Instruction(
        Opcode(mnemonic),
        Vector(Operand.Register("%r0", None)),
        Vector(Modifier("test")),
        Some(Predication(false, Operand.Register("%p0", None)))
      )
      val known = KnownInstruction.fromSyntax(syntax).fold(error => fail(s"$mnemonic: $error"), identity)
      assertEquals(known.name, mnemonic)
      assertEquals(known.syntax, syntax)
    }
  }

  test("an unknown opcode is not coerced into a known instruction") {
    val syntax = Instruction(Opcode("gemstone_unknown"))
    assertEquals(
      KnownInstruction.fromSyntax(syntax),
      Left(InstructionDecodeError.UnknownOpcode("gemstone_unknown"))
    )
  }
}
