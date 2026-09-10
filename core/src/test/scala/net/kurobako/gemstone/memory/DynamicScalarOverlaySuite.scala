package net.kurobako.gemstone.memory

import net.kurobako.gemstone.Col

final class DynamicScalarOverlaySuite extends munit.FunSuite {
  for (offset <- Seq(2, 6)) {
    test(s"Int32 read at mixed overlay boundary $offset agrees with byte ownership") {
      val backing  = ArrayCheckedMemory.allocate(16, GuestAddress(0x1000))
      val memory   = DynamicAddressMemory(backing, DynamicAddressMemory.State(GuestAddress(0x1004)))
      val owned    = GuestAddress(memory.allocate(4, Array[Byte](1, 2, 3, 4)))
      val address  = backing.allocation.start.offset(offset)
      val expected = if (offset == 2) 0x02010000 else 0x00000403
      assertEquals(memory.readInt32(address), Right(expected))
      assertEquals(memory.readLittleEndian(address, 4), Right(expected.toLong))
      assertEquals(memory.readInt32(owned), Right(0x04030201))
    }
    test(s"Int32 write at mixed overlay boundary $offset preserves hidden backing and free semantics") {
      val backing = ArrayCheckedMemory.allocate(16, GuestAddress(0x1000))
      val memory  = DynamicAddressMemory(backing, DynamicAddressMemory.State(GuestAddress(0x1004)))
      val owned   = GuestAddress(memory.allocate(4))
      val address = backing.allocation.start.offset(offset)
      assertEquals(memory.writeInt32(address, 0x44332211), Right(()))
      assertEquals(memory.readBytes(address, 4), Right(Col[Byte](0x11, 0x22, 0x33, 0x44)))
      assertEquals(backing.readInt32(owned), Right(0))
      memory.free(owned.value)
      assertEquals(memory.readInt32(owned), Right(0))
    }
  }
  test("failed mixed Int32 write leaves both owners unchanged") {
    val backing = ArrayCheckedMemory.allocate(3, GuestAddress(0x1000))
    val memory  = DynamicAddressMemory(backing, DynamicAddressMemory.State(GuestAddress(0x1002)))
    val owned   = GuestAddress(memory.allocate(1, Array[Byte](9)))
    assert(memory.writeInt32(GuestAddress(0x1000), 0x44332211).isLeft)
    assertEquals(backing.readBytes(GuestAddress(0x1000), 3), Right(Col[Byte](0, 0, 0)))
    assertEquals(memory.readInt8(owned), Right(9.toByte))
  }
}
