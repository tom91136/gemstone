package net.kurobako.gemstone.vm

import AddressSpace.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import scala.runtime.LongRef

/** Reports each device memory access to an observer, then forwards it unchanged. */
private class ObservedAddressSpace(backing: AddressSpace, observer: LaunchObserver) extends AddressSpace {
  import LaunchObserver.*

  private def within(kind: AccessKind, region: AllocationId, offset: Long >= 0, size: Int >= 0): Unit =
    observer.memory(Access(kind, Location.Within(region, offset), size))

  private def absolute(kind: AccessKind, address: GuestAddress, size: Int >= 0): Unit =
    observer.memory(Access(kind, Location.Absolute(address), size))

  override def readDeferred(region: AllocationId, offset: Long >= 0, size: Int >= 0): ReadResult[Vec[Byte]] = {
    within(AccessKind.Read, region, offset, size)
    backing.readDeferred(region, offset, size)
  }

  override def readValueDeferred(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8): ReadResult[Long] = {
    within(AccessKind.Read, region, offset, size.widen[Int >= 0])
    backing.readValueDeferred(region, offset, size)
  }

  override def readValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8): Either[Fault, Long] = {
    within(AccessKind.Read, region, offset, size.widen[Int >= 0])
    backing.readValue(region, offset, size)
  }

  override def readValueInto(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8, into: LongRef): Fault |
    Null = {
    within(AccessKind.Read, region, offset, size.widen[Int >= 0])
    backing.readValueInto(region, offset, size, into)
  }

  override def hasNativePointers(region: AllocationId): Boolean = backing.hasNativePointers(region)
  override def fenceHostMemory(): Either[Fault, Unit]           = backing.fenceHostMemory()
  override def flushHostWrites(): Either[Fault, Unit]           = backing.flushHostWrites()
  override def invalidateHostReads(): Unit                      = backing.invalidateHostReads()
  override def defersHostReads: Boolean                         = backing.defersHostReads
  override def flushHostReads(): Either[Fault, Unit]            = backing.flushHostReads()

  override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] = {
    within(AccessKind.Read, region, offset, size)
    backing.read(region, offset, size)
  }

  override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] = {
    within(AccessKind.Write, region, offset, bytes.nonNegativeSize)
    backing.write(region, offset, bytes)
  }

  override def writeValue(region: AllocationId, offset: Long >= 0, value: Long, size: Int in 1 ~ 8)
      : Either[Fault, Unit] = {
    within(AccessKind.Write, region, offset, size.widen[Int >= 0])
    backing.writeValue(region, offset, value, size)
  }

  override def atomicModifyValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)(
      update: Long => Long
  ): Either[Fault, Long] = {
    within(AccessKind.Atomic, region, offset, size.widen[Int >= 0])
    backing.atomicModifyValue(region, offset, size)(update)
  }

  override def prepareReads(ranges: ReadRanges): Either[Fault, Unit]       = backing.prepareReads(ranges)
  override def prepareSystemReads(ranges: ReadRanges): Either[Fault, Unit] = backing.prepareSystemReads(ranges)
  override def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
    backing.address(region, offset)
  override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, Location] = backing.locate(address, size)

  override def read(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] = {
    absolute(AccessKind.Read, address, size)
    backing.read(address, size)
  }

  override def readDeferred(address: GuestAddress, size: Int >= 0): ReadResult[Vec[Byte]] = {
    absolute(AccessKind.Read, address, size)
    backing.readDeferred(address, size)
  }

  override def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] = {
    absolute(AccessKind.Write, address, bytes.nonNegativeSize)
    backing.write(address, bytes)
  }

  override def readValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] = {
    absolute(AccessKind.Read, address, size.widen[Int >= 0])
    backing.readValue(address, size)
  }

  override def readValueInto(address: GuestAddress, size: Int in 1 ~ 8, into: LongRef): Fault | Null = {
    absolute(AccessKind.Read, address, size.widen[Int >= 0])
    backing.readValueInto(address, size, into)
  }

  override def readSystemValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] = {
    absolute(AccessKind.Read, address, size.widen[Int >= 0])
    backing.readSystemValue(address, size)
  }

  override def readSystemValueInto(address: GuestAddress, size: Int in 1 ~ 8, into: LongRef): Fault | Null = {
    absolute(AccessKind.Read, address, size.widen[Int >= 0])
    backing.readSystemValueInto(address, size, into)
  }

  override def writeValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] = {
    absolute(AccessKind.Write, address, size.widen[Int >= 0])
    backing.writeValue(address, value, size)
  }

  override def writeSystemValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] = {
    absolute(AccessKind.Write, address, size.widen[Int >= 0])
    backing.writeSystemValue(address, value, size)
  }

  override def writeSystemBatch(writes: Vec[(GuestAddress, Long, Int in 1 ~ 8)]): Either[Fault, Unit] = {
    writes.foreach((address, _, size) => absolute(AccessKind.Write, address, size.widen[Int >= 0]))
    backing.writeSystemBatch(writes)
  }

  override def atomicModifyValue(
      address: GuestAddress,
      size: Int in 1 ~ 8
  )(update: Long => Long): Either[Fault, Long] = {
    absolute(AccessKind.Atomic, address, size.widen[Int >= 0])
    backing.atomicModifyValue(address, size)(update)
  }
}
