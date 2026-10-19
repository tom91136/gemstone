package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction as VMInstruction
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.either.*
import scala.util.Random

class LeaSuite extends munit.FunSuite {
  private def is(instruction: DecodedInstruction, mnemonic: String, modifiers: Instruction.Modifier*): Boolean =
    instruction.mnemonic.raw == mnemonic && instruction.modifiers == Vec.from(modifiers)

  private def register(index: Int): Operand = Operand.Register(RegisterBank.R, index.refine[Int in 0 ~ 255].value)

  private def predicate(index: Option[Int], negated: Boolean = false): Operand.Predicate =
    Operand.Predicate(index.map(_.refine[Int in 0 ~ 6].value), uniform = false, negated = negated)

  private def statement(text: String, address: Int): DecodedInstruction =
    Listing.program(text).value.instructions.head.copy(address = address.unsafe[Int >= 0])

  private val carryDestination = Operand.Qualified(register(0), Vec(OperandSelector.CC))

  private val Shifts             = Vec(0, 1, 2, 3, 31)
  private val Flags              = Vec(false, true)
  private val FalseOnly          = Vec(false)
  private val DivisionForms      = Vec(("s5", 32), ("s6", 64), ("s24", 16777216))
  private val ImmediateEncodings = Vec(0x80000, 0xfc100, 0xfffff, 0x100000)
  private val ScaledIndexKernels =
    Vec(("index_scaled7_minus_base", 7), ("index_scaled_minus_base", 8), ("index_scaled9_minus_base", 9))
  private val NegatedScaleIndices = Vec(0, 1, -1, Int.MinValue, Int.MaxValue, 0x800001)

  private def pair(sm: Int, shift: Int): Kernel =
    load(sm, Fixture.listing(s"leaS${shift}Sm${if (shift == 0) 50 else sm}"), s"lea_s$shift")

  private val pairInputs = Vec(
    (0, 0, 0, 0),
    (0x12345678, 0x10203040, 0x55667788, 0x99aabbcc),
    (0xffffffff, 0x00000001, 0x00000001, 0x00000002),
    (0x40000000, 0x80000000, 0x80000000, 0x00000001),
    (0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff),
    (0x87654321, 0x13579bdf, 0xfedcba98, 0x2468ace0)
  )

  private def unsigned(value: Int): BigInt = BigInt(java.lang.Integer.toUnsignedLong(value))

  private def expected(words: (Int, Int, Int, Int), shift: Int, negative: Boolean = false): Vec[Long] = {
    val (low, high, baseLow, baseHigh) = words
    val offset                         = (unsigned(high) << 32) | unsigned(low)
    val result                         =
      (((unsigned(baseHigh) << 32) | unsigned(baseLow)) + (if (negative) -(offset << shift) else offset << shift)) &
        ((BigInt(1) << 64) - 1)
    Vec((result & 0xffffffffL).toLong, (result >> 32).toLong)
  }

  private def address(kernel: Kernel, words: (Int, Int, Int, Int), output: Vec[Long] = Vec(0L, 0L)): Vec[Long] =
    kernel.launch(1)(
      Arg.Buffer(Harness.words(output)),
      Arg.u32(words._1),
      Arg.u32(words._2),
      Arg.u32(words._3),
      Arg.u32(words._4)
    ).words(0)

