package net.kurobako.gemstone.isa.amdgpu

import VMLoader.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class VMLoaderSuite extends munit.FunSuite {
  private object EmptyMemory extends Memory with TestAddressSpace {
    override protected val regionSizes: Iterable[(AllocationId, Long)]                               = Seq.empty
    override def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
      Right(Vec.empty[Byte])
    override def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] = Right(())
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

  test("kernel aliases prefer usable exact revisions before family and generic bodies") {
    val generic = "wrapped_target_archE"
    val family  = "wrapped_target_archE1030E"
    val exact   = "wrapped_target_archE1036E"
    val names   = Set(generic, family, exact)
    assertEquals(AMDGPUKernelVariants.select(names, "gfx1036").toOption.get(generic), exact)
    assertEquals(AMDGPUKernelVariants.select(names, "gfx1036", _ != exact).toOption.get(generic), family)
    assertEquals(AMDGPUKernelVariants.select(names, "gfx1036", _ == generic).toOption.get(exact), generic)
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
      Left[NonEmpty[String], Nothing]("AMDGPU kernel variant wrapped_target_archE has no gfx900 implementation")
    )
  }

  test("AMDGPU code-object loading rejects an invalid ELF header before deferring its index") {
    val result = CodeObject.decode(Vec.empty).left.map(_.message)

    assertEquals(result, Left[NonEmpty[String], Nothing]("expected a little-endian ELF64 code object"))
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
    val program = CodeObject.decode(Vec.unsafeWrapArray(header)).toOption.get

    val loaded = LoadedProgram.load(program).toOption.get
    val task   = KernelTask.validated(
      "kernel",
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get

    assertEquals(
      loaded.execute(task, EmptyMemory, TaskControl()),
      Left(Fault.InvalidTask("invalid ELF section table"))
    )
  }
}
