package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Harness.*

/** Integer, predicate and uniform-datapath forms the NVIDIA disassembler tables list besides the ones ptxas favours:
  * the named LOP3 and PLOP3 operations, comparisons without a Boolean operation or with the constant `.F`/`.T` tests,
  * Kepler and Maxwell's unsigned comparison names, predicate files moved with dynamic masks, the uniform siblings of
  * the general-register instructions, packed lane arithmetic and the legacy video and SIMD instructions. Unless noted,
  * each is hand-assembled from its table entry, and its expected result follows the PTX ISA 13.3 instruction the form
  * implements.
  */
class IntegerFormsSuite extends munit.FunSuite {

  private def one(sm: Int, body: String, expected: Long)(using munit.Location): Unit =
    check(sm, s"$body; LDC.64 R2, c[0x0][0x160]; STG.E [R2], R4; EXIT", Vec(expected.toInt))

  private def pair(sm: Int, body: String, low: Long, high: Long)(using munit.Location): Unit =
    check(sm, s"$body; LDC.64 R2, c[0x0][0x160]; STG.E.64 [R2], R4; EXIT", Vec(low.toInt, high.toInt))

  private val Sections = java.util.regex.Pattern.compile("; ")

  private def holds(predicate: String): String = s"SEL R4, RZ, 0x1, !$predicate"

  // the constant tests and the named unsigned relations of Kepler and Maxwell
  for (
    (sm, comparison, expected) <- Vec(
      (75, "ISETP.F.AND P0, PT, R5, R6, PT", 0L),
      (75, "ISETP.T.AND P0, PT, R5, R6, PT", 1L),
      (35, "ISETP.LO.AND P0, PT, R5, R6, PT", 1L),
      (35, "ISETP.LT.AND P0, PT, R5, R6, PT", 0L),
      (50, "ISETP.HS P0, R6, R5", 1L),
      (50, "ISETP.LO.S32 P0, R5, R6", 0L),
      (120, "ISETP.F.U64 P0, R6, R8", 0L)
    )
  )
    test(s"sm_$sm $comparison") {
      one(
        sm,
        s"MOV R5, 0x1; MOV R6, 0xffffffff; MOV R7, RZ; MOV R8, RZ; MOV R9, RZ; $comparison; ${holds("P0")}",
        expected
      )
    }

  test("sm_50 ISET.LO.AND writes all ones where the unsigned relation holds, ISET.BF 1.0") {
    one(50, "MOV R5, 0x1; MOV R6, 0xffffffff; ISET.LO.AND R4, R5, R6, PT", 0xffffffffL)
    one(50, "MOV R5, 0x1; MOV R6, 0xffffffff; ISET.BF.LT.AND R4, R6, R5, PT", 0x3f800000L)
  }

  test("sm_120 ISETP.U64.EX compares high words, deferring to the low comparison where they are equal") {
    one(
      120,
      "MOV R8, 0x7; MOV R9, 0x1; MOV R10, 0x7; MOV R11, 0x1; ISETP.LT.U32.AND P1, PT, RZ, 0x1, PT; " +
        "ISETP.LT.U64.AND.EX P0, PT, R8, R10, PT, P1; " + holds("P0"),
      1L
    )
  }

  test("sm_75 PLOP3 single and paired outputs and the named operations") {
    val inputs = "MOV R5, 0x3; ISETP.NE.AND P0, PT, R5, RZ, PT; ISETP.EQ.AND P1, PT, R5, RZ, PT"
    one(75, s"$inputs; PLOP3.LUT P2, P0, P1, PT, 0xfc; ${holds("P2")}", 1L)
    one(
      75,
      s"$inputs; PLOP3.LUT P2, P3, P0, P1, PT, 0xc0, 0xfc; ${holds("P2")}; SEL R7, RZ, 0x2, !P3; " +
        "LOP3.LUT R4, R4, R7, RZ, 0xfc, !PT",
      2L
    )
    one(75, s"$inputs; PLOP3.AND P2, P0, P1, PT; ${holds("P2")}", 0L)
    one(75, s"$inputs; PLOP3.SEL P2, P0, PT, P1; ${holds("P2")}", 1L)
    one(75, s"$inputs; PLOP3.LUT P2, P0, R5.SIGN, PT, 0x30; ${holds("P2")}", 1L)
  }

