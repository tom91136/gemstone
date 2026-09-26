package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.memory.AddressMemory
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.memory.MemoryError
import net.kurobako.gemstone.memory.MemoryValueByteWidth
import net.kurobako.gemstone.vm.AddressSpace
import net.kurobako.gemstone.vm.Fault
import net.kurobako.gemstone.vm.Memory

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[v9_3] final class VMMemory(memory: Memory, addresses: AddressSpace) extends AddressMemory {
  override def fence(): Either[MemoryError, Unit] =
    memory.fenceHostMemory().left.map(error(GuestAddress(0), 0, _))
  override def validateRange(address: GuestAddress, size: Int :| Positive0): Either[MemoryError, Unit] =
    addresses.locate(address, size).map(_ => ()).left.map(error(address, size, _))

  override def prepareReads(ranges: Vec[(GuestAddress, Int :| Positive0)]): Either[MemoryError, Unit] =
    addresses.prepareReads(ranges).left.map(error(GuestAddress(0), 0, _))

  override def readInt8(address: GuestAddress): Either[MemoryError, Byte] =
    readValue(address, MemoryValueByteWidth.Byte).map(_.toByte)

  override def readInt32(address: GuestAddress): Either[MemoryError, Int] =
    readValue(address, MemoryValueByteWidth.Dword).map(_.toInt)

  override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
    writeValue(address, value.toLong, MemoryValueByteWidth.Byte)

  override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
    writeValue(address, value.toLong, MemoryValueByteWidth.Dword)

  override def readBytes(address: GuestAddress, size: Int :| Positive0): Either[MemoryError, Vec[Byte]] =
    addresses.read(memory, address, size).left.map(error(address, size, _))

  override def writeBytes(address: GuestAddress, bytes: Vec[Byte]): Either[MemoryError, Unit] =
    addresses.write(memory, address, bytes).left.map(error(address, bytes.size, _))

  override def readLittleEndian(
      address: GuestAddress,
      size: MemoryValueByteWidth
  ): Either[MemoryError, Long] = readValue(address, size)

  override def writeLittleEndian(
      address: GuestAddress,
      value: Long,
      size: MemoryValueByteWidth
  ): Either[MemoryError, Unit] = writeValue(address, value, size)

  override def atomicModifyLittleEndian(
      address: GuestAddress,
      size: MemoryValueByteWidth
  )(update: Long => Long): Either[MemoryError, Long] =
    addresses.atomicModifyValue(memory, address, size)(update).left.map(error(address, size.value, _))

  private def readValue(address: GuestAddress, size: MemoryValueByteWidth): Either[MemoryError, Long] =
    addresses.readValue(memory, address, size).left.map(error(address, size.value, _))

  private def writeValue(
      address: GuestAddress,
      value: Long,
      size: MemoryValueByteWidth
  ): Either[MemoryError, Unit] =
    addresses.writeValue(memory, address, value, size).left.map(error(address, size.value, _))

  private def error(address: GuestAddress, size: Int, fault: Fault): MemoryError =
    MemoryError.InvalidAddress(address, size, fault.message)
}
