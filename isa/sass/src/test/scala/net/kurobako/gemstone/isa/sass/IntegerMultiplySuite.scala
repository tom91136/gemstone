package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.either.*

class IntegerMultiplySuite extends munit.FunSuite {
  private def u32(value: Long): Long                 = value & 0xffffffffL
  private def half(value: Long, high: Boolean): Long = (value >>> (if (high) 16 else 0)) & 0xffffL
  private def signed16(value: Long): Long            = if ((value & 0x8000L) != 0L) value - 0x10000L else value

  private def opcodes(kernel: Kernel): Set[String] =
    kernel.function.instructions.map(i => (i.mnemonic.raw +: i.modifiers.map(_.name)).mkString(".")).toSet

  private val xmadText = """MOV R2, c[0x0][0x148]
    |MOV R3, c[0x0][0x14c]
    |MOV R4, c[0x0][0x140]
    |MOV R5, c[0x0][0x144]
    |LDG R0, [R2]
    |LDG R6, [R2+0x4]
    |LDG R7, [R2+0x8]
    |XMAD R8, R0.H0.reuse, R6.H0, R7.reuse
    |STG [R4], R8
    |XMAD.PSL R8, R0.H1.reuse, R6.H0, R7
    |STG [R4+0x4], R8
    |XMAD.PSL.CLO R8, R0.H1, 0x1100, R7
    |STG [R4+0x8], R8
    |XMAD.PSL.CBCC R8, R0.H1.reuse, R6.H1, R7
    |STG [R4+0xc], R8
    |XMAD.CHI R8, R0.H1.reuse, 0xab, R7
    |STG [R4+0x10], R8
    |XMAD.MRG R8, R0.H1.reuse, R6.H1.reuse, R7
    |STG [R4+0x14], R8
    |XMAD.S16.U16 R8, R0.H1, R6.H0, R7
    |STG [R4+0x18], R8
    |XMAD.U16.S16 R8, R0.H0, R6.H1, R7
    |STG [R4+0x1c], R8
    |XMAD.PSL.CBCC R7, R0.H1, R6.H1, R7
    |STG [R4+0x20], R7
    |XMAD.MRG R8, R0.H1, c[0x0][0x8].H1, R7
    |STG [R4+0x24], R8
    |XMAD.S16.S16.CSFU R8, R0.H1, R6.H1, RZ
    |STG [R4+0x28], R8
    |XMAD.MRG R8, R0.reuse, RZ.H1, RZ
    |STG [R4+0x2c], R8
    |XMAD.MRG R8, R0.H1, RZ, R7
    |STG [R4+0x30], R8
    |EXIT""".stripMargin

  // the aliased CBCC must read C before its write; c[0x0][0x8] is ntid.x, whose H1 half is zero
  private def xmadExpected(a: Long, b: Long, c: Long): Vec[Long] = {
    val (al, ah, bl, bh) = (half(a, false), half(a, true), half(b, false), half(b, true))
    val csfu             = {
      val (sah, sbh) = (BigInt(signed16(ah)), BigInt(signed16(bh)))
      val correction = BigInt(65536) * ((if (sah < 0 && bl != 0) 1 else 0) + (if (sbh < 0 && al != 0) 1 else 0))
      ((sah * sbh - correction) & 0xffffffffL).toLong
    }
    Vec(
      u32(al * bl + c),
      u32((ah * bl << 16) + c),
      u32((ah * 0x1100L << 16) + (c & 0xffffL)),
      u32((ah * bh << 16) + c + (b << 16)),
      u32(ah * 0xabL + (c >>> 16)),
      u32((u32(ah * bh + c) & 0xffffL) | (b << 16)),
      u32(signed16(ah) * bl + c),
      u32(al * signed16(bh) + c),
      u32((ah * bh << 16) + c + (b << 16)),
      u32((c & 0xffffL) | (1L << 16)),
      csfu,
      0L,
      c & 0xffffL
    )
  }

