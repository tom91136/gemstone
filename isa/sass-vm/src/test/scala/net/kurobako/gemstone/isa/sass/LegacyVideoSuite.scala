package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.either.*

/** The Maxwell and Pascal forms ptxas (CUDA 11.8, `ptxas -arch=sm_50`) gives the scalar video instructions, saturating
  * high multiplies and generic atomics of conformance/unified/src/cuda/video.cu and half_atomics.cu, with the PTX ISA
  * 13.3 results of the PTX they come from.
  */
class LegacyVideoSuite extends Suite {
  import Harness.*

  private val a = 0x807f80ffL
  private val b = 0x01020304L
  private val c = 0x12345678L

  private val Sms    = Vec(50, 60)
  private val Spaces = Vec("shared", "global")

  // ptxas's cvta.shared takes the high word of a generic shared address from c0[0x100] on Pascal and zero on Maxwell
  private def sharedHigh(sm: Int): String = if (sm < 60) "RZ" else "c[0x0][0x100]"

  private def run(sm: Int, body: String, expected: Long)(using munit.Location): Unit =
    check(
      sm,
      f"MOV32I R0, 0x$a%x; MOV32I R1, 0x$b%x; MOV32I R2, 0x$c%x; $body; MOV R6, c[0x0][0x160]; " +
        "MOV R7, c[0x0][0x164]; STG.E [R6], R4; EXIT",
      Vec(expected.toInt)
    )

  // each instruction with the result of the PTX ptxas lowers to it, from the field values of the sources above
  for (
    (text, ptx, expected) <- Vec(
      ("VADD.UD.U32.U32 R4, R0, R1, RZ", "vadd.u32.u32.u32", 0x81818403L),
      ("VADD.S16.S8.SAT R4, R0.H1, R1.B2, RZ", "vadd.s32.s32.s32.sat d, a.h1, b.b2", 0xffff8081L),
      ("VADD.UD.U8.U8.ACC R4, R0, R1.B3, R2", "vadd.u32.u32.u32.add d, a.b0, b.b3, c", 0x12345778L),
      ("VADD.U16.S16.MIN R4, R0, -R1.H1, R2", "vsub.s32.u32.s32.min d, a.h0, b.h1, c", 0x7ffdL),
      ("VABSDIFF.SD.S8.S8.SAT.MRG_16H R4, R0, R1.B2, R2", "vabsdiff.s32.s32.s32.sat d.h1, a.b0, b.b2, c", 0x00035678L),
      ("VMNMX.UD.U8.S16.MX.SAT R4, R0.B1, R1.H1, RZ", "vmax.u32.u32.s32.sat d, a.b1, b.h1", 0x102L),
      ("VSHL.S16.U8.W.SAT R4, R0, R1, RZ", "vshl.s32.s32.u32.sat.wrap d, a.h0, b.b0", 0xfff80ff0L),
      ("VSHR.UD.U32.U8.ACC R4, R0, R1, R2", "vshr.u32.u32.u32.clamp.add d, a, b.b0, c", 0x1a3c4e87L),
      ("VSET.NE.U8.U8.ACC R4, R0.B2, R1.B2, R2", "vset.u32.u32.ne.add d, a.b2, b.b2, c", 0x12345679L),
      ("VSET.LT.U32.S32 R4, R0, R1, RZ", "vset.u32.s32.lt", 0L),
      ("VMAD.U16.U16.SHR_7 R4, R0, R1, R2", "vmad.u32.u32.u32.shr7 d, a.h0, b.h0, c", 0x2772aeL),
      ("VMAD.SAT R4, R0, R1, -R2", "vmad.s32.s32.s32.sat d, a, b, -c", 0x80000000L),
      ("IMAD.HI.SAT R4, R0, R1, R2", "mad.hi.sat.s32", 0x11b3d577L)
    )
  )
    for (sm <- Sms) test(s"sm_$sm $text, from $ptx") {
      run(sm, text, expected)
    }

