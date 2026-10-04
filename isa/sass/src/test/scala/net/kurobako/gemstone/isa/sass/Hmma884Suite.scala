package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.*

class Hmma884Suite extends munit.FunSuite {
  private val Layouts = Vec.nonEmpty("row-col", "row-row", "col-col", "col-row")

  private val listings: Map[(String, String), String] = Map(
    ("f16", "col-col") -> Hmma884Fixtures.hmma884Sm70Hmma884F16ColColSm70,
    ("f16", "col-row") -> Hmma884Fixtures.hmma884Sm70Hmma884F16ColRowSm70,
    ("f16", "row-col") -> Hmma884Fixtures.hmma884Sm70Hmma884F16RowColSm70,
    ("f16", "row-row") -> Hmma884Fixtures.hmma884Sm70Hmma884F16RowRowSm70,
    ("f32", "col-col") -> Hmma884Fixtures.hmma884Sm70Hmma884F32ColColSm70,
    ("f32", "col-row") -> Hmma884Fixtures.hmma884Sm70Hmma884F32ColRowSm70,
    ("f32", "row-col") -> Hmma884Fixtures.hmma884Sm70Hmma884F32RowColSm70,
    ("f32", "row-row") -> Hmma884Fixtures.hmma884Sm70Hmma884F32RowRowSm70
  )
  private val directSources: Map[(String, String), String] = Map(
    ("f16", "col-col") -> Hmma884Fixtures.hmma884Sm70Hmma884F16ColColSource,
    ("f16", "col-row") -> Hmma884Fixtures.hmma884Sm70Hmma884F16ColRowSource,
    ("f16", "row-col") -> Hmma884Fixtures.hmma884Sm70Hmma884F16RowColSource,
    ("f16", "row-row") -> Hmma884Fixtures.hmma884Sm70Hmma884F16RowRowSource,
    ("f32", "col-col") -> Hmma884Fixtures.hmma884Sm70Hmma884F32ColColSource,
    ("f32", "col-row") -> Hmma884Fixtures.hmma884Sm70Hmma884F32ColRowSource,
    ("f32", "row-col") -> Hmma884Fixtures.hmma884Sm70Hmma884F32RowColSource,
    ("f32", "row-row") -> Hmma884Fixtures.hmma884Sm70Hmma884F32RowRowSource
  )

  private def kernelName(kind: String, layout: String): NonEmpty[String] =
    s"hmma884_${kind}_${layout.replace('-', '_')}_probe"

  private def module(kind: String, layout: String): Cubin.Module =
    SASSFixture.module(
      70,
      Vec.nonEmpty(SASSFixture.Kernel(
        kernelName(kind, layout),
        352,
        Vec.nonEmpty((0, 8), (8, 8), (16, 8), (24, 8)),
        registers = if (kind == "f16") 20 else 24
      ))
    )

  private def function(kind: String, layout: String): DecodedFunction =
    ListingDecoder.parse(
      listings((kind, layout)),
      kernelName(kind, layout),
      70
    ).fold(fail(_), _.head)

  private def halfBits(value: Int): Int = value match {
    case -3    => 0xc200
    case -2    => 0xc000
    case -1    => 0xbc00
    case 0     => 0x0000
    case 1     => 0x3c00
    case 2     => 0x4000
    case 3     => 0x4200
    case other => fail(s"half test value $other is out of range")
  }

  private def packHalf(low: Int, high: Int): Int = halfBits(low) | (halfBits(high) << 16)

  private def bytes32(values: Iterable[Int]): Vec[Byte] =
    Vec.from(values).flatMap(value => LittleEndian.toVec(value.toLong & 0xffffffffL, 4))

  private def matrixInputs(kind: String): (Vec[Byte], Vec[Byte], Vec[Byte], Int) = {
    val a = (0 until 32).flatMap { lane =>
      (0 until 2).map { register =>
        packHalf((lane + register * 2) % 7 - 3, (lane * 3 + register + 1) % 7 - 3)
      }
    }
    val b = (0 until 32).flatMap { lane =>
      (0 until 2).map { register =>
        packHalf((lane * 2 + register + 2) % 7 - 3, (lane * 5 + register * 3) % 7 - 3)
      }
    }
    val c = if (kind == "f32") {
      (0 until 32).flatMap { lane =>
        (0 until 8).map { index =>
          java.lang.Float.floatToRawIntBits(((lane * 11 + index * 7) % 23 - 11).toFloat)
        }
      }
    } else {
      (0 until 32).flatMap { lane =>
        (0 until 4).map { register =>
          packHalf((lane + register * 3 + 1) % 7 - 3, (lane * 3 + register * 2 + 4) % 7 - 3)
        }
      }
    }
    (bytes32(a), bytes32(b), bytes32(c), 32 * (if (kind == "f32") 8 else 4) * 4)
  }

