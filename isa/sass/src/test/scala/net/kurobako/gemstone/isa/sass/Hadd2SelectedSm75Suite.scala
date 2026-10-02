package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class Hadd2SelectedSm75Suite extends munit.FunSuite {
  {
    def fixtureModule: Cubin.Module =
      SASSFixture.module(75, Vec(SASSFixture.Kernel("probe", 352, Vec(0 -> 8, 8 -> 8), registers = 5)))

    test("CUDA 13.3 sm_75 HADD2.F32 accepts the selected negative-zero source") {
      val module   = fixtureModule
      val sassText = Hadd2Fixtures.hadd2F32Sm75Probe
      assert(sassText.contains("HADD2.F32 R2, R0.reuse.H0_H0, -RZ.H0_H0"))
      assert(sassText.contains("HADD2.F32 R0, R0.H1_H1, -RZ.H0_H0"))
      val function = ListingDecoder.parse(sassText, "probe", 75).fold(fail(_), _.head)
      val kernel   = module.kernels.find(_.name == "probe").get
      val sass     = SASSFixture(module, kernel, Vec(function))

      val halves = Vec(
        0x0000, 0x8000, 0x0001, 0x03ff, 0x0400, 0x3c00, 0xbc00,
        0x7c00, 0xfc00, 0x7c01, 0xfc01, 0x7e00, 0xfe00, 0x3555
      )
      val packed     = halves.grouped(2).map(pair => pair(0).toLong | (pair(1).toLong << 16)).toVec
      val inputBytes = Vec.from(packed).flatMap(value => LittleEndian.toVec(value, 4))
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate((halves.size.toLong * 4).unsafe[Long > 0])
        val input   = service.allocate(inputBytes.size.toLong.unsafe[Long > 0])
        assert(service.write(input, 0L, inputBytes).isRight)
        val program = service.compile(sass)
          .fold(error => fail(error.message), identity)
        val task = SASSFixture.task(
          "probe": NonEmpty[String],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions((halves.size / 2).unsafe[Int > 0], 1, 1),
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Memory(input, 0L)
          )
        )
        service.run(program, task).fold(error => fail(error.message), identity)
        val words = service.readWords(output, halves.size.unsafe[Int >= 0])
        halves.zipWithIndex.foreach { case (half, index) =>
          val actual    = words(index).toInt
          val converted = java.lang.Float.floatToRawIntBits(IEEE754.binary16ToFloat(Binary16.low(half.toLong)))
          val expected = if (java.lang.Float.isNaN(java.lang.Float.intBitsToFloat(converted))) 0x7fffffff else converted
          assertEquals(actual, expected, f"half=0x$half%04x")
        }
      }
    }
  }

}
