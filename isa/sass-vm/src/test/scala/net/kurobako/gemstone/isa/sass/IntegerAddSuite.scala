package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.either.*
import scala.util.Random

class IntegerAddSuite extends munit.FunSuite {
  private val Mask64     = (BigInt(1) << 64) - 1
  private val Constants  = Vec(0x00000000ffffffffL, 0x12345678ffffffffL, 0xffffffffffffffffL)
  private val Flags      = Vec(false, true)
  private val Predicates = Vec("P0", "!P0")

  private def sum(values: Long*): Long = (values.map(value => BigInt(value) & Mask64).sum & Mask64).toLong

  private def hasSelector(operand: Operand, selector: OperandSelector): Boolean = operand match {
    case Operand.Qualified(value, selectors) => selectors.contains(selector) || hasSelector(value, selector)
    case _                                   => false
  }

  @annotation.tailrec
  private def baseOperand(operand: Operand): Operand = operand match {
    case Operand.Qualified(value, _) => baseOperand(value)
    case other                       => other
  }

  private def guard(predicate: String): Option[Instruction.Guard] = Parser.parse(s"@$predicate NOP;").value.guard

  private def statement(text: String, address: Int): DecodedInstruction =
    Listing.program(text).value.instructions.head.copy(address = address.unsafe[Int >= 0])

  private def iadd3(name: String, sm: Int, function: String): Kernel =
    load(sm, Fixture.listing(s"iadd3$name$sm"), function)

  test("CUDA 11.8 sm_50/sm_60 IADD3.CC/X compiler pairs preserve carry counts zero, one, and two") {
    val cases = Vec(
      (0x0000000000000002L, 0x0000000000000004L, 0x0000000000000008L),
      (0x00000000ffffffffL, 0x0000000000000000L, 0x0000000000000001L),
      (0x00000000ffffffffL, 0x00000000ffffffffL, 0x00000000fffffffeL),
      (0x00000001ffffffffL, 0x00000001ffffffffL, 0x00000000fffffffeL),
      (0xffffffffffffffffL, 0xffffffffffffffffL, 0xfffffffffffffffeL)
    )
    val first    = words(cases.map(_._1), 8)
    val second   = words(cases.map(_._2), 8)
    val third    = words(cases.map(_._3), 8)
    val expected = cases.map(sum(_, _, _))
    for (sm <- Vec(50, 60)) {
      val run = iadd3("CarrySm", sm, "probe").launch(cases.size)(
        Arg.zeros(cases.size * 8),
        Arg.Buffer(first),
        Arg.Buffer(second),
        Arg.Buffer(third)
      )
      assertEquals(run.words(argument = 0, width = 8), expected, s"sm_$sm")
    }
  }

  test("CUDA 11.8 sm_50/sm_60 IADD3.CC/X pairs accept parameter constants in both halves") {
    val cases = Vec(
      0x0000000000000000L -> 0x0000000000000000L,
      0x00000000ffffffffL -> 0x0000000000000000L,
      0x00000000ffffffffL -> 0x00000000ffffffffL,
      0x0000000100000000L -> 0x00000000ffffffffL,
      0xffffffffffffffffL -> 0x0000000000000001L
    )
    val first  = words(cases.map(_._1), 8)
    val second = words(cases.map(_._2), 8)
    for {
      sm       <- Vec(50, 60)
      constant <- Constants
    } {
      val run = iadd3("ConstantSm", sm, "probe_constant").launch(cases.size)(
        Arg.zeros(cases.size * 8),
        Arg.Buffer(first),
        Arg.Buffer(second),
        Arg.u64(constant)
      )
      assertEquals(
        run.words(argument = 0, width = 8),
        cases.map(sum(_, _, constant)),
        s"sm_$sm constant ${constant.toHexString}"
      )
    }
  }

