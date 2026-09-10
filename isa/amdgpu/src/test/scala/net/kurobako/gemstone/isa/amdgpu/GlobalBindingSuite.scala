package net.kurobako.gemstone.isa.amdgpu
import net.kurobako.gemstone.Col
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.binary.LittleEndian
final class GlobalBindingSuite extends munit.FunSuite {
  private val code = Col[Long](
    0xc00a0100L, 0x00000000L, 0xbf8cc07fL, 0xbe8800ffL, 0x00003000L, 0xbe890080L, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x800a810aL, 0x7e000280L, 0x7e02020aL, 0xdc708000L, 0x00040100L, 0xbf8c0f70L, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x7e02020aL, 0xdc708000L, 0x00060100L, 0x880a0804L, 0x880b0905L, 0x870a0b0aL, 0xbf06800aL, 0x850a8081L, 0x7e02020aL,
    0x7e000284L, 0xdc708000L, 0x00060100L, 0xbf810000L
  ).flatMap(value => LittleEndian.toCol(value, 4))
  private class Memory(override val allocationAddresses: Col[AllocationAddress] = Col.empty) extends ExecutionMemory {
    val storage = Map(1L -> Array.ofDim[Byte](32), 2L -> Array.ofDim[Byte](16))
    override def read(id: AllocationId, offset: Long, size: Int) =
      Right(Col.from(storage(id.toLong).slice(offset.toInt, offset.toInt + size)))
    override def write(id: AllocationId, offset: Long, bytes: Col[Byte]) = {
      val _ = bytes.copyToArray(storage(id.toLong), offset.toInt, bytes.size); Right(())
    }
    override def recordPointerRelocation(relocation: PointerRelocation): Unit = ()
  }
  test("bound ELF global and kernel allocation pointer use one backing across dispatches") {
    val memory = Memory()
    val shadow = Array.fill[Byte](16)(0x55)
    val kernel = CodeObjectKernel(
      "kernel",
      Target.unsafe("gfx900"),
      16,
      0,
      0,
      0x80,
      0x08,
      code,
      globalMemory = Array(0x3000L -> shadow)
    )
    val prepared = DispatchInterpreter.prepare(kernel).toOption.get
    val global   = AllocationId.fromLong(1).toOption.get
    val output   = AllocationId.fromLong(2).toOption.get
    val dispatch = KernelDispatch.create(
      ProgramId.fromLong(1).toOption.get,
      "kernel",
      Dimensions.One,
      Dimensions.One,
      Col(KernelArgument.Allocation(global, 8), KernelArgument.Allocation(output, 0))
    ).toOption.get
    for (value <- Seq(41L, 100L)) {
      assertEquals(memory.write(global, 8, LittleEndian.toCol(value, 4)), Right(()))
      assertEquals(
        DispatchInterpreter.execute(
          prepared,
          dispatch,
          memory,
          ExecutionControl.default,
          Col(DispatchInterpreter.GlobalBinding(0x3000, 16, global, 8, 32))
        ),
        Right(())
      )
      assertEquals(LittleEndian.decode(memory.storage(1L), 8, 4), value + 1)
      assertEquals(LittleEndian.decode(memory.storage(2L), 0, 4), value + 1)
      assertEquals(LittleEndian.decode(memory.storage(2L), 4, 4), 1L)
      assert(shadow.forall(_ == 0x55.toByte), "cached/private ELF image must not become the bound backing")
    }
  }
  test("global ELF pointers stay canonical while native global pointers are storage aliases only") {
    val global = AllocationId.fromLong(1).toOption.get
    val output = AllocationId.fromLong(2).toOption.get
    val memory = Memory(Col(AllocationAddress(global, 32, Some(0x8000L)), AllocationAddress(output, 16, None)))
    val kernel = CodeObjectKernel(
      "kernel",
      Target.unsafe("gfx900"),
      16,
      0,
      0,
      0x80,
      0x08,
      code,
      globalMemory = Array(0x3000L -> Array.ofDim[Byte](16))
    )
    val prepared = DispatchInterpreter.prepare(kernel).toOption.get
    val bindings = Col(DispatchInterpreter.GlobalBinding(0x3000L, 16, global, 8, 32))
    assertEquals(memory.write(global, 8, LittleEndian.toCol(41L, 4)), Right(()))
    for (
      (pointer, equal, value) <- Seq(
        (KernelArgument.Allocation(global, 8), 1L, 42L),
        (KernelArgument.Scalar(LittleEndian.toCol(0x8008L, 8)), 0L, 43L)
      )
    ) {
      val dispatch = KernelDispatch.create(
        ProgramId.fromLong(1).toOption.get,
        "kernel",
        Dimensions.One,
        Dimensions.One,
        Col(pointer, KernelArgument.Allocation(output, 0))
      ).toOption.get
      assertEquals(
        DispatchInterpreter.execute(prepared, dispatch, memory, ExecutionControl.default, bindings),
        Right(())
      )
      assertEquals(LittleEndian.decode(memory.storage(1L), 8, 4), value)
      assertEquals(LittleEndian.decode(memory.storage(2L), 0, 4), value)
      assertEquals(LittleEndian.decode(memory.storage(2L), 4, 4), equal)
    }
  }

}
