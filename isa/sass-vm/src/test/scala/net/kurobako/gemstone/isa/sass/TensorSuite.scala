package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction as VMInstruction
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.testing.either.*

class TensorSuite extends munit.FunSuite {
  private val Layouts = Vec("row-col", "row-row", "col-col", "col-row")

  private def hmma884(kind: String, layout: String): Kernel = {
    val name = layout.split('-').map(_.capitalize).mkString
    load(
      70,
      Fixture.listing(s"hmma884${kind.toUpperCaseRoot}${name}Sm70"),
      s"hmma884_${kind}_${layout.replace('-', '_')}_probe"
    )
  }

  private def isHmma884(instruction: DecodedInstruction): Boolean =
    instruction.mnemonic.raw == "HMMA" && instruction.modifiers.headOption.exists(_.name == "884")

  /** Per-lane fragment registers: A and B hold two packed halves per register, C holds f16 pairs or f32 words. */
  private def fragments(kind: String): (a: Vec[Long], b: Vec[Long], c: Vec[Long]) = {
    def pack(low: Int, high: Int): Long = half(low.toFloat) | (half(high.toFloat) << 16)
    val a                               =
      Vec.tabulate(64)(index => pack((index / 2 + index % 2 * 2) % 7 - 3, (index / 2 * 3 + index % 2 + 1) % 7 - 3))
    val b =
      Vec.tabulate(64)(index => pack((index / 2 * 2 + index % 2 + 2) % 7 - 3, (index / 2 * 5 + index % 2 * 3) % 7 - 3))
    val c =
      if (kind == "f32")
        Vec.tabulate(256)(index =>
          java.lang.Float.floatToRawIntBits(((index / 8 * 11 + index % 8 * 7) % 23 - 11).toFloat).toLong & 0xffffffffL
        )
      else Vec.tabulate(128)(index =>
        pack((index / 4 + index % 4 * 3 + 1) % 7 - 3, (index / 4 * 3 + index % 4 * 2 + 4) % 7 - 3)
      )
    (a, b, c)
  }

  /** `mma.m8n8k4` reference: each quad pair multiplies its own 8x4 A and 4x8 B using the PTX fragment layouts. */
  private def reference(kind: String, layout: String, a: Vec[Long], b: Vec[Long], c: Vec[Long]): Vec[Long] = {
    val Array(layoutA, layoutB)                                                = layout.split('-'): @unchecked
    def element(words: Vec[Long], lane: Int, index: Int, perLane: Int): Double =
      IEEE754.toDouble((words(lane * perLane + index / 2) >>> (index % 2 * 16)) & 0xffffL, 16)
    def accumulator(lane: Int, index: Int): (Int, Int) = {
      val high = if (lane >= 16) 4 else 0
      if (kind == "f32") ((lane & 1) + (index & 2) + high, (index & 4) + (lane & 2) + (index & 1))
      else (lane % 4 + high, index)
    }
    val count                       = 8
    def lanes(group: Int): Vec[Int] = Vec.tabulate(8)(index => group * 4 + index % 4 + index / 4 * 16)
    val results                     = (0 until 4).flatMap { group =>
      val matrixA = Array.ofDim[Double](8, 4)
      val matrixB = Array.ofDim[Double](4, 8)
      val matrixC = Array.ofDim[Double](8, 8)
      lanes(group).foreach { lane =>
        val high = if (lane >= 16) 4 else 0
        (0 until 4).foreach { index =>
          if (layoutA == "row") matrixA(lane % 4 + high)(index) = element(a, lane, index, 2)
          else matrixA(index + high)(lane % 4) = element(a, lane, index, 2)
          if (layoutB == "row") matrixB(lane % 4)(index + high) = element(b, lane, index, 2)
          else matrixB(index)(lane % 4 + high) = element(b, lane, index, 2)
        }
        (0 until count).foreach { index =>
          val (row, column) = accumulator(lane, index)
          matrixC(row)(column) =
            if (kind == "f32") java.lang.Float.intBitsToFloat(c(lane * 8 + index).toInt).toDouble
            else element(c, lane, index, 4)
        }
      }
      lanes(group).map { lane =>
        lane -> (0 until count).map { index =>
          val (row, column) = accumulator(lane, index)
          (0 until 4).map(k => matrixA(row)(k) * matrixB(k)(column)).sum + matrixC(row)(column)
        }
      }
    }.sortBy(_._1)
    Vec.from(results.flatMap { (_, values) =>
      if (kind == "f32") values.map(value => java.lang.Float.floatToRawIntBits(value.toFloat).toLong & 0xffffffffL)
      else values.grouped(2).map(pair => IEEE754.fromDouble(pair(0), 16) | (IEEE754.fromDouble(pair(1), 16) << 16))
    })
  }