  test("CUDA 11.8 sm_50/sm_60 IADD3 carry survives an unrelated scheduled store") {
    val low     = 0x00000000ffffffffL
    val triples = Vec(
      (0L, 0L, 0L),
      (low, 0L, 0L),
      (low, low, 0L),
      (low, low, low),
      (0x00000001ffffffffL, 1L, low),
      (0x12345678ffffffffL, 0x87654321ffffffffL, 0xfedcba98ffffffffL),
      (0x7fffffffffffffffL, 0x0000000000000001L, 0x8000000000000000L),
      (0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL),
      (0x01020304aabbccddL, 0x10203040ddeeff01L, 0x5566778899aabbccL),
      (0x80000000ffffffffL, 0x7fffffff00000001L, 0x00000000ffffffffL),
      (0x00000000ffffffffL, 0x0000000000000001L, 0x0000000000000001L),
      (0xaaaaaaaaaaaaaaaaL, 0x5555555555555555L, 1L),
      (0x1111111122222222L, 0x3333333344444444L, 0x5555555566666666L),
      (0x00000000ffffffffL, 0x00000000ffffffffL, 0x0000000000000001L),
      (0xdeadbeefffffffffL, 0x10203040ffffffffL, 0x99887766ffffffffL),
      (0x0000000000000001L, 0x0000000000000002L, 0x0000000000000003L)
    )
    val first    = words(triples.map(_._1), 8)
    val second   = words(triples.map(_._2), 8)
    val third    = words(triples.map(_._3), 8)
    val expected = triples.map(sum(_, _, _))
    for (sm <- Vec(50, 60)) {
      val run = iadd3("MultipairSm", sm, "probe_multi").launch(4)(
        Arg.zeros(triples.size * 8),
        Arg.Buffer(first),
        Arg.Buffer(second),
        Arg.Buffer(third)
      )
      assertEquals(run.words(argument = 0, width = 8), expected, s"sm_$sm")
    }
  }

  test("guarded IADD3 carry crosses a branch, and a missing or skipped producer faults") {
    val kernel       = iadd3("CarrySm", 50, "probe")
    val instructions = kernel.function.instructions
    val index        = instructions.indexWhere(i =>
      i.mnemonic.raw == "IADD3" && i.operands.headOption.exists(hasSelector(_, OperandSelector.CC))
    )
    val producer = instructions(index)
    val consumer = instructions.find(i => i.mnemonic.raw == "IADD3" && i.modifiers.contains(Instruction.Modifier.X)).get
    val branch   = statement(s"BRA 0x${consumer.address.raw.toHexString}", producer.address.raw + 1)
    def variant(f: Vec[DecodedInstruction] => Vec[DecodedInstruction]): Kernel =
      kernel.copy(functions = Vec(kernel.function.copy(instructions = f(instructions))))
    def withGuard(text: String): Kernel =
      variant(_.updated(index, producer.rewrite(guard = guard(text))).patch(index + 1, Vec(branch), 0))
    val values                 = Vec(0x00000000ffffffffL, 1L, 0x00000000ffffffffL)
    def run(candidate: Kernel) =
      candidate.run(1)(Arg.zeros(8) +: values.map(value => Arg.Buffer(words(Vec(value), 8)))*)
    assertEquals(run(withGuard("PT")).value.words(argument = 0, width = 8), Vec(sum(values*)))
    assert(run(variant(_.filterNot(_.address == producer.address).patch(index, Vec(branch), 0))).isLeft)
    assert(run(withGuard("!PT")).isLeft)
  }

  private val wideValues = Vec(
    0L,
    1L,
    0xfffffffeL,
    0xffffffffL,
    0x100000000L,
    0x1ffffffffL,
    0x7fffffffffffffffL,
    Long.MinValue,
    0xfffffffffffffffeL,
    -1L
  )
  private val narrowValues = Vec(0, 1, 0x3eff, 0x3f00, 0x3f01, 0x7fffffff, Int.MinValue, -2, -1)

