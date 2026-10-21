package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** The transcendental unit's operations against the hardware's own results. */
class TranscendentalUnitSuite extends munit.FunSuite {

  private def check(rows: String, digits: Int)(function: Long => Long): Unit = {
    val wrong = rows.stripMargin.linesIterator.map(_.trim).filter(_.nonEmpty).flatMap { line =>
      val arrow    = line.indexOf("->")
      val input    = java.lang.Long.parseUnsignedLong(line.take(arrow).trim, 16)
      val expected = java.lang.Long.parseUnsignedLong(line.substring(arrow + 2).trim, 16)
      val produced = function(input)
      Option.when(produced != expected)(s"  $line\n  gemstone ${s"%0${digits}x".format(produced)}")
    }.toList
    if (wrong.nonEmpty) fail(s"${wrong.size} rows differ:\n" + wrong.take(8).mkString("\n"))
  }

  // gfx803, gfx906, gfx1036 and gfx1150 agree on every row
  test("the binary32 reciprocal matches the hardware") {
    check(
      """
        |00000000 -> 7f800000
        |80000001 -> ff800000
        |00800001 -> 7e7ffffe
        |3f800000 -> 3f800000
        |3f7fffff -> 3f800001
        |3fbfffff -> 3f2aaaac
        |40490fdb -> 3ea2f983
        |1aec7d51 -> 640a8f5f
        |6b531ecd -> 139b35c8
        |7e800000 -> 00800000
        |7e800001 -> 00000000
        |ff800000 -> 80000000
        |7f800001 -> 7fc00001
        |""",
      8
    )(TranscendentalUnit.reciprocal32)
  }

  test("the binary32 reciprocal square root matches the hardware") {
    check(
      """
        |80000000 -> ff800000
        |007fffff -> 7f800000
        |00800000 -> 5f000000
        |bf800000 -> ffc00000
        |3f7fffff -> 3f800000
        |40000000 -> 3f3504f3
        |3fbfffff -> 3f5105ec
        |7e800001 -> 1fffffff
        |4d871bdc -> 38792c99
        |7f800000 -> 00000000
        |ff800000 -> ffc00000
        |7fc00001 -> 7fc00001
        |""",
      8
    )(TranscendentalUnit.reciprocalSquareRoot32)
  }

  test("the binary32 square root matches the hardware") {
    check(
      """
        |80000001 -> 80000000
        |00800001 -> 20000000
        |3f800001 -> 3f800000
        |3f7fffff -> 3f7fffff
        |40000000 -> 3fb504f3
        |3f317218 -> 3f55224c
        |6dde8361 -> 56a8c3e3
        |4b7fffff -> 457fffff
        |c0000000 -> ffc00000
        |7f800000 -> 7f800000
        |ff800000 -> ffc00000
        |7f800001 -> 7fc00001
        |""",
      8
    )(TranscendentalUnit.squareRoot32)
  }

  test("the binary32 power of two matches the hardware") {
    check(
      """
        |00000000 -> 3f800000
        |807fffff -> 3f800000
        |33800000 -> 3f800000
        |b3800000 -> 3f7fffff
        |b37fffff -> 3f800000
        |34000000 -> 3f800001
        |3f800000 -> 40000000
        |bf800000 -> 3f000000
        |3f800001 -> 40000001
        |bf800001 -> 3efffffe
        |bf7fffff -> 3f000000
        |bfffffff -> 3e800001
        |c0000001 -> 3e7ffffd
        |3f000000 -> 3fb504f3
        |bf000000 -> 3f3504f3
        |3e800001 -> 3f9837f0
        |3fbc0000 -> 403123f6
        |40490fdb -> 410d331c
        |c0490fdb -> 3de8118d
        |42fe0000 -> 7f000000
        |42ffffff -> 7f7fffa7
        |43000000 -> 7f800000
        |c2fc0000 -> 00800000
        |c2fbffff -> 0080002c
        |c2fc0001 -> 00000000
        |7f7fffff -> 7f800000
        |ff7fffff -> 00000000
        |7f800000 -> 7f800000
        |ff800000 -> 00000000
        |7f800001 -> 7fc00001
        |ff800001 -> ffc00001
        |ffc00000 -> ffc00000
        |""",
      8
    )(TranscendentalUnit.powerOfTwo32)
  }

  test("the binary32 logarithm matches the hardware") {
    check(
      """
        |00000000 -> ff800000
        |80000000 -> ff800000
        |000441f2 -> ff800000
        |3f800000 -> 00000000
        |3f82f000 -> 3d0614a4
        |3f7c0800 -> bcb8a849
        |3f872000 -> 3da00d88
        |40071000 -> 3f89eaf9
        |3e3fe000 -> c01a9f5f
        |3fdb0000 -> 3f465872
        |40674000 -> 3fed393f
        |15162214 -> c2a98a31
        |6983ab3a -> 42a814df
        |7f7fffff -> 42ffffff
        |bf800000 -> ffc00000
        |7f800000 -> 7f800000
        |ff800000 -> ffc00000
        |7fa00000 -> 7fe00000
        |""",
      8
    )(TranscendentalUnit.logarithm32)
  }

