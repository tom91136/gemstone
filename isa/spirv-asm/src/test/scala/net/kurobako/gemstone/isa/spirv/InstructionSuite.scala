package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

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
      val instruction =
        Instruction.decode(raw, Map.empty).fold(error => fail(s"${definition.opcode}: ${error.message}"), identity)
      assertEquals(instruction.definition, definition, definition.opcode)
      assertEquals(Instruction.encode(instruction).operandWords, raw.operandWords, definition.opcode)
      val module = RawModule(Header(ISA.version, Word(0), Word(1), Word(0)), Vec(raw), Endianness.Little)
      assertEquals(Binary.encode(module).flatMap(Binary.decode), Right(module), definition.opcode)
    }
  }

  test("known opcodes with malformed grammar operands fail typed decode") {
    val capability = RawInstruction(Instruction.Definition.Capability.opcodeNumber, Vec.empty)
    assert(Instruction.decode(capability, Map.empty) match {
      case Left(InstructionDecodeError.InvalidOperandCount(opcode, _)) => opcode == "OpCapability"
      case _                                                           => false
    })
    assertEquals(
      Instruction.decode(RawInstruction(Instruction.Definition.Capability.opcodeNumber, Vec(Word(-1))), Map.empty),
      Left(InstructionDecodeError.InvalidEnumerant(OperandKind.Capability, 0xffffffffL))
    )
    assertEquals(
      Instruction.decode(RawInstruction(Instruction.Definition.Name.opcodeNumber, Vec(Word(1), Word(-1))), Map.empty),
      Left(InstructionDecodeError.UnterminatedString("OpName"))
    )
  }

  test("a per-form decoder rejects a raw instruction for another definition") {
    val raw = RawInstruction(Instruction.Definition.Undef.opcodeNumber, Vec.empty)
    assertEquals(
      Instruction.Definition.Nop.decoder(raw, Map.empty),
      Left(InstructionDecodeError.DecoderOpcodeMismatch(
        Instruction.Definition.Nop.opcodeNumber,
        Instruction.Definition.Undef.opcodeNumber
      ))
    )
  }

  test("a truncated Phi pair is a typed decode error") {
    val raw = RawInstruction(Instruction.Definition.Phi.opcodeNumber, Vec(Word(1), Word(2), Word(3)))
    assertEquals(
      Instruction.decode(raw, Map.empty),
      Left(InstructionDecodeError.InvalidOperandCount("OpPhi", "missing IdRef at word 3"))
    )
  }

  private def caseWords(width: Int, index: Int): Vec[Word] =
    if (width == 64) Vec(Word(index + 1), Word(1)) else Vec(Word(index + 1))

  private def switchModule(width: Int, cases: Int): (RawModule, Vec[Vec[Word]]) = {
    val pairs        = Vec.from((0 until cases).map(index => caseWords(width, index) ++ Vec(Word(10 + index))))
    val constant     = Vec(Word(1), Word(2)) ++ Vec.fill((width + 31) / 32)(Word(0))
    val instructions = Vec(
      RawInstruction(Instruction.Definition.TypeInt.opcodeNumber, Vec(Word(1), Word(width), Word(0))),
      RawInstruction(Instruction.Definition.Constant.opcodeNumber, constant),
      RawInstruction(Instruction.Definition.Switch.opcodeNumber, Vec(Word(2), Word(3)) ++ pairs.flatMap(pair => pair))
    )
    (RawModule(Header(ISA.version, Word(0), Word(20), Word(0)), instructions, Endianness.Little), pairs)
  }

  for {
    width <- Vec(32, 64)
    cases <- 1 to 4
  } test(s"OpSwitch $width-bit selector groups $cases case/label pairs") {
    val (module, pairs) = switchModule(width, cases)
    val decoded         = ISA.decode(module).fold(error => fail(error.toString), identity)
    decoded.instructions.last match {
      case Instruction.Switch(_, _, target) => assertEquals(target.map(_.words), pairs)
      case other                            => fail(other.toString)
    }
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
