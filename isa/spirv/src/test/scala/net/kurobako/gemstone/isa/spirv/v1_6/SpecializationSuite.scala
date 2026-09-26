package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.spirv.v1_6.Specialization.Constant

final class SpecializationSuite extends munit.FunSuite {
  private val words = Array[Long](
    0x07230203L,
    0x00010600L,
    0L,
    6L,
    0L,
    (4L << 16) | 71L,
    3L,
    1L,
    101L,
    (4L << 16) | 71L,
    5L,
    1L,
    102L,
    (4L << 16) | 21L,
    2L,
    32L,
    1L,
    (2L << 16) | 20L,
    4L,
    (4L << 16) | 50L,
    2L,
    3L,
    7L,
    (3L << 16) | 49L,
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
    assertEquals(Specialization.describe(module), Right(Vec(Constant(101, 4), Constant(102, 1))))
  }

  test("specialize patches numeric and boolean constants") {
    val value   = bytes(42L)
    val numeric = Specialization.specialize(module, Map(101 -> value, 102 -> Vec[Byte](1)))
    assert(numeric.isRight)
    val patched = numeric.toOption.get
    assertEquals(word(patched, 19), (4L << 16) | 50L)
    assertEquals(word(patched, 22), 42L)
    assertEquals(word(patched, 23), (3L << 16) | 48L)

    val disabled = Specialization.specialize(module, Map(102 -> Vec[Byte](0))).toOption.get
    assertEquals(word(disabled, 23), (3L << 16) | 49L)
  }

  test("specialize ignores unknown ids and rejects wrong sizes") {
    assertEquals(Specialization.specialize(module, Map(999 -> bytes(1L))), Right(module))
    assert(Specialization.specialize(module, Map(101 -> Vec[Byte](1, 2, 3))).isLeft)
    assert(Specialization.specialize(Vec[Byte](1, 2, 3), Map.empty).isLeft)
  }
}
