package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.NonEmptyVec

final class InstructionSuite extends munit.FunSuite {
  test("every grammar opcode decodes to exactly one typed enum case") {
    assertEquals(Instruction.definitions.size, 875)
    Instruction.definitions.foreach { definition =>
      val raw         = RawInstruction(definition.opcodeNumber, Instruction.minimumOperandWords(definition))
      val instruction =
        Instruction.decode(raw).fold(error => fail(s"${definition.opcode}: ${error.message}"), value => value)
      assertEquals(instruction.definition, definition)
      assertEquals(instruction.opcode, definition.opcode)
      assertEquals(instruction.opcodeNumber, definition.opcodeNumber)
      assert(instruction.decoder eq definition.decoder)
      assertEquals(instruction.meta, definition.meta)
      val encoded = Instruction.encode(instruction)
      assertEquals(encoded.opcodeNumber, raw.opcodeNumber)
      assert(encoded.operandWords.sameElements(raw.operandWords))
    }
  }

  test("top-level definitions are singleton, complete, and uniquely keyed") {
    assert(Instruction.definitions.sameElements(Instruction.Definition.values))
    assertEquals(Instruction.definitions.map(_.opcodeNumber).distinct.size, Instruction.definitions.size)
    assertEquals(Instruction.definitions.map(_.opcode).distinct.size, Instruction.definitions.size)
  }

  test("a per-form decoder rejects a raw instruction for another definition") {
    assertEquals(
      Instruction.Definition.Nop.decoder(RawInstruction(SPIRVOpcodeNumber(1), Vec.empty)),
      Left(InstructionDecodeError.DecoderOpcodeMismatch(SPIRVOpcodeNumber(0), SPIRVOpcodeNumber(1)))
    )
  }

  test("unknown opcode is valid raw data but cannot inhabit the typed enum") {
    val unknownOpcode = SPIRVOpcodeNumber(65535)
    val raw           = RawInstruction(unknownOpcode, Vec(Word(1), Word(2)))
    assertEquals(Instruction.decode(raw), Left(InstructionDecodeError.UnknownOpcode(unknownOpcode)))
  }

  test("a truncated Phi pair is a typed decode error") {
    val raw =
      RawInstruction(Instruction.Definition.Phi.opcodeNumber, Vec(Word(1), Word(2), Word(3)))
    assertEquals(
      Instruction.decode(raw),
      Left(
        InstructionDecodeError.InvalidOperandCount(
          NonEmptyString("OpPhi"),
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
    assertEquals(instruction.opcode, NonEmptyString("OpIAdd"))
    assertEquals(instruction.operands.toSeq, Seq(resultType, dest, srcA, srcB))
  }

  private def operand(kind: String, value: Int): Operand = Operand(
    NonEmptyString.either(kind).fold(message => fail(message), valid => valid),
    Vec.nonEmpty(Word(value))
  )
}
