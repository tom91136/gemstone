package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class SharedMatrixLoadSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module =
    SASSFixture.module(
      89,
      Vec(SASSFixture.Kernel("matrix_load", 352, Vec(0 -> 8, 8 -> 4), registers = 30, shared = 528))
    )

  private val expected = (0 until 32).toVec.flatMap { lane =>
    Vec((1, false), (2, false), (4, false), (4, true)).flatMap { case (count, transpose) =>
      Vec.tabulate(count) { matrix =>
        val row  = if (transpose) lane % 4 * 2 else lane / 4
        val col  = if (transpose) lane / 4 else lane % 4 * 2
        val low  = 1 + matrix * 64 + row * 8 + col
        val high = low + (if (transpose) 8 else 1)
        low.toLong | (high.toLong << 16)
      }
    }
  }

  for (
    (fixture, listing) <- Vec(
      "probe"   -> LdsmFixtures.ldsmSm89Probe,
      "uniform" -> LdsmFixtures.ldsmSm89Uniform
    )
  )
    test(s"sm_89 LDSM $fixture preserves x1/x2/x4 and transpose lane layouts") {
      val module    = fixtureModule
      val functions =
        ListingDecoder.parse(listing, "matrix_load", 89)
          .fold(fail(_), identity)
      assertEquals(functions.head.instructions.count(_.mnemonic.raw == "LDSM"), 4)
      val lowered = SASSFixture(module, module.kernels.head, functions)
      locally {
        val service = SASSFixture.Execution()
        val output  =
          service.allocate((expected.size.toLong * 4).unsafe[Long > 0])
        val program = service.compile(lowered)
          .fold(e => fail(e.message), identity)
        val task = SASSFixture.task(
          "matrix_load": NonEmpty[String],
          LaunchGeometry.Dimensions(32, 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(16L, 4))
          )
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val bytes = service.read(output, 0L, (expected.size * 4).unsafe[Int >= 0]).toOption.get
        assertEquals(
          expected.indices.map(i => LittleEndian.decode(bytes, i * 4, 4)).toVec,
          expected
        )
      }
    }
}
