package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyCallSuite extends munit.FunSuite {
  private def fixtureModule(sm: Int, indirect: Boolean): Cubin.Module = {
    val name = if (indirect) "indirect_call" else "calls"
    SASSFixture.module(sm, Vec(SASSFixture.Kernel(name, 320, Vec(0 -> 8), registers = 5)))
  }

  for (
    (label, sm, indirect, name, text) <- Vec(
      ("calls-sm35", 35, false, "calls", CallFixtures.callsSm35),
      ("calls-sm50", 50, false, "calls", CallFixtures.callsSm50),
      ("calls-sm60", 60, false, "calls", CallFixtures.callsSm60),
      ("indirect-call-sm35", 35, true, "indirect_call", CallFixtures.indirectCallSm35)
    )
  )
    test(s"$label preserves per-lane return stacks across nested or indirect calls") {
      val module   = fixtureModule(sm, indirect)
      val function = ListingDecoder.parse(text, name, module.sm).fold(fail(_), _.head)
      if (name == "calls") {
        assertEquals(function.instructions.count(_.mnemonic.raw == "CAL"), 3)
        if (module.sm == 35) assert(function.instructions.exists(i => i.address.raw == 0xb8 && i.mnemonic.raw == "CAL"))
      } else assert(function.instructions.exists(_.mnemonic.raw == "PRET"))
      val source = SASSFixture(module, module.kernels.head, Vec(function))
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(256L.unsafe[Long > 0])
        val program = service.compile(source)
          .fold(e => fail(e.message), identity)
        val task = SASSFixture.task(
          name.unsafe[NonEmpty[String]],
          LaunchGeometry.Dimensions(32, 1, 1),
          LaunchGeometry.Dimensions(2, 1, 1),
          Vec(KernelTask.Argument.Memory(output, 0L))
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val expected = Vec.from((0 until 64).map { index =>
          val lane = index % 32
          if (name == "calls") { if (lane % 2 == 0) 6 * lane + 5 else lane }
          else if (lane % 2 == 0) 3 * lane else 2 * lane
        }).flatMap(value => LittleEndian.toVec(value.toLong, 4))
        assertEquals(service.read(output, 0L, 256), Right(expected))
      }
    }
}