  for {
    sm       <- Vec(50, 60)
    shift    <- Shifts
    negative <- if (shift == 0) FalseOnly else Flags
  }
    test(s"CUDA 11.8 sm_$sm 64-bit address pair matches unsigned arithmetic at shift $shift, negative=$negative") {
      val kernel       = pair(sm, shift)
      val instructions = kernel.function.instructions
      if (shift == 0) assert(instructions.exists(_.mnemonic.raw == "IADD"))
      else {
        val low  = instructions.find(_.mnemonic.raw == "LEA").get
        val high = instructions.find(is(_, "LEA", Instruction.Modifier.HI, Instruction.Modifier.X)).get
        assertEquals(low.operands.head, carryDestination)
        assertEquals(high.operands.take(2), Vec(register(4), register(4)))
        // Pascal schedules ordinary MOVs between the pair
        if (sm == 60) assert(instructions.indexOf(high) - instructions.indexOf(low) > 1)
      }
      val executed =
        if (!negative) kernel
        else kernel.map(i =>
          if (i.mnemonic.raw == "LEA")
            i.rewrite(operands = i.operands.updated(1, Operand.Unary(i.operands(1), true, false, false)))
          else i
        )
      if (shift != 0) assert(executed.instructions(8, 4, 4, 4, 4).exists {
        case VMInstruction.ReadConditionPredicate(_, VMConditionCodes.Flag.Carry) => true
        case _                                                                    => false
      })
      pairInputs.foreach(words =>
        assertEquals(address(executed, words), expected(words, shift, negative), s"inputs=$words")
      )
    }

  for {
    sm                <- Vec(50, 60)
    (suffix, divisor) <- DivisionForms
  }
    test(s"CUDA 11.8 sm_$sm no-carry LEA.HI folds signed division correction by $divisor") {
      val kernel = load(sm, Fixture.listing(s"leaDiv${suffix.capitalize}Sm$sm"), s"div_$suffix")
      assertEquals(
        kernel.function.instructions.find(is(_, "LEA", Instruction.Modifier.HI)).get.operands(3),
        register(255)
      )
      val inputs = Vec(
        Int.MinValue,
        Int.MinValue + 1,
        -16777217,
        -16777216,
        -65,
        -64,
        -63,
        -33,
        -32,
        -31,
        -2,
        -1,
        0,
        1,
        2,
        31,
        32,
        33,
        63,
        64,
        65,
        16777215,
        16777216,
        16777217,
        Int.MaxValue
      ) ++ Vec.tabulate(103)(i => (i * 7919) ^ 0x9e3779b9)
      val run =
        kernel.launch(inputs.size)(Arg.zeros(inputs.size * 4), Arg.Buffer(words(inputs.map(_.toLong & 0xffffffffL))))
      assertEquals(run.words(0).map(_.toInt), inputs.map(_ / divisor))
    }

  private def high(kernel: Kernel): DecodedInstruction =
    kernel.function.instructions.find(is(_, "LEA", Instruction.Modifier.HI, Instruction.Modifier.X)).get

  private def low(kernel: Kernel): DecodedInstruction =
    kernel.function.instructions.find(i => i.mnemonic.raw == "LEA" && i.operands.head == carryDestination).get

  test("sm_50/sm_60 LEA rejects forms outside the CUDA 11.8 compiler fixture") {
    val kernel                                        = pair(60, 2)
    val original                                      = high(kernel)
    def replaced(invalid: DecodedInstruction): Kernel = kernel.map(i => if (i == original) invalid else i)
    Vec(
      original.rewrite(operands = original.operands.updated(4, Operand.Integer(BigInt(0x20)))),
      original.rewrite(operands =
        original.operands.updated(2, Operand.Qualified(register(4), Vec(OperandSelector.H1)))
      ),
      original.rewrite(operands = original.operands.updated(0, Operand.Qualified(register(4), Vec(OperandSelector.CC))))
    ).foreach(invalid => assert(replaced(invalid).lower(8, 4, 4, 4, 4).isLeft))
    assert(replaced(original.rewrite(guard = Some(Instruction.Guard(predicate(Some(0)))))).lower(8, 4, 4, 4, 4).isRight)
  }

