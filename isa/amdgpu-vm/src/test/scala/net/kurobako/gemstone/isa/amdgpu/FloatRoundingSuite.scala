package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.testing.*

class FloatRoundingSuite extends Suite {
  private val NearestEven = 0L
  private val Up          = 1L
  private val Down        = 2L
  private val TowardZero  = 3L

  private def mode(value: Long): UInt[32] = fromRaw[UInt[32]](value)

  private def single(value: Long): Float32 = fromRaw[Float32](value)
  private def half(value: Long): Float16   = fromRaw[Float16](value)
  private def double(value: Long): Float64 = fromRaw[Float64](value)

  private def expect(actual: Long, expected: Long, label: String): Unit =
    assertEquals(actual, expected, f"$label: $actual%x != $expected%x")

  test("a binary32 sum rounds as the low field of MODE says") {
    val (one, tiny, minusOne, minusTiny) = (0x3f800000L, 0x30800000L, 0xbf800000L, 0xb0800000L)
    for (
      (rounding, positive, negative) <- Seq(
        (NearestEven, one, minusOne),
        (Up, 0x3f800001L, minusOne),
        (Down, one, 0xbf800001L),
        (TowardZero, one, minusOne)
      )
    ) {
      expect(toRaw(Floats.add(single(one), single(tiny), mode(rounding))), positive, s"1+tiny $rounding")
      expect(toRaw(Floats.add(single(minusOne), single(minusTiny), mode(rounding))), negative, s"-1-tiny $rounding")
      expect(toRaw(Floats.sub(single(one), single(minusTiny), mode(rounding))), positive, s"1-(-tiny) $rounding")
    }
  }

  test("a binary32 product rounds as the low field of MODE says") {
    val x = 0x3f800001L
    for (
      (rounding, expected) <- Seq(
        (NearestEven, 0x3f800002L),
        (Up, 0x3f800003L),
        (Down, 0x3f800002L),
        (TowardZero, 0x3f800002L)
      )
    )
      expect(toRaw(Floats.mul(single(x), single(x), mode(rounding))), expected, s"x*x $rounding")
  }

  test("a binary32 fused multiply-add rounds as the low field of MODE says") {
    val (one, tiny) = (0x3f800000L, 0x30800000L)
    for ((rounding, expected) <- Seq((NearestEven, one), (Up, 0x3f800001L), (Down, one), (TowardZero, one)))
      expect(toRaw(Floats.fma(single(one), single(one), single(tiny), mode(rounding))), expected, s"fma $rounding")
  }

  test("binary16 and binary64 arithmetic round as the second field of MODE, and binary32 ignores it") {
    val field = (rounding: Long) => rounding << 2
    for (
      (rounding, expected16, expected64) <- Seq(
        (NearestEven, 0x3c00L, 0x3ff0000000000000L),
        (Up, 0x3c01L, 0x3ff0000000000001L),
        (Down, 0x3c00L, 0x3ff0000000000000L),
        (TowardZero, 0x3c00L, 0x3ff0000000000000L)
      )
    ) {
      val m = mode(field(rounding))
      expect(toRaw(Floats.add(half(0x3c00L), half(0x0c00L), m)), expected16, s"half sum $rounding")
      expect(
        toRaw(Floats.add(double(0x3ff0000000000000L), double(0x3c30000000000000L), m)),
        expected64,
        s"double sum $rounding"
      )
      expect(
        toRaw(Floats.add(single(0x3f800000L), single(0x30800000L), m)),
        0x3f800000L,
        s"single under field $rounding"
      )
    }
    expect(
      toRaw(Floats.add(double(0x3ff0000000000000L), double(0x3c30000000000000L), mode(Up))),
      0x3ff0000000000000L,
      "double ignores the low field"
    )
  }

  test("directed rounding sets the overflow result and the sign of an exact zero") {
    val (largest, infinity) = (0x7f7fffffL, 0x7f800000L)
    expect(toRaw(Floats.add(single(largest), single(largest), mode(NearestEven))), infinity, "overflow nearest")
    expect(toRaw(Floats.add(single(largest), single(largest), mode(TowardZero))), largest, "overflow toward zero")
    expect(toRaw(Floats.add(single(largest), single(largest), mode(Down))), largest, "overflow down")
    expect(toRaw(Floats.add(single(largest), single(largest), mode(Up))), infinity, "overflow up")
    val (one, minusOne) = (0x3f800000L, 0xbf800000L)
    expect(toRaw(Floats.add(single(one), single(minusOne), mode(NearestEven))), 0L, "cancel nearest")
    expect(toRaw(Floats.add(single(one), single(minusOne), mode(Down))), 0x80000000L, "cancel down")
  }

  test("an invalid operation gives the negative default NaN of its width whatever the rounding") {
    val infinity =
      Seq((32, 0x7f800000L, 0xffc00000L), (16, 0x7c00L, 0xfe00L), (64, 0x7ff0000000000000L, 0xfff8000000000000L))
    for {
      rounding               <- Seq(NearestEven, Up, Down, TowardZero)
      (width, inf, expected) <- infinity
      m = mode(rounding | (rounding << 2))
    } width match {
      case 32 => expect(toRaw(Floats.sub(single(inf), single(inf), m)), expected, s"binary32 inf-inf $rounding")
      case 16 => expect(toRaw(Floats.sub(half(inf), half(inf), m)), expected, s"binary16 inf-inf $rounding")
      case _  =>
        expect(toRaw(Floats.add(double(inf), double(inf | Long.MinValue), m)), expected, s"binary64 inf-inf $rounding")
    }
  }
}
