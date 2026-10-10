package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*

class BinarySuite extends munit.FunSuite {
  // Fixed bytes and boundary values below are independent expectations for the wire/bit representation.
  // Do not derive these from the encoder or the constants whose values these cases verify.

  private val header = Header(Version(1, 6), Word(0), Word(1), Word(0))

  private def words(values: Int*): Vec[Byte] = Vec.from(values).flatMap { value =>
    Vec(value.toByte, (value >>> 8).toByte, (value >>> 16).toByte, (value >>> 24).toByte)
  }

  test("instruction count boundaries include the header word and reject all larger counts") {
    val largest = RawInstruction(SPIRVOpcodeNumber(65535), Vec.fill(65534)(Word(0x89abcdef)))
    Seq(Endianness.Little, Endianness.Big).foreach { order =>
      val encoded = Binary.encode(RawModule(header, Vec(largest), order)).fold(error => fail(error.message), identity)
      assertEquals(encoded.length, (5 + 65535) * 4)
      val decoded = Binary.decode(encoded).fold(error => fail(error.message), identity)
      assertEquals(decoded.header, header)
      assertEquals(decoded.endianness, order)
      assertEquals(decoded.instructions.length, 1)
      assertEquals(decoded.instructions.head.opcodeNumber, largest.opcodeNumber)
      assert(decoded.instructions.head.operandWords == largest.operandWords)
    }
    Seq(65535, 65536).foreach { operandCount =>
      val instruction = RawInstruction(SPIRVOpcodeNumber(65535), Vec.fill(operandCount)(Word(0)))
      val error       = Binary.encode(RawModule(header, Vec(instruction), Endianness.Little))
        .swap.toOption.getOrElse(fail("overlong instruction was encoded"))
      assertEquals(
        error,
        Binary.BinaryError.InstructionTooLong(instruction.opcodeNumber, (operandCount + 1).unsafe[Int > 0])
      )
    }
  }

  test("instruction errors identify the failing instruction after an intact prefix") {
    val prefix = words(0x07230203, 0x00010600, 0, 1, 0, 0x00010000)
    assertEquals(Binary.decode(prefix ++ words(0)), Left(Binary.BinaryError.ZeroInstructionWordCount(6)))
    assertEquals(
      Binary.decode(prefix ++ words(0x00030011, 6)),
      Left(Binary.BinaryError.TruncatedInstruction(wordOffset = 6, declaredWords = 3, remainingWords = 2))
    )
    val valid = Binary.decode(prefix ++ words(0x00020011, 6)).fold(error => fail(error.message), identity)
    assertEquals(valid.instructions.map(_.opcodeNumber.toInt), Vec(0, 17))
    assertEquals(valid.instructions(1).operandWords, Vec(Word(6)))
  }

  test("binary headers reject malformed version words without throwing or discarding bits") {
    Seq(0, 0x00000600, 0x01010600, 0x00010601).foreach { version =>
      assertEquals(
        Binary.decode(words(0x07230203, version, 0, 1, 0)),
        Left(Binary.BinaryError.InvalidVersion(version.toLong))
      )
    }
    val future = Binary.decode(words(0x07230203, 0x00020000, 0, 1, 0)).fold(error => fail(error.message), identity)
    assertEquals(future.header.version, Version(2, 0))
  }

  test("version encoding fits each component into its own byte without truncation") {
    Seq(Version(1, 0), Version(255, 255)).foreach { version =>
      val module  = RawModule(header.copy(version = version), Vec.empty, Endianness.Little)
      val encoded = Binary.encode(module).fold(error => fail(error.message), identity)
      assertEquals(Binary.decode(encoded).map(_.header.version), Right(version))
    }
    Seq(Version(256, 0), Version(1, 256)).foreach { version =>
      assertEquals(
        Binary.encode(RawModule(header.copy(version = version), Vec.empty, Endianness.Little)),
        Left(Binary.BinaryError.VersionTooLarge(version))
      )
    }
  }

  test("malformed binary headers and instruction lengths are rejected") {
    Seq(
      Vec[Byte](1, 2, 3),
      words(0x07230203, 0x00010600, 0, 1),
      words(0xdeadbeef, 0x00010600, 0, 1, 0),
      words(0x07230203, 0x00010600, 0, 1, 0, 0),
      words(0x07230203, 0x00010600, 0, 1, 0, (3 << 16) | 17, 6)
    ).foreach(bytes => assert(Binary.decode(bytes).isLeft, bytes))
  }

  test("big-endian instruction words match fixed bytes including high-bit operands") {
    val expected = Vec.from(Seq(
      7, 35, 2, 3, 0, 1, 6, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0,
      0, 1, 0, 0, 0, 2, 255, 255, 137, 171, 205, 239
    ).map(_.toByte))
    val module = RawModule(
      header,
      Vec(
        RawInstruction(SPIRVOpcodeNumber(0), Vec.empty),
        RawInstruction(SPIRVOpcodeNumber(65535), Vec(Word(0x89abcdef)))
      ),
      Endianness.Big
    )
    assertEquals(Binary.encode(module), Right(expected))
    assertEquals(Binary.decode(expected), Right(module))
  }
}