  test("predicated LEA carry pairs execute and skip together when their guard is equal") {
    val kernel = pair(50, 2)
    val (l, h) = (low(kernel), high(kernel))
    val words  = (0x12345678, 0x10203040, 0x55667788, 0x99aabbcc)
    val poison = Vec(0xdeadbeefL, 0x13579bdfL)
    Vec(("EQ", true), ("NE", false)).foreach { (comparison, executes) =>
      val guard   = Some(Instruction.Guard(predicate(Some(1))))
      val guarded =
        kernel.copy(functions = Vec(kernel.function.copy(instructions = kernel.function.instructions.flatMap { i =>
          val rewritten = if (i.address == l.address || i.address == h.address || i.mnemonic.raw == "STG")
            i.rewrite(guard = guard)
          else i
          if (i.address == l.address)
            Vec(statement(s"ISETP.$comparison.AND P1, PT, RZ, RZ, PT", l.address.raw - 1), rewritten)
          else Vec(rewritten)
        })))
      assertEquals(address(guarded, words, poison), if (executes) expected(words, 2) else poison)
    }
  }

  private def immediatePair(kernel: Kernel, addend: Int)(high: DecodedInstruction => DecodedInstruction): Kernel = {
    val (l, h) = (low(kernel), this.high(kernel))
    kernel.map { i =>
      if (i.address == l.address) i.rewrite(operands =
        Vec(carryDestination, register(4), Operand.Integer(BigInt(addend)), Operand.Integer(BigInt(2)))
      )
      else if (i.address == h.address) high(i)
      else i
    }
  }

  test("shifted immediate LEA.CC/HI.X pairs match independent 64-bit wrap arithmetic") {
    Vec(50 -> 0x2400, 60 -> 0x1c00).foreach { (sm, addend) =>
      val kernel  = pair(sm, 2)
      val changed = immediatePair(kernel, addend)(
        _.rewrite(operands = Vec(register(4), register(4), register(255), register(5), Operand.Integer(BigInt(2))))
      )
      ImmediateEncodings.foreach { encoded =>
        val unverified = changed.map(i =>
          if (i.address == low(kernel).address)
            i.rewrite(operands = i.operands.updated(2, Operand.Integer(BigInt(encoded))))
          else i
        )
        assert(unverified.lower(8, 4, 4, 4, 4).isLeft)
      }
      Vec(
        0x00000000 -> 0x00000000,
        0xffffffff -> 0xffffffff,
        0xfffff000 -> 0x3fffffff,
        0x80000000 -> 0x80000000,
        0x12345678 -> 0xfedcba98
      )
        .foreach { (lowWord, highWord) =>
          assertEquals(
            address(changed, (lowWord, highWord, 0, 0)),
            expected((lowWord, highWord, addend, 0), 2),
            s"sm_$sm low=$lowWord"
          )
        }
    }
  }

  test("LEA.CC carry is visible to the following IADD.X") {
    Vec(50 -> 0x2400, 60 -> 0x1c00).foreach { (sm, addend) =>
      val changed = immediatePair(pair(sm, 2), addend)(
        _.rewrite(
          mnemonic = "IADD".unsafe[NonEmpty[String]],
          modifiers = Vec(Instruction.Modifier.X),
          operands = Vec(register(4), register(5), register(255))
        )
      )
      Vec(0x00000001 -> 0x12345678, 0x3fffffff -> 0x87654321).foreach { (lowWord, highWord) =>
        val total = unsigned(lowWord) * 4 + addend
        assertEquals(
          address(changed, (lowWord, highWord, 0, 0)),
          Vec((total & 0xffffffffL).toLong, (highWord.toLong + (total >> 32).toLong) & 0xffffffffL)
        )
      }
    }
  }

  private def iscadd(sm: Int, function: String): Kernel =
    load(sm, Fixture.listing(s"iscadd${function.split('_').map(_.capitalize).mkString}Sm50"), function)

