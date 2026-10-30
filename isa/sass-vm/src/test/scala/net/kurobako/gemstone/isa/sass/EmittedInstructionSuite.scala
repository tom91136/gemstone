package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.testing.Suite

/** Instructions as ptxas 13.3 (`ptxas -O3 -arch=sm_XX` on nvcc 13.3 and clang 22 PTX, disassembled by `nvdisasm -c`)
  * emits them for ordinary CUDA C++ or through the CUDA and CCCL headers, each named after its source, with the results
  * the matching PTX ISA 13.3 instruction specifies.
  */
class EmittedInstructionSuite extends Suite {

  /** Runs `body` on 32 threads, each storing `R4` to its word of the output. */
  private def lanes(sm: Int, body: String, expected: Vec[Int])(using munit.Location): Unit = check(
    sm,
    s"""S2R R0, SR_TID.X
       |$body
       |LDC.64 R2, c[0x0][0x160]
       |IMAD.WIDE.U32 R2, R0, 4, R2
       |STG.E [R2.64], R4
       |EXIT""".stripMargin,
    expected
  )

  private def same(sm: Int, body: String, expected: Long)(using munit.Location): Unit =
    lanes(sm, body, Vec.fill(32)(expected.toInt))

  // __floats2half2_rn and __floats2bfloat162_rn: cvt.rn.f16x2.f32 and cvt.rn.bf16x2.f32 put their first source in the
  // upper half.
  for (
    (sm, spelling, expected) <- Vec(
      (86, "F2FP.PACK_AB R4, R5, R6", 0x3e00c000L),
      (86, "F2FP.BF16.PACK_AB R4, R5, R6", 0x3fc0c000L),
      (90, "F2FP.F16.F32.PACK_AB R4, R5, R6", 0x3e00c000L),
      (90, "F2FP.F16.F32.PACK_AB R4, RZ, R6", 0x0000c000L),
      (90, "F2FP.BF16.F32.PACK_AB R4, R5, R6", 0x3fc0c000L),
      (90, "F2FP.BF16.F32.PACK_AB R4, RZ, R6", 0x0000c000L)
    )
  )
    test(s"sm_$sm $spelling packs the first source into the upper half") {
      same(sm, s"MOV R5, 0x3fc00000\nMOV R6, 0xc0000000\n$spelling", expected)
    }

  // __hmin2, __hmax2, __hmax2_nan and their bfloat16 forms: PT selects the minimum, !PT the maximum.
  for (
    (spelling, a, b, expected) <- Vec(
      ("HMNMX2 R4, R5, R6, PT", 0x3c004200L, 0x40004000L, 0x3c004000L),
      ("HMNMX2 R4, R5, R6, !PT", 0x3c004200L, 0x40004000L, 0x40004200L),
      ("HMNMX2 R4, R5.H0_H0, R6.H0_H0, PT", 0x3c004200L, 0x40004000L, 0x40004000L),
      ("HMNMX2.NAN R4, R5, R6, !PT", 0x7e003c00L, 0x3c004000L, 0x7fff4000L),
      ("HMNMX2.BF16_V2 R4, R5, R6, !PT", 0x3f804040L, 0x40004000L, 0x40004040L),
      ("HMNMX2.BF16_V2.NAN R4, R5, R6, PT", 0x3f804040L, 0x40004000L, 0x3f804000L)
    )
  )
    test(s"sm_86 $spelling") {
      same(86, f"MOV R5, 0x$a%08x\nMOV R6, 0x$b%08x\n$spelling", expected)
    }

  // __heq2, __hisnan2 and __hlt2_mask: HSET2 writes 1.0 under .BF, otherwise an all-ones mask, to each half that holds.
  for (
    (spelling, a, b, expected) <- Vec(
      ("HSET2.BF.EQ.AND R4, R5, R6, PT", 0x3c004000L, 0x3c003c00L, 0x3c000000L),
      ("HSET2.BF.EQ.AND R4, R5, R6, !PT", 0x3c004000L, 0x3c003c00L, 0L),
      ("HSET2.BF.EQ.OR R4, R5, R6, !PT", 0x3c004000L, 0x3c003c00L, 0x3c000000L),
      ("HSET2.BF.NAN.AND R4, R5, R5, PT", 0x7e003c00L, 0L, 0x3c000000L),
      ("HSET2.LT.AND R4, R5, R6, PT", 0x3c004000L, 0x40004000L, 0xffff0000L),
      ("HSET2.BF.NEU.AND R4, R5, R6, PT", 0x7e004000L, 0x7e004200L, 0x3c003c00L)
    )
  )
    test(s"sm_89 $spelling") {
      same(89, f"MOV R5, 0x$a%08x\nMOV R6, 0x$b%08x\n$spelling", expected)
    }

