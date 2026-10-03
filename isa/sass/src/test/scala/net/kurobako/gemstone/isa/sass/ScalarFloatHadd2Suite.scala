package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class ScalarFloatHadd2Suite extends munit.FunSuite {
  {
    def fixtureModule: Cubin.Module =
      SASSFixture.module(75, Vec(SASSFixture.Kernel("probe", 352, Vec((0, 8), (8, 8)), registers = 5)))

    val Kernel: NonEmpty[String] = "probe"

    val inputs = NumericCases.f32ToF16.mapNonEmpty(_._1)

    val expected = NumericCases.f32ToF16.map(_._2 * 0x00010001L)

    def lower(module: Cubin.Module, function: DecodedFunction): Either[String, SASSFixture] = {
      val kernel = module.kernels.find(_.name == Kernel).getOrElse(fail("probe kernel is missing"))
      SASSFixture.lower(module, kernel, Vec(function))
    }

    test("sm_75 scalar-float HADD2 rounds to and duplicates the packed half result") {
      val module   = fixtureModule
      val sassText = Hadd2Fixtures.hadd2F32ToHalfSm75ProbeSm75Patched
      assert(sassText.contains("HADD2 R0, R0.F32, -RZ.H0_H0"))
      assert(sassText.contains("NOP"))
      val function   = ListingDecoder.parse(sassText, Kernel, 75).fold(fail(_), _.head)
      val sass       = lower(module, function).fold(fail(_), identity)
      val inputBytes = Vec.from(inputs).flatMap(LittleEndian.toVec(_, 4))
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(inputs.positiveSize.toLong.widen[Long > 0] * 4L)
        val input   = service.allocate(inputBytes)
        val program = service.compile(sass)
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
        val actual = service.readWords(output, inputs.nonNegativeSize)
        assertEquals(actual, expected)
      }
    }

    test("scalar-float HADD2 rejects other architectures and unmeasured zero-source forms") {
      val module   = fixtureModule
      val sassText = Hadd2Fixtures.hadd2F32ToHalfSm75ProbeSm75Patched
      val function = ListingDecoder.parse(sassText, Kernel, 75).fold(fail(_), _.head)
      val arch89   = module.copy(sm = 89)
      assert(lower(arch89, function).left.toOption.exists(_.contains("supported only on sm_75")))

      val positiveZero = function.copy(instructions = function.instructions.map { instruction =>
        if (
          instruction.mnemonic.raw == "HADD2" && instruction.modifiers.isEmpty &&
          instruction.operands.head == SASSFixture.operand("R0")
        )
          instruction.rewrite(operands = instruction.operands.updated(2, SASSFixture.operand("RZ.H0_H0")))
        else instruction
      })
      assert(lower(
        module,
        positiveZero
      ).left.toOption.exists(_.contains("requires the compiler-emitted -RZ.H0_H0 zero addend")))
    }
  }

}
