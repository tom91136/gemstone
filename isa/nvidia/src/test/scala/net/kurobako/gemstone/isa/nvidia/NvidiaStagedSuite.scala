package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.vm.*

class NvidiaStagedSuite extends munit.FunSuite {
  private val register  = Register(0)
  private val predicate = Predicate(0)
  private val ra        = Value.Register(Register(1))
  private val rb        = Value.Register(Register(2))
  private val rc        = Value.Register(Register(3))
  private val re        = Value.Register(Register(4))
  private val q         = Predicate(1)
  private val r         = Predicate(2)

  private class Operands(registers: Map[Register, String], predicates: Map[Predicate, String]) extends SlotNaming {
    def resolve(slot: Slot): Vec[Naming.Name] = (slot match {
      case Slot.Gpr(found)  => registers.get(found).map(Naming.Name(_, Type.unsigned(32)))
      case Slot.Pred(found) => predicates.get(found).map(Naming.Name(_, Type.Bool))
    }).toVec
  }

  private def names: Names = Names(Operands(
    Map(Register(0)  -> "d", Register(1)  -> "a", Register(2)  -> "b", Register(3) -> "c", Register(4) -> "e"),
    Map(Predicate(0) -> "p", Predicate(1) -> "q", Predicate(2) -> "r")
  ))

  private def show(instruction: Instruction)(using munit.Location): String =
    StepRenderer.definition("op", Vec(instruction), names, Vec("d", "p", "a", "b", "c", "e", "q", "r"), Vec.empty)
      .fold(failure => fail(failure.message), ScalaBackend.show(_))

  test("three-input logic matches its truth table for every table") {
    val random = scala.util.Random(3)
    for {
      table <- 0 to 255
      _     <- 0 until 16
    } {
      val Seq(a, b, c) = Seq.fill(3)(random.nextLong())
      val expected     = (0 until 32).foldLeft(0L) { (result, bit) =>
        val index = (((a >>> bit) & 1L) << 2 | ((b >>> bit) & 1L) << 1 | ((c >>> bit) & 1L)).toInt
        result | (((table >>> index) & 1L) << bit)
      }
      val sinks = Probe(a, b, c)
      assertEquals(
        IntegerSemantics.logic3(Execute, table, 32)(
          sinks = sinks,
          destination = register,
          first = ra,
          second = rb,
          third = rc
        ).head,
        expected,
        f"table 0x$table%02x"
      )
    }
  }

  test("carry chains match unsigned addition and subtraction with carries") {
    val random  = scala.util.Random(5)
    val samples = Vector(0L, 1L, -1L, 0xffffffffL, 0x80000000L, Long.MinValue, Long.MaxValue) ++
      Vector.fill(24)(random.nextLong())
    for {
      width   <- Vector[Int in 1 ~ 64](8, 16, 32, 64)
      carryIn <- Vector(false, true)
      carry   <- Vector(0L, 1L)
      a       <- samples
      b       <- samples
    } {
      val incoming              = carryIn && carry == 1L
      val Vector(sum, carryOut) =
        IntegerSemantics.addWithCarry(Execute, width, carryIn, carryOut = true)(Probe(a, b, carry), register, ra, rb)
      val added = IntegerArithmetic.addUnsigned(a, b, incoming, width)
      assertEquals((sum, carryOut != 0L), (added.value, added.carry), s"add $width $carryIn $carry $a $b")
      val Vector(difference, borrowOut) =
        IntegerSemantics.subtractWithBorrow(Execute, width, carryIn, borrowOut = true)(
          Probe(a, b, carry),
          register,
          ra,
          rb
        )
      val subtracted = IntegerArithmetic.subtractUnsigned(a, b, incoming, width)
      assertEquals(
        (difference, borrowOut != 0L),
        (subtracted.value, subtracted.borrow),
        s"sub $width $carryIn $carry $a $b"
      )
    }
  }

