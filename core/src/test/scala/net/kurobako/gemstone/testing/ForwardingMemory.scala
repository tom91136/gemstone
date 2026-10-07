package net.kurobako.gemstone.testing

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** Forwards region operations and host coherence to `delegate`. Scalar and address-form operations keep their defaults,
  * so they resolve through this wrapper and a region-level override intercepts them too.
  */
class ForwardingMemory(delegate: AddressSpace) extends AddressSpace {
  override def hasNativePointers(region: AllocationId): Boolean = delegate.hasNativePointers(region)
  override def fenceHostMemory(): Either[Fault, Unit]           = delegate.fenceHostMemory()
  override def flushHostWrites(): Either[Fault, Unit]           = delegate.flushHostWrites()
  override def invalidateHostReads(): Unit                      = delegate.invalidateHostReads()
  override def defersHostReads: Boolean                         = delegate.defersHostReads
  override def flushHostReads(): Either[Fault, Unit]            = delegate.flushHostReads()
  override def prepareReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] =
    delegate.prepareReads(ranges)
  override def prepareSystemReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] =
    delegate.prepareSystemReads(ranges)
  override def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
    delegate.address(region, offset)
  override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] =
    delegate.locate(address, size)
  override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
    delegate.read(region, offset, size)
  override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
    delegate.write(region, offset, bytes)
  override def atomicModifyValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)(
      update: Long => Long
  ): Either[Fault, Long] = delegate.atomicModifyValue(region, offset, size)(update)
}
