package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class ModernIntegerLoweringSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module =
    SASSFixture.module(89, Vec(SASSFixture.Kernel("divide_probe", 352, Vec((0, 8), (8, 4), (12, 4)), registers = 10)))

  test("compiler-produced sm_89 signed division and remainder cover boundary values") {
    val module   = fixtureModule
    val sassText = DivideFixtures.divideSm89
    assert(sassText.contains("IMAD.HI.U32 R3, R3, R7, R2"))
    val function           = ListingDecoder.parse(sassText, "divide_probe", 89).fold(fail(_), _.head)
    val lowered            = SASSFixture(module, module.kernels.head, Vec(function))
    def scalar(value: Int) = KernelTask.Argument.Scalar(
      LittleEndian.toVec(value.toLong, 4)
    )

    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(8L)
      val program = service.compile(lowered)
        .fold(e => fail(e.message), identity)
      Vec(
        Int.MinValue -> 3,
        Int.MaxValue -> 3,
        -7           -> 3,
        7            -> -3,
        -2147483647  -> -1,
        0            -> Int.MinValue
      ).foreach { case (a, b) =>
        val task = SASSFixture.task(
          "divide_probe": NonEmpty[String],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            scalar(a),
            scalar(b)
          )
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val bytes                   = service.read(output, 0L, 8).toOption.get
        def intAt(offset: Int): Int = (bytes(offset) & 255) | ((bytes(offset + 1) & 255) << 8) |
          ((bytes(offset + 2) & 255) << 16) | (bytes(offset + 3) << 24)
        assertEquals(intAt(0), a / b, s"$a / $b")
        assertEquals(intAt(4), a % b, s"$a % $b")
      }
    }
  }
}
