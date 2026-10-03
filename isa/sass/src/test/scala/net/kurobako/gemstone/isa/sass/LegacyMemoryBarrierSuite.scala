package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyMemoryBarrierSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module =
    SASSFixture.module(35, Vec(SASSFixture.Kernel("probe", 320, Vec((0, 8)), registers = 5)))

  test("compiler-produced sm_35 MEMBAR.GL reaches the generic global memory fence") {
    val module = fixtureModule
    val sass   = MembarFixtures.membarSm35Probe
    assert(sass.contains("MEMBAR.GL"))
    val functions = ListingDecoder.parse(sass, "probe", 35).fold(fail(_), identity)
    val lowered   = SASSFixture(module, module.kernels.head, functions)
    assert(lowered.instructions.contains(Instruction.MemoryBarrier))
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(8L)
      val program = service.compile(lowered)
        .fold(error => fail(error.message), identity)
      val task = SASSFixture.task(
        "probe": NonEmpty[String],
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(output, 0L))
      )
      service.run(program, task).fold(error => fail(error.message), identity)
      val bytes = service.read(output, 0L, 8).toOption.get
      assertEquals(Vec.tabulate(2)(i => LittleEndian.decode(bytes, i * 4, 4)), Vec(7L, 7L))
    }
  }
}
