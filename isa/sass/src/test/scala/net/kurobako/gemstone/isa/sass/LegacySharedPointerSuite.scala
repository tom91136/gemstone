package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacySharedPointerSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module = SASSFixture.module(
    35,
    Vec(SASSFixture.Kernel("shared_base_probe", 320, Vec((0, 8), (8, 8)), registers = 14, shared = 512))
  )

  test("sm_35 cvta shared base is nonnull and generic LD/ST dispatches to shared memory") {
    val module = fixtureModule
    val sass   = SharedMemoryFixtures.sharedPointerSm35SharedPointerSm35
    assert(sass.contains("c[0x0][0x20]"))
    assert(sass.contains("LD.E R6, [R4]"))
    assert(sass.contains("ST.E [R4], R6"))
    val function = ListingDecoder.parse(sass, "shared_base_probe", 35).fold(fail(_), _.head)
    val lowered  = SASSFixture(module, module.kernels.head, Vec(function))
    assert(lowered.instructions.exists {
      case Instruction.Load(_, _, width, _, MemoryAccess.Generic(false)) if width.raw == 32 => true
      case _                                                                                => false
    })
    assert(lowered.instructions.exists {
      case Instruction.Store(_, _, width, MemoryAccess.Generic(false)) if width.raw == 32 => true
      case _                                                                              => false
    })

    locally {
      val service  = SASSFixture.Execution()
      val pointers = service.allocate((256L * 8L).unsafe[Long > 0])
      val values   = service.allocate((128L * 4L).unsafe[Long > 0])
      val input    = Vec.from((0 until 128).flatMap(index =>
        LittleEndian.toVec(index.toLong + 100L, 4)
      ))
      assert(service.write(values, 0L, input).isRight)
      val program = service.compile(lowered)
        .fold(error => fail(error.message), identity)
      val task = SASSFixture.task(
        "shared_base_probe": NonEmpty[String],
        LaunchGeometry.Dimensions(128, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(pointers, 0L),
          KernelTask.Argument.Memory(values, 0L)
        )
      )
      service.run(program, task).fold(error => fail(error.message), identity)

      val output        = service.read(pointers, 0L, 256 * 8).toOption.get
      val pointerValues = Vec.tabulate(128)(index =>
        LittleEndian.decode(
          output,
          index * 8,
          8
        )
      )
      assertEquals(pointerValues, Vec.fill(128)(0x10000000L))
      val loadedValues = Vec.tabulate(128)(index =>
        LittleEndian.decode(
          output,
          (128 + index) * 8,
          8
        )
      )
      assertEquals(loadedValues, Vec.tabulate(128)(index => index.toLong + 107L))
    }
  }
}
