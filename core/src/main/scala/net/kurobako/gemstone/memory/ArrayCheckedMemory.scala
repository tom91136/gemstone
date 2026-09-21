package net.kurobako.gemstone.memory

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.concurrent.withLock

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.locks.ReentrantLock

final class ArrayCheckedMemory private (val allocation: Allocation, mutableBytes: Array[Byte]) extends AddressMemory {
  private val lock = ReentrantLock()

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var live = true

  override def readInt8(address: GuestAddress): Either[MemoryError, Byte] = lock.withLock {
    validate(address, 1) match {
      case Left(error)   => Left(error)
      case Right(offset) => Right(mutableBytes(offset))
    }
  }

  override def readInt32(address: GuestAddress): Either[MemoryError, Int] = lock.withLock {
    validate(address, 4) match {
      case Left(error)   => Left(error)
      case Right(offset) => Right(LittleEndian.decodeInt32(mutableBytes, offset))
    }
  }

  override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] = lock.withLock {
    validate(address, 4) match {
      case Left(error)   => Left(error)
      case Right(offset) =>
        LittleEndian.write(value.toLong, mutableBytes, offset, ScalarByteWidth(4))
        Right(())
    }
  }

  override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] = lock.withLock {
    validate(address, 1) match {
      case Left(error)   => Left(error)
      case Right(offset) =>
        mutableBytes(offset) = value
        Right(())
    }
  }

  override def readBytes(address: GuestAddress, size: Int :| Positive0): Either[MemoryError, Vec[Byte]] = lock.withLock {
    validate(address, size) match {
      case Left(error)   => Left(error)
      case Right(offset) =>
        Right(Vec.unsafeFromArray(java.util.Arrays.copyOfRange(mutableBytes, offset, offset + size)))
    }
  }

  override def writeBytes(address: GuestAddress, bytes: Vec[Byte]): Either[MemoryError, Unit] = lock.withLock {
    validate(address, bytes.length) match {
      case Left(error)   => Left(error)
      case Right(offset) =>
        val _ = bytes.copyToArray(mutableBytes, offset, bytes.size)
        Right(())
    }
  }

  override def readLittleEndian(address: GuestAddress, size: MemoryValueByteWidth): Either[MemoryError, Long] =
    lock.withLock {
      validate(address, size.value).map(offset =>
        LittleEndian.decode(mutableBytes, offset, size.scalarByteWidth)
      )
    }

  override def writeLittleEndian(
      address: GuestAddress,
      value: Long,
      size: MemoryValueByteWidth
  ): Either[MemoryError, Unit] =
    lock.withLock {
      validate(address, size.value) match {
        case Left(error)   => Left(error)
        case Right(offset) =>
          LittleEndian.write(value, mutableBytes, offset, size.scalarByteWidth)
          Right(())
      }
    }

  override def atomicModifyLittleEndian(
      address: GuestAddress,
      size: MemoryValueByteWidth
  )(update: Long => Long): Either[MemoryError, Long] = lock.withLock {
    validate(address, size.value) match {
      case Left(error)   => Left(error)
      case Right(offset) =>
        val width    = size.scalarByteWidth
        val previous = LittleEndian.decode(mutableBytes, offset, width)
        LittleEndian.write(update(previous), mutableBytes, offset, width)
        Right(previous)
    }
  }

  def free(): Unit = lock.withLock {
    live = false
  }

  private def validate(address: GuestAddress, width: Int): Either[MemoryError, Int] =
    if (!live) Left(MemoryError.UseAfterFree(address, allocation))
    else {
      val relative   = address.value - allocation.start.value
      val validStart = java.lang.Long.compareUnsigned(address.value, allocation.start.value) >= 0
      val validEnd   = width >= 0 && width <= allocation.size &&
        java.lang.Long.compareUnsigned(relative, allocation.size.toLong - width.toLong) <= 0
      if (validStart && validEnd)
        Right(relative.toInt)
      else Left(MemoryError.OutOfBounds(address, width, allocation))
    }

}

object ArrayCheckedMemory {
  def allocate(
      size: Int :| Positive0,
      start: GuestAddress = GuestAddress(0x10000L),
      kind: AllocationKind = AllocationKind.Device,
      provenance: String :| MinLength[1] = "anonymous"
  ): ArrayCheckedMemory =
    ArrayCheckedMemory(Allocation(start, size, kind, provenance), Array.ofDim[Byte](size))
}