  // %nsmid reads a driver constant, and from sm_90 %nwarpid reads bits 8-15 of SR_VIRTCFG.
  for ((sm, offset) <- Vec(70 -> 0x10c, 75 -> 0x10c, 89 -> 0x10c, 90 -> 0x10c, 100 -> 0x2d0, 120 -> 0x2d0))
    test(f"sm_$sm c[0x0][0x$offset%x] is the multiprocessor count") {
      same(sm, f"LDC R4, c[0x0][0x$offset%x]", 4)
    }

  test("sm_90 SR_VIRTCFG holds the warps per multiprocessor") {
    same(90, "S2R R7, SR_VIRTCFG\nSHF.R.U32.HI R7, RZ, 0x8, R7\nSGXT.U32 R4, R7, 0x8", 48)
  }

  // __syncthreads_count: bar.red.popc.u32 counts the threads whose predicate holds.
  for (
    (sm, barrier) <- Vec(
      75 -> "BAR.RED.POPC 0x0, P0",
      86 -> "BAR.RED.POPC.DEFER_BLOCKING 0x0, P0",
      90 -> "BAR.RED.POPC.DEFER_BLOCKING 0x0, 0x20, P0"
    )
  )
    test(s"sm_$sm $barrier then B2R.RESULT counts the participants whose predicate holds") {
      same(sm, s"ISETP.GE.U32.AND P0, PT, R0, 0x14, PT\n$barrier\nB2R.RESULT R4", 12)
    }

  // bar.arrive and barrier.arrive with a thread count.
  test("sm_86 BAR.ARV signals arrival without waiting") {
    same(86, "BAR.ARV 0x1, 0x20\nMOV R4, 0x7", 7)
  }

  // clock64() and cuda::ptx::get_sreg_globaltimer: the counters advance and are nonzero.
  for (
    (
      sm,
      read
    ) <- Vec(
      86  -> "CS2R R4, SR_CLOCKLO",
      86  -> "CS2R R4, SR_GLOBALTIMERLO",
      120 -> "S2UR UR4, SR_CLOCKLO\nMOV R4, UR4",
      120 -> "CS2UR UR4, SR_CLOCKLO\nMOV R4, UR4"
    )
  )
    test(s"sm_$sm ${read.linesIterator.next()} reads a running counter") {
      same(
        sm,
        s"$read\nMOV R7, R4\nMOV R8, R4\n${read.replace("R4", "R6").replace("UR4", "UR6")}\nIADD3 R4, R6, -R7, RZ\nISETP.NE.U32.AND P0, PT, R4, RZ, PT\nSEL R4, RZ, 0x1, !P0",
        1
      )
    }

  // %smid, as ptxas reads it.
  test("sm_86 S2R SR_VIRTUALSMID names a multiprocessor of the device") {
    same(86, "S2R R4, SR_VIRTUALSMID\nISETP.LT.U32.AND P0, PT, R4, 0x4, PT\nSEL R4, RZ, 0x1, !P0", 1)
  }

  // __vabsdiffu4 and __vsadu4 under nvcc, and __sad and __usad.
  for (
    (sm, spelling, expected) <- Vec(
      (86, "VABSDIFF4.U8 R4, R5, R6, RZ", 0x03010103L),
      (86, "VABSDIFF4.U8.ACC R4, R5, R6, RZ", 8L),
      (86, "VABSDIFF4.U8.ACC R4, R5, R6, R7", 18L),
      (90, "VABSDIFF4.U8 R4, R5, R6, RZ", 0x03010103L)
    )
  )
    test(s"sm_$sm $spelling") {
      same(sm, s"MOV R5, 0x01020304\nMOV R6, 0x04030201\nMOV R7, 0xa\n$spelling", expected)
    }

  for ((spelling, expected) <- Vec("VABSDIFF.U32 R4, R5, R6, R7" -> 2L, "VABSDIFF R4, R5, R6, R7" -> 18L))
    test(s"sm_86 $spelling adds the absolute difference to the third source") {
      same(86, s"MOV R5, 0x3\nMOV R6, 0xfffffffb\nMOV R7, 0xa\n$spelling", expected)
    }

