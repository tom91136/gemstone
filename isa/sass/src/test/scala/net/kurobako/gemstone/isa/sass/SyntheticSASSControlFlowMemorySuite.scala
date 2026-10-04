package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.vm.*

class SyntheticSASSControlFlowMemorySuite extends SyntheticSASSExecutionSuiteBase {
  test("shared-window high register constructs a generic alias of explicit shared storage") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("MOV", "R2,16", None),
        ("IMAD", "R2,R0,4,R2", None),
        ("S2R", "R3,SR_SWINHI", None),
        ("MOV", "R4,42", None),
        ("ST.E", "[R2.64],R4", None),
        ("LDS", "R5,[R2]", None),
        ("MOV", "R4,99", None),
        ("STS", "[R2],R4", None),
        ("LD.E", "R6,[R2.64]", None),
        ("IADD3", "R4,R5,R6,RZ", None),
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R10,R0,4,R10", None),
        ("STG.E", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(141, 141, 141, 141),
      shared = 32,
      sm = 100
    )
  }

  test("single-CTA cluster cancellation completes a failed response transaction") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("ISETP.EQ.U32.AND", "P0,PT,R0,RZ,PT", None),
        ("UMOV", "UR4,0x1ffffe", None),
        ("UMOV", "UR5,0x7ffff800", None),
        ("SYNCS.EXCH.64", "URZ,[0],UR4", Some("@P0")),
        ("UCGABAR_ARV", "", None),
        ("UCGABAR_WAIT", "", None),
        ("UMOV", "UR4,16", None),
        ("UMOV", "UR5,0", None),
        ("UGETNEXTWORKID.SELFCAST", "[UR4],[UR5]", None),
        ("MOV", "R4,16", None),
        ("SYNCS.ARRIVE.TRANS64", "RZ,[0],R4", Some("@P0")),
        ("BAR.SYNC", "0", None),
        ("SYNCS.PHASECHK.TRANS64.TRYWAIT", "P1,[0],RZ", None),
        ("MOV", "R4,1", None),
        ("SEL", "R4,R4,RZ,P1", None),
        ("LDS", "R5,[24]", None),
        ("ISETP.EQ.U32.AND", "P1,PT,R5,RZ,PT", None),
        ("SEL", "R4,R4,RZ,P1", None),
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R10,R0,4,R10", None),
        ("STG.E", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(1, 1, 1, 1),
      shared = 32,
      sm = 100
    )
  }

  test("indexed constant loads read scalar and relocated pointer launch arguments") {
    executeSynthetic(
      Vec.nonEmpty(
        ("MOV", "R0,20", None),
        ("LDC", "R4,c[0x0][R0+0x160]", None),
        ("MOV", "R2,0", None),
        ("LDC.64", "R2,c[0x0][R2+0x160]", None),
        ("S2R", "R0,SR_TID.X", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(4, 4, 4, 4)
    )
  }

  test("local stack bounds permit a frame and preserve its spilled value") {
    executeSynthetic(
      Vec.nonEmpty(
        ("MOV", "R1,c[0x0][0x28]", None),
        ("IADD3", "R1,R1,-0x20,RZ", None),
        ("S2R", "R0,SR_LMEMHIOFF", None),
        ("ISETP.GE.U32.AND", "P0,PT,R1,R0,PT", None),
        ("BRA", "0x60", Some("@P0")),
        ("BPT.TRAP", "1", None),
        ("MOV", "R4,42", None),
        ("STL", "[R1+8],R4", None),
        ("LDL", "R5,[R1+8]", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R5", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(42)
    )
  }

  test("local stack bounds reject an offset inside the shared-memory aperture") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_LMEMHIOFF", None),
        ("MOV", "R1,0x3ffffff0", None),
        ("ISETP.GE.U32.AND", "P0,PT,R1,R0,PT", None),
        ("MOV", "R4,1", None),
        ("SEL", "R4,R4,RZ,P0", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(0)
    )
  }

  test("BRA.ANY branches every active lane when one lane satisfies the guard") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("ISETP.EQ.U32.AND", "P0,PT,R0,0,PT", None),
        ("MOV", "R6,1", None),
        ("BRA.ANY", "0x60", Some("@P0")),
        ("MOV", "R6,3", None),
        ("BRA", "0x70", None),
        ("MOV", "R6,7", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R6", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(7, 7, 7, 7)
    )
  }

  test("leader election selects one active lane and global reduction preserves its update") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("MOV", "R4,0", None),
        ("STG.E", "[R10],R4", None),
        ("ELECT", "P0,URZ,PT", None),
        ("MOV", "R4,1", None),
        ("REDG.E.ADD", "[R10.64],R4", Some("@P0")),
        ("CGAERRBAR", "", None),
        ("PREEXIT", "", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(1, 0, 0, 0, 0),
      sm = 90
    )
  }

  test("Blackwell signed 64-bit comparisons and selects preserve extended uniform registers") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("UMOV", "UR64,0", None),
        ("UMOV", "UR65,0x80000000", None),
        ("UISETP.LT.S64.AND", "UP0,UPT,UR64,0,UPT", None),
        ("USEL.64", "UR66,UR64,URZ,UP0", None),
        ("MOV.64", "R4,UR66", None),
        ("SEL.64", "R4,R4,RZ,PT", None),
        ("STG.E.64", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(0, Int.MinValue),
      sm = 120
    )
  }

  test("Blackwell launch dimensions retain the ABI padding before grid dimensions") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("LDC", "R4,c[0x0][0x360]", None),
        ("LDC", "R5,c[0x0][0x364]", None),
        ("LDC", "R6,c[0x0][0x368]", None),
        ("LDC", "R7,c[0x0][0x370]", None),
        ("LDC", "R8,c[0x0][0x374]", None),
        ("LDC", "R9,c[0x0][0x378]", None),
        ("STG.E.128", "[R10],R4", None),
        ("STG.E.64", "[R10+16],R8", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(6, 1, 1, 2, 3, 4),
      sm = 120,
      grid = LaunchGeometry.Dimensions(2, 3, 4)
    )
  }

  test("shared memory barrier arrivals advance parity and reset their count across phases") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R10,R0,4,R10", None),
        ("ISETP.EQ.U32.AND", "P0,PT,R0,RZ,PT", None),
        ("UMOV", "UR4,0x1ffffc", None),
        ("UMOV", "UR5,0x7ffff000", None),
        ("SYNCS.EXCH.64", "URZ,[0],UR4", Some("@P0")),
        ("BAR.SYNC", "0", None),
        ("SYNCS.PHASECHK.TRANS64.TRYWAIT", "P1,[0],RZ", None),
        ("SEL", "R4,RZ,1,P1", None),
        ("SYNCS.ARRIVE.TRANS64", "RZ,[0],RZ", None),
        ("SYNCS.PHASECHK.TRANS64.TRYWAIT", "P1,[0],RZ", None),
        ("SEL", "R4,R4,RZ,P1", None),
        ("MOV", "R5,0x80000000", None),
        ("SYNCS.PHASECHK.TRANS64.TRYWAIT", "P1,[0],R5", None),
        ("SEL", "R4,RZ,R4,P1", None),
        ("SYNCS.ARRIVE.TRANS64", "RZ,[0],RZ", None),
        ("SYNCS.PHASECHK.TRANS64.TRYWAIT", "P1,[0],R5", None),
        ("SEL", "R4,R4,RZ,P1", None),
        ("SYNCS.CCTL.IV", "[0]", Some("@P0")),
        ("STG.E", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(1, 1),
      shared = 8,
      sm = 90
    )
  }

  test("uniform indirect branches use the next instruction as their relative base") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("UMOV", "UR4,0x50", None),
        ("BRXU", "UR4,-0x30", None),
        ("MOV", "R4,1", None),
        ("BRA", "0x60", None),
        ("MOV", "R4,2", None),
        ("STG.E", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(2),
      sm = 120
    )
  }

  test("uniform bulk copies transfer each byte once and complete the destination barrier") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("LDCU.64", "UR6,c[0x0][0x160]", None),
        ("MOV.64", "R10,UR6", None),
        ("IMAD.WIDE.U32", "R10,R0,4,R10", None),
        ("IADD3", "R4,R0,101,RZ", None),
        ("STG.E", "[R10],R4", None),
        ("ISETP.EQ.U32.AND", "P0,PT,R0,RZ,PT", None),
        ("UMOV", "UR8,0x1ffffe", None),
        ("UMOV", "UR9,0x7ffff800", None),
        ("SYNCS.EXCH.64", "URZ,[16],UR8", Some("@P0")),
        ("BAR.SYNC", "0", None),
        ("UMOV", "UR4,0", None),
        ("UMOV", "UR5,16", None),
        ("UMOV", "UR10,1", None),
        ("UBLKCP.S.G", "[UR4],[UR6],UR10", None),
        ("MOV", "R4,16", None),
        ("SYNCS.ARRIVE.TRANS64", "RZ,[16],R4", Some("@P0")),
        ("BAR.SYNC", "0", None),
        ("SYNCS.PHASECHK.TRANS64.TRYWAIT", "P1,[16],RZ", None),
        ("IMAD.SHL.U32", "R0,R0,4,RZ", None),
        ("LDS", "R4,[R0]", None),
        ("SEL", "R4,R4,RZ,P1", None),
        ("STG.E", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(101, 102, 103, 104),
      shared = 24,
      sm = 120
    )
  }

  test("wide uniform constants retain all four parameter words") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDCU.128", "UR4,c[0x0][0x160]", None),
        ("MOV.64", "R10,UR4", None),
        ("MOV", "R4,UR4", None),
        ("MOV", "R5,UR5", None),
        ("LOP3.LUT", "R4,R4,UR6,RZ,0x3c,!PT", None),
        ("LOP3.LUT", "R5,R5,UR7,RZ,0x3c,!PT", None),
        ("STG.E.64", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(0, 0),
      sm = 120
    )
  }

  test("shared 64-bit stores add uniform address offsets before storing both words") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("MOV", "R0,4", None),
        ("UMOV", "UR4,4", None),
        ("MOV", "R4,123", None),
        ("MOV", "R5,456", None),
        ("STS.64", "[R0+UR4],R4", None),
        ("ACQBULK", "", None),
        ("ACQSHMINIT", "", None),
        ("FENCE.VIEW.ASYNC.S", "", None),
        ("LDS.64", "R6,[8]", None),
        ("STG.E.64", "[R10],R6", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(123, 456),
      shared = 16,
      sm = 90
    )
  }

  for {
    lanes     <- Vec.nonEmpty[Int > 0](1, 16, 32)
    divergent <- Vec.nonEmpty(false, true)
  } test(s"convergence branches exclude unlaunched lanes: count=$lanes DIV=$divergent") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R10,R0,4,R10", None),
        ("UMOV", "UR4,-1", None),
        (if (divergent) "BRA.DIV" else "BRA.CONV", "UR4,0x70", None),
        ("MOV", "R4,1", None),
        ("BRA", "0x80", None),
        ("MOV", "R4,2", None),
        ("STG.E", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.tabulateNonEmpty(lanes)(_ => if (divergent) 1 else 2),
      sm = 90
    )
  }

  test("collective ALL pairs its target with ENDCOLLECTIVE and includes every lane in the vote") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R10,R0,4,R10", None),
        ("MOV", "R4,42", None),
        ("ISETP.EQ.U32.AND", "P0,PT,R0,31,PT", None),
        ("WARPSYNC.COLLECTIVE.ALL", "0x90", None),
        ("VOTE.ANY", "P1,P0", None),
        ("SEL", "R4,R4,RZ,P1", None),
        ("ENDCOLLECTIVE", "", None),
        ("STG.E", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.tabulateNonEmpty(32)(_ => 42),
      sm = 100
    )
  }

  test("collective warp synchronization excludes nonmembers from the vote and rejoins them") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R10,R0,4,R10", None),
        ("MOV", "R4,-1", None),
        ("MOV", "R5,0xffff", None),
        ("ISETP.GE.U32.AND", "P0,PT,R0,16,PT", None),
        ("BSSY", "B0,0xc0", None),
        ("WARPSYNC.COLLECTIVE", "R5,0xb0", None),
        ("VOTE.ANY", "P1,P0", None),
        ("SEL", "R4,R4,RZ,P1", None),
        ("ENDCOLLECTIVE", "", None),
        ("BSYNC", "B0", None),
        ("STG.E", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.tabulateNonEmpty(16)(_ => 0).concatNonEmpty(Vec.tabulateNonEmpty(16)(_ => -1)),
      sm = 90
    )
  }

  for (sm <- Vec.nonEmpty(50, 60))
    test(s"sm_$sm complementary guarded address producers preserve separate lane values") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("LDC.64", "R10,c[0x0][0x160]", None),
          ("ISCADD", "R10.CC,R0,R10,2", None),
          ("IADD.X", "R11,R11,RZ", None),
          ("IADD", "R4,R0,10", None),
          ("STG.E", "[R10],R4", None),
          ("ISETP.LT.U32.AND", "P0,PT,R0,2,PT", None),
          ("LEA", "R12.CC,R10,RZ", Some("@P0")),
          ("LEA.HI.X", "P1,R13,R10,RZ,R11", Some("@P0")),
          ("LEA", "R14.CC,R10,RZ", Some("@!P0")),
          ("LD.E.CG", "R4,[R12],P1", Some("@P0")),
          ("LEA.HI.X", "P1,R15,R10,RZ,R11", Some("@!P0")),
          ("LD.E.CG", "R4,[R14],P1", Some("@!P0")),
          ("ISETP.EQ.AND", "P0,PT,RZ,RZ,PT", None),
          ("ISETP.EQ.AND", "P1,PT,RZ,RZ,PT", None),
          ("IADD", "R4,R4,1", None),
          ("STG.E", "[R10],R4", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(11, 12, 13, 14),
        sm = sm
      )
    }

  for (sm <- Vec.nonEmpty(50, 60))
    test(s"sm_$sm generic loads retain independent address arithmetic and the decoded LU cache alias") {
      executeSynthetic(
        Vec.nonEmpty(
          ("LDC.64", "R10,c[0x0][0x160]", None),
          ("MOV", "R6,40", None),
          ("STG.E", "[R10],R6", None),
          ("LEA", "R12.CC,R10,RZ", None),
          ("MOV", "R11,R11", None),
          ("XMAD", "R7,RZ,RZ,RZ", None),
          ("FSETP.EQ.AND", "P2,PT,RZ,RZ,PT", None),
          ("LEA.HI.X", "P1,R13,R10,RZ,R11", None),
          ("ISETP.EQ.AND", "P2,PT,RZ,RZ,PT", None),
          ("ISCADD", "R4.CC,RZ,2,2", None),
          ("IADD.X", "R5,RZ,RZ", None),
          ("LEA", "R14.CC,R10,RZ", None),
          ("LEA.HI.X", "P2,R15,R10,RZ,R11", None),
          ("LD.E.LU", "R6,[R12],P1", None),
          ("LD.E.LU", "R7,[R14],P2", None),
          ("ISETP.EQ.AND", "P2,PT,RZ,RZ,PT", None),
          ("LOP.AND.NZ", "P1,RZ,R6,42", None),
          ("SEL", "R6,R6,RZ,P1", None),
          ("IADD", "R6,R6,R4", None),
          ("STG.E", "[R10],R6", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(42),
        sm = sm
      )
    }

  test("ordinary Hopper blocks have cluster CTA rank zero and clustered kernels reject execution") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("S2UR", "UR4,SR_CgaCtaId", None),
        ("WARPSYNC.ALL", "", None),
        ("MOV", "R4,UR4", None),
        ("STG.E", "[R2],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(0),
      sm = 90
    )
    val module   = CubinSuiteFixtures.module.copy(sm = 90)
    val kernel   = module.kernels.head
    val function =
      Vec.nonEmpty(DecodedFunction(kernel.name, Vec.nonEmpty(SASSFixture.instruction(0, "EXIT", Vec.empty, None))))
    Vec.nonEmpty(
      kernel.copy(requiredCluster = LaunchGeometry.Dimensions(2, 1, 1)),
      kernel.copy(explicitCluster = true)
    ).foreach(value => assert(SASSFixture.lower(module, value, function).left.exists(_.contains("cluster"))))
  }

  test("CUDA byte loads read one unsigned byte from shared memory") {
    for (sm <- Vec.nonEmpty(50, 60))
      executeSynthetic(
        Vec.nonEmpty(
          ("MOV", "R4,0x123456fe", None),
          ("STS", "[0x18],R4", None),
          ("LDS.U.U8", "R8,[0x18]", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("STG.E", "[R2],R8", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(254),
        shared = 28,
        sm = sm,
        rewrite = instruction =>
          instruction.operation match {
            case _: Instruction.Lds =>
              // nvdisasm prints LDS.U.U8; the recovered SIZE vocabulary spells unsigned bytes as 8.
              val bytes = LittleEndian.toVec(0xef4810000187ff08L, 8).toArray ++ Array[Byte](0xfd.toByte, 0x07, 0x00)
              InstructionDecoder.decodeTyped(instruction.address, bytes, s"SM$sm").fold(fail(_), identity)
            case _ => instruction
          }
      )
  }

  test("CUDA generic loads accept the generated alias for GPU scope and uniform addresses") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R4,c[0x0][0x160]", None),
        ("MOV", "R6,42", None),
        ("STG.E", "[R4],R6", None),
        ("ULDC.64", "UR4,c[0x0][0x160]", None),
        ("LD.E.STRONG.GPU", "R2,[UR4]", None),
        ("MOV", "R3,7", None),
        ("RED.E.MIN.STRONG.VC", "[UR4],R3", None),
        ("LD.E.STRONG.GPU", "R2,[UR4]", None),
        ("STG.E", "[R4],R2", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(7),
      sm = 75,
      rewrite = instruction =>
        instruction.operation match {
          case _: Instruction.Ld =>
            // nvdisasm prints GPU; the generated SCO vocabulary also calls this scope VC.
            val bytes = LittleEndian.toVec(0x00000004ff027980L, 8).toArray ++
              LittleEndian.toVec(0x000ea8000c114900L, 8).toArray
            InstructionDecoder.decodeTyped(instruction.address, bytes, "SM75").fold(fail(_), identity)
          case _ => instruction
        }
    )
  }

  for (setup <- Vec.nonEmpty("BSSY", "BSSY.RECONVERGENT", "BSSY.RELIABLE"))
    test(s"$setup and BSYNC reconverge divergent shared-memory producers before consumption") {
      val body = Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        (setup, "B0,0xa0", None),
        ("ISETP.LT.U32.AND", "P0,PT,R0,16,PT", None),
        ("BRA", "0x70", Some("@P0")),
        ("MOV", "R4,2", None),
        ("STS", "[R0.X4],R4", None),
        ("BRA", "0x90", None),
        ("MOV", "R4,1", None),
        ("STS", "[R0.X4],R4", None),
        (setup.replace("BSSY", "BSYNC"), "B0", None),
        ("LOP3.LUT", "R6,R0,16,RZ,0x3c,!PT", None),
        ("LDS", "R4,[R6.X4]", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      )
      executeSynthetic(
        body,
        Vec.tabulateNonEmpty(16)(_ => 2).concatNonEmpty(Vec.tabulateNonEmpty(16)(_ => 1)),
        shared = 128
      )
    }

  test("barrier tokens survive register clearing, local spills and restoration into another slot") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("BSSY", "B0,0x100", None),
        ("BMOV.32.CLEAR", "R31,B0", None),
        ("BMOV.32.CLEAR", "RZ,B11", None),
        ("STL", "[RZ],R31", None),
        ("MOV", "R31,0", None),
        ("LDL", "R31,[RZ]", None),
        ("ISETP.LT.U32.AND", "P0,PT,R0,16,PT", None),
        ("BRA", "0xc0", Some("@P0")),
        ("MOV", "R4,2", None),
        ("STS", "[R0.X4],R4", None),
        ("BRA", "0xe0", None),
        ("MOV", "R4,1", None),
        ("STS", "[R0.X4],R4", None),
        ("BMOV.32", "B11,R31", None),
        ("BSYNC", "B11", None),
        ("LOP3.LUT", "R6,R0,16,RZ,0x3c,!PT", None),
        ("LDS", "R4,[R6.X4]", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.tabulateNonEmpty(16)(_ => 2).concatNonEmpty(Vec.tabulateNonEmpty(16)(_ => 1)),
      shared = 128
    )
  }

  test("zero-fill loads skip memory reads and preserve an inactive instruction destination") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("CS2R", "R4,SRZ", None),
        ("ISETP.EQ.AND", "P0,PT,R0,RZ,PT", None),
        ("MOV", "R6,17", None),
        ("LDG.E", "R6,[R4.64],!PT", Some("@!P0")),
        ("STG.E", "[R2.64],R6", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(17, 0)
    )
  }

  test("text disassembly preserves zero-fill operands, register banks and local branch targets") {
    val text = """probe:
      |/*0000*/ @P0 LDG.E R3, [R4], !P1;
      |/*0010*/ P2R R6, PR, RZ, 0x78;
      |/*0020*/ BRA `(.L_end);
      |.L_end:
      |/*0030*/ RET.ABS R8 0x20;
      |""".stripMargin
    val decoded = ListingDecoder.parse(text, "probe").toOption.get.head.instructions
    assertEquals(decoded.head.operands, SASSFixture.operands("R3, [R4], !P1"))
    assertEquals(decoded.head.guard, Some(Instruction.Guard(Operand.Predicate(Some(0), false, false))))
    assertEquals(decoded(1).operands, SASSFixture.operands("R6, RZ, 0x78"))
    assertEquals(decoded(2).operands, SASSFixture.operands("0x30"))
    assertEquals(decoded(3).operands, SASSFixture.operands("R8, 0x20"))
    assert(ListingDecoder.parse(text.replace("/*0010*/", "/*0018*/"), "probe").isLeft)
  }

  test("local subroutine returns use the saved PC and preserve caller registers") {
    executeSynthetic(
      Vec.nonEmpty(
        ("MOV", "R5,1", None),
        ("LEPC", "R16", None),
        ("CALL.REL.NOINC", "0x70", None),
        ("IADD3", "R5,R5,2,RZ", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R5", None),
        ("EXIT", "", None),
        ("IADD3", "R5,R5,4,RZ", None),
        ("RET.ABS", "R16,0x20", None)
      ),
      Vec.nonEmpty(7)
    )
  }

  test("dependency barriers accept completed copy scoreboards and explicit wait masks") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("MOV", "R5,41", None),
        ("STG.E", "[R2.64],R5", None),
        ("LDGSTS.E", "[RZ],[R2.64]", None),
        ("DEPBAR.LE", "SB1,0x0,{3,2}", None),
        ("LDS", "R6,[0x0]", None),
        ("IADD3", "R6,R6,1,RZ", None),
        ("STG.E", "[R2.64],R6", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(42),
      shared = 4
    )
  }

  test("Maxwell and Pascal mask-only dependency barriers observe completed loads") {
    for {
      sm     <- Vec.nonEmpty(50, 60)
      binary <- Vec.nonEmpty(false, true)
    }
      executeSynthetic(
        Vec.nonEmpty(
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("MOV", "R5,41", None),
          ("STG.E", "[R2],R5", None),
          ("LDG.E", "R6,[R2]", None),
          ("DEPBAR", "{0}", None),
          ("IADD", "R6,R6,1", None),
          ("DEPBAR", "{0,3,5}", None),
          ("STG.E", "[R2],R6", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(42),
        sm = sm,
        rewrite = instruction =>
          if (
            binary &&
            (instruction.operation match {
              case _: Instruction.Depbar => true
              case _                     => false
            })
          ) {
            // CUDA emits this mask-only DEPBAR {0} word; the decoder exposes its bitmask.
            val bytes = LittleEndian.toVec(0xf0f0000000070001L, 8).toArray ++
              Array[Byte](0xfd.toByte, 0x07, 0x00)
            InstructionDecoder.decodeTyped(instruction.address, bytes, s"SM$sm").fold(fail(_), identity)
          } else instruction
      )
  }

  test("register and absolute shared addresses refer to the same storage on legacy and modern SASS") {
    Vec.nonEmpty(35, 86).foreach { sm =>
      executeSynthetic(
        Vec.nonEmpty(
          ("MOV", "R4,31", None),
          ("STS", "[RZ],R4", None),
          ("LDS", "R5,[0x0]", None),
          ("MOV", "R6,11", None),
          ("STS", "[0x4],R6", None),
          ("MOV", "R7,4", None),
          ("LDS", "R6,[R7]", None),
          ("IADD", "R8,R5,R6", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("STG.E", "[R2],R8", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(42),
        shared = 8,
        sm = sm
      )
    }
  }

  test("LDS.U.128 reads a four-register vector from shared memory") {
    executeSynthetic(
      Vec.nonEmpty(
        ("MOV", "R4,1", None),
        ("MOV", "R5,2", None),
        ("MOV", "R6,3", None),
        ("MOV", "R7,4", None),
        ("STS.128", "[0x0],R4", None),
        ("LDS.U.128", "R8,[0x0]", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E.128", "[R2.64],R8", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(1, 2, 3, 4),
      shared = 16
    )
  }

  test("BREAK removes lanes from a pending convergence barrier") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("BSSY", "B0,0x90", None),
        ("ISETP.LT.U32.AND", "P0,PT,R0,16,PT", None),
        ("BRA", "0x70", Some("@!P0")),
        ("BREAK", "B0", None),
        ("MOV", "R4,1", None),
        ("BRA", "0x90", None),
        ("MOV", "R4,2", None),
        ("BSYNC", "B0", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.tabulateNonEmpty(16)(_ => 1).concatNonEmpty(Vec.tabulateNonEmpty(16)(_ => 2))
    )
  }

  test("block predicate reduction and per-lane local spills preserve values") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("STL", "[RZ],R0", None),
        ("ISETP.EQ.U32.AND", "P0,PT,R0,17,PT", None),
        ("BAR.RED.OR.DEFER_BLOCKING", "0,P0", None),
        ("B2R.RESULT", "RZ,P1", None),
        ("LDL", "R4,[RZ]", None),
        ("SEL", "R4,R4,0xffffffff,P1", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.tabulateNonEmpty(64)(_.raw)
    )
  }

  test("SASS barrier IDs reject values that would wrap during Int conversion") {
    def lowerBarrier(opcode: String, operands: String) = {
      val function = DecodedFunction(
        "saxpy",
        Vec.nonEmpty(SASSFixture.instruction(0, opcode, operands.split(',').toVec.map(_.trim), None))
      )
      val decoded = Vec.nonEmpty(function)
      SASSFixture.lower(CubinSuiteFixtures.module, CubinSuiteFixtures.module.kernels.head, decoded)
    }

    Vec.nonEmpty(
      ("BAR.SYNC", "3"),
      ("BAR.RED.OR.DEFER_BLOCKING", "3,P0")
    ).foreach { case (opcode, validOperands) =>
      assert(lowerBarrier(opcode, validOperands).isRight, s"$opcode $validOperands")
      Vec.nonEmpty("4294967296", "-4294967296").foreach { outOfRange =>
        val operands = validOperands.replace("3", outOfRange)
        val result   = lowerBarrier(opcode, operands)
        assert(result.left.exists(_.contains("barrier id is outside Int range")), s"$opcode $operands: $result")
      }
    }
  }

  test("signed shared atomic minimum includes negative lane values") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("ISETP.EQ.U32.AND", "P0,PT,R0,RZ,PT", None),
        ("STS", "[RZ],RZ", Some("@P0")),
        ("BAR.SYNC", "0", None),
        ("IADD3", "R4,-R0,RZ,RZ", None),
        ("ATOMS.MIN.S32", "RZ,[RZ],R4", None),
        ("BAR.SYNC", "0", None),
        ("LDS", "R4,[RZ]", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.tabulateNonEmpty(32)(_ => -31),
      shared = 4
    )
  }

  test("signed shared atomic maximum compares both signs") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("ISETP.EQ.U32.AND", "P0,PT,R0,RZ,PT", None),
        ("MOV", "R5,0x80000000", None),
        ("STS", "[RZ],R5", Some("@P0")),
        ("BAR.SYNC", "0", None),
        ("IADD3", "R4,R0,-16,RZ", None),
        ("ATOMS.MAX.S32", "RZ,[RZ],R4", None),
        ("BAR.SYNC", "0", None),
        ("LDS", "R4,[RZ]", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.tabulateNonEmpty(32)(_ => 15),
      shared = 4
    )
  }

  test("lane masks and bit reversal preserve warp-local lane numbering") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("S2R", "R4,SR_GEMASK", None),
        ("BREV", "R4,R4", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.tabulateNonEmpty(64)(lane => Integer.reverse(-1 << (lane.raw % 32)))
    )
  }

  test("legacy entry RET followed by EXIT completes without a device-call token") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R0", None),
        ("RET", "", None),
        ("EXIT", "", None)
      ),
      Vec.tabulateNonEmpty(32)(_.raw),
      sm = 35
    )
  }

  test("legacy SSY and predicated NOP.S rendezvous at the forward target") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("MOV", "R4,1", None),
        ("SSY", "0x70", None),
        ("ISETP.LT.U32.AND", "P0,PT,R0,16,PT", None),
        ("NOP.S", "", Some("@P0")),
        ("MOV", "R4,2", None),
        ("NOP.S", "", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.tabulateNonEmpty(16)(_ => 1).concatNonEmpty(Vec.tabulateNonEmpty(16)(_ => 2)),
      sm = 35
    )
  }

  test("legacy stores with .S execute before reconverging at the SSY target") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("MOV", "R4,1", None),
        ("SSY", "0x80", None),
        ("ISETP.LT.U32.AND", "P0,PT,R0,16,PT", None),
        ("STS.S", "[R0.X4],R4", Some("@P0")),
        ("MOV", "R4,2", None),
        ("STS.S", "[R0.X4],R4", None),
        ("NOP", "", None),
        ("LOP3.LUT", "R6,R0,16,RZ,0x3c,!PT", None),
        ("LDS", "R4,[R6.X4]", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.tabulateNonEmpty(16)(_ => 2).concatNonEmpty(Vec.tabulateNonEmpty(16)(_ => 1)),
      shared = 128,
      sm = 35
    )
  }

  test("skipping a legacy SSY does not revive the previous token in its reused slot") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("SSY", "0xb0", None),
        ("SSY", "0x40", None),
        ("NOP.S", "", None),
        ("ISETP.NE.AND", "P0,PT,R0,RZ,PT", None),
        ("NOP.S", "", Some("@P0")),
        ("BRA", "0x90", None),
        ("SSY", "0x90", None),
        ("NOP.S", "", None),
        ("MOV", "R4,7", None),
        ("NOP.S", "", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("MOV", "R4,1", Some("@P0")),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(7).concatNonEmpty(Vec.tabulateNonEmpty(31)(_ => 1)),
      sm = 35
    )
  }

  test("legacy BRK removes bypassed nested SSY lanes before the PBK target") {
    executeSynthetic(
      Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("MOV", "R4,1", None),
        ("PBK", "0xc0", None),
        ("SSY", "0x90", None),
        ("ISETP.LT.U32.AND", "P0,PT,R0,16,PT", None),
        ("BRK", "", Some("@P0")),
        ("BRA", "0x90", None),
        ("NOP", "", None),
        ("NOP", "", None),
        ("MOV", "R4,2", None),
        ("BRA", "0xc0", None),
        ("NOP", "", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.tabulateNonEmpty(16)(_ => 1).concatNonEmpty(Vec.tabulateNonEmpty(16)(_ => 2)),
      sm = 35
    )
  }

  test("BRX adds its register and displacement to the following instruction address") {
    executeSynthetic(
      Vec.nonEmpty(
        ("MOV", "R0,0x30", None),
        ("BRX", "R0,-0x20", None),
        ("EXIT", "", None),
        ("MOV", "R4,19", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(19)
    )
  }
}
