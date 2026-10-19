package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Harness.*

/** Floating-point and packed-half forms with the results an RTX 4060 Ti (sm_89) gives for them, or that the PTX ISA 9.0
  * instruction ptxas 13.3 lowers to them specifies. The sm_89 forms were assembled into a `nvcc -arch=sm_89` kernel in
  * place of its arithmetic and run with the CUDA driver; the others are ptxas output for the source each test names.
  */
class FloatingFormSuite extends munit.FunSuite {

  /** Runs `body` after moving each value into its register and stores R20 onwards, one word per expected value. */
  private def words(sm: Int, values: Vec[(Int, Long)], body: String, expected: Long*)(using munit.Location): Unit =
    check(
      sm,
      (Vec("LDC.64 R2, c[0x0][0x160]") ++ values.map((register, value) => f"MOV R$register, 0x$value%x") ++
        Vec(body) ++ Vec.from(expected.indices).map(index => f"STG.E [R2.64+0x${index * 4}%x], R${20 + index}") :+
        "EXIT").mkString("\n"),
      Vec.from(expected).map(_.toInt)
    )

  /** The legacy form of [[words]]: one result in R20. */
  private def legacy(sm: Int, values: Vec[(Int, Long)], body: String, expected: Long)(using munit.Location): Unit =
    check(
      sm,
      (values.map((register, value) => f"MOV32I R$register, 0x$value%x") ++
        Vec(body, "LDC.64 R2, c[0x0][0x160]", "STG.E [R2], R20", "EXIT")).mkString("\n"),
      Vec(expected.toInt)
    )

  private val predicates = "SEL R20, RZ, 0x1, !P0\nSEL R21, RZ, 0x1, !P1"

  // a = (1, 2), b = (2, 1), c = (3, 1), d = (1, 3), e = (1, 1), f = (2, 2) as (high, low) binary16 pairs
  private val halves =
    Vec(4 -> 0x3c004000L, 5 -> 0x40003c00L, 6 -> 0x42003c00L, 7 -> 0x3c004200L, 8 -> 0x3c003c00L, 9 -> 0x40004000L)

  for (
    (spelling, expected) <- Vec(
      "HSETP2.LT.AND P0, P1, R4, R5, PT"           -> Vec(0L, 1L),
      "HSETP2.EQ.OR P0, P1, R4, R5, P2"            -> Vec(1L, 1L),
      "HSETP2.EQ.OR P0, P1, R4, R5, !P2"           -> Vec(0L, 0L),
      "HSETP2.LT.H_AND.AND P0, P1, R8, R9, PT"     -> Vec(1L, 0L),
      "HSETP2.LT.H_AND.AND P0, P1, R4, R5, PT"     -> Vec(0L, 1L),
      "HSETP2.LT.H_AND.XOR P0, P1, R6, R7, P2"     -> Vec(1L, 0L),
      "HSETP2.T.AND P0, P1, R4, R5, PT"            -> Vec(1L, 1L),
      "HSETP2.F.OR P0, P1, R4, R5, P2"             -> Vec(1L, 1L),
      "HSETP2.GEU.AND P0, P1, |R4|, -R5.H0_H0, PT" -> Vec(1L, 1L),
      "HSETP2.NE.AND P0, P1, R4.H1_H1, 1, 2, PT"   -> Vec(1L, 0L)
    )
  )
    test(s"sm_89 $spelling") {
      words(89, halves, s"ISETP.EQ.AND P2, PT, RZ, RZ, PT\n$spelling\n$predicates", expected*)
    }

  for (
    (spelling, expected) <- Vec(
      "HSET2.BF.LT.OR R20, R6, R7, P2"          -> 0x3c003c00L,
      "HSET2.LT.XOR R20, R4, R5, P2"            -> 0x0000ffffL,
      "HSET2.BF.T.AND R20, R4, R5, !P2"         -> 0L,
      "HSET2.F.OR R20, R4, R5, P2"              -> 0xffffffffL,
      "HSET2.BF.LT.AND R20, R4.H1_H1, 2, 1, PT" -> 0x3c000000L,
      "HSET2.BF.GE.AND R20, R4, -R5, PT"        -> 0x3c003c00L
    )
  )
    test(s"sm_89 $spelling") {
      words(89, halves, s"ISETP.EQ.AND P2, PT, RZ, RZ, PT\n$spelling", expected)
    }

