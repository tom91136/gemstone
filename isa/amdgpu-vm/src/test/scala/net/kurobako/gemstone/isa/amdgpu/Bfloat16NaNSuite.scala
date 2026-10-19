package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

class Bfloat16NaNSuite extends munit.FunSuite {
  private def single(bits: Long): Float32 = fromRaw[Float32](bits)

  test("V_CVT_PK_BF16_F32 keeps NaN sign and leading payload, quieted") {
    val expected = Seq(
      0x7fc00000L -> 0x7fc0L,
      0x7f800001L -> 0x7fc0L,
      0xffc00001L -> 0xffc0L,
      0x7fffffffL -> 0x7fffL,
      0x3f818000L -> 0x3f82L,
      0x7f7fffffL -> 0x7f80L
    )
    expected.foreach((bits, narrowed) =>
      assertEquals(toRaw(Media.packBF16F32(single(bits), single(0L))), narrowed, f"$bits%08x")
    )
  }

  test("matrix bfloat16 results narrow NaN to the canonical quiet NaN") {
    Seq(0x7fc00000L, 0x7f800001L, 0xffc00001L, 0x7fffffffL).foreach(bits =>
      assertEquals(toRaw(MatrixArithmetic.roundBf16(single(bits))), 0x7fc0L, f"$bits%08x")
    )
  }
}
