package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.util.Using

class KernargLayoutSuite extends Suite {
  test("listed arguments land at their metadata offsets with the hidden arguments the dispatch implies") {
    val memory      = ArrayMemory()
    val source      = memory.allocate(64L)
    val destination = memory.allocate(64L)
    memory.write(source, 0L, Vec.tabulate(16)(index => index + 100L).flatMap(LittleEndian.toVec(_, 4))).value
    val device = Device(BackendRegistry(Vec(AMDGPUBackend())), memory)
    Using.resource(
      device.load(ProgramFormat.HSACodeObject, Vec.unsafeWrapArray(bytes.hex(codeObjects.vectorCopyGfx906))).value
    ) { program =>
      val task = KernelTask.validated(
        "vector_copy",
        Dimensions(4, 1, 1),
        Dimensions(2, 1, 1),
        Vec(
          KernelTask.Argument.Memory(source, 0L),
          KernelTask.Argument.Memory(destination, 0L),
          KernelTask.Argument.Memory(destination, 0L)
        ),
        globalOffset = Coordinates(4, 0, 0)
      ).value
      assertEquals(device.launch(program, task).left.map(_.description: String), Right(Vec.empty[Event]))
      val copied = memory.bytes(destination).grouped(4).map(LittleEndian.decode(_, 0, 4)).toVector
      assertEquals(copied, Vector.fill(4)(0L) ++ Vector.tabulate(8)(index => index + 104L) ++ Vector.fill(4)(0L))
    }
  }

  test("the private and shared aperture bases are the upper words of the flat segment apertures") {
    val task   = KernelTask.validated("k", Dimensions(1, 1, 1), Dimensions(1, 1, 1), Vec.empty).value
    val layout = KernargLayout(
      Vec.empty,
      Vec(
        CodeObjectMetadata.HiddenArgument("hidden_private_base", 0, 4),
        CodeObjectMetadata.HiddenArgument("hidden_shared_base", 4, 4)
      )
    )
    val packed = layout.pack(Vec.empty, task.geometry, 8).value
    assertEquals(LittleEndian.decode(packed, 0, 4), VMAddressSpace.PrivateBase >>> 32)
    assertEquals(LittleEndian.decode(packed, 4, 4), VMAddressSpace.LocalBase >>> 32)
  }

  test("a host-built kernarg buffer receives the aperture bases in place") {
    val memory = ArrayMemory()
    val region = memory.allocate(16L)
    val task   = KernelTask.validated("k", Dimensions(1, 1, 1), Dimensions(1, 1, 1), Vec.empty).value
    val layout = KernargLayout(
      Vec.empty,
      Vec(
        CodeObjectMetadata.HiddenArgument("hidden_private_base", 8, 4),
        CodeObjectMetadata.HiddenArgument("hidden_shared_base", 12, 4)
      )
    )
    layout.stampApertures(memory, KernelTask.ArgumentBuffer(region, 0L), task.geometry).value
    val bytes = memory.bytes(region)
    assertEquals(LittleEndian.decode(bytes, 8, 4), VMAddressSpace.PrivateBase >>> 32)
    assertEquals(LittleEndian.decode(bytes, 12, 4), VMAddressSpace.LocalBase >>> 32)
  }
}
