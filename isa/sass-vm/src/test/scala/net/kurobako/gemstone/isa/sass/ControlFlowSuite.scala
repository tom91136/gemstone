package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class ControlFlowSuite extends Suite {
  private val Flags = Vec(false, true)

  for (sm <- Vec(35, 50, 60)) {
    test(s"sm_$sm loop backedges retain the enclosing break token across divergent exits") {
      check(
        sm,
        """S2R R0, SR_TID.X
          |IADD R5, R0, 1
          |MOV R4, RZ
          |PBK 0xa0
          |IADD R4, R4, 1
          |ISETP.GE.U32.AND P0, PT, R4, R5, PT
          |@P0 BRK
          |BRA 0x40
          |NOP
          |BRK
          |LDC.64 R2, c[0x0][0x160]
          |ISCADD R2.CC, R0, R2, 2
          |IADD.X R3, RZ, R3
          |STG.E [R2], R4
          |EXIT""".stripMargin,
        Vec(1, 2, 3, 4)
      )
    }

    test(s"sm_$sm a callee's break token does not clobber the caller's live synchronisation token") {
      check(
        sm,
        """S2R R0, SR_TID.X
          |MOV R4, RZ
          |ISETP.LT.U32.AND P0, PT, R0, 16, PT
          |SSY 0x70
          |@P0 NOP.S
          |CAL 0xd0
          |NOP.S
          |BAR.SYNC 0x0
          |LDC.64 R2, c[0x0][0x160]
          |ISCADD R2.CC, R0, R2, 2
          |IADD.X R3, RZ, R3
          |STG.E [R2], R4
          |EXIT
          |PBK 0x100
          |IADD R4, R4, 7
          |BRK
          |RET""".stripMargin,
        Vec.tabulate(32)(lane => if (lane < 16) 0 else 7)
      )
    }

    test(s"sm_$sm an indirect callee's break token does not clobber the caller's live synchronisation token") {
      check(
        sm,
        """S2R R0, SR_TID.X
          |MOV R4, RZ
          |ISETP.LT.U32.AND P0, PT, R0, 16, PT
          |MOV R6, 0xf0
          |SSY 0x90
          |@P0 NOP.S
          |PRET 0x80
          |BRX R6, -0x78
          |NOP.S
          |BAR.SYNC 0x0
          |LDC.64 R2, c[0x0][0x160]
          |ISCADD R2.CC, R0, R2, 2
          |IADD.X R3, RZ, R3
          |STG.E [R2], R4
          |EXIT
          |PBK 0x120
          |IADD R4, R4, 7
          |BRK
          |RET""".stripMargin,
        Vec.tabulate(32)(lane => if (lane < 16) 0 else 7)
      )
    }

    test(s"sm_$sm a branch may leave a synchronisation region for code that consumes none of its tokens") {
      check(
        sm,
        """S2R R0, SR_TID.X
          |LDC.64 R2, c[0x0][0x160]
          |IMAD.WIDE.U32 R2, R0, 4, R2
          |ISETP.LT.U32.AND P0, PT, R0, 16, PT
          |MOV R4, 2
          |STG.E [R2.64], R4
          |SSY 0xa0
          |@P0 BRA 0xb0
          |MOV R4, 1
          |NOP.S
          |STG.E [R2.64], R4
          |EXIT""".stripMargin,
        Vec.fill(16)(2) ++ Vec.fill(16)(1)
      )
    }

    test(s"sm_$sm code joined from inside and outside a synchronisation region cannot transfer or re-enter it") {
      Vec(
        "SSY 0x80; SSY 0x50; @P0 BRA 0x60; NOP.S; NOP; NOP; NOP.S; NOP; EXIT",
        "SSY 0x40; @P0 BRA 0x50; NOP.S; NOP; MOV R5, 1; @P1 BRA 0x40; EXIT",
        "PBK 0x80; SSY 0x50; @P0 BRA 0x60; NOP.S; NOP; MOV R5, 1; BRK; NOP; EXIT"
      ).foreach(text => assert(program(sm, text, base = 0).compile().isLeft, text))
    }

    test(s"sm_$sm tokens pushed after leaving a synchronisation region are popped on every joined path") {
      check(
        sm,
        """S2R R0, SR_TID.X
          |LDC.64 R2, c[0x0][0x160]
          |IMAD.WIDE.U32 R2, R0, 4, R2
          |ISETP.LT.U32.AND P0, PT, R0, 16, PT
          |ISETP.LT.U32.AND P1, PT, R0, 8, PT
          |MOV R4, 2
          |SSY 0x90
          |@P0 BRA 0xa0
          |NOP.S
          |MOV R4, 1
          |SSY 0xe0
          |@P1 NOP.S
          |IADD R4, R4, 4
          |NOP.S
          |STG.E [R2.64], R4
          |EXIT""".stripMargin,
        Vec.fill(8)(2) ++ Vec.fill(8)(6) ++ Vec.fill(16)(5)
      )
    }

    test(s"sm_$sm never-executed transfers outside any token region are ignored") {
      check(
        sm,
        """S2R R0, SR_TID.X
          |@!PT NOP.S
          |@!PT BRK
          |@!PT SYNC
          |LDC.64 R2, c[0x0][0x160]
          |IMAD.WIDE.U32 R2, R0, 4, R2
          |STG.E [R2.64], R0
          |EXIT""".stripMargin,
        Vec.tabulate(4)(identity)
      )
    }

    test(s"sm_$sm a break from a block placed after the PBK target pops the token its path pushed") {
      check(
        sm,
        """S2R R0, SR_TID.X
          |MOV R4, RZ
          |ISETP.LT.U32.AND P0, PT, R0, 16, PT
          |PBK 0x70
          |@P0 BRA 0xb0
          |MOV R4, 1
          |BRK
          |LDC.64 R2, c[0x0][0x160]
          |IMAD.WIDE.U32 R2, R0, 4, R2
          |STG.E [R2.64], R4
          |EXIT
          |MOV R4, 2
          |BRK""".stripMargin,
        Vec.fill(16)(2) ++ Vec.fill(16)(1)
      )
    }

    test(s"sm_$sm switch cases reached through an indirect branch keep the enclosing synchronisation token") {
      check(
        sm,
        """S2R R0, SR_TID.X
          |LOP.AND R4, R0, 1
          |SHL R5, R4, 0x5
          |IADD R5, R5, 0x70
          |SSY 0xb0
          |BRX R5, -0x58
          |EXIT
          |MOV R6, 3
          |NOP.S
          |MOV R6, 5
          |NOP.S
          |LDC.64 R2, c[0x0][0x160]
          |IMAD.WIDE.U32 R2, R0, 4, R2
          |STG.E [R2.64], R6
          |EXIT""".stripMargin,
        Vec.tabulate(32)(lane => if (lane % 2 == 0) 3 else 5)
      )
    }

    test(s"sm_$sm an external late entry cannot bypass setup and loop back into a break") {
      assert(program(sm, "BRA 0x40; PBK 0x70; NOP; BRK; BRA 0x20; NOP; BRK; EXIT", base = 0).compile().isLeft)
    }

    test(s"sm_$sm indirect branches resolve a control-word address") {
      val addresses = Vec(8, 16, 24, 32, 72, 80, 88, 96)
      check(
        sm,
        "MOV R4, 64; BRX R4, -0x18; MOV R6, 99; EXIT; MOV R6, 7; LDC.64 R2, c[0x0][0x160]; STG.E [R2], R6; EXIT",
        Vec(7),
        rewrite = instruction => instruction.copy(address = addresses(instruction.address.raw / 16).unsafe)
      )
    }

    // The shape CUDA 11.8 ptxas -O3 -arch=sm_35 gives a non-inlined recursive device function: its own break token is
    // live across the recursive CAL, so each level restores it when the callee returns.
    test(s"sm_$sm recursive calls inside a break region restore the caller's token on return") {
      check(
        sm,
        """S2R R0, SR_TID.X
          |MOV R4, R0
          |MOV R5, RZ
          |CAL 0x90
          |LDC.64 R2, c[0x0][0x160]
          |ISCADD R2.CC, R0, R2, 2
          |IADD.X R3, RZ, R3
          |STG.E [R2], R5
          |EXIT
          |PBK 0x100
          |ISETP.EQ.U32.AND P0, PT, R4, RZ, PT
          |@P0 BRK
          |IADD R5, R5, R4
          |IADD32I R4, R4, -0x1
          |CAL 0x90
          |BRK
          |RET""".stripMargin,
        Vec.tabulate(20)(depth => depth * (depth + 1) / 2)
      )
    }
  }

  test("only unconditional terminators that end execution start an indirectly called function") {
    val instructions = Listing.program(
      "MOV R6, 0x20; PRET 0x30; BRX R6, -0x10; EXIT; BPT.INT 0x1; MOV R4, 1; BPT.TRAP 0x1; MOV R4, 2; BRA.CC.EQ 0xc0; " +
        "MOV R4, 3; BRA CC.EQU, 0xc0; MOV R4, 4; RET"
    ).value.instructions
    assertEquals(
      LegacyControlFlow.functionEntries(instructions).map(instructions(_).address.raw),
      Vec(0x30, 0x40, 0x70)
    )
  }

  for ((sm, bundle) <- Vec(35 -> 64, 50 -> 32, 60 -> 32))
    test(s"sm_$sm branches can target the control word preceding an instruction") {
      def branch(target: Int) = program(sm, s"BRA 0x${target.toHexString}; EXIT", base = 0)
        .map(i => i.copy(address = (if (i.address == 0) 8 else bundle + 8).unsafe))
      assert(branch(bundle).compile().isRight)
      assert(branch(bundle + 4).compile().isLeft)
    }

  for ((sm, indirect) <- Vec((35, false), (50, false), (60, false), (35, true)))
    test(s"sm_$sm ${if (indirect) "indirect" else "nested"} calls preserve per-lane return stacks") {
      val name   = if (indirect) "indirect_call" else "calls"
      val kernel = load(sm, Fixture.listing(if (indirect) s"callIndirectSm$sm" else s"callSm$sm"), name)
      val calls  = kernel.function.instructions
      if (indirect) assert(calls.exists(_.mnemonic.raw == "PRET"))
      else {
        assertEquals(calls.count(_.mnemonic.raw == "CAL"), 3)
        if (sm == 35) assert(calls.exists(i => i.address == 0xb8 && i.mnemonic.raw == "CAL"))
      }
      val expected = Vec.tabulate(64) { index =>
        val lane = index % 32
        if (!indirect) { if (lane % 2 == 0) 6 * lane + 5 else lane }
        else if (lane % 2 == 0) 3 * lane
        else 2 * lane
      }
      assertEquals(kernel.launch(32, 2)(Arg.zeros(256)).words(0).map(_.toInt), expected)
    }

  private def bmov(sm: Int): Kernel = load(sm, Fixture.listing(s"bmovClearSm$sm"), "divergent_loop")

  /** CUDA 13.3 ptxas for sm_89: the lock loop spins on `ATOMG.E.CAS` after `YIELD` and before `BSYNC B0`. */
  private def spinLock: Kernel = load(89, Fixture.listing("spinLockSm89"), "spin")

  /** The same lock from CUDA 13.3 ptxas for sm_89, recording `__activemask()` in the critical section. */
  private def spinMask: Kernel = load(89, Fixture.listing("spinMaskSm89"), "spin_mask")

  /** CUDA 13.3 ptxas for sm_89: odd lanes sleep on the divergent side of a branch before the join. */
  private def nanosleepOne: Kernel = load(89, Fixture.listing("nanosleepOneSm89"), "_Z10sleepShapeILi0EEvPjS0_")

  private def guardedYield: Kernel = load(89, Fixture.listing("guardedYieldSm89"), "_Z10yieldShapeILi0EEvPjS0_")

  private def has(instruction: DecodedInstruction, mnemonic: String, modifiers: String*): Boolean =
    instruction.mnemonic.raw == mnemonic && instruction.modifiers.map(_.name) == Vec.from(modifiers)

  private def checkClears(kernel: Kernel): Unit = {
    val instructions = kernel.function.instructions
    val clears       = instructions.filter(i => has(i, "BMOV", "32", "CLEAR") && i.operands == operands("RZ, B0"))
    val setups       = instructions.filter(i => has(i, "BSSY") && i.operands.headOption.contains(operand("B0")))
    val syncs        = instructions.filter(i => has(i, "BSYNC") && i.operands == operands("B0"))
    assertEquals((clears.size, setups.size, syncs.size), (2, 2, 2))
    assert(clears(0).address < setups(0).address)
    assert(syncs(0).address < clears(1).address)
    assert(clears(1).address < setups(1).address)
    assert(syncs(1).address < instructions.find(i => has(i, "EXIT")).get.address)
  }

  private def checkLoops(kernel: Kernel): Unit = {
    val limit = 5
    val run   = kernel.launch(128)(Arg.zeros(512), Arg.u32(limit))
    assertEquals(
      run.words(0).map(_.toInt),
      Vec.tabulate(128)(lane => if (lane % 4 < 2) limit + 2 else if (lane % 4 == 2) limit else 0)
    )
  }

  for (sm <- Vec(75, 120))
    test(s"sm_$sm EXIT exits only the guarded lanes whose predicate operand holds") {
      val kernel = load(sm, Fixture.listing(s"exitPredicateSm$sm"), "exit_unless_end")
      val last   = 5
      assertEquals(
        kernel.launch(32)(Arg.zeros(128), Arg.u32(last.toLong)).words(0).map(_.toInt),
        Vec.tabulate(32)(lane => if (lane == 0 || lane == last) lane + 1 else 0)
      )
    }

  test("compiler BMOV clears B0 before first BSSY and after BSYNC before reuse") {
    checkClears(bmov(75))
    checkLoops(bmov(75))
  }

  test("sm70 CUDA 11.8 fixture has the same inactive B0 clears and executes both loops") {
    val listing = Fixture.listing("bmovClearSm70")
    assert(listing.contains("0x0000000000ff7355"))
    assert(listing.contains("0x000fe20000100000"))
    checkClears(bmov(70))
    checkLoops(bmov(70))
  }

  test("BSSY requires a forward reconvergence target") {
    val kernel  = bmov(75)
    val setup   = kernel.function.instructions.find(i => has(i, "BSSY")).get
    val invalid = setup.rewrite(operands = setup.operands.updated(1, operand(s"0x${setup.address.raw.toHexString}")))
    val result  = kernel.map(i => if (i == setup) invalid else i).lower(8, 4)
    assert(result.left.exists(_.contains("convergence barrier target must be forward")))
  }

  test("BRA.ANY branches every active lane when one lane satisfies the guard") {
    check(
      86,
      """S2R R0, SR_TID.X
        |ISETP.EQ.U32.AND P0, PT, R0, 0, PT
        |MOV R6, 1
        |@P0 BRA.ANY 0x60
        |MOV R6, 3
        |BRA 0x70
        |MOV R6, 7
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R6
        |EXIT""".stripMargin,
      Vec(7, 7, 7, 7)
    )
  }

  for {
    lanes     <- Vec(1, 16, 32)
    divergent <- Flags
  } test(s"convergence branches exclude unlaunched lanes: count=$lanes DIV=$divergent") {
    check(
      90,
      s"""S2R R0, SR_TID.X
        |LDC.64 R10, c[0x0][0x160]
        |IMAD.WIDE.U32 R10, R0, 4, R10
        |UMOV UR4, -1
        |BRA.${if (divergent) "DIV" else "CONV"} UR4, 0x70
        |MOV R4, 1
        |BRA 0x80
        |MOV R4, 2
        |STG.E [R10], R4
        |EXIT""".stripMargin,
      Vec.tabulate(lanes)(_ => if (divergent) 1 else 2)
    )
  }

  for (setup <- Vec("BSSY", "BSSY.RECONVERGENT", "BSSY.RELIABLE"))
    test(s"$setup and BSYNC reconverge divergent shared-memory producers before consumption") {
      check(
        86,
        s"""S2R R0, SR_TID.X
          |$setup B0, 0xa0
          |ISETP.LT.U32.AND P0, PT, R0, 16, PT
          |@P0 BRA 0x70
          |MOV R4, 2
          |STS [R0.X4], R4
          |BRA 0x90
          |MOV R4, 1
          |STS [R0.X4], R4
          |${setup.replace("BSSY", "BSYNC")} B0
          |LOP3.LUT R6, R0, 16, RZ, 0x3c, !PT
          |LDS R4, [R6.X4]
          |LDC.64 R2, c[0x0][0x160]
          |IMAD.WIDE.U32 R2, R0, 4, R2
          |STG.E [R2.64], R4
          |EXIT""".stripMargin,
        Vec.fill(16)(2) ++ Vec.fill(16)(1),
        shared = 128
      )
    }

  test("BSYNC may reach a BSSY target through an unconditional branch") {
    check(
      86,
      """S2R R0, SR_TID.X
        |BSSY B0, 0xb0
        |ISETP.LT.U32.AND P0, PT, R0, 16, PT
        |@P0 BRA 0x70
        |MOV R4, 2
        |BRA 0x90
        |NOP
        |MOV R4, 1
        |NOP
        |BSYNC B0
        |BRA 0xb0
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R4
        |EXIT""".stripMargin,
      Vec.fill(16)(1) ++ Vec.fill(16)(2)
    )
  }

  test("barrier tokens survive register clearing, local spills and restoration into another slot") {
    check(
      86,
      """S2R R0, SR_TID.X
        |BSSY B0, 0x100
        |BMOV.32.CLEAR R31, B0
        |BMOV.32.CLEAR RZ, B11
        |STL [RZ], R31
        |MOV R31, 0
        |LDL R31, [RZ]
        |ISETP.LT.U32.AND P0, PT, R0, 16, PT
        |@P0 BRA 0xc0
        |MOV R4, 2
        |STS [R0.X4], R4
        |BRA 0xe0
        |MOV R4, 1
        |STS [R0.X4], R4
        |BMOV.32 B11, R31
        |BSYNC B11
        |LOP3.LUT R6, R0, 16, RZ, 0x3c, !PT
        |LDS R4, [R6.X4]
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R4
        |EXIT""".stripMargin,
      Vec.fill(16)(2) ++ Vec.fill(16)(1),
      shared = 128
    )
  }

  test("BREAK removes lanes from a pending convergence barrier") {
    check(
      86,
      """S2R R0, SR_TID.X
        |BSSY B0, 0x90
        |ISETP.LT.U32.AND P0, PT, R0, 16, PT
        |@!P0 BRA 0x70
        |BREAK B0
        |MOV R4, 1
        |BRA 0x90
        |MOV R4, 2
        |BSYNC B0
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R4
        |EXIT""".stripMargin,
      Vec.fill(16)(1) ++ Vec.fill(16)(2)
    )
  }

  test("YIELD releases the lanes an intra-warp spin lock waits for at its barrier") {
    val run = spinLock.launch(32)(Arg.zeros(4), Arg.zeros(4))
    assertEquals(run.words(1), Vec(496L))
  }

  test("lanes that yield at an intra-warp spin lock pass its BSYNC one at a time, as on sm_89") {
    val run = spinMask.launch(32)(Arg.zeros(4), Arg.zeros(4), Arg.zeros(128))
    assertEquals(run.words(1), Vec(496L))
    assertEquals(run.words(2), Vec.tabulate(32)(lane => 1L << lane))
  }

  test("lanes that sleep before a join leave the barrier at once, as on sm_89") {
    val run = nanosleepOne.launch(32)(Arg.zeros(128))
    assertEquals(run.words(0), Vec.tabulate(32)(lane => if (lane % 2 == 0) 0x55555555L else 0xaaaaaaaaL))
  }

  test("guarded YIELD drains only the lanes whose predicate holds") {
    val run = guardedYield.launch(32)(Arg.zeros(128))
    assertEquals(run.words(0), Vec.tabulate(32)(lane => if (lane % 2 == 0) 0x55555555L else 0xaaaaaaaaL))
  }

  test("YIELD in a compare-and-swap retry loop lets each lane pass the barrier once it has swapped") {
    check(
      89,
      """S2R R7, SR_TID.X
        |LDC.64 R2, c[0x0][0x160]
        |MOV R9, 0x20
        |IMAD.WIDE.U32 R2, R9, 4, R2
        |BSSY B0, `(reconverge)
        |LDG.E R4, [R2.64]
        |retry: IADD3 R6, R4, 0x1, RZ
        |YIELD
        |ATOMG.E.CAS.STRONG.GPU PT, R6, [R2], R4, R6
        |ISETP.NE.AND P0, PT, R6, R4, PT
        |IMAD.MOV.U32 R4, RZ, RZ, R6
        |@P0 BRA `(retry)
        |BSYNC B0
        |reconverge: VOTEU.ANY UR4, UPT, PT
        |UPOPC UR5, UR4
        |MOV R8, UR5
        |LDC.64 R10, c[0x0][0x160]
        |IMAD.WIDE.U32 R10, R7, 4, R10
        |STG.E [R10.64], R8
        |EXIT""".stripMargin,
      Vec.fill(32)(1) ++ Vec(32),
      block = Some(32)
    )
  }

  test("lanes of a warp that win a contended compare and swap loop in turn pass its barrier alone") {
    val kernel = program(
      89,
      """S2R R0, SR_TID.X
        |LDC.64 R10, c[0x0][0x160]
        |LDC.64 R6, c[0x0][0x168]
        |IMAD.WIDE.U32 R12, R0, 4, R10
        |LDG.E R4, [R6.64]
        |BSSY B0, `(after)
        |loop: IADD3 R8, R4, 0x2, RZ
        |YIELD
        |ATOMG.E.CAS.STRONG.GPU PT, R8, [R6.64], R4, R8
        |ISETP.NE.AND P0, PT, R8, R4, PT
        |IMAD.MOV.U32 R4, RZ, RZ, R8
        |@P0 BRA `(loop)
        |BSYNC B0
        |after: IADD3 R2, R0, 0x1, RZ
        |REDUX.SUM UR5, R2
        |IMAD.U32 R3, RZ, RZ, UR5
        |STG.E [R12.64], R3
        |EXIT""".stripMargin
    )
    val run = kernel.launch(128)(Arg.zeros(512), Arg.zeros(4))
    assertEquals(run.words(1), Vec(256L))
    assertEquals(run.words(0), Vec.tabulate(128)(lane => lane + 1L))
  }

  test("integer atomics and CAS loops count each lane once per block and per grid") {
    val name   = "_ZN40_GLOBAL__N__abd92d82_11_integers_cu_main8integersEPjPyS0_S1_S0_"
    val kernel = load(86, Fixture.listing("integersSm86"), name, shared = 256)
    val run    = kernel.launch(block = 128, grid = 4)(
      Arg.zeros(28 * 4),
      Arg.zeros(20 * 8),
      Arg.zeros(4 * 14 * 4),
      Arg.zeros(4 * 10 * 8),
      Arg.zeros(512 * 4 * 4)
    )
    val global = run.words(0)
    val blocks = run.words(2).grouped(14).toVec
    assertEquals(global(0), 131328L)
    assertEquals(global(12), 1024L)
    assertEquals(global(13), 512L)
    assertEquals(blocks.map(_(0)), Vec.fill(4)(8256L))
    assertEquals(blocks.map(_(12)), Vec.fill(4)(256L))
    assertEquals(blocks.map(_(13)), Vec.fill(4)(128L))
  }

  test("a ballot gathers its members when lanes leave an earlier YIELD loop at different times") {
    val name   = "_ZN48_GLOBAL__N__137ec495_10_control_cu_07573323_28358lookbackILNS_5ShapeE4EEEvPKjPj"
    val kernel = load(89, Fixture.listing("lookbackRepeatedSm89"), name)
    val blocks = 8
    val flag   = 0x40000000L
    val table  = Vec.tabulate(blocks * 32) { i =>
      val (block, lane) = (i / 32, i % 32)
      (if (block == lane % 9) flag else 0L) | (lane + block + 1).toLong
    }
    val stops = Vec.tabulate(32)(lane => if (lane % 9 < blocks) lane % 9 else 0)
    val sums  = Vec.tabulate(32)(lane => (stops(lane) until blocks).map(block => (lane + block + 1).toLong).sum)
    val masks = Vec.tabulate(32) { lane =>
      (0 until 32).filter(other => other % 9 < stops(lane) || other % 9 == blocks).map(1L << _).sum
    }
    assertEquals(kernel.launch(32)(Arg.words(table*), Arg.zeros(64 * 4)).words(1), sums ++ masks)
  }

  test("local subroutine returns use the saved PC and preserve caller registers") {
    check(
      86,
      """MOV R5, 1
        |LEPC R16
        |CALL.REL.NOINC 0x70
        |IADD3 R5, R5, 2, RZ
        |LDC.64 R2, c[0x0][0x160]
        |STG.E [R2.64], R5
        |EXIT
        |IADD3 R5, R5, 4, RZ
        |RET.ABS R16, 0x20""".stripMargin,
      Vec(7)
    )
  }

  test("JMP and JMX branch to absolute code addresses") {
    check(
      86,
      "MOV R4, 1; JMP 0x30; MOV R4, 2; MOV R0, 0x20; JMX R0, 0x40; MOV R4, 3; LDC.64 R2, c[0x0][0x160]; STG.E [R2.64], R4; EXIT",
      Vec(1)
    )
  }

  test("absolute calls reach code addresses through an immediate or a register") {
    check(
      86,
      """MOV R5, 1
        |LEPC R16
        |CALL.ABS.NOINC 0x90
        |MOV R6, 0x90
        |LEPC R16
        |CALL.ABS R6
        |LDC.64 R2, c[0x0][0x160]
        |STG.E [R2.64], R5
        |EXIT
        |IADD3 R5, R5, 4, RZ
        |RET.ABS R16, 0x20""".stripMargin,
      Vec(9)
    )
  }

  test("WARPSYNC synchronises each warp on its own member mask") {
    check(
      89,
      """S2R R0, SR_TID.X
        |ISETP.GE.U32.AND P0, PT, R0, 48, PT
        |@P0 EXIT
        |ISETP.LT.U32.AND P1, PT, R0, 32, PT
        |MOV R9, 0xffff
        |@P1 MOV R9, 0xffffffff
        |WARPSYNC R9
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R9
        |EXIT""".stripMargin,
      Vec.tabulate(64)(lane => if (lane < 32) -1 else if (lane < 48) 0xffff else 0)
    )
  }

  test("WARPSYNC.EXCLUSIVE synchronises each warp on its member mask") {
    check(
      89,
      """S2R R0, SR_TID.X
        |ISETP.GE.U32.AND P0, PT, R0, 48, PT
        |@P0 EXIT
        |ISETP.LT.U32.AND P1, PT, R0, 32, PT
        |MOV R9, 0xffff
        |@P1 MOV R9, 0xffffffff
        |WARPSYNC.EXCLUSIVE R9
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R9
        |EXIT""".stripMargin,
      Vec.tabulate(64)(lane => if (lane < 32) -1 else if (lane < 48) 0xffff else 0)
    )
  }

  test("WARPSYNC.EXCLUSIVE synchronises each member group of a warp on its own mask") {
    check(
      89,
      """S2R R0, SR_TID.X
        |ISETP.LT.U32.AND P1, PT, R0, 16, PT
        |MOV R9, 0xffff0000
        |@P1 MOV R9, 0xffff
        |WARPSYNC.EXCLUSIVE R9
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R9
        |EXIT""".stripMargin,
      Vec.tabulate(32)(lane => if (lane < 16) 0xffff else 0xffff0000)
    )
  }

  test("a diverged path runs ahead, so another path cannot rewrite a uniform register it is about to read") {
    check(
      89,
      """S2R R0, SR_TID.X
        |ISETP.GE.U32.AND P0, PT, R0, 16, PT
        |@P0 BRA 0x80
        |UMOV UR4, 0x1
        |NOP
        |NOP
        |MOV R5, UR4
        |BRA 0xc0
        |UMOV UR4, 0x2
        |NOP
        |NOP
        |MOV R5, UR4
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R5
        |EXIT""".stripMargin,
      Vec.tabulate(32)(lane => if (lane < 16) 1 else 2)
    )
  }

  test("a failed __assertfail fails the launch") {
    val failed = program(89, "MOV R2, 0x40; MOV R3, 0xffffff00; CALL.ABS.NOINC R2; EXIT").run(1)(Arg.zeros(4))
    assert(failed.left.exists { case Fault.Assertion(_) => true; case _ => false }, failed)
  }

  test("a register call lowers to as many statements in a large kernel as in a small one") {
    def statements(calls: Int, filler: Int): Int =
      program(
        89,
        (("MOV R11, RZ" +: Vec.fill(calls)("CALL.ABS.NOINC R2")) ++ Vec.fill(filler)("NOP") :+ "EXIT").mkString("; ")
      )
        .lower().value.size
    val perCall = statements(calls = 4, filler = 0) - statements(calls = 0, filler = 0)
    assertEquals(statements(calls = 4, filler = 64) - statements(calls = 0, filler = 64), perCall)
  }

  test("a failed __assertfail names the expression, location and thread") {
    val failed = program(
      89,
      "MOV R20, 0x00793d78; STS [RZ], R20; MOV R4, c[0x0][0x18]; MOV R5, c[0x0][0x1c]; MOV R6, R4; MOV R7, R5; " +
        "MOV R8, 0x24; MOV R10, R4; MOV R11, R5; MOV R2, 0x40; MOV R3, 0xffffff00; CALL.ABS.NOINC R2; EXIT",
      shared = 16
    ).run(1)(Arg.zeros(4))
    val detail = failed match {
      case Left(Fault.Assertion(detail)) => detail.raw
      case other                         => fail(other.toString)
    }
    assert(detail.endsWith("x=y:36: x=y: block: [0,0,0], thread: [0,0,0] Assertion `x=y` failed."), detail)
  }

  test("LEPC and ULEPC write the instruction's 64-bit address") {
    check(
      90,
      "MOV R17, 7; LEPC R16; ULEPC UR4; MOV R18, UR4; MOV R19, UR5; LDC.64 R2, c[0x0][0x160]; STG.E.128 [R2.64], R16; EXIT",
      Vec(0x10, 0, 0x20, 0)
    )
  }

  for (sm <- Vec(35, 50, 60)) {
    test(s"sm_$sm JCAL calls a subroutine that returns with RET") {
      check(
        sm,
        """MOV32I R4, 0x1
          |JCAL 0x60
          |IADD32I R4, R4, 0x2
          |LDC.64 R2, c[0x0][0x160]
          |STG.E [R2], R4
          |EXIT
          |IADD32I R4, R4, 0x4
          |RET""".stripMargin,
        Vec(7)
      )
    }

    test(s"sm_$sm CONT leaves an iteration for the PCNT target, where the lanes reconverge") {
      check(
        sm,
        """S2R R0, SR_TID.X
          |MOV R4, RZ
          |MOV R5, RZ
          |PCNT 0x80
          |ISETP.LT.U32.AND P0, PT, R0, 16, PT
          |@P0 CONT
          |IADD R4, R4, R5
          |CONT
          |IADD32I R5, R5, 0x1
          |ISETP.LT.U32.AND P1, PT, R5, 4, PT
          |@P1 BRA 0x30
          |LDC.64 R2, c[0x0][0x160]
          |ISCADD R2.CC, R0, R2, 2
          |IADD.X R3, RZ, R3
          |STG.E [R2], R4
          |EXIT""".stripMargin,
        Vec.tabulate(32)(lane => if (lane < 16) 0 else 6)
      )
    }

    test(s"sm_$sm lanes parked at a reconvergence point vote with the lane whose polling loop runs on") {
      check(
        sm,
        """S2R R0, SR_TID.X
          |LDC.64 R2, c[0x0][0x160]
          |IMAD.WIDE.U32 R2, R0, 4, R2
          |ISETP.EQ.U32.AND P3, PT, R0, RZ, PT
          |ISETP.NE.U32.AND P0, PT, R0, RZ, PT
          |MOV R5, RZ
          |SSY `(done)
          |@P0 NOP.S
          |loop: LDG.E R4, [R2.64]
          |IADD32I R5, R5, 0x1
          |ISETP.LT.U32.AND P1, PT, R5, 0x4, PT
          |@P1 BRA `(loop)
          |NOP.S
          |done: VOTE.ANY R6, PT, P3
          |STG.E [R2.64], R6
          |EXIT""".stripMargin,
        Vec.fill(32)(1)
      )
    }
  }

  for (sm <- Vec(50, 60)) test(s"sm_$sm a look-back vote covers the lanes still polling when other lanes have left") {
    val predecessors = 8
    val flag         = 0x40000000L
    val table        = Vec.tabulate(predecessors * 32) { index =>
      val (block, lane) = (index / 32, index % 32)
      (if (block == lane % 9) flag else 0L) | (lane + block + 1)
    }
    val expected = Vec.tabulate(64) { index =>
      val lane = index % 32
      val stop = if (lane % 9 < predecessors) lane % 9 else 0
      if (index < 32) (stop until predecessors).map(block => (lane + block + 1).toLong).sum
      else (0 until 32).filter(other => other % 9 < stop || other % 9 == predecessors).map(1L << _).sum
    }
    val run = program(
      sm,
      """MOV32I R10, 0x40000000
        |MOV R1, c[0x0][0x20]
        |MOV32I R0, 0x8
        |S2R R6, SR_TID.X
        |SSY 0x160
        |ISETP.NE.AND P0, PT, R0, RZ, PT
        |@!P0 SYNC
        |IADD32I R3, R0, -0x1
        |SSY 0x120
        |ISCADD R0.CC, R3, R6, 0x5
        |IADD.X R2, RZ, RZ
        |LEA R4.CC, R0.reuse, c[0x0][0x160], 0x2
        |LEA.HI.X R5, R0, c[0x0][0x164], R2, 0x2
        |MEMBAR.CTA
        |LDG.E.CV R0, [R4]
        |ISETP.NE.AND P0, PT, R0, RZ, PT
        |@!P0 BRA 0xd0
        |SYNC
        |LOP.AND.NZ P0, RZ, R0, R10
        |MOV R0, R3
        |@!P0 BRA 0x50
        |SYNC
        |MOV32I R0, 0x8
        |SSY 0x2f0
        |MOV R4, RZ
        |MOV32I R5, 0xffffffff
        |ISETP.NE.AND P0, PT, R0, RZ, PT
        |@!P0 SYNC
        |IADD32I R7, R0, -0x1
        |SSY 0x270
        |ISCADD R0.CC, R7, R6, 0x5
        |IADD.X R2, RZ, RZ
        |LEA R8.CC, R0.reuse, c[0x0][0x160], 0x2
        |LEA.HI.X R9, R0, c[0x0][0x164], R2, 0x2
        |MEMBAR.CTA
        |LDG.E.CV R0, [R8]
        |ISETP.NE.AND P0, PT, R0, RZ, PT
        |@!P0 BRA 0x220
        |SYNC
        |LOP.AND.NZ P0, RZ, R0.reuse, R10
        |LOP32I.AND R2, R0, 0x3fffffff
        |MOV R0, R7
        |IADD R4, R4, R2
        |VOTE.ANY R3, PT, !P0
        |LOP.AND R5, R3, R5
        |@!P0 BRA 0x1a0
        |SYNC
        |LEA R2.CC, R6.reuse, c[0x0][0x168], 0x2
        |LEA.HI.X R3, R6, c[0x0][0x16c], RZ, 0x2
        |STG.E [R2], R4
        |STG.E [R2+0x80], R5
        |EXIT""".stripMargin
    ).launch(block = 32)(Arg.Buffer(Harness.words(table)), Arg.zeros(256))
    assertEquals(run.words(1), expected)
  }

  for (sm <- Vec(50, 60)) test(s"sm_$sm lanes polling a flag stay in the convergence barrier of the loop") {
    val flags = Vec.tabulate(32)(lane => if (lane < 16) 1L else 0L)
    val run   = program(
      sm,
      """S2R R6, SR_TID.X
        |LEA R4.CC, R6, c[0x0][0x160], 0x2
        |LEA.HI.X R5, R6, c[0x0][0x164], RZ, 0x2
        |LEA R8.CC, R6, c[0x0][0x168], 0x2
        |LEA.HI.X R9, R6, c[0x0][0x16c], RZ, 0x2
        |ISETP.GE.U32.AND P0, PT, R6, 0x20, PT
        |@P0 BRA `(produce)
        |SSY `(done)
        |spin: LDG.E.CV R0, [R4]
        |ISETP.EQ.U32.AND P1, PT, R0, RZ, PT
        |@P1 BRA `(spin)
        |NOP.S
        |done: VOTE.ANY R7, PT, PT
        |STG.E [R8], R7
        |EXIT
        |produce: IADD32I R10, R6, -0x20
        |LEA R2.CC, R10, c[0x0][0x160], 0x2
        |LEA.HI.X R3, R10, c[0x0][0x164], RZ, 0x2
        |MOV32I R1, 0x3e8
        |work: IADD32I R1, R1, -0x1
        |ISETP.NE.AND P1, PT, R1, RZ, PT
        |@P1 BRA `(work)
        |MOV32I R0, 0x1
        |STG.E [R2], R0
        |EXIT""".stripMargin
    ).launch(block = 64)(Arg.Buffer(Harness.words(flags)), Arg.zeros(256))
    assertEquals(run.words(1).take(32), Vec.fill(32)(0xffffffffL))
  }

  test("BRX adds its register and displacement to the following instruction address") {
    check(
      86,
      "MOV R0, 0x30; BRX R0, -0x20; EXIT; MOV R4, 19; LDC.64 R2, c[0x0][0x160]; STG.E [R2.64], R4; EXIT",
      Vec(19)
    )
  }

  test("uniform indirect branches use the next instruction as their relative base") {
    check(
      120,
      "LDC.64 R10, c[0x0][0x160]; UMOV UR4, 0x50; BRXU UR4, -0x30; MOV R4, 1; BRA 0x60; MOV R4, 2; STG.E [R10], R4; EXIT",
      Vec(2)
    )
  }

  test("legacy entry RET followed by EXIT completes without a device-call token") {
    check(
      35,
      "S2R R0, SR_TID.X; LDC.64 R2, c[0x0][0x160]; IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2.64], R0; RET; EXIT",
      Vec.tabulate(32)(identity)
    )
  }

  test("legacy SSY and predicated NOP.S rendezvous at the forward target") {
    check(
      35,
      """S2R R0, SR_TID.X
        |MOV R4, 1
        |SSY 0x70
        |ISETP.LT.U32.AND P0, PT, R0, 16, PT
        |@P0 NOP.S
        |MOV R4, 2
        |NOP.S
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R4
        |EXIT""".stripMargin,
      Vec.fill(16)(1) ++ Vec.fill(16)(2)
    )
  }

  test("legacy stores with .S execute before reconverging at the SSY target") {
    check(
      35,
      """S2R R0, SR_TID.X
        |MOV R4, 1
        |SSY 0x80
        |ISETP.LT.U32.AND P0, PT, R0, 16, PT
        |@P0 STS.S [R0.X4], R4
        |MOV R4, 2
        |STS.S [R0.X4], R4
        |NOP
        |LOP3.LUT R6, R0, 16, RZ, 0x3c, !PT
        |LDS R4, [R6.X4]
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R4
        |EXIT""".stripMargin,
      Vec.fill(16)(2) ++ Vec.fill(16)(1),
      shared = 128
    )
  }

  test("skipping a legacy SSY does not revive the previous token in its reused slot") {
    check(
      35,
      """S2R R0, SR_TID.X
        |SSY 0xb0
        |SSY 0x40
        |NOP.S
        |ISETP.NE.AND P0, PT, R0, RZ, PT
        |@P0 NOP.S
        |BRA 0x90
        |SSY 0x90
        |NOP.S
        |MOV R4, 7
        |NOP.S
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |@P0 MOV R4, 1
        |STG.E [R2.64], R4
        |EXIT""".stripMargin,
      Vec(7) ++ Vec.fill(31)(1)
    )
  }

  test("legacy BRK removes bypassed nested SSY lanes before the PBK target") {
    check(
      35,
      """S2R R0, SR_TID.X
        |MOV R4, 1
        |PBK 0xc0
        |SSY 0x90
        |ISETP.LT.U32.AND P0, PT, R0, 16, PT
        |@P0 BRK
        |BRA 0x90
        |NOP
        |NOP
        |MOV R4, 2
        |BRA 0xc0
        |NOP
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R4
        |EXIT""".stripMargin,
      Vec.fill(16)(1) ++ Vec.fill(16)(2)
    )
  }

  test("a diverged path runs on until it yields, so a plain read-modify-write on shared memory stays whole") {
    check(
      70,
      """S2R R0, SR_TID.X
        |ISETP.NE.AND P0, PT, R0, RZ, PT
        |BSSY B0, 0xd0
        |@P0 BRA 0x80
        |LDS R4, [RZ]
        |IADD3 R4, R4, 1, RZ
        |STS [RZ], R4
        |BRA 0xc0
        |LDS R4, [RZ]
        |IADD3 R4, R4, 1, RZ
        |STS [RZ], R4
        |NOP
        |BSYNC B0
        |YIELD
        |BAR.SYNC 0
        |LDS R5, [RZ]
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R5
        |EXIT""".stripMargin,
      Vec(2, 2),
      shared = 4
    )
  }
}
