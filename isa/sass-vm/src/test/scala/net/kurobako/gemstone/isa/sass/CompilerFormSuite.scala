package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite

/** Forms ptxas 13.3 emits for the conversions, saturating arithmetic and half-precision atomics of the unified CUDA
  * cases (conformance/unified/src/cuda), each run on one lane over a few inputs.
  */
class CompilerFormSuite extends Suite {
  import Harness.*

  private def single(sm: Int, body: String, expected: Int*)(using munit.Location): Unit =
    check(sm, s"$body; LDC.64 R2, c[0x0][0x160]; STG.E [R2.64], R4; EXIT", Vec.from(expected))

  // cvt.rn.tf32.f32 and cvt.rz.relu.satfinite.tf32.f32 from sm_90
  for (
    (input, modifiers, expected) <- Vec(
      (0x3f801000, "", 0x3f800000),
      (0x3f803000, "", 0x3f804000),
      (0x3f803000, ".RZ", 0x3f802000),
      (0xbf803000, ".RELU", 0),
      (0x7f7fffff, ".SATFINITE", 0x7f7fe000),
      (0x7fc00001, "", 0x7fffe000)
    )
  )
    test(f"sm_90 F2FP.TF32.F32.PACK_B$modifiers of 0x$input%08x") {
      val rounding = if (modifiers == ".RZ") ".RZ" else ""
      val options  = if (modifiers == ".RZ") "" else modifiers
      single(90, f"MOV R2, 0x$input%x; F2FP$options.TF32.F32.PACK_B$rounding R4, R2", expected)
    }

  // cvt.rn.satfinite.relu.e4m3x2.f32: negative values become zero, NaN stays 0x7f
  test("sm_89 F2FP.SATFINITE.RELU.E4M3.F32.PACK_AB_MERGE_C") {
    single(
      89,
      "MOV R2, 0xbf800000; MOV R3, 0x3f800000; F2FP.SATFINITE.RELU.E4M3.F32.PACK_AB_MERGE_C R4, R2, R3, RZ",
      0x38
    )
    single(
      89,
      "MOV R2, 0x7fc00000; MOV R3, 0x44000000; F2FP.SATFINITE.RELU.E4M3.F32.PACK_AB_MERGE_C R4, R2, R3, RZ",
      0x7f7e
    )
  }

  // the bfloat16 atomicAdd compare-and-swap loops: binary32 to bfloat16, and a + b as a * 1 + b or a * b + 1
  test("sm_89 F2FP.E8M7.F32.PACK_AB and HFMA2.BF16_V2 with an immediate multiplier or addend") {
    single(89, "MOV R2, 0x3fc00000; F2FP.E8M7.F32.PACK_AB R4, R2, 1", 0x3fc03f80)
    single(89, "MOV R2, 0x40003f80; HFMA2.BF16_V2 R4, R2, 1, 1, R2", 0x40804000)
    single(89, "MOV R2, 0x40003f80; MOV R3, 0x3f803f80; HFMA2.BF16_V2 R4, R2.H0_H0, R3.H1_H1, 1, 1", 0x40004000)
  }

  // HADD2 with the second source as two immediate halves, upper first, and its bfloat16 form from sm_90
  test("HADD2 adds two immediate halves, and HADD2.BF16_V2 bfloat16 ones") {
    single(89, "MOV R2, 0x3c003c00; HADD2 R4, R2, 0.5, 1", 0x3e004000)
    single(90, "MOV R2, 0x3f803f80; HADD2.BF16_V2 R4, R2, 1, 1", 0x40004000)
  }

  // add.sat.s32: PLOP3 over the signs of the operands and the sum before sm_120, VIADD.S32.ISAT from it
  for ((a, b, expected) <- Vec((0x7fffffff, 1, 0x7fffffff), (0x80000000, -1, 0x80000000), (5, -7, -2)))
    test(f"saturating add of 0x$a%08x and 0x$b%08x") {
      single(
        89,
        f"MOV R0, 0x$a%x; MOV R1, 0x$b%x; IADD3 R3, R0, R1, RZ; " +
          "PLOP3.LUT P0, PT, R0.SIGN, R1.SIGN, R3.SIGN, 0x2, 0x0; PLOP3.LUT P1, PT, R0.SIGN, R1.SIGN, R3.SIGN, 0x40, 0x0; " +
          "SEL R4, R3, 0x7fffffff, !P0; SEL R4, R4, 0x80000000, !P1",
        expected
      )
      single(120, f"MOV R0, 0x$a%x; MOV R1, 0x$b%x; VIADD.S32.ISAT R4, R0, R1", expected)
    }

  // (double)x: widening is exact, so Maxwell and Hopper convert as Volta to Ada do
  test("sm_50 and sm_90 F2F.F64.F32 widens exactly") {
    check(
      50,
      "MOV32I R2, 0x3eaaaaab; F2F.F64.F32 R4, R2; MOV R2, c[0x0][0x160]; MOV R3, c[0x0][0x164]; STG.E.64 [R2], R4; EXIT",
      Vec(0x60000000, 0x3fd55555)
    )
    check(
      90,
      "MOV R2, 0x3eaaaaab; F2F.F64.F32 R4, R2; LDC.64 R2, c[0x0][0x160]; STG.E.64 [R2.64], R4; EXIT",
      Vec(0x60000000, 0x3fd55555)
    )
  }
}
