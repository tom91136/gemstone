package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class FrndSuite extends munit.FunSuite {
  private def fixtureModule(sm: Int): Cubin.Module =
    SASSFixture.module(sm, Vec(SASSFixture.Kernel("probe", 352, Vec((0, 8), (8, 8)), registers = 10)))

  private val listings = Map(
    70 -> FrndFixtures.frndSm86ProbeSm70,
    75 -> FrndFixtures.frndSm86ProbeSm75,
    80 -> FrndFixtures.frndSm86ProbeSm80,
    86 -> FrndFixtures.frndSm86Probe,
    89 -> FrndFixtures.frndSm86ProbeSm89
  )

  private val vectors = FrndFixtures.frndSm86VectorsVectors.trim.split("\n").toVec.refine[NonEmpty[Vec[String]]].fold(
    fail(_),
    identity
  ).mapNonEmpty { line =>
    val words = line.split(" ")
    (java.lang.Long.parseLong(words(0), 16), java.lang.Long.parseLong(words(1), 16))
  }
  private def bytes(values: Vec[Long]): Vec[Byte] =
    Vec.from(values).flatMap(LittleEndian.toVec(_, 4))

  for (sm <- Vec(70, 75, 80, 86, 89)) test(s"sm_$sm FRND matches nearest-even rounding and measured NaN bits") {
    val module    = fixtureModule(sm)
    val kernel    = module.kernels.head
    val functions = ListingDecoder.parse(listings(sm), kernel.name, sm)
      .fold(fail(_), identity)
    assert(functions.head.instructions.exists(_.mnemonic.raw == "FRND"))
    val source = SASSFixture(module, kernel, functions)
    locally {
      val service = SASSFixture.Execution()
      val size    = vectors.positiveSize * 4
      val input   = service.allocate(size.toLong)
      val output  = service.allocate(size.toLong)
      assert(service.write(input, 0L, bytes(vectors.map(_._1))).isRight)
      val program = service.compile(source)
        .fold(e => fail(e.message), identity)
      val task = SASSFixture.task(
        kernel.name,
        LaunchGeometry.Dimensions(vectors.positiveSize, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(output, input).map(id => KernelTask.Argument.Memory(id, 0L))
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      assertEquals(
        service.read(output, 0L, size),
        Right(bytes(vectors.map(_._2)))
      )
    }
  }
}
