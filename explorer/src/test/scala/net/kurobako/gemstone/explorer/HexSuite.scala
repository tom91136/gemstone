package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*

class HexSuite extends munit.FunSuite {
  test("pairs keep memory order and prefixed words are little-endian") {
    assertEquals(Hex.parse("01 05, 00 06"), Right(Vec[Byte](1, 5, 0, 6)))
    assertEquals(Hex.parse("/* 0x000000000000794d */"), Right(Vec[Byte](0x4d, 0x79, 0, 0, 0, 0, 0, 0)))
    assertEquals(Hex.parse("0x123"), Right(Vec[Byte](0x23, 0x01)))
  }

  test("invalid digits are reported, not truncated") {
    assert(Hex.parse("01 zz").isLeft)
    assert(Hex.parse("0x").isLeft)
  }

  test("rendering prints sixteen pairs per line") {
    assertEquals(Hex.render(Vec.tabulate[Byte](17)(_.toByte)), (0 until 16).map(i => f"$i%02x").mkString(" ") + "\n10")
  }
}
