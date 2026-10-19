package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.testing.either.*

class ConditionCodeSuite extends munit.FunSuite {
  private val Flags     = Vec(false, true)
  private val ZeroTests = Vec("Z", "NZ")

  private def probe(body: String): Kernel =
    program(35, s"LDC.64 R4, c[0x0][0x140]; MOV R0, c[0x0][0x148]; MOV R1, c[0x0][0x150]; $body", base = 0x140)

  /** Packed CC as stored by `P2R R6, RZ, 0xf000`: zero, sign, carry and overflow in bits 12 to 15. */
  private def packed(result: Int, carry: Boolean, overflow: Boolean): Long =
    (if (result == 0) 0x1000 else 0) | (if (result < 0) 0x2000 else 0) | (if (carry) 0x4000 else 0) |
      (if (overflow) 0x8000 else 0)

  private def flags(kernel: Kernel, a: Long, b: Long): Vec[Long] =
    kernel.launch(1)(Arg.zeros(8), Arg.u64(a), Arg.u64(b), Arg.u32(0)).words(0)

  private def add(a: Long, b: Long, subtract: Boolean): (Long, Long) = {
    val (x, y) = (a.toInt, b.toInt)
    val result = if (subtract) x - y else x + y
    val carry  =
      if (subtract) (a & 0xffffffffL) >= (b & 0xffffffffL) else (a & 0xffffffffL) + (b & 0xffffffffL) > 0xffffffffL
    val overflow = ((if (subtract) (x ^ y) & (x ^ result) else (x ^ result) & (y ^ result)) & 0x80000000) != 0
    (packed(result, carry, overflow), if (carry) 1L else 0L)
  }

  private val restore =
    "P2R R6, RZ, 0xf000; R2P PR, R6, 0xf000; IADD.X R7, RZ, RZ; ST.E [R4], R6; ST.E [R4+0x4], R7; EXIT"

  test("sm_35 IADD CC bits survive P2R/R2P and restore carry for a following IADD.X") {
    val cases = Vec(
      0x00000000L -> 0x00000000L,
      0x00000001L -> 0x00000002L,
      0xffffffffL -> 0x00000001L,
      0x7fffffffL -> 0x00000001L,
      0x80000000L -> 0x80000000L,
      0xffffffffL -> 0x00000000L,
      0x80000000L -> 0x7fffffffL
    )
    for ((lhs, rhs) <- Vec(("R0", "R1"), ("-R0", "R1"), ("R0", "-R1"))) {
      // every CC bit is destroyed before the packed state is restored
      val kernel = probe(
        s"IADD RZ.CC, $lhs, $rhs; P2R R6, RZ, 0xf000; IADD RZ.CC, R0, R0; R2P PR, R6, 0xf000; IADD.X R7, RZ, RZ; " +
          "ST.E [R4], R6; ST.E [R4+0x4], R7; EXIT"
      )
      cases.foreach { (a, b) =>
        val (cc, carry) =
          if (lhs.startsWith("-")) add(b, a, subtract = true) else add(a, b, subtract = rhs.startsWith("-"))
        assertEquals(flags(kernel, a, b), Vec(cc, carry), s"$lhs, $rhs a=0x${a.toHexString} b=0x${b.toHexString}")
      }
    }
  }

  test("sm_35 ISCADD.CC aliases its shifted source and reports add flags") {
    val kernel = probe(s"ISCADD R0.CC, R0, R1, 0x2; $restore")
    Vec(
      0x20000000L -> 0x80000000L,
      0x1fffffffL -> 0x00000003L,
      0x3fffffffL -> 0x00000008L,
      0xffffffffL -> 0x00000003L
    ).foreach { (a, b) =>
      val (cc, carry) = add(((a & 0xffffffffL) << 2) & 0xffffffffL, b, subtract = false)
      assertEquals(flags(kernel, a, b), Vec(cc, carry), s"a=0x${a.toHexString}")
    }
  }

  test("sm_35 plain IMAD.CC aliases its source and preserves valid flags under predication") {
    val cases = Vec(
      0x1fffffffL -> 0x00000008L,
      0x3fffffffL -> 0xffffffffL,
      0x40000000L -> 0x00000000L,
      0xffffffffL -> 0x00000001L
    )
    for (
      (setup, guard, executes) <- Vec(
        ("", "", true),
        ("IADD RZ.CC, R0, R1; ISETP.NE.AND P0, PT, RZ, RZ, PT; ", "@P0 ", false),
        ("ISETP.EQ.AND P0, PT, RZ, RZ, PT; ", "@P0 ", true)
      )
    ) {
      val kernel = probe(s"$setup${guard}IMAD R0.CC, R0, 0x4, R1; $restore")
      cases.foreach { (a, b) =>
        val (cc, carry) = if (executes) add(((a & 0xffffffffL) * 4L) & 0xffffffffL, b, subtract = false)
        else add(a, b, subtract = false)
        assertEquals(flags(kernel, a, b), Vec(cc, carry), s"IMAD a=0x${a.toHexString} b=0x${b.toHexString}")
      }
    }
  }

