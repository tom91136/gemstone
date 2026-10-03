package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyMultiplySubtractSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module = SASSFixture.module(
    35,
    Vec(SASSFixture.Kernel("subtract_product", 320, Vec((0, 8), (8, 4), (12, 4), (16, 8)), registers = 6))
  )

  test("CUDA 11.8 sm_35 IMAD low/high subtracts a 64-bit addend with borrow") {
    val module   = fixtureModule
    val sassText = MultiplySubtractFixtures.multiplySubtractSm35
    assert(sassText.contains("IMAD.U32.U32 R4.CC"))
    assert(sassText.contains("IMAD.U32.U32.HI.X R5"))
    val function = ListingDecoder.parse(sassText, "subtract_product", 35).fold(fail(_), _.head)
    val kernel   = module.kernels.find(_.name == "subtract_product").get
    val sass     = SASSFixture(module, kernel, Vec(function))
    val cases    = Vec(
      (0L, 0L, 0L),
      (0xffffffffL, 0xffffffffL, 0L),
      (0xffffffffL, 0xffffffffL, 1L),
      (0x10000L, 0x10000L, 0L),
      (0xffffffffL, 2L, 0x100000000L),
      (0xffffL, 0xffffL, 0xfffe0001L),
      (0xffffL, 0xffffL, 0xfffe0002L),
      (0xffffL, 0xffffL, 0x1fffe0001L),
      (0x80000000L, 0xffffffffL, 0x7fffffffffffffffL)
    )
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(8L)
      val program = service.compile(sass)
        .fold(e => fail(e.message), identity)
      cases.foreach { case (a, b, c) =>
        val task = SASSFixture.task(
          "subtract_product": NonEmpty[String],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(a, 4)),
            KernelTask.Argument.Scalar(LittleEndian.toVec(b, 4)),
            KernelTask.Argument.Scalar(LittleEndian.toVec(c, 8))
          )
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val bytes   = service.read(output, 0L, 8).toOption.get
        val actual  = LittleEndian.decode(bytes, 0, 8)
        val product = (a & 0xffffffffL) * (b & 0xffffffffL)
        assertEquals(actual, product - c)
      }
    }
  }
}
