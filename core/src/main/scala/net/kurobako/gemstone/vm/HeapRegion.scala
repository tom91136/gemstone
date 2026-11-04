package net.kurobako.gemstone.vm

import net.kurobako.gemstone.memory.*

private[gemstone] object HeapRegion {

  /** Where a launch's device heap lies in `memory`. */
  def of(heap: KernelTask.DeviceHeap, memory: AddressSpace): Either[Fault, HeapRegion] =
    memory.address(heap.region, heap.offset).map(HeapRegion(_, heap.size))
}

/** The memory a context reserves for device-side `malloc`, which stays addressable by every launch of the context. */
private[gemstone] case class HeapRegion(base: GuestAddress, size: Long)
