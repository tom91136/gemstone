package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.testing.*

class FloatingWireSuite extends Suite {
  // Independent MessagePack fixtures: marker and network-order IEEE 754 payload bytes.
  test("floating codecs use MessagePack widths and reject incomplete or wrong fields") {
    val floats      = summon[Wire.Codec[Float]]
    val doubles     = summon[Wire.Codec[Double]]
    val floatBytes  = Array(0xca, 0x3f, 0xc0, 0, 0).map(_.toByte)
    val doubleBytes = Array(0xcb, 0x3f, 0xf8, 0, 0, 0, 0, 0, 0).map(_.toByte)
    assertEquals(floats.encode(1.5f, ExecutionRPC.Limits).toOption.get.toVector, floatBytes.toVector)
    assertEquals(doubles.encode(1.5d, ExecutionRPC.Limits).toOption.get.toVector, doubleBytes.toVector)
    assertEquals(floats.decode(floatBytes, ExecutionRPC.Limits), Right(1.5f))
    assertEquals(doubles.decode(doubleBytes, ExecutionRPC.Limits), Right(1.5d))
    assertEquals(doubles.decode(floatBytes, ExecutionRPC.Limits), Right(1.5d))
    for (length <- floatBytes.indices)
      assert(floats.decode(floatBytes.take(length), ExecutionRPC.Limits).isLeft)
    for (length <- doubleBytes.indices)
      assert(doubles.decode(doubleBytes.take(length), ExecutionRPC.Limits).isLeft)
    assert(floats.decode(doubleBytes, ExecutionRPC.Limits).isLeft)
    assert(floats.decode(Array(0xc3.toByte), ExecutionRPC.Limits).isLeft)
    assert(doubles.decode(Array(0xc3.toByte), ExecutionRPC.Limits).isLeft)
    assert(doubles.decode(doubleBytes ++ Array(0.toByte), ExecutionRPC.Limits).isLeft)
  }

  test("floating wire fields preserve signed zero, infinities and NaN payload bits") {
    val floats        = summon[Wire.Codec[Float]]
    val floatPayloads = if (floating.NaNPayloads) Vector(0x7fc01234, 0x7fa01234) else Vector.empty
    (Vector(0, Int.MinValue, 0x3f800000, 0x7f800000, 0xff800000) ++ floatPayloads).foreach { bits =>
      val encoded = floats.encode(java.lang.Float.intBitsToFloat(bits), ExecutionRPC.Limits).toOption.get
      val decoded = floats.decode(encoded, ExecutionRPC.Limits).toOption.get
      assertEquals(java.lang.Float.floatToRawIntBits(decoded), bits)
    }
    val doubles = summon[Wire.Codec[Double]]
    (Vector(
      0L,
      Long.MinValue,
      0x3ff0000000000000L,
      0x7ff0000000000000L,
      0xfff0000000000000L
    ) ++ (if (floating.NaNPayloads) Vector(0x7ff8123456789abcL) else Vector.empty)).foreach { bits =>
      val encoded = doubles.encode(java.lang.Double.longBitsToDouble(bits), ExecutionRPC.Limits).toOption.get
      val decoded = doubles.decode(encoded, ExecutionRPC.Limits).toOption.get
      assertEquals(java.lang.Double.doubleToRawLongBits(decoded), bits)
    }
  }
}
