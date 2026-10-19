package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.either.*

class IntegerCompareSuite extends munit.FunSuite {
  private def mask(flags: Boolean*): Vec[Long] = Vec.from(flags).map(flag => if (flag) 0xffffffffL else 0L)

  test("compiler-produced ISET.X compares signed and unsigned 64-bit values across low-word ties and borrows") {
    val values = Vec(
      0L,
      1L,
      0x7fffffffL,
      0x80000000L,
      0xffffffffL,
      0x100000000L,
      0x100000001L,
      0x1ffffffffL,
      Long.MaxValue,
      Long.MinValue,
      0xffffffff00000000L,
      -1L
    )
    val kernel = load(35, Fixture.listing("isetExtendedSm35"), "iset_probe")
    assertEquals(kernel.function.instructions.count(_.mnemonic.raw == "ISET"), 5)
    for {
      a <- values
      b <- values
    }
      assertEquals(
        kernel.launch(1)(Arg.zeros(20), Arg.u64(a), Arg.u64(b)).words(0),
        mask(a < b, java.lang.Long.compareUnsigned(a, b) < 0, a <= b, a > b, a >= b),
        s"a=${a.toHexString}, b=${b.toHexString}"
      )
  }

  test("compiler-produced sm_35 64-bit equality extends low-word CC into high-word ISET.X") {
    val cases = Vec(
      (0L, 0L, Vec(1L, 0L, 1L, 1L)),
      (1L, 1L, Vec(1L, 0L, 0L, 0L)),
      (1L, 2L, Vec(0L, 1L, 0L, 0L)),
      (0x0000000100000000L, 0L, Vec(0L, 1L, 0L, 1L)),
      (0x0000000100000001L, 0x0000000100000002L, Vec(0L, 1L, 0L, 0L)),
      (Long.MinValue, Long.MinValue, Vec(1L, 0L, 0L, 0L)),
      (-1L, 0L, Vec(0L, 1L, 0L, 1L))
    )
    for ((name, function, count) <- Vec(("isetEq64Sm35", "eq64", 4), ("isetEq64SignedSm35", "eq64s", 2))) {
      val kernel = load(35, Fixture.listing(name), function)
      cases.foreach { (a, b, expected) =>
        assertEquals(kernel.launch(1)(Arg.zeros(16), Arg.u64(a), Arg.u64(b)).words(0).take(count), expected.take(count))
      }
    }
  }

  test("ICMP compares its third source with zero and selects the first or second source") {
    check(
      86,
      """LDC.64 R4, c[0x0][0x160]
        |MOV R0, 11
        |MOV R1, 22
        |MOV R2, -1
        |ICMP.NE R3, R0, R1, R2
        |STG.E [R4.64], R3
        |MOV R2, 0
        |ICMP.NE R3, R0, R1, R2
        |STG.E [R4.64+0x4], R3
        |MOV R2, 1
        |ICMP.NE R3, R0, R1, R2
        |STG.E [R4.64+0x8], R3
        |EXIT""".stripMargin,
      Vec(11, 22, 11),
      block = Some(1)
    )
  }

  test("compiler-produced sm_35 ICMP sequence matches signed select behaviour") {
    val kernel = load(35, Fixture.listing("icmpSm35"), "probe")
    Vec((-1, Vec(11L, 11L, 22L)), (0, Vec(22L, 22L, 22L)), (1, Vec(11L, 22L, 11L))).foreach { (c, expected) =>
      assertEquals(kernel.launch(1)(Arg.zeros(12), Arg.u32(11), Arg.u32(22), Arg.u32(c)).words(0), expected)
    }
  }

  test("ISET combines its compare with the incoming predicate and supports unsigned comparison") {
    check(
      86,
      """LDC.64 R4, c[0x0][0x160]
        |MOV R0, -1
        |MOV R1, 1
        |ISETP.EQ.AND P0, PT, RZ, RZ, PT
        |ISET.GT.U32.AND R2, R0, R1, P0
        |STG.E [R4.64], R2
        |ISETP.EQ.AND P0, PT, RZ, 1, PT
        |ISET.GT.U32.AND R2, R0, R1, P0
        |STG.E [R4.64+0x4], R2
        |ISETP.EQ.AND P0, PT, RZ, RZ, PT
        |ISET.GT.U32.AND R2, R0, R1, P0
        |STG.E [R4.64+0x8], R2
        |EXIT""".stripMargin,
      Vec(-1, 0, -1),
      block = Some(1)
    )
  }

