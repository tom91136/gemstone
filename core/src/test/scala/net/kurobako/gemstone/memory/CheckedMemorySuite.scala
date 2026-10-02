package net.kurobako.gemstone.memory

import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.collection.immutable.ArraySeq
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class CheckedMemorySuite extends munit.FunSuite {
  private val doubleWord: Int where Between[1, 8] = 4
  private val quadWord: Int where Between[1, 8]   = 8

  private def allocate(
      memory: DynamicAddressMemory,
      size: Int > 0,
      initial: Vec[Byte] = Vec.empty
  ): GuestAddress =
    memory.allocate(size, initial).fold(error => fail(error.message), address => address)

  test("integer access is little endian and bounds checked") {
    val memory =
      ArrayCheckedMemory.allocate(8, provenance = "test allocation")
    val start = memory.allocation.start

    assertEquals(memory.writeInt32(start, 0x12345678), Right(()))
    assertEquals(memory.readInt32(start), Right(0x12345678))
    assert(memory.readInt32(start.offset(5L)).isLeft)
  }

  test("freed memory is quarantined") {
    val memory =
      ArrayCheckedMemory.allocate(4, provenance = "freed allocation")
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
      provenance = "delegate"
    )
    val memory = DynamicAddressMemory(delegate, GuestAddress(0x2000L))
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
      provenance = "scalar delegate"
    )
    val memory  = DynamicAddressMemory(delegate, GuestAddress(0x2000L))
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
    class BulkDelegate extends AddressMemory {
      override def readInt8(address: GuestAddress): Either[MemoryError, Byte]               = backing.readInt8(address)
      override def readInt32(address: GuestAddress): Either[MemoryError, Int]               = backing.readInt32(address)
      override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
        backing.writeInt8(address, value)
      override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
        backing.writeInt32(address, value)
      override def readLittleEndian(address: GuestAddress, size: Int where Between[1, 8]): Either[MemoryError, Long] = {
        val _ = readCalls.incrementAndGet()
        backing.readLittleEndian(address, size)
      }
      override def writeLittleEndian(
          address: GuestAddress,
          value: Long,
          size: Int where Between[1, 8]
      ): Either[MemoryError, Unit] = {
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

  test("system-scope accesses delegate external ranges and keep dynamic allocations local") {
    val backing = ArrayCheckedMemory.allocate(16, GuestAddress(0x1000L))
    class ScopedDelegate extends AddressMemory {
      val prepared: ArrayBuffer[(GuestAddress.Type, Int)] = mutable.ArrayBuffer.empty[(GuestAddress, Int)]
      val reads: ArrayBuffer[(GuestAddress.Type, Int)]    = mutable.ArrayBuffer.empty[(GuestAddress, Int)]
      val batches: ArrayBuffer[ArraySeq[(GuestAddress.Type, Long, Int where AtLeast[1] & AtMost[8])]] =
        mutable.ArrayBuffer.empty[Vec[(GuestAddress, Long, Int where Between[1, 8])]]
      override def readInt8(address: GuestAddress): Either[MemoryError, Byte]               = backing.readInt8(address)
      override def readInt32(address: GuestAddress): Either[MemoryError, Int]               = backing.readInt32(address)
      override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
        backing.writeInt8(address, value)
      override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
        backing.writeInt32(address, value)
      override def prepareSystemReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[MemoryError, Unit] = {
        prepared ++= ranges.map((address, size) => address -> size.raw)
        Right(())
      }
      override def readSystemLittleEndian(
          address: GuestAddress,
          size: Int where Between[1, 8]
      ): Either[MemoryError, Long] = {
        reads += address -> size.raw
        backing.readLittleEndian(address, size)
      }
      override def writeSystemBatch(
          writes: Vec[(GuestAddress, Long, Int where Between[1, 8])]
      ): Either[MemoryError, Unit] = {
        batches += writes
        writes.foldLeft[Either[MemoryError, Unit]](Right(())) { case (result, (address, value, size)) =>
          result.flatMap(_ => backing.writeLittleEndian(address, value, size))
        }
      }
    }
    val delegate = ScopedDelegate()
    val memory   = DynamicAddressMemory(delegate, GuestAddress(0x2000L))
    val external = backing.allocation.start
    val local    = allocate(memory, 4, Vec[Byte](1, 2, 3, 4))
    assertEquals(memory.prepareSystemReads(Vec((external, 4), (local, 4))), Right(()))
    assertEquals(delegate.prepared.toVector, Vector(external -> 4))
    assertEquals(memory.readSystemLittleEndian(external, doubleWord), Right(0L))
    assertEquals(memory.readSystemLittleEndian(local, doubleWord), Right(0x04030201L))
    assertEquals(delegate.reads.toVector, Vector(external -> 4))
    val stores = Vec((external, 0x55667788L, doubleWord), (local, 0x12345678L, doubleWord))
    assertEquals(memory.writeSystemBatch(stores), Right(()))
    assertEquals(delegate.batches.toVector, Vector(Vec((external, 0x55667788L, doubleWord))))
    assertEquals(memory.readSystemLittleEndian(local, doubleWord), Right(0x12345678L))
    assertEquals(backing.readLittleEndian(external, doubleWord), Right(0x55667788L))
  }

  test("system-scope word accesses preserve byte ownership across an overlay boundary") {
    val backing = ArrayCheckedMemory.allocate(16, GuestAddress(0x1000L))
    val memory  = DynamicAddressMemory(backing, GuestAddress(0x1004L))
    val local   = allocate(memory, 4, Vec.fill(4)(0.toByte))
    val address = backing.allocation.start.offset(2)
    val value   = 0x8877665544332211L
    assertEquals(
      memory.prepareSystemReads(Vec((address, quadWord.nonNegative))),
      Right(())
    )
    assertEquals(memory.writeSystemLittleEndian(address, value, quadWord), Right(()))
    assertEquals(memory.readSystemLittleEndian(address, quadWord), Right(value))
    assertEquals(memory.readSystemLittleEndian(local, doubleWord), Right(0x66554433L))
    assertEquals(
      backing.readBytes(address, quadWord.nonNegative),
      Right(Vec[Byte](0x11, 0x22, 0, 0, 0, 0, 0x77.toByte, 0x88.toByte))
    )
  }

  test("word accesses crossing dynamic ownership retain per-byte routing and bounds") {
    val backing = ArrayCheckedMemory.allocate(16, GuestAddress(0x1000L))
    val memory  = DynamicAddressMemory(backing, GuestAddress(0x1004L))
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

  test("wide dynamic transfers preserve owned storage and delegated transaction boundaries") {
    val backing    = ArrayCheckedMemory.allocate(32, GuestAddress(0x1000L))
    val readCalls  = AtomicInteger()
    val writeCalls = AtomicInteger()
    class BulkMemory extends AddressMemory {
      override def readInt8(address: GuestAddress): Either[MemoryError, Byte]               = backing.readInt8(address)
      override def readInt32(address: GuestAddress): Either[MemoryError, Int]               = backing.readInt32(address)
      override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
        backing.writeInt8(address, value)
      override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
        backing.writeInt32(address, value)
      override def readBytes(address: GuestAddress, size: Int >= 0): Either[MemoryError, Vec[Byte]] = {
        val _ = readCalls.incrementAndGet()
        backing.readBytes(address, size)
      }
      override def writeBytes(address: GuestAddress, bytes: Vec[Byte]): Either[MemoryError, Unit] = {
        val _ = writeCalls.incrementAndGet()
        backing.writeBytes(address, bytes)
      }
    }
    val delegate = BulkMemory()
    val memory   = DynamicAddressMemory(delegate, GuestAddress(0x2000L))
    val owned    = allocate(memory, 16)
    val bytes    = Vec.from((1 to 16).map(_.toByte))
    assertEquals(memory.writeBytes(owned, bytes), Right(()))
    assertEquals(memory.readBytes(owned, 16), Right(bytes))
    assertEquals(writeCalls.get(), 0)
    assertEquals(readCalls.get(), 0)
    assertEquals(memory.writeBytes(backing.allocation.start, bytes), Right(()))
    assertEquals(memory.readBytes(backing.allocation.start, 16), Right(bytes))
    assertEquals(writeCalls.get(), 1)
    assertEquals(readCalls.get(), 1)
  }

  test("wide mixed overlay ranges preserve per-byte ownership and failed-write contents") {
    val backing = ArrayCheckedMemory.allocate(16, GuestAddress(0x1000L))
    val memory  = DynamicAddressMemory(backing, GuestAddress(0x1004L))
    val owned   = allocate(memory, 4)
    val bytes   = Vec.from((1 to 16).map(_.toByte))
    assertEquals(memory.writeBytes(backing.allocation.start, bytes), Right(()))
    assertEquals(memory.readBytes(backing.allocation.start, 16), Right(bytes))
    assertEquals(memory.readBytes(owned, 4), Right(bytes.slice(4, 8)))
    assertEquals(backing.readBytes(owned, 4), Right(Vec.fill(4)(0.toByte)))
    assert(memory.writeBytes(backing.allocation.start.offset(1), Vec.fill(16)(0.toByte)).isLeft)
    assertEquals(memory.readBytes(backing.allocation.start, 16), Right(bytes))
  }

  for (offset <- Seq(2, 6)) {
    test(s"Int32 read at mixed overlay boundary $offset agrees with byte ownership") {
      val backing  = ArrayCheckedMemory.allocate(16, GuestAddress(0x1000L))
      val memory   = DynamicAddressMemory(backing, GuestAddress(0x1004L))
      val owned    = allocate(memory, 4, Vec[Byte](1, 2, 3, 4))
      val address  = backing.allocation.start.offset(offset)
      val expected = if (offset == 2) 0x02010000 else 0x00000403
      assertEquals(memory.readInt32(address), Right(expected))
      assertEquals(memory.readLittleEndian(address, doubleWord), Right(expected.toLong))
      assertEquals(memory.readInt32(owned), Right(0x04030201))
    }
    test(s"Int32 write at mixed overlay boundary $offset preserves hidden backing and free semantics") {
      val backing = ArrayCheckedMemory.allocate(16, GuestAddress(0x1000L))
      val memory  = DynamicAddressMemory(backing, GuestAddress(0x1004L))
      val owned   = allocate(memory, 4)
      val address = backing.allocation.start.offset(offset)
      assertEquals(memory.writeInt32(address, 0x44332211), Right(()))
      assertEquals(memory.readBytes(address, 4), Right(Vec[Byte](0x11, 0x22, 0x33, 0x44)))
      assertEquals(backing.readInt32(owned), Right(0))
      assert(memory.free(owned))
      assertEquals(memory.readInt32(owned), Right(0))
    }
  }
  test("failed mixed Int32 write leaves both owners unchanged") {
    val backing = ArrayCheckedMemory.allocate(3, GuestAddress(0x1000L))
    val memory  = DynamicAddressMemory(backing, GuestAddress(0x1002L))
    val owned   = allocate(memory, 1, Vec(9.toByte))
    assert(memory.writeInt32(GuestAddress(0x1000L), 0x44332211).isLeft)
    assertEquals(backing.readBytes(GuestAddress(0x1000L), 3), Right(Vec[Byte](0, 0, 0)))
    assertEquals(memory.readInt8(owned), Right(9.toByte))
  }
}
