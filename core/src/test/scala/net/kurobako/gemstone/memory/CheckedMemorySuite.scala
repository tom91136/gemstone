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

  test("scalar overlays preserve endian order, bounds and dynamic ownership") {
    val delegate = ArrayCheckedMemory.allocate(16, GuestAddress(0x1000L), provenance = "scalar delegate")
    val memory   = DynamicAddressMemory(delegate, DynamicAddressMemory.State(GuestAddress(0x2000L)))
    val address  = delegate.allocation.start.offset(1L)
    val value    = 0x8877665544332211L
    assertEquals(memory.writeLittleEndian(address, value, 8), Right(()))
    assertEquals(memory.readLittleEndian(address, 8), Right(value))
    assertEquals(delegate.readInt8(address), Right(0x11.toByte))
    assert(memory.writeLittleEndian(delegate.allocation.start.offset(12L), 0L, 8).isLeft)
    assertEquals(memory.readLittleEndian(address, 8), Right(value))
    val allocated = GuestAddress(memory.allocate(8))
    assertEquals(memory.writeLittleEndian(allocated, value, 8), Right(()))
    assertEquals(memory.readLittleEndian(allocated, 8), Right(value))
    assertEquals(memory.readLittleEndian(address, 8), Right(value))
    memory.free(allocated.value)
    assert(memory.readLittleEndian(allocated, 8).isLeft)
    delegate.free()
    assert(memory.readLittleEndian(address, 8).isLeft)
  }

  test("live dynamic allocations do not split delegated machine-word transactions") {
    val backing = ArrayCheckedMemory.allocate(16, GuestAddress(0x1000L))
    var reads   = 0
    var writes  = 0
    class BulkDelegate extends AddressMemory {
      override def readInt8(address: GuestAddress)                    = backing.readInt8(address)
      override def readInt32(address: GuestAddress)                   = backing.readInt32(address)
      override def writeInt8(address: GuestAddress, value: Byte)      = backing.writeInt8(address, value)
      override def writeInt32(address: GuestAddress, value: Int)      = backing.writeInt32(address, value)
      override def readLittleEndian(address: GuestAddress, size: Int) = {
        reads += 1
        backing.readLittleEndian(address, size)
      }
      override def writeLittleEndian(address: GuestAddress, value: Long, size: Int) = {
        writes += 1
        backing.writeLittleEndian(address, value, size)
      }
    }
    val memory    = DynamicAddressMemory(BulkDelegate())
    val allocated = memory.allocate(8)
    assert(allocated != 0L)
    val value = 0x123456789abcdef0L
    assertEquals(memory.writeLittleEndian(backing.allocation.start, value, 8), Right(()))
    assertEquals(memory.readLittleEndian(backing.allocation.start, 8), Right(value))
    assertEquals(writes, 1)
    assertEquals(reads, 1)
  }

  test("word accesses crossing dynamic ownership retain per-byte routing and bounds") {
    val backing = ArrayCheckedMemory.allocate(16, GuestAddress(0x1000L))
    val memory  = DynamicAddressMemory(backing, DynamicAddressMemory.State(GuestAddress(0x1004L)))
    val address = GuestAddress(memory.allocate(4))
    val start   = backing.allocation.start.offset(2)
    val value   = 0x8877665544332211L
    assertEquals(memory.writeLittleEndian(start, value, 8), Right(()))
    assertEquals(memory.readLittleEndian(start, 8), Right(value))
    assertEquals(memory.readLittleEndian(address, 4), Right(0x66554433L))
    assertEquals(backing.readInt32(address), Right(0))
    assert(memory.writeLittleEndian(backing.allocation.start.offset(12), value, 8).isLeft)
    assertEquals(memory.readLittleEndian(start, 8), Right(value))
  }

  test("wide byte transfers return snapshots and reject bounds before changing memory") {
    val memory = ArrayCheckedMemory.allocate(16)
    val bytes  = net.kurobako.gemstone.Col.from((1 to 16).map(_.toByte))
    assertEquals(memory.writeBytes(memory.allocation.start, bytes), Right(()))
    val snapshot = memory.readBytes(memory.allocation.start, 16)
    assertEquals(snapshot, Right(bytes))
    assert(memory.writeBytes(memory.allocation.start.offset(1), net.kurobako.gemstone.Col.fill(16)(0.toByte)).isLeft)
    assertEquals(memory.readBytes(memory.allocation.start, 16), snapshot)
    assertEquals(memory.writeBytes(memory.allocation.start, net.kurobako.gemstone.Col.fill(16)(0.toByte)), Right(()))
    assertEquals(snapshot, Right(bytes))
    memory.free()
    assert(memory.readBytes(memory.allocation.start, 16).isLeft)
  }

  test("wide dynamic transfers share owned state and preserve delegated transaction boundaries") {
    val backing = ArrayCheckedMemory.allocate(32, GuestAddress(0x1000L))
    var reads   = 0
    var writes  = 0
    class BulkMemory extends AddressMemory {
      override def readInt8(address: GuestAddress)               = backing.readInt8(address)
      override def readInt32(address: GuestAddress)              = backing.readInt32(address)
      override def writeInt8(address: GuestAddress, value: Byte) = backing.writeInt8(address, value)
      override def writeInt32(address: GuestAddress, value: Int) = backing.writeInt32(address, value)
      override def readBytes(address: GuestAddress, size: Int)   = { reads += 1; backing.readBytes(address, size) }
      override def writeBytes(address: GuestAddress, bytes: net.kurobako.gemstone.Col[Byte]) = {
        writes += 1; backing.writeBytes(address, bytes)
      }
    }
    val state    = DynamicAddressMemory.State(GuestAddress(0x2000L))
    val delegate = BulkMemory()
    val first    = DynamicAddressMemory(delegate, state)
    val second   = DynamicAddressMemory(delegate, state)
    val owned    = GuestAddress(first.allocate(16))
    val bytes    = net.kurobako.gemstone.Col.from((1 to 16).map(_.toByte))
    assertEquals(first.writeBytes(owned, bytes), Right(()))
    assertEquals(second.readBytes(owned, 16), Right(bytes))
    assertEquals(writes, 0)
    assertEquals(reads, 0)
    assertEquals(first.writeBytes(backing.allocation.start, bytes), Right(()))
    assertEquals(second.readBytes(backing.allocation.start, 16), Right(bytes))
    assertEquals(writes, 1)
    assertEquals(reads, 1)
  }

  test("wide mixed overlay ranges preserve per-byte ownership and failed-write contents") {
    val backing = ArrayCheckedMemory.allocate(16, GuestAddress(0x1000L))
    val memory  = DynamicAddressMemory(backing, DynamicAddressMemory.State(GuestAddress(0x1004L)))
    val owned   = GuestAddress(memory.allocate(4))
    val bytes   = net.kurobako.gemstone.Col.from((1 to 16).map(_.toByte))
    assertEquals(memory.writeBytes(backing.allocation.start, bytes), Right(()))
    assertEquals(memory.readBytes(backing.allocation.start, 16), Right(bytes))
    assertEquals(memory.readBytes(owned, 4), Right(bytes.slice(4, 8)))
    assertEquals(backing.readBytes(owned, 4), Right(net.kurobako.gemstone.Col.fill(4)(0.toByte)))
    assert(memory.writeBytes(backing.allocation.start.offset(1), net.kurobako.gemstone.Col.fill(16)(0.toByte)).isLeft)
    assertEquals(memory.readBytes(backing.allocation.start, 16), Right(bytes))
  }

}
