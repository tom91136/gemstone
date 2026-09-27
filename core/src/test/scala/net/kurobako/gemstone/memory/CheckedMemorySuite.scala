package net.kurobako.gemstone.memory

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Constraint.NonNegative

import net.kurobako.gemstone.Constraint.Positive
import net.kurobako.gemstone.Vec

import java.util.concurrent.atomic.AtomicInteger

final class CheckedMemorySuite extends munit.FunSuite {
  private val doubleWord: IntBetween[1, 8] = 4
  private val quadWord: IntBetween[1, 8]   = 8

  private def allocate(
      memory: DynamicAddressMemory,
      size: Positive[Int],
      initial: Vec[Byte] = Vec.empty
  ): GuestAddress =
    memory.allocate(size, initial).fold(error => fail(error.message), address => address)

  test("integer access is little endian and bounds checked") {
    val memory =
      ArrayCheckedMemory.allocate(8, provenance = NonEmptyString("test allocation"))
    val start = memory.allocation.start

    assertEquals(memory.writeInt32(start, 0x12345678), Right(()))
    assertEquals(memory.readInt32(start), Right(0x12345678))
    assert(memory.readInt32(start.offset(5L)).isLeft)
  }

  test("freed memory is quarantined") {
    val memory =
      ArrayCheckedMemory.allocate(4, provenance = NonEmptyString("freed allocation"))
    val start = memory.allocation.start
    memory.free()

    assert(memory.readInt32(start).left.exists {
      case MemoryError.UseAfterFree(_, _) => true
      case _                              => false
    })
  }

  test("dynamic memory resolves ordered allocations before delegating") {
    val delegate = ArrayCheckedMemory.allocate(
      4,
      GuestAddress(0x1000L),
      provenance = NonEmptyString("delegate")
    )
    val memory = DynamicAddressMemory(delegate, DynamicAddressMemory.State(GuestAddress(0x2000L)))
    val first  = allocate(memory, 4, Vec[Byte](1, 2, 3, 4))
    val second = allocate(memory, 4, Vec[Byte](5, 6, 7, 8))

    assertEquals(memory.readInt32(first), Right(0x04030201))
    assertEquals(memory.readInt32(second), Right(0x08070605))
    assertEquals(memory.writeInt32(delegate.allocation.start, 0x12345678), Right(()))
    assertEquals(delegate.readInt32(delegate.allocation.start), Right(0x12345678))

    assert(memory.free(first))
    assert(memory.readInt8(first).isLeft)
  }

  test("scalar overlays preserve endian order, bounds and dynamic ownership") {
    val delegate = ArrayCheckedMemory.allocate(
      16,
      GuestAddress(0x1000L),
      provenance = NonEmptyString("scalar delegate")
    )
    val memory  = DynamicAddressMemory(delegate, DynamicAddressMemory.State(GuestAddress(0x2000L)))
    val address = delegate.allocation.start.offset(1L)
    val value   = 0x8877665544332211L
    assertEquals(memory.writeLittleEndian(address, value, quadWord), Right(()))
    assertEquals(memory.readLittleEndian(address, quadWord), Right(value))
    assertEquals(delegate.readInt8(address), Right(0x11.toByte))
    assert(memory.writeLittleEndian(delegate.allocation.start.offset(12L), 0L, quadWord).isLeft)
    assertEquals(memory.readLittleEndian(address, quadWord), Right(value))
    val allocated = allocate(memory, 8)
    assertEquals(memory.writeLittleEndian(allocated, value, quadWord), Right(()))
    assertEquals(memory.readLittleEndian(allocated, quadWord), Right(value))
    assertEquals(memory.readLittleEndian(address, quadWord), Right(value))
    assert(memory.free(allocated))
    assert(memory.readLittleEndian(allocated, quadWord).isLeft)
    delegate.free()
    assert(memory.readLittleEndian(address, quadWord).isLeft)
  }

  test("live dynamic allocations do not split delegated machine-word transactions") {
    val backing    = ArrayCheckedMemory.allocate(16, GuestAddress(0x1000L))
    val readCalls  = AtomicInteger()
    val writeCalls = AtomicInteger()
    final class BulkDelegate extends AddressMemory {
      override def readInt8(address: GuestAddress)                                 = backing.readInt8(address)
      override def readInt32(address: GuestAddress)                                = backing.readInt32(address)
      override def writeInt8(address: GuestAddress, value: Byte)                   = backing.writeInt8(address, value)
      override def writeInt32(address: GuestAddress, value: Int)                   = backing.writeInt32(address, value)
      override def readLittleEndian(address: GuestAddress, size: IntBetween[1, 8]) = {
        val _ = readCalls.incrementAndGet()
        backing.readLittleEndian(address, size)
      }
      override def writeLittleEndian(address: GuestAddress, value: Long, size: IntBetween[1, 8]) = {
        val _ = writeCalls.incrementAndGet()
        backing.writeLittleEndian(address, value, size)
      }
    }
    val memory    = DynamicAddressMemory(BulkDelegate())
    val allocated = allocate(memory, 8)
    assert(allocated.raw != 0L)
    val value = 0x123456789abcdef0L
    assertEquals(memory.writeLittleEndian(backing.allocation.start, value, quadWord), Right(()))
    assertEquals(memory.readLittleEndian(backing.allocation.start, quadWord), Right(value))
    assertEquals(writeCalls.get(), 1)
    assertEquals(readCalls.get(), 1)
  }