  test("integer arithmetic renders as definitions over the operand types") {
    def binary(operation: IntegerOperation, width: Int in (32, 64)) =
      show(Instruction.IntegerBinary(operation, register, ra, rb, width.widen))
    assertEquals(
      binary(IntegerOperation.Add, 32),
      """def op(a: UInt[32], b: UInt[32]): (d: UInt[32]) =
        |  (d = a + b)""".stripMargin
    )
    assertEquals(
      binary(IntegerOperation.MultiplyHigh(signed = true), 64),
      """def op(a: UInt[32], b: UInt[32]): (d: UInt[32]) =
        |  (d = multiplyHigh(a.to[SInt[64]], b.to[SInt[64]]).to[UInt[32]])""".stripMargin
    )
    assertEquals(
      binary(IntegerOperation.ShiftLeft, 32),
      """def op(a: UInt[32], b: UInt[32]): (d: UInt[32]) =
        |  (d = if (b < 32) a << b else UInt[32](0))""".stripMargin
    )
    assertEquals(
      binary(IntegerOperation.Divide(signed = false), 32),
      """def op(a: UInt[32], b: UInt[32]): (d: UInt[32]) =
        |  (d = if (b == UInt[32](0)) UInt[32](0) else a / b)""".stripMargin
    )
    assertEquals(
      binary(IntegerOperation.AddSaturated, 32),
      """def op(a: UInt[32], b: UInt[32]): (d: UInt[32]) =
        |  (d = max(min(a.to[SInt[32]].to[SInt[64]] + b.to[SInt[32]].to[SInt[64]], SInt[64](0x7fffffff)), SInt[64](-0x80000000)).to[UInt[32]])""".stripMargin
    )
    assertEquals(
      binary(IntegerOperation.SubtractSaturated, 64),
      """def op(a: UInt[32], b: UInt[32]): (d: UInt[32]) = {
        |  val v0 = a.to[UInt[64]] - b.to[UInt[64]]
        |  (d = if (((a.to[UInt[64]] ^ v0) & (~b.to[UInt[64]] ^ v0)).to[SInt[64]] < 0) UInt[64](if (a < 0) -0x8000000000000000L else 0x7fffffffffffffffL).to[UInt[32]] else v0.to[UInt[32]])
        |}""".stripMargin
    )
    assertEquals(
      show(Instruction.IntegerUnary(IntegerUnaryOperation.Absolute, register, ra, 32)),
      """def op(a: UInt[32]): (d: UInt[32]) = {
        |  val v0 = a.to[SInt[32]].to[SInt[64]]
        |  (d = if (a.to[SInt[32]] < 0) (SInt[32](0).to[SInt[64]] - v0).to[UInt[32]] else v0.to[UInt[32]])
        |}""".stripMargin
    )
    assertEquals(
      show(Instruction.IntegerSubtractAdd(register, ra, rb, rc, 32)),
      """def op(a: UInt[32], b: UInt[32], c: UInt[32]): (d: UInt[32]) =
        |  (d = a - b + c)""".stripMargin
    )
    assertEquals(
      show(Instruction.MultiplyAdd(register, ra, rb, rc, IntegerMultiplyAddOperation.Low(32, 64, false, false))),
      """def op(a: UInt[32], b: UInt[32], c: UInt[32]): (d: UInt[32]) =
        |  (d = a * b + c)""".stripMargin
    )
    assertEquals(
      show(Instruction.MultiplyAdd(register, ra, rb, rc, IntegerMultiplyAddOperation.Low(32, 32, true, true))),
      """def op(a: UInt[32], b: UInt[32], c: UInt[32], `CC.CF`: Bool): (d: UInt[32], `CC.CF`: Bool) = {
        |  val v0 = a * b
        |  val v1 = `CC.CF`.to[UInt[1]].to[UInt[32]]
        |  (
        |    d = v0 + c + v1,
        |    `CC.CF` = (v0.to[UInt[64]] + c.to[UInt[64]] + v1.to[UInt[64]] >> 32).to[UInt[1]] != UInt[1](0)
        |  )
        |}""".stripMargin
    )
    assertEquals(
      show(Instruction.IntegerAddWithCarry(register, ra, rb, 32, carryIn = false, carryOut = true)),
      """def op(a: UInt[32], b: UInt[32]): (d: UInt[32], `CC.CF`: Bool) =
        |  (
        |    d = a + b + 0,
        |    `CC.CF` = (a.to[UInt[64]] + b.to[UInt[64]] + 0 >> 32) != UInt[64](0)
        |  )""".stripMargin
    )
    assertEquals(
      show(Instruction.IntegerAddWithCarry(
        register,
        Value.Register(register),
        rb,
        32,
        carryIn = true,
        carryOut = true
      )),
      """def op(d: UInt[32], b: UInt[32], `CC.CF`: Bool): (`CC.CF`: Bool, d: UInt[32]) = {
        |  val v0 = `CC.CF`.to[UInt[1]].to[UInt[32]]
        |  (
        |    `CC.CF` = (d.to[UInt[64]] + b.to[UInt[64]] + v0.to[UInt[64]] >> 32) != UInt[64](0),
        |    d = d + b + v0
        |  )
        |}""".stripMargin
    )
    assertEquals(
      show(Instruction.IntegerAddThree(register, ra, rb, rc, 32, AddThreeCarryMode.None, false, true, false)),
      """def op(a: UInt[32], b: UInt[32], c: UInt[32]): (d: UInt[32]) =
        |  (d = a + ~b + c)""".stripMargin
    )
    assertEquals(
      show(Instruction.IntegerAddThree(register, ra, rb, rc, 32, AddThreeCarryMode.Produce, false, true, false)),
      """def op(a: UInt[32], b: UInt[32], c: UInt[32]): (`iadd3.carry`: UInt[2], d: UInt[32]) = {
        |  val v0 = ~b
        |  (
        |    `iadd3.carry` = (a.to[UInt[64]] + v0.to[UInt[64]] + c.to[UInt[64]] + 1 >> 32).to[UInt[2]],
        |    d = a + v0 + c + 1
        |  )
        |}""".stripMargin
    )
    assertEquals(
      show(Instruction.IntegerAddThree(register, ra, rb, rc, 32, AddThreeCarryMode.Consume, false, false, false)),
      """def op(a: UInt[32], b: UInt[32], c: UInt[32], `iadd3.carry`: UInt[2] in 0 ~ 2): (d: UInt[32]) =
        |  (d = a + b + c + `iadd3.carry`.to[UInt[32]])""".stripMargin
    )
  }

