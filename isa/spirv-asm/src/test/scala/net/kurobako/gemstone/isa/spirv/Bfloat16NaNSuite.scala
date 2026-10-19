package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.semantics.prelude.*

class Bfloat16NaNSuite extends munit.FunSuite {
  test("OpConvertFToBF16INTEL keeps NaN sign and leading payload, quieted") {
    val expected = Seq(
      0x7fc00000L -> 0x7fc0L,
      0x7f800001L -> 0x7fc0L,
      0xffc00001L -> 0xffc0L,
      0x7fffffffL -> 0x7fffL,
      0x7fbfffffL -> 0x7fffL,
      0xff800001L -> 0xffc0L,
      0x3f808000L -> 0x3f80L,
      0x3f818000L -> 0x3f82L,
      0x7f7fffffL -> 0x7f80L
    )
    expected.foreach((bits, narrowed) =>
      assertEquals(IntelSemantics.toBFloat16(FloatingPoint[32](bits)).raw, narrowed, f"$bits%08x")
    )
  }
}