  test("sm_75 uniform predicate logic and predicate-to-uniform moves") {
    one(
      75,
      "UMOV UR5, 0x3; UISETP.NE.AND UP0, UPT, UR5, URZ, UPT; UPLOP3.LUT UP1, UP0, UPT, UPT, 0xf0; " +
        "UPSETP.AND UP2, UP1, UP0; UP2UR UR4, UPR, URZ, 0x7; MOV R4, UR4",
      0x7L
    )
    one(75, "UMOV UR5, 0x5; UR2UP UPR, UR5, 0x7; UP2UR UR4, UPR, URZ, 0x7; MOV R4, UR4", 0x5L)
  }

  test("sm_50 PSETP and PSET without their second operation") {
    val inputs = "MOV R5, 0x3; ISETP.NE.AND P0, PT, R5, RZ, PT; ISETP.EQ.AND P1, PT, R5, RZ, PT"
    one(50, s"$inputs; PSETP.OR P2, P0, P1; ${holds("P2")}", 1L)
    one(50, s"$inputs; PSET.BF.AND R4, P0, !P1", 0x3f800000L)
    one(50, s"$inputs; PSET.AND.OR R4, P0, P1, P1", 0L)
  }

  // IADD sets CC.C where the addition carries out and CC.Z where it is zero
  test("sm_50 CSET and CSETP test the condition-code flags") {
    val flags = "MOV R5, 0xffffffff; MOV R6, 0x1; IADD RZ.CC, R5, R6"
    one(50, s"$flags; CSET.CFT.AND R4, CC, PT", 0xffffffffL)
    one(50, s"$flags; CSET.BF.ZFT.AND R4, CC, PT", 0x3f800000L)
    one(50, s"$flags; CSETP.LO.AND P0, PT, CC, PT; ${holds("P0")}", 0L)
    one(50, s"$flags; CSETP.HI P0, CC; ${holds("P0")}", 0L)
  }

  test("sm_75 P2R and R2P with dynamic masks and byte positions") {
    val inputs = "MOV R5, 0x3; ISETP.NE.AND P0, PT, R5, RZ, PT; ISETP.EQ.AND P1, PT, R5, RZ, PT"
    one(75, s"$inputs; MOV R7, 0x3; P2R R4, PR, RZ, R7", 1L)
    one(75, s"$inputs; MOV R8, 0xff00; P2R.B1 R4, PR, R8, 0x3", 0xfd00L)
    one(75, "MOV R7, 0x500; R2P PR, R7.B1, 0x7; P2R R4, PR, RZ, 0x7", 0x5L)
    one(75, s"$inputs; MOV R7, 0x2; MOV R8, 0x2; R2P PR, R7, R8; P2R R4, PR, RZ, 0x7", 0x3L)
  }

  test("sm_89 named LOP3 operations combine three sources, and an unused predicate input is ignored") {
    val inputs = "MOV R5, 0xf0f0; MOV R6, 0xff00"
    one(89, s"$inputs; LOP3.AND R4, R5, 0xffff, R6", 0xf000L)
    one(89, s"$inputs; LOP3.XOR R4, ~R5, 0x0, R6", 0xfffff00fL)
    one(89, s"$inputs; LOP3.PASS_B R4, R5, 0x12, R6", 0x12L)
    one(89, s"$inputs; MOV R7, 0x1; ISETP.NE.AND P0, PT, R7, RZ, PT; LOP3.LUT R4, R5, R6, R7, 0x96, P0", 0x0ff1L)
    one(89, s"$inputs; LOP3.LUT R4, R5, R6, RZ, 0xc0", 0xf000L)
  }

