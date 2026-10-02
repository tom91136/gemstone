package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction as VMInstruction
import net.kurobako.gemstone.isa.nvidia.VMTensor.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class HmmaF16AccumulatorSuite extends munit.FunSuite {
  private def fixtureModule(sm: Int, shuffle: Boolean = false): Cubin.Module = {
    val kernels = if (sm == 89) Vec(
      SASSFixture.Kernel("mma_f16_acc_nonalias_1688", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8, 24 -> 8), registers = 18),
      SASSFixture.Kernel("mma_f16_acc_alias_1688", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8, 24 -> 8), registers = 16),
      SASSFixture.Kernel("mma_f16_acc_nonalias", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8, 24 -> 8), registers = 20),
      SASSFixture.Kernel("mma_f16_acc_alias", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8, 24 -> 8), registers = 20)
    )
    else if (shuffle) Vec(
      SASSFixture.Kernel("mma_f16_acc_nonalias_1688", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8, 24 -> 8), registers = 18),
      SASSFixture.Kernel("mma_f16_acc_alias_1688", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8, 24 -> 8), registers = 16)
    )
    else Vec(
      SASSFixture.Kernel("mma_f16_acc_nonalias_1688", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8, 24 -> 8), registers = 16),
      SASSFixture.Kernel("mma_f16_acc_alias_1688", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8, 24 -> 8), registers = 14)
    )
    SASSFixture.module(sm, kernels)
  }

  private def words(values: Vec[Long]): Vec[Byte] =
    Vec.from(values).flatMap(LittleEndian.toVec(_, 4))

  private def packed(low: Int, high: Int): Long = (low & 0xffffL) | ((high & 0xffffL) << 16)

  private def halfInt(value: Int): Int = IEEE754.floatToBinary16(value.toFloat).raw.toInt

  private def fragments(shape: Int, dense: Boolean): (Vec[Long], Vec[Long], Vec[Long], Vec[Long]) = {
    val aCount  = if (shape == 16) 4 else 2
    val bCount  = if (shape == 16) 2 else 1
    val a       = Array.fill(32 * aCount)(0L)
    val b       = Array.fill(32 * bCount)(0L)
    val matrixA = Array.ofDim[Int](16, shape)
    val matrixB = Array.ofDim[Int](shape, 8)
    if (dense) {
      for (lane <- 0 until 32) {
        val group  = lane / 4
        val thread = lane % 4
        for (index <- 0 until aCount * 2) {
          val row   = group + ((index / 2) % 2) * 8
          val col   = thread * 2 + index   % 2 + (index / 4) * 8
          val value = 1 + ((row * 2 + col * 5) % 3)
          matrixA(row)(col) = value
          a(lane * aCount + index / 2) |= halfInt(value).toLong << ((index % 2) * 16)
        }
        for (index <- 0 until bCount * 2) {
          val row   = thread * 2 + index % 2 + (index / 2) * 8
          val col   = group
          val value = 1 + ((row * 5 + col * 7) % 3)
          matrixB(row)(col) = value
          b(lane * bCount + index / 2) |= halfInt(value).toLong << ((index % 2) * 16)
        }
      }
    } else {
      // A single exact 2 * 3 product contributes 6 to logical output (0, 0).
      a(0) = packed(0x4000, 0)
      b(0) = packed(0x4200, 0)
    }
    val matrixC = Array.tabulate(16, 8)((row, col) => 10 + row * 8 + col)
    val c       = (0 until 32).toVec.flatMap { lane =>
      val group  = lane / 4
      val thread = lane % 4
      Vec(
        if (dense) packed(halfInt(matrixC(group)(thread * 2)), halfInt(matrixC(group)(thread * 2 + 1)))
        else packed(0x3c00 + group * 8 + thread * 2, 0x3c00 + group * 8 + thread * 2 + 1),
        if (dense) packed(halfInt(matrixC(group + 8)(thread * 2)), halfInt(matrixC(group + 8)(thread * 2 + 1)))
        else packed(0x3c00 + (group + 8) * 8 + thread * 2, 0x3c00 + (group + 8) * 8 + thread * 2 + 1)
      )
    }
    val expected = if (dense) {
      val words = Array.ofDim[Long](64)
      for (lane <- 0 until 32) {
        val group  = lane / 4
        val thread = lane % 4
        val output = Vec(
          (group, thread * 2),
          (group, thread * 2 + 1),
          (group + 8, thread * 2),
          (group + 8, thread * 2 + 1)
        ).map { (row, col) =>
          matrixC(row)(col) + (0 until shape).map(index => matrixA(row)(index) * matrixB(index)(col)).sum
        }
        words(lane * 2) = packed(halfInt(output(0)), halfInt(output(1)))
        words(lane * 2 + 1) = packed(halfInt(output(2)), halfInt(output(3)))
      }
      words.toVec
    } else c.updated(0, packed(0x4700, 0x3c01))
    (a.toVec, b.toVec, c, expected)
  }

  private def aliasDestinationToAccumulator(function: DecodedFunction): DecodedFunction = {
    val hmma = function.instructions.find(_.mnemonic.raw == "HMMA").getOrElse(fail("fixture has no HMMA"))
    val from = hmma.operands.head
    val to   = hmma.operands(3)
    def nextRegister(operand: Operand): Operand = operand match {
      case Operand.Register(bank, index) => Operand.Register(
          bank,
          (index.raw + 1).refine[Int where Between[0, 255]].fold(error => fail(error.raw), identity)
        )
      case other => fail(s"expected register operand, got $other")
    }
    val fromNext = nextRegister(from)
    val toNext   = nextRegister(to)
    function.copy(instructions = function.instructions.map { instruction =>
      if (instruction.mnemonic.raw == "HMMA") instruction.rewrite(operands = instruction.operands.updated(0, to))
      else if (instruction.mnemonic.raw == "STG") {
        val source = instruction.operands.last
        val target = if (source == from) to else if (source == fromNext) toNext else source
        instruction.rewrite(operands = instruction.operands.updated(instruction.operands.size - 1, target))
      } else instruction
    })
  }

  private def run(
      sm: Int,
      sassText: String,
      kernel: String,
      shape: Int,
      forceAlias: Boolean = false,
      dense: Boolean = false,
      shuffle: Boolean = false
  ): Unit = {
    val module      = fixtureModule(sm, shuffle)
    val parsed      = ListingDecoder.parse(sassText, kernel, module.sm).fold(fail(_), _.head)
    val function    = if (forceAlias) aliasDestinationToAccumulator(parsed) else parsed
    val instruction = function.instructions.find(_.mnemonic.raw == "HMMA").getOrElse(fail("fixture has no HMMA"))
    assert(instruction.operands.size == 4)
    if (forceAlias) assertEquals(instruction.operands.head, instruction.operands(3))
    val alias   = instruction.operands.head == instruction.operands(3)
    val lowered = SASSFixture(module, module.kernels.find(_.name == kernel).get, Vec(function))

    val (aWords, bWords, cWords, expected) = fragments(shape, dense)
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(512L.unsafe[Long > 0])
      val inputs  = Vec(aWords, bWords, cWords).map(values =>
        service.allocate((values.size.toLong * 4).unsafe[Long > 0])
      )
      inputs.zip(Vec(aWords, bWords, cWords)).foreach { case (allocation, values) =>
        assert(service.write(allocation, 0L, words(values)).isRight)
      }
      val program = service.compile(lowered)
        .fold(error => fail(error.message), identity)
      val task = SASSFixture.task(
        kernel.unsafe[NonEmpty[String]],
        LaunchGeometry.Dimensions(32, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(output, 0L),
          KernelTask.Argument.Memory(inputs(0), 0L),
          KernelTask.Argument.Memory(inputs(1), 0L),
          KernelTask.Argument.Memory(inputs(2), 0L)
        )
      )
      service.run(program, task).fold(error => fail(error.message), identity)
      val bytes  = service.read(output, 0L, (128 * 4).unsafe[Int >= 0]).toOption.get
      val actual = (0 until 128).map(index => LittleEndian.decode(bytes, index * 4, 4)).toVec
      assertEquals(actual.take(64), expected)
      if (!alias) assertEquals(actual.drop(64), cWords)
    }
  }

  test("sm_89 compiler fixtures verify HMMA.1688.F16 and HMMA.16816.F16 register tuples") {
    val names =
      Vec("mma_f16_acc_nonalias_1688", "mma_f16_acc_alias_1688", "mma_f16_acc_nonalias", "mma_f16_acc_alias")
    val instructions = Vec(
      names(0) -> HmmaFixtures.hmmaF16AccSm89MmaF16AccNonalias1688,
      names(1) -> HmmaFixtures.hmmaF16AccSm89MmaF16AccAlias1688,
      names(2) -> HmmaFixtures.hmmaF16AccSm89MmaF16AccNonalias,
      names(3) -> HmmaFixtures.hmmaF16AccSm89MmaF16AccAlias
    ).flatMap { case (name, text) =>
      ListingDecoder.parse(text, name, 89).fold(fail(_), _.flatMap(_.instructions))
        .filter(_.mnemonic.raw == "HMMA")
    }
    assertEquals(instructions.size, 4)
    assertEquals(
      instructions.map(_.operands.head).map(OperandPrinter.print(_).fold(fail(_), identity)),
      Vec("R12", "R10", "R10", "R10")
    )
    assert(instructions.forall(_.modifiers.contains(SASSInstruction.Modifier("F16"))))
  }

  test("F16 accumulation handles compiler layouts, in-place C/D, and distinct output registers") {
    run(89, HmmaFixtures.hmmaF16AccSm89MmaF16AccAlias1688, "mma_f16_acc_alias_1688", 8)
    run(89, HmmaFixtures.hmmaF16AccSm89MmaF16AccNonalias1688, "mma_f16_acc_nonalias_1688", 8)
    run(89, HmmaFixtures.hmmaF16AccSm89MmaF16AccAlias, "mma_f16_acc_alias", 16, forceAlias = true)
    run(89, HmmaFixtures.hmmaF16AccSm89MmaF16AccNonalias, "mma_f16_acc_nonalias", 16)
    run(89, HmmaFixtures.hmmaF16AccSm89MmaF16AccAlias1688, "mma_f16_acc_alias_1688", 8, dense = true)
    run(89, HmmaFixtures.hmmaF16AccSm89MmaF16AccNonalias1688, "mma_f16_acc_nonalias_1688", 8, dense = true)
    run(89, HmmaFixtures.hmmaF16AccSm89MmaF16AccAlias, "mma_f16_acc_alias", 16, forceAlias = true, dense = true)
    run(89, HmmaFixtures.hmmaF16AccSm89MmaF16AccNonalias, "mma_f16_acc_nonalias", 16, dense = true)
  }

  test("Turing sm_75 compiler fixture supports the F16 1688 form") {
    val alias    = HmmaFixtures.hmmaF16AccSm89ShflSm75MmaF16AccAlias1688
    val nonalias = HmmaFixtures.hmmaF16AccSm89ShflSm75MmaF16AccNonalias1688
    for (text <- Vec(alias, nonalias)) {
      assert(text.contains("HMMA.1688.F16"))
      assert(text.contains("SHFL.BFLY"))
      assert(!text.contains(".U32+UR"))
    }
    run(75, HmmaFixtures.hmmaF16AccSm89ShflSm75MmaF16AccAlias1688, "mma_f16_acc_alias_1688", 8, shuffle = true)
    run(75, HmmaFixtures.hmmaF16AccSm89ShflSm75MmaF16AccNonalias1688, "mma_f16_acc_nonalias_1688", 8, shuffle = true)
    run(
      75,
      HmmaFixtures.hmmaF16AccSm89ShflSm75MmaF16AccAlias1688,
      "mma_f16_acc_alias_1688",
      8,
      dense = true,
      shuffle = true
    )
    run(
      75,
      HmmaFixtures.hmmaF16AccSm89ShflSm75MmaF16AccNonalias1688,
      "mma_f16_acc_nonalias_1688",
      8,
      dense = true,
      shuffle = true
    )
  }

  test("Turing sm_75 LDG offset plus uniform 64-bit base executes with tensor operations") {
    val module = fixtureModule(75)
    for (kernel <- Vec("mma_f16_acc_alias_1688", "mma_f16_acc_nonalias_1688")) {
      val text = if (kernel == "mma_f16_acc_alias_1688") HmmaFixtures.hmmaF16AccSm89OrigSm75MmaF16AccAlias1688
      else HmmaFixtures.hmmaF16AccSm89OrigSm75MmaF16AccNonalias1688
      assert(text.contains(".U32+UR"))
      val function = ListingDecoder.parse(text, kernel, 75).fold(fail(_), _.head)
      val lowered  = SASSFixture(module, module.kernels.find(_.name == kernel).get, Vec(function))
      assert(lowered.instructions.exists {
        case VMInstruction.Tensor(
              Operation.Multiply(_, _, _, _, _, _, _, _, _),
              _
            ) => true
        case _ => false
      })
      run(75, text, kernel, 8)
      run(75, text, kernel, 8, dense = true)
    }
  }

  test("sm_75 U32 uniform-base address lowering rejects unverified variants") {
    val module    = fixtureModule(75)
    val kernel    = module.kernels.find(_.name == "mma_f16_acc_alias_1688").get
    val text      = HmmaFixtures.hmmaF16AccSm89OrigSm75MmaF16AccAlias1688
    val function  = ListingDecoder.parse(text, kernel.name, 75).fold(fail(_), _.head)
    val loadIndex =
      function.instructions.indexWhere(i =>
        i.mnemonic.raw == "LDG" && i.operands.exists { operand =>
          OperandPrinter.print(operand).toOption.exists(_.contains(".U32+UR"))
        }
      )
    assert(loadIndex >= 0)
    val load         = function.instructions(loadIndex)
    val addressIndex = load.operands.indexWhere(operand =>
      OperandPrinter.print(operand).toOption.exists(_.contains(".U32+UR"))
    )
    def changedAddress(value: String): DecodedFunction =
      function.copy(instructions =
        function.instructions.updated(
          loadIndex,
          load.rewrite(operands = load.operands.updated(addressIndex, SASSFixture.operand(value)))
        )
      )
    def rejected(candidateModule: Cubin.Module, candidate: DecodedFunction): Unit =
      assert(
        SASSFixture.lower(
          candidateModule,
          kernel,
          Vec(candidate)
        ).left.exists(_.contains("unsupported .U32 uniform global address"))
      )

    rejected(module, changedAddress("[R12.U32+UR4+0x4]"))
    rejected(module, changedAddress("[R12.U32]"))
    val storeOperation = Parser.parse("STG.E.SYS [R12.U32+UR4], R8;").fold(fail(_), identity)
    val store          = load.rewrite(
      mnemonic = storeOperation.mnemonic,
      modifiers = storeOperation.modifiers,
      operands = storeOperation.operands,
      guard = storeOperation.guard
    )
    rejected(module, function.copy(instructions = function.instructions.updated(loadIndex, store)))
    rejected(module.copy(sm = 89), function)
  }
}
