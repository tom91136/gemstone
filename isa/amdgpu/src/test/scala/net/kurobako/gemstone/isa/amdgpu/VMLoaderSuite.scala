package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.NonNegative

import net.kurobako.gemstone.Constraint.NonEmptyString

import VMLoader.AMDGPUKernelVariants
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

final class VMLoaderSuite extends munit.FunSuite with CodeObjectTestSupport {
  private object EmptyMemory extends Memory with TestAddressSpace {
    override protected val regionSizes: Iterable[(MemoryRegion, Long)]                     = Seq.empty
    override def read(id: MemoryRegion, offset: NonNegative[Long], size: NonNegative[Int]) =
      Right(Vec.empty[Byte])
    override def write(id: MemoryRegion, offset: NonNegative[Long], bytes: Vec[Byte]) = Right(())
  }

  test("AMDGPU kernel aliases select the indexed implementation matching the code-object target") {
    val generic = "wrapped_target_archE"
    val gfx900  = "wrapped_target_archE900E"
    val gfx906  = "wrapped_target_archE906E"
    val aliases = AMDGPUKernelVariants.select(Set(generic, gfx900, gfx906), "gfx900").toOption.get

    assertEquals(aliases(generic), gfx900)
    assertEquals(aliases(gfx900), gfx900)
    assertEquals(aliases(gfx906), gfx900)
  }

  test("AMDGPU kernel aliases preserve unrelated symbols") {
    val aliases = AMDGPUKernelVariants.select(Set("first", "second"), "gfx900").toOption.get

    assertEquals(aliases, Map("first" -> "first", "second" -> "second"))
  }

  test("AMDGPU kernel aliases use the ROCm family enum for concrete GFX revisions") {
    val generic = "wrapped_target_archE"
    val gfx1030 = "wrapped_target_archE1030E"
    val aliases = AMDGPUKernelVariants.select(Set(generic, gfx1030), "gfx1036").toOption.get

    assertEquals(aliases(generic), gfx1030)
    assertEquals(aliases(gfx1030), gfx1030)
  }

  test("AMDGPU kernel aliases fall back to the generic body when the target alias is a stub") {
    val generic = "wrapped_target_archE"
    val gfx1030 = "wrapped_target_archE1030E"
    val aliases = AMDGPUKernelVariants.select(
      Set(generic, gfx1030),
      "gfx1036",
      name => name == generic
    ).toOption.get

    assertEquals(aliases(generic), generic)
    assertEquals(aliases(gfx1030), generic)
  }

  test("AMDGPU kernel aliases reject a variant group without the code-object target") {
    val generic = "wrapped_target_archE"
    val gfx906  = "wrapped_target_archE906E"

    assertEquals(
      AMDGPUKernelVariants.select(Set(generic, gfx906), "gfx900", _ => false),
      Left("AMDGPU kernel variant wrapped_target_archE has no gfx900 implementation")
    )
  }

  test("AMDGPU code-object loading rejects an invalid ELF header before deferring its index") {
    val result = CodeObject.Decoder.decode(Vec.empty).left.map(_.message)

    assertEquals(result, Left("expected a little-endian ELF64 code object"))
  }

  test("AMDGPU code-object loading defers malformed section-table failures until first launch") {
    val header = Array.ofDim[Byte](64)
    header(0) = 0x7f.toByte
    header(1) = 'E'.toByte
    header(2) = 'L'.toByte
    header(3) = 'F'.toByte
    header(4) = 2
    header(5) = 1
    header(18) = 224.toByte
    val target = "amdgcn-amd-amdhsa--gfx900\u0000".getBytes(java.nio.charset.StandardCharsets.US_ASCII)
    Array.copy(target, 0, header, 24, target.length)
    val program = CodeObject.Decoder.decode(Vec.unsafeFromArray(header)).toOption.get
    val machine = VirtualMachine(Observer.discard, Scheduler.Sequential)
    val loaded  = machine.load(program).toOption.get
    val task    = KernelTask.validated(
      NonEmptyString("kernel"),
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get

    assertEquals(
      machine.execute(loaded, task, EmptyMemory, TaskControl.default),
      Left(Fault.InvalidTask("invalid ELF section table"))
    )
  }
}
