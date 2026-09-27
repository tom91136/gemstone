package net.kurobako.gemstone.vm

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Constraint.NonNegative

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.memory.GuestAddress
trait AddressSpace {
  @scala.annotation.nowarn("msg=unused explicit parameter")
  def prepareReads(ranges: Vec[(GuestAddress, NonNegative[Int])]): Either[Fault, Unit] = Right(())

  def address(
      region: MemoryRegion,
      offset: NonNegative[Long]
  ): Either[Fault, GuestAddress]

  def locate(
      address: GuestAddress,
      size: NonNegative[Int]
  ): Either[Fault, AddressSpace.Location]

  /** Resolve and read in one operation. Implementations that own both the address map and memory can override this
    * boundary to avoid allocating an intermediate [[AddressSpace.Location]].
    */
  def read(memory: Memory, address: GuestAddress, size: NonNegative[Int]): Either[Fault, Vec[Byte]] =
    locate(address, size) match {
      case Left(error)     => Left(error)
      case Right(location) => memory.read(location.region, location.offset, size)
    }

  /** Resolve and write in one operation. */
  def write(memory: Memory, address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    locate(address, bytes.nonNegativeSize) match {
      case Left(error)     => Left(error)
      case Right(location) => memory.write(location.region, location.offset, bytes)
    }

  /** Scalar form of [[read]], preserving a backing memory's allocation-free scalar implementation. */
  def readValue(memory: Memory, address: GuestAddress, size: IntBetween[1, 8]): Either[Fault, Long] =
    locate(address, size.nonNegative) match {
      case Left(error)     => Left(error)
      case Right(location) => memory.readValue(location.region, location.offset, size)
    }

  /** Scalar form of [[write]], preserving a backing memory's allocation-free scalar implementation. */
  def writeValue(
      memory: Memory,
      address: GuestAddress,
      value: Long,
      size: IntBetween[1, 8]
  ): Either[Fault, Unit] =
    locate(address, size.nonNegative) match {
      case Left(error)     => Left(error)
      case Right(location) => memory.writeValue(location.region, location.offset, value, size)
    }

  /** Resolve and perform one indivisible scalar update. */
  def atomicModifyValue(
      memory: Memory,
      address: GuestAddress,
      size: IntBetween[1, 8]
  )(update: Long => Long): Either[Fault, Long] =
    locate(address, size.nonNegative) match {
      case Left(error)     => Left(error)
      case Right(location) => memory.atomicModifyValue(location.region, location.offset, size)(update)
    }
}

object AddressSpace {
  case class Location(region: MemoryRegion, offset: NonNegative[Long])
}