  test("sm_50 IADD.SAT clamps the signed sum, from add.sat.s32") {
    check(
      50,
      "MOV32I R0, 0x7fffffff; MOV32I R1, 0x10; IADD.SAT R4, R0, R1; MOV R6, c[0x0][0x160]; MOV R7, c[0x0][0x164]; " +
        "STG.E [R6], R4; EXIT",
      Vec(0x7fffffff)
    )
  }

  // ptxas's mad.hi.s64 sequence takes signs through the condition codes
  for (sm <- Vec(35, 50)) test(s"sm_$sm I2I.S32.S32 RZ.CC and LOP.XOR RZ.CC set the sign that CSETP.SFT tests") {
    check(
      sm,
      "MOV32I R0, 0x80000000; MOV32I R1, 0x1; I2I.S32.S32 RZ.CC, R0; CSETP.SFT.AND P0, PT, CC, PT; " +
        "LOP.XOR RZ.CC, R1, R1; CSETP.SFT.AND P1, PT, CC, PT; SEL R4, RZ, 0x1, !P0; SEL R5, RZ, 0x2, !P1; " +
        "IADD R4, R4, R5; MOV R6, c[0x0][0x160]; MOV R7, c[0x0][0x164]; STG.E [R6], R4; EXIT",
      Vec(1)
    )
  }

  // atomicAdd through a generic pointer: LEA.HI.X tests whether the address is outside the shared window, and the
  // shared path masks it to an offset for ATOMS
  for (target <- Spaces)
    test(s"sm_50 generic RED and ATOMS paths behind the shared window test reach a $target counter") {
      val low  = if (target == "shared") "c[0x0][0x0]" else "c[0x0][0x160]"
      val high = if (target == "shared") sharedHigh(50) else "c[0x0][0x164]"
      check(
        50,
        "S2R R0, SR_TID.X; ISETP.NE.AND P1, PT, R0, RZ, PT; @!P1 STS [RZ], RZ; MOV32I R6, 0x7; @!P1 MOV R9, RZ; " +
          "MOV R2, c[0x0][0x160]; MOV R3, c[0x0][0x164]; @!P1 STG.E [R2], R9; BAR.SYNC 0x0; " +
          s"MOV R4, $low; MOV R5, $high; LEA RZ.CC, R4, RZ; LEA.HI.X P0, RZ, R4, RZ, R5; @!P0 BRA `(local); " +
          "RED.E.ADD [R4], R6; BRA `(done); local: LOP32I.AND R8, R4, 0xffffff; ATOMS.ADD RZ, [R8], R6; " +
          "done: BAR.SYNC 0x0; @P1 EXIT; LDS R10, [RZ]; LDG.E R11, [R2]; IADD R10, R10, R11; STG.E [R2], R10; EXIT",
        Vec(7 * 32),
        block = Some(32),
        shared = 4
      )
    }

  // clock64() as ptxas 11.8 lowers it: each CS2R reads one 32-bit half, and sm_60 discards a read into RZ first
  for (sm <- Vec(50, 60)) test(s"sm_$sm CS2R reads the 32-bit clock halves clock64() combines") {
    val kernel = load(sm, Fixture.listing(s"clock64Sm$sm"), "probe")
    assertEquals(kernel.launch(1)(Arg.zeros(8)).words(0).take(2), Vec(1L, 1L))
  }

  // %smid, %nsmid, %warpid and %nwarpid as ptxas 11.8 reads them: fields of SR_VIRTID and SR_VIRTCFG
  for (sm <- Vec(50, 60)) test(s"sm_$sm SR_VIRTID and SR_VIRTCFG place every warp on one of the multiprocessors") {
    val words = load(sm, Fixture.listing(s"virtualIdSm$sm"), "probe").launch(64)(Arg.zeros(64 * 16)).words(0)
    for (Vec(smid, nsmid, warpid, nwarpid) <- words.grouped(4)) {
      assert(smid < nsmid, s"smid $smid nsmid $nsmid")
      assert(warpid < nwarpid, s"warpid $warpid nwarpid $nwarpid")
    }
  }

