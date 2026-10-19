package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*

/** NVIDIA GPUs fault a load, store or atomic whose address is not a multiple of its size, in every state space, with
  * `cudaErrorMisalignedAddress`; R2 and R3 hold the 16-byte aligned global buffer.
  */
class AlignmentSuite extends munit.FunSuite {
  import Harness.*

  private def run(access: String, offset: Int): Either[Fault, Run] = {
    val located = access.replace("OFFSET", s"0x${offset.toHexString}")
    program(89, s"LDC.64 R2, c[0x0][0x160]; MOV R4, 0x1; MOV R5, 0x2; MOV R6, 0x3; MOV R7, 0x4; $located; EXIT", 64)
      .run(1)(Arg.zeros(64))
  }

  private val accesses = Vec(
    "LDG.E.U16 R4, [R2.64+OFFSET]"                      -> 2,
    "LDG.E R4, [R2.64+OFFSET]"                          -> 4,
    "LDG.E.64 R4, [R2.64+OFFSET]"                       -> 8,
    "LDG.E.128 R4, [R2.64+OFFSET]"                      -> 16,
    "STG.E.U16 [R2.64+OFFSET], R4"                      -> 2,
    "STG.E.64 [R2.64+OFFSET], R4"                       -> 8,
    "STG.E.128 [R2.64+OFFSET], R4"                      -> 16,
    "LD.E.64 R4, [R2.64+OFFSET]"                        -> 8,
    "ST.E [R2.64+OFFSET], R4"                           -> 4,
    "ATOMG.E.ADD.STRONG.GPU PT, R4, [R2.64+OFFSET], R5" -> 4,
    "ATOM.E.ADD R4, [R2.64+OFFSET], R5"                 -> 4,
    "RED.E.ADD [R2.64+OFFSET], R5"                      -> 4,
    "LDS.64 R4, [OFFSET]"                               -> 8,
    "STS.128 [OFFSET], R4"                              -> 16,
    "ATOMS.ADD R4, [OFFSET], R5"                        -> 4,
    "LDL.64 R4, [OFFSET]"                               -> 8,
    "STL [OFFSET], R4"                                  -> 4
  )

  for ((access, size) <- accesses) {
    test(s"$access at a multiple of $size bytes runs") {
      assert(run(access, size).isRight, run(access, size))
    }
    test(s"$access at an odd multiple of ${size / 2} bytes faults as misaligned") {
      assert(
        run(access, size / 2).left.exists { case Fault.Misaligned(_) => true; case _ => false },
        run(access, size / 2)
      )
    }
  }

  private def wide(access: String, offset: Int): Either[Fault, Run] =
    program(
      100,
      s"LDC.64 R2, c[0x0][0x380]; MOV R4, 0x1; MOV R8, 0x2; ${access.replace("OFFSET", s"0x${offset.toHexString}")}; EXIT",
      base = 0x380
    ).run(1)(Arg.zeros(128))

  private val wideAccesses = Vec(
    "LDG.E.ENL2.256 R4, R8, desc[UR4][R2.64+OFFSET], 0xff",
    "STG.E.ENL2.256 desc[UR4][R2.64+OFFSET], R4, R8"
  )

  for (access <- wideAccesses) {
    test(s"sm_100 $access at a multiple of 32 bytes runs") {
      assert(wide(access, 32).isRight, wide(access, 32))
    }
    test(s"sm_100 $access at an odd multiple of 16 bytes faults as misaligned, as 256-bit accesses need 32") {
      assert(wide(access, 16).left.exists { case Fault.Misaligned(_) => true; case _ => false }, wide(access, 16))
    }
  }
}
