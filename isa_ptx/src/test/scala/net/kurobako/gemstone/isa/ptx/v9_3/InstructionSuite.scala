package net.kurobako.gemstone.isa.ptx.v9_3

/** Verifies the generated PTX instruction sum type and its colocated decoders. */
final class InstructionSuite extends munit.FunSuite {
  test("case fields and enum constructor expose one instruction model") {
    val dest        = Operand.Register("%d", None)
    val srcA        = Operand.Register("%a", None)
    val instruction = Instruction.Abs(dest = dest, srcA = srcA, mods = Vector(Modifier("s32")), pred = None)

    assertEquals(instruction.opcode, "abs")
    assertEquals(instruction.definition, InstructionDef.Abs)
    assertEquals(instruction.operands, Vector(dest, srcA))
    assertEquals(instruction.mods, Vector(Modifier("s32")))
    assertEquals(instruction.pred, None)
    assertEquals(instruction.qualifiedOpcode, "abs.s32")
    assert(instruction.meta.entries.nonEmpty)
    assert(instruction.decoder eq InstructionDef.Abs.decoder)
  }

  test("every specified opcode decodes directly to its generated instruction case") {
    val opcodes = Instructions.map(_.opcode).distinct.sorted

    assert(Instructions.nonEmpty)
    assertEquals(opcodes.size, 195)
    opcodes.foreach { opcode =>
      val forms        = Instructions.filter(_.opcode == opcode).flatMap(_.meta.entries).flatMap(_.forms)
      val operandCount = forms.map(_.operands.size).minOption.getOrElse(0)
      val operands     = Vector.tabulate(operandCount)(index => Operand.Symbol(s"operand$index"))
      val instruction  = Instruction.decode(opcode, operands).fold(error => fail(s"$opcode: $error"), identity)
      assertEquals(instruction.opcode, opcode)
      assertEquals(instruction.operands, operands)
      assertEquals(instruction.meta, instruction.definition.meta)
      assertEquals(instruction.decoder(instruction.operands, instruction.mods, instruction.pred), Right(instruction))
    }
  }

  test("unknown opcodes and invalid arities fail to decode") {
    assertEquals(
      Instruction.decode("gemstone_unknown", Vector.empty),
      Left(InstructionDecodeError.UnknownOpcode("gemstone_unknown"))
    )
    assertEquals(
      Instruction.decode("add", Vector(Operand.Symbol("destination"))),
      Left(InstructionDecodeError.InvalidOperandCount("add", Vector(3), 1))
    )
  }
}
