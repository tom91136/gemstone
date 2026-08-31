package net.kurobako.gemstone.memory

class CheckedMemorySuite extends munit.FunSuite {
  test("integer access is little endian and bounds checked") {
    val memory = ArrayCheckedMemory.allocate(8, provenance = "test allocation")
    val start  = memory.allocation.start

    assertEquals(memory.writeInt32(start, 0x12345678), Right(()))
    assertEquals(memory.readInt32(start), Right(0x12345678))
    assert(memory.readInt32(start.offset(5L)).isLeft)
  }

  test("freed memory is quarantined") {
    val memory = ArrayCheckedMemory.allocate(4, provenance = "freed allocation")
    val start  = memory.allocation.start
    memory.free()

    assert(memory.readInt32(start).left.exists(_.isInstanceOf[MemoryError.UseAfterFree]))
  }

  test("dynamic memory resolves ordered allocations before delegating") {
    val delegate = ArrayCheckedMemory.allocate(4, GuestAddress(0x1000L), provenance = "delegate")
    val memory   = DynamicAddressMemory(delegate, DynamicAddressMemory.State(GuestAddress(0x2000L)))
    val first    = memory.allocate(4, Array[Byte](1, 2, 3, 4))
    val second   = memory.allocate(4, Array[Byte](5, 6, 7, 8))

    assertEquals(memory.readInt32(GuestAddress(first)), Right(0x04030201))
    assertEquals(memory.readInt32(GuestAddress(second)), Right(0x08070605))
    assertEquals(memory.writeInt32(delegate.allocation.start, 0x12345678), Right(()))
    assertEquals(delegate.readInt32(delegate.allocation.start), Right(0x12345678))

    memory.free(first)
    assert(memory.readInt8(GuestAddress(first)).isLeft)
  }
}