  test("ISET.CC supplies the zero flag consumed by BRA CC.EQU and CC.NEU") {
    check(
      86,
      """LDC.64 R4, c[0x0][0x160]
        |MOV R0, 5
        |MOV R1, 5
        |ISET.EQ.AND RZ.CC, R0, R1, PT
        |BRA CC.NEU, `(one)
        |MOV R2, 0
        |BRA `(first)
        |one:
        |MOV R2, 1
        |first:
        |STG.E [R4.64], R2
        |MOV R0, 5
        |MOV R1, 6
        |ISET.EQ.AND RZ.CC, R0, R1, PT
        |BRA CC.EQU, `(two)
        |MOV R2, 0
        |BRA `(second)
        |two:
        |MOV R2, 1
        |second:
        |STG.E [R4.64+0x4], R2
        |EXIT""".stripMargin,
      Vec(1, 1),
      block = Some(1)
    )
  }

  for (sm <- Vec(50, 60))
    test(s"sm_$sm decoded PSET predicate mask agrees with the source selection") {
      val kernel      = load(sm, Fixture.listing(s"psetSm${sm}Patched"), "probe")
      val modifiers   = Vec("AND", "AND")
      val instruction = kernel.function.instructions
        .find(i => i.mnemonic.raw == "PSET" && i.modifiers.map(_.name) == modifiers).get
      assertEquals(instruction.operands, OperandParser.parseMany("R0, P0, PT, PT").value)
      Vec(0, 1, Int.MinValue, -1, 0).foreach { input =>
        assertEquals(kernel.launch(1)(Arg.zeros(4), Arg.u32(input)).words(0), mask(input != 0))
      }
      val flags = kernel.map(i =>
        if (i == instruction) i.rewrite(operands = i.operands.updated(0, OperandParser.parse("R0.CC").value)) else i
      )
      assert(flags.lower(8, 4).isLeft)
    }

  test("UISETP.U64 compares both halves of uniform register pairs") {
    check(
      120,
      """LDC.64 R10, c[0x0][0x160]
        |UMOV UR4, 0
        |UMOV UR5, 1
        |UISETP.GT.U64.AND UP0, UPT, UR4, 512, UPT
        |UMOV UR6, 1
        |USEL UR6, UR6, URZ, UP0
        |UVIMNMX.U32 UR6, UR6, URZ, !UPT
        |MOV R4, UR6
        |VIMNMX.S32 R4, R4, -1, !PT
        |STG.E [R10], R4
        |EXIT""".stripMargin,
      Vec(1)
    )
  }

  test("carry, extended comparison, float negation and aliased predicates retain their inputs") {
    check(
      86,
      """MOV R0, 0xffffffff
        |MOV R8, 8
        |MOV R9, 4
        |MOV R10, 2
        |IADD3 R4, P0, R0, 2, RZ
        |SEL R5, R8, 0, P0
        |ISETP.NE.U32.AND P1, PT, R4, 0, PT
        |ISETP.NE.AND.EX P1, PT, RZ, RZ, PT, P1
        |ISETP.EQ.AND P0, P1, RZ, RZ, P1
        |SEL R6, R9, 0, P0
        |IADD3 R5, R5, R6, RZ
        |MOV R6, 0x3f800000
        |FMUL R6, -R6, 2
        |FSETP.EQ.AND P0, PT, R6, -2, PT
        |SEL R6, R10, 0, P0
        |IADD3 R5, R5, R6, R4
        |LDC.64 R2, c[0x0][0x160]
        |STG.E [R2.64], R5
        |EXIT""".stripMargin,
      Vec(15)
    )
  }
}
