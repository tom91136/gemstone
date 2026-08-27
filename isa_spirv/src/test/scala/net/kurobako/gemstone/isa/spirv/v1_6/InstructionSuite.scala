package net.kurobako.gemstone.isa.spirv.v1_6

final class InstructionSuite extends munit.FunSuite {
  test("every grammar opcode decodes to exactly one typed enum case") {
    assertEquals(Instructions.size, 875)
    Instructions.foreach { definition =>
      val raw         = RawInstruction(definition.opcodeNumber, Instruction.minimumOperandWords(definition))
      val instruction = Instruction.decode(raw).fold(error => fail(s"${definition.opcode}: ${error.message}"), identity)
      assertEquals(instruction.definition, definition)
      assertEquals(instruction.opcode, definition.opcode)
      assertEquals(instruction.opcodeNumber, definition.opcodeNumber)
      assert(instruction.decoder eq definition.decoder)
      assertEquals(instruction.meta, definition.meta)
      assertEquals(Instruction.encode(instruction), raw)
    }
  }

  test("top-level definitions are singleton, complete, and uniquely keyed") {
    assertEquals(Instructions, InstructionDef.values.toVector)
    assertEquals(Instructions.map(_.opcodeNumber).distinct.size, Instructions.size)
    assertEquals(Instructions.map(_.opcode).distinct.size, Instructions.size)
  }

  test("a per-form decoder rejects a raw instruction for another definition") {
    assertEquals(
      InstructionDef.Nop.decoder(RawInstruction(1, Vector.empty)),
      Left(InstructionDecodeError.DecoderOpcodeMismatch(0, 1))
    )
  }

  test("unknown opcode is valid raw data but cannot inhabit the typed enum") {
    val raw = RawInstruction(65535, Vector(Word(1), Word(2)))
    assertEquals(Instruction.decode(raw), Left(InstructionDecodeError.UnknownOpcode(65535)))
  }

  test("constructor fields and common operands are the same values") {
    val resultType  = operand("IdResultType", 1)
    val dest        = operand("IdResult", 2)
    val srcA        = operand("IdRef", 3)
    val srcB        = operand("IdRef", 4)
    val instruction = Instruction.IAdd(resultType, dest, srcA, srcB)
    assertEquals(instruction.opcode, "OpIAdd")
    assertEquals(instruction.operands, Vector(resultType, dest, srcA, srcB))
  }

  private def operand(kind: String, value: Int): Operand = Operand(kind, Vector(Word(value)))
}