  private def matchesReference(kernel: Kernel, kind: String, layout: String, zeroAccumulator: Boolean = false): Unit = {
    val (a, b, c) = fragments(kind)
    val run = kernel.launch(32)(Arg.zeros(c.size * 4), Arg.Buffer(words(a)), Arg.Buffer(words(b)), Arg.Buffer(words(c)))
    assertEquals(
      run.words(0),
      reference(kind, layout, a, b, if (zeroAccumulator) c.map(_ => 0L) else c),
      s"$kind $layout"
    )
  }

  private def checkLayouts(kind: String, steps: Vec[String]): Unit = Layouts.foreach { layout =>
    val kernel       = hmma884(kind, layout)
    val instructions = kernel.function.instructions
    val hmma         = instructions.filter(isHmma884(_))
    assertEquals(hmma.map(_.modifiers.last.name), steps, s"$kind $layout step sequence")
    assert(hmma.forall(i => i.operands.head == i.operands(3)), s"$kind $layout should exercise in-place D/C pairs")
    val Array(layoutA, layoutB) = layout.split('-'): @unchecked
    assert(hmma.forall(i => OperandPrinter.print(i.operands(1)).toOption.exists(_.endsWith(layoutA.toUpperCaseRoot))))
    assert(hmma.forall(i => OperandPrinter.print(i.operands(2)).toOption.exists(_.endsWith(layoutB.toUpperCaseRoot))))
    if (kind == "f32") {
      val step1 = hmma.find(_.modifiers.last.name == "STEP1").get
      val step2 = hmma.find(_.modifiers.last.name == "STEP2").get
      assert(
        instructions.exists(i => i.address > step1.address && i.address < step2.address && !isHmma884(i)),
        s"$kind $layout should have interleaved instructions"
      )
    }
    matchesReference(kernel, kind, layout)
  }

  test("sm70 F32 HMMA.884 steps preserve all four A/B layouts and accumulator pairs") {
    checkLayouts("f32", Vec("STEP0", "STEP1", "STEP2", "STEP3"))
  }

  test("sm70 F16-accumulator HMMA.884 steps preserve both accumulator steps for all A/B layouts") {
    checkLayouts("f16", Vec("STEP0", "STEP1"))
  }

  test("sm75 HMMA.884 retains Volta lane partitions for both accumulator formats") {
    for {
      kind   <- Vec("f16", "f32")
      layout <- Layouts
    } matchesReference(hmma884(kind, layout).copy(sm = 75), kind, layout)
  }

  test("sm70 HMMA.884 treats RZ accumulator operands as a zero C pair") {
    val kernel = load(70, Fixture.listing("hmma884F32ZeroSm70"), "hmma884_f32_probe")
    assert(kernel.function.instructions.filter(isHmma884(_)).forall(_.operands(3) ==
      OperandParser.parse("RZ").value))
    matchesReference(kernel, "f32", "row-col", zeroAccumulator = true)
  }

  test("HMMA.884 rejects unmeasured type, layout, selector, architecture and step forms") {
    val kernel                         = hmma884("f32", "row-col")
    val original                       = kernel.function.instructions.find(isHmma884(_)).get
    def operand(text: String): Operand = OperandParser.parse(text).value
    Vec(
      original.rewrite(modifiers =
        original.modifiers.updated(original.modifiers.size - 1, Instruction.Modifier("STEP4"))
      ),
      original.rewrite(operands = original.operands.updated(1, operand("R2.H0_H0.ROW"))),
      original.rewrite(modifiers = original.modifiers.updated(2, Instruction.Modifier("BF16"))),
      original.rewrite(guard = Some(Instruction.Guard(Operand.Predicate(Some(0), false, false)))),
      original.rewrite(operands = original.operands.updated(0, operand("R8.F32"))),
      original.rewrite(operands = original.operands.updated(1, operand("R2.ROW.H0_H0"))),
      original.rewrite(operands = original.operands.updated(0, operand("R254")))
    ).foreach(invalid => assert(kernel.map(i => if (i == original) invalid else i).lower(8, 8, 8, 8).isLeft))
    assert(kernel.copy(sm = 80).lower(8, 8, 8, 8).isLeft)
  }

