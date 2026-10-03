package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class F2fpPackAbSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module =
    SASSFixture.module(89, Vec(SASSFixture.Kernel("f2fp_pack_ab_probe", 352, Vec((0, 8), (8, 8)), registers = 10)))

  private val cases = Vec.nonEmpty(
    (0x00000000L, 0x80000000L, 0x80000000L),
    (0x80000000L, 0x3f800000L, 0x3c008000L),
    (0x3f800000L, 0xc0000000L, 0xc0003c00L),
    (0xc0000000L, 0x477fe000L, 0x7bffc000L),
    (0x477fe000L, 0x477ff000L, 0x7c007bffL),
    (0x477ff000L, 0x33800000L, 0x00017c00L),
    (0x33800000L, 0x33000000L, 0x00000001L),
    (0x33000000L, 0x38800000L, 0x04000000L),
    (0x38800000L, 0x38000000L, 0x02000400L),
    (0x38000000L, 0x3f801000L, 0x3c000200L),
    (0x3f801000L, 0x3f803000L, 0x3c023c00L),
    (0x3f803000L, 0x7f800000L, 0x7c003c02L),
    (0x7f800000L, 0xff800000L, 0xfc007c00L),
    (0xff800000L, 0x7fc12345L, 0x7ffffc00L),
    (0x7fc12345L, 0xffc54321L, 0x7fff7fffL),
    (0xffc54321L, 0x7f812345L, 0x7fff7fffL),
    (0x7f812345L, 0xff812345L, 0x7fff7fffL),
    (0xff812345L, 0x40600000L, 0x43007fffL),
    (0x40600000L, 0xc0e80000L, 0xc7404300L),
    (0xc0e80000L, 0x00000000L, 0x0000c740L),
    (0x7f800001L, 0xff800001L, 0x7fff7fffL),
    (0x7fc12345L, 0x3f800000L, 0x3c007fffL),
    (0x3f800000L, 0xffc54321L, 0x7fff3c00L)
  )

  private def execute(function: DecodedFunction, expected: Vec[Long]): Unit = {
    val module = fixtureModule
    val sass   = SASSFixture(
      module,
      module.kernels.find(_.name == "f2fp_pack_ab_probe").get,
      Vec(function)
    )
    val inputWords = cases.flatMap { case (first, second, _) => Vec(first, second) }
    val inputs     = Vec.from(inputWords).flatMap(LittleEndian.toVec(_, 4))
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(cases.positiveSize.toLong.widen[Long > 0] * 4L)
      val input   =
        service.allocate(cases.positiveSize.toLong.widen[Long > 0] * 8L)
      assert(service.write(input, 0L, inputs).isRight)
      val program = service.compile(sass)
        .fold(error => fail(error.message), identity)
      val task = SASSFixture.task(
        "f2fp_pack_ab_probe": NonEmpty[String],
        LaunchGeometry.Dimensions(cases.positiveSize, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(input, 0L),
          KernelTask.Argument.Memory(output, 0L)
        )
      )
      service.run(program, task).fold(error => fail(error.message), identity)
      val bytes  = service.read(output, 0L, cases.nonNegativeSize * 4).toOption.get
      val actual = cases.indices.map { index =>
        LittleEndian.decode(bytes, index * 4, 4)
      }.toVec
      assertEquals(actual, expected)
    }
  }

  test("compiler-produced F2FP.PACK_AB matches sm_89 lane packing, rounding, and NaN behavior") {
    val sassText = ConversionFixtures.f2fpPackAbSm89F2fpPackAbSm89
    assert(sassText.contains("F2FP.F16.F32.PACK_AB R7, R7, R0"))
    val function = ListingDecoder.parse(sassText, "f2fp_pack_ab_probe", 89).fold(fail(_), _.head)
    execute(function, cases.map(_._3))
  }

  test("modifierless F2FP.PACK_AB supports destination aliasing its second source") {
    val sassText = ConversionFixtures.f2fpPackAbSm89F2fpPackAbSm89
    val parsed   = ListingDecoder.parse(sassText, "f2fp_pack_ab_probe", 89).fold(fail(_), _.head)
    val function = parsed.copy(instructions = parsed.instructions.map { instruction =>
      if (instruction.mnemonic.raw == "F2FP") {
        val replacement = Parser.parse("F2FP.PACK_AB R7, RZ, R7;").fold(fail(_), identity)
        instruction.rewrite(replacement)
      } else instruction
    })
    execute(function, cases.map { case (_, _, expected) => (expected >>> 16) & 0xffffL })
  }

  test("F2FP.PACK_AB lowering rejects unverified modifier forms") {
    val module   = fixtureModule
    val sassText = ConversionFixtures.f2fpPackAbSm89F2fpPackAbSm89
    val parsed   = ListingDecoder.parse(sassText, "f2fp_pack_ab_probe", 89).fold(fail(_), _.head)
    val invalid  = parsed.copy(instructions = parsed.instructions.map { instruction =>
      if (instruction.mnemonic.raw == "F2FP")
        instruction.rewrite(modifiers = instruction.modifiers :+ Instruction.Modifier("SATFINITE"))
      else instruction
    })
    val kernel = module.kernels.find(_.name == "f2fp_pack_ab_probe").get
    assert(SASSFixture.lower(module, kernel, Vec(invalid)).isLeft)
  }
}
