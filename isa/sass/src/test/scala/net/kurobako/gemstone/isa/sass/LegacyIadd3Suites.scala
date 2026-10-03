package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*
import scala.util.Random

class LegacyIadd3Suites extends munit.FunSuite {
  private def hasModifier(instruction: DecodedInstruction, modifier: Instruction.Modifier): Boolean =
    instruction.modifiers.contains(modifier)

  @annotation.tailrec
  private def baseOperand(operand: Operand): Operand = operand match {
    case Operand.Decorated(value, _) => baseOperand(value)
    case Operand.Qualified(value, _) => baseOperand(value)
    case other                       => other
  }

  private def hasSelector(operand: Operand, selector: OperandSelector): Boolean = operand match {
    case Operand.Qualified(value, selectors) => selectors.contains(selector) || hasSelector(value, selector)
    case Operand.Decorated(value, _)         => hasSelector(value, selector)
    case _                                   => false
  }

  private def predicateGuard(source: String): Option[Instruction.Guard] =
    SASSFixture.operand(source) match {
      case predicate: Operand.Predicate => Some(Instruction.Guard(predicate))
      case other                        => fail(s"expected predicate operand, found $other")
    }

  private def branch(address: Int, target: Int): DecodedInstruction = {
    val checkedAddress = address.refine[Int >= 0].fold(error => fail(error.raw), identity)
    val operation      = Instruction.decode(
      "BRA",
      Vec.empty,
      SASSFixture.operands(s"0x${target.toHexString}"),
      None
    ).fold(error => fail(error), identity)
    DecodedInstruction(checkedAddress, operation, None, None)
  }

