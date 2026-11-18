package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.check.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** Global memory at every address, over a harness's bytes, so execution reads and writes what definitions do. */
private[isa] class AMDGPUGlobalSpace(bytes: SeededMemory) extends AddressSpace {
  private def unsupported = Fault.Unsupported("AMDGPU definitions address global memory by guest address alone")

  def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] = Left(unsupported)
  def write(region: AllocationId, offset: Long >= 0, data: Vec[Byte]): Either[Fault, Unit]    = Left(unsupported)
  def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress]           = Left(unsupported)

  def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] =
    Right(AddressSpace.Location.Absolute(address))

  override def read(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] =
    Right(Vec.tabulate(size)(index => bytes.load(address.raw + index, 1).toByte))

  override def readValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    Right(bytes.load(address.raw, size))

  override def write(address: GuestAddress, data: Vec[Byte]): Either[Fault, Unit] = {
    data.indices.foreach(index => bytes.store(address.raw + index, data(index).toLong, 1))
    Right(())
  }

  override def writeValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] = {
    bytes.store(address.raw, value, size)
    Right(())
  }

  override def atomicModifyValue(
      address: GuestAddress,
      size: Int in 1 ~ 8
  )(update: Long => Long): Either[Fault, Long] = {
    val previous = bytes.load(address.raw, size)
    bytes.store(address.raw, update(previous), size)
    Right(previous)
  }
}
