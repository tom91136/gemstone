package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyVideoMinMaxSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module = SASSFixture.module(
    35,
    Vec(SASSFixture.Kernel("video_minmax", 320, Vec((0, 8), (8, 4), (12, 4), (16, 4)), registers = 15))
  )

  test("Kepler VMNMX preserves signedness and secondary min, max and accumulation") {
    val module   = fixtureModule
    val text     = VideoMinMaxFixtures.videoMinmaxSm35
    val function = ListingDecoder.parse(text, "video_minmax", 35).fold(fail(_), _.head)
    assertEquals(function.instructions.count(_.mnemonic.raw == "VMNMX"), 12)
    val source = SASSFixture(module, module.kernels.head, Vec(function))
    val cases  = Vec(
      (1, 2, 3),
      (3, 2, 1),
      (-1, 1, 0),
      (Int.MinValue, Int.MaxValue, -1),
      (Int.MaxValue, Int.MaxValue, 1),
      (Int.MinValue, Int.MinValue, -1),
      (-1, -2, -3)
    )
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(48L)
      val program = service.compile(source)
        .fold(e => fail(e.message), identity)
      cases.foreach { case (a, b, c) =>
        val expected = Vec(false, true).flatMap { unsigned =>
          def widen(value: Int): Long = if (unsigned) value.toLong & 0xffffffffL else value.toLong
          val low                     = math.min(widen(a), widen(b)); val high = math.max(widen(a), widen(b))
          Vec(low, high).flatMap(value =>
            Vec(math.min(value, widen(c)), math.max(value, widen(c)), value + widen(c))
          )
        }.map(_.toInt)
        val task = SASSFixture.task(
          "video_minmax": NonEmpty[String],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec[KernelTask.Argument](KernelTask.Argument.Memory(output, 0L)) ++
            Vec(a, b, c).map(value =>
              KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong, 4))
            )
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        assertEquals(
          service.read(output, 0L, 48),
          Right(Vec.from(expected).flatMap(value => LittleEndian.toVec(value.toLong, 4))),
          s"a=$a b=$b c=$c"
        )
      }
    }
  }
}
