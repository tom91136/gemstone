package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite

/** Atomics as ptxas 13.3 emits them for atomicAdd on __half2 and __nv_bfloat162, atomicMin and atomicCAS through
  * generic pointers, and the compare-and-store loops of the bfloat16 and shared fallbacks
  * (conformance/unified/src/cuda/half_atomics.cu): a generic ATOM or RED acts on the window its address falls in.
  */
class AtomicSuite extends Suite {
  import Harness.*

  /** Zeroes four shared words but the third, all ones; runs `body` on 32 lanes; lanes 0 to 3 then store one word each.
    */
  private def shared(body: String): String =
    "S2R R0, SR_TID.X; ISETP.NE.AND P0, PT, R0, RZ, PT; MOV R8, RZ; MOV R9, RZ; MOV R10, 0xffffffff; MOV R11, RZ; " +
      s"@!P0 STS.128 [RZ], R8; BAR.SYNC 0; IADD3 R12, R0, 0x1, RZ; $body; BAR.SYNC 0; " +
      "ISETP.GE.U32.AND P1, PT, R0, 0x4, PT; @P1 EXIT; SHF.L.U32 R17, R0, 0x2, RZ; LDS R16, [R17]; " +
      "LDC.64 R2, c[0x0][0x160]; IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2.64], R16; EXIT"

  // atom.add.noftz.f16x2, atomicMin(unsigned) and atomicCAS on a generic pointer to shared memory (sm_89 listings of
  // half_atomics.cu), and the ATOMS.CAST.SPIN loop of the shared fallback.
  test("sm_89 generic ATOM reaches shared memory: F16x2 add, untyped unsigned MIN, CAS, and CAST.SPIN") {
    check(
      89,
      shared(
        "MOV R4, c[0x0][0x18]; MOV R5, c[0x0][0x1c]; MOV R6, 0x3c003c00; " +
          "ATOM.E.ADD.F16x2.RN.STRONG.GPU P2, R7, [R4.64], R6; ATOM.E.MIN.STRONG.GPU PT, RZ, [R4.64+0x8], R12; " +
          "retry: LDS R13, [0x4]; IADD3 R14, R13, 0x1, RZ; ATOMS.CAST.SPIN R15, [0x4], R13, R14; " +
          "ISETP.EQ.U32.AND P3, PT, R15, 0x1, PT; @!P3 BRA `(retry); " +
          "cas: LD.E R18, [R4.64+0xc]; IADD3 R19, R18, 0x2, RZ; ATOM.E.CAS.STRONG.GPU PT, R19, [R4+0xc], R18, R19; " +
          "ISETP.NE.AND P4, PT, R19, R18, PT; @P4 BRA `(cas)"
      ),
      Vec(0x50005000, 32, 1, 64),
      block = Some(32),
      shared = 16
    )
  }

  // atomicAdd(__nv_bfloat162 *) from sm_90, a generic ATOM.E.CAST.SPIN with a register result, and ATOMS.CAST.SPIN
  // reporting through a lone predicate (sm_90 listings of half_atomics.cu).
  test("sm_90 generic BF16x2 add and both CAST.SPIN result forms") {
    check(
      90,
      shared(
        "S2R R5, SR_SWINHI; MOV R4, RZ; MOV R6, 0x3f803f00; ATOM.E.ADD.BF16x2.RN.STRONG.GPU P2, RZ, [R4.64], R6; " +
          "retry: LDS R13, [0x4]; IADD3 R14, R13, 0x1, RZ; ATOMS.CAST.SPIN P3, [0x4], R13, R14; @!P3 BRA `(retry); " +
          "generic: LD.E R18, [R4.64+0xc]; IADD3 R19, R18, 0x3, RZ; ATOM.E.CAST.SPIN PT, R19, [R4.64+0xc], R18, R19; " +
          "ISETP.EQ.U32.AND P4, PT, R19, 0x1, PT; @!P4 BRA `(generic)"
      ),
      Vec(0x42004180, 32, -1, 96),
      block = Some(32),
      shared = 16
    )
  }

  // atomicAdd(double *) on shared memory (sm_89 listing of memory_atomics.cu): ATOMS.CAST.SPIN.64 reports success as
  // the 64-bit value 1, which the loop tests word by word.
  test("sm_89 ATOMS.CAST.SPIN.64 writes its success flag to both words of the pair") {
    check(
      89,
      shared(
        "MOV R6, 0x12345678; MOV R7, 0x9abcdef0; " +
          "retry: LDS.64 R4, [0x8]; DADD R6, R4, 0.75; ATOMS.CAST.SPIN.64 R6, [0x8], R4, R6; " +
          "ISETP.EQ.U32.AND P3, PT, R6, 0x1, PT; ISETP.EQ.U32.AND.EX P3, PT, R7, RZ, PT, P3; @!P3 BRA `(retry)"
      ),
      Vec(0, 0, 0, 0x40380000),
      block = Some(32),
      shared = 16
    )
  }
}