  test("sm_75 two-input uniform logic, shifts and sign extension") {
    one(75, "UMOV UR5, 0xf0; UMOV UR6, 0x3c; ULOP.XOR UR4, UR5, UR6; MOV R4, UR4", 0xccL)
    one(75, "UMOV UR5, 0xf0; ULOP32I.AND UR4, UR5, 0x30; MOV R4, UR4", 0x30L)
    one(75, "UMOV UR5, 0xf0; USHL UR4, UR5, 0x4; MOV R4, UR4", 0xf00L)
    one(75, "UMOV UR5, 0x3; USHL UR4, 0x1, UR5; MOV R4, UR4", 0x8L)
    one(75, "UMOV UR5, 0x80000000; USHR UR4, UR5, 0x4; MOV R4, UR4", 0xf8000000L)
    one(75, "UMOV UR5, 0x80000000; USHR.U32 UR4, UR5, 0x4; MOV R4, UR4", 0x08000000L)
    one(75, "UMOV UR5, 0x80; USGXT UR4, UR5, 0x8; MOV R4, UR4", 0xffffff80L)
    one(75, "MOV R5, 0xf; MOV R6, 0x24; SGXT.W R4, R5, R6", 0xffffffffL)
    one(75, "MOV R5, 0xf; MOV R6, 0x24; SGXT R4, R5, R6", 0xfL)
  }

  test("sm_120 uniform absolute value, conversion, packing, addition and moves") {
    one(120, "UMOV UR5, 0xfffffffb; UIABS UR4, UR5; MOV R4, UR4", 5L)
    one(120, "UMOV UR5, 0x12c; UI2I.U8.S32.SAT UR4, UR5; MOV R4, UR4", 0xffL)
    one(
      120,
      "UMOV UR5, 0x14; UMOV UR6, 0xfffffffd; UMOV UR7, 0x123456; UI2IP.U4.S32.SAT UR4, UR5, UR6, UR7.24; MOV R4, UR4",
      0x123456f0L
    )
    one(120, "UMOV UR5, 0x7; UMOV UR6, 0x9; UVIADD.32 UR4, UR5, UR6; MOV R4, UR4", 0x10L)
    one(100, "UMOV UR5, 0x2a; MOV.SPILL R4, UR5", 0x2aL)
    pair(120, "MOV64IUR R4, 0x1234", low = 0x1234L, high = 0L)
    pair(120, "UMOV UR6, 0x5; UMOV UR7, 0x6; MOV64IUR R4, UR6", low = 5L, high = 6L)
  }

  test("sm_75 UIADD3.64.X adds two carry inputs to a 64-bit sum") {
    pair(
      75,
      "UMOV UR6, 0xffffffff; UMOV UR7, 0x1; UMOV UR8, 0x1; UMOV UR9, URZ; UIADD3.64.X UR4, UR6, UR8, URZ, UPT, !UPT; " +
        "MOV R4, UR4; MOV R5, UR5",
      low = 1L,
      high = 2L
    )
  }

  test("integer conversions saturate, select bytes and pack lanes") {
    one(75, "MOV R5, 0xfffffed4; I2I.S8.S32.SAT R4, R5", 0xffffff80L)
    one(75, "MOV R5, 0xffff63c0; I2I.S16.S32.SAT R4, R5", 0xffff8000L)
    one(50, "MOV R5, 0x1234; I2I.U8.U16 R4, R5", 0x34L)
    one(50, "MOV R5, 0x1234; I2I.U8.U16.SAT R4, R5", 0xffL)
    one(50, "MOV R5, 0xab0000; I2I.U32.U8 R4, R5.B2", 0xabL)
    one(89, "MOV R5, 0x1ff; MOV R6, 0x102; MOV R7, 0xabcd; I2IP.U8.S32 R4, R5, R6, R7", 0xabcdff02L)
    one(75, "MOV R5, 0x14; MOV R6, 0xfffffffd; MOV R7, 0x123456; I2IP.U4.S32.SAT R4, R5, R6, R7.24", 0x123456f0L)
    one(89, "MOV R5, 0x5; MOV R6, 0xfffffffe; MOV R7, 0x1; I2IP.S2.S32.RELU R4, R5, R6, R7.28", 0x14L)
  }

  test("sm_75 wide multiplies and carry-in additions") {
    pair(75, "MOV R5, 0xfffffffe; MOV R6, 0x3; IMUL.WIDE R4, R5, R6", low = 0xfffffffaL, high = 0xffffffffL)
    pair(75, "MOV R5, 0xfffffffe; MOV R6, 0x3; IMUL.WIDE.U32 R4, R5, R6", low = 0xfffffffaL, high = 0x2L)
    pair(75, "MOV R5, 0xfffffffe; IMUL32I.WIDE.U32 R4, R5, 0x3", low = 0xfffffffaL, high = 0x2L)
    one(75, "MOV R5, 0x7; ISETP.NE.AND P0, PT, R5, RZ, PT; IADD32I.X R4, R5, 0x10, P0", 0x18L)
    one(75, "MOV R5, 0x3; MOV R6, 0x10; ISETP.NE.AND P0, PT, R5, RZ, PT; LEA.X R4, R5, R6, 0x2, P0", 0x1dL)
    pair(
      120,
      "MOV R6, 0xffffffff; MOV R7, 0x1; MOV R8, 0x1; MOV R9, 0x2; ISETP.NE.AND P0, PT, R7, RZ, PT; IADD.64.X R4, R6, R8, P0",
      low = 0x1L,
      high = 0x4L
    )
  }

