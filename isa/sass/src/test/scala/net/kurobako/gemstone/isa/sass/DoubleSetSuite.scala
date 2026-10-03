package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class DoubleSetSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module =
    SASSFixture.module(35, Vec(SASSFixture.Kernel("probe", 320, Vec((0, 8), (8, 8)), registers = 24)))

  test("compiler-produced legacy DSET preserves ordered, unordered, boolean and BF comparisons") {
    val pairs = Vec.nonEmpty(
      (1.0, 2.0),
      (2.0, 1.0),
      (1.0, 1.0),
      (-0.0, 0.0),
      (0.0, -0.0),
      (Double.NaN, 1.0),
      (1.0, Double.NaN),
      (Double.NaN, Double.NaN),
      (Double.PositiveInfinity, Double.PositiveInfinity),
      (Double.NegativeInfinity, Double.PositiveInfinity),
      (Double.PositiveInfinity, Double.NegativeInfinity),
      (java.lang.Double.MIN_VALUE, 0.0),
      (-java.lang.Double.MIN_VALUE, -0.0),
      (-1.0, -2.0)
    )
    val expected = pairs.zipWithIndex.flatMap { case ((a, b), index) =>
      val p     = index % 2 == 0
      val flags = Vec(
        a != b,
        a == b,
        a < b,
        a.isNaN || b.isNaN || a >= b,
        a == b && p,
        a < b || p,
        (a != b) != !p
      )
      flags.map(flag => if (flag) 0xffffffffL else 0L) :+ (if (a >= b) 0x3f800000L else 0L)
    }
    val module    = fixtureModule
    val functions = ListingDecoder.parse(DsetFixtures.dsetSm35ProbeSm35, "probe", 35)
      .fold(fail(_), identity)
    assertEquals(functions.head.instructions.count(_.mnemonic.raw == "DSET"), 8)
    val lowered = SASSFixture(module, module.kernels.head, functions)
    locally {
      val service = SASSFixture.Execution()
      val input   =
        service.allocate(pairs.positiveSize.toLong.widen[Long > 0] * 16L)
      val output =
        service.allocate(pairs.positiveSize.toLong.widen[Long > 0] * 32L)
      val bytes = pairs.flatMap { case (a, b) =>
        Vec(a, b).flatMap(value =>
          LittleEndian.toArray(java.lang.Double.doubleToRawLongBits(value), 8)
        )
      }
      service.write(input, 0L, Vec.from(bytes)).fold(e => fail(e.message), identity)
      val program = service.compile(lowered)
        .fold(e => fail(e.message), identity)
      val task = SASSFixture.task(
        "probe": NonEmpty[String],
        LaunchGeometry.Dimensions(pairs.positiveSize, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(output, 0L),
          KernelTask.Argument.Memory(input, 0L)
        )
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      val actual = service.read(output, 0L, expected.nonNegativeSize * 4).toOption.get
      assertEquals(
        expected.indices.map(i => LittleEndian.decode(actual, i * 4, 4)).toVec,
        expected
      )
    }
  }
}
