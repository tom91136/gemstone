package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyI2ICCZeroSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module =
    SASSFixture.module(35, Vec(SASSFixture.Kernel("probe", 320, Vec((0, 8), (8, 8)), registers = 5)))

  private val cases = Vec(
    0L                  -> 0L,
    1L                  -> 1L,
    0x800000000000L     -> 1L,
    0x0001000000000000L -> 0L,
    0xffffffffffffffffL -> 1L,
    0x0000000100000000L -> 1L,
    0xffff000000000000L -> 0L
  )

  test("compiler-produced sm_35 I2I CC chain reports a masked 48-bit nonzero test") {
    val module = fixtureModule
    val text   = LegacyI2iCcFixtures.legacyI2iCcSm35Probe
    assert(text.contains("I2I.U32.U32 RZ.CC"))
    assert(text.contains("LOP32I.AND.X RZ.CC"))
    assert(text.contains("CSETP.NE.AND P0, PT, CC, PT"))
    val function = ListingDecoder.parse(text, "probe", 35).fold(fail(_), _.head)
    val lowered  = SASSFixture(module, module.kernels.head, Vec(function))

    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(4L)
      val program = service.compile(lowered)
        .fold(error => fail(error.message), identity)
      cases.foreach { case (input, expected) =>
        val task = SASSFixture.task(
          "probe": NonEmpty[String],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Scalar(LittleEndian.toVec(input, 8)),
            KernelTask.Argument.Memory(output, 0L)
          )
        )
        service.run(program, task).fold(error => fail(error.message), identity)
        val bytes = service.read(output, 0L, 4).toOption.get
        assertEquals(LittleEndian.decode(bytes, 0, 4), expected, f"input=$input%016x")
      }
    }
  }
}