  test("packed VIADD adds, negates and saturates each lane") {
    one(90, "MOV R5, 0x1ffff; VIADD.16x2 R4, R5, 0x10001", 0x20000L)
    one(120, "MOV R5, 0x10002; VIADD.U16x2 R4, -R5, RZ", 0xfffffffeL)
    one(100, "MOV R5, 0x7fff8000; MOV R6, 0x10001; VIADD.S16x2.ISAT R4, R5, R6", 0x7fff8001L)
    one(100, "MOV R5, 0xff01fe80; MOV R6, 0x01020304; VIADD.U8x4.ISAT R4, R5, R6", 0xff03ff84L)
  }

  // the accumulating 32-bit-immediate multiply-add writes the register its addend reads
  for (sm <- Vec(35, 50))
    test(s"sm_$sm IMAD32I multiplies by a full immediate and accumulates") {
      one(sm, "MOV R4, 0x5; MOV R5, 0x3; IMAD32I R4, R5, 0x12345, R4", 3L * 0x12345L + 5L)
    }

  // __sad and __usad on sm_35 (CUDA 11.8 ptxas emits ISAD and ISAD.U32)
  test("sm_35 ISAD sums an absolute difference") {
    one(35, "MOV R5, 0x5; MOV R6, 0x9; MOV R7, 0xa; ISAD R4, R5, R6, R7", 14L)
    one(35, "MOV R5, 0xffffffff; MOV R6, 0x1; MOV R7, 0xa; ISAD R4, R5, R6, R7", 12L)
    one(35, "MOV R5, 0xffffffff; MOV R6, 0x1; MOV R7, 0xa; ISAD.U32 R4, R5, R6, R7", 0x100000008L & 0xffffffffL)
  }

  test("sm_50 LEA.HI without a carry adds the funnel-shifted high word") {
    one(50, "MOV32I R5, 0xf0000001; MOV32I R6, 0x100; MOV32I R7, 0x2; LEA.HI R4, R5, R6, R7, 0x4", 0x100L + 0x2fL)
  }

  test("legacy CS2R reads one 32-bit counter word and leaves the next register alone") {
    one(50, "MOV R5, 0x77; CS2R R4, SR_CLOCKLO; CS2R R6, SR_CLOCKHI; MOV R4, R5", 0x77L)
    one(60, "MOV R5, 0x77; CS2R R4, SR_CLOCKHI; MOV R4, R5", 0x77L)
  }

  test("sm_120 CS2UR reads a running clock into a uniform pair") {
    one(
      120,
      "CS2UR UR6, SR_CLOCKLO; MOV R7, UR6; CS2UR.32 UR8, SR_CLOCKLO; MOV R8, UR8; IADD3 R4, R8, -R7, RZ; " +
        "ISETP.NE.U32.AND P0, PT, R4, RZ, PT; SEL R4, RZ, 0x1, !P0",
      1L
    )
  }

  test("sm_75 IDP4A names only its byte formats") {
    one(75, "MOV R5, 0x01020304; MOV R6, 0x01010101; MOV R7, 0xa; IDP4A.U8.U8 R4, R5, R6, R7", 20L)
  }

  // __vadd2, __vaddus2, __vcmpgtu2 and __vmaxs4 on sm_35 (CUDA 11.8 ptxas emits the SIMD video forms)
  test("sm_35 SIMD video instructions operate on each lane") {
    val inputs = "MOV R5, 0x1ffff; MOV R6, 0x10002"
    one(35, s"$inputs; VADD2.UD.U16.U16 R4, R5, R6, RZ", 0x20001L)
    one(35, s"$inputs; VADD2.UD.U16.U16.SAT R4, R5, R6, RZ", 0x2ffffL)
    one(35, s"$inputs; VSET2.GT.U16.U16 R4, R5, R6, RZ", 0x1L)
    one(35, "MOV R5, 0x7f80ff01; MOV R6, 0x01020304; VMNMX4.MX R4, R5, R6, RZ", 0x7f020304L)
    one(35, "MOV R5, 0x01020304; MOV R6, 0x04030201; VABSDIFF4.U8.U8.ACC R4, R5, R6, RZ", 8L)
  }

