package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

class VMMemory(addresses: AddressSpace) extends AddressMemory {
  override def fence(): Either[MemoryError, Unit] =
    addresses.fenceHostMemory().left.map(error(_))
  override def validateRange(address: GuestAddress, size: Int >= 0): Either[MemoryError, Unit] =
    addresses.locate(address, size).map(_ => ()).left.map(error(_))

  override def prepareReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[MemoryError, Unit] =
    addresses.prepareReads(ranges).left.map(error(_))

  override def prepareSystemReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[MemoryError, Unit] =
    addresses.prepareSystemReads(ranges).left.map(error(_))

  override def readInt8(address: GuestAddress): Either[MemoryError, Byte] =
    readValue(address, 1).map(_.toByte)

  override def readInt32(address: GuestAddress): Either[MemoryError, Int] =
    readValue(address, 4).map(_.toInt)

  override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
    writeValue(address, value.toLong, 1)

  override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
    writeValue(address, value.toLong, 4)

  override def readBytes(address: GuestAddress, size: Int >= 0): Either[MemoryError, Vec[Byte]] =
    addresses.read(address, size).left.map(error(_))

  override def writeBytes(address: GuestAddress, bytes: Vec[Byte]): Either[MemoryError, Unit] =
    addresses.write(address, bytes).left.map(error(_))

  override def readLittleEndian(
      address: GuestAddress,
      size: Int where Between[1, 8]
  ): Either[MemoryError, Long] = readValue(address, size)

  override def readSystemLittleEndian(
      address: GuestAddress,
      size: Int where Between[1, 8]
  ): Either[MemoryError, Long] = addresses.readSystemValue(address, size).left.map(error(_))

  override def writeLittleEndian(
      address: GuestAddress,
      value: Long,
      size: Int where Between[1, 8]
  ): Either[MemoryError, Unit] = writeValue(address, value, size)

  override def writeSystemLittleEndian(
      address: GuestAddress,
      value: Long,
      size: Int where Between[1, 8]
  ): Either[MemoryError, Unit] = addresses.writeSystemValue(address, value, size).left.map(error(_))

  override def writeSystemBatch(
      writes: Vec[(GuestAddress, Long, Int where Between[1, 8])]
  ): Either[MemoryError, Unit] = addresses.writeSystemBatch(writes).left.map(error(_))

  override def atomicModifyLittleEndian(
      address: GuestAddress,
      size: Int where Between[1, 8]
  )(update: Long => Long): Either[MemoryError, Long] =
    addresses.atomicModifyValue(address, size)(update).left.map(error(_))

  private def readValue(address: GuestAddress, size: Int where Between[1, 8]): Either[MemoryError, Long] =
    addresses.readValue(address, size).left.map(error(_))

  private def writeValue(
      address: GuestAddress,
      value: Long,
      size: Int where Between[1, 8]
  ): Either[MemoryError, Unit] =
    addresses.writeValue(address, value, size).left.map(error(_))

  private def error(fault: Fault): MemoryError = fault match {
    case Fault.Cancelled => MemoryError.Cancelled
    case other           => MemoryError.BackingFailure(other.message)
  }

}