  test("bit manipulation renders in the algebra") {
    assertEquals(
      show(Instruction.BitFind(register, ra, 32, signed = false, shiftAmount = true)),
      """def op(a: UInt[32]): (d: UInt[32]) =
        |  (d = if (a == UInt[32](0)) UInt[32](0xffffffffL) else a.leadingZeros)""".stripMargin
    )
    assertEquals(
      show(Instruction.DotProduct4(register, ra, rb, rc, leftSigned = true, rightSigned = false)),
      """def op(a: UInt[32], b: UInt[32], c: UInt[32]): (d: UInt[32]) =
        |  (d = c + (a.to[SInt[8]].to[SInt[64]].to[UInt[64]] * b.to[UInt[8]].to[UInt[64]]).to[UInt[32]] + ((a >> 8).to[SInt[8]].to[SInt[64]].to[UInt[64]] * (b >> 8).to[UInt[8]].to[UInt[64]]).to[UInt[32]] + ((a >> 16).to[SInt[8]].to[SInt[64]].to[UInt[64]] * (b >> 16).to[UInt[8]].to[UInt[64]]).to[UInt[32]] + (a >> 24).to[SInt[8]].to[UInt[32]] * (b >> 24))""".stripMargin
    )
    assertEquals(
      show(Instruction.BytePermute(register, ra, rb, rc, BytePermuteMode.RC8)),
      """def op(a: UInt[32], b: UInt[32], c: UInt[32]): (d: UInt[32]) = {
        |  val v0 = ((a.to[UInt[64]] | (b.to[UInt[64]] << 32)) >> (c.to[UInt[2]].to[UInt[64]] << 3)).to[UInt[8]].to[UInt[64]]
        |  (d = (v0 | (v0 << 8) | (v0 << 16) | (v0 << 24)).to[UInt[32]])
        |}""".stripMargin
    )
    assertEquals(
      show(Instruction.BitFieldInsert(register, ra, rb, rc, re, 32)),
      """def op(a: UInt[32], b: UInt[32], c: UInt[32], e: UInt[32]): (d: UInt[32]) =
        |  (d = if (e.to[UInt[8]] == UInt[8](0) || c.to[UInt[8]] >= 32) b.to[UInt[64]].to[UInt[32]] else blend((UInt[64](-1) >> (SInt[64](64) - min(e.to[UInt[8]].to[SInt[64]], (UInt[8](32).to[UInt[64]] - c.to[UInt[8]].to[UInt[64]]).to[SInt[64]])).to[UInt[64]]) << c.to[UInt[8]], a.to[UInt[64]] << c.to[UInt[8]], b.to[UInt[64]]).to[UInt[32]])""".stripMargin
    )
    assertEquals(
      show(Instruction.Convert(register, ra, NumericFormat.Signed(32), NumericFormat.Signed(64), None)),
      """def op(a: UInt[32]): (d: SInt[32]) =
        |  (d = a.to[SInt[32]])""".stripMargin
    )
  }

