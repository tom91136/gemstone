package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LdmatrixHmma1688Suite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module = SASSFixture.module(
    80,
    Vec(SASSFixture.Kernel("ldmatrix_trans_mma_f16", 352, Vec((0, 8)), registers = 18, shared = 384))
  )

  private def half(value: Int): Long            = IEEE754.floatToBinary16(value.toFloat).raw & 0xffffL
  private def packed(low: Int, high: Int): Long = half(low) | (half(high) << 16)

  private val expected = (0 until 32).toVec.flatMap { lane =>
    val group  = lane / 4
    val thread = lane % 4
    Vec(group, group + 8).map { row =>
      val low = (0 until 8).map(k =>
        (1 + ((row * 2 + k * 5) % 3)) *
          (1 + ((k * 5 + thread * 2 * 7) % 3))
      ).sum
      val high = (0 until 8).map(k =>
        (1 + ((row * 2 + k * 5) % 3)) *
          (1 + ((k * 5 + (thread * 2 + 1) * 7) % 3))
      ).sum
      packed(low, high)
    }
  }

  test("nonuniform ldmatrix.trans fragments feed HMMA.1688.F16 in the measured lane layout") {
    val module   = fixtureModule
    val sassText = HmmaFixtures.ldsmHmma1688TransSm80Probe
    assert(sassText.contains("LDSM.16.MT88"))
    assert(sassText.contains("LDSM.16.M88.2"))
    assert(sassText.contains("HMMA.1688.F16"))
    val function = ListingDecoder.parse(sassText, "ldmatrix_trans_mma_f16", module.sm.raw).fold(fail(_), _.head)
    val kernel   = module.kernels.find(_.name == "ldmatrix_trans_mma_f16").get
    val sass     = SASSFixture(module, kernel, Vec(function))
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(256L)
      val program = service.compile(sass)
        .fold(error => fail(error.message), identity)
      val task = SASSFixture.task(
        "ldmatrix_trans_mma_f16": NonEmpty[String],
        LaunchGeometry.Dimensions(32, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(output, 0L))
      )
      service.run(program, task).fold(error => fail(error.message), identity)
      val bytes  = service.read(output, 0L, 256).toOption.get
      val actual = (0 until 64).map(index => LittleEndian.decode(bytes, index * 4, 4)).toVec
      assertEquals(actual, expected)
    }
  }
}
