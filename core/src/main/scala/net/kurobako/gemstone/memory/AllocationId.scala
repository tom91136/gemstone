package net.kurobako.gemstone.memory

import net.kurobako.gemstone.constraint.*

type AllocationId = AllocationId.Type

/** A strictly positive allocation identifier. */
object AllocationId extends Newtype[Long > 0] {
  extension (id: AllocationId) {
    def toLong: Long       = id.raw
    def next: AllocationId = apply(id.unwrap + 1L)
  }
}
