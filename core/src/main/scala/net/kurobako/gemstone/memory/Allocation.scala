package net.kurobako.gemstone.memory

import net.kurobako.gemstone.constraint.*

case class Allocation(
    start: GuestAddress,
    size: Int >= 0,
    provenance: NonEmpty[String]
)