  test("the binary32 sine matches the hardware") {
    check(
      """
        |80000000 -> 80000000
        |960ff9a4 -> 976227d2
        |3977bbc1 -> 3ac291bd
        |39c00000 -> 3b16cbdf
        |3a200000 -> 3b7b53af
        |3f80c000 -> 3d16c331
        |3e70a000 -> 3f7edc94
        |3f84a800 -> 3e680423
        |3e800000 -> 3f800000
        |3ed21000 -> 3f08cb20
        |4057a800 -> 3f3b0629
        |3fc08800 -> bcd59aac
        |3fdd3000 -> bf7d906e
        |3ff51000 -> bf02ef9e
        |c2447ac4 -> bf2f1cf0
        |bfb5bb11 -> bef74d63
        |4a800001 -> 00000000
        |7f800000 -> ffc00000
        |ff9140d4 -> ffd140d4
        |""",
      8
    )(TranscendentalUnit.sine32)
  }

  test("the binary32 cosine matches the hardware") {
    check(
      """
        |00000000 -> 3f800000
        |807fffff -> 3f800000
        |1cfbfc9e -> 3f800000
        |ba7fb78b -> 3f7ffec4
        |3f80d000 -> 3f7fcbe1
        |3da0c000 -> 3f617e5a
        |3f90f800 -> 3f2c35b9
        |3e800000 -> 00000000
        |3fb74800 -> bf68e907
        |3f2ab800 -> beff6ed6
        |40601000 -> bf7ffec4
        |3f7e2800 -> 3f7fbce8
        |c0280001 -> bf3504e1
        |bf4c0f8c -> 3e955b1a
        |7e800000 -> 3f800000
        |7f800000 -> ffc00000
        |7fa00000 -> 7fe00000
        |""",
      8
    )(TranscendentalUnit.cosine32)
  }

  private def half(function: Float16 => Float16)(bits: Long): Long = function(fromRaw[Float16](bits)).bits.raw

  test("the binary16 logarithm, sine and cosine match the hardware") {
    check(
      """
        |0000 -> fc00
        |8000 -> fc00
        |020c -> cb7c
        |0aef -> ca1a
        |3c00 -> 0000
        |3c01 -> 15c5
        |3bff -> 91c6
        |3c76 -> 3109
        |4a4d -> 4350
        |7b8f -> 4bf6
        |85a8 -> fe00
        |7c00 -> 7c00
        |fc00 -> fe00
        |7fba -> 7fba
        |febb -> febb
        |""",
      4
    )(half(Floats.log2(_: Float16)))
    check(
      """
        |0000 -> 0000
        |8000 -> 8000
        |82fa -> 8cad
        |04b0 -> 0f5d
        |238e -> 2ded
        |34a9 -> 3bbc
        |3400 -> 3c00
        |4a15 -> 3add
        |5071 -> b23e
        |c141 -> 39ba
        |69a4 -> 0000
        |7c00 -> fe00
        |7dba -> 7fba
        |""",
      4
    )(half(Floats.sin(_: Float16)))
    check(
      """
        |0000 -> 3c00
        |02d8 -> 3c00
        |2a83 -> 3b98
        |317e -> 3790
        |3400 -> 0000
        |3e7d -> b9c3
        |4dca -> 3872
        |5bf5 -> b9a8
        |b5a2 -> b8c9
        |d032 -> bb64
        |7bb2 -> 3c00
        |fc00 -> fe00
        |fd94 -> ff94
        |""",
      4
    )(half(Floats.cos(_: Float16)))
  }

  // gfx803's rows
  test("GFX8's sine and cosine give those of zero beyond 256 turns") {
    def single(function: Float32 => Float32)(bits: Long): Long = function(fromRaw[Float32](bits)).bits.raw
    check(
      """
        |c2447ac4 -> bf2f1cf0
        |43800001 -> 00000000
        |c48987e7 -> 00000000
        |7f800000 -> ffc00000
        |""",
      8
    )(single(Floats.sinBounded(_: Float32)))
    check(
      """
        |c2dc8aa4 -> be05559a
        |43800001 -> 3f800000
        |c48987e7 -> 3f800000
        |7f800000 -> ffc00000
        |""",
      8
    )(single(Floats.cosBounded(_: Float32)))
    check(
      """
        |5bff -> b9a8
        |5c01 -> 0000
        |dc01 -> 0000
        |7c00 -> fe00
        |""",
      4
    )(half(Floats.sinBounded(_: Float16)))
    check(
      """
        |5bff -> 39a8
        |5c01 -> 3c00
        |dc01 -> 3c00
        |7c00 -> fe00
        |""",
      4
    )(half(Floats.cosBounded(_: Float16)))
  }

