package net.kurobako.gemstone.testing

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** Rejects every access; for kernels that must not touch memory. */
class RejectingMemory extends AddressSpace {
  override def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
    Left(Fault.Memory("unexpected address lookup"))
  override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] =
    Left(Fault.Memory("unexpected address lookup"))
  override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
    Left(Fault.Memory("unexpected read"))
  override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
    Left(Fault.Memory("unexpected write"))
}