  test("word accesses crossing dynamic ownership retain per-byte routing and bounds") {
    val backing = ArrayCheckedMemory.allocate(16, GuestAddress(0x1000L))
    val memory  = DynamicAddressMemory(backing, DynamicAddressMemory.State(GuestAddress(0x1004L)))
    val address = allocate(memory, 4)
    val start   = backing.allocation.start.offset(2)
    val value   = 0x8877665544332211L
    assertEquals(memory.writeLittleEndian(start, value, quadWord), Right(()))
    assertEquals(memory.readLittleEndian(start, quadWord), Right(value))
    assertEquals(memory.readLittleEndian(address, doubleWord), Right(0x66554433L))
    assertEquals(backing.readInt32(address), Right(0))
    assert(memory.writeLittleEndian(backing.allocation.start.offset(12), value, quadWord).isLeft)
    assertEquals(memory.readLittleEndian(start, quadWord), Right(value))
  }

  test("wide byte transfers return snapshots and reject bounds before changing memory") {
    val memory = ArrayCheckedMemory.allocate(16)
    val bytes  = Vec.from((1 to 16).map(_.toByte))
    assertEquals(memory.writeBytes(memory.allocation.start, bytes), Right(()))
    val snapshot = memory.readBytes(memory.allocation.start, 16)
    assertEquals(snapshot, Right(bytes))
    assert(memory.writeBytes(memory.allocation.start.offset(1), Vec.fill(16)(0.toByte)).isLeft)
    assertEquals(memory.readBytes(memory.allocation.start, 16), snapshot)
    assertEquals(memory.writeBytes(memory.allocation.start, Vec.fill(16)(0.toByte)), Right(()))
    assertEquals(snapshot, Right(bytes))
    memory.free()
    assert(memory.readBytes(memory.allocation.start, 16).isLeft)
  }

  test("wide dynamic transfers share owned state and preserve delegated transaction boundaries") {
    val backing    = ArrayCheckedMemory.allocate(32, GuestAddress(0x1000L))
    val readCalls  = AtomicInteger()
    val writeCalls = AtomicInteger()
    final class BulkMemory extends AddressMemory {
      override def readInt8(address: GuestAddress)                          = backing.readInt8(address)
      override def readInt32(address: GuestAddress)                         = backing.readInt32(address)
      override def writeInt8(address: GuestAddress, value: Byte)            = backing.writeInt8(address, value)
      override def writeInt32(address: GuestAddress, value: Int)            = backing.writeInt32(address, value)
      override def readBytes(address: GuestAddress, size: NonNegative[Int]) = {
        val _ = readCalls.incrementAndGet()
        backing.readBytes(address, size)
      }
      override def writeBytes(address: GuestAddress, bytes: Vec[Byte]) = {
        val _ = writeCalls.incrementAndGet()
        backing.writeBytes(address, bytes)
      }
    }
    val state    = DynamicAddressMemory.State(GuestAddress(0x2000L))
    val delegate = BulkMemory()
    val first    = DynamicAddressMemory(delegate, state)
    val second   = DynamicAddressMemory(delegate, state)
    val owned    = allocate(first, 16)
    val bytes    = Vec.from((1 to 16).map(_.toByte))
    assertEquals(first.writeBytes(owned, bytes), Right(()))
    assertEquals(second.readBytes(owned, 16), Right(bytes))
    assertEquals(writeCalls.get(), 0)
    assertEquals(readCalls.get(), 0)
    assertEquals(first.writeBytes(backing.allocation.start, bytes), Right(()))
    assertEquals(second.readBytes(backing.allocation.start, 16), Right(bytes))
    assertEquals(writeCalls.get(), 1)
    assertEquals(readCalls.get(), 1)
  }

  test("wide mixed overlay ranges preserve per-byte ownership and failed-write contents") {
    val backing = ArrayCheckedMemory.allocate(16, GuestAddress(0x1000L))
    val memory  = DynamicAddressMemory(backing, DynamicAddressMemory.State(GuestAddress(0x1004L)))
    val owned   = allocate(memory, 4)
    val bytes   = Vec.from((1 to 16).map(_.toByte))
    assertEquals(memory.writeBytes(backing.allocation.start, bytes), Right(()))
    assertEquals(memory.readBytes(backing.allocation.start, 16), Right(bytes))
    assertEquals(memory.readBytes(owned, 4), Right(bytes.slice(4, 8)))
    assertEquals(backing.readBytes(owned, 4), Right(Vec.fill(4)(0.toByte)))
    assert(memory.writeBytes(backing.allocation.start.offset(1), Vec.fill(16)(0.toByte)).isLeft)
    assertEquals(memory.readBytes(backing.allocation.start, 16), Right(bytes))
  }

}
