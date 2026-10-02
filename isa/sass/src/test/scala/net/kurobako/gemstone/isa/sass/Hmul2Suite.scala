package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class Hmul2Suite extends munit.FunSuite {
  {
    val fixtureModules = Map(
      "hmul2_selected" -> SASSFixture.module(
        89,
        Vec(SASSFixture.Kernel("hmul2_selected", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 12))
      ),
      "hmul2_vector" -> SASSFixture.module(
        89,
        Vec(SASSFixture.Kernel("hmul2_vector", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 12))
      )
    )
    def run(
        sassText: String,
        kernelName: String,
        cases: Vec[(Long, Long, Long)],
        transform: DecodedFunction => DecodedFunction = identity
    ): Unit = {
      val module   = fixtureModules(kernelName)
      val function = ListingDecoder.parse(sassText, kernelName, 89).fold(fail(_), _.head)
      val sass     = SASSFixture(module, module.kernels.find(_.name == kernelName).get, Vec(transform(function)))
      val lhs      = Vec.from(cases).flatMap { case (a, _, _) => LittleEndian.toVec(a, 4) }
      val rhs      = Vec.from(cases).flatMap { case (_, b, _) => LittleEndian.toVec(b, 4) }
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate((cases.size * 4L).unsafe[Long > 0])
        val left    = service.allocate(lhs.size.toLong.unsafe[Long > 0])
        val right   = service.allocate(rhs.size.toLong.unsafe[Long > 0])
        assert(service.write(left, 0L, lhs).isRight)
        assert(service.write(right, 0L, rhs).isRight)
        val program = service.compile(sass)
          .fold(error => fail(error.message), identity)
        val task = SASSFixture.task(
          kernelName.unsafe[NonEmpty[String]],
          LaunchGeometry.Dimensions(cases.size.unsafe[Int > 0], 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Memory(left, 0L),
            KernelTask.Argument.Memory(right, 0L)
          )
        )
        service.run(program, task).fold(error => fail(error.message), identity)
        val actual = service.readWords(output, cases.size.unsafe[Int >= 0])
        assertEquals(actual, cases.map(_._3))
      }
    }

    test("compiler-produced sm_89 HMUL2 matches binary16 lane products and NVIDIA NaN canonicalization") {
      val sassText = Hmul2Fixtures.hmul2Sm89
      assert(sassText.contains("HMUL2 R9, R2, R5"))
      run(
        Hmul2Fixtures.hmul2Sm89,
        "hmul2_vector",
        NumericCases.packedMultiply
      )
    }

    test("compiler-produced HMUL2 H0_H0 selectors duplicate the selected half") {
      val sassText = Hmul2Fixtures.hmul2SelectedSm89
      assert(sassText.contains("HMUL2 R9, R2.H0_H0, R5.H0_H0"))
      run(
        Hmul2Fixtures.hmul2SelectedSm89,
        "hmul2_selected",
        Vec(
          (0x3c004000L, 0x40003c00L, 0x44004400L),
          (0x04000001L, 0x38003c00L, 0x00000000L),
          (0x00007e01L, 0x3c000000L, 0x7fff7fffL)
        )
      )
    }

    test("HMUL2 H1_H1 selectors and a destination alias retain both operand values") {
      val function = ListingDecoder.parse(
        Hmul2Fixtures.hmul2Sm89,
        "hmul2_vector",
        89
      ).fold(fail(_), _.head)
      val aliased = function.copy(instructions = function.instructions.map { instruction =>
        if (instruction.mnemonic.raw == "HMUL2")
          instruction.rewrite(operands = SASSOperandParser.parseMany("R2, R2.H1_H1, R5.H1_H1").fold(fail(_), identity))
        else if (instruction.mnemonic.raw == "STG")
          instruction.rewrite(operands = SASSOperandParser.parseMany("[R6.64], R2").fold(fail(_), identity))
        else instruction
      })
      run(
        Hmul2Fixtures.hmul2Sm89,
        "hmul2_vector",
        Vec(
          (0x40003c00L, 0x40004000L, 0x44004400L),
          (0x35550000L, 0x35550000L, 0x2f1c2f1cL),
          (0x3c007e01L, 0x3c003c00L, 0x3c003c00L)
        ),
        _ => aliased
      )
    }
  }

}
