package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.sass.asm.*

class SyntheticSASSControlFlowMemorySuite extends SyntheticSASSExecutionSuiteBase {
  test("BSSY and BSYNC reconverge divergent shared-memory producers before consumption") {
    val body = Vec(
      ("S2R", "R0,SR_TID.X", None),
      ("BSSY", "B0,0xa0", None),
      ("ISETP.LT.U32.AND", "P0,PT,R0,16,PT", None),
      ("BRA", "0x70", Some("@P0")),
      ("MOV", "R4,2", None),
      ("STS", "[R0.X4],R4", None),
      ("BRA", "0x90", None),
      ("MOV", "R4,1", None),
      ("STS", "[R0.X4],R4", None),
      ("BSYNC", "B0", None),
      ("LOP3.LUT", "R6,R0,16,RZ,0x3c,!PT", None),
      ("LDS", "R4,[R6.X4]", None),
      ("LDC.64", "R2,c[0x0][0x160]", None),
      ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
      ("STG.E", "[R2.64],R4", None),
      ("EXIT", "", None)
    )
    executeSynthetic(body, Vec.fill(16)(2) ++ Vec.fill(16)(1), shared = 128)
  }

  test("zero-fill loads skip memory reads and preserve an inactive instruction destination") {
    executeSynthetic(
      Vec(
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
      Vec(17, 0)
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
      Vec(
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
      Vec(7)
    )
  }

  test("dependency barriers accept completed copy scoreboards and explicit wait masks") {
    executeSynthetic(
      Vec(
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
      Vec(42),
      shared = 4
    )
  }

  test("Maxwell and Pascal mask-only dependency barriers observe completed loads") {
    Vec(50, 60).foreach { sm =>
      executeSynthetic(
        Vec(
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
        Vec(42),
        sm = sm
      )
    }
  }

  test("register and absolute shared addresses refer to the same storage on legacy and modern SASS") {
    Vec(35, 86).foreach { sm =>
      executeSynthetic(
        Vec(
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
        Vec(42),
        shared = 8,
        sm = sm
      )
    }
  }

  test("LDS.U.128 reads a four-register vector from shared memory") {
    executeSynthetic(
      Vec(
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
      Vec(1, 2, 3, 4),
      shared = 16
    )
  }

  test("BREAK removes lanes from a pending convergence barrier") {
    executeSynthetic(
      Vec(
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
      Vec.fill(16)(1) ++ Vec.fill(16)(2)
    )
  }

  test("block predicate reduction and per-lane local spills preserve values") {
    executeSynthetic(
      Vec(
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
      (0 until 64).toVec
    )
  }

  test("SASS barrier IDs reject values that would wrap during Int conversion") {
    def lowerBarrier(opcode: String, operands: String) = {
      val function = DecodedFunction(
        "saxpy",
        Vec(SASSFixture.instruction(0, opcode, operands.split(',').toVec.map(_.trim), None))
      )
      val decoded = Vec(function)
      SASSFixture.lower(CubinSuiteFixtures.module, CubinSuiteFixtures.module.kernels.head, decoded)
    }

    Vec(
      ("BAR.SYNC", "3"),
      ("BAR.RED.OR.DEFER_BLOCKING", "3,P0")
    ).foreach { case (opcode, validOperands) =>
      assert(lowerBarrier(opcode, validOperands).isRight, s"$opcode $validOperands")
      Vec("4294967296", "-4294967296").foreach { outOfRange =>
        val operands = validOperands.replace("3", outOfRange)
        val result   = lowerBarrier(opcode, operands)
        assert(result.left.exists(_.contains("barrier id is outside Int range")), s"$opcode $operands: $result")
      }
    }
  }

  test("signed shared atomic minimum includes negative lane values") {
    executeSynthetic(
      Vec(
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
      Vec.fill(32)(-31),
      shared = 4
    )
  }

  test("signed shared atomic maximum compares both signs") {
    executeSynthetic(
      Vec(
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
      Vec.fill(32)(15),
      shared = 4
    )
  }

  test("lane masks and bit reversal preserve warp-local lane numbering") {
    executeSynthetic(
      Vec(
        ("S2R", "R0,SR_TID.X", None),
        ("S2R", "R4,SR_GEMASK", None),
        ("BREV", "R4,R4", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec.tabulate(64)(lane => Integer.reverse(-1 << (lane % 32)))
    )
  }

  test("legacy entry RET followed by EXIT completes without a device-call token") {
    executeSynthetic(
      Vec(
        ("S2R", "R0,SR_TID.X", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2.64],R0", None),
        ("RET", "", None),
        ("EXIT", "", None)
      ),
      (0 until 32).toVec,
      sm = 35
    )
  }

  test("legacy SSY and predicated NOP.S rendezvous at the forward target") {
    executeSynthetic(
      Vec(
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
      Vec.fill(16)(1) ++ Vec.fill(16)(2),
      sm = 35
    )
  }

  test("legacy stores with .S execute before reconverging at the SSY target") {
    executeSynthetic(
      Vec(
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
      Vec.fill(16)(2) ++ Vec.fill(16)(1),
      shared = 128,
      sm = 35
    )
  }

  test("skipping a legacy SSY does not revive the previous token in its reused slot") {
    executeSynthetic(
      Vec(
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
      Vec(7) ++ Vec.fill(31)(1),
      sm = 35
    )
  }

  test("legacy BRK removes bypassed nested SSY lanes before the PBK target") {
    executeSynthetic(
      Vec(
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
      Vec.fill(16)(1) ++ Vec.fill(16)(2),
      sm = 35
    )
  }

  test("BRX adds its register and displacement to the following instruction address") {
    executeSynthetic(
      Vec(
        ("MOV", "R0,0x30", None),
        ("BRX", "R0,-0x20", None),
        ("EXIT", "", None),
        ("MOV", "R4,19", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R4", None),
        ("EXIT", "", None)
      ),
      Vec(19)
    )
  }
}
