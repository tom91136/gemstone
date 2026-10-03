package net.kurobako.gemstone.memory

import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

object ArrayCheckedMemory {
  def allocate(
      size: Int >= 0,
      start: GuestAddress = GuestAddress(0x10000L),
      provenance: NonEmpty[String] = "anonymous"
  ): ArrayCheckedMemory =
    ArrayCheckedMemory(Allocation(start, size, provenance), Array.ofDim[Byte](size.raw))
}

class ArrayCheckedMemory private (val allocation: Allocation, mutableBytes: Array[Byte]) extends AddressMemory {
  private val lock = ReentrantLock()

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var live = true

  override def readInt8(address: GuestAddress): Either[MemoryError, Byte] = lock.withLock {
    validate(address, 1) match {
      case Left(error)   => Left(error)
      case Right(offset) => Right(mutableBytes(offset.raw))
    }
  }

  override def readInt32(address: GuestAddress): Either[MemoryError, Int] = lock.withLock {
    validate(address, 4) match {
      case Left(error)   => Left(error)
      case Right(offset) => Right(LittleEndian.decodeInt32(mutableBytes, offset.raw))
    }
  }

  override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] = lock.withLock {
    validate(address, 4) match {
      case Left(error)   => Left(error)
      case Right(offset) =>
        LittleEndian.write(value.toLong, mutableBytes, offset.raw, 4)
        Right(())
    }
  }

  override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] = lock.withLock {
    validate(address, 1) match {
      case Left(error)   => Left(error)
      case Right(offset) =>
        mutableBytes(offset.raw) = value
        Right(())
    }
  }

  override def readBytes(address: GuestAddress, size: Int >= 0): Either[MemoryError, Vec[Byte]] = lock.withLock {
    validate(address, size) match {
      case Left(error)   => Left(error)
      case Right(offset) =>
        Right(Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(mutableBytes, offset.raw, (offset + size).raw)))
    }
  }

  override def writeBytes(address: GuestAddress, bytes: Vec[Byte]): Either[MemoryError, Unit] = lock.withLock {
    validate(address, bytes.nonNegativeSize) match {
      case Left(error)   => Left(error)
      case Right(offset) =>
        val _ = bytes.copyToArray(mutableBytes, offset.raw, bytes.size)
        Right(())
    }
  }

  override def readLittleEndian(address: GuestAddress, size: Int in 1 ~ 8): Either[MemoryError, Long] =
    lock.withLock {
      validate(address, size).map(offset =>
        LittleEndian.decode(mutableBytes, offset.raw, size.scalarByteWidth)
      )
    }

  override def writeLittleEndian(
      address: GuestAddress,
      value: Long,
      size: Int in 1 ~ 8
  ): Either[MemoryError, Unit] =
    lock.withLock {
      validate(address, size) match {
        case Left(error)   => Left(error)
        case Right(offset) =>
          LittleEndian.write(value, mutableBytes, offset.raw, size.scalarByteWidth)
          Right(())
      }
    }

  override def atomicModifyLittleEndian(
      address: GuestAddress,
      size: Int in 1 ~ 8
  )(update: Long => Long): Either[MemoryError, Long] = lock.withLock {
    validate(address, size) match {
      case Left(error)   => Left(error)
      case Right(offset) => Right(LittleEndian.atomicModify(mutableBytes, offset.raw, size.scalarByteWidth)(update))
    }
  }

  def free(): Unit = lock.withLock {
    live = false
  }

  private def validate(address: GuestAddress, width: Int >= 0): Either[MemoryError, Int >= 0] =
    if (!live) Left(MemoryError.UseAfterFree(address, allocation))
    else {
      val relative = address.raw - allocation.start.raw
      if (
        AddressRanges.containsUnsigned(allocation.start.raw, allocation.size.raw.toLong, address.raw, width.toLong.raw)
      )
        Right(relative.toInt.unsafe[Int >= 0])
      else Left(MemoryError.OutOfBounds(address, width.raw, allocation))
    }

}
