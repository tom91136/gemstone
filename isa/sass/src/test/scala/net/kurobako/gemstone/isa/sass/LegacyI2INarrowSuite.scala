package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyI2INarrowSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module =
    SASSFixture.module(35, Vec(SASSFixture.Kernel("probe", 320, Vec(0 -> 8, 8 -> 8), registers = 8)))

  test("unsupported I2I modifiers, destinations, and operands fail through the cubin compiler") {
    val module = fixtureModule
    val text   = LegacyI2iS16S8Fixtures.legacyI2iS16S8Sm35Probe
    val kernel = module.kernels.head
    Vec(
      text.replace("I2I.S16.S8 R0, R0", "I2I.S16.S8.SAT R0, R0"),
      text.replace("I2I.S16.S8 R0, R0", "I2I.S16.S8 R0.CC, R0"),
      text.replace("I2I.S16.S8 R0, R0", "I2I.S16.S8 R0, 0xff"),
      text.replace("I2I.S16.S8 R0, R0", "I2I.S16.S8 R0, -R0")
    ).foreach { unsupported =>
      val function = ListingDecoder.parse(unsupported, "probe", 35).fold(fail(_), _.head)
      assert(SASSFixture.lower(module, kernel, Vec(function)).isLeft)
    }
  }

  test("compiler-produced sm_35 signed I2I widening and negation preserve signed 16-bit values") {
    val module = fixtureModule
    val text   = LegacyI2iS16S8Fixtures.legacyI2iS16S8Sm35Probe
    assert(text.contains("I2I.S16.S8"))
    assert(text.contains("I2I.S16.S16"))
    val function = ListingDecoder.parse(text, "probe", 35).fold(fail(_), _.head)
    val lowered  = SASSFixture(module, module.kernels.head, Vec(function))

    val cases = Vec(
      (-128, Short.MinValue.toInt),
      (-1, -1),
      (0, 0),
      (1, 1),
      (127, Short.MaxValue.toInt)
    )
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(4L.unsafe[Long > 0])
      val input   = service.allocate(4L.unsafe[Long > 0])
      val program = service.compile(lowered)
        .fold(error => fail(error.message), identity)
      cases.foreach { case (byte, half) =>
        val inputBytes = Array(byte.toByte, 0.toByte, half.toByte, (half >> 8).toByte)
        assert(service.write(input, 0L, Vec.from(inputBytes)).isRight)
        val task = SASSFixture.task(
          "probe": NonEmpty[String],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Memory(input, 0L)
          )
        )
        service.run(program, task).fold(error => fail(error.message), identity)
        val bytes = service.read(output, 0L, 4).toOption.get
        assertEquals(LittleEndian.decode(bytes, 0, 2), byte.toShort.toLong & 0xffffL)
        assertEquals(
          LittleEndian.decode(bytes, 2, 2),
          (-half.toInt).toShort.toLong & 0xffffL
        )
      }
    }
  }
}
