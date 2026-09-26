package net.kurobako.gemstone.memory

import net.kurobako.gemstone.{NonNegativeInt, Vec}

final class DynamicScalarOverlaySuite extends munit.FunSuite {
  private val doubleWord = MemoryValueByteWidth.Dword

  private def allocate(
      memory: DynamicAddressMemory,
      size: DeviceAllocationByteSize,
      initial: Vec[Byte] = Vec.empty
  ): GuestAddress =
    memory.allocate(size, initial).fold(error => fail(error.message), address => address)

  for (offset <- Seq(2, 6)) {
    test(s"Int32 read at mixed overlay boundary $offset agrees with byte ownership") {
      val backing  = ArrayCheckedMemory.allocate(NonNegativeInt(16), GuestAddress(0x1000L))
      val memory   = DynamicAddressMemory(backing, DynamicAddressMemory.State(GuestAddress(0x1004L)))
      val owned    = allocate(memory, DeviceAllocationByteSize(4), Vec[Byte](1, 2, 3, 4))
      val address  = backing.allocation.start.offset(offset)
      val expected = if (offset == 2) 0x02010000 else 0x00000403
      assertEquals(memory.readInt32(address), Right(expected))
      assertEquals(memory.readLittleEndian(address, doubleWord), Right(expected.toLong))
      assertEquals(memory.readInt32(owned), Right(0x04030201))
    }
    test(s"Int32 write at mixed overlay boundary $offset preserves hidden backing and free semantics") {
      val backing = ArrayCheckedMemory.allocate(NonNegativeInt(16), GuestAddress(0x1000L))
      val memory  = DynamicAddressMemory(backing, DynamicAddressMemory.State(GuestAddress(0x1004L)))
      val owned   = allocate(memory, DeviceAllocationByteSize(4))
      val address = backing.allocation.start.offset(offset)
      assertEquals(memory.writeInt32(address, 0x44332211), Right(()))
      assertEquals(memory.readBytes(address, NonNegativeInt(4)), Right(Vec[Byte](0x11, 0x22, 0x33, 0x44)))
      assertEquals(backing.readInt32(owned), Right(0))
      assert(memory.free(owned))
      assertEquals(memory.readInt32(owned), Right(0))
    }
  }
  test("failed mixed Int32 write leaves both owners unchanged") {
    val backing = ArrayCheckedMemory.allocate(NonNegativeInt(3), GuestAddress(0x1000L))
    val memory  = DynamicAddressMemory(backing, DynamicAddressMemory.State(GuestAddress(0x1002L)))
    val owned   = allocate(memory, DeviceAllocationByteSize(1), Vec(9.toByte))
    assert(memory.writeInt32(GuestAddress(0x1000L), 0x44332211).isLeft)
    assertEquals(backing.readBytes(GuestAddress(0x1000L), NonNegativeInt(3)), Right(Vec[Byte](0, 0, 0)))
    assertEquals(memory.readInt8(owned), Right(9.toByte))
  }
}
