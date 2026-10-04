package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.asm.*

class UniformRegisterSuite extends SyntheticSASSExecutionSuiteBase {
  test("general, uniform and predicate banks remain distinct while lowering uses temporaries") {
    executeSynthetic(
      body = Vec.nonEmpty(
        ("MOV", "R4,11", None),
        ("UMOV", "UR4,17", None),
        ("UISETP.EQ.U32.AND", "UP0,UPT,UR4,17,UPT", None),
        ("ISETP.EQ.U32.AND", "P0,PT,R4,12,PT", None),
        ("MOV", "R6,23", None),
        ("MOV", "R6,1", Some("@P0")),
        ("IADD3", "R6,R6,R4,RZ", Some("@UP0")),
        ("IADD3", "R6,R6,UR4,RZ", None),
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("STG.E", "[R10.64],R6", None),
        ("EXIT", "", None)
      ),
      expected = Vec.nonEmpty(51),
      sm = 120
    )
  }

  for (opcode <- Vec.nonEmpty("R2UR", "R2UR.BROADCAST"))
    test(s"an elected $opcode writer updates the entire live warp across divergence") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("IADD3", "R4,R0,7,RZ", None),
          ("ISETP.EQ.U32.AND", "P0,PT,R0,2,PT", None),
          (opcode, "UR4,R4", Some("@P0")),
          ("MOV", "R6,UR4", None),
          ("ISETP.EQ.U32.AND", "P0,PT,R0,1,PT", None),
          ("BRA", "0x90", Some("@!P0")),
          ("UMOV", "UR4,11", None),
          ("BRA", "0x90", None),
          ("BAR.SYNC", "0", None),
          ("IADD3", "R6,R6,UR4,RZ", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2],R6", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(20, 20, 20, 20),
        sm = 120
      )
    }

  test("older targets reject a numbered register that aliases their zero encoding") {
    assert(Assembler.assemble("UMOV UR4, UR63;", "SM75").isLeft)
  }

  test("Blackwell UR63 retains a shift count independently of URZ") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("UMOV", "UR63,5", None),
        ("MOV", "R2,64", None),
        ("SHF.R.U64", "R4,R2,UR63,RZ", None),
        ("MOV", "R5,URZ", None),
        ("STG.E.64", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(2, 0),
      sm = 120
    )
  }

  test("machine vocabularies distinguish zero registers from Blackwell UR63") {
    Vec.nonEmpty(
      "SM75"  -> "UMOV UR4, URZ;",
      "SM90"  -> "UMOV UR4, URZ;",
      "SM120" -> "UMOV UR4, URZ;",
      "SM120" -> "UMOV UR63, 0x5;",
      "SM120" -> "SHF.R.U64 R13, R2, UR63, R3;"
    ).foreach { (target, source) =>
      val encoded = Assembler.assemble(source, target).fold(fail(_), identity)
      val decoded = Disassembler.decode(encoded, target).fold(fail(_), identity)
      assertEquals(decoded.operation, Parser.parse(source).fold(fail(_), identity), clues(target, source))
      val printed = Printer.print(decoded.operation).fold(fail(_), identity)
      assertEquals(Parser.parse(printed), Right(decoded.operation), clues(target, source))
    }
  }
  for (
    (opcode, left, right, result) <- Vec.nonEmpty(
      ("UFADD", 0x3fc00000, 0x40100000, 0x40700000),
      ("UFADD.FTZ", 1, 0, 0)
    )
  ) test(s"$opcode reads aliased uniform sources once across active lanes") {
    executeSynthetic(
      body = Vec.nonEmpty(
        ("UMOV", s"UR6,0x${Integer.toUnsignedString(left, 16)}", None),
        ("UMOV", s"UR7,0x${Integer.toUnsignedString(right, 16)}", None),
        (opcode, "UR7,UR6,UR7", None),
        ("MOV", "R4,UR7", None),
        ("S2R", "R0,SR_TID.X", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
        ("STG.E", "[R2],R4", None),
        ("EXIT", "", None)
      ),
      expected = Vec.nonEmpty(result, result, result, result),
      sm = 120
    )
  }

}
