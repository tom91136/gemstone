package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as VMInstruction, *}
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyLds32Suite extends munit.FunSuite {
  private def fixtureModule(sm: Int): Cubin.Module =
    SASSFixture.module(sm, Vec(SASSFixture.Kernel("probe", 320, Vec(0 -> 8, 8 -> 4), registers = 5, shared = 8)))

  for (sm <- Vec(50, 60))
    test(s"sm_$sm compiler LDS.U.32 preserves all data bits across a shared-memory barrier") {
      val module   = fixtureModule(sm)
      val function = ListingDecoder.parse(
        Map(50 -> SharedMemoryFixtures.lds32LegacyProbeSm50, 60 -> SharedMemoryFixtures.lds32LegacyProbeSm60)(sm),
        "probe",
        sm
      ).fold(fail(_), _.head)
      assert(function.instructions.exists(instruction =>
        instruction.mnemonic.raw == "LDS" &&
          instruction.modifiers == Vec(SASSInstruction.Modifier("U"), SASSInstruction.Modifier("32"))
      ))
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
          assertEquals(result, LittleEndian.toVec(input.toLong & 0xffffffffL, 4))
        }
      }

    }

  test("a shared-memory load into RZ still emits a real load and discards only its result") {
    val module   = fixtureModule(50)
    val function = DecodedFunction(
      "probe",
      Vec(
        SASSFixture.instruction(0x08, "MOV", Vec("R4", "RZ"), None),
        SASSFixture.instruction(0x10, "LDS.U.32", Vec("RZ", "[R4+0x4]"), None),
        SASSFixture.instruction(0x18, "EXIT", Vec.empty, None)
      )
    )
    val lowered        = SASSFixture(module, module.kernels.head, Vec(function))
    val discardedLoads = lowered.instructions.collect {
      case VMInstruction.Load(destination, _, width, _, MemoryAccess.Shared) if width.raw == 32 =>
        destination
    }
    assertEquals(discardedLoads.size, 1)
    assertNotEquals(discardedLoads.head.raw, "RZ")
  }
}
