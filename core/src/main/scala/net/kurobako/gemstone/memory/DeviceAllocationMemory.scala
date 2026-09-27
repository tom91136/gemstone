package net.kurobako.gemstone.memory

import net.kurobako.gemstone.Constraint.Positive

/** Optional device-side allocation capability used by instruction-level malloc/free calls. */
trait DeviceAllocationMemory extends AddressMemory {
  def allocate(size: Positive[Int]): Either[MemoryError, GuestAddress]
  def free(address: GuestAddress): Boolean
}
