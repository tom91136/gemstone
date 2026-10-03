package net.kurobako.gemstone.memory

import net.kurobako.gemstone.constraint.*

enum MemoryError(val message: NonEmpty[String]) {
  case Cancelled                                extends MemoryError("memory operation was cancelled")
  case BackingFailure(detail: NonEmpty[String]) extends MemoryError(detail)

  case InvalidAddress(address: GuestAddress, width: Int, detail: String)
      extends MemoryError(s"${address.show} + $width bytes is invalid: $detail")

  case OutOfBounds(address: GuestAddress, width: Int, allocation: Allocation)
      extends MemoryError(
        s"${address.show} + $width bytes is outside ${allocation.start.show} + ${allocation.size} (${allocation.provenance})"
      )

  case UseAfterFree(address: GuestAddress, allocation: Allocation)
      extends MemoryError(
        s"${address.show} refers to freed allocation ${allocation.provenance}"
      )

  case UnsupportedAtomic(address: GuestAddress, width: Int in 1 ~ 8)
      extends MemoryError(s"${address.show} + $width bytes does not support atomic modification")

  case AddressSpaceExhausted(size: Int > 0)
      extends MemoryError(s"device address space cannot fit an allocation of $size bytes")

  case AllocationInitializerTooLarge(size: Int > 0, initializerSize: Int)
      extends MemoryError(
        s"$initializerSize initializer bytes exceed a $size-byte device allocation"
      )
}