  // __dp4a, __dp2a_lo and __dp2a_hi.
  for (
    (spelling, a, b, expected) <- Vec(
      ("IDP.4A.S8.S8 R4, R5, R6, R7", 0x01ff0302L, 0x04030201L, 109L),
      ("IDP.4A.U8.U8 R4, R5, R6, R7", 0x01ff0302L, 0x04030201L, 877L),
      ("IDP.2A.LO.S16.S8 R4, R5, R6, R7", 0xfffe0003L, 0x0405ff02L, 108L),
      ("IDP.2A.HI.U16.U8 R4, R5, R6, R7", 0xfffe0003L, 0x0405ff02L, 262251L)
    )
  )
    test(s"sm_86 $spelling") {
      same(86, f"MOV R5, 0x$a%08x\nMOV R6, 0x$b%08x\nMOV R7, 0x64\n$spelling", expected)
    }

  // isspacep under clang: the output buffer is global, not shared.
  for ((spelling, expected) <- Vec("QSPC.E.G P0, RZ, [R2]" -> 1L, "QSPC.E.S P0, RZ, [R2]" -> 0L))
    test(s"sm_86 $spelling tests the address space of a global address") {
      same(86, s"LDC.64 R2, c[0x0][0x160]\n$spelling\nSEL R4, RZ, 0x1, !P0", expected)
    }

  // __nanosleep.
  for ((sm, spelling) <- Vec(75 -> "NANOSLEEP.WARP R5", 86 -> "NANOSLEEP R5", 90 -> "NANOSLEEP 0x64"))
    test(s"sm_$sm $spelling only delays the warp") {
      same(sm, s"MOV R5, 0x64\n$spelling\nMOV R4, 0x7", 7)
    }

  // __ffs: brev then bfind.shiftamt.u32.
  test("sm_86 FLO.U32.SH counts the bits above the highest set bit") {
    same(86, "MOV R5, 0x10\nFLO.U32.SH R4, R5", 27)
  }

  // atomicAdd(float *) with the result unused, atomicInc and atomicDec, and a generic float atomicAdd on sm_90, whose
  // predicate holds once the global operation is performed.
  for (
    (sm, operations, expected) <- Vec(
      (
        86,
        Vec("RED.E.ADD.F32.FTZ.RN.STRONG.GPU [R2.64], R5", "RED.E.ADD.F32.FTZ.RN.STRONG.GPU [R2.64], R5"),
        0x40400000L
      ),
      (
        90,
        Vec("REDG.E.ADD.F32.FTZ.RN.STRONG.GPU [R2.64], R5", "REDG.E.ADD.F32.FTZ.RN.STRONG.SM [R2.64], R5"),
        0x40400000L
      ),
      (
        86,
        Vec("MOV R5, 0x1", "ATOMG.E.INC.STRONG.GPU PT, R6, [R2.64], R5", "ATOMG.E.INC.STRONG.GPU PT, R6, [R2.64], R5"),
        0L
      ),
      (86, Vec("MOV R5, 0x3", "ATOMG.E.DEC.STRONG.GPU PT, R6, [R2.64], R5"), 3L),
      (
        90,
        Vec(
          "ATOM.E.ADD.F32.FTZ.RN.STRONG.SYS P0, RZ, [R2.64], R5",
          "@!P0 MOV R5, RZ",
          "@P0 ATOM.E.ADD.F32.FTZ.RN.STRONG.SYS PT, RZ, [R2.64], R5"
        ),
        0x40400000L
      )
    )
  )
    test(s"sm_$sm ${operations.mkString("; ")}") {
      same(
        sm,
        s"""LDC.64 R2, c[0x0][0x160]
           |IMAD.WIDE.U32 R2, R0, 4, R2
           |MOV R5, 0x3fc00000
           |${operations.mkString("\n")}
           |LDG.E R4, [R2.64]""".stripMargin,
        expected
      )
    }

