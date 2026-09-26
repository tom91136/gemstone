package net.kurobako.gemstone.isa.amdgpu

import CodeObject.*

import net.kurobako.gemstone.{NonNegativeInt, NonNegativeLong, PositiveLong, Vec}
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions
import net.kurobako.gemstone.binary.{LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.memory.GuestAddress
final class GlobalBindingSuite extends munit.FunSuite with CodeObjectTestSupport {
  private val code = Vec[Long](
    0xc00a0100L, 0x00000000L, 0xbf8cc07fL, 0xbe8800ffL, 0x00003000L, 0xbe890080L, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x800a810aL, 0x7e000280L, 0x7e02020aL, 0xdc708000L, 0x00040100L, 0xbf8c0f70L, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x7e02020aL, 0xdc708000L, 0x00060100L, 0x880a0804L, 0x880b0905L, 0x870a0b0aL, 0xbf06800aL, 0x850a8081L, 0x7e02020aL,
    0x7e000284L, 0xdc708000L, 0x00060100L, 0xbf810000L
  ).flatMap(value => LittleEndian.toVec(value, ScalarByteWidth.Dword))
  private final class Memory(nativeAddresses: Map[MemoryRegion, GuestAddress] = Map.empty)
      extends net.kurobako.gemstone.vm.Memory with TestAddressSpace {
    val storage = Map(MemoryRegion(1) -> Array.ofDim[Byte](32), MemoryRegion(2) -> Array.ofDim[Byte](16))
    override protected def regionSizes: Iterable[(MemoryRegion, Long)]       = storage.view.mapValues(_.length.toLong)
    override protected def regionAddress(region: MemoryRegion): GuestAddress =
      nativeAddresses.getOrElse(region, super.regionAddress(region))
    override def read(id: MemoryRegion, offset: NonNegativeLong, size: NonNegativeInt) =
      Right(Vec.from(storage(id).slice(offset.raw.toInt, offset.raw.toInt + size.raw)))
    override def write(id: MemoryRegion, offset: NonNegativeLong, bytes: Vec[Byte]) = {
      val _ = bytes.copyToArray(storage(id), offset.raw.toInt, bytes.size); Right(())
    }
  }
  test("bound ELF global and kernel allocation pointer use one backing across dispatches") {
    val memory = Memory()
    val shadow = Array.fill[Byte](16)(0x55)
    val kernel = CodeObjectKernel(
      "kernel",
      Target("gfx900"),
      16,
      0,
      0,
      0x80,
      0x08,
      code,
      globalMemory = Array(0x3000L -> shadow)
    )
    val prepared = VMCompiler.compile(kernel).toOption.get
    val global   = MemoryRegion(1)
    val output   = MemoryRegion(2)
    val dispatch = KernelTask.validated(
      KernelName("kernel"),
      Dimensions.One,
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(global, NonNegativeLong(8L)),
        KernelTask.Argument.Memory(output, NonNegativeLong(0L))
      )
    ).toOption.get
    for (value <- Seq(41L, 100L)) {
      assertEquals(
        memory.write(global, NonNegativeLong(8L), LittleEndian.toVec(value, ScalarByteWidth(4))),
        Right(())
      )
      assertEquals(
        VMInterpreter.execute(
          prepared,
          dispatch,
          memory,
          memory,
          TaskControl.default,
          Scheduler.Sequential,
          Vec(VMLoadedProgram.GlobalBinding(
            0x3000,
            NonNegativeInt(16),
            global,
            NonNegativeLong(8L),
            PositiveLong(32L)
          ))
        ),
        Right(())
      )
      assertEquals(LittleEndian.decode(memory.storage(global), 8, ScalarByteWidth.Dword), value + 1)
      assertEquals(LittleEndian.decode(memory.storage(output), 0, ScalarByteWidth.Dword), value + 1)
      assertEquals(LittleEndian.decode(memory.storage(output), 4, ScalarByteWidth.Dword), 1L)
      assert(shadow.forall(_ == 0x55.toByte), "cached/private ELF image must not become the bound backing")
    }
  }
  test("global ELF pointers stay canonical while native global pointers are storage aliases only") {
    val global = MemoryRegion(1)
    val output = MemoryRegion(2)
    val memory = Memory(Map(global -> GuestAddress(0x8000L)))
    val kernel = CodeObjectKernel(
      "kernel",
      Target("gfx900"),
      16,
      0,
      0,
      0x80,
      0x08,
      code,
      globalMemory = Array(0x3000L -> Array.ofDim[Byte](16))
    )
    val prepared = VMCompiler.compile(kernel).toOption.get
    val bindings = Vec(VMLoadedProgram.GlobalBinding(
      0x3000L,
      NonNegativeInt(16),
      global,
      NonNegativeLong(8L),
      PositiveLong(32L)
    ))
    assertEquals(
      memory.write(global, NonNegativeLong(8L), LittleEndian.toVec(41L, ScalarByteWidth(4))),
      Right(())
    )
    for (
      (pointer, equal, value) <- Seq(
        (KernelTask.Argument.Memory(global, NonNegativeLong(8L)), 1L, 42L),
        (KernelTask.Argument.Scalar(LittleEndian.toVec(0x8008L, ScalarByteWidth(8))), 0L, 43L)
      )
    ) {
      val dispatch = KernelTask.validated(
        KernelName("kernel"),
        Dimensions.One,
        Dimensions.One,
        Vec(pointer, KernelTask.Argument.Memory(output, NonNegativeLong(0L)))
      ).toOption.get
      assertEquals(
        VMInterpreter.execute(
          prepared,
          dispatch,
          memory,
          memory,
          TaskControl.default,
          Scheduler.Sequential,
          bindings
        ),
        Right(())
      )
      assertEquals(LittleEndian.decode(memory.storage(global), 8, ScalarByteWidth.Dword), value)
      assertEquals(LittleEndian.decode(memory.storage(output), 0, ScalarByteWidth.Dword), value)
      assertEquals(LittleEndian.decode(memory.storage(output), 4, ScalarByteWidth.Dword), equal)
    }
  }

}
