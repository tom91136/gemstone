package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6.Specialization.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv

class SpecializationSuite extends munit.FunSuite {
  // Word offsets below address this fixture: integer type width at 15, numeric instruction at 19
  // (value at 22), and boolean instruction at 23. Patching must preserve every other word.
  private val words = Array[Long](
    spv.SPIRV.Magic.toLong,
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

  for {
    specId     <- Seq(100L, 0x80000000L, 0xffffffffL)
    typeOpcode <- Seq(20L, 21L, 22L)
    width      <- if (typeOpcode == 20) Seq(8) else if (typeOpcode == 21) Seq(8, 16, 32, 64) else Seq(16, 32, 64)
  } test(s"specialize updates every SpecId alias for spec $specId type $typeOpcode and width $width") {
    val boolean     = typeOpcode == 20
    val decorations = Seq[Long](
      0x07230203,
      0x00010600,
      0,
      6,
      0,
      (4L << 16) | 71,
      3,
      1,
      specId,
      (4L << 16) | 71,
      4,
      1,
      specId,
      (4L << 16) | 71,
      5,
      1,
      101
    )
    val declaration = if (boolean) Seq((2L << 16) | 20, 2L)
    else if (typeOpcode == 21) Seq((4L << 16) | 21, 2L, width.toLong, 0L)
    else Seq((3L << 16) | 22, 2L, width.toLong)
    val constants = Seq(3L, 4L, 5L).flatMap { id =>
      if (boolean) Seq((3L << 16) | (if (id == 4) 48L else 49L), 2L, id)
      else Seq(((3L + (width + 31) / 32) << 16) | 50L, 2L, id, id) ++ (if (width == 64) Seq(0L) else Seq.empty)
    }
    val source = decorations ++ declaration ++ constants
    val image  = Vec.from(source.flatMap(value => (0 until 4).map(byte => ((value >>> (byte * 8)) & 0xff).toByte)))
    assertEquals(
      Specialization.describe(image),
      Right(Vec(
        Constant(Binary32(specId.unsafe[Long where Between[0L, 4294967295L]]), (width / 8).unsafe[Int > 0]),
        Constant(Binary32(101L), (width / 8).unsafe[Int > 0])
      ).sortBy(_.specId.toLong))
    )
    for (zero <- Seq(false, true)) {
      val replacement = Vec.tabulate(width / 8)(i => if (zero) 0.toByte else (0xa0 + i).toByte)
      val patched     = Specialization.specialize(
        image,
        Map(Binary32(specId.unsafe[Long where Between[0L, 4294967295L]]) -> replacement)
      ).toOption.get
      val first = decorations.size + declaration.size
      val count = if (boolean) 3 else 3 + (width + 31) / 32
      for (index <- 0 until 3) {
        val offset = first + index * count
        if (index == 2)
          assertEquals(patched.slice(offset * 4, (offset + count) * 4), image.slice(offset * 4, (offset + count) * 4))
        else if (boolean) assertEquals(word(patched, offset), (3L << 16) | (if (zero) 49L else 48L))
        else assertEquals(patched.slice((offset + 3) * 4, (offset + 3) * 4 + width / 8), replacement)
      }
    }
  }

  test("Vulkan boolean specialization consumes all four VkBool32 bytes") {
    for (value <- Vector(0L, 1L, 0x100L, 0x80000000L)) {
      val result = Specialization.specialize(
        module,
        Map(Binary32(102L) -> bytes(value), Binary32(999L) -> Vec[Byte](1))
      ).toOption.get
      assertEquals(word(result, 23) & 65535L, if (value == 0L) 49L else 48L)
    }
    assert(Specialization.specialize(module, Map(Binary32(102L) -> Vec[Byte](1, 0))).isLeft)
  }

  test("specialization updates every scalar sharing a SpecId") {
    val sharedWords = Vector[Long](
      0x07230203,
      0x10000,
      0,
      20,
      0,
      (4 << 16) | 71,
      5,
      1,
      17,
      (4 << 16) | 71,
      6,
      1,
      17,
      (4 << 16) | 71,
      7,
      1,
      17,
      (4 << 16) | 71,
      8,
      1,
      17,
      (4 << 16) | 71,
      9,
      1,
      18,
      (4 << 16) | 21,
      1,
      32,
      1,
      (4 << 16) | 21,
      2,
      32,
      0,
      (3 << 16) | 22,
      3,
      32,
      (2 << 16) | 20,
      4,
      (4 << 16) | 50,
      1,
      5,
      13,
      (4 << 16) | 50,
      2,
      6,
      21,
      (4 << 16) | 50,
      3,
      7,
      0x41880000,
      (3 << 16) | 49,
      4,
      8,
      (4 << 16) | 50,
      2,
      9,
      42
    )
    val image = Vec.from(sharedWords.flatMap(bytes(_)))
    for (value <- Vector(0L, 0x4000001fL, 0x80000000L)) {
      val result = Specialization.specialize(image, Map(Binary32(17L) -> bytes(value))).toOption.get
      @scala.annotation.tailrec
      def verify(at: Int, checked: Int): Unit =
        if (at == sharedWords.size) assertEquals(checked, 5)
        else {
          val header = word(result, at)
          val opcode = header & 65535L
          if (opcode == 50L) assertEquals(word(result, at + 3), if (word(result, at + 2) == 9L) 42L else value)
          if (opcode == 48L || opcode == 49L) assertEquals(opcode, if (value == 0L) 49L else 48L)
          verify(at + (header >>> 16).toInt, checked + (if (opcode >= 48L && opcode <= 50L) 1 else 0))
        }
      verify(5, 0)
    }
  }
}
