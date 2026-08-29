package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Col

/** Verifies the generated PTX instruction sum type and its colocated decoders. */
final class InstructionSuite extends munit.FunSuite {
  test("case fields and enum constructor expose one instruction model") {
    val dest        = Operand.Register("%d", None)
    val srcA        = Operand.Register("%a", None)
    val instruction = Instruction.Abs(dest = dest, srcA = srcA, mods = Col(Modifier("s32")), pred = None)

    assertEquals(instruction.opcode, "abs")
    assertEquals(instruction.definition, InstructionDef.Abs)
    assertEquals(instruction.operands.toSeq, Seq(dest, srcA))
    assertEquals(instruction.mods.toSeq, Seq(Modifier("s32")))
    assertEquals(instruction.pred, None)
    assertEquals(instruction.qualifiedOpcode, "abs.s32")
    assert(instruction.meta.entries.nonEmpty)
    assert(instruction.decoder eq InstructionDef.Abs.decoder)
  }

  test("every specified opcode decodes directly to its generated instruction case") {
    val opcodes = Instruction.definitions.map(_.opcode).distinct.sorted

    assert(Instruction.definitions.nonEmpty)
    assertEquals(opcodes.size, 195)
    opcodes.foreach { opcode =>
      val forms        = Instruction.definitions.filter(_.opcode == opcode).flatMap(_.meta.entries).flatMap(_.forms)
      val operandCount = forms.map(_.operands.size).minOption.getOrElse(0)
      val operands     = Col.tabulate(operandCount)(index => Operand.Symbol(s"operand$index"))
      val instruction  = Instruction.decode(opcode, operands).fold(error => fail(s"$opcode: $error"), identity)
      assertEquals(instruction.opcode, opcode)
      assert(instruction.operands.sameElements(operands))
      assertEquals(instruction.meta, instruction.definition.meta)
      val decoded = instruction.decoder(instruction.operands, instruction.mods, instruction.pred).toOption.get
      assertEquals(decoded.definition, instruction.definition)
      assert(decoded.operands.sameElements(instruction.operands))
    }
  }

  test("unknown opcodes and invalid arities fail to decode") {
    assertEquals(
      Instruction.decode("gemstone_unknown", Col.empty),
      Left(InstructionDecodeError.UnknownOpcode("gemstone_unknown"))
    )
    Instruction.decode("add", Col(Operand.Symbol("destination"))) match {
      case Left(InstructionDecodeError.InvalidOperandCount("add", expected, 1)) =>
        assertEquals(expected.toSeq, Seq(3))
      case value => fail(s"unexpected result: $value")
    }
  }
}
