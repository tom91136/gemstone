package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Harness.*

class WarpSuite extends munit.FunSuite {
  for {
    sm                    <- Vec(100, 103)
    (operation, expected) <- Vec("MIN" -> 0, "MAX" -> -1, "MIN.S32" -> -16, "MAX.S32" -> 15)
  }
    test(s"sm$sm CREDUX.$operation reduces varying lanes into a uniform register") {
      check(
        sm,
        s"""S2R R0, SR_TID.X
          |IADD3 R4, R0, -16, RZ
          |CREDUX.$operation UR6, R4
          |MOV R4, UR6
          |LDC.64 R2, c[0x0][0x160]
          |IMAD.WIDE.U32 R2, R0, 4, R2
          |STG.E [R2.64], R4
          |EXIT""".stripMargin,
        Vec.fill(32)(expected)
      )
    }

  for (sm <- Vec(100, 103)) {
    test(s"sm$sm VIMNMX accepts discarded predicate outputs") {
      check(
        sm,
        """S2R R0, SR_TID.X
          |VIMNMX R4, PT, PT, R0, 15, PT
          |LDC.64 R2, c[0x0][0x160]
          |IMAD.WIDE.U32 R2, R0, 4, R2
          |STG.E [R2.64], R4
          |EXIT""".stripMargin,
        Vec.tabulate(32)(lane => math.min(lane, 15))
      )
    }

    test(s"sm$sm REDUX defaults to bitwise AND with a uniform destination") {
      check(
        sm,
        """S2R R0, SR_TID.X
          |IADD3 R4, R0, 32, RZ
          |REDUX UR6, R4
          |MOV R4, UR6
          |LDC.64 R2, c[0x0][0x160]
          |IMAD.WIDE.U32 R2, R0, 4, R2
          |STG.E [R2.64], R4
          |EXIT""".stripMargin,
        Vec.fill(32)(32)
      )
    }
  }

  for ((operation, expected) <- Vec("MIN" -> -16, "MAX" -> 15))
    test(s"signed warp REDUX.$operation retains negative values") {
      check(
        86,
        s"""S2R R0, SR_TID.X
          |IADD3 R4, R0, -16, RZ
          |REDUX.$operation.S32 R4, R4
          |LDC.64 R2, c[0x0][0x160]
          |IMAD.WIDE.U32 R2, R0, 4, R2
          |STG.E [R2.64], R4
          |EXIT""".stripMargin,
        Vec.fill(32)(expected)
      )
    }

  test("collective ALL pairs its target with ENDCOLLECTIVE and includes every lane in the vote") {
    check(
      100,
      """S2R R0, SR_TID.X
        |LDC.64 R10, c[0x0][0x160]
        |IMAD.WIDE.U32 R10, R0, 4, R10
        |MOV R4, 42
        |ISETP.EQ.U32.AND P0, PT, R0, 31, PT
        |WARPSYNC.COLLECTIVE.ALL 0x90
        |VOTE.ANY P1, P0
        |SEL R4, R4, RZ, P1
        |ENDCOLLECTIVE
        |STG.E [R10], R4
        |EXIT""".stripMargin,
      Vec.fill(32)(42)
    )
  }

  test("collective warp synchronisation excludes nonmembers from the vote and rejoins them") {
    check(
      90,
      """S2R R0, SR_TID.X
        |LDC.64 R10, c[0x0][0x160]
        |IMAD.WIDE.U32 R10, R0, 4, R10
        |MOV R4, -1
        |MOV R5, 0xffff
        |ISETP.GE.U32.AND P0, PT, R0, 16, PT
        |BSSY B0, 0xc0
        |WARPSYNC.COLLECTIVE R5, 0xb0
        |VOTE.ANY P1, P0
        |SEL R4, R4, RZ, P1
        |ENDCOLLECTIVE
        |BSYNC B0
        |STG.E [R10], R4
        |EXIT""".stripMargin,
      Vec.fill(16)(0) ++ Vec.fill(16)(-1)
    )
  }

  test("leader election selects one active lane and global reduction preserves its update") {
    check(
      90,
      """LDC.64 R10, c[0x0][0x160]
        |MOV R4, 0
        |STG.E [R10], R4
        |ELECT P0, URZ, PT
        |MOV R4, 1
        |@P0 REDG.E.ADD [R10.64], R4
        |CGAERRBAR
        |PREEXIT
        |EXIT""".stripMargin,
      Vec(1, 0, 0, 0, 0)
    )
  }

  test("lane masks and bit reversal preserve warp-local lane numbering") {
    check(
      86,
      """S2R R0, SR_TID.X
        |S2R R4, SR_GEMASK
        |BREV R4, R4
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R4
        |EXIT""".stripMargin,
      Vec.tabulate(64)(lane => Integer.reverse(-1 << (lane % 32)))
    )
  }
}
