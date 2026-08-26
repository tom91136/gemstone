package net.kurobako.gemstone.isa.spirv.v1_6

final class KnownInstructionSuite extends munit.FunSuite {
  test("every grammar opcode has a generated typed case and lossless syntax conversion") {
    assertEquals(InstructionCatalog.instructions.size, 875)
    InstructionCatalog.instructions.foreach { info =>
      val syntax = Instruction(info.opcode, Vector(Word(1), Word(2), Word(-1)))
      val known  = KnownInstruction.fromSyntax(syntax).fold(error => fail(s"${info.name}: $error"), identity)
      assertEquals(known.name, info.name)
      assertEquals(known.opcode, info.opcode)
      assertEquals(known.syntax, syntax)
    }
  }

  test("an unknown opcode is not coerced into a known instruction") {
    val syntax = Instruction(Opcode.unsafe(65535), Vector.empty)
    assertEquals(
      KnownInstruction.fromSyntax(syntax),
      Left(InstructionDecodeError.UnknownOpcode("65535"))
    )
  }
}
