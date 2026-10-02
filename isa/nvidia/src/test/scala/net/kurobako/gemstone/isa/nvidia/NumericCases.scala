package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.collection.*

/** Independent bit-pattern expectations shared by the PTX and SASS frontend tests. */
object NumericCases {
  val f64ToF32: Vec[(Long, Long)] = Vec(
    0x0000000000000000L -> 0x00000000L,
    0x8000000000000000L -> 0x80000000L,
    0x0000000000000001L -> 0x00000000L,
    0x000fffffffffffffL -> 0x00000000L,
    0x0010000000000000L -> 0x00000000L,
    0x36a0000000000000L -> 0x00000001L,
    0x3690000000000000L -> 0x00000000L,
    0x3690000000000001L -> 0x00000001L,
    0xb690000000000000L -> 0x80000000L,
    0x3ff0000010000000L -> 0x3f800000L,
    0x3ff0000010000001L -> 0x3f800001L,
    0x47efffffe0000000L -> 0x7f7fffffL,
    0x47effffff0000000L -> 0x7f800000L,
    0xc7effffff0000000L -> 0xff800000L,
    0x7ff0000000000000L -> 0x7f800000L,
    0xfff0000000000000L -> 0xff800000L,
    0x7ff8123456789abcL -> 0x7fc091a2L,
    0x7ff0123456789abcL -> 0x7fc091a2L
  )

  val f32ToF64: Vec[(Long, Long)] = Vec(
    0x00000000L -> 0x0000000000000000L,
    0x80000000L -> 0x8000000000000000L,
    0x00000001L -> 0x36a0000000000000L,
    0x007fffffL -> 0x380fffffc0000000L,
    0x00800000L -> 0x3810000000000000L,
    0x3f800001L -> 0x3ff0000020000000L,
    0x7f7fffffL -> 0x47efffffe0000000L,
    0x7f800000L -> 0x7ff0000000000000L,
    0xff800000L -> 0xfff0000000000000L,
    0x7fc12345L -> 0x7ff82468a0000000L,
    0x7f812345L -> 0x7ff82468a0000000L
  )

  val f32ToF16: Vec[(Long, Long)] = Vec(
    0x00000000L -> 0x0000L,
    0x80000000L -> 0x8000L,
    0x33800000L -> 0x0001L,
    0xb3800000L -> 0x8001L,
    0x387fc000L -> 0x03ffL,
    0x38800000L -> 0x0400L,
    0xb8800000L -> 0x8400L,
    0x3f800000L -> 0x3c00L,
    0x3f801000L -> 0x3c00L,
    0x3f803000L -> 0x3c02L,
    0xbf801000L -> 0xbc00L,
    0x477fe000L -> 0x7bffL,
    0xc77fe000L -> 0xfbffL,
    0x477ff000L -> 0x7c00L,
    0xc77ff000L -> 0xfc00L,
    0x7f800000L -> 0x7c00L,
    0xff800000L -> 0xfc00L,
    0x7fc00001L -> 0x7fffL,
    0x7fa00001L -> 0x7fffL,
    0xffc54321L -> 0x7fffL,
    0xff812345L -> 0x7fffL,
    0x7f7fffffL -> 0x7c00L,
    0xff7fffffL -> 0xfc00L
  )

  val packedMultiply: Vec[(Long, Long, Long)] = Vec(
    (0x80000000L, 0x40007c00L, 0x80007fffL),
    (0x03ff0001L, 0x3c003800L, 0x03ff0000L),
    (0x7c000400L, 0xfc003800L, 0xfc000200L),
    (0x7e01fc00L, 0x3c003c00L, 0x7ffffc00L),
    (0x80003555L, 0x80003555L, 0x00002f1cL)
  )

  val packedAdd: Vec[(Long, Long, Long)] = Vec(
    (0x3c003c00L, 0xc0004000L, 0xbc004200L),
    (0x7e013c00L, 0x3c007e01L, 0x7fff7fffL),
    (0x7c003c00L, 0x3c007c00L, 0x7c007c00L),
    (0x00018000L, 0x80000001L, 0x00010001L),
    (0x00008000L, 0x80000000L, 0x00000000L),
    (0x80008000L, 0x80008000L, 0x80008000L),
    (0x7e017e01L, 0x7e017e01L, 0x7fff7fffL),
    (0x3c003c00L, 0x00010001L, 0x3c003c00L)
  )

  val f32Extrema: Vec[(Long, Long, Long, Long)] = Vec(
    (0x3f800000L, 0x40000000L, 0x3f800000L, 0x40000000L),
    (0x40000000L, 0x3f800000L, 0x3f800000L, 0x40000000L),
    (0x00000000L, 0x80000000L, 0x80000000L, 0x00000000L),
    (0x80000000L, 0x00000000L, 0x80000000L, 0x00000000L),
    (0x7fc12345L, 0x3f800000L, 0x3f800000L, 0x3f800000L),
    (0x3f800000L, 0x7fc12345L, 0x3f800000L, 0x3f800000L),
    (0x7fc12345L, 0xffc54321L, 0x7fffffffL, 0x7fffffffL),
    (0x7f812345L, 0x40000000L, 0x40000000L, 0x40000000L),
    (0x7f800000L, 0xff800000L, 0xff800000L, 0x7f800000L)
  )

  // Ordered equality and unordered inequality, including signed zeros and NaNs.
  val f32Comparisons: Vec[(Long, Long, Boolean, Boolean)] = Vec(
    (0L, 0x80000000L, true, false),
    (0x3f800000L, 0x3f800000L, true, false),
    (0xbf800000L, 0x3f800000L, false, true),
    (0x7fc12345L, 0x3f800000L, false, true),
    (0x3f800000L, 0x7f812345L, false, true),
    (0x7fc12345L, 0xffc54321L, false, true),
    (0x7f800000L, 0x7f800000L, true, false)
  )

  // PTX permits NaN encodings to differ; preserve every non-NaN bit, including signed zero.
  def canonicalHalf(bits: Long): Long =
    if ((bits & 0x7c00L) == 0x7c00L && (bits & 0x03ffL) != 0) 0x7fffL else bits & 0xffffL

  def canonicalPackedHalf(bits: Long): Long = canonicalHalf(bits) | (canonicalHalf(bits >>> 16) << 16)

  def canonicalFloat(bits: Long): Long =
    if ((bits & 0x7f800000L) == 0x7f800000L && (bits & 0x007fffffL) != 0) 0x7fffffffL
    else bits & 0xffffffffL
}
