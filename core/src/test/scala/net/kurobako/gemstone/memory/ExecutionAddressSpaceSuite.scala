package net.kurobako.gemstone.memory

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.execution.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

final class ExecutionAddressSpaceSuite extends munit.FunSuite {
  test("scalar address operations reach value methods once and validate the bound view") {
    val allocation = AllocationId(7)
    final class Memory extends ExecutionMemory {
      private val lock              = ReentrantLock()
      val mutableBytes              = Array.ofDim[Byte](24)
      private val mutableReadCount  = AtomicInteger()
      private val mutableWriteCount = AtomicInteger()

      def reads: Int  = mutableReadCount.get()
      def writes: Int = mutableWriteCount.get()

      def read(
          _id: AllocationId,
          _offset: Long :| Positive0,
          _size: Int :| Positive0
      ): Either[ExecutionFailure, Vec[Byte]] =
        Left(ExecutionFailure.Failed("unexpected byte collection read"))
      def write(
          _id: AllocationId,
          _offset: Long :| Positive0,
          _value: Vec[Byte]
      ): Either[ExecutionFailure, Unit] =
        Left(ExecutionFailure.Failed("unexpected byte collection write"))

      override def readValue(id: AllocationId, offset: Long :| Positive0, size: MemoryValueByteWidth) = lock.withLock {
        val _ = mutableReadCount.incrementAndGet()
        assertEquals(id, allocation)
        val index = offset.toInt.refineEither[Positive0].fold(message => fail(message), value => value)
        Right(LittleEndian.decode(mutableBytes, index, size.scalarByteWidth))
      }

      override def writeValue(
          id: AllocationId,
          offset: Long :| Positive0,
          value: Long,
          size: MemoryValueByteWidth
      ) = lock.withLock {
        val _ = mutableWriteCount.incrementAndGet()
        assertEquals(id, allocation)
        val index = offset.toInt.refineEither[Positive0].fold(message => fail(message), value => value)
        LittleEndian.write(value, mutableBytes, index, size.scalarByteWidth)
        Right(())
      }

      override def atomicModifyValue(
          id: AllocationId,
          offset: Long :| Positive0,
          size: MemoryValueByteWidth
      )(update: Long => Long): Either[ExecutionFailure, Long] = lock.withLock {
        readValue(id, offset, size) match {
          case error @ Left(_) => error
          case Right(previous) => writeValue(id, offset, update(previous), size) match {
              case Left(error) => Left(error)
              case Right(())   => Right(previous)
            }
        }
      }

      def recordPointerRelocation(_value: PointerRelocation): Unit = ()
    }
    val memory  = Memory()
    val base    = GuestAddress(0x1000L)
    val space   = ExecutionAddressSpace(memory, Vec(AddressBinding(base, allocation, 4, 12)))
    val overlay = DynamicAddressMemory(space)
    assert(overlay.allocate(DeviceAllocationByteSize(8L)).exists(_.value != 0L))
    val value = 0x8877665544332211L
    for (width <- 1 to 8) {
      val byteWidth = MemoryValueByteWidth.either(width).fold(message => fail(message), value => value)
      assertEquals(overlay.writeLittleEndian(base.offset(1), value, byteWidth), Right(()))
      val expected = if (width == 8) value else value & ((1L << (width * 8)) - 1)
      assertEquals(overlay.readLittleEndian(base.offset(1), byteWidth), Right(expected))
    }
    assertEquals(
      overlay.atomicModifyLittleEndian(base.offset(1), MemoryValueByteWidth(4))(_ + 1L),
      Right(0x44332211L)
    )
    assertEquals(overlay.readLittleEndian(base.offset(1), MemoryValueByteWidth(4)), Right(0x44332212L))
    assertEquals(memory.reads, 10)
    assertEquals(memory.writes, 9)
    assertEquals(space.readInt8(base.offset(1)), Right(0x12.toByte))
    assertEquals(space.readInt32(base.offset(1)), Right(0x44332212))
    assertEquals(space.writeInt8(base, 0x7f.toByte), Right(()))
    assertEquals(space.writeInt32(base, 0x12345678), Right(()))
    val snapshot = memory.mutableBytes.clone()
    val reads    = memory.reads
    val writes   = memory.writes
    assert(space.readLittleEndian(base.offset(5), MemoryValueByteWidth(8)).isLeft)
    assert(space.writeLittleEndian(base.offset(5), 0L, MemoryValueByteWidth(8)).isLeft)
    assertEquals(memory.reads, reads)
    assertEquals(memory.writes, writes)
    assert(memory.mutableBytes.sameElements(snapshot))
  }
}
