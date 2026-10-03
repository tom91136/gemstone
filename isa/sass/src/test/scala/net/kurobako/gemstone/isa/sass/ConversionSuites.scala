package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class ConversionSuites extends munit.FunSuite {
  {
    def fixtureModule(sm: Int): Cubin.Module = SASSFixture.module(
      sm,
      Vec(SASSFixture.Kernel(
        "f2f_conversion_probe",
        352,
        Vec((0, 8), (8, 8), (16, 8), (24, 8), (32, 4)),
        registers = 16
      ))
    )

    val f64Input    = NumericCases.f64ToF32.mapNonEmpty(_._1)
    val f32Expected = NumericCases.f64ToF32.mapNonEmpty(_._2)
    val f32Input    = NumericCases.f32ToF64.mapNonEmpty(_._1)
    val f64Expected = NumericCases.f32ToF64.mapNonEmpty(_._2)

    val architectures = Vec(
      70 -> ConversionFixtures.f2fF32F64Sm70Probe,
      75 -> ConversionFixtures.f2fF32F64Sm75Probe,
      80 -> ConversionFixtures.f2fF32F64Sm80Probe,
      86 -> ConversionFixtures.f2fF32F64Sm86Probe,
      89 -> ConversionFixtures.f2fF32F64Sm89Probe
    )

    def words32(values: Vec[Long]): Vec[Byte] =
      Vec.from(values).flatMap(LittleEndian.toVec(_, 4))
    def words64(values: Vec[Long]): Vec[Byte] =
      Vec.from(values).flatMap(LittleEndian.toVec(_, 8))
    test("F2F narrowing and widening match the compiler fixtures and hardware reference values") {
      for ((sm, text) <- architectures) {
        val module = fixtureModule(sm)
        val kernel = module.kernels.find(_.name == "f2f_conversion_probe").get
        val sass   = ListingDecoder.parse(text, kernel.name, sm).fold(fail(_), _.head)
        assertEquals(
          sass.instructions.count(i => i.mnemonic.raw == "F2F" && i.modifiers.map(_.name) == Vec("F32", "F64")),
          1
        )
        assertEquals(
          sass.instructions.count(i => i.mnemonic.raw == "F2F" && i.modifiers.map(_.name) == Vec("F64", "F32")),
          1
        )
        val lowered = SASSFixture(module, kernel, Vec(sass))

        locally {
          val service = SASSFixture.Execution()
          val n       = f64Input.positiveSize.max(f32Input.positiveSize)
          val in64    = service.allocate(words64(f64Input ++ Vec.fill(n.raw - f64Input.size)(0L)))
          val in32    = service.allocate(words32(f32Input ++ Vec.fill(n.raw - f32Input.size)(0L)))
          val out32   = service.allocate(n.toLong.widen[Long > 0] * 4L)
          val out64   = service.allocate(n.toLong.widen[Long > 0] * 8L)
          val program = service.compile(lowered)
            .fold(e => fail(e.message), identity)
          val task = SASSFixture.task(
            kernel.name,
            LaunchGeometry.Dimensions(n, 1, 1),
            LaunchGeometry.Dimensions.One,
            Vec(
              KernelTask.Argument.Memory(out32, 0L),
              KernelTask.Argument.Memory(in64, 0L),
              KernelTask.Argument.Memory(out64, 0L),
              KernelTask.Argument.Memory(in32, 0L),
              KernelTask.Argument.Scalar(LittleEndian.toVec(n.toLong.raw, 4))
            )
          )
          service.run(program, task).fold(e => fail(e.message), identity)
          assertEquals(
            service.readWords(out32, f32Expected.nonNegativeSize, width = 4),
            f32Expected
          )
          assertEquals(
            service.readWords(out64, f64Expected.nonNegativeSize, width = 8),
            f64Expected
          )
        }
      }
    }

    test("F2F rejects unverified rounding modifiers and architectures") {
      val module = fixtureModule(89)
      val kernel = module.kernels.find(_.name == "f2f_conversion_probe").get
      val sass   = ListingDecoder.parse(ConversionFixtures.f2fF32F64Sm89Probe, kernel.name, 89)
        .fold(fail(_), _.head)
      val conversion =
        sass.instructions.find(i => i.mnemonic.raw == "F2F" && i.modifiers.map(_.name) == Vec("F32", "F64")).get
      val modified = sass.copy(instructions =
        sass.instructions.map(i =>
          if (i == conversion) i.rewrite(modifiers = i.modifiers.updated(0, Instruction.Modifier("RZ"))) else i
        )
      )
      assert(SASSFixture.lower(module, kernel, Vec(modified)).isLeft)
      assert(SASSFixture.lower(module.copy(sm = 90), kernel, Vec(sass)).isLeft)
    }
  }

  {
    val registerCounts                       = Map(70 -> 10, 75 -> 4, 89 -> 10)
    def fixtureModule(sm: Int): Cubin.Module = SASSFixture.module(
      sm,
      Vec(SASSFixture.Kernel(
        "probe",
        352,
        Vec((0, 8), (8, 8)),
        registers = registerCounts.getOrElse(sm, fail(s"unsupported F2F fixture sm_$sm"))
      ))
    )

    val Kernel: NonEmpty[String] = "probe"

    val listings = Vec(
      70 -> ConversionFixtures.f2fF16F32Sm75ProbeSm70,
      75 -> ConversionFixtures.f2fF16F32Sm75ProbeSm75,
      89 -> ConversionFixtures.f2fF16F32Sm75ProbeSm89
    )

    val inputs = NumericCases.f32ToF16.mapNonEmpty(_._1)

    def module(sm: Int): Cubin.Module = fixtureModule(sm)

    def function(sm: Int): DecodedFunction = {
      val text = listings.find(_._1 == sm).map(_._2).getOrElse(fail(s"missing F2F fixture sm_$sm"))
      ListingDecoder.parse(text, Kernel, sm).fold(fail(_), _.head)
    }

    def execute(image: Cubin.Module, sass: DecodedFunction): Vec[Long] = {
      val kernel     = image.kernels.find(_.name == Kernel).getOrElse(fail("F2F probe kernel is missing"))
      val source     = SASSFixture(image, kernel, Vec(sass))
      val inputBytes = Vec.from(inputs).flatMap(LittleEndian.toVec(_, 4))
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(inputs.positiveSize.toLong.widen[Long > 0] * 4L)
        val input   = service.allocate(inputBytes)
        val program = service.compile(source)
          .fold(error => fail(error.message), identity)
        val task = SASSFixture.task(
          Kernel,
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions(inputs.positiveSize, 1, 1),
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Memory(input, 0L)
          )
        )
        service.run(program, task).fold(error => fail(error.message), identity)
        service.readWords(output, inputs.nonNegativeSize).map(_.toLong & 0xffffffffL)
      }
    }

    test("compiler-produced F2F.F16.F32 rounds, canonicalizes NaNs, and zero-extends") {
      Vec(70, 75, 89).foreach { sm =>
        val sass = function(sm)
        assert(sass.instructions.exists(i =>
          i.mnemonic.raw == "F2F" && i.modifiers.map(_.name) == Vec("F16", "F32") && i.operands.size == 2
        ))
        assertEquals(execute(module(sm), sass), NumericCases.f32ToF16.mapNonEmpty(_._2), s"sm_$sm")
      }
    }

    test("F2F.F16.F32 lowering preserves generic predicate handling") {
      val sass = function(75)
      val f2f = sass.instructions.find(i => i.mnemonic.raw == "F2F" && i.modifiers.map(_.name) == Vec("F16", "F32")).get
      val setPredicate = SASSFixture.instruction(
        f2f.address.raw - 8,
        "ISETP.EQ.U32.AND",
        Vec("P0", "PT", "RZ", "RZ", "PT"),
        None
      )
      val predicated = sass.copy(instructions = sass.instructions.flatMap { instruction =>
        if (instruction eq f2f) Vec(
          setPredicate,
          instruction.rewrite(
            guard = Some(Instruction.Guard(Operand.Predicate(Some(0), uniform = false, negated = false)))
          )
        )
        else Vec(instruction)
      })
      assertEquals(execute(module(75), predicated), NumericCases.f32ToF16.mapNonEmpty(_._2))
    }
  }

  {
    def fixtureModule: Cubin.Module = SASSFixture.module(
      80,
      Vec(
        SASSFixture.Kernel("i2f_f16_immediate_probe", 352, Vec((0, 8)), registers = 8),
        SASSFixture.Kernel("i2f_f16_probe", 352, Vec((0, 8), (8, 8)), registers = 10)
      )
    )

    val cases = Vec.nonEmpty(
      (0, 0x0000L),
      (1, 0x3c00L),
      (-1, 0xbc00L),
      (2, 0x4000L),
      (-2, 0xc000L),
      (1023, 0x63feL),
      (1024, 0x6400L),
      (2047, 0x67ffL),
      (2048, 0x6800L),
      (2049, 0x6800L),
      (2050, 0x6801L),
      (2051, 0x6802L),
      (2052, 0x6802L),
      (2053, 0x6802L),
      (-2049, 0xe800L),
      (-2051, 0xe802L),
      (65504, 0x7bffL),
      (65519, 0x7bffL),
      (65520, 0x7c00L),
      (-65504, 0xfbffL),
      (16777217, 0x7c00L),
      (-16777217, 0xfc00L),
      (Int.MaxValue, 0x7c00L),
      (Int.MinValue, 0xfc00L)
    )

    def execute(
        kernelName: NonEmpty[String],
        sassText: String,
        input: Option[Vec[Long]],
        count: Int > 0,
        expected: Vec[Long]
    ): Unit = {
      val module   = fixtureModule
      val function = ListingDecoder.parse(sassText, kernelName, 80).fold(fail(_), _.head)
      val sass     = SASSFixture(module, module.kernels.find(_.name == kernelName).get, Vec(function))
      val inputs   = input.map(values => Vec.from(values).flatMap(LittleEndian.toVec(_, 4)))
      locally {
        val service         = SASSFixture.Execution()
        val output          = service.allocate(count.toLong.widen[Long > 0] * 4L)
        val inputAllocation = inputs.map(service.allocate)
        val args            = inputAllocation match {
          case Some(allocation) => Vec(
              KernelTask.Argument.Memory(allocation, 0L),
              KernelTask.Argument.Memory(output, 0L)
            )
          case None => Vec(KernelTask.Argument.Memory(output, 0L))
        }
        val program = service.compile(sass)
          .fold(error => fail(error.message), identity)
        val task = SASSFixture.task(
          kernelName,
          LaunchGeometry.Dimensions(count, 1, 1),
          LaunchGeometry.Dimensions.One,
          args
        )
        service.run(program, task).fold(error => fail(error.message), identity)
        val actual = service.readWords(output, count)
        assertEquals(actual, expected)
      }
    }

    test("compiler-produced I2F.F16 converts signed integers with RN and zero-extends the half result") {
      val sassText = ConversionFixtures.i2fF16Sm80I2fF16Sm80
      assert(sassText.contains("I2F.F16 R7, R2"))
      execute(
        "i2f_f16_probe",
        ConversionFixtures.i2fF16Sm80I2fF16Sm80,
        Some(cases.map { case (value, _) => value.toLong & 0xffffffffL }),
        cases.positiveSize,
        cases.map(_._2)
      )
    }

    test("compiler-produced I2F.F16 converts an immediate integer source") {
      assert(ConversionFixtures.i2fF16Sm80I2fF16ImmediateSm80.contains("I2F.F16 R5, 0x1"))
      execute(
        "i2f_f16_immediate_probe",
        ConversionFixtures.i2fF16Sm80I2fF16ImmediateSm80,
        None,
        4,
        Vec.fill(4)(0x3c00L)
      )
    }

    test("I2F.F16 lowering rejects unverified rounding modifiers") {
      val module  = fixtureModule
      val text    = ConversionFixtures.i2fF16Sm80I2fF16Sm80
      val parsed  = ListingDecoder.parse(text, "i2f_f16_probe", 80).fold(fail(_), _.head)
      val altered = parsed.copy(instructions = parsed.instructions.map { instruction =>
        if (instruction.mnemonic.raw == "I2F" && instruction.modifiers.headOption.exists(_.name == "F16"))
          instruction.rewrite(modifiers = instruction.modifiers :+ Instruction.Modifier("RZ"))
        else instruction
      })
      assert(SASSFixture.lower(module, module.kernels.find(_.name == "i2f_f16_probe").get, Vec(altered)).isLeft)
    }
  }

  {
    def fixtureModule: Cubin.Module =
      SASSFixture.module(86, Vec(SASSFixture.Kernel("convert", 352, Vec((0, 8), (8, 4)), registers = 14)))

    test("sm_86 I2FP and I2F preserve signed conversion rounding modes") {
      val module   = fixtureModule
      val sassText = ConversionFixtures.i2fpSm86
      assert(sassText.contains("I2FP.F32.S32 R5"))
      assert(sassText.contains("I2FP.F32.S32.RZ R11"))
      val function = ListingDecoder.parse(sassText, "convert", 86).fold(fail(_), _.head)
      val kernel   = module.kernels.find(_.name == "convert").get
      val sass     = SASSFixture(module, kernel, Vec(function))
      // Expected binary32 bits for RN, RM, RP, RZ, including inexact conversion at 2^24.
      val cases = Vec.nonEmpty(
        (0, Vec(0, 0, 0, 0)),
        (1, Vec.fill(4)(0x3f800000)),
        (-1, Vec.fill(4)(0xbf800000)),
        (16777217, Vec(0x4b800000, 0x4b800000, 0x4b800001, 0x4b800000)),
        (-16777217, Vec(0xcb800000, 0xcb800001, 0xcb800000, 0xcb800000)),
        (Int.MaxValue, Vec(0x4f000000, 0x4effffff, 0x4f000000, 0x4effffff)),
        (Int.MinValue, Vec.fill(4)(0xcf000000))
      )
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(16L)
        val program = service.compile(sass)
          .fold(e => fail(e.message), identity)
        cases.foreach { case (input, expected) =>
          val task = SASSFixture.task(
            "convert": NonEmpty[String],
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec(
              KernelTask.Argument.Memory(output, 0L),
              KernelTask.Argument.Scalar(LittleEndian.toVec(input.toLong, 4))
            )
          )
          service.run(program, task).fold(e => fail(e.message), identity)
          val actual = service.readWords(output, 4).map(_.toInt)
          assertEquals(actual, expected, s"input=$input")
        }
      }
    }
  }
}
