package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class DoubleTensorSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module =
    SASSFixture.module(80, Vec(SASSFixture.Kernel("double_mma", 352, Vec((0, 8)), registers = 16)))

  test("compiler DMMA.884 preserves FP64 fragment layout and overlapping input/output pairs") {
    val module   = fixtureModule
    val text     = DmmaFixtures.dmmaSm80
    val function = ListingDecoder.parse(text, "double_mma", 80).fold(fail(_), _.head)
    assert(function.instructions.exists(i =>
      i.mnemonic.raw == "DMMA" &&
        i.modifiers.map(_.name).contains("884") && i.operands.head == i.operands(2)
    ))
    val source = SASSFixture(module, module.kernels.head, Vec(function))
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(512L)
      val program = service.compile(source)
        .fold(e => fail(e.message), identity)
      val task = SASSFixture.task(
        "double_mma": NonEmpty[String],
        LaunchGeometry.Dimensions(32, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(output, 0L))
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      val expected = Vec.from((0 until 32).flatMap { lane =>
        val row = lane / 4
        (0 until 2).map { item =>
          val column  = 2 * (lane % 4) + item
          val product = (0 until 4).map(k => (4 * row + k + 1) * (2 * (4 * column + k) + 1)).sum
          product.toDouble + (if (item == 0) 5.0 else -7.0)
        }
      }).flatMap(value => LittleEndian.toVec(java.lang.Double.doubleToRawLongBits(value), 8))
      assertEquals(service.read(output, 0L, 512), Right(expected))
    }
  }
}
