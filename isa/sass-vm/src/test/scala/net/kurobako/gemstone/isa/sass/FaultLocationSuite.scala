package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.vm.LaunchGeometry.*

class FaultLocationSuite extends Suite {
  test("a faulting instruction reports its kernel, function, byte address, workgroup and lone lane") {
    val kernel = load(35, Fixture.listing("cacheInvalidateSm35"), "cache_probe")
    kernel.run(1)(Arg.zeros(4), Arg.words(0L)) match {
      case Left(fault) =>
        val location = fault.location.getOrElse(fail(s"unlocated fault $fault"))
        assertEquals(
          (location.kernel: String, location.function.map(_.raw), location.workgroup, location.lane.map(_.raw)),
          ("cache_probe", Some("cache_probe"), Some(Coordinates.origin), Some(0))
        )
        val faulting = kernel.function.instructions.find(instruction =>
          location.instruction.exists(_.raw == instruction.address.toLong)
        ).getOrElse(fail(s"no instruction at ${location.instruction}"))
        assert(faulting.mnemonic.raw.startsWith("ST"), faulting.mnemonic)
      case Right(_) => fail("an undersized output buffer must fault")
    }
  }
}
