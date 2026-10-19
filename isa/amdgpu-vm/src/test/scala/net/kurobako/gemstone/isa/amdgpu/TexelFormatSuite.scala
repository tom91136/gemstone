package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*

class TexelFormatSuite extends munit.FunSuite {
  private def store(number: TexelNumber, width: Int in 1 ~ 32, value: Float): Long =
    TexelFormat.write(number, width, IEEE754.floatToBinary32(value).toLong)

  // tbuffer_store_format_x on gfx1036 with BUF_FMT_{8,16}_{USCALED,SSCALED}
  test("scaled stores clamp and scale as their normalised counterparts") {
    Seq(5.0f -> 0xffL, 300.0f -> 0xffL, -3.0f -> 0L, 0.7f -> 0xb2L, Float.NaN -> 0L).foreach((value, bits) =>
      assertEquals(store(TexelNumber.Uscaled, 8, value), bits, s"$value")
    )
    Seq(5.0f -> 0x7fL, -300.0f -> 0x81L, -3.0f -> 0x81L, 0.7f -> 0x59L, -0.5f -> 0xc0L, Float.NaN -> 0L).foreach(
      (value, bits) => assertEquals(store(TexelNumber.Sscaled, 8, value) & 0xffL, bits, s"$value")
    )
    assertEquals(store(TexelNumber.Uscaled, 16, 0.7f), 0xb332L)
    assertEquals(store(TexelNumber.Sscaled, 16, 0.7f), 0x5999L)
  }
}
