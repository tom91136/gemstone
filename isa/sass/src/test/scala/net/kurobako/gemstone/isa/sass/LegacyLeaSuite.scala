package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction as VMInstruction
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyLeaSuite extends munit.FunSuite {
  private def is(instruction: DecodedInstruction, mnemonic: String, modifiers: Instruction.Modifier*): Boolean =
    instruction.mnemonic.raw == mnemonic && instruction.modifiers == Vec.from(modifiers)

  private val carryDestination =
    Operand.Qualified(Operand.Register(RegisterBank.R, 0), Vec(OperandSelector.CC))
  private val divKernels = Vec("div_u5", "div_s24", "div_s6", "div_s5").map { name =>
    SASSFixture.Kernel(name, 320, Vec(0 -> 8, 8 -> 8), registers = 7)
  }
  private val pairKernels = Vec("lea_s31", "lea_s3", "lea_s2", "lea_s1", "lea_s0").map { name =>
    SASSFixture.Kernel(
      name,
      320,
      Vec(0 -> 8, 8 -> 4, 12 -> 4, 16 -> 4, 20 -> 4),
      registers = if (name == "lea_s0") 5 else 6
    )
  }
  private def fixtureModule(sm: Int, division: Boolean = false): Cubin.Module =
    SASSFixture.module(sm, if (division) divKernels else pairKernels)

  private val pairListings = Map(
    (50, 0)  -> LeaFixtures.leaSm50PairsSm50LeaS0,
    (50, 1)  -> LeaFixtures.leaSm50PairsSm50LeaS1,
    (50, 2)  -> LeaFixtures.leaSm50PairsSm50LeaS2,
    (50, 3)  -> LeaFixtures.leaSm50PairsSm50LeaS3,
    (50, 31) -> LeaFixtures.leaSm50PairsSm50LeaS31,
    (60, 0)  -> LeaFixtures.leaSm60PairsSm60LeaS0,
    (60, 1)  -> LeaFixtures.leaSm60PairsSm60LeaS1,
    (60, 2)  -> LeaFixtures.leaSm60PairsSm60LeaS2,
    (60, 3)  -> LeaFixtures.leaSm60PairsSm60LeaS3,
    (60, 31) -> LeaFixtures.leaSm60PairsSm60LeaS31
  )
  private def pairListing(sm: Int, shift: Int): String =
    pairListings.getOrElse((sm, shift), fail(s"unknown LEA pair fixture sm_$sm shift $shift"))

  private val divisionListings = Map(
    (50, "s5")  -> LeaFixtures.leaSm50DivDivSm50DivS5,
    (50, "s6")  -> LeaFixtures.leaSm50DivDivSm50DivS6,
    (50, "s24") -> LeaFixtures.leaSm50DivDivSm50DivS24,
    (60, "s5")  -> LeaFixtures.leaSm60DivDivSm60DivS5,
    (60, "s6")  -> LeaFixtures.leaSm60DivDivSm60DivS6,
    (60, "s24") -> LeaFixtures.leaSm60DivDivSm60DivS24
  )
  private def divisionListing(sm: Int, suffix: String): String =
    divisionListings.getOrElse((sm, suffix), fail(s"unknown LEA division fixture sm_$sm/$suffix"))

  private def scalar(value: Int): KernelTask.Argument.Scalar =
    KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong & 0xffffffffL, 4))

  private val inputs = Vec(
    (0, 0, 0, 0),
    (0x12345678, 0x10203040, 0x55667788, 0x99aabbcc),
    (0xffffffff, 0x00000001, 0x00000001, 0x00000002),
    (0x40000000, 0x80000000, 0x80000000, 0x00000001),
    (0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff),
    (0x87654321, 0x13579bdf, 0xfedcba98, 0x2468ace0)
  )

  private def expected(words: (Int, Int, Int, Int), shift: Int): (Int, Int) = {
    val mask                            = (BigInt(1) << 64) - 1
    def unsigned(value: Int): BigInt    = BigInt(java.lang.Integer.toUnsignedLong(value))
    val (low, upper, baseLow, baseHigh) = words
    val offset                          = (unsigned(upper) << 32) | unsigned(low)
    val base                            = (unsigned(baseHigh) << 32) | unsigned(baseLow)
    val result                          = ((offset << shift) + base) & mask
    ((result & 0xffffffff).toLong.toInt, ((result >> 32) & 0xffffffff).toLong.toInt)
  }

  private def intAt(bytes: Vec[Byte], offset: Int): Int = (bytes(offset).toInt & 255) |
    ((bytes(offset + 1).toInt & 255) << 8) |
    ((bytes(offset + 2).toInt & 255) << 16) | (bytes(offset + 3).toInt << 24)

  private def words(values: Vec[Int]): Vec[Byte] =
    Vec.from(values.flatMap(value => LittleEndian.toVec(value.toLong & 0xffffffffL, 4)))

  for {
    sm    <- Vec(50, 60)
    shift <- Vec(0, 1, 2, 3, 31)
  }
    test(s"CUDA 11.8 sm_$sm 64-bit address pair matches unsigned arithmetic at shift $shift") {
      val module   = fixtureModule(sm)
      val name     = s"lea_s$shift"
      val kernel   = module.kernels.find(_.name == name).getOrElse(fail(s"missing $name kernel"))
      val sassText = pairListing(sm, shift)
      val function = ListingDecoder.parse(sassText, name, sm).fold(fail(_), _.head)
      if (shift == 0) assert(function.instructions.exists(_.mnemonic.raw == "IADD"))
      else {
        val low  = function.instructions.find(_.mnemonic.raw == "LEA").getOrElse(fail("missing LEA.CC"))
        val high = function.instructions.find(is(
          _,
          "LEA",
          Instruction.Modifier.HI,
          Instruction.Modifier.X
        )).getOrElse(fail("missing LEA.HI.X"))
        assertEquals(low.operands.head, carryDestination)
        assertEquals(high.operands.head, Operand.Register(RegisterBank.R, 4))
        assertEquals(high.operands(1), Operand.Register(RegisterBank.R, 4)) // destination aliases source A
        val lowIndex  = function.instructions.indexOf(low)
        val highIndex = function.instructions.indexOf(high)
        val between   = function.instructions.slice(lowIndex + 1, highIndex)
        if (sm == 60) assert(between.nonEmpty) // Pascal schedules ordinary MOVs between the instructions.
      }
      val lowered = SASSFixture(module, kernel, Vec(function))
      if (shift != 0) assert(lowered.instructions.exists {
        case VMInstruction.ReadConditionFlag(_, VMConditionCodes.Flag.Carry, true) => true
        case _                                                                     => false
      })

      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(8L.unsafe[Long > 0])
        val program = service.compile(lowered)
          .fold(error => fail(error.message), identity)
        inputs.foreach { words =>
          val task = SASSFixture.task(
            name.unsafe[NonEmpty[String]],
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec(
              KernelTask.Argument.Memory(output, 0L),
              scalar(words._1),
              scalar(words._2),
              scalar(words._3),
              scalar(words._4)
            )
          )
          service.run(program, task).fold(error => fail(error.message), identity)
          val bytes = service.read(output, 0L, 8).toOption.get
          assertEquals((intAt(bytes, 0), intAt(bytes, 4)), expected(words, shift), s"inputs=$words")
        }
      }
    }

  for {
    sm                       <- Vec(50, 60)
    (suffix, divisor, shift) <- Vec(("s5", 32, 5), ("s6", 64, 6), ("s24", 16777216, 24))
  }
    test(s"CUDA 11.8 sm_$sm no-carry LEA.HI folds signed division correction by $divisor") {
      val module = fixtureModule(sm, division = true)
      assertEquals(module.sm, sm)
      val name   = s"div_$suffix"
      val kernel = module.kernels.find(_.name == name).getOrElse(fail(s"missing $name kernel"))
      val text   = divisionListing(sm, suffix)
      assert(text.contains(s"LEA.HI R0, R0, R2, RZ, 0x${shift.toHexString}"), text)
      val function = ListingDecoder.parse(text, name, sm).fold(fail(_), _.head)
      val lea      = function.instructions.find(is(_, "LEA", Instruction.Modifier.HI)).getOrElse(fail("missing LEA.HI"))
      assertEquals(lea.operands(3), Operand.Register(RegisterBank.R, 255))
      val lowered = SASSFixture(module, kernel, Vec(function))
      val count   = 128
      val inputs  = Vec(
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
      ) ++ Vec.tabulate(count - 25)(i => (i * 7919) ^ 0x9e3779b9)
      locally {
        val service = SASSFixture.Execution()
        val bytes   = count * 4L
        val output  = service.allocate(bytes.unsafe[Long > 0])
        val input   = service.allocate(bytes.unsafe[Long > 0])
        assert(service.write(input, 0L, words(inputs)).isRight)
        val program = service.compile(lowered)
          .fold(error => fail(error.message), identity)
        val task = SASSFixture.task(
          name.unsafe[NonEmpty[String]],
          LaunchGeometry.Dimensions(count.unsafe[Int > 0], 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Memory(input, 0L)
          )
        )
        service.run(program, task).fold(error => fail(error.message), identity)
        val actual = service.read(output, 0L, (count * 4).unsafe[Int >= 0])
          .toOption.get
        inputs.indices.foreach { i =>
          assertEquals(intAt(actual, i * 4), inputs(i) / divisor, s"input=${inputs(i)}")
        }
      }
    }

  test("sm_50/sm_60 LEA rejects forms outside the CUDA 11.8 compiler fixture") {
    val sm       = 60
    val module   = fixtureModule(sm)
    val sassText = pairListing(sm, 2)
    val function = ListingDecoder.parse(sassText, "lea_s2", sm).fold(fail(_), _.head)
    val original = function.instructions.find(is(_, "LEA", Instruction.Modifier.HI, Instruction.Modifier.X)).get
    Vec(
      original.rewrite(operands = original.operands.updated(4, Operand.Integer(BigInt(0x20)))),
      original.rewrite(operands =
        original.operands.updated(2, Operand.Qualified(Operand.Register(RegisterBank.R, 4), Vec(OperandSelector.H1)))
      ),
      original.rewrite(modifiers = Vec(Instruction.Modifier.HI)),
      original.rewrite(operands =
        original.operands.updated(0, Operand.Qualified(Operand.Register(RegisterBank.R, 4), Vec(OperandSelector.CC)))
      )
    ).foreach { invalid =>
      val changed = function.copy(instructions = function.instructions.map(i => if (i == original) invalid else i))
      assert(SASSFixture.lower(module, module.kernels.find(_.name == "lea_s2").get, Vec(changed)).isLeft)
    }
    val predicated =
      original.rewrite(guard = Some(Instruction.Guard(Operand.Predicate(Some(0), uniform = false, negated = false))))
    val changed = function.copy(instructions = function.instructions.map(i => if (i == original) predicated else i))
    assert(SASSFixture.lower(module, module.kernels.find(_.name == "lea_s2").get, Vec(changed)).isRight)
  }

  test("predicated LEA carry pairs execute and skip together when their guard is equal") {
    val sm       = 50
    val module   = fixtureModule(sm)
    val name     = "lea_s2"
    val kernel   = module.kernels.find(_.name == name).get
    val listing  = pairListing(sm, 2)
    val function = ListingDecoder.parse(listing, name, sm).fold(fail(_), _.head)
    val low      = function.instructions.find(i => i.mnemonic.raw == "LEA" && i.operands.head == carryDestination).get
    val high     = function.instructions.find(is(_, "LEA", Instruction.Modifier.HI, Instruction.Modifier.X)).get
    val expectedValue = expected((0x12345678, 0x10203040, 0x55667788, 0x99aabbcc), 2)
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(8L.unsafe[Long > 0])
      Vec(("EQ", true), ("NE", false)).foreach { case (comparison, execute) =>
        assert(service.write(output, 0L, words(Vec(0xdeadbeef, 0x13579bdf))).isRight)
        val setGuard =
          SASSFixture.instruction(low.address.raw - 1, s"ISETP.$comparison.AND", Vec("P1", "PT", "RZ", "RZ", "PT"))
        val guarded = function.copy(instructions = function.instructions.map { instruction =>
          if (
            instruction.address == low.address || instruction.address == high.address ||
            instruction.mnemonic.raw == "STG"
          )
            instruction.rewrite(guard =
              Some(Instruction.Guard(Operand.Predicate(Some(1), uniform = false, negated = false)))
            )
          else instruction
        }.patch(function.instructions.indexOf(low), Vec(setGuard), 0))
        val sass    = SASSFixture(module, kernel, Vec(guarded))
        val program = service.compile(sass)
          .fold(error => fail(error.message), identity)
        val task = SASSFixture.task(
          name.unsafe[NonEmpty[String]],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            scalar(0x12345678),
            scalar(0x10203040),
            scalar(0x55667788),
            scalar(0x99aabbcc)
          )
        )
        service.run(program, task).fold(error => fail(error.message), identity)
        val bytes = service.read(output, 0L, 8).toOption.get
        if (execute) assertEquals((intAt(bytes, 0), intAt(bytes, 4)), expectedValue)
        else assertEquals((intAt(bytes, 0), intAt(bytes, 4)), (0xdeadbeef, 0x13579bdf))
      }
    }
  }

  test("shifted immediate LEA.CC/HI.X pairs match independent 64-bit wrap arithmetic") {
    Vec(50 -> 0x2400, 60 -> 0x1c00).foreach { case (sm, addend) =>
      val module  = fixtureModule(sm)
      val name    = "lea_s2"
      val kernel  = module.kernels.find(_.name == name).get
      val listing = pairListing(sm, 2)
      val parsed  = ListingDecoder.parse(listing, name, sm).fold(fail(_), _.head)
      val low     = parsed.instructions.find(i => i.mnemonic.raw == "LEA" && i.operands.head == carryDestination).get
      val high    = parsed.instructions.find(is(_, "LEA", Instruction.Modifier.HI, Instruction.Modifier.X)).get
      val changed = parsed.copy(instructions = parsed.instructions.map { instruction =>
        if (instruction.address == low.address)
          instruction.rewrite(operands =
            Vec(
              carryDestination,
              Operand.Register(RegisterBank.R, 4),
              Operand.Integer(BigInt(addend)),
              Operand.Integer(BigInt(2))
            )
          )
        else if (instruction.address == high.address)
          instruction.rewrite(operands =
            Vec(
              Operand.Register(RegisterBank.R, 4),
              Operand.Register(RegisterBank.R, 4),
              Operand.Register(RegisterBank.R, 255),
              Operand.Register(RegisterBank.R, 5),
              Operand.Integer(BigInt(2))
            )
          )
        else instruction
      })
      Vec("0x80000", "0xfc100", "0xfffff", "0x100000").foreach { encoded =>
        val unverifiedImmediate = changed.copy(instructions = changed.instructions.map { item =>
          if (item.address == low.address)
            item.rewrite(operands = item.operands.updated(2, Operand.Integer(BigInt(encoded.drop(2), 16))))
          else item
        })
        assert(SASSFixture.lower(module, kernel, Vec(unverifiedImmediate)).isLeft)
      }
      val sass  = SASSFixture(module, kernel, Vec(changed))
      val cases = Vec(
        0x00000000 -> 0x00000000,
        0xffffffff -> 0xffffffff,
        0xfffff000 -> 0x3fffffff,
        0x80000000 -> 0x80000000,
        0x12345678 -> 0xfedcba98
      )
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(8L.unsafe[Long > 0])
        val program = service.compile(sass)
          .fold(error => fail(error.message), identity)
        cases.foreach { case (lowWord, highWord) =>
          val task = SASSFixture.task(
            name.unsafe[NonEmpty[String]],
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec(
              KernelTask.Argument.Memory(output, 0L),
              scalar(lowWord),
              scalar(highWord),
              scalar(0),
              scalar(0)
            )
          )
          service.run(program, task).fold(error => fail(error.message), identity)
          val bytes = service.read(output, 0L, 8).toOption.get
          assertEquals(
            (intAt(bytes, 0), intAt(bytes, 4)),
            expected((lowWord, highWord, addend, 0), 2),
            s"sm_$sm low=0x${lowWord.toHexString} high=0x${highWord.toHexString}"
          )
        }
      }
    }
  }

  test("LEA.CC carry is visible to the following IADD.X") {
    Vec(50 -> 0x2400, 60 -> 0x1c00).foreach { case (sm, addend) =>
      val module  = fixtureModule(sm)
      val name    = "lea_s2"
      val kernel  = module.kernels.find(_.name == name).get
      val listing = pairListing(sm, 2)
      val parsed  = ListingDecoder.parse(listing, name, sm).fold(fail(_), _.head)
      val low     = parsed.instructions.find(i => i.mnemonic.raw == "LEA" && i.operands.head == carryDestination).get
      val high    = parsed.instructions.find(is(_, "LEA", Instruction.Modifier.HI, Instruction.Modifier.X)).get
      val changed = parsed.copy(instructions = parsed.instructions.map { instruction =>
        if (instruction.address == low.address)
          instruction.rewrite(operands =
            Vec(
              carryDestination,
              Operand.Register(RegisterBank.R, 4),
              Operand.Integer(BigInt(addend)),
              Operand.Integer(BigInt(2))
            )
          )
        else if (instruction.address == high.address)
          instruction.rewrite(
            mnemonic = "IADD".unsafe[NonEmpty[String]],
            modifiers = Vec(Instruction.Modifier.X),
            operands = Vec(
              Operand.Register(RegisterBank.R, 4),
              Operand.Register(RegisterBank.R, 5),
              Operand.Register(RegisterBank.R, 255)
            )
          )
        else instruction
      })
      val sass  = SASSFixture(module, kernel, Vec(changed))
      val cases = Vec(0x00000001 -> 0x12345678, 0x3fffffff -> 0x87654321)
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(8L.unsafe[Long > 0])
        val program = service.compile(sass)
          .fold(error => fail(error.message), identity)
        cases.foreach { case (lowWord, highWord) =>
          val task = SASSFixture.task(
            name.unsafe[NonEmpty[String]],
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec(
              KernelTask.Argument.Memory(output, 0L),
              scalar(lowWord),
              scalar(highWord),
              scalar(0),
              scalar(0)
            )
          )
          service.run(program, task).fold(error => fail(error.message), identity)
          val bytes        = service.read(output, 0L, 8).toOption.get
          val total        = BigInt(java.lang.Integer.toUnsignedLong(lowWord)) * 4 + addend
          val expectedLow  = (total & 0xffffffffL).toLong.toInt
          val expectedHigh = (highWord.toLong + (total >> 32).toLong).toInt
          assertEquals((intAt(bytes, 0), intAt(bytes, 4)), (expectedLow, expectedHigh))
        }
      }
    }
  }
}