  test("CUDA 11.8 sm_50/sm_60 negative-source IADD3 pairs implement full 64-bit expressions") {
    def pairs[A, B](as: Vec[A], bs: Vec[B]): Vec[(A, B)] = for {
      a <- as
      b <- bs
    } yield (a, b)
    val constantCases = pairs(wideValues, narrowValues) ++ {
      val random = Random(0x51a7L); Vec.fill(256)((random.nextLong(), random.nextInt()))
    }
    val negativeCases = pairs(wideValues, wideValues) ++ {
      val random = Random(0x51a9L); Vec.fill(256)((random.nextLong(), random.nextLong()))
    }
    val subtractCases =
      (for {
        a <- wideValues
        b <- wideValues
        c <- wideValues
      } yield (a, b, c)) ++
        { val random = Random(0x51a8L); Vec.fill(256)((random.nextLong(), random.nextLong(), random.nextLong())) }
    def u32(value: Int): Long = value.toLong & 0xffffffffL
    def execute(sm: Int, function: String, name: String, inputs: Vec[Vec[Byte]], expected: Vec[Long]): Unit = {
      val kernel = load(sm, Fixture.listing(s"iadd3${name}Sm$sm"), function)
        .copy(constants = Vec(".nv.constant2.add_large_constant_sub" -> Vec[Byte](0, -63, 15, 0)))
      val pair = kernel.function.instructions.filter(_.mnemonic.raw == "IADD3")
      assertEquals(
        pair.map(_.modifiers),
        Vec(Vec.empty[Instruction.Modifier], Vec(Instruction.Modifier.X)),
        s"sm_$sm $function"
      )
      assert(hasSelector(pair.head.operands.head, OperandSelector.CC))
      def operands(text: String): Vec[Operand] = OperandParser.parseMany(text).value
      function match {
        case "negative_constant_sub" =>
          assertEquals(pair.map(_.operands.drop(1)), Vec(operands("-R4, 0xfc100, R2"), operands("-R5, 0xfffff, R3")))
        case "add_constant_sub"       => assertEquals(pair.head.operands(2), operands("0x3f00").head)
        case "add_large_constant_sub" => assertEquals(pair.head.operands(2), operands("c[0x2][0x0]").head)
        case _                        => ()
      }
      val run = kernel.launch(1, expected.size)((inputs.map(Arg.Buffer(_)) :+ Arg.zeros(expected.size * 8))*)
      assertEquals(run.words(inputs.size, 8), expected, s"sm_$sm $function")
    }
    val wide             = words(constantCases.map(_._1), 8)
    val narrow           = words(constantCases.map(c => u32(c._2)))
    val constantInputs   = Vec(wide, narrow)
    val addConstant      = constantCases.map((a, b) => sum(a, 0x3f00L, -u32(b)))
    val addLargeConstant = constantCases.map((a, b) => sum(a, 0xfc100L, -u32(b)))
    val negativeInputs   = Vec(words(negativeCases.map(_._1), 8), words(negativeCases.map(_._2), 8))
    val negativeExpected =
      negativeCases.map((a, b) => (((BigInt(a) & Mask64) - 0x3f00 - (BigInt(b) & Mask64)) & Mask64).toLong)
    val subtractInputs =
      Vec(words(subtractCases.map(_._1), 8), words(subtractCases.map(_._2), 8), words(subtractCases.map(_._3), 8))
    val subtractExpected = subtractCases.map((a, b, c) =>
      (((BigInt(a) & Mask64) + (BigInt(b) & Mask64) - (BigInt(c) & Mask64)) & Mask64).toLong
    )
    for (sm <- Vec(50, 60)) {
      execute(sm, "add_constant_sub", "AddConstantSub", constantInputs, addConstant)
      execute(sm, "add_large_constant_sub", "AddLargeConstantSub", constantInputs, addLargeConstant)
      execute(sm, "negative_constant_sub", "NegativeConstantSub", negativeInputs, negativeExpected)
      execute(sm, "add_sub", "AddSub", subtractInputs, subtractExpected)
    }
  }

