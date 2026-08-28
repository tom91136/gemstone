// Generated from pinned Khronos grammar and SPIRV-Tools tests. Do not edit by hand.
package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Col

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
    assertEquals(Instructions.size, 875)
    Instructions.foreach { definition =>
      val rawInstruction = RawInstruction(definition.opcodeNumber, Instruction.minimumOperandWords(definition))
      val instruction =
        Instruction.decode(rawInstruction).fold(error => fail(s"${definition.opcode}: ${error.message}"), identity)
      assertEquals(instruction.definition, definition, definition.opcode)
      assert(same(Instruction.encode(instruction), rawInstruction), definition.opcode)
      val rawModule = RawModule(
        Header(Version, Word(0), Word(1), Word(0)),
        Col(rawInstruction),
        Endianness.Little
      )
      val bytes = Binary.serialize(rawModule).fold(error => fail(error.message), identity)
      assert(Binary.parse(bytes).exists(same(_, rawModule)), definition.opcode)
    }
  }

  test("SPIRV-Tools endian oracle is represented") {
    assert(upstreamTestCounts("binary_endianness_test.cpp") > 0)
    val module = RawModule(Header(Version, Word(7), Word(2), Word(0)), Col.empty, Endianness.Big)
    val bytes = Binary.serialize(module).fold(error => fail(error.message), identity)
    assert(Binary.parse(bytes).exists(same(_, module)))
  }

  test("SPIRV-Tools parser negative classes are fail-closed") {
    assert(upstreamTestCounts("binary_parse_test.cpp") >= 20)
    val malformed = Col(
      Col[Byte](1, 2, 3),
      words(0x07230203, 0x00010600, 0, 1),
      words(0xdeadbeef, 0x00010600, 0, 1, 0),
      words(0x07230203, 0x00010600, 0, 1, 0, 0),
      words(0x07230203, 0x00010600, 0, 1, 0, (3 << 16) | 17, 6)
    )
    malformed.foreach(bytes => assert(Binary.parse(bytes).isLeft))
  }

  test("unknown binary opcodes round-trip raw but fail typed decode") {
    val raw = RawInstruction(65535, Col(Word(7)))
    assertEquals(Instruction.decode(raw), Left(InstructionDecodeError.UnknownOpcode(65535)))
    val module = RawModule(Header(Version, Word(0), Word(1), Word(0)), Col(raw), Endianness.Little)
    val bytes = Binary.serialize(module).fold(error => fail(error.message), identity)
    assert(Binary.parse(bytes).exists(same(_, module)))
  }

  test("known opcodes with malformed grammar operands fail typed decode") {
    Instruction.decode(RawInstruction(17, Col.empty)) match {
      case Left(InstructionDecodeError.InvalidOperandCount("OpCapability", _)) => ()
      case other => fail(s"expected missing-operand failure, found $other")
    }
    Instruction.decode(RawInstruction(17, Col(Word(-1)))) match {
      case Left(InstructionDecodeError.InvalidEnumerant("Capability", 0xffffffffL)) => ()
      case other => fail(s"expected invalid-enumerant failure, found $other")
    }
    Instruction.decode(RawInstruction(5, Col(Word(1), Word(-1)))) match {
      case Left(InstructionDecodeError.UnterminatedString("OpName")) => ()
      case other => fail(s"expected unterminated-string failure, found $other")
    }
  }

  test("SPIRV-Tools OpenCL validation corpus is tracked") {
    assert(upstreamTestCounts("val/val_opencl_test.cpp") >= 10)
  }

  private def words(values: Int*): Col[Byte] = {
    val bytes = new Array[Byte](values.size * 4)
    values.zipWithIndex.foreach { case (value, index) =>
      bytes(index * 4) = value.toByte
      bytes(index * 4 + 1) = (value >>> 8).toByte
      bytes(index * 4 + 2) = (value >>> 16).toByte
      bytes(index * 4 + 3) = (value >>> 24).toByte
    }
    Col.unsafeFromArray(bytes)
  }
}
