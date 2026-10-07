package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

/** One device allocation at `start`, with the scalar accessors kernels' results are read through. */
class DeviceMemory(size: Int >= 0, val start: GuestAddress = GuestAddress(RegionMemory.Base)) extends RegionMemory {
  allocate(AllocationId(1), size.raw, start)

  // Region accesses bypass the address-level hooks fixtures override to observe kernels.
  private def within[A](address: GuestAddress, size: Int >= 0)(access: (AllocationId, Long >= 0) => Either[Fault, A])
      : Either[Fault, A] = locate(address, size).flatMap {
    case AddressSpace.Location.Within(region, offset) => access(region, offset)
    case AddressSpace.Location.Absolute(_)            => Left(Fault.Memory(s"${address.show} is not a test allocation"))
  }

  def readInt8(address: GuestAddress): Either[Fault, Byte]               = readLittleEndian(address, 1).map(_.toByte)
  def writeInt8(address: GuestAddress, value: Byte): Either[Fault, Unit] = writeLittleEndian(address, value.toLong, 1)
  def readInt32(address: GuestAddress): Either[Fault, Int]               = readLittleEndian(address, 4).map(_.toInt)
  def writeInt32(address: GuestAddress, value: Int): Either[Fault, Unit] = writeLittleEndian(address, value.toLong, 4)
  def readLittleEndian(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    within(address, size.widen[Int >= 0])(readValue(_, _, size))
  def writeLittleEndian(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
    within(address, size.widen[Int >= 0])(writeValue(_, _, value, size))
  def readBytes(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] =
    within(address, size)(read(_, _, size))
  def writeBytes(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    within(address, bytes.nonNegativeSize)(write(_, _, bytes))
}
