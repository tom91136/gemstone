package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.testing.Suite

class UniformRegisterSuite extends Suite {
  test("general, uniform and predicate banks remain distinct while lowering uses temporaries") {
    check(
      120,
      """MOV R4, 11
        |UMOV UR4, 17
        |UISETP.EQ.U32.AND UP0, UPT, UR4, 17, UPT
        |ISETP.EQ.U32.AND P0, PT, R4, 12, PT
        |MOV R6, 23
        |@P0 MOV R6, 1
        |@UP0 IADD3 R6, R6, R4, RZ
        |IADD3 R6, R6, UR4, RZ
        |LDC.64 R10, c[0x0][0x160]
        |STG.E [R10.64], R6
        |EXIT""".stripMargin,
      Vec(51)
    )
  }

  for (opcode <- Vec("R2UR", "R2UR.BROADCAST"))
    test(s"an elected $opcode writer updates the entire live warp across divergence") {
      check(
        120,
        s"""S2R R0, SR_TID.X
          |IADD3 R4, R0, 7, RZ
          |ISETP.EQ.U32.AND P0, PT, R0, 2, PT
          |@P0 $opcode UR4, R4
          |MOV R6, UR4
          |ISETP.EQ.U32.AND P0, PT, R0, 1, PT
          |@!P0 BRA 0x90
          |UMOV UR4, 11
          |BRA 0x90
          |BAR.SYNC 0
          |IADD3 R6, R6, UR4, RZ
          |LDC.64 R2, c[0x0][0x160]
          |IMAD.WIDE.U32 R2, R0, 4, R2
          |STG.E [R2], R6
          |EXIT""".stripMargin,
        Vec(20, 20, 20, 20)
      )
    }

  // measured on sm_89: the elected value goes to the uniform register as for R2UR, and each lane that executes ORs
  // whether its value differs into the predicate where R2UR assigns it; other lanes keep their predicate
  private def orProgram(values: String, preset: String, guard: String): String =
    s"""S2R R0, SR_TID.X
      |MOV R4, 7
      |$values
      |$preset
      |ISETP.NE.U32.AND P1, PT, R0, 0, PT
      |${guard}R2UR.OR P0, UR4, R4
      |MOV R6, UR4
      |SEL R8, RZ, 0x1, !P0
      |IADD3 R6, R6, R6, R8
      |LDC.64 R2, c[0x0][0x160]
      |IMAD.WIDE.U32 R2, R0, 4, R2
      |STG.E [R2], R6
      |EXIT""".stripMargin

  test("R2UR.OR keeps a predicate that is set where the value matches the elected one") {
    check(
      89,
      orProgram(
        "ISETP.EQ.U32.AND P2, PT, R0, 2, PT\n@P2 MOV R4, 9",
        "ISETP.GE.U32.AND P0, PT, R0, 3, PT",
        ""
      ),
      Vec(14, 14, 15, 15)
    )
  }

  test("R2UR.OR elects the first live lane and sets the predicate of every lane that differs from it") {
    check(
      89,
      orProgram(
        "ISETP.EQ.U32.AND P2, PT, R0, 0, PT\n@P2 MOV R4, 9",
        "ISETP.GE.U32.AND P0, PT, R0, 99, PT",
        ""
      ),
      Vec(18, 19, 19, 19)
    )
  }

  test("a predicated R2UR.OR leaves the uniform value and predicate of the lanes it skips") {
    check(
      89,
      orProgram(
        "ISETP.EQ.U32.AND P2, PT, R0, 0, PT\n@P2 MOV R4, 9",
        "ISETP.EQ.U32.AND P0, PT, R0, 1, PT",
        "@P1 "
      ),
      Vec(14, 15, 14, 14)
    )
  }

  test("older targets reject a numbered register that aliases their zero encoding") {
    assert(Assembler.assemble("UMOV UR4, UR63;", target(75)).isLeft)
  }

  test("Blackwell UR63 retains a shift count independently of URZ") {
    check(
      120,
      "LDC.64 R10, c[0x0][0x160]; UMOV UR63, 5; MOV R2, 64; SHF.R.U64 R4, R2, UR63, RZ; MOV R5, URZ; STG.E.64 [R10], R4; EXIT",
      Vec(2, 0)
    )
  }

  test("machine vocabularies distinguish zero registers from Blackwell UR63") {
    Vec(
      75  -> "UMOV UR4, URZ;",
      90  -> "UMOV UR4, URZ;",
      120 -> "UMOV UR4, URZ;",
      120 -> "UMOV UR63, 0x5;",
      120 -> "SHF.R.U64 R13, R2, UR63, R3;"
    ).foreach { (sm, source) =>
      val encoded = Assembler.assemble(source, target(sm)).fold(fail(_), identity)
      val decoded = disassemble(encoded, sm).fold(fail(_), identity)
      assertEquals(decoded.operation, Parser.parse(source).fold(fail(_), identity), clues(target, source))
      val printed = Printer.print(decoded.operation).fold(fail(_), identity)
      assertEquals(Parser.parse(printed), Right(decoded.operation), clues(target, source))
    }
  }

  for ((opcode, left, right, result) <- Vec(("UFADD", 0x3fc00000, 0x40100000, 0x40700000), ("UFADD.FTZ", 1, 0, 0)))
    test(s"$opcode reads aliased uniform sources once across active lanes") {
      check(
        120,
        s"""UMOV UR6, 0x${Integer.toUnsignedString(left, 16)}
          |UMOV UR7, 0x${Integer.toUnsignedString(right, 16)}
          |$opcode UR7, UR6, UR7
          |MOV R4, UR7
          |S2R R0, SR_TID.X
          |LDC.64 R2, c[0x0][0x160]
          |IMAD.WIDE.U32 R2, R0, 4, R2
          |STG.E [R2], R4
          |EXIT""".stripMargin,
        Vec.fill(4)(result)
      )
    }

  test("UFSEL selects a floating constant through a negated uniform predicate") {
    check(
      120,
      """UPLOP3.LUT UP0, UPT, UPT, UPT, UPT, 0x80, 0
        |UMOV UR4, 0
        |UFSEL UR4, UR4, 1, !UP0
        |MOV R6, UR4
        |LDC.64 R2, c[0x0][0x160]
        |STG.E [R2], R6
        |EXIT""".stripMargin,
      Vec(0x3f800000)
    )
  }

  test("Blackwell signed 64-bit comparisons and selects preserve extended uniform registers") {
    check(
      120,
      """LDC.64 R10, c[0x0][0x160]
        |UMOV UR64, 0
        |UMOV UR65, 0x80000000
        |UISETP.LT.S64.AND UP0, UPT, UR64, 0, UPT
        |USEL.64 UR66, UR64, URZ, UP0
        |MOV.64 R4, UR66
        |SEL.64 R4, R4, RZ, PT
        |STG.E.64 [R10], R4
        |EXIT""".stripMargin,
      Vec(0, Int.MinValue)
    )
  }
}
