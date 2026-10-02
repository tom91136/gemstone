package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyCacheInvalidationSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module =
    SASSFixture.module(35, Vec(SASSFixture.Kernel("cache_probe", 320, Vec(0 -> 8, 8 -> 8), registers = 10)))

  test("compiler-produced sm_35 cache invalidation preserves atomic-to-normal-load coherence") {
    val module    = fixtureModule
    val functions = ListingDecoder.parse(CacheInvalidateFixtures.cacheInvalidateSm35Cache, "cache_probe", 35)
      .fold(fail(_), identity)
    assert(functions.head.instructions.exists(instruction =>
      instruction.mnemonic.raw == "CCTL" &&
        instruction.modifiers == Vec(Instruction.Modifier("E"), Instruction.Modifier("IV"))
    ))
    val lowered = SASSFixture(module, module.kernels.head, functions)
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(8L.unsafe[Long > 0])
      val input   = service.allocate(4L.unsafe[Long > 0])
      val program = service.compile(lowered)
        .fold(e => fail(e.message), identity)
      for (initial <- Vec(0L, 42L, 0xfffffffcL)) {
        service.write(input, 0L, LittleEndian.toVec(initial, 4))
          .fold(e => fail(e.message), identity)
        val task = SASSFixture.task(
          "cache_probe": NonEmpty[String],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Memory(input, 0L)
          )
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val bytes   = service.read(output, 0L, 8).toOption.get
        val updated = (initial + 7L) & 0xffffffffL
        assertEquals(
          Vec.tabulate(2)(i => LittleEndian.decode(bytes, i * 4, 4)),
          Vec(initial, updated)
        )
        val actual = service.read(input, 0L, 4).toOption.get
        assertEquals(LittleEndian.decode(actual, 0, 4), updated)
      }
    }
  }
}
