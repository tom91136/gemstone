package net.kurobako.gemstone.isa.spirv.v1_6

class NarrowImageFormatSuite extends munit.FunSuite {
  test("half image lanes preserve every finite encoding including signed zero and subnormals") {
    val layout = ImageFormats.narrow(9).get
    for (code <- 0 until 65536) {
      val value   = layout.decode(code.toLong, 0)
      val encoded = layout.encodeLane(value, 0)
      if (value.isNaN) assert(layout.decode(encoded, 0).isNaN)
      else assertEquals(encoded, code.toLong)
    }
    assertEquals(layout.encodeLane(1.0 + Math.scalb(1.0, -11), 0), 0x3c00L)
    assertEquals(layout.encodeLane(1.0 + 3 * Math.scalb(1.0, -11), 0), 0x3c02L)
    assertEquals(layout.encodeLane(Math.scalb(1.0, -25), 0), 0L)
    assertEquals(layout.encodeLane(3 * Math.scalb(1.0, -25), 0), 2L)
  }
  test("unsigned float image lanes preserve all finite codes and clamp negatives") {
    val layout = ImageFormats.narrow(8).get
    for {
      lane <- 0 until 3
      code <- 0 until (1 << (if (lane == 2) 10 else 11))
    } {
      val value = layout.decode(code.toLong << (if (lane == 2) 22 else lane * 11), lane)
      if (!value.isNaN) assertEquals(layout.encodeLane(value, lane), code.toLong)
    }
    assertEquals(layout.encodePacked(Array(1.0, 0.5, 0.25, 0.0)), 0x68000000L | (0x380L << 11) | 0x3c0L)
    assertEquals(layout.encodePacked(Array(-1.0, Double.NegativeInfinity, -0.0, 0.0)), 0L)
  }
  test("packed normalized formats use the specified channel order") {
    for ((format, red) <- Seq(1007 -> 0x00f0L, 1008 -> 0xf800L, 1009 -> 0x7c00L, 11 -> 0x3ffL)) {
      val layout = ImageFormats.narrow(format).get
      assertEquals(layout.decode(red, 0), 1.0)
      assertEquals(layout.decode(red, 1), 0.0)
      assertEquals(layout.decode(red, 2), 0.0)
      assertEquals(layout.encodePacked(Array(1.0, 0.0, 0.0, 0.0)), red)
    }
  }
  test("RGB9E5 decodes shared exponents without a hidden bit or special exponent") {
    val layout = ImageFormats.narrow(1010).get
    val bits   = (16L << 27) | (64L << 18) | (128L << 9) | 256L
    assertEquals((0 until 3).map(layout.decode(bits, _)), Seq(1.0, 0.5, 0.25))
    assertEquals(layout.decode(1L, 0), Math.scalb(1.0, -24))
    assertEquals(layout.decode(0xffffffffL, 0), 65408.0)
  }
  test("10-bit integer storage saturates each channel independently") {
    val layout = ImageFormats.integer(34).get
    val packed = layout.encodePacked(Array(1024L, 42L, 0xffffffffL, 4L))
    assertEquals(packed, 0xfff00000L | (42L << 10) | 1023L)
    assertEquals((0 until 4).map(layout.decode(packed, _)), Seq(1023L, 42L, 1023L, 3L))
  }
}