  {
    val registerCounts = Map(
      "probe"          -> 12,
      "probe_constant" -> 10,
      "probe_multi"    -> 32
    )
    def fixtureModule(sm: Int, variant: NonEmpty[String]): Cubin.Module = {
      val registers = registerCounts(variant)
      SASSFixture.module(
        sm,
        Vec(SASSFixture.Kernel(variant, 320, Vec((0, 8), (8, 8), (16, 8), (24, 8)), registers = registers))
      )
    }

    def qwords(values: Vec[Long]): Vec[Byte] =
      Vec.from(values.flatMap(value => LittleEndian.toVec(value, 8)))

    test("CUDA 11.8 sm_50/sm_60 IADD3.CC/X compiler pairs preserve carry counts zero, one, and two") {
      Vec(50, 60).foreach {
        sm =>
          val module = fixtureModule(sm, "probe")
          assertEquals(module.sm.raw, sm)
          val listing = Map(50 -> Iadd3Fixtures.iadd3LegacyProbeSm50, 60 -> Iadd3Fixtures.iadd3LegacyProbeSm60)(sm)
          assert(listing.contains("IADD3 R8.CC, R6, R4, R2"))
          assert(listing.contains("IADD3.X R9, R7, R5, R3"))
          val function = ListingDecoder.parse(listing, "probe", sm).fold(fail(_), _.head)
          val kernel   = module.kernels.find(_.name == "probe").get
          val sass     = SASSFixture(module, kernel, Vec(function))

        val cases = Vec.nonEmpty(
          (0x0000000000000002L, 0x0000000000000004L, 0x0000000000000008L),
          (0x00000000ffffffffL, 0x0000000000000000L, 0x0000000000000001L),
          (0x00000000ffffffffL, 0x00000000ffffffffL, 0x00000000fffffffeL),
          (0x00000001ffffffffL, 0x00000001ffffffffL, 0x00000000fffffffeL),
          (0xffffffffffffffffL, 0xffffffffffffffffL, 0xfffffffffffffffeL)
        )
        val aValues = cases.map(_._1)
        val bValues = cases.map(_._2)
        val cValues = cases.map(_._3)
        locally {
          val service  = SASSFixture.Execution()
          val byteSize = cases.positiveSize.toLong.widen[Long > 0] * 8L
          val output   = service.allocate(byteSize)
          val a        = service.allocate(byteSize)
          val b        = service.allocate(byteSize)
          val c        = service.allocate(byteSize)
          assert(service.write(a, 0L, qwords(aValues)).isRight)
          assert(service.write(b, 0L, qwords(bValues)).isRight)
          assert(service.write(c, 0L, qwords(cValues)).isRight)
          val program = service.compile(sass)
            .fold(e => fail(e.message), identity)
          val task = SASSFixture.task(
            "probe": NonEmpty[String],
            LaunchGeometry.Dimensions(cases.positiveSize, 1, 1),
            LaunchGeometry.Dimensions.One,
            Vec(
              KernelTask.Argument.Memory(output, 0L),
              KernelTask.Argument.Memory(a, 0L),
              KernelTask.Argument.Memory(b, 0L),
              KernelTask.Argument.Memory(c, 0L)
            )
          )
          service.run(program, task).fold(e => fail(e.message), identity)
          val bytes  = service.read(output, 0L, cases.nonNegativeSize * 8).toOption.get
          val actual = cases.indices.map { i =>
            LittleEndian.decode(bytes, i * 8, 8)
          }.toVec
          val expected = cases.map { case (x, y, z) => BigInt(x) + BigInt(y) + BigInt(z) }
            .map(value => (value & ((BigInt(1) << 64) - 1)).toLong)
          assertEquals(actual, expected)
        }
      }
    }

    test("CUDA 11.8 sm_50/sm_60 IADD3.CC/X pairs accept parameter constants in both halves") {
      val cases = Vec.nonEmpty(
        0x0000000000000000L -> 0x0000000000000000L,
        0x00000000ffffffffL -> 0x0000000000000000L,
        0x00000000ffffffffL -> 0x00000000ffffffffL,
        0x0000000100000000L -> 0x00000000ffffffffL,
        0xffffffffffffffffL -> 0x0000000000000001L
      )
      val constants = Vec(0x00000000ffffffffL, 0x12345678ffffffffL, 0xffffffffffffffffL)

      Vec(50, 60).foreach { sm =>
        val module = fixtureModule(sm, "probe_constant")
        assertEquals(module.sm.raw, sm)
        val listing =
          Map(50 -> Iadd3Fixtures.iadd3LegacyProbeConstantSm50, 60 -> Iadd3Fixtures.iadd3LegacyProbeConstantSm60)(sm)
        assert(listing.contains("IADD3 R6.CC, R4, c[0x0][0x158], R2"))
        assert(listing.contains("IADD3.X R7, R5, c[0x0][0x15c], R3"))
        val function = ListingDecoder.parse(listing, "probe_constant", sm).fold(fail(_), _.head)
        val kernel   = module.kernels.find(_.name == "probe_constant").get
        val sass     = SASSFixture(module, kernel, Vec(function))
        val byteSize = cases.positiveSize.toLong.widen[Long > 0] * 8L
        constants.foreach { constant =>
          locally {
            val service = SASSFixture.Execution()
            val output  = service.allocate(byteSize)
            val a       = service.allocate(byteSize)
            val b       = service.allocate(byteSize)
            assert(service.write(a, 0L, qwords(cases.map(_._1))).isRight)
            assert(service.write(b, 0L, qwords(cases.map(_._2))).isRight)
            val program = service.compile(sass)
              .fold(error => fail(error.message), identity)
            val task = SASSFixture.task(
              "probe_constant": NonEmpty[String],
              LaunchGeometry.Dimensions(cases.positiveSize, 1, 1),
              LaunchGeometry.Dimensions.One,
              Vec(
                KernelTask.Argument.Memory(output, 0L),
                KernelTask.Argument.Memory(a, 0L),
                KernelTask.Argument.Memory(b, 0L),
                KernelTask.Argument.Scalar(LittleEndian.toVec(constant, 8))
              )
            )
            service.run(program, task).fold(error => fail(error.message), identity)
            val bytes  = service.read(output, 0L, cases.nonNegativeSize * 8).toOption.get
            val actual = cases.indices.map { i =>
              LittleEndian.decode(bytes, i * 8, 8)
            }.toVec
            val mask     = (BigInt(1) << 64) - 1
            val expected = cases.map { case (x, y) => ((BigInt(x) + BigInt(y) + BigInt(constant)) & mask).toLong }
            assertEquals(actual, expected, s"sm_$sm parameter-constant ${constant.toHexString} IADD3 pair")
          }
        }
      }
    }

    test("CUDA 11.8 sm_50/sm_60 IADD3 carry survives an unrelated scheduled store") {
      val maxLow = 0x00000000ffffffffL
      val rows   = Vec.nonEmpty(
        Vec.nonEmpty(
          (0L, 0L, 0L),
          (maxLow, 0L, 0L),
          (maxLow, maxLow, 0L),
          (maxLow, maxLow, maxLow)
        ),
        Vec.nonEmpty(
          (0x00000001ffffffffL, 1L, maxLow),
          (0x12345678ffffffffL, 0x87654321ffffffffL, 0xfedcba98ffffffffL),
          (0x7fffffffffffffffL, 0x0000000000000001L, 0x8000000000000000L),
          (0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL)
        ),
        Vec.nonEmpty(
          (0x01020304aabbccddL, 0x10203040ddeeff01L, 0x5566778899aabbccL),
          (0x80000000ffffffffL, 0x7fffffff00000001L, 0x00000000ffffffffL),
          (0x00000000ffffffffL, 0x0000000000000001L, 0x0000000000000001L),
          (0xaaaaaaaaaaaaaaaaL, 0x5555555555555555L, 1L)
        ),
        Vec.nonEmpty(
          (0x1111111122222222L, 0x3333333344444444L, 0x5555555566666666L),
          (0x00000000ffffffffL, 0x00000000ffffffffL, 0x0000000000000001L),
          (0xdeadbeefffffffffL, 0x10203040ffffffffL, 0x99887766ffffffffL),
          (0x0000000000000001L, 0x0000000000000002L, 0x0000000000000003L)
        )
      )
      val triples  = rows.flatMapNonEmpty(identity)
      val mask     = (BigInt(1) << 64) - 1
      val expected = triples.map { case (a, b, c) =>
        ((BigInt(a) + BigInt(b) + BigInt(c)) & mask).toLong
      }

      Vec(50, 60).foreach { sm =>
        val module  = fixtureModule(sm, "probe_multi")
        val listing = sm match {
          case 50    => Iadd3Fixtures.iadd3LegacyProbeMultipairSm50;
          case 60    => Iadd3Fixtures.iadd3LegacyProbeMultipairSm60;
          case other => fail(s"unknown fixture architecture $other")
        }
        assertEquals(module.sm.raw, sm)
        assert(listing.contains("IADD3 R12.CC, R28, R8, R2"))
        assert(listing.contains("STG.E.64 [R18], R20"))
        assert(listing.contains("IADD3.X R13, R29, R9, R3"))
        val function = ListingDecoder.parse(listing, "probe_multi", sm).fold(fail(_), _.head)
        val kernel   = module.kernels.find(_.name == "probe_multi").get
        val sass     = SASSFixture(module, kernel, Vec(function))
        val byteSize = triples.positiveSize.toLong.widen[Long > 0] * 8L
        locally {
          val service = SASSFixture.Execution()
          val output  = service.allocate(byteSize)
          val a       = service.allocate(byteSize)
          val b       = service.allocate(byteSize)
          val c       = service.allocate(byteSize)
          assert(service.write(a, 0L, qwords(triples.map(_._1))).isRight)
          assert(service.write(b, 0L, qwords(triples.map(_._2))).isRight)
          assert(service.write(c, 0L, qwords(triples.map(_._3))).isRight)
          val program = service.compile(sass)
            .fold(error => fail(error.message), identity)
          val task = SASSFixture.task(
            "probe_multi": NonEmpty[String],
            LaunchGeometry.Dimensions(rows.positiveSize, 1, 1),
            LaunchGeometry.Dimensions.One,
            Vec(
              KernelTask.Argument.Memory(output, 0L),
              KernelTask.Argument.Memory(a, 0L),
              KernelTask.Argument.Memory(b, 0L),
              KernelTask.Argument.Memory(c, 0L)
            )
          )
          service.run(program, task).fold(error => fail(error.message), identity)
          val actual = service.read(output, 0L, triples.nonNegativeSize * 8).toOption.get
          assertEquals(actual, qwords(expected), s"sm_$sm scheduled carry pair")
        }
      }
    }

    test("guarded IADD3 carry crosses a branch, and a missing or skipped producer faults") {
      val sm            = 50
      val module        = fixtureModule(sm, "probe")
      val listing       = Map(50 -> Iadd3Fixtures.iadd3LegacyProbeSm50, 60 -> Iadd3Fixtures.iadd3LegacyProbeSm60)(sm)
      val function      = ListingDecoder.parse(listing, "probe", sm).fold(fail(_), _.head)
      val producerIndex = function.instructions.indexWhere(instruction =>
        instruction.mnemonic.raw == "IADD3" && instruction.operands.headOption.exists(operand =>
          hasSelector(operand, OperandSelector.CC)
        )
      )
      val producer = function.instructions(producerIndex)
      val consumer = function.instructions.find(instruction =>
        instruction.mnemonic.raw == "IADD3" && hasModifier(instruction, Instruction.Modifier.X)
      ).get
      val branchInstruction = branch(producer.address.raw + 1, consumer.address.raw)
      val validFunction     = function.copy(instructions =
        function.instructions
          .updated(producerIndex, producer.rewrite(guard = predicateGuard("PT")))
          .patch(producerIndex + 1, Vec(branchInstruction), 0)
      )
      val noProducerFunction = function.copy(instructions =
        function.instructions
          .filterNot(_.address == producer.address)
          .patch(producerIndex, Vec(branchInstruction), 0)
      )
      val kernel = module.kernels.find(_.name == "probe").get
      val values = Vec(0x00000000ffffffffL, 1L, 0x00000000ffffffffL)

      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(8L)
        val a       = service.allocate(8L)
        val b       = service.allocate(8L)
        val c       = service.allocate(8L)
        Vec(a, b, c).zip(values).foreach { case (allocation, value) =>
          assert(service.write(allocation, 0L, qwords(Vec(value))).isRight)
        }
        val valid   = SASSFixture(module, kernel, Vec(validFunction))
        val program =
          service.compile(valid).fold(error => fail(error.message), identity)
        val task = SASSFixture.task(
          "probe": NonEmpty[String],
          LaunchGeometry.Dimensions(1, 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Memory(a, 0L),
            KernelTask.Argument.Memory(b, 0L),
            KernelTask.Argument.Memory(c, 0L)
          )
        )
        service.run(program, task).fold(error => fail(error.message), identity)
        val expected = ((BigInt(values(0)) + BigInt(values(1)) + BigInt(values(2))) & ((BigInt(1) << 64) - 1)).toLong
        val bytes    = service.read(output, 0L, 8).toOption.get
        assertEquals(LittleEndian.decode(8)(bytes(_)), expected)

        val missing        = SASSFixture(module, kernel, Vec(noProducerFunction))
        val missingProgram = service.compile(missing)
          .fold(error => fail(error.message), identity)
        val missingCompletion = service.run(missingProgram, task)
        assert(missingCompletion.isLeft)

        val skippedFunction = function.copy(instructions =
          function.instructions
            .updated(producerIndex, producer.rewrite(guard = predicateGuard("!PT")))
            .patch(producerIndex + 1, Vec(branchInstruction), 0)
        )
        val skipped        = SASSFixture(module, kernel, Vec(skippedFunction))
        val skippedProgram = service.compile(skipped)
          .fold(error => fail(error.message), identity)
        val skippedCompletion = service.run(skippedProgram, task)
        assert(skippedCompletion.isLeft)
      }
    }
  }

  {
    val negativeKernelsLegacy = Vec(
      SASSFixture.Kernel("add_sub", 320, Vec((0, 8), (8, 8), (16, 8), (24, 8)), registers = 12),
      SASSFixture.Kernel("negative_constant_sub", 320, Vec((0, 8), (8, 8), (16, 8)), registers = 10),
      SASSFixture.Kernel("add_large_constant_sub", 320, Vec((0, 8), (8, 8), (16, 8)), registers = 10),
      SASSFixture.Kernel("add_constant_sub", 320, Vec((0, 8), (8, 8), (16, 8)), registers = 10)
    )
    val negativeKernelsModern = Vec(
      SASSFixture.Kernel("add_sub", 352, Vec((0, 8), (8, 8), (16, 8), (24, 8)), registers = 14),
      SASSFixture.Kernel("add_large_constant_sub", 352, Vec((0, 8), (8, 8), (16, 8)), registers = 12),
      SASSFixture.Kernel("add_constant_sub", 352, Vec((0, 8), (8, 8), (16, 8)), registers = 12)
    )
    def fixtureModule(sm: Int): Cubin.Module = {
      val constants: Vec[(String, Vec[Byte])] = if (sm == 50 || sm == 60)
        Vec(".nv.constant2.add_large_constant_sub" -> Vec[Byte](0, -63, 15, 0))
      else Vec.empty
      val kernels = if (sm == 50 || sm == 60) negativeKernelsLegacy
      else if (sm == 89) negativeKernelsModern
      else fail(s"unsupported negative IADD3 architecture sm_$sm")
      SASSFixture.module(sm, kernels, constants = constants)
    }

    val negativeListings = Map(
      (50, "add_sub")                -> Iadd3Fixtures.iadd3NegativeAddSubSm50,
      (60, "add_sub")                -> Iadd3Fixtures.iadd3NegativeAddSubSm60,
      (50, "add_constant_sub")       -> Iadd3Fixtures.iadd3NegativeAddConstantSubSm50,
      (60, "add_constant_sub")       -> Iadd3Fixtures.iadd3NegativeAddConstantSubSm60,
      (50, "add_large_constant_sub") -> Iadd3Fixtures.iadd3NegativeAddLargeConstantSubSm50,
      (60, "add_large_constant_sub") -> Iadd3Fixtures.iadd3NegativeAddLargeConstantSubSm60,
      (50, "negative_constant_sub")  -> Iadd3Fixtures.iadd3NegativeNegativeConstantSubSm50,
      (60, "negative_constant_sub")  -> Iadd3Fixtures.iadd3NegativeNegativeConstantSubSm60
    )
    val mask64 = (BigInt(1) << 64) - 1

    def unsigned(value: Long): BigInt = BigInt(value) & mask64

    def qwords(values: Vec[Long]): Vec[Byte] =
      Vec.from(values.flatMap(value => LittleEndian.toVec(value, 8)))

    def dwords(values: Vec[Int]): Vec[Byte] =
      Vec.from(values.flatMap(value => LittleEndian.toVec(value.toLong, 4)))

    def execute(
        service: SASSFixture.Execution,
        module: Cubin.Module,
        sm: Int,
        kernelName: NonEmpty[String],
        listing: String,
        inputBytes: Vec[Vec[Byte]],
        expected: NonEmpty[Vec[Long]]
    ): Unit = {
      val function = ListingDecoder.parse(
        listing,
        kernelName,
        sm
      ).fold(fail(_), _.head)
      val pair = function.instructions.filter(instruction =>
        instruction.mnemonic.raw == "IADD3"
      )
      assertEquals(pair.size, 2, s"sm_$sm $kernelName")
      assertEquals(
        pair.map(_.modifiers),
        Vec(Vec.empty[Instruction.Modifier], Vec(Instruction.Modifier.X)),
        s"sm_$sm $kernelName"
      )
      assert(hasSelector(pair.head.operands.head, OperandSelector.CC))
      if (kernelName == "negative_constant_sub") {
        assertEquals(pair.head.operands.drop(1), SASSFixture.operands("-R4, 0xfc100, R2"), s"sm_$sm $kernelName")
        assertEquals(pair(1).operands.drop(1), SASSFixture.operands("-R5, 0xfffff, R3"), s"sm_$sm $kernelName")
      }
      if (kernelName == "add_constant_sub")
        assertEquals(pair.head.operands(2), SASSFixture.operand("0x3f00"), s"sm_$sm $kernelName")
      if (kernelName == "add_large_constant_sub")
        assertEquals(pair.head.operands(2), SASSFixture.operand("c[0x2][0x0]"), s"sm_$sm $kernelName")
      val sass = SASSFixture(
        module,
        module.kernels.find(_.name == kernelName).get,
        Vec(function)
      )
      val program = service.compile(sass)
        .fold(error => fail(error.message), identity)
      val count  = expected.positiveSize
      val output = service.allocate(count.toLong.widen[Long > 0] * 8L)
      val inputs = inputBytes.map(bytes =>
        service.allocate(bytes)
      )
      inputs.zip(inputBytes).foreach { case (memory, bytes) =>
        assert(service.write(memory, 0L, bytes).isRight)
      }
      val args = inputs.map(memory =>
        KernelTask.Argument.Memory(memory, 0L)
      ) :+ KernelTask.Argument.Memory(output, 0L)
      val task = SASSFixture.task(
        kernelName,
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions(count, 1, 1),
        Vec.from(args)
      )
      service.run(program, task).fold(error => fail(error.message), identity)
      val bytes = service.read(
        output,
        0L,
        count * 8
      ).fold(error => fail(error.message), identity)
      val actual = bytes.indices.grouped(8).map(_.zipWithIndex.foldLeft(0L) { case (value, (index, byte)) =>
        value | ((bytes(index).toLong & 255L) << (byte * 8))
      }).toVec
      assertEquals(actual, expected, s"sm_$sm $kernelName")
    }

    val wideValues = Vec.nonEmpty(
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
    val narrowValues = Vec.nonEmpty(0, 1, 0x3eff, 0x3f00, 0x3f01, 0x7fffffff, Int.MinValue, -2, -1)

    val constantCases = {
      val boundary = wideValues.flatMapNonEmpty(a => narrowValues.mapNonEmpty(b => (a, b)))
      val random   = Random(0x51a7L)
      boundary.concatNonEmpty(Vec.fill(256)((random.nextLong(), random.nextInt())))
    }

    val subtractCases = {
      val boundary =
        wideValues.flatMapNonEmpty(a => wideValues.flatMapNonEmpty(b => wideValues.mapNonEmpty(c => (a, b, c))))
      val random = Random(0x51a8L)
      boundary.concatNonEmpty(Vec.fill(256)((random.nextLong(), random.nextLong(), random.nextLong())))
    }

    test("CUDA 11.8 sm_50/sm_60 negative-source IADD3 pairs implement full 64-bit expressions") {
      Vec(50, 60).foreach { sm =>
        val module = fixtureModule(sm)
        assertEquals(module.sm.raw, sm)
        locally {
          val service          = SASSFixture.Execution()
          val aConstant        = constantCases.mapNonEmpty(_._1)
          val bConstant        = constantCases.mapNonEmpty(_._2)
          val expectedConstant = constantCases.mapNonEmpty { case (a, b) =>
            (unsigned(a) + BigInt(0x3f00) - BigInt(b.toLong & 0xffffffffL) & mask64).toLong
          }
          execute(
            service,
            module,
            sm,
            "add_constant_sub",
            negativeListings((sm, "add_constant_sub")),
            Vec(qwords(aConstant), dwords(bConstant)),
            expectedConstant
          )

          val expectedLargeConstant = constantCases.mapNonEmpty { case (a, b) =>
            (unsigned(a) + BigInt(0xfc100) - BigInt(b.toLong & 0xffffffffL) & mask64).toLong
          }
          execute(
            service,
            module,
            sm,
            "add_large_constant_sub",
            negativeListings((sm, "add_large_constant_sub")),
            Vec(qwords(aConstant), dwords(bConstant)),
            expectedLargeConstant
          )

          val negativeConstantCases = {
            val boundary = wideValues.flatMapNonEmpty(a => wideValues.mapNonEmpty(b => (a, b)))
            val random   = Random(0x51a9L)
            boundary.concatNonEmpty(Vec.fill(256)((random.nextLong(), random.nextLong())))
          }
          val negativeA                = negativeConstantCases.mapNonEmpty(_._1)
          val negativeB                = negativeConstantCases.mapNonEmpty(_._2)
          val expectedNegativeConstant = negativeConstantCases.mapNonEmpty { case (a, b) =>
            (unsigned(a) - BigInt(0x3f00) - unsigned(b) & mask64).toLong
          }
          execute(
            service,
            module,
            sm,
            "negative_constant_sub",
            negativeListings((sm, "negative_constant_sub")),
            Vec(qwords(negativeA), qwords(negativeB)),
            expectedNegativeConstant
          )

          val aSubtract        = subtractCases.mapNonEmpty(_._1)
          val bSubtract        = subtractCases.mapNonEmpty(_._2)
          val cSubtract        = subtractCases.mapNonEmpty(_._3)
          val expectedSubtract = subtractCases.mapNonEmpty { case (a, b, c) =>
            (unsigned(a) + unsigned(b) - unsigned(c) & mask64).toLong
          }
          execute(
            service,
            module,
            sm,
            "add_sub",
            negativeListings((sm, "add_sub")),
            Vec(qwords(aSubtract), qwords(bSubtract), qwords(cSubtract)),
            expectedSubtract
          )
        }
      }
    }
  }

  {
    def fixtureModule(sm: Int): Cubin.Module =
      SASSFixture.module(
        sm,
        Vec(SASSFixture.Kernel("mulwide", 320, Vec((0, 8), (8, 8), (16, 8), (24, 4)), registers = 14))
      )

    for (execute <- Vec(None, Some(true), Some(false)))
      test(s"CUDA 11.8 sm_50/sm_60 IADD3.RS full products and register preservation, guard=$execute") {
        // Products force nonzero cross terms and overflow in the 32-bit sum of partials.
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
        Vec(50, 60).foreach { sm =>
          val module = fixtureModule(sm)
          assertEquals(module.sm.raw, sm)
          val listing = Map(50 -> Iadd3Fixtures.iadd3LegacyMulwideSm50, 60 -> Iadd3Fixtures.iadd3LegacyMulwideSm60)(sm)
          assert(listing.contains("IADD3.RS R7, R13, R10, R11"))
          val original = ListingDecoder.parse(listing, "mulwide", sm).fold(fail(_), _.head)
          val index    = original.instructions.indexWhere(instruction =>
            instruction.mnemonic.raw == "IADD3" && hasModifier(instruction, Instruction.Modifier.RS)
          )
          val rs                       = original.instructions(index)
          val previousDestinationWrite = original.instructions.take(index).reverse.find(instruction =>
            instruction.operands.headOption.exists(baseOperand(_) == SASSFixture.operand("R7"))
          ).get
          assertEquals(previousDestinationWrite.mnemonic.raw, "XMAD")
          assertEquals(
            previousDestinationWrite.operands.drop(1).map(baseOperand(_)),
            SASSFixture.operands("R2, R4, RZ")
          )
          val function = execute.fold(original) { enabled =>
            val guardOperation = rs.rewrite(
              mnemonic = "ISETP",
              modifiers =
                Vec(if (enabled) Instruction.Modifier.EQ else Instruction.Modifier.NE, Instruction.Modifier.AND),
              operands = SASSFixture.operands("P6, PT, RZ, RZ, PT"),
              guard = None
            )
            val guard = DecodedInstruction(
              (rs.address.raw - 1).refine[Int >= 0].fold(error => fail(error.raw), identity),
              guardOperation.operation,
              None,
              None
            )
            original.copy(instructions =
              original.instructions.patch(index, Vec(guard, rs.rewrite(guard = predicateGuard("P6"))), 1)
            )
          }
          val kernel = module.kernels.find(_.name == "mulwide").get
          val sass   = SASSFixture(module, kernel, Vec(function))

          locally {
            val service = SASSFixture.Execution()
            val output  = service.allocate(8L)
            val a       = service.allocate(4L)
            val b       = service.allocate(4L)
            val program = service.compile(sass)
              .fold(error => fail(error.message), identity)
            val task = SASSFixture.task(
              "mulwide": NonEmpty[String],
              LaunchGeometry.Dimensions.One,
              LaunchGeometry.Dimensions.One,
              Vec(
                KernelTask.Argument.Memory(output, 0L),
                KernelTask.Argument.Memory(a, 0L),
                KernelTask.Argument.Memory(b, 0L),
                KernelTask.Argument.Scalar(LittleEndian.toVec(1L, 4))
              )
            )
            inputs.foreach { case (left, right) =>
              assert(service.write(a, 0L, LittleEndian.toVec(left, 4)).isRight)
              assert(service.write(b, 0L, LittleEndian.toVec(right, 4)).isRight)
              service.run(program, task).fold(error => fail(error.message), identity)
              val bytes  = service.read(output, 0L, 8).toOption.get
              val actual = (0 until 8).foldLeft(0L)((value, index) =>
                value | ((bytes(index).toLong & 255L) << (index * 8))
              )
              // An inactive RS preserves R7, whose last compiler assignment is
              // the low-half product. The low output word is unchanged in both cases.
              val expected = if (execute.contains(false))
                ((left * right) & 0xffffffffL) | (((left & 65535L) * (right & 65535L)) << 32)
              else (BigInt(left) * BigInt(right)).toLong
              assertEquals(actual, expected, s"sm_$sm ${left.toHexString} * ${right.toHexString}")
            }
          }
        }
      }
  }

  {
    def fixtureModule: Cubin.Module = SASSFixture.module(
      35,
      Vec(
        SASSFixture.Kernel("eq64s", 320, Vec((0, 8), (8, 8), (16, 8)), registers = 6),
        SASSFixture.Kernel("eq64", 320, Vec((0, 8), (8, 8), (16, 8)), registers = 8)
      )
    )

    test("compiler-produced sm_35 64-bit equality extends low-word CC into high-word ISET.X") {
      val module = fixtureModule
      val cases  = Vec(
        (0L, 0L, Vec(1, 0, 1, 1)),
        (1L, 1L, Vec(1, 0, 0, 0)),
        (1L, 2L, Vec(0, 1, 0, 0)),
        (0x0000000100000000L, 0L, Vec(0, 1, 0, 1)),
        (0x0000000100000001L, 0x0000000100000002L, Vec(0, 1, 0, 0)),
        (Long.MinValue, Long.MinValue, Vec(1, 0, 0, 0)),
        (-1L, 0L, Vec(0, 1, 0, 1))
      )
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(16L)
        Vec[NonEmpty[String]]("eq64", "eq64s").foreach { name =>
          val sassText = name match {
            case "eq64"  => Iadd3Fixtures.eq64ExtendedSm35
            case "eq64s" => Iadd3Fixtures.eq64sExtendedSm35
          }
          assert(sassText.contains("ISUB RZ.CC"))
          assert(sassText.contains("ISET.EQ.X.AND"))
          assert(sassText.contains("ISET.NE.X.AND"))
          val function = ListingDecoder.parse(sassText, name, 35).fold(fail(_), _.head)
          val kernel   = module.kernels.find(_.name == name).get
          val sass     = SASSFixture(module, kernel, Vec(function))
          val program  = service.compile(sass)
            .fold(e => fail(e.message), identity)
          val count: Int > 0 = if (name == "eq64") 4 else 2
          cases.foreach { case (a, b, expectedAll) =>
            val task = SASSFixture.task(
              name,
              LaunchGeometry.Dimensions.One,
              LaunchGeometry.Dimensions.One,
              Vec(
                KernelTask.Argument.Memory(output, 0L),
                KernelTask.Argument.Scalar(LittleEndian.toVec(a, 8)),
                KernelTask.Argument.Scalar(LittleEndian.toVec(b, 8))
              )
            )
            service.run(program, task).fold(e => fail(e.message), identity)
            val expected = expectedAll.take(count.raw)
            val bytes    = service.read(output, 0L, count * 4).toOption.get
            val actual   = (0 until count.raw).map { i =>
              val at = i * 4
              (bytes(at) & 255) | ((bytes(at + 1) & 255) << 8) |
                ((bytes(at + 2) & 255) << 16) | (bytes(at + 3) << 24)
            }.toVec
            assertEquals(actual, expected)
          }
        }
      }
    }
  }
}
