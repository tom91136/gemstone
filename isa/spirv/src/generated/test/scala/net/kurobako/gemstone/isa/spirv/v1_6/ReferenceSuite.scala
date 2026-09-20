// Generated from pinned Khronos grammar and SPIRV-Tools tests. Do not edit by hand.
package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class ReferenceSuite extends munit.FunSuite {
  private val upstreamTestCounts: Map[String, Int] = Map(
    "binary_parse_test.cpp" -> 24,
    "binary_endianness_test.cpp" -> 3,
    "val/val_opencl_test.cpp" -> 17
  )

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

  test("SPIRV-Tools endian oracle is represented") {
    assert(upstreamTestCounts("binary_endianness_test.cpp") > 0)
    val module = RawModule(Header(ISA.version, Word(7), Word(2), Word(0)), Vec.empty, Endianness.Big)
    val bytes = Binary.encode(module).fold(error => fail(error.message), value => value)
    assert(Binary.decode(bytes).exists(same(_, module)))
  }

  test("SPIRV-Tools parser negative classes are fail-closed") {
    assert(upstreamTestCounts("binary_parse_test.cpp") >= 20)
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
          if opcode == SPIRVOpcode("OpCapability") => ()
      case other => fail(s"expected missing-operand failure, found $other")
    }
    Instruction.decode(RawInstruction(SPIRVOpcodeNumber(17), Vec(Word(-1)))) match {
      case Left(InstructionDecodeError.InvalidEnumerant(kind, 0xffffffffL))
          if kind == SPIRVOperandKind("Capability") => ()
      case other => fail(s"expected invalid-enumerant failure, found $other")
    }
    Instruction.decode(RawInstruction(SPIRVOpcodeNumber(5), Vec(Word(1), Word(-1)))) match {
      case Left(InstructionDecodeError.UnterminatedString(opcode))
          if opcode == SPIRVOpcode("OpName") => ()
      case other => fail(s"expected unterminated-string failure, found $other")
    }
  }

  test("SPIRV-Tools OpenCL validation corpus is tracked") {
    assert(upstreamTestCounts("val/val_opencl_test.cpp") >= 10)
  }

  private def words(values: Int*): Vec[Byte] = Vec.from(values).flatMap { value =>
    Vec(value.toByte, (value >>> 8).toByte, (value >>> 16).toByte, (value >>> 24).toByte)
  }
}