  test("CUDA 11.8 compiler fixtures cover Maxwell/Pascal XMAD families; VM arithmetic matches independent formulas") {
    val kernel = program(50, xmadText, base = 0x140)
    for (sm <- Vec(50, 60)) {
      assert(Set(
        "XMAD.MRG",
        "XMAD.CHI",
        "XMAD.PSL.CBCC",
        "XMAD.S16.U16"
      ).subsetOf(opcodes(load(sm, Fixture.listing("xmadCompilerFormsSm50"), "xmad_patterns"))))
      Vec(
        (0x8001ffffL, 0xffff8001L, 0xffff0003L),
        (0x1234abcdL, 0x5678fedcL, 0x76543210L),
        (0x00010001L, 0x00020003L, 0x0000ffffL),
        (0x0000ffffL, 0x80000001L, 0x76543210L),
        (0xffff1234L, 0x0000ffffL, 0x80000000L),
        (0x80018001L, 0x0001ffffL, 0x00000000L),
        (0xffff0000L, 0x00010000L, 0x00000000L),
        (0xffffffffL, 0x00000001L, 0x00000000L)
      ).foreach { (a, b, c) =>
        assertEquals(
          kernel.copy(sm = sm).launch(1)(Arg.zeros(52), Arg.words(a, b, c)).words(0),
          xmadExpected(a, b, c),
          s"sm_$sm a=$a b=$b c=$c"
        )
      }
    }
  }

  test("XMAD rejects unsupported modifiers and condition-code modifier combinations") {
    val kernel = program(50, xmadText, base = 0x140)
    val target = kernel.function.instructions(7)
    Vec("XMAD.SAT R8, R0, R6, R7", "XMAD.PSL R8.CC, R0, R6, R7").foreach { form =>
      val replacement = Listing.program(form).value.instructions.head.copy(address = target.address)
      assert(kernel.map(i => if (i eq target) replacement else i).lower(8, 8).isLeft, form)
    }
  }

  test("CUDA 11.8 signed mul.hi.s32 compiler chains match independent BigInt arithmetic") {
    val boundaries = Vec(0L, 1L, 0x7fffL, 0x8000L, 0xffffL, 0x10000L, 0x7fff0000L, 0x7fffffffL, 0x80000000L,
      0x80000001L, 0xffff0000L, 0xffffffffL)
    val cases =
      (for {
        a <- boundaries
        b <- boundaries
      } yield (a, b)) ++ Vec(
        (0x80000000L, 0x80000000L),
        (0x7fffffffL, 0xffffffffL),
        (0xffff0001L, 0x0001ffffL),
        (0x12345678L, 0x87654321L),
        (0xffffffffL, 0xffffffffL),
        (0x00000000L, 0x80000000L),
        (0xffff0000L, 0x00010000L),
        (0xffffffffL, 0x00000001L)
      )
    for (sm <- Vec(50, 60)) {
      val kernel = load(sm, Fixture.listing("xmadSignedHiSm50"), "signed_hi")
      assert(Set("XMAD.S16.S16.CSFU", "IADD3.RS").subsetOf(opcodes(kernel)))
      cases.foreach { (a, b) =>
        assertEquals(
          kernel.launch(1)(Arg.zeros(4), Arg.words(a, b)).words(0),
          Vec((((BigInt(a.toInt) * BigInt(b.toInt)) >> 32) & 0xffffffffL).toLong),
          s"sm_$sm a=$a b=$b"
        )
      }
    }
  }

