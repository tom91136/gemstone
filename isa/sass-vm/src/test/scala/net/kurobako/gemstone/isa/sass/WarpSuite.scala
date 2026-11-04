package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.*

class WarpSuite extends Suite {
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

  // ptxas spells redux.sync.min.u32 and .max.u32 REDUX.MIN and REDUX.MAX, and the .s32 forms with .S32
  for ((operation, expected) <- Vec("MIN" -> 0, "MAX" -> -1))
    test(s"untyped warp REDUX.$operation compares unsigned") {
      check(
        89,
        s"""S2R R0, SR_TID.X
          |IADD3 R4, R0, -16, RZ
          |REDUX.$operation UR4, R4
          |MOV R4, UR4
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

  test("REDUX.MAX reduces each MATCH.ANY group in a full warp") {
    check(
      89,
      """S2R R0, SR_TID.X
        |IMAD.HI.U32 R7, R0, 0x55555556, RZ
        |IMAD R5, R7, -0x3, R0
        |MATCH.ANY R7, R5
        |WARPSYNC.EXCLUSIVE R7
        |REDUX.MAX.S32 UR4, R0
        |MOV R4, UR4
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R4
        |EXIT""".stripMargin,
      Vec.tabulate(32)(lane => (0 until 32).filter(_ % 3 == lane % 3).max)
    )
  }

  test("REDUX.MAX reduces each MATCH.ANY group among the lanes that diverged into it") {
    check(
      89,
      """S2R R0, SR_TID.X
        |ISETP.LT.U32.AND P0, PT, R0, 16, PT
        |IMAD.HI.U32 R7, R0, 0x55555556, RZ
        |IMAD R5, R7, -0x3, R0
        |MOV R4, -1
        |BSSY B0, 0xc0
        |@!P0 BRA 0xb0
        |MATCH.ANY R7, R5
        |WARPSYNC.EXCLUSIVE R7
        |REDUX.MAX.S32 UR4, R0
        |MOV R4, UR4
        |BSYNC B0
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R4
        |EXIT""".stripMargin,
      Vec.tabulate(32)(lane => if (lane < 16) (0 until 16).filter(_ % 3 == lane % 3).max else -1)
    )
  }

  private val labelled =
    """S2R R0, SR_TID.X
      |IMAD.HI.U32 R7, R0, 0x55555556, RZ
      |IMAD R5, R7, -0x3, R0
      |MATCH.ANY R7, R5
      |""".stripMargin

  private val store =
    """LDC.64 R2, c[0x0][0x160]
      |IMAD.WIDE.U32 R2, R0, 4, R2
      |STG.E [R2.64], R4
      |EXIT""".stripMargin

  private def groupMask(lane: Int): Int = (0 until 32).collect { case i if i % 3 == lane % 3 => 1 << i }.sum

  test("WARPSYNC.EXCLUSIVE leaves only the member group active for a vote until the convergence barrier") {
    check(
      89,
      labelled +
        """BSSY B0, 0x80
          |WARPSYNC.EXCLUSIVE R7
          |VOTE.ANY R4, PT, PT
          |BSYNC B0
          |""".stripMargin + store,
      Vec.tabulate(32)(groupMask(_))
    )
  }

  test("the member groups of WARPSYNC.EXCLUSIVE rejoin at the convergence barrier") {
    check(
      89,
      labelled +
        """BSSY B0, 0x80
          |WARPSYNC.EXCLUSIVE R7
          |NOP
          |BSYNC B0
          |VOTE.ANY R4, PT, PT
          |""".stripMargin + store,
      Vec.fill(32)(-1)
    )
  }

  test("WARPSYNC.COLLECTIVE runs the lanes that name different masks as separate groups") {
    check(
      89,
      labelled +
        """BSSY B0, 0x90
          |WARPSYNC.COLLECTIVE R7, 0x80
          |VOTE.ANY R4, PT, PT
          |ENDCOLLECTIVE
          |BSYNC B0
          |""".stripMargin + store,
      Vec.tabulate(32)(groupMask(_))
    )
  }

  for ((limit, expected) <- Vec(16 -> 2, 32 -> 7))
    test(
      s"a guarded BRA.DIV is judged on the lanes its guard keeps and taken by every active lane (guard below $limit)"
    ) {
      check(
        120,
        s"""S2R R0, SR_TID.X
          |ISETP.LT.U32.AND P0, PT, R0, $limit, PT
          |UMOV UR4, 0xffffffff
          |MOV R4, 1
          |@P0 BRA.DIV UR4, 0x70
          |MOV R4, 7
          |BRA 0x80
          |MOV R4, 2
          |LDC.64 R2, c[0x0][0x160]
          |IMAD.WIDE.U32 R2, R0, 4, R2
          |STG.E [R2], R4
          |EXIT""".stripMargin,
        Vec.fill(32)(expected)
      )
    }

  test("a convergence barrier inside a member group leaves the group apart") {
    check(
      89,
      labelled +
        """BSSY B0, 0xb0
          |WARPSYNC.EXCLUSIVE R7
          |BSSY B1, 0x90
          |NOP
          |BSYNC B1
          |VOTE.ANY R4, PT, PT
          |BSYNC B0
          |""".stripMargin + store,
      Vec.tabulate(32)(groupMask(_))
    )
  }

  test("REDUX after the convergence barrier of WARPSYNC.EXCLUSIVE reduces over the whole warp") {
    check(
      89,
      labelled +
        """BSSY B0, 0x80
          |WARPSYNC.EXCLUSIVE R7
          |NOP
          |BSYNC B0
          |REDUX.MAX.S32 UR4, R0
          |MOV R4, UR4
          |""".stripMargin + store,
      Vec.fill(32)(31)
    )
  }

  test("ELECT elects one lane in each member group of WARPSYNC.EXCLUSIVE") {
    check(
      89,
      labelled +
        """BSSY B0, 0x80
          |WARPSYNC.EXCLUSIVE R7
          |ELECT P1, URZ, PT
          |BSYNC B0
          |MOV R6, 1
          |SEL R4, R6, RZ, P1
          |""".stripMargin + store,
      Vec.tabulate(32)(lane => if (lane < 3) 1 else 0)
    )
  }

  test("sm_120 labeled partition ballots only the peers its MATCH.ANY groups name") {
    val kernel = load(120, Fixture.listing("labeledPartitionSm120"), "labeled")
    val values = Vec.tabulate(32)(lane => (lane * 37L + 11L) % 101L)
    val label  = (lane: Int) => (lane * 7) % 5
    val peers  = (lane: Int) => (0 until 32).collect { case i if label(i) == label(lane) => 1L << i }.sum
    val run    = kernel.launch(32)(Arg.words(values*), Arg.zeros(128 * 4))
    assertEquals(run.words(1).slice(96, 128), Vec.tabulate(32)(peers))
    assertEquals(
      run.words(1).slice(64, 96),
      Vec.tabulate(32)(lane =>
        (0 until 32).collect {
          case other if label(other) == label(lane) && (values(other) & 1L) != 0L => 1L << other
        }.sum
      )
    )
    assertEquals(run.words(1)(64 + 1), 0x80200040L)
  }
}