  test("CUDA 11.8 sm_50/sm_60 negated ISCADD wraps shifted index at 32 bits") {
    val random  = Random(0x1caddL)
    val indices = Vec(0, 1, -1, Int.MinValue, Int.MaxValue, 0x100, -0x100, 0x01000000, 0x7fffffff) ++
      Vec.fill(64)(random.nextInt())
    val base                              = Vec.fill(indices.size)(random.nextInt())
    def execute(kernel: Kernel): Vec[Int] = kernel.launch(1, base.size)(
      Arg.Buffer(words(indices.map(_.toLong & 0xffffffffL))),
      Arg.Buffer(words(base.map(_.toLong & 0xffffffffL))),
      Arg.zeros(base.size * 4)
    ).words(2).map(_.toInt)
    for {
      sm                <- Vec(50, 60)
      (function, shift) <- ScaledIndexKernels
    } {
      val kernel = iscadd(sm, function)
      val scaled = kernel.function.instructions.filter(_.mnemonic.raw == "ISCADD")
      assertEquals(
        scaled.map(_.operands.drop(1)),
        Vec(Vec(
          Operand.Unary(register(4), negative = true, not = false, absolute = false),
          register(2),
          Operand.Bits(BigInt(shift), 0)
        ))
      )
      assertEquals(
        execute(kernel),
        Vec.tabulate(indices.size)(i => (base(i).toLong - (indices(i).toLong << shift)).toInt),
        s"sm_$sm $function"
      )
      if (shift == 8) {
        val instruction = scaled.head
        val destination = OperandPrinter.print(instruction.operands.head).value
        val guarded     =
          kernel.copy(functions = Vec(kernel.function.copy(instructions = kernel.function.instructions.flatMap { i =>
            if (i eq instruction) Vec(
              statement(s"MOV32I $destination, 0x5a5a5a5a", instruction.address.raw - 2),
              statement("ISETP.EQ.AND P0, PT, RZ, RZ, PT", instruction.address.raw - 1),
              i.rewrite(guard = Some(Instruction.Guard(predicate(Some(0), negated = true))))
            )
            else Vec(i)
          })))
        assertEquals(execute(guarded), Vec.fill(base.size)(0x5a5a5a5a), s"sm_$sm false guard")
      }
    }
  }

  for {
    sm    <- Vec(35, 50, 60)
    index <- NegatedScaleIndices
  }
    test(s"sm_$sm negated ISCADD scales before subtraction and produces no-borrow for index $index") {
      val shifted = (index << 9).toLong & 0xffffffffL
      val carry   = if (1L >= shifted) 1 else 0
      val result  = 1 - shifted.toInt
      val packed  = (if (result == 0) 0x1000 else 0) | (if (result < 0) 0x2000 else 0) | (carry << 14) |
        (if (((1 ^ shifted.toInt) & (1 ^ result)) < 0) 0x8000 else 0)
      val flags = if (sm == 35) s"P2R R7, RZ, 0xf000; LOP.XOR R7, R7, $packed; LOP.OR R6, R6, R7; " else ""
      check(
        sm,
        s"MOV R0, $index; ISCADD R0.CC, -R0, c[0x0][0x174], 9; IADD.X R6, RZ, RZ; IADD R0, R0, ${(shifted - 1).toInt}; " +
          s"IADD R6, R6, R0; ${flags}LDC.64 R2, c[0x0][0x160]; STG.E [R2], R6; EXIT",
        Vec(carry)
      )
    }

  for (sm <- Vec(35, 50, 60))
    test(s"ISCADD32I shifts and adds a negative literal on sm_$sm") {
      check(
        sm,
        "MOV R4, 128; ISCADD32I R4, R4, -0x3f800000, 23; LDC.64 R2, c[0x0][0x160]; STG.E [R2], R4; EXIT",
        Vec(0x00800000)
      )
    }

  test("LEA subtraction propagates the full 64-bit complement and zero-operand carry") {
    check(
      86,
      """S2R R0, SR_TID.X
        |IMAD R4, R0, 17, RZ
        |MOV R5, RZ
        |MOV R6, 32
        |MOV R7, 0x42
        |LEA R8, P0, -R4, R6, 2
        |LEA.HI.X R9, ~R4, R7, R5, 2, P0
        |IADD3 R6, R8, R9, RZ
        |LDC.64 R2, c[0x0][0x160]
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R6
        |EXIT""".stripMargin,
      Vec(98, 29)
    )
  }
}