  // a = (1.5, 2.25), b = (2, 1): H0_NH1 puts the low half above the negated high half.
  test("sm_89 HFMA2 H0_NH1 swaps the halves and negates the new low half") {
    words(89, Vec(4 -> 0x3e004080L, 5 -> 0x40003c00L), "HFMA2 R20, R4, R5.H0_NH1, RZ", 0x3e00c480L)
  }

  // fma.rn.ftz.f16x2 (ptxas -arch=sm_89 emits HFMA2.FTZ R11, R2, R5, R6)
  for (
    (a, b, c, expected) <- Vec(
      (0x00010001L, 0x3c003c00L, 0L, 0L),
      (0x3c000200L, 0x3c003c00L, 0L, 0x3c000000L),
      (0x04000400L, 0x38003800L, 0L, 0L),
      (0x3c003c00L, 0x80010001L, 0x00010000L, 0L)
    )
  )
    test(f"sm_89 HFMA2.FTZ of 0x$a%08x, 0x$b%08x and 0x$c%08x flushes subnormal halves") {
      words(89, Vec(4 -> a, 5 -> b, 6 -> c), "HFMA2.FTZ R20, R4, R5, R6", expected)
    }

  test("sm_89 HADD2.F32 reads the low half of a source without a selector") {
    words(89, Vec(4 -> 0x3e004080L), "HADD2.F32 R20, -RZ, R4\nHADD2.F32 R21, -RZ, R4.H1_H1", 0x40100000L, 0x3fc00000L)
  }

  test("sm_89 FRND.F16 rounds the low half and clears the high half") {
    words(89, Vec(4 -> 0x3e004080L, 5 -> 0x7bfffbffL), "FRND.F16 R20, R4\nFRND.F16 R21, R5", 0x4000L, 0xfbffL)
  }

  test("sm_89 F2F.F32.F16 widens the high half under H1") {
    words(89, Vec(4 -> 0x3e004080L), "F2F.F32.F16 R20, R4.H1", 0x3fc00000L)
  }

  // The approximations agree with the correctly rounded results here.
  test("sm_89 MUFU.RCP.F16 and MUFU.TANH.F16 compute on the low half and clear the high half") {
    words(89, Vec(4 -> 0x7bfffbffL, 5 -> 0x63d01419L), "MUFU.RCP.F16 R20, R4\nMUFU.TANH.F16 R21, R5", 0x8100L, 0x1419L)
  }

  // c = (3, 4), (5, 6) and (7, 8)
  for (
    (spelling, source, merged, expected) <- Vec(
      ("F2FP.F16.F32.MERGE_C R20, R4, R5", 0x3fc00000L, 0x42004400L, 0x44003e00L),
      ("F2FP.F16.F32.MERGE_C R20, R4, R5.H1", 0x3fc00000L, 0x42004400L, 0x42003e00L),
      ("F2FP.F16.F32.MERGE_C.RZ R20, R4, R5", 0x477ff000L, 0x48004800L, 0x48007bffL),
      ("F2FP.SATFINITE.F16.F32.MERGE_C R20, R4, R5", 0x477ff000L, 0x48004800L, 0x48007bffL),
      ("F2FP.RELU.F16.F32.MERGE_C R20, R4, R5", 0xc0000000L, 0x45004600L, 0x46000000L)
    )
  )
    test(s"sm_89 $spelling") {
      words(89, Vec(4 -> source, 5 -> merged), spelling, expected)
    }

  for (
    (spelling, a, b, c, expected) <- Vec(
      ("F2IP.U8.F32 R20, R4, R5, R6", 0x3fc00000L, 0x40e00000L, 0x11223344L, 0x33440207L),
      ("F2IP.U8.F32 R20, R4, R5, R6.H1", 0x3fc00000L, 0x40e00000L, 0x11223344L, 0x11220207L),
      ("F2IP.S8.F32 R20, R4, R5, R6", 0xc0000000L, 0x437a999aL, 0x55667788L, 0x7788fe7fL),
      ("F2IP.U8.F32 R20, R4, R5, R6", 0x7fc00000L, 0x42ff0000L, 0x01020304L, 0x03048080L),
      ("F2IP.S8.F32.RELU R20, R4, R5, R6", 0x7fc00000L, 0x42ff0000L, 0x01020304L, 0x0304007fL)
    )
  )
    test(f"sm_89 $spelling with 0x$a%08x and 0x$b%08x") {
      words(89, Vec(4 -> a, 5 -> b, 6 -> c), spelling, expected)
    }