  private def half(value: Float): Long = IEEE754.floatToBinary16(value).raw & 0xffffL

  private def packed(low: Int, high: Int): Long = half(low.toFloat) | (half(high.toFloat) << 16)

  private def f16Accumulator(name: String, sm: Int, function: String): Kernel =
    load(sm, Fixture.listing(name), function)

  /** HMMA.1688/16816.F16 operands: sparse inputs contribute one exact product, dense inputs cover every lane. */
  private def f16Fragments(shape: Int, dense: Boolean): (a: Vec[Long], b: Vec[Long], c: Vec[Long], d: Vec[Long]) = {
    val aCount  = if (shape == 16) 4 else 2
    val bCount  = if (shape == 16) 2 else 1
    val a       = Array.fill(32 * aCount)(0L)
    val b       = Array.fill(32 * bCount)(0L)
    val matrixA = Array.ofDim[Int](16, shape)
    val matrixB = Array.ofDim[Int](shape, 8)
    val matrixC = Array.tabulate(16, 8)((row, col) => 10 + row * 8 + col)
    if (dense) for (lane <- 0 until 32) {
      val (group, thread) = (lane / 4, lane % 4)
      for (index <- 0 until aCount * 2) {
        val row = group + index / 2  % 2 * 8
        val col = thread * 2 + index % 2 + index / 4 * 8
        matrixA(row)(col) = 1 + (row * 2 + col * 5) % 3
        a(lane * aCount + index / 2) |= half(matrixA(row)(col).toFloat) << (index % 2 * 16)
      }
      for (index <- 0 until bCount * 2) {
        val row = thread * 2 + index % 2 + index / 2 * 8
        matrixB(row)(group) = 1 + (row * 5 + group * 7) % 3
        b(lane * bCount + index / 2) |= half(matrixB(row)(group).toFloat) << (index % 2 * 16)
      }
    }
    else {
      a(0) = 0x4000L
      b(0) = 0x4200L
    }
    def tile(lane: Int, value: (Int, Int) => Int): Vec[Long] = {
      val (group, thread) = (lane / 4, lane % 4)
      Vec(group, group + 8).map(row => packed(value(row, thread * 2), value(row, thread * 2 + 1)))
    }
    val c = Vec.tabulate(32)(lane =>
      if (dense) tile(lane, matrixC(_)(_))
      else {
        val (group, thread) = (lane / 4, lane % 4)
        Vec(group, group + 8).map(row =>
          (0x3c00L + row * 8 + thread * 2) |
            ((0x3c00L + row * 8 + thread * 2 + 1) << 16)
        )
      }
    ).flatten
    val d =
      if (dense) Vec.tabulate(32)(lane =>
        tile(lane, (row, col) => matrixC(row)(col) + (0 until shape).map(k => matrixA(row)(k) * matrixB(k)(col)).sum)
      ).flatten
      else c.updated(0, 0x4700L | (0x3c01L << 16))
    (Vec.from(a), Vec.from(b), c, d)
  }

  private def aliasAccumulator(kernel: Kernel): Kernel = {
    val hmma                            = kernel.function.instructions.find(_.mnemonic.raw == "HMMA").get
    val (from, to)                      = (hmma.operands.head, hmma.operands(3))
    def next(operand: Operand): Operand = operand match {
      case Operand.Register(bank, index) => Operand.Register(bank, (index.raw + 1).refine[Int in 0 ~ 255].value)
      case other                         => fail(s"expected register operand, got $other")
    }
    kernel.map { instruction =>
      if (instruction.mnemonic.raw == "HMMA") instruction.rewrite(operands = instruction.operands.updated(0, to))
      else if (instruction.mnemonic.raw == "STG") {
        val source = instruction.operands.last
        val target = if (source == from) to else if (source == next(from)) next(to) else source
        instruction.rewrite(operands = instruction.operands.updated(instruction.operands.size - 1, target))
      } else instruction
    }
  }

  private def accumulateF16(kernel: Kernel, shape: Int, dense: Boolean = false): Unit = {
    val hmma = kernel.function.instructions.find(_.mnemonic.raw == "HMMA").get
    assertEquals(hmma.operands.size, 4)
    val (a, b, c, d) = f16Fragments(shape, dense)
    val actual       =
      kernel.launch(32)(Arg.zeros(512), Arg.Buffer(words(a)), Arg.Buffer(words(b)), Arg.Buffer(words(c))).words(0)
    assertEquals(actual.take(64), d)
    if (hmma.operands.head != hmma.operands(3)) assertEquals(actual.drop(64), c)
  }

