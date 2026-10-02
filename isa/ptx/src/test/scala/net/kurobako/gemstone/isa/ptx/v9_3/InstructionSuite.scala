package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** Verifies the generated PTX instruction sum type and its colocated decoders. */
class InstructionSuite extends munit.FunSuite {
  test("case fields and enum constructor expose one instruction model") {
    val dest        = Operand.Register("%d", None)
    val srcA        = Operand.Register("%a", None)
    val instruction =
      Instruction.Abs(dest = dest, srcA = srcA, mods = Vec(Modifier("s32")), pred = None)

    assertEquals(instruction.opcode, "abs")
    assertEquals(instruction.definition, Instruction.Definition.Abs)
    assertEquals(instruction.operands, Seq(dest, srcA))
    assertEquals(instruction.mods, Seq(Modifier("s32")))
    assertEquals(instruction.pred, None)
    assertEquals(instruction.qualifiedOpcode, "abs.s32")
    assert(instruction.meta.entries.nonEmpty)
    assert(
      instruction.decoder eq Instruction.Definition.Abs.decoder,
      "instruction must expose its definition's decoder"
    )
  }

  test("every specified opcode decodes directly to its generated instruction case") {
    val opcodes = Instruction.definitions.map(_.opcode).distinct.sorted

    assert(Instruction.definitions.nonEmpty)
    // Independent count for the pinned PTX 9.3 input; catches accidentally omitted generated definitions.
    assertEquals(opcodes.size, 195)
    opcodes.foreach { opcode =>
      val forms =
        Instruction.definitions.filter(_.opcode == opcode).flatMap(_.meta.entries).flatMap(_.forms)
      val operandCount = forms.map(_.operands.size).minOption.getOrElse(0)
      val operands     = Vec.tabulate(operandCount)(index => Operand.Symbol(s"operand$index"))
      val instruction  = Instruction.decode(opcode, operands).fold(error => fail(s"$opcode: $error"), value => value)
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
      Instruction.decode("gemstone_unknown", Vec.empty),
      Left(Instruction.DecodeError.UnknownOpcode("gemstone_unknown"))
    )
    Instruction.decode("add", Vec(Operand.Symbol("destination"))) match {
      case Left(Instruction.DecodeError.InvalidOperandCount("add", expected, 1)) =>
        assertEquals(expected, Seq(3))
      case value => fail(s"unexpected result: $value")
    }
  }
}