  test("CUDA 11.8 compiled PTX multiply-add chains match their source arithmetic in the VM") {
    for {
      sm        <- Vec(50, 60)
      (a, b, c) <- Vec(
        (0x8001ffffL, 0xffff8001L, 0xffff0003L),
        (0x1234abcdL, 0x5678fedcL, 0x76543210L),
        (0x00010001L, 0x00020003L, 0x0000ffffL)
      )
    } {
      val listing = if (sm == 50) "xmadArithmeticSm50" else "xmadCompilerFormsSm50"
      val product = half(a, false) * half(b, false)
      assertEquals(
        load(sm, Fixture.listing(listing), "xmad_patterns").launch(1)(Arg.zeros(28), Arg.words(a, b, c)).words(0),
        Vec(
          u32(product + c),
          u32((product << 16) + c),
          u32((product << 16) + (c & 0xffffL)),
          u32((product << 16) + c + (b << 16)),
          u32(product + (c >>> 16)),
          u32((product + c) & 0xffffL) | u32(b << 16),
          u32(signed16(half(a, false)) * half(b, false) + c)
        ),
        s"sm_$sm"
      )
    }
  }

  for (
    (comparison, holds) <- Vec[(String, Int => Boolean)](
      "GT" -> (16 > _),
      "GE" -> (16 >= _),
      "LT" -> (16 < _),
      "LE" -> (16 <= _),
      "EQ" -> (16 == _),
      "NE" -> (16 != _)
    )
  )
    test(s"Kepler IMAD with a negated addend feeds extended $comparison bounds checks") {
      check(
        35,
        s"S2R R0, SR_TID.X; MOV R4, 4; IMAD.U32.U32 RZ.CC, R4, 4, -R0; ISETP.$comparison.U32.X.AND P0, PT, RZ, RZ, PT; MOV R8, 1; " +
          "SEL R4, R8, 0, P0; LDC.64 R2, c[0x0][0x160]; IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2.64], R4; EXIT",
        Vec.tabulate(32)(lane => if (holds(lane)) 1 else 0)
      )
    }

  for {
    sm        <- Vec(35, 50)
    immediate <- Vec(-1, -2, -17)
  }
    test(s"sm$sm extended addition preserves negative immediate $immediate") {
      check(
        sm,
        s"S2R R0, SR_TID.X; IADD32I R4.CC, -R0, 16; IADD32I.X R4, RZ, $immediate; LDC.64 R2, c[0x0][0x160]; " +
          "IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2.64], R4; EXIT",
        Vec.tabulate(32)(lane => immediate + (if (lane <= 16) 1 else 0))
      )
    }

  for {
    sm     <- Vec(50, 60)
    addend <- Vec(0L, 1L, 0xfffffff0L)
  }
    test(s"sm$sm XMAD condition codes carry into the next word with addend $addend") {
      check(
        sm,
        s"S2R R0, SR_TID.X; MOV R4, $addend; MOV R5, 65535; XMAD R6.CC, R0, R5, R4; IADD.X R4, RZ, RZ; LDC.64 R2, c[0x0][0x160]; " +
          "IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2.64], R4; EXIT",
        Vec.tabulate(32)(lane => ((lane * 65535L + addend) >>> 32).toInt)
      )
    }

  for (sm <- Vec(50, 60))
    test(s"sm$sm XMAD.X consumes carry without losing the selected halfword") {
      check(
        sm,
        "S2R R0, SR_TID.X; MOV R5, 0x30007; MOV R4, 11; IADD32I R6.CC, R0, 0xfffffff0; XMAD.X R4, R5.H1, 7, R4; " +
          "LDC.64 R2, c[0x0][0x160]; IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2.64], R4; EXIT",
        Vec.tabulate(32)(lane => 32 + (if (lane >= 16) 1 else 0))
      )
    }

  test("decoded XMAD retains the unsigned first operand when the second operand is signed") {
    check(
      50,
      "LDC.64 R10, c[0x0][0x160]; MOV R0, 0x8000; MOV R3, 0xfffe0000; ISETP.EQ.AND P1, PT, RZ, RZ, PT; @P1 XMAD.U16.S16 R3, R0, R3.H1, RZ; " +
        "STG.E [R10], R3; EXIT",
      Vec(-65536),
      rewrite = instruction =>
        if (instruction.mnemonic.raw != "XMAD") instruction
        else {
          val bytes = LittleEndian.toVec(0x5b027f8800310003L, 8).toArray ++ Array[Byte](0xfd.toByte, 7, 0)
          InstructionDecoder.decode(instruction.address, bytes, target(50)).value
        }
    )
  }