  for (enabled <- Vec(None, Some(true), Some(false)))
    test(s"CUDA 11.8 sm_50/sm_60 IADD3.RS full products and register preservation, guard=$enabled") {
      // products force nonzero cross terms and overflow in the 32-bit sum of partials
      val inputs = Vec(
        0x00000000L -> 0xffffffffL,
        0xffffffffL -> 0xffffffffL,
        0x80000000L -> 0x80000000L,
        0xffffffffL -> 0x80000001L,
        0x12345678L -> 0x9abcdef0L,
        0x00010001L -> 0x00010001L,
        0x0000ffffL -> 0xffff0001L,
        0x7fffffffL -> 0xdeadbeefL
      )
      for (sm <- Vec(50, 60)) {
        val original     = load(sm, Fixture.listing(s"iadd3MulwideSm$sm"), "mulwide")
        val instructions = original.function.instructions
        val index        =
          instructions.indexWhere(i => i.mnemonic.raw == "IADD3" && i.modifiers.contains(Instruction.Modifier.RS))
        val rs       = instructions(index)
        val r7       = OperandParser.parse("R7").value
        val previous = instructions.take(index).findLast(_.operands.headOption.exists(baseOperand(_) == r7)).get
        assertEquals(previous.mnemonic.raw, "XMAD")
        assertEquals(previous.operands.drop(1).map(baseOperand(_)), OperandParser.parseMany("R2, R4, RZ").value)
        val kernel = enabled.fold(original) { execute =>
          val set = statement(s"ISETP.${if (execute) "EQ" else "NE"}.AND P6, PT, RZ, RZ, PT", rs.address.raw - 1)
          original.copy(functions =
            Vec(original.function.copy(instructions =
              instructions.patch(index, Vec(set, rs.rewrite(guard = guard("P6"))), 1)
            ))
          )
        }
        inputs.foreach { (left, right) =>
          val actual = kernel.launch(1)(Arg.zeros(8), Arg.words(left), Arg.words(right), Arg.u32(1)).words(
            argument = 0,
            width = 8
          ).head
          // an inactive RS preserves R7, whose last assignment is the low-half product
          val expected =
            if (enabled.contains(false)) ((left * right) & 0xffffffffL) | (((left & 65535L) * (right & 65535L)) << 32)
            else (BigInt(left) * BigInt(right)).toLong
          assertEquals(actual, expected, s"sm_$sm ${left.toHexString} * ${right.toHexString}")
        }
      }
    }

  for {
    sm                  <- Vec(50, 60)
    (immediate, signed) <-
      Vec("0x7ffff" -> 524287, "0x80000" -> -524288, "0xffaff" -> -1281, "-0x501" -> -1281, "0xfffff" -> -1)
  }
    test(s"sm$sm plain IADD3 sign-extends $immediate and retains an aliased addend") {
      check(
        sm,
        s"S2R R0, SR_TID.X; MOV R4, 0x501; IADD3 R4, -R0, $immediate, R4; LDC.64 R2, c[0x0][0x160]; IMAD.WIDE.U32 R2, R0, 4, R2; " +
          "STG.E [R2.64], R4; EXIT",
        Vec.tabulate(32)(lane => 1281 + signed - lane)
      )
    }

  for {
    sm                           <- Vec(50, 60)
    (opcode, operands, expected) <- Vec(
      (
        "IADD3.LS",
        "R4, R4.H1.reuse, R5.H0, R6.H0",
        (input: Long) => ((((input >>> 16) + 65535L) << 16) + 0xabcdL).toInt
      ),
      ("IADD3.RS", "R4, R4.H1, R5, R6.H0", (input: Long) => ((((input >>> 16) + 0x1234ffffL) >>> 16) + 0xabcdL).toInt),
      ("VADD.U16.U16.MRG_16H", "R4, R4, R5, R6", (input: Long) => ((((input & 65535L) + 65535L) << 16) | 0xabcdL).toInt)
    )
  } test(s"sm$sm $opcode selects and merges halfwords across carry boundaries") {
    check(
      sm,
      s"S2R R0, SR_TID.X; IADD32I R4, R0, 0xfffffff0; MOV R5, 0x1234ffff; MOV R6, 0x8765abcd; $opcode $operands; " +
        "LDC.64 R2, c[0x0][0x160]; IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2.64], R4; EXIT",
      Vec.tabulate(32)(lane => expected((0xfffffff0L + lane) & 0xffffffffL))
    )
  }