  // __vsub4 and __vsubus4 on sm_35 negate the second source of VADD4
  test("sm_35 VADD4 with a negated source subtracts each lane") {
    val inputs = "MOV R5, 0x01020304; MOV R6, 0x04030201"
    one(35, s"$inputs; VADD4.UD.U8.U8 R4, R5, -R6, RZ", 0xfdff0103L)
    one(35, s"$inputs; VADD4.UD.U8.U8.SAT R4, R5, -R6, RZ", 0x00000103L)
    one(35, s"$inputs; VADD4.SAT R4, R5, -R6, RZ", 0xfdff0103L)
  }

  test("legacy scalar video takes immediate sources and VSETP compares parts into a predicate") {
    one(50, "MOV R5, 0x10010; VADD.UD.U16.U16 R4, R5, 0x7, RZ", 0x17L)
    one(35, "MOV R5, 0x7; MOV R6, 0x9; VADD.UD.U32.U32 R4, R5, R6, RZ", 0x10L)
    one(50, "MOV R5, 0x5; MOV R6, 0x3; VSETP.GT.U16.U16.AND P0, PT, R5, R6, PT; SEL R4, RZ, 0x1, !P0", 1L)
    one(50, "MOV R5, 0x5; VSETP.LT.U16.U16 P0, R5, 0x7; SEL R4, RZ, 0x1, !P0", 1L)
  }

  // elect.sync's leader among a candidate predicate or a uniform lane mask, as ptxas lowers to on sm_90
  for (
    candidates <- Vec("ISETP.GT.U32.AND P1, PT, R0, 0x3, PT; ELECT P0, UR4, P1", "UMOV UR5, 0xf0; ELECT P0, UR4, UR5")
  )
    test(s"sm_90 ${Sections.split(candidates).last} elects the lowest candidate lane") {
      check(
        90,
        s"S2R R0, SR_TID.X; $candidates; SEL R4, RZ, 0x1, !P0; MOV R5, UR4; IMAD R4, R5, 0x10, R4; " +
          "LDC.64 R2, c[0x0][0x160]; IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2], R4; EXIT",
        Vec.tabulate(32)(lane => 0x40 + (if (lane == 4) 1 else 0))
      )
    }

  private def lanes(sm: Int, body: String, expected: Vec[Int])(using munit.Location): Unit =
    check(
      sm,
      s"S2R R0, SR_TID.X; $body; LDC.64 R2, c[0x0][0x160]; IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2], R4; EXIT",
      expected
    )

  private val residues = Vec.tabulate(32)(lane => (0 until 32).filter(_ % 3 == lane % 3).map(1 << _).sum)

  // __match_any_sync and __match_all_sync, which ptxas 13.3 emits as MATCH.ANY and MATCH.ALL on sm_70 and later
  test("MATCH.ANY gives each lane the lanes holding its value") {
    lanes(89, "MOV R6, 0x3; IMAD.HI.U32 R7, R0, 0x55555556, RZ; IMAD R5, R7, -0x3, R0; MATCH.ANY R4, R5", residues)
    lanes(
      75,
      "IMAD.HI.U32 R7, R0, 0x55555556, RZ; IMAD R6, R7, -0x3, R0; MOV R7, 0x9; MATCH.ANY.U64 R4, R6",
      residues
    )
  }

  test("MATCH.ALL gives every lane the active lanes only where all hold one value") {
    lanes(70, "MOV R5, 0x7; MATCH.ALL P0, R4, R5; SEL R6, RZ, 0x1, !P0; IADD3 R4, R4, R6, RZ", Vec.fill(32)(0))
    lanes(70, "MATCH.ALL P0, R4, R0; SEL R6, RZ, 0x1, !P0; IADD3 R4, R4, R6, RZ", Vec.fill(32)(0))
  }
}