  test("wide signed and unsigned IMAD preserve negative 32-bit operands") {
    for ((opcode, expected) <- Vec("IMAD.WIDE" -> Vec(-2, -1), "IMAD.WIDE.U32" -> Vec(-2, 1)))
      check(
        89,
        s"MOV R0, 0xffffffff; MOV R1, 2; $opcode R2, R0, R1, RZ; LDC.64 R4, c[0x0][0x160]; STG.E.64 [R4.64], R2; EXIT",
        expected
      )
  }

  test("CUDA 11.8 sm_35 IMAD low/high subtracts a 64-bit addend with borrow") {
    val kernel = load(35, Fixture.listing("multiplySubtractSm35"), "subtract_product")
    Vec(
      (0L, 0L, 0L),
      (0xffffffffL, 0xffffffffL, 0L),
      (0xffffffffL, 0xffffffffL, 1L),
      (0x10000L, 0x10000L, 0L),
      (0xffffffffL, 2L, 0x100000000L),
      (0xffffL, 0xffffL, 0xfffe0001L),
      (0xffffL, 0xffffL, 0xfffe0002L),
      (0xffffL, 0xffffL, 0x1fffe0001L),
      (0x80000000L, 0xffffffffL, 0x7fffffffffffffffL)
    )
      .foreach { (a, b, c) =>
        assertEquals(kernel.launch(1)(Arg.zeros(8), Arg.u32(a), Arg.u32(b), Arg.u64(c)).words(0, 8), Vec(a * b - c))
      }
  }

  test("compiler-produced sm_89 signed division and remainder cover boundary values") {
    val kernel = load(89, Fixture.listing("divideSm89"), "divide_probe")
    Vec(Int.MinValue -> 3, Int.MaxValue -> 3, -7 -> 3, 7 -> -3, -2147483647 -> -1, 0 -> Int.MinValue).foreach { (a, b) =>
      assertEquals(
        kernel.launch(1)(Arg.zeros(8), Arg.u32(a), Arg.u32(b)).words(0).map(_.toInt),
        Vec(a / b, a % b),
        s"$a / $b"
      )
    }
  }

  test("sm_35 SHF.L.U64 and IMUL32I preserve aliased sources across boundary shifts") {
    // the wide source is {R1,R0}; both destination/source aliases are exercised
    val kernel = program(
      35,
      """LDC.64 R4, c[0x0][0x140]
        |MOV R0, c[0x0][0x148]
        |MOV R1, c[0x0][0x150]
        |MOV R2, c[0x0][0x158]
        |SHF.L.U64 R0, R0, R2, R1
        |ST.E [R4], R0
        |MOV R0, c[0x0][0x148]
        |SHF.L.U64 R1, R0, R2, R1
        |ST.E [R4+0x4], R1
        |IMUL32I R0, R0, 0x500
        |ST.E [R4+0x8], R0
        |MOV R1, c[0x0][0x148]
        |IMUL32I.U32.HI R1, R1, 0x500
        |ST.E [R4+0xc], R1
        |EXIT""".stripMargin,
      base = 0x140
    )
    for {
      word  <- Vec(0x00000000L, 0xffffffffL, 0x80000000L, 0x81234567L)
      shift <- Vec(0, 1, 31, 32, 33, 63, 64, 79)
    } {
      val high    = (~word) & 0xffffffffL
      val shifted = if (shift >= 64) 0L else ((high << 32 | word) << shift) >>> 32
      assertEquals(
        kernel.launch(1)(Arg.zeros(16), Arg.u64(word), Arg.u64(high), Arg.u32(shift)).words(0),
        Vec(shifted, shifted, (word * 0x500L) & 0xffffffffL, ((word * 0x500L) >>> 32) & 0xffffffffL),
        s"word=0x${word.toHexString}, shift=$shift"
      )
    }
  }
}
