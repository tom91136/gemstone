package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class InstructionSuite extends munit.FunSuite {
  test("top-level definitions are singleton, complete, and uniquely keyed") {
    assert(Instruction.definitions.sameElements(Instruction.Definition.values))
    assertEquals(Instruction.definitions.map(_.opcodeNumber).distinct.size, Instruction.definitions.size)
    assertEquals(Instruction.definitions.map(_.opcode).distinct.size, Instruction.definitions.size)
  }

  test("a per-form decoder rejects a raw instruction for another definition") {
    assertEquals(
      Instruction.Definition.Nop.decoder(RawInstruction(Instruction.Definition.Undef.opcodeNumber, Vec.empty)),
      Left(InstructionDecodeError.DecoderOpcodeMismatch(
        Instruction.Definition.Nop.opcodeNumber,
        Instruction.Definition.Undef.opcodeNumber
      ))
    )
  }

  test("a truncated Phi pair is a typed decode error") {
    val raw =
      RawInstruction(Instruction.Definition.Phi.opcodeNumber, Vec(Word(1), Word(2), Word(3)))
    assertEquals(
      Instruction.decode(raw),
      Left(
        InstructionDecodeError.InvalidOperandCount(
          "OpPhi",
          "short PairIdRefIdRef at word 2"
        )
      )
    )
  }

  test("constructor fields and common operands are the same values") {
    val resultType  = operand("IdResultType", 1)
    val dest        = operand("IdResult", 2)
    val srcA        = operand("IdRef", 3)
    val srcB        = operand("IdRef", 4)
    val instruction = Instruction.IAdd(resultType, dest, srcA, srcB)
    assertEquals(instruction.opcode, "OpIAdd")
    assertEquals(instruction.operands, Seq(resultType, dest, srcA, srcB))
  }

  private def operand(kind: NonEmpty[String], value: Int): Operand = Operand(
    kind,
    Vec.nonEmpty(Word(value))
  )
}
