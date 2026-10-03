package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyMinMaxLoweringSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module = SASSFixture.module(
    35,
    Vec(
      SASSFixture.Kernel("minmax_signed_probe", 320, Vec((0, 8), (8, 8), (16, 8)), registers = 8),
      SASSFixture.Kernel("minmax_probe", 320, Vec((0, 8), (8, 8), (16, 8)), registers = 8)
    )
  )

  private def scalar(value: Long): KernelTask.Argument = KernelTask.Argument.Scalar(
    LittleEndian.toVec(value, 8)
  )

  test("CUDA 11.8 sm_35 extended IMNMX implements signed and unsigned 64-bit min/max") {
    val module  = fixtureModule
    val outputs = Vec(
      (
        "minmax_probe",
        Vec(
          (0x00000001ffffffffL, 0x0000000200000000L),
          (0x0000000200000000L, 0x00000001ffffffffL),
          (0x00000002ffffffffL, 0x0000000200000001L),
          (0x0000000100000001L, 0x00000001ffffffffL),
          (0L, -1L),
          (-1L, Long.MinValue),
          (Long.MaxValue, Long.MinValue)
        ),
        true
      ),
      (
        "minmax_signed_probe",
        Vec(
          (Long.MinValue, Long.MaxValue),
          (Long.MaxValue, Long.MinValue),
          (-1L, 0L),
          (0L, -1L),
          (Long.MinValue, -1L),
          (0x0000000200000001L, 0x00000001ffffffffL),
          (0x0000000100000001L, 0x00000001ffffffffL)
        ),
        false
      )
    )

    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(16L)
      outputs.foreach { case (name, vectors, unsigned) =>
        val kernel = module.kernels.find(_.name == name).get
        val text   = if (unsigned) MinMaxFixtures.minmaxSm35 else MinMaxFixtures.minmaxSignedSm35
        assert(text.contains("IMNMX.U32.XLO"))
        assert(text.contains(if (unsigned) "IMNMX.U32.XHI" else "IMNMX.XHI"))
        val function = ListingDecoder.parse(text, name, 35).fold(fail(_), _.head)
        val lowered  = SASSFixture(module, kernel, Vec(function))
        val program  = service.compile(lowered)
          .fold(e => fail(e.message), identity)
        vectors.foreach { case (a, b) =>
          val task = SASSFixture.task(
            name.unsafe[NonEmpty[String]],
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec(KernelTask.Argument.Memory(output, 0L), scalar(a), scalar(b))
          )
          service.run(program, task).fold(e => fail(e.message), identity)
          val bytes                     = service.read(output, 0L, 16).toOption.get
          def longAt(offset: Int): Long = LittleEndian.decode(Vec.toArray(bytes), offset, 8)
          val (minimum, maximum)        = if (unsigned) {
            (
              if (java.lang.Long.compareUnsigned(a, b) <= 0) a else b,
              if (java.lang.Long.compareUnsigned(a, b) >= 0) a else b
            )
          } else (math.min(a, b), math.max(a, b))
          assertEquals(longAt(0), minimum)
          assertEquals(longAt(8), maximum)
        }
      }
    }
  }
}
