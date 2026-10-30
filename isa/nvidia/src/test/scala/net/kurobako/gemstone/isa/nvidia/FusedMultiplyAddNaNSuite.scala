package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.testing.Suite

// NaN results as an RTX 4060 Ti computes them
class FusedMultiplyAddNaNSuite extends Suite {
  private val first      = 0x7ff8000000000001L
  private val second     = 0xfff8000000000002L
  private val signalling = 0x7ff0000000000003L
  private val one        = 0x3ff0000000000000L
  private val infinity   = 0x7ff0000000000000L
  private val invalid    = 0xfff8000000000000L

  private def float64(bits: Long): Float64 = fromRaw[Float64](bits)

  private inline def repeated(cases: Seq[(Long, Long, Long, Long)])(
      inline operation: (Long, Long, Long) => Long
  ): Unit = for {
    _                   <- 0 until 20000
    (a, b, c, expected) <- cases
  } assertEquals(operation(a, b, c), expected, f"$a%016x $b%016x $c%016x")

  test("DFMA propagates the NaN of b, then c, then a, quieted, and an invalid operation gives the default NaN") {
    repeated(
      Seq(
        (first, second, signalling, second),
        (second, one, signalling, 0x7ff8000000000003L),
        (first, one, signalling, 0x7ff8000000000003L),
        (first, second, one, second),
        (signalling, first, one, first),
        (infinity, 0L, one, invalid),
        (one, infinity, 0xfff0000000000000L, invalid)
      )
    )((a, b, c) =>
      toRaw(
        FloatArithmetic.fusedMultiplyAdd(float64(a), float64(b), float64(c), ConversionRounding.RN)
      )
    )
  }

  test("DADD and DMUL propagate the NaN of b, then a, quieted, and an invalid operation gives the default NaN") {
    val cases = Seq(
      (first, second, 0L, second),
      (second, first, 0L, first),
      (first, signalling, 0L, 0x7ff8000000000003L),
      (signalling, first, 0L, first),
      (signalling, one, 0L, 0x7ff8000000000003L)
    )
    repeated(cases :+ (infinity, 0xfff0000000000000L, 0L, invalid))((a, b, _) =>
      toRaw(FloatArithmetic.add(float64(a), float64(b)))
    )
    repeated(cases :+ (infinity, 0L, 0L, invalid))((a, b, _) =>
      toRaw(FloatArithmetic.multiply(float64(a), float64(b)))
    )
  }

  test("binary64 atomic addition returns a NaN operand, then a NaN old value, unquieted") {
    repeated(
      Seq(
        (first, signalling, 0L, signalling),
        (signalling, one, 0L, signalling),
        (signalling, first, 0L, first),
        (infinity, 0xfff0000000000000L, 0L, invalid)
      )
    )((old, operand, _) => toRaw(FloatArithmetic.atomicAdd(float64(old), float64(operand))))
  }

  test("binary16 and binary32 atomic addition gives the canonical NaN, and binary32 flushes subnormals") {
    repeated(
      Seq((0x7fc00001L, 0xffc00002L, 0L, 0x7fffffffL), (0x7f800000L, 0xff800000L, 0L, 0x7fffffffL))
        ++ Seq((1L, 0L, 0L, 0L), (0x3f800000L, 5L, 0L, 0x3f800000L))
    )((old, operand, _) =>
      toRaw(
        FloatArithmetic.atomicAdd(fromRaw[Float32](old), fromRaw[Float32](operand))
      )
    )
    repeated(Seq((0x7e01L, 0xfe02L, 0L, 0x7fffL), (0x7c00L, 0xfc00L, 0L, 0x7fffL), (0x3c00L, 0x3c00L, 0L, 0x4000L)))(
      (old, operand, _) =>
        toRaw(
          FloatArithmetic.atomicAdd(fromRaw[Float16](old), fromRaw[Float16](operand))
        )
    )
    repeated(Seq((0x7e013c00L, 0x3c00fc02L, 0L, 0x7fff7fffL), (0x3c003c00L, 0x3c00fc02L, 0L, 0x40007fffL)))(
      (old, operand, _) =>
        toRaw(
          FloatArithmetic.atomicAddPackedHalf(fromRaw[UInt[32]](old), fromRaw[UInt[32]](operand))
        )
    )
  }
}