  // min.xorsign.abs.f32 (ptxas -arch=sm_89 emits FMNMX.XORSIGN R9, |R2|, |R5|, PT)
  for (
    (a, b, expected) <- Vec(
      (0xbf800000L, 0x40000000L, 0xbf800000L),
      (0x80000000L, 0x00000000L, 0x80000000L),
      (0x7fc00000L, 0xbf800000L, 0xbf800000L),
      (0xffc00000L, 0xc0800000L, 0x40800000L)
    )
  )
    test(f"sm_89 FMNMX.XORSIGN of 0x$a%08x and 0x$b%08x takes the sources' sign") {
      words(89, Vec(4 -> a, 5 -> b), "FMNMX.XORSIGN R20, |R4|, |R5|, PT", expected)
    }

  test("sm_89 FMUL scales round the scaled exact product once") {
    words(
      89,
      Vec(4 -> 0x40000000L, 5 -> 0x40400000L, 6 -> 0x000ae398L, 7 -> 0x3f800000L, 8 -> 0x7f800000L),
      "FMUL.D2 R20, R4, R5\nFMUL.D2 R21, R6, R7\nFMUL.M4 R22, R4, R5\nFMUL.M4 R23, R6, R7\nFMUL.D2 R24, RZ, R8",
      0x40400000L,
      0x000571ccL,
      0x41c00000L,
      0x002b8e60L,
      0x7fffffffL
    )
  }

  test("sm_89 FSET with a constant comparison") {
    words(89, Vec(4 -> 0x3f800000L), "FSET.BF.F.AND R20, R4, R4, PT\nFSET.BF.T.AND R21, R4, R4, PT", 0L, 0x3f800000L)
  }

  // __fadd_rz, __fadd_ru and __fadd_rd (ptxas emits FADD.RZ on every target)
  test("sm_89 directed FADD rounds the exact sum once") {
    words(
      89,
      Vec(4 -> 0x3f800000L, 5 -> 0x33800001L, 6 -> 0xb3800001L),
      "FADD.RZ R20, R4, R5\nFADD.RP R21, R4, R5\nFADD.RM R22, R4, R6\nFADD R23, R4, R5",
      0x3f800000L,
      0x3f800001L,
      0x3f7ffffeL,
      0x3f800001L
    )
  }

  // fmaxf on sm_35 to sm_62 (ptxas 11.8 emits FMNMX R6, R0, R5, !PT); max.f32 returns the number of a NaN pair and
  // orders -0 below +0.
  for (
    (sm, a, b, selector, expected) <- Vec(
      (50, 0x3f800000L, 0x40000000L, "!PT", 0x40000000L),
      (52, 0x7fc00000L, 0xc0000000L, "PT", 0xc0000000L),
      (61, 0x80000000L, 0x00000000L, "PT", 0x80000000L),
      (35, 0x80000000L, 0x00000000L, "!PT", 0x00000000L)
    )
  )
    test(f"sm_$sm FMNMX R20, R4, R5, $selector of 0x$a%08x and 0x$b%08x") {
      legacy(sm, Vec(4 -> a, 5 -> b), s"FMNMX R20, R4, R5, $selector", expected)
    }

  // a + 1.3f, y * 3.7f and the half2 forms with immediates under ptxas 11.8 -arch=sm_61
  for (
    (spelling, a, b, expected) <- Vec(
      ("FADD32I R20, R4, 1.2999999523162841797", 0x3f800000L, 0L, 0x40133333L),
      ("FMUL32I R20, R4, 3.7000000476837158203", 0x40000000L, 0L, 0x40eccccdL),
      ("FFMA32I R20, R4, 2, R20", 0x3fc00000L, 0L, 0x40400000L),
      ("HADD2_32I R20, R4, -2.69921875, 1.2998046875", 0x3c003c00L, 0L, 0xbecc409aL),
      ("HMUL2_32I R20, R4, 0.300048828125, 3.099609375", 0x40004000L, 0L, 0x38cd4633L),
      ("HFMA2_32I R20, R4, 7.5, 2.099609375, R20", 0x3c003c00L, 0L, 0x47804033L)
    )
  )
    test(s"sm_61 $spelling") {
      legacy(61, Vec(4 -> a, 20 -> b), spelling, expected)
    }

  test("sm_35 two-source FFMA32I accumulates into its destination") {
    legacy(35, Vec(4 -> 0x40000000L, 20 -> 0x3f800000L), "FFMA32I R20, R4, 1.5", 0x40800000L)
  }