  // Conversions to and from binary16 and bfloat16: __double2half, __int2half_rn, __short2half_rn, __half2int_rz,
  // __half2short_rz, __half2uchar_rz, __uint2bfloat16_rn, __bfloat162int_rz and __float2half_rz.
  for (
    (sm, setup, spelling, expected) <- Vec(
      (86, "MOV R6, RZ\nMOV R7, 0x3ff80000", "F2F.F16.F64 R4, R6", 0x3e00L),
      (86, "MOV R5, 0x3", "I2F.F16 R4, R5", 0x4200L),
      (86, "MOV R5, 0xffff", "I2F.F16.S16 R4, R5", 0xbc00L),
      (86, "MOV R5, 0xc100", "F2I.F16.TRUNC.NTZ R4, R5", 0xfffffffeL),
      (86, "MOV R5, 0xc100", "F2I.S16.F16.TRUNC.NTZ R4, R5\nLOP3.LUT R4, R4, 0xffff, RZ, 0xc0, !PT", 0xfffeL),
      (86, "MOV R5, 0x5cb0", "F2I.U8.F16.TRUNC.NTZ R4, R5\nLOP3.LUT R4, R4, 0xff, RZ, 0xc0, !PT", 0xffL),
      (90, "MOV R5, 0x3", "I2F.BF16.U32 R4, R5", 0x4040L),
      (90, "MOV R5, 0xc020", "F2I.BF16.TRUNC.NTZ R4, R5", 0xfffffffeL),
      (86, "MOV R5, 0x3f801800", "F2FP.PACK_AB.RZ R4, RZ, R5", 0x3c00L),
      (90, "MOV R5, 0x3f801800", "F2FP.F16.F32.PACK_AB.RZ R4, RZ, R5", 0x3c00L),
      (90, "MOV R5, 0x3f801800", "F2FP.F16.F32.PACK_AB R4, RZ, R5", 0x3c01L)
    )
  )
    test(s"sm_$sm ${spelling.linesIterator.next()}") {
      same(sm, s"$setup\n$spelling", expected)
    }

  // __hadd2, __hmul2, __hsub2, __hfma2_relu and __hfma2_sat on binary16 and bfloat16 pairs.
  for (
    (sm, spelling, expected) <- Vec(
      (90, "HFMA2.MMA R4, R5, 1, 1, R6", 0x3e004100L),
      (86, "HFMA2 R4, R5, 1, 1, R6", 0x3e004100L),
      (86, "HADD2 R4, R5, -R6", 0x38003e00L),
      (86, "HMUL2 R4, R5, R6", 0x38003c00L),
      (90, "HFMA2.MMA.RELU R4, R6, R6, -R5", 0x00000000L),
      (90, "HFMA2.MMA.SAT R4, R5, R5, RZ", 0x3c003c00L),
      (90, "HFMA2.BF16_V2 R4, R7, 1, 1, R8", 0x3fc04020L),
      (90, "HFMA2.BF16_V2.RELU R4, R8, R8, -R7", 0x00000000L)
    )
  )
    test(s"sm_$sm $spelling") {
      same(sm, s"MOV R5, 0x3c004000\nMOV R6, 0x38003800\nMOV R7, 0x3f804000\nMOV R8, 0x3f003f00\n$spelling", expected)
    }

  // cvt.pack.sat.s8.s32.b32, as ptxas lowers it.
  test("sm_90 I2IP.S8.S32.SAT clamps two words to bytes and packs them") {
    same(90, "MOV R5, 0x12c\nMOV R6, 0xffffff38\nI2IP.S8.S32.SAT R4, R5, R6, RZ", 0x7f80L)
  }

  // __vadd2 in conformance/unified/src/cuda/intrinsics.cu: each halfword sum wraps.
  for (sm <- Vec(90, 100, 120)) test(s"sm_$sm VIADD.16x2 adds the halfwords separately") {
    same(sm, "MOV R5, 0x7fff8001\nMOV R6, 0x1ffff\nVIADD.16x2 R4, R5, R6", 0x80008000L)
  }

  // __vsub2 and __vsub4 in conformance/unified/src/cuda/intrinsics.cu, as ptxas lowers them for sm_120: a negated
  // source negates each lane
  for (
    (spelling, expected) <- Vec(
      "MOV R5, 0x7fff8001\nMOV R6, 0x1ffff\nVIADD.16x2 R4, R5, -R6"  -> 0x7ffe8002L,
      "MOV R5, 0x1ff8040\nMOV R6, 0x2108080\nVIADD.U8x4 R4, R5, R6"  -> 0x030f00c0L,
      "MOV R5, 0x1ff8040\nMOV R6, 0x2108080\nVIADD.U8x4 R4, R5, -R6" -> 0xffef00c0L
    )
  ) test(s"sm_120 ${spelling.linesIterator.toSeq.last}") {
    same(120, spelling, expected)
  }

  // __vmaxu4, __vminu4 and __vmaxs4 in conformance/unified/src/cuda/intrinsics.cu, as ptxas lowers them for sm_120.
  for (
    (spelling, expected) <- Vec(
      "VIMNMX.U8x4 R4, R5, R6, !PT" -> 0x02ff8080L,
      "VIMNMX.U8x4 R4, R5, R6, PT"  -> 0x01108040L,
      "VIMNMX.S8x4 R4, R5, R6, !PT" -> 0x02108040L,
      "VIMNMX.U16x2 R4, R5, R6, PT" -> 0x01ff8040L
    )
  ) test(s"sm_120 $spelling") {
    same(120, s"MOV R5, 0x1ff8040\nMOV R6, 0x2108080\n$spelling", expected)
  }

