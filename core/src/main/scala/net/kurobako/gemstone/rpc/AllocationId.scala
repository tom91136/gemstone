package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.Constraint.{LongBetween, Newtype}

/** A strictly positive allocation identifier. */
object AllocationId extends Newtype[LongBetween[1L, Long.MaxValue.type]]("allocation id") {
  extension (id: AllocationId) def next: AllocationId = unsafe(Math.addExact(id.raw, 1L))
}

type AllocationId = AllocationId.Type