  for (
    (left, right, carried) <- Vec(
      ("R0", "0xfffffff0", (lane: Int) => lane >= 16),
      ("R9", "-R0", (lane: Int) => lane <= 16),
      ("-R0", "16", (lane: Int) => lane <= 16),
      ("RZ", "-R0", (lane: Int) => lane == 0)
    )
  ) test(s"Blackwell IADD explicit carry for $left plus $right") {
    check(
      120,
      s"S2R R0, SR_TID.X; MOV R9, 16; IADD R8, P0, $left, $right; MOV R8, 1; SEL R4, R8, 0, P0; LDC.64 R2, c[0x0][0x160]; " +
        "IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2.64], R4; EXIT",
      Vec.tabulate(32)(lane => if (carried(lane)) 1 else 0)
    )
  }

  for {
    predicate  <- Predicates
    complement <- Flags
  }
    test(s"Blackwell IADD.X reads $predicate independently of hidden carry, complement=$complement") {
      check(
        120,
        "S2R R0, SR_TID.X; IADD R8, P0, R0, 0xfffffff0; IADD R8, P1, RZ, RZ; MOV R4, R0; " +
          s"IADD.X R4, ${if (complement) s"RZ, ~R4, $predicate" else s"R4, 10, $predicate"}; LDC.64 R2, c[0x0][0x160]; " +
          "IMAD.WIDE.U32 R2, R0, 4, R2; STG.E [R2.64], R4; EXIT",
        Vec.tabulate(32) { lane =>
          val carry = (lane >= 16) != (predicate == "!P0")
          (if (complement) ~lane else lane + 10) + (if (carry) 1 else 0)
        }
      )
    }

  private val arithmetic = load(35, Fixture.listing("arithmeticSm35"), "arith_probe")

  test("CUDA 11.8 sm_35 64-bit add/sub and signed/unsigned right shifts match arithmetic semantics") {
    Vec(
      (0L, 0L, 0),
      (0x00000000ffffffffL, 1L, 1),
      (Long.MaxValue, 1L, 1),
      (Long.MinValue, -1L, 31),
      (-1L, Long.MinValue, 32),
      (0x0123456789abcdefL, 0xfedcba9876543210L, 63),
      (0x80000000ffffffffL, 0x7fffffff00000001L, 17)
    ).foreach { (a, b, shift) =>
      assertEquals(
        arithmetic.launch(1)(Arg.zeros(64), Arg.u64(a), Arg.u64(b), Arg.u32(shift)).words(argument = 0, width = 8),
        Vec(a + b, a - b, a >> shift, a >>> shift, b >> shift, b >>> shift, -a, -b)
      )
    }
  }

  private def scalarProbe(text: String): Kernel = program(35, text, base = 0x140)

  test("legacy paired IADD with negated sources carries correctly across 64-bit words") {
    val kernel = scalarProbe(
      """LDC.64 R4, c[0x0][0x140]
        |MOV R17, c[0x0][0x148]
        |MOV R15, c[0x0][0x14c]
        |MOV R18, c[0x0][0x150]
        |MOV R19, c[0x0][0x154]
        |IADD R13.CC, -R18, R17
        |IADD.X R16, -R19, R15
        |ST.E [R4], R13
        |ST.E [R4+0x4], R16
        |EXIT""".stripMargin
    )
    Vec(
      (0L, 0L),
      (0L, 1L),
      (0x0000000100000000L, 1L),
      (0x0000000100000000L, 0x0000000100000000L),
      (0x0000000100000000L, 0x0000000100000001L),
      (0x8000000000000000L, 1L),
      (-1L, Long.MinValue),
      (Long.MinValue, -1L)
    ).foreach { (a, b) =>
      assertEquals(
        kernel.launch(1)(Arg.zeros(8), Arg.u64(a), Arg.u64(b), Arg.u32(0)).words(argument = 0, width = 8),
        Vec(a - b)
      )
    }
  }

