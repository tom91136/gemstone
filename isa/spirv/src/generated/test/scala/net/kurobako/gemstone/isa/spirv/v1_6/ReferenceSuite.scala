// Generated from pinned Khronos grammar and SPIRV-Tools tests. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

// Reference inventory: binary_parse_test.cpp (24 upstream test declarations; not a coverage claim).
// Reference inventory: binary_endianness_test.cpp (3 upstream test declarations; not a coverage claim).
// Reference inventory: val/val_opencl_test.cpp (17 upstream test declarations; not a coverage claim).
@javax.annotation.processing.Generated(Array("sbt codegen"))
class ReferenceSuite extends munit.FunSuite {
  private def same(left: RawInstruction, right: RawInstruction): Boolean =
    left.opcodeNumber == right.opcodeNumber && left.operandWords.sameElements(right.operandWords)

  private def same(left: RawModule, right: RawModule): Boolean =
    left.header == right.header && left.endianness == right.endianness &&
      left.instructions.size == right.instructions.size &&
      left.instructions.zip(right.instructions).forall { case (leftInstruction, rightInstruction) =>
        same(leftInstruction, rightInstruction)
      }

  test("every grammar opcode decodes to a typed case and round-trips little endian") {
    assertEquals(Instruction.definitions.size, 875)
    Instruction.definitions.foreach { definition =>
      val rawInstruction = RawInstruction(definition.opcodeNumber, Instruction.minimumOperandWords(definition))
      val instruction = Instruction.decode(rawInstruction).fold(
        error => fail(s"${definition.opcode}: ${error.message}"),
        value => value
      )
      assertEquals(instruction.definition, definition, definition.opcode)
      assert(same(Instruction.encode(instruction), rawInstruction), definition.opcode)
      val rawModule = RawModule(
        Header(ISA.version, Word(0), Word(1), Word(0)),
        Vec(rawInstruction),
        Endianness.Little
      )
      val bytes = Binary.encode(rawModule).fold(error => fail(error.message), value => value)
      assert(Binary.decode(bytes).exists(same(_, rawModule)), definition.opcode)
    }
  }

  test("big-endian headers match fixed wire bytes") {
    val module = RawModule(Header(ISA.version, Word(7), Word(2), Word(0)), Vec.empty, Endianness.Big)
    val bytes = Binary.encode(module).fold(error => fail(error.message), value => value)
    val expected = Vec[Byte](7, 35, 2, 3, 0, 1, 6, 0, 0, 0, 0, 7, 0, 0, 0, 2, 0, 0, 0, 0)
    assertEquals(bytes, expected)
    assert(Binary.decode(expected).exists(same(_, module)))
  }

  test("malformed binary headers and instruction lengths are rejected") {
    val malformed = Vec(
      Vec[Byte](1, 2, 3),
      words(0x07230203, 0x00010600, 0, 1),
      words(0xdeadbeef, 0x00010600, 0, 1, 0),
      words(0x07230203, 0x00010600, 0, 1, 0, 0),
      words(0x07230203, 0x00010600, 0, 1, 0, (3 << 16) | 17, 6)
    )
    malformed.foreach(bytes => assert(Binary.decode(bytes).isLeft))
  }

  test("unknown binary opcodes round-trip raw but fail typed decode") {
    val unknownOpcode = SPIRVOpcodeNumber(65535)
    val raw = RawInstruction(unknownOpcode, Vec(Word(7)))
    assertEquals(Instruction.decode(raw), Left(InstructionDecodeError.UnknownOpcode(unknownOpcode)))
    val module = RawModule(Header(ISA.version, Word(0), Word(1), Word(0)), Vec(raw), Endianness.Little)
    val bytes = Binary.encode(module).fold(error => fail(error.message), value => value)
    assert(Binary.decode(bytes).exists(same(_, module)))
  }

  test("known opcodes with malformed grammar operands fail typed decode") {
    Instruction.decode(RawInstruction(SPIRVOpcodeNumber(17), Vec.empty)) match {
      case Left(InstructionDecodeError.InvalidOperandCount(opcode, _))
          if opcode == "OpCapability" => ()
      case other => fail(s"expected missing-operand failure, found $other")
    }
    Instruction.decode(RawInstruction(SPIRVOpcodeNumber(17), Vec(Word(-1)))) match {
      case Left(InstructionDecodeError.InvalidEnumerant(kind, 0xffffffffL))
          if kind == "Capability" => ()
      case other => fail(s"expected invalid-enumerant failure, found $other")
    }
    Instruction.decode(RawInstruction(SPIRVOpcodeNumber(5), Vec(Word(1), Word(-1)))) match {
      case Left(InstructionDecodeError.UnterminatedString(opcode))
          if opcode == "OpName" => ()
      case other => fail(s"expected unterminated-string failure, found $other")
    }
  }

  private def words(values: Int*): Vec[Byte] = Vec.from(values).flatMap { value =>
    Vec(value.toByte, (value >>> 8).toByte, (value >>> 16).toByte, (value >>> 24).toByte)
  }
}