  test("sm_89 compiler fixtures verify HMMA.1688.F16 and HMMA.16816.F16 register tuples") {
    val hmma = Vec(
      "hmma1688F16AccNonaliasSm89" -> "mma_f16_acc_nonalias_1688",
      "hmma1688F16AccAliasSm89"    -> "mma_f16_acc_alias_1688",
      "hmmaF16AccNonaliasSm89"     -> "mma_f16_acc_nonalias",
      "hmmaF16AccAliasSm89"        -> "mma_f16_acc_alias"
    ).flatMap((name, function) =>
      f16Accumulator(name, 89, function).function.instructions.filter(_.mnemonic.raw == "HMMA")
    )
    assertEquals(hmma.map(i => OperandPrinter.print(i.operands.head).value), Vec("R12", "R10", "R10", "R10"))
    assert(hmma.forall(_.modifiers.contains(Instruction.Modifier("F16"))))
  }

  test("F16 accumulation handles compiler layouts, in-place C/D, and distinct output registers") {
    for (dense <- Vec(false, true)) {
      accumulateF16(f16Accumulator("hmma1688F16AccAliasSm89", 89, "mma_f16_acc_alias_1688"), 8, dense)
      accumulateF16(f16Accumulator("hmma1688F16AccNonaliasSm89", 89, "mma_f16_acc_nonalias_1688"), 8, dense)
      accumulateF16(aliasAccumulator(f16Accumulator("hmmaF16AccAliasSm89", 89, "mma_f16_acc_alias")), 16, dense)
      accumulateF16(f16Accumulator("hmmaF16AccNonaliasSm89", 89, "mma_f16_acc_nonalias"), 16, dense)
    }
  }

  test("Turing sm_75 compiler fixture supports the F16 1688 form") {
    for {
      dense   <- Vec(false, true)
      variant <- Vec("Alias", "Nonalias")
    }
      accumulateF16(
        f16Accumulator(s"hmma1688F16AccShuffle${variant}Sm75", 75, s"mma_f16_acc_${variant.toLowerCaseRoot}_1688"),
        8,
        dense
      )
  }

  test("Turing sm_75 LDG offset plus uniform 64-bit base executes with tensor operations") {
    for (variant <- Vec("Alias", "Nonalias")) {
      val kernel = f16Accumulator(s"hmma1688F16Acc${variant}Sm75", 75, s"mma_f16_acc_${variant.toLowerCaseRoot}_1688")
      assert(kernel.instructions(8, 8, 8, 8).exists {
        case VMInstruction.Tensor(VMIR.Matrix.Operation.Multiply(_, _, _, _, _, _, _, _, _)) => true
        case _                                                                               => false
      })
      accumulateF16(kernel, 8)
      accumulateF16(kernel, 8, dense = true)
    }
  }

  test("sm_75 U32 uniform-base address lowering rejects unverified variants") {
    val kernel                             = f16Accumulator("hmma1688F16AccAliasSm75", 75, "mma_f16_acc_alias_1688")
    def uniform(operand: Operand): Boolean = OperandPrinter.print(operand).toOption.exists(_.contains(".U32+UR"))
    val load  = kernel.function.instructions.find(i => i.mnemonic.raw == "LDG" && i.operands.exists(uniform(_))).get
    val index = load.operands.indexWhere(uniform(_))
    def rejected(candidate: Kernel): Unit =
      assert(candidate.lower(8, 8, 8, 8).left.exists(_.contains("unsupported .U32 uniform global address")))
    def address(text: String): Kernel =
      kernel.map(i =>
        if (i eq load) load.rewrite(operands = load.operands.updated(index, OperandParser.parse(text).value)) else i
      )
    rejected(address("[R12.U32+UR4+0x4]"))
    rejected(address("[R12.U32]"))
    val store = Parser.parse("STG.E.SYS [R12.U32+UR4], R8;").value
    rejected(kernel.map(i => if (i eq load) load.rewrite(store) else i))
    rejected(kernel.copy(sm = 89))
  }