  // __heq2 and __hisnan2 in conformance/unified/src/cuda/intrinsics.cu as ptxas 11.8 lowers them for sm_60
  test("sm_60 HSET2.BF writes 1.0 to each half that holds") {
    val inputs = Vec(0x3c004000L, 0x3c003c00L, 0x7e003c00L, 0x00003c00L)
    val run    = load(60, Fixture.listing("hset2Sm60"), "probe").launch(inputs.size)(
      Arg.zeros(inputs.size * 8),
      Arg.Buffer(Harness.words(inputs))
    )
    assertEquals(run.words(0), Vec(0x3c000000L, 0L, 0x3c000000L, 0L, 0x00003c00L, 0x3c000000L, 0x00003c00L, 0L))
  }

  // __heq2 in conformance/unified/src/cuda/intrinsics.cu, where ptxas 11.8 for sm_60 compares into a source register
  test("sm_60 HSET2.BF.EQ compares a pair into its own source register") {
    check(
      60,
      "MOV32I R6, 0x3c004000; MOV32I R0, 0x3c003c00; HSET2.BF.EQ.AND R0, R6, R0, PT; MOV R4, R0; " +
        "MOV R6, c[0x0][0x160]; MOV R7, c[0x0][0x164]; STG.E [R6], R4; EXIT",
      Vec(0x3c000000)
    )
  }

  // __hgeu(__high2half(x), __float2half(1.5F)) in conformance/unified/src/cuda/floating.cu, as ptxas 11.8 lowers it for
  // sm_60 with each immediate half held in its top ten bits
  for ((input, expected) <- Vec(0x3c004000 -> 0, 0x3e003c00 -> 1))
    test(f"sm_60 HSETP2.GEU compares the high half of 0x$input%08x with immediate halves of 1.5") {
      check(
        60,
        s"MOV32I R0, $input; HSETP2.GEU.AND P0, PT, R0.H1_H1, 1.5, 1.5, PT; SEL R4, RZ, 0x1, !P0; " +
          "MOV R6, c[0x0][0x160]; MOV R7, c[0x0][0x164]; STG.E [R6], R4; EXIT",
        Vec(expected),
        rewrite = instruction =>
          instruction.operation.form match {
            case _: Form.Hsetp2 =>
              val bytes = LittleEndian.toVec(0x7e1d83be0f870007L, 8).toArray ++ Array.fill[Byte](3)(0)
              InstructionDecoder.decode(instruction.address, bytes, target(60)).value
            case _ => instruction
          }
      )
    }

  // cuda::atomic_ref's relaxed fetch_add in conformance/unified/src/cuda/intrinsics.cu, as ptxas 11.8 lowers it for
  // sm_60, tests the window with the high word in a register that is not the pair's second half
  for {
    sm     <- Sms
    target <- Spaces
  }
    test(s"sm_$sm the shared window test reads its high word from an unpaired register for a $target counter") {
      val low  = if (target == "shared") "c[0x0][0x0]" else "c[0x0][0x160]"
      val high = if (target == "shared") sharedHigh(sm) else "c[0x0][0x164]"
      check(
        sm,
        "S2R R0, SR_TID.X; ISETP.NE.AND P1, PT, R0, RZ, PT; @!P1 STS [RZ], RZ; MOV32I R6, 0x7; @!P1 MOV R9, RZ; " +
          "MOV R2, c[0x0][0x160]; MOV R3, c[0x0][0x164]; @!P1 STG.E [R2], R9; BAR.SYNC 0x0; " +
          s"MOV R4, $low; MOV R0, $high; LEA RZ.CC, R4, RZ; LEA.HI.X P0, RZ, R4, RZ, R0; @!P0 BRA `(local); " +
          s"MOV R4, $low; MOV R5, $high; RED.E.ADD [R4], R6; BRA `(done); local: LOP32I.AND R8, R4, 0xffffff; " +
          "ATOMS.ADD RZ, [R8], R6; done: BAR.SYNC 0x0; @P1 EXIT; LDS R10, [RZ]; LDG.E R11, [R2]; " +
          "IADD R10, R10, R11; STG.E [R2], R10; EXIT",
        Vec(7 * 32),
        block = Some(32),
        shared = 4
      )
    }
}
