package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.testing.Suite

class Bfloat16FmaSuite extends Suite {
  test("fma.rn.bf16 rounds the exact sum once, as an RTX 4060 Ti does") {
    // (1 + 2^-7) * 1.5 is halfway between two bfloat16 values, and the +-2^-80 addend decides the direction
    val cases = Seq(
      (0x3f81L, 0x3fc0L, 0x9780L, 0x3fc1L),
      (0x3f81L, 0x3fc0L, 0x1780L, 0x3fc2L),
      (0xbf81L, 0x3fc0L, 0x1780L, 0xbfc1L),
      (0x3f81L, 0x3fc0L, 0x0000L, 0x3fc2L)
    )
    cases.foreach { case (a, b, c, expected) =>
      assertEquals(VMArithmetic.bfloat16FusedMultiplyAdd(a, b, c, saturate = false), expected, f"$a%04x $b%04x $c%04x")
    }
  }
}
