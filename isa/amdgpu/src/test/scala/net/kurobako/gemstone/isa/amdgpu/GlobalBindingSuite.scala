package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import fixtures.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
class GlobalBindingSuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  private val code = Vec[Long](
    0xc00a0100L, 0x00000000L, 0xbf8cc07fL, 0xbe8800ffL, 0x00003000L, 0xbe890080L, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x800a810aL, 0x7e000280L, 0x7e02020aL, 0xdc708000L, 0x00040100L, 0xbf8c0f70L, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x7e02020aL, 0xdc708000L, 0x00060100L, 0x880a0804L, 0x880b0905L, 0x870a0b0aL, 0xbf06800aL, 0x850a8081L, 0x7e02020aL,
    0x7e000284L, 0xdc708000L, 0x00060100L, 0xbf810000L
  ).flatMap(value => LittleEndian.toVec(value, 4))
  private class Memory(nativeAddresses: Map[AllocationId, GuestAddress] = Map.empty)
      extends vm.Memory with TestAddressSpace {
    val storage: Map[AllocationId.Type, Array[Byte]] =
      Map(AllocationId(1) -> Array.ofDim[Byte](32), AllocationId(2) -> Array.ofDim[Byte](16))
    override protected def regionSizes: Iterable[(AllocationId, Long)]       = storage.view.mapValues(_.length.toLong)
    override protected def regionAddress(region: AllocationId): GuestAddress =
      nativeAddresses.getOrElse(region, super.regionAddress(region))
    override def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
      Right(Vec.from(storage(id).slice(offset.raw.toInt, offset.raw.toInt + size.raw)))
    override def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] = {
      val _ = bytes.copyToArray(storage(id), offset.raw.toInt, bytes.size); Right(())
    }
  }
  test("bound ELF global and kernel allocation pointer use one backing across dispatches") {
    val memory = Memory()
    val shadow = Array.fill[Byte](16)(0x55)
    val kernel = CodeObjectKernel(
      name = "kernel",
      target = Target("gfx900"),
      kernargSize = 16,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0x80,
      kernelCodeProperties = 0x08,
      code = code,
      globalMemory = Array(0x3000L -> shadow)
    )
    val prepared = VMCompiler.compile(kernel).toOption.get
    val global   = AllocationId(1)
    val output   = AllocationId(2)
    val dispatch = KernelTask.validated(
      "kernel",
      Dimensions.One,
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(global, 8L),
        KernelTask.Argument.Memory(output, 0L)
      )
    ).toOption.get
    for (value <- Seq(41L, 100L)) {
      assertEquals(
        memory.write(global, 8L, LittleEndian.toVec(value, 4)),
        Right(())
      )
      assertEquals(
        VMInterpreter.execute(
          prepared,
          dispatch,
          memory,
          TaskControl(),
          Workgroups.Sequential,
          Vec(LoadedProgram.GlobalBinding(
            0x3000,
            16,
            global,
            8L,
            32L
          ))
        ),
        Right(())
      )
      assertEquals(LittleEndian.decode(memory.storage(global), 8, 4), value + 1)
      assertEquals(LittleEndian.decode(memory.storage(output), 0, 4), value + 1)
      assertEquals(LittleEndian.decode(memory.storage(output), 4, 4), 1L)
      assert(shadow.forall(_ == 0x55.toByte), "cached/private ELF image must not become the bound backing")
    }
  }
  test("global ELF pointers stay canonical while native global pointers are storage aliases only") {
    val global = AllocationId(1)
    val output = AllocationId(2)
    val memory = Memory(Map(global -> GuestAddress(0x8000L)))
    val kernel = CodeObjectKernel(
      name = "kernel",
      target = Target("gfx900"),
      kernargSize = 16,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0x80,
      kernelCodeProperties = 0x08,
      code = code,
      globalMemory = Array(0x3000L -> Array.ofDim[Byte](16))
    )
    val prepared = VMCompiler.compile(kernel).toOption.get
    val bindings = Vec(LoadedProgram.GlobalBinding(
      0x3000L,
      16,
      global,
      8L,
      32L
    ))
    assertEquals(
      memory.write(global, 8L, LittleEndian.toVec(41L, 4)),
      Right(())
    )
    for (
      (pointer, equal, value) <- Seq(
        (KernelTask.Argument.Memory(global, 8L), 1L, 42L),
        (KernelTask.Argument.Scalar(LittleEndian.toVec(0x8008L, 8)), 0L, 43L)
      )
    ) {
      val dispatch = KernelTask.validated(
        "kernel",
        Dimensions.One,
        Dimensions.One,
        Vec(pointer, KernelTask.Argument.Memory(output, 0L))
      ).toOption.get
      assertEquals(
        VMInterpreter.execute(
          prepared,
          dispatch,
          memory,
          TaskControl(),
          Workgroups.Sequential,
          bindings
        ),
        Right(())
      )
      assertEquals(LittleEndian.decode(memory.storage(global), 8, 4), value)
      assertEquals(LittleEndian.decode(memory.storage(output), 0, 4), value)
      assertEquals(LittleEndian.decode(memory.storage(output), 4, 4), equal)
    }
  }

}
