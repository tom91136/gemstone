package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class GlobalAtomicSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module =
    SASSFixture.module(86, Vec(SASSFixture.Kernel("atomic_add", 352, Vec(0 -> 8, 8 -> 8), registers = 14)))

  test("compiler ATOMG returns the prior value through a predicated warp-aggregated atomic") {
    val module   = fixtureModule
    val text     = AtomicAddFixtures.atomicAddSm86
    val function = ListingDecoder.parse(text, "atomic_add", 86).fold(fail(_), _.head)
    assert(function.instructions.exists(i => i.mnemonic.raw == "ATOMG" && i.guard.nonEmpty))
    val source = SASSFixture(module, module.kernels.head, Vec(function))
    locally {
      val service = SASSFixture.Execution()
      val counter = service.allocate(4L.unsafe[Long > 0])
      val output  = service.allocate(128L.unsafe[Long > 0])
      assert(service.write(counter, 0L, LittleEndian.toVec(-16L, 4)).isRight)
      val program = service.compile(source)
        .fold(e => fail(e.message), identity)
      val task = SASSFixture.task(
        "atomic_add": NonEmpty[String],
        LaunchGeometry.Dimensions(32, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(counter, output).map(id => KernelTask.Argument.Memory(id, 0L))
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      val expected = Vec.from(-16 until 16).flatMap(value => LittleEndian.toVec(value.toLong, 4))
      assertEquals(service.read(output, 0L, 128), Right(expected))
      assertEquals(
        service.read(counter, 0L, 4),
        Right(LittleEndian.toVec(16L, 4))
      )
    }
  }
}
