package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class Hadd2SelectedSm89Suite extends munit.FunSuite {
  {
    def fixtureModule: Cubin.Module =
      SASSFixture.module(89, Vec(SASSFixture.Kernel("convert_half", 352, Vec((0, 8), (8, 8)), registers = 12)))

    test("CUDA 13.3 sm_89 HADD2.F32 converts selected half lanes including IEEE edge values") {
      val module   = fixtureModule
      val sassText = Hadd2Fixtures.hadd2Sm89
      assert(sassText.contains("HADD2.F32 R7, -RZ, R2.reuse.H0_H0"))
      assert(sassText.contains("HADD2.F32 R9, -RZ, R2.H1_H1"))
      val function = ListingDecoder.parse(sassText, "convert_half", 89).fold(fail(_), _.head)
      val kernel   = module.kernels.find(_.name == "convert_half").get
      val sass     = SASSFixture(module, kernel, Vec(function))

      val halves = Vec(
        0x0000,
        0x8000,
        0x0001,
        0x03ff,
        0x0400,
        0x3c00,
        0xbc00,
        0x7c00,
        0xfc00,
        0x7c01,
        0xfc01,
        0x7e00,
        0xfe00,
        0x3555
      )
      val inputBytes = Vec.from(halves).flatMap(value => LittleEndian.toVec(value.toLong, 2))
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate((halves.size.toLong * 4).unsafe[Long > 0])
        val input   =
          service.allocate(inputBytes.size.toLong.unsafe[Long > 0])
        assert(service.write(input, 0L, inputBytes).isRight)
        val program = service.compile(sass)
          .fold(error => fail(error.message), identity)
        val task = SASSFixture.task(
          "convert_half": NonEmpty[String],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions((halves.size / 2).unsafe[Int > 0], 1, 1),
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Memory(input, 0L)
          )
        )
        service.run(program, task).fold(error => fail(error.message), identity)
        val words     = service.readWords(output, halves.nonNegativeSize)
        val knownBits = Map(
          0x0000 -> 0x00000000,
          0x8000 -> 0x80000000,
          0x0001 -> 0x33800000,
          0x03ff -> 0x387fc000,
          0x0400 -> 0x38800000,
          0x3c00 -> 0x3f800000,
          0xbc00 -> 0xbf800000,
          0x7c00 -> 0x7f800000,
          0xfc00 -> 0xff800000
        )
        halves.zipWithIndex.foreach { case (half, index) =>
          val actual   = words(index).toInt
          val expected = java.lang.Float.floatToRawIntBits(IEEE754.binary16ToFloat(Binary16.low(half.toLong)))
          knownBits.get(half).foreach(bits => assertEquals(actual, bits, f"half=0x$half%04x IEEE binary16 conversion"))
          if (java.lang.Float.isNaN(java.lang.Float.intBitsToFloat(expected)))
            assertEquals(actual, 0x7fffffff, f"half=0x$half%04x NVIDIA HADD2 canonical NaN")
          else assertEquals(actual, expected, f"half=0x$half%04x")
        }
      }
    }
  }

}
