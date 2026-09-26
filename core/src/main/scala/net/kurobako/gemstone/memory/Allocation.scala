package net.kurobako.gemstone.memory

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class Allocation(
    start: GuestAddress,
    size: Int :| Positive0,
    provenance: String :| MinLength[1]
)
