package net.kurobako.gemstone.memory

import net.kurobako.gemstone.constraint.*

/** Optional device-side allocation capability used by instruction-level malloc/free calls. */
trait DeviceAllocationMemory extends AddressMemory {
  def allocate(size: Int > 0): Either[MemoryError, GuestAddress]
  def free(address: GuestAddress): Boolean
}
