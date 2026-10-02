package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyPsetSuite extends munit.FunSuite {
  private def fixtureModule(sm: Int): Cubin.Module =
    SASSFixture.module(sm, Vec(SASSFixture.Kernel("probe", 320, Vec(0 -> 8, 8 -> 4), registers = 4)))

  for (sm <- Vec(50, 60))
    test(s"sm_$sm decoded PSET predicate mask agrees with the source selection") {
      val module   = fixtureModule(sm)
      val function = ListingDecoder.parse(
        Map(50 -> PsetFixtures.psetLegacyPsetSm50, 60 -> PsetFixtures.psetLegacyPsetSm60)(sm),
        "probe",
        sm
      ).fold(fail(_), _.head)
      val instruction = function.instructions.find(i =>
        i.mnemonic.raw == "PSET" && i.modifiers == Vec(
          Instruction.Modifier("AND"),
          Instruction.Modifier("AND")
        )
      ).get
      assertEquals(instruction.operands, SASSFixture.operands("R0, P0, PT, PT"))
      val lowered = SASSFixture(module, module.kernels.head, Vec(function))
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(4L.unsafe[Long > 0])
        val program = service.compile(lowered)
          .fold(e => fail(e.message), identity)
        Vec(0, 1, Int.MinValue, -1, 0).foreach { input =>
          val task = SASSFixture.task(
            "probe": NonEmpty[String],
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec(
              KernelTask.Argument.Memory(output, 0L),
              KernelTask.Argument.Scalar(LittleEndian.toVec(input.toLong & 0xffffffffL, 4))
            )
          )
          service.run(program, task).fold(e => fail(e.message), identity)
          val result = service.read(output, 0L, 4).toOption.get
          assertEquals(result, Vec.fill(4)(if (input == 0) 0.toByte else 255.toByte))
        }
      }
      val withFlags = function.copy(instructions = function.instructions.map { i =>
        if (i == instruction) i.rewrite(operands = i.operands.updated(0, SASSFixture.operand("R0.CC"))) else i
      })
      assert(SASSFixture.lower(module, module.kernels.head, Vec(withFlags)).isLeft)
    }
}