  test("the binary64 reciprocal matches the hardware") {
    check(
      """
        |0000000000000000 -> 7ff0000000000000
        |8000000000000001 -> fff0000000000000
        |000fffffffffffff -> 7fd000000af80000
        |0008000000000000 -> 7fe0000000000000
        |3ff0000000000001 -> 3fefffffffffffff
        |3fefffffffffffff -> 3ff0000000000001
        |3ff921fb54442d18 -> 3fe45f306c400000
        |3fe62e42fefa39ef -> 3ff7154764dc0000
        |7fd8000000000000 -> 000aaaaaaa000000
        |7fe0000000000001 -> 0007ffffffffffff
        |7fefffffffffffff -> 0004000000000001
        |ffefffffffffffff -> 8004000000000001
        |fff0000000000000 -> 8000000000000000
        |7ff4000000000000 -> 7ffc000000000000
        |""",
      16
    )(TranscendentalUnit.reciprocal64)
  }

  test("the binary64 reciprocal square root matches the hardware") {
    check(
      """
        |0000000000000001 -> 6180000000000000
        |000fffffffffffff -> 5fe00000019e0000
        |00000000deadbeef -> 608127c7be820000
        |3ff0000000000001 -> 3fefffffffffffff
        |3fefffffffffffff -> 3ff00000019e0000
        |3fe0000000000000 -> 3ff6a09e60000000
        |4010000000000000 -> 3fe0000000000000
        |7fe5555555555555 -> 1ff3988e195a0000
        |7fefffffffffffff -> 1ff00000019e0000
        |8000000000000001 -> fff8000000000000
        |7ff0000000000000 -> 0000000000000000
        |fff0000000000000 -> fff8000000000000
        |7ff0000000000001 -> 7ff8000000000001
        |""",
      16
    )(TranscendentalUnit.reciprocalSquareRoot64)
  }

  test("the binary64 square root matches the hardware") {
    check(
      """
        |0000000000000000 -> 0000000000000000
        |8000000000000000 -> 8000000000000000
        |0000000000000001 -> 1e60000004000000
        |000fffffffffffff -> 1ffffffffc080000
        |3ff0000000000001 -> 3ff0000004000000
        |3fefffffffffffff -> 3feffffffc080000
        |4010000000000000 -> 4000000004000000
        |7fe8000000000000 -> 5febb67aec000000
        |3fffffffffffffff -> 3ff6a09e5fba0000
        |8000000000000001 -> fff8000000000000
        |7ff0000000000000 -> 7ff0000000000000
        |fff0000000000000 -> fff8000000000000
        |7ff4000000000000 -> 7ffc000000000000
        |""",
      16
    )(TranscendentalUnit.squareRoot64)
  }

  // gfx803, gfx906, gfx1036 and gfx1150 agree on the infinity rows
  test("FRACT of an infinity gives the negative default NaN at every width") {
    check(
      """
        |7c00 -> fe00
        |fc00 -> fe00
        |""",
      4
    )(half(Floats.fract(_: Float16)))
    check(
      """
        |7f800000 -> ffc00000
        |ff800000 -> ffc00000
        |""",
      8
    )(value => Floats.fract(fromRaw[Float32](value)).bits.raw)
    check(
      """
        |7ff0000000000000 -> fff8000000000000
        |fff0000000000000 -> fff8000000000000
        |""",
      16
    )(value => Floats.fract(fromRaw[Float64](value)).bits.raw)
  }

  test("LDEXP returns a NaN quieted with its sign and payload, and a quiet NaN unchanged, at every width") {
    for (exponent <- Seq(0, 5, -5))
      check(
        """
          |7c01 -> 7e01
          |fc01 -> fe01
          |7dff -> 7fff
          |7e01 -> 7e01
          |fe55 -> fe55
          |""",
        4
      )(value => Floats.ldexp(fromRaw[Float16](value), fromRaw[SInt[16]](exponent.toLong)).bits.raw)
    for (exponent <- Seq(0, 5, -5, Int.MaxValue, Int.MinValue)) {
      check(
        """
          |7f800001 -> 7fc00001
          |ff800001 -> ffc00001
          |7fa12345 -> 7fe12345
          |7fc00001 -> 7fc00001
          |ffc12345 -> ffc12345
          |""",
        8
      )(value => Floats.ldexp(fromRaw[Float32](value), fromRaw[SInt[32]](exponent.toLong)).bits.raw)
      check(
        """
          |7ff0000000000001 -> 7ff8000000000001
          |fff0000000000001 -> fff8000000000001
          |7ff4000000000000 -> 7ffc000000000000
          |7ff8000000000001 -> 7ff8000000000001
          |fff8000000000123 -> fff8000000000123
          |""",
        16
      )(value => Floats.ldexp(fromRaw[Float64](value), fromRaw[SInt[32]](exponent.toLong)).bits.raw)
    }
  }
}