  // truncf, rintf and floor under ptxas 11.8 -arch=sm_60; nvdisasm spells the integral rounding ROUND, the decoder RN
  for (
    (spelling, expected) <- Vec(
      "F2F.F32.F32.ROUND R20, R4" -> 0x40000000L,
      "F2F.RN R20, R4"            -> 0x40000000L,
      "F2F.F32.F32.FLOOR R20, R4" -> 0x40000000L,
      "F2F.F32.F32.CEIL R20, R4"  -> 0x40400000L,
      "F2F.F16.F32 R20, R4"       -> 0x4100L
    )
  )
    test(s"sm_60 $spelling of 2.5") {
      legacy(60, Vec(4 -> 0x40200000L), spelling, expected)
    }

  // __expf under ptxas 11.8 -arch=sm_60: RRO.EX2 R15, R10 feeding MUFU.EX2 R10, R15
  test("sm_60 RRO.EX2 feeds MUFU.EX2") {
    legacy(60, Vec(4 -> 0x40400000L), "RRO.EX2 R6, R4\nMUFU.EX2 R20, R6", 0x41000000L)
  }

  // __ffma2_rn, __fadd2_rn and __ffma2_rz under ptxas 13.3 -arch=sm_100
  test("sm_100 FFMA2 and directed FADD2 round each element once") {
    words(
      100,
      Vec(
        6  -> 0x3f800000L,
        7  -> 0x40000000L,
        8  -> 0x33800001L,
        9  -> 0xb3800001L,
        10 -> 0x40400000L,
        11 -> 0x3f800000L
      ),
      "FFMA2 R12, R6.F32x2, R10.F32x2, R8.F32x2\nFFMA2.RZ R14, R6.F32x2, 1.5, R8.F32x2\nFADD2.RZ R16, R6.F32x2, R8.F32x2\n" +
        "MOV R20, R12\nMOV R21, R13\nMOV R22, R14\nMOV R23, R15\nMOV R24, R16\nMOV R25, R17",
      0x40400000L,
      0x3fffffffL,
      0x3fc00000L,
      0x403fffffL,
      0x3f800000L,
      0x3fffffffL
    )
  }

  // fmaxf(fmaxf(a, b), c) and fminf(fminf(a, 2.5f), c) under ptxas 13.3 -arch=sm_100
  test("sm_100 FMNMX3 takes the extremum of three sources") {
    words(
      100,
      Vec(4 -> 0x3f800000L, 5 -> 0x7fc00000L, 6 -> 0xc0000000L),
      "FMNMX3 R20, R4, R5, R6, !PT\nFMNMX3 R21, R4, 2.5, R6, PT\nFMNMX3.NAN R22, R4, R5, R6, PT",
      0x3f800000L,
      0xc0000000L,
      0x7fffffffL
    )
  }

  // add.rn.f32.f16, sub.rn.f32.f16, add.rz.sat.f32.bf16 and fma.rn.f32.f16 under ptxas 13.3 -arch=sm_100
  test("sm_100 FHADD and FHFMA widen one half and round once") {
    words(
      100,
      Vec(4 -> 0x3f804200L, 5 -> 0x3f000000L, 6 -> 0x3fc00000L),
      "FHADD R20, R4, R5\nFHADD R21, R4, -R5\nFHADD.BF16.RZ.SAT R22, R4.H1, R5\nFHFMA R23, R4, R4.H1, R6",
      0x40600000L,
      0x40200000L,
      0x3f800000L,
      0x40e40000L
    )
  }

  // fmaf(a, b, 1.0f), u * b, rintf and floorf of uniform values under ptxas 13.3 -arch=sm_120
  test("sm_120 uniform floating arithmetic") {
    words(
      120,
      Vec.empty,
      "UMOV UR4, 0x40200000\nUMOV UR5, 0x40000000\nUFFMA UR6, UR4, UR5, 1\nUFMUL UR7, UR4, UR5\nUFRND UR8, UR4\n" +
        "UFRND.FLOOR UR9, UR4\nUF2F.BF16.F32 UR10, UR4\nUFMNMX UR11, UR4, UR5, !UPT\n" +
        "MOV R20, UR6\nMOV R21, UR7\nMOV R22, UR8\nMOV R23, UR9\nMOV R24, UR10\nMOV R25, UR11",
      0x40c00000L,
      0x40a00000L,
      0x40000000L,
      0x40000000L,
      0x4020L,
      0x40200000L
    )
  }
}
