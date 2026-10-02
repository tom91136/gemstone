package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class Hmul2F32Suite extends munit.FunSuite {
  {
    def fixtureModule(sm: Int): Cubin.Module = SASSFixture.module(
      sm,
      Vec(SASSFixture.Kernel("hmul2_f32_probe", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 18))
    )

    val Kernel = "hmul2_f32_probe"

    def halfValue(bits: Int): Float = {
      val sign     = if ((bits & 0x8000) == 0) 1.0f else -1.0f
      val exponent = (bits >>> 10) & 0x1f
      val fraction = bits & 0x3ff
      if (exponent == 0x1f) {
        if (fraction == 0) sign * Float.PositiveInfinity else Float.NaN
      } else if (exponent == 0) {
        sign * Math.scalb(fraction.toFloat, -24)
      } else {
        sign * Math.scalb((1024 + fraction).toFloat / 1024.0f, exponent - 15)
      }
    }

    def productBits(left: Int, right: Int): Int = {
      val result = halfValue(left) * halfValue(right)
      if (java.lang.Float.isNaN(result)) 0x7fffffff
      else java.lang.Float.floatToRawIntBits(result)
    }

    def expected(a: Int, b: Int): Vec[Int] =
      Vec(
        productBits(a & 0xffff, b & 0xffff),
        productBits(a & 0xffff, b >>> 16),
        productBits(a >>> 16, b & 0xffff),
        productBits(a >>> 16, b >>> 16)
      )

    val cases = Vec(
      0x40003c00 -> 0x44004200, // distinct lanes: 1/2 multiplied by 3/4
      0x80000000 -> 0xbc003c00, // signed zeros times +/-1
      0x03ff0001 -> 0x3c000001, // subnormal and normal lanes
      0xfc007c00 -> 0x7c00fc00, // infinities and sign changes
      0x7d557e01 -> 0xfe017c01, // signaling/quiet NaNs in both sources
      0x7c000000 -> 0x00007c00, // zero * infinity
      0x3555bc00 -> 0x3c008001
    )

    def transformedFunction(aliasDestination: Boolean): DecodedFunction = {
      val text   = Hmul2Fixtures.hmul2F32Sm75Hmul2F32Sm75Patched
      val parsed = ListingDecoder.parse(text, Kernel, 75).fold(fail(_), _.head)
      if (!aliasDestination) parsed
      else {
        val aliased = parsed.copy(instructions = parsed.instructions.map { instruction =>
          if (
            instruction.mnemonic.raw == "HMUL2" && instruction.modifiers == Vec(Instruction.Modifier.F32) &&
            instruction.operands.headOption.contains(Operand.Register(RegisterBank.R, 13))
          )
            instruction.rewrite(operands = instruction.operands.updated(0, Operand.Register(RegisterBank.R, 2)))
          else if (
            instruction.mnemonic.raw == "STG" &&
            instruction.operands.lastOption.contains(Operand.Register(RegisterBank.R, 13))
          )
            instruction.rewrite(operands =
              instruction.operands.updated(
                instruction.operands.size - 1,
                Operand.Register(RegisterBank.R, 2)
              )
            )
          else instruction
        })
        assertEquals(
          aliased.instructions.find(_.mnemonic.raw == "HMUL2").map(_.operands.head),
          Some(Operand.Register(RegisterBank.R, 2))
        )
        aliased
      }
    }

    def execute(
        inputs: Vec[(Int, Int)],
        aliasDestination: Boolean = false
    ): Vec[Vec[Int]] = {
      val module   = fixtureModule(75)
      val kernel   = module.kernels.find(_.name == Kernel).getOrElse(fail("HMUL2.F32 kernel is missing"))
      val function = transformedFunction(aliasDestination)
      val sass     = SASSFixture(module, kernel, Vec(function))
      val lhs      = Vec.from(inputs).flatMap { case (a, _) =>
        LittleEndian.toVec(a.toLong & 0xffffffffL, 4)
      }
      val rhs = Vec.from(inputs).flatMap { case (_, b) =>
        LittleEndian.toVec(b.toLong & 0xffffffffL, 4)
      }
      val outputBytes = inputs.size * 16

      locally {
        val service = SASSFixture.Execution()
        val output  =
          service.allocate(outputBytes.toLong.unsafe[Long > 0])
        val left  = service.allocate(lhs.size.toLong.unsafe[Long > 0])
        val right = service.allocate(rhs.size.toLong.unsafe[Long > 0])
        assert(service.write(left, 0L, lhs).isRight)
        assert(service.write(right, 0L, rhs).isRight)
        val program = service.compile(sass)
          .fold(error => fail(error.message), identity)
        val task = SASSFixture.task(
          Kernel.unsafe[NonEmpty[String]],
          LaunchGeometry.Dimensions(inputs.size.unsafe[Int > 0], 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Memory(left, 0L),
            KernelTask.Argument.Memory(right, 0L)
          )
        )
        service.run(program, task).fold(error => fail(error.message), identity)
        val words = service.readWords(output, (inputs.size * 4).unsafe[Int >= 0])
        inputs.indices.map(index => (0 until 4).map(lane => words(index * 4 + lane).toInt).toVec).toVec
      }
    }

    test("sm_75 HMUL2.F32 low/high selected products match the source-equivalent oracle") {
      val sassText = Hmul2Fixtures.hmul2F32Sm75Hmul2F32Sm75Patched
      assert(sassText.contains("HMUL2.F32 R13, R2.H0_H0, R4.H0_H0"))
      assert(sassText.contains("HMUL2.F32 R15, R2.H0_H0, R4.H1_H1"))
      assert(sassText.contains("HMUL2.F32 R9, R2.H1_H1, R4.H0_H0"))
      assert(sassText.contains("HMUL2.F32 R11, R2.H1_H1, R4.H1_H1"))
      assertEquals(execute(cases), cases.map((a, b) => expected(a, b)))
    }

    test("sm_75 HMUL2.F32 reads selected inputs before an aliased destination write") {
      val input  = Vec(0x40003c00 -> 0x44004200, 0x03ff0001 -> 0x3c000001)
      val actual = execute(input, aliasDestination = true)
      assertEquals(actual.map(_.head), input.map((a, b) => expected(a, b).head))
    }

    test("HMUL2.F32 rejects other architectures and mixed selector forms") {
      val module75   = fixtureModule(75)
      val kernel75   = module75.kernels.find(_.name == Kernel).getOrElse(fail("sm_75 HMUL2.F32 kernel is missing"))
      val function75 = transformedFunction(aliasDestination = false)

      val module89 = fixtureModule(89)
      val kernel89 = module89.kernels.find(_.name == Kernel).getOrElse(fail("sm_89 probe kernel is missing"))
      assert(SASSFixture.lower(module89, kernel89, Vec(function75)).left.exists(
        _.contains("HMUL2.F32 is supported only on sm_75")
      ))

      val mixedSelector = function75.copy(instructions = function75.instructions.map { instruction =>
        if (instruction.mnemonic.raw == "HMUL2" && instruction.operands(1) == SASSFixture.operand("R2.H0_H0"))
          instruction.rewrite(operands = instruction.operands.updated(1, SASSFixture.operand("R2.H0_H1")))
        else instruction
      })
      assert(SASSFixture.lower(module75, kernel75, Vec(mixedSelector)).left.exists(
        _.contains("HMUL2.F32 supports only duplicated H0_H0 or H1_H1 selectors")
      ))
    }
  }

}
