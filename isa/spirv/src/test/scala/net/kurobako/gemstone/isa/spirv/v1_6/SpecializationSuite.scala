package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.Specialization.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv

class SpecializationSuite extends munit.FunSuite {
  // Word offsets below address this fixture: integer type width at 15, numeric instruction at 19
  // (value at 22), and boolean instruction at 23. Patching must preserve every other word.
  private val words = Array[Long](
    spv.Binary.Magic.toLong,
    0x00010600L,
    0L,
    6L,
    0L,
    (4L << 16) | spv.Instruction.Definition.Decorate.opcodeNumber.toInt.toLong,
    3L,
    1L,
    101L,
    (4L << 16) | spv.Instruction.Definition.Decorate.opcodeNumber.toInt.toLong,
    5L,
    1L,
    102L,
    (4L << 16) | spv.Instruction.Definition.TypeInt.opcodeNumber.toInt.toLong,
    2L,
    32L,
    1L,
    (2L << 16) | spv.Instruction.Definition.TypeBool.opcodeNumber.toInt.toLong,
    4L,
    (4L << 16) | spv.Instruction.Definition.SpecConstant.opcodeNumber.toInt.toLong,
    2L,
    3L,
    7L,
    (3L << 16) | spv.Instruction.Definition.SpecConstantFalse.opcodeNumber.toInt.toLong,
    4L,
    5L
  )

  private def module: Vec[Byte] =
    Vec.from(words.flatMap(word => (0 until 4).map(byte => ((word >>> (byte * 8)) & 0xff).toByte)))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def word(image: Vec[Byte], index: Int): Long = {
    var result = 0L
    var byte   = 0
    while (byte < 4) {
      result |= (image(index * 4 + byte).toLong & 0xffL) << (byte * 8)
      byte += 1
    }
    result
  }

  private def bytes(value: Long): Vec[Byte] = Vec.from((0 until 4).map(byte => ((value >>> (byte * 8)) & 0xff).toByte))

  test("describe enumerates scalar specialization constants") {
    assertEquals(Specialization.describe(module), Right(Vec(Constant(Binary32(101L), 4), Constant(Binary32(102L), 1))))
  }

  test("unsigned specialization IDs are ordered and patched without changing their bits") {
    // Words 8 and 12 hold the two SpecId decoration literals.
    val image = Vec.from(words.updated(8, 0xffffffffL).updated(12, 0x80000000L).flatMap(value => bytes(value)))
    assertEquals(
      Specialization.describe(image),
      Right(Vec(Constant(Binary32(0x80000000L), 1), Constant(Binary32(0xffffffffL), 4)))
    )
    val patched = Specialization.specialize(image, Map(Binary32(0xffffffffL) -> bytes(42L))).toOption.get
    assertEquals(word(patched, 22), 42L)
    assertEquals(patched.take(22 * 4), image.take(22 * 4))
    assertEquals(patched.drop(23 * 4), image.drop(23 * 4))
  }

  test("specialize patches numeric and boolean constants") {
    val value   = bytes(42L)
    val numeric = Specialization.specialize(module, Map(Binary32(101L) -> value, Binary32(102L) -> Vec[Byte](1)))
    assert(numeric.isRight)
    val patched = numeric.toOption.get
    assertEquals(word(patched, 19), (4L << 16) | spv.Instruction.Definition.SpecConstant.opcodeNumber.toInt.toLong)
    assertEquals(word(patched, 22), 42L)
    assertEquals(word(patched, 23), (3L << 16) | spv.Instruction.Definition.SpecConstantTrue.opcodeNumber.toInt.toLong)

    val disabled = Specialization.specialize(module, Map(Binary32(102L) -> Vec[Byte](0))).toOption.get
    assertEquals(
      word(disabled, 23),
      (3L << 16) | spv.Instruction.Definition.SpecConstantFalse.opcodeNumber.toInt.toLong
    )
  }

  test("specialize ignores unknown ids and rejects wrong sizes") {
    assertEquals(Specialization.specialize(module, Map(Binary32(999L) -> bytes(1L))), Right(module))
    assert(Specialization.specialize(module, Map(Binary32(101L) -> Vec[Byte](1, 2, 3))).isLeft)
    assert(Specialization.specialize(Vec[Byte](1, 2, 3), Map.empty).isLeft)
  }
  test("malformed negative scalar widths cannot become byte sizes") {
    val malformed = Vec.from(words.updated(15, 0xfffffff8L).flatMap(word =>
      (0 until 4).map(byte => ((word >>> (byte * 8)) & 0xff).toByte)
    ))
    assertEquals(Specialization.describe(malformed), Right(Vec(Constant(Binary32(102L), 1))))
    assertEquals(
      Specialization.specialize(malformed, Map(Binary32(101L) -> bytes(1L))),
      Left("specialization constant 101 is not a scalar constant")
    )
  }

}
