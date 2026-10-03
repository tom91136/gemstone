package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class ModernSharedPointerSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module = SASSFixture.module(
    89,
    Vec(SASSFixture.Kernel("shared_pointer_probe", 352, Vec((0, 8), (8, 4), (12, 4)), registers = 12, shared = 16))
  )

  test("sm_89 opaque generic shared pointers alias direct shared-space accesses") {
    val module   = fixtureModule
    val sassText = SharedMemoryFixtures.modernSharedPointerSm89Probe
    assert(sassText.contains("ST.E [R4.64]"))
    assert(sassText.contains("LD.E R5, [R4.64+0x4]"))
    assert(sassText.contains("STS [0x4]"))
    assert(sassText.contains("LDS R7, [RZ]"))
    val function = ListingDecoder.parse(sassText, "shared_pointer_probe", 89).fold(fail(_), _.head)
    val kernel   = module.kernels.find(_.name == "shared_pointer_probe").get
    val sass     = SASSFixture(module, kernel, Vec(function))

    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(16L)
      val program = service.compile(sass)
        .fold(e => fail(e.message), identity)
      val task = SASSFixture.task(
        "shared_pointer_probe": NonEmpty[String],
        LaunchGeometry.Dimensions(4, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(output, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0x1234abcdL, 4)),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0x89abcdefL, 4))
        )
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      val bytes = service.read(output, 0L, 16).toOption.get
      val words = (0 until 4).map { i =>
        LittleEndian.decode(bytes, i * 4, 4)
      }.toVec
      assert(words.take(2).exists(_ != 0L), s"expected a nonzero shared generic pointer: $words")
      assertEquals(words.drop(2), Vec(0x1234abcdL, 0x89abcdefL))
    }
  }
}
