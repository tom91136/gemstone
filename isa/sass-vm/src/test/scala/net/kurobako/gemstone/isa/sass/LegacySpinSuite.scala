package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.testing.*

/** Kepler and Maxwell have no YIELD, so a warp polling a flag with a volatile load has to let the warp that sets it run
  * ahead, or the pollers spend the instruction budget the producer needs.
  */
class LegacySpinSuite extends Suite {
  private def waiters(sm: Int, load: String)(using munit.Location): Unit =
    check(
      sm,
      "S2R R0, SR_TID.X; LDC.64 R2, c[0x0][0x160]; ISETP.GE.AND P0, PT, R0, 0x20, PT; @P0 BRA `(spin); " +
        "MOV32I R5, 0x1388; work: IADD32I R5, R5, -0x1; ISETP.NE.AND P1, PT, R5, RZ, PT; @P1 BRA `(work); " +
        s"MOV32I R6, 0x1; ST.E [R2], R6; EXIT; spin: $load R6, [R2]; ISETP.EQ.AND P1, PT, R6, RZ, PT; " +
        "@P1 BRA `(spin); EXIT",
      Vec(1),
      block = Some(512),
      limit = 4000000L
    )

  private def sharedWaiters(sm: Int)(using munit.Location): Unit =
    check(
      sm,
      "S2R R0, SR_TID.X; LDC.64 R2, c[0x0][0x160]; ISETP.GE.AND P0, PT, R0, 0x20, PT; @P0 BRA `(spin); " +
        "MOV32I R5, 0x1388; work: LD.E.CV R7, [R2]; IADD32I R5, R5, -0x1; ISETP.NE.AND P1, PT, R5, RZ, PT; " +
        "@P1 BRA `(work); MOV32I R6, 0x1; STS [RZ], R6; ST.E [R2], R6; EXIT; " +
        "spin: LDS R6, [RZ]; ISETP.EQ.AND P1, PT, R6, RZ, PT; @P1 BRA `(spin); EXIT",
      Vec(1),
      block = Some(512),
      limit = 100000000L
    )

  for (sm <- Vec(35, 50)) {
    test(s"sm_$sm fifteen warps polling a volatile load stay within the budget of the warp that sets the flag") {
      waiters(sm, "LD.E.CV")
    }

    test(s"sm_$sm fifteen warps polling shared memory stay within the budget of the warp that sets the flag") {
      sharedWaiters(sm)
    }
  }
}
