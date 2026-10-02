package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyExtendedSetSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module =
    SASSFixture.module(35, Vec(SASSFixture.Kernel("iset_probe", 320, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 10)))

  test("compiler-produced ISET.X compares signed and unsigned 64-bit values across low-word ties and borrows") {
    val values = Vec(
      0L,
      1L,
      0x7fffffffL,
      0x80000000L,
      0xffffffffL,
      0x100000000L,
      0x100000001L,
      0x1ffffffffL,
      Long.MaxValue,
      Long.MinValue,
      0xffffffff00000000L,
      -1L
    )
    val module    = fixtureModule
    val functions = ListingDecoder.parse(IsetExtendedFixtures.isetExtendedSm35Iset, "iset_probe", 35)
      .fold(fail(_), identity)
    assertEquals(functions.head.instructions.count(_.mnemonic.raw == "ISET"), 5)
    val lowered = SASSFixture(module, module.kernels.head, functions)
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(20L.unsafe[Long > 0])
      val program = service.compile(lowered)
        .fold(e => fail(e.message), identity)
      for {
        a <- values
        b <- values
      } {
        val task = SASSFixture.task(
          "iset_probe": NonEmpty[String],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(a, 8)),
            KernelTask.Argument.Scalar(LittleEndian.toVec(b, 8))
          )
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val bytes    = service.read(output, 0L, 20).toOption.get
        val actual   = Vec.tabulate(5)(i => LittleEndian.decode(bytes, i * 4, 4))
        val expected = Vec(a < b, java.lang.Long.compareUnsigned(a, b) < 0, a <= b, a > b, a >= b)
          .map(flag => if (flag) 0xffffffffL else 0L)
        assertEquals(actual, expected, s"a=${a.toHexString}, b=${b.toHexString}")
      }
    }
  }
}