  private def runPrograms(
      programs: Vec[Either[String, SASSFixture]],
      kernel: String,
      inputs: (Vec[Byte], Vec[Byte], Vec[Byte], Int)
  ): Vec[Vec[Byte]] = {
    val (a, b, c, outputSize) = inputs
    programs.map { programText =>
      val service = SASSFixture.Execution()
      val output  = service.allocate(Vec.fill(outputSize)(0xa5.toByte))
      val left    = service.allocate(a)
      val right   = service.allocate(b)
      val accum   = service.allocate(c)
      locally {
        val task = SASSFixture.task(
          kernel.unsafe[NonEmpty[String]],
          LaunchGeometry.Dimensions(32, 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec.nonEmpty(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Memory(left, 0L),
            KernelTask.Argument.Memory(right, 0L),
            KernelTask.Argument.Memory(accum, 0L)
          )
        )
        programText.fold(
          ptx => service.runPtx(ptx, task),
          fixture => service.compile(fixture).flatMap(program => service.run(program, task))
        ).fold(error => fail(error.message), identity)
        service.read(output, 0L, outputSize.unsafe[Int >= 0]).toOption.get
      }
    }
  }

  test("paired execution gives each program fresh poisoned output") {
    val image = SASSFixture.module(
      89,
      Vec.nonEmpty(SASSFixture.Kernel("probe", 352, Vec.nonEmpty((0, 8), (8, 8), (16, 8), (24, 8))))
    )
    val empty = SASSFixture(
      image,
      image.kernels.head,
      Vec.nonEmpty(DecodedFunction(
        "probe",
        Vec.nonEmpty(SASSFixture.instruction(0, "EXIT", Vec.empty, None))
      ))
    )
    val ptx = """.version 8.0
      |.target sm_89
      |.address_size 64
      |.visible .entry probe(.param .u64 out, .param .u64 a, .param .u64 b, .param .u64 c) {
      |  .reg .u64 %out;
      |  ld.param.u64 %out, [out];
      |  st.global.u32 [%out], 42;
      |  ret;
      |}
      |""".stripMargin
    val results = runPrograms(
      Vec.nonEmpty(Left(ptx), Right(empty)),
      "probe",
      (Vec.nonEmpty(0.toByte), Vec.nonEmpty(0.toByte), Vec.nonEmpty(0.toByte), 4)
    )
    assertEquals(results(0), LittleEndian.toVec(42, 4))
    assertEquals(results(1), Vec.fill(4)(0xa5.toByte))
  }

  private def compareDirectAndLowered(kind: String, layout: String, sm: Int >= 35 = 70): Unit = {
    val image            = module(kind, layout).copy(sm = sm)
    val name             = kernelName(kind, layout)
    val kernel           = image.kernels.find(_.name == name).getOrElse(fail("HMMA probe is missing"))
    val sass             = function(kind, layout)
    val lowered          = SASSFixture(image, kernel, Vec.nonEmpty(sass))
    val directPtx        = directSources((kind, layout))
    val (direct, actual) = runPrograms(
      Vec.nonEmpty(Left(directPtx), Right(lowered)),
      name,
      matrixInputs(kind)
    ) match {
      case Vec(expected, got) => expected -> got
      case other              => fail(s"expected two matrix results, got ${other.size}")
    }
    assertEquals(actual, direct, s"$kind A/B layouts $layout")
  }

  private def checkLayouts(kind: String, stepsCount: Int): Unit = Layouts.foreach { layout =>
    val instructions = function(kind, layout).instructions
    val steps = instructions.filter(i => i.mnemonic.raw == "HMMA" && i.modifiers.headOption.exists(_.name == "884"))
    val expectedSteps =
      if (stepsCount == 4) Vec.nonEmpty("STEP0", "STEP1", "STEP2", "STEP3") else Vec.nonEmpty("STEP0", "STEP1")
    assertEquals(steps.map(_.modifiers.last.name), expectedSteps, s"$kind $layout step sequence")
    assert(steps.forall(i => i.operands.head == i.operands(3)), s"$kind $layout should exercise in-place D/C pairs")
    val Array(layoutA, layoutB) = layout.split('-')
    assert(steps.forall(i => OperandPrinter.print(i.operands(1)).toOption.exists(_.endsWith(layoutA.toUpperCaseRoot))))
    assert(steps.forall(i => OperandPrinter.print(i.operands(2)).toOption.exists(_.endsWith(layoutB.toUpperCaseRoot))))
    if (kind == "f32") {
      val step1 = steps.find(_.modifiers.last.name == "STEP1").get
      val step2 = steps.find(_.modifiers.last.name == "STEP2").get
      assert(
        instructions.exists(i =>
          i.address > step1.address && i.address < step2.address &&
            !(i.mnemonic.raw == "HMMA" && i.modifiers.headOption.exists(_.name == "884"))
        ),
        s"$kind $layout should have interleaved instructions"
      )
    }
    compareDirectAndLowered(kind, layout)
  }

  test("sm70 F32 HMMA.884 steps preserve all four A/B layouts and accumulator pairs") {
    checkLayouts("f32", 4)
  }

  test("sm70 F16-accumulator HMMA.884 steps preserve both accumulator steps for all A/B layouts") {
    checkLayouts("f16", 2)
  }

  test("sm75 HMMA.884 retains Volta lane partitions for both accumulator formats") {
    Vec.nonEmpty("f16", "f32").foreach { kind =>
      Layouts.foreach(layout => compareDirectAndLowered(kind, layout, 75))
    }
  }

  test("sm70 HMMA.884 treats RZ accumulator operands as a zero C pair") {
    val image = SASSFixture.module(
      70,
      Vec.nonEmpty(SASSFixture.Kernel(
        "hmma884_f32_probe",
        352,
        Vec.nonEmpty((0, 8), (8, 8), (16, 8), (24, 8)),
        registers = 22
      ))
    )
    val kernel = image.kernels.find(_.name == "hmma884_f32_probe").getOrElse(fail("zero-C probe is missing"))
    val sass   = ListingDecoder.parse(
      Hmma884Fixtures.hmma884Sm70Hmma884F32ZeroSm70,
      "hmma884_f32_probe",
      70
    ).fold(fail(_), _.head)
    assert(sass.instructions.filter(i => i.mnemonic.raw == "HMMA" && i.modifiers.headOption.exists(_.name == "884"))
      .forall(_.operands(3) == SASSFixture.operand("RZ")))
    val lowered         = SASSFixture(image, kernel, Vec.nonEmpty(sass))
    val inputs          = matrixInputs("f32")
    val direct          = Hmma884Fixtures.hmma884Sm70Hmma884F32ZeroSource
    val actual          = lowered
    val (expected, got) =
      runPrograms(Vec.nonEmpty(Left(direct), Right(actual)), "hmma884_f32_probe", inputs) match {
        case Vec(reference, result) => reference -> result
        case other                  => fail(s"expected two zero-C results, got ${other.size}")
      }
    assertEquals(got, expected)
  }

  test("HMMA.884 rejects unmeasured type, layout, selector, architecture and step forms") {
    val layout   = "row-col"
    val f32      = function("f32", layout)
    val original =
      f32.instructions.find(i => i.mnemonic.raw == "HMMA" && i.modifiers.headOption.exists(_.name == "884")).get
    val image   = module("f32", layout)
    val kernel  = image.kernels.find(_.name == kernelName("f32", layout)).get
    val badStep = original.rewrite(modifiers =
      original.modifiers.updated(
        original.modifiers.size - 1,
        Instruction.Modifier("STEP4")
      )
    )
    val badLayout    = original.rewrite(operands = original.operands.updated(1, SASSFixture.operand("R2.H0_H0.ROW")))
    val badTypes     = original.rewrite(modifiers = original.modifiers.updated(2, Instruction.Modifier("BF16")))
    val badPredicate = original.rewrite(guard = Some(Instruction.Guard(Operand.Predicate(Some(0), false, false))))
    val badDestinationSuffix = original.rewrite(operands = original.operands.updated(0, SASSFixture.operand("R8.F32")))
    val badSelectorSuffix    =
      original.rewrite(operands = original.operands.updated(1, SASSFixture.operand("R2.ROW.H0_H0")))
    val badBoundaryRegister = original.rewrite(operands = original.operands.updated(0, SASSFixture.operand("R254")))
    Vec.nonEmpty(
      badStep,
      badLayout,
      badTypes,
      badPredicate,
      badDestinationSuffix,
      badSelectorSuffix,
      badBoundaryRegister
    ).foreach { invalid =>
      val changed = f32.copy(instructions = f32.instructions.map(i => if (i == original) invalid else i))
      assert(SASSFixture.lower(image, kernel, Vec.nonEmpty(changed)).isLeft)
    }
    assert(SASSFixture.lower(image.copy(sm = 80), kernel, Vec.nonEmpty(f32)).isLeft)
  }
}