  private def decoded(raw: Long, control: Long, address: Int, sm: Int): DecodedInstruction =
    InstructionDecoder.decode(
      address.unsafe[Int >= 0],
      (LittleEndian.toVec(raw, 8) ++ LittleEndian.toVec(control, 3)).toArray,
      target(sm)
    ).value

  private def roundTrips(instruction: DecodedInstruction, sm: Int): Unit = {
    val bytes = Assembler.encode(instruction.operation, target(sm), instruction.address).value
    assertEquals(
      InstructionDecoder.decode(instruction.address, bytes, target(sm)).value.operation,
      instruction.operation
    )
  }

  for (sm <- Vec(50, 60)) {
    test(s"sm$sm machine instructions preserve the condition register bank") {
      // CUDA 11.8 scan-by-key saves and restores CC around unrelated address arithmetic
      val (save, restore) = (
        decoded(raw = 0x38e8010000f7ff1dL, control = 0xff1L, 0, sm),
        decoded(raw = 0x38f0010000f71d00L, control = 0x7f1L, 8, sm)
      )
      assertEquals((save.operands(1), restore.operands.head), (Operand.Symbol("CC"), Operand.Symbol("CC")))
      assertEquals(
        (save.modifiers, restore.modifiers),
        (Vec.empty[Instruction.Modifier], Vec.empty[Instruction.Modifier])
      )
      Vec(save, restore).foreach(roundTrips(_, sm))
    }

    test(s"sm$sm normalised predicate-bank instructions assemble and decode") {
      val (save, restore) = (
        decoded(raw = 0x38e8000000f7ff1dL, control = 0xff1L, 0, sm),
        decoded(raw = 0x38f0000000f71d00L, control = 0x7f1L, 8, sm)
      )
      assertEquals(save.operands.size, 3)
      assertEquals(restore.operands.head, Operand.Symbol("PR"))
      Vec(save, restore).foreach(roundTrips(_, sm))
    }

    for (binary <- Flags)
      test(s"sm$sm condition save/restore retains carry, zero, sign and overflow: binary=$binary") {
        Vec(("0xffffffff", 5, 1), ("0x7fffffff", 10, 0), ("1", 0, 0)).foreach { (input, cc, carry) =>
          check(
            sm,
            s"MOV R0, $input; IADD RZ.CC, R0, 1; P2R R29, CC, RZ, 0xf; IADD RZ.CC, RZ, RZ; R2P CC, R29, 0xf; IADD.X R3, RZ, RZ; " +
              "P2R R2, CC, RZ, 0xf; LDC.64 R4, c[0x0][0x160]; STG.E.64 [R4.64], R2; EXIT",
            Vec(cc, carry),
            rewrite = instruction =>
              if (!binary) instruction
              else instruction.operation.form match {
                case _: Form.P2r =>
                  instruction.operands.head match {
                    case Operand.Register(_, index) =>
                      decoded(0x38e8010000f7ff00L | index, 0xff1L, instruction.address, sm)
                    case other => fail(s"expected saved condition register, found $other")
                  }
                case _: Form.R2p =>
                  decoded(raw = 0x38f0010000f71d00L, control = 0x7f1L, instruction.address, sm)
                case _ => instruction
              }
          )
        }
      }

    test(s"sm$sm masked condition transfers preserve unselected bits and flags") {
      check(
        sm,
        "IADD RZ.CC, RZ, RZ; MOV R0, 4; R2P CC, R0, 4; MOV R1, 0xfffffff0; P2R R2, CC, R1, 0xf; LDC.64 R4, c[0x0][0x160]; STG.E [R4.64], R2; EXIT",
        Vec(0xfffffff5)
      )
    }
  }

  for (sm <- Vec(35, 50, 60))
    test(s"sm$sm identity conversion supplies the low-word equality flag") {
      check(
        sm,
        "S2R R0, SR_TID.X; I2I.U32.U32 RZ.CC, R0; ISETP.NE.X.AND P0, PT, RZ, RZ, PT; MOV R5, 11; SEL R4, R5, 22, P0; " +
          "LDC.64 R2, c[0x0][0x160]; IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2.64], R4; EXIT",
        Vec.tabulate(32)(lane => if (lane == 0) 22 else 11)
      )
    }

  for {
    sm        <- Vec(50, 60)
    condition <- ZeroTests
  }
    test(s"sm$sm LOP.AND.X.$condition combines low and high zero tests") {
      check(
        sm,
        "S2R R0, SR_TID.X; LOP.AND R4, R0, 1; LOP.AND R5, R0, 2; LOP.OR R5, R5, 0x10000; I2I.U32.U32 RZ.CC, R4; " +
          s"LOP.AND.X.$condition P0, R5, R5, 0xffff; SEL R4, RZ, 1, P0; LDC.64 R2, c[0x0][0x160]; IMAD.WIDE.U32 R2, R0, 4, R2; " +
          "STG.E [R2.64], R4; EXIT",
        Vec.tabulate(32)(lane => if (((lane & 3) == 0) == (condition == "Z")) 0 else 1)
      )
    }

