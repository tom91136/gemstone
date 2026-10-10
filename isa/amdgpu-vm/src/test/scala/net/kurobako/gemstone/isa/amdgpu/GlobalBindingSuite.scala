package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
class GlobalBindingSuite extends munit.FunSuite {
  private def value(routing: VMDispatchMemory, address: Long, size: Int in 1 ~ 8): Either[Fault, Long] =
    AddressSpace.scalar(routing.readValueInto(address, size, _))

  private def mapping(region: AllocationId): VMDispatchMemory.GlobalMapping =
    VMDispatchMemory.GlobalMapping(
      GuestAddress(0x3000L),
      16,
      GlobalBinding.validated("global", region, 8L, 16L, 32L).value
    )

  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  private val code = words(
    0xc00a0100, 0x00000000, 0xbf8cc07f, 0xbe8800ff, 0x00003000, 0xbe890080, 0xc0020284, 0x00000000, 0xbf8cc07f,
    0x800a810a, 0x7e000280, 0x7e02020a, 0xdc708000, 0x00040100, 0xbf8c0f70, 0xc0020284, 0x00000000, 0xbf8cc07f,
    0x7e02020a, 0xdc708000, 0x00060100, 0x880a0804, 0x880b0905, 0x870a0b0a, 0xbf06800a, 0x850a8081, 0x7e02020a,
    0x7e000284, 0xdc708000, 0x00060100, 0xbf810000
  )
  private def backing(nativeAddresses: Map[AllocationId, GuestAddress] = Map.empty): RegionMemory = {
    val memory = RegionMemory()
    for ((id, size) <- Seq(AllocationId(1) -> 32, AllocationId(2) -> 16))
      nativeAddresses.get(id).fold(memory.allocate(id, size))(memory.allocate(id, size, _))
    memory
  }
  test("bound ELF global and kernel allocation pointer use one backing across dispatches") {
    val memory = backing()
    val shadow = Array.fill[Byte](16)(0x55)
    val kernel = codeKernel(
      kernargSize = 16,
      computePgmRsrc2 = 0x80,
      kernelCodeProperties = 0x08,
      code = code,
      globalMemory = Array(0x3000L -> shadow)
    )
    val prepared = compileKernel(kernel)
    val global   = AllocationId(1)
    val output   = AllocationId(2)
    val dispatch = singleTask(KernelTask.Argument.Memory(global, 8L), KernelTask.Argument.Memory(output, 0L))
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
          Vec(mapping(global)),
          _ => Right(())
        ),
        Right(())
      )
      assertEquals(LittleEndian.decode(memory.array(global), 8, 4), value + 1)
      assertEquals(LittleEndian.decode(memory.array(output), 0, 4), value + 1)
      assertEquals(LittleEndian.decode(memory.array(output), 4, 4), 1L)
      assert(shadow.forall(_ == 0x55.toByte), "cached/private ELF image must not become the bound backing")
    }
  }
  test("global ELF pointers stay canonical while native global pointers are storage aliases only") {
    val global = AllocationId(1)
    val output = AllocationId(2)
    val memory = backing(Map(global -> GuestAddress(0x8000L)))
    val kernel = codeKernel(
      kernargSize = 16,
      computePgmRsrc2 = 0x80,
      kernelCodeProperties = 0x08,
      code = code,
      globalMemory = Array(0x3000L -> Array.ofDim[Byte](16))
    )
    val prepared = compileKernel(kernel)
    val bindings = Vec(mapping(global))
    assertEquals(
      memory.write(global, 8L, LittleEndian.toVec(41L, 4)),
      Right(())
    )
    for (
      (pointer, equal, value) <- Seq(
        (KernelTask.Argument.Memory(global, 8L), 1L, 42L),
        (KernelTask.Argument.Scalar(LittleEndian.toVec(0x8008L, 8).unsafe), 0L, 43L)
      )
    ) {
      val dispatch = singleTask(pointer, KernelTask.Argument.Memory(output, 0L))
      assertEquals(
        VMInterpreter.execute(
          prepared,
          dispatch,
          memory,
          TaskControl(),
          Workgroups.Sequential,
          bindings,
          _ => Right(())
        ),
        Right(())
      )
      assertEquals(LittleEndian.decode(memory.array(global), 8, 4), value)
      assertEquals(LittleEndian.decode(memory.array(output), 0, 4), value)
      assertEquals(LittleEndian.decode(memory.array(output), 4, 4), equal)
    }
  }

  test("dispatch routing preserves image boundaries, alias precedence and external allocations") {
    val memory  = backing()
    val first   = Array.fill[Byte](16)(0x11)
    val shadow  = Array.fill[Byte](16)(0x55)
    val last    = Array.fill[Byte](8)(0x22)
    val kernel  = codeKernel(globalMemory = Array(0x3000L -> shadow, 0x2000L -> first, (Long.MaxValue - 3L) -> last))
    val global  = AllocationId(1)
    val routing = VMDispatchMemory(
      kernel = kernel,
      kernarg = Array.emptyByteArray,
      dispatch = Array.emptyByteArray,
      allocations = Array(mapping(global)),
      memory = memory,
      kernargAddress = VMDispatchMemory.KernargAddress
    )
    assertEquals(value(routing, 0x2000L, 1), Right(0x11L))
    assertEquals(value(routing, 0x200fL, 1), Right(0x11L))
    assert(value(routing, 0x200fL, 2).isLeft)
    assert(routing.isExternal(0x1fffL, 1))
    assert(routing.isExternal(0x2010L, 1))
    assert(routing.isExternal(0x2fffL, 1))
    assert(routing.isExternal(0x3010L, 1))
    assertEquals(routing.readBytes(0x2010L, 0), Right(Vec.empty[Byte]))
    assertEquals(value(routing, Long.MaxValue, 1), Right(0x22L))
    assertEquals(routing.writeValue(address = 0x3000L, value = 41L, 4), Right(()))
    assertEquals(routing.atomicModifyValue(0x2000L, 4)(_ + 1L), Right(0x11111111L))
    assertEquals(value(routing, 0x2000L, 1), Right(0x12L))
    assertEquals(routing.writeValue(address = 0x3000L, value = 42L, 4), Right(()))
    assertEquals(value(routing, 0x3000L, 4), Right(42L))
    assertEquals(LittleEndian.decode(memory.array(global), 8, 4), 42L)
    assert(shadow.forall(_ == 0x55.toByte))
    val external = memory.address(global, 8L).value.raw
    assert(routing.isExternal(external, 4))
    assertEquals(value(routing, external, 4), Right(42L))
    assertEquals(routing.writeBytes(external, Array[Byte](7, 8)), Right(()))
    assertEquals(routing.readBytes(0x3000L, 2), Right(Vec[Byte](7, 8)))
  }

}