  test("sm_35 ISUB.X consumes the no-borrow carry from a negated IADD.CC") {
    val kernel = scalarProbe(
      """LDC.64 R4, c[0x0][0x140]
        |MOV R10, c[0x0][0x148]
        |MOV R11, c[0x0][0x150]
        |MOV R12, c[0x0][0x158]
        |IADD R13.CC, -R12, R10
        |ISUB.X R14, R11, R10
        |ST.E [R4], R13
        |ST.E [R4+0x4], R14
        |EXIT""".stripMargin
    )
    Vec(
      (0L, 0L, 0L),
      (0L, 0L, 1L),
      (1L, 0L, 0L),
      (0L, 1L, 0L),
      (0xffffffffL, 0L, 0xffffffffL),
      (0x7fffffffL, 0x80000000L, 0x80000001L)
    )
      .foreach { (a, b, c) =>
        assertEquals(
          kernel.launch(1)(Arg.zeros(8), Arg.u64(b), Arg.u64(c), Arg.u32(a)).words(0),
          Vec((b - a) & 0xffffffffL, (c - b - (if (b < a) 1L else 0L)) & 0xffffffffL),
          s"a=$a b=$b c=$c"
        )
      }
  }

  test("IADD32I adds a negated register and a negative immediate with wrapping arithmetic") {
    check(
      50,
      "LDC.64 R10, c[0x0][0x160]; MOV R4, 0x7fffffff; IADD32I R4, -R4, -257; STG.E [R10], R4; EXIT",
      Vec(0x7fffff00)
    )
  }

  test("Hopper VIADD wraps 32-bit sums with aliased sources and respects predicates") {
    check(
      90,
      """LDC.64 R2, c[0x0][0x160]
        |MOV R4, 0xffffff80
        |VIADD R4, R4, 0x100
        |MOV R5, 0x7fffffff
        |VIADD R5, R4, R5
        |VIADD R5, R5, -R4
        |ISETP.EQ.AND P0, PT, RZ, 1, PT
        |@P0 VIADD R5, R5, 1
        |@!P0 VIADD R5, R5, 1
        |STG.E [R2.64], R5
        |EXIT""".stripMargin,
      Vec(Int.MinValue)
    )
  }

  test("UIADD3.64 reads aliased uniform pairs before storing its result") {
    check(
      120,
      """LDC.64 R10, c[0x0][0x160]
        |UMOV UR4, 1
        |UMOV UR5, 2
        |UMOV UR6, 0
        |UMOV UR7, 4
        |UIADD3.64 UR6, -UR4, UR6, URZ
        |MOV.64 R4, UR6
        |IADD.64 R4, R4, UR4
        |STG.E.64 [R10], R4
        |EXIT""".stripMargin,
      Vec(0, 4)
    )
  }

  test("funnel shifts, two carry outputs and extended ordered comparison") {
    check(
      86,
      """MOV R0, 0xffffffff
        |IADD3 R4, P0, P1, R0, 1, R0
        |IADD3.X R5, RZ, RZ, RZ, P0, P1
        |SHF.R.U64 R6, RZ, 31, R5
        |ISETP.GT.U32.AND P0, PT, R4, 0, PT
        |ISETP.GT.U32.AND.EX P0, PT, R5, 1, PT, P0
        |SEL R6, R6, 0, P0
        |LDC.64 R2, c[0x0][0x160]
        |STG.E [R2.64], R6
        |EXIT""".stripMargin,
      Vec(2)
    )
  }

  // clang's predicates kernel in conformance/unified/src/cuda/integer_forms.cu adds a flag through R9.H0 on sm_50
  for (sm <- Vec(50, 60)) test(s"sm_$sm IADD3 zero-extends the half each source selects") {
    check(
      sm,
      "MOV32I R5, 0x10; MOV32I R6, 0xfffe0003; MOV32I R7, 0x8001ffff; IADD3 R4, R5, R6.H1, R7.H0; " +
        "MOV R2, c[0x0][0x160]; MOV R3, c[0x0][0x164]; STG.E [R2], R4; EXIT",
      Vec(0x10 + 0xfffe + 0xffff)
    )
  }
}
