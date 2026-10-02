package net.kurobako.gemstone.vm

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
object AddressSpace {
  enum Location {
    case Within(allocation: AllocationId, offset: Long >= 0)
    case Absolute(address: GuestAddress)
  }
}

trait AddressSpace extends Memory {
  import AddressSpace.*

  def prepareReads(@scala.annotation.unused ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] = Right(())

  def prepareSystemReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] = prepareReads(ranges)

  def address(
      region: AllocationId,
      offset: Long >= 0
  ): Either[Fault, GuestAddress]

  def locate(
      address: GuestAddress,
      size: Int >= 0
  ): Either[Fault, AddressSpace.Location]

  /** Resolve and read in one operation. Implementations that own both the address map and memory can override this
    * boundary to avoid allocating an intermediate [[AddressSpace.Location]].
    */
  def read(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] =
    locate(address, size) match {
      case Left(error)                                => Left(error)
      case Right(Location.Within(allocation, offset)) => read(allocation, offset, size)
      case Right(Location.Absolute(address))          =>
        Left(Fault.Unsupported(s"absolute address ${address.show} requires an address-backed memory"))
    }

  /** Stage an address read, returning Pending until the backing owner completes it. */
  def readDeferred(address: GuestAddress, size: Int >= 0): Memory.ReadResult[Vec[Byte]] =
    locate(address, size) match {
      case Left(error)                                => Left(error)
      case Right(Location.Within(allocation, offset)) => readDeferred(allocation, offset, size)
      case Right(Location.Absolute(address))          =>
        Left(Fault.Unsupported(s"absolute address ${address.show} requires an address-backed memory"))
    }

  /** Resolve and write in one operation. */
  def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    locate(address, bytes.nonNegativeSize) match {
      case Left(error)                                => Left(error)
      case Right(Location.Within(allocation, offset)) => write(allocation, offset, bytes)
      case Right(Location.Absolute(address))          =>
        Left(Fault.Unsupported(s"absolute address ${address.show} requires an address-backed memory"))
    }

  /** Scalar form of [[read]], preserving a backing memory's allocation-free scalar implementation. */
  def readValue(address: GuestAddress, size: Int where Between[1, 8]): Either[Fault, Long] =
    locate(address, size.nonNegative) match {
      case Left(error)                                => Left(error)
      case Right(Location.Within(allocation, offset)) => readValue(allocation, offset, size)
      case Right(Location.Absolute(address))          =>
        Left(Fault.Unsupported(s"absolute address ${address.show} requires an address-backed memory"))
    }

  def readSystemValue(address: GuestAddress, size: Int where Between[1, 8]): Either[Fault, Long] =
    readValue(address, size)

  /** Scalar form of [[write]], preserving a backing memory's allocation-free scalar implementation. */
  def writeValue(
      address: GuestAddress,
      value: Long,
      size: Int where Between[1, 8]
  ): Either[Fault, Unit] =
    locate(address, size.nonNegative) match {
      case Left(error)                                => Left(error)
      case Right(Location.Within(allocation, offset)) => writeValue(allocation, offset, value, size)
      case Right(Location.Absolute(address))          =>
        Left(Fault.Unsupported(s"absolute address ${address.show} requires an address-backed memory"))
    }

  def writeSystemValue(
      address: GuestAddress,
      value: Long,
      size: Int where Between[1, 8]
  ): Either[Fault, Unit] = writeValue(address, value, size)

  def writeSystemBatch(
      writes: Vec[(GuestAddress, Long, Int where Between[1, 8])]
  ): Either[Fault, Unit] = writes.foldLeft[Either[Fault, Unit]](Right(())) {
    case (result, (address, value, size)) => result.flatMap(_ => writeSystemValue(address, value, size))
  }

  /** Resolve and perform one indivisible scalar update. */
  def atomicModifyValue(
      address: GuestAddress,
      size: Int where Between[1, 8]
  )(update: Long => Long): Either[Fault, Long] =
    locate(address, size.nonNegative) match {
      case Left(error)                                => Left(error)
      case Right(Location.Within(allocation, offset)) => atomicModifyValue(allocation, offset, size)(update)
      case Right(Location.Absolute(address))          =>
        Left(Fault.Unsupported(s"absolute address ${address.show} requires an address-backed memory"))
    }
}
