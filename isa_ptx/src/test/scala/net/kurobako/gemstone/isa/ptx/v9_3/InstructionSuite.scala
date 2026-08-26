package net.kurobako.gemstone.isa.ptx.v9_3

final class InstructionSuite extends munit.FunSuite {
  test("every specified opcode decodes directly to its generated instruction case") {
    val opcodes = Instructions.flatMap(_.opcodes).distinct.sorted

    assert(Instructions.nonEmpty)
    assertEquals(opcodes.size, 195)
    opcodes.foreach { opcode =>
      val forms        = InstructionsByOpcode(opcode).flatMap(_.forms).filter(_.opcode == opcode)
      val operandCount = forms.map(_.operands.size).minOption.getOrElse(0)
      val operands     = Vector.tabulate(operandCount)(index => Operand.Symbol(s"operand$index"))
      val instruction  = Instruction.decode(opcode, operands).fold(error => fail(s"$opcode: $error"), identity)
      assertEquals(instruction.opcode, opcode)
      assertEquals(instruction.operands, operands)
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