  test("legacy IADD condition codes retain low-word ordering for extended bounds checks") {
    check(
      35,
      "S2R R0, SR_TID.X; IADD RZ.CC, -R0, 16; ISETP.GT.U32.X.AND P0, PT, RZ, RZ, PT; MOV R8, 1; SEL R4, R8, 0, P0; " +
        "LDC.64 R2, c[0x0][0x160]; IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2.64], R4; EXIT",
      Vec.tabulate(32)(lane => if (lane < 16) 1 else 0)
    )
  }

  for (sm <- Vec(35, 50, 60))
    test(s"BRA.CC.EQ observes the legacy floating zero flag on sm_$sm") {
      check(
        sm,
        "MOV R4, 0x7f800000; FSET.NEU.FTZ.AND RZ.CC, |R4|, |R4|, PT; BRA.CC.EQ `(one); MOV R6, 0; BRA `(store); one: MOV R6, 1; " +
          "store: LDC.64 R2, c[0x0][0x160]; STG.E [R2], R6; EXIT",
        Vec(1)
      )
    }

  test("predicate packing retains unselected bits and restores a selected byte") {
    check(
      86,
      "MOV R4, 0x55; R2P PR, R4, 0x7f; MOV R5, 0xa50000ff; P2R.B1 R5, R5, 0x7f; R2P.PR RZ, 0x7f; R2P PR, R5.reuse.B1, 0x7f; " +
        "P2R R6, R5, 0x7f; LDC.64 R2, c[0x0][0x160]; STG.E [R2.64], R6; EXIT",
      Vec(0xa50055d5)
    )
  }

  private def fset(sm: Int, text: String): Kernel = program(sm, text)

  test("only the observed sm35 unordered-greater RZ.CC form supplies a zero flag") {
    val supported = fset(35, "FSET.GTU.AND RZ.CC, |R3|, R4, PT")
    assert(supported.lower(8, 8, 4, 4).isRight)
    val unsupported = supported.map(_.rewrite(modifiers = Vec(Instruction.Modifier("GT"), Instruction.Modifier("AND"))))
    assert(unsupported.lower(8, 8, 4, 4).left.exists(_.contains("FSET condition-code output is not implemented")))
    assert(supported.copy(sm =
      86
    ).lower(8, 8, 4, 4).left.exists(_.contains("FSET condition-code output is not implemented")))
  }

  test("sm35 FSET.GTU.CC drives CC.NEU for numeric, unordered, predicate-masked, and overwritten flags") {
    def run(
        a: String,
        b: String,
        predicate: String,
        expected: Int,
        overwrite: Boolean = false,
        infinity: Boolean = false,
        sm: Int = 35,
        operation: String = "FSET.GTU.AND"
    ): Unit = {
      val text = s"MOV R0, $a; MOV R1, $b; ISETP.EQ.AND P0, PT, RZ, ${if (predicate == "PT") "RZ" else "1"}, PT; " +
        s"$operation RZ.CC, |R0|, |R1|, $predicate; ${if (overwrite) "FSET.GTU.AND RZ.CC, |R1|, |R0|, PT; " else ""}" +
        "BRA CC.NEU, `(yes); MOV R2, 0; BRA `(end); yes: MOV R2, 1; end: LDC.64 R4, c[0x0][0x160]; STG.E [R4.64], R2; EXIT"
      for (binary <- if (sm == 35) Vec(false, true) else Vec(false))
        check(
          sm,
          text,
          Vec(expected),
          rewrite = instruction =>
            if (binary && instruction.mnemonic.raw == "BRA" && instruction.operands.size == 2)
              InstructionDecoder.decode(
                instruction.address,
                LittleEndian.toVec(0x1200000020200034L, 8).toArray,
                target(35)
              ).value
                .rewrite(operands = Vec(instruction.operands.last), guard = None)
            else if (infinity && instruction.mnemonic.raw == "FSET")
              instruction.rewrite(operands =
                instruction.operands.updated(2, Operand.FloatingBits(FloatEncoding.F32Imm, 32, BigInt(0x7f800000)))
              )
            else instruction
        )
    }
    for (sm <- Vec(35, 50, 60)) {
      run("0x7fc00000", "0x7f800000", "PT", 1, infinity = true, sm = sm, operation = "FSET.NEU.FTZ.AND")
      run("0x7f800000", "0x7f800000", "PT", 0, infinity = true, sm = sm, operation = "FSET.NEU.FTZ.AND")
    }
    run("0x7fc00000", "0x7f800000", "PT", 1, infinity = true)
    run("0x7f800000", "0x7f800000", "PT", 0, infinity = true)
    run("0x40000000", "0x3f800000", "PT", 1)
    run("0x3f800000", "0x3f800000", "PT", 0)
    run("0x7fc00000", "0x3f800000", "PT", 1)
    run("0x3f800000", "0x7fc00000", "PT", 1)
    run("0x40000000", "0x3f800000", "P0", 0)
    run("0x40000000", "0x3f800000", "PT", 0, overwrite = true)
  }
}
