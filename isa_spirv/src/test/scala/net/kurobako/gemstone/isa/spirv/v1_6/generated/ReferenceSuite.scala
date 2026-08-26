// Generated from pinned Khronos grammar and SPIRV-Tools tests. Do not edit by hand.
package net.kurobako.gemstone.isa.spirv.v1_6.generated

import net.kurobako.gemstone.isa.spirv.v1_6.*

final class ReferenceSuite extends munit.FunSuite {
  private val upstreamTestCounts: Map[String, Int] = Map(
    "binary_parse_test.cpp" -> 24,
    "binary_endianness_test.cpp" -> 3,
    "val/val_opencl_test.cpp" -> 17
  )

  test("grammar opcode corpus is complete and round-trips little endian") {
    assertEquals(InstructionCatalog.instructions.size, 875)
    InstructionCatalog.instructions.foreach { info =>
      val operands = Vector.fill(info.minimumOperandCount)(Word(0))
      val module = Module(
        Header(Version.V1_6, Word(0), Word(1), Word(0)),
        Vector(Instruction(info.opcode, operands)),
        Endianness.Little
      )
      val bytes = Binary.serialize(module).fold(error => fail(error.message), identity)
      assertEquals(Binary.parse(bytes), Right(module), info.name)
    }
  }

  test("SPIRV-Tools endian oracle is represented") {
    assert(upstreamTestCounts("binary_endianness_test.cpp") > 0)
    val module = Module(Header(Version.V1_6, Word(7), Word(2), Word(0)), Vector.empty, Endianness.Big)
    val bytes = Binary.serialize(module).fold(error => fail(error.message), identity)
    assertEquals(Binary.parse(bytes), Right(module))
  }

  test("SPIRV-Tools parser negative classes are fail-closed") {
    assert(upstreamTestCounts("binary_parse_test.cpp") >= 20)
    val malformed = Vector(
      IArray[Byte](1, 2, 3),
      words(0x07230203, 0x00010600, 0, 1),
      words(0xdeadbeef, 0x00010600, 0, 1, 0),
      words(0x07230203, 0x00010600, 0, 1, 0, 0),
      words(0x07230203, 0x00010600, 0, 1, 0, (3 << 16) | 17, 6)
    )
    malformed.foreach(bytes => assert(Binary.parse(bytes).isLeft))
  }

  test("SPIRV-Tools OpenCL validation corpus is tracked") {
    assert(upstreamTestCounts("val/val_opencl_test.cpp") >= 10)
  }

  private def words(values: Int*): IArray[Byte] = {
    val bytes = new Array[Byte](values.size * 4)
    values.zipWithIndex.foreach { case (value, index) =>
      bytes(index * 4) = value.toByte
      bytes(index * 4 + 1) = (value >>> 8).toByte
      bytes(index * 4 + 2) = (value >>> 16).toByte
      bytes(index * 4 + 3) = (value >>> 24).toByte
    }
    IArray.unsafeFromArray(bytes)
  }
}
