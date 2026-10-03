package net.kurobako.gemstone.memory

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import scala.annotation.nowarn

/** Address-oriented view used by instruction sets whose pointer operands contain virtual device addresses. */
trait AddressMemory {
  def fence(): Either[MemoryError, Unit] = Right(())

  /** Stage only resolved accesses from active lanes; implementations may merge adjacent ranges. */
  @nowarn("msg=unused explicit parameter")
  def prepareReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[MemoryError, Unit] = Right(())

  /** Prepare fresh system-scope reads while preserving dirty local writes. */
  def prepareSystemReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[MemoryError, Unit] = prepareReads(ranges)

  def readInt8(address: GuestAddress): Either[MemoryError, Byte]
  def readInt32(address: GuestAddress): Either[MemoryError, Int]
  def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit]
  def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit]

  /** A byte-range fallback for implementations without a bulk operation. Atomic bulk access belongs to the backing
    * owner and must be provided by overriding this method.
    */
  def readBytes(address: GuestAddress, size: Int >= 0): Either[MemoryError, Vec[Byte]] = {
    val mutableBytes = Vec.newBuilder[Byte]
    @scala.annotation.tailrec
    def read(index: Int): Either[MemoryError, Vec[Byte]] =
      if (index == size.raw) Right(mutableBytes.result())
      else readInt8(address.offset(index.toLong)) match {
        case Left(error) => Left(error)
        case Right(byte) =>
          val _ = mutableBytes += byte
          read(index + 1)
      }
    read(0)
  }

  def writeBytes(address: GuestAddress, bytes: Vec[Byte]): Either[MemoryError, Unit] =
    validateRange(address, bytes.nonNegativeSize) match {
      case Left(error) => Left(error)
      case Right(())   =>
        @scala.annotation.tailrec
        def write(index: Int): Either[MemoryError, Unit] =
          if (index == bytes.size) Right(())
          else writeInt8(address.offset(index.toLong), bytes(index)) match {
            case Left(error) => Left(error)
            case Right(())   => write(index + 1)
          }
        write(0)
    }

  def validateRange(address: GuestAddress, size: Int >= 0): Either[MemoryError, Unit] = {
    @scala.annotation.tailrec
    def validate(index: Int): Either[MemoryError, Unit] =
      if (index == size.raw) Right(())
      else readInt8(address.offset(index.toLong)) match {
        case Left(error) => Left(error)
        case Right(_)    => validate(index + 1)
      }

    validate(0)
  }

  def readLittleEndian(address: GuestAddress, size: Int in 1 ~ 8): Either[MemoryError, Long] = {
    @scala.annotation.tailrec
    def read(index: Int, value: Long): Either[MemoryError, Long] =
      if (index == size.raw) Right(value)
      else readInt8(address.offset(index.toLong)) match {
        case Left(error) => Left(error)
        case Right(byte) => read(index + 1, value | ((byte.toLong & 0xffL) << (index * 8)))
      }
    read(0, 0L)
  }

  def readSystemLittleEndian(
      address: GuestAddress,
      size: Int in 1 ~ 8
  ): Either[MemoryError, Long] = readLittleEndian(address, size)

  def writeLittleEndian(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[MemoryError, Unit] =
    validateRange(address, size.nonNegative) match {
      case Left(error) => Left(error)
      case Right(())   =>
        @scala.annotation.tailrec
        def write(index: Int): Either[MemoryError, Unit] =
          if (index == size.raw) Right(())
          else writeInt8(address.offset(index.toLong), (value >>> (index * 8)).toByte) match {
            case Left(error) => Left(error)
            case Right(())   => write(index + 1)
          }
        write(0)
    }

  def writeSystemLittleEndian(
      address: GuestAddress,
      value: Long,
      size: Int in 1 ~ 8
  ): Either[MemoryError, Unit] = writeLittleEndian(address, value, size)

  def writeSystemBatch(
      writes: Vec[(GuestAddress, Long, Int in 1 ~ 8)]
  ): Either[MemoryError, Unit] = writes.foldLeft[Either[MemoryError, Unit]](Right(())) {
    case (result, (address, value, size)) => result.flatMap(_ => writeSystemLittleEndian(address, value, size))
  }

  /** Atomically replace a value and return its previous contents. Views over shared storage must delegate the whole
    * operation to their backing owner.
    */
  @nowarn("msg=unused explicit parameter")
  def atomicModifyLittleEndian(
      address: GuestAddress,
      size: Int in 1 ~ 8
  )(update: Long => Long): Either[MemoryError, Long] =
    Left(MemoryError.UnsupportedAtomic(address, size))
}
