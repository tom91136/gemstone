package net.kurobako.gemstone.rpc

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type AllocationId = AllocationId.T
object AllocationId extends RefinedType[Long, Positive] {
  extension (id: AllocationId) def next: AllocationId = AllocationId.applyUnsafe(Math.addExact(id.value, 1L))
}
