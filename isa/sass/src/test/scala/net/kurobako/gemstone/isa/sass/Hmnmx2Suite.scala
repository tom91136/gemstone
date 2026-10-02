package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class Hmnmx2Suite extends munit.FunSuite {
  {
    def fixtureModule: Cubin.Module = SASSFixture.module(
      80,
      Vec(
        SASSFixture.Kernel("hmnmx2_mixed_alias_probe", 352, Vec(0 -> 8, 8 -> 8), registers = 10),
        SASSFixture.Kernel("hmnmx2_mixed_probe", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 12),
        SASSFixture.Kernel("hmnmx2_alias_probe", 352, Vec(0 -> 8, 8 -> 8), registers = 8),
        SASSFixture.Kernel("hmnmx2_select_probe", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 14)
      )
    )

    val cases = Vec(
      (0x40003c00L, 0x3c004000L, 0x40004000L),
      (0x49003c00L, 0x3c004200L, 0x49004200L),
      (0x3c004800L, 0x3c004000L, 0x40004800L),
      (0x41003c00L, 0x3c004000L, 0x41004000L),
      (0x40008000L, 0x3c000000L, 0x40000000L),
      (0x3c000000L, 0x40008000L, 0x3c000000L),
      (0x40000000L, 0x3c000000L, 0x40000000L),
      (0x40008000L, 0x3c008000L, 0x40008000L),
      (0x40007e01L, 0x3c004000L, 0x40004000L),
      (0x42003c00L, 0x40007e01L, 0x42003c00L),
      (0x4000fc01L, 0x3c004000L, 0x40004000L),
      (0x42003c00L, 0x40007d55L, 0x42003c00L),
      (0x40007e01L, 0x3c00ffc1L, 0x40007fffL),
      (0x4000fc01L, 0x3c007d55L, 0x40007fffL),
      (0x7c003c00L, 0xfc004000L, 0x7c004000L),
      (0x40000001L, 0x3c008000L, 0x40000001L),
      (0x7e013c00L, 0x3c003c00L, 0x3c003c00L),
      (0xfc013c00L, 0x3c003c00L, 0x3c003c00L),
      (0x7d553c00L, 0x3c004000L, 0x40004000L)
    )
    val minLow = Vec(
      0x3c00L, 0x3c00L, 0x4000L, 0x3c00L, 0x8000L, 0x8000L, 0x0000L, 0x8000L,
      0x4000L, 0x3c00L, 0x4000L, 0x3c00L, 0x7fffL, 0x7fffL, 0x3c00L, 0x8000L, 0x3c00L, 0x3c00L, 0x3c00L
    )
    val maxLow = Vec(
      0x4000L, 0x4200L, 0x4800L, 0x4000L, 0x0000L, 0x0000L, 0x0000L, 0x8000L,
      0x4000L, 0x3c00L, 0x4000L, 0x3c00L, 0x7fffL, 0x7fffL, 0x4000L, 0x0001L, 0x3c00L, 0x3c00L, 0x4000L
    )

    def execute(kernel: String, text: String, expected: Vec[Long]): Unit = {
      val module   = fixtureModule
      val function = ListingDecoder.parse(text, kernel, 80).fold(fail(_), _.head)
      val sass     =
        SASSFixture(module, module.kernels.find(_.name == kernel).get, Vec(function))
      val left  = Vec.from(cases.map(_._1)).flatMap(LittleEndian.toVec(_, 4))
      val right = Vec.from(cases.map(_._2)).flatMap(LittleEndian.toVec(_, 4))
      locally {
        val service                      = SASSFixture.Execution()
        def allocation(bytes: Vec[Byte]) = {
          val value =
            service.allocate(bytes.size.toLong.unsafe[Long > 0])
          assert(service.write(value, 0L, bytes).isRight)
          value
        }
        val a       = allocation(left); val b = allocation(right)
        val inPlace = kernel == "hmnmx2_mixed_alias_probe"
        val out     = if (inPlace) a
        else service.allocate((expected.size * 4L).unsafe[Long > 0])
        val program = service.compile(sass).fold(e => fail(e.message), identity)
        val task    = SASSFixture.task(
          kernel.unsafe[NonEmpty[String]],
          LaunchGeometry.Dimensions(cases.size.unsafe[Int > 0], 1, 1),
          LaunchGeometry.Dimensions.One,
          if (inPlace) Vec(
            KernelTask.Argument.Memory(a, 0L),
            KernelTask.Argument.Memory(b, 0L)
          )
          else Vec(
            KernelTask.Argument.Memory(a, 0L),
            KernelTask.Argument.Memory(b, 0L),
            KernelTask.Argument.Memory(out, 0L)
          )
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val actual = service.readWords(out, expected.size.unsafe[Int >= 0])
        assertEquals(actual, expected)
      }
    }

    test("HMNMX2 H0 selectors implement ordered min and max with measured NaN and signed-zero behavior") {
      val expected = cases.indices.flatMap(i => Vec(minLow(i), maxLow(i))).toVec
      execute("hmnmx2_select_probe", Hmnmx2Fixtures.hmnmx2Sm80Hmnmx2SelectSm80, expected)
    }

    test("HMNMX2 mixed packed source computes both halves and handles source/destination alias") {
      execute("hmnmx2_mixed_probe", Hmnmx2Fixtures.hmnmx2Sm80Hmnmx2MixedSm80, cases.map(_._3))
      execute("hmnmx2_mixed_alias_probe", Hmnmx2Fixtures.hmnmx2Sm80Hmnmx2MixedAliasPatchedSm80, cases.map(_._3))
    }

    test("HMNMX2 lowering rejects unverified selectors and modifiers") {
      val module   = fixtureModule
      val text     = Hmnmx2Fixtures.hmnmx2Sm80Hmnmx2MixedSm80
      val function = ListingDecoder.parse(text, "hmnmx2_mixed_probe", 80).fold(fail(_), _.head)
      val selector = function.copy(instructions =
        function.instructions.map(i =>
          if (i.mnemonic.raw == "HMNMX2") i.rewrite(operands =
            i.operands.updated(
              2,
              SASSOperandParser.parse("R5.H0_H1").fold(fail(_), identity)
            )
          )
          else i
        )
      )
      val modifier = function.copy(instructions =
        function.instructions.map(i =>
          if (i.mnemonic.raw == "HMNMX2") i.rewrite(modifiers = i.modifiers :+ Instruction.Modifier("RN")) else i
        )
      )
      val kernel = module.kernels.find(_.name == "hmnmx2_mixed_probe").get
      assert(SASSFixture.lower(module, kernel, Vec(selector)).isLeft)
      assert(SASSFixture.lower(module, kernel, Vec(modifier)).isLeft)
    }
  }

}
