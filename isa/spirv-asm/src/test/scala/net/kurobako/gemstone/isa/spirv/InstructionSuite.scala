package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite

class InstructionSuite extends Suite {
  private def minimum(shapes: Vec[OperandShape]): Vec[Word] =
    shapes.filter(_.quantifier == OperandQuantifier.One).flatMap(shape => minimum(shape.kind))

  private def minimum(kind: OperandKind): Vec[Word] =
    if (kind.category == OperandKind.Category.Composite)
      Vec.from(kind.components).flatMap(component => minimum(component))
    else Instruction.operandKinds.get(kind).filter(_.enumerants.nonEmpty) match {
      case Some(info) =>
        val selected = info.enumerants.minBy(enumerant => (enumerant.parameterKinds.size, enumerant.value))
        Vec(Word(selected.value.toInt)) ++ selected.parameterKinds.flatMap(parameter => minimum(parameter))
      case None => Vec(Word(0))
    }

  test("every grammar opcode decodes its minimal operands to a typed case and round-trips a binary module") {
    // instruction count of the pinned core grammar
    assertEquals(Instruction.definitions.size, 875)
    Instruction.definitions.foreach { definition =>
      val raw         = RawInstruction(definition.opcodeNumber, minimum(definition.shapes))
      val instruction = Instruction.decode(raw).fold(error => fail(s"${definition.opcode}: ${error.message}"), identity)
      assertEquals(instruction.definition, definition, definition.opcode)
      assertEquals(Instruction.encode(instruction).operandWords, raw.operandWords, definition.opcode)
      val module = RawModule(Header(ISA.version, Word(0), Word(1), Word(0)), Vec(raw), Endianness.Little)
      assertEquals(Binary.encode(module).flatMap(Binary.decode), Right(module), definition.opcode)
    }
  }

  test("known opcodes with malformed grammar operands fail typed decode") {
    assert(Instruction.decode(RawInstruction(Instruction.Definition.Capability.opcodeNumber, Vec.empty)) match {
      case Left(InstructionDecodeError.InvalidOperandCount(opcode, _)) => opcode == "OpCapability"
      case _                                                           => false
    })
    assertEquals(
      Instruction.decode(RawInstruction(Instruction.Definition.Capability.opcodeNumber, Vec(Word(-1)))),
      Left(InstructionDecodeError.InvalidEnumerant(OperandKind.Capability, 0xffffffffL))
    )
    assertEquals(
      Instruction.decode(RawInstruction(Instruction.Definition.Name.opcodeNumber, Vec(Word(1), Word(-1)))),
      Left(InstructionDecodeError.UnterminatedString("OpName"))
    )
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
    val raw = RawInstruction(Instruction.Definition.Phi.opcodeNumber, Vec(Word(1), Word(2), Word(3)))
    assertEquals(
      Instruction.decode(raw),
      Left(InstructionDecodeError.InvalidOperandCount("OpPhi", "missing IdRef at word 3"))
    )
  }

  test("an unknown raw opcode fails module decoding") {
    val unknown = SPIRVOpcodeNumber(65535)
    val raw     = RawModule(
      Header(ISA.version, Word(0), Word(10), Word(0)),
      Vec(RawInstruction(unknown, Vec.empty)),
      Endianness.Little
    )
    assertEquals(ISA.decode(raw), Left(ModuleDecodeError.Instruction(0, InstructionDecodeError.UnknownOpcode(unknown))))
    assertEquals(Binary.encode(raw).flatMap(Binary.decode), Right(raw))
  }
}
