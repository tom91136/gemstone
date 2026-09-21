package net.kurobako.gemstone.memory

/** Optional device-side allocation capability used by instruction-level malloc/free calls. */
trait DeviceAllocationMemory extends AddressMemory {
  def allocate(size: DeviceAllocationByteSize): Either[MemoryError, GuestAddress]
  def free(address: GuestAddress): Boolean
}