  test("moves, warp exchanges and memory accesses render with their destinations") {
    assertEquals(
      show(Instruction.MoveVector(Vec(register, Register(5)), Vec(rc, Value.Immediate(0L)), 32)),
      """def op(c: UInt[32]): (d: UInt[32]) =
        |  (d = c)""".stripMargin
    )
    assertEquals(
      show(Instruction.MoveSplit(Vec(Some(register), None), rc, 32)),
      """def op(c: UInt[32]): (d: UInt[32]) =
        |  (d = c)""".stripMargin
    )
    assertEquals(
      show(Instruction.MoveSplit(Vec(Some(Register(3)), Some(register)), rc, 32)),
      """def op(c: UInt[32]): (d: UInt[32], c: UInt[32]) =
        |  (
        |    d = c >> 32,
        |    c = c
        |  )""".stripMargin
    )
    assertEquals(
      show(Instruction.VotePredicate(q, predicate, rc, VoteOperation.Any)),
      """def op(p: Bool, c: UInt[32])(using warp: Warp): (q: Bool) =
        |  (q = warp.ballot(p, c) != UInt[32](0))""".stripMargin
    )
    assertEquals(
      show(Instruction.Elect(Some(register), predicate, rc)),
      """def op(c: UInt[32])(using warp: Warp): (d: UInt[32], p: Bool) = {
        |  val v0 = warp.participants(c).trailingZeros
        |  (
        |    d = v0,
        |    p = warp.laneId() == v0
        |  )
        |}""".stripMargin
    )
    assertEquals(
      show(Instruction.Shuffle(ShuffleMode.Down, register, Some(predicate), ra, rb, rc, re, 32)),
      """def op(a: UInt[32], b: UInt[32], c: UInt[32])(using warp: Warp): (p: Bool, d: UInt[32]) = {
        |  val v0 = warp.laneId().to[UInt[64]]
        |  val v1 = v0 + b.to[UInt[5]].to[UInt[32]].to[UInt[64]]
        |  val v2 = v1.to[SInt[64]]
        |  val v3 = v2 <= min(c.to[SInt[64]], SInt[64](31)) && v2 < warp.lanes().to[SInt[64]]
        |  (
        |    p = v3,
        |    d = warp.shuffle[UInt[32]](a, if (v3) v1.to[UInt[32]] else v0.to[UInt[32]])
        |  )
        |}""".stripMargin
    )
    assertEquals(
      show(Instruction.WarpReduce(register, rc, Value.Immediate(0L), IntegerOperation.Maximum(signed = false), 32)),
      """def op(c: UInt[32])(using warp: Warp): (d: UInt[32]) =
        |  (d = warp.reduceMaximum[UInt[32]](c, warp.members(UInt[32](0))))""".stripMargin
    )
    assertEquals(
      show(Instruction.Load(register, Value.Address(rc, 16), 8, signed = true, MemoryAccess.Global(false))),
      """def op(c: UInt[32])(using memory: Memory): (d: UInt[32]) =
        |  (d = memory.loadGlobal[UInt[8]]((c.to[UInt[64]] + 16).to[UInt[64]]).to[SInt[8]].to[UInt[32]])""".stripMargin
    )
    assertEquals(
      show(Instruction.StoreVector(rc, Vec(rc, Value.Immediate(0L)), 32, MemoryAccess.Shared)),
      """def op(c: UInt[32])(using memory: Memory): Unit = {
        |  memory.storeShared[UInt[32]]((c.to[UInt[64]] + 0).to[UInt[64]], c)
        |  memory.storeShared[UInt[32]]((c.to[UInt[64]] + 4).to[UInt[64]], UInt[32](0))
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      show(Instruction.ReadConditionPredicate(predicate, VMConditionCodes.Flag.Carry)),
      """def op(`cc.carry`: Bool): (p: Bool) =
        |  (p = `cc.carry`)""".stripMargin
    )
    assertEquals(
      show(Instruction.WriteConditionFlag(VMConditionCodes.Flag.Zero, ra)),
      """def op(a: UInt[32]): (`cc.zero`: Bool) =
        |  (`cc.zero` = a != UInt[32](0))""".stripMargin
    )
    assertEquals(
      show(Instruction.AtomicCompareExchange(register, rc, ra, Value.Immediate(5L), 32, MemorySpace.Shared)),
      """def op(a: UInt[32], c: UInt[32])(using memory: Memory): (d: UInt[32]) = {
        |  val v0 = memory.loadShared[UInt[32]](c.to[UInt[64]])
        |  memory.storeShared[UInt[32]](c.to[UInt[64]], if (v0 == a) UInt[32](5) else v0)
        |  (d = v0)
        |}""".stripMargin
    )
    assertEquals(
      show(Instruction.Atomic(Some(register), rc, ra, 32, MemorySpace.Global, AtomicOperation.MaximumSigned)),
      """def op(a: UInt[32], c: UInt[32])(using memory: Memory): (d: UInt[32]) = {
        |  val v0 = memory.loadGlobal[UInt[32]](c.to[UInt[64]])
        |  memory.storeGlobal[UInt[32]](c.to[UInt[64]], max(v0.to[SInt[32]], a.to[SInt[32]]).to[UInt[32]])
        |  (d = v0)
        |}""".stripMargin
    )
    assertEquals(
      show(Instruction.Atomic(None, rc, ra, 64, MemorySpace.Generic, AtomicOperation.Add)),
      """def op(a: UInt[32], c: UInt[32])(using memory: Memory): Unit = {
        |  val v0 = memory.load[UInt[64]](c.to[UInt[64]])
        |  memory.store[UInt[64]](c.to[UInt[64]], v0 + a.to[UInt[64]])
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      show(Instruction.UniformMove(register, Value.LaneId)),
      """def op()(using warp: Warp): (d: UInt[32]) =
        |  (d = warp.leader[UInt[32]](warp.laneId()))""".stripMargin
    )
  }

  test("matrix products define each element of D from a row of A, a column of B and an element of C") {
    import Matrix.*
    def multiply(shape: Shape, operand: Element, result: Element, saturate: Boolean) = {
      def fragment(role: Role, element: Element) = Fragment(shape, role, element, wmma = false, role == Role.B)
      val a                                      = fragment(Role.A, operand)
      val c                                      = fragment(Role.C, result)
      StepRenderer.definition(
        "mma",
        Vec(Instruction.Tensor(Operation.Multiply(
          a,
          fragment(Role.B, operand),
          c,
          c,
          Vec.fill(c.registers)(register),
          Vec.fill(a.registers)(ra),
          Vec.fill(fragment(Role.B, operand).registers)(rb),
          Vec.fill(c.registers)(rc),
          Matrix.accumulation(89, shape, operand, result, ConversionRounding.RN),
          saturate
        ))),
        names,
        Vec("c") ++ Vec.tabulate(shape.k)(index => s"a$index") ++ Vec.tabulate(shape.k)(index => s"b$index"),
        Vec.empty
      ).fold(failure => fail(failure.message), ScalaBackend.show(_))
    }
    assertEquals(
      multiply(Shape(8, 8, 4), Element.F64, Element.F64, saturate = false),
      """def mma(c: Float64, a0: Float64, a1: Float64, a2: Float64, a3: Float64, b0: Float64, b1: Float64, b2: Float64, b3: Float64): (d: Float64) = {
        |  val v0 = TensorArithmetic.fused(a0, b0, c, rounding = VMIR.ConversionRounding.RN)
        |  val v1 = TensorArithmetic.fused(a1, b1, v0, rounding = VMIR.ConversionRounding.RN)
        |  val v2 = TensorArithmetic.fused(a2, b2, v1, rounding = VMIR.ConversionRounding.RN)
        |  val v3 = TensorArithmetic.fused(a3, b3, v2, rounding = VMIR.ConversionRounding.RN)
        |  (d = v3)
        |}""".stripMargin
    )
    assertEquals(
      multiply(Shape(16, 8, 8), Element.F16, Element.F32, saturate = false),
      """def mma(c: Float32, a0: Float16, a1: Float16, a2: Float16, a3: Float16, a4: Float16, a5: Float16, a6: Float16, a7: Float16, b0: Float16, b1: Float16, b2: Float16, b3: Float16, b4: Float16, b5: Float16, b6: Float16, b7: Float16): (d: Float32) = {
        |  val v0 = TensorArithmetic.widen(c)
        |  val v1 = TensorArithmetic.widen(c)
        |  val v2 = TensorArithmetic.widen(a0)
        |  val v3 = TensorArithmetic.widen(b0)
        |  val v4 = TensorArithmetic.product(v2, v3)
        |  val v5 = TensorArithmetic.special(v0, v4)
        |  val v6 = TensorArithmetic.exponent(v2, minimum = -14) + TensorArithmetic.exponent(v3, minimum = -14)
        |  val v7 = TensorArithmetic.widen(a1)
        |  val v8 = TensorArithmetic.widen(b1)
        |  val v9 = TensorArithmetic.product(v7, v8)
        |  val v10 = TensorArithmetic.special(v5, v9)
        |  val v11 = TensorArithmetic.exponent(v7, minimum = -14) + TensorArithmetic.exponent(v8, minimum = -14)
        |  val v12 = TensorArithmetic.widen(a2)
        |  val v13 = TensorArithmetic.widen(b2)
        |  val v14 = TensorArithmetic.product(v12, v13)
        |  val v15 = TensorArithmetic.special(v10, v14)
        |  val v16 = TensorArithmetic.exponent(v12, minimum = -14) + TensorArithmetic.exponent(v13, minimum = -14)
        |  val v17 = TensorArithmetic.widen(a3)
        |  val v18 = TensorArithmetic.widen(b3)
        |  val v19 = TensorArithmetic.product(v17, v18)
        |  val v20 = TensorArithmetic.special(v15, v19)
        |  val v21 = TensorArithmetic.exponent(v17, minimum = -14) + TensorArithmetic.exponent(v18, minimum = -14)
        |  val v22 = TensorArithmetic.widen(a4)
        |  val v23 = TensorArithmetic.widen(b4)
        |  val v24 = TensorArithmetic.product(v22, v23)
        |  val v25 = TensorArithmetic.special(v20, v24)
        |  val v26 = TensorArithmetic.exponent(v22, minimum = -14) + TensorArithmetic.exponent(v23, minimum = -14)
        |  val v27 = TensorArithmetic.widen(a5)
        |  val v28 = TensorArithmetic.widen(b5)
        |  val v29 = TensorArithmetic.product(v27, v28)
        |  val v30 = TensorArithmetic.special(v25, v29)
        |  val v31 = TensorArithmetic.exponent(v27, minimum = -14) + TensorArithmetic.exponent(v28, minimum = -14)
        |  val v32 = TensorArithmetic.widen(a6)
        |  val v33 = TensorArithmetic.widen(b6)
        |  val v34 = TensorArithmetic.product(v32, v33)
        |  val v35 = TensorArithmetic.special(v30, v34)
        |  val v36 = TensorArithmetic.exponent(v32, minimum = -14) + TensorArithmetic.exponent(v33, minimum = -14)
        |  val v37 = TensorArithmetic.widen(a7)
        |  val v38 = TensorArithmetic.widen(b7)
        |  val v39 = TensorArithmetic.product(v37, v38)
        |  val v40 = TensorArithmetic.special(v35, v39)
        |  val v41 = TensorArithmetic.exponent(v37, minimum = -14) + TensorArithmetic.exponent(v38, minimum = -14)
        |  val v42 = max(max(max(max(max(max(max(max(max(SInt[64](-132), TensorArithmetic.exponent(v1, minimum = -126)), v6), v11), v16), v21), v26), v31), v36), v41)
        |  val v43 = TensorArithmetic.align(v1, v42, extra = 1) + TensorArithmetic.align(v4, v42, extra = 1) + TensorArithmetic.align(v9, v42, extra = 1) + TensorArithmetic.align(v14, v42, extra = 1) + TensorArithmetic.align(v19, v42, extra = 1) + TensorArithmetic.align(v24, v42, extra = 1) + TensorArithmetic.align(v29, v42, extra = 1) + TensorArithmetic.align(v34, v42, extra = 1) + TensorArithmetic.align(v39, v42, extra = 1)
        |  val v44 = TensorArithmetic.round(v43, v42, v1, extra = 1, fraction = 23, nearest = false)
        |  (d = TensorArithmetic.finish(v44, v40))
        |}""".stripMargin
    )
    assertEquals(
      multiply(Shape(8, 8, 16), Element.S8, Element.S32, saturate = true),
      """def mma(c: SInt[32], a0: SInt[8], a1: SInt[8], a2: SInt[8], a3: SInt[8], a4: SInt[8], a5: SInt[8], a6: SInt[8], a7: SInt[8], a8: SInt[8], a9: SInt[8], a10: SInt[8], a11: SInt[8], a12: SInt[8], a13: SInt[8], a14: SInt[8], a15: SInt[8], b0: SInt[8], b1: SInt[8], b2: SInt[8], b3: SInt[8], b4: SInt[8], b5: SInt[8], b6: SInt[8], b7: SInt[8], b8: SInt[8], b9: SInt[8], b10: SInt[8], b11: SInt[8], b12: SInt[8], b13: SInt[8], b14: SInt[8], b15: SInt[8]): (d: SInt[32]) = {
        |  val v0 = TensorArithmetic.widen(c)
        |  val v1 = v0 + TensorArithmetic.widen(a0) * TensorArithmetic.widen(b0)
        |  val v2 = v1 + TensorArithmetic.widen(a1) * TensorArithmetic.widen(b1)
        |  val v3 = v2 + TensorArithmetic.widen(a2) * TensorArithmetic.widen(b2)
        |  val v4 = v3 + TensorArithmetic.widen(a3) * TensorArithmetic.widen(b3)
        |  val v5 = v4 + TensorArithmetic.widen(a4) * TensorArithmetic.widen(b4)
        |  val v6 = v5 + TensorArithmetic.widen(a5) * TensorArithmetic.widen(b5)
        |  val v7 = v6 + TensorArithmetic.widen(a6) * TensorArithmetic.widen(b6)
        |  val v8 = v7 + TensorArithmetic.widen(a7) * TensorArithmetic.widen(b7)
        |  val v9 = v8 + TensorArithmetic.widen(a8) * TensorArithmetic.widen(b8)
        |  val v10 = v9 + TensorArithmetic.widen(a9) * TensorArithmetic.widen(b9)
        |  val v11 = v10 + TensorArithmetic.widen(a10) * TensorArithmetic.widen(b10)
        |  val v12 = v11 + TensorArithmetic.widen(a11) * TensorArithmetic.widen(b11)
        |  val v13 = v12 + TensorArithmetic.widen(a12) * TensorArithmetic.widen(b12)
        |  val v14 = v13 + TensorArithmetic.widen(a13) * TensorArithmetic.widen(b13)
        |  val v15 = v14 + TensorArithmetic.widen(a14) * TensorArithmetic.widen(b14)
        |  val v16 = v15 + TensorArithmetic.widen(a15) * TensorArithmetic.widen(b15)
        |  (d = TensorArithmetic.saturate(v16))
        |}""".stripMargin
    )
  }

  test("three-input logic renders common tables directly and folds the rest") {
    def logic3(table: Int in 0 ~ 255) = show(Instruction.Logic3(register, ra, rb, rc, table, 32))
    assertEquals(
      logic3(0x96),
      """def op(a: UInt[32], b: UInt[32], c: UInt[32]): (d: UInt[32]) =
        |  (d = a ^ b ^ c)""".stripMargin
    )
    assertEquals(
      Vector(0xf0, 0x01, 0x00, 0xca, 0x5a, 0xb8).map(table => logic3(table.unsafe[Int in 0 ~ 255])),
      Vector(
        """def op(a: UInt[32]): (d: UInt[32]) =
        |  (d = a)""".stripMargin,
        """def op(a: UInt[32], b: UInt[32], c: UInt[32]): (d: UInt[32]) =
        |  (d = ~a & (~b & ~c))""".stripMargin,
        """def op(): (d: UInt[32]) =
        |  (d = UInt[32](0))""".stripMargin,
        """def op(a: UInt[32], b: UInt[32], c: UInt[32]): (d: UInt[32]) =
        |  (d = blend(a, b, c))""".stripMargin,
        """def op(a: UInt[32], c: UInt[32]): (d: UInt[32]) =
        |  (d = a ^ c)""".stripMargin,
        """def op(a: UInt[32], b: UInt[32], c: UInt[32]): (d: UInt[32]) =
        |  (d = blend(a.to[UInt[64]], ~b.to[UInt[64]] | c.to[UInt[64]], (b & c).to[UInt[64]]).to[UInt[32]])""".stripMargin
      )
    )
  }

  test("funnel shifts and comparisons render") {
    assertEquals(
      show(Instruction.FunnelShift(register, ra, rb, rc, left = true, clamp = false)),
      """def op(a: UInt[32], b: UInt[32], c: UInt[32]): (d: UInt[32]) =
        |  (d = (((a.to[UInt[64]] | (b.to[UInt[64]] << 32)) << c.to[UInt[5]]) >> 32).to[UInt[32]])""".stripMargin
    )
    assertEquals(
      show(Instruction.FunnelShift(register, ra, rb, rc, left = false, clamp = true)),
      """def op(a: UInt[32], b: UInt[32], c: UInt[32]): (d: UInt[32]) =
        |  (d = ((a.to[UInt[64]] | (b.to[UInt[64]] << 32)) >> min(c.to[SInt[64]], SInt[64](32)).to[UInt[64]]).to[UInt[32]])""".stripMargin
    )
    assertEquals(
      show(Instruction.SetPredicate(predicate, Comparison.LessThanUnordered, NumericFormat.Signed(32), ra, rb)),
      """def op(a: UInt[32], b: UInt[32]): (p: Bool) =
        |  (p = a.to[SInt[32]] < b.to[SInt[32]])""".stripMargin
    )
    assertEquals(
      show(Instruction.SetPredicate(predicate, Comparison.GreaterThan, NumericFormat.IEEE(32), ra, rb)),
      """def op(a: UInt[32], b: UInt[32]): (p: Bool) =
        |  (p = FloatArithmetic.compare(a.asFloat, b.asFloat, comparison = VMIR.Comparison.GreaterThan))""".stripMargin
    )
    assertEquals(
      show(Instruction.PredicateCompare(predicate, Comparison.LessThan, q, r)),
      """def op(q: Bool, r: Bool): (p: Bool) =
        |  (p = q.to[UInt[1]] < r.to[UInt[1]])""".stripMargin
    )
  }

  test("floating point applies only the controls the instruction asks for") {
    assertEquals(
      show(Instruction.FloatBinary(FloatOperation.Add, register, ra, rb, 32)),
      """def op(a: UInt[32], b: UInt[32]): (d: UInt[32]) =
        |  (d = FloatArithmetic.canonical(FloatArithmetic.add(a.asFloat, b.asFloat)).bits)""".stripMargin
    )
    assertEquals(
      show(
        Instruction.FloatBinary(
          FloatOperation.Subtract,
          register,
          ra,
          rb,
          32,
          FloatControl(flushSubnormal = true, saturate = true)
        )
      ),
      """def op(a: UInt[32], b: UInt[32]): (d: UInt[32]) =
        |  (d = FloatArithmetic.flush(FloatArithmetic.saturate(FloatArithmetic.canonical(FloatArithmetic.add(FloatArithmetic.flush(a.asFloat), (FloatArithmetic.flush(b.asFloat).bits ^ 0x80000000L).asFloat)))).bits)""".stripMargin
    )
    assertEquals(
      show(Instruction.FusedMultiplyAdd(register, ra, rb, rc, 64, rounding = ConversionRounding.RZ)),
      """def op(a: UInt[32], b: UInt[32], c: UInt[32]): (d: UInt[32]) =
        |  (d = FloatArithmetic.fusedMultiplyAdd(a.to[UInt[64]].asFloat, b.to[UInt[64]].asFloat, c.to[UInt[64]].asFloat, rounding = VMIR.ConversionRounding.RZ).bits.to[UInt[32]])""".stripMargin
    )
    assertEquals(
      show(Instruction.FloatUnary(FloatUnaryOperation.Negate, register, ra, 16)),
      """def op(a: UInt[32]): (d: UInt[32]) =
        |  (d = (a ^ 0x8000).to[UInt[16]].to[UInt[32]])""".stripMargin
    )
  }

  test("conversions and selects render") {
    assertEquals(
      show(
        Instruction.Convert(
          register,
          ra,
          NumericFormat.IEEE(32),
          NumericFormat.Signed(32),
          Some(ConversionRounding.RZI)
        )
      ),
      """def op(a: UInt[32]): (d: SInt[32]) =
        |  (d = Conversions.toSigned(a.asFloat, rounding = VMIR.ConversionRounding.RZI, width = 32))""".stripMargin
    )
    assertEquals(
      show(Instruction.Select(register, ra, rb, q, 32)),
      """def op(a: UInt[32], b: UInt[32], q: Bool): (d: UInt[32]) =
        |  (d = if (q) a else b)""".stripMargin
    )
  }

  test("registers without a name render as temporaries in the order they appear") {
    val anonymous = Names(Operands(Map.empty, Map.empty))
    assertEquals(
      StepRenderer.describe(
        Vec(
          Instruction.IntegerBinary(IntegerOperation.Add, Register(7), ra, rb, 32),
          Instruction.IntegerBinary(IntegerOperation.ShiftLeft, Register(9), Value.Register(Register(7)), rb, 32)
        ),
        anonymous
      ).map(_.show),
      Some("t2 = zext32(t0 + t1)\n" +
        "t3 = t1 & 0xffffffff\n" +
        "t4 = (t3 <s 32) ? zext32(t2 << t3) : 0")
    )
  }

  test("instructions without staged semantics are not described") {
    assertEquals(StepRenderer.describe(Vec(Instruction.Branch(Pc.Entry)), names), None)
    assertEquals(
      StepRenderer.describe(
        Vec(Instruction.WarpReduce(register, ra, rb, IntegerOperation.MultiplyLow, 32)),
        names
      ),
      None
    )
  }
}