  // ptxas for sm_100 can emit the packed forms with both predicate outputs discarded
  for (
    (spelling, expected) <- Vec(
      "VIMNMX.S16x2 R4, PT, PT, R5, R6, PT"  -> 0x01ff8040L,
      "VIMNMX.U16x2 R4, PT, PT, R5, R6, !PT" -> 0x02108080L
    )
  ) test(s"sm_100 $spelling") {
    same(100, s"MOV R5, 0x1ff8040\nMOV R6, 0x2108080\n$spelling", expected)
  }

  // cuda_fp8.hpp conversions on sm_89 and later.
  for (
    (setup, spelling, expected) <- Vec(
      (
        "MOV R5, 0x3fc00000\nMOV R6, 0xc0000000\nMOV R7, 0x12345678",
        "F2FP.SATFINITE.E4M3.F32.PACK_AB_MERGE_C R4, R5, R6, R7",
        0x12343cc0L
      ),
      ("MOV R5, 0x3fc00000\nMOV R6, 0xc0000000", "F2FP.SATFINITE.E5M2.F32.PACK_AB_MERGE_C R4, R5, R6, RZ", 0x3ec0L),
      ("MOV R5, 0x3e00c000", "F2FP.SATFINITE.E4M3.F16.UNPACK_B_MERGE_C R4, R5, RZ", 0x3cc0L),
      ("MOV R5, 0x3cc0", "F2FP.F16.E4M3.UNPACK_B R4, R5", 0x3e00c000L),
      ("MOV R5, 0x3ec0", "F2FP.F16.E5M2.UNPACK_B R4, R5", 0x3e00c000L)
    )
  )
    test(s"sm_90 $spelling") {
      same(90, s"$setup\n$spelling", expected)
    }

  // PTX 9.3 `clmad.lo.u64` and `clmad.hi.u64` as ptxas 13.3 emits them for sm_80, sm_90 and sm_100, the constant
  // bank reading the zero word and count; an RTX 4060 Ti gives the same results for a = 0x0123456789abcdef,
  // b = 0xfedcba9876543210 and c = 0x5555aaaa0000ffff.
  for (
    (sm, setup, spelling, expected) <- Vec(
      (80, "", "CLMAD.LO R4, R2, c[0x0][0x170], R8", 0x460231740000ffffL),
      (80, "", "CLMAD.HI R4, R2, c[0x0][0x170], R8", 0x5555aaaa02467530L),
      (90, "UMOV UR4, 0x76543210\nUMOV UR5, 0xfedcba98", "CLMAD.LO R4, R2, UR4, R8", 0x15f5d23228c8ef0fL),
      (90, "UMOV UR6, 0x87\nUMOV UR7, 0x0", "CLMAD.LO R4, R2, UR6, R8", 0xc31fc35869b569f2L),
      (100, "MOV R12, 0x76543210\nMOV R13, 0xfedcba98", "CLMAD.LO R4, R2, R12, R8", 0x15f5d23228c8ef0fL),
      (100, "MOV R12, 0x76543210\nMOV R13, 0xfedcba98", "CLMAD.HI R4, R2, R12, R8", 0x55b592726888af4fL),
      (
        100,
        "MOV R12, 0x76543210\nMOV R13, 0xfedcba98\nUMOV UR6, 0x5\nUMOV UR7, 0x0",
        "CLMAD.HI R4, R2, R12, UR6",
        0x00e038d8688850b5L
      ),
      (
        100,
        "MOV R12, 0x76543210\nMOV R13, 0xfedcba98",
        "CLMAD.HI R2, R2, R12, R8\nMOV R4, R2\nMOV R5, R3",
        0x55b592726888af4fL
      )
    )
  )
    test(s"sm_$sm ${spelling.linesIterator.next()}") {
      check(
        sm,
        Vec(
          "MOV R2, 0x89abcdef\nMOV R3, 0x1234567\nMOV R8, 0xffff\nMOV R9, 0x5555aaaa",
          setup,
          spelling,
          "LDC.64 R10, c[0x0][0x160]\nSTG.E.64 [R10.64], R4\nEXIT"
        ).filter(_.nonEmpty).mkString("\n"),
        Vec(expected.toInt, (expected >>> 32).toInt)
      )
    }
}