  test("nonuniform ldmatrix.trans fragments feed HMMA.1688.F16 in the measured lane layout") {
    val expected = Vec.tabulate(32) { lane =>
      val thread = lane % 4
      Vec(lane / 4, lane / 4 + 8).map { row =>
        def dot(col: Int): Int = (0 until 8).map(k =>
          (1 + (row * 2 + k * 5) % 3) *
            (1 + (k * 5 + col * 7) % 3)
        ).sum
        packed(dot(thread * 2), dot(thread * 2 + 1))
      }
    }.flatten
    val kernel = load(80, Fixture.listing("ldsmHmma1688TransSm80"), "ldmatrix_trans_mma_f16", shared = 384)
    assertEquals(kernel.launch(32)(Arg.zeros(256)).words(0), expected)
  }

  test("compiler DMMA.884 preserves FP64 fragment layout and overlapping input/output pairs") {
    val kernel = load(80, Fixture.listing("dmmaSm80"), "double_mma")
    assert(kernel.function.instructions.exists(i =>
      i.mnemonic.raw == "DMMA" && i.modifiers.map(_.name).contains("884") && i.operands.head == i.operands(2)
    ))
    val expected = Vec.tabulate(32) { lane =>
      Vec.tabulate(2) { item =>
        val (row, column) = (lane / 4, 2 * (lane % 4) + item)
        val product       = (0 until 4).map(k => (4 * row + k + 1) * (2 * (4 * column + k) + 1)).sum
        java.lang.Double.doubleToRawLongBits(product.toDouble + (if (item == 0) 5.0 else -7.0))
      }
    }.flatten
    assertEquals(kernel.launch(32)(Arg.zeros(512)).words(argument = 0, width = 8), expected)
  }

  for (variant <- Vec("ldsmSm89", "ldsmUniformSm89Patched"))
    test(s"sm_89 LDSM $variant preserves x1/x2/x4 and transpose lane layouts") {
      val expected = Vec.tabulate(32) { lane =>
        Vec((1, false), (2, false), (4, false), (4, true)).flatMap { (count, transpose) =>
          Vec.tabulate(count) { matrix =>
            val (row, col) = if (transpose) (lane % 4 * 2, lane / 4) else (lane / 4, lane % 4 * 2)
            val low        = 1 + matrix * 64 + row * 8 + col
            low.toLong | ((low + (if (transpose) 8 else 1)).toLong << 16)
          }
        }
      }.flatten
      val kernel = load(89, Fixture.listing(variant), "matrix_load", shared = 528)
      assertEquals(kernel.function.instructions.count(_.mnemonic.raw == "LDSM"), 4)
      assertEquals(kernel.launch(32)(Arg.zeros(expected.size * 4), Arg.u32(16)).words(0), expected)
    }

  test("compiler-emitted HMMA GEMM uses real SASS fragments across blocks and warps") {
    def a(tile: Int, row: Int, col: Int): Float = ((row + 3 * col + tile) % 7 - 3).toFloat
    def b(row: Int, col: Int): Float            = ((2 * row + col)        % 5 - 2).toFloat
    def floats(values: Vec[Float]): Vec[Long]   =
      values.map(v => java.lang.Float.floatToRawIntBits(v).toLong & 0xffffffffL)
    val av = Vec.tabulate(2048)(i => if (i % 32 < 16) a(i / 512, i % 512 / 32, i % 32) else 0f)
    val bv = Vec.tabulate(2048)(i => if (i % 32 < 16) b(i % 32, i % 512 / 32) else 0f)
    val cv = Vec.tabulate(2048)(i => if (i % 32 < 16) (i % 512 / 32 - i % 32).toFloat else -1234f)
    // The oracle accumulates in the hardware's order, so it folds over boxed floats.
    @scala.annotation.nowarn("msg=baumkuchen:loop-boxing")
    val expected = cv.zipWithIndex.map { (initial, i) =>
      val (row, col) = (i % 512 / 32, i % 32)
      if (col >= 16) initial else 2f * (0 until 16).foldLeft(initial)((sum, n) => sum + a(i / 512, row, n) * b(n, col))
    }
    val kernel = load(86, Fixture.listing("tensorSm86"), "_ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf")
    val run    = kernel.launch(block = 64, grid = 2)(
      Arg.Buffer(words(av.map(half(_)), 2)),
      Arg.Buffer(words(bv.map(half(_)), 2)),
      Arg.Buffer(words(floats(cv)))
    )
    assertEquals(run.words(2), floats(expected))
  }
}
